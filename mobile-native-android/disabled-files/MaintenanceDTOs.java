package com.edham.logistics.dto;

import com.edham.logistics.model.MaintenanceType;
import com.edham.logistics.model.MaintenanceStatus;
import com.edham.logistics.model.MaintenancePriority;
import com.edham.logistics.model.MaintenancePartDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Objects for Maintenance System
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MaintenanceRequestDTO {
    private Long vehicleId;
    private MaintenanceType type;
    private MaintenancePriority priority;
    private String title;
    private String description;
    private LocalDateTime scheduledDate;
    private Duration estimatedDuration;
    private Double estimatedCost;
    private Long assignedMechanicId;
    private String workshopName;
    private String workshopLocation;
    private Long odometerReading;
    private List<String> reportedIssues;
    private List<String> toolsRequired;
    private Boolean safetyInspectionRequired;
    private String notes;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MaintenanceResponseDTO {
    private Boolean success;
    private String maintenanceId;
    private String message;
    private LocalDateTime timestamp;
    private String error;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MaintenanceUpdateDTO {
    private String maintenanceId;
    private MaintenanceStatus status;
    private String resolutionNotes;
    private Double actualCost;
    private Double laborCost;
    private Double partsCost;
    private List<MaintenancePartDTO> partsUsed;
    private List<String> findings;
    private List<String> recommendations;
    private List<String> photos;
    private Boolean qualityCheckPassed;
    private String qualityCheckNotes;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MaintenanceStatisticsDTO {
    private Long totalMaintenance;
    private Long completedMaintenance;
    private Long overdueMaintenance;
    private Double totalCost;
    private Double averageCost;
    private Long totalDowntimeHours;
    private Map<MaintenanceType, Long> maintenanceByType;
    private Map<MaintenancePriority, Long> maintenanceByPriority;
    private Double completionRate;
    private LocalDateTime generatedAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MaintenanceDashboardDTO {
    private List<Maintenance> overdueMaintenance;
    private List<Maintenance> todayMaintenance;
    private List<Maintenance> upcomingMaintenance;
    private List<Maintenance> inProgressMaintenance;
    private MaintenanceStatisticsDTO last30DaysStats;
    private MaintenanceStatisticsDTO last7DaysStats;
    private Integer totalVehicles;
    private Integer vehiclesNeedingMaintenance;
    private LocalDateTime generatedAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MaintenanceScheduleDTO {
    private Long vehicleId;
    private String licensePlate;
    private List<Maintenance> scheduledMaintenance;
    private LocalDateTime nextMaintenanceDate;
    private MaintenanceType nextMaintenanceType;
    private MaintenancePriority nextMaintenancePriority;
    private Boolean requiresImmediateAttention;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MaintenanceCostAnalysisDTO {
    private Long vehicleId;
    private String licensePlate;
    private Double totalMaintenanceCost;
    private Double averageCostPerMaintenance;
    private Double costPerKilometer;
    private Map<MaintenanceType, Double> costByType;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private Integer maintenanceCount;
    private List<Maintenance> maintenanceHistory;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MaintenancePerformanceDTO {
    private Long vehicleId;
    private String licensePlate;
    private Integer totalMaintenanceEvents;
    private Integer breakdownEvents;
    private Integer preventiveMaintenanceEvents;
    private Double averageDowntimeHours;
    private Double reliabilityScore;
    private Double maintenanceEfficiency;
    private LocalDateTime lastMaintenanceDate;
    private LocalDateTime nextScheduledMaintenance;
    private List<String> performanceIssues;
    private List<String> recommendations;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MaintenanceAlertDTO {
    private String alertId;
    private Long vehicleId;
    private String licensePlate;
    private String alertType; // OVERDUE, UPCOMING, CRITICAL, PREDICTIVE
    private String title;
    private String message;
    private MaintenanceType maintenanceType;
    private MaintenancePriority priority;
    private LocalDateTime dueDate;
    private LocalDateTime alertDate;
    private Boolean acknowledged;
    private String acknowledgedBy;
    private LocalDateTime acknowledgedDate;
    private Map<String, Object> details;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MaintenancePredictionDTO {
    private Long vehicleId;
    private String licensePlate;
    private List<PredictiveMaintenanceDTO> predictions;
    private LocalDateTime nextPredictedMaintenance;
    private MaintenanceType predictedMaintenanceType;
    private Double confidenceLevel;
    private Map<String, Object> riskFactors;
    private List<String> recommendations;
    private LocalDateTime generatedAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class PredictiveMaintenanceDTO {
    private MaintenanceType maintenanceType;
    private LocalDateTime predictedDate;
    private Double probability;
    private String reason;
    private Map<String, Object> indicators;
    private MaintenancePriority suggestedPriority;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MaintenanceInventoryDTO {
    private String partNumber;
    private String partName;
    private String manufacturer;
    private Integer currentStock;
    private Integer minimumStock;
    private Integer reorderLevel;
    private Double unitCost;
    private Double totalValue;
    private List<String> compatibleVehicles;
    private LocalDateTime lastReorderDate;
    private LocalDateTime nextReorderDate;
    private List<Maintenance> recentUsage;
    private Boolean reorderRequired;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MaintenanceWorkshopDTO {
    private String workshopId;
    private String name;
    private String location;
    private String contactPhone;
    private String contactEmail;
    private List<String> services;
    private Integer capacity;
    private Integer currentLoad;
    private Double averageTurnaroundTime;
    private Double averageCost;
    private List<Long> assignedMechanics;
    private List<Maintenance> activeJobs;
    private Map<String, Object> performanceMetrics;
    private Boolean available;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MaintenanceReportDTO {
    private String reportId;
    private String reportType; // SUMMARY, DETAILED, COST, PERFORMANCE
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private MaintenanceStatisticsDTO statistics;
    private List<MaintenanceCostAnalysisDTO> costAnalysis;
    private List<MaintenancePerformanceDTO> performanceAnalysis;
    private List<MaintenanceAlertDTO> alerts;
    private Map<String, Object> insights;
    private List<String> recommendations;
    private LocalDateTime generatedAt;
    private String generatedBy;
}
