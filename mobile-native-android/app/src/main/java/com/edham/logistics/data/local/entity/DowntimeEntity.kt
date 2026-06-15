package com.edham.logistics.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicle_downtime")
data class DowntimeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: String = "",
    val repairId: Long = 0L,
    val startTime: Long = 0L,
    val endTime: Long? = null,
    val totalHours: Float = 0f,
    val reason: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
