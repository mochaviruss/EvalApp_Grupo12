package com.example.evalapp_grupo12.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.evalapp_grupo12.data.UsuarioRepository
import com.example.evalapp_grupo12.model.LoginErrores
import com.example.evalapp_grupo12.model.LoginUiState
import com.example.evalapp_grupo12.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UsuarioRepository(application)

    private val _estado = MutableStateFlow(LoginUiState())

    val estado: StateFlow<LoginUiState> = _estado

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value
        val errores = LoginErrores(
            correo = if (!estadoActual.correo.contains("@")) "Correo inválido" else null,
            clave = if (estadoActual.clave.isBlank()) "La contraseña es obligatoria" else null
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
}
