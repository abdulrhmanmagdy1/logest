package com.edham.logistics.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "speed_violations")
data class SpeedViolationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: String,
    val locationLat: Double,
    val locationLng: Double,
    val recordedSpeed: Double,
    val speedLimit: Double,
    val timestamp: Date,
    val severity: String
)
