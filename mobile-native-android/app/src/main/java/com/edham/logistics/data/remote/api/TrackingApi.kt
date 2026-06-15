package com.edham.logistics.data.remote.api

import com.edham.logistics.data.remote.dto.request.LocationUpdateRequest
import com.edham.logistics.data.remote.dto.response.RouteResponse
import retrofit2.http.*

interface TrackingApi {
    @POST("tracking/location")
    suspend fun updateLocation(@Body request: LocationUpdateRequest): RouteResponse
    
    @GET("tracking/route/{routeId}")
    suspend fun getRoute(@Path("routeId") routeId: String): RouteResponse
    
    @POST("tracking/start")
    suspend fun startTracking(@Body request: Map<String, Any>): Map<String, Any>
    
    @POST("tracking/stop")
    suspend fun stopTracking(@Body request: Map<String, Any>): Map<String, Any>
}
