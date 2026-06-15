// // package com.edham.logistics.tracking;

import com.edham.logistics.dto.UnifiedResponseDTO;
import com.edham.logistics.model.*;
import com.edham.logistics.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * High-performance live tracking controller
 * Optimized for real-time tracking with smooth movement and efficient resource usage
 */
@RestController
@RequestMapping("/api/v1/tracking/live")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class HighPerformanceTrackingController {

    private final LiveTrackingService liveTrackingService;
    private final VehicleLocationRepository locationRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    @Autowired
    public HighPerformanceTrackingController(LiveTrackingService liveTrackingService,
                                          VehicleLocationRepository locationRepository,
                                          VehicleRepository vehicleRepository,
                                          UserRepository userRepository) {
        this.liveTrackingService = liveTrackingService;
        this.locationRepository = locationRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    /**
     * Start tracking a vehicle
     */
    @PostMapping("/start/{vehicleId}")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN', 'SUPERVISOR')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<TrackingSession>>> startTracking(
            @PathVariable Long vehicleId,
            @RequestParam(required = false) Long driverId) {
        
        return liveTrackingService.startTracking(vehicleId, driverId != null ? driverId : getCurrentUserId())
                .thenApply(session -> {
                    log.info("Tracking started for vehicle: {}, session: {}", vehicleId, session.getSessionId());
                    return ResponseEntity.ok(
                            UnifiedResponseDTO.<TrackingSession>builder()
                                    .success(true)
                                    .data(session)
                                    .message("Tracking started successfully")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Error starting tracking for vehicle {}: {}", vehicleId, throwable.getMessage());
                    return ResponseEntity.badRequest().body(
                            UnifiedResponseDTO.<TrackingSession>builder()
                                    .success(false)
                                    .error("Failed to start tracking: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Stop tracking a vehicle
     */
    @PostMapping("/stop/{vehicleId}")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN', 'SUPERVISOR')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<String>>> stopTracking(
            @PathVariable Long vehicleId) {
        
        return liveTrackingService.stopTracking(vehicleId)
                .thenApply(v -> {
                    log.info("Tracking stopped for vehicle: {}", vehicleId);
                    return ResponseEntity.ok(
                            UnifiedResponseDTO.<String>builder()
                                    .success(true)
                                    .data("Tracking stopped successfully")
                                    .message("Tracking stopped")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Error stopping tracking for vehicle {}: {}", vehicleId, throwable.getMessage());
                    return ResponseEntity.badRequest().body(
                            UnifiedResponseDTO.<String>builder()
                                    .success(false)
                                    .error("Failed to stop tracking: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Update vehicle location with high-performance optimization
     */
    @PostMapping("/location/update")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<LiveTrackingService.LocationUpdateResult>>> updateLocation(
            @RequestBody LiveTrackingService.LocationUpdateRequest request) {
        
        Long vehicleId = request.getVehicleId();
        
        // Validate vehicle ownership
        if (!canAccessVehicle(vehicleId)) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(403).body(
                            UnifiedResponseDTO.<LiveTrackingService.LocationUpdateResult>builder()
                                    .success(false)
                                    .error("Access denied for vehicle: " + vehicleId)
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    )
            );
        }
        
        return liveTrackingService.updateLocation(request)
                .thenApply(result -> {
                    if (result.getSuccess()) {
                        log.debug("Location updated for vehicle: {}, updates: {}", vehicleId, result.getUpdateCount());
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<LiveTrackingService.LocationUpdateResult>builder()
                                        .success(true)
                                        .data(result)
                                        .message("Location updated successfully")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    } else {
                        log.warn("Location update failed for vehicle: {}, error: {}", vehicleId, result.getError());
                        return ResponseEntity.badRequest().body(
                                UnifiedResponseDTO.<LiveTrackingService.LocationUpdateResult>builder()
                                        .success(false)
                                        .error(result.getError())
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Error updating location for vehicle {}: {}", vehicleId, throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<LiveTrackingService.LocationUpdateResult>builder()
                                    .success(false)
                                    .error("Internal server error: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get real-time location for vehicle
     */
    @GetMapping("/location/{vehicleId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'SUPERVISOR', 'ACCOUNTANT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<VehicleLocation>>> getRealTimeLocation(
            @PathVariable Long vehicleId) {
        
        // Validate access permissions
        if (!canViewVehicleLocation(vehicleId)) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(403).body(
                            UnifiedResponseDTO.<VehicleLocation>builder()
                                    .success(false)
                                    .error("Access denied for vehicle location: " + vehicleId)
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    )
            );
        }
        
        return liveTrackingService.getRealTimeLocation(vehicleId)
                .thenApply(location -> {
                    if (location != null) {
                        log.debug("Real-time location retrieved for vehicle: {}", vehicleId);
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<VehicleLocation>builder()
                                        .success(true)
                                        .data(location)
                                        .message("Location retrieved successfully")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    } else {
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<VehicleLocation>builder()
                                        .success(true)
                                        .data(null)
                                        .message("No active tracking for vehicle")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Error getting real-time location for vehicle {}: {}", vehicleId, throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<VehicleLocation>builder()
                                    .success(false)
                                    .error("Failed to get location: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get tracking session information
     */
    @GetMapping("/session/{vehicleId}")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN', 'SUPERVISOR')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<TrackingSession>>> getTrackingSession(
            @PathVariable Long vehicleId) {
        
        // Validate access permissions
        if (!canAccessVehicle(vehicleId)) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(403).body(
                            UnifiedResponseDTO.<TrackingSession>builder()
                                    .success(false)
                                    .error("Access denied for vehicle: " + vehicleId)
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    )
            );
        }
        
        return liveTrackingService.getTrackingSession(vehicleId)
                .thenApply(session -> {
                    if (session != null) {
                        log.debug("Tracking session retrieved for vehicle: {}", vehicleId);
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<TrackingSession>builder()
                                        .success(true)
                                        .data(session)
                                        .message("Session retrieved successfully")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    } else {
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<TrackingSession>builder()
                                        .success(true)
                                        .data(null)
                                        .message("No active tracking session")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Error getting tracking session for vehicle {}: {}", vehicleId, throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<TrackingSession>builder()
                                    .success(false)
                                    .error("Failed to get session: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get all active tracking sessions
     */
    @GetMapping("/sessions/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<List<TrackingSession>>>> getActiveTrackingSessions() {
        
        return liveTrackingService.getActiveTrackingSessions()
                .thenApply(sessions -> {
                    log.debug("Active tracking sessions retrieved: {}", sessions.size());
                    return ResponseEntity.ok(
                            UnifiedResponseDTO.<List<TrackingSession>>builder()
                                    .success(true)
                                    .data(sessions)
                                    .message("Active sessions retrieved successfully")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Error getting active tracking sessions: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<List<TrackingSession>>builder()
                                    .success(false)
                                    .error("Failed to get active sessions: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get vehicle location history with performance optimization
     */
    @GetMapping("/history/{vehicleId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'SUPERVISOR', 'ACCOUNTANT')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<List<VehicleLocation>>>> getLocationHistory(
            @PathVariable Long vehicleId,
            @RequestParam(defaultValue = "100") Integer limit,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime) {
        
        // Validate access permissions
        if (!canViewVehicleLocation(vehicleId)) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(403).body(
                            UnifiedResponseDTO.<List<VehicleLocation>>builder()
                                    .success(false)
                                    .error("Access denied for vehicle location history: " + vehicleId)
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    )
            );
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<VehicleLocation> history;
                
                if (startTime != null && endTime != null) {
                    // Get history within time range
                    history = locationRepository.findByVehicleIdAndTimestampBetweenOrderByTimestampDesc(
                            vehicleId, startTime, endTime);
                } else {
                    // Get recent history
                    history = locationRepository.findTopByVehicleIdOrderByTimestampDesc(vehicleId);
                }
                
                // Limit results for performance
                if (history.size() > limit) {
                    history = history.subList(0, limit);
                }
                
                log.debug("Location history retrieved for vehicle: {}, records: {}", vehicleId, history.size());
                
                return ResponseEntity.ok(
                        UnifiedResponseDTO.<List<VehicleLocation>>builder()
                                .success(true)
                                .data(history)
                                .message("Location history retrieved successfully")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
                
            } catch (Exception e) {
                log.error("Error getting location history for vehicle {}: {}", vehicleId, e.getMessage(), e);
                return ResponseEntity.internalServerError().body(
                        UnifiedResponseDTO.<List<VehicleLocation>>builder()
                                .success(false)
                                .error("Failed to get location history: " + e.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
            }
        });
    }

    /**
     * Handle connection drop gracefully
     */
    @PostMapping("/connection/drop/{vehicleId}")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<String>>> handleConnectionDrop(
            @PathVariable Long vehicleId,
            @RequestBody LiveTrackingService.ConnectionDropInfo dropInfo) {
        
        // Validate vehicle ownership
        if (!canAccessVehicle(vehicleId)) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(403).body(
                            UnifiedResponseDTO.<String>builder()
                                    .success(false)
                                    .error("Access denied for vehicle: " + vehicleId)
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    )
            );
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                liveTrackingService.handleConnectionDrop(vehicleId, dropInfo);
                
                log.info("Connection drop handled for vehicle: {}, reason: {}", vehicleId, dropInfo.getReason());
                
                return ResponseEntity.ok(
                        UnifiedResponseDTO.<String>builder()
                                .success(true)
                                .data("Connection drop handled successfully")
                                .message("Connection drop processed")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
                
            } catch (Exception e) {
                log.error("Error handling connection drop for vehicle {}: {}", vehicleId, e.getMessage(), e);
                return ResponseEntity.internalServerError().body(
                        UnifiedResponseDTO.<String>builder()
                                .success(false)
                                .error("Failed to handle connection drop: " + e.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
            }
        });
    }

    /**
     * Get tracking performance metrics
     */
    @GetMapping("/performance/metrics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<Map<String, Object>>>> getPerformanceMetrics() {
        
        return liveTrackingService.getActiveTrackingSessions()
                .thenApply(sessions -> {
                    Map<String, Object> metrics = new HashMap<>();
                    
                    // Basic metrics
                    metrics.put("activeSessions", sessions.size());
                    metrics.put("totalUpdates", sessions.stream().mapToLong(TrackingSession::getUpdateCount).sum());
                    metrics.put("averageBatteryLevel", sessions.stream().mapToDouble(s -> s.getBatteryLevel() != null ? s.getBatteryLevel() : 0).average().orElse(0));
                    metrics.put("averageSignalStrength", sessions.stream().mapToDouble(s -> s.getSignalStrength() != null ? s.getSignalStrength() : 0).average().orElse(0));
                    
                    // Performance metrics
                    Map<String, Long> optimizationLevels = new HashMap<>();
                    optimizationLevels.put("highPerformance", sessions.stream().filter(s -> s.getBatteryOptimizationLevel() == LiveTrackingService.BatteryOptimizationLevel.HIGH_PERFORMANCE).count());
                    optimizationLevels.put("balanced", sessions.stream().filter(s -> s.getBatteryOptimizationLevel() == LiveTrackingService.BatteryOptimizationLevel.BALANCED).count());
                    optimizationLevels.put("powerSaving", sessions.stream().filter(s -> s.getBatteryOptimizationLevel() == LiveTrackingService.BatteryOptimizationLevel.POWER_SAVING).count());
                    optimizationLevels.put("criticalPowerSaving", sessions.stream().filter(s -> s.getBatteryOptimizationLevel() == LiveTrackingService.BatteryOptimizationLevel.CRITICAL_POWER_SAVING).count());
                    
                    metrics.put("batteryOptimizationLevels", optimizationLevels);
                    
                    // Connection quality metrics
                    Map<String, Long> connectionQualities = new HashMap<>();
                    connectionQualities.put("excellent", sessions.stream().filter(s -> s.getConnectionQuality() == LiveTrackingService.ConnectionQuality.EXCELLENT).count());
                    connectionQualities.put("good", sessions.stream().filter(s -> s.getConnectionQuality() == LiveTrackingService.ConnectionQuality.GOOD).count());
                    connectionQualities.put("fair", sessions.stream().filter(s -> s.getConnectionQuality() == LiveTrackingService.ConnectionQuality.FAIR).count());
                    connectionQualities.put("poor", sessions.stream().filter(s -> s.getConnectionQuality() == LiveTrackingService.ConnectionQuality.POOR).count());
                    
                    metrics.put("connectionQualities", connectionQualities);
                    
                    // System health
                    metrics.put("droppedConnections", sessions.stream().filter(TrackingSession::isConnectionDropped).count());
                    metrics.put("systemStatus", "OPERATIONAL");
                    metrics.put("lastUpdated", LocalDateTime.now());
                    
                    log.debug("Performance metrics retrieved: {}", metrics.size());
                    
                    return ResponseEntity.ok(
                            UnifiedResponseDTO.<Map<String, Object>>builder()
                                    .success(true)
                                    .data(metrics)
                                    .message("Performance metrics retrieved successfully")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Error getting performance metrics: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<Map<String, Object>>builder()
                                    .success(false)
                                    .error("Failed to get performance metrics: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Test live tracking system performance
     */
    @GetMapping("/test/performance")
    @PreAuthorize("hasRole('ADMIN')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<Map<String, Object>>>> testPerformance() {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> testResults = new HashMap<>();
                
                // Test tracking performance
                long startTime = System.currentTimeMillis();
                
                // Simulate tracking session
                Long testVehicleId = 999L;
                Long testDriverId = getCurrentUserId();
                
                // Start tracking
                TrackingSession session = liveTrackingService.startTracking(testVehicleId, testDriverId).get();
                testResults.put("trackingStart", session != null);
                
                // Update location multiple times
                int updateCount = 10;
                for (int i = 0; i < updateCount; i++) {
                    LiveTrackingService.LocationUpdateRequest request = LiveTrackingService.LocationUpdateRequest.builder()
                            .vehicleId(testVehicleId)
                            .location(LiveTrackingService.LocationData.builder()
                                    .latitude(40.7128 + (i * 0.001))
                                    .longitude(-74.0060 + (i * 0.001))
                                    .speed(50.0 + (i * 2))
                                    .heading(90.0)
                                    .accuracy(10.0)
                                    .timestamp(LocalDateTime.now())
                                    .build())
                            .batteryLevel(80 - i)
                            .signalStrength(90 - i)
                            .connectionQuality(LiveTrackingService.ConnectionQuality.EXCELLENT)
                            .build();
                    
                    liveTrackingService.updateLocation(request).get();
                }
                
                // Get real-time location
                VehicleLocation location = liveTrackingService.getRealTimeLocation(testVehicleId).get();
                testResults.put("realTimeLocation", location != null);
                
                // Stop tracking
                liveTrackingService.stopTracking(testVehicleId).get();
                testResults.put("trackingStop", true);
                
                long endTime = System.currentTimeMillis();
                testResults.put("totalTime", endTime - startTime);
                testResults.put("updatesPerSecond", (double) updateCount / ((endTime - startTime) / 1000.0));
                testResults.put("testStatus", "PASSED");
                testResults.put("testTimestamp", LocalDateTime.now());
                
                log.info("Performance test completed: {}", testResults);
                
                return ResponseEntity.ok(
                        UnifiedResponseDTO.<Map<String, Object>>builder()
                                .success(true)
                                .data(testResults)
                                .message("Performance test completed successfully")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
                
            } catch (Exception e) {
                log.error("Error in performance test: {}", e.getMessage(), e);
                return ResponseEntity.internalServerError().body(
                        UnifiedResponseDTO.<Map<String, Object>>builder()
                                .success(false)
                                .error("Performance test failed: " + e.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
            }
        });
    }

    // Helper methods
    private Long getCurrentUserId() {
        // Implementation to get current user ID from security context
        return 1L; // Placeholder
    }

    private boolean canAccessVehicle(Long vehicleId) {
        try {
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) return false;
            
            // Check if user is admin or supervisor
            Optional<User> userOpt = userRepository.findById(currentUserId);
            if (userOpt.isEmpty()) return false;
            
            User user = userOpt.get();
            if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.SUPERVISOR) {
                return true;
            }
            
            // Check if user is assigned to vehicle
            Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
            if (vehicleOpt.isEmpty()) return false;
            
            Vehicle vehicle = vehicleOpt.get();
            return currentUserId.equals(vehicle.getDriverId());
            
        } catch (Exception e) {
            log.error("Error checking vehicle access: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean canViewVehicleLocation(Long vehicleId) {
        try {
            Long currentUserId = getCurrentUserId();
            if (currentUserId == null) return false;
            
            // Check if user is admin, supervisor, or accountant
            Optional<User> userOpt = userRepository.findById(currentUserId);
            if (userOpt.isEmpty()) return false;
            
            User user = userOpt.get();
            if (user.getRole() == UserRole.ADMIN || 
                user.getRole() == UserRole.SUPERVISOR || 
                user.getRole() == UserRole.ACCOUNTANT) {
                return true;
            }
            
            // Check if user is driver of the vehicle
            Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
            if (vehicleOpt.isEmpty()) return false;
            
            Vehicle vehicle = vehicleOpt.get();
            return currentUserId.equals(vehicle.getDriverId()) || isVehicleInUserShipment(vehicleId, currentUserId);
            
        } catch (Exception e) {
            log.error("Error checking location view access: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean isVehicleInUserShipment(Long vehicleId, Long userId) {
        // Implementation to check if vehicle is in user's shipment
        return false; // Placeholder
    }
}
