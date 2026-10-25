package com.example.evalapp_grupo12.model

data class CargarUiState(
    val titulo: String = "",
    val uri: String = "",
    val tipo: String = "",
    val errores: CargarErrores = CargarErrores()
)

data class CargarErrores(
    val titulo: String? = null,
    val archivo: String? = null
)
