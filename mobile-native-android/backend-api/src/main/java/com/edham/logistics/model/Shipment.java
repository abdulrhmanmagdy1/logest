package com.edham.logistics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String trackingNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private User driver;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;

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
    private String currentLocation;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime pickupTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime estimatedDelivery;
    private String specialInstructions;

    // Added for Cold Chain and Analytics
    private String coldChainStatus;
    private LocalDateTime actualDeliveryTime;
    private LocalDateTime expectedDeliveryTime;
    private String delayReason;
    private java.math.BigDecimal shippingCost;
    private String productType;
}
