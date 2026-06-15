package com.edham.logistics.feature.notifications.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.edham.logistics.feature.notifications.domain.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val message: String,
    val type: String,
    val category: String,
    val priority: String,
    val userId: String,
    val userRole: String,
    val shipmentId: String?,
    val driverId: String?,
    val customerId: String?,
    val data: String?, // JSON string
    val imageUrl: String?,
    val actionUrl: String?,
    val isRead: Boolean,
    val isPush: Boolean,
    val timestamp: Long,
    val expiresAt: Long?,
    val sound: String?,
    val vibration: Boolean,
    val badge: Int?,
    val deepLink: String?
) {
    
    fun toDomainModel(): Notification {
        val gson = Gson()
        val dataType = object : TypeToken<Map<String, Any>>() {}.type
        val dataMap = data?.let { gson.fromJson<Map<String, Any>>(it, dataType) }
        
        return Notification(
            id = id,
            title = title,
            message = message,
            type = NotificationType.valueOf(type),
            category = NotificationCategory.valueOf(category),
            priority = NotificationPriority.valueOf(priority),
            userId = userId,
            userRole = UserRole.valueOf(userRole),
            shipmentId = shipmentId,
            driverId = driverId,
            customerId = customerId,
            data = dataMap,
            imageUrl = imageUrl,
            actionUrl = actionUrl,
            isRead = isRead,
            isPush = isPush,
            timestamp = timestamp,
            expiresAt = expiresAt,
            sound = sound,
            vibration = vibration,
            badge = badge,
            deepLink = deepLink
        )
    }
}

fun Notification.toEntity(): NotificationEntity {
    val gson = Gson()
    val dataJson = data?.let { gson.toJson(it) }
    
    return NotificationEntity(
        id = id,
        title = title,
        message = message,
        type = type.name,
        category = category.name,
        priority = priority.name,
        userId = userId,
        userRole = userRole.name,
        shipmentId = shipmentId,
        driverId = driverId,
        customerId = customerId,
        data = dataJson,
        imageUrl = imageUrl,
        actionUrl = actionUrl,
        isRead = isRead,
        isPush = isPush,
        timestamp = timestamp,
        expiresAt = expiresAt,
        sound = sound,
        vibration = vibration,
        badge = badge,
        deepLink = deepLink
    )
}
