package com.example.myapitest.api

import com.example.myapitest.model.Car
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CarApiService {
    @GET("/car")
    suspend fun getCars(): List<Car>

    @POST("/car")
    suspend fun createCar(@Body car: Car): Car

    @GET("/car/{id}")
    suspend fun getCarById(@Path("id") carId: String): Car

    @DELETE("/car/{id}")
    suspend fun deleteCar(@Path("id") carId: String)

    @PATCH("/car/{id}")
    suspend fun updateCar(@Path("id") carId: String, @Body updatedCar: Car): Car
}