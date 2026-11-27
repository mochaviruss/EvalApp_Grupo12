package com.example.evalapp_grupo12.data

import com.example.evalapp_grupo12.logic.DescripcionClima
import com.example.evalapp_grupo12.model.Clima

class ClimaRepository(private val api: ClimaApiService) {

    suspend fun obtenerClima(): Clima {
        val respuesta = api.obtenerClima(LATITUD, LONGITUD, DATOS)
        val actual = respuesta.actual

        return Clima(
            temperatura = actual.temperatura,
            viento = actual.viento,
            descripcion = DescripcionClima.desdeCodigo(actual.codigo),
            aptoParaTerreno = DescripcionClima.esAptoParaTerreno(actual.codigo, actual.viento)
        )
    }

    companion object {
        const val LATITUD = -33.45
        const val LONGITUD = -70.66
        const val DATOS = "temperature_2m,wind_speed_10m,weather_code"
    }
}
