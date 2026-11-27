package com.example.evalapp_grupo12.data

import com.example.evalapp_grupo12.model.Evaluacion

class EvaluacionRepository(private val api: ApiService) {

    suspend fun listar(): List<Evaluacion> = api.listarEvaluaciones()

    suspend fun guardar(evaluacion: Evaluacion) {
        if (evaluacion.id == 0L) {
            api.crearEvaluacion(evaluacion)
        } else {
            api.actualizarEvaluacion(evaluacion.id, evaluacion)
        }
    }

    suspend fun eliminar(id: Long) {
        api.eliminarEvaluacion(id)
    }
}
