package com.example.evalapp_grupo12.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.evalapp_grupo12.data.UsuarioRepository
import com.example.evalapp_grupo12.logic.Validaciones
import com.example.evalapp_grupo12.model.EditarUsuarioErrores
import com.example.evalapp_grupo12.model.EditarUsuarioUiState
import com.example.evalapp_grupo12.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EditarUsuarioViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _estado = MutableStateFlow(EditarUsuarioUiState())
    val estado: StateFlow<EditarUsuarioUiState> = _estado.asStateFlow()

    fun cargarDatos(usuario: Usuario) {
        _estado.value = EditarUsuarioUiState(
            nombre = usuario.nombre,
            clave = usuario.clave,
            rol = usuario.rol
        )
    }

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun onRolChange(valor: String) {
        _estado.update { it.copy(rol = valor) }
    }

    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value
        val errores = EditarUsuarioErrores(
            nombre = Validaciones.validarNombre(estadoActual.nombre),
            clave = Validaciones.validarClave(estadoActual.clave)
        )

        val hayErrores = listOfNotNull(
            errores.nombre,
            errores.clave
        ).isNotEmpty()

        _estado.update { it.copy(errores = errores) }

        return !hayErrores
    }

    fun guardarCambios(usuarioActual: Usuario): Usuario? {
        if (!validarFormulario()) return null

        val estadoActual = _estado.value
        val usuarioEditado = usuarioActual.copy(
            nombre = estadoActual.nombre,
            clave = estadoActual.clave,
            rol = estadoActual.rol
        )
        repository.actualizar(usuarioEditado)
        return usuarioEditado
    }

    class Factory(private val repository: UsuarioRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EditarUsuarioViewModel::class.java)) {
                return EditarUsuarioViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel no reconocido: ${modelClass.name}")
        }
    }
}
