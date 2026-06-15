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
public class ColdChainStatistics {
    private Long shipmentId;
    private Integer totalReadings;
    private Long sessionDuration;
    private Double averageTemperature;
    private Double minTemperature;
    private Double maxTemperature;
    private Double temperatureVariance;
    private Integer alertCount;
    private Double safePercentage;
    private Double warningPercentage;
    private Double criticalPercentage;
    private LocalDateTime lastUpdateTime;
}
