package com.example.evalapp_grupo12

import com.example.evalapp_grupo12.data.ApiService
import com.example.evalapp_grupo12.model.Alumno
import com.example.evalapp_grupo12.model.Evaluacion
import com.example.evalapp_grupo12.model.Rubrica
import java.io.IOException

class FakeApiService : ApiService {

    val alumnos = mutableMapOf<Long, Alumno>()
    val rubricas = mutableMapOf<Long, Rubrica>()
    val evaluaciones = mutableMapOf<Long, Evaluacion>()
    var servidorApagado = false
    private var siguienteId = 100L

    private fun revisarServidor() {
        if (servidorApagado) throw IOException("El microservicio no responde")
    }

    override suspend fun listarAlumnos(): List<Alumno> {
        revisarServidor()
        return alumnos.values.sortedBy { it.nombre }
    }

    override suspend fun crearAlumno(alumno: Alumno): Alumno {
        revisarServidor()
        val nuevo = alumno.copy(id = siguienteId++)
        alumnos[nuevo.id] = nuevo
        return nuevo
    }

    override suspend fun actualizarAlumno(id: Long, alumno: Alumno): Alumno {
        revisarServidor()
        alumnos[id] = alumno
        return alumno
    }

    override suspend fun eliminarAlumno(id: Long) {
        revisarServidor()
        alumnos.remove(id)
    }

    override suspend fun listarRubricas(): List<Rubrica> {
        revisarServidor()
        return rubricas.values.sortedBy { it.nombre }
    }

    override suspend fun crearRubrica(rubrica: Rubrica): Rubrica {
        revisarServidor()
        val nueva = rubrica.copy(id = siguienteId++)
        rubricas[nueva.id] = nueva
        return nueva
    }

    override suspend fun actualizarRubrica(id: Long, rubrica: Rubrica): Rubrica {
        revisarServidor()
        rubricas[id] = rubrica
        return rubrica
    }

    override suspend fun eliminarRubrica(id: Long) {
        revisarServidor()
        rubricas.remove(id)
    }

    override suspend fun listarEvaluaciones(): List<Evaluacion> {
        revisarServidor()
        return evaluaciones.values.toList()
    }

    override suspend fun crearEvaluacion(evaluacion: Evaluacion): Evaluacion {
        revisarServidor()
        val nueva = evaluacion.copy(id = siguienteId++)
        evaluaciones[nueva.id] = nueva
        return nueva
    }

    override suspend fun actualizarEvaluacion(id: Long, evaluacion: Evaluacion): Evaluacion {
        revisarServidor()
        evaluaciones[id] = evaluacion
        return evaluacion
    }

    override suspend fun eliminarEvaluacion(id: Long) {
        revisarServidor()
        evaluaciones.remove(id)
    }
}
