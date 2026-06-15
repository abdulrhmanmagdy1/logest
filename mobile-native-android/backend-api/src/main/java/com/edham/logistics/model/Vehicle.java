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
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String licensePlate;
    private String vehicleType;

    @Enumerated(EnumType.STRING)
    private VehicleStatus status;

    private Long driverId;
    private Double fuelLevel;
    private Boolean maintenanceRequired;
    private LocalDateTime lastMaintenanceDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
