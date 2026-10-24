package com.example.evalapp_grupo12.model

data class EditarUsuarioUiState(
    val nombre: String = "",
    val clave: String = "",
    val rol: String = "",
    val errores: EditarUsuarioErrores = EditarUsuarioErrores()
)

data class EditarUsuarioErrores(
    val nombre: String? = null,
    val clave: String? = null
)
