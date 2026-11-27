package com.example.evalapp_grupo12.model

import com.google.gson.annotations.SerializedName

data class RespuestaClima(
    @SerializedName("current") val actual: ClimaActual
)

data class ClimaActual(
    @SerializedName("temperature_2m") val temperatura: Double,
    @SerializedName("wind_speed_10m") val viento: Double,
    @SerializedName("weather_code") val codigo: Int
)
