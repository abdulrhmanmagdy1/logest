package com.edham.logistics.core.network.api

import com.edham.logistics.Load
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Shipment / Load endpoints.
 */
interface ShipmentApi {

    @GET("shipments")
    suspend fun getShipments(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<PagedResponse<Load>>>

    @GET("shipments/{id}")
    suspend fun getShipment(@Path("id") id: String): Response<ApiResponse<Load>>

    @POST("shipments/{id}/status")
    suspend fun updateStatus(
        @Path("id") id: String,
        @Query("status") status: String
    ): Response<Unit>

    @POST("shipments")
    suspend fun createShipment(@Body request: CreateShipmentRequest): Response<ApiResponse<Load>>
}

data class CreateShipmentRequest(
    val pickupLocation: String,
    val deliveryLocation: String,
    val cargoType: String,
    val weightKg: Double,
    val temperatureCelsius: Double? = null,
    val priority: String = "normal",
    val notes: String? = null,
    val insuranceValue: Double? = null,
    // Extended fields for full shipment workflow
    val pickupCity: String? = null,
    val dropCity: String? = null,
    val pickupDate: String? = null,
    val pickupTime: String? = null,
    val recipientName: String? = null,
    val recipientPhone: String? = null,
    val pieceCount: Int? = null,
    val vehicleType: String? = null,
    val estimatedPrice: Double? = null,
    val pickupLat: Double? = null,
    val pickupLng: Double? = null,
    val dropLat: Double? = null,
    val dropLng: Double? = null,
    val photoUris: List<String>? = null
)

data class PagedResponse<T>(
    val data: List<T>,
    val page: Int,
    val totalPages: Int,
    val totalItems: Int
)
