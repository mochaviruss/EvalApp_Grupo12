package com.example.evalapp_grupo12.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.evalapp_grupo12.ui.components.EvaluacionCard
import com.example.evalapp_grupo12.ui.components.MenuApp
import com.example.evalapp_grupo12.viewmodel.EvaluacionViewModel
import com.example.evalapp_grupo12.viewmodel.SesionViewModel

@Composable
fun HistorialScreen(
    navController: NavController,
    sesionViewModel: SesionViewModel,
    evaluacionViewModel: EvaluacionViewModel
) {
    val usuario by sesionViewModel.usuario.collectAsState()
    val estado by evaluacionViewModel.estado.collectAsStateWithLifecycle()
    var busqueda by remember { mutableStateOf("") }

    val usuarioActual = usuario ?: return

    LaunchedEffect(Unit) {
        evaluacionViewModel.cargarEvaluaciones()
    }

    val filtradas = estado.evaluaciones.filter {
        it.alumno.contains(busqueda, ignoreCase = true) ||
                it.curso.contains(busqueda, ignoreCase = true) ||
                it.rubrica.contains(busqueda, ignoreCase = true)
    }

    MenuApp(navController, usuarioActual, "Historial") {
        Column(modifier = Modifier.fillMaxSize()) {

            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)) {
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { busqueda = it },
                    label = { Text("Buscar por alumno, curso o rúbrica") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedButton(
                    onClick = { evaluacionViewModel.cargarEvaluaciones() },
                    enabled = !estado.cargando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = null)
                    Text(" Actualizar")
                }

                estado.mensaje?.let {
                    Text(it, color = MaterialTheme.colorScheme.primary)
                }

                estado.error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }

            if (estado.cargando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (filtradas.isEmpty()) {
                Text(
                    text = "No hay evaluaciones para mostrar.",
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtradas) { evaluacion ->
                        EvaluacionCard(
                            evaluacion = evaluacion,
                            onVerDetalle = {
                                evaluacionViewModel.verDetalle(evaluacion)
                                navController.navigate("detalle_evaluacion")
                            }
                        )
                    }
                }
            }
        }
    }
}
