package com.edham.logistics.core.reports.data

import java.util.*

/**
 * Report Models - Data models for business intelligence reports
 */

// Report Types
enum class RevenueReportType {
    DAILY, WEEKLY, MONTHLY, YEARLY, DETAILED
}

enum class ShipmentReportType {
    SUMMARY, COMPREHENSIVE, PERFORMANCE, DELAYS
}

enum class DriverReportType {
    SUMMARY, DETAILED, PERFORMANCE, RATING
}

enum class CustomerReportType {
    SUMMARY, COMPREHENSIVE, BEHAVIOR, INSIGHTS
}

// Risk Levels
enum class RiskLevel {
    LOW, MEDIUM, HIGH, CRITICAL
}

// Revenue Report Models
data class RevenueReport(
    val id: String,
    val title: String,
    val startDate: Long,
    val endDate: Long,
    val reportType: RevenueReportType,
    val generatedAt: Long,
    val totalRevenue: Double,
    val totalInvoices: Int,
    val paidInvoices: Int,
    val unpaidInvoices: Int,
    val revenueByPeriod: List<RevenueByPeriod>,
    val revenueByCustomer: List<RevenueByCustomer>,
    val revenueByServiceType: List<RevenueByServiceType>,
    val paymentMethods: List<PaymentMethodAnalysis>,
    val overdueAnalysis: OverdueAnalysis,
    val averageInvoiceValue: Double,
    val revenueGrowth: Double
)

data class RevenueByPeriod(
    val period: String,
    val revenue: Double,
    val transactionCount: Int
)

data class RevenueByCustomer(
    val customerId: String,
    val revenue: Double,
    val invoiceCount: Int,
    val averageInvoiceValue: Double
)

data class RevenueByServiceType(
    val serviceType: String,
    val revenue: Double,
    val invoiceCount: Int,
    val averageInvoiceValue: Double
)

data class PaymentMethodAnalysis(
    val paymentMethod: String,
    val transactionCount: Int,
    val totalAmount: Double,
    val averageAmount: Double,
    val percentage: Double
)

data class OverdueAnalysis(
    val overdueCount: Int,
    val totalOverdueAmount: Double,
    val averageOverdueDays: Double,
    val overduePercentage: Double
)

// Shipment Performance Report Models
data class ShipmentPerformanceReport(
    val id: String,
    val title: String,
    val startDate: Long,
    val endDate: Long,
    val reportType: ShipmentReportType,
    val generatedAt: Long,
    val totalShipments: Int,
    val deliveredShipments: Int,
    val delayedShipments: Int,
    val cancelledShipments: Int,
    val onTimeDeliveryRate: Double,
    val averageDeliveryTime: Double,
    val performanceByRoute: List<RoutePerformance>,
    val performanceByDriver: List<DriverPerformance>,
    val performanceByVehicleType: List<VehicleTypePerformance>,
    val delayAnalysis: DelayAnalysis,
    val geographicPerformance: List<GeographicPerformance>
)

data class RoutePerformance(
    val route: String,
    val totalShipments: Int,
    val deliveredShipments: Int,
    val delayedShipments: Int,
    val onTimeDeliveryRate: Double,
    val averageDeliveryTime: Double
)

data class VehicleTypePerformance(
    val vehicleType: String,
    val totalShipments: Int,
    val deliveredShipments: Int,
    val delayedShipments: Int,
    val onTimeDeliveryRate: Double,
    val averageDeliveryTime: Double
)

data class DelayAnalysis(
    val delayedCount: Int,
    val averageDelayMinutes: Double,
    val maxDelayMinutes: Int,
    val delayPercentage: Double
)

data class GeographicPerformance(
    val location: String,
    val totalShipments: Int,
    val deliveredShipments: Int,
    val delayedShipments: Int,
    val onTimeDeliveryRate: Double
)

// Driver Performance Report Models
data class DriverPerformanceReport(
    val id: String,
    val title: String,
    val startDate: Long,
    val endDate: Long,
    val reportType: DriverReportType,
    val generatedAt: Long,
    val driverPerformances: List<DriverPerformance>,
    val teamStatistics: TeamStatistics,
    val performanceTrends: List<PerformanceTrend>,
    val topPerformers: List<DriverPerformance>,
    val areasForImprovement: List<String>
)

data class DriverPerformance(
    val driverId: String,
    val driverName: String,
    val totalShipments: Int,
    val deliveredShipments: Int,
    val delayedShipments: Int,
    val onTimeDeliveryRate: Double,
    val averageDeliveryTime: Double,
    val revenueGenerated: Double,
    val customerSatisfaction: Double,
    val fuelEfficiency: Double,
    val rating: Double
)

data class TeamStatistics(
    val totalDrivers: Int,
    val averageOnTimeDeliveryRate: Double,
    val averageCustomerSatisfaction: Double,
    val totalRevenueGenerated: Double,
    val topPerformer: DriverPerformance?
)

data class PerformanceTrend(
    val period: String,
    val metric: String,
    val value: Double,
    val changePercentage: Double
)

// Customer Behavior Report Models
data class CustomerBehaviorReport(
    val id: String,
    val title: String,
    val startDate: Long,
    val endDate: Long,
    val reportType: CustomerReportType,
    val generatedAt: Long,
    val customerInsights: List<CustomerInsight>,
    val marketSegmentation: MarketSegmentation,
    val customerLifetimeValue: CustomerLifetimeValue,
    val churnAnalysis: ChurnAnalysis,
    val seasonalPatterns: List<SeasonalPattern>,
    val topCustomers: List<CustomerInsight>,
    val atRiskCustomers: List<CustomerInsight>
)

data class CustomerInsight(
    val customerId: String,
    val customerName: String,
    val totalShipments: Int,
    val totalRevenue: Double,
    val averageOrderValue: Double,
    val shipmentFrequency: Double,
    val loyaltyScore: Double,
    val preferredServices: List<String>,
    val paymentBehavior: PaymentBehavior,
    val geographicPatterns: List<String>,
    val riskLevel: RiskLevel,
    val growthPotential: Double
)

data class PaymentBehavior(
    val onTimePaymentRate: Double,
    val averagePaymentDelay: Double,
    val preferredPaymentMethods: List<String>
)

data class MarketSegmentation(
    val totalCustomers: Int,
    val segments: List<Segment>
)

data class Segment(
    val name: String,
    val customerCount: Int
)

data class CustomerLifetimeValue(
    val averageLifetimeValue: Double,
    val totalLifetimeValue: Double,
    val averageCustomerLifetime: Double
)

data class ChurnAnalysis(
    val churnRate: Double,
    val atRiskCustomers: Int,
    val retentionRate: Double
)

data class SeasonalPattern(
    val season: String,
    val metric: String,
    val value: Double,
    val trend: String
)

// Export Models
data class ExportRequest(
    val reportId: String,
    val exportFormat: ExportFormat,
    val fileName: String,
    val includeCharts: Boolean = true,
    val includeRawData: Boolean = false
)

enum class ExportFormat {
    PDF, EXCEL, CSV, JSON
}

data class ExportResult(
    val success: Boolean,
    val filePath: String?,
    val fileSize: Long?,
    val errorMessage: String?
)

// Report Configuration Models
data class ReportConfiguration(
    val reportType: String,
    val defaultFilters: Map<String, Any>,
    val availableMetrics: List<String>,
    val chartTypes: List<String>,
    val exportFormats: List<ExportFormat>
)

data class ReportFilter(
    val key: String,
    val displayName: String,
    val type: FilterType,
    val options: List<String>? = null,
    val defaultValue: Any? = null
)

enum class FilterType {
    DATE_RANGE, TEXT, NUMBER, SELECT, MULTI_SELECT, BOOLEAN
}

data class ReportMetric(
    val key: String,
    val displayName: String,
    val description: String,
    val unit: String,
    val format: String
)

// Chart Models
data class ChartData(
    val type: ChartType,
    val title: String,
    val data: List<ChartSeries>,
    val config: ChartConfig
)

enum class ChartType {
    LINE, BAR, PIE, AREA, SCATTER, DONUT
}

data class ChartSeries(
    val name: String,
    val data: List<ChartPoint>
)

data class ChartPoint(
    val x: Any,
    val y: Double,
    val label: String? = null
)

data class ChartConfig(
    val showLegend: Boolean = true,
    val showGrid: Boolean = true,
    val colors: List<String> = emptyList(),
    val xAxisTitle: String? = null,
    val yAxisTitle: String? = null
)

// Report Schedule Models
data class ReportSchedule(
    val id: String,
    val name: String,
    val reportType: String,
    val frequency: ScheduleFrequency,
    val recipients: List<String>,
    val filters: Map<String, Any>,
    val exportFormat: ExportFormat,
    val isActive: Boolean,
    val nextRunTime: Long,
    val createdAt: Long
)

enum class ScheduleFrequency {
    DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY
}

// Report Template Models
data class ReportTemplate(
    val id: String,
    val name: String,
    val description: String,
    val reportType: String,
    val layout: ReportLayout,
    val metrics: List<String>,
    val filters: List<ReportFilter>,
    val charts: List<ChartData>,
    val isDefault: Boolean
)

data class ReportLayout(
    val sections: List<ReportSection>
)

data class ReportSection(
    val id: String,
    val title: String,
    val type: SectionType,
    val order: Int,
    val config: Map<String, Any>
)

enum class SectionType {
    HEADER, SUMMARY, CHART, TABLE, METRICS, TEXT
}
