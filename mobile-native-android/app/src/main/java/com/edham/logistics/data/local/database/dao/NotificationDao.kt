package com.edham.logistics.data.local.database.dao

import androidx.room.*
import com.edham.logistics.data.local.database.NotificationEntity

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY created_at DESC")
    suspend fun getAllNotifications(): List<NotificationEntity>

    @Query("SELECT * FROM notifications WHERE id = :notificationId")
    suspend fun getNotificationById(notificationId: String): NotificationEntity?

    @Query("SELECT * FROM notifications WHERE user_id = :userId ORDER BY created_at DESC")
    suspend fun getNotificationsByUserId(userId: String): List<NotificationEntity>

    @Query("SELECT * FROM notifications WHERE user_id = :userId AND is_read = 0 ORDER BY created_at DESC")
    suspend fun getUnreadNotificationsByUserId(userId: String): List<NotificationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNotifications(notifications: List<NotificationEntity>)

    @Update
    suspend fun updateNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET is_read = 1 WHERE id = :notificationId")
    suspend fun markAsRead(notificationId: String)

    @Delete
    suspend fun deleteNotification(notification: NotificationEntity)

    @Query("DELETE FROM notifications WHERE id = :notificationId")
    suspend fun deleteNotificationById(notificationId: String)

    @Query("DELETE FROM notifications WHERE user_id = :userId")
    suspend fun deleteNotificationsByUserId(userId: String)
}
