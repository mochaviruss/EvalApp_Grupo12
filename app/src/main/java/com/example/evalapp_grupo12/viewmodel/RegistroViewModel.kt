package com.example.evalapp_grupo12.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.evalapp_grupo12.data.UsuarioRepository
import com.example.evalapp_grupo12.logic.Validaciones
import com.example.evalapp_grupo12.model.RegistroErrores
import com.example.evalapp_grupo12.model.RegistroUiState
import com.example.evalapp_grupo12.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegistroViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _estado = MutableStateFlow(RegistroUiState())
    val estado: StateFlow<RegistroUiState> = _estado.asStateFlow()

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun onConfirmarClaveChange(valor: String) {
        _estado.update { it.copy(confirmarClave = valor, errores = it.errores.copy(confirmarClave = null)) }
    }

    fun onRolChange(valor: String) {
        _estado.update { it.copy(rol = valor, errores = it.errores.copy(rol = null)) }
    }

    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value
        val errores = RegistroErrores(
            nombre = Validaciones.validarNombre(estadoActual.nombre),
            correo = when {
                Validaciones.validarCorreo(estadoActual.correo) != null -> "Correo inválido"
                repository.existeCorreo(estadoActual.correo) -> "Este correo ya está registrado"
                else -> null
            },
            clave = Validaciones.validarClave(estadoActual.clave),
            confirmarClave = Validaciones.validarConfirmacion(estadoActual.clave, estadoActual.confirmarClave),
            rol = Validaciones.validarRol(estadoActual.rol)
        )

        val hayErrores = listOfNotNull(
            errores.nombre,
            errores.correo,
            errores.clave,
            errores.confirmarClave,
            errores.rol
        ).isNotEmpty()

        _estado.update { it.copy(errores = errores) }

        return !hayErrores
    }

    fun registrar(): Boolean {
        if (!validarFormulario()) return false

        val estadoActual = _estado.value
        repository.registrar(
            Usuario(
                id = 0,
                nombre = estadoActual.nombre,
                correo = estadoActual.correo,
                clave = estadoActual.clave,
                rol = estadoActual.rol
            )
        )
        _estado.value = RegistroUiState()
        return true
    }

    class Factory(private val repository: UsuarioRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegistroViewModel::class.java)) {
                return RegistroViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel no reconocido: ${modelClass.name}")
        }
    }
}
