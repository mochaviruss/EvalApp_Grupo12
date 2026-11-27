package com.example.evalapp_grupo12.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.evalapp_grupo12.ui.components.MenuApp
import com.example.evalapp_grupo12.viewmodel.RubricaViewModel
import com.example.evalapp_grupo12.viewmodel.SesionViewModel

@Composable
fun EditarRubricaScreen(
    navController: NavController,
    sesionViewModel: SesionViewModel,
    rubricaViewModel: RubricaViewModel
) {
    val usuario by sesionViewModel.usuario.collectAsState()
    val estado by rubricaViewModel.estado.collectAsStateWithLifecycle()

    val usuarioActual = usuario ?: return
    val titulo = if (estado.editandoId != null) "Editar rúbrica" else "Nueva rúbrica"
    val total = estado.criterios.sumOf { it.puntajeMaximo.toIntOrNull() ?: 0 }

    MenuApp(navController, usuarioActual, titulo) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = estado.nombre,
                onValueChange = { rubricaViewModel.cambiarNombre(it) },
                label = { Text("Nombre de la rúbrica") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = estado.asignatura,
                onValueChange = { rubricaViewModel.cambiarAsignatura(it) },
                label = { Text("Asignatura") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Criterios", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            estado.criterios.forEachIndexed { indice, criterio ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = criterio.nombre,
                                onValueChange = { rubricaViewModel.cambiarNombreCriterio(indice, it) },
                                label = { Text("Criterio ${indice + 1}") },
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = criterio.puntajeMaximo,
                                onValueChange = { rubricaViewModel.cambiarPuntajeCriterio(indice, it) },
                                label = { Text("Pts") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.width(72.dp)
                            )
                            IconButton(
                                onClick = { rubricaViewModel.quitarCriterio(indice) },
                                enabled = estado.criterios.size > 1
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = "Quitar criterio")
                            }
                        }
                        OutlinedTextField(
                            value = criterio.descripcion,
                            onValueChange = { rubricaViewModel.cambiarDescripcionCriterio(indice, it) },
                            label = { Text("Qué se evalúa (opcional)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = { rubricaViewModel.agregarCriterio() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Text(" Agregar criterio")
            }

            Text("Puntaje máximo: $total pts", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Niveles de desempeño", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                text = "Cada nivel da un porcentaje del puntaje del criterio. Debe haber uno con 100%.",
                style = MaterialTheme.typography.bodySmall
            )

            estado.niveles.forEachIndexed { indice, nivel ->
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = nivel.nombre,
                        onValueChange = { rubricaViewModel.cambiarNombreNivel(indice, it) },
                        label = { Text("Nivel ${indice + 1}") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = nivel.porcentaje,
                        onValueChange = { rubricaViewModel.cambiarPorcentajeNivel(indice, it) },
                        label = { Text("%") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(72.dp)
                    )
                    IconButton(
                        onClick = { rubricaViewModel.quitarNivel(indice) },
                        enabled = estado.niveles.size > 2
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Quitar nivel")
                    }
                }
            }

            OutlinedButton(
                onClick = { rubricaViewModel.agregarNivel() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Text(" Agregar nivel")
            }

            estado.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    rubricaViewModel.guardar {
                        navController.popBackStack()
                    }
                },
                enabled = !estado.procesando,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (estado.procesando) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Text(" Guardando...")
                } else {
                    Text("Guardar rúbrica")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}
