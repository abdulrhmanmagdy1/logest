package com.edham.logistics.data.remote.api

import com.edham.logistics.data.local.entity.RouteHistoryEntity
import retrofit2.http.*

interface RouteHistoryApi {
    @GET("route-history")
    suspend fun getRouteHistory(): List<RouteHistoryEntity>
    
    @POST("route-history")
    suspend fun uploadRouteHistory(@Body history: RouteHistoryEntity): RouteHistoryEntity
    
    @GET("route-history/{id}")
    suspend fun getRouteHistoryById(@Path("id") id: Long): RouteHistoryEntity
}
