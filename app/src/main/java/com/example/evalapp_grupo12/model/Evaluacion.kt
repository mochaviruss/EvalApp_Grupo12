package com.example.evalapp_grupo12.model

data class Evaluacion(
    val id: Long = 0,
    val alumnoId: Long,
    val alumno: String,
    val curso: String,
    val rubricaId: Long,
    val rubrica: String,
    val fecha: String,
    val puntaje: Int,
    val puntajeMaximo: Int,
    val nota: Double,
    val retroalimentacion: String,
    val criterios: List<CriterioEvaluado>,
    val niveles: List<Nivel>,
    val grupo: String = "",
    val evaluador: String
)
