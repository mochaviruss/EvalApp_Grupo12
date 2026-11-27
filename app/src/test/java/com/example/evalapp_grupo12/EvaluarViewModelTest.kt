package com.example.evalapp_grupo12

import com.example.evalapp_grupo12.data.EvaluacionRepository
import com.example.evalapp_grupo12.model.Criterio
import com.example.evalapp_grupo12.model.Nivel
import com.example.evalapp_grupo12.model.Rubrica
import com.example.evalapp_grupo12.viewmodel.EvaluarViewModel
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
class EvaluarViewModelTest {

    private val api = FakeApiService()

    @Before
    fun preparar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    private fun crearViewModel() = EvaluarViewModel(EvaluacionRepository(api))

    private fun prepararEvaluacion(viewModel: EvaluarViewModel) {
        viewModel.seleccionarCurso("Sección A")
        viewModel.cambiarSeleccionAlumno(DatosPrueba.camila)
        viewModel.seleccionarRubrica(DatosPrueba.rubrica)
    }

    private fun evaluarTodo(viewModel: EvaluarViewModel, nivel: Nivel) {
        for (indice in 0..3) {
            viewModel.cambiarNivel(indice, nivel)
        }
    }

    @Test
    fun seleccionarCursoBorraLosAlumnosElegidos() {
        val viewModel = crearViewModel()
        viewModel.cambiarSeleccionAlumno(DatosPrueba.camila)

        viewModel.seleccionarCurso("Sección B")

        assertEquals("Sección B", viewModel.estado.value.curso)
        assertTrue(viewModel.estado.value.alumnosSeleccionados.isEmpty())
    }

    @Test
    fun tocarUnAlumnoLoSeleccionaYTocarloOtraVezLoQuita() {
        val viewModel = crearViewModel()

        viewModel.cambiarSeleccionAlumno(DatosPrueba.camila)
        viewModel.cambiarSeleccionAlumno(DatosPrueba.diego)
        viewModel.cambiarSeleccionAlumno(DatosPrueba.camila)

        assertEquals(listOf(DatosPrueba.diego), viewModel.estado.value.alumnosSeleccionados)
    }

    @Test
    fun seleccionarRubricaCopiaCriteriosYNiveles() {
        val viewModel = crearViewModel()

        viewModel.seleccionarRubrica(DatosPrueba.rubrica)

        val estado = viewModel.estado.value
        assertEquals(7L, estado.rubricaId)
        assertEquals("Presentación oral", estado.rubricaNombre)
        assertEquals(4, estado.criterios.size)
        assertEquals("Explica sin leer.", estado.criterios[0].descripcion)
        assertEquals(4, estado.niveles.size)
        assertTrue(estado.criterios.all { it.nivel.isEmpty() })
        assertEquals(20, estado.puntajeMaximo)
        assertEquals(1.0, estado.nota, 0.001)
    }

    @Test
    fun validarSeleccionSinAlumnos() {
        val viewModel = crearViewModel()

        assertFalse(viewModel.validarSeleccion())
        assertEquals("Debe elegir al menos un alumno.", viewModel.estado.value.error)
    }

    @Test
    fun validarSeleccionSinRubrica() {
        val viewModel = crearViewModel()
        viewModel.cambiarSeleccionAlumno(DatosPrueba.camila)

        assertFalse(viewModel.validarSeleccion())
        assertEquals("Debe elegir una rúbrica.", viewModel.estado.value.error)
    }

    @Test
    fun validarSeleccionCompleta() {
        val viewModel = crearViewModel()
        prepararEvaluacion(viewModel)

        assertTrue(viewModel.validarSeleccion())
    }

    @Test
    fun elegirUnNivelDaLosPuntosYCambiaLaNota() {
        val viewModel = crearViewModel()
        prepararEvaluacion(viewModel)

        viewModel.cambiarNivel(0, DatosPrueba.logrado)

        val estado = viewModel.estado.value
        assertEquals("Logrado", estado.criterios[0].nivel)
        assertEquals(4, estado.criterios[0].puntaje)
        assertEquals(4, estado.puntajeObtenido)
        assertEquals(1, estado.criteriosEvaluados)
        assertEquals(2.0, estado.nota, 0.001)
    }

    @Test
    fun cambiarDeNivelReemplazaLosPuntos() {
        val viewModel = crearViewModel()
        prepararEvaluacion(viewModel)
        viewModel.cambiarNivel(0, DatosPrueba.muyBueno)

        viewModel.cambiarNivel(0, DatosPrueba.noLogrado)

        assertEquals(0, viewModel.estado.value.puntajeObtenido)
        assertEquals(1, viewModel.estado.value.criteriosEvaluados)
    }

    @Test
    fun todoMuyBuenDesempenoEsUnSiete() {
        val viewModel = crearViewModel()
        prepararEvaluacion(viewModel)

        evaluarTodo(viewModel, DatosPrueba.muyBueno)

        assertEquals(20, viewModel.estado.value.puntajeObtenido)
        assertEquals(4, viewModel.estado.value.criteriosEvaluados)
        assertEquals(7.0, viewModel.estado.value.nota, 0.001)
    }

    @Test
    fun todoAceptableEsUnCuatro() {
        val viewModel = crearViewModel()
        prepararEvaluacion(viewModel)

        evaluarTodo(viewModel, DatosPrueba.aceptable)

        assertEquals(12, viewModel.estado.value.puntajeObtenido)
        assertEquals(4.0, viewModel.estado.value.nota, 0.001)
    }

    @Test
    fun usaLosNivelesPropiosDeLaRubrica() {
        val tresNiveles = Rubrica(
            id = 9,
            nombre = "Primeros auxilios",
            asignatura = "Primeros auxilios",
            criterios = listOf(Criterio("Técnica", "", 10)),
            niveles = listOf(Nivel("Logrado completamente", 100), Nivel("Logrado parcialmente", 50), Nivel("No logrado", 0))
        )
        val viewModel = crearViewModel()
        viewModel.seleccionarRubrica(tresNiveles)

        viewModel.cambiarNivel(0, tresNiveles.niveles[1])

        assertEquals(3, viewModel.estado.value.niveles.size)
        assertEquals(5, viewModel.estado.value.puntajeObtenido)
    }

    @Test
    fun indiceInvalidoNoHaceNada() {
        val viewModel = crearViewModel()
        prepararEvaluacion(viewModel)

        viewModel.cambiarNivel(10, DatosPrueba.muyBueno)
        viewModel.cambiarComentario(10, "No existe")

        assertEquals(0, viewModel.estado.value.puntajeObtenido)
    }

    @Test
    fun comentariosPorCriterioYGeneral() {
        val viewModel = crearViewModel()
        prepararEvaluacion(viewModel)

        viewModel.cambiarComentario(1, "Mejorar la postura")
        viewModel.cambiarRetroalimentacion("Buen trabajo")

        assertEquals("Mejorar la postura", viewModel.estado.value.criterios[1].comentario)
        assertEquals("Buen trabajo", viewModel.estado.value.retroalimentacion)
    }

    @Test
    fun noSePuedeEnviarSinAlumnos() {
        val viewModel = crearViewModel()

        viewModel.enviar("Instructor Demo") {}

        assertEquals("Debe elegir al menos un alumno.", viewModel.estado.value.error)
    }

    @Test
    fun noSePuedeEnviarSinRubrica() {
        val viewModel = crearViewModel()
        viewModel.cambiarSeleccionAlumno(DatosPrueba.camila)

        viewModel.enviar("Instructor Demo") {}

        assertEquals("Debe elegir una rúbrica.", viewModel.estado.value.error)
    }

    @Test
    fun noSePuedeEnviarSiFaltanCriterios() {
        val viewModel = crearViewModel()
        prepararEvaluacion(viewModel)
        viewModel.cambiarNivel(0, DatosPrueba.logrado)
        var termino = false

        viewModel.enviar("Instructor Demo") { termino = true }

        assertFalse(termino)
        assertEquals("Debe evaluar todos los criterios.", viewModel.estado.value.error)
        assertTrue(api.evaluaciones.isEmpty())
    }

    @Test
    fun enviarUnaEvaluacionIndividual() {
        val viewModel = crearViewModel()
        prepararEvaluacion(viewModel)
        evaluarTodo(viewModel, DatosPrueba.muyBueno)
        viewModel.cambiarComentario(0, "  Muy claro  ")
        viewModel.cambiarRetroalimentacion(" Excelente ")
        var mensaje = ""

        viewModel.enviar("Instructor Demo") { mensaje = it }

        val enviada = api.evaluaciones.values.first()
        assertEquals("Evaluación enviada.", mensaje)
        assertEquals(1, api.evaluaciones.size)
        assertEquals(1L, enviada.alumnoId)
        assertEquals("Camila Rojas Soto", enviada.alumno)
        assertEquals(7.0, enviada.nota, 0.001)
        assertEquals("Muy claro", enviada.criterios[0].comentario)
        assertEquals("Excelente", enviada.retroalimentacion)
        assertEquals(4, enviada.niveles.size)
        assertEquals("", enviada.grupo)
        assertEquals("Instructor Demo", enviada.evaluador)
        assertTrue(viewModel.estado.value.criterios.isEmpty())
    }

    @Test
    fun enviarUnaEvaluacionEnGrupoCreaUnaPorAlumno() {
        val viewModel = crearViewModel()
        prepararEvaluacion(viewModel)
        viewModel.cambiarSeleccionAlumno(DatosPrueba.diego)
        evaluarTodo(viewModel, DatosPrueba.logrado)
        var mensaje = ""

        viewModel.enviar("Instructor Demo") { mensaje = it }

        assertEquals("Se enviaron 2 evaluaciones, una por cada integrante.", mensaje)
        assertEquals(2, api.evaluaciones.size)
        assertTrue(api.evaluaciones.values.all { it.grupo == "Camila Rojas Soto, Diego Muñoz Vera" })
        assertTrue(api.evaluaciones.values.all { it.nota == 5.5 })
    }

    @Test
    fun enviarConElServidorApagadoMuestraErrorYNoBorraLoEvaluado() {
        api.servidorApagado = true
        val viewModel = crearViewModel()
        prepararEvaluacion(viewModel)
        evaluarTodo(viewModel, DatosPrueba.aceptable)
        var termino = false

        viewModel.enviar("Instructor Demo") { termino = true }

        val estado = viewModel.estado.value
        assertFalse(termino)
        assertEquals("No se pudo enviar la evaluación. Revisa que el microservicio esté encendido.", estado.error)
        assertFalse(estado.procesando)
        assertEquals(4, estado.criteriosEvaluados)
    }

    @Test
    fun editarCargaLaEvaluacionGuardada() {
        val viewModel = crearViewModel()

        viewModel.editar(DatosPrueba.evaluacion())

        val estado = viewModel.estado.value
        assertEquals(1L, estado.editandoId)
        assertEquals(listOf(DatosPrueba.camila), estado.alumnosSeleccionados)
        assertEquals("Aceptable", estado.criterios[1].nivel)
        assertEquals("Hablar más fuerte", estado.criterios[1].comentario)
        assertEquals(15, estado.puntajeObtenido)
        assertEquals(4, estado.criteriosEvaluados)
        assertEquals(5.1, estado.nota, 0.001)
    }

    @Test
    fun editarYEnviarActualizaLaMismaEvaluacion() {
        api.evaluaciones[1] = DatosPrueba.evaluacion()
        val viewModel = crearViewModel()
        viewModel.editar(DatosPrueba.evaluacion())
        viewModel.cambiarNivel(1, DatosPrueba.muyBueno)
        var mensaje = ""

        viewModel.enviar("Instructor Demo") { mensaje = it }

        val actualizada = api.evaluaciones[1]!!
        assertEquals("Evaluación actualizada.", mensaje)
        assertEquals(1, api.evaluaciones.size)
        assertEquals(17, actualizada.puntaje)
        assertEquals(5.9, actualizada.nota, 0.001)
        assertEquals("2026-11-20 10:00", actualizada.fecha)
        assertNull(viewModel.estado.value.editandoId)
    }

    @Test
    fun nuevaEvaluacionLimpiaTodo() {
        val viewModel = crearViewModel()
        viewModel.editar(DatosPrueba.evaluacion())

        viewModel.nuevaEvaluacion()

        assertTrue(viewModel.estado.value.criterios.isEmpty())
        assertTrue(viewModel.estado.value.alumnosSeleccionados.isEmpty())
        assertNull(viewModel.estado.value.editandoId)
    }
}
