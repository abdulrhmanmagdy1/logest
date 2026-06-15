package com.edham.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Mobile-specific Data Transfer Objects for real-time communications
 * Optimized for mobile applications with reduced payload
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileLocationUpdateDTO {
    private Double latitude;
    private Double longitude;
    private Double accuracy;
    private Double speed;
    private Double heading;
    private String vehicleId;
    private String status; // ACTIVE, IDLE, OFFLINE
    private Map<String, Object> metadata;
    
    // Mobile-specific fields
    private String deviceId;
    private String provider; // GPS, NETWORK, PASSIVE
    private Long timestamp; // Unix timestamp for mobile compatibility
    private Double batteryLevel;
    private Boolean isCharging;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileTrackingEventDTO {
    private TrackingEventType eventType;
    private String description;
    private LocationDTO location;
    private Map<String, Object> metadata;
    
    // Mobile-specific fields
    private String photoUrl; // For photo evidence
    private String signatureUrl; // For digital signatures
    private Boolean requiresAction; // If user needs to take action
    private String actionType; // Type of action required
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileNotificationDTO {
    private String id;
    private String title;
    private String message;
    private NotificationType type;
    private NotificationPriority priority;
    private LocalDateTime timestamp;
    private Map<String, Object> data;
    private Boolean requiresAction;
    private String actionUrl;
    private String deepLink; // For mobile deep linking
    private String imageUrl; // For rich notifications
    private String sound; // Custom notification sound
    private Integer badgeCount; // For app badge
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileShipmentDTO {
    private Long id;
    private String trackingNumber;
    private ShipmentStatus status;
    private AddressDTO pickupAddress;
    private AddressDTO deliveryAddress;
    private LocalDateTime createdAt;
    private LocalDateTime estimatedDelivery;
    private String specialInstructions;
    private ShipmentPriority priority;
    
    // Mobile-specific fields
    private String customerName;
    private String driverName;
    private String driverPhone;
    private String driverPhoto;
    private LocationDTO currentLocation;
    private Double progressPercentage; // Delivery progress
    private Boolean isDelayed;
    private String delayReason;
    private String estimatedDeliveryTime; // Human readable
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileDriverTaskDTO {
    private Long taskId;
    private Long shipmentId;
    private String trackingNumber;
    private TaskStatus status;
    private AddressDTO pickupLocation;
    private AddressDTO deliveryLocation;
    private LocalDateTime estimatedPickup;
    private LocalDateTime estimatedDelivery;
    
    // Mobile-specific fields
    private String customerName;
    private String customerPhone;
    private String specialInstructions;
    private Double distanceToPickup; // in meters
    private Double distanceToDelivery; // in meters
    private Integer estimatedTimeToPickup; // in minutes
    private Integer estimatedTimeToDelivery; // in minutes
    private String routeUrl; // Google Maps navigation URL
    private Boolean requiresSignature;
    private Boolean requiresPhoto;
    private Double cargoWeight;
    private String cargoType;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileFleetStatusDTO {
    private int totalVehicles;
    private int onlineVehicles;
    private int activeVehicles;
    private int maintenanceVehicles;
    private LocalDateTime timestamp;
    
    // Mobile-specific fields
    private Double averageSpeed; // Average speed of fleet
    private Double totalDistance; // Total distance traveled today
    private Integer activeTasks; // Number of active tasks
    private Integer completedTasks; // Number of completed tasks today
    private Integer delayedTasks; // Number of delayed tasks
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileEmergencyAlertDTO {
    private String id;
    private EmergencyAlertType type;
    private String title;
    private String message;
    private Long vehicleId;
    private String vehicleLicensePlate;
    private Long driverId;
    private String driverName;
    private LocationDTO location;
    private LocalDateTime timestamp;
    private AlertSeverity severity;
    
    // Mobile-specific fields
    private String emergencyContact; // Emergency contact number
    private Boolean requiresImmediateResponse;
    private String[] quickActions; // Quick action buttons
    private String audioAlert; // Type of audio alert
    private Integer priorityLevel; // 1-10 priority level
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileStatsDTO {
    private LocalDateTime timestamp;
    private int onlineUsers;
    private int onlineDrivers;
    private int activeShipments;
    private int pendingShipments;
    private int completedToday;
    
    // Mobile-specific fields
    private Double averageDeliveryTime; // Average delivery time today
    private Double onTimeDeliveryRate; // Percentage of on-time deliveries
    private Integer customerSatisfactionScore; // Customer satisfaction score
    private Double totalRevenue; // Total revenue today
    private Integer activeTasks; // Number of active tasks
    private Map<String, Integer> statusBreakdown; // Breakdown by status
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileConnectionInfoDTO {
    private Boolean success;
    private String message;
    private Long userId;
    private String username;
    private String role;
    private LocalDateTime timestamp;
    private Map<String, String> endpoints;
    
    // Mobile-specific fields
    private String serverVersion; // Server version
    private String websocketUrl; // WebSocket connection URL
    private String[] supportedFeatures; // Supported features
    private Integer maxConnections; // Maximum allowed connections
    private Integer reconnectInterval; // Reconnection interval in seconds
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileErrorDTO {
    private String code;
    private String message;
    private String details;
    private LocalDateTime timestamp;
    private String requestId; // For debugging
    private Map<String, Object> metadata;
    
    // Mobile-specific fields
    private Boolean isRetryable; // Can the operation be retried
    private Integer retryAfter; // Seconds to wait before retry
    private String[] suggestedActions; // Suggested user actions
    private String supportUrl; // Support link
}

// Mobile-specific enums
enum MobileNotificationChannel {
    SHIPMENT_UPDATES,
    DRIVER_LOCATION,
    PAYMENT_NOTIFICATIONS,
    SYSTEM_ALERTS,
    MARKETING_MESSAGES,
    EMERGENCY_ALERTS
}

enum MobileConnectionStatus {
    CONNECTING,
    CONNECTED,
    DISCONNECTED,
    RECONNECTING,
    ERROR
}

enum MobileDataSyncStatus {
    SYNCED,
    SYNCING,
    OFFLINE,
    ERROR
}

enum MobilePushNotificationType {
    SHIPMENT_STATUS,
    DRIVER_ARRIVED,
    DELIVERY_CONFIRMED,
    PAYMENT_RECEIVED,
    EMERGENCY_ALERT,
    SYSTEM_MAINTENANCE
}

// Supporting DTOs for mobile optimization
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileCompactLocationDTO {
    private Double lat; // Shortened for mobile
    private Double lng; // Shortened for mobile
    private Long ts; // Unix timestamp
    private Double acc; // Accuracy
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileCompactShipmentDTO {
    private Long id;
    private String tn; // Tracking number shortened
    private String st; // Status shortened
    private Long edt; // Estimated delivery timestamp
    private Double prog; // Progress percentage
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileCompactDriverDTO {
    private Long id;
    private String name;
    private String phone;
    private MobileCompactLocationDTO loc; // Current location
    private String status;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileSyncRequestDTO {
    private String deviceId;
    private Long lastSyncTimestamp;
    private String[] dataTypes; // Types of data to sync
    private Boolean forceSync; // Force full sync
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileSyncResponseDTO {
    private Boolean success;
    private String message;
    private Long syncTimestamp;
    private Map<String, Integer> updatedCounts; // Count of updated records per type
    private String[] conflicts; // Any sync conflicts
    private Long nextSyncIn; // Seconds until next sync
}
