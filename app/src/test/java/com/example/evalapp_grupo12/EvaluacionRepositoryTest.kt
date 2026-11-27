package com.example.evalapp_grupo12

import com.example.evalapp_grupo12.data.EvaluacionRepository
import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EvaluacionRepositoryTest {

    private val api = FakeApiService()
    private val repository = EvaluacionRepository(api)

    @Test
    fun listarTraeLasEvaluacionesDelMicroservicio() = runBlocking {
        api.evaluaciones[1] = DatosPrueba.evaluacion()

        val evaluaciones = repository.listar()

        assertEquals(listOf(DatosPrueba.evaluacion()), evaluaciones)
    }

    @Test(expected = IOException::class)
    fun listarConElServidorApagadoLanzaError() {
        runBlocking {
            api.servidorApagado = true

            repository.listar()
        }
    }

    @Test
    fun guardarEvaluacionNuevaUsaPost() = runBlocking {
        repository.guardar(DatosPrueba.evaluacion(id = 0))

        assertEquals(1, api.evaluaciones.size)
        assertTrue(api.evaluaciones.values.first().id > 0)
    }

    @Test
    fun guardarEvaluacionExistenteUsaPut() = runBlocking {
        api.evaluaciones[1] = DatosPrueba.evaluacion()

        repository.guardar(DatosPrueba.evaluacion().copy(nota = 6.0))

        assertEquals(6.0, api.evaluaciones[1]!!.nota, 0.001)
        assertEquals(1, api.evaluaciones.size)
    }

    @Test(expected = IOException::class)
    fun guardarConElServidorApagadoLanzaError() {
        runBlocking {
            api.servidorApagado = true

            repository.guardar(DatosPrueba.evaluacion(id = 0))
        }
    }

    @Test
    fun eliminarBorraEnElMicroservicio() = runBlocking {
        api.evaluaciones[1] = DatosPrueba.evaluacion()

        repository.eliminar(1)

        assertTrue(api.evaluaciones.isEmpty())
    }
}
