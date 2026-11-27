package com.example.evalapp_grupo12

import com.example.evalapp_grupo12.data.AlumnoRepository
import com.example.evalapp_grupo12.model.Alumno
import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlumnoRepositoryTest {

    private val api = FakeApiService()
    private val repository = AlumnoRepository(api)

    @Test
    fun listarTraeLosAlumnosDelMicroservicio() = runBlocking {
        api.alumnos[1] = DatosPrueba.camila

        val alumnos = repository.listar()

        assertEquals(listOf(DatosPrueba.camila), alumnos)
    }

    @Test(expected = IOException::class)
    fun listarConElServidorApagadoLanzaError() {
        runBlocking {
            api.servidorApagado = true

            repository.listar()
        }
    }

    @Test
    fun guardarAlumnoNuevoUsaPost() = runBlocking {
        repository.guardar(Alumno(nombre = "Ana Torres", curso = "Sección A"))

        assertEquals(1, api.alumnos.size)
        assertTrue(api.alumnos.values.first().id > 0)
    }

    @Test
    fun guardarAlumnoExistenteUsaPut() = runBlocking {
        api.alumnos[1] = DatosPrueba.camila

        repository.guardar(DatosPrueba.camila.copy(curso = "Sección C"))

        assertEquals("Sección C", api.alumnos[1]!!.curso)
        assertEquals(1, api.alumnos.size)
    }

    @Test
    fun eliminarBorraEnElMicroservicio() = runBlocking {
        api.alumnos[1] = DatosPrueba.camila

        repository.eliminar(1)

        assertTrue(api.alumnos.isEmpty())
    }
}
