package com.edham.logistics.feature.notifications.domain.model

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val category: NotificationCategory,
    val priority: NotificationPriority,
    val userId: String,
    val userRole: UserRole,
    val shipmentId: String?,
    val driverId: String?,
    val customerId: String?,
    val data: Map<String, Any>?,
    val imageUrl: String?,
    val actionUrl: String?,
    val isRead: Boolean,
    val isPush: Boolean,
    val timestamp: Long,
    val expiresAt: Long?,
    val sound: String?,
    val vibration: Boolean,
    val badge: Int?,
    val deepLink: String?
)

data class NotificationSettings(
    val id: String,
    val userId: String,
    val userRole: UserRole,
    val pushEnabled: Boolean,
    val emailEnabled: Boolean,
    val smsEnabled: Boolean,
    val inAppEnabled: Boolean,
    val categories: Map<NotificationCategory, CategorySettings>,
    val quietHours: QuietHours?,
    val deviceId: String?,
    val fcmToken: String?,
    val lastUpdated: Long
)

data class CategorySettings(
    val enabled: Boolean,
    val pushEnabled: Boolean,
    val emailEnabled: Boolean,
    val smsEnabled: Boolean,
    val sound: String?,
    val vibration: Boolean,
    val priority: NotificationPriority
)

data class QuietHours(
    val enabled: Boolean,
    val startTime: String, // HH:mm format
    val endTime: String,   // HH:mm format
    val timezone: String,
    val weekendsOnly: Boolean,
    val emergencyBypass: Boolean
)

data class NotificationTemplate(
    val id: String,
    val type: NotificationType,
    val category: NotificationCategory,
    val titleTemplate: String,
    val messageTemplate: String,
    val variables: List<String>,
    val defaultSound: String?,
    val defaultVibration: Boolean,
    val defaultPriority: NotificationPriority,
    val roleSpecific: Map<UserRole, RoleSpecificTemplate>
)

data class RoleSpecificTemplate(
    val titleTemplate: String?,
    val messageTemplate: String?,
    val enabled: Boolean,
    val sound: String?,
    val vibration: Boolean,
    val priority: NotificationPriority
)

data class NotificationStats(
    val totalSent: Long,
    val totalDelivered: Long,
    val totalRead: Long,
    val totalClicked: Long,
    val deliveryRate: Double,
    val readRate: Double,
    val clickRate: Double,
    val statsByType: Map<NotificationType, TypeStats>,
    val statsByRole: Map<UserRole, RoleStats>,
    val statsByCategory: Map<NotificationCategory, CategoryStats>,
    val period: String
)

data class TypeStats(
    val type: NotificationType,
    val sent: Long,
    val delivered: Long,
    val read: Long,
    val clicked: Long
)

data class RoleStats(
    val role: UserRole,
    val sent: Long,
    val delivered: Long,
    val read: Long,
    val clicked: Long
)

data class CategoryStats(
    val category: NotificationCategory,
    val sent: Long,
    val delivered: Long,
    val read: Long,
    val clicked: Long
)

data class PushNotificationPayload(
    val to: String, // FCM token or topic
    val notification: PushNotification,
    val data: Map<String, String>?,
    val priority: String?, // "high" or "normal"
    val ttl: Long?, // Time to live in seconds
    val collapseKey: String?,
    val dryRun: Boolean
)

data class PushNotification(
    val title: String,
    val body: String,
    val icon: String?,
    val sound: String?,
    val badge: Int?,
    val clickAction: String?,
    val color: String?,
    val tag: String?,
    val imageUrl: String?
)

// Enums
enum class NotificationType {
    SHIPMENT_CREATED,
    SHIPMENT_ASSIGNED,
    SHIPMENT_PICKED_UP,
    SHIPMENT_IN_TRANSIT,
    SHIPMENT_DELIVERED,
    SHIPMENT_CANCELLED,
    SHIPMENT_DELAYED,
    SHIPMENT_STATUS_UPDATED,
    
    DRIVER_ASSIGNED,
    DRIVER_LOCATION_UPDATED,
    DRIVER_ARRIVED,
    DRIVER_DEPARTED,
    DRIVER_COMPLETED_TRIP,
    DRIVER_STATUS_CHANGED,
    
    TEMPERATURE_ALERT,
    TEMPERATURE_NORMAL,
    SENSOR_OFFLINE,
    SENSOR_BATTERY_LOW,
    SENSOR_CALIBRATION_REQUIRED,
    
    PAYMENT_RECEIVED,
    PAYMENT_FAILED,
    PAYMENT_PENDING,
    PAYMENT_OVERDUE,
    INVOICE_GENERATED,
    INVOICE_OVERDUE,
    
    SYSTEM_MAINTENANCE,
    SYSTEM_UPDATE,
    SYSTEM_ALERT,
    SYSTEM_ERROR,
    USER_LOGIN,
    USER_LOGOUT,
    
    GENERAL_ANNOUNCEMENT,
    PROMOTION,
    REMINDER,
    WARNING,
    ERROR
}

enum class NotificationCategory {
    SHIPMENT,
    DRIVER,
    TEMPERATURE,
    PAYMENT,
    SYSTEM,
    USER,
    MARKETING,
    GENERAL
}

enum class NotificationPriority {
    LOW,
    NORMAL,
    HIGH,
    URGENT
}

enum class UserRole {
    CUSTOMER,
    DRIVER,
    ADMIN,
    ACCOUNTANT
}

// Request/Response models
data class SendNotificationRequest(
    val userIds: List<String>,
    val userRoles: List<UserRole>?,
    val title: String,
    val message: String,
    val type: NotificationType,
    val category: NotificationCategory,
    val priority: NotificationPriority,
    val data: Map<String, Any>?,
    val imageUrl: String?,
    val actionUrl: String?,
    val sound: String?,
    val vibration: Boolean,
    val deepLink: String?,
    val scheduledAt: Long?
)

data class SendBulkNotificationRequest(
    val notification: SendNotificationRequest,
    val userIds: List<String>,
    val batchSize: Int = 1000
)

data class UpdateNotificationSettingsRequest(
    val userId: String,
    val settings: NotificationSettings
)

data class GetNotificationsRequest(
    val userId: String,
    val userRole: UserRole,
    val types: List<NotificationType>?,
    val categories: List<NotificationCategory>?,
    val isRead: Boolean?,
    val limit: Int = 50,
    val offset: Int = 0,
    val startDate: Long?,
    val endDate: Long?
)

data class MarkNotificationReadRequest(
    val notificationId: String,
    val userId: String
)

data class MarkAllNotificationsReadRequest(
    val userId: String,
    val userRole: UserRole
)

data class DeleteNotificationRequest(
    val notificationId: String,
    val userId: String
)

data class GetNotificationStatsRequest(
    val period: String, // "daily", "weekly", "monthly"
    val startDate: Long?,
    val endDate: Long?,
    val userRole: UserRole?,
    val notificationType: NotificationType?,
    val notificationCategory: NotificationCategory?
)

// WebSocket/Real-time models
data class NotificationWebSocketMessage(
    val type: NotificationWebSocketType,
    val data: Any,
    val timestamp: Long,
    val userId: String?,
    val userRole: UserRole?
)

enum class NotificationWebSocketType {
    NOTIFICATION_SENT,
    NOTIFICATION_DELIVERED,
    NOTIFICATION_READ,
    NOTIFICATION_CLICKED,
    NOTIFICATION_FAILED,
    SETTINGS_UPDATED,
    DEVICE_CONNECTED,
    DEVICE_DISCONNECTED
}

// Predefined notification templates
object NotificationTemplates {
    
    // Shipment notifications
    val SHIPMENT_CREATED = NotificationTemplate(
        id = "shipment_created",
        type = NotificationType.SHIPMENT_CREATED,
        category = NotificationCategory.SHIPMENT,
        titleTemplate = "شحنة جديدة: {shipmentNumber}",
        messageTemplate = "تم إنشاء شحنة جديدة من {pickupLocation} إلى {deliveryLocation}",
        variables = listOf("shipmentNumber", "pickupLocation", "deliveryLocation"),
        defaultSound = "default",
        defaultVibration = true,
        defaultPriority = NotificationPriority.NORMAL,
        roleSpecific = mapOf(
            UserRole.CUSTOMER to RoleSpecificTemplate(
                titleTemplate = "شحنتك الجديدة: {shipmentNumber}",
                messageTemplate = "تم إنشاء شحنتك الجديدة من {pickupLocation} إلى {deliveryLocation}",
                enabled = true,
                sound = "default",
                vibration = true,
                priority = NotificationPriority.NORMAL
            ),
            UserRole.DRIVER to RoleSpecificTemplate(
                titleTemplate = "شحنة جديدة متاحة",
                messageTemplate = "شحنة جديدة متاحة: {shipmentNumber}",
                enabled = true,
                sound = "default",
                vibration = true,
                priority = NotificationPriority.HIGH
            ),
            UserRole.ADMIN to RoleSpecificTemplate(
                titleTemplate = "شحنة جديدة: {shipmentNumber}",
                messageTemplate = "العميل {customerName} أنشأ شحنة جديدة: {shipmentNumber}",
                enabled = true,
                sound = "default",
                vibration = false,
                priority = NotificationPriority.NORMAL
            )
        )
    )
    
    val SHIPMENT_ASSIGNED = NotificationTemplate(
        id = "shipment_assigned",
        type = NotificationType.DRIVER_ASSIGNED,
        category = NotificationCategory.DRIVER,
        titleTemplate = "تعيين سائق",
        messageTemplate = "تم تعيين السائق {driverName} للشحنة {shipmentNumber}",
        variables = listOf("driverName", "shipmentNumber"),
        defaultSound = "default",
        defaultVibration = true,
        defaultPriority = NotificationPriority.HIGH,
        roleSpecific = mapOf(
            UserRole.DRIVER to RoleSpecificTemplate(
                titleTemplate = "شحنة جديدة: {shipmentNumber}",
                messageTemplate = "تم تعيينك لشحنة {shipmentNumber} من {pickupLocation} إلى {deliveryLocation}",
                enabled = true,
                sound = "notification_high",
                vibration = true,
                priority = NotificationPriority.HIGH
            ),
            UserRole.CUSTOMER to RoleSpecificTemplate(
                titleTemplate = "تعيين السائق",
                messageTemplate = "تم تعيين السائق {driverName} لشحنتك {shipmentNumber}",
                enabled = true,
                sound = "default",
                vibration = true,
                priority = NotificationPriority.NORMAL
            )
        )
    )
    
    val TEMPERATURE_ALERT = NotificationTemplate(
        id = "temperature_alert",
        type = NotificationType.TEMPERATURE_ALERT,
        category = NotificationCategory.TEMPERATURE,
        titleTemplate = "تنبيه درجة حرارة",
        messageTemplate = "درجة حرارة الشحنة {shipmentNumber} خارج النطاق المسموح: {temperature}°C",
        variables = listOf("shipmentNumber", "temperature", "threshold"),
        defaultSound = "alert",
        defaultVibration = true,
        defaultPriority = NotificationPriority.HIGH,
        roleSpecific = mapOf(
            UserRole.ADMIN to RoleSpecificTemplate(
                titleTemplate = "تنبيه حراري حرج",
                messageTemplate = "الشحنة {shipmentNumber} تحتاج انتباه فوري: {temperature}°C (الحد: {threshold}°C)",
                enabled = true,
                sound = "emergency",
                vibration = true,
                priority = NotificationPriority.URGENT
            ),
            UserRole.DRIVER to RoleSpecificTemplate(
                titleTemplate = "تنبيه حراري",
                messageTemplate = "درجة حرارة الشحنة: {temperature}°C (الحد: {threshold}°C)",
                enabled = true,
                sound = "alert",
                vibration = true,
                priority = NotificationPriority.HIGH
            ),
            UserRole.CUSTOMER to RoleSpecificTemplate(
                titleTemplate = "تنبيه شحنتك",
                messageTemplate = "درجة حرارة شحنتك {shipmentNumber}: {temperature}°C",
                enabled = true,
                sound = "alert",
                vibration = true,
                priority = NotificationPriority.HIGH
            )
        )
    )
    
    val SHIPMENT_DELAYED = NotificationTemplate(
        id = "shipment_delayed",
        type = NotificationType.SHIPMENT_DELAYED,
        category = NotificationCategory.SHIPMENT,
        titleTemplate = "تأخير في الشحنة",
        messageTemplate = "الشحنة {shipmentNumber} متأخرة {delayHours} ساعة",
        variables = listOf("shipmentNumber", "delayHours", "reason"),
        defaultSound = "default",
        defaultVibration = true,
        defaultPriority = NotificationPriority.NORMAL,
        roleSpecific = mapOf(
            UserRole.CUSTOMER to RoleSpecificTemplate(
                titleTemplate = "تأخير شحنتك",
                messageTemplate = "شحنتك {shipmentNumber} متأخرة {delayHours} ساعة. السبب: {reason}",
                enabled = true,
                sound = "default",
                vibration = true,
                priority = NotificationPriority.NORMAL
            ),
            UserRole.ADMIN to RoleSpecificTemplate(
                titleTemplate = "تأخير في الشحنة",
                messageTemplate = "الشحنة {shipmentNumber} متأخرة {delayHours} ساعة",
                enabled = true,
                sound = "default",
                vibration = false,
                priority = NotificationPriority.NORMAL
            )
        )
    )
    
    val PAYMENT_RECEIVED = NotificationTemplate(
        id = "payment_received",
        type = NotificationType.PAYMENT_RECEIVED,
        category = NotificationCategory.PAYMENT,
        titleTemplate = "استلام دفعة",
        messageTemplate = "تم استلام دفعة {amount} للشحنة {shipmentNumber}",
        variables = listOf("amount", "shipmentNumber", "customerName"),
        defaultSound = "default",
        defaultVibration = true,
        defaultPriority = NotificationPriority.NORMAL,
        roleSpecific = mapOf(
            UserRole.ACCOUNTANT to RoleSpecificTemplate(
                titleTemplate = "دفعة جديدة",
                messageTemplate = "العميل {customerName} دفع {amount} للشحنة {shipmentNumber}",
                enabled = true,
                sound = "payment",
                vibration = true,
                priority = NotificationPriority.HIGH
            ),
            UserRole.CUSTOMER to RoleSpecificTemplate(
                titleTemplate = "تأكيد الدفع",
                messageTemplate = "تم استلام دفعتك {amount} بنجاح",
                enabled = true,
                sound = "default",
                vibration = true,
                priority = NotificationPriority.NORMAL
            )
        )
    )
    
    fun getTemplate(type: NotificationType): NotificationTemplate? {
        return when (type) {
            NotificationType.SHIPMENT_CREATED -> SHIPMENT_CREATED
            NotificationType.DRIVER_ASSIGNED -> SHIPMENT_ASSIGNED
            NotificationType.TEMPERATURE_ALERT -> TEMPERATURE_ALERT
            NotificationType.SHIPMENT_DELAYED -> SHIPMENT_DELAYED
            NotificationType.PAYMENT_RECEIVED -> PAYMENT_RECEIVED
            else -> null
        }
    }
}
