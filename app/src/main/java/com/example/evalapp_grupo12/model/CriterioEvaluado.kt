package com.example.evalapp_grupo12.model

data class CriterioEvaluado(
    val nombre: String,
    val descripcion: String = "",
    val puntajeMaximo: Int,
    val nivel: String = "",
    val puntaje: Int = 0,
    val comentario: String = ""
)
