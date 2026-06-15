package com.edham.logistics.data.remote.api

import com.edham.logistics.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface VehicleApi {

    @GET("vehicles")
    suspend fun getAllVehicles(): Response<List<VehicleDto>>

    @GET("vehicles/{id}")
    suspend fun getVehicleById(@Path("id") id: String): Response<VehicleDto>

    @POST("vehicles")
    suspend fun createVehicle(@Body request: VehicleCreateRequest): Response<VehicleDto>

    @PUT("vehicles/{id}/status")
    suspend fun updateVehicleStatus(
        @Path("id") id: String,
        @Body request: VehicleStatusUpdateRequest
    ): Response<VehicleDto>

    @DELETE("vehicles/{id}")
    suspend fun deleteVehicle(@Path("id") id: String): Response<Unit>

    @GET("vehicles/active")
    suspend fun getActiveVehicles(): Response<List<VehicleDto>>
}
