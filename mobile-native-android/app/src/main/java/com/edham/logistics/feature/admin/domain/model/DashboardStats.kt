package com.edham.logistics.feature.admin.domain.model

data class DashboardStats(
    val totalShipments: Int,
    val activeShipments: Int,
    val completedShipments: Int,
    val cancelledShipments: Int,
    val totalDrivers: Int,
    val activeDrivers: Int,
    val totalVehicles: Int,
    val availableVehicles: Int,
    val totalRevenue: Double,
    val monthlyRevenue: Double,
    val totalCustomers: Int,
    val newCustomersThisMonth: Int,
    val pendingTasks: Int,
    val overdueTasks: Int,
    val systemHealth: SystemHealth
)

data class SystemHealth(
    val overallHealth: String, // "healthy", "warning", "critical"
    val apiStatus: String,
    val databaseStatus: String,
    val lastBackupTime: String,
    val storageUsage: Double, // percentage
    val memoryUsage: Double, // percentage
    val cpuUsage: Double // percentage
)

data class UserManagement(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String,
    val status: String,
    val department: String? = null,
    val position: String? = null,
    val hireDate: String,
    val lastLogin: String? = null,
    val profileImage: String? = null,
    val permissions: List<String>
)

data class SystemSettings(
    val id: String,
    val key: String,
    val value: String,
    val description: String,
    val category: String,
    val isEditable: Boolean,
    val dataType: String // "string", "number", "boolean", "json"
)

data class ActivityLog(
    val id: String,
    val userId: String,
    val userName: String,
    val action: String,
    val resource: String,
    val resourceId: String? = null,
    val timestamp: String,
    val ipAddress: String? = null,
    val userAgent: String? = null,
    val details: Map<String, Any> = emptyMap()
)
