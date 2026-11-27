package com.example.evalapp_grupo12.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.evalapp_grupo12.ui.components.MenuApp
import com.example.evalapp_grupo12.viewmodel.AlumnoViewModel
import com.example.evalapp_grupo12.viewmodel.EvaluarViewModel
import com.example.evalapp_grupo12.viewmodel.RubricaViewModel
import com.example.evalapp_grupo12.viewmodel.SesionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EvaluarScreen(
    navController: NavController,
    sesionViewModel: SesionViewModel,
    evaluarViewModel: EvaluarViewModel,
    alumnoViewModel: AlumnoViewModel,
    rubricaViewModel: RubricaViewModel
) {
    val usuario by sesionViewModel.usuario.collectAsState()
    val estado by evaluarViewModel.estado.collectAsStateWithLifecycle()
    val estadoAlumnos by alumnoViewModel.estado.collectAsStateWithLifecycle()
    val estadoRubricas by rubricaViewModel.estado.collectAsStateWithLifecycle()

    val usuarioActual = usuario ?: return
    val alumnosDelCurso = estadoAlumnos.alumnos.filter { it.curso == estado.curso }

    LaunchedEffect(Unit) {
        alumnoViewModel.cargarAlumnos()
        rubricaViewModel.cargarRubricas()
    }

    MenuApp(navController, usuarioActual, "Evaluar") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("1. Elige el curso", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            estadoAlumnos.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                TextButton(onClick = { alumnoViewModel.cargarAlumnos() }) {
                    Text("Reintentar")
                }
            }

            if (estadoAlumnos.cargando) {
                CircularProgressIndicator()
            } else if (estadoAlumnos.cursos.isEmpty()) {
                Text("No hay alumnos registrados.")
                TextButton(onClick = { navController.navigate("alumnos") }) {
                    Text("Registrar alumnos")
                }
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(estadoAlumnos.cursos) { curso ->
                        FilterChip(
                            selected = estado.curso == curso,
                            onClick = { evaluarViewModel.seleccionarCurso(curso) },
                            label = { Text(curso) }
                        )
                    }
                }
            }

            if (estado.curso.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Text("2. Elige el alumno", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    text = "Si es un trabajo en grupo, marca a todos los integrantes.",
                    style = MaterialTheme.typography.bodySmall
                )

                for (alumno in alumnosDelCurso) {
                    val seleccionado = estado.alumnosSeleccionados.any { it.id == alumno.id }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { evaluarViewModel.cambiarSeleccionAlumno(alumno) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = seleccionado,
                            onCheckedChange = { evaluarViewModel.cambiarSeleccionAlumno(alumno) }
                        )
                        Text(alumno.nombre)
                    }
                }

                if (estado.alumnosSeleccionados.size > 1) {
                    Text(
                        text = "Evaluación en grupo: ${estado.alumnosSeleccionados.size} alumnos",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("3. Elige la rúbrica", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

            if (estadoRubricas.rubricas.isEmpty() && !estadoRubricas.cargando) {
                Text("No hay rúbricas. Crea una en la sección Rúbricas.")
            }

            for (rubrica in estadoRubricas.rubricas) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { evaluarViewModel.seleccionarRubrica(rubrica) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = estado.rubricaId == rubrica.id,
                        onClick = { evaluarViewModel.seleccionarRubrica(rubrica) }
                    )
                    Column {
                        Text(rubrica.nombre)
                        Text(
                            text = "${rubrica.asignatura} · ${rubrica.criterios.size} criterios · " +
                                    "${rubrica.puntajeMaximo()} pts · ${rubrica.niveles.size} niveles",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
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
                    if (evaluarViewModel.validarSeleccion()) {
                        navController.navigate("aplicar_rubrica")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Checklist, contentDescription = null)
                Text(" Aplicar rúbrica")
            }
        }
    }
}
