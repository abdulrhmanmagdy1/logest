package com.edham.logistics.dto;

import com.edham.logistics.model.Shipment;
import com.edham.logistics.model.Vehicle;
import com.edham.logistics.performance.SystemHealthStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Unified Data Transfer Objects for enterprise-grade system
 * Standardizes all API responses and requests across modules
 */
// UnifiedResponseDTO is now in UnifiedResponseDTO.java

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UnifiedShipmentRequestDTO {
    private String trackingNumber;
    private Long customerId;
    private Long driverId;
    private Long vehicleId;
    private String pickupAddress;
    private String deliveryAddress;
    private LocationDTO pickupLocation;
    private LocationDTO deliveryLocation;
    private ShipmentPriority priority;
    private LocalDateTime estimatedDelivery;
    private String instructions;
    private Map<String, Object> metadata;
    private String requestId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class DriverStatusUpdateDTO {
    private DriverStatus status;
    private String reason;
    private LocationDTO currentLocation;
    private Boolean available;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UnifiedReportRequestDTO {
    private String reportType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Map<String, Object> filters;
    private String format;
    private List<String> includeSections;
    private Map<String, Object> parameters;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UnifiedDashboardDTO {
    private Long totalShipments;
    private Long totalUsers;
    private Long totalVehicles;
    private Long activeEmergencies;
    private SystemHealthStatus systemHealth;
    private List<Map<String, Object>> recentActivities;
    private Map<String, Object> keyMetrics;
    private LocalDateTime lastUpdated;
    
    // Role-specific dashboard data
    private List<Shipment> assignedShipments;
    private List<Shipment> myShipments;
    private Vehicle myVehicle;
    private Double myEarnings;
    private Double myPerformance;
    private Map<String, Object> myProfile;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UnifiedAnalyticsDTO {
    private Map<String, Object> shipmentAnalytics;
    private Map<String, Object> driverAnalytics;
    private Map<String, Object> vehicleAnalytics;
    private Map<String, Object> revenueAnalytics;
    private Map<String, Object> performanceAnalytics;
    private Map<String, Object> systemAnalytics;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private String reportType;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UnifiedNotificationDTO {
    private String notificationId;
    private String type;
    private String title;
    private String message;
    private Long recipientId;
    private String recipientRole;
    private Map<String, Object> data;
    private LocalDateTime timestamp;
    private Boolean read;
    private String priority;
    private Map<String, Object> metadata;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UnifiedSearchRequestDTO {
    private String query;
    private String type; // SHIPMENT, DRIVER, VEHICLE, CUSTOMER
    private Map<String, Object> filters;
    private String sortBy;
    private String sortOrder;
    private Integer page;
    private Integer size;
    private Map<String, Object> metadata;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UnifiedSearchResponseDTO<T> {
    private List<T> results;
    private Long totalCount;
    private Integer page;
    private Integer size;
    private Integer totalPages;
    private Boolean hasNext;
    private Boolean hasPrevious;
    private Map<String, Object> aggregations;
    private LocalDateTime timestamp;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UnifiedConfigRequestDTO {
    private String configType;
    private String category;
    private Map<String, Object> config;
    private String description;
    private Boolean isGlobal;
    private String appliesToRole;
    private Map<String, Object> metadata;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UnifiedConfigResponseDTO {
    private String configId;
    private String configType;
    private String category;
    private Map<String, Object> config;
    private String description;
    private Boolean isGlobal;
    private String appliesToRole;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UnifiedAuditLogDTO {
    private String logId;
    private String userId;
    private String userRole;
    private String action;
    private String entityType;
    private String entityId;
    private String oldValues;
    private String newValues;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime timestamp;
    private String status;
    private String description;
    private Map<String, Object> metadata;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UnifiedBulkOperationDTO {
    private String operationId;
    private String operationType;
    private List<Map<String, Object>> items;
    private Map<String, Object> parameters;
    private Boolean validateBeforeExecution;
    private Boolean stopOnFirstError;
    private String requestId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UnifiedBulkOperationResponseDTO {
    private String operationId;
    private String operationType;
    private Integer totalItems;
    private Integer successfulItems;
    private Integer failedItems;
    private List<Map<String, Object>> errors;
    private List<Map<String, Object>> results;
    private Boolean completed;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Duration duration;
    private String requestId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UnifiedWebSocketMessageDTO {
    private String messageId;
    private String type;
    private String channel;
    private String targetRole;
    private Map<String, Object> data;
    private LocalDateTime timestamp;
    private String senderId;
    private String senderRole;
    private Boolean broadcast;
    private List<String> recipients;
    private Map<String, Object> metadata;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UnifiedFileOperationDTO {
    private String operationId;
    private String operationType; // UPLOAD, DOWNLOAD, DELETE
    private String fileName;
    private String fileType;
    private Long fileSize;
    private String path;
    private Map<String, Object> metadata;
    private String uploadedBy;
    private String requestId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UnifiedFileOperationResponseDTO {
    private String operationId;
    private String operationType;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private String status;
    private String message;
    private LocalDateTime timestamp;
    private String requestId;
    private Map<String, Object> metadata;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UnifiedSystemHealthDTO {
    private String systemId;
    private String version;
    private String status;
    private Double healthScore;
    private Map<String, Object> components;
    private Map<String, Object> metrics;
    private List<String> activeAlerts;
    private LocalDateTime lastCheck;
    private Long uptime;
    private Map<String, Object> performance;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UnifiedUserSessionDTO {
    private String sessionId;
    private String userId;
    private String userRole;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime loginTime;
    private LocalDateTime lastActivity;
    private Boolean isActive;
    private Map<String, Object> sessionData;
    private List<String> permissions;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UnifiedPermissionDTO {
    private String permissionId;
    private String name;
    private String description;
    private String resource;
    private String action;
    private List<String> roles;
    private Boolean isActive;
    private Map<String, Object> conditions;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class UnifiedRolePermissionDTO {
    private String roleId;
    private String roleName;
    private List<UnifiedPermissionDTO> permissions;
    private Map<String, Object> roleConfig;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private String createdBy;
}

// Enums for unified system
enum UnifiedOperationType {
    CREATE, UPDATE, DELETE, READ, SEARCH, EXPORT, IMPORT
}

enum UnifiedEntityType {
    SHIPMENT, DRIVER, VEHICLE, CUSTOMER, MAINTENANCE, EMERGENCY, USER, REPORT, CONFIG
}

enum UnifiedNotificationType {
    SHIPMENT, DRIVER, VEHICLE, MAINTENANCE, EMERGENCY, SYSTEM, ANNOUNCEMENT
}

enum UnifiedSearchType {
    SHIPMENT, DRIVER, VEHICLE, CUSTOMER, MAINTENANCE, EMERGENCY, USER, REPORT
}

enum UnifiedConfigType {
    SYSTEM, USER, VEHICLE, SHIPMENT, NOTIFICATION, REPORT, SECURITY, PERFORMANCE
}

enum UnifiedFileType {
    DOCUMENT, IMAGE, VIDEO, AUDIO, DATA, BACKUP, LOG
}

enum DriverStatus {
    AVAILABLE, BUSY, OFFLINE, ON_BREAK, DRIVING, UNAVAILABLE
}

enum ShipmentPriority {
    LOW, MEDIUM, HIGH, URGENT, CRITICAL
}




