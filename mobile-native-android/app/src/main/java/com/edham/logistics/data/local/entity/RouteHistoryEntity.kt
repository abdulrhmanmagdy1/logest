package com.edham.logistics.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "route_history")
data class RouteHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val routeId: String,
    val startTime: Date,
    val endTime: Date?,
    val status: String,
    val totalDistance: Double,
    val totalDuration: Long,
    val averageSpeed: Double,
    val maxSpeed: Double,
    val fuelConsumed: Double,
    val locationUpdateCount: Int,
    val events: List<String>,
    val metadata: Map<String, Any>
)
