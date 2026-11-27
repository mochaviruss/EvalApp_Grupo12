package com.example.evalapp_grupo12.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.evalapp_grupo12.model.Alumno
import com.example.evalapp_grupo12.ui.components.MenuApp
import com.example.evalapp_grupo12.viewmodel.AlumnoViewModel
import com.example.evalapp_grupo12.viewmodel.SesionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlumnosScreen(
    navController: NavController,
    sesionViewModel: SesionViewModel,
    alumnoViewModel: AlumnoViewModel
) {
    val usuario by sesionViewModel.usuario.collectAsState()
    val estado by alumnoViewModel.estado.collectAsStateWithLifecycle()
    var alumnoAEliminar by remember { mutableStateOf<Alumno?>(null) }

    val usuarioActual = usuario ?: return
    val editando = estado.editandoId != null

    LaunchedEffect(Unit) {
        alumnoViewModel.cargarAlumnos()
    }

    MenuApp(navController, usuarioActual, "Alumnos") {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (editando) "Editar alumno" else "Nuevo alumno",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = estado.nombre,
                            onValueChange = { alumnoViewModel.cambiarNombre(it) },
                            label = { Text("Nombre completo") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = estado.curso,
                            onValueChange = { alumnoViewModel.cambiarCurso(it) },
                            label = { Text("Curso o sección") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (estado.cursos.isNotEmpty()) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(estado.cursos) { curso ->
                                    FilterChip(
                                        selected = estado.curso == curso,
                                        onClick = { alumnoViewModel.cambiarCurso(curso) },
                                        label = { Text(curso) }
                                    )
                                }
                            }
                        }

                        estado.error?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                        estado.mensaje?.let {
                            Text(it, color = MaterialTheme.colorScheme.primary)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { alumnoViewModel.guardar() },
                                enabled = !estado.procesando,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(if (editando) "Actualizar" else "Registrar")
                            }
                            if (editando) {
                                OutlinedButton(
                                    onClick = { alumnoViewModel.cancelarEdicion() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Cancelar")
                                }
                            }
                        }
                    }
                }
            }

            if (estado.alumnos.isEmpty() && !estado.cargando) {
                item {
                    Text("Aún no hay alumnos registrados.")
                }
            }

            for (curso in estado.cursos) {
                item {
                    Text(
                        text = curso,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(estado.alumnos.filter { it.curso == curso }) { alumno ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(start = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(alumno.nombre, modifier = Modifier.weight(1f))
                            IconButton(onClick = { alumnoViewModel.seleccionarParaEditar(alumno) }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Editar")
                            }
                            IconButton(onClick = { alumnoAEliminar = alumno }) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = "Eliminar",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }

        val alumno = alumnoAEliminar
        if (alumno != null) {
            AlertDialog(
                onDismissRequest = { alumnoAEliminar = null },
                title = { Text("Eliminar alumno") },
                text = { Text("¿Seguro que quieres eliminar a ${alumno.nombre}? Sus evaluaciones no se borran.") },
                confirmButton = {
                    TextButton(onClick = {
                        alumnoViewModel.eliminar(alumno.id)
                        alumnoAEliminar = null
                    }) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { alumnoAEliminar = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
