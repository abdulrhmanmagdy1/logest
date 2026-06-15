package com.edham.logistics.models

data class VehicleFuelData(
    val vehicleId: String,
    val vehicleNumber: String,
    val fuelLevel: Double,
    val fuelCapacity: Double,
    val lastRefuelDate: String,
    val averageFuelConsumption: Double,
    val totalDistance: Double,
    val status: String
)
