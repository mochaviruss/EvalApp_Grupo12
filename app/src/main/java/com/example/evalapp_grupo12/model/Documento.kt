package com.example.evalapp_grupo12.model

data class Documento(
    val id: Int = 0,
    val usuarioId: Int,
    val titulo: String,
    val tipo: String,
    val uri: String
)
