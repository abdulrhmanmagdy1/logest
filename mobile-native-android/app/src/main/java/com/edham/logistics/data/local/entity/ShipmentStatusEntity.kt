package com.edham.logistics.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "shipment_status")
data class ShipmentStatusEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val shipmentId: String,
    val status: String,
    val timestamp: Date,
    val location: String?,
    val notes: String?,
    val updatedBy: String?
)
