package com.edham.logistics.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "temperature_readings")
data class TemperatureReadingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: String,
    val temperature: Double,
    val humidity: Double?,
    val timestamp: Date,
    val sensorId: String,
    val locationLat: Double,
    val locationLng: Double
)
