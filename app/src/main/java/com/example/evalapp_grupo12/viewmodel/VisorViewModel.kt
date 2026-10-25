package com.example.evalapp_grupo12.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.example.evalapp_grupo12.model.Documento
import com.example.evalapp_grupo12.utils.Archivos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class VisorViewModel(application: Application) : AndroidViewModel(application) {

    private val contexto = application

    private val _documento = MutableStateFlow<Documento?>(null)
    val documento: StateFlow<Documento?> = _documento

    private val _paginaActual = MutableStateFlow(0)
    val paginaActual: StateFlow<Int> = _paginaActual

    private val _totalPaginas = MutableStateFlow(0)
    val totalPaginas: StateFlow<Int> = _totalPaginas

    private val _imagenPagina = MutableStateFlow<Bitmap?>(null)
    val imagenPagina: StateFlow<Bitmap?> = _imagenPagina

    fun verDocumento(documento: Documento) {
        _documento.value = documento
        _paginaActual.value = 0
        _totalPaginas.value = 0
        _imagenPagina.value = null

        if (documento.tipo == "PDF") {
            _totalPaginas.value = Archivos.contarPaginas(contexto, Uri.parse(documento.uri))
            cargarPagina()
        }
    }

    fun paginaSiguiente() {
        if (_paginaActual.value < _totalPaginas.value - 1) {
            _paginaActual.value = _paginaActual.value + 1
            cargarPagina()
        }
    }

    fun paginaAnterior() {
        if (_paginaActual.value > 0) {
            _paginaActual.value = _paginaActual.value - 1
            cargarPagina()
        }
    }

    private fun cargarPagina() {
        val documentoActual = _documento.value ?: return
        _imagenPagina.value = Archivos.mostrarPagina(contexto, Uri.parse(documentoActual.uri), _paginaActual.value)
    }
}
