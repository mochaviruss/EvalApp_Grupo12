package com.example.evalapp_grupo12.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.evalapp_grupo12.ui.components.MenuApp
import com.example.evalapp_grupo12.viewmodel.SesionViewModel

@Composable
fun InicioScreen(
    navController: NavController,
    sesionViewModel: SesionViewModel
) {
    val usuario by sesionViewModel.usuario.collectAsState()
    var mostrarAyuda by remember { mutableStateOf(false) }

    val usuarioActual = usuario ?: return

    MenuApp(navController, usuarioActual, "Inicio") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.AssignmentTurnedIn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = "¡Hola, ${usuarioActual.nombre}!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text("Bienvenido a EvalApp")
            Text("Rol: ${usuarioActual.rol}")

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate("documentos") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Folder, contentDescription = null)
                Text(" Ver mis documentos")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { navController.navigate("cargar") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.UploadFile, contentDescription = null)
                Text(" Cargar documento")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TextButton(onClick = { mostrarAyuda = !mostrarAyuda }) {
                        Text(if (mostrarAyuda) "Ocultar ayuda" else "¿Cómo usar EvalApp?")
                    }

                    if (mostrarAyuda) {
                        Text("1. Toca + para cargar un documento PDF o Word.")
                        Text("2. En Mis documentos toca el ojo para verlo.")
                        Text("3. Toca el basurero para eliminarlo.")
                    }
                }
            }
        }
    }
}
