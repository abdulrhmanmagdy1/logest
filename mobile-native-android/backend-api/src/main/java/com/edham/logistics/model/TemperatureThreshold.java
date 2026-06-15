package com.edham.logistics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemperatureThreshold {
    private String productType;
    private Double minTemperature;
    private Double maxTemperature;
    private Double criticalMin;
    private Double criticalMax;
    private String unit;
    private String description;
}
