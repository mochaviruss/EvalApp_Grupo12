package com.example.evalapp_grupo12

import com.example.evalapp_grupo12.data.UsuarioDao
import com.example.evalapp_grupo12.model.Usuario

class FakeUsuarioDao : UsuarioDao {

    val usuarios = mutableListOf<Usuario>()

    override fun existeCorreo(correo: String): Boolean = usuarios.any { it.correo == correo }

    override fun registrar(usuario: Usuario) {
        usuarios.add(usuario.copy(id = usuarios.size + 1))
    }

    override fun buscarUsuario(correo: String, clave: String): Usuario? =
        usuarios.firstOrNull { it.correo == correo && it.clave == clave }

    override fun actualizar(usuario: Usuario) {
        val indice = usuarios.indexOfFirst { it.id == usuario.id }
        if (indice >= 0) {
            usuarios[indice] = usuario
        }
    }
}
