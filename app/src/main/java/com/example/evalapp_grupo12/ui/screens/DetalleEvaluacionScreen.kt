package com.example.evalapp_grupo12.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.evalapp_grupo12.logic.CalculadoraNota
import com.example.evalapp_grupo12.ui.components.MenuApp
import com.example.evalapp_grupo12.viewmodel.EvaluacionViewModel
import com.example.evalapp_grupo12.viewmodel.EvaluarViewModel
import com.example.evalapp_grupo12.viewmodel.SesionViewModel

@Composable
fun DetalleEvaluacionScreen(
    navController: NavController,
    sesionViewModel: SesionViewModel,
    evaluacionViewModel: EvaluacionViewModel,
    evaluarViewModel: EvaluarViewModel
) {
    val usuario by sesionViewModel.usuario.collectAsState()
    val estado by evaluacionViewModel.estado.collectAsStateWithLifecycle()
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    val usuarioActual = usuario ?: return
    val evaluacion = estado.seleccionada ?: return

    MenuApp(navController, usuarioActual, "Detalle de evaluación") {
        val porcentaje = CalculadoraNota.calcularPorcentaje(evaluacion.puntaje, evaluacion.puntajeMaximo)
        val aprobado = CalculadoraNota.estaAprobado(evaluacion.nota)
        val colorNota = if (aprobado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = evaluacion.alumno,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Curso: ${evaluacion.curso}")
                        Text("Fecha: ${evaluacion.fecha}")
                        Text("Rúbrica: ${evaluacion.rubrica}")
                        Text("Evaluador: ${evaluacion.evaluador}")
                        if (evaluacion.grupo.isNotBlank()) {
                            Text("En grupo con: ${evaluacion.grupo}", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("NOTA", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = "${evaluacion.nota}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = colorNota
                        )
                        Text(if (aprobado) "Aprobado" else "Reprobado", color = colorNota)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Puntaje: ${evaluacion.puntaje}/${evaluacion.puntajeMaximo} pts ($porcentaje%)")

            Spacer(modifier = Modifier.height(16.dp))

            Text("Resultado por criterio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            for (criterio in evaluacion.criterios) {
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    Text(criterio.nombre, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("${criterio.puntaje}/${criterio.puntajeMaximo} pts")
                }
                Text(
                    text = criterio.nivel.ifEmpty { "Sin evaluar" },
                    color = MaterialTheme.colorScheme.primary
                )
                LinearProgressIndicator(
                    progress = { criterio.puntaje.toFloat() / criterio.puntajeMaximo },
                    modifier = Modifier.fillMaxWidth()
                )
                if (criterio.comentario.isNotBlank()) {
                    Text(
                        text = "Comentario: ${criterio.comentario}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Comentario general", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(evaluacion.retroalimentacion.ifBlank { "Sin comentarios." })

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        evaluarViewModel.editar(evaluacion)
                        navController.navigate("aplicar_rubrica")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = null)
                    Text(" Editar")
                }
                Button(
                    onClick = { mostrarConfirmacion = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = null)
                    Text(" Eliminar")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }

        if (mostrarConfirmacion) {
            AlertDialog(
                onDismissRequest = { mostrarConfirmacion = false },
                title = { Text("Eliminar evaluación") },
                text = { Text("¿Seguro que quieres eliminar la evaluación de ${evaluacion.alumno}?") },
                confirmButton = {
                    TextButton(onClick = {
                        mostrarConfirmacion = false
                        evaluacionViewModel.eliminar(evaluacion.id)
                        navController.popBackStack()
                    }) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarConfirmacion = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
