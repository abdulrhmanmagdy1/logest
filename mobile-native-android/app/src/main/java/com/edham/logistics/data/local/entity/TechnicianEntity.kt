package com.edham.logistics.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "technicians")
data class TechnicianEntity(
    @PrimaryKey val id: String = "",
    val name: String = "",
    val skills: String = "", // Comma-separated
    val workingDays: String = "", // Comma-separated
    val isActive: Boolean = true
)
