package com.example.evalapp_grupo12.data

import com.example.evalapp_grupo12.model.Alumno

class AlumnoRepository(private val api: ApiService) {

    suspend fun listar(): List<Alumno> = api.listarAlumnos()

    suspend fun guardar(alumno: Alumno) {
        if (alumno.id == 0L) {
            api.crearAlumno(alumno)
        } else {
            api.actualizarAlumno(alumno.id, alumno)
        }
    }

    suspend fun eliminar(id: Long) {
        api.eliminarAlumno(id)
    }
}
