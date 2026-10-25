package com.example.evalapp_grupo12.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.evalapp_grupo12.data.DocumentoRepository
import com.example.evalapp_grupo12.model.CargarErrores
import com.example.evalapp_grupo12.model.CargarUiState
import com.example.evalapp_grupo12.model.Documento
import com.example.evalapp_grupo12.utils.Archivos
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DocumentoViewModel(application: Application) : AndroidViewModel(application) {

    private val contexto = application
    private val repository = DocumentoRepository(application)

    private val _documentos = MutableStateFlow<List<Documento>>(emptyList())
    val documentos: StateFlow<List<Documento>> = _documentos

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    fun cargarDocumentos(usuarioId: Int) {
        viewModelScope.launch {
            _cargando.value = true
            delay(800)
            _documentos.value = repository.obtenerDocumentos(usuarioId)
            _cargando.value = false
        }
    }

    fun eliminarDocumento(documento: Documento) {
        repository.eliminarDocumento(documento.id)
        _documentos.value = repository.obtenerDocumentos(documento.usuarioId)
        mostrarMensaje("Documento eliminado")
    }

    private fun mostrarMensaje(texto: String) {
        viewModelScope.launch {
            _mensaje.value = texto
            delay(2000)
            _mensaje.value = ""
        }
    }

    private val _formulario = MutableStateFlow(CargarUiState())
    val formulario: StateFlow<CargarUiState> = _formulario

    fun onTituloChange(valor: String) {
        _formulario.update { it.copy(titulo = valor, errores = it.errores.copy(titulo = null)) }
    }

    fun onArchivoSeleccionado(uri: Uri?) {
        if (uri == null) return

        try {
            contexto.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (e: Exception) {
        }

        _formulario.update {
            it.copy(
                uri = uri.toString(),
                tipo = Archivos.obtenerTipo(contexto, uri),
                errores = it.errores.copy(archivo = null)
            )
        }
    }

    fun validarFormulario(): Boolean {
        val estadoActual = _formulario.value
        val errores = CargarErrores(
            titulo = if (estadoActual.titulo.length < 3) "El título debe tener al menos 3 caracteres" else null,
            archivo = if (estadoActual.uri.isEmpty()) "Selecciona un archivo PDF o Word" else null
        )

        val hayErrores = listOfNotNull(errores.titulo, errores.archivo).isNotEmpty()

        _formulario.update { it.copy(errores = errores) }

        return !hayErrores
    }

    fun guardarDocumento(usuarioId: Int): Boolean {
        if (!validarFormulario()) return false

        val estadoActual = _formulario.value
        repository.agregarDocumento(
            Documento(
                usuarioId = usuarioId,
                titulo = estadoActual.titulo,
                tipo = estadoActual.tipo,
                uri = estadoActual.uri
            )
        )
        _documentos.value = repository.obtenerDocumentos(usuarioId)
        _formulario.value = CargarUiState()
        mostrarMensaje("Documento cargado correctamente")
        return true
    }
}
