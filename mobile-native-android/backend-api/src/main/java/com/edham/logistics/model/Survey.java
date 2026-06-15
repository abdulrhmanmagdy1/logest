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
@Table(name = "surveys")
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long driverId;

    private String driverName;

    // specific scores 1-100 or 1-5
    private Integer workScheduleScore;
    private Integer vehicleQualityScore;
    private Integer maintenanceResponseScore;
    private Integer managementCommunicationScore;
    private Integer salarySatisfactionScore;
    private Integer safetyScore;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    private LocalDateTime createdAt;
}
