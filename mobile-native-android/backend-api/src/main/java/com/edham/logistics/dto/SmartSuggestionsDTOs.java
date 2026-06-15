package com.edham.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Objects for Smart Suggestions System
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class DriverSuggestionDTO {
    private Long shipmentId;
    private List<DriverScoreDTO> suggestedDrivers;
    private Integer totalAvailableDrivers;
    private LocalDateTime calculatedAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class DriverScoreDTO {
    private Long driverId;
    private String driverName;
    private Double score;
    private List<String> reasons;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RouteSuggestionDTO {
    private Long shipmentId;
    private List<RouteOptionDTO> routeOptions;
    private RouteOptionDTO recommendedRoute;
    private LocalDateTime calculatedAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RouteOptionDTO {
    private String routeId;
    private String name;
    private Duration estimatedTime;
    private Double estimatedDistance;
    private Double estimatedCost;
    private String trafficLevel;
    private Boolean recommended;
    private List<String> waypoints;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class PricingRequestDTO {
    private Double distance;
    private Double weight;
    private String priority;
    private String vehicleType;
    private String pickupAddress;
    private String deliveryAddress;
    private LocalDateTime requestedDeliveryTime;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class PricingSuggestionDTO {
    private Double basePrice;
    private Double finalPrice;
    private Map<String, Double> modifiers;
    private Duration estimatedDeliveryTime;
    private Double confidenceLevel;
    private LocalDateTime calculatedAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class DelayedShipmentDTO {
    private Long shipmentId;
    private String trackingNumber;
    private LocalDateTime estimatedDelivery;
    private Duration currentDelay;
    private String delayReason;
    private List<String> recommendedActions;
    private LocalDateTime detectedAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class SmartSuggestionRequestDTO {
    private String suggestionType; // DRIVER, ROUTE, PRICING, DELAY_DETECTION
    private Long shipmentId;
    private PricingRequestDTO pricingRequest;
    private Map<String, Object> parameters;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class SmartSuggestionResponseDTO {
    private Boolean success;
    private String suggestionType;
    private Object suggestionData;
    private String message;
    private LocalDateTime timestamp;
    private String error;
}

