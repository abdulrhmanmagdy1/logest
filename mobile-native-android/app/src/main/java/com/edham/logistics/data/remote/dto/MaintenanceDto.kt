package com.edham.logistics.data.remote.dto

data class MaintenanceRecordDto(
    val id: String,
    val vehicleId: String,
    val vehiclePlate: String,
    val type: String,
    val description: String,
    val cost: Double,
    val date: String,
    val nextServiceDate: String? = null,
    val status: String,
    val performedBy: String? = null
)

data class CreateMaintenanceRequest(
    val vehicleId: String,
    val type: String,
    val description: String,
    val cost: Double,
    val nextServiceDate: String? = null
)

data class OilChangeDto(
    val id: String,
    val vehicleId: String,
    val vehiclePlate: String,
    val currentMileage: Int,
    val nextOilChangeMileage: Int,
    val oilType: String,
    val date: String,
    val status: String
)

data class MaintenanceAlertDto(
    val id: String,
    val vehicleId: String,
    val vehiclePlate: String,
    val alertType: String,
    val description: String,
    val dueDate: String,
    val priority: String
)
