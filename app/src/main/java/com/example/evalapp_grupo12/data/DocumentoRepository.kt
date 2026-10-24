package com.example.evalapp_grupo12.data

import android.content.ContentValues
import android.content.Context
import com.example.evalapp_grupo12.model.Documento

class DocumentoRepository(context: Context) {

    private val baseDatos = BaseDatos(context)

    fun agregarDocumento(documento: Documento) {
        val valores = ContentValues()
        valores.put("usuario_id", documento.usuarioId)
        valores.put("titulo", documento.titulo)
        valores.put("tipo", documento.tipo)
        valores.put("uri", documento.uri)
        baseDatos.writableDatabase.insert("documento", null, valores)
    }

    fun obtenerDocumentos(usuarioId: Int): List<Documento> {
        val lista = mutableListOf<Documento>()
        val cursor = baseDatos.readableDatabase.rawQuery(
            "SELECT id, usuario_id, titulo, tipo, uri FROM documento WHERE usuario_id = ?",
            arrayOf(usuarioId.toString())
        )
        while (cursor.moveToNext()) {
            lista.add(
                Documento(
                    id = cursor.getInt(0),
                    usuarioId = cursor.getInt(1),
                    titulo = cursor.getString(2),
                    tipo = cursor.getString(3),
                    uri = cursor.getString(4)
                )
            )
        }
        cursor.close()
        return lista
    }

    fun eliminarDocumento(id: Int) {
        baseDatos.writableDatabase.delete("documento", "id = ?", arrayOf(id.toString()))
    }
}
