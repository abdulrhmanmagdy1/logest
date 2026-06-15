package com.edham.logistics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColdChainSession {
    private String sessionId;
    private Long shipmentId;
    private String productType;
    private TemperatureThreshold threshold;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime lastUpdateTime;
    private boolean active;
    private Integer totalReadings;
    private Integer alertCount;
    private ColdChainAlert.TemperatureStatus currentStatus;
    private TemperatureReading lastReading;
    private Double lastTemperature;
    private Double averageTemperature;
    private Double minTemperature;
    private Double maxTemperature;

    public String getSessionId() {
        if (sessionId == null) {
            sessionId = "SESS-" + shipmentId + "-" + System.currentTimeMillis();
        }
        return sessionId;
    }
}
