package com.example.evalapp_grupo12.ui.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
fun InicioScreen(
    navController: NavController,
    sesionViewModel: SesionViewModel,
    evaluacionViewModel: EvaluacionViewModel,
    evaluarViewModel: EvaluarViewModel
) {
    val usuario by sesionViewModel.usuario.collectAsState()
    val estado by evaluacionViewModel.estado.collectAsStateWithLifecycle()
    var mostrarAyuda by remember { mutableStateOf(false) }
    val contexto = LocalContext.current

    val usuarioActual = usuario ?: return

    LaunchedEffect(Unit) {
        evaluacionViewModel.cargarEvaluaciones()
    }

    val aprobadas = estado.evaluaciones.count { CalculadoraNota.estaAprobado(it.nota) }
    val ultimas = estado.evaluaciones.take(3)

    MenuApp(navController, usuarioActual, "Inicio") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "¡Hola, ${usuarioActual.nombre}!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text("Rol: ${usuarioActual.rol}")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    evaluarViewModel.nuevaEvaluacion()
                    navController.navigate("evaluar")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
            ) {
                Icon(Icons.Filled.EditNote, contentDescription = null, modifier = Modifier.size(32.dp))
                Text(" EVALUAR ALUMNO", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ResumenCard("Evaluaciones", "${estado.evaluaciones.size}", Modifier.weight(1f))
                ResumenCard("Aprobadas", "$aprobadas", Modifier.weight(1f))
            }

            estado.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Últimas evaluaciones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { navController.navigate("historial") }) {
                    Text("Ver todas")
                }
            }

            if (ultimas.isEmpty()) {
                Text("Aún no hay evaluaciones. Toca Evaluar alumno para empezar.")
            }

            for (evaluacion in ultimas) {
                val colorNota = if (CalculadoraNota.estaAprobado(evaluacion.nota)) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            evaluacionViewModel.verDetalle(evaluacion)
                            navController.navigate("detalle_evaluacion")
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(evaluacion.alumno, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${evaluacion.rubrica} · ${evaluacion.fecha}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Text(
                            text = "${evaluacion.nota}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorNota
                        )
                    }
                }
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
                        Text("1. Toca Evaluar alumno y elige el curso.")
                        Text("2. Marca al alumno, o a varios si es un trabajo en grupo.")
                        Text("3. Elige la rúbrica y toca Aplicar rúbrica.")
                        Text("4. En cada criterio elige el nivel. La nota cambia al tiro.")
                        Text("5. Cuando estén todos los criterios, toca Enviar.")
                        Text("6. En Rúbricas y Alumnos puedes crear, editar y eliminar.")
                        Text("7. Los documentos están en el menú lateral.")
                    }

                    TextButton(onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "+56912345678", null))

                        try {
                            contexto.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            Toast.makeText(contexto, "No hay una aplicación de teléfono disponible", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(Icons.Filled.Phone, contentDescription = null)
                        Text(" Llamar a soporte")
                    }
                }
            }
        }
    }
}

@Composable
fun ResumenCard(titulo: String, valor: String, modifier: Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = valor,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(titulo, style = MaterialTheme.typography.bodySmall)
        }
    }
}
