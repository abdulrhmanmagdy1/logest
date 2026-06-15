// // package com.edham.logistics.controller;

import com.edham.logistics.dto.*;
import com.edham.logistics.service.*;
import com.edham.logistics.websocket.RealTimeService;
import com.edham.logistics.websocket.WebSocketSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * REST Controller for real-time communication features
 * Provides endpoints for WebSocket-based live updates
 */
@RestController
@RequestMapping("/api/v1/realtime")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class RealTimeController {

    private final RealTimeService realTimeService;
    private final ShipmentService shipmentService;
    private final UserService userService;
    private final TrackingService trackingService;

    @Autowired
    public RealTimeController(RealTimeService realTimeService,
                             ShipmentService shipmentService,
                             UserService userService,
                             TrackingService trackingService) {
        this.realTimeService = realTimeService;
        this.shipmentService = shipmentService;
        this.userService = userService;
        this.trackingService = trackingService;
    }

    /**
     * Get real-time statistics
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'ACCOUNTANT')")
    public ResponseEntity<RealTimeStatsDTO> getRealTimeStats() {
        try {
            RealTimeStatsDTO stats = RealTimeStatsDTO.builder()
                    .timestamp(java.time.LocalDateTime.now())
                    .onlineUsers(WebSocketSessionManager.getOnlineUsers().size())
                    .onlineDrivers(WebSocketSessionManager.getOnlineUsersByRole("DRIVER").size())
                    .onlineCustomers(WebSocketSessionManager.getOnlineUsersByRole("CUSTOMER").size())
                    .onlineSupervisors(WebSocketSessionManager.getOnlineUsersByRole("SUPERVISOR").size())
                    .activeShipments(shipmentService.getActiveShipmentsCount())
                    .pendingShipments(shipmentService.getPendingShipmentsCount())
                    .inTransitShipments(shipmentService.getInTransitShipmentsCount())
                    .completedToday(shipmentService.getCompletedTodayCount())
                    .delayedShipments(shipmentService.getDelayedShipmentsCount())
                    .roleDistribution(Map.of(
                            "DRIVER", WebSocketSessionManager.getOnlineUsersByRole("DRIVER").size(),
                            "CUSTOMER", WebSocketSessionManager.getOnlineUsersByRole("CUSTOMER").size(),
                            "SUPERVISOR", WebSocketSessionManager.getOnlineUsersByRole("SUPERVISOR").size(),
                            "ACCOUNTANT", WebSocketSessionManager.getOnlineUsersByRole("ACCOUNTANT").size(),
                            "WORKSHOP", WebSocketSessionManager.getOnlineUsersByRole("WORKSHOP").size(),
                            "ADMIN", WebSocketSessionManager.getOnlineUsersByRole("ADMIN").size()
                    ))
                    .statusDistribution(shipmentService.getShipmentStatusDistribution())
                    .build();

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting real-time stats: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get online users by role
     */
    @GetMapping("/online-users/{role}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Set<String>> getOnlineUsersByRole(@PathVariable String role) {
        try {
            Set<String> onlineUsers = WebSocketSessionManager.getOnlineUsersByRole(role.toUpperCase());
            return ResponseEntity.ok(onlineUsers);
        } catch (Exception e) {
            log.error("Error getting online users for role {}: {}", role, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all online users
     */
    @GetMapping("/online-users")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Set<String>> getAllOnlineUsers() {
        try {
            Set<String> onlineUsers = WebSocketSessionManager.getOnlineUsers();
            return ResponseEntity.ok(onlineUsers);
        } catch (Exception e) {
            log.error("Error getting all online users: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Send custom notification
     */
    @PostMapping("/send-notification")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<String> sendNotification(@Valid @RequestBody NotificationMessageDTO notification) {
        try {
            realTimeService.sendSystemNotification(notification);
            return ResponseEntity.ok("Notification sent successfully");
        } catch (Exception e) {
            log.error("Error sending notification: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to send notification");
        }
    }

    /**
     * Send emergency alert
     */
    @PostMapping("/send-emergency-alert")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<String> sendEmergencyAlert(@Valid @RequestBody EmergencyAlertDTO alert) {
        try {
            realTimeService.sendEmergencyAlert(alert);
            return ResponseEntity.ok("Emergency alert sent successfully");
        } catch (Exception e) {
            log.error("Error sending emergency alert: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to send emergency alert");
        }
    }

    /**
     * Update driver location (for mobile apps)
     */
    @PostMapping("/driver/location")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<String> updateDriverLocation(@Valid @RequestBody LocationUpdateDTO locationUpdate) {
        try {
            Long driverId = getCurrentUserId();
            realTimeService.sendDriverLocationUpdate(driverId, locationUpdate);
            return ResponseEntity.ok("Location updated successfully");
        } catch (Exception e) {
            log.error("Error updating driver location: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to update location");
        }
    }

    /**
     * Get driver location history
     */
    @GetMapping("/driver/{driverId}/location-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR') or #driverId == authentication.principal.id")
    public ResponseEntity<List<LocationUpdateDTO>> getDriverLocationHistory(
            @PathVariable Long driverId,
            @RequestParam(defaultValue = "24") int hours) {
        try {
            List<LocationUpdateDTO> history = trackingService.getDriverLocationHistory(driverId, hours);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Error getting driver location history: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Send shipment status update
     */
    @PostMapping("/shipment/{shipmentId}/status-update")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'DRIVER')")
    public ResponseEntity<String> updateShipmentStatus(
            @PathVariable Long shipmentId,
            @RequestBody Map<String, Object> updateData) {
        try {
            ShipmentStatus oldStatus = shipmentService.getShipmentById(shipmentId).getStatus();
            ShipmentStatus newStatus = ShipmentStatus.valueOf((String) updateData.get("status"));
            String message = (String) updateData.get("message");

            // Update shipment status
            shipmentService.updateShipmentStatus(shipmentId, newStatus, message);

            // Send real-time update
            realTimeService.sendShipmentStatusUpdate(shipmentId, oldStatus, newStatus);

            return ResponseEntity.ok("Shipment status updated successfully");
        } catch (Exception e) {
            log.error("Error updating shipment status: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to update shipment status");
        }
    }

    /**
     * Add tracking event
     */
    @PostMapping("/shipment/{shipmentId}/tracking-event")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'DRIVER')")
    public ResponseEntity<String> addTrackingEvent(
            @PathVariable Long shipmentId,
            @Valid @RequestBody TrackingEventDTO trackingEvent) {
        try {
            // Save tracking event
            TrackingEvent savedEvent = trackingService.createTrackingEvent(trackingEvent);

            // Send real-time update
            realTimeService.sendTrackingEventUpdate(shipmentId, savedEvent);

            return ResponseEntity.ok("Tracking event added successfully");
        } catch (Exception e) {
            log.error("Error adding tracking event: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to add tracking event");
        }
    }

    /**
     * Get real-time shipment tracking
     */
    @GetMapping("/shipment/{shipmentId}/tracking")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR') or @shipmentService.isShipmentOwner(#shipmentId, authentication.principal.id)")
    public ResponseEntity<List<TrackingEventDTO>> getShipmentTracking(@PathVariable Long shipmentId) {
        try {
            List<TrackingEventDTO> trackingEvents = trackingService.getShipmentTracking(shipmentId);
            return ResponseEntity.ok(trackingEvents);
        } catch (Exception e) {
            log.error("Error getting shipment tracking: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get fleet status
     */
    @GetMapping("/fleet/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'WORKSHOP')")
    public ResponseEntity<FleetStatusDTO> getFleetStatus() {
        try {
            FleetStatusDTO fleetStatus = FleetStatusDTO.builder()
                    .totalVehicles(userService.getVehicleCount())
                    .onlineVehicles(WebSocketSessionManager.getOnlineUsersByRole("DRIVER").size())
                    .activeVehicles(shipmentService.getActiveVehiclesCount())
                    .maintenanceVehicles(userService.getMaintenanceVehiclesCount())
                    .offlineVehicles(userService.getOfflineVehiclesCount())
                    .timestamp(java.time.LocalDateTime.now())
                    .build();

            realTimeService.sendFleetStatusUpdate(fleetStatus);
            return ResponseEntity.ok(fleetStatus);
        } catch (Exception e) {
            log.error("Error getting fleet status: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Send custom message to user
     */
    @PostMapping("/send-message")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<String> sendMessageToUser(@RequestBody Map<String, Object> messageData) {
        try {
            String username = (String) messageData.get("username");
            String destination = (String) messageData.get("destination");
            Object message = messageData.get("message");

            realTimeService.sendToUser(username, destination, message);
            return ResponseEntity.ok("Message sent successfully");
        } catch (Exception e) {
            log.error("Error sending message to user: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to send message");
        }
    }

    /**
     * Broadcast message to all users
     */
    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> broadcastMessage(@RequestBody Map<String, Object> broadcastData) {
        try {
            String destination = (String) broadcastData.get("destination");
            Object message = broadcastData.get("message");

            realTimeService.broadcast(destination, message);
            return ResponseEntity.ok("Message broadcasted successfully");
        } catch (Exception e) {
            log.error("Error broadcasting message: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to broadcast message");
        }
    }

    /**
     * Get WebSocket connection info
     */
    @GetMapping("/connection-info")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<WebSocketSessionManager.SessionStats> getConnectionInfo() {
        try {
            WebSocketSessionManager.SessionStats stats = WebSocketSessionManager.getSessionStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting connection info: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Test WebSocket connection
     */
    @PostMapping("/test-connection")
    public ResponseEntity<String> testConnection() {
        try {
            realTimeService.broadcast("/topic/test", Map.of(
                    "message", "WebSocket connection test successful",
                    "timestamp", java.time.LocalDateTime.now(),
                    "status", "connected"
            ));
            return ResponseEntity.ok("Test message sent");
        } catch (Exception e) {
            log.error("Error testing WebSocket connection: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Test failed");
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
                // Extract user ID from custom user details or database
                String username = authentication.getName();
                return userService.findByUsername(username).map(user -> user.getId()).orElse(null);
            }
            return null;
        } catch (Exception e) {
            log.error("Error getting current user ID: {}", e.getMessage());
            return null;
        }
    }
}
