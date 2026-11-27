package com.example.evalapp_grupo12.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.evalapp_grupo12.logic.CalculadoraNota
import com.example.evalapp_grupo12.ui.components.MenuApp
import com.example.evalapp_grupo12.viewmodel.EvaluarViewModel
import com.example.evalapp_grupo12.viewmodel.SesionViewModel

@Composable
fun AplicarRubricaScreen(
    navController: NavController,
    sesionViewModel: SesionViewModel,
    evaluarViewModel: EvaluarViewModel
) {
    val usuario by sesionViewModel.usuario.collectAsState()
    val estado by evaluarViewModel.estado.collectAsStateWithLifecycle()
    val contexto = LocalContext.current

    val usuarioActual = usuario ?: return
    val faltan = estado.criterios.size - estado.criteriosEvaluados
    val completa = estado.criterios.isNotEmpty() && faltan == 0
    val titulo = if (estado.editandoId != null) "Editar evaluación" else "Aplicar rúbrica"

    MenuApp(navController, usuarioActual, titulo) {
        Column(modifier = Modifier.fillMaxSize()) {

            if (estado.criterios.isNotEmpty()) {
                val porcentaje = CalculadoraNota.calcularPorcentaje(estado.puntajeObtenido, estado.puntajeMaximo)
                val colorNota = if (CalculadoraNota.estaAprobado(estado.nota)) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(estado.rubricaNombre, fontWeight = FontWeight.Bold)
                            Text(estado.alumnosSeleccionados.joinToString(", ") { it.nombre })
                            Text("Puntaje: ${estado.puntajeObtenido}/${estado.puntajeMaximo} pts ($porcentaje%)")
                            Text("Criterios evaluados: ${estado.criteriosEvaluados} de ${estado.criterios.size}")
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("NOTA", style = MaterialTheme.typography.labelMedium)
                            Text(
                                text = "${estado.nota}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = colorNota
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                if (estado.criterios.isEmpty()) {
                    Text("Primero elige el curso, los alumnos y la rúbrica en Evaluar.")
                }

                estado.criterios.forEachIndexed { indice, criterio ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row {
                                Text(
                                    text = "${indice + 1}. ${criterio.nombre}",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Text("${criterio.puntaje}/${criterio.puntajeMaximo} pts")
                            }

                            if (criterio.descripcion.isNotBlank()) {
                                Text(criterio.descripcion, style = MaterialTheme.typography.bodySmall)
                            }

                            for (nivel in estado.niveles) {
                                val puntos = CalculadoraNota.calcularPuntajeCriterio(nivel.porcentaje, criterio.puntajeMaximo)
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { evaluarViewModel.cambiarNivel(indice, nivel) },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = criterio.nivel == nivel.nombre,
                                        onClick = { evaluarViewModel.cambiarNivel(indice, nivel) }
                                    )
                                    Text("${nivel.nombre} ($puntos pts)")
                                }
                            }

                            OutlinedTextField(
                                value = criterio.comentario,
                                onValueChange = { evaluarViewModel.cambiarComentario(indice, it) },
                                label = { Text("Comentario del criterio (opcional)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = estado.retroalimentacion,
                    onValueChange = { evaluarViewModel.cambiarRetroalimentacion(it) },
                    label = { Text("Comentario general de la evaluación (opcional)") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

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
                        evaluarViewModel.enviar(usuarioActual.nombre) { mensaje ->
                            Toast.makeText(contexto, mensaje, Toast.LENGTH_LONG).show()
                            navController.popBackStack("inicio", false)
                        }
                    },
                    enabled = completa && !estado.procesando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (estado.procesando) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        Text(" Enviando...")
                    } else if (faltan == 1) {
                        Text("Falta 1 criterio por evaluar")
                    } else if (faltan > 1) {
                        Text("Faltan $faltan criterios por evaluar")
                    } else {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                        Text(" Enviar nota y retroalimentación")
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
        }
    }
}
