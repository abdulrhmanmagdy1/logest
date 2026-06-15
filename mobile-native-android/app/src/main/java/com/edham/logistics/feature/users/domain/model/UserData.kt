package com.edham.logistics.feature.users.domain.model

import java.util.*

/**
 * User data models for advanced user and role management system
 */

/**
 * User entity with comprehensive information
 */
data class User(
    val id: String,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String?,
    val role: UserRole,
    val status: UserStatus,
    val profile: UserProfile,
    val permissions: Set<Permission>,
    val createdAt: Long,
    val updatedAt: Long,
    val lastLoginAt: Long?,
    val createdBy: String?,
    val updatedBy: String?,
    val isActive: Boolean,
    val isEmailVerified: Boolean,
    val isPhoneVerified: Boolean,
    val twoFactorEnabled: Boolean,
    val loginAttempts: Int,
    val lockedUntil: Long?,
    val passwordChangedAt: Long,
    val sessionId: String?,
    val deviceId: String?,
    val ipAddress: String?,
    val userAgent: String?
)

/**
 * User roles with hierarchy
 */
enum class UserRole(val displayName: String, val level: Int) {
    CUSTOMER("العميل", 1),
    DRIVER("السائق", 2),
    ACCOUNTANT("المحاسب", 3),
    ADMIN("المشرف", 4);
    
    companion object {
        fun fromString(role: String): UserRole {
            return values().find { it.name.equals(role, ignoreCase = true) } ?: CUSTOMER
        }
    }
}

/**
 * User status
 */
enum class UserStatus(val displayName: String) {
    ACTIVE("نشط"),
    INACTIVE("غير نشط"),
    SUSPENDED("معلق"),
    LOCKED("مقفول"),
    PENDING("في الانتظار"),
    DISABLED("معطل")
}

/**
 * User profile information
 */
data class UserProfile(
    val avatar: String?,
    val bio: String?,
    val address: Address?,
    val dateOfBirth: Long?,
    val gender: Gender?,
    val nationality: String?,
    val language: String,
    val timezone: String,
    val preferences: UserPreferences,
    val documents: List<UserDocument>,
    val emergencyContacts: List<EmergencyContact>
)

/**
 * Address information
 */
data class Address(
    val street: String?,
    val city: String?,
    val state: String?,
    val postalCode: String?,
    val country: String?,
    val latitude: Double?,
    val longitude: Double?,
    val isPrimary: Boolean
)

/**
 * Gender enum
 */
enum class Gender {
    MALE,
    FEMALE,
    OTHER,
    PREFER_NOT_TO_SAY
}

/**
 * User preferences
 */
data class UserPreferences(
    val theme: Theme,
    val language: String,
    val notifications: NotificationPreferences,
    val privacy: PrivacyPreferences,
    val accessibility: AccessibilityPreferences
)

/**
 * Theme preferences
 */
enum class Theme {
    LIGHT,
    DARK,
    SYSTEM,
    AUTO
}

/**
 * Notification preferences
 */
data class NotificationPreferences(
    val emailNotifications: Boolean,
    val pushNotifications: Boolean,
    val smsNotifications: Boolean,
    val marketingEmails: Boolean,
    val securityAlerts: Boolean,
    val shipmentUpdates: Boolean,
    val billingNotifications: Boolean,
    val systemUpdates: Boolean
)

/**
 * Privacy preferences
 */
data class PrivacyPreferences(
    val profileVisibility: ProfileVisibility,
    val showOnlineStatus: Boolean,
    val showLastSeen: Boolean,
    val allowDirectMessages: Boolean,
    val dataSharing: Boolean,
    val analyticsTracking: Boolean
)

/**
 * Profile visibility
 */
enum class ProfileVisibility {
    PUBLIC,
    PRIVATE,
    FRIENDS_ONLY,
    ROLE_BASED
}

/**
 * Accessibility preferences
 */
data class AccessibilityPreferences(
    val fontSize: FontSize,
    val highContrast: Boolean,
    val reducedMotion: Boolean,
    val screenReader: Boolean,
    val largeButtons: Boolean
)

/**
 * Font size
 */
enum class FontSize {
    SMALL,
    MEDIUM,
    LARGE,
    EXTRA_LARGE
}

/**
 * User documents
 */
data class UserDocument(
    val id: String,
    val type: DocumentType,
    val name: String,
    val url: String,
    val uploadedAt: Long,
    val expiresAt: Long?,
    val isVerified: Boolean,
    val verifiedAt: Long?,
    val verifiedBy: String?,
    val size: Long,
    val mimeType: String
)

/**
 * Document types
 */
enum class DocumentType {
    ID_CARD,
    PASSPORT,
    DRIVING_LICENSE,
    PROFESSIONAL_LICENSE,
    INSURANCE,
    VEHICLE_REGISTRATION,
    BANK_STATEMENT,
    OTHER
}

/**
 * Emergency contact
 */
data class EmergencyContact(
    val id: String,
    val name: String,
    val relationship: String,
    val phoneNumber: String,
    val email: String?,
    val isPrimary: Boolean
)

/**
 * Permission system
 */
data class Permission(
    val id: String,
    val name: String,
    val description: String,
    val category: PermissionCategory,
    val isSystem: Boolean
)

/**
 * Permission categories
 */
enum class PermissionCategory {
    USER_MANAGEMENT,
    ROLE_MANAGEMENT,
    PERMISSION_MANAGEMENT,
    SHIPMENT_MANAGEMENT,
    BILLING_MANAGEMENT,
    ANALYTICS_VIEW,
    SYSTEM_CONFIG,
    LOGS_VIEW,
    NOTIFICATION_MANAGEMENT,
    REPORT_GENERATION
}

/**
 * User activity log
 */
data class UserActivityLog(
    val id: String,
    val userId: String,
    val action: ActivityAction,
    val resource: String?,
    val resourceId: String?,
    val details: Map<String, Any>,
    val ipAddress: String?,
    val userAgent: String?,
    val deviceId: String?,
    val location: String?,
    val timestamp: Long,
    val sessionId: String?,
    val success: Boolean,
    val errorMessage: String?
)

/**
 * Activity actions
 */
enum class ActivityAction(val displayName: String) {
    LOGIN("تسجيل الدخول"),
    LOGOUT("تسجيل الخروج"),
    CREATE("إنشاء"),
    READ("قراءة"),
    UPDATE("تحديث"),
    DELETE("حذف"),
    ENABLE("تفعيل"),
    DISABLE("تعطيل"),
    SUSPEND("تعليق"),
    UNSUSPEND("إلغاء التعليق"),
    LOCK("قفل"),
    UNLOCK("إلغاء القفل"),
    PASSWORD_CHANGE("تغيير كلمة المرور"),
    PASSWORD_RESET("إعادة تعيين كلمة المرور"),
    EMAIL_VERIFY("التحقق من البريد الإلكتروني"),
    PHONE_VERIFY("التحقق من الهاتف"),
    TWO_FACTOR_ENABLE("تفعيل المصادقة الثنائية"),
    TWO_FACTOR_DISABLE("تعطيل المصادقة الثنائية"),
    ROLE_CHANGE("تغيير الدور"),
    PERMISSION_GRANT("منح الصلاحية"),
    PERMISSION_REVOKE("سحب الصلاحية"),
    PROFILE_UPDATE("تحديث الملف الشخصي"),
    DOCUMENT_UPLOAD("رفع مستند"),
    DOCUMENT_VERIFY("التحقق من المستند"),
    SESSION_START("بدء الجلسة"),
    SESSION_END("انتهاء الجلسة"),
    SECURITY_ALERT("تنبيه أمني"),
    SYSTEM_ACCESS("الوصول للنظام"),
    DATA_EXPORT("تصدير البيانات"),
    DATA_IMPORT("استيراد البيانات"),
    BACKUP("نسخ احتياطي"),
    RESTORE("استعادة"),
    CONFIG_CHANGE("تغيير الإعدادات"),
    LOG_VIEW("عرض السجلات"),
    REPORT_GENERATE("إنشاء تقرير"),
    API_CALL("استدعاء API"),
    FAILED_LOGIN("فشل تسجيل الدخول"),
    SUSPICIOUS_ACTIVITY("نشاط مشبوه"),
    SECURITY_VIOLATION("انتهاك أمني")
}

/**
 * Login history
 */
data class LoginHistory(
    val id: String,
    val userId: String,
    val loginTime: Long,
    val logoutTime: Long?,
    val ipAddress: String,
    val userAgent: String?,
    val deviceId: String?,
    val location: String?,
    val loginMethod: LoginMethod,
    val success: Boolean,
    val failureReason: String?,
    val sessionId: String,
    val duration: Long?,
    val isNewDevice: Boolean,
    val isNewLocation: Boolean,
    val riskScore: Float,
    val securityFlags: Set<SecurityFlag>
)

/**
 * Login methods
 */
enum class LoginMethod {
    PASSWORD,
    BIOMETRIC,
    TWO_FACTOR,
    SSO,
    SOCIAL_LOGIN,
    MAGIC_LINK,
    QR_CODE
}

/**
 * Security flags
 */
enum class SecurityFlag {
    NEW_DEVICE,
    NEW_LOCATION,
    SUSPICIOUS_IP,
    UNUSUAL_TIME,
    MULTIPLE_ATTEMPTS,
    ROOTED_DEVICE,
    VPN_DETECTED,
    TOR_DETECTED,
    OUTDATED_APP,
    WEAK_PASSWORD
}

/**
 * User session information
 */
data class UserSession(
    val id: String,
    val userId: String,
    val deviceId: String,
    val ipAddress: String,
    val userAgent: String?,
    val location: String?,
    val startTime: Long,
    val lastActivity: Long,
    val expiryTime: Long,
    val isActive: Boolean,
    val loginMethod: LoginMethod,
    val securityFlags: Set<SecurityFlag>,
    val permissions: Set<Permission>
)

/**
 * User statistics
 */
data class UserStatistics(
    val totalUsers: Int,
    val activeUsers: Int,
    val inactiveUsers: Int,
    val suspendedUsers: Int,
    val lockedUsers: Int,
    val usersByRole: Map<UserRole, Int>,
    val usersByStatus: Map<UserStatus, Int>,
    val recentLogins: Int,
    val failedLogins: Int,
    val newUsersToday: Int,
    val newUsersThisWeek: Int,
    val newUsersThisMonth: Int,
    val averageSessionDuration: Long,
    val topActiveUsers: List<UserActivitySummary>
)

/**
 * User activity summary
 */
data class UserActivitySummary(
    val userId: String,
    val username: String,
    val role: UserRole,
    val activityCount: Int,
    val lastActivity: Long,
    val totalSessionTime: Long,
    val loginCount: Int,
    val riskScore: Float
)

/**
 * User creation request
 */
data class CreateUserRequest(
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String?,
    val role: UserRole,
    val password: String,
    val profile: UserProfile?,
    val permissions: Set<Permission>?,
    val isActive: Boolean = true,
    val sendWelcomeEmail: Boolean = true,
    val requireEmailVerification: Boolean = true,
    val requirePhoneVerification: Boolean = false,
    val twoFactorEnabled: Boolean = false
)

/**
 * User update request
 */
data class UpdateUserRequest(
    val id: String,
    val username: String?,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String?,
    val role: UserRole?,
    val status: UserStatus?,
    val profile: UserProfile?,
    val permissions: Set<Permission>?,
    val isActive: Boolean?,
    val isEmailVerified: Boolean?,
    val isPhoneVerified: Boolean?,
    val twoFactorEnabled: Boolean?
)

/**
 * User search filters
 */
data class UserSearchFilters(
    val query: String?,
    val role: UserRole?,
    val status: UserStatus?,
    val isActive: Boolean?,
    val isEmailVerified: Boolean?,
    val isPhoneVerified: Boolean?,
    val twoFactorEnabled: Boolean?,
    val createdAfter: Long?,
    val createdBefore: Long?,
    val lastLoginAfter: Long?,
    val lastLoginBefore: Long?,
    val sortBy: UserSortField?,
    val sortOrder: SortOrder?,
    val page: Int = 1,
    val limit: Int = 20
)

/**
 * User sort fields
 */
enum class UserSortField {
    USERNAME,
    EMAIL,
    FIRST_NAME,
    LAST_NAME,
    ROLE,
    STATUS,
    CREATED_AT,
    UPDATED_AT,
    LAST_LOGIN_AT,
    LOGIN_COUNT
}

/**
 * Sort order
 */
enum class SortOrder {
    ASC,
    DESC
}

/**
 * User search response
 */
data class UserSearchResponse(
    val users: List<User>,
    val totalCount: Int,
    val currentPage: Int,
    val totalPages: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean
)

/**
 * Bulk user operations
 */
data class BulkUserOperation(
    val operation: BulkOperationType,
    val userIds: List<String>,
    val parameters: Map<String, Any>?
)

/**
 * Bulk operation types
 */
enum class BulkOperationType {
    ENABLE,
    DISABLE,
    SUSPEND,
    UNSUSPEND,
    LOCK,
    UNLOCK,
    DELETE,
    ROLE_CHANGE,
    PERMISSION_GRANT,
    PERMISSION_REVOKE,
    EXPORT,
    PASSWORD_RESET,
    EMAIL_VERIFY,
    PHONE_VERIFY
}

/**
 * Bulk operation result
 */
data class BulkOperationResult(
    val operation: BulkOperationType,
    val totalUsers: Int,
    val successfulUsers: Int,
    val failedUsers: Int,
    val results: List<UserOperationResult>,
    val errors: List<String>
)

/**
 * Individual user operation result
 */
data class UserOperationResult(
    val userId: String,
    val success: Boolean,
    val error: String?
)

/**
 * Role management
 */
data class Role(
    val id: String,
    val name: String,
    val displayName: String,
    val description: String,
    val level: Int,
    val permissions: Set<Permission>,
    val isActive: Boolean,
    val isSystem: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    val createdBy: String?,
    val updatedBy: String?,
    val userCount: Int
)

/**
 * Permission management
 */
data class PermissionGroup(
    val id: String,
    val name: String,
    val description: String,
    val category: PermissionCategory,
    val permissions: List<Permission>,
    val isActive: Boolean
)

/**
 * User audit log
 */
data class UserAuditLog(
    val id: String,
    val userId: String,
    val targetUserId: String?,
    val action: AuditAction,
    val oldValue: Any?,
    val newValue: Any?,
    val reason: String?,
    val performedBy: String,
    val performedAt: Long,
    val ipAddress: String?,
    val userAgent: String?,
    val sessionId: String?
)

/**
 * Audit actions
 */
enum class AuditAction {
    USER_CREATED,
    USER_UPDATED,
    USER_DELETED,
    USER_ENABLED,
    USER_DISABLED,
    USER_SUSPENDED,
    USER_UNSUSPENDED,
    USER_LOCKED,
    USER_UNLOCKED,
    ROLE_CHANGED,
    PERMISSIONS_GRANTED,
    PERMISSIONS_REVOKED,
    PASSWORD_CHANGED,
    PASSWORD_RESET,
    EMAIL_VERIFIED,
    PHONE_VERIFIED,
    TWO_FACTOR_ENABLED,
    TWO_FACTOR_DISABLED,
    PROFILE_UPDATED,
    DOCUMENT_UPLOADED,
    DOCUMENT_VERIFIED,
    DOCUMENT_REJECTED,
    SESSION_TERMINATED,
    SECURITY_ALERT_TRIGGERED
}
