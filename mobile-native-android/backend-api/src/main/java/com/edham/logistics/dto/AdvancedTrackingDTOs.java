package com.edham.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Data Transfer Objects for Advanced Tracking System
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class VehicleAnimationDTO {
    private Long vehicleId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<LocationDTO> trackingPoints;
    private Duration animationDuration;
    private Boolean smoothTransitions;
    private LocalDateTime generatedAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RouteReplayDTO {
    private Long shipmentId;
    private String trackingNumber;
    private List<RoutePointDTO> routeHistory;
    private RouteStatisticsDTO statistics;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double totalDistance;
    private Duration estimatedTime;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RoutePointDTO {
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
    private Double speed;
    private Double heading;
    private Double altitude;
    private Double accuracy;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RouteStatisticsDTO {
    private Double totalDistance;
    private Duration totalDuration;
    private Double maxSpeed;
    private Double avgSpeed;
    private Integer totalStops;
    private Duration stopDuration;
    private Double fuelConsumed;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class GeofenceAlertDTO {
    private Long vehicleId;
    private String geofenceId;
    private String geofenceName;
    private String alertType; // ENTRY, EXIT, DWELL
    private LocationDTO location;
    private Double distance;
    private LocalDateTime timestamp;
    private String message;
    private Boolean acknowledged;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class GeofenceDTO {
    private String id;
    private String name;
    private Double latitude;
    private Double longitude;
    private Double radius;
    private String type; // CIRCLE, POLYGON
    private Boolean alertOnEntry;
    private Boolean alertOnExit;
    private Boolean alertOnDwell;
    private Duration dwellTime;
    private Map<String, Object> metadata;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MultiVehicleTrackingDTO {
    private List<Long> vehicleIds;
    private List<VehicleTrackingInfoDTO> vehicleTrackingInfo;
    private FleetStatisticsDTO fleetStatistics;
    private Integer totalVehicles;
    private Integer activeVehicles;
    private LocalDateTime generatedAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class VehicleTrackingInfoDTO {
    private Long vehicleId;
    private String licensePlate;
    private String vehicleType;
    private String status;
    private LocationDTO currentLocation;
    private Long driverId;
    private String driverName;
    private LocalDateTime lastUpdate;
    private Double speed;
    private Double heading;
    private Double fuelLevel;
    private Boolean maintenanceRequired;
    private String currentShipmentId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class FleetStatisticsDTO {
    private Integer totalVehicles;
    private Integer activeVehicles;
    private Integer inactiveVehicles;
    private Integer maintenanceVehicles;
    private Double averageSpeed;
    private Double averageFuelLevel;
    private Double fleetUtilization;
    private Double totalDistanceToday;
    private Integer activeEmergencies;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MapPerformanceDTO {
    private MapBoundsDTO bounds;
    private List<MapClusterDTO> clusters;
    private MapPerformanceMetricsDTO metrics;
    private List<String> optimizations;
    private LocalDateTime generatedAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MapBoundsDTO {
    private Double minLat;
    private Double maxLat;
    private Double minLon;
    private Double maxLon;
    private Double centerLat;
    private Double centerLon;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MapClusterDTO {
    private String clusterId;
    private Double latitude;
    private Double longitude;
    private Integer vehicleCount;
    private List<Long> vehicleIds;
    private Boolean isCluster;
    private Double expansionRadius;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MapPerformanceMetricsDTO {
    private Integer totalMarkers;
    private Integer clusteredMarkers;
    private Double clusterRatio;
    private Integer optimalZoom;
    private Long estimatedRenderTime; // milliseconds
    private Double memoryUsage; // MB
    private Boolean requiresClustering;
    private Boolean requiresOptimization;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class TrackingRequestDTO {
    private Long vehicleId;
    private Long shipmentId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean includeAnimation;
    private Boolean includeStatistics;
    private Boolean includeGeofencing;
    private MapBoundsDTO mapBounds;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AdvancedTrackingResponseDTO {
    private Boolean success;
    private String trackingType;
    private Object trackingData;
    private String message;
    private LocalDateTime timestamp;
    private String error;
}
