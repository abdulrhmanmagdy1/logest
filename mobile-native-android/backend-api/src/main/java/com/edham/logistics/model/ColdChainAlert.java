package com.edham.logistics.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cold_chain_alerts")
public class ColdChainAlert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long shipmentId;
    private String sessionId;
    
    @Enumerated(EnumType.STRING)
    private AlertSeverity severity;
    
    @Enumerated(EnumType.STRING)
    private AlertType alertType;
    
    private Double temperature;
    
    @Transient
    private TemperatureThreshold threshold;
    
    @Enumerated(EnumType.STRING)
    private TemperatureStatus previousStatus;
    
    @Enumerated(EnumType.STRING)
    private TemperatureStatus newStatus;
    
    private String message;
    private LocalDateTime timestamp;
    private Boolean resolved;
    private LocalDateTime resolvedAt;
    private String productType;

    public enum AlertSeverity {
        INFO, WARNING, CRITICAL
    }

    public enum AlertType {
        TEMPERATURE_UPDATE, TEMPERATURE_WARNING, TEMPERATURE_CRITICAL, TEMPERATURE_NORMALIZED, SENSOR_OFFLINE
    }

    public enum TemperatureStatus {
        SAFE, WARNING, CRITICAL
    }
}
