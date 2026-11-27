package com.example.evalapp_grupo12.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.evalapp_grupo12.model.Rubrica
import com.example.evalapp_grupo12.ui.components.MenuApp
import com.example.evalapp_grupo12.viewmodel.RubricaViewModel
import com.example.evalapp_grupo12.viewmodel.SesionViewModel

@Composable
fun RubricasScreen(
    navController: NavController,
    sesionViewModel: SesionViewModel,
    rubricaViewModel: RubricaViewModel
) {
    val usuario by sesionViewModel.usuario.collectAsState()
    val estado by rubricaViewModel.estado.collectAsStateWithLifecycle()
    var rubricaAEliminar by remember { mutableStateOf<Rubrica?>(null) }

    val usuarioActual = usuario ?: return

    LaunchedEffect(Unit) {
        rubricaViewModel.cargarRubricas()
    }

    MenuApp(navController, usuarioActual, "Rúbricas") {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Button(
                    onClick = {
                        rubricaViewModel.nuevaRubrica()
                        navController.navigate("editar_rubrica")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null)
                    Text(" Nueva rúbrica")
                }
            }

            item {
                Column {
                    estado.mensaje?.let {
                        Text(it, color = MaterialTheme.colorScheme.primary)
                    }
                    estado.error?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                    if (estado.cargando) {
                        CircularProgressIndicator()
                    }
                    if (estado.rubricas.isEmpty() && !estado.cargando) {
                        Text("No hay rúbricas. Toca Nueva rúbrica para crear una.")
                    }
                }
            }

            items(estado.rubricas) { rubrica ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = rubrica.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(rubrica.asignatura, style = MaterialTheme.typography.bodySmall)

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Text("Criterios", fontWeight = FontWeight.Bold)
                        for (criterio in rubrica.criterios) {
                            Row {
                                Text(criterio.nombre, modifier = Modifier.weight(1f))
                                Text("${criterio.puntajeMaximo} pts")
                            }
                        }
                        Row {
                            Text("Puntaje máximo", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Text("${rubrica.puntajeMaximo()} pts", fontWeight = FontWeight.Bold)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Text("Niveles", fontWeight = FontWeight.Bold)
                        Text(
                            text = rubrica.niveles.joinToString(" · ") { "${it.nombre} ${it.porcentaje}%" },
                            style = MaterialTheme.typography.bodySmall
                        )

                        Row {
                            TextButton(onClick = {
                                rubricaViewModel.seleccionarParaEditar(rubrica)
                                navController.navigate("editar_rubrica")
                            }) {
                                Icon(Icons.Filled.Edit, contentDescription = null)
                                Text(" Editar")
                            }
                            TextButton(onClick = { rubricaAEliminar = rubrica }) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Text(" Eliminar", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }

        val rubrica = rubricaAEliminar
        if (rubrica != null) {
            AlertDialog(
                onDismissRequest = { rubricaAEliminar = null },
                title = { Text("Eliminar rúbrica") },
                text = { Text("¿Seguro que quieres eliminar la rúbrica ${rubrica.nombre}? Las evaluaciones ya hechas no se borran.") },
                confirmButton = {
                    TextButton(onClick = {
                        rubricaViewModel.eliminar(rubrica.id)
                        rubricaAEliminar = null
                    }) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { rubricaAEliminar = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
