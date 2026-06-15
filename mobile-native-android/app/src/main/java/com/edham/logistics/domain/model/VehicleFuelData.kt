package com.edham.logistics.domain.model

data class VehicleFuelData(
    val vehicleId: String,
    val fuelType: String,
    val currentFuelLevel: Double,
    val fuelCapacity: Double,
    val averageConsumption: Double,
    val lastFillupDate: String,
    val estimatedRangeKm: Double,
    val lastUpdated: String
)
