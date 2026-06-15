package com.edham.logistics.data.remote.api

import com.edham.logistics.data.local.entity.FuelConsumptionEntity
import retrofit2.http.*

interface FuelMonitoringApi {
    @GET("fuel-consumption")
    suspend fun getFuelConsumption(): List<FuelConsumptionEntity>
    
    @POST("fuel-consumption")
    suspend fun reportFuelConsumption(@Body consumption: FuelConsumptionEntity): FuelConsumptionEntity
    
    @GET("fuel-analytics/{vehicleId}")
    suspend fun getFuelAnalytics(@Path("vehicleId") vehicleId: String): Map<String, Any>
}
