package com.example.evalapp_grupo12

import com.example.evalapp_grupo12.logic.DescripcionClima
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DescripcionClimaTest {

    @Test
    fun codigoCeroEsDespejado() {
        assertEquals("Despejado", DescripcionClima.desdeCodigo(0))
    }

    @Test
    fun codigosDeNubes() {
        assertEquals("Parcialmente nublado", DescripcionClima.desdeCodigo(2))
        assertEquals("Nublado", DescripcionClima.desdeCodigo(3))
        assertEquals("Neblina", DescripcionClima.desdeCodigo(45))
    }

    @Test
    fun codigosDeLluvia() {
        assertEquals("Llovizna", DescripcionClima.desdeCodigo(53))
        assertEquals("Lluvia", DescripcionClima.desdeCodigo(63))
        assertEquals("Chubascos", DescripcionClima.desdeCodigo(81))
    }

    @Test
    fun codigosDeNieveYTormenta() {
        assertEquals("Nieve", DescripcionClima.desdeCodigo(73))
        assertEquals("Tormenta", DescripcionClima.desdeCodigo(95))
    }

    @Test
    fun codigoDesconocido() {
        assertEquals("Sin información", DescripcionClima.desdeCodigo(150))
    }

    @Test
    fun despejadoYPocoVientoEsApto() {
        assertTrue(DescripcionClima.esAptoParaTerreno(0, 10.0))
    }

    @Test
    fun lluviaNoEsApta() {
        assertFalse(DescripcionClima.esAptoParaTerreno(63, 10.0))
    }

    @Test
    fun muchoVientoNoEsApto() {
        assertFalse(DescripcionClima.esAptoParaTerreno(0, 45.0))
    }
}
