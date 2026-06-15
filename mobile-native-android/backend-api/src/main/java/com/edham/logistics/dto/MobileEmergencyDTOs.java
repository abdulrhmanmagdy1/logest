package com.edham.logistics.dto;

import com.edham.logistics.model.EmergencySeverity;
import com.edham.logistics.model.EmergencyStatus;
import com.edham.logistics.model.EmergencyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Mobile-specific Data Transfer Objects for Emergency System
 * Optimized for mobile payloads and real-time communication
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileEmergencyRequestDTO {
    private Long driverId;
    private Long shipmentId;
    private Long vehicleId;
    private EmergencyType emergencyType;
    private EmergencySeverity severity;
    private String description;
    private LocationDTO location;
    private Boolean requiresImmediateAction;
    private Map<String, Object> metadata;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileEmergencyResponseDTO {
    private Boolean success;
    private String emergencyId;
    private String message;
    private LocalDateTime timestamp;
    private String error;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileEmergencyButtonDTO {
    private Long driverId;
    private Long vehicleId;
    private Long shipmentId;
    private LocationDTO location;
    private String triggeredAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileEmergencyStatusDTO {
    private String emergencyId;
    private EmergencyStatus status;
    private String resolution;
    private LocalDateTime resolvedAt;
    private String resolvedBy;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileEmergencyHistoryDTO {
    private String emergencyId;
    private EmergencyType type;
    private EmergencySeverity severity;
    private EmergencyStatus status;
    private Long driverId;
    private String driverName;
    private Long shipmentId;
    private String trackingNumber;
    private LocationDTO location;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
    private String resolution;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileEmergencyContactDTO {
    private String name;
    private String phoneNumber;
    private String email;
    private String role;
    private Boolean isPrimary;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileEmergencyConfigDTO {
    private Boolean emergencyButtonEnabled;
    private List<MobileEmergencyContactDTO> emergencyContacts;
    private Integer autoNotifyDelaySeconds;
    private Boolean autoLocationEnabled;
    private Boolean soundAlertEnabled;
    private Boolean vibrationAlertEnabled;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class DriverEmergencyButtonConfigDTO {
    private Long driverId;
    private Boolean buttonEnabled;
    private Integer cooldownSeconds;
    private LocalDateTime lastPressedAt;
    private Integer maxPressesPerHour;
    private Boolean requiresConfirmation;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MobileEmergencyStatsDTO {
    private Long totalEmergencies;
    private Long resolvedEmergencies;
    private Long activeEmergencies;
    private Long driverEmergencies;
    private Double averageResponseTimeMinutes;
    private List<MobileEmergencyHistoryDTO> recentEmergencies;
}

