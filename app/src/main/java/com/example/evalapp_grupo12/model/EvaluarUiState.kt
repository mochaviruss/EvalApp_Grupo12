package com.example.evalapp_grupo12.model

data class EvaluarUiState(
    val curso: String = "",
    val alumnosSeleccionados: List<Alumno> = emptyList(),
    val rubricaId: Long = 0,
    val rubricaNombre: String = "",

    val criterios: List<CriterioEvaluado> = emptyList(),
    val niveles: List<Nivel> = emptyList(),
    val retroalimentacion: String = "",

    val puntajeObtenido: Int = 0,
    val puntajeMaximo: Int = 0,
    val nota: Double = 1.0,
    val criteriosEvaluados: Int = 0,

    val editandoId: Long? = null,
    val fechaOriginal: String? = null,
    val grupoOriginal: String = "",

    val procesando: Boolean = false,
    val error: String? = null
)
