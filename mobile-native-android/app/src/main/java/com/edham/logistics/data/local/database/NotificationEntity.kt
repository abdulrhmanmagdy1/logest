package com.edham.logistics.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val priority: String,
    val isRead: Boolean,
    val createdAt: Long
)
