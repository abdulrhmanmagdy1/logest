package com.edham.logistics.controller;

import com.edham.logistics.dto.*;
import com.edham.logistics.service.*;
import com.edham.logistics.websocket.RealTimeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Mobile-specific real-time controller
 * Provides endpoints optimized for mobile applications
 */
@RestController
@RequestMapping("/api/v1/mobile/realtime")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class MobileRealTimeController {

    private final RealTimeService realTimeService;
    private final RealTimeTrackingService trackingService;
    private final ShipmentService shipmentService;
    private final UserService userService;

    @Autowired
    public MobileRealTimeController(RealTimeService realTimeService,
                                  RealTimeTrackingService trackingService,
                                  ShipmentService shipmentService,
                                  UserService userService) {
        this.realTimeService = realTimeService;
        this.trackingService = trackingService;
        this.shipmentService = shipmentService;
        this.userService = userService;
    }

    /**
     * Connect to real-time updates for mobile
     */
    @PostMapping("/connect")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> connect() {
        try {
            Long userId = getCurrentUserId();
            String username = getCurrentUsername();
            
            Map<String, Object> connectionInfo = Map.of(
                "success", true,
                "message", "Connected to real-time updates",
                "userId", userId,
                "username", username,
                "timestamp", java.time.LocalDateTime.now(),
                "endpoints", Map.of(
                    "shipmentUpdates", "/user/queue/shipment-updates",
                    "driverLocation", "/topic/driver/" + userId + "/location",
                    "notifications", "/user/queue/notifications",
                    "trackingEvents", "/topic/shipment/{shipmentId}/tracking"
                )
            );

            return ResponseEntity.ok(connectionInfo);

        } catch (Exception e) {
            log.error("Error connecting to real-time: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "error", "Connection failed"
            ));
        }
    }

    /**
     * Update driver location (mobile optimized)
     */
    @PostMapping("/driver/location")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Map<String, String>> updateDriverLocation(@Valid @RequestBody MobileLocationUpdateDTO locationUpdate) {
        try {
            Long driverId = getCurrentUserId();
            
            // Validate location data
            if (locationUpdate.getLatitude() == null || locationUpdate.getLongitude() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid location coordinates"
                ));
            }

            // Convert to standard DTO
            LocationUpdateDTO standardLocation = LocationUpdateDTO.builder()
                    .latitude(locationUpdate.getLatitude())
                    .longitude(locationUpdate.getLongitude())
                    .accuracy(locationUpdate.getAccuracy())
                    .speed(locationUpdate.getSpeed())
                    .heading(locationUpdate.getHeading())
                    .vehicleId(locationUpdate.getVehicleId())
                    .status(locationUpdate.getStatus())
                    .metadata(locationUpdate.getMetadata())
                    .build();

            // Update location
            trackingService.updateDriverLocation(driverId, standardLocation);

            return ResponseEntity.ok(Map.of(
                "success", "Location updated successfully",
                "timestamp", java.time.LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("Error updating driver location: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to update location"
            ));
        }
    }

    /**
     * Get shipment real-time tracking
     */
    @GetMapping("/shipment/{shipmentId}/tracking")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'DRIVER', 'SUPERVISOR') or @shipmentService.isShipmentOwner(#shipmentId, authentication.principal.id)")
    public ResponseEntity<Map<String, Object>> getShipmentTracking(@PathVariable Long shipmentId) {
        try {
            List<TrackingEventDTO> trackingEvents = trackingService.getShipmentTracking(shipmentId);
            Shipment shipment = shipmentService.getShipmentById(shipmentId);
            
            Map<String, Object> response = Map.of(
                "success", true,
                "shipment", Map.of(
                    "id", shipment.getId(),
                    "trackingNumber", shipment.getTrackingNumber(),
                    "status", shipment.getStatus(),
                    "customerName", shipment.getCustomer() != null ? shipment.getCustomer().getFullName() : null,
                    "driverName", shipment.getDriver() != null ? shipment.getDriver().getFullName() : null,
                    "pickupAddress", shipment.getPickupAddress(),
                    "deliveryAddress", shipment.getDeliveryAddress(),
                    "estimatedDelivery", shipment.getEstimatedDelivery()
                ),
                "trackingEvents", trackingEvents,
                "realTimeUpdates", Map.of(
                    "websocketTopic", "/topic/shipment/" + shipmentId + "/tracking",
                    "statusUpdates", "/user/queue/shipment-updates"
                )
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting shipment tracking: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to get tracking information"
            ));
        }
    }

    /**
     * Update shipment status (mobile optimized)
     */
    @PostMapping("/shipment/{shipmentId}/status")
    @PreAuthorize("hasAnyRole('DRIVER', 'SUPERVISOR')")
    public ResponseEntity<Map<String, String>> updateShipmentStatus(
            @PathVariable Long shipmentId,
            @RequestBody Map<String, Object> statusUpdate) {
        try {
            Long userId = getCurrentUserId();
            String status = (String) statusUpdate.get("status");
            String message = (String) statusUpdate.get("message");
            
            // Validate status
            try {
                ShipmentStatus newStatus = ShipmentStatus.valueOf(status.toUpperCase());
                
                // Update shipment status
                shipmentService.updateShipmentStatus(shipmentId, newStatus, message, userId);
                
                // Send real-time update
                ShipmentStatus oldStatus = shipmentService.getShipmentById(shipmentId).getStatus();
                realTimeService.sendShipmentStatusUpdate(shipmentId, oldStatus, newStatus);

                return ResponseEntity.ok(Map.of(
                    "success", "Status updated successfully",
                    "oldStatus", oldStatus.name(),
                    "newStatus", newStatus.name()
                ));

            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid shipment status"
                ));
            }

        } catch (Exception e) {
            log.error("Error updating shipment status: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to update shipment status"
            ));
        }
    }

    /**
     * Add tracking event (mobile optimized)
     */
    @PostMapping("/shipment/{shipmentId}/tracking-event")
    @PreAuthorize("hasAnyRole('DRIVER', 'SUPERVISOR')")
    public ResponseEntity<Map<String, String>> addTrackingEvent(
            @PathVariable Long shipmentId,
            @Valid @RequestBody MobileTrackingEventDTO trackingEventDTO) {
        try {
            // Convert to standard DTO
            TrackingEventDTO standardEvent = TrackingEventDTO.builder()
                    .eventType(trackingEventDTO.getEventType())
                    .description(trackingEventDTO.getDescription())
                    .location(trackingEventDTO.getLocation())
                    .metadata(trackingEventDTO.getMetadata())
                    .build();

            // Add tracking event
            trackingService.addTrackingEvent(shipmentId, standardEvent);

            return ResponseEntity.ok(Map.of(
                "success", "Tracking event added successfully",
                "eventId", standardEvent.getId()
            ));

        } catch (Exception e) {
            log.error("Error adding tracking event: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to add tracking event"
            ));
        }
    }

    /**
     * Get driver's active task with real-time updates
     */
    @GetMapping("/driver/active-task")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Map<String, Object>> getActiveTask() {
        try {
            Long driverId = getCurrentUserId();
            Shipment activeShipment = shipmentService.getActiveShipmentForDriver(driverId);
            
            if (activeShipment == null) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "hasActiveTask", false,
                    "message", "No active tasks"
                ));
            }

            Map<String, Object> activeTask = Map.of(
                "success", true,
                "hasActiveTask", true,
                "task", Map.of(
                    "shipmentId", activeShipment.getId(),
                    "trackingNumber", activeShipment.getTrackingNumber(),
                    "status", activeShipment.getStatus(),
                    "pickupAddress", activeShipment.getPickupAddress(),
                    "deliveryAddress", activeShipment.getDeliveryAddress(),
                    "customerName", activeShipment.getCustomer() != null ? activeShipment.getCustomer().getFullName() : null,
                    "estimatedDelivery", activeShipment.getEstimatedDelivery(),
                    "specialInstructions", activeShipment.getSpecialInstructions()
                ),
                "realTimeUpdates", Map.of(
                    "statusUpdates", "/user/queue/shipment-updates",
                    "locationTopic", "/topic/driver/" + driverId + "/location",
                    "trackingTopic", "/topic/shipment/" + activeShipment.getId() + "/tracking"
                )
            );

            return ResponseEntity.ok(activeTask);

        } catch (Exception e) {
            log.error("Error getting active task: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to get active task"
            ));
        }
    }

    /**
     * Get customer's shipments with real-time updates
     */
    @GetMapping("/customer/shipments")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Map<String, Object>> getCustomerShipments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        try {
            Long customerId = getCurrentUserId();
            
            // Get shipments with pagination
            List<Shipment> shipments = shipmentService.getCustomerShipments(customerId, page, size, status);
            
            Map<String, Object> response = Map.of(
                "success", true,
                "shipments", shipments.stream().map(shipment -> Map.of(
                    "id", shipment.getId(),
                    "trackingNumber", shipment.getTrackingNumber(),
                    "status", shipment.getStatus(),
                    "createdAt", shipment.getCreatedAt(),
                    "estimatedDelivery", shipment.getEstimatedDelivery(),
                    "pickupAddress", shipment.getPickupAddress(),
                    "deliveryAddress", shipment.getDeliveryAddress()
                )).toList(),
                "realTimeUpdates", Map.of(
                    "shipmentUpdates", "/user/queue/shipment-updates",
                    "notifications", "/user/queue/notifications"
                ),
                "pagination", Map.of(
                    "page", page,
                    "size", size,
                    "hasMore", shipments.size() == size
                )
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting customer shipments: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to get shipments"
            ));
        }
    }

    /**
     * Get real-time notifications for mobile
     */
    @GetMapping("/notifications")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type) {
        try {
            Long userId = getCurrentUserId();
            
            // Get notifications with pagination
            List<NotificationMessageDTO> notifications = userService.getUserNotifications(userId, page, size, type);
            
            Map<String, Object> response = Map.of(
                "success", true,
                "notifications", notifications,
                "realTimeUpdates", Map.of(
                    "notifications", "/user/queue/notifications"
                ),
                "pagination", Map.of(
                    "page", page,
                    "size", size,
                    "hasMore", notifications.size() == size
                )
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting notifications: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to get notifications"
            ));
        }
    }

    /**
     * Mark notifications as read
     */
    @PostMapping("/notifications/mark-read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> markNotificationsRead(@RequestBody List<Long> notificationIds) {
        try {
            Long userId = getCurrentUserId();
            
            // Mark notifications as read
            userService.markNotificationsRead(userId, notificationIds);

            return ResponseEntity.ok(Map.of(
                "success", "Notifications marked as read"
            ));

        } catch (Exception e) {
            log.error("Error marking notifications as read: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to mark notifications as read"
            ));
        }
    }

    /**
     * Get real-time fleet status for supervisors
     */
    @GetMapping("/supervisor/fleet-status")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> getFleetStatus() {
        try {
            Map<Long, LocationUpdateDTO> activeDrivers = trackingService.getActiveDriversLocations();
            
            Map<String, Object> response = Map.of(
                "success", true,
                "fleetStatus", Map.of(
                    "totalDrivers", userService.getDriverCount(),
                    "activeDrivers", activeDrivers.size(),
                    "onlineDrivers", activeDrivers.size(),
                    "activeShipments", shipmentService.getActiveShipmentsCount()
                ),
                "drivers", activeDrivers.entrySet().stream()
                        .map(entry -> Map.of(
                            "driverId", entry.getKey(),
                            "location", entry.getValue()
                        )).toList(),
                "realTimeUpdates", Map.of(
                    "driverLocations", "/topic/driver-locations",
                    "fleetStatus", "/topic/fleet-status",
                    "newShipments", "/topic/new-shipments"
                )
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting fleet status: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to get fleet status"
            ));
        }
    }

    /**
     * Get current user ID from security context
     */
    private Long getCurrentUserId() {
        try {
            org.springframework.security.core.Authentication authentication = 
                    org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
                String username = authentication.getName();
                return userService.findByUsername(username).map(user -> user.getId()).orElse(null);
            }
            return null;
        } catch (Exception e) {
            log.error("Error getting current user ID: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get current username from security context
     */
    private String getCurrentUsername() {
        try {
            org.springframework.security.core.Authentication authentication = 
                    org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            return authentication != null ? authentication.getName() : null;
        } catch (Exception e) {
            log.error("Error getting current username: {}", e.getMessage());
            return null;
        }
    }
}
