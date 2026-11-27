package com.example.evalapp_grupo12

import com.example.evalapp_grupo12.data.RubricaRepository
import com.example.evalapp_grupo12.viewmodel.RubricaViewModel
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
class RubricaViewModelTest {

    private val api = FakeApiService()

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    private fun crearViewModel() = RubricaViewModel(RubricaRepository(api))

    private fun llenarFormulario(viewModel: RubricaViewModel) {
        viewModel.cambiarNombre("Informe de laboratorio")
        viewModel.cambiarAsignatura("Ciencias")
        viewModel.cambiarNombreCriterio(0, "Orden")
        viewModel.cambiarDescripcionCriterio(0, "Sigue la estructura pedida")
        viewModel.cambiarPuntajeCriterio(0, "5")
        viewModel.agregarCriterio()
        viewModel.cambiarNombreCriterio(1, "Conclusiones")
        viewModel.cambiarPuntajeCriterio(1, "10")
    }

    @Test
    fun alCrearseCargaLasRubricas() {
        api.rubricas[7] = DatosPrueba.rubrica

        val estado = crearViewModel().estado.value

        assertEquals(1, estado.rubricas.size)
        assertFalse(estado.cargando)
        assertNull(estado.error)
    }

    @Test
    fun conElServidorApagadoMuestraError() {
        api.servidorApagado = true

        val estado = crearViewModel().estado.value

        assertFalse(estado.cargando)
        assertEquals("No se pudieron cargar las rúbricas. Revisa que el microservicio esté encendido.", estado.error)
    }

    @Test
    fun elFormularioParteConUnCriterioYCuatroNiveles() {
        val estado = crearViewModel().estado.value

        assertEquals(1, estado.criterios.size)
        assertEquals(4, estado.niveles.size)
        assertEquals("Muy buen desempeño", estado.niveles[0].nombre)
    }

    @Test
    fun agregarYQuitarCriterios() {
        val viewModel = crearViewModel()

        viewModel.agregarCriterio()
        viewModel.agregarCriterio()
        viewModel.quitarCriterio(0)
        viewModel.quitarCriterio(9)

        assertEquals(2, viewModel.estado.value.criterios.size)
    }

    @Test
    fun noSePuedeQuitarElUltimoCriterio() {
        val viewModel = crearViewModel()

        viewModel.quitarCriterio(0)

        assertEquals(1, viewModel.estado.value.criterios.size)
    }

    @Test
    fun agregarYQuitarNiveles() {
        val viewModel = crearViewModel()

        viewModel.agregarNivel()
        viewModel.cambiarNombreNivel(4, "Incipiente")
        viewModel.cambiarPorcentajeNivel(4, "30")
        viewModel.quitarNivel(1)

        val niveles = viewModel.estado.value.niveles
        assertEquals(4, niveles.size)
        assertEquals("Incipiente", niveles[3].nombre)
        assertEquals("30", niveles[3].porcentaje)
    }

    @Test
    fun siempreQuedanAlMenosDosNiveles() {
        val viewModel = crearViewModel()

        viewModel.quitarNivel(0)
        viewModel.quitarNivel(0)
        viewModel.quitarNivel(0)

        assertEquals(2, viewModel.estado.value.niveles.size)
    }

    @Test
    fun indiceInvalidoNoHaceNada() {
        val viewModel = crearViewModel()

        viewModel.cambiarNombreCriterio(5, "No existe")
        viewModel.cambiarNombreNivel(9, "No existe")

        assertEquals("", viewModel.estado.value.criterios[0].nombre)
        assertEquals("Muy buen desempeño", viewModel.estado.value.niveles[0].nombre)
    }

    @Test
    fun guardarSinNombreMuestraError() {
        val viewModel = crearViewModel()

        viewModel.guardar {}

        assertEquals("El nombre de la rúbrica debe tener al menos 3 letras.", viewModel.estado.value.error)
    }

    @Test
    fun guardarSinAsignaturaMuestraError() {
        val viewModel = crearViewModel()
        viewModel.cambiarNombre("Informe")

        viewModel.guardar {}

        assertEquals("Debe ingresar la asignatura.", viewModel.estado.value.error)
    }

    @Test
    fun guardarConCriterioSinNombreMuestraError() {
        val viewModel = crearViewModel()
        viewModel.cambiarNombre("Informe")
        viewModel.cambiarAsignatura("Ciencias")

        viewModel.guardar {}

        assertEquals("Todos los criterios deben tener nombre.", viewModel.estado.value.error)
    }

    @Test
    fun guardarConPuntajeInvalidoMuestraError() {
        val viewModel = crearViewModel()
        llenarFormulario(viewModel)
        viewModel.cambiarPuntajeCriterio(1, "abc")

        viewModel.guardar {}

        assertEquals("El puntaje de cada criterio debe ser un número mayor a 0.", viewModel.estado.value.error)
        assertTrue(api.rubricas.isEmpty())
    }

    @Test
    fun guardarConNivelSinNombreMuestraError() {
        val viewModel = crearViewModel()
        llenarFormulario(viewModel)
        viewModel.cambiarNombreNivel(1, " ")

        viewModel.guardar {}

        assertEquals("Todos los niveles deben tener nombre.", viewModel.estado.value.error)
    }

    @Test
    fun guardarConNivelesRepetidosMuestraError() {
        val viewModel = crearViewModel()
        llenarFormulario(viewModel)
        viewModel.cambiarNombreNivel(1, "Muy buen desempeño")

        viewModel.guardar {}

        assertEquals("No se pueden repetir nombres de niveles.", viewModel.estado.value.error)
    }

    @Test
    fun guardarConPorcentajeFueraDeRangoMuestraError() {
        val viewModel = crearViewModel()
        llenarFormulario(viewModel)
        viewModel.cambiarPorcentajeNivel(1, "150")

        viewModel.guardar {}

        assertEquals("El porcentaje de cada nivel debe ser un número entre 0 y 100.", viewModel.estado.value.error)
    }

    @Test
    fun guardarSinNivelDeCienMuestraError() {
        val viewModel = crearViewModel()
        llenarFormulario(viewModel)
        viewModel.cambiarPorcentajeNivel(0, "90")

        viewModel.guardar {}

        assertEquals("Debe haber un nivel con 100% para poder llegar al 7.0.", viewModel.estado.value.error)
    }

    @Test
    fun guardarRubricaNuevaOrdenaLosNiveles() {
        val viewModel = crearViewModel()
        llenarFormulario(viewModel)
        viewModel.agregarNivel()
        viewModel.cambiarNombreNivel(4, "Incipiente")
        viewModel.cambiarPorcentajeNivel(4, "30")
        var termino = false

        viewModel.guardar { termino = true }

        val guardada = api.rubricas.values.first()
        assertTrue(termino)
        assertEquals(15, guardada.puntajeMaximo())
        assertEquals("Sigue la estructura pedida", guardada.criterios[0].descripcion)
        assertEquals(listOf(100, 80, 60, 30, 0), guardada.niveles.map { it.porcentaje })
        assertEquals("Rúbrica guardada.", viewModel.estado.value.mensaje)
        assertEquals(1, viewModel.estado.value.rubricas.size)
        assertEquals("", viewModel.estado.value.nombre)
    }

    @Test
    fun seleccionarParaEditarLlenaElFormulario() {
        val viewModel = crearViewModel()

        viewModel.seleccionarParaEditar(DatosPrueba.rubrica)

        val estado = viewModel.estado.value
        assertEquals("Presentación oral", estado.nombre)
        assertEquals(7L, estado.editandoId)
        assertEquals("5", estado.criterios[1].puntajeMaximo)
        assertEquals("80", estado.niveles[1].porcentaje)
    }

    @Test
    fun editarMantieneElId() {
        api.rubricas[7] = DatosPrueba.rubrica
        val viewModel = crearViewModel()
        viewModel.seleccionarParaEditar(DatosPrueba.rubrica)
        viewModel.cambiarNombre("Disertación")

        viewModel.guardar {}

        assertEquals(1, api.rubricas.size)
        assertEquals("Disertación", api.rubricas[7]!!.nombre)
        assertNull(viewModel.estado.value.editandoId)
    }

    @Test
    fun guardarConElServidorApagadoMuestraError() {
        val viewModel = crearViewModel()
        llenarFormulario(viewModel)
        api.servidorApagado = true
        var termino = false

        viewModel.guardar { termino = true }

        assertFalse(termino)
        assertEquals("No se pudo guardar la rúbrica. Revisa que el microservicio esté encendido.", viewModel.estado.value.error)
    }

    @Test
    fun nuevaRubricaLimpiaElFormulario() {
        val viewModel = crearViewModel()
        viewModel.seleccionarParaEditar(DatosPrueba.rubrica)

        viewModel.nuevaRubrica()

        assertEquals("", viewModel.estado.value.nombre)
        assertEquals(1, viewModel.estado.value.criterios.size)
        assertEquals(4, viewModel.estado.value.niveles.size)
        assertNull(viewModel.estado.value.editandoId)
    }

    @Test
    fun eliminarRubrica() {
        api.rubricas[7] = DatosPrueba.rubrica
        val viewModel = crearViewModel()

        viewModel.eliminar(7)

        assertTrue(viewModel.estado.value.rubricas.isEmpty())
        assertEquals("Rúbrica eliminada.", viewModel.estado.value.mensaje)
    }

    @Test
    fun eliminarConElServidorApagadoMuestraError() {
        val viewModel = crearViewModel()
        api.servidorApagado = true

        viewModel.eliminar(7)

        assertEquals("No se pudo eliminar la rúbrica. Revisa que el microservicio esté encendido.", viewModel.estado.value.error)
    }
}
