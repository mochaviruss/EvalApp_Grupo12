package com.example.evalapp_grupo12.data

import android.content.ContentValues
import android.content.Context
import com.example.evalapp_grupo12.model.Usuario

class UsuarioDaoSQLite(context: Context) : UsuarioDao {

    private val baseDatos = BaseDatos(context)

    override fun existeCorreo(correo: String): Boolean {
        val cursor = baseDatos.readableDatabase.rawQuery(
            "SELECT id FROM usuario WHERE correo = ?",
            arrayOf(correo)
        )
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }

    override fun registrar(usuario: Usuario) {
        val valores = ContentValues()
        valores.put("nombre", usuario.nombre)
        valores.put("correo", usuario.correo)
        valores.put("clave", usuario.clave)
        valores.put("rol", usuario.rol)
        baseDatos.writableDatabase.insert("usuario", null, valores)
    }

    override fun buscarUsuario(correo: String, clave: String): Usuario? {
        val cursor = baseDatos.readableDatabase.rawQuery(
            "SELECT id, nombre, correo, clave, rol FROM usuario WHERE correo = ? AND clave = ?",
            arrayOf(correo, clave)
        )
        var usuario: Usuario? = null
        if (cursor.moveToFirst()) {
            usuario = Usuario(
                id = cursor.getInt(0),
                nombre = cursor.getString(1),
                correo = cursor.getString(2),
                clave = cursor.getString(3),
                rol = cursor.getString(4)
            )
        }
        cursor.close()
        return usuario
    }

    override fun actualizar(usuario: Usuario) {
        val valores = ContentValues()
        valores.put("nombre", usuario.nombre)
        valores.put("clave", usuario.clave)
        valores.put("rol", usuario.rol)
        baseDatos.writableDatabase.update("usuario", valores, "id = ?", arrayOf(usuario.id.toString()))
    }
}
