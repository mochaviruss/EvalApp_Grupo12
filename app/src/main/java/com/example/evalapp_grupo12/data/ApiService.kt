package com.example.evalapp_grupo12.data

import com.example.evalapp_grupo12.model.Alumno
import com.example.evalapp_grupo12.model.Evaluacion
import com.example.evalapp_grupo12.model.Rubrica
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("api/alumnos")
    suspend fun listarAlumnos(): List<Alumno>

    @POST("api/alumnos")
    suspend fun crearAlumno(@Body alumno: Alumno): Alumno

    @PUT("api/alumnos/{id}")
    suspend fun actualizarAlumno(@Path("id") id: Long, @Body alumno: Alumno): Alumno

    @DELETE("api/alumnos/{id}")
    suspend fun eliminarAlumno(@Path("id") id: Long)

    @GET("api/rubricas")
    suspend fun listarRubricas(): List<Rubrica>

    @POST("api/rubricas")
    suspend fun crearRubrica(@Body rubrica: Rubrica): Rubrica

    @PUT("api/rubricas/{id}")
    suspend fun actualizarRubrica(@Path("id") id: Long, @Body rubrica: Rubrica): Rubrica

    @DELETE("api/rubricas/{id}")
    suspend fun eliminarRubrica(@Path("id") id: Long)

    @GET("api/evaluaciones")
    suspend fun listarEvaluaciones(): List<Evaluacion>

    @POST("api/evaluaciones")
    suspend fun crearEvaluacion(@Body evaluacion: Evaluacion): Evaluacion

    @PUT("api/evaluaciones/{id}")
    suspend fun actualizarEvaluacion(@Path("id") id: Long, @Body evaluacion: Evaluacion): Evaluacion

    @DELETE("api/evaluaciones/{id}")
    suspend fun eliminarEvaluacion(@Path("id") id: Long)
}
