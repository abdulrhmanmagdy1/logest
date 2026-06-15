package com.edham.logistics.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "location_updates")
data class LocationUpdateEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var routeHistoryId: Long,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val accuracy: Float,
    val speed: Float,
    val heading: Float,
    val timestamp: Date,
    val batteryLevel: Int,
    val networkType: String,
    val additionalData: String
)
