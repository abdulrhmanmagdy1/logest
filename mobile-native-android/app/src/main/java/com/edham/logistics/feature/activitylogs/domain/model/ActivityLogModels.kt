package com.edham.logistics.feature.activitylogs.domain.model

import java.util.*

/**
 * Activity logging system models for comprehensive tracking
 */

/**
 * Main activity log entity
 */
data class ActivityLog(
    val id: String,
    val userId: String,
    val userRole: UserRole,
    val userName: String,
    val userEmail: String,
    val action: ActivityAction,
    val actionType: ActionType,
    val entityType: EntityType,
    val entityId: String?,
    val entityName: String?,
    val description: String,
    val details: Map<String, Any>,
    val ipAddress: String?,
    val userAgent: String?,
    val deviceId: String?,
    val location: String?,
    val sessionId: String?,
    val timestamp: Long,
    val success: Boolean,
    val errorMessage: String?,
    val severity: LogSeverity,
    val category: LogCategory,
    val source: LogSource,
    val metadata: ActivityLogMetadata
)

/**
 * Activity action types
 */
enum class ActivityAction(val displayName: String, val category: LogCategory) {
    // Authentication Actions
    LOGIN("تسجيل الدخول", LogCategory.AUTHENTICATION),
    LOGOUT("تسجيل الخروج", LogCategory.AUTHENTICATION),
    FAILED_LOGIN("فشل تسجيل الدخول", LogCategory.AUTHENTICATION),
    PASSWORD_CHANGE("تغيير كلمة المرور", LogCategory.AUTHENTICATION),
    PASSWORD_RESET("إعادة تعيين كلمة المرور", LogCategory.AUTHENTICATION),
    TWO_FACTOR_ENABLE("تفعيل المصادقة الثنائية", LogCategory.AUTHENTICATION),
    TWO_FACTOR_DISABLE("تعطيل المصادقة الثنائية", LogCategory.AUTHENTICATION),
    
    // User Management Actions
    USER_CREATE("إنشاء مستخدم", LogCategory.USER_MANAGEMENT),
    USER_UPDATE("تحديث مستخدم", LogCategory.USER_MANAGEMENT),
    USER_DELETE("حذف مستخدم", LogCategory.USER_MANAGEMENT),
    USER_ENABLE("تفعيل مستخدم", LogCategory.USER_MANAGEMENT),
    USER_DISABLE("تعطيل مستخدم", LogCategory.USER_MANAGEMENT),
    USER_SUSPEND("تعليق مستخدم", LogCategory.USER_MANAGEMENT),
    USER_UNSUSPEND("إلغاء تعليق مستخدم", LogCategory.USER_MANAGEMENT),
    USER_LOCK("قفل مستخدم", LogCategory.USER_MANAGEMENT),
    USER_UNLOCK("إلغاء قفل مستخدم", LogCategory.USER_MANAGEMENT),
    ROLE_CHANGE("تغيير دور المستخدم", LogCategory.USER_MANAGEMENT),
    
    // Shipment Actions
    SHIPMENT_CREATE("إنشاء شحنة", LogCategory.SHIPMENT),
    SHIPMENT_UPDATE("تحديث شحنة", LogCategory.SHIPMENT),
    SHIPMENT_DELETE("حذف شحنة", LogCategory.SHIPMENT),
    SHIPMENT_CANCEL("إلغاء شحنة", LogCategory.SHIPMENT),
    SHIPMENT_PICKUP("استلام شحنة", LogCategory.SHIPMENT),
    SHIPMENT_DELIVER("تسليم شحنة", LogCategory.SHIPMENT),
    SHIPMENT_RETURN("إرجاع شحنة", LogCategory.SHIPMENT),
    SHIPMENT_STATUS_UPDATE("تحديث حالة الشحنة", LogCategory.SHIPMENT),
    SHIPMENT_ROUTE_UPDATE("تحديث مسار الشحنة", LogCategory.SHIPMENT),
    SHIPMENT_LOCATION_UPDATE("تحديث موقع الشحنة", LogCategory.SHIPMENT),
    
    // Billing Actions
    INVOICE_CREATE("إنشاء فاتورة", LogCategory.BILLING),
    INVOICE_UPDATE("تحديث فاتورة", LogCategory.BILLING),
    INVOICE_DELETE("حذف فاتورة", LogCategory.BILLING),
    INVOICE_SEND("إرسال فاتورة", LogCategory.BILLING),
    INVOICE_PAID("دفع فاتورة", LogCategory.BILLING),
    INVOICE_CANCEL("إلغاء فاتورة", LogCategory.BILLING),
    PAYMENT_PROCESS("معالجة دفعة", LogCategory.BILLING),
    PAYMENT_REFUND("استرداد دفعة", LogCategory.BILLING),
    PAYMENT_FAILED("فشل الدفعة", LogCategory.BILLING),
    
    // System Actions
    SYSTEM_CONFIG_UPDATE("تحديث إعدادات النظام", LogCategory.SYSTEM),
    SYSTEM_BACKUP("نسخ احتياطي للنظام", LogCategory.SYSTEM),
    SYSTEM_RESTORE("استعادة النظام", LogCategory.SYSTEM),
    SYSTEM_MAINTENANCE("صيانة النظام", LogCategory.SYSTEM),
    SYSTEM_UPGRADE("ترقية النظام", LogCategory.SYSTEM),
    
    // Security Actions
    SECURITY_ALERT("تنبيه أمني", LogCategory.SECURITY),
    SECURITY_VIOLATION("انتهاك أمني", LogCategory.SECURITY),
    SUSPICIOUS_ACTIVITY("نشاط مشبوه", LogCategory.SECURITY),
    UNAUTHORIZED_ACCESS("وصول غير مصرح", LogCategory.SECURITY),
    DATA_BREACH("اختراق البيانات", LogCategory.SECURITY),
    
    // Data Actions
    DATA_EXPORT("تصدير البيانات", LogCategory.DATA),
    DATA_IMPORT("استيراد البيانات", LogCategory.DATA),
    DATA_DELETE("حذف البيانات", LogCategory.DATA),
    DATA_BACKUP("نسخ احتياطي للبيانات", LogCategory.DATA),
    DATA_RESTORE("استعادة البيانات", LogCategory.DATA),
    
    // Notification Actions
    NOTIFICATION_SEND("إرسال إشعار", LogCategory.NOTIFICATION),
    NOTIFICATION_READ("قراءة إشعار", LogCategory.NOTIFICATION),
    NOTIFICATION_DELETE("حذف إشعار", LogCategory.NOTIFICATION),
    
    // Report Actions
    REPORT_GENERATE("إنشاء تقرير", LogCategory.REPORT),
    REPORT_VIEW("عرض تقرير", LogCategory.REPORT),
    REPORT_EXPORT("تصدير تقرير", LogCategory.REPORT),
    REPORT_DELETE("حذف تقرير", LogCategory.REPORT),
    
    // API Actions
    API_CALL("استدعاء API", LogCategory.API),
    API_ERROR("خطأ في API", LogCategory.API),
    API_RATE_LIMIT("تجاوز حد المعدل", LogCategory.API),
    
    // Other Actions
    FILE_UPLOAD("رفع ملف", LogCategory.OTHER),
    FILE_DOWNLOAD("تنزيل ملف", LogCategory.OTHER),
    SEARCH("بحث", LogCategory.OTHER),
    FILTER("فلترة", LogCategory.OTHER),
    SORT("ترتيب", LogCategory.OTHER),
    VIEW("عرض", LogCategory.OTHER),
    EDIT("تعديل", LogCategory.OTHER),
    CREATE("إنشاء", LogCategory.OTHER),
    DELETE("حذف", LogCategory.OTHER),
    UPDATE("تحديث", LogCategory.OTHER),
    READ("قراءة", LogCategory.OTHER)
}

/**
 * Action types for categorization
 */
enum class ActionType {
    CREATE,
    READ,
    UPDATE,
    DELETE,
    AUTHENTICATION,
    AUTHORIZATION,
    SYSTEM,
    SECURITY,
    DATA,
    NOTIFICATION,
    REPORT,
    API,
    OTHER
}

/**
 * Entity types
 */
enum class EntityType {
    USER,
    SHIPMENT,
    INVOICE,
    PAYMENT,
    VEHICLE,
    DRIVER,
    CUSTOMER,
    ADMIN,
    ACCOUNTANT,
    SYSTEM,
    CONFIGURATION,
    REPORT,
    FILE,
    NOTIFICATION,
    SESSION,
    DEVICE,
    LOCATION,
    ROLE,
    PERMISSION,
    DOCUMENT,
    AUDIT_LOG,
    ACTIVITY_LOG,
    LOGIN_HISTORY,
    BILLING_RECORD,
    ANALYTICS_DATA,
    BACKUP,
    OTHER
}

/**
 * Log severity levels
 */
enum class LogSeverity(val level: Int, val displayName: String) {
    DEBUG(0, "تصحيح"),
    INFO(1, "معلومات"),
    WARNING(2, "تحذير"),
    ERROR(3, "خطأ"),
    CRITICAL(4, "حرج"),
    FATAL(5, "قاتل")
}

/**
 * Log categories
 */
enum class LogCategory(val displayName: String) {
    AUTHENTICATION("المصادقة"),
    USER_MANAGEMENT("إدارة المستخدمين"),
    SHIPMENT("الشحنات"),
    BILLING("الفواتير"),
    SYSTEM("النظام"),
    SECURITY("الأمان"),
    DATA("البيانات"),
    NOTIFICATION("الإشعارات"),
    REPORT("التقارير"),
    API("واجهة برمجة التطبيقات"),
    OTHER("أخرى")
}

/**
 * Log sources
 */
enum class LogSource(val displayName: String) {
    WEB("الويب"),
    MOBILE("الجوال"),
    API("واجهة برمجة التطبيقات"),
    SYSTEM("النظام"),
    ADMIN("لوحة التحكم"),
    BACKGROUND("الخلفية"),
    SCHEDULED("مجدول"),
    MANUAL("يدوي"),
    AUTOMATED("آلي"),
    THIRD_PARTY("جهة خارجية")
}

/**
 * Activity log metadata
 */
data class ActivityLogMetadata(
    val requestId: String?,
    val correlationId: String?,
    val traceId: String?,
    val duration: Long?,
    val memoryUsage: Long?,
    val cpuUsage: Float?,
    val networkLatency: Long?,
    val databaseQueryTime: Long?,
    val cacheHitRate: Float?,
    val errorCode: String?,
    val errorStack: String?,
    val userAgentParsed: UserAgentInfo?,
    val geoLocation: GeoLocation?,
    val deviceInfo: DeviceInfo?,
    val sessionInfo: SessionInfo?,
    val performanceMetrics: PerformanceMetrics?
)

/**
 * User agent information
 */
data class UserAgentInfo(
    val browser: String?,
    val browserVersion: String?,
    val os: String?,
    val osVersion: String?,
    val device: String?,
    val isMobile: Boolean,
    val isTablet: Boolean,
    val isDesktop: Boolean
)

/**
 * Geographic location
 */
data class GeoLocation(
    val country: String?,
    val region: String?,
    val city: String?,
    val latitude: Double?,
    val longitude: Double?,
    val accuracy: Float?,
    val timezone: String?
)

/**
 * Device information
 */
data class DeviceInfo(
    val deviceId: String?,
    val deviceType: String?,
    val deviceModel: String?,
    val deviceBrand: String?,
    val osVersion: String?,
    val appVersion: String?,
    val screenSize: String?,
    val isRooted: Boolean?,
    val isEmulator: Boolean?
)

/**
 * Session information
 */
data class SessionInfo(
    val sessionId: String?,
    val sessionStartTime: Long?,
    val sessionDuration: Long?,
    val sessionActivityCount: Int?,
    val lastActivityTime: Long?,
    val isNewSession: Boolean?
)

/**
 * Performance metrics
 */
data class PerformanceMetrics(
    val responseTime: Long?,
    val throughput: Float?,
    val errorRate: Float?,
    val availability: Float?,
    val resourceUtilization: Map<String, Float>?
)

/**
 * Activity log search filters
 */
data class ActivityLogFilters(
    val userId: String? = null,
    val userRole: UserRole? = null,
    val action: ActivityAction? = null,
    val actionType: ActionType? = null,
    val entityType: EntityType? = null,
    val entityId: String? = null,
    val category: LogCategory? = null,
    val severity: LogSeverity? = null,
    val source: LogSource? = null,
    val success: Boolean? = null,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val ipAddress: String? = null,
    val deviceId: String? = null,
    val sessionId: String? = null,
    val location: String? = null,
    val searchQuery: String? = null,
    val sortBy: ActivityLogSortField? = null,
    val sortOrder: SortOrder? = null,
    val page: Int = 1,
    val limit: Int = 50
)

/**
 * Activity log sort fields
 */
enum class ActivityLogSortField {
    TIMESTAMP,
    USER_NAME,
    ACTION,
    ENTITY_TYPE,
    SEVERITY,
    CATEGORY,
    SOURCE,
    SUCCESS,
    DURATION
}

/**
 * Sort order
 */
enum class SortOrder {
    ASC,
    DESC
}

/**
 * Activity log statistics
 */
data class ActivityLogStatistics(
    val totalLogs: Long,
    val logsByCategory: Map<LogCategory, Long>,
    val logsBySeverity: Map<LogSeverity, Long>,
    val logsByAction: Map<ActivityAction, Long>,
    val logsByEntityType: Map<EntityType, Long>,
    val logsByUser: Map<String, Long>,
    val logsBySource: Map<LogSource, Long>,
    val successRate: Float,
    val errorRate: Float,
    val averageDuration: Double,
    val topUsers: List<UserActivitySummary>,
    val topActions: List<ActionActivitySummary>,
    val timeSeriesData: List<ActivityLogTimeSeries>,
    val generatedAt: Long
)

/**
 * User activity summary
 */
data class UserActivitySummary(
    val userId: String,
    val userName: String,
    val userRole: UserRole,
    val activityCount: Long,
    val successCount: Long,
    val errorCount: Long,
    val averageDuration: Double,
    val lastActivity: Long,
    val topActions: List<ActivityAction>
)

/**
 * Action activity summary
 */
data class ActionActivitySummary(
    val action: ActivityAction,
    val count: Long,
    val successCount: Long,
    val errorCount: Long,
    val averageDuration: Double,
    val topUsers: List<String>
)

/**
 * Activity log time series data
 */
data class ActivityLogTimeSeries(
    val timestamp: Long,
    val count: Long,
    val successCount: Long,
    val errorCount: Long,
    val averageDuration: Double
)

/**
 * Activity log export options
 */
data class ActivityLogExportOptions(
    val format: ExportFormat,
    val filters: ActivityLogFilters,
    val includeMetadata: Boolean = false,
    val includeDetails: Boolean = true,
    val compress: Boolean = false,
    val password: String? = null
)

/**
 * Export formats
 */
enum class ExportFormat {
    CSV,
    JSON,
    XML,
    PDF,
    EXCEL,
    PARQUET
}

/**
 * Activity log export result
 */
data class ActivityLogExportResult(
    val fileId: String,
    val fileName: String,
    val fileSize: Long,
    val recordCount: Long,
    val exportFormat: ExportFormat,
    val downloadUrl: String,
    val expiresAt: Long,
    val createdAt: Long
)

/**
 * Activity log retention policy
 */
data class ActivityLogRetentionPolicy(
    val category: LogCategory,
    val severity: LogSeverity,
    val retentionDays: Int,
    val archiveAfterDays: Int,
    val deleteAfterDays: Int,
    val isActive: Boolean
)

/**
 * Activity log alert rule
 */
data class ActivityLogAlertRule(
    val id: String,
    val name: String,
    val description: String,
    val category: LogCategory?,
    val severity: LogSeverity?,
    val action: ActivityAction?,
    val entityType: EntityType?,
    val condition: AlertCondition,
    val threshold: Int,
    val timeWindow: Int,
    val isActive: Boolean,
    val notificationChannels: List<NotificationChannel>,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Alert conditions
 */
enum class AlertCondition {
    COUNT_GREATER_THAN,
    COUNT_LESS_THAN,
    COUNT_EQUALS,
    RATE_GREATER_THAN,
    RATE_LESS_THAN,
    ERROR_RATE_GREATER_THAN,
    RESPONSE_TIME_GREATER_THAN,
    SPECIFIC_ACTION,
    SPECIFIC_ERROR
}

/**
 * Notification channels
 */
enum class NotificationChannel {
    EMAIL,
    SMS,
    PUSH,
    WEBHOOK,
    SLACK,
    TELEGRAM,
    DISCORD
}

/**
 * Activity log alert
 */
data class ActivityLogAlert(
    val id: String,
    val ruleId: String,
    val ruleName: String,
    val triggeredAt: Long,
    val condition: AlertCondition,
    val threshold: Int,
    val actualValue: Int,
    val timeWindow: Int,
    val matchingLogs: List<ActivityLog>,
    val notificationsSent: List<NotificationResult>
)

/**
 * Notification result
 */
data class NotificationResult(
    val channel: NotificationChannel,
    val recipient: String,
    val sentAt: Long,
    val success: Boolean,
    val errorMessage: String?
)

/**
 * Activity log dashboard data
 */
data class ActivityLogDashboardData(
    val statistics: ActivityLogStatistics,
    val recentLogs: List<ActivityLog>,
    val criticalLogs: List<ActivityLog>,
    val errorLogs: List<ActivityLog>,
    val alerts: List<ActivityLogAlert>,
    val trends: List<ActivityLogTrend>,
    val topEntities: List<EntityActivitySummary>,
    val systemHealth: SystemHealthMetrics
)

/**
 * Activity log trend
 */
data class ActivityLogTrend(
    val period: String,
    val timestamp: Long,
    val totalCount: Long,
    val successCount: Long,
    val errorCount: Long,
    val averageDuration: Double,
    val uniqueUsers: Long
)

/**
 * Entity activity summary
 */
data class EntityActivitySummary(
    val entityType: EntityType,
    val entityId: String,
    val entityName: String,
    val activityCount: Long,
    val lastActivity: Long,
    val topActions: List<ActivityAction>
)

/**
 * System health metrics
 */
data class SystemHealthMetrics(
    val overallHealth: HealthStatus,
    val logProcessingRate: Float,
    val averageLogAge: Long,
    val storageUsage: StorageUsage,
    val performanceMetrics: SystemPerformanceMetrics,
    val errorRate: Float,
    val alertCount: Int
)

/**
 * Health status
 */
enum class HealthStatus {
    HEALTHY,
    WARNING,
    CRITICAL,
    DOWN
}

/**
 * Storage usage
 */
data class StorageUsage(
    val totalSpace: Long,
    val usedSpace: Long,
    val availableSpace: Long,
    val usagePercentage: Float
)

/**
 * System performance metrics
 */
data class SystemPerformanceMetrics(
    val cpuUsage: Float,
    val memoryUsage: Float,
    val diskUsage: Float,
    val networkLatency: Long,
    val databaseResponseTime: Long
)

/**
 * Activity log batch operation
 */
data class ActivityLogBatchOperation(
    val operation: BatchOperationType,
    val filters: ActivityLogFilters,
    val parameters: Map<String, Any>?
)

/**
 * Batch operation types
 */
enum class BatchOperationType {
    DELETE,
    ARCHIVE,
    EXPORT,
    UPDATE_SEVERITY,
    UPDATE_CATEGORY,
    ASSIGN_TAGS,
    MARK_AS_REVIEWED
}

/**
 * Activity log batch operation result
 */
data class ActivityLogBatchOperationResult(
    val operation: BatchOperationType,
    val totalRecords: Long,
    val processedRecords: Long,
    val successfulRecords: Long,
    val failedRecords: Long,
    val errors: List<String>,
    val startTime: Long,
    val endTime: Long,
    val duration: Long
)

/**
 * Activity log tag
 */
data class ActivityLogTag(
    val id: String,
    val name: String,
    val description: String,
    val color: String,
    val isActive: Boolean,
    val createdAt: Long,
    val usageCount: Long
)

/**
 * Activity log with tags
 */
data class ActivityLogWithTags(
    val log: ActivityLog,
    val tags: List<ActivityLogTag>
)

/**
 * User role enum
 */
enum class UserRole {
    CUSTOMER,
    DRIVER,
    ACCOUNTANT,
    ADMIN
}
