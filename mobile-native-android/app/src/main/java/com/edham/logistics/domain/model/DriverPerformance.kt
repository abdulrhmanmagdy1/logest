package com.edham.logistics.domain.model

data class DriverPerformance(
    val averageRating: Float,
    val onTimeDeliveryRate: Float,
    val averageDeliveryTime: Int,
    val totalDistance: Double,
    val fuelEfficiency: Double,
    val customerSatisfactionScore: Float,
    val safetyScore: Float,
    val complianceScore: Float
)
