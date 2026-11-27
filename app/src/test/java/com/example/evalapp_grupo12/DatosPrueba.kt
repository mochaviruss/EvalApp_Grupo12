package com.example.evalapp_grupo12

import com.example.evalapp_grupo12.model.Alumno
import com.example.evalapp_grupo12.model.Criterio
import com.example.evalapp_grupo12.model.CriterioEvaluado
import com.example.evalapp_grupo12.model.Evaluacion
import com.example.evalapp_grupo12.model.Nivel
import com.example.evalapp_grupo12.model.Rubrica

object DatosPrueba {

    val muyBueno = Nivel("Muy buen desempeño", 100)
    val logrado = Nivel("Logrado", 80)
    val aceptable = Nivel("Aceptable", 60)
    val noLogrado = Nivel("No logrado", 0)

    val camila = Alumno(1, "Camila Rojas Soto", "Sección A")
    val diego = Alumno(2, "Diego Muñoz Vera", "Sección A")
    val matias = Alumno(3, "Matías Pérez Lagos", "Sección B")

    val rubrica = Rubrica(
        id = 7,
        nombre = "Presentación oral",
        asignatura = "Comunicación oral",
        criterios = listOf(
            Criterio("Dominio del tema", "Explica sin leer.", 5),
            Criterio("Voz y postura", "Habla fuerte y claro.", 5),
            Criterio("Uso del tiempo", "Respeta el tiempo.", 5),
            Criterio("Responde preguntas", "Contesta con argumentos.", 5)
        ),
        niveles = listOf(muyBueno, logrado, aceptable, noLogrado)
    )

    fun evaluacion(id: Long = 1) = Evaluacion(
        id = id,
        alumnoId = 1,
        alumno = "Camila Rojas Soto",
        curso = "Sección A",
        rubricaId = 7,
        rubrica = "Presentación oral",
        fecha = "2026-11-20 10:00",
        puntaje = 15,
        puntajeMaximo = 20,
        nota = 5.1,
        retroalimentacion = "Buen trabajo",
        criterios = listOf(
            CriterioEvaluado("Dominio del tema", "", 5, "Muy buen desempeño", 5, ""),
            CriterioEvaluado("Voz y postura", "", 5, "Aceptable", 3, "Hablar más fuerte"),
            CriterioEvaluado("Uso del tiempo", "", 5, "Aceptable", 3, ""),
            CriterioEvaluado("Responde preguntas", "", 5, "Logrado", 4, "")
        ),
        niveles = listOf(muyBueno, logrado, aceptable, noLogrado),
        grupo = "",
        evaluador = "Instructor Demo"
    )
}
