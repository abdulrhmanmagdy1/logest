package com.edham.logistics.data.remote.api

import retrofit2.http.*

/**
 * Aggregation API - Remote API for data aggregation endpoints
 */
interface AggregationApi {
    
    @GET("aggregation/fleet")
    suspend fun getFleetAggregation(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): retrofit2.Response<FleetAggregationResponse>
    
    @GET("aggregation/shipments")
    suspend fun getShipmentAggregation(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): retrofit2.Response<ShipmentAggregationResponse>
    
    @GET("aggregation/revenue")
    suspend fun getRevenueAggregation(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): retrofit2.Response<RevenueAggregationResponse>
    
    @GET("aggregation/temperature")
    suspend fun getTemperatureAggregation(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): retrofit2.Response<TemperatureAggregationResponse>
}

data class FleetAggregationResponse(
    val totalDistance: Double,
    val totalFuelCost: Double,
    val averageEfficiency: Double,
    val activeVehicles: Int
)

data class ShipmentAggregationResponse(
    val totalShipments: Int,
    val deliveredShipments: Int,
    val delayedShipments: Int,
    val onTimeDeliveryRate: Double
)

data class RevenueAggregationResponse(
    val totalRevenue: Double,
    val paidRevenue: Double,
    val outstandingRevenue: Double,
    val collectionRate: Double
)

data class TemperatureAggregationResponse(
    val totalReadings: Int,
    val averageTemperature: Double,
    val violations: Int,
    val complianceRate: Double
)
