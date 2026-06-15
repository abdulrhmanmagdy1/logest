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
@Table(name = "emergencies")
public class Emergency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;
    private String severity;
    private String status;
    private String description;
    private String location;
    private Double latitude;
    private Double longitude;
    
    private Long reportedBy;
    private LocalDateTime reportedAt;
    private LocalDateTime resolvedAt;
    
    private Long driverId;
    private Long vehicleId;
    private Long shipmentId;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
