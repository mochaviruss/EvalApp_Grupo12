package com.example.evalapp_grupo12.data

import com.example.evalapp_grupo12.model.RespuestaClima
import retrofit2.http.GET
import retrofit2.http.Query

interface ClimaApiService {

    @GET("v1/forecast")
    suspend fun obtenerClima(
        @Query("latitude") latitud: Double,
        @Query("longitude") longitud: Double,
        @Query("current") datos: String
    ): RespuestaClima
}
