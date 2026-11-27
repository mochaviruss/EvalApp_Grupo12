package com.example.evalapp_grupo12.logic

object DescripcionClima {

    private const val VIENTO_MAXIMO = 40.0

    fun desdeCodigo(codigo: Int): String = when (codigo) {
        0 -> "Despejado"
        1, 2 -> "Parcialmente nublado"
        3 -> "Nublado"
        45, 48 -> "Neblina"
        in 51..57 -> "Llovizna"
        in 61..67 -> "Lluvia"
        in 71..77 -> "Nieve"
        in 80..82 -> "Chubascos"
        in 95..99 -> "Tormenta"
        else -> "Sin información"
    }

    fun esAptoParaTerreno(codigo: Int, viento: Double): Boolean =
        codigo <= 48 && viento < VIENTO_MAXIMO
}
