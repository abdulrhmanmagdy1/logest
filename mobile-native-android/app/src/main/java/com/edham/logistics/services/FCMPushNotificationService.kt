package com.edham.logistics.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.edham.logistics.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * FCM Push Notification Service - handles Firebase Cloud Messaging notifications
 * Provides push notifications for shipment updates, trip assignments, and alerts
 */
class FCMPushNotificationService : FirebaseMessagingService() {
    
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    companion object {
        private const val TAG = "FCMPushNotification"
        private const val CHANNEL_ID = "edham_notifications"
        private const val CHANNEL_NAME = "Edham Logistics Notifications"
        private const val CHANNEL_DESCRIPTION = "Notifications for shipment updates, trip assignments, and alerts"
    }
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Log.d(TAG, "Message received from: ${remoteMessage.from}")
        
        // Check user preferences
        val prefs = getSharedPreferences("customer_settings", Context.MODE_PRIVATE)
        val pushEnabled = prefs.getBoolean("push_enabled", true)
        if (!pushEnabled) {
            Log.d(TAG, "Push notifications are disabled in settings. Ignoring message.")
            return
        }
        
        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            sendNotification(it.title ?: "Edham Logistics", it.body ?: "New notification", remoteMessage.data)
        }
        
        // Check if message contains data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage.data)
        }
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        
        // Send token to server
        sendTokenToServer(token)
    }
    
    /**
     * Handle data-only messages
     */
    private fun handleDataMessage(data: Map<String, String>) {
        val title = data["title"] ?: "Edham Logistics"
        val body = data["body"] ?: "New notification"
        val type = data["type"] ?: "general"
        
        when (type) {
            "shipment_update" -> handleShipmentUpdate(data)
            "trip_assignment" -> handleTripAssignment(data)
            "alert" -> handleAlert(data)
            else -> sendNotification(title, body, data)
        }
    }
    
    /**
     * Handle shipment update notifications
     */
    private fun handleShipmentUpdate(data: Map<String, String>) {
        val shipmentId = data["shipmentId"] ?: ""
        val status = data["status"] ?: ""
        
        val title = "تحديث الشحنة"
        val body = when (status) {
            "in_transit" -> "الشحنة $shipmentId في الطريق"
            "delivered" -> "تم تسليم الشحنة $shipmentId بنجاح"
            "delayed" -> "تأخير في الشحنة $shipmentId"
            else -> "تحديث جديد للشحنة $shipmentId"
        }
        
        sendNotification(title, body, data)
    }
    
    /**
     * Handle trip assignment notifications
     */
    private fun handleTripAssignment(data: Map<String, String>) {
        val tripId = data["tripId"] ?: ""
        val pickup = data["pickup"] ?: ""
        val delivery = data["delivery"] ?: ""
        
        val title = "تعيين رحلة جديدة"
        val body = "رحلة جديدة: من $pickup إلى $delivery"
        
        sendNotification(title, body, data)
    }
    
    /**
     * Handle alert notifications
     */
    private fun handleAlert(data: Map<String, String>) {
        val alertType = data["alertType"] ?: ""
        
        val title = when (alertType) {
            "low_fuel" -> "تحذير: الوقود منخفض"
            "maintenance_due" -> "تنبيه: صيانة مستحقة"
            "emergency" -> "طوارئ"
            else -> "تنبيه"
        }
        
        val body = data["message"] ?: "تنبيه جديد"
        sendNotification(title, body, data)
    }
    
    /**
     * Create and show notification
     */
    private fun sendNotification(title: String, messageBody: String, data: Map<String, String>) {
        createNotificationChannel()
        
        val notificationId = System.currentTimeMillis().toInt()
        
        // Create intent for notification tap
        val intent = createNotificationIntent(data)
        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
        
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
    
    /**
     * Create intent for notification tap
     */
    private fun createNotificationIntent(data: Map<String, String>): Intent {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        
        // Add extra data based on notification type
        data["type"]?.let { type ->
            when (type) {
                "shipment_update" -> {
                    data["shipmentId"]?.let { shipmentId ->
                        intent?.putExtra("shipmentId", shipmentId)
                    }
                }
                "trip_assignment" -> {
                    data["tripId"]?.let { tripId ->
                        intent?.putExtra("tripId", tripId)
                    }
                }
                else -> {
                    // Handle other notification types
                }
            }
        }
        
        return intent ?: Intent()
    }
    
    /**
     * Create notification channel (required for Android O+)
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Send FCM token to server
     */
    private fun sendTokenToServer(token: String) {
        serviceScope.launch {
            try {
                // TODO: Send token to backend API
                Log.d(TAG, "Token sent to server: $token")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send token to server", e)
            }
        }
    }
}
