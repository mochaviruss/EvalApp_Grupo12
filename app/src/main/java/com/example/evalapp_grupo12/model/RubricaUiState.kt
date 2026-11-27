package com.example.evalapp_grupo12.model

data class RubricaUiState(
    val rubricas: List<Rubrica> = emptyList(),

    val nombre: String = "",
    val asignatura: String = "",
    val criterios: List<CriterioFormulario> = listOf(CriterioFormulario()),
    val niveles: List<NivelFormulario> = listOf(
        NivelFormulario("Muy buen desempeño", "100"),
        NivelFormulario("Logrado", "80"),
        NivelFormulario("Aceptable", "60"),
        NivelFormulario("No logrado", "0")
    ),
    val editandoId: Long? = null,

    val cargando: Boolean = true,
    val procesando: Boolean = false,
    val error: String? = null,
    val mensaje: String? = null
)
