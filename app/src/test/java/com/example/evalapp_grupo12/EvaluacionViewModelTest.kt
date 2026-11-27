package com.example.evalapp_grupo12

import com.example.evalapp_grupo12.data.EvaluacionRepository
import com.example.evalapp_grupo12.viewmodel.EvaluacionViewModel
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
class EvaluacionViewModelTest {

    private val api = FakeApiService()

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    private fun crearViewModel() = EvaluacionViewModel(EvaluacionRepository(api))

    @Test
    fun alCrearseCargaLasEvaluaciones() {
        api.evaluaciones[1] = DatosPrueba.evaluacion(1)
        api.evaluaciones[2] = DatosPrueba.evaluacion(2)

        val estado = crearViewModel().estado.value

        assertEquals(2, estado.evaluaciones.size)
        assertFalse(estado.cargando)
        assertNull(estado.error)
    }

    @Test
    fun conElServidorApagadoMuestraError() {
        api.servidorApagado = true

        val estado = crearViewModel().estado.value

        assertFalse(estado.cargando)
        assertTrue(estado.evaluaciones.isEmpty())
        assertEquals("No se pudieron cargar las evaluaciones. Revisa que el microservicio esté encendido.", estado.error)
    }

    @Test
    fun actualizarTraeLasNuevas() {
        val viewModel = crearViewModel()
        api.evaluaciones[1] = DatosPrueba.evaluacion(1)

        viewModel.cargarEvaluaciones()

        assertEquals(1, viewModel.estado.value.evaluaciones.size)
    }

    @Test
    fun verDetalleGuardaLaSeleccionada() {
        val viewModel = crearViewModel()

        viewModel.verDetalle(DatosPrueba.evaluacion(5))

        assertEquals(5L, viewModel.estado.value.seleccionada?.id)
    }

    @Test
    fun eliminarQuitaLaEvaluacion() {
        api.evaluaciones[1] = DatosPrueba.evaluacion(1)
        val viewModel = crearViewModel()
        viewModel.verDetalle(DatosPrueba.evaluacion(1))

        viewModel.eliminar(1)

        val estado = viewModel.estado.value
        assertTrue(estado.evaluaciones.isEmpty())
        assertTrue(api.evaluaciones.isEmpty())
        assertNull(estado.seleccionada)
        assertEquals("Evaluación eliminada.", estado.mensaje)
        assertFalse(estado.procesando)
    }

    @Test
    fun eliminarConElServidorApagadoMuestraError() {
        api.evaluaciones[1] = DatosPrueba.evaluacion(1)
        val viewModel = crearViewModel()
        api.servidorApagado = true

        viewModel.eliminar(1)

        assertEquals("No se pudo eliminar. Revisa que el microservicio esté encendido.", viewModel.estado.value.error)
        assertEquals(1, viewModel.estado.value.evaluaciones.size)
        assertFalse(viewModel.estado.value.procesando)
    }
}
