package com.edham.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentRequest {
    private Long customerId;
    private Long driverId;
    private String origin;
    private String destination;
    private String pickupAddress;
    private String deliveryAddress;
    private String recipientName;
    private String recipientPhone;
    private String packageDescription;
    private Double weight;
    private String dimensions;
    private Double value;
    private String specialInstructions;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentResponse {
    private Long id;
    private String trackingNumber;
    private Long customerId;
    private String customerName;
    private Long driverId;
    private String driverName;
    private String origin;
    private String destination;
    private String pickupAddress;
    private String deliveryAddress;
    private String recipientName;
    private String recipientPhone;
    private String packageDescription;
    private Double weight;
    private String dimensions;
    private Double value;
    private String specialInstructions;
    private String status;
    private String currentLocation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime pickupTime;
    private LocalDateTime deliveryTime;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentUpdateRequest {
    private Long driverId;
    private String origin;
    private String destination;
    private String pickupAddress;
    private String deliveryAddress;
    private String recipientName;
    private String recipientPhone;
    private String packageDescription;
    private Double weight;
    private String dimensions;
    private Double value;
    private String specialInstructions;
    private String status;
}
