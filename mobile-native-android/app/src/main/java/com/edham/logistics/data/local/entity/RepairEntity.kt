package com.edham.logistics.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "repairs")
data class RepairEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: String = "",
    val repairType: String = "",
    val reportedDate: Long = 0L,
    val reportedBy: String = "",
    val description: String = "",
    val technicianId: String = "",
    val technicianName: String = "",
    val startDate: Long = 0L,
    val estimatedCompletionDate: Long = 0L,
    val actualCompletionDate: Long? = null,
    val laborHours: Float = 0f,
    val laborCost: Float = 0f,
    val partsCost: Float = 0f,
    val totalCost: Float = 0f,
    val status: String = "",
    val priority: String = "",
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
