package com.edham.logistics.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey val id: String,
    val shipmentId: String,
    val startPoint: String,
    val endPoint: String,
    val distance: Double,
    val estimatedTime: Int,
    val createdAt: Long
)
