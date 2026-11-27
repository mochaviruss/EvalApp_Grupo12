package com.example.evalapp_grupo12.model

data class EvaluacionUiState(
    val evaluaciones: List<Evaluacion> = emptyList(),
    val seleccionada: Evaluacion? = null,

    val cargando: Boolean = true,
    val procesando: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null
)
