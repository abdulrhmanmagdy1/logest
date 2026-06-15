package com.edham.logistics.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "maintenance")
data class MaintenanceEntity(
    @PrimaryKey val id: String,
    val vehicleId: String,
    val type: String,
    val status: String,
    val date: Long
)
