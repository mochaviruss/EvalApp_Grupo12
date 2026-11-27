package com.example.evalapp_grupo12

import com.example.evalapp_grupo12.data.RubricaRepository
import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RubricaRepositoryTest {

    private val api = FakeApiService()
    private val repository = RubricaRepository(api)

    @Test
    fun listarTraeLasRubricasDelMicroservicio() = runBlocking {
        api.rubricas[7] = DatosPrueba.rubrica

        val rubricas = repository.listar()

        assertEquals(listOf(DatosPrueba.rubrica), rubricas)
    }

    @Test(expected = IOException::class)
    fun listarConElServidorApagadoLanzaError() {
        runBlocking {
            api.servidorApagado = true

            repository.listar()
        }
    }

    @Test
    fun guardarRubricaNuevaUsaPost() = runBlocking {
        repository.guardar(DatosPrueba.rubrica.copy(id = 0))

        assertEquals(1, api.rubricas.size)
        assertTrue(api.rubricas.values.first().id > 0)
    }

    @Test
    fun guardarRubricaExistenteUsaPut() = runBlocking {
        api.rubricas[7] = DatosPrueba.rubrica

        repository.guardar(DatosPrueba.rubrica.copy(nombre = "Disertación"))

        assertEquals("Disertación", api.rubricas[7]!!.nombre)
        assertEquals(1, api.rubricas.size)
    }

    @Test
    fun eliminarBorraEnElMicroservicio() = runBlocking {
        api.rubricas[7] = DatosPrueba.rubrica

        repository.eliminar(7)

        assertTrue(api.rubricas.isEmpty())
    }
}
