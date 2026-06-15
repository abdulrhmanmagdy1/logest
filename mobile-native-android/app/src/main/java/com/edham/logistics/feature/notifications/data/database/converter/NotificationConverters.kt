package com.edham.logistics.feature.notifications.data.database.converter

import androidx.room.TypeConverter
import com.edham.logistics.feature.notifications.domain.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NotificationConverters {
    
    private val gson = Gson()
    
    @TypeConverter
    fun fromNotificationType(value: NotificationType): String = value.name
    
    @TypeConverter
    fun toNotificationType(value: String): NotificationType = NotificationType.valueOf(value)
    
    @TypeConverter
    fun fromNotificationCategory(value: NotificationCategory): String = value.name
    
    @TypeConverter
    fun toNotificationCategory(value: String): NotificationCategory = NotificationCategory.valueOf(value)
    
    @TypeConverter
    fun fromNotificationPriority(value: NotificationPriority): String = value.name
    
    @TypeConverter
    fun toNotificationPriority(value: String): NotificationPriority = NotificationPriority.valueOf(value)
    
    @TypeConverter
    fun fromUserRole(value: UserRole): String = value.name
    
    @TypeConverter
    fun toUserRole(value: String): UserRole = UserRole.valueOf(value)
    
    @TypeConverter
    fun fromStringMap(value: Map<String, Any>?): String? {
        return value?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toStringMap(value: String?): Map<String, Any>? {
        return value?.let {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            gson.fromJson(it, type)
        }
    }
    
    @TypeConverter
    fun fromCategorySettingsMap(value: Map<NotificationCategory, CategorySettings>?): String? {
        return value?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toCategorySettingsMap(value: String?): Map<NotificationCategory, CategorySettings>? {
        return value?.let {
            val type = object : TypeToken<Map<NotificationCategory, CategorySettings>>() {}.type
            gson.fromJson(it, type)
        }
    }
    
    @TypeConverter
    fun fromQuietHours(value: QuietHours?): String? {
        return value?.let { gson.toJson(it) }
    }
    
    @TypeConverter
    fun toQuietHours(value: String?): QuietHours? {
        return value?.let {
            val type = object : TypeToken<QuietHours>() {}.type
            gson.fromJson(it, type)
        }
    }
}
