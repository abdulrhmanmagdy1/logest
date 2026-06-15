// // package com.edham.logistics.coldchain;

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
 * Cold-chain monitoring controller
 * Provides REST API for temperature tracking, alerts, and role-based visibility
 */
@RestController
@RequestMapping("/api/v1/coldchain")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class ColdChainController {

    private final ColdChainMonitoringService coldChainService;
    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;

    @Autowired
    public ColdChainController(ColdChainMonitoringService coldChainService,
                             ShipmentRepository shipmentRepository,
                             UserRepository userRepository) {
        this.coldChainService = coldChainService;
        this.shipmentRepository = shipmentRepository;
        this.userRepository = userRepository;
    }

    /**
     * Start cold-chain monitoring for shipment
     */
    @PostMapping("/monitoring/start/{shipmentId}")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN', 'SUPERVISOR')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<ColdChainMonitoringService.ColdChainSession>>> startMonitoring(
            @PathVariable Long shipmentId,
            @RequestParam String productType) {
        
        // Validate access
        if (!canAccessShipment(shipmentId)) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(403).body(
                            UnifiedResponseDTO.<ColdChainMonitoringService.ColdChainSession>builder()
                                    .success(false)
                                    .error("Access denied for shipment: " + shipmentId)
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    )
            );
        }
        
        return coldChainService.startMonitoring(shipmentId, productType)
                .thenApply(session -> {
                    log.info("Cold-chain monitoring started for shipment: {}, session: {}", shipmentId, session.getSessionId());
                    return ResponseEntity.ok(
                            UnifiedResponseDTO.<ColdChainMonitoringService.ColdChainSession>builder()
                                    .success(true)
                                    .data(session)
                                    .message("Cold-chain monitoring started successfully")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Error starting cold-chain monitoring for shipment {}: {}", shipmentId, throwable.getMessage(), throwable);
                    return ResponseEntity.badRequest().body(
                            UnifiedResponseDTO.<ColdChainMonitoringService.ColdChainSession>builder()
                                    .success(false)
                                    .error("Failed to start monitoring: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Stop cold-chain monitoring for shipment
     */
    @PostMapping("/monitoring/stop/{shipmentId}")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN', 'SUPERVISOR')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<String>>> stopMonitoring(
            @PathVariable Long shipmentId) {
        
        // Validate access
        if (!canAccessShipment(shipmentId)) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(403).body(
                            UnifiedResponseDTO.<String>builder()
                                    .success(false)
                                    .error("Access denied for shipment: " + shipmentId)
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    )
            );
        }
        
        return coldChainService.stopMonitoring(shipmentId)
                .thenApply(v -> {
                    log.info("Cold-chain monitoring stopped for shipment: {}", shipmentId);
                    return ResponseEntity.ok(
                            UnifiedResponseDTO.<String>builder()
                                    .success(true)
                                    .data("Cold-chain monitoring stopped successfully")
                                    .message("Monitoring stopped")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Error stopping cold-chain monitoring for shipment {}: {}", shipmentId, throwable.getMessage(), throwable);
                    return ResponseEntity.badRequest().body(
                            UnifiedResponseDTO.<String>builder()
                                    .success(false)
                                    .error("Failed to stop monitoring: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Record temperature reading
     */
    @PostMapping("/temperature/record")
    @PreAuthorize("hasAnyRole('DRIVER', 'ADMIN')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<ColdChainMonitoringService.TemperatureReadingResult>>> recordTemperature(
            @RequestBody ColdChainMonitoringService.TemperatureReadingRequest request) {
        
        Long shipmentId = request.getShipmentId();
        
        // Validate access
        if (!canAccessShipment(shipmentId)) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(403).body(
                            UnifiedResponseDTO.<ColdChainMonitoringService.TemperatureReadingResult>builder()
                                    .success(false)
                                    .error("Access denied for shipment: " + shipmentId)
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    )
            );
        }
        
        return coldChainService.recordTemperature(request)
                .thenApply(result -> {
                    if (result.getSuccess()) {
                        log.debug("Temperature recorded for shipment: {}, temperature: {}", shipmentId, request.getTemperature());
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<ColdChainMonitoringService.TemperatureReadingResult>builder()
                                        .success(true)
                                        .data(result)
                                        .message("Temperature recorded successfully")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    } else {
                        log.warn("Temperature recording failed for shipment: {}, error: {}", shipmentId, result.getError());
                        return ResponseEntity.badRequest().body(
                                UnifiedResponseDTO.<ColdChainMonitoringService.TemperatureReadingResult>builder()
                                        .success(false)
                                        .error(result.getError())
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Error recording temperature for shipment {}: {}", shipmentId, throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<ColdChainMonitoringService.TemperatureReadingResult>builder()
                                    .success(false)
                                    .error("Internal server error: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get current temperature for shipment
     */
    @GetMapping("/temperature/current/{shipmentId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'SUPERVISOR')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<TemperatureReading>>> getCurrentTemperature(
            @PathVariable Long shipmentId) {
        
        // Validate access
        if (!canViewShipment(shipmentId)) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(403).body(
                            UnifiedResponseDTO.<TemperatureReading>builder()
                                    .success(false)
                                    .error("Access denied for shipment: " + shipmentId)
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    )
            );
        }
        
        return coldChainService.getCurrentTemperature(shipmentId)
                .thenApply(reading -> {
                    if (reading != null) {
                        log.debug("Current temperature retrieved for shipment: {}, temperature: {}", shipmentId, reading.getTemperature());
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<TemperatureReading>builder()
                                        .success(true)
                                        .data(reading)
                                        .message("Current temperature retrieved successfully")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    } else {
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<TemperatureReading>builder()
                                        .success(true)
                                        .data(null)
                                        .message("No temperature data available")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Error getting current temperature for shipment {}: {}", shipmentId, throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<TemperatureReading>builder()
                                    .success(false)
                                    .error("Failed to get current temperature: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get temperature history for shipment
     */
    @GetMapping("/temperature/history/{shipmentId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'SUPERVISOR')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<List<TemperatureReading>>>> getTemperatureHistory(
            @PathVariable Long shipmentId,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime) {
        
        // Validate access
        if (!canViewShipment(shipmentId)) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(403).body(
                            UnifiedResponseDTO.<List<TemperatureReading>>builder()
                                    .success(false)
                                    .error("Access denied for shipment: " + shipmentId)
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    )
            );
        }
        
        return coldChainService.getTemperatureHistory(shipmentId, startTime, endTime)
                .thenApply(history -> {
                    log.debug("Temperature history retrieved for shipment: {}, records: {}", shipmentId, history.size());
                    return ResponseEntity.ok(
                            UnifiedResponseDTO.<List<TemperatureReading>>builder()
                                    .success(true)
                                    .data(history)
                                    .message("Temperature history retrieved successfully")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Error getting temperature history for shipment {}: {}", shipmentId, throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<List<TemperatureReading>>builder()
                                    .success(false)
                                    .error("Failed to get temperature history: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get alerts for shipment
     */
    @GetMapping("/alerts/{shipmentId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'SUPERVISOR')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<List<ColdChainAlert>>>> getAlerts(
            @PathVariable Long shipmentId,
            @RequestParam(required = false) ColdChainMonitoringService.AlertSeverity severity) {
        
        // Validate access
        if (!canViewShipment(shipmentId)) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(403).body(
                            UnifiedResponseDTO.<List<ColdChainAlert>>builder()
                                    .success(false)
                                    .error("Access denied for shipment: " + shipmentId)
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    )
            );
        }
        
        return coldChainService.getAlerts(shipmentId, severity)
                .thenApply(alerts -> {
                    log.debug("Alerts retrieved for shipment: {}, count: {}", shipmentId, alerts.size());
                    return ResponseEntity.ok(
                            UnifiedResponseDTO.<List<ColdChainAlert>>builder()
                                    .success(true)
                                    .data(alerts)
                                    .message("Alerts retrieved successfully")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Error getting alerts for shipment {}: {}", shipmentId, throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<List<ColdChainAlert>>builder()
                                    .success(false)
                                    .error("Failed to get alerts: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get cold-chain statistics for shipment
     */
    @GetMapping("/statistics/{shipmentId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'SUPERVISOR')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<ColdChainMonitoringService.ColdChainStatistics>>> getStatistics(
            @PathVariable Long shipmentId) {
        
        // Validate access
        if (!canViewShipment(shipmentId)) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(403).body(
                            UnifiedResponseDTO.<ColdChainMonitoringService.ColdChainStatistics>builder()
                                    .success(false)
                                    .error("Access denied for shipment: " + shipmentId)
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    )
            );
        }
        
        return coldChainService.getStatistics(shipmentId)
                .thenApply(statistics -> {
                    if (statistics != null) {
                        log.debug("Statistics retrieved for shipment: {}", shipmentId);
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<ColdChainMonitoringService.ColdChainStatistics>builder()
                                        .success(true)
                                        .data(statistics)
                                        .message("Statistics retrieved successfully")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    } else {
                        return ResponseEntity.ok(
                                UnifiedResponseDTO.<ColdChainMonitoringService.ColdChainStatistics>builder()
                                        .success(true)
                                        .data(null)
                                        .message("No statistics available")
                                        .timestamp(LocalDateTime.now())
                                        .build()
                        );
                    }
                })
                .exceptionally(throwable -> {
                    log.error("Error getting statistics for shipment {}: {}", shipmentId, throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<ColdChainMonitoringService.ColdChainStatistics>builder()
                                    .success(false)
                                    .error("Failed to get statistics: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get active monitoring sessions
     */
    @GetMapping("/monitoring/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<List<ColdChainMonitoringService.ColdChainSession>>>> getActiveSessions() {
        
        return coldChainService.getActiveSessions()
                .thenApply(sessions -> {
                    log.debug("Active monitoring sessions retrieved: {}", sessions.size());
                    return ResponseEntity.ok(
                            UnifiedResponseDTO.<List<ColdChainMonitoringService.ColdChainSession>>builder()
                                    .success(true)
                                    .data(sessions)
                                    .message("Active sessions retrieved successfully")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Error getting active monitoring sessions: {}", throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<List<ColdChainMonitoringService.ColdChainSession>>builder()
                                    .success(false)
                                    .error("Failed to get active sessions: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    /**
     * Get cold-chain dashboard for user role
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'SUPERVISOR')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<Map<String, Object>>>> getDashboard() {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                Long currentUserId = getCurrentUserId();
                UserRole userRole = getCurrentUserRole();
                
                Map<String, Object> dashboard = new HashMap<>();
                
                switch (userRole) {
                    case CLIENT:
                        dashboard = getClientDashboard(currentUserId);
                        break;
                    case DRIVER:
                        dashboard = getDriverDashboard(currentUserId);
                        break;
                    case ADMIN:
                    case SUPERVISOR:
                        dashboard = getAdminDashboard();
                        break;
                    default:
                        dashboard = new HashMap<>();
                }
                
                log.debug("Dashboard retrieved for user: {}, role: {}", currentUserId, userRole);
                
                return ResponseEntity.ok(
                        UnifiedResponseDTO.<Map<String, Object>>builder()
                                .success(true)
                                .data(dashboard)
                                .message("Dashboard retrieved successfully")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
                
            } catch (Exception e) {
                log.error("Error getting dashboard: {}", e.getMessage(), e);
                return ResponseEntity.internalServerError().body(
                        UnifiedResponseDTO.<Map<String, Object>>builder()
                                .success(false)
                                .error("Failed to get dashboard: " + e.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
            }
        });
    }

    /**
     * Get client dashboard
     */
    private Map<String, Object> getClientDashboard(Long clientId) {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Get client's shipments
        List<Shipment> shipments = shipmentRepository.findByCustomerIdOrderByCreatedAtDesc(clientId);
        
        // Calculate statistics
        int totalShipments = shipments.size();
        int activeMonitoring = 0;
        int criticalAlerts = 0;
        int warningAlerts = 0;
        
        for (Shipment shipment : shipments) {
            if (shipment.getColdChainStatus() == ColdChainStatus.MONITORING) {
                activeMonitoring++;
            }
            
            // Count alerts (simplified - would need alert repository)
            // criticalAlerts += alertRepository.countByShipmentIdAndSeverity(shipment.getId(), AlertSeverity.CRITICAL);
            // warningAlerts += alertRepository.countByShipmentIdAndSeverity(shipment.getId(), AlertSeverity.WARNING);
        }
        
        dashboard.put("totalShipments", totalShipments);
        dashboard.put("activeMonitoring", activeMonitoring);
        dashboard.put("criticalAlerts", criticalAlerts);
        dashboard.put("warningAlerts", warningAlerts);
        dashboard.put("safeShipments", totalShipments - criticalAlerts - warningAlerts);
        dashboard.put("recentShipments", shipments.stream().limit(5).map(this::shipmentToSummary).toList());
        
        return dashboard;
    }

    /**
     * Get driver dashboard
     */
    private Map<String, Object> getDriverDashboard(Long driverId) {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Get driver's shipments
        List<Shipment> shipments = shipmentRepository.findByDriverIdOrderByCreatedAtDesc(driverId);
        
        // Calculate statistics
        int totalShipments = shipments.size();
        int activeMonitoring = 0;
        int criticalAlerts = 0;
        int warningAlerts = 0;
        
        for (Shipment shipment : shipments) {
            if (shipment.getColdChainStatus() == ColdChainStatus.MONITORING) {
                activeMonitoring++;
            }
            
            // Count alerts
            // criticalAlerts += alertRepository.countByShipmentIdAndSeverity(shipment.getId(), AlertSeverity.CRITICAL);
            // warningAlerts += alertRepository.countByShipmentIdAndSeverity(shipment.getId(), AlertSeverity.WARNING);
        }
        
        dashboard.put("totalShipments", totalShipments);
        dashboard.put("activeMonitoring", activeMonitoring);
        dashboard.put("criticalAlerts", criticalAlerts);
        dashboard.put("warningAlerts", warningAlerts);
        dashboard.put("safeShipments", totalShipments - criticalAlerts - warningAlerts);
        dashboard.put("currentShipments", shipments.stream()
                .filter(s -> s.getColdChainStatus() == ColdChainStatus.MONITORING)
                .limit(5)
                .map(this::shipmentToSummary)
                .toList());
        
        return dashboard;
    }

    /**
     * Get admin dashboard
     */
    private Map<String, Object> getAdminDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Get all shipments
        List<Shipment> shipments = shipmentRepository.findAll();
        
        // Calculate global statistics
        int totalShipments = shipments.size();
        int activeMonitoring = 0;
        int criticalAlerts = 0;
        int warningAlerts = 0;
        
        Map<String, Integer> productTypeStats = new HashMap<>();
        
        for (Shipment shipment : shipments) {
            if (shipment.getColdChainStatus() == ColdChainStatus.MONITORING) {
                activeMonitoring++;
            }
            
            // Product type statistics
            String productType = shipment.getProductType();
            productTypeStats.put(productType, productTypeStats.getOrDefault(productType, 0) + 1);
            
            // Count alerts
            // criticalAlerts += alertRepository.countByShipmentIdAndSeverity(shipment.getId(), AlertSeverity.CRITICAL);
            // warningAlerts += alertRepository.countByShipmentIdAndSeverity(shipment.getId(), AlertSeverity.WARNING);
        }
        
        dashboard.put("totalShipments", totalShipments);
        dashboard.put("activeMonitoring", activeMonitoring);
        dashboard.put("criticalAlerts", criticalAlerts);
        dashboard.put("warningAlerts", warningAlerts);
        dashboard.put("safeShipments", totalShipments - criticalAlerts - warningAlerts);
        dashboard.put("productTypeStats", productTypeStats);
        dashboard.put("systemStatus", "OPERATIONAL");
        dashboard.put("lastUpdated", LocalDateTime.now());
        
        return dashboard;
    }

    /**
     * Convert shipment to summary
     */
    private Map<String, Object> shipmentToSummary(Shipment shipment) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("id", shipment.getId());
        summary.put("trackingNumber", shipment.getTrackingNumber());
        summary.put("status", shipment.getStatus());
        summary.put("coldChainStatus", shipment.getColdChainStatus());
        summary.put("productType", shipment.getProductType());
        summary.put("createdAt", shipment.getCreatedAt());
        summary.put("origin", shipment.getOrigin());
        summary.put("destination", shipment.getDestination());
        return summary;
    }

    /**
     * Get visual indicators for shipment
     */
    @GetMapping("/indicators/{shipmentId}")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'SUPERVISOR')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<Map<String, Object>>>> getVisualIndicators(
            @PathVariable Long shipmentId) {
        
        // Validate access
        if (!canViewShipment(shipmentId)) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.status(403).body(
                            UnifiedResponseDTO.<Map<String, Object>>builder()
                                    .success(false)
                                    .error("Access denied for shipment: " + shipmentId)
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    )
            );
        }
        
        return coldChainService.getCurrentTemperature(shipmentId)
                .thenApply(reading -> {
                    Map<String, Object> indicators = new HashMap<>();
                    
                    if (reading != null) {
                        // Get temperature threshold
                        String productType = "REFRIGERATED"; // Default, would get from shipment
                        TemperatureThreshold threshold = getTemperatureThreshold(productType);
                        
                        // Determine status
                        ColdChainMonitoringService.TemperatureStatus status = 
                                determineTemperatureStatus(reading.getTemperature(), threshold);
                        
                        // Create visual indicators
                        indicators.put("temperature", reading.getTemperature());
                        indicators.put("status", status.name());
                        indicators.put("statusColor", getStatusColor(status));
                        indicators.put("statusIcon", getStatusIcon(status));
                        indicators.put("statusMessage", getStatusMessage(status, threshold, reading.getTemperature()));
                        indicators.put("timestamp", reading.getTimestamp());
                        indicators.put("batteryLevel", reading.getBatteryLevel());
                        indicators.put("signalStrength", reading.getSignalStrength());
                        
                        // Threshold indicators
                        indicators.put("minTemperature", threshold.getMinTemperature());
                        indicators.put("maxTemperature", threshold.getMaxTemperature());
                        indicators.put("criticalMinTemperature", threshold.getCriticalMin());
                        indicators.put("criticalMaxTemperature", threshold.getCriticalMax());
                        indicators.put("unit", threshold.getUnit());
                        
                    } else {
                        indicators.put("status", "NO_DATA");
                        indicators.put("statusColor", "#9CA3AF"); // Gray
                        indicators.put("statusIcon", "thermometer-off");
                        indicators.put("statusMessage", "No temperature data available");
                    }
                    
                    return ResponseEntity.ok(
                            UnifiedResponseDTO.<Map<String, Object>>builder()
                                    .success(true)
                                    .data(indicators)
                                    .message("Visual indicators retrieved successfully")
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                })
                .exceptionally(throwable -> {
                    log.error("Error getting visual indicators for shipment {}: {}", shipmentId, throwable.getMessage(), throwable);
                    return ResponseEntity.internalServerError().body(
                            UnifiedResponseDTO.<Map<String, Object>>builder()
                                    .success(false)
                                    .error("Failed to get visual indicators: " + throwable.getMessage())
                                    .timestamp(LocalDateTime.now())
                                    .build()
                    );
                });
    }

    // Helper methods
    private Long getCurrentUserId() {
        // Implementation to get current user ID from security context
        return 1L; // Placeholder
    }

    private UserRole getCurrentUserRole() {
        // Implementation to get current user role from security context
        return UserRole.CLIENT; // Placeholder
    }

    private boolean canAccessShipment(Long shipmentId) {
        try {
            Long currentUserId = getCurrentUserId();
            UserRole userRole = getCurrentUserRole();
            
            if (userRole == UserRole.ADMIN || userRole == UserRole.SUPERVISOR) {
                return true;
            }
            
            Optional<Shipment> shipmentOpt = shipmentRepository.findById(shipmentId);
            if (shipmentOpt.isEmpty()) {
                return false;
            }
            
            Shipment shipment = shipmentOpt.get();
            return currentUserId.equals(shipment.getCustomerId()) || 
                   currentUserId.equals(shipment.getDriverId());
            
        } catch (Exception e) {
            log.error("Error checking shipment access: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean canViewShipment(Long shipmentId) {
        try {
            Long currentUserId = getCurrentUserId();
            UserRole userRole = getCurrentUserRole();
            
            if (userRole == UserRole.ADMIN || userRole == UserRole.SUPERVISOR) {
                return true;
            }
            
            Optional<Shipment> shipmentOpt = shipmentRepository.findById(shipmentId);
            if (shipmentOpt.isEmpty()) {
                return false;
            }
            
            Shipment shipment = shipmentOpt.get();
            return currentUserId.equals(shipment.getCustomerId()) || 
                   currentUserId.equals(shipment.getDriverId());
            
        } catch (Exception e) {
            log.error("Error checking shipment view access: {}", e.getMessage(), e);
            return false;
        }
    }

    private TemperatureThreshold getTemperatureThreshold(String productType) {
        // Implementation would get threshold from service
        return TemperatureThreshold.builder()
                .productType(productType)
                .minTemperature(2.0)
                .maxTemperature(8.0)
                .criticalMin(0.0)
                .criticalMax(10.0)
                .unit("°C")
                .build();
    }

    private ColdChainMonitoringService.TemperatureStatus determineTemperatureStatus(double temperature, TemperatureThreshold threshold) {
        if (temperature < threshold.getCriticalMin() || temperature > threshold.getCriticalMax()) {
            return ColdChainMonitoringService.TemperatureStatus.CRITICAL;
        } else if (temperature < threshold.getMinTemperature() || temperature > threshold.getMaxTemperature()) {
            return ColdChainMonitoringService.TemperatureStatus.WARNING;
        } else {
            return ColdChainMonitoringService.TemperatureStatus.SAFE;
        }
    }

    private String getStatusColor(ColdChainMonitoringService.TemperatureStatus status) {
        switch (status) {
            case SAFE: return "#10B981"; // Green
            case WARNING: return "#F59E0B"; // Yellow
            case CRITICAL: return "#EF4444"; // Red
            default: return "#9CA3AF"; // Gray
        }
    }

    private String getStatusIcon(ColdChainMonitoringService.TemperatureStatus status) {
        switch (status) {
            case SAFE: return "thermometer-safe";
            case WARNING: return "thermometer-warning";
            case CRITICAL: return "thermometer-critical";
            default: return "thermometer-off";
        }
    }

    private String getStatusMessage(ColdChainMonitoringService.TemperatureStatus status, TemperatureThreshold threshold, double temperature) {
        switch (status) {
            case SAFE:
                return String.format("Temperature is safe: %.1f°C", temperature);
            case WARNING:
                return String.format("Temperature warning: %.1f°C (Safe range: %.1f°C to %.1f°C)", 
                        temperature, threshold.getMinTemperature(), threshold.getMaxTemperature());
            case CRITICAL:
                return String.format("Critical temperature: %.1f°C (Critical range: < %.1f°C or > %.1f°C)", 
                        temperature, threshold.getCriticalMin(), threshold.getCriticalMax());
            default:
                return "Temperature status unknown";
        }
    }
}
