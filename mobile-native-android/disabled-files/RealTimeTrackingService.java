package com.edham.logistics.service;

import com.edham.logistics.dto.LocationUpdateDTO;
import com.edham.logistics.dto.TrackingEventDTO;
import com.edham.logistics.model.*;
import com.edham.logistics.repository.*;
import com.edham.logistics.websocket.RealTimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Service for real-time tracking functionality
 * Handles location updates, tracking events, and route optimization
 */
@Slf4j
@Service
public class RealTimeTrackingService {

    private final ShipmentRepository shipmentRepository;
    private final TrackingEventRepository trackingEventRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RealTimeService realTimeService;

    // Cache keys
    private static final String DRIVER_LOCATION_KEY = "driver:location:";
    private static final String SHIPMENT_TRACKING_KEY = "shipment:tracking:";
    private static final String ACTIVE_DRIVERS_KEY = "drivers:active";

    @Autowired
    public RealTimeTrackingService(ShipmentRepository shipmentRepository,
                                TrackingEventRepository trackingEventRepository,
                                UserRepository userRepository,
                                RedisTemplate<String, Object> redisTemplate,
                                RealTimeService realTimeService) {
        this.shipmentRepository = shipmentRepository;
        this.trackingEventRepository = trackingEventRepository;
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
        this.realTimeService = realTimeService;
    }

    /**
     * Update driver location in real-time
     */
    @Async
    @Transactional
    public void updateDriverLocation(Long driverId, LocationUpdateDTO locationUpdate) {
        try {
            // Validate driver exists and is active
            User driver = userRepository.findById(driverId)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));

            if (!driver.isActive() || driver.getRole() != UserRole.DRIVER) {
                throw new RuntimeException("Invalid driver account");
            }

            // Store location in Redis for fast access
            String locationKey = DRIVER_LOCATION_KEY + driverId;
            redisTemplate.opsForValue().set(locationKey, locationUpdate, 1, TimeUnit.HOURS);

            // Add to active drivers set
            redisTemplate.opsForSet().add(ACTIVE_DRIVERS_KEY, driverId.toString());
            redisTemplate.expire(ACTIVE_DRIVERS_KEY, 2, TimeUnit.HOURS);

            // Store location history in database
            saveDriverLocationToDatabase(driverId, locationUpdate);

            // Send real-time update via WebSocket
            realTimeService.sendDriverLocationUpdate(driverId, locationUpdate);

            // Check for geofence events
            checkGeofenceEvents(driverId, locationUpdate);

            log.debug("Updated driver {} location: {}, {}", 
                    driverId, locationUpdate.getLatitude(), locationUpdate.getLongitude());

        } catch (Exception e) {
            log.error("Error updating driver {} location: {}", driverId, e.getMessage(), e);
        }
    }

    /**
     * Add tracking event to shipment
     */
    @Async
    @Transactional
    public TrackingEvent addTrackingEvent(Long shipmentId, TrackingEventDTO eventDTO) {
        try {
            Shipment shipment = shipmentRepository.findById(shipmentId)
                    .orElseThrow(() -> new RuntimeException("Shipment not found"));

            // Create tracking event
            TrackingEvent event = TrackingEvent.builder()
                    .shipment(shipment)
                    .eventType(eventDTO.getEventType())
                    .description(eventDTO.getDescription())
                    .location(convertToLocation(eventDTO.getLocation()))
                    .timestamp(LocalDateTime.now())
                    .createdBy(getCurrentUser())
                    .build();

            // Save to database
            TrackingEvent savedEvent = trackingEventRepository.save(event);

            // Update shipment cache
            updateShipmentTrackingCache(shipmentId, savedEvent);

            // Send real-time update
            realTimeService.sendTrackingEventUpdate(shipmentId, savedEvent);

            // Check for automated events
            checkAutomatedEvents(shipment, savedEvent);

            log.info("Added tracking event {} for shipment {}", 
                    eventDTO.getEventType(), shipmentId);

            return savedEvent;

        } catch (Exception e) {
            log.error("Error adding tracking event for shipment {}: {}", 
                    shipmentId, e.getMessage(), e);
            throw new RuntimeException("Failed to add tracking event");
        }
    }

    /**
     * Get driver's current location
     */
    public LocationUpdateDTO getDriverCurrentLocation(Long driverId) {
        try {
            String locationKey = DRIVER_LOCATION_KEY + driverId;
            LocationUpdateDTO location = (LocationUpdateDTO) redisTemplate.opsForValue().get(locationKey);
            
            if (location == null) {
                // Fallback to database
                location = getLatestLocationFromDatabase(driverId);
                if (location != null) {
                    redisTemplate.opsForValue().set(locationKey, location, 1, TimeUnit.HOURS);
                }
            }

            return location;

        } catch (Exception e) {
            log.error("Error getting driver {} current location: {}", driverId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get shipment tracking history
     */
    public List<TrackingEventDTO> getShipmentTracking(Long shipmentId) {
        try {
            // Try cache first
            String trackingKey = SHIPMENT_TRACKING_KEY + shipmentId;
            List<TrackingEventDTO> cachedTracking = (List<TrackingEventDTO>) 
                    redisTemplate.opsForValue().get(trackingKey);

            if (cachedTracking != null) {
                return cachedTracking;
            }

            // Fallback to database
            List<TrackingEvent> events = trackingEventRepository
                    .findByShipmentIdOrderByTimestampDesc(shipmentId);

            List<TrackingEventDTO> eventDTOs = events.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            // Cache for 5 minutes
            redisTemplate.opsForValue().set(trackingKey, eventDTOs, 5, TimeUnit.MINUTES);

            return eventDTOs;

        } catch (Exception e) {
            log.error("Error getting shipment {} tracking: {}", shipmentId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Get driver location history
     */
    public List<LocationUpdateDTO> getDriverLocationHistory(Long driverId, int hours) {
        try {
            LocalDateTime since = LocalDateTime.now().minusHours(hours);
            
            // Get from database (location history is stored in database)
            List<LocationUpdateDTO> history = getDriverLocationsFromDatabase(driverId, since);

            return history;

        } catch (Exception e) {
            log.error("Error getting driver {} location history: {}", driverId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * Get all active drivers with their locations
     */
    public Map<Long, LocationUpdateDTO> getActiveDriversLocations() {
        try {
            Map<Long, LocationUpdateDTO> activeDrivers = new HashMap<>();
            
            // Get active driver IDs from Redis
            Set<String> activeDriverIds = redisTemplate.opsForSet().members(ACTIVE_DRIVERS_KEY);
            
            if (activeDriverIds != null) {
                for (String driverIdStr : activeDriverIds) {
                    Long driverId = Long.parseLong(driverIdStr);
                    LocationUpdateDTO location = getDriverCurrentLocation(driverId);
                    if (location != null) {
                        activeDrivers.put(driverId, location);
                    }
                }
            }

            return activeDrivers;

        } catch (Exception e) {
            log.error("Error getting active drivers locations: {}", e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    /**
     * Check for geofence events
     */
    private void checkGeofenceEvents(Long driverId, LocationUpdateDTO locationUpdate) {
        try {
            // Get driver's active shipments
            List<Shipment> activeShipments = shipmentRepository
                    .findByDriverIdAndStatusIn(driverId, 
                            Arrays.asList(ShipmentStatus.ASSIGNED, ShipmentStatus.PICKED_UP, ShipmentStatus.IN_TRANSIT));

            for (Shipment shipment : activeShipments) {
                checkShipmentGeofence(shipment, locationUpdate);
            }

        } catch (Exception e) {
            log.error("Error checking geofence events: {}", e.getMessage(), e);
        }
    }

    /**
     * Check geofence for specific shipment
     */
    private void checkShipmentGeofence(Shipment shipment, LocationUpdateDTO locationUpdate) {
        try {
            // Check if driver is near pickup location
            if (shipment.getStatus() == ShipmentStatus.ASSIGNED) {
                if (isNearLocation(locationUpdate, shipment.getPickupLocation(), 100)) { // 100m radius
                    // Trigger pickup proximity event
                    TrackingEventDTO proximityEvent = TrackingEventDTO.builder()
                            .eventType(TrackingEventType.PICKUP_PROXIMITY)
                            .description("Driver is near pickup location")
                            .location(convertToDTO(locationUpdate))
                            .build();

                    addTrackingEvent(shipment.getId(), proximityEvent);
                }
            }

            // Check if driver is near delivery location
            if (shipment.getStatus() == ShipmentStatus.IN_TRANSIT) {
                if (isNearLocation(locationUpdate, shipment.getDeliveryLocation(), 100)) { // 100m radius
                    // Trigger delivery proximity event
                    TrackingEventDTO proximityEvent = TrackingEventDTO.builder()
                            .eventType(TrackingEventType.DELIVERY_PROXIMITY)
                            .description("Driver is near delivery location")
                            .location(convertToDTO(locationUpdate))
                            .build();

                    addTrackingEvent(shipment.getId(), proximityEvent);
                }
            }

        } catch (Exception e) {
            log.error("Error checking shipment geofence: {}", e.getMessage(), e);
        }
    }

    /**
     * Check if location is near a target location
     */
    private boolean isNearLocation(LocationUpdateDTO current, Location target, double radiusMeters) {
        if (current == null || target == null) return false;

        double distance = calculateDistance(
                current.getLatitude(), current.getLongitude(),
                target.getLatitude(), target.getLongitude()
        );

        return distance <= radiusMeters;
    }

    /**
     * Calculate distance between two points using Haversine formula
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of earth in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c * 1000; // Convert to meters
    }

    /**
     * Check for automated events based on tracking event
     */
    private void checkAutomatedEvents(Shipment shipment, TrackingEvent event) {
        try {
            switch (event.getEventType()) {
                case PICKED_UP:
                    // Schedule delivery time estimation
                    scheduleDeliveryEstimation(shipment);
                    break;
                    
                case IN_TRANSIT:
                    // Start delay monitoring
                    startDelayMonitoring(shipment);
                    break;
                    
                case DELIVERED:
                    // Stop delay monitoring
                    stopDelayMonitoring(shipment);
                    // Trigger completion workflows
                    triggerCompletionWorkflows(shipment);
                    break;
                    
                case DELAYED:
                    // Send delay notifications
                    sendDelayNotifications(shipment);
                    break;
            }

        } catch (Exception e) {
            log.error("Error checking automated events: {}", e.getMessage(), e);
        }
    }

    /**
     * Schedule delivery estimation
     */
    private void scheduleDeliveryEstimation(Shipment shipment) {
        // Implementation for delivery time estimation
        log.info("Scheduling delivery estimation for shipment {}", shipment.getId());
    }

    /**
     * Start delay monitoring
     */
    private void startDelayMonitoring(Shipment shipment) {
        // Implementation for delay monitoring
        log.info("Starting delay monitoring for shipment {}", shipment.getId());
    }

    /**
     * Stop delay monitoring
     */
    private void stopDelayMonitoring(Shipment shipment) {
        // Implementation to stop delay monitoring
        log.info("Stopping delay monitoring for shipment {}", shipment.getId());
    }

    /**
     * Send delay notifications
     */
    private void sendDelayNotifications(Shipment shipment) {
        // Implementation for delay notifications
        log.info("Sending delay notifications for shipment {}", shipment.getId());
    }

    /**
     * Trigger completion workflows
     */
    private void triggerCompletionWorkflows(Shipment shipment) {
        // Implementation for completion workflows
        log.info("Triggering completion workflows for shipment {}", shipment.getId());
    }

    /**
     * Clean up old location data
     */
    @Scheduled(cron = "0 0 2 * * ?") // Every day at 2 AM
    public void cleanupOldLocationData() {
        try {
            log.info("Starting cleanup of old location data");
            
            // Clean up Redis location data older than 24 hours
            Set<String> keys = redisTemplate.keys(DRIVER_LOCATION_KEY + "*");
            if (keys != null) {
                for (String key : keys) {
                    Long ttl = redisTemplate.getExpire(key);
                    if (ttl != null && ttl <= 0) {
                        redisTemplate.delete(key);
                    }
                }
            }

            // Clean up database location history older than 30 days
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
            // Implementation for database cleanup
            
            log.info("Completed cleanup of old location data");

        } catch (Exception e) {
            log.error("Error during location data cleanup: {}", e.getMessage(), e);
        }
    }

    /**
     * Update shipment tracking cache
     */
    private void updateShipmentTrackingCache(Long shipmentId, TrackingEvent event) {
        try {
            String trackingKey = SHIPMENT_TRACKING_KEY + shipmentId;
            List<TrackingEventDTO> cachedTracking = (List<TrackingEventDTO>) 
                    redisTemplate.opsForValue().get(trackingKey);

            if (cachedTracking != null) {
                cachedTracking.add(0, convertToDTO(event));
                redisTemplate.opsForValue().set(trackingKey, cachedTracking, 5, TimeUnit.MINUTES);
            }

        } catch (Exception e) {
            log.error("Error updating shipment tracking cache: {}", e.getMessage(), e);
        }
    }

    /**
     * Save driver location to database
     */
    private void saveDriverLocationToDatabase(Long driverId, LocationUpdateDTO locationUpdate) {
        // Implementation for saving to database
        log.debug("Saving driver {} location to database", driverId);
    }

    /**
     * Get latest location from database
     */
    private LocationUpdateDTO getLatestLocationFromDatabase(Long driverId) {
        // Implementation for getting from database
        return null;
    }

    /**
     * Get driver locations from database
     */
    private List<LocationUpdateDTO> getDriverLocationsFromDatabase(Long driverId, LocalDateTime since) {
        // Implementation for getting from database
        return Collections.emptyList();
    }

    /**
     * Convert Location to LocationDTO
     */
    private LocationDTO convertToDTO(LocationUpdateDTO location) {
        if (location == null) return null;
        
        return LocationDTO.builder()
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .address(location.getAddress())
                .accuracy(location.getAccuracy())
                .timestamp(location.getTimestamp())
                .build();
    }

    /**
     * Convert Location to entity
     */
    private Location convertToLocation(LocationDTO locationDTO) {
        if (locationDTO == null) return null;
        
        return Location.builder()
                .latitude(locationDTO.getLatitude())
                .longitude(locationDTO.getLongitude())
                .address(locationDTO.getAddress())
                .accuracy(locationDTO.getAccuracy())
                .timestamp(locationDTO.getTimestamp())
                .build();
    }

    /**
     * Convert TrackingEvent to DTO
     */
    private TrackingEventDTO convertToDTO(TrackingEvent event) {
        if (event == null) return null;
        
        return TrackingEventDTO.builder()
                .id(event.getId())
                .shipmentId(event.getShipment().getId())
                .eventType(event.getEventType())
                .description(event.getDescription())
                .location(convertToDTO(event.getLocation()))
                .timestamp(event.getTimestamp())
                .createdBy(event.getCreatedBy())
                .build();
    }

    /**
     * Get current user from security context
     */
    private String getCurrentUser() {
        try {
            org.springframework.security.core.Authentication authentication = 
                    org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            return authentication != null ? authentication.getName() : "system";
        } catch (Exception e) {
            return "system";
        }
    }
}
