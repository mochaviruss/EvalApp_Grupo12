package com.example.evalapp_grupo12.viewmodel

import androidx.lifecycle.ViewModel
import com.example.evalapp_grupo12.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SesionViewModel : ViewModel() {

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    fun iniciarSesion(usuario: Usuario) {
        _usuario.value = usuario
    }

    fun actualizarUsuario(usuario: Usuario) {
        _usuario.value = usuario
    }

    fun cerrarSesion() {
        _usuario.value = null
    }
}
