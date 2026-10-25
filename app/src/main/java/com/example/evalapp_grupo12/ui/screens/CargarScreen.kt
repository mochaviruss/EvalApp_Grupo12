package com.example.evalapp_grupo12.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.evalapp_grupo12.utils.Archivos
import com.example.evalapp_grupo12.viewmodel.DocumentoViewModel
import com.example.evalapp_grupo12.viewmodel.SesionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CargarScreen(
    navController: NavController,
    sesionViewModel: SesionViewModel,
    documentoViewModel: DocumentoViewModel
) {
    val usuario by sesionViewModel.usuario.collectAsState()
    val estado by documentoViewModel.formulario.collectAsState()

    val selectorArchivo = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> documentoViewModel.onArchivoSeleccionado(uri) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cargar documento") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = estado.titulo,
                onValueChange = { documentoViewModel.onTituloChange(it) },
                label = { Text("Título del documento") },
                leadingIcon = { Icon(Icons.Filled.Title, contentDescription = null) },
                trailingIcon = {
                    if (estado.errores.titulo != null) {
                        Icon(Icons.Filled.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                    }
                },
                isError = estado.errores.titulo != null,
                supportingText = {
                    estado.errores.titulo?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedButton(
                onClick = { selectorArchivo.launch(Archivos.TIPOS_PERMITIDOS) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.AttachFile, contentDescription = null)
                Text(" Seleccionar archivo (PDF o Word)")
            }

            AnimatedVisibility(
                visible = estado.uri != "",
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text("Archivo ${estado.tipo} seleccionado", color = MaterialTheme.colorScheme.primary)
            }

            estado.errores.archivo?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Button(
                onClick = {
                    val usuarioActual = usuario
                    if (usuarioActual != null && documentoViewModel.guardarDocumento(usuarioActual.id)) {
                        navController.navigate("documentos") {
                            popUpTo("inicio")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar documento")
            }
        }
    }
}
