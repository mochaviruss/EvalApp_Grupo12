package com.example.evalapp_grupo12.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.evalapp_grupo12.data.EvaluacionRepository
import com.example.evalapp_grupo12.logic.CalculadoraNota
import com.example.evalapp_grupo12.model.Alumno
import com.example.evalapp_grupo12.model.CriterioEvaluado
import com.example.evalapp_grupo12.model.Evaluacion
import com.example.evalapp_grupo12.model.EvaluarUiState
import com.example.evalapp_grupo12.model.Nivel
import com.example.evalapp_grupo12.model.Rubrica
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EvaluarViewModel(private val repository: EvaluacionRepository) : ViewModel() {

    private val _estado = MutableStateFlow(EvaluarUiState())
    val estado: StateFlow<EvaluarUiState> = _estado.asStateFlow()

    fun nuevaEvaluacion() {
        _estado.value = EvaluarUiState()
    }

    fun seleccionarCurso(curso: String) {
        _estado.update { it.copy(curso = curso, alumnosSeleccionados = emptyList(), error = null) }
    }

    fun cambiarSeleccionAlumno(alumno: Alumno) {
        val seleccionados = _estado.value.alumnosSeleccionados.toMutableList()
        if (seleccionados.any { it.id == alumno.id }) {
            seleccionados.removeAll { it.id == alumno.id }
        } else {
            seleccionados.add(alumno)
        }
        _estado.update { it.copy(alumnosSeleccionados = seleccionados, error = null) }
    }

    fun seleccionarRubrica(rubrica: Rubrica) {
        val criterios = rubrica.criterios.map {
            CriterioEvaluado(it.nombre, it.descripcion, it.puntajeMaximo)
        }
        _estado.update {
            calcularResultado(
                it.copy(
                    rubricaId = rubrica.id,
                    rubricaNombre = rubrica.nombre,
                    criterios = criterios,
                    niveles = rubrica.niveles,
                    error = null
                )
            )
        }
    }

    fun validarSeleccion(): Boolean {
        val estado = _estado.value
        if (estado.alumnosSeleccionados.isEmpty()) {
            mostrarError("Debe elegir al menos un alumno.")
            return false
        }
        if (estado.criterios.isEmpty()) {
            mostrarError("Debe elegir una rúbrica.")
            return false
        }
        return true
    }

    fun cambiarNivel(indice: Int, nivel: Nivel) {
        val criterios = _estado.value.criterios.toMutableList()
        if (indice !in criterios.indices) return
        val criterio = criterios[indice]
        criterios[indice] = criterio.copy(
            nivel = nivel.nombre,
            puntaje = CalculadoraNota.calcularPuntajeCriterio(nivel.porcentaje, criterio.puntajeMaximo)
        )
        _estado.update { calcularResultado(it.copy(criterios = criterios, error = null)) }
    }

    fun cambiarComentario(indice: Int, comentario: String) {
        val criterios = _estado.value.criterios.toMutableList()
        if (indice !in criterios.indices) return
        criterios[indice] = criterios[indice].copy(comentario = comentario)
        _estado.update { it.copy(criterios = criterios, error = null) }
    }

    fun cambiarRetroalimentacion(valor: String) {
        _estado.update { it.copy(retroalimentacion = valor, error = null) }
    }

    fun editar(evaluacion: Evaluacion) {
        _estado.value = calcularResultado(
            EvaluarUiState(
                curso = evaluacion.curso,
                alumnosSeleccionados = listOf(Alumno(evaluacion.alumnoId, evaluacion.alumno, evaluacion.curso)),
                rubricaId = evaluacion.rubricaId,
                rubricaNombre = evaluacion.rubrica,
                criterios = evaluacion.criterios,
                niveles = evaluacion.niveles,
                retroalimentacion = evaluacion.retroalimentacion,
                editandoId = evaluacion.id,
                fechaOriginal = evaluacion.fecha,
                grupoOriginal = evaluacion.grupo
            )
        )
    }

    fun enviar(evaluador: String, alTerminar: (String) -> Unit) {
        val estado = _estado.value
        if (estado.procesando) return

        if (estado.alumnosSeleccionados.isEmpty()) {
            mostrarError("Debe elegir al menos un alumno.")
            return
        }
        if (estado.criterios.isEmpty()) {
            mostrarError("Debe elegir una rúbrica.")
            return
        }
        if (estado.criterios.any { it.nivel.isEmpty() }) {
            mostrarError("Debe evaluar todos los criterios.")
            return
        }

        val editando = estado.editandoId != null
        val criterios = estado.criterios.map { it.copy(comentario = it.comentario.trim()) }
        val fecha = estado.fechaOriginal ?: fechaActual()
        val grupo = if (editando) {
            estado.grupoOriginal
        } else if (estado.alumnosSeleccionados.size > 1) {
            estado.alumnosSeleccionados.joinToString(", ") { it.nombre }
        } else {
            ""
        }

        val evaluaciones = estado.alumnosSeleccionados.map { alumno ->
            Evaluacion(
                id = estado.editandoId ?: 0,
                alumnoId = alumno.id,
                alumno = alumno.nombre,
                curso = alumno.curso,
                rubricaId = estado.rubricaId,
                rubrica = estado.rubricaNombre,
                fecha = fecha,
                puntaje = estado.puntajeObtenido,
                puntajeMaximo = estado.puntajeMaximo,
                nota = estado.nota,
                retroalimentacion = estado.retroalimentacion.trim(),
                criterios = criterios,
                niveles = estado.niveles,
                grupo = grupo,
                evaluador = evaluador
            )
        }

        _estado.update { it.copy(procesando = true, error = null) }

        viewModelScope.launch {
            try {
                for (evaluacion in evaluaciones) {
                    repository.guardar(evaluacion)
                }

                val mensaje = if (editando) {
                    "Evaluación actualizada."
                } else if (evaluaciones.size == 1) {
                    "Evaluación enviada."
                } else {
                    "Se enviaron ${evaluaciones.size} evaluaciones, una por cada integrante."
                }

                _estado.value = EvaluarUiState()
                alTerminar(mensaje)
            } catch (e: Exception) {
                _estado.update {
                    it.copy(procesando = false, error = "No se pudo enviar la evaluación. Revisa que el microservicio esté encendido.")
                }
            }
        }
    }

    private fun calcularResultado(estado: EvaluarUiState): EvaluarUiState {
        val obtenido = estado.criterios.sumOf { it.puntaje }
        val maximo = estado.criterios.sumOf { it.puntajeMaximo }
        val evaluados = estado.criterios.count { it.nivel.isNotEmpty() }
        val nota = if (maximo > 0) CalculadoraNota.calcularNota(obtenido, maximo) else 1.0

        return estado.copy(
            puntajeObtenido = obtenido,
            puntajeMaximo = maximo,
            nota = nota,
            criteriosEvaluados = evaluados
        )
    }

    private fun mostrarError(texto: String) {
        _estado.update { it.copy(error = texto) }
    }

    private fun fechaActual(): String =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

    class Factory(private val repository: EvaluacionRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EvaluarViewModel::class.java)) {
                return EvaluarViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel no reconocido: ${modelClass.name}")
        }
    }
}
