package com.example.evalapp_grupo12.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.evalapp_grupo12.data.UsuarioRepository
import com.example.evalapp_grupo12.logic.Validaciones
import com.example.evalapp_grupo12.model.LoginErrores
import com.example.evalapp_grupo12.model.LoginUiState
import com.example.evalapp_grupo12.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _estado = MutableStateFlow(LoginUiState())

    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value
        val errores = LoginErrores(
            correo = Validaciones.validarCorreo(estadoActual.correo),
            clave = Validaciones.validarClaveLogin(estadoActual.clave)
        )

        val hayErrores = listOfNotNull(errores.correo, errores.clave).isNotEmpty()

        _estado.update { it.copy(errores = errores) }

        return !hayErrores
    }

    fun iniciarSesion(): Usuario? {
        if (!validarFormulario()) return null

        val estadoActual = _estado.value
        val usuario = repository.buscarUsuario(estadoActual.correo, estadoActual.clave)

        if (usuario == null) {
            _estado.update { it.copy(errores = it.errores.copy(clave = "Correo o contraseña incorrectos")) }
        } else {
            _estado.value = LoginUiState()
        }
        return usuario
    }

    class Factory(private val repository: UsuarioRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                return LoginViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel no reconocido: ${modelClass.name}")
        }
    }
}
