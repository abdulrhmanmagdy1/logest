package com.edham.logistics.feature.notifications.data.database.dao

import androidx.room.*
import com.edham.logistics.feature.notifications.data.database.entity.NotificationSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationSettingsDao {
    
    @Query("SELECT * FROM notification_settings WHERE userId = :userId")
    suspend fun getSettings(userId: String): NotificationSettingsEntity?
    
    @Query("SELECT * FROM notification_settings WHERE userId = :userId")
    fun observeSettings(userId: String): Flow<NotificationSettingsEntity?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: NotificationSettingsEntity)
    
    @Update
    suspend fun updateSettings(settings: NotificationSettingsEntity)
    
    @Delete
    suspend fun deleteSettings(settings: NotificationSettingsEntity)
    
    @Query("DELETE FROM notification_settings WHERE userId = :userId")
    suspend fun deleteSettings(userId: String)
    
    @Query("SELECT * FROM notification_settings WHERE userRole = :userRole ORDER BY lastUpdated DESC LIMIT 1")
    suspend fun getLatestSettingsByRole(userRole: String): NotificationSettingsEntity?
    
    @Query("SELECT * FROM notification_settings ORDER BY lastUpdated DESC")
    suspend fun getAllSettings(): List<NotificationSettingsEntity>
    
    @Query("UPDATE notification_settings SET fcmToken = :fcmToken WHERE userId = :userId")
    suspend fun updateFcmToken(userId: String, fcmToken: String)
    
    @Query("UPDATE notification_settings SET deviceId = :deviceId WHERE userId = :userId")
    suspend fun updateDeviceId(userId: String, deviceId: String)
    
    @Query("DELETE FROM notification_settings")
    suspend fun clearAllSettings()
}
