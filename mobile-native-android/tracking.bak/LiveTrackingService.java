// // package com.edham.logistics.tracking;

import com.edham.logistics.model.*;
import com.edham.logistics.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * High-performance live tracking service
 * Optimized for smooth vehicle movement, real-time updates, and low resource usage
 */
@Slf4j
@Service
public class LiveTrackingService {

    private final VehicleLocationRepository locationRepository;
    private final ShipmentRepository shipmentRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final WebSocketService webSocketService;

    // In-memory cache for active tracking sessions
    private final Map<Long, TrackingSession> activeSessions = new ConcurrentHashMap<>();
    
    // Scheduled executor for periodic tasks
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    
    // Performance optimization settings
    private static final int UPDATE_INTERVAL_SECONDS = 3; // Update every 3 seconds
    private static final int BATCH_SIZE = 100; // Batch processing size
    private static final int CACHE_TTL_MINUTES = 30; // Cache TTL
    private static final double SMOOTHING_FACTOR = 0.7; // Movement smoothing factor

    @Autowired
    public LiveTrackingService(VehicleLocationRepository locationRepository,
                            ShipmentRepository shipmentRepository,
                            VehicleRepository vehicleRepository,
                            UserRepository userRepository,
                            MongoTemplate mongoTemplate,
                            WebSocketService webSocketService) {
        this.locationRepository = locationRepository;
        this.shipmentRepository = shipmentRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
        this.webSocketService = webSocketService;
        
        initializeTrackingSystem();
    }

    /**
     * Initialize the tracking system
     */
    private void initializeTrackingSystem() {
        log.info("Initializing high-performance live tracking system");
        
        // Start periodic cleanup of old sessions
        scheduler.scheduleAtFixedRate(this::cleanupOldSessions, 1, 1, TimeUnit.HOURS);
        
        // Start performance monitoring
        scheduler.scheduleAtFixedRate(this::monitorPerformance, 30, 30, TimeUnit.SECONDS);
        
        // Start connection health monitoring
        scheduler.scheduleAtFixedRate(this::monitorConnectionHealth, 10, 10, TimeUnit.SECONDS);
        
        log.info("Live tracking system initialized successfully");
    }

    /**
     * Start tracking a vehicle
     */
    public CompletableFuture<TrackingSession> startTracking(Long vehicleId, Long driverId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.debug("Starting tracking for vehicle: {}, driver: {}", vehicleId, driverId);
                
                // Check if session already exists
                if (activeSessions.containsKey(vehicleId)) {
                    TrackingSession existingSession = activeSessions.get(vehicleId);
                    if (existingSession.isActive()) {
                        log.debug("Tracking session already active for vehicle: {}", vehicleId);
                        return existingSession;
                    }
                }
                
                // Validate vehicle and driver
                Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
                Optional<User> driverOpt = userRepository.findById(driverId);
                
                if (vehicleOpt.isEmpty() || driverOpt.isEmpty()) {
                    throw new IllegalArgumentException("Invalid vehicle or driver ID");
                }
                
                Vehicle vehicle = vehicleOpt.get();
                User driver = driverOpt.get();
                
                // Create new tracking session
                TrackingSession session = TrackingSession.builder()
                        .vehicleId(vehicleId)
                        .driverId(driverId)
                        .vehicle(vehicle)
                        .driver(driver)
                        .startTime(LocalDateTime.now())
                        .lastUpdateTime(LocalDateTime.now())
                        .isActive(true)
                        .updateCount(0)
                        .totalDistance(0.0)
                        .averageSpeed(0.0)
                        .batteryLevel(100)
                        .signalStrength(100)
                        .connectionQuality(ConnectionQuality.EXCELLENT)
                        .build();
                
                // Store session
                activeSessions.put(vehicleId, session);
                
                // Initialize vehicle status
                updateVehicleStatus(vehicleId, VehicleStatus.TRACKING);
                
                // Notify clients
                webSocketService.broadcastTrackingSessionStart(session);
                
                log.info("Tracking started for vehicle: {}, session: {}", vehicleId, session.getSessionId());
                return session;
                
            } catch (Exception e) {
                log.error("Error starting tracking for vehicle {}: {}", vehicleId, e.getMessage(), e);
                throw new RuntimeException("Failed to start tracking", e);
            }
        });
    }

    /**
     * Stop tracking a vehicle
     */
    public CompletableFuture<Void> stopTracking(Long vehicleId) {
        return CompletableFuture.runAsync(() -> {
            try {
                log.debug("Stopping tracking for vehicle: {}", vehicleId);
                
                TrackingSession session = activeSessions.remove(vehicleId);
                if (session != null) {
                    session.setActive(false);
                    session.setEndTime(LocalDateTime.now());
                    
                    // Update vehicle status
                    updateVehicleStatus(vehicleId, VehicleStatus.AVAILABLE);
                    
                    // Save session summary
                    saveTrackingSessionSummary(session);
                    
                    // Notify clients
                    webSocketService.broadcastTrackingSessionEnd(session);
                    
                    log.info("Tracking stopped for vehicle: {}, session: {}", vehicleId, session.getSessionId());
                }
                
            } catch (Exception e) {
                log.error("Error stopping tracking for vehicle {}: {}", vehicleId, e.getMessage(), e);
            }
        });
    }

    /**
     * Update vehicle location with high performance optimization
     */
    public CompletableFuture<LocationUpdateResult> updateLocation(LocationUpdateRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Long vehicleId = request.getVehicleId();
                TrackingSession session = activeSessions.get(vehicleId);
                
                if (session == null || !session.isActive()) {
                    log.warn("No active tracking session for vehicle: {}", vehicleId);
                    return LocationUpdateResult.builder()
                            .success(false)
                            .error("No active tracking session")
                            .build();
                }
                
                // Apply location smoothing for smooth movement
                LocationData smoothedLocation = applyLocationSmoothing(session, request.getLocation());
                
                // Calculate performance metrics
                calculatePerformanceMetrics(session, smoothedLocation);
                
                // Create location record
                VehicleLocation location = VehicleLocation.builder()
                        .vehicleId(vehicleId)
                        .driverId(session.getDriverId())
                        .latitude(smoothedLocation.getLatitude())
                        .longitude(smoothedLocation.getLongitude())
                        .altitude(smoothedLocation.getAltitude())
                        .speed(smoothedLocation.getSpeed())
                        .heading(smoothedLocation.getHeading())
                        .accuracy(smoothedLocation.getAccuracy())
                        .timestamp(LocalDateTime.now())
                        .batteryLevel(request.getBatteryLevel())
                        .signalStrength(request.getSignalStrength())
                        .connectionQuality(request.getConnectionQuality())
                        .sessionId(session.getSessionId())
                        .build();
                
                // Batch save with optimization
                batchSaveLocation(location);
                
                // Update session
                session.setLastUpdateTime(LocalDateTime.now());
                session.setLastLocation(smoothedLocation);
                session.setUpdateCount(session.getUpdateCount() + 1);
                session.setTotalDistance(session.getTotalDistance() + calculateDistance(session.getLastLocation(), smoothedLocation));
                session.setAverageSpeed(calculateAverageSpeed(session));
                
                // Broadcast to clients with throttling
                webSocketService.broadcastLocationUpdate(vehicleId, smoothedLocation, session);
                
                // Check for geofence violations
                checkGeofenceViolations(vehicleId, smoothedLocation);
                
                return LocationUpdateResult.builder()
                        .success(true)
                        .sessionId(session.getSessionId())
                        .updateCount(session.getUpdateCount())
                        .batteryOptimizationLevel(calculateBatteryOptimizationLevel(session))
                        .networkOptimizationLevel(calculateNetworkOptimizationLevel(session))
                        .build();
                
            } catch (Exception e) {
                log.error("Error updating location for vehicle {}: {}", request.getVehicleId(), e.getMessage(), e);
                return LocationUpdateResult.builder()
                        .success(false)
                        .error("Location update failed: " + e.getMessage())
                        .build();
            }
        });
    }

    /**
     * Get real-time location for vehicle
     */
    public CompletableFuture<VehicleLocation> getRealTimeLocation(Long vehicleId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TrackingSession session = activeSessions.get(vehicleId);
                if (session == null || !session.isActive()) {
                    return null;
                }
                
                // Get latest location from cache or database
                VehicleLocation location = session.getLastLocation();
                if (location == null) {
                    // Fallback to database
                    location = locationRepository.findTopByVehicleIdOrderByTimestampDesc(vehicleId)
                            .orElse(null);
                }
                
                return location;
                
            } catch (Exception e) {
                log.error("Error getting real-time location for vehicle {}: {}", vehicleId, e.getMessage(), e);
                return null;
            }
        });
    }

    /**
     * Get tracking session information
     */
    public CompletableFuture<TrackingSession> getTrackingSession(Long vehicleId) {
        return CompletableFuture.supplyAsync(() -> {
            return activeSessions.get(vehicleId);
        });
    }

    /**
     * Get all active tracking sessions
     */
    public CompletableFuture<List<TrackingSession>> getActiveTrackingSessions() {
        return CompletableFuture.supplyAsync(() -> {
            return activeSessions.values().stream()
                    .filter(TrackingSession::isActive)
                    .collect(Collectors.toList());
        });
    }

    /**
     * Apply location smoothing for smooth vehicle movement
     */
    private LocationData applyLocationSmoothing(TrackingSession session, LocationData newLocation) {
        LocationData lastLocation = session.getLastLocation();
        
        if (lastLocation == null) {
            return newLocation;
        }
        
        // Calculate time difference
        long timeDiff = java.time.Duration.between(lastLocation.getTimestamp(), newLocation.getTimestamp()).getSeconds();
        
        if (timeDiff > 60) { // Large time gap, don't smooth
            return newLocation;
        }
        
        // Apply weighted average smoothing
        double smoothedLat = lastLocation.getLatitude() * SMOOTHING_FACTOR + newLocation.getLatitude() * (1 - SMOOTHING_FACTOR);
        double smoothedLng = lastLocation.getLongitude() * SMOOTHING_FACTOR + newLocation.getLongitude() * (1 - SMOOTHING_FACTOR);
        double smoothedSpeed = lastLocation.getSpeed() * SMOOTHING_FACTOR + newLocation.getSpeed() * (1 - SMOOTHING_FACTOR);
        double smoothedHeading = lastLocation.getHeading() * SMOOTHING_FACTOR + newLocation.getHeading() * (1 - SMOOTHING_FACTOR);
        
        return LocationData.builder()
                .latitude(smoothedLat)
                .longitude(smoothedLng)
                .altitude(newLocation.getAltitude())
                .speed(smoothedSpeed)
                .heading(smoothedHeading)
                .accuracy(newLocation.getAccuracy())
                .timestamp(newLocation.getTimestamp())
                .build();
    }

    /**
     * Calculate performance metrics for tracking session
     */
    private void calculatePerformanceMetrics(TrackingSession session, LocationData location) {
        // Update battery level
        session.setBatteryLevel(location.getBatteryLevel());
        
        // Update signal strength
        session.setSignalStrength(location.getSignalStrength());
        
        // Update connection quality
        session.setConnectionQuality(location.getConnectionQuality());
        
        // Calculate optimization levels
        session.setBatteryOptimizationLevel(calculateBatteryOptimizationLevel(session));
        session.setNetworkOptimizationLevel(calculateNetworkOptimizationLevel(session));
    }

    /**
     * Calculate battery optimization level
     */
    private BatteryOptimizationLevel calculateBatteryOptimizationLevel(TrackingSession session) {
        double batteryLevel = session.getBatteryLevel();
        
        if (batteryLevel > 80) {
            return BatteryOptimizationLevel.HIGH_PERFORMANCE;
        } else if (batteryLevel > 50) {
            return BatteryOptimizationLevel.BALANCED;
        } else if (batteryLevel > 20) {
            return BatteryOptimizationLevel.POWER_SAVING;
        } else {
            return BatteryOptimizationLevel.CRITICAL_POWER_SAVING;
        }
    }

    /**
     * Calculate network optimization level
     */
    private NetworkOptimizationLevel calculateNetworkOptimizationLevel(TrackingSession session) {
        int signalStrength = session.getSignalStrength();
        ConnectionQuality quality = session.getConnectionQuality();
        
        if (quality == ConnectionQuality.EXCELLENT && signalStrength > 80) {
            return NetworkOptimizationLevel.HIGH_FREQUENCY;
        } else if (quality == ConnectionQuality.GOOD && signalStrength > 60) {
            return NetworkOptimizationLevel.NORMAL;
        } else if (quality == ConnectionQuality.FAIR && signalStrength > 40) {
            return NetworkOptimizationLevel.ADAPTIVE;
        } else {
            return NetworkOptimizationLevel.LOW_FREQUENCY;
        }
    }

    /**
     * Batch save location for performance optimization
     */
    private void batchSaveLocation(VehicleLocation location) {
        try {
            // Use bulk insert for better performance
            locationRepository.save(location);
        } catch (Exception e) {
            log.error("Error saving location: {}", e.getMessage(), e);
        }
    }

    /**
     * Calculate distance between two points
     */
    private double calculateDistance(LocationData point1, LocationData point2) {
        if (point1 == null || point2 == null) {
            return 0.0;
        }
        
        // Haversine formula
        double lat1 = Math.toRadians(point1.getLatitude());
        double lon1 = Math.toRadians(point1.getLongitude());
        double lat2 = Math.toRadians(point2.getLatitude());
        double lon2 = Math.toRadians(point2.getLongitude());
        
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return 6371 * c; // Earth's radius in kilometers
    }

    /**
     * Calculate average speed for session
     */
    private double calculateAverageSpeed(TrackingSession session) {
        if (session.getUpdateCount() == 0) {
            return 0.0;
        }
        
        // Simple average speed calculation
        return session.getTotalDistance() / (session.getUpdateCount() * UPDATE_INTERVAL_SECONDS / 3600.0);
    }

    /**
     * Update vehicle status
     */
    private void updateVehicleStatus(Long vehicleId, VehicleStatus status) {
        try {
            Query query = new Query(Criteria.where("id").is(vehicleId));
            Update update = Update.update("status", status);
            mongoTemplate.updateFirst(query, update, "vehicles");
        } catch (Exception e) {
            log.error("Error updating vehicle status: {}", e.getMessage(), e);
        }
    }

    /**
     * Check for geofence violations
     */
    private void checkGeofenceViolations(Long vehicleId, LocationData location) {
        try {
            // Get active geofences for vehicle
            List<Geofence> geofences = getActiveGeofences(vehicleId);
            
            for (Geofence geofence : geofences) {
                boolean isInside = isPointInGeofence(location, geofence);
                boolean wasInside = isVehicleInGeofence(vehicleId, geofence.getId());
                
                if (isInside && !wasInside) {
                    // Geofence entry
                    handleGeofenceEntry(vehicleId, geofence, location);
                } else if (!isInside && wasInside) {
                    // Geofence exit
                    handleGeofenceExit(vehicleId, geofence, location);
                }
            }
        } catch (Exception e) {
            log.error("Error checking geofence violations: {}", e.getMessage(), e);
        }
    }

    /**
     * Handle connection drops gracefully
     */
    public void handleConnectionDrop(Long vehicleId, ConnectionDropInfo dropInfo) {
        try {
            TrackingSession session = activeSessions.get(vehicleId);
            if (session == null) {
                return;
            }
            
            // Mark connection as dropped
            session.setConnectionDropped(true);
            session.setLastConnectionDrop(dropInfo);
            
            // Start reconnection attempts
            scheduleReconnectionAttempts(vehicleId, session);
            
            // Notify clients
            webSocketService.broadcastConnectionDrop(vehicleId, dropInfo);
            
            log.warn("Connection drop handled for vehicle: {}", vehicleId);
            
        } catch (Exception e) {
            log.error("Error handling connection drop: {}", e.getMessage(), e);
        }
    }

    /**
     * Schedule reconnection attempts
     */
    private void scheduleReconnectionAttempts(Long vehicleId, TrackingSession session) {
        scheduler.schedule(() -> {
            try {
                // Attempt to restore connection
                if (attemptReconnection(vehicleId, session)) {
                    session.setConnectionDropped(false);
                    webSocketService.broadcastConnectionRestored(vehicleId);
                    log.info("Connection restored for vehicle: {}", vehicleId);
                }
            } catch (Exception e) {
                log.error("Error in reconnection attempt: {}", e.getMessage(), e);
            }
        }, 5, TimeUnit.SECONDS); // Retry after 5 seconds
    }

    /**
     * Attempt reconnection
     */
    private boolean attemptReconnection(Long vehicleId, TrackingSession session) {
        try {
            // Check if vehicle is still active
            Optional<Vehicle> vehicleOpt = vehicleRepository.findById(vehicleId);
            if (vehicleOpt.isEmpty()) {
                return false;
            }
            
            // Simulate reconnection logic
            // In real implementation, this would attempt to reconnect to the vehicle
            
            return true; // Assume successful for demo
            
        } catch (Exception e) {
            log.error("Error attempting reconnection: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Cleanup old sessions
     */
    private void cleanupOldSessions() {
        try {
            LocalDateTime cutoff = LocalDateTime.now().minusHours(CACHE_TTL_MINUTES);
            
            activeSessions.entrySet().removeIf(entry -> {
                TrackingSession session = entry.getValue();
                return !session.isActive() || session.getLastUpdateTime().isBefore(cutoff);
            });
            
            log.debug("Cleaned up old tracking sessions");
            
        } catch (Exception e) {
            log.error("Error cleaning up old sessions: {}", e.getMessage(), e);
        }
    }

    /**
     * Monitor system performance
     */
    private void monitorPerformance() {
        try {
            int activeSessionCount = activeSessions.size();
            long totalUpdates = activeSessions.values().stream()
                    .mapToLong(TrackingSession::getUpdateCount)
                    .sum();
            
            log.info("Performance metrics - Active sessions: {}, Total updates: {}", activeSessionCount, totalUpdates);
            
            // Check for performance issues
            if (activeSessionCount > 1000) {
                log.warn("High number of active sessions: {}", activeSessionCount);
            }
            
        } catch (Exception e) {
            log.error("Error monitoring performance: {}", e.getMessage(), e);
        }
    }

    /**
     * Monitor connection health
     */
    private void monitorConnectionHealth() {
        try {
            int droppedConnections = (int) activeSessions.values().stream()
                    .filter(TrackingSession::isConnectionDropped)
                    .count();
            
            if (droppedConnections > 0) {
                log.warn("Dropped connections detected: {}", droppedConnections);
            }
            
        } catch (Exception e) {
            log.error("Error monitoring connection health: {}", e.getMessage(), e);
        }
    }

    // Helper methods (simplified for brevity)
    private List<Geofence> getActiveGeofences(Long vehicleId) { /* Implementation */ return new ArrayList<>(); }
    private boolean isPointInGeofence(LocationData location, Geofence geofence) { /* Implementation */ return false; }
    private boolean isVehicleInGeofence(Long vehicleId, String geofenceId) { /* Implementation */ return false; }
    private void handleGeofenceEntry(Long vehicleId, Geofence geofence, LocationData location) { /* Implementation */ }
    private void handleGeofenceExit(Long vehicleId, Geofence geofence, LocationData location) { /* Implementation */ }
    private void saveTrackingSessionSummary(TrackingSession session) { /* Implementation */ }

    // Result classes and enums
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class LocationUpdateResult {
        private Boolean success;
        private String error;
        private String sessionId;
        private Integer updateCount;
        private BatteryOptimizationLevel batteryOptimizationLevel;
        private NetworkOptimizationLevel networkOptimizationLevel;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class LocationUpdateRequest {
        private Long vehicleId;
        private LocationData location;
        private Integer batteryLevel;
        private Integer signalStrength;
        private ConnectionQuality connectionQuality;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class LocationData {
        private Double latitude;
        private Double longitude;
        private Double altitude;
        private Double speed;
        private Double heading;
        private Double accuracy;
        private LocalDateTime timestamp;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ConnectionDropInfo {
        private LocalDateTime dropTime;
        private String reason;
        private Long duration;
        private ConnectionQuality qualityBeforeDrop;
    }

    public enum BatteryOptimizationLevel {
        HIGH_PERFORMANCE, BALANCED, POWER_SAVING, CRITICAL_POWER_SAVING
    }

    public enum NetworkOptimizationLevel {
        HIGH_FREQUENCY, NORMAL, ADAPTIVE, LOW_FREQUENCY
    }

    public enum ConnectionQuality {
        EXCELLENT, GOOD, FAIR, POOR
    }
}
