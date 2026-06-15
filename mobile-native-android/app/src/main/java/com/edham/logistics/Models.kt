package com.edham.logistics

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Load(
    val id: String,
    val clientName: String,
    val from: String,
    val to: String,
    val weight: String,
    val temperature: String? = null,
    val status: String,
    val date: String,
    val price: String,
    val driverName: String? = null,
    val truckNumber: String? = null,
    // Location tracking fields
    val pickupLat: Double? = null,
    val pickupLng: Double? = null,
    val dropLat: Double? = null,
    val dropLng: Double? = null,
    val currentLat: Double? = null,
    val currentLng: Double? = null,
    val driverPhone: String? = null,
    val trackingNumber: String? = null,
    // Status history
    val statusHistory: List<StatusHistory>? = null
) : Parcelable

@Parcelize
data class StatusHistory(
    val status: String,
    val timestamp: String,
    val location: String? = null,
    val notes: String? = null,
    val updatedBy: String? = null
) : Parcelable

data class Invoice(
    val id: String,
    val clientName: String,
    val amount: String,
    val status: String,
    val date: String,
    val loadId: String
)

data class Driver(
    val id: String,
    val name: String,
    val phone: String,
    val status: String,
    val rating: Float,
    val trips: Int,
    val currentLocation: String? = null
)

data class Vehicle(
    val id: String,
    val name: String,
    val capacity: String,
    val dimensions: String,
    val basePrice: Int,
    val pricePerKg: Double,
    val status: String = "Available",
    val plateNumber: String? = null,
    val type: String = "truck"
)

data class MaintenanceRecord(
    val id: String,
    val vehicleId: String,
    val vehicleName: String,
    val type: String,
    val date: String,
    val cost: String,
    val nextService: String,
    val status: String
)

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val type: String,
    val time: String,
    val isRead: Boolean = false
)

data class ColdChainRecord(
    val id: String,
    val loadId: String,
    val temperature: String,
    val status: String,
    val time: String
)

data class Survey(
    val id: String,
    val driverId: String,
    val loadId: String,
    val rating: Int,
    val comment: String,
    val date: String
)

// MockData object removed to ensure 100% real data connectivity.
