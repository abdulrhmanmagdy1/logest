package com.edham.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Objects for real-time WebSocket communications
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ShipmentStatusUpdateDTO {
    private Long shipmentId;
    private String trackingNumber;
    private ShipmentStatus oldStatus;
    private ShipmentStatus newStatus;
    private LocalDateTime timestamp;
    private String updatedBy;
    private String message;
    private Map<String, Object> metadata;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class LocationUpdateDTO {
    private Long driverId;
    private String driverName;
    private Double latitude;
    private Double longitude;
    private Double accuracy;
    private Double speed;
    private Double heading;
    private LocalDateTime timestamp;
    private String vehicleId;
    private String vehicleType;
    private String status;
    private Map<String, Object> metadata;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class NewShipmentDTO {
    private Long shipmentId;
    private String trackingNumber;
    private String customerName;
    private AddressDTO pickupAddress;
    private AddressDTO deliveryAddress;
    private ShipmentPriority priority;
    private LocalDateTime estimatedDelivery;
    private LocalDateTime createdAt;
    private String specialInstructions;
    private List<String> requiredSkills;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class TrackingEventDTO {
    private Long id;
    private Long shipmentId;
    private TrackingEventType eventType;
    private String description;
    private LocationDTO location;
    private LocalDateTime timestamp;
    private String createdBy;
    private Map<String, Object> metadata;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class PaymentStatusUpdateDTO {
    private Long invoiceId;
    private PaymentStatus status;
    private LocalDateTime timestamp;
    private String updatedBy;
    private String message;
    private Double amount;
    private String paymentMethod;
    private Map<String, Object> metadata;
}

// NotificationMessageDTO is now in NotificationMessageDTO.java

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class FleetStatusDTO {
    private int totalVehicles;
    private int onlineVehicles;
    private int activeVehicles;
    private int maintenanceVehicles;
    private int offlineVehicles;
    private LocalDateTime timestamp;
    private List<VehicleStatusDTO> vehicleStatuses;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class VehicleStatusDTO {
    private Long vehicleId;
    private String licensePlate;
    private String vehicleType;
    private VehicleStatus status;
    private LocationDTO currentLocation;
    private String driverName;
    private LocalDateTime lastSeen;
    private Double fuelLevel;
    private Boolean maintenanceRequired;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EmergencyAlertDTO {
    private String id;
    private com.edham.logistics.model.EmergencyType type;
    private String title;
    private String message;
    private Long vehicleId;
    private String vehicleLicensePlate;
    private Long driverId;
    private String driverName;
    private LocationDTO location;
    private LocalDateTime timestamp;
    private com.edham.logistics.model.EmergencySeverity severity;
    private Boolean requiresImmediateAction;
    private List<String> assignedRoles;
    private Map<String, Object> details;
    private Long shipmentId;
    private String shipmentTrackingNumber;
    private String customerName;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RealTimeStatsDTO {
    private LocalDateTime timestamp;
    private int onlineUsers;
    private int onlineDrivers;
    private int onlineCustomers;
    private int onlineSupervisors;
    private int activeShipments;
    private int pendingShipments;
    private int inTransitShipments;
    private int completedToday;
    private int delayedShipments;
    private Map<String, Integer> roleDistribution;
    private Map<String, Integer> statusDistribution;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class DriverTaskUpdateDTO {
    private Long taskId;
    private Long driverId;
    private String driverName;
    private Long shipmentId;
    private String trackingNumber;
    private TaskStatus status;
    private LocalDateTime timestamp;
    private String message;
    private LocationDTO pickupLocation;
    private LocationDTO deliveryLocation;
    private Map<String, Object> metadata;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class SystemMaintenanceDTO {
    private String id;
    private MaintenanceType type;
    private String title;
    private String message;
    private LocalDateTime scheduledStart;
    private LocalDateTime scheduledEnd;
    private Integer durationMinutes;
    private List<String> affectedServices;
    private List<String> targetRoles;
    private Boolean requiresLogout;
    private String severity;
}

// Supporting enums - moved to separate files for public access
// ShipmentStatus is now in ShipmentStatus.java

enum TrackingEventType {
    SHIPMENT_CREATED, PICKUP_SCHEDULED, PICKED_UP, IN_TRANSIT, 
    DELIVERY_ATTEMPTED, DELIVERED, DELAYED, EXCEPTION
}

enum PaymentStatus {
    PENDING, PROCESSING, COMPLETED, FAILED, REFUNDED, CANCELLED
}

enum NotificationType {
    INFO, SUCCESS, WARNING, ERROR, SYSTEM, SHIPMENT, PAYMENT, MAINTENANCE, EMERGENCY
}

enum NotificationPriority {
    LOW, NORMAL, HIGH, URGENT
}

// VehicleStatus is now in VehicleStatus.java

enum EmergencyAlertType {
    ACCIDENT, BREAKDOWN, THEFT, MEDICAL_EMERGENCY, VEHICLE_MALFUNCTION, DELAY
}

enum AlertSeverity {
    LOW, MEDIUM, HIGH, CRITICAL
}

enum TaskStatus {
    ASSIGNED, ACCEPTED, REJECTED, IN_PROGRESS, COMPLETED, CANCELLED
}

enum MaintenanceType {
    SCHEDULED, EMERGENCY, SECURITY, DATABASE, API, INFRASTRUCTURE
}

// Supporting DTOs
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AddressDTO {
    private String street;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private Double latitude;
    private Double longitude;
}

// LocationDTO is now in LocationDTO.java
