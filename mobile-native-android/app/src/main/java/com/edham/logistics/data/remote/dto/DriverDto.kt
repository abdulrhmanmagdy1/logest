package com.edham.logistics.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DriverDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("email")
    val email: String,
    
    @SerializedName("phone")
    val phone: String,
    
    @SerializedName("national_id")
    val nationalId: String,
    
    @SerializedName("driver_license")
    val driverLicense: String,
    
    @SerializedName("license_expiry")
    val licenseExpiry: String,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("rating")
    val rating: Float,
    
    @SerializedName("total_trips")
    val totalTrips: Int,
    
    @SerializedName("completed_trips")
    val completedTrips: Int,
    
    @SerializedName("cancelled_trips")
    val cancelledTrips: Int,
    
    @SerializedName("current_location")
    val currentLocation: String? = null,
    
    @SerializedName("current_latitude")
    val currentLatitude: Double? = null,
    
    @SerializedName("current_longitude")
    val currentLongitude: Double? = null,
    
    @SerializedName("current_vehicle_id")
    val currentVehicleId: String? = null,
    
    @SerializedName("current_shipment_id")
    val currentShipmentId: String? = null,
    
    @SerializedName("is_online")
    val isOnline: Boolean = false,
    
    @SerializedName("last_active_time")
    val lastActiveTime: String? = null,
    
    @SerializedName("profile_image")
    val profileImage: String? = null,
    
    @SerializedName("hire_date")
    val hireDate: String,
    
    @SerializedName("salary")
    val salary: Double,
    
    @SerializedName("bank_account")
    val bankAccount: String? = null,
    
    @SerializedName("emergency_contact")
    val emergencyContact: EmergencyContactDto
)
