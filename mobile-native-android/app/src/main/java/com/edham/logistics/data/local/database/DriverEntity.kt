package com.edham.logistics.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drivers")
data class DriverEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val status: String,
    val userId: String
)
