package com.edham.logistics.user

import androidx.room.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * User Management Data Models
 */

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val email: String,
    val name: String,
    val phone: String,
    val passwordHash: String,
    val userType: String,
    val status: UserStatus,
    val roleIds: List<String>,
    val createdAt: Long,
    val updatedAt: Long,
    val createdBy: String? = null,
    val lastLoginAt: Long? = null,
    val loginAttempts: Int = 0,
    val isLocked: Boolean = false,
    val lockedUntil: Long? = null,
    val additionalInfo: Map<String, Any>? = null,
    val deactivatedBy: String? = null,
    val deactivatedAt: Long? = null,
    val deactivationReason: String? = null,
    val reactivatedBy: String? = null,
    val reactivatedAt: Long? = null,
    val reactivationReason: String? = null,
    val rolesUpdatedBy: String? = null,
    val rolesUpdatedAt: Long? = null,
    val passwordResetBy: String? = null,
    val passwordResetAt: Long? = null,
    val forcePasswordChange: Boolean = false,
    val blockedBy: String? = null,
    val blockedAt: Long? = null,
    val blockingReason: String? = null,
    val blockingEvidence: Map<String, Any>? = null
)

@Entity(tableName = "user_roles")
data class UserRole(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val permissions: List<String>,
    val isSystemRole: Boolean = false,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String? = null
)

@Entity(tableName = "permissions")
data class Permission(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: String,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_sessions")
data class UserSession(
    @PrimaryKey val id: String,
    val userId: String,
    val loginTime: Long,
    val logoutTime: Long? = null,
    val ipAddress: String,
    val userAgent: String,
    val loginSuccess: Boolean,
    val failureReason: String? = null,
    val isActive: Boolean = true,
    val deviceInfo: Map<String, String>? = null
)

@Entity(tableName = "user_activities")
data class UserActivity(
    @PrimaryKey val id: String,
    val userId: String,
    val targetUserId: String,
    val activityType: UserActivityType,
    val description: String,
    val metadata: Map<String, Any>? = null,
    val timestamp: Long,
    val ipAddress: String,
    val userAgent: String
)

@Entity(tableName = "security_events")
data class SecurityEvent(
    @PrimaryKey val id: String,
    val userId: String,
    val eventType: SecurityEventType,
    val description: String,
    val severity: SecurityEventSeverity,
    val metadata: Map<String, Any>? = null,
    val timestamp: Long,
    val ipAddress: String,
    val userAgent: String,
    val resolved: Boolean = false,
    val resolvedBy: String? = null,
    val resolvedAt: Long? = null,
    val resolution: String? = null
)

@Entity(tableName = "password_reset_tokens")
data class PasswordResetToken(
    @PrimaryKey val id: String,
    val userId: String,
    val token: String,
    val createdAt: Long,
    val expiresAt: Long,
    val isUsed: Boolean = false,
    val usedAt: Long? = null,
    val ipAddress: String,
    val userAgent: String
)

@Entity(tableName = "user_management_config")
data class UserManagementConfig(
    @PrimaryKey val id: Int,
    val maxLoginAttempts: Int,
    val lockoutDurationMinutes: Int,
    val passwordMinLength: Int,
    val passwordRequireSpecialChars: Boolean,
    val passwordRequireNumbers: Boolean,
    val sessionTimeoutMinutes: Int,
    val enableTwoFactorAuth: Boolean,
    val enableSuspiciousActivityDetection: Boolean,
    val enableAccountLockout: Boolean,
    val enablePasswordReset: Boolean,
    val passwordResetTokenExpiryHours: Int,
    val enableActivityLogging: Boolean,
    val enableRoleManagement: Boolean,
    val enableUserDeactivation: Boolean,
    val enableAccountBlocking: Boolean,
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Result classes
 */
data class UserManagementResult(
    val success: Boolean,
    val data: Map<String, Any>? = null,
    val message: String? = null,
    val error: String? = null,
    val errorCode: String? = null
)

data class UserStatistics(
    val userId: String,
    val userName: String?,
    val userEmail: String?,
    val userType: String?,
    val currentStatus: UserStatus?,
    val totalActivities: Int,
    val activitiesByType: Map<UserActivityType, Int>,
    val totalSessions: Int,
    val averageSessionDuration: Double,
    val lastActivity: Long?,
    val lastLogin: Long?,
    val loginCount: Int,
    val failedLoginCount: Int,
    val accountCreated: Long?,
    val roles: List<String>
)

data class SystemStatistics(
    val totalUsers: Int,
    val usersByStatus: Map<UserStatus, Int>,
    val usersByType: Map<String, Int>,
    val usersByRole: Map<String, Int>,
    val recentActivityCount: Int,
    val recentSecurityEventsCount: Int,
    val activeSessionsCount: Int,
    val systemConfig: UserManagementConfig?
)

data class UserSearchResult(
    val user: User,
    val roles: List<UserRole>,
    val lastActivity: UserActivity?,
    val activeSessions: Int,
    val securityEvents: Int
)

data class RoleAssignmentResult(
    val success: Boolean,
    val assignedRoles: List<String>,
    val removedRoles: List<String>,
    val message: String? = null,
    val error: String? = null
)

data class PasswordResetResult(
    val success: Boolean,
    val temporaryPassword: String?,
    val resetToken: String?,
    val expiresAt: Long?,
    val message: String? = null,
    val error: String? = null
)

data class AccountBlockingResult(
    val success: Boolean,
    val blockedAt: Long?,
    val blockingReason: String?,
    val evidence: Map<String, Any>?,
    val message: String? = null,
    val error: String? = null
)

/**
 * Enums
 */
enum class UserStatus {
    ACTIVE,
    INACTIVE,
    DEACTIVATED,
    BLOCKED,
    SUSPENDED
}

enum class UserActivityType {
    USER_CREATED,
    USER_UPDATED,
    USER_DELETED,
    USER_DEACTIVATED,
    USER_REACTIVATED,
    ROLES_ASSIGNED,
    ROLES_REMOVED,
    PASSWORD_CHANGED,
    PASSWORD_RESET,
    LOGIN_SUCCESS,
    LOGIN_FAILED,
    LOGOUT,
    SESSION_TERMINATED,
    ACCOUNT_LOCKED,
    ACCOUNT_UNLOCKED,
    ACCOUNT_BLOCKED,
    ACCOUNT_UNBLOCKED,
    PROFILE_UPDATED,
    PERMISSIONS_GRANTED,
    PERMISSIONS_REVOKED,
    SECURITY_ALERT,
    SYSTEM_CONFIG_UPDATED,
    DATA_EXPORTED,
    DATA_IMPORTED
}

enum class SecurityEventType {
    LOGIN_ATTEMPT_FAILED,
    MULTIPLE_LOGIN_FAILURES,
    SUSPICIOUS_LOGIN_LOCATION,
    SUSPICIOUS_LOGIN_TIME,
    ACCOUNT_LOCKED,
    ACCOUNT_BLOCKED,
    PASSWORD_RESET_REQUESTED,
    PASSWORD_RESET_COMPLETED,
    PRIVILEGE_ESCALATION_ATTEMPT,
    UNAUTHORIZED_ACCESS_ATTEMPT,
    DATA_ACCESS_VIOLATION,
    SYSTEM_TAMPERING,
    SUSPICIOUS_ACTIVITY_PATTERN,
    MALICIOUS_REQUEST_DETECTED,
    BRUTE_FORCE_ATTACK,
    SESSION_HIJACKING_ATTEMPT,
    ACCOUNT_TAKEOVER_ATTEMPT
}

enum class SecurityEventSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

/**
 * Data Access Objects
 */
@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: String): User?
    
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?
    
    @Query("SELECT * FROM users WHERE phone = :phone")
    suspend fun getUserByPhone(phone: String): User?
    
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
    suspend fun userExistsByEmail(email: String): Boolean
    
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE id = :id)")
    suspend fun userExistsById(id: String): Boolean
    
    @Query("SELECT * FROM users ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getAllUsers(limit: Int, offset: Int): List<User>
    
    @Query("SELECT * FROM users WHERE status = :status ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getUsersByStatus(status: UserStatus, limit: Int, offset: Int): List<User>
    
    @Query("SELECT * FROM users WHERE userType = :userType ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getUsersByType(userType: String, limit: Int, offset: Int): List<User>
    
    @Query("SELECT * FROM users WHERE :roleId IN (SELECT value FROM json_each(roleIds)) ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getUsersByRole(roleId: String, limit: Int, offset: Int): List<User>
    
    @Query("""
        SELECT * FROM users 
        WHERE (:status IS NULL OR status = :status)
        AND (:userType IS NULL OR userType = :userType)
        AND (:roleIds IS NULL OR :roleIds IN (SELECT value FROM json_each(roleIds)))
        ORDER BY createdAt DESC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getAllUsers(
        status: UserStatus? = null,
        userType: String? = null,
        roleIds: List<String>? = null,
        limit: Int = 100,
        offset: Int = 0
    ): List<User>
    
    @Query("SELECT * FROM users WHERE name LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%' ORDER BY createdAt DESC LIMIT :limit")
    suspend fun searchUsers(query: String, limit: Int): List<User>
    
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getTotalUserCount(): Int
    
    // Commented out due to KAPT errors - Room cannot generate code for Map return types with these queries
    /*
    @Query("SELECT status, COUNT(*) as count FROM users GROUP BY status")
    suspend fun getUsersByStatus(): Map<UserStatus, Int>
    
    @Query("SELECT userType, COUNT(*) as count FROM users GROUP BY userType")
    suspend fun getUsersByType(): Map<String, Int>
    
    @Query("SELECT roleId, COUNT(*) as count FROM users, json_each(roleIds) GROUP BY roleId")
    suspend fun getUsersByRoleCount(): Map<String, Int>
    */
    
    @Query("SELECT * FROM users WHERE lastLoginAt > :timestamp ORDER BY lastLoginAt DESC")
    suspend fun getRecentlyActiveUsers(timestamp: Long): List<User>
    
    @Query("SELECT * FROM users WHERE isLocked = 1 AND lockedUntil > :currentTime")
    suspend fun getLockedUsers(currentTime: Long): List<User>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)
    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUser(user: User)
    
    @Delete
    suspend fun deleteUser(user: User)
    
    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: String)
}

@Dao
interface UserRoleDao {
    @Query("SELECT * FROM user_roles WHERE id = :id")
    suspend fun getRoleById(id: String): UserRole?
    
    @Query("SELECT * FROM user_roles WHERE isActive = 1 ORDER BY name")
    suspend fun getAllActiveRoles(): List<UserRole>
    
    @Query("SELECT * FROM user_roles WHERE :id IN (SELECT value FROM json_each(permissions))")
    suspend fun getRolesByPermission(id: String): List<UserRole>
    
    @Query("SELECT * FROM user_roles WHERE id IN (:roleIds)")
    suspend fun getRolesByIds(roleIds: List<String>): List<UserRole>
    
    @Query("SELECT EXISTS(SELECT 1 FROM user_roles WHERE id = :id)")
    suspend fun roleExists(id: String): Boolean
    
    @Query("SELECT * FROM user_roles ORDER BY createdAt DESC")
    suspend fun getAllRoles(): List<UserRole>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRole(role: UserRole)
    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRole(role: UserRole)
    
    @Delete
    suspend fun deleteRole(role: UserRole)
    
    @Query("DELETE FROM user_roles WHERE id = :id")
    suspend fun deleteRoleById(id: String)
}

@Dao
interface PermissionDao {
    @Query("SELECT * FROM permissions WHERE id = :id")
    suspend fun getPermissionById(id: String): Permission?
    
    @Query("SELECT * FROM permissions WHERE isActive = 1 ORDER BY category, name")
    suspend fun getAllActivePermissions(): List<Permission>
    
    @Query("SELECT * FROM permissions WHERE category = :category AND isActive = 1 ORDER BY name")
    suspend fun getPermissionsByCategory(category: String): List<Permission>
    
    @Query("SELECT * FROM permissions ORDER BY category, name")
    suspend fun getAllPermissions(): List<Permission>
    
    @Query("SELECT EXISTS(SELECT 1 FROM permissions WHERE id = :id)")
    suspend fun permissionExists(id: String): Boolean
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPermission(permission: Permission)
    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePermission(permission: Permission)
    
    @Delete
    suspend fun deletePermission(permission: Permission)
    
    @Query("DELETE FROM permissions WHERE id = :id")
    suspend fun deletePermissionById(id: String)
}

@Dao
interface UserSessionDao {
    @Query("SELECT * FROM user_sessions WHERE id = :id")
    suspend fun getSessionById(id: String): UserSession?
    
    @Query("SELECT * FROM user_sessions WHERE userId = :userId ORDER BY loginTime DESC LIMIT :limit")
    suspend fun getUserSessions(userId: String, limit: Int): List<UserSession>
    
    @Query("SELECT * FROM user_sessions WHERE userId = :userId AND isActive = 1")
    suspend fun getActiveUserSessions(userId: String): List<UserSession>
    
    @Query("SELECT COUNT(*) FROM user_sessions WHERE isActive = 1")
    suspend fun getActiveSessionCount(): Int
    
    @Query("SELECT * FROM user_sessions WHERE loginTime > :timestamp ORDER BY loginTime DESC")
    suspend fun getRecentSessions(timestamp: Long): List<UserSession>
    
    @Query("SELECT * FROM user_sessions WHERE loginSuccess = 0 AND loginTime > :timestamp")
    suspend fun getFailedLogins(timestamp: Long): List<UserSession>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: UserSession)
    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSession(session: UserSession)
    
    @Query("UPDATE user_sessions SET isActive = 0, logoutTime = :logoutTime WHERE userId = :userId AND isActive = 1")
    suspend fun terminateAllUserSessions(userId: String, logoutTime: Long = System.currentTimeMillis())
    
    @Query("UPDATE user_sessions SET isActive = 0, logoutTime = :logoutTime WHERE id = :sessionId")
    suspend fun terminateSession(sessionId: String, logoutTime: Long = System.currentTimeMillis())
    
    @Delete
    suspend fun deleteSession(session: UserSession)
}

@Dao
interface UserActivityDao {
    @Query("SELECT * FROM user_activities WHERE id = :id")
    suspend fun getActivityById(id: String): UserActivity?
    
    @Query("SELECT * FROM user_activities WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getUserActivity(userId: String, limit: Int): List<UserActivity>
    
    @Query("""
        SELECT * FROM user_activities 
        WHERE userId = :userId 
        AND (:activityType IS NULL OR activityType = :activityType)
        AND (:startDate IS NULL OR timestamp >= :startDate)
        AND (:endDate IS NULL OR timestamp <= :endDate)
        ORDER BY timestamp DESC 
        LIMIT :limit
    """)
    suspend fun getUserActivity(
        userId: String,
        activityType: UserActivityType? = null,
        startDate: Long? = null,
        endDate: Long? = null,
        limit: Int = 100
    ): List<UserActivity>
    
    @Query("SELECT * FROM user_activities WHERE targetUserId = :targetUserId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getTargetUserActivity(targetUserId: String, limit: Int): List<UserActivity>
    
    @Query("SELECT * FROM user_activities WHERE timestamp > :timestamp ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentActivity(timestamp: Long, limit: Int = 1000): List<UserActivity>
    
    @Query("SELECT * FROM user_activities WHERE activityType = :activityType ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getActivityByType(activityType: UserActivityType, limit: Int): List<UserActivity>
    
    // Commented out due to KAPT errors - Room cannot generate code for Map return types with these queries
    /*
    @Query("SELECT activityType, COUNT(*) as count FROM user_activities GROUP BY activityType")
    suspend fun getActivityByTypeCount(): Map<UserActivityType, Int>
    */
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: UserActivity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(activities: List<UserActivity>)
    
    @Delete
    suspend fun deleteActivity(activity: UserActivity)
    
    @Query("DELETE FROM user_activities WHERE timestamp < :cutoffTime")
    suspend fun deleteOldActivities(cutoffTime: Long)
}

@Dao
interface SecurityEventDao {
    @Query("SELECT * FROM security_events WHERE id = :id")
    suspend fun getEventById(id: String): SecurityEvent?
    
    @Query("SELECT * FROM security_events WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getUserSecurityEvents(userId: String, limit: Int): List<SecurityEvent>
    
    @Query("SELECT * FROM security_events WHERE severity = :severity ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getEventsBySeverity(severity: SecurityEventSeverity, limit: Int): List<SecurityEvent>
    
    @Query("SELECT * FROM security_events WHERE eventType = :eventType ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getEventsByType(eventType: SecurityEventType, limit: Int): List<SecurityEvent>
    
    @Query("SELECT * FROM security_events WHERE resolved = 0 ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getUnresolvedEvents(limit: Int): List<SecurityEvent>
    
    @Query("""
        SELECT * FROM security_events 
        WHERE (:startDate IS NULL OR timestamp >= :startDate)
        AND (:endDate IS NULL OR timestamp <= :endDate)
        ORDER BY timestamp DESC 
        LIMIT :limit
    """)
    suspend fun getEventsByDateRange(
        startDate: Long? = null,
        endDate: Long? = null,
        limit: Int = 1000
    ): List<SecurityEvent>
    
    @Query("""
        SELECT * FROM security_events 
        WHERE severity IN ('HIGH', 'CRITICAL')
        AND (:startDate IS NULL OR timestamp >= :startDate)
        AND (:endDate IS NULL OR timestamp <= :endDate)
        ORDER BY timestamp DESC 
        LIMIT :limit
    """)
    suspend fun getSuspiciousEvents(
        startDate: Long? = null,
        endDate: Long? = null,
        limit: Int = 100
    ): List<SecurityEvent>
    
    @Query("SELECT * FROM security_events WHERE timestamp > :timestamp ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentSecurityEvents(timestamp: Long, limit: Int = 1000): List<SecurityEvent>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: SecurityEvent)
    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateEvent(event: SecurityEvent)
    
    @Query("UPDATE security_events SET resolved = 1, resolvedBy = :resolvedBy, resolvedAt = :resolvedAt, resolution = :resolution WHERE id = :eventId")
    suspend fun resolveEvent(eventId: String, resolvedBy: String, resolvedAt: Long, resolution: String)
    
    @Delete
    suspend fun deleteEvent(event: SecurityEvent)
    
    @Query("DELETE FROM security_events WHERE timestamp < :cutoffTime")
    suspend fun deleteOldEvents(cutoffTime: Long)
}

@Dao
interface PasswordResetTokenDao {
    @Query("SELECT * FROM password_reset_tokens WHERE token = :token")
    suspend fun getTokenByToken(token: String): PasswordResetToken?
    
    @Query("SELECT * FROM password_reset_tokens WHERE userId = :userId AND isUsed = 0 AND expiresAt > :currentTime")
    suspend fun getActiveTokensForUser(userId: String, currentTime: Long = System.currentTimeMillis()): List<PasswordResetToken>
    
    @Query("SELECT * FROM password_reset_tokens WHERE isUsed = 0 AND expiresAt > :currentTime")
    suspend fun getActiveTokens(currentTime: Long = System.currentTimeMillis()): List<PasswordResetToken>
    
    @Query("SELECT * FROM password_reset_tokens WHERE expiresAt < :currentTime")
    suspend fun getExpiredTokens(currentTime: Long = System.currentTimeMillis()): List<PasswordResetToken>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToken(token: PasswordResetToken)
    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateToken(token: PasswordResetToken)
    
    @Query("UPDATE password_reset_tokens SET isUsed = 1, usedAt = :usedAt WHERE token = :token")
    suspend fun markTokenAsUsed(token: String, usedAt: Long = System.currentTimeMillis())
    
    @Delete
    suspend fun deleteToken(token: PasswordResetToken)
    
    @Query("DELETE FROM password_reset_tokens WHERE expiresAt < :currentTime")
    suspend fun deleteExpiredTokens(currentTime: Long = System.currentTimeMillis())
}

@Dao
interface UserManagementConfigDao {
    @Query("SELECT * FROM user_management_config WHERE id = 1")
    suspend fun getConfig(): UserManagementConfig?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: UserManagementConfig)
    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateConfig(config: UserManagementConfig)
}

/**
 * Type converters
 */
class UserManagementConverters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value)
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, type)
    }
    
    @TypeConverter
    fun fromStringMap(value: Map<String, Any>): String {
        return Gson().toJson(value)
    }
    
    @TypeConverter
    fun toStringMap(value: String): Map<String, Any> {
        val type = object : TypeToken<Map<String, Any>>() {}.type
        return Gson().fromJson(value, type)
    }
    
    @TypeConverter
    fun fromStringStringMap(value: Map<String, String>): String {
        return Gson().toJson(value)
    }
    
    @TypeConverter
    fun toStringStringMap(value: String): Map<String, String> {
        val type = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(value, type)
    }
    
    @TypeConverter
    fun fromUserStatus(value: UserStatus): String {
        return value.name
    }
    
    @TypeConverter
    fun toUserStatus(value: String): UserStatus {
        return UserStatus.valueOf(value)
    }
    
    @TypeConverter
    fun fromUserActivityType(value: UserActivityType): String {
        return value.name
    }
    
    @TypeConverter
    fun toUserActivityType(value: String): UserActivityType {
        return UserActivityType.valueOf(value)
    }
    
    @TypeConverter
    fun fromSecurityEventType(value: SecurityEventType): String {
        return value.name
    }
    
    @TypeConverter
    fun toSecurityEventType(value: String): SecurityEventType {
        return SecurityEventType.valueOf(value)
    }
    
    @TypeConverter
    fun fromSecurityEventSeverity(value: SecurityEventSeverity): String {
        return value.name
    }
    
    @TypeConverter
    fun toSecurityEventSeverity(value: String): SecurityEventSeverity {
        return SecurityEventSeverity.valueOf(value)
    }
}
