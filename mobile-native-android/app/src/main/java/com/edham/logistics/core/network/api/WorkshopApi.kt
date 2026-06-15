package com.edham.logistics.core.network.api

import retrofit2.Response
import retrofit2.http.*

interface WorkshopApi {

    @GET("workshop/stats")
    suspend fun getWorkshopStats(): Response<ApiResponse<WorkshopStats>>

    @GET("workshop/inventory")
    suspend fun getInventory(): Response<ApiResponse<List<PartItem>>>

    @GET("workshop/vehicles")
    suspend fun getVehicles(): Response<ApiResponse<List<VehicleItem>>>

    @POST("workshop/request-part")
    suspend fun requestPart(
        @Query("partId") partId: String,
        @Query("quantity") quantity: Int,
        @Query("priority") priority: String
    ): Response<Unit>

    @POST("workshop/vehicle/{id}/ground")
    suspend fun groundVehicle(
        @Path("id") vehicleId: String,
        @Query("reason") reason: String
    ): Response<Unit>

    @POST("workshop/vehicle/{id}/release")
    suspend fun releaseVehicle(@Path("id") vehicleId: String): Response<Unit>
}

data class WorkshopStats(
    val groundedTrucks: Int,
    val readyTrucks: Int,
    val pendingRepairs: Int,
    val lowStockItems: Int,
    val fleetHealthScore: Double
)
