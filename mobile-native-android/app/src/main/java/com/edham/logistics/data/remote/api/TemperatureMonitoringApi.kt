package com.edham.logistics.data.remote.api

import com.edham.logistics.data.local.entity.TemperatureReadingEntity
import retrofit2.http.*

interface TemperatureMonitoringApi {
    @GET("temperature-readings")
    suspend fun getTemperatureReadings(): List<TemperatureReadingEntity>
    
    @POST("temperature-readings")
    suspend fun uploadTemperatureReading(@Body reading: TemperatureReadingEntity): TemperatureReadingEntity
    
    @GET("temperature-analytics/{vehicleId}")
    suspend fun getTemperatureAnalytics(@Path("vehicleId") vehicleId: String): Map<String, Any>
}
