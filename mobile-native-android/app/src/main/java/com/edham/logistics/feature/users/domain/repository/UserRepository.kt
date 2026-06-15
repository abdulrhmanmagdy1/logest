package com.edham.logistics.feature.users.domain.repository

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.users.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user management operations
 */
interface UserRepository {
    
    // Basic User Operations
    suspend fun createUser(request: CreateUserRequest): Result<User>
    suspend fun updateUser(request: UpdateUserRequest): Result<User>
    suspend fun getUserById(userId: String): Result<User>
    suspend fun getUserByUsername(username: String): Result<User>
    suspend fun getUserByEmail(email: String): Result<User>
    suspend fun deleteUser(userId: String, reason: String): Result<Unit>
    
    // User Status Management
    suspend fun enableUser(userId: String, reason: String): Result<User>
    suspend fun disableUser(userId: String, reason: String): Result<User>
    suspend fun suspendUser(userId: String, reason: String): Result<User>
    suspend fun unsuspendUser(userId: String, reason: String): Result<User>
    suspend fun lockUser(userId: String, reason: String): Result<User>
    suspend fun unlockUser(userId: String, reason: String): Result<User>
    
    // Role Management
    suspend fun changeUserRole(userId: String, newRole: UserRole, reason: String): Result<User>
    suspend fun getUserPermissions(userId: String): Result<Set<Permission>>
    suspend fun grantUserPermissions(userId: String, permissions: Set<Permission>, reason: String): Result<User>
    suspend fun revokeUserPermissions(userId: String, permissions: Set<Permission>, reason: String): Result<User>
    
    // Search and Filtering
    suspend fun searchUsers(filters: UserSearchFilters): Result<UserSearchResponse>
    suspend fun getUsersByRole(role: UserRole): Result<List<User>>
    suspend fun getUsersByStatus(status: UserStatus): Result<List<User>>
    suspend fun getActiveUsers(): Result<List<User>>
    suspend fun getInactiveUsers(): Result<List<User>>
    
    // Statistics and Analytics
    suspend fun getUserStatistics(): Result<UserStatistics>
    suspend fun getUserActivitySummary(userId: String): Result<UserActivitySummary>
    suspend fun getRoleStatistics(): Result<Map<UserRole, Int>>
    suspend fun getStatusStatistics(): Result<Map<UserStatus, Int>>
    
    // Activity Logging
    suspend fun logUserActivity(activityLog: UserActivityLog): Result<Unit>
    suspend fun getUserActivityLogs(userId: String, limit: Int, offset: Int): Result<List<UserActivityLog>>
    suspend fun getAllUserActivityLogs(limit: Int, offset: Int): Result<List<UserActivityLog>>
    suspend fun searchUserActivityLogs(filters: ActivityLogFilters): Result<List<UserActivityLog>>
    fun observeUserActivity(userId: String): Flow<List<UserActivityLog>>
    fun observeAllUserActivity(): Flow<List<UserActivityLog>>
    
    // Login History
    suspend fun logLogin(loginHistory: LoginHistory): Result<Unit>
    suspend fun getLoginHistory(userId: String, limit: Int, offset: Int): Result<List<LoginHistory>>
    suspend fun getAllLoginHistory(limit: Int, offset: Int): Result<List<LoginHistory>>
    suspend fun searchLoginHistory(filters: LoginHistoryFilters): Result<List<LoginHistory>>
    fun observeLoginHistory(userId: String): Flow<List<LoginHistory>>
    fun observeAllLoginHistory(): Flow<List<LoginHistory>>
    
    // Session Management
    suspend fun getUserSessions(userId: String): Result<List<UserSession>>
    suspend fun terminateUserSession(userId: String, sessionId: String): Result<Unit>
    suspend fun terminateAllUserSessions(userId: String): Result<Unit>
    suspend fun getActiveSessions(): Result<List<UserSession>>
    
    // Password Management
    suspend fun resetUserPassword(userId: String, reason: String): Result<Unit>
    suspend fun changeUserPassword(userId: String, oldPassword: String, newPassword: String): Result<Unit>
    suspend fun verifyUserPassword(userId: String, password: String): Result<Boolean>
    
    // Email and Phone Verification
    suspend fun verifyUserEmail(userId: String): Result<User>
    suspend fun verifyUserPhone(userId: String): Result<User>
    suspend fun sendEmailVerification(userId: String): Result<Unit>
    suspend fun sendPhoneVerification(userId: String): Result<Unit>
    
    // Two-Factor Authentication
    suspend fun enableTwoFactor(userId: String): Result<User>
    suspend fun disableTwoFactor(userId: String): Result<User>
    suspend fun generateTwoFactorSecret(userId: String): Result<String>
    suspend fun verifyTwoFactorCode(userId: String, code: String): Result<Boolean>
    
    // Document Management
    suspend fun uploadUserDocument(userId: String, document: UserDocument): Result<UserDocument>
    suspend fun verifyUserDocument(userId: String, documentId: String): Result<UserDocument>
    suspend fun rejectUserDocument(userId: String, documentId: String, reason: String): Result<UserDocument>
    suspend fun getUserDocuments(userId: String): Result<List<UserDocument>>
    suspend fun deleteUserDocument(userId: String, documentId: String): Result<Unit>
    
    // Profile Management
    suspend fun updateUserProfile(userId: String, profile: UserProfile): Result<User>
    suspend fun updateUserPreferences(userId: String, preferences: UserPreferences): Result<User>
    suspend fun updateUserAvatar(userId: String, avatarUrl: String): Result<User>
    
    // Emergency Contacts
    suspend fun addEmergencyContact(userId: String, contact: EmergencyContact): Result<EmergencyContact>
    suspend fun updateEmergencyContact(userId: String, contactId: String, contact: EmergencyContact): Result<EmergencyContact>
    suspend fun deleteEmergencyContact(userId: String, contactId: String): Result<Unit>
    suspend fun getEmergencyContacts(userId: String): Result<List<EmergencyContact>>
    
    // Audit Logging
    suspend fun logAuditEvent(auditLog: UserAuditLog): Result<Unit>
    suspend fun getAuditLogs(userId: String, limit: Int, offset: Int): Result<List<UserAuditLog>>
    suspend fun getAllAuditLogs(limit: Int, offset: Int): Result<List<UserAuditLog>>
    suspend fun searchAuditLogs(filters: AuditLogFilters): Result<List<UserAuditLog>>
    
    // Bulk Operations
    suspend fun bulkCreateUsers(requests: List<CreateUserRequest>): Result<BulkOperationResult>
    suspend fun bulkUpdateUsers(requests: List<UpdateUserRequest>): Result<BulkOperationResult>
    suspend fun bulkDeleteUsers(userIds: List<String>, reason: String): Result<BulkOperationResult>
    suspend fun bulkEnableUsers(userIds: List<String>, reason: String): Result<BulkOperationResult>
    suspend fun bulkDisableUsers(userIds: List<String>, reason: String): Result<BulkOperationResult>
    suspend fun bulkChangeRole(userIds: List<String>, newRole: UserRole, reason: String): Result<BulkOperationResult>
    suspend fun bulkExportUsers(filters: UserSearchFilters): Result<String> // Returns export file path
    
    // Role and Permission Management
    suspend fun getAllRoles(): Result<List<Role>>
    suspend fun getRoleById(roleId: String): Result<Role>
    suspend fun createRole(role: Role): Result<Role>
    suspend fun updateRole(role: Role): Result<Role>
    suspend fun deleteRole(roleId: String): Result<Unit>
    suspend fun getUsersInRole(roleId: String): Result<List<User>>
    
    suspend fun getAllPermissions(): Result<List<Permission>>
    suspend fun getPermissionById(permissionId: String): Result<Permission>
    suspend fun getPermissionsByCategory(category: PermissionCategory): Result<List<Permission>>
    suspend fun createPermission(permission: Permission): Result<Permission>
    suspend fun updatePermission(permission: Permission): Result<Permission>
    suspend fun deletePermission(permissionId: String): Result<Unit>
    
    suspend fun getAllPermissionGroups(): Result<List<PermissionGroup>>
    suspend fun getPermissionGroupById(groupId: String): Result<PermissionGroup>
    suspend fun createPermissionGroup(group: PermissionGroup): Result<PermissionGroup>
    suspend fun updatePermissionGroup(group: PermissionGroup): Result<PermissionGroup>
    suspend fun deletePermissionGroup(groupId: String): Result<Unit>
    
    // Security and Compliance
    suspend fun getSecurityReport(userId: String): Result<SecurityReport>
    suspend fun getComplianceReport(): Result<ComplianceReport>
    suspend fun getRiskAssessment(userId: String): Result<RiskAssessment>
    suspend fun updateSecurityFlags(userId: String, flags: Set<SecurityFlag>): Result<User>
    
    // Notifications
    suspend fun sendUserNotification(userId: String, notification: UserNotification): Result<Unit>
    suspend fun sendBulkNotifications(userIds: List<String>, notification: UserNotification): Result<BulkOperationResult>
    suspend fun getUserNotifications(userId: String, limit: Int, offset: Int): Result<List<UserNotification>>
    suspend fun markNotificationAsRead(userId: String, notificationId: String): Result<Unit>
    suspend fun deleteNotification(userId: String, notificationId: String): Result<Unit>
    
    // Data Export and Import
    suspend fun exportUserData(userId: String, format: ExportFormat): Result<String>
    suspend fun importUserData(userData: String, format: ExportFormat): Result<BulkOperationResult>
    suspend fun exportAllUsers(filters: UserSearchFilters, format: ExportFormat): Result<String>
    
    // Search and Discovery
    suspend fun searchUsersByQuery(query: String, limit: Int, offset: Int): Result<List<User>>
    suspend fun findUsersByEmailDomain(domain: String): Result<List<User>>
    suspend fun findUsersByPhonePrefix(prefix: String): Result<List<User>>
    suspend fun findUsersByLocation(latitude: Double, longitude: Double, radius: Double): Result<List<User>>
    suspend fun findUsersByDevice(deviceId: String): Result<List<User>>
    
    // Real-time Monitoring
    fun observeUserCount(): Flow<Int>
    fun observeActiveUserCount(): Flow<Int>
    fun observeRoleDistribution(): Flow<Map<UserRole, Int>>
    fun observeStatusDistribution(): Flow<Map<UserStatus, Int>>
    fun observeRecentLogins(): Flow<List<LoginHistory>>
    fun observeSecurityEvents(): Flow<List<UserActivityLog>>
    
    // Cache Management
    suspend fun refreshUserCache(userId: String): Result<Unit>
    suspend fun refreshAllUserCache(): Result<Unit>
    suspend fun clearUserCache(userId: String): Result<Unit>
    suspend fun clearAllUserCache(): Result<Unit>
    
    // Validation and Verification
    suspend fun validateUserData(userData: UserData): Result<ValidationResult>
    suspend fun verifyUserIntegrity(userId: String): Result<IntegrityReport>
    suspend fun checkUserCompliance(userId: String): Result<ComplianceStatus>
}

/**
 * Activity log filters
 */
data class ActivityLogFilters(
    val userId: String? = null,
    val action: ActivityAction? = null,
    val resource: String? = null,
    val resourceId: String? = null,
    val success: Boolean? = null,
    val ipAddress: String? = null,
    val deviceId: String? = null,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val limit: Int = 50,
    val offset: Int = 0
)

/**
 * Login history filters
 */
data class LoginHistoryFilters(
    val userId: String? = null,
    val success: Boolean? = null,
    val loginMethod: LoginMethod? = null,
    val ipAddress: String? = null,
    val deviceId: String? = null,
    val location: String? = null,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val securityFlags: Set<SecurityFlag>? = null,
    val limit: Int = 50,
    val offset: Int = 0
)

/**
 * Audit log filters
 */
data class AuditLogFilters(
    val userId: String? = null,
    val targetUserId: String? = null,
    val action: AuditAction? = null,
    val performedBy: String? = null,
    val startTime: Long? = null,
    val endTime: Long? = null,
    val limit: Int = 50,
    val offset: Int = 0
)

/**
 * Export format
 */
enum class ExportFormat {
    JSON,
    CSV,
    XML,
    PDF,
    EXCEL
}

/**
 * User notification
 */
data class UserNotification(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val priority: NotificationPriority,
    val data: Map<String, Any>?,
    val isRead: Boolean,
    val createdAt: Long,
    val readAt: Long?,
    val expiresAt: Long?
)

/**
 * Notification types
 */
enum class NotificationType {
    SYSTEM,
    SECURITY,
    ACCOUNT,
    BILLING,
    SHIPMENT,
    ANNOUNCEMENT,
    REMINDER,
    ALERT
}

/**
 * Notification priority
 */
enum class NotificationPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

/**
 * Security report
 */
data class SecurityReport(
    val userId: String,
    val riskScore: Float,
    val securityFlags: Set<SecurityFlag>,
    val loginAttempts: Int,
    val failedLogins: Int,
    val suspiciousActivities: List<UserActivityLog>,
    val recommendations: List<String>,
    val generatedAt: Long
)

/**
 * Compliance report
 */
data class ComplianceReport(
    val totalUsers: Int,
    val compliantUsers: Int,
    val nonCompliantUsers: Int,
    val complianceIssues: List<ComplianceIssue>,
    val recommendations: List<String>,
    val generatedAt: Long
)

/**
 * Compliance issue
 */
data class ComplianceIssue(
    val userId: String,
    val issue: String,
    val severity: ComplianceSeverity,
    val description: String,
    val recommendation: String
)

/**
 * Compliance severity
 */
enum class ComplianceSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * Risk assessment
 */
data class RiskAssessment(
    val userId: String,
    val overallRisk: RiskLevel,
    val riskFactors: Map<RiskFactor, Float>,
    val recommendations: List<String>,
    val assessedAt: Long
)

/**
 * Risk level
 */
enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * Risk factors
 */
enum class RiskFactor {
    WEAK_PASSWORD,
    NO_TWO_FACTOR,
    MULTIPLE_FAILED_LOGINS,
    SUSPICIOUS_IP,
    NEW_DEVICE,
    UNUSUAL_LOCATION,
    OUTDATED_APP,
    ROOTED_DEVICE,
    VPN_USAGE,
    TOR_USAGE
}

/**
 * User data for validation
 */
data class UserData(
    val username: String,
    val email: String,
    val phoneNumber: String?,
    val firstName: String,
    val lastName: String,
    val role: UserRole,
    val profile: UserProfile?
)

/**
 * Validation result
 */
data class ValidationResult(
    val isValid: Boolean,
    val errors: List<ValidationError>,
    val warnings: List<ValidationWarning>
)

/**
 * Validation error
 */
data class ValidationError(
    val field: String,
    val message: String,
    val code: String
)

/**
 * Validation warning
 */
data class ValidationWarning(
    val field: String,
    val message: String,
    val code: String
)

/**
 * Integrity report
 */
data class IntegrityReport(
    val userId: String,
    val isIntact: Boolean,
    val issues: List<IntegrityIssue>,
    val recommendations: List<String>,
    val checkedAt: Long
)

/**
 * Integrity issue
 */
data class IntegrityIssue(
    val type: IntegrityIssueType,
    val description: String,
    val severity: IntegritySeverity
)

/**
 * Integrity issue types
 */
enum class IntegrityIssueType {
    MISSING_PROFILE,
    INVALID_PERMISSIONS,
    CORRUPTED_DATA,
    ORPHANED_RECORDS,
    DUPLICATE_RECORDS,
    INCONSISTENT_DATA
}

/**
 * Integrity severity
 */
enum class IntegritySeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * Compliance status
 */
data class ComplianceStatus(
    val isCompliant: Boolean,
    val score: Float,
    val issues: List<ComplianceIssue>,
    val lastChecked: Long
)
