package com.edham.logistics.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "vehicle_id")
    val vehicleId: String,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val speed: Double,
    val heading: Double,
    val timestamp: Date
)
