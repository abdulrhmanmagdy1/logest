package com.edham.logistics.controller;

import com.edham.logistics.dto.*;
import com.edham.logistics.websocket.RealTimeService;
import com.edham.logistics.websocket.WebSocketSessionManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

/**
 * Controller for testing real-time WebSocket functionality
 * Provides endpoints for testing and debugging real-time features
 */
@RestController
@RequestMapping("/api/v1/realtime/test")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class RealTimeTestController {

    private final RealTimeService realTimeService;

    @Autowired
    public RealTimeTestController(RealTimeService realTimeService) {
        this.realTimeService = realTimeService;
    }

    /**
     * Test WebSocket connection
     */
    @PostMapping("/connection")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> testConnection() {
        try {
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "WebSocket connection test successful",
                "timestamp", LocalDateTime.now(),
                "serverStatus", "online",
                "websocketEndpoint", "/ws",
                "testMessage", "Hello from real-time system!"
            );

            // Send test message via WebSocket
            realTimeService.broadcast("/topic/test", response);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error testing WebSocket connection: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Connection test failed",
                "timestamp", LocalDateTime.now()
            ));
        }
    }

    /**
     * Test shipment status update
     */
    @PostMapping("/shipment-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'DRIVER')")
    public ResponseEntity<Map<String, String>> testShipmentStatusUpdate(
            @RequestParam Long shipmentId,
            @RequestParam String status) {
        try {
            ShipmentStatus newStatus = ShipmentStatus.valueOf(status.toUpperCase());
            ShipmentStatus oldStatus = ShipmentStatus.PENDING; // Mock old status

            // Send real-time update
            realTimeService.sendShipmentStatusUpdate(shipmentId, oldStatus, newStatus);

            return ResponseEntity.ok(Map.of(
                "success", "Shipment status update test sent",
                "shipmentId", shipmentId.toString(),
                "oldStatus", oldStatus.name(),
                "newStatus", newStatus.name(),
                "timestamp", LocalDateTime.now().toString()
            ));

        } catch (Exception e) {
            log.error("Error testing shipment status update: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to test shipment status update"
            ));
        }
    }

    /**
     * Test driver location update
     */
    @PostMapping("/driver-location")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'DRIVER')")
    public ResponseEntity<Map<String, String>> testDriverLocationUpdate(
            @RequestParam Long driverId,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        try {
            LocationUpdateDTO locationUpdate = LocationUpdateDTO.builder()
                    .driverId(driverId)
                    .latitude(latitude)
                    .longitude(longitude)
                    .accuracy(10.0)
                    .speed(50.0)
                    .heading(90.0)
                    .status("ACTIVE")
                    .timestamp(LocalDateTime.now())
                    .build();

            // Send real-time location update
            realTimeService.sendDriverLocationUpdate(driverId, locationUpdate);

            return ResponseEntity.ok(Map.of(
                "success", "Driver location update test sent",
                "driverId", driverId.toString(),
                "latitude", latitude.toString(),
                "longitude", longitude.toString(),
                "timestamp", LocalDateTime.now().toString()
            ));

        } catch (Exception e) {
            log.error("Error testing driver location update: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to test driver location update"
            ));
        }
    }

    /**
     * Test system notification
     */
    @PostMapping("/notification")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Map<String, String>> testNotification(
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam(required = false) String targetRole) {
        try {
            NotificationMessageDTO notification = NotificationMessageDTO.builder()
                    .id("test-" + System.currentTimeMillis())
                    .title(title)
                    .message(message)
                    .type(NotificationType.SYSTEM)
                    .priority(NotificationPriority.NORMAL)
                    .timestamp(LocalDateTime.now())
                    .requiresAction(false)
                    .build();

            if (targetRole != null && !targetRole.isEmpty()) {
                notification.setTargetRoles(Set.of(targetRole.toUpperCase()));
            }

            // Send notification
            realTimeService.sendSystemNotification(notification);

            return ResponseEntity.ok(Map.of(
                "success", "Notification test sent",
                "title", title,
                "message", message,
                "targetRole", targetRole != null ? targetRole : "ALL",
                "timestamp", LocalDateTime.now().toString()
            ));

        } catch (Exception e) {
            log.error("Error testing notification: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to test notification"
            ));
        }
    }

    /**
     * Test emergency alert
     */
    @PostMapping("/emergency-alert")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Map<String, String>> testEmergencyAlert(
            @RequestParam String alertType,
            @RequestParam String message,
            @RequestParam(required = false) Long vehicleId) {
        try {
            EmergencyAlertType type = EmergencyAlertType.valueOf(alertType.toUpperCase());
            
            EmergencyAlertDTO alert = EmergencyAlertDTO.builder()
                    .id("test-" + System.currentTimeMillis())
                    .type(type)
                    .title("Test Emergency Alert")
                    .message(message)
                    .vehicleId(vehicleId)
                    .timestamp(LocalDateTime.now())
                    .severity(AlertSeverity.HIGH)
                    .requiresImmediateAction(false)
                    .build();

            // Send emergency alert
            realTimeService.sendEmergencyAlert(alert);

            return ResponseEntity.ok(Map.of(
                "success", "Emergency alert test sent",
                "alertType", alertType,
                "message", message,
                "vehicleId", vehicleId != null ? vehicleId.toString() : "N/A",
                "timestamp", LocalDateTime.now().toString()
            ));

        } catch (Exception e) {
            log.error("Error testing emergency alert: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to test emergency alert"
            ));
        }
    }

    /**
     * Test fleet status update
     */
    @PostMapping("/fleet-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'WORKSHOP')")
    public ResponseEntity<Map<String, String>> testFleetStatus() {
        try {
            FleetStatusDTO fleetStatus = FleetStatusDTO.builder()
                    .totalVehicles(100)
                    .onlineVehicles(75)
                    .activeVehicles(60)
                    .maintenanceVehicles(10)
                    .offlineVehicles(15)
                    .timestamp(LocalDateTime.now())
                    .build();

            // Send fleet status update
            realTimeService.sendFleetStatusUpdate(fleetStatus);

            return ResponseEntity.ok(Map.of(
                "success", "Fleet status test sent",
                "totalVehicles", "100",
                "onlineVehicles", "75",
                "activeVehicles", "60",
                "maintenanceVehicles", "10",
                "offlineVehicles", "15",
                "timestamp", LocalDateTime.now().toString()
            ));

        } catch (Exception e) {
            log.error("Error testing fleet status: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to test fleet status"
            ));
        }
    }

    /**
     * Get WebSocket session statistics
     */
    @GetMapping("/session-stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<WebSocketSessionManager.SessionStats> getSessionStats() {
        try {
            WebSocketSessionManager.SessionStats stats = WebSocketSessionManager.getSessionStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting session stats: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Test broadcast to all users
     */
    @PostMapping("/broadcast")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> testBroadcast(
            @RequestParam String message,
            @RequestParam(required = false) String topic) {
        try {
            String destination = topic != null ? "/topic/" + topic : "/topic/test";
            
            Map<String, Object> broadcastMessage = Map.of(
                "type", "broadcast",
                "message", message,
                "timestamp", LocalDateTime.now(),
                "sender", "system"
            );

            // Broadcast message
            realTimeService.broadcast(destination, broadcastMessage);

            return ResponseEntity.ok(Map.of(
                "success", "Broadcast test sent",
                "message", message,
                "destination", destination,
                "timestamp", LocalDateTime.now().toString()
            ));

        } catch (Exception e) {
            log.error("Error testing broadcast: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to test broadcast"
            ));
        }
    }

    /**
     * Test sending message to specific user
     */
    @PostMapping("/send-to-user")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Map<String, String>> testSendToUser(
            @RequestParam String username,
            @RequestParam String message) {
        try {
            Map<String, Object> userMessage = Map.of(
                "type", "direct-message",
                "message", message,
                "timestamp", LocalDateTime.now(),
                "sender", "system"
            );

            // Send to specific user
            realTimeService.sendToUser(username, "/queue/test", userMessage);

            return ResponseEntity.ok(Map.of(
                "success", "Direct message test sent",
                "username", username,
                "message", message,
                "timestamp", LocalDateTime.now().toString()
            ));

        } catch (Exception e) {
            log.error("Error testing direct message: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to test direct message"
            ));
        }
    }

    /**
     * Test new shipment notification
     */
    @PostMapping("/new-shipment")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Map<String, String>> testNewShipment() {
        try {
            NewShipmentDTO newShipment = NewShipmentDTO.builder()
                    .shipmentId(999L)
                    .trackingNumber("TEST-123456")
                    .customerName("Test Customer")
                    .priority(ShipmentPriority.NORMAL)
                    .estimatedDelivery(LocalDateTime.now().plusDays(3))
                    .createdAt(LocalDateTime.now())
                    .specialInstructions("Test shipment for real-time system")
                    .build();

            // Send new shipment notification
            realTimeService.sendNewShipmentNotification(null); // Pass null for test

            return ResponseEntity.ok(Map.of(
                "success", "New shipment test notification sent",
                "trackingNumber", "TEST-123456",
                "customerName", "Test Customer",
                "timestamp", LocalDateTime.now().toString()
            ));

        } catch (Exception e) {
            log.error("Error testing new shipment notification: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to test new shipment notification"
            ));
        }
    }

    /**
     * Get real-time system health
     */
    @GetMapping("/health")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getRealTimeHealth() {
        try {
            WebSocketSessionManager.SessionStats stats = WebSocketSessionManager.getSessionStats();
            
            Map<String, Object> health = Map.of(
                "status", "healthy",
                "websocket", "online",
                "messageBroker", "operational",
                "timestamp", LocalDateTime.now(),
                "sessionStats", Map.of(
                    "totalUsers", stats.getTotalUsers(),
                    "totalSessions", stats.getTotalSessions(),
                    "onlineDrivers", stats.getTotalDrivers(),
                    "onlineCustomers", stats.getTotalCustomers(),
                    "onlineSupervisors", stats.getTotalSupervisors()
                ),
                "endpoints", Map.of(
                    "websocket", "/ws",
                    "test", "/api/v1/realtime/test",
                    "mobile", "/api/v1/mobile/realtime"
                )
            );

            return ResponseEntity.ok(health);

        } catch (Exception e) {
            log.error("Error getting real-time health: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "status", "unhealthy",
                "error", e.getMessage(),
                "timestamp", LocalDateTime.now()
            ));
        }
    }

    /**
     * Stress test WebSocket connections
     */
    @PostMapping("/stress-test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> stressTest(
            @RequestParam(defaultValue = "10") int messageCount,
            @RequestParam(defaultValue = "100") int messageInterval) {
        try {
            log.info("Starting WebSocket stress test: {} messages with {}ms interval", 
                    messageCount, messageInterval);

            for (int i = 0; i < messageCount; i++) {
                Map<String, Object> stressMessage = Map.of(
                        "type", "stress-test",
                        "messageId", i,
                        "timestamp", LocalDateTime.now(),
                        "testData", "Stress test message " + i
                );

                realTimeService.broadcast("/topic/stress-test", stressMessage);

                if (messageInterval > 0) {
                    Thread.sleep(messageInterval);
                }
            }

            return ResponseEntity.ok(Map.of(
                "success", "Stress test completed",
                "messageCount", String.valueOf(messageCount),
                "messageInterval", String.valueOf(messageInterval),
                "timestamp", LocalDateTime.now().toString()
            ));

        } catch (Exception e) {
            log.error("Error during stress test: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Stress test failed"
            ));
        }
    }
}
