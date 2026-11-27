package com.example.evalapp_grupo12.data

import com.example.evalapp_grupo12.model.Rubrica

class RubricaRepository(private val api: ApiService) {

    suspend fun listar(): List<Rubrica> = api.listarRubricas()

    suspend fun guardar(rubrica: Rubrica) {
        if (rubrica.id == 0L) {
            api.crearRubrica(rubrica)
        } else {
            api.actualizarRubrica(rubrica.id, rubrica)
        }
    }

    suspend fun eliminar(id: Long) {
        api.eliminarRubrica(id)
    }
}
