package com.example.evalapp_grupo12.data

import com.example.evalapp_grupo12.model.Usuario

class UsuarioRepository(private val dao: UsuarioDao) {

    fun existeCorreo(correo: String): Boolean = dao.existeCorreo(normalizarCorreo(correo))

    fun registrar(usuario: Usuario) {
        dao.registrar(usuario.copy(nombre = usuario.nombre.trim(), correo = normalizarCorreo(usuario.correo)))
    }

    fun buscarUsuario(correo: String, clave: String): Usuario? =
        dao.buscarUsuario(normalizarCorreo(correo), clave)

    fun actualizar(usuario: Usuario) {
        dao.actualizar(usuario.copy(nombre = usuario.nombre.trim()))
    }

    private fun normalizarCorreo(correo: String): String = correo.trim().lowercase()
}
