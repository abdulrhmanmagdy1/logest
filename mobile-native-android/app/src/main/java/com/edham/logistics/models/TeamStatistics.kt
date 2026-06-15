package com.edham.logistics.models

data class TeamStatistics(
    val totalDrivers: Int,
    val activeDrivers: Int,
    val totalShipments: Int,
    val completedShipments: Int,
    val averageDeliveryTime: Double,
    val onTimeDeliveryRate: Double,
    val totalRevenue: Double,
    val period: String
)
