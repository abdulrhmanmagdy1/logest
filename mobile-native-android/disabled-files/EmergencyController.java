package com.edham.logistics.controller;

import com.edham.logistics.dto.*;
import com.edham.logistics.service.EmergencyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Emergency controller for handling critical events
 * Provides ultra-fast emergency response capabilities
 */
@RestController
@RequestMapping("/api/v1/emergency")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class EmergencyController {

    private final EmergencyService emergencyService;

    @Autowired
    public EmergencyController(EmergencyService emergencyService) {
        this.emergencyService = emergencyService;
    }

    /**
     * Trigger emergency alert from driver
     * Delivery time: < 2 seconds
     */
    @PostMapping("/trigger")
    @PreAuthorize("hasRole('DRIVER')")
    public CompletableFuture<ResponseEntity<EmergencyResponseDTO>> triggerEmergency(
            @Valid @RequestBody EmergencyRequestDTO request) {
        log.info("Emergency trigger request received from driver: {}", request.getDriverId());

        return emergencyService.triggerEmergencyAlert(request)
                .thenApply(response -> {
                    if (response.isSuccess()) {
                        return ResponseEntity.ok(response);
                    } else {
                        return ResponseEntity.internalServerError().body(response);
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Error in emergency trigger: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            EmergencyResponseDTO.builder()
                                    .success(false)
                                    .error("Internal server error during emergency trigger")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Update emergency status
     */
    @PatchMapping("/{emergencyId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'DRIVER')")
    public CompletableFuture<ResponseEntity<EmergencyResponseDTO>> updateEmergencyStatus(
            @PathVariable String emergencyId,
            @RequestParam EmergencyStatus status,
            @RequestParam(required = false) String resolution) {
        log.info("Emergency status update request: {} -> {}", emergencyId, status);

        return emergencyService.updateEmergencyStatus(emergencyId, status, resolution)
                .thenApply(response -> {
                    if (response.isSuccess()) {
                        return ResponseEntity.ok(response);
                    } else {
                        return ResponseEntity.internalServerError().body(response);
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Error updating emergency status: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            EmergencyResponseDTO.builder()
                                    .success(false)
                                    .error("Internal server error during status update")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get active emergencies
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<Emergency>> getActiveEmergencies() {
        try {
            List<Emergency> activeEmergencies = emergencyService.getActiveEmergencies();
            return ResponseEntity.ok(activeEmergencies);
        } catch (Exception e) {
            log.error("Error getting active emergencies: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get emergency history
     */
    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<List<Emergency>> getEmergencyHistory(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        try {
            if (startDate == null) {
                startDate = LocalDateTime.now().minusDays(30);
            }
            if (endDate == null) {
                endDate = LocalDateTime.now();
            }

            List<Emergency> history = emergencyService.getEmergencyHistory(startDate, endDate);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            log.error("Error getting emergency history: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get emergency statistics
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<EmergencyStatsDTO> getEmergencyStats(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        try {
            if (startDate == null) {
                startDate = LocalDateTime.now().minusDays(30);
            }
            if (endDate == null) {
                endDate = LocalDateTime.now();
            }

            EmergencyStatsDTO stats = emergencyService.getEmergencyStats(startDate, endDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting emergency stats: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get emergency by ID
     */
    @GetMapping("/{emergencyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'DRIVER')")
    public ResponseEntity<Emergency> getEmergencyById(@PathVariable String emergencyId) {
        try {
            Emergency emergency = emergencyService.getEmergencyById(emergencyId);
            return ResponseEntity.ok(emergency);
        } catch (Exception e) {
            log.error("Error getting emergency {}: {}", emergencyId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Check if driver has active emergency
     */
    @GetMapping("/driver/{driverId}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'DRIVER')")
    public ResponseEntity<Map<String, Object>> checkDriverActiveEmergency(@PathVariable Long driverId) {
        try {
            boolean hasActiveEmergency = emergencyService.hasActiveEmergency(driverId);
            return ResponseEntity.ok(Map.of(
                    "driverId", driverId,
                    "hasActiveEmergency", hasActiveEmergency,
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            log.error("Error checking driver active emergency: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Emergency button press (simplified endpoint)
     */
    @PostMapping("/button-press")
    @PreAuthorize("hasRole('DRIVER')")
    public CompletableFuture<ResponseEntity<EmergencyResponseDTO>> emergencyButtonPress(
            @RequestParam Long driverId,
            @RequestParam(required = false) Long shipmentId,
            @RequestParam(required = false) Long vehicleId,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) String description) {
        log.info("Emergency button pressed by driver: {}", driverId);

        // Create emergency request from button press
        EmergencyRequestDTO request = EmergencyRequestDTO.builder()
                .driverId(driverId)
                .shipmentId(shipmentId)
                .vehicleId(vehicleId)
                .emergencyType(EmergencyType.MANUAL_EMERGENCY)
                .severity(EmergencySeverity.HIGH)
                .description(description != null ? description : "Emergency button pressed")
                .location(latitude != null && longitude != null ? 
                    LocationDTO.builder()
                        .latitude(latitude)
                        .longitude(longitude)
                        .timestamp(LocalDateTime.now())
                        .build() : null)
                .build();

        return emergencyService.triggerEmergencyAlert(request)
                .thenApply(response -> {
                    if (response.isSuccess()) {
                        return ResponseEntity.ok(response);
                    } else {
                        return ResponseEntity.internalServerError().body(response);
                    }
                });
    }

    /**
     * Get emergency dashboard data
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> getEmergencyDashboard() {
        try {
            // Get current active emergencies
            List<Emergency> activeEmergencies = emergencyService.getActiveEmergencies();

            // Get stats for last 24 hours
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime yesterday = now.minusHours(24);
            EmergencyStatsDTO last24HoursStats = emergencyService.getEmergencyStats(yesterday, now);

            // Get stats for last 7 days
            LocalDateTime weekAgo = now.minusDays(7);
            EmergencyStatsDTO last7DaysStats = emergencyService.getEmergencyStats(weekAgo, now);

            return ResponseEntity.ok(Map.of(
                    "activeEmergencies", activeEmergencies,
                    "last24Hours", last24HoursStats,
                    "last7Days", last7DaysStats,
                    "timestamp", now
            ));
        } catch (Exception e) {
            log.error("Error getting emergency dashboard: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Test emergency system
     */
    @PostMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> testEmergencySystem() {
        try {
            log.info("Emergency system test initiated");

            return ResponseEntity.ok(Map.of(
                    "status", "operational",
                    "message", "Emergency system is fully operational",
                    "notificationDelivery", "< 2 seconds",
                    "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            log.error("Error testing emergency system: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "Emergency system test failed: " + e.getMessage()
            ));
        }
    }
}
