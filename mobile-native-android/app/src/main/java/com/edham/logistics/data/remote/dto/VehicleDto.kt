package com.edham.logistics.data.remote.dto

data class VehicleDto(
    val id: String,
    val plateNumber: String,
    val type: String,
    val status: String,
    val driverId: String? = null,
    val driverName: String? = null,
    val capacity: Double? = null,
    val lastMaintenanceDate: String? = null,
    val currentLocation: String? = null,
    val isActive: Boolean = true
)

data class VehicleCreateRequest(
    val plateNumber: String,
    val type: String,
    val capacity: Double
)

data class VehicleStatusUpdateRequest(
    val status: String,
    val reason: String? = null
)
