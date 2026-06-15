package com.edham.logistics.models

data class FuelTransactionEntity(
    val id: String,
    val vehicleId: String,
    val vehicleNumber: String,
    val driverId: String,
    val driverName: String,
    val fuelAmount: Double,
    val cost: Double,
    val location: String,
    val date: String,
    val odometerReading: Double,
    val paymentMethod: String
)
