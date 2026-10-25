package com.example.evalapp_grupo12.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.evalapp_grupo12.ui.theme.ColorWord
import com.example.evalapp_grupo12.viewmodel.VisorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisorScreen(
    navController: NavController,
    viewModel: VisorViewModel
) {
    val documento by viewModel.documento.collectAsState()
    val paginaActual by viewModel.paginaActual.collectAsState()
    val totalPaginas by viewModel.totalPaginas.collectAsState()
    val imagenPagina by viewModel.imagenPagina.collectAsState()
    val contexto = LocalContext.current

    val documentoActual = documento ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(documentoActual.titulo) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (documentoActual.tipo == "PDF") {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.paginaAnterior() }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Página anterior")
                    }
                    Text("Página ${paginaActual + 1} de $totalPaginas")
                    IconButton(onClick = { viewModel.paginaSiguiente() }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Página siguiente")
                    }
                }

                val imagen = imagenPagina
                if (imagen != null) {
                    Image(
                        bitmap = imagen.asImageBitmap(),
                        contentDescription = "Página del documento",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    Text("No se pudo abrir el documento", color = MaterialTheme.colorScheme.error)
                }

            } else {
                Icon(
                    imageVector = Icons.Filled.Description,
                    contentDescription = null,
                    tint = ColorWord,
                    modifier = Modifier.size(120.dp)
                )
                Text("Los documentos Word se abren con otra aplicación del teléfono.")
                Button(onClick = {
                    val uri = Uri.parse(documentoActual.uri)
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(uri, contexto.contentResolver.getType(uri))
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                    try {
                        contexto.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(contexto, "No hay una aplicación para abrir Word", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Abrir documento")
                }
            }
        }
    }
}
