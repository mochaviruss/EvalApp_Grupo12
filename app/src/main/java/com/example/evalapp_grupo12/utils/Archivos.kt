package com.example.evalapp_grupo12.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri

object Archivos {

    val TIPOS_PERMITIDOS = arrayOf(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    )

    fun obtenerTipo(context: Context, uri: Uri): String {
        val tipo = context.contentResolver.getType(uri)
        return if (tipo == "application/pdf") "PDF" else "WORD"
    }

    fun contarPaginas(context: Context, uri: Uri): Int {
        try {
            val archivo = context.contentResolver.openFileDescriptor(uri, "r")
            val lector = PdfRenderer(archivo!!)
            val total = lector.pageCount
            lector.close()
            return total
        } catch (e: Exception) {
            return 0
        }
    }

    fun mostrarPagina(context: Context, uri: Uri, numeroPagina: Int): Bitmap? {
        try {
            val archivo = context.contentResolver.openFileDescriptor(uri, "r")
            val lector = PdfRenderer(archivo!!)
            val pagina = lector.openPage(numeroPagina)

            val imagen = Bitmap.createBitmap(pagina.width * 2, pagina.height * 2, Bitmap.Config.ARGB_8888)
            imagen.eraseColor(Color.WHITE)
            pagina.render(imagen, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            pagina.close()
            lector.close()
            return imagen
        } catch (e: Exception) {
            return null
        }
    }
}
