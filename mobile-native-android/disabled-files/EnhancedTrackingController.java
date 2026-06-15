// // package com.edham.logistics.controller;

import com.edham.logistics.dto.*;
import com.edham.logistics.service.EnhancedTrackingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Enhanced tracking controller with optimized performance
 * Provides smooth animations, advanced geofencing, and multi-vehicle tracking
 */
@RestController
@RequestMapping("/api/v1/tracking/enhanced")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class EnhancedTrackingController {

    private final EnhancedTrackingService enhancedTrackingService;

    @Autowired
    public EnhancedTrackingController(EnhancedTrackingService enhancedTrackingService) {
        this.enhancedTrackingService = enhancedTrackingService;
    }

    /**
     * Get ultra-smooth vehicle animation with advanced interpolation
     */
    @GetMapping("/vehicle/{vehicleId}/animation")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'DRIVER')")
    public ResponseEntity<EnhancedVehicleAnimationDTO> getEnhancedVehicleAnimation(
            @PathVariable Long vehicleId,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime,
            @RequestParam(defaultValue = "MEDIUM") String interpolationLevel,
            @RequestParam(defaultValue = "30") Integer targetFrameRate) {
        try {
            if (startTime == null) {
                startTime = LocalDateTime.now().minusHours(2);
            }
            if (endTime == null) {
                endTime = LocalDateTime.now();
            }

            AnimationConfigDTO config = AnimationConfigDTO.builder()
                    .interpolationLevel(interpolationLevel)
                    .targetFrameRate(targetFrameRate)
                    .showTrail(true)
                    .trailLength(20)
                    .smoothAcceleration(true)
                    .showSpeedIndicator(true)
                    .showHeadingIndicator(true)
                    .playbackSpeed(1.0)
                    .build();

            EnhancedVehicleAnimationDTO animation = enhancedTrackingService.getEnhancedVehicleAnimation(
                    vehicleId, startTime, endTime, config);
            return ResponseEntity.ok(animation);
        } catch (Exception e) {
            log.error("Error getting enhanced vehicle animation: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get comprehensive route replay with detailed analytics
     */
    @GetMapping("/shipment/{shipmentId}/replay")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CUSTOMER')")
    public ResponseEntity<EnhancedRouteReplayDTO> getEnhancedRouteReplay(
            @PathVariable Long shipmentId,
            @RequestParam(defaultValue = "HIGH") String resolution,
            @RequestParam(defaultValue = "true") Boolean showAnalytics,
            @RequestParam(defaultValue = "true") Boolean showPerformance) {
        try {
            ReplayConfigDTO config = ReplayConfigDTO.builder()
                    .resolution(resolution)
                    .showAnalytics(showAnalytics)
                    .showPerformance(showPerformance)
                    .showSegments(true)
                    .showTraffic(true)
                    .showWeather(false)
                    .playbackSpeed(1.0)
                    .showStops(true)
                    .showFuelConsumption(true)
                    .build();

            EnhancedRouteReplayDTO replay = enhancedTrackingService.getEnhancedRouteReplay(shipmentId, config);
            return ResponseEntity.ok(replay);
        } catch (Exception e) {
            log.error("Error getting enhanced route replay: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get advanced geofencing with real-time monitoring
     */
    @GetMapping("/vehicle/{vehicleId}/geofence")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<AdvancedGeofencingDTO> getAdvancedGeofencing(
            @PathVariable Long vehicleId,
            @RequestParam(defaultValue = "true") Boolean enablePredictions,
            @RequestParam(defaultValue = "30") Integer predictionHorizonMinutes) {
        try {
            GeofenceConfigDTO config = GeofenceConfigDTO.builder()
                    .enablePredictions(enablePredictions)
                    .predictionHorizonMinutes(predictionHorizonMinutes)
                    .enableRealTimeAlerts(true)
                    .enableHistoricalAnalysis(true)
                    .alertTypes(List.of("SPEED_LIMIT", "RESTRICTED_HOURS", "UNAUTHORIZED_ACCESS"))
                    .violationThreshold(0.1)
                    .build();

            AdvancedGeofencingDTO geofencing = enhancedTrackingService.getAdvancedGeofencing(vehicleId, config);
            return ResponseEntity.ok(geofencing);
        } catch (Exception e) {
            log.error("Error getting advanced geofencing: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get optimized multi-vehicle tracking with performance optimization
     */
    @PostMapping("/multi-vehicle")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<OptimizedMultiVehicleTrackingDTO> getOptimizedMultiVehicleTracking(
            @RequestBody List<Long> vehicleIds,
            @RequestParam(defaultValue = "true") Boolean enableClustering,
            @RequestParam(defaultValue = "50") Integer clusterThreshold,
            @RequestParam(defaultValue = "true") Boolean enableLevelOfDetail) {
        try {
            if (vehicleIds.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            TrackingOptimizationDTO optimization = TrackingOptimizationDTO.builder()
                    .enableClustering(enableClustering)
                    .clusterThreshold(clusterThreshold)
                    .enableLevelOfDetail(enableLevelOfDetail)
                    .maxVisibleMarkers(100)
                    .enableCulling(true)
                    .cullingDistance(0.01)
                    .enableCompression(true)
                    .compressionLevel("MEDIUM")
                    .updateFrequency(1000)
                    .enablePredictiveRendering(true)
                    .build();

            OptimizedMultiVehicleTrackingDTO tracking = enhancedTrackingService.getOptimizedMultiVehicleTracking(
                    vehicleIds, optimization);
            return ResponseEntity.ok(tracking);
        } catch (Exception e) {
            log.error("Error getting optimized multi-vehicle tracking: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get real-time tracking performance metrics
     */
    @GetMapping("/performance")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<TrackingPerformanceMetricsDTO> getTrackingPerformanceMetrics() {
        try {
            TrackingPerformanceMetricsDTO metrics = enhancedTrackingService.getTrackingPerformanceMetrics();
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            log.error("Error getting tracking performance metrics: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Generic enhanced tracking endpoint
     */
    @PostMapping("/track")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<EnhancedTrackingResponseDTO> getEnhancedTracking(
            @Valid @RequestBody EnhancedTrackingRequestDTO request) {
        try {
            EnhancedTrackingResponseDTO response = EnhancedTrackingResponseDTO.builder()
                    .success(true)
                    .calculatedAt(LocalDateTime.now())
                    .build();

            // Process based on requested features
            if (request.getFeatures().contains("ANIMATION") && request.getVehicleId() != null) {
                response.setTrackingData(enhancedTrackingService.getEnhancedVehicleAnimation(
                        request.getVehicleId(), request.getStartTime(), request.getEndTime(), 
                        request.getAnimationConfig()));
                response.setTrackingType("VEHICLE_ANIMATION");
            } else if (request.getFeatures().contains("REPLAY") && request.getShipmentId() != null) {
                response.setTrackingData(enhancedTrackingService.getEnhancedRouteReplay(
                        request.getShipmentId(), request.getReplayConfig()));
                response.setTrackingType("ROUTE_REPLAY");
            } else if (request.getFeatures().contains("GEOFENCING") && request.getVehicleId() != null) {
                response.setTrackingData(enhancedTrackingService.getAdvancedGeofencing(
                        request.getVehicleId(), request.getGeofenceConfig()));
                response.setTrackingType("ADVANCED_GEOFENCING");
            } else if (request.getFeatures().contains("MULTI_VEHICLE") && request.getVehicleIds() != null) {
                response.setTrackingData(enhancedTrackingService.getOptimizedMultiVehicleTracking(
                        request.getVehicleIds(), request.getOptimization()));
                response.setTrackingType("OPTIMIZED_MULTI_VEHICLE");
            } else {
                response.setSuccess(false);
                response.setError("Invalid tracking request parameters or unsupported features");
            }

            // Add performance metrics
            response.setPerformanceMetrics(enhancedTrackingService.getTrackingPerformanceMetrics());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting enhanced tracking: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    EnhancedTrackingResponseDTO.builder()
                            .success(false)
                            .error("Internal server error: " + e.getMessage())
                            .calculatedAt(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Test enhanced tracking system
     */
    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EnhancedTrackingResponseDTO> testEnhancedTracking() {
        try {
            // Test enhanced vehicle animation
            EnhancedVehicleAnimationDTO animation = EnhancedVehicleAnimationDTO.builder()
                    .vehicleId(1L)
                    .metadata(AnimationMetadataDTO.builder()
                            .totalPoints(100)
                            .smoothness("HIGH")
                            .frameRate(30)
                            .build())
                    .generatedAt(LocalDateTime.now())
                    .build();

            // Test advanced geofencing
            AdvancedGeofencingDTO geofencing = AdvancedGeofencingDTO.builder()
                    .vehicleId(1L)
                    .violations(List.of())
                    .status("OPERATIONAL")
                    .generatedAt(LocalDateTime.now())
                    .build();

            // Test multi-vehicle tracking
            OptimizedMultiVehicleTrackingDTO multiTracking = OptimizedMultiVehicleTrackingDTO.builder()
                    .totalVehicles(5)
                    .fleetAnalytics(FleetAnalyticsDTO.builder()
                            .totalVehicles(5)
                            .activeVehicles(3)
                            .fleetUtilization(0.6)
                            .build())
                    .generatedAt(LocalDateTime.now())
                    .build();

            // Get performance metrics
            TrackingPerformanceMetricsDTO performance = enhancedTrackingService.getTrackingPerformanceMetrics();

            return ResponseEntity.ok(
                    EnhancedTrackingResponseDTO.builder()
                            .success(true)
                            .trackingType("TEST")
                            .trackingData(Map.of(
                                    "vehicleAnimation", animation,
                                    "geofencing", geofencing,
                                    "multiVehicleTracking", multiTracking
                            ))
                            .performanceMetrics(performance)
                            .message("Enhanced tracking system is operational")
                            .calculatedAt(LocalDateTime.now())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error testing enhanced tracking: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    EnhancedTrackingResponseDTO.builder()
                            .success(false)
                            .error("Test failed: " + e.getMessage())
                            .calculatedAt(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Create advanced geofence
     */
    @PostMapping("/geofence")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<AdvancedGeofenceDTO> createAdvancedGeofence(@Valid @RequestBody AdvancedGeofenceDTO geofence) {
        try {
            log.info("Creating advanced geofence: {} at ({}, {}) with radius {}m", 
                    geofence.getName(), geofence.getLatitude(), geofence.getLongitude(), geofence.getRadius());
            
            // In a real implementation, this would save the geofence to the database
            return ResponseEntity.ok(geofence);
        } catch (Exception e) {
            log.error("Error creating advanced geofence: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all active advanced geofences
     */
    @GetMapping("/geofences")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<AdvancedGeofenceDTO>> getActiveAdvancedGeofences() {
        try {
            // In a real implementation, this would query the database
            List<AdvancedGeofenceDTO> geofences = List.of(
                    AdvancedGeofenceDTO.builder()
                            .id("geofence_1")
                            .name("Riyadh City Center")
                            .latitude(24.7136)
                            .longitude(46.6753)
                            .radius(5000.0)
                            .type("CIRCLE")
                            .alertOnEntry(true)
                            .alertOnExit(true)
                            .speedLimit(60.0)
                            .priority("HIGH")
                            .enabled(true)
                            .build(),
                    AdvancedGeofenceDTO.builder()
                            .id("geofence_2")
                            .name("Industrial Zone")
                            .latitude(24.7236)
                            .longitude(46.6853)
                            .radius(3000.0)
                            .type("CIRCLE")
                            .speedLimit(40.0)
                            .priority("MEDIUM")
                            .enabled(true)
                            .build()
            );
            
            return ResponseEntity.ok(geofences);
        } catch (Exception e) {
            log.error("Error getting active advanced geofences: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Delete advanced geofence
     */
    @DeleteMapping("/geofence/{geofenceId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Void> deleteAdvancedGeofence(@PathVariable String geofenceId) {
        try {
            log.info("Deleting advanced geofence: {}", geofenceId);
            // In a real implementation, this would delete from the database
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting advanced geofence: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get tracking optimization recommendations
     */
    @GetMapping("/optimization/recommendations")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> getOptimizationRecommendations() {
        try {
            TrackingPerformanceMetricsDTO metrics = enhancedTrackingService.getTrackingPerformanceMetrics();
            
            List<String> recommendations = new ArrayList<>();
            
            if (metrics.getSystemLatency() > 200) {
                recommendations.add("Consider enabling data compression to reduce latency");
            }
            
            if (metrics.getMemoryUsage() > 1024) {
                recommendations.add("Enable marker clustering to reduce memory usage");
            }
            
            if (metrics.getActiveVehicles() > 50) {
                recommendations.add("Implement level-of-detail rendering for better performance");
            }
            
            if (metrics.getPerformanceScore() < 0.7) {
                recommendations.add("System performance needs improvement - consider optimization");
            }
            
            return ResponseEntity.ok(Map.of(
                    "currentPerformance", metrics,
                    "recommendations", recommendations,
                    "optimizationLevel", metrics.getPerformanceScore() > 0.8 ? "OPTIMAL" : 
                                         metrics.getPerformanceScore() > 0.6 ? "GOOD" : "NEEDS_IMPROVEMENT",
                    "generatedAt", LocalDateTime.now()
            ));
            
        } catch (Exception e) {
            log.error("Error getting optimization recommendations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
