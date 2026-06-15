package com.edham.logistics.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ShipmentDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("client_name")
    val clientName: String,
    
    @SerializedName("client_phone")
    val clientPhone: String,
    
    @SerializedName("client_email")
    val clientEmail: String,
    
    @SerializedName("pickup_address")
    val pickupAddress: String,
    
    @SerializedName("delivery_address")
    val deliveryAddress: String,
    
    @SerializedName("weight")
    val weight: Double,
    
    @SerializedName("dimensions")
    val dimensions: String,
    
    @SerializedName("cargo_type")
    val cargoType: String,
    
    @SerializedName("temperature")
    val temperature: Double? = null,
    
    @SerializedName("humidity")
    val humidity: Double? = null,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("priority")
    val priority: String = "normal",
    
    @SerializedName("special_instructions")
    val specialInstructions: String? = null,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("estimated_delivery_time")
    val estimatedDeliveryTime: String? = null,
    
    @SerializedName("actual_delivery_time")
    val actualDeliveryTime: String? = null,
    
    @SerializedName("driver_id")
    val driverId: String? = null,
    
    @SerializedName("driver_name")
    val driverName: String? = null,
    
    @SerializedName("driver_phone")
    val driverPhone: String? = null,
    
    @SerializedName("vehicle_id")
    val vehicleId: String? = null,
    
    @SerializedName("vehicle_name")
    val vehicleName: String? = null,
    
    @SerializedName("tracking_number")
    val trackingNumber: String,
    
    @SerializedName("created_at")
    val createdAt: String,
    
    @SerializedName("updated_at")
    val updatedAt: String,
    
    @SerializedName("pickup_time")
    val pickupTime: String? = null,
    
    @SerializedName("delivery_time")
    val deliveryTime: String? = null,
    
    @SerializedName("pickup_photos")
    val pickupPhotos: List<String> = emptyList(),
    
    @SerializedName("delivery_photos")
    val deliveryPhotos: List<String> = emptyList(),
    
    @SerializedName("documents")
    val documents: List<String> = emptyList()
)
