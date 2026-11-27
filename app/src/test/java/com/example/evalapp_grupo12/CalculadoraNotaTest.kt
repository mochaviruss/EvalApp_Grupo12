package com.example.evalapp_grupo12

import com.example.evalapp_grupo12.logic.CalculadoraNota
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculadoraNotaTest {

    @Test
    fun puntajeMaximoDaNotaSiete() {
        assertEquals(7.0, CalculadoraNota.calcularNota(16, 16), 0.001)
    }

    @Test
    fun puntajeCeroDaNotaUno() {
        assertEquals(1.0, CalculadoraNota.calcularNota(0, 16), 0.001)
    }

    @Test
    fun sesentaPorCientoDaNotaCuatro() {
        assertEquals(4.0, CalculadoraNota.calcularNota(6, 10), 0.001)
    }

    @Test
    fun mitadDelPuntajeDaTresComaCinco() {
        assertEquals(3.5, CalculadoraNota.calcularNota(2, 4), 0.001)
    }

    @Test
    fun doceDeDieciseisDaCincoComaUno() {
        assertEquals(5.1, CalculadoraNota.calcularNota(12, 16), 0.001)
    }

    @Test
    fun quinceDeDieciseisDaSeisComaCinco() {
        assertEquals(6.5, CalculadoraNota.calcularNota(15, 16), 0.001)
    }

    @Test(expected = IllegalArgumentException::class)
    fun puntajeMaximoCeroProduceExcepcion() {
        CalculadoraNota.calcularNota(0, 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun puntajeNegativoProduceExcepcion() {
        CalculadoraNota.calcularNota(-1, 16)
    }

    @Test(expected = IllegalArgumentException::class)
    fun puntajeMayorAlMaximoProduceExcepcion() {
        CalculadoraNota.calcularNota(17, 16)
    }

    @Test
    fun porcentajeSeRedondeaAlEnteroMasCercano() {
        assertEquals(94, CalculadoraNota.calcularPorcentaje(15, 16))
    }

    @Test
    fun porcentajeDeLaMitadEsCincuenta() {
        assertEquals(50, CalculadoraNota.calcularPorcentaje(2, 4))
    }

    @Test(expected = IllegalArgumentException::class)
    fun porcentajeConMaximoCeroProduceExcepcion() {
        CalculadoraNota.calcularPorcentaje(1, 0)
    }

    @Test
    fun notaCuatroEstaAprobada() {
        assertTrue(CalculadoraNota.estaAprobado(4.0))
    }

    @Test
    fun notaTresComaNueveNoEstaAprobada() {
        assertFalse(CalculadoraNota.estaAprobado(3.9))
    }

    @Test
    fun puntajeDeCriterioSegunPorcentaje() {
        assertEquals(5, CalculadoraNota.calcularPuntajeCriterio(100, 5))
        assertEquals(4, CalculadoraNota.calcularPuntajeCriterio(80, 5))
        assertEquals(3, CalculadoraNota.calcularPuntajeCriterio(60, 5))
        assertEquals(0, CalculadoraNota.calcularPuntajeCriterio(0, 5))
    }

    @Test
    fun puntajeDeCriterioSeRedondea() {
        assertEquals(3, CalculadoraNota.calcularPuntajeCriterio(80, 4))
        assertEquals(6, CalculadoraNota.calcularPuntajeCriterio(60, 10))
    }

    @Test(expected = IllegalArgumentException::class)
    fun porcentajeMayorA100LanzaError() {
        CalculadoraNota.calcularPuntajeCriterio(120, 5)
    }
}
