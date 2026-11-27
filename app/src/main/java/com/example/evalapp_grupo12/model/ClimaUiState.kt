package com.example.evalapp_grupo12.model

data class ClimaUiState(
    val cargando: Boolean = true,
    val clima: Clima? = null,
    val error: String? = null
)
