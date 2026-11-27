package com.example.evalapp_grupo12

import com.example.evalapp_grupo12.data.ClimaRepository
import com.example.evalapp_grupo12.model.ClimaActual
import com.example.evalapp_grupo12.model.RespuestaClima
import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClimaRepositoryTest {

    private val api = FakeClimaApiService()
    private val repository = ClimaRepository(api)

    @Test
    fun convierteLaRespuestaEnClima() = runBlocking {
        val clima = repository.obtenerClima()

        assertEquals(18.5, clima.temperatura, 0.001)
        assertEquals(12.0, clima.viento, 0.001)
        assertEquals("Despejado", clima.descripcion)
        assertTrue(clima.aptoParaTerreno)
    }

    @Test
    fun usaLasCoordenadasConfiguradas() = runBlocking {
        repository.obtenerClima()

        assertEquals(ClimaRepository.LATITUD, api.ultimaLatitud, 0.001)
        assertEquals(ClimaRepository.LONGITUD, api.ultimaLongitud, 0.001)
    }

    @Test
    fun conLluviaNoEsAptoParaTerreno() = runBlocking {
        api.respuesta = RespuestaClima(ClimaActual(temperatura = 9.0, viento = 20.0, codigo = 63))

        val clima = repository.obtenerClima()

        assertEquals("Lluvia", clima.descripcion)
        assertFalse(clima.aptoParaTerreno)
    }

    @Test(expected = IOException::class)
    fun sinConexionLanzaError() {
        runBlocking {
            api.sinConexion = true

            repository.obtenerClima()
        }
    }
}
