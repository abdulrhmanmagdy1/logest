package com.edham.logistics.dto;

import com.edham.logistics.model.Emergency;
import com.edham.logistics.model.EmergencySeverity;
import com.edham.logistics.model.EmergencyStatus;
import com.edham.logistics.model.EmergencyType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Emergency Data Transfer Objects
 * Supports ultra-fast emergency response and notification delivery
 */

/**
 * Request DTO for triggering emergency alert
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EmergencyRequestDTO {

    @NotNull(message = "Driver ID is required")
    private Long driverId;

    private Long shipmentId;

    private Long vehicleId;

    @NotNull(message = "Emergency type is required")
    private EmergencyType emergencyType;

    @NotNull(message = "Severity is required")
    private EmergencySeverity severity;

    private LocationDTO location;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    private Boolean requiresImmediateAction = true;

    private Map<String, Object> additionalData;
}

/**
 * Response DTO for emergency operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EmergencyResponseDTO {

    private boolean success;

    private String emergencyId;

    private String message;

    private String error;

    private LocalDateTime timestamp;

    private Map<String, Object> metadata;
}

/**
 * Emergency statistics DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EmergencyStatsDTO {

    private Long totalEmergencies;

    private Long resolvedEmergencies;

    private Long activeEmergencies;

    private Long criticalEmergencies;

    private Long highSeverityEmergencies;

    private Long mediumSeverityEmergencies;

    private Long lowSeverityEmergencies;

    private Map<EmergencyType, Long> typeBreakdown;

    private Map<EmergencyStatus, Long> statusBreakdown;

    private Double averageResolutionTime; // in minutes

    private Long longestResolutionTime; // in minutes

    private Long shortestResolutionTime; // in minutes

    private Double resolutionRate; // percentage

    private LocalDateTime periodStart;

    private LocalDateTime periodEnd;

    private Map<String, Object> additionalMetrics;

    // Method to set period
    public void period(LocalDateTime start, LocalDateTime end) {
        this.periodStart = start;
        this.periodEnd = end;
    }
}

/**
 * Emergency dashboard DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EmergencyDashboardDTO {

    private List<Emergency> activeEmergencies;

    private EmergencyStatsDTO last24Hours;

    private EmergencyStatsDTO last7Days;

    private EmergencyStatsDTO last30Days;

    private List<EmergencyAlertDTO> recentAlerts;

    private Map<String, Object> systemStatus;

    private Map<String, Object> responseMetrics;

    private LocalDateTime lastUpdated;

    private Map<String, Object> performanceIndicators;
}

/**
 * Emergency notification DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EmergencyNotificationDTO {

    private String id;

    private String title;

    private String message;

    private EmergencyType type;

    private EmergencySeverity severity;

    private Long driverId;

    private String driverName;

    private Long shipmentId;

    private String shipmentTrackingNumber;

    private Long vehicleId;

    private LocationDTO location;

    private LocalDateTime timestamp;

    private boolean requiresAction;

    private String actionRequired;

    private LocalDateTime actionDeadline;

    private String assignedTo;

    private Set<String> targetRoles;

    private Map<String, Object> metadata;

    private List<String> contactMethods;

    private String escalationLevel;

    private LocalDateTime escalationTime;
}

/**
 * Emergency update DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EmergencyUpdateDTO {

    private String emergencyId;

    private EmergencyStatus status;

    private String update;

    private String updatedBy;

    private LocalDateTime updateTimestamp;

    private LocationDTO currentLocation;

    private String additionalNotes;

    private Map<String, Object> updateMetadata;

    private List<String> attachments;

    private Boolean requiresFollowUp;

    private LocalDateTime followUpTime;

    private String assignedTo;

    private String priority;

    private String estimatedResolutionTime;
}

/**
 * Emergency resolution DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EmergencyResolutionDTO {

    private String emergencyId;

    private EmergencyStatus finalStatus;

    private String resolution;

    private String resolvedBy;

    private LocalDateTime resolvedAt;

    private Long totalResolutionTime; // in minutes

    private String resolutionCategory;

    private List<String> actionsTaken;

    private Map<String, Object> resolutionDetails;

    private String lessonsLearned;

    private List<String> preventiveMeasures;

    private Boolean followUpRequired;

    private LocalDateTime followUpDate;

    private String followUpAssignedTo;

    private Double resolutionCost;

    private String costBreakdown;

    private List<String> resourcesUsed;

    private Map<String, Object> performanceMetrics;
}

/**
 * Emergency contact DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EmergencyContactDTO {

    private String id;

    private String name;

    private String phoneNumber;

    private String email;

    private String role;

    private String department;

    private Boolean isPrimary;

    private Boolean is24Hours;

    private String specialization;

    private List<EmergencyType> handledTypes;

    private Map<String, Object> contactMetadata;

    private LocalDateTime lastContacted;

    private String lastContactReason;

    private Integer responseTime; // in minutes

    private Double successRate; // percentage

    private Boolean isActive;

    private String availabilitySchedule;

    private String backupContact;

    private String escalationContact;
}

/**
 * Emergency protocol DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EmergencyProtocolDTO {

    private String id;

    private EmergencyType emergencyType;

    private EmergencySeverity severity;

    private String protocolName;

    private String description;

    private List<String> steps;

    private List<String> requiredActions;

    private List<String> contactsToNotify;

    private List<String> resourcesNeeded;

    private Integer expectedResponseTime; // in minutes

    private Integer expectedResolutionTime; // in minutes

    private String escalationProcedure;

    private List<String> approvalRequired;

    private Map<String, Object> protocolMetadata;

    private LocalDateTime lastUpdated;

    private String updatedBy;

    private Boolean isActive;

    private String version;

    private List<String> complianceRequirements;

    private String trainingRequirements;

    private LocalDateTime nextReviewDate;
}

/**
 * Emergency report DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EmergencyReportDTO {

    private String id;

    private String reportTitle;

    private LocalDateTime reportPeriodStart;

    private LocalDateTime reportPeriodEnd;

    private EmergencyStatsDTO statistics;

    private List<Emergency> incidents;

    private List<String> keyFindings;

    private List<String> recommendations;

    private Map<String, Object> trendAnalysis;

    private Map<String, Object> performanceAnalysis;

    private List<String> correctiveActions;

    private String generatedBy;

    private LocalDateTime generatedAt;

    private String reportFormat; // PDF, Excel, etc.

    private List<String> recipients;

    private Boolean isConfidential;

    private String classification;

    private Map<String, Object> reportMetadata;
}

/**
 * Emergency drill DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EmergencyDrillDTO {

    private String id;

    private String drillName;

    private String description;

    private EmergencyType drillType;

    private LocalDateTime scheduledDate;

    private LocalDateTime actualStartTime;

    private LocalDateTime actualEndTime;

    private Integer duration; // in minutes

    private List<Long> participantIds;

    private List<String> objectives;

    private List<String> scenarios;

    private Map<String, Object> drillParameters;

    private List<String> evaluationCriteria;

    private Map<String, Object> results;

    private List<String> lessonsLearned;

    private List<String> improvements;

    private String drillStatus;

    private String conductedBy;

    private String evaluatedBy;

    private Double successScore; // percentage

    private List<String> recommendations;

    private LocalDateTime nextDrillDate;

    private Map<String, Object> drillMetadata;
}

/**
 * Emergency resource DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EmergencyResourceDTO {

    private String id;

    private String resourceName;

    private String resourceType;

    private String description;

    private String location;

    private Boolean isAvailable;

    private Integer capacity;

    private String contactPerson;

    private String contactNumber;

    private List<EmergencyType> applicableTypes;

    private Integer responseTime; // in minutes

    private Double costPerHour;

    private Map<String, Object> resourceMetadata;

    private LocalDateTime lastMaintenance;

    private LocalDateTime nextMaintenance;

    private String status;

    private List<String> certifications;

    private String specialRequirements;

    private Map<String, Object> availabilitySchedule;

    private String backupResource;

    private Double utilizationRate; // percentage

    private List<String> recentUsage;

    private Integer totalUsageCount;

    private Double averageUsageDuration; // in hours

    private String performanceRating;

    private List<String> userFeedback;

    private Map<String, Object> performanceMetrics;
}

/**
 * Emergency training DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EmergencyTrainingDTO {

    private String id;

    private String trainingName;

    private String description;

    private List<EmergencyType> coveredTypes;

    private LocalDateTime trainingDate;

    private Integer duration; // in hours

    private String instructor;

    private List<Long> participantIds;

    private List<String> trainingMaterials;

    private List<String> learningObjectives;

    private List<String> assessmentCriteria;

    private Map<String, Object> trainingMetadata;

    private String trainingStatus;

    private List<String> prerequisites;

    private String certificationIssued;

    private LocalDateTime certificationExpiry;

    private Double passRate; // percentage

    private List<String> improvements;

    private LocalDateTime nextTrainingDate;

    private String trainingMethod; // Online, In-person, Hybrid

    private Map<String, Object> evaluationResults;

    private List<String> feedback;

    private Double satisfactionScore; // percentage

    private String trainingLocation;

    private Double trainingCost;

    private String costBreakdown;

    private List<String> resourcesUsed;

    private Map<String, Object> roiAnalysis;

    private String complianceStatus;

    private List<String> complianceRequirements;

    private LocalDateTime nextReviewDate;
}

/**
 * Emergency equipment DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EmergencyEquipmentDTO {

    private String id;

    private String equipmentName;

    private String equipmentType;

    private String description;

    private String serialNumber;

    private String location;

    private Boolean isOperational;

    private LocalDateTime lastInspection;

    private LocalDateTime nextInspection;

    private String maintenanceStatus;

    private List<String> capabilities;

    private List<EmergencyType> applicableTypes;

    private Map<String, Object> equipmentMetadata;

    private String assignedTo;

    private LocalDateTime purchaseDate;

    private Double purchaseCost;

    private Double currentValue;

    private String warrantyExpiry;

    private String insuranceDetails;

    private List<String> maintenanceHistory;

    private List<String> usageHistory;

    private Double utilizationRate; // percentage

    private String performanceRating;

    private List<String> knownIssues;

    private String replacementSchedule;

    private String disposalMethod;

    private Map<String, Object> environmentalImpact;

    private List<String> safetyCertifications;

    private String operatingProcedures;

    private List<String> emergencyProcedures;

    private String trainingRequirements;

    private List<String> authorizedUsers;

    private Map<String, Object> performanceMetrics;

    private String status;

    private Boolean isCritical;

    private String backupEquipment;

    private Integer expectedLifespan; // in years

    private Integer currentAge; // in years

    private String condition;

    private List<String> spareParts;

    private String supplier;

    private String supplierContact;

    private Double maintenanceCostPerYear;

    private String energyConsumption;

    private Map<String, Object> technicalSpecifications;
}

/**
 * Emergency communication log DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EmergencyCommunicationLogDTO {

    private String id;

    private String emergencyId;

    private String communicationType; // Call, SMS, Email, Radio, etc.

    private String sender;

    private String recipient;

    private LocalDateTime timestamp;

    private String message;

    private String subject;

    private Integer duration; // for calls in seconds

    private String status; // Sent, Delivered, Failed, etc.

    private String priority;

    private Map<String, Object> communicationMetadata;

    private String responseRequired;

    private LocalDateTime responseDeadline;

    private String actualResponse;

    private LocalDateTime responseTimestamp;

    private Boolean isEscalation;

    private String escalationLevel;

    private String parentCommunicationId;

    private List<String> attachments;

    private String channel; // Primary, Secondary, Emergency

    private String deliveryConfirmation;

    private String readReceipt;

    private String acknowledgementRequired;

    private String actualAcknowledgement;

    private LocalDateTime acknowledgementTimestamp;

    private Map<String, Object> performanceMetrics;

    private String qualityRating;

    private List<String> issues;

    private String followUpRequired;

    private LocalDateTime followUpTimestamp;

    private String followUpAssignedTo;

    private Boolean isConfidential;

    private String classification;

    private String retentionPeriod;

    private LocalDateTime deletionDate;

    private Map<String, Object> complianceData;
}

/**
 * Emergency cost analysis DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EmergencyCostAnalysisDTO {

    private String id;

    private String emergencyId;

    private LocalDateTime analysisDate;

    private Double totalCost;

    private Map<String, Double> costBreakdown;

    private Double directCosts;

    private Double indirectCosts;

    private Double opportunityCosts;

    private Double recoveryCosts;

    private Double preventionCosts;

    private Double insuranceClaim;

    private Double outOfPocket;

    private String currency;

    private Map<String, Object> costMetadata;

    private List<String> costCategories;

    private Map<String, Double> categoryCosts;

    private Double budgetAllocated;

    private Double budgetVariance;

    private String varianceReason;

    private List<String> costSavingMeasures;

    private Double projectedSavings;

    private String costCenter;

    private String department;

    private String approvedBy;

    private LocalDateTime approvalDate;

    private String paymentStatus;

    private List<String> invoices;

    private Map<String, Object> financialMetrics;

    private Double roi;

    private String costEffectivenessRating;

    private List<String> recommendations;

    private LocalDateTime nextReviewDate;

    private Map<String, Object> trendAnalysis;

    private String benchmarkComparison;

    private Double industryAverage;

    private Double performanceIndex;

    private List<String> improvementAreas;

    private Map<String, Object> optimizationOpportunities;

    private String costReductionPlan;

    private Double targetReduction;

    private LocalDateTime targetDate;

    private String responsibility;

    private Map<String, Object> monitoringPlan;
}
