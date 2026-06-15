package com.edham.logistics.data.remote.api

import com.edham.logistics.data.local.entity.ShipmentEntity
import retrofit2.http.*

interface ShipmentApi {
    @GET("shipments")
    suspend fun getShipments(): List<ShipmentEntity>
    
    @GET("shipments/{id}")
    suspend fun getShipmentById(@Path("id") id: String): ShipmentEntity
    
    @POST("shipments")
    suspend fun createShipment(@Body shipment: ShipmentEntity): ShipmentEntity
    
    @PUT("shipments/{id}")
    suspend fun updateShipment(@Path("id") id: String, @Body shipment: ShipmentEntity): ShipmentEntity
}
