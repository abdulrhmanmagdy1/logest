package com.edham.logistics.feature.notifications.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.edham.logistics.feature.notifications.domain.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "notification_settings")
data class NotificationSettingsEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val userRole: String,
    val pushEnabled: Boolean,
    val emailEnabled: Boolean,
    val smsEnabled: Boolean,
    val inAppEnabled: Boolean,
    val categories: String?, // JSON string
    val quietHours: String?, // JSON string
    val deviceId: String?,
    val fcmToken: String?,
    val lastUpdated: Long
) {
    
    fun toDomainModel(): NotificationSettings {
        val gson = Gson()
        val categoriesType = object : TypeToken<Map<NotificationCategory, CategorySettings>>() {}.type
        val quietHoursType = object : TypeToken<QuietHours>() {}.type
        
        val categoriesMap = categories?.let { gson.fromJson<Map<NotificationCategory, CategorySettings>>(it, categoriesType) }
        val quietHoursObj = quietHours?.let { gson.fromJson<QuietHours>(it, quietHoursType) }
        
        return NotificationSettings(
            id = id,
            userId = userId,
            userRole = UserRole.valueOf(userRole),
            pushEnabled = pushEnabled,
            emailEnabled = emailEnabled,
            smsEnabled = smsEnabled,
            inAppEnabled = inAppEnabled,
            categories = categoriesMap ?: emptyMap(),
            quietHours = quietHoursObj,
            deviceId = deviceId,
            fcmToken = fcmToken,
            lastUpdated = lastUpdated
        )
    }
}

fun NotificationSettings.toEntity(): NotificationSettingsEntity {
    val gson = Gson()
    val categoriesJson = gson.toJson(categories)
    val quietHoursJson = quietHours?.let { gson.toJson(it) }
    
    return NotificationSettingsEntity(
        id = id,
        userId = userId,
        userRole = userRole.name,
        pushEnabled = pushEnabled,
        emailEnabled = emailEnabled,
        smsEnabled = smsEnabled,
        inAppEnabled = inAppEnabled,
        categories = categoriesJson,
        quietHours = quietHoursJson,
        deviceId = deviceId,
        fcmToken = fcmToken,
        lastUpdated = lastUpdated
    )
}
