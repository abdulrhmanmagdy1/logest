package com.edham.logistics.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "fuel_consumption")
data class FuelConsumptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleId: String,
    val fuelLevel: Double,
    val consumptionRate: Double,
    val distance: Double,
    val timestamp: Date,
    val locationLat: Double,
    val locationLng: Double
)
