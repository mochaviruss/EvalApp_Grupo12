package com.example.evalapp_grupo12.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.evalapp_grupo12.logic.CalculadoraNota
import com.example.evalapp_grupo12.model.Evaluacion

@Composable
fun EvaluacionCard(
    evaluacion: Evaluacion,
    onVerDetalle: () -> Unit
) {
    val porcentaje = CalculadoraNota.calcularPorcentaje(evaluacion.puntaje, evaluacion.puntajeMaximo)
    val colorNota = if (CalculadoraNota.estaAprobado(evaluacion.nota)) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = evaluacion.alumno,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Curso: ${evaluacion.curso}")
                    Text("Fecha: ${evaluacion.fecha}")
                    Text("Rúbrica: ${evaluacion.rubrica}")
                    if (evaluacion.grupo.isNotBlank()) {
                        Text(
                            text = "En grupo: ${evaluacion.grupo}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("NOTA", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "${evaluacion.nota}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorNota
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Puntaje: ${evaluacion.puntaje}/${evaluacion.puntajeMaximo} pts ($porcentaje%)",
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onVerDetalle) {
                    Icon(Icons.Filled.Visibility, contentDescription = null)
                    Text(" Ver Detalle")
                }
            }
        }
    }
}
