package com.edham.logistics.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.edham.logistics.R
import com.edham.logistics.ui.screens.TrackShipmentActivity

/**
 * Simple notification service for shipment status updates.
 * Uses local notifications for real-time alerts.
 */
class NotificationService(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val CHANNEL_ID = "edham_shipments"
    private val CHANNEL_NAME = "تحديثات الشحنات"

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات تحديثات حالة الشحنات"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showShipmentStatusNotification(
        shipmentId: String,
        status: String,
        message: String
    ) {
        val intent = Intent(context, TrackShipmentActivity::class.java).apply {
            putExtra("shipment_id", shipmentId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            shipmentId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val statusText = when (status.lowercase()) {
            "pending" -> "بانتظار التأكيد"
            "confirmed" -> "تم التأكيد"
            "assigned" -> "تم تعيين سائق"
            "at_pickup" -> "السائق في موقع الاستلام"
            "picked_up" -> "تم استلام البضاعة"
            "on_the_way" -> "الشحنة في الطريق"
            "at_delivery" -> "السائق في موقع التسليم"
            "delivered" -> "تم التسليم"
            "completed" -> "اكتملت الرحلة"
            else -> status
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("تحديث الشحنة $shipmentId")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText("$statusText\n\n$message"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(shipmentId.hashCode(), notification)
    }

    fun showDriverAssignmentNotification(
        shipmentId: String,
        driverName: String,
        driverPhone: String
    ) {
        val intent = Intent(context, TrackShipmentActivity::class.java).apply {
            putExtra("shipment_id", shipmentId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            shipmentId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("تم تعيين سائق")
            .setContentText("السائق $driverName معين لشحنتك")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("السائق: $driverName\nرقم الهاتف: $driverPhone\n\nاضغط للتتبع"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify("driver_$shipmentId".hashCode(), notification)
    }

    fun showAlertNotification(
        title: String,
        message: String
    ) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify("alert_${System.currentTimeMillis()}".hashCode(), notification)
    }
}
