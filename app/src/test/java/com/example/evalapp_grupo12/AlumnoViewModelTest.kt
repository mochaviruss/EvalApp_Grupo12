package com.example.evalapp_grupo12

import com.example.evalapp_grupo12.data.AlumnoRepository
import com.example.evalapp_grupo12.viewmodel.AlumnoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AlumnoViewModelTest {

    private val api = FakeApiService()

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    private fun crearViewModel() = AlumnoViewModel(AlumnoRepository(api))

    @Test
    fun alCrearseCargaLosAlumnosYLosCursos() {
        api.alumnos[1] = DatosPrueba.camila
        api.alumnos[3] = DatosPrueba.matias

        val estado = crearViewModel().estado.value

        assertEquals(2, estado.alumnos.size)
        assertEquals(listOf("Sección A", "Sección B"), estado.cursos)
        assertFalse(estado.cargando)
        assertNull(estado.error)
    }

    @Test
    fun conElServidorApagadoMuestraError() {
        api.servidorApagado = true

        val estado = crearViewModel().estado.value

        assertFalse(estado.cargando)
        assertTrue(estado.alumnos.isEmpty())
        assertEquals("No se pudieron cargar los alumnos. Revisa que el microservicio esté encendido.", estado.error)
    }

    @Test
    fun guardarSinNombreMuestraError() {
        val viewModel = crearViewModel()
        viewModel.cambiarNombre("Al")

        viewModel.guardar()

        assertEquals("El nombre del alumno debe tener al menos 3 letras.", viewModel.estado.value.error)
    }

    @Test
    fun guardarSinCursoMuestraError() {
        val viewModel = crearViewModel()
        viewModel.cambiarNombre("Ana Torres")

        viewModel.guardar()

        assertEquals("Debe ingresar el curso o sección.", viewModel.estado.value.error)
    }

    @Test
    fun guardarAlumnoNuevo() {
        val viewModel = crearViewModel()
        viewModel.cambiarNombre("  Ana Torres  ")
        viewModel.cambiarCurso("Sección C")

        viewModel.guardar()

        val estado = viewModel.estado.value
        assertEquals("Alumno registrado.", estado.mensaje)
        assertEquals("Ana Torres", estado.alumnos[0].nombre)
        assertEquals(listOf("Sección C"), estado.cursos)
        assertEquals("", estado.nombre)
    }

    @Test
    fun editarAlumno() {
        api.alumnos[1] = DatosPrueba.camila
        val viewModel = crearViewModel()
        viewModel.seleccionarParaEditar(DatosPrueba.camila)
        viewModel.cambiarCurso("Sección B")

        viewModel.guardar()

        assertEquals("Alumno actualizado.", viewModel.estado.value.mensaje)
        assertEquals("Sección B", api.alumnos[1]!!.curso)
        assertNull(viewModel.estado.value.editandoId)
    }

    @Test
    fun guardarConElServidorApagadoMuestraError() {
        val viewModel = crearViewModel()
        api.servidorApagado = true
        viewModel.cambiarNombre("Ana Torres")
        viewModel.cambiarCurso("Sección A")

        viewModel.guardar()

        assertEquals("No se pudo guardar. Revisa que el microservicio esté encendido.", viewModel.estado.value.error)
        assertFalse(viewModel.estado.value.procesando)
    }

    @Test
    fun cancelarEdicionLimpiaElFormulario() {
        val viewModel = crearViewModel()
        viewModel.seleccionarParaEditar(DatosPrueba.camila)

        viewModel.cancelarEdicion()

        assertEquals("", viewModel.estado.value.nombre)
        assertNull(viewModel.estado.value.editandoId)
    }

    @Test
    fun eliminarAlumno() {
        api.alumnos[1] = DatosPrueba.camila
        val viewModel = crearViewModel()
        viewModel.seleccionarParaEditar(DatosPrueba.camila)

        viewModel.eliminar(1)

        assertTrue(viewModel.estado.value.alumnos.isEmpty())
        assertEquals("Alumno eliminado.", viewModel.estado.value.mensaje)
        assertNull(viewModel.estado.value.editandoId)
    }

    @Test
    fun eliminarConElServidorApagadoMuestraError() {
        val viewModel = crearViewModel()
        api.servidorApagado = true

        viewModel.eliminar(1)

        assertEquals("No se pudo eliminar. Revisa que el microservicio esté encendido.", viewModel.estado.value.error)
    }

    @Test
    fun reintentarCuandoElServidorVuelve() {
        api.servidorApagado = true
        val viewModel = crearViewModel()
        api.servidorApagado = false
        api.alumnos[1] = DatosPrueba.camila

        viewModel.cargarAlumnos()

        assertNull(viewModel.estado.value.error)
        assertEquals(1, viewModel.estado.value.alumnos.size)
    }
}
