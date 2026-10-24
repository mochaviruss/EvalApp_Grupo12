package com.example.evalapp_grupo12.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.evalapp_grupo12.data.UsuarioRepository
import com.example.evalapp_grupo12.model.EditarUsuarioErrores
import com.example.evalapp_grupo12.model.EditarUsuarioUiState
import com.example.evalapp_grupo12.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class EditarUsuarioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UsuarioRepository(application)

    private val _estado = MutableStateFlow(EditarUsuarioUiState())
    val estado: StateFlow<EditarUsuarioUiState> = _estado

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
            nombre = if (estadoActual.nombre.length < 3) "Debe tener al menos 3 letras" else null,
            clave = if (estadoActual.clave.length < 6) "Debe tener al menos 6 caracteres" else null
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
}
