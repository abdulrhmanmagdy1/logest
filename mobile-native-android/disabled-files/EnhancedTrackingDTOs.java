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
 * Enhanced Data Transfer Objects for Advanced Tracking System
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EnhancedVehicleAnimationDTO {
    private Long vehicleId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<LocationDTO> trackingPoints;
    private AnimationMetadataDTO metadata;
    private AnimationConfigDTO config;
    private LocalDateTime generatedAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AnimationMetadataDTO {
    private Integer totalPoints;
    private Duration animationDuration;
    private Double averageSpeed;
    private Double maxSpeed;
    private Double totalDistance;
    private String smoothness; // LOW, MEDIUM, HIGH
    private Double frameRate;
    private Long estimatedMemoryUsage; // KB
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AnimationConfigDTO {
    private String interpolationLevel; // LOW, MEDIUM, HIGH
    private Integer targetFrameRate;
    private Boolean showTrail;
    private Integer trailLength;
    private Boolean smoothAcceleration;
    private Boolean showSpeedIndicator;
    private Boolean showHeadingIndicator;
    private Double playbackSpeed;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EnhancedRouteReplayDTO {
    private Long shipmentId;
    private String trackingNumber;
    private List<DetailedRoutePointDTO> routeHistory;
    private List<RouteSegmentDTO> segments;
    private RouteAnalyticsDTO analytics;
    private RoutePerformanceDTO performance;
    private ReplayConfigDTO config;
    private LocalDateTime generatedAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class DetailedRoutePointDTO extends LocationDTO {
    private Double gpsSignalStrength;
    private Integer satelliteCount;
    private Double batteryLevel;
    private String engineStatus;
    private Double fuelConsumption;
    private String roadType;
    private String trafficCondition;
    private String weatherCondition;
    private Integer signalQuality;
    private Boolean isStop;
    private Duration stopDuration;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RouteAnalyticsDTO {
    private Double totalDistance;
    private Double maxSpeed;
    private Double avgSpeed;
    private Double totalFuelConsumption;
    private Double averageFuelEfficiency;
    private Map<String, Integer> roadTypeDistribution;
    private Map<String, Integer> trafficDistribution;
    private Integer totalStops;
    private Duration totalStopTime;
    private Duration movingTime;
    private Double idlePercentage;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RouteSegmentDTO {
    private String segmentId;
    private LocationDTO startPoint;
    private LocationDTO endPoint;
    private Double distance;
    private Duration duration;
    private Double avgSpeed;
    private String roadType;
    private String trafficCondition;
    private String difficulty; // EASY, MODERATE, DIFFICULT
    private List<String> landmarks;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class RoutePerformanceDTO {
    private Double onTimePercentage;
    private Double efficiencyScore;
    private Double safetyScore;
    private Double fuelEfficiency;
    private Double overallPerformance;
    private List<String> performanceIssues;
    private List<String> recommendations;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ReplayConfigDTO {
    private String resolution; // LOW, MEDIUM, HIGH
    private Boolean showAnalytics;
    private Boolean showPerformance;
    private Boolean showSegments;
    private Boolean showTraffic;
    private Boolean showWeather;
    private Double playbackSpeed;
    private Boolean showStops;
    private Boolean showFuelConsumption;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AdvancedGeofencingDTO {
    private Long vehicleId;
    private LocationDTO currentLocation;
    private List<AdvancedGeofenceDTO> activeGeofences;
    private List<GeofenceViolationDTO> violations;
    private GeofenceStatisticsDTO statistics;
    private List<GeofencePredictionDTO> predictions;
    private GeofenceConfigDTO config;
    private LocalDateTime generatedAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AdvancedGeofenceDTO extends GeofenceDTO {
    private Boolean alertOnDwell;
    private Duration dwellTime;
    private Double speedLimit;
    private List<Integer> restrictedHours;
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL
    private Boolean enabled;
    private Map<String, Object> customRules;
    private List<String> allowedVehicleTypes;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class GeofenceViolationDTO {
    private Long vehicleId;
    private String geofenceId;
    private String geofenceName;
    private String violationType; // SPEED_LIMIT, RESTRICTED_HOURS, UNAUTHORIZED_ACCESS
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL
    private LocationDTO location;
    private Double distance;
    private Double actualSpeed;
    private Double speedLimit;
    private Integer restrictedHour;
    private LocalDateTime timestamp;
    private Boolean acknowledged;
    private String acknowledgedBy;
    private LocalDateTime acknowledgedAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class GeofenceStatisticsDTO {
    private Integer totalGeofences;
    private Integer activeViolations;
    private Integer violationsToday;
    private Integer violationsThisWeek;
    private Map<String, Integer> violationTypes;
    private Map<String, Integer> violationSeverity;
    private Double averageViolationDistance;
    private LocalDateTime lastViolation;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class GeofencePredictionDTO {
    private String geofenceId;
    private String geofenceName;
    private LocalDateTime predictedEntryTime;
    private LocalDateTime predictedExitTime;
    private Double probability;
    private String approachDirection;
    private Double estimatedSpeed;
    private Duration timeToEntry;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class GeofenceConfigDTO {
    private Boolean enablePredictions;
    private Integer predictionHorizonMinutes;
    private Boolean enableRealTimeAlerts;
    private Boolean enableHistoricalAnalysis;
    private List<String> alertTypes;
    private Double violationThreshold;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class OptimizedMultiVehicleTrackingDTO {
    private List<Long> vehicleIds;
    private List<EnhancedVehicleTrackingDTO> vehicleTrackingData;
    private List<VehicleClusterDTO> clusters;
    private FleetAnalyticsDTO fleetAnalytics;
    private TrackingPerformanceDTO performanceMetrics;
    private MapRenderingDTO mapRendering;
    private TrackingOptimizationDTO optimization;
    private LocalDateTime generatedAt;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EnhancedVehicleTrackingDTO extends VehicleTrackingInfoDTO {
    private Double gpsSignalStrength;
    private Double batteryLevel;
    private String engineStatus;
    private Boolean maintenanceRequired;
    private LocalDateTime lastMaintenanceDate;
    private Integer totalDistanceToday;
    private Double averageSpeedToday;
    private List<String> activeAlerts;
    private Map<String, Object> telemetryData;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class VehicleClusterDTO {
    private String clusterId;
    private Double latitude;
    private Double longitude;
    private Integer vehicleCount;
    private List<Long> vehicleIds;
    private Double clusterRadius;
    private Double density;
    private String clusterType; // DENSE, SPARSE, NORMAL
    private List<String> dominantVehicleTypes;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class FleetAnalyticsDTO {
    private Integer totalVehicles;
    private Integer activeVehicles;
    private Integer idleVehicles;
    private Integer maintenanceVehicles;
    private Double fleetUtilization;
    private Double averageSpeed;
    private Double averageFuelLevel;
    private Double totalDistanceToday;
    private Integer activeEmergencies;
    private Map<String, Integer> vehicleTypeDistribution;
    private Map<String, Integer> statusDistribution;
    private Double fleetEfficiency;
    private Double fleetSafetyScore;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class TrackingPerformanceDTO {
    private Double dataProcessingLatency;
    private Double mapRenderTime;
    private Double memoryUsage;
    private Double cpuUsage;
    private Integer processedPointsPerSecond;
    private Double networkLatency;
    private Integer activeWebSocketConnections;
    private Double cacheHitRate;
    private List<String> performanceBottlenecks;
    private List<String> optimizationRecommendations;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MapRenderingDTO {
    private Integer totalMarkers;
    private Integer clusteredMarkers;
    private Integer visibleMarkers;
    private Integer zoomLevel;
    private String renderingStrategy; // CLUSTER, INDIVIDUAL, HYBRID
    private Long estimatedRenderTime;
    private Double memoryFootprint;
    private List<MapLayerDTO> layers;
    private Map<String, Object> renderingOptions;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class MapLayerDTO {
    private String layerId;
    private String layerName;
    private String layerType; // VEHICLES, GEOFFENCES, ROUTES, CLUSTERS
    private Boolean visible;
    private Integer zIndex;
    private Double opacity;
    private Map<String, Object> styleOptions;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class TrackingOptimizationDTO {
    private Boolean enableClustering;
    private Integer clusterThreshold;
    private Boolean enableLevelOfDetail;
    private Integer maxVisibleMarkers;
    private Boolean enableCulling;
    private Double cullingDistance;
    private Boolean enableCompression;
    private String compressionLevel;
    private Integer updateFrequency; // milliseconds
    private Boolean enablePredictiveRendering;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class TrackingPerformanceMetricsDTO {
    private Integer activeConnections;
    private Integer activeVehicles;
    private Integer totalTrackingPoints;
    private Double averageUpdateFrequency;
    private Double dataTransferRate;
    private Double systemLatency;
    private Double memoryUsage;
    private Double performanceScore;
    private String status; // EXCELLENT, GOOD, NEEDS_IMPROVEMENT, CRITICAL
    private LocalDateTime generatedAt;
    private Map<String, Object> detailedMetrics;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EnhancedTrackingRequestDTO {
    private Long vehicleId;
    private Long shipmentId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AnimationConfigDTO animationConfig;
    private ReplayConfigDTO replayConfig;
    private GeofenceConfigDTO geofenceConfig;
    private TrackingOptimizationDTO optimization;
    private List<String> features; // ANIMATION, REPLAY, GEOFENCING, MULTI_VEHICLE
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class EnhancedTrackingResponseDTO {
    private Boolean success;
    private String trackingType;
    private Object trackingData;
    private TrackingPerformanceMetricsDTO performanceMetrics;
    private String message;
    private LocalDateTime timestamp;
    private String error;
}
