package com.example.evalapp_grupo12.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.evalapp_grupo12.data.AlumnoRepository
import com.example.evalapp_grupo12.model.Alumno
import com.example.evalapp_grupo12.model.AlumnoUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlumnoViewModel(private val repository: AlumnoRepository) : ViewModel() {

    private val _estado = MutableStateFlow(AlumnoUiState())
    val estado: StateFlow<AlumnoUiState> = _estado.asStateFlow()

    init {
        cargarAlumnos()
    }

    fun cargarAlumnos() {
        _estado.update { it.copy(cargando = true, error = null) }

        viewModelScope.launch {
            try {
                val alumnos = repository.listar()
                _estado.update { it.copy(alumnos = alumnos, cursos = cursosDe(alumnos), cargando = false) }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(cargando = false, error = "No se pudieron cargar los alumnos. Revisa que el microservicio esté encendido.")
                }
            }
        }
    }

    fun cambiarNombre(valor: String) {
        _estado.update { it.copy(nombre = valor, error = null, mensaje = null) }
    }

    fun cambiarCurso(valor: String) {
        _estado.update { it.copy(curso = valor, error = null, mensaje = null) }
    }

    fun seleccionarParaEditar(alumno: Alumno) {
        _estado.update {
            it.copy(
                nombre = alumno.nombre,
                curso = alumno.curso,
                editandoId = alumno.id,
                error = null,
                mensaje = null
            )
        }
    }

    fun cancelarEdicion() {
        _estado.update { limpiarFormulario(it) }
    }

    fun guardar() {
        val estado = _estado.value
        if (estado.procesando) return

        val nombre = estado.nombre.trim()
        val curso = estado.curso.trim()

        if (nombre.length < 3) {
            mostrarError("El nombre del alumno debe tener al menos 3 letras.")
            return
        }
        if (curso.isEmpty()) {
            mostrarError("Debe ingresar el curso o sección.")
            return
        }

        _estado.update { it.copy(procesando = true, error = null, mensaje = null) }

        viewModelScope.launch {
            try {
                val editando = estado.editandoId != null
                repository.guardar(Alumno(id = estado.editandoId ?: 0, nombre = nombre, curso = curso))
                val alumnos = repository.listar()
                _estado.update {
                    limpiarFormulario(it).copy(
                        alumnos = alumnos,
                        cursos = cursosDe(alumnos),
                        procesando = false,
                        mensaje = if (editando) "Alumno actualizado." else "Alumno registrado."
                    )
                }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(procesando = false, error = "No se pudo guardar. Revisa que el microservicio esté encendido.")
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
                val alumnos = repository.listar()
                _estado.update {
                    val nuevoEstado = if (it.editandoId == id) limpiarFormulario(it) else it
                    nuevoEstado.copy(
                        alumnos = alumnos,
                        cursos = cursosDe(alumnos),
                        procesando = false,
                        mensaje = "Alumno eliminado."
                    )
                }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(procesando = false, error = "No se pudo eliminar. Revisa que el microservicio esté encendido.")
                }
            }
        }
    }

    private fun cursosDe(alumnos: List<Alumno>): List<String> =
        alumnos.map { it.curso }.distinct().sorted()

    private fun mostrarError(texto: String) {
        _estado.update { it.copy(error = texto, mensaje = null) }
    }

    private fun limpiarFormulario(estado: AlumnoUiState): AlumnoUiState = estado.copy(
        nombre = "",
        curso = "",
        editandoId = null,
        error = null,
        mensaje = null
    )

    class Factory(private val repository: AlumnoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AlumnoViewModel::class.java)) {
                return AlumnoViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel no reconocido: ${modelClass.name}")
        }
    }
}
