package com.example.evalapp_grupo12.data

import com.example.evalapp_grupo12.model.Usuario

interface UsuarioDao {

    fun existeCorreo(correo: String): Boolean

    fun registrar(usuario: Usuario)

    fun buscarUsuario(correo: String, clave: String): Usuario?

    fun actualizar(usuario: Usuario)
}
