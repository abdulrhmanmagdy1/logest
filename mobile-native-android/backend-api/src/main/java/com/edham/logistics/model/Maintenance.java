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
@Table(name = "maintenance_records")
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long vehicleId;
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    private MaintenanceType type;

    @Enumerated(EnumType.STRING)
    private MaintenanceStatus status;

    @Enumerated(EnumType.STRING)
    private MaintenancePriority priority;

    private String title;
    private String description;
    
    private LocalDateTime scheduledDate;
    private LocalDateTime actualStartDate;
    private LocalDateTime completionDate;
    
    private Double estimatedCost;
    private Double actualCost;
    
    private String mechanicName;
    private String workshopName;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
