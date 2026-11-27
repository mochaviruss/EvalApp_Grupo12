package com.example.evalapp_grupo12.model

data class Rubrica(
    val id: Long = 0,
    val nombre: String,
    val asignatura: String,
    val criterios: List<Criterio>,
    val niveles: List<Nivel>
) {
    fun puntajeMaximo(): Int = criterios.sumOf { it.puntajeMaximo }
}
