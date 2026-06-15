package com.edham.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDTO {
    private Double latitude;
    private Double longitude;
    private String address;
    private Double accuracy;
    private LocalDateTime timestamp;
}
