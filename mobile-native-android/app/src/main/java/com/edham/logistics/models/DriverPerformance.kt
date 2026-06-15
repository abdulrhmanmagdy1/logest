package com.edham.logistics.models

data class DriverPerformance(
    val driverId: String,
    val driverName: String,
    val totalDeliveries: Int,
    val onTimeDeliveries: Int,
    val averageDeliveryTime: Double,
    val customerRating: Float,
    val fuelEfficiency: Double,
    val totalDistance: Double,
    val totalRevenue: Double,
    val period: String
)
