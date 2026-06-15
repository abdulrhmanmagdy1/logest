package com.edham.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Objects for Executive Reporting System
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ExecutiveReportDTO {
    private String reportId;
    private String reportType; // DAILY, WEEKLY, MONTHLY
    private LocalDate reportDate;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private DailyMetricsDTO dailyMetrics;
    private WeeklyMetricsDTO weeklyMetrics;
    private MonthlyMetricsDTO monthlyMetrics;
    private RevenueBreakdownDTO revenueBreakdown;
    private ShipmentEfficiencyDTO shipmentEfficiency;
    private DriverPerformanceDTO driverPerformance;
    private SystemHealthDTO systemHealth;
    private String generatedBy;
    private LocalDateTime generatedAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class DailyMetricsDTO {
    private Integer totalShipments;
    private Integer deliveredShipments;
    private Integer pendingShipments;
    private Integer inTransitShipments;
    private Double totalRevenue;
    private Double averageDeliveryTime; // hours
    private Double deliveryRate;
    private Map<String, Integer> hourlyBreakdown;
    private List<String> topPerformingDrivers;
    private List<String> delayedShipments;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WeeklyMetricsDTO {
    private Integer totalShipments;
    private Integer deliveredShipments;
    private Double totalRevenue;
    private Map<String, Integer> dailyBreakdown; // Day name -> shipment count
    private Double averageShipmentsPerDay;
    private Double weekOverWeekGrowth;
    private Map<String, Double> dailyRevenue;
    private List<String> weeklyHighlights;
    private List<String> weeklyChallenges;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MonthlyMetricsDTO {
    private Integer totalShipments;
    private Integer deliveredShipments;
    private Double totalRevenue;
    private Map<String, Integer> weeklyBreakdown; // Week number -> shipment count
    private Double averageShipmentsPerWeek;
    private Double monthlyGrowth;
    private Map<String, Double> weeklyRevenue;
    private Double monthOverMonthGrowth;
    private List<String> monthlyHighlights;
    private List<String> monthlyChallenges;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RevenueBreakdownDTO {
    private Double totalRevenue;
    private Map<String, Double> revenueByServiceType; // STANDARD, EXPRESS, PREMIUM
    private Map<String, Double> revenueByRegion;
    private Map<String, Double> revenueByVehicleType;
    private Double averageRevenuePerShipment;
    private Double revenuePerDriver;
    private Double revenuePerVehicle;
    private List<TopRevenueSourceDTO> topRevenueSources;
    private Map<String, Double> revenueTrends;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class TopRevenueSourceDTO {
    private String sourceName;
    private String sourceType; // REGION, SERVICE_TYPE, VEHICLE_TYPE
    private Double revenue;
    private Double percentage;
    private Integer shipmentCount;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ShipmentEfficiencyDTO {
    private Double onTimeDeliveryRate;
    private Double averageDeliveryTime;
    private Double fuelEfficiency;
    private Double vehicleUtilization;
    private Double routeOptimizationRate;
    private Double loadingEfficiency;
    private Double efficiencyScore;
    private Map<String, Double> efficiencyByRegion;
    private Map<String, Double> efficiencyByVehicleType;
    private List<EfficiencyIssueDTO> efficiencyIssues;
    private List<String> efficiencyRecommendations;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EfficiencyIssueDTO {
    private String issueType;
    private String description;
    private Integer affectedShipments;
    private Double impactScore;
    private String recommendedAction;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class DriverPerformanceDTO {
    private Integer totalDrivers;
    private Integer activeDrivers;
    private Integer inactiveDrivers;
    private Double driverUtilization;
    private Double averageRating;
    private Double averageDeliveriesPerDriver;
    private Double averageRevenuePerDriver;
    private Map<String, Integer> performanceDistribution; // EXCELLENT, GOOD, AVERAGE, POOR
    private List<TopDriverDTO> topPerformers;
    private List<BottomDriverDTO> bottomPerformers;
    private Map<String, Double> performanceByRegion;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class TopDriverDTO {
    private Long driverId;
    private String driverName;
    private Integer totalShipments;
    private Integer deliveredShipments;
    private Double totalRevenue;
    private Double averageRating;
    private Double onTimeDeliveryRate;
    private Double performanceScore;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class BottomDriverDTO {
    private Long driverId;
    private String driverName;
    private Integer totalShipments;
    private Integer deliveredShipments;
    private Double totalRevenue;
    private Double averageRating;
    private Double onTimeDeliveryRate;
    private Double performanceScore;
    private List<String> improvementAreas;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class SystemHealthDTO {
    private Double systemUptime;
    private Double averageResponseTime; // milliseconds
    private Double errorRate;
    private Integer activeVehicles;
    private Integer totalVehicles;
    private Double vehicleAvailability;
    private Integer overdueMaintenance;
    private Integer activeEmergencies;
    private Double healthScore;
    private Map<String, Double> componentHealth; // API, DATABASE, WEBSOCKET, NOTIFICATIONS
    private List<SystemAlertDTO> activeAlerts;
    private List<String> healthRecommendations;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class SystemAlertDTO {
    private String alertId;
    private String alertType; // PERFORMANCE, AVAILABILITY, SECURITY, MAINTENANCE
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    private String title;
    private String description;
    private LocalDateTime timestamp;
    private Boolean acknowledged;
    private String acknowledgedBy;
    private LocalDateTime acknowledgedAt;
    private String recommendedAction;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class DriverRankingDTO {
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private List<DriverPerformanceSummaryDTO> driverPerformances;
    private RankingStatisticsDTO statistics;
    private Integer totalDrivers;
    private LocalDateTime generatedAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class DriverPerformanceSummaryDTO {
    private Long driverId;
    private String driverName;
    private Integer rank;
    private Integer totalShipments;
    private Integer deliveredShipments;
    private Double totalRevenue;
    private Double averageRating;
    private Double onTimeDeliveryRate;
    private Double overallScore;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> recommendations;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RankingStatisticsDTO {
    private Double averageScore;
    private Double medianScore;
    private Double standardDeviation;
    private Integer topPerformers; // Score >= 0.8
    private Integer averagePerformers; // Score between 0.6 and 0.8
    private Integer bottomPerformers; // Score < 0.6
    private Double scoreDistribution;
    private Map<String, Integer> performanceDistribution;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ReportRequestDTO {
    private String reportType; // DAILY, WEEKLY, MONTHLY
    private LocalDate reportDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> includeSections; // METRICS, REVENUE, EFFICIENCY, PERFORMANCE, HEALTH
    private String format; // JSON, PDF, EXCEL
    private Map<String, Object> filters;
    private Boolean includeCharts;
    private Boolean includeRecommendations;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ReportResponseDTO {
    private Boolean success;
    private String reportId;
    private ExecutiveReportDTO reportData;
    private String downloadUrl;
    private String format;
    private Long fileSize; // bytes
    private LocalDateTime generatedAt;
    private String message;
    private String error;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ReportScheduleDTO {
    private String scheduleId;
    private String reportType;
    private String frequency; // DAILY, WEEKLY, MONTHLY
    private LocalDateTime nextRunTime;
    private List<String> recipients;
    private Map<String, Object> scheduleConfig;
    private Boolean active;
    private LocalDateTime createdAt;
    private String createdBy;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ReportTemplateDTO {
    private String templateId;
    private String templateName;
    private String reportType;
    private List<String> includedSections;
    private Map<String, Object> templateConfig;
    private String description;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private String createdBy;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ExecutiveDashboardDTO {
    private LocalDateTime lastUpdated;
    private ExecutiveReportDTO latestDailyReport;
    private ExecutiveReportDTO latestWeeklyReport;
    private ExecutiveReportDTO latestMonthlyReport;
    private List<ReportScheduleDTO> activeSchedules;
    private List<SystemAlertDTO> criticalAlerts;
    private Map<String, Object> keyMetrics;
    private List<String> recommendations;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ReportExportDTO {
    private String exportId;
    private String reportId;
    private String format; // PDF, EXCEL, CSV
    private byte[] fileData;
    private String fileName;
    private String mimeType;
    private Long fileSize;
    private LocalDateTime exportedAt;
    private String exportedBy;
    private String downloadUrl;
}
