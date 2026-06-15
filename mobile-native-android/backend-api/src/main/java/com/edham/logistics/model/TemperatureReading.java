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
@Table(name = "temperature_readings")
public class TemperatureReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long shipmentId;
    private Double temperature;
    private Double humidity;
    private String sensorId;
    private String location;
    private LocalDateTime timestamp;
    private String deviceId;
    private Integer batteryLevel;
    private Integer signalStrength;
    private String sessionId;
}
