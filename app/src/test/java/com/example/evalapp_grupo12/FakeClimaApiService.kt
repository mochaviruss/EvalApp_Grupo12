package com.example.evalapp_grupo12

import com.example.evalapp_grupo12.data.ClimaApiService
import com.example.evalapp_grupo12.model.ClimaActual
import com.example.evalapp_grupo12.model.RespuestaClima
import java.io.IOException

class FakeClimaApiService : ClimaApiService {

    var respuesta = RespuestaClima(ClimaActual(temperatura = 18.5, viento = 12.0, codigo = 0))
    var sinConexion = false
    var ultimaLatitud = 0.0
    var ultimaLongitud = 0.0

    override suspend fun obtenerClima(latitud: Double, longitud: Double, datos: String): RespuestaClima {
        if (sinConexion) throw IOException("Sin conexión")
        ultimaLatitud = latitud
        ultimaLongitud = longitud
        return respuesta
    }
}
