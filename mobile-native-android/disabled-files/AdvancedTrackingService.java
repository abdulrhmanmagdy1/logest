package com.edham.logistics.service;

import com.edham.logistics.dto.*;
import com.edham.logistics.model.*;
import com.edham.logistics.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Advanced tracking service for enhanced map features
 * Provides smooth animations, route replay, geofencing, and multi-vehicle tracking
 */
@Slf4j
@Service
public class AdvancedTrackingService {

    private final TrackingService trackingService;
    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final WebSocketSessionManager webSocketSessionManager;

    @Autowired
    public AdvancedTrackingService(TrackingService trackingService,
                                 ShipmentRepository shipmentRepository,
                                 UserRepository userRepository,
                                 VehicleRepository vehicleRepository,
                                 WebSocketSessionManager webSocketSessionManager) {
        this.trackingService = trackingService;
        this.shipmentRepository = shipmentRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.webSocketSessionManager = webSocketSessionManager;
    }

    /**
     * Get smooth vehicle movement animation data
     */
    public VehicleAnimationDTO getVehicleAnimation(Long vehicleId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            // Get tracking points for the time range
            List<LocationDTO> trackingPoints = getTrackingPoints(vehicleId, startTime, endTime);
            
            // Interpolate points for smooth animation
            List<LocationDTO> interpolatedPoints = interpolateTrackingPoints(trackingPoints);
            
            return VehicleAnimationDTO.builder()
                    .vehicleId(vehicleId)
                    .startTime(startTime)
                    .endTime(endTime)
                    .trackingPoints(interpolatedPoints)
                    .animationDuration(calculateAnimationDuration(interpolatedPoints))
                    .smoothTransitions(true)
                    .generatedAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Error generating vehicle animation: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate vehicle animation", e);
        }
    }

    /**
     * Get route replay history for shipment
     */
    public RouteReplayDTO getRouteReplay(Long shipmentId) {
        try {
            Shipment shipment = shipmentRepository.findById(shipmentId)
                    .orElseThrow(() -> new RuntimeException("Shipment not found"));

            // Get complete route history
            List<RoutePointDTO> routeHistory = getCompleteRouteHistory(shipmentId);
            
            // Calculate route statistics
            RouteStatisticsDTO statistics = calculateRouteStatistics(routeHistory);

            return RouteReplayDTO.builder()
                    .shipmentId(shipmentId)
                    .trackingNumber(shipment.getTrackingNumber())
                    .routeHistory(routeHistory)
                    .statistics(statistics)
                    .startTime(routeHistory.get(0).getTimestamp())
                    .endTime(routeHistory.get(routeHistory.size() - 1).getTimestamp())
                    .totalDistance(statistics.getTotalDistance())
                    .estimatedTime(statistics.getTotalDuration())
                    .build();

        } catch (Exception e) {
            log.error("Error getting route replay: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get route replay", e);
        }
    }

    /**
     * Check geofencing alerts for vehicles
     */
    public List<GeofenceAlertDTO> checkGeofencingAlerts(Long vehicleId) {
        try {
            List<GeofenceAlertDTO> alerts = new ArrayList<>();
            
            // Get current vehicle location
            LocationDTO currentLocation = getCurrentVehicleLocation(vehicleId);
            if (currentLocation == null) {
                return alerts;
            }

            // Get active geofences for this vehicle
            List<GeofenceDTO> activeGeofences = getActiveGeofences(vehicleId);

            // Check each geofence
            for (GeofenceDTO geofence : activeGeofences) {
                GeofenceAlertDTO alert = checkGeofence(vehicleId, currentLocation, geofence);
                if (alert != null) {
                    alerts.add(alert);
                }
            }

            return alerts;

        } catch (Exception e) {
            log.error("Error checking geofencing alerts: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to check geofencing alerts", e);
        }
    }

    /**
     * Get multi-vehicle tracking view
     */
    public MultiVehicleTrackingDTO getMultiVehicleTracking(List<Long> vehicleIds) {
        try {
            List<VehicleTrackingInfoDTO> vehicleTrackingInfo = new ArrayList<>();

            for (Long vehicleId : vehicleIds) {
                VehicleTrackingInfoDTO info = getVehicleTrackingInfo(vehicleId);
                if (info != null) {
                    vehicleTrackingInfo.add(info);
                }
            }

            // Calculate fleet statistics
            FleetStatisticsDTO fleetStats = calculateFleetStatistics(vehicleTrackingInfo);

            return MultiVehicleTrackingDTO.builder()
                    .vehicleIds(vehicleIds)
                    .vehicleTrackingInfo(vehicleTrackingInfo)
                    .fleetStatistics(fleetStats)
                    .totalVehicles(vehicleTrackingInfo.size())
                    .activeVehicles((int) vehicleTrackingInfo.stream()
                            .filter(info -> info.getStatus().equals("ACTIVE"))
                            .count())
                    .generatedAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Error getting multi-vehicle tracking: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get multi-vehicle tracking", e);
        }
    }

    /**
     * Get optimized map performance data
     */
    public MapPerformanceDTO getMapPerformanceData(List<Long> vehicleIds, MapBoundsDTO bounds) {
        try {
            // Calculate optimal clustering
            List<MapClusterDTO> clusters = calculateMapClusters(vehicleIds, bounds);
            
            // Determine optimal zoom level
            int optimalZoom = calculateOptimalZoom(bounds, vehicleIds.size());
            
            // Get performance metrics
            MapPerformanceMetricsDTO metrics = MapPerformanceMetricsDTO.builder()
                    .totalMarkers(vehicleIds.size())
                    .clusteredMarkers(clusters.size())
                    .clusterRatio((double) clusters.size() / vehicleIds.size())
                    .optimalZoom(optimalZoom)
                    .estimatedRenderTime(calculateRenderTime(vehicleIds.size()))
                    .memoryUsage(calculateMemoryUsage(vehicleIds.size()))
                    .build();

            return MapPerformanceDTO.builder()
                    .bounds(bounds)
                    .clusters(clusters)
                    .metrics(metrics)
                    .optimizations(getPerformanceOptimizations(vehicleIds.size()))
                    .generatedAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Error getting map performance data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get map performance data", e);
        }
    }

    // Helper methods
    private List<LocationDTO> getTrackingPoints(Long vehicleId, LocalDateTime startTime, LocalDateTime endTime) {
        // Simplified tracking points retrieval
        // In real implementation, this would query the tracking database
        List<LocationDTO> points = new ArrayList<>();
        
        // Generate sample tracking points
        for (int i = 0; i < 10; i++) {
            points.add(LocationDTO.builder()
                    .latitude(24.7136 + (i * 0.01))
                    .longitude(46.6753 + (i * 0.01))
                    .timestamp(startTime.plusMinutes(i * 5))
                    .speed(60.0 + Math.random() * 20)
                    .heading(45.0 + Math.random() * 90)
                    .build());
        }
        
        return points;
    }

    private List<LocationDTO> interpolateTrackingPoints(List<LocationDTO> points) {
        if (points.size() < 2) return points;
        
        List<LocationDTO> interpolated = new ArrayList<>();
        
        for (int i = 0; i < points.size() - 1; i++) {
            LocationDTO current = points.get(i);
            LocationDTO next = points.get(i + 1);
            
            // Add current point
            interpolated.add(current);
            
            // Add interpolated points for smooth animation
            for (int j = 1; j < 5; j++) {
                double ratio = j / 5.0;
                interpolated.add(LocationDTO.builder()
                        .latitude(current.getLatitude() + (next.getLatitude() - current.getLatitude()) * ratio)
                        .longitude(current.getLongitude() + (next.getLongitude() - current.getLongitude()) * ratio)
                        .timestamp(current.getTimestamp().plusSeconds(j * 30))
                        .speed(current.getSpeed() + (next.getSpeed() - current.getSpeed()) * ratio)
                        .heading(current.getHeading() + (next.getHeading() - current.getHeading()) * ratio)
                        .build());
            }
        }
        
        // Add last point
        interpolated.add(points.get(points.size() - 1));
        
        return interpolated;
    }

    private Duration calculateAnimationDuration(List<LocationDTO> points) {
        if (points.size() < 2) return Duration.ZERO;
        
        LocalDateTime start = points.get(0).getTimestamp();
        LocalDateTime end = points.get(points.size() - 1).getTimestamp();
        
        return Duration.between(start, end);
    }

    private List<RoutePointDTO> getCompleteRouteHistory(Long shipmentId) {
        // Simplified route history retrieval
        List<RoutePointDTO> history = new ArrayList<>();
        
        // Generate sample route points
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 20; i++) {
            history.add(RoutePointDTO.builder()
                    .latitude(24.7136 + (i * 0.005))
                    .longitude(46.6753 + (i * 0.005))
                    .timestamp(now.minusHours(2).plusMinutes(i * 6))
                    .speed(55.0 + Math.random() * 25)
                    .heading(45.0 + Math.random() * 45)
                    .altitude(600.0 + Math.random() * 100)
                    .accuracy(5.0 + Math.random() * 10)
                    .build());
        }
        
        return history;
    }

    private RouteStatisticsDTO calculateRouteStatistics(List<RoutePointDTO> routeHistory) {
        double totalDistance = 0.0;
        Duration totalDuration = Duration.ZERO;
        double maxSpeed = 0.0;
        double avgSpeed = 0.0;

        for (int i = 0; i < routeHistory.size() - 1; i++) {
            RoutePointDTO current = routeHistory.get(i);
            RoutePointDTO next = routeHistory.get(i + 1);
            
            // Calculate distance between points (simplified)
            double distance = calculateDistance(current.getLatitude(), current.getLongitude(),
                                              next.getLatitude(), next.getLongitude());
            totalDistance += distance;
            
            // Update speed statistics
            maxSpeed = Math.max(maxSpeed, current.getSpeed());
            avgSpeed += current.getSpeed();
        }
        
        if (routeHistory.size() > 1) {
            totalDuration = Duration.between(routeHistory.get(0).getTimestamp(),
                                           routeHistory.get(routeHistory.size() - 1).getTimestamp());
            avgSpeed /= routeHistory.size();
        }

        return RouteStatisticsDTO.builder()
                .totalDistance(totalDistance)
                .totalDuration(totalDuration)
                .maxSpeed(maxSpeed)
                .avgSpeed(avgSpeed)
                .totalStops(routeHistory.size())
                .build();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Simplified distance calculation (Haversine formula)
        double R = 6371; // Earth's radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    private LocationDTO getCurrentVehicleLocation(Long vehicleId) {
        // Simplified current location retrieval
        return LocationDTO.builder()
                .latitude(24.7136 + Math.random() * 0.1)
                .longitude(46.6753 + Math.random() * 0.1)
                .timestamp(LocalDateTime.now())
                .speed(60.0 + Math.random() * 20)
                .heading(45.0 + Math.random() * 90)
                .build();
    }

    private List<GeofenceDTO> getActiveGeofences(Long vehicleId) {
        // Simplified geofence retrieval
        List<GeofenceDTO> geofences = new ArrayList<>();
        
        geofences.add(GeofenceDTO.builder()
                .id("geofence_1")
                .name("Riyadh City Center")
                .latitude(24.7136)
                .longitude(46.6753)
                .radius(5000.0) // 5km radius
                .type("CIRCLE")
                .alertOnEntry(true)
                .alertOnExit(true)
                .build());
        
        return geofences;
    }

    private GeofenceAlertDTO checkGeofence(Long vehicleId, LocationDTO location, GeofenceDTO geofence) {
        double distance = calculateDistance(location.getLatitude(), location.getLongitude(),
                                          geofence.getLatitude(), geofence.getLongitude());
        
        if (distance <= geofence.getRadius()) {
            return GeofenceAlertDTO.builder()
                    .vehicleId(vehicleId)
                    .geofenceId(geofence.getId())
                    .geofenceName(geofence.getName())
                    .alertType("ENTRY")
                    .location(location)
                    .distance(distance)
                    .timestamp(LocalDateTime.now())
                    .message("Vehicle entered geofence: " + geofence.getName())
                    .build();
        }
        
        return null;
    }

    private VehicleTrackingInfoDTO getVehicleTrackingInfo(Long vehicleId) {
        try {
            Vehicle vehicle = vehicleRepository.findById(vehicleId).orElse(null);
            if (vehicle == null) return null;

            LocationDTO currentLocation = getCurrentVehicleLocation(vehicleId);
            User driver = userRepository.findById(vehicle.getDriverId()).orElse(null);

            return VehicleTrackingInfoDTO.builder()
                    .vehicleId(vehicleId)
                    .licensePlate(vehicle.getLicensePlate())
                    .vehicleType(vehicle.getVehicleType())
                    .status(vehicle.getStatus().name())
                    .currentLocation(currentLocation)
                    .driverId(vehicle.getDriverId())
                    .driverName(driver != null ? driver.getFullName() : "Unknown")
                    .lastUpdate(LocalDateTime.now())
                    .speed(currentLocation.getSpeed())
                    .heading(currentLocation.getHeading())
                    .fuelLevel(vehicle.getFuelLevel())
                    .build();

        } catch (Exception e) {
            log.error("Error getting vehicle tracking info: {}", e.getMessage(), e);
            return null;
        }
    }

    private FleetStatisticsDTO calculateFleetStatistics(List<VehicleTrackingInfoDTO> vehicles) {
        int activeVehicles = (int) vehicles.stream()
                .filter(v -> v.getStatus().equals("ACTIVE"))
                .count();
        
        double avgSpeed = vehicles.stream()
                .mapToDouble(VehicleTrackingInfoDTO::getSpeed)
                .average()
                .orElse(0.0);
        
        double avgFuelLevel = vehicles.stream()
                .mapToDouble(v -> v.getFuelLevel() != null ? v.getFuelLevel() : 0.0)
                .average()
                .orElse(0.0);

        return FleetStatisticsDTO.builder()
                .totalVehicles(vehicles.size())
                .activeVehicles(activeVehicles)
                .inactiveVehicles(vehicles.size() - activeVehicles)
                .averageSpeed(avgSpeed)
                .averageFuelLevel(avgFuelLevel)
                .fleetUtilization((double) activeVehicles / vehicles.size())
                .build();
    }

    private List<MapClusterDTO> calculateMapClusters(List<Long> vehicleIds, MapBoundsDTO bounds) {
        // Simplified clustering algorithm
        List<MapClusterDTO> clusters = new ArrayList<>();
        
        // Group vehicles by proximity
        Map<String, List<Long>> clusterGroups = new HashMap<>();
        
        for (Long vehicleId : vehicleIds) {
            LocationDTO location = getCurrentVehicleLocation(vehicleId);
            String clusterKey = getClusterKey(location, bounds);
            
            clusterGroups.computeIfAbsent(clusterKey, k -> new ArrayList<>()).add(vehicleId);
        }
        
        // Create cluster objects
        for (Map.Entry<String, List<Long>> entry : clusterGroups.entrySet()) {
            List<Long> vehiclesInCluster = entry.getValue();
            if (vehiclesInCluster.size() == 1) {
                // Single vehicle cluster
                Long vehicleId = vehiclesInCluster.get(0);
                LocationDTO location = getCurrentVehicleLocation(vehicleId);
                
                clusters.add(MapClusterDTO.builder()
                        .clusterId("single_" + vehicleId)
                        .latitude(location.getLatitude())
                        .longitude(location.getLongitude())
                        .vehicleCount(1)
                        .vehicleIds(vehiclesInCluster)
                        .isCluster(false)
                        .build());
            } else {
                // Multiple vehicle cluster
                LocationDTO centerLocation = calculateClusterCenter(vehiclesInCluster);
                
                clusters.add(MapClusterDTO.builder()
                        .clusterId("cluster_" + entry.getKey())
                        .latitude(centerLocation.getLatitude())
                        .longitude(centerLocation.getLongitude())
                        .vehicleCount(vehiclesInCluster.size())
                        .vehicleIds(vehiclesInCluster)
                        .isCluster(true)
                        .build());
            }
        }
        
        return clusters;
    }

    private String getClusterKey(LocationDTO location, MapBoundsDTO bounds) {
        // Simple grid-based clustering
        double latStep = (bounds.getMaxLat() - bounds.getMinLat()) / 10;
        double lonStep = (bounds.getMaxLon() - bounds.getMinLon()) / 10;
        
        int latGrid = (int) ((location.getLatitude() - bounds.getMinLat()) / latStep);
        int lonGrid = (int) ((location.getLongitude() - bounds.getMinLon()) / lonStep);
        
        return latGrid + "_" + lonGrid;
    }

    private LocationDTO calculateClusterCenter(List<Long> vehicleIds) {
        double totalLat = 0.0;
        double totalLon = 0.0;
        int count = 0;
        
        for (Long vehicleId : vehicleIds) {
            LocationDTO location = getCurrentVehicleLocation(vehicleId);
            totalLat += location.getLatitude();
            totalLon += location.getLongitude();
            count++;
        }
        
        return LocationDTO.builder()
                .latitude(totalLat / count)
                .longitude(totalLon / count)
                .timestamp(LocalDateTime.now())
                .build();
    }

    private int calculateOptimalZoom(MapBoundsDTO bounds, int vehicleCount) {
        // Simplified zoom calculation
        double area = (bounds.getMaxLat() - bounds.getMinLat()) * (bounds.getMaxLon() - bounds.getMinLon());
        
        if (vehicleCount > 100) return 8;
        if (vehicleCount > 50) return 10;
        if (vehicleCount > 20) return 12;
        if (vehicleCount > 10) return 14;
        return 16;
    }

    private long calculateRenderTime(int vehicleCount) {
        // Simplified render time calculation in milliseconds
        return 50 + (vehicleCount * 2);
    }

    private double calculateMemoryUsage(int vehicleCount) {
        // Simplified memory usage calculation in MB
        return 10.0 + (vehicleCount * 0.5);
    }

    private List<String> getPerformanceOptimizations(int vehicleCount) {
        List<String> optimizations = new ArrayList<>();
        
        if (vehicleCount > 100) {
            optimizations.add("Enable marker clustering");
            optimizations.add("Use level of detail rendering");
            optimizations.add("Implement viewport culling");
        }
        
        if (vehicleCount > 50) {
            optimizations.add("Enable marker clustering");
            optimizations.add("Use simplified icons at low zoom");
        }
        
        optimizations.add("Enable hardware acceleration");
        optimizations.add("Use efficient data structures");
        
        return optimizations;
    }
}
