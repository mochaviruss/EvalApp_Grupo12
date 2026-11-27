package com.example.evalapp_grupo12.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val URL_MICROSERVICIO = "http://10.0.2.2:8080/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(URL_MICROSERVICIO)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: ApiService = retrofit.create(ApiService::class.java)
}
