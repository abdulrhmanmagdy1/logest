package com.edham.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
