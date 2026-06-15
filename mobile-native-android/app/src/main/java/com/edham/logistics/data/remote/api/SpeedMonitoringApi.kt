package com.edham.logistics.data.remote.api

import com.edham.logistics.data.local.entity.SpeedViolationEntity
import retrofit2.http.*

interface SpeedMonitoringApi {
    @GET("speed-violations")
    suspend fun getSpeedViolations(): List<SpeedViolationEntity>
    
    @POST("speed-violations")
    suspend fun reportSpeedViolation(@Body violation: SpeedViolationEntity): SpeedViolationEntity
    
    @GET("speed-analytics/{vehicleId}")
    suspend fun getSpeedAnalytics(@Path("vehicleId") vehicleId: String): Map<String, Any>
}
