package com.example.evalapp_grupo12.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.evalapp_grupo12.data.ClimaRepository
import com.example.evalapp_grupo12.model.ClimaUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ClimaViewModel(private val repository: ClimaRepository) : ViewModel() {

    private val _estado = MutableStateFlow(ClimaUiState())
    val estado: StateFlow<ClimaUiState> = _estado.asStateFlow()

    init {
        cargarClima()
    }

    fun cargarClima() {
        _estado.update { it.copy(cargando = true, error = null) }

        viewModelScope.launch {
            try {
                val clima = repository.obtenerClima()
                _estado.update { it.copy(cargando = false, clima = clima) }
            } catch (e: Exception) {
                _estado.update {
                    it.copy(cargando = false, error = "No se pudo obtener el clima. Revisa la conexión a internet.")
                }
            }
        }
    }

    class Factory(private val repository: ClimaRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ClimaViewModel::class.java)) {
                return ClimaViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel no reconocido: ${modelClass.name}")
        }
    }
}
