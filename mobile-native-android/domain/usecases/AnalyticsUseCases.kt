// ============================================
// 🚀 Edham Logistics - Analytics Use Cases
// Premium Dark Theme with Edham Orange Identity
// ============================================

package com.edham.logistics.domain.usecases

import com.edham.logistics.data.repository.AnalyticsRepository
import com.edham.logistics.domain.model.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ============================================
 * Analytics Use Cases - حالات استخدام التحليلات
 * ============================================
 * تنظيم منطق الأعمال للتحليلات والتقارير
 */

@Singleton
class GetDashboardMetricsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    /**
     * الحصول على مقاييس لوحة التحكم
     */
    suspend operator fun invoke(
        timeRange: String = "last_30_days",
        userId: String? = null
    ): Result<DashboardMetrics> {
        return try {
            val metrics = analyticsRepository.getDashboardMetrics(timeRange, userId)
            Result.Success(metrics)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

@Singleton
class GetRevenueAnalyticsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    /**
     * الحصول على تحليلات الإيرادات
     */
    suspend operator fun invoke(
        timeRange: String = "last_30_days",
        role: String? = null
    ): Result<RevenueAnalytics> {
        return try {
            val analytics = analyticsRepository.getRevenueAnalytics(timeRange, role)
            Result.Success(analytics)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

@Singleton
class GetPerformanceMetricsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    /**
     * الحصول على مقاييس الأداء
     */
    suspend operator fun invoke(
        timeRange: String = "last_30_days",
        driverId: String? = null
    ): Result<PerformanceMetrics> {
        return try {
            val metrics = analyticsRepository.getPerformanceMetrics(timeRange, driverId)
            Result.Success(metrics)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

@Singleton
class GetFleetAnalyticsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    /**
     * الحصول على تحليلات الأسطول
     */
    suspend operator fun invoke(
        timeRange: String = "last_30_days"
    ): Result<FleetAnalytics> {
        return try {
            val analytics = analyticsRepository.getFleetAnalytics(timeRange)
            Result.Success(analytics)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

@Singleton
class GenerateReportUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    /**
     * إنشاء تقرير PDF
     */
    suspend operator fun invoke(
        reportType: String,
        timeRange: String,
        filters: Map<String, String> = emptyMap()
    ): Result<String> {
        return try {
            val reportPath = analyticsRepository.generateReport(reportType, timeRange, filters)
            Result.Success(reportPath)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

@Singleton
class GetSystemHealthUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    /**
     * الحصول على صحة النظام
     */
    suspend operator fun invoke(): Result<SystemHealth> {
        return try {
            val health = analyticsRepository.getSystemHealth()
            Result.Success(health)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

@Singleton
class GetUserActivityUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    /**
     * الحصول على نشاط المستخدمين
     */
    suspend operator fun invoke(
        timeRange: String = "last_24_hours",
        role: String? = null
    ): Result<List<UserActivity>> {
        return try {
            val activities = analyticsRepository.getUserActivity(timeRange, role)
            Result.Success(activities)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

@Singleton
class GetAlertsUseCase @Inject constructor(
    private val analyticsRepository: AnalyticsRepository
) {
    /**
     * الحصول على التنبيهات
     */
    suspend operator fun invoke(
        severity: String? = null,
        limit: Int = 50
    ): Result<List<Alert>> {
        return try {
            val alerts = analyticsRepository.getAlerts(severity, limit)
            Result.Success(alerts)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

// Data classes for analytics
data class DashboardMetrics(
    val totalShipments: Int,
    val activeShipments: Int,
    val completedShipments: Int,
    val totalRevenue: Double,
    val averageDeliveryTime: Double,
    val customerSatisfaction: Double,
    val fleetUtilization: Double,
    val temperatureCompliance: Double
)

data class RevenueAnalytics(
    val totalRevenue: Double,
    val revenueByMonth: List<MonthlyRevenue>,
    val revenueByService: List<ServiceRevenue>,
    val growthRate: Double,
    val forecastRevenue: Double
)

data class MonthlyRevenue(
    val month: String,
    val revenue: Double,
    val shipments: Int
)

data class ServiceRevenue(
    val service: String,
    val revenue: Double,
    val percentage: Double
)

data class PerformanceMetrics(
    val onTimeDeliveryRate: Double,
    val averageDeliveryTime: Double,
    val fuelEfficiency: Double,
    val vehicleUtilization: Double,
    val driverPerformance: List<DriverPerformance>
)

data class DriverPerformance(
    val driverId: String,
    val driverName: String,
    val completedShipments: Int,
    val onTimeDeliveryRate: Double,
    val averageDeliveryTime: Double,
    val rating: Double
)

data class FleetAnalytics(
    val totalVehicles: Int,
    val activeVehicles: Int,
    val maintenanceRequired: Int,
    val averageAge: Double,
    val fuelConsumption: Double,
    val utilizationRate: Double,
    val vehiclesByType: Map<String, Int>
)

data class SystemHealth(
    val serverStatus: String,
    val databaseStatus: String,
    val apiResponseTime: Double,
    val uptime: Double,
    val errorRate: Double,
    val activeUsers: Int,
    val storageUsage: Double
)

data class UserActivity(
    val userId: String,
    val userName: String,
    val role: String,
    val lastActive: Long,
    val actionsCount: Int,
    val sessionId: String
)

data class Alert(
    val id: String,
    val type: String,
    val severity: String,
    val message: String,
    val timestamp: Long,
    val resolved: Boolean,
    val assignedTo: String?
)
