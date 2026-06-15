package com.edham.logistics.data.remote.api

import retrofit2.http.*

/**
 * Analytics API - Remote API for analytics endpoints
 */
interface AnalyticsApi {
    
    @GET("analytics/kpis")
    suspend fun getRealTimeKPIs(): retrofit2.Response<RealTimeKPIsResponse>
    
    @GET("analytics/fleet")
    suspend fun getFleetAnalytics(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): retrofit2.Response<FleetAnalyticsResponse>
    
    @GET("analytics/shipments")
    suspend fun getShipmentAnalytics(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): retrofit2.Response<ShipmentAnalyticsResponse>
    
    @GET("analytics/revenue")
    suspend fun getRevenueAnalytics(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): retrofit2.Response<RevenueAnalyticsResponse>
    
    @GET("analytics/delays")
    suspend fun getDelayAnalytics(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): retrofit2.Response<DelayAnalyticsResponse>
    
    @GET("analytics/temperature")
    suspend fun getTemperatureAnalytics(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): retrofit2.Response<TemperatureAnalyticsResponse>
    
    @GET("analytics/incidents")
    suspend fun getIncidentAnalytics(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): retrofit2.Response<IncidentAnalyticsResponse>
    
    @GET("analytics/fuel")
    suspend fun getFuelAnalytics(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): retrofit2.Response<FuelAnalyticsResponse>
    
    @GET("analytics/drivers")
    suspend fun getDriverAnalytics(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): retrofit2.Response<DriverAnalyticsResponse>
}

data class RealTimeKPIsResponse(
    val activeShipments: Int,
    val activeDrivers: Int,
    val onTimeDeliveryRate: Double,
    val averageResponseTime: Double,
    val systemUptime: Double
)

data class FleetAnalyticsResponse(
    val totalVehicles: Int,
    val activeVehicles: Int,
    val averageEfficiency: Double,
    val totalDistance: Double,
    val maintenanceIssues: Int
)

data class ShipmentAnalyticsResponse(
    val totalShipments: Int,
    val deliveredShipments: Int,
    val inTransitShipments: Int,
    val delayedShipments: Int,
    val onTimeDeliveryRate: Double
)

data class RevenueAnalyticsResponse(
    val totalRevenue: Double,
    val averageRevenuePerShipment: Double,
    val revenueGrowth: Double,
    val collectionRate: Double
)

data class DelayAnalyticsResponse(
    val totalDelays: Int,
    val averageDelayMinutes: Double,
    val delayCost: Double,
    val onTimeDeliveryRate: Double
)

data class TemperatureAnalyticsResponse(
    val totalReadings: Int,
    val averageTemperature: Double,
    val violations: Int,
    val complianceRate: Double
)

data class IncidentAnalyticsResponse(
    val totalIncidents: Int,
    val resolvedIncidents: Int,
    val averageResponseTime: Double,
    val resolutionRate: Double
)

data class FuelAnalyticsResponse(
    val totalFuelConsumed: Double,
    val totalFuelCost: Double,
    val averageEfficiency: Double,
    val fuelTheftAlerts: Int
)

data class DriverAnalyticsResponse(
    val totalDrivers: Int,
    val activeDrivers: Int,
    val averageDeliveriesPerDriver: Double,
    val averageRating: Double
)
