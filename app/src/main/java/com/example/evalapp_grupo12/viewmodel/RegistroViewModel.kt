package com.example.evalapp_grupo12.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.evalapp_grupo12.data.UsuarioRepository
import com.example.evalapp_grupo12.model.RegistroErrores
import com.example.evalapp_grupo12.model.RegistroUiState
import com.example.evalapp_grupo12.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UsuarioRepository(application)

    private val _estado = MutableStateFlow(RegistroUiState())
    val estado: StateFlow<RegistroUiState> = _estado

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
            nombre = if (estadoActual.nombre.length < 3) "Debe tener al menos 3 letras" else null,
            correo = when {
                !estadoActual.correo.contains("@") -> "Correo inválido"
                repository.existeCorreo(estadoActual.correo) -> "Este correo ya está registrado"
                else -> null
            },
            clave = if (estadoActual.clave.length < 6) "Debe tener al menos 6 caracteres" else null,
            confirmarClave = if (estadoActual.confirmarClave != estadoActual.clave) "Las contraseñas no coinciden" else null,
            rol = if (estadoActual.rol.isEmpty()) "Selecciona un rol" else null
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
}
