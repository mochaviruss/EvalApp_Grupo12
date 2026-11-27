package com.example.evalapp_grupo12.logic

import kotlin.math.roundToInt

object CalculadoraNota {

    private const val EXIGENCIA = 0.6

    fun calcularNota(puntaje: Int, puntajeMaximo: Int): Double {
        require(puntajeMaximo > 0) { "El puntaje máximo debe ser mayor que cero" }
        require(puntaje in 0..puntajeMaximo) { "El puntaje está fuera de rango" }

        val puntajeAprobacion = puntajeMaximo * EXIGENCIA

        val nota = if (puntaje < puntajeAprobacion) {
            1.0 + 3.0 * puntaje / puntajeAprobacion
        } else {
            4.0 + 3.0 * (puntaje - puntajeAprobacion) / (puntajeMaximo - puntajeAprobacion)
        }

        return (nota * 10).roundToInt() / 10.0
    }

    fun calcularPorcentaje(puntaje: Int, puntajeMaximo: Int): Int {
        require(puntajeMaximo > 0) { "El puntaje máximo debe ser mayor que cero" }
        return (puntaje * 100.0 / puntajeMaximo).roundToInt()
    }

    fun estaAprobado(nota: Double): Boolean = nota >= 4.0

    fun calcularPuntajeCriterio(porcentaje: Int, puntajeMaximo: Int): Int {
        require(porcentaje in 0..100) { "El porcentaje debe estar entre 0 y 100" }
        return (puntajeMaximo * porcentaje / 100.0).roundToInt()
    }
}
