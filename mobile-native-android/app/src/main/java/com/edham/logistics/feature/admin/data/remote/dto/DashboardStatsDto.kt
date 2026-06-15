package com.edham.logistics.feature.admin.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DashboardStatsDto(
    @SerializedName("total_shipments")
    val totalShipments: Int,
    
    @SerializedName("active_shipments")
    val activeShipments: Int,
    
    @SerializedName("completed_shipments")
    val completedShipments: Int,
    
    @SerializedName("cancelled_shipments")
    val cancelledShipments: Int,
    
    @SerializedName("total_drivers")
    val totalDrivers: Int,
    
    @SerializedName("active_drivers")
    val activeDrivers: Int,
    
    @SerializedName("total_vehicles")
    val totalVehicles: Int,
    
    @SerializedName("available_vehicles")
    val availableVehicles: Int,
    
    @SerializedName("total_revenue")
    val totalRevenue: Double,
    
    @SerializedName("monthly_revenue")
    val monthlyRevenue: Double,
    
    @SerializedName("total_customers")
    val totalCustomers: Int,
    
    @SerializedName("new_customers_this_month")
    val newCustomersThisMonth: Int,
    
    @SerializedName("pending_tasks")
    val pendingTasks: Int,
    
    @SerializedName("overdue_tasks")
    val overdueTasks: Int,
    
    @SerializedName("system_health")
    val systemHealth: SystemHealthDto
)

data class SystemHealthDto(
    @SerializedName("overall_health")
    val overallHealth: String,
    
    @SerializedName("api_status")
    val apiStatus: String,
    
    @SerializedName("database_status")
    val databaseStatus: String,
    
    @SerializedName("last_backup_time")
    val lastBackupTime: String,
    
    @SerializedName("storage_usage")
    val storageUsage: Double,
    
    @SerializedName("memory_usage")
    val memoryUsage: Double,
    
    @SerializedName("cpu_usage")
    val cpuUsage: Double
)
