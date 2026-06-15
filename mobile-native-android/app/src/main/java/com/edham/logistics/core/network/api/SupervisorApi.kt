package com.edham.logistics.core.network.api

import com.edham.logistics.feature.driver.data.models.Trip
import com.edham.logistics.feature.driver.data.models.DriverProfile
import retrofit2.Response
import retrofit2.http.*

interface SupervisorApi {

    @GET("supervisor/stats")
    suspend fun getStats(): Response<ApiResponse<SupervisorStats>>

    @GET("supervisor/orders")
    suspend fun getOrders(
        @Query("status") status: String? = null
    ): Response<ApiResponse<List<Trip>>>

    @GET("supervisor/drivers")
    suspend fun getDrivers(): Response<ApiResponse<List<DriverProfile>>>

    @POST("supervisor/assign-trip")
    suspend fun assignTrip(
        @Query("tripId") tripId: String,
        @Query("driverId") driverId: String
    ): Response<Unit>
}

data class SupervisorStats(
    val delivered_today: Int,
    val in_transit: Int,
    val available_vehicles: Int,
    val total_earnings: Double
)
