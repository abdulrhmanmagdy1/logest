package com.edham.logistics.domain.model

data class TeamStatistics(
    val teamId: String,
    val teamName: String,
    val totalMembers: Int,
    val activeMembers: Int,
    val totalShipments: Int,
    val completedShipments: Int,
    val totalRevenue: Double,
    val averageDeliveryTime: Int,
    val performanceScore: Float,
    val lastUpdated: String
)
