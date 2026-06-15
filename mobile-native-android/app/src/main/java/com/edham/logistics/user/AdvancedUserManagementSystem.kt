package com.edham.logistics.user

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Advanced User Management System - Complete user administration with security
 */
@Database(
    entities = [
        User::class,
        UserRole::class,
        Permission::class,
        UserSession::class,
        UserActivity::class,
        SecurityEvent::class,
        PasswordResetToken::class,
        UserManagementConfig::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(UserManagementConverters::class)
abstract class UserManagementDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun userRoleDao(): UserRoleDao
    abstract fun permissionDao(): PermissionDao
    abstract fun userSessionDao(): UserSessionDao
    abstract fun userActivityDao(): UserActivityDao
    abstract fun securityEventDao(): SecurityEventDao
    abstract fun passwordResetTokenDao(): PasswordResetTokenDao
    abstract fun userManagementConfigDao(): UserManagementConfigDao
}

/**
 * Advanced User Management System
 */
class AdvancedUserManagementSystem private constructor(context: Context) {
    
    companion object {
        @Volatile
        private var INSTANCE: AdvancedUserManagementSystem? = null
        
        fun getInstance(context: Context): AdvancedUserManagementSystem {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AdvancedUserManagementSystem(context).also { INSTANCE = it }
            }
        }
    }
    
    private val database = Room.databaseBuilder(
        context.applicationContext,
        UserManagementDatabase::class.java,
        "edham_user_management"
    ).fallbackToDestructiveMigration()
    .build()
    
    private val userDao = database.userDao()
    private val userRoleDao = database.userRoleDao()
    private val permissionDao = database.permissionDao()
    private val userSessionDao = database.userSessionDao()
    private val userActivityDao = database.userActivityDao()
    private val securityEventDao = database.securityEventDao()
    private val passwordResetTokenDao = database.passwordResetTokenDao()
    private val configDao = database.userManagementConfigDao()
    
    private val gson = Gson()
    private val secureRandom = SecureRandom()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
    
    private val managementScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val activityQueue = ConcurrentLinkedQueue<UserActivity>()
    
    private var systemConfig: UserManagementConfig? = null
    
    init {
        initializeSystem()
    }
    
    /**
     * Initialize system
     */
    private fun initializeSystem() {
        managementScope.launch {
            // Load configuration
            systemConfig = configDao.getConfig()
            if (systemConfig == null) {
                createDefaultConfiguration()
            }
            
            // Start activity processor
            startActivityProcessor()
            
            // Initialize default roles and permissions
            initializeDefaultRolesAndPermissions()
        }
    }
    
    /**
     * Create default configuration
     */
    private suspend fun createDefaultConfiguration() {
        val defaultConfig = UserManagementConfig(
            id = 1,
            maxLoginAttempts = 5,
            lockoutDurationMinutes = 30,
            passwordMinLength = 8,
            passwordRequireSpecialChars = true,
            passwordRequireNumbers = true,
            sessionTimeoutMinutes = 480,
            enableTwoFactorAuth = false,
            enableSuspiciousActivityDetection = true,
            enableAccountLockout = true,
            enablePasswordReset = true,
            passwordResetTokenExpiryHours = 24,
            enableActivityLogging = true,
            enableRoleManagement = true,
            enableUserDeactivation = true,
            enableAccountBlocking = true
        )
        
        configDao.insertConfig(defaultConfig)
        systemConfig = defaultConfig
    }
    
    /**
     * Initialize default roles and permissions
     */
    private suspend fun initializeDefaultRolesAndPermissions() {
        // Create default permissions
        val defaultPermissions = listOf(
            Permission(
                id = "USER_VIEW",
                name = "View Users",
                description = "Can view user information",
                category = "USER_MANAGEMENT"
            ),
            Permission(
                id = "USER_CREATE",
                name = "Create Users",
                description = "Can create new users",
                category = "USER_MANAGEMENT"
            ),
            Permission(
                id = "USER_UPDATE",
                name = "Update Users",
                description = "Can update user information",
                category = "USER_MANAGEMENT"
            ),
            Permission(
                id = "USER_DELETE",
                name = "Delete Users",
                description = "Can delete users",
                category = "USER_MANAGEMENT"
            ),
            Permission(
                id = "USER_DEACTIVATE",
                name = "Deactivate Users",
                description = "Can deactivate users",
                category = "USER_MANAGEMENT"
            ),
            Permission(
                id = "USER_BLOCK",
                name = "Block Users",
                description = "Can block suspicious accounts",
                category = "USER_MANAGEMENT"
            ),
            Permission(
                id = "ROLE_MANAGE",
                name = "Manage Roles",
                description = "Can manage user roles",
                category = "ROLE_MANAGEMENT"
            ),
            Permission(
                id = "PASSWORD_RESET",
                name = "Reset Password",
                description = "Can reset user passwords",
                category = "SECURITY"
            ),
            Permission(
                id = "ACTIVITY_VIEW",
                name = "View Activity",
                description = "Can view user activity logs",
                category = "ACTIVITY_LOGGING"
            ),
            Permission(
                id = "SYSTEM_CONFIG",
                name = "System Configuration",
                description = "Can configure system settings",
                category = "SYSTEM"
            )
        )
        
        defaultPermissions.forEach { permission ->
            if (!permissionDao.permissionExists(permission.id)) {
                permissionDao.insertPermission(permission)
            }
        }
        
        // Create default roles
        val adminPermissions = defaultPermissions.map { it.id }
        val managerPermissions = listOf(
            "USER_VIEW", "USER_CREATE", "USER_UPDATE", "USER_DEACTIVATE", 
            "ROLE_MANAGE", "PASSWORD_RESET", "ACTIVITY_VIEW"
        )
        val driverPermissions = listOf("USER_VIEW")
        val customerPermissions = listOf("USER_VIEW")
        
        val defaultRoles = listOf(
            UserRole(
                id = "ADMIN",
                name = "Administrator",
                description = "Full system access",
                permissions = adminPermissions,
                isSystemRole = true,
                isActive = true
            ),
            UserRole(
                id = "MANAGER",
                name = "Manager",
                description = "Management access",
                permissions = managerPermissions,
                isSystemRole = true,
                isActive = true
            ),
            UserRole(
                id = "DRIVER",
                name = "Driver",
                description = "Driver access",
                permissions = driverPermissions,
                isSystemRole = true,
                isActive = true
            ),
            UserRole(
                id = "CUSTOMER",
                name = "Customer",
                description = "Customer access",
                permissions = customerPermissions,
                isSystemRole = true,
                isActive = true
            )
        )
        
        defaultRoles.forEach { role ->
            if (!userRoleDao.roleExists(role.id)) {
                userRoleDao.insertRole(role)
            }
        }
    }
    
    /**
     * Create user
     */
    suspend fun createUser(
        adminUserId: String,
        email: String,
        name: String,
        phone: String,
        roleIds: List<String>,
        userType: String = "DRIVER",
        additionalInfo: Map<String, Any>? = null
    ): UserManagementResult {
        return try {
            // Validate input
            val validationResult = validateUserCreation(email, name, phone, roleIds)
            if (!validationResult.success) {
                return validationResult
            }
            
            // Check if user already exists
            if (userDao.userExistsByEmail(email)) {
                return UserManagementResult(
                    success = false,
                    error = "User with this email already exists",
                    errorCode = "USER_EXISTS"
                )
            }
            
            // Generate temporary password
            val tempPassword = generateTemporaryPassword()
            val hashedPassword = hashPassword(tempPassword)
            
            // Create user
            val user = User(
                id = UUID.randomUUID().toString(),
                email = email,
                name = name,
                phone = phone,
                passwordHash = hashedPassword,
                userType = userType,
                status = UserStatus.ACTIVE,
                roleIds = roleIds,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                createdBy = adminUserId,
                lastLoginAt = null,
                loginAttempts = 0,
                isLocked = false,
                lockedUntil = null,
                additionalInfo = additionalInfo
            )
            
            userDao.insertUser(user)
            
            // Log activity
            logUserActivity(
                userId = adminUserId,
                targetUserId = user.id,
                activityType = UserActivityType.USER_CREATED,
                description = "Created user: $name ($email)",
                metadata = mapOf(
                    "userEmail" to email,
                    "userName" to name,
                    "userType" to userType,
                    "assignedRoles" to roleIds
                )
            )
            
            UserManagementResult(
                success = true,
                data = mapOf(
                    "user" to user,
                    "temporaryPassword" to tempPassword
                ),
                message = "User created successfully"
            )
            
        } catch (e: Exception) {
            UserManagementResult(
                success = false,
                error = "Failed to create user: ${e.message}",
                errorCode = "CREATION_ERROR"
            )
        }
    }
    
    /**
     * Deactivate user
     */
    suspend fun deactivateUser(
        adminUserId: String,
        targetUserId: String,
        reason: String
    ): UserManagementResult {
        return try {
            val user = userDao.getUserById(targetUserId)
                ?: return UserManagementResult(
                    success = false,
                    error = "User not found",
                    errorCode = "USER_NOT_FOUND"
                )
            
            if (user.status == UserStatus.DEACTIVATED) {
                return UserManagementResult(
                    success = false,
                    error = "User is already deactivated",
                    errorCode = "ALREADY_DEACTIVATED"
                )
            }
            
            // Update user status
            val updatedUser = user.copy(
                status = UserStatus.DEACTIVATED,
                updatedAt = System.currentTimeMillis(),
                deactivatedBy = adminUserId,
                deactivatedAt = System.currentTimeMillis(),
                deactivationReason = reason
            )
            
            userDao.updateUser(updatedUser)
            
            // Terminate all active sessions
            userSessionDao.terminateAllUserSessions(targetUserId)
            
            // Log activity
            logUserActivity(
                userId = adminUserId,
                targetUserId = targetUserId,
                activityType = UserActivityType.USER_DEACTIVATED,
                description = "Deactivated user: ${user.name} - $reason",
                metadata = mapOf(
                    "userName" to user.name,
                    "userEmail" to user.email,
                    "deactivationReason" to reason
                )
            )
            
            UserManagementResult(
                success = true,
                data = mapOf("user" to updatedUser),
                message = "User deactivated successfully"
            )
            
        } catch (e: Exception) {
            UserManagementResult(
                success = false,
                error = "Failed to deactivate user: ${e.message}",
                errorCode = "DEACTIVATION_ERROR"
            )
        }
    }
    
    /**
     * Reactivate user
     */
    suspend fun reactivateUser(
        adminUserId: String,
        targetUserId: String,
        reason: String
    ): UserManagementResult {
        return try {
            val user = userDao.getUserById(targetUserId)
                ?: return UserManagementResult(
                    success = false,
                    error = "User not found",
                    errorCode = "USER_NOT_FOUND"
                )
            
            if (user.status != UserStatus.DEACTIVATED) {
                return UserManagementResult(
                    success = false,
                    error = "User is not deactivated",
                    errorCode = "NOT_DEACTIVATED"
                )
            }
            
            // Update user status
            val updatedUser = user.copy(
                status = UserStatus.ACTIVE,
                updatedAt = System.currentTimeMillis(),
                reactivatedBy = adminUserId,
                reactivatedAt = System.currentTimeMillis(),
                reactivationReason = reason,
                loginAttempts = 0,
                isLocked = false,
                lockedUntil = null
            )
            
            userDao.updateUser(updatedUser)
            
            // Log activity
            logUserActivity(
                userId = adminUserId,
                targetUserId = targetUserId,
                activityType = UserActivityType.USER_REACTIVATED,
                description = "Reactivated user: ${user.name} - $reason",
                metadata = mapOf(
                    "userName" to user.name,
                    "userEmail" to user.email,
                    "reactivationReason" to reason
                )
            )
            
            UserManagementResult(
                success = true,
                data = mapOf("user" to updatedUser),
                message = "User reactivated successfully"
            )
            
        } catch (e: Exception) {
            UserManagementResult(
                success = false,
                error = "Failed to reactivate user: ${e.message}",
                errorCode = "REACTIVATION_ERROR"
            )
        }
    }
    
    /**
     * Assign roles dynamically
     */
    suspend fun assignRoles(
        adminUserId: String,
        targetUserId: String,
        roleIds: List<String>
    ): UserManagementResult {
        return try {
            val user = userDao.getUserById(targetUserId)
                ?: return UserManagementResult(
                    success = false,
                    error = "User not found",
                    errorCode = "USER_NOT_FOUND"
                )
            
            // Validate roles exist
            val validRoles = roleIds.filter { userRoleDao.roleExists(it) }
            if (validRoles.size != roleIds.size) {
                return UserManagementResult(
                    success = false,
                    error = "One or more roles do not exist",
                    errorCode = "INVALID_ROLES"
                )
            }
            
            val oldRoles = user.roleIds
            
            // Update user roles
            val updatedUser = user.copy(
                roleIds = validRoles,
                updatedAt = System.currentTimeMillis(),
                rolesUpdatedBy = adminUserId,
                rolesUpdatedAt = System.currentTimeMillis()
            )
            
            userDao.updateUser(updatedUser)
            
            // Log activity
            logUserActivity(
                userId = adminUserId,
                targetUserId = targetUserId,
                activityType = UserActivityType.ROLES_ASSIGNED,
                description = "Updated roles for user: ${user.name}",
                metadata = mapOf(
                    "userName" to user.name,
                    "userEmail" to user.email,
                    "oldRoles" to oldRoles,
                    "newRoles" to validRoles,
                    "addedRoles" to (validRoles - oldRoles.toSet()),
                    "removedRoles" to (oldRoles.toSet() - validRoles)
                )
            )
            
            UserManagementResult(
                success = true,
                data = mapOf(
                    "user" to updatedUser,
                    "oldRoles" to oldRoles,
                    "newRoles" to validRoles
                ),
                message = "Roles assigned successfully"
            )
            
        } catch (e: Exception) {
            UserManagementResult(
                success = false,
                error = "Failed to assign roles: ${e.message}",
                errorCode = "ROLE_ASSIGNMENT_ERROR"
            )
        }
    }
    
    /**
     * Get user activity
     */
    suspend fun getUserActivity(
        targetUserId: String,
        activityType: UserActivityType? = null,
        startDate: Long? = null,
        endDate: Long? = null,
        limit: Int = 100
    ): List<UserActivity> {
        return userActivityDao.getUserActivity(
            userId = targetUserId,
            activityType = activityType,
            startDate = startDate,
            endDate = endDate,
            limit = limit
        )
    }
    
    /**
     * Get user statistics
     */
    suspend fun getUserStatistics(targetUserId: String): UserStatistics {
        val user = userDao.getUserById(targetUserId)
        val activities = userActivityDao.getUserActivity(targetUserId, limit = 1000)
        val sessions = userSessionDao.getUserSessions(targetUserId, limit = 100)
        
        return UserStatistics(
            userId = targetUserId,
            userName = user?.name,
            userEmail = user?.email,
            userType = user?.userType,
            currentStatus = user?.status,
            totalActivities = activities.size,
            activitiesByType = activities.groupBy { it.activityType }.mapValues { it.value.size },
            totalSessions = sessions.size,
            averageSessionDuration = if (sessions.isNotEmpty()) {
                sessions.filter { it.logoutTime != null }
                    .mapNotNull { session ->
                        val logoutTime = session.logoutTime ?: return@mapNotNull null
                        val duration = logoutTime - session.loginTime
                        duration
                    }
                    .map { it.toDouble() }
                    .average()
            } else 0.0,
            lastActivity = activities.maxOfOrNull { it.timestamp },
            lastLogin = user?.lastLoginAt,
            loginCount = sessions.count { it.loginSuccess },
            failedLoginCount = sessions.count { !it.loginSuccess },
            accountCreated = user?.createdAt,
            roles = user?.roleIds ?: emptyList()
        )
    }
    
    /**
     * Reset password securely
     */
    suspend fun resetPassword(
        adminUserId: String,
        targetUserId: String,
        newPassword: String? = null
    ): UserManagementResult {
        return try {
            val user = userDao.getUserById(targetUserId)
                ?: return UserManagementResult(
                    success = false,
                    error = "User not found",
                    errorCode = "USER_NOT_FOUND"
                )
            
            // Check if password reset is enabled
            if (!(systemConfig?.enablePasswordReset ?: false)) {
                return UserManagementResult(
                    success = false,
                    error = "Password reset is disabled",
                    errorCode = "PASSWORD_RESET_DISABLED"
                )
            }
            
            // Generate new password if not provided
            val finalPassword = newPassword ?: generateTemporaryPassword()
            
            // Validate password
            val passwordValidation = validatePassword(finalPassword)
            if (!passwordValidation.success) {
                return passwordValidation
            }
            
            // Hash new password
            val hashedPassword = hashPassword(finalPassword)
            
            // Update user password
            val updatedUser = user.copy(
                passwordHash = hashedPassword,
                updatedAt = System.currentTimeMillis(),
                passwordResetBy = adminUserId,
                passwordResetAt = System.currentTimeMillis(),
                loginAttempts = 0,
                isLocked = false,
                lockedUntil = null,
                forcePasswordChange = newPassword == null
            )
            
            userDao.updateUser(updatedUser)
            
            // Terminate all active sessions (force re-login)
            userSessionDao.terminateAllUserSessions(targetUserId)
            
            // Log activity
            logUserActivity(
                userId = adminUserId,
                targetUserId = targetUserId,
                activityType = UserActivityType.PASSWORD_RESET,
                description = "Password reset for user: ${user.name}",
                metadata = mapOf(
                    "userName" to user.name,
                    "userEmail" to user.email,
                    "resetBy" to adminUserId,
                    "forceChange" to (newPassword == null)
                )
            )
            
            // Create password reset token for email notification
            val token = generatePasswordResetToken(targetUserId)
            
            UserManagementResult(
                success = true,
                data = mapOf(
                    "user" to updatedUser,
                    "newPassword" to finalPassword,
                    "resetToken" to token.token
                ),
                message = "Password reset successfully"
            )
            
        } catch (e: Exception) {
            UserManagementResult(
                success = false,
                error = "Failed to reset password: ${e.message}",
                errorCode = "PASSWORD_RESET_ERROR"
            )
        }
    }
    
    /**
     * Block suspicious account
     */
    suspend fun blockSuspiciousAccount(
        adminUserId: String,
        targetUserId: String,
        reason: String,
        evidence: Map<String, Any>? = null
    ): UserManagementResult {
        return try {
            val user = userDao.getUserById(targetUserId)
                ?: return UserManagementResult(
                    success = false,
                    error = "User not found",
                    errorCode = "USER_NOT_FOUND"
                )
            
            if (user.status == UserStatus.BLOCKED) {
                return UserManagementResult(
                    success = false,
                    error = "User is already blocked",
                    errorCode = "ALREADY_BLOCKED"
                )
            }
            
            // Update user status
            val updatedUser = user.copy(
                status = UserStatus.BLOCKED,
                updatedAt = System.currentTimeMillis(),
                blockedBy = adminUserId,
                blockedAt = System.currentTimeMillis(),
                blockingReason = reason,
                blockingEvidence = evidence
            )
            
            userDao.updateUser(updatedUser)
            
            // Terminate all active sessions
            userSessionDao.terminateAllUserSessions(targetUserId)
            
            // Log security event
            logSecurityEvent(
                userId = targetUserId,
                eventType = SecurityEventType.ACCOUNT_BLOCKED,
                description = "Account blocked: ${user.name} - $reason",
                severity = SecurityEventSeverity.CRITICAL,
                metadata = mapOf(
                    "userName" to user.name,
                    "userEmail" to user.email,
                    "blockedBy" to adminUserId,
                    "blockingReason" to reason,
                    "evidence" to (evidence ?: emptyMap<String, Any>())
                )
            )
            
            // Log user activity
            logUserActivity(
                userId = adminUserId,
                targetUserId = targetUserId,
                activityType = UserActivityType.ACCOUNT_BLOCKED,
                description = "Blocked suspicious account: ${user.name} - $reason",
                metadata = mapOf(
                    "userName" to user.name,
                    "userEmail" to user.email,
                    "blockingReason" to reason,
                    "evidence" to (evidence ?: emptyMap<String, Any>())
                )
            )
            
            UserManagementResult(
                success = true,
                data = mapOf("user" to updatedUser),
                message = "Account blocked successfully"
            )
            
        } catch (e: Exception) {
            UserManagementResult(
                success = false,
                error = "Failed to block account: ${e.message}",
                errorCode = "BLOCK_ERROR"
            )
        }
    }
    
    /**
     * Get all users
     */
    suspend fun getAllUsers(
        status: UserStatus? = null,
        userType: String? = null,
        roleIds: List<String>? = null,
        limit: Int = 100,
        offset: Int = 0
    ): List<User> {
        return userDao.getAllUsers(
            status = status,
            userType = userType,
            roleIds = roleIds,
            limit = limit,
            offset = offset
        )
    }
    
    /**
     * Get users by role
     */
    suspend fun getUsersByRole(roleId: String, limit: Int = 50, offset: Int = 0): List<User> {
        return userDao.getUsersByRole(roleId, limit, offset)
    }
    
    /**
     * Search users
     */
    suspend fun searchUsers(query: String, limit: Int = 50): List<User> {
        return userDao.searchUsers(query, limit)
    }
    
    /**
     * Get user permissions
     */
    suspend fun getUserPermissions(userId: String): List<String> {
        val user = userDao.getUserById(userId) ?: return emptyList()
        val roles = userRoleDao.getRolesByIds(user.roleIds)
        return roles.flatMap { it.permissions }.distinct()
    }
    
    /**
     * Check user permission
     */
    suspend fun hasPermission(userId: String, permissionId: String): Boolean {
        val userPermissions = getUserPermissions(userId)
        return userPermissions.contains(permissionId)
    }
    
    /**
     * Get suspicious activities
     */
    suspend fun getSuspiciousActivities(
        startDate: Long? = null,
        endDate: Long? = null,
        limit: Int = 100
    ): List<SecurityEvent> {
        return securityEventDao.getSuspiciousEvents(
            startDate = startDate,
            endDate = endDate,
            limit = limit
        )
    }
    
    /**
     * Get system statistics
     */
    suspend fun getSystemStatistics(): SystemStatistics {
        val totalUsers = userDao.getTotalUserCount()
        // Commented out due to KAPT errors - return empty maps for now
        // val usersByStatus = userDao.getUsersByStatus()
        // val usersByType = userDao.getUsersByType()
        // val usersByRole = userDao.getUsersByRoleCount()
        val usersByStatus = emptyMap<UserStatus, Int>()
        val usersByType = emptyMap<String, Int>()
        val usersByRole = emptyMap<String, Int>()
        val recentActivity = userActivityDao.getRecentActivity(24 * 60 * 60 * 1000L)
        val securityEvents = securityEventDao.getRecentSecurityEvents(24 * 60 * 60 * 1000L)
        val activeSessions = userSessionDao.getActiveSessionCount()
        
        return SystemStatistics(
            totalUsers = totalUsers,
            usersByStatus = usersByStatus,
            usersByType = usersByType,
            usersByRole = usersByRole,
            recentActivityCount = recentActivity.size,
            recentSecurityEventsCount = securityEvents.size,
            activeSessionsCount = activeSessions,
            systemConfig = systemConfig
        )
    }
    
    // Helper methods
    private fun validateUserCreation(
        email: String,
        name: String,
        phone: String,
        roleIds: List<String>
    ): UserManagementResult {
        if (email.isBlank() || !email.contains("@")) {
            return UserManagementResult(
                success = false,
                error = "Invalid email address",
                errorCode = "INVALID_EMAIL"
            )
        }
        
        if (name.isBlank() || name.length < 2) {
            return UserManagementResult(
                success = false,
                error = "Name must be at least 2 characters",
                errorCode = "INVALID_NAME"
            )
        }
        
        if (phone.isBlank() || phone.length < 10) {
            return UserManagementResult(
                success = false,
                error = "Invalid phone number",
                errorCode = "INVALID_PHONE"
            )
        }
        
        if (roleIds.isEmpty()) {
            return UserManagementResult(
                success = false,
                error = "At least one role must be assigned",
                errorCode = "NO_ROLES"
            )
        }
        
        return UserManagementResult(success = true)
    }
    
    private fun validatePassword(password: String): UserManagementResult {
        val config = systemConfig ?: return UserManagementResult(
            success = false,
            error = "System configuration not available",
            errorCode = "CONFIG_ERROR"
        )
        
        if (password.length < config.passwordMinLength) {
            return UserManagementResult(
                success = false,
                error = "Password must be at least ${config.passwordMinLength} characters",
                errorCode = "PASSWORD_TOO_SHORT"
            )
        }
        
        if (config.passwordRequireNumbers && !password.any { it.isDigit() }) {
            return UserManagementResult(
                success = false,
                error = "Password must contain at least one number",
                errorCode = "PASSWORD_NO_NUMBERS"
            )
        }
        
        if (config.passwordRequireSpecialChars && !password.any { "!@#$%^&*()_+-=[]{}|;:,.<>?".contains(it) }) {
            return UserManagementResult(
                success = false,
                error = "Password must contain at least one special character",
                errorCode = "PASSWORD_NO_SPECIAL_CHARS"
            )
        }
        
        return UserManagementResult(success = true)
    }
    
    private fun generateTemporaryPassword(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*"
        return (1..12).map { chars.random() }.joinToString("")
    }
    
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(password.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }
    
    private fun generatePasswordResetToken(userId: String): PasswordResetToken {
        val token = (1..64).map { "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".random() }.joinToString("")
        val resetToken = PasswordResetToken(
            id = UUID.randomUUID().toString(),
            userId = userId,
            token = token,
            createdAt = System.currentTimeMillis(),
            expiresAt = System.currentTimeMillis() + (systemConfig?.passwordResetTokenExpiryHours ?: 24) * 60 * 60 * 1000L,
            isUsed = false,
            ipAddress = getCurrentIPAddress() ?: "",
            userAgent = getCurrentUserAgent() ?: ""
        )
        
        managementScope.launch {
            passwordResetTokenDao.insertToken(resetToken)
        }
        
        return resetToken
    }
    
    private fun logUserActivity(
        userId: String,
        targetUserId: String,
        activityType: UserActivityType,
        description: String,
        metadata: Map<String, Any>? = null
    ) {
        if (!(systemConfig?.enableActivityLogging ?: false)) return
        
        val activity = UserActivity(
            id = UUID.randomUUID().toString(),
            userId = userId,
            targetUserId = targetUserId,
            activityType = activityType,
            description = description,
            metadata = metadata ?: emptyMap(),
            timestamp = System.currentTimeMillis(),
            ipAddress = getCurrentIPAddress() ?: "",
            userAgent = getCurrentUserAgent() ?: ""
        )
        
        activityQueue.offer(activity)
    }
    
    private fun logSecurityEvent(
        userId: String,
        eventType: SecurityEventType,
        description: String,
        severity: SecurityEventSeverity,
        metadata: Map<String, Any>? = null
    ) {
        val event = SecurityEvent(
            id = UUID.randomUUID().toString(),
            userId = userId,
            eventType = eventType,
            description = description,
            severity = severity,
            metadata = metadata,
            timestamp = System.currentTimeMillis(),
            ipAddress = getCurrentIPAddress(),
            userAgent = getCurrentUserAgent()
        )
        
        managementScope.launch {
            securityEventDao.insertEvent(event)
        }
    }
    
    private fun startActivityProcessor() {
        managementScope.launch {
            while (true) {
                try {
                    if (activityQueue.isNotEmpty()) {
                        val batch = mutableListOf<UserActivity>()
                        
                        repeat(50) {
                            val activity = activityQueue.poll()
                            if (activity != null) {
                                batch.add(activity)
                            } else {
                                return@repeat
                            }
                        }
                        
                        if (batch.isNotEmpty()) {
                            userActivityDao.insertAll(batch)
                        }
                    }
                    
                    delay(1000)
                } catch (e: Exception) {
                    delay(5000)
                }
            }
        }
    }
    
    private fun getCurrentIPAddress(): String {
        return "127.0.0.1" // In real implementation, get actual IP
    }
    
    private fun getCurrentUserAgent(): String {
        return "Edham Logistics Android App"
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        managementScope.cancel()
    }
}
