package com.example.evalapp_grupo12.model

data class AlumnoUiState(
    val alumnos: List<Alumno> = emptyList(),
    val cursos: List<String> = emptyList(),

    val nombre: String = "",
    val curso: String = "",
    val editandoId: Long? = null,

    val cargando: Boolean = true,
    val procesando: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null
)
