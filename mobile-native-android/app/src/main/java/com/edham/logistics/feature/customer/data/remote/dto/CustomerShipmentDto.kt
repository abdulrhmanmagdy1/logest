package com.edham.logistics.feature.customer.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CustomerShipmentDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("tracking_number")
    val trackingNumber: String,
    
    @SerializedName("cargo_type")
    val cargoType: String,
    
    @SerializedName("pickup_address")
    val pickupAddress: String,
    
    @SerializedName("delivery_address")
    val deliveryAddress: String,
    
    @SerializedName("weight")
    val weight: Double,
    
    @SerializedName("dimensions")
    val dimensions: String,
    
    @SerializedName("temperature")
    val temperature: Double? = null,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("price")
    val price: Double,
    
    @SerializedName("created_at")
    val createdAt: String,
    
    @SerializedName("estimated_delivery_time")
    val estimatedDeliveryTime: String,
    
    @SerializedName("pickup_time")
    val pickupTime: String? = null,
    
    @SerializedName("delivery_time")
    val deliveryTime: String? = null,
    
    @SerializedName("driver_name")
    val driverName: String? = null,
    
    @SerializedName("driver_phone")
    val driverPhone: String? = null,
    
    @SerializedName("vehicle_name")
    val vehicleName: String? = null,
    
    @SerializedName("tracking_history")
    val trackingHistory: List<TrackingEventDto> = emptyList(),
    
    @SerializedName("documents")
    val documents: List<String> = emptyList(),
    
    @SerializedName("can_cancel")
    val canCancel: Boolean = false,
    
    @SerializedName("can_track")
    val canTrack: Boolean = true,
    
    @SerializedName("can_modify")
    val canModify: Boolean = false
)

data class TrackingEventDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("timestamp")
    val timestamp: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("location")
    val location: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("latitude")
    val latitude: Double? = null,
    
    @SerializedName("longitude")
    val longitude: Double? = null,
    
    @SerializedName("created_by")
    val createdBy: String? = null
)

data class ShipmentCreationRequestDto(
    @SerializedName("customer_id")
    val customerId: String,
    
    @SerializedName("pickup_address")
    val pickupAddress: String,
    
    @SerializedName("pickup_latitude")
    val pickupLatitude: Double,
    
    @SerializedName("pickup_longitude")
    val pickupLongitude: Double,
    
    @SerializedName("delivery_address")
    val deliveryAddress: String,
    
    @SerializedName("delivery_latitude")
    val deliveryLatitude: Double,
    
    @SerializedName("delivery_longitude")
    val deliveryLongitude: Double,
    
    @SerializedName("cargo_type")
    val cargoType: String,
    
    @SerializedName("weight")
    val weight: Double,
    
    @SerializedName("notes")
    val notes: String? = null,
    
    @SerializedName("temperature")
    val temperature: Double? = null,
    
    @SerializedName("priority")
    val priority: String = "normal",
    
    @SerializedName("insurance_value")
    val insuranceValue: Double? = null
)

data class ShipmentTrackingDataDto(
    @SerializedName("shipment")
    val shipment: CustomerShipmentDto,
    
    @SerializedName("current_location")
    val currentLocation: TrackingLocationDto? = null,
    
    @SerializedName("tracking_history")
    val trackingHistory: List<TrackingEventDto>,
    
    @SerializedName("estimated_arrival_time")
    val estimatedArrivalTime: String,
    
    @SerializedName("is_refrigerated")
    val isRefrigerated: Boolean = false,
    
    @SerializedName("current_temperature")
    val currentTemperature: Double? = null
)

data class TrackingLocationDto(
    @SerializedName("latitude")
    val latitude: Double,
    
    @SerializedName("longitude")
    val longitude: Double,
    
    @SerializedName("address")
    val address: String,
    
    @SerializedName("timestamp")
    val timestamp: String,
    
    @SerializedName("speed")
    val speed: Float? = null,
    
    @SerializedName("heading")
    val heading: Float? = null
)
