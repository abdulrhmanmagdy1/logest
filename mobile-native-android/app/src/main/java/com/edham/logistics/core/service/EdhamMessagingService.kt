package com.edham.logistics.core.service

import com.edham.logistics.core.utils.NotificationHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber

/**
 * Firebase Cloud Messaging service to handle incoming high-priority alerts.
 */
class EdhamMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Timber.d("Message Received: ${message.data}")

        val type = message.data["type"]
        val title = message.notification?.title ?: message.data["title"] ?: "تنبيه إدهام"
        val body = message.notification?.body ?: message.data["body"] ?: ""

        when (type) {
            "CRITICAL_TEMP" -> {
                NotificationHelper.showCriticalAlert(applicationContext, title, body)
            }
            "NEW_ORDER" -> {
                // Show a standard notification for supervisor
                NotificationHelper.showCriticalAlert(applicationContext, title, body)
            }
            "MISSION_ASSIGNED" -> {
                // For Driver
                NotificationHelper.showCriticalAlert(applicationContext, "مهمة جديدة", body)
                // Logic to trigger DriverNewMissionActivity
            }
            "MAINTENANCE_APPROVAL" -> {
                // For Accountant
                NotificationHelper.showCriticalAlert(applicationContext, "طلب اعتماد مالي", body)
            }
            "DRIVER_EXPENSE" -> {
                // For Accountant
                NotificationHelper.showCriticalAlert(applicationContext, "مصروف سائق جديد", body)
            }
            else -> {
                // Handle other types
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.d("New Token: $token")
        // Usually you'd send this to your backend
    }
}
