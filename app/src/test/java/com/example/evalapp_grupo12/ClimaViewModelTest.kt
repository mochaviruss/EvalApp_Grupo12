package com.example.evalapp_grupo12

import com.example.evalapp_grupo12.data.ClimaRepository
import com.example.evalapp_grupo12.viewmodel.ClimaViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ClimaViewModelTest {

    private val api = FakeClimaApiService()

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun alCrearseCargaElClima() {
        val viewModel = ClimaViewModel(ClimaRepository(api))

        val estado = viewModel.estado.value
        assertFalse(estado.cargando)
        assertEquals("Despejado", estado.clima?.descripcion)
        assertNull(estado.error)
    }

    @Test
    fun sinConexionMuestraError() {
        api.sinConexion = true

        val viewModel = ClimaViewModel(ClimaRepository(api))

        val estado = viewModel.estado.value
        assertFalse(estado.cargando)
        assertNull(estado.clima)
        assertEquals("No se pudo obtener el clima. Revisa la conexión a internet.", estado.error)
    }

    @Test
    fun reintentarDespuesDelErrorCargaElClima() {
        api.sinConexion = true
        val viewModel = ClimaViewModel(ClimaRepository(api))
        api.sinConexion = false

        viewModel.cargarClima()

        assertNull(viewModel.estado.value.error)
        assertEquals(18.5, viewModel.estado.value.clima!!.temperatura, 0.001)
    }
}
