package com.example.evalapp_grupo12.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.evalapp_grupo12.data.RubricaRepository
import com.example.evalapp_grupo12.model.Criterio
import com.example.evalapp_grupo12.model.CriterioFormulario
import com.example.evalapp_grupo12.model.Nivel
import com.example.evalapp_grupo12.model.NivelFormulario
import com.example.evalapp_grupo12.model.Rubrica
import com.example.evalapp_grupo12.model.RubricaUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RubricaViewModel(private val repository: RubricaRepository) : ViewModel() {

    private val _estado = MutableStateFlow(RubricaUiState())
    val estado: StateFlow<RubricaUiState> = _estado.asStateFlow()

    init {
        cargarRubricas()
    }

    fun cargarRubricas() {
        _estado.update { it.copy(cargando = true, error = null) }

        viewModelScope.launch {
            try {
                val rubricas = repository.listar()
                _estado.update { it.copy(rubricas = rubricas, cargando = false) }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(cargando = false, error = "No se pudieron cargar las rúbricas. Revisa que el microservicio esté encendido.")
                }
            }
        }
    }

    fun nuevaRubrica() {
        _estado.update { limpiarFormulario(it) }
    }

    fun seleccionarParaEditar(rubrica: Rubrica) {
        val criterios = rubrica.criterios.map {
            CriterioFormulario(it.nombre, it.descripcion, it.puntajeMaximo.toString())
        }
        val niveles = rubrica.niveles.map {
            NivelFormulario(it.nombre, it.porcentaje.toString())
        }
        _estado.update {
            it.copy(
                nombre = rubrica.nombre,
                asignatura = rubrica.asignatura,
                criterios = criterios,
                niveles = niveles,
                editandoId = rubrica.id,
                error = null,
                mensaje = null
            )
        }
    }

    fun cambiarNombre(valor: String) {
        _estado.update { it.copy(nombre = valor, error = null, mensaje = null) }
    }

    fun cambiarAsignatura(valor: String) {
        _estado.update { it.copy(asignatura = valor, error = null, mensaje = null) }
    }

    fun cambiarNombreCriterio(indice: Int, valor: String) {
        cambiarCriterio(indice) { it.copy(nombre = valor) }
    }

    fun cambiarDescripcionCriterio(indice: Int, valor: String) {
        cambiarCriterio(indice) { it.copy(descripcion = valor) }
    }

    fun cambiarPuntajeCriterio(indice: Int, valor: String) {
        cambiarCriterio(indice) { it.copy(puntajeMaximo = valor) }
    }

    fun agregarCriterio() {
        _estado.update { it.copy(criterios = it.criterios + CriterioFormulario(), error = null, mensaje = null) }
    }

    fun quitarCriterio(indice: Int) {
        val criterios = _estado.value.criterios.toMutableList()
        if (indice !in criterios.indices || criterios.size == 1) return
        criterios.removeAt(indice)
        _estado.update { it.copy(criterios = criterios, error = null, mensaje = null) }
    }

    fun cambiarNombreNivel(indice: Int, valor: String) {
        cambiarNivel(indice) { it.copy(nombre = valor) }
    }

    fun cambiarPorcentajeNivel(indice: Int, valor: String) {
        cambiarNivel(indice) { it.copy(porcentaje = valor) }
    }

    fun agregarNivel() {
        _estado.update { it.copy(niveles = it.niveles + NivelFormulario(), error = null, mensaje = null) }
    }

    fun quitarNivel(indice: Int) {
        val niveles = _estado.value.niveles.toMutableList()
        if (indice !in niveles.indices || niveles.size <= 2) return
        niveles.removeAt(indice)
        _estado.update { it.copy(niveles = niveles, error = null, mensaje = null) }
    }

    fun guardar(alTerminar: () -> Unit) {
        val estado = _estado.value
        if (estado.procesando) return

        val nombre = estado.nombre.trim()
        val asignatura = estado.asignatura.trim()
        val nombresNiveles = estado.niveles.map { it.nombre.trim() }
        val porcentajes = estado.niveles.map { it.porcentaje.toIntOrNull() ?: -1 }

        if (nombre.length < 3) {
            mostrarError("El nombre de la rúbrica debe tener al menos 3 letras.")
            return
        }
        if (asignatura.isEmpty()) {
            mostrarError("Debe ingresar la asignatura.")
            return
        }
        if (estado.criterios.any { it.nombre.isBlank() }) {
            mostrarError("Todos los criterios deben tener nombre.")
            return
        }
        if (estado.criterios.any { (it.puntajeMaximo.toIntOrNull() ?: 0) < 1 }) {
            mostrarError("El puntaje de cada criterio debe ser un número mayor a 0.")
            return
        }
        if (nombresNiveles.any { it.isEmpty() }) {
            mostrarError("Todos los niveles deben tener nombre.")
            return
        }
        if (nombresNiveles.distinct().size != nombresNiveles.size) {
            mostrarError("No se pueden repetir nombres de niveles.")
            return
        }
        if (porcentajes.any { it !in 0..100 }) {
            mostrarError("El porcentaje de cada nivel debe ser un número entre 0 y 100.")
            return
        }
        if (!porcentajes.contains(100)) {
            mostrarError("Debe haber un nivel con 100% para poder llegar al 7.0.")
            return
        }

        val criterios = estado.criterios.map {
            Criterio(it.nombre.trim(), it.descripcion.trim(), it.puntajeMaximo.toInt())
        }
        val niveles = estado.niveles.map {
            Nivel(it.nombre.trim(), it.porcentaje.toInt())
        }.sortedByDescending { it.porcentaje }

        val rubrica = Rubrica(
            id = estado.editandoId ?: 0,
            nombre = nombre,
            asignatura = asignatura,
            criterios = criterios,
            niveles = niveles
        )

        _estado.update { it.copy(procesando = true, error = null, mensaje = null) }

        viewModelScope.launch {
            try {
                repository.guardar(rubrica)
                val rubricas = repository.listar()
                _estado.update {
                    limpiarFormulario(it).copy(
                        rubricas = rubricas,
                        procesando = false,
                        mensaje = "Rúbrica guardada."
                    )
                }
                alTerminar()
            } catch (e: Exception) {
                _estado.update {
                    it.copy(procesando = false, error = "No se pudo guardar la rúbrica. Revisa que el microservicio esté encendido.")
                }
            }
        }
    }

    fun eliminar(id: Long) {
        if (_estado.value.procesando) return
        _estado.update { it.copy(procesando = true, error = null, mensaje = null) }

        viewModelScope.launch {
            try {
                repository.eliminar(id)
                val rubricas = repository.listar()
                _estado.update {
                    it.copy(rubricas = rubricas, procesando = false, mensaje = "Rúbrica eliminada.")
                }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(procesando = false, error = "No se pudo eliminar la rúbrica. Revisa que el microservicio esté encendido.")
                }
            }
        }
    }

    private fun cambiarCriterio(indice: Int, cambio: (CriterioFormulario) -> CriterioFormulario) {
        val criterios = _estado.value.criterios.toMutableList()
        if (indice !in criterios.indices) return
        criterios[indice] = cambio(criterios[indice])
        _estado.update { it.copy(criterios = criterios, error = null, mensaje = null) }
    }

    private fun cambiarNivel(indice: Int, cambio: (NivelFormulario) -> NivelFormulario) {
        val niveles = _estado.value.niveles.toMutableList()
        if (indice !in niveles.indices) return
        niveles[indice] = cambio(niveles[indice])
        _estado.update { it.copy(niveles = niveles, error = null, mensaje = null) }
    }

    private fun mostrarError(texto: String) {
        _estado.update { it.copy(error = texto, mensaje = null) }
    }

    private fun limpiarFormulario(estado: RubricaUiState): RubricaUiState {
        val vacio = RubricaUiState()
        return estado.copy(
            nombre = "",
            asignatura = "",
            criterios = vacio.criterios,
            niveles = vacio.niveles,
            editandoId = null,
            error = null,
            mensaje = null
        )
    }

    class Factory(private val repository: RubricaRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RubricaViewModel::class.java)) {
                return RubricaViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel no reconocido: ${modelClass.name}")
        }
    }
}
