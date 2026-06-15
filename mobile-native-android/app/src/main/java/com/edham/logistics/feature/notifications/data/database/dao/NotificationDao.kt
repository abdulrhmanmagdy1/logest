package com.edham.logistics.feature.notifications.data.database.dao

import androidx.room.*
import com.edham.logistics.feature.notifications.data.database.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    
    @Query("SELECT * FROM notifications WHERE userId = :userId AND userRole = :userRole ORDER BY timestamp DESC LIMIT :limit OFFSET :offset")
    suspend fun getNotifications(userId: String, userRole: String, limit: Int, offset: Int): List<NotificationEntity>
    
    @Query("SELECT * FROM notifications WHERE userId = :userId AND userRole = :userRole AND isRead = 0 ORDER BY timestamp DESC")
    fun observeUnreadNotifications(userId: String, userRole: String): Flow<List<NotificationEntity>>
    
    @Query("SELECT * FROM notifications WHERE userId = :userId AND userRole = :userRole AND isRead = 0")
    suspend fun getUnreadNotifications(userId: String, userRole: String): List<NotificationEntity>
    
    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND userRole = :userRole AND isRead = 0")
    suspend fun getUnreadCount(userId: String, userRole: String): Int
    
    @Query("SELECT * FROM notifications WHERE id = :notificationId")
    suspend fun getNotificationById(notificationId: String): NotificationEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)
    
    @Update
    suspend fun updateNotification(notification: NotificationEntity)
    
    @Query("UPDATE notifications SET isRead = 1 WHERE id = :notificationId")
    suspend fun markAsRead(notificationId: String)
    
    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId AND userRole = :userRole")
    suspend fun markAllAsRead(userId: String, userRole: String)
    
    @Query("UPDATE notifications SET isRead = 1 WHERE id IN (:notificationIds)")
    suspend fun markMultipleAsRead(notificationIds: List<String>)
    
    @Delete
    suspend fun deleteNotification(notification: NotificationEntity)
    
    @Query("DELETE FROM notifications WHERE id = :notificationId")
    suspend fun deleteNotification(notificationId: String)
    
    @Query("DELETE FROM notifications WHERE userId = :userId AND userRole = :userRole")
    suspend fun deleteAllNotifications(userId: String, userRole: String)
    
    @Query("DELETE FROM notifications WHERE id IN (:notificationIds)")
    suspend fun deleteMultipleNotifications(notificationIds: List<String>)
    
    @Query("DELETE FROM notifications WHERE timestamp < :timestamp")
    suspend fun deleteOldNotifications(timestamp: Long)
    
    @Query("SELECT * FROM notifications WHERE userId = :userId AND type = :type ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getNotificationsByType(userId: String, type: String, limit: Int): List<NotificationEntity>
    
    @Query("SELECT * FROM notifications WHERE userId = :userId AND category = :category ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getNotificationsByCategory(userId: String, category: String, limit: Int): List<NotificationEntity>
    
    @Query("SELECT * FROM notifications WHERE userId = :userId AND (title LIKE '%' || :query || '%' OR message LIKE '%' || :query || '%') ORDER BY timestamp DESC")
    suspend fun searchNotifications(userId: String, query: String): List<NotificationEntity>
    
    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId")
    suspend fun getTotalNotificationCount(userId: String): Int
    
    @Query("DELETE FROM notifications")
    suspend fun clearAllNotifications()
}
