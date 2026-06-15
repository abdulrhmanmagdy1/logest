package com.edham.logistics.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "maintenance_schedules")
data class MaintenanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: String = "",
    val maintenanceType: String = "",
    val scheduledDate: Long = 0L,
    val estimatedDuration: Int = 0,
    val technicianId: String = "",
    val technicianName: String = "",
    val workshopBay: Int = 0,
    val priority: String = "",
    val status: String = "",
    val rescheduleReason: String? = null,
    val cancelReason: String? = null,
    val totalCost: Float = 0f,
    val partsCost: Float = 0f,
    val laborCost: Float = 0f,
    val laborHours: Float = 0f,
    val completionDate: Long? = null,
    val actualDuration: Int = 0,
    val partsUsed: String = "", // JSON or comma-separated
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
