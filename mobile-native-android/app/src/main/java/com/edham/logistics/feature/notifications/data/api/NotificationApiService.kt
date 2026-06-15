package com.edham.logistics.feature.notifications.data.api

import com.edham.logistics.feature.notifications.domain.model.*
import retrofit2.Response
import retrofit2.http.*

interface NotificationApiService {

    // Send notifications
    @POST("notifications/send")
    suspend fun sendNotification(
        @Body request: SendNotificationRequest
    ): Response<String>

    @POST("notifications/send-bulk")
    suspend fun sendBulkNotification(
        @Body request: SendBulkNotificationRequest
    ): Response<List<String>>

    @POST("notifications/push")
    suspend fun sendPushNotification(
        @Body payload: PushNotificationPayload
    ): Response<String>

    @POST("notifications/email")
    suspend fun sendEmailNotification(
        @Body request: com.google.gson.JsonObject
    ): Response<Unit>

    @POST("notifications/sms")
    suspend fun sendSmsNotification(
        @Body request: com.google.gson.JsonObject
    ): Response<Unit>

    // Get notifications
    @POST("notifications/list")
    suspend fun getNotifications(
        @Body request: GetNotificationsRequest
    ): Response<List<Notification>>

    @GET("notifications/{notificationId}")
    suspend fun getNotificationById(
        @Path("notificationId") notificationId: String
    ): Response<Notification>

    @GET("notifications/{userId}/unread-count")
    suspend fun getUnreadCount(
        @Path("userId") userId: String
    ): Response<Int>

    // Update notifications
    @POST("notifications/mark-read")
    suspend fun markNotificationAsRead(
        @Body request: MarkNotificationReadRequest
    ): Response<Unit>

    @POST("notifications/mark-all-read")
    suspend fun markAllNotificationsAsRead(
        @Body request: MarkAllNotificationsReadRequest
    ): Response<Unit>

    @DELETE("notifications/{notificationId}")
    suspend fun deleteNotification(
        @Path("notificationId") notificationId: String,
        @Query("userId") userId: String
    ): Response<Unit>

    // Notification settings
    @GET("notifications/settings/{userId}")
    suspend fun getNotificationSettings(
        @Path("userId") userId: String
    ): Response<NotificationSettings>

    @PUT("notifications/settings")
    suspend fun updateNotificationSettings(
        @Body request: UpdateNotificationSettingsRequest
    ): Response<NotificationSettings>

    @GET("notifications/settings/default/{userRole}")
    suspend fun getDefaultNotificationSettings(
        @Path("userRole") userRole: UserRole
    ): Response<NotificationSettings>

    @POST("notifications/settings/reset/{userId}")
    suspend fun resetNotificationSettings(
        @Path("userId") userId: String
    ): Response<NotificationSettings>

    // Templates
    @GET("notifications/templates/{type}")
    suspend fun getNotificationTemplate(
        @Path("type") type: NotificationType
    ): Response<NotificationTemplate>

    @GET("notifications/templates")
    suspend fun getAllNotificationTemplates(): Response<List<NotificationTemplate>>

    @PUT("notifications/templates")
    suspend fun updateNotificationTemplate(
        @Body template: NotificationTemplate
    ): Response<NotificationTemplate>

    // Statistics
    @POST("notifications/stats")
    suspend fun getNotificationStats(
        @Body request: GetNotificationStatsRequest
    ): Response<NotificationStats>

    @GET("notifications/{notificationId}/delivery-stats")
    suspend fun getDeliveryStats(
        @Path("notificationId") notificationId: String
    ): Response<Map<String, Any>>

    @GET("notifications/{userId}/user-stats")
    suspend fun getUserStats(
        @Path("userId") userId: String,
        @Query("period") period: String
    ): Response<Map<String, Any>>

    // Device management
    @POST("notifications/devices/register")
    suspend fun registerDevice(
        @Body request: com.google.gson.JsonObject
    ): Response<Unit>

    @DELETE("notifications/devices/{deviceId}")
    suspend fun unregisterDevice(
        @Path("deviceId") deviceId: String,
        @Query("userId") userId: String
    ): Response<Unit>

    @GET("notifications/devices/{userId}")
    suspend fun getUserDevices(
        @Path("userId") userId: String
    ): Response<List<Map<String, Any>>>

    @PUT("notifications/devices/{deviceId}/token")
    suspend fun updateDeviceToken(
        @Path("deviceId") deviceId: String,
        @Body request: com.google.gson.JsonObject
    ): Response<Unit>

    // Scheduling
    @POST("notifications/schedule")
    suspend fun scheduleNotification(
        @Body request: SendNotificationRequest,
        @Query("scheduledAt") scheduledAt: Long
    ): Response<String>

    @DELETE("notifications/scheduled/{notificationId}")
    suspend fun cancelScheduledNotification(
        @Path("notificationId") notificationId: String
    ): Response<Unit>

    @GET("notifications/scheduled/{userId}")
    suspend fun getScheduledNotifications(
        @Path("userId") userId: String
    ): Response<List<Notification>>

    // Batch operations
    @POST("notifications/mark-multiple-read")
    suspend fun markMultipleAsRead(
        @Body request: com.google.gson.JsonObject
    ): Response<Unit>

    @DELETE("notifications/multiple")
    suspend fun deleteMultipleNotifications(
        @Body request: com.google.gson.JsonObject
    ): Response<Unit>

    @POST("notifications/archive")
    suspend fun archiveNotifications(
        @Body request: com.google.gson.JsonObject
    ): Response<Unit>

    // Search and filtering
    @POST("notifications/search")
    suspend fun searchNotifications(
        @Path("userId") userId: String,
        @Query("query") query: String,
        @Body filters: Map<String, Any>
    ): Response<List<Notification>>

    @GET("notifications/{userId}/type/{type}")
    suspend fun getNotificationsByType(
        @Path("userId") userId: String,
        @Path("type") type: NotificationType,
        @Query("limit") limit: Int
    ): Response<List<Notification>>

    @GET("notifications/{userId}/category/{category}")
    suspend fun getNotificationsByCategory(
        @Path("userId") userId: String,
        @Path("category") category: NotificationCategory,
        @Query("limit") limit: Int
    ): Response<List<Notification>>

    // Analytics
    @POST("notifications/track-event")
    suspend fun trackNotificationEvent(
        @Path("notificationId") notificationId: String,
        @Query("event") event: String,
        @Body data: Map<String, Any>
    ): Response<Unit>

    @POST("notifications/analytics")
    suspend fun getNotificationAnalytics(
        @Query("period") period: String,
        @Body filters: Map<String, Any>
    ): Response<Map<String, Any>>
}
