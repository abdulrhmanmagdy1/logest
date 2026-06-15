package com.edham.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // Extended fields for mobile parity
    private String cargoType;
    private Double weightKg;
    private Double temperatureCelsius;
    private String priority;
    private String notes;
    private Double insuranceValue;
    private String pickupCity;
    private String dropCity;
    private String pickupDate;
    private String pickupTime;
    private Integer pieceCount;
    private String vehicleType;
    private Double estimatedPrice;
    private Double pickupLat;
    private Double pickupLng;
    private Double dropLat;
    private Double dropLng;
    private java.util.List<String> photoUris;
}
