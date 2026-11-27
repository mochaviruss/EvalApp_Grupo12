package com.example.evalapp_grupo12.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.evalapp_grupo12.data.EvaluacionRepository
import com.example.evalapp_grupo12.model.Evaluacion
import com.example.evalapp_grupo12.model.EvaluacionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EvaluacionViewModel(private val repository: EvaluacionRepository) : ViewModel() {

    private val _estado = MutableStateFlow(EvaluacionUiState())
    val estado: StateFlow<EvaluacionUiState> = _estado.asStateFlow()

    init {
        cargarEvaluaciones()
    }

    fun cargarEvaluaciones() {
        _estado.update { it.copy(cargando = true, error = null) }

        viewModelScope.launch {
            try {
                val evaluaciones = repository.listar()
                _estado.update { it.copy(evaluaciones = evaluaciones, cargando = false) }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(cargando = false, error = "No se pudieron cargar las evaluaciones. Revisa que el microservicio esté encendido.")
                }
            }
        }
    }

    fun verDetalle(evaluacion: Evaluacion) {
        _estado.update { it.copy(seleccionada = evaluacion, error = null, mensaje = null) }
    }

    fun eliminar(id: Long) {
        if (_estado.value.procesando) return
        _estado.update { it.copy(procesando = true, error = null, mensaje = null) }

        viewModelScope.launch {
            try {
                repository.eliminar(id)
                val evaluaciones = repository.listar()
                _estado.update {
                    it.copy(
                        evaluaciones = evaluaciones,
                        seleccionada = null,
                        procesando = false,
                        mensaje = "Evaluación eliminada."
                    )
                }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(procesando = false, error = "No se pudo eliminar. Revisa que el microservicio esté encendido.")
                }
            }
        }
    }

    class Factory(private val repository: EvaluacionRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EvaluacionViewModel::class.java)) {
                return EvaluacionViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel no reconocido: ${modelClass.name}")
        }
    }
}
