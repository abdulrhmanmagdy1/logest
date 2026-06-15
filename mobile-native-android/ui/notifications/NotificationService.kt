// ============================================
// 🚀 Edham Logistics - Advanced Notification Service
// Premium Dark Theme with Smart Notifications
// ============================================

package com.edham.logistics.ui.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.edham.logistics.R
import com.edham.logistics.ui.theme.EdhamOrange
import com.edham.logistics.ui.theme.SuccessGreen
import com.edham.logistics.ui.theme.WarningYellow
import com.edham.logistics.ui.theme.ErrorRed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ============================================
 * Advanced Notification Service
 * ============================================
 * خدمة الإشعارات المتقدمة مع تخصيص كامل
 */
class NotificationService(private val context: Context) {
    
    private val notificationManager = NotificationManagerCompat.from(context)
    private val scope = CoroutineScope(Dispatchers.IO)
    
    companion object {
        const val SHIPMENT_UPDATES_CHANNEL = "shipment_updates"
        const val DELIVERY_ALERTS_CHANNEL = "delivery_alerts"
        const val PAYMENT_NOTIFICATIONS_CHANNEL = "payment_notifications"
        const val SYSTEM_NOTIFICATIONS_CHANNEL = "system_notifications"
        const val PROMOTIONAL_CHANNEL = "promotional"
        
        const val SHIPMENT_DELIVERED = 1001
        const val SHIPMENT_IN_TRANSIT = 1002
        const val SHIPMENT_DELAYED = 1003
        const val PAYMENT_RECEIVED = 2001
        const val PAYMENT_FAILED = 2002
        const val NEW_MESSAGE = 3001
        const val SYSTEM_UPDATE = 4001
        const val PROMOTIONAL_OFFER = 5001
    }
    
    init {
        createNotificationChannels()
    }
    
    /**
     * ============================================
     * Create Notification Channels
     * ============================================
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    SHIPMENT_UPDATES_CHANNEL,
                    "تحديثات الشحنات",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "إشعارات حول حالة الشحنات والتوصيل"
                    enableLights(true)
                    lightColor = EdhamOrange.hashCode()
                    enableVibration(true)
                    setShowBadge(true)
                },
                
                NotificationChannel(
                    DELIVERY_ALERTS_CHANNEL,
                    "تنبيهات التوصيل",
                    NotificationManager.IMPORTANCE_URGENT
                ).apply {
                    description = "تنبيهات عاجلة للتوصيل"
                    enableLights(true)
                    lightColor = WarningYellow.hashCode()
                    enableVibration(true)
                    setShowBadge(true)
                },
                
                NotificationChannel(
                    PAYMENT_NOTIFICATIONS_CHANNEL,
                    "إشعارات الدفع",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "إشعارات حول المدفوعات والفواتير"
                    enableLights(true)
                    lightColor = SuccessGreen.hashCode()
                    enableVibration(false)
                    setShowBadge(true)
                },
                
                NotificationChannel(
                    SYSTEM_NOTIFICATIONS_CHANNEL,
                    "إشعارات النظام",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "إشعارات النظام والتحديثات"
                    enableLights(false)
                    enableVibration(false)
                    setShowBadge(false)
                },
                
                NotificationChannel(
                    PROMOTIONAL_CHANNEL,
                    "عروض ترويجية",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "عروض وخصومات خاصة"
                    enableLights(true)
                    lightColor = EdhamOrange.hashCode()
                    enableVibration(false)
                    setShowBadge(true)
                }
            )
            
            channels.forEach { channel ->
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
    
    /**
     * ============================================
     * Shipment Notifications
     * ============================================
     */
    fun showShipmentDeliveredNotification(
        shipmentId: String,
        trackingNumber: String,
        recipientName: String
    ) {
        val intent = createShipmentDetailsIntent(shipmentId)
        val pendingIntent = PendingIntent.getActivity(
            context,
            SHIPMENT_DELIVERED,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val largeIcon = BitmapFactory.decodeResource(context.resources, R.drawable.edham_logo)
        
        val notification = NotificationCompat.Builder(context, SHIPMENT_UPDATES_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification_shipment)
            .setLargeIcon(largeIcon)
            .setContentTitle("تم تسليم الشحنة بنجاح! 🎉")
            .setContentText("الشحنة $trackingNumber تم تسليمها إلى $recipientName")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("تهانينا! تم تسليم شحنتك بنجاح.\nرقم التتبع: $trackingNumber\nالمستلم: $recipientName\n\nشكراً لثقتكم في إدهام اللوجستي"))
            .setColor(SuccessGreen.hashCode())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_rate,
                "قيم الخدمة",
                createRateServiceIntent(shipmentId)
            )
            .addAction(
                R.drawable.ic_track,
                "تتبع الشحنة",
                createTrackShipmentIntent(shipmentId)
            )
            .build()
        
        notificationManager.notify(SHIPMENT_DELIVERED, notification)
    }
    
    fun showShipmentInTransitNotification(
        shipmentId: String,
        trackingNumber: String,
        currentLocation: String,
        estimatedDelivery: String
    ) {
        val intent = createShipmentDetailsIntent(shipmentId)
        val pendingIntent = PendingIntent.getActivity(
            context,
            SHIPMENT_IN_TRANSIT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, SHIPMENT_UPDATES_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification_shipment)
            .setContentTitle("شحنتك في الطريق 🚚")
            .setContentText("$trackingNumber - حالياً في $currentLocation")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("شحنتك $trackingNumber حالياً في الطريق\nالموقع الحالي: $currentLocation\nالتوصيل المتوقع: $estimatedDelivery\n\nاضغط للمزيد من التفاصيل"))
            .setColor(EdhamOrange.hashCode())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setProgress(0, 0, true)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_map,
                "عرض على الخريطة",
                createMapIntent(shipmentId)
            )
            .build()
        
        notificationManager.notify(SHIPMENT_IN_TRANSIT, notification)
    }
    
    fun showShipmentDelayedNotification(
        shipmentId: String,
        trackingNumber: String,
        delayReason: String,
        newEstimatedDelivery: String
    ) {
        val intent = createShipmentDetailsIntent(shipmentId)
        val pendingIntent = PendingIntent.getActivity(
            context,
            SHIPMENT_DELAYED,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, DELIVERY_ALERTS_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification_alert)
            .setContentTitle("تأخير في الشحنة ⚠️")
            .setContentText("$trackingNumber - تأخير في التوصيل")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("عذراً، هناك تأخير في شحنتك $trackingNumber\nسبب التأخير: $delayReason\nالتاريخ الجديد: $newEstimatedDelivery\n\nنعتذر عن الإزعاج"))
            .setColor(ErrorRed.hashCode())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_support,
                "تواصل مع الدعم",
                createSupportIntent(shipmentId)
            )
            .build()
        
        notificationManager.notify(SHIPMENT_DELAYED, notification)
    }
    
    /**
     * ============================================
     * Payment Notifications
     * ============================================
     */
    fun showPaymentReceivedNotification(
        paymentId: String,
        amount: String,
        shipmentId: String
    ) {
        val intent = createPaymentDetailsIntent(paymentId)
        val pendingIntent = PendingIntent.getActivity(
            context,
            PAYMENT_RECEIVED,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, PAYMENT_NOTIFICATIONS_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification_payment)
            .setContentTitle("تم استلام الدفع بنجاح! 💰")
            .setContentText("مبلغ $amount تم استلامه للشحنة $shipmentId")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("تم استلام دفعتك بنجاح\nالمبلغ: $amount\nرقم الشحنة: $shipmentId\nرقم المعاملة: $paymentId\n\nشكراً لك"))
            .setColor(SuccessGreen.hashCode())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_receipt,
                "عرض الإيصال",
                createReceiptIntent(paymentId)
            )
            .build()
        
        notificationManager.notify(PAYMENT_RECEIVED, notification)
    }
    
    fun showPaymentFailedNotification(
        paymentId: String,
        amount: String,
        failureReason: String
    ) {
        val intent = createRetryPaymentIntent(paymentId)
        val pendingIntent = PendingIntent.getActivity(
            context,
            PAYMENT_FAILED,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, PAYMENT_NOTIFICATIONS_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification_error)
            .setContentTitle("فشل عملية الدفع ❌")
            .setContentText("لم يتم استلام مبلغ $amount")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("فشلت عملية الدفع\nالمبلغ: $amount\nالسبب: $failureReason\nرقم المعاملة: $paymentId\n\nاضغط لإعادة المحاولة"))
            .setColor(ErrorRed.hashCode())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_retry,
                "إعادة المحاولة",
                createRetryPaymentIntent(paymentId)
            )
            .build()
        
        notificationManager.notify(PAYMENT_FAILED, notification)
    }
    
    /**
     * ============================================
     * Message Notifications
     * ============================================
     */
    fun showNewMessageNotification(
        messageId: String,
        senderName: String,
        messageContent: String,
        isUrgent: Boolean = false
    ) {
        val intent = createMessageDetailsIntent(messageId)
        val pendingIntent = PendingIntent.getActivity(
            context,
            NEW_MESSAGE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, SHIPMENT_UPDATES_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification_message)
            .setContentTitle("رسالة جديدة من $senderName")
            .setContentText(messageContent.take(50) + "...")
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageContent))
            .setColor(if (isUrgent) WarningYellow.hashCode() else EdhamOrange.hashCode())
            .setPriority(if (isUrgent) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setDefaults(if (isUrgent) NotificationCompat.DEFAULT_ALL else NotificationCompat.DEFAULT_SOUND)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_reply,
                "رد",
                createReplyIntent(messageId)
            )
            .build()
        
        notificationManager.notify(NEW_MESSAGE, notification)
    }
    
    /**
     * ============================================
     * System Notifications
     * ============================================
     */
    fun showSystemUpdateNotification(
        updateTitle: String,
        updateDescription: String,
        isMandatory: Boolean = false
    ) {
        val intent = createUpdateIntent()
        val pendingIntent = PendingIntent.getActivity(
            context,
            SYSTEM_UPDATE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, SYSTEM_NOTIFICATIONS_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification_system)
            .setContentTitle(updateTitle)
            .setContentText(updateDescription)
            .setStyle(NotificationCompat.BigTextStyle().bigText(updateDescription))
            .setColor(EdhamOrange.hashCode())
            .setPriority(if (isMandatory) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(false)
            .setOngoing(isMandatory)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_download,
                "تحديث الآن",
                createUpdateIntent()
            )
            .build()
        
        notificationManager.notify(SYSTEM_UPDATE, notification)
    }
    
    /**
     * ============================================
     * Promotional Notifications
     * ============================================
     */
    fun showPromotionalNotification(
        offerTitle: String,
        offerDescription: String,
        discountPercentage: String,
        validUntil: String
    ) {
        val intent = createPromoIntent()
        val pendingIntent = PendingIntent.getActivity(
            context,
            PROMOTIONAL_OFFER,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, PROMOTIONAL_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification_promo)
            .setContentTitle("عرض خاص! 🎁")
            .setContentText("$offerTitle - خصم $discountPercentage")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$offerTitle\n$offerDescription\nخصم: $discountPercentage\nصالح حتى: $validUntil\n\nاضغط للاستفادة من العرض"))
            .setColor(EdhamOrange.hashCode())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(
                R.drawable.ic_offer,
                "استخدم العرض",
                createPromoIntent()
            )
            .build()
        
        notificationManager.notify(PROMOTIONAL_OFFER, notification)
    }
    
    /**
     * ============================================
     * Progress Notifications
     * ============================================
     */
    fun showProgressNotification(
        notificationId: Int,
        title: String,
        content: String,
        progress: Int,
        max: Int = 100,
        indeterminate: Boolean = false
    ) {
        val notification = NotificationCompat.Builder(context, SYSTEM_NOTIFICATIONS_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification_progress)
            .setContentTitle(title)
            .setContentText(content)
            .setProgress(max, progress, indeterminate)
            .setColor(EdhamOrange.hashCode())
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
        
        notificationManager.notify(notificationId, notification)
    }
    
    /**
     * ============================================
     * Cancel Notifications
     * ============================================
     */
    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
    
    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
    
    /**
     * ============================================
     * Intent Creators
     * ============================================
     */
    private fun createShipmentDetailsIntent(shipmentId: String): Intent {
        return Intent(context, ShipmentDetailsActivity::class.java).apply {
            putExtra("shipment_id", shipmentId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }
    
    private fun createPaymentDetailsIntent(paymentId: String): Intent {
        return Intent(context, PaymentDetailsActivity::class.java).apply {
            putExtra("payment_id", paymentId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }
    
    private fun createMessageDetailsIntent(messageId: String): Intent {
        return Intent(context, MessageDetailsActivity::class.java).apply {
            putExtra("message_id", messageId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }
    
    private fun createUpdateIntent(): Intent {
        return Intent(context, UpdateActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }
    
    private fun createPromoIntent(): Intent {
        return Intent(context, PromotionsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }
    
    private fun createRateServiceIntent(shipmentId: String): PendingIntent {
        val intent = Intent(context, RateServiceActivity::class.java).apply {
            putExtra("shipment_id", shipmentId)
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    
    private fun createTrackShipmentIntent(shipmentId: String): PendingIntent {
        val intent = Intent(context, TrackShipmentActivity::class.java).apply {
            putExtra("shipment_id", shipmentId)
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    
    private fun createMapIntent(shipmentId: String): PendingIntent {
        val intent = Intent(context, MapActivity::class.java).apply {
            putExtra("shipment_id", shipmentId)
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    
    private fun createSupportIntent(shipmentId: String): PendingIntent {
        val intent = Intent(context, SupportActivity::class.java).apply {
            putExtra("shipment_id", shipmentId)
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    
    private fun createReceiptIntent(paymentId: String): PendingIntent {
        val intent = Intent(context, ReceiptActivity::class.java).apply {
            putExtra("payment_id", paymentId)
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    
    private fun createRetryPaymentIntent(paymentId: String): PendingIntent {
        val intent = Intent(context, PaymentActivity::class.java).apply {
            putExtra("payment_id", paymentId)
            putExtra("retry", true)
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    
    private fun createReplyIntent(messageId: String): PendingIntent {
        val intent = Intent(context, ReplyActivity::class.java).apply {
            putExtra("message_id", messageId)
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
    
    /**
     * ============================================
     * Notification Groups
     * ============================================
     */
    fun createShipmentGroupNotification(
        shipmentCount: Int,
        activeShipments: List<String>
    ) {
        val intent = Intent(context, ShipmentsActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, SHIPMENT_UPDATES_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification_shipment)
            .setContentTitle("تحديثات الشحنات")
            .setContentText("لديك $shipmentCount شحنات نشطة")
            .setStyle(NotificationCompat.InboxStyle()
                .addLine("شحنة EDH-001 في الطريق")
                .addLine("شحنة EDH-002 تم تسليمها")
                .addLine("شحنة EDH-033 تأخير في التوصيل")
                .setSummaryText("$shipmentCount شحنات نشطة"))
            .setColor(EdhamOrange.hashCode())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setGroup("shipments")
            .setGroupSummary(true)
            .setContentIntent(pendingIntent)
            .build()
        
        notificationManager.notify(0, notification)
    }
    
    /**
     * ============================================
     * Smart Notifications
     * ============================================
     */
    fun showSmartNotification(
        type: SmartNotificationType,
        data: Map<String, String>
    ) {
        when (type) {
            SmartNotificationType.SHIPMENT_NEARBY -> {
                showShipmentNearbyNotification(
                    data["shipmentId"] ?: "",
                    data["trackingNumber"] ?: "",
                    data["distance"] ?: ""
                )
            }
            SmartNotificationType.OPTIMAL_DELIVERY_TIME -> {
                showOptimalDeliveryTimeNotification(
                    data["shipmentId"] ?: "",
                    data["optimalTime"] ?: ""
                )
            }
            SmartNotificationType.WEATHER_ALERT -> {
                showWeatherAlertNotification(
                    data["alert"] ?: "",
                    data["impact"] ?: ""
                )
            }
            SmartNotificationType.PRICE_DROP -> {
                showPriceDropNotification(
                    data["service"] ?: "",
                    data["oldPrice"] ?: "",
                    data["newPrice"] ?: ""
                )
            }
        }
    }
    
    private fun showShipmentNearbyNotification(
        shipmentId: String,
        trackingNumber: String,
        distance: String
    ) {
        val notification = NotificationCompat.Builder(context, DELIVERY_ALERTS_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification_location)
            .setContentTitle("شحنتك قريبة! 📍")
            .setContentText("$trackingNumber - على بعد $distance من عنوانك")
            .setColor(WarningYellow.hashCode())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(createShipmentDetailsIntent(shipmentId))
            .build()
        
        notificationManager.notify(6001, notification)
    }
    
    private fun showOptimalDeliveryTimeNotification(
        shipmentId: String,
        optimalTime: String
    ) {
        val notification = NotificationCompat.Builder(context, SHIPMENT_UPDATES_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification_time)
            .setContentTitle("وقت التوصيل الأمثل! ⏰")
            .setContentText("أفضل وقت لاستلام شحنتك: $optimalTime")
            .setColor(SuccessGreen.hashCode())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(createShipmentDetailsIntent(shipmentId))
            .build()
        
        notificationManager.notify(6002, notification)
    }
    
    private fun showWeatherAlertNotification(
        alert: String,
        impact: String
    ) {
        val notification = NotificationCompat.Builder(context, DELIVERY_ALERTS_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification_weather)
            .setContentTitle("تنبيه طقس 🌦️")
            .setContentText(alert)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("$alert\nالتأثير على الشحنات: $impact"))
            .setColor(WarningYellow.hashCode())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        
        notificationManager.notify(6003, notification)
    }
    
    private fun showPriceDropNotification(
        service: String,
        oldPrice: String,
        newPrice: String
    ) {
        val notification = NotificationCompat.Builder(context, PROMOTIONAL_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification_price)
            .setContentTitle("انخفاض السعر! 📉")
            .setContentText("$service: $oldPrice → $newPrice")
            .setColor(SuccessGreen.hashCode())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(createPromoIntent())
            .build()
        
        notificationManager.notify(6004, notification)
    }
}

/**
 * ============================================
 * Smart Notification Types
 * ============================================
 */
enum class SmartNotificationType {
    SHIPMENT_NEARBY,
    OPTIMAL_DELIVERY_TIME,
    WEATHER_ALERT,
    PRICE_DROP
}
