package com.example.evalapp_grupo12.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.evalapp_grupo12.ui.components.DocumentoCard
import com.example.evalapp_grupo12.ui.components.MenuApp
import com.example.evalapp_grupo12.viewmodel.DocumentoViewModel
import com.example.evalapp_grupo12.viewmodel.SesionViewModel
import com.example.evalapp_grupo12.viewmodel.VisorViewModel

@Composable
fun DocumentosScreen(
    navController: NavController,
    sesionViewModel: SesionViewModel,
    documentoViewModel: DocumentoViewModel,
    visorViewModel: VisorViewModel
) {
    val usuario by sesionViewModel.usuario.collectAsState()
    val documentos by documentoViewModel.documentos.collectAsState()
    val cargando by documentoViewModel.cargando.collectAsState()
    val mensaje by documentoViewModel.mensaje.collectAsState()

    val usuarioActual = usuario ?: return

    LaunchedEffect(Unit) {
        documentoViewModel.cargarDocumentos(usuarioActual.id)
    }

    MenuApp(navController, usuarioActual, "Mis documentos") {
        Column {

            AnimatedVisibility(
                visible = mensaje != "",
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (cargando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (documentos.isEmpty()) {
                Text(
                    text = "Aún no tienes documentos. Toca + para cargar un PDF o Word.",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(documentos) { documento ->
                        DocumentoCard(
                            documento = documento,
                            onVer = {
                                visorViewModel.verDocumento(documento)
                                navController.navigate("visor")
                            },
                            onEliminar = { documentoViewModel.eliminarDocumento(documento) }
                        )
                    }
                }
            }
        }
    }
}
