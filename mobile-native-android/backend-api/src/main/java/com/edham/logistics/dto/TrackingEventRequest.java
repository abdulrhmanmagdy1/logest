package com.edham.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingEventRequest {
    private Long shipmentId;
    private String eventType;
    private String description;
    private String location;
    private Double latitude;
    private Double longitude;
}
