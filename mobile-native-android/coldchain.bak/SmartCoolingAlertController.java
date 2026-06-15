// // package com.edham.logistics.coldchain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST API Controller for Smart Cooling Alert System
 */
@RestController
@RequestMapping("/api/v1/coldchain/smart-alerts")
@CrossOrigin(origins = "*")
public class SmartCoolingAlertController {

    @Autowired
    private SmartCoolingAlertService smartCoolingAlertService;

    /**
     * Get all active cooling alerts
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'DRIVER')")
    public ResponseEntity<List<SmartCoolingAlertService.SmartCoolingAlert>> getActiveAlerts() {
        List<SmartCoolingAlertService.SmartCoolingAlert> alerts = smartCoolingAlertService.getActiveAlerts();
        return ResponseEntity.ok(alerts);
    }

    /**
     * Get specific alert by ID
     */
    @GetMapping("/{alertId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'DRIVER')")
    public ResponseEntity<SmartCoolingAlertService.SmartCoolingAlert> getAlert(@PathVariable String alertId) {
        SmartCoolingAlertService.SmartCoolingAlert alert = smartCoolingAlertService.getAlert(alertId);
        if (alert != null) {
            return ResponseEntity.ok(alert);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Acknowledge an alert
     */
    @PostMapping("/{alertId}/acknowledge")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'DRIVER')")
    public ResponseEntity<Map<String, Object>> acknowledgeAlert(
            @PathVariable String alertId,
            @RequestBody Map<String, String> request) {
        
        String userId = request.get("userId");
        if (userId == null || userId.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "User ID is required"
            ));
        }

        smartCoolingAlertService.acknowledgeAlert(alertId, userId);
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Alert acknowledged successfully",
            "acknowledgedAt", LocalDateTime.now().toString()
        ));
    }

    /**
     * Get temperature history for a shipment
     */
    @GetMapping("/temperature-history/{shipmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'DRIVER', 'CUSTOMER')")
    public ResponseEntity<List<SmartCoolingAlertService.TemperatureReading>> getTemperatureHistory(
            @PathVariable String shipmentId,
            @RequestParam(defaultValue = "24") int hours) {
        
        List<SmartCoolingAlertService.TemperatureReading> history = 
            smartCoolingAlertService.getTemperatureHistory(shipmentId, hours);
        
        return ResponseEntity.ok(history);
    }

    /**
     * Get cooling failure prediction for a shipment
     */
    @GetMapping("/failure-prediction/{shipmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<SmartCoolingAlertService.CoolingFailurePrediction> getFailurePrediction(
            @PathVariable String shipmentId) {
        
        SmartCoolingAlertService.CoolingFailurePrediction prediction = 
            smartCoolingAlertService.getFailurePrediction(shipmentId);
        
        if (prediction != null) {
            return ResponseEntity.ok(prediction);
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * Get alert statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> getAlertStatistics() {
        Map<String, Object> statistics = smartCoolingAlertService.getAlertStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Process new temperature reading (for testing/manual input)
     */
    @PostMapping("/process-reading")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'DRIVER')")
    public ResponseEntity<Map<String, Object>> processTemperatureReading(
            @RequestBody Map<String, Object> request) {
        
        try {
            String shipmentId = (String) request.get("shipmentId");
            Double temperature = (Double) request.get("temperature");
            String productType = (String) request.get("productType");
            String sensorId = (String) request.get("sensorId");
            Double batteryLevel = request.get("batteryLevel") != null ? 
                (Double) request.get("batteryLevel") : 100.0;
            String location = (String) request.get("location");

            if (shipmentId == null || temperature == null || productType == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "shipmentId, temperature, and productType are required"
                ));
            }

            smartCoolingAlertService.processTemperatureReading(
                shipmentId, temperature, productType, sensorId, batteryLevel, location);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Temperature reading processed successfully",
                "processedAt", LocalDateTime.now().toString()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error processing temperature reading: " + e.getMessage()
            ));
        }
    }

    /**
     * Get alert levels information
     */
    @GetMapping("/alert-levels")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'DRIVER', 'CUSTOMER')")
    public ResponseEntity<Map<String, Object>> getAlertLevels() {
        Map<String, Object> levels = Map.of(
            "WARNING", Map.of(
                "name", "تحذير",
                "severity", 1,
                "color", "#F59E0B",
                "escalationMinutes", 30,
                "description", "درجة الحرارة أعلى من المعتاد ولكن لا تزال في نطاق آمن نسبياً"
            ),
            "CRITICAL", Map.of(
                "name", "حرج",
                "severity", 2,
                "color", "#EF4444",
                "escalationMinutes", 15,
                "description", "درجة الحرارة خطرة وتتطلب اتخاذ إجراء فوري"
            ),
            "EMERGENCY", Map.of(
                "name", "طوارئ",
                "severity", 3,
                "color", "#DC2626",
                "escalationMinutes", 5,
                "description", "درجة الحرارة في مستوى الطوارئ وتتطلب استجابة فورية"
            )
        );

        return ResponseEntity.ok(levels);
    }

    /**
     * Get risk levels information
     */
    @GetMapping("/risk-levels")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> getRiskLevels() {
        Map<String, Object> riskLevels = Map.of(
            "LOW", Map.of(
                "name", "منخفض",
                "scoreRange", "0.0 - 0.29",
                "color", "#10B981",
                "description", "مخاطر منخفضة، الوضع طبيعي"
            ),
            "MEDIUM", Map.of(
                "name", "متوسط",
                "scoreRange", "0.30 - 0.59",
                "color", "#F59E0B",
                "description", "مخاطر متوسطة، يجب المراقبة"
            ),
            "HIGH", Map.of(
                "name", "مرتفع",
                "scoreRange", "0.60 - 0.79",
                "color", "#EF4444",
                "description", "مخاطر مرتفعة، يجب اتخاذ إجراءات وقائية"
            ),
            "CRITICAL", Map.of(
                "name", "حرج",
                "scoreRange", "0.80 - 1.00",
                "color", "#DC2626",
                "description", "مخاطر حرجة، يتطلب استجابة فورية"
            )
        );

        return ResponseEntity.ok(riskLevels);
    }

    /**
     * Get system health status
     */
    @GetMapping("/health")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        Map<String, Object> statistics = smartCoolingAlertService.getAlertStatistics();
        
        // Calculate health metrics
        long totalAlerts = (Long) statistics.get("totalActiveAlerts");
        long emergencyAlerts = (Long) statistics.get("emergencyAlerts");
        long criticalAlerts = (Long) statistics.get("criticalAlerts");
        
        String healthStatus;
        if (emergencyAlerts > 0) {
            healthStatus = "CRITICAL";
        } else if (criticalAlerts > 0) {
            healthStatus = "WARNING";
        } else if (totalAlerts > 0) {
            healthStatus = "NORMAL";
        } else {
            healthStatus = "HEALTHY";
        }

        Map<String, Object> health = Map.of(
            "status", healthStatus,
            "totalActiveAlerts", totalAlerts,
            "highPriorityAlerts", emergencyAlerts + criticalAlerts,
            "acknowledgmentRate", statistics.containsKey("acknowledgedAlerts") ? 
                (double) statistics.get("acknowledgedAlerts") / Math.max(totalAlerts, 1) : 0.0,
            "lastUpdated", LocalDateTime.now().toString()
        );

        return ResponseEntity.ok(health);
    }

    /**
     * Get dashboard summary
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        Map<String, Object> statistics = smartCoolingAlertService.getAlertStatistics();
        List<SmartCoolingAlertService.SmartCoolingAlert> activeAlerts = 
            smartCoolingAlertService.getActiveAlerts();

        // Recent alerts (last 10)
        List<SmartCoolingAlertService.SmartCoolingAlert> recentAlerts = activeAlerts.stream()
            .sorted((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()))
            .limit(10)
            .toList();

        // Critical alerts requiring immediate attention
        List<SmartCoolingAlertService.SmartCoolingAlert> criticalAlerts = activeAlerts.stream()
            .filter(alert -> !alert.isAcknowledged() && 
                (alert.getLevel() == SmartCoolingAlertService.AlertLevel.CRITICAL || 
                 alert.getLevel() == SmartCoolingAlertService.AlertLevel.EMERGENCY))
            .toList();

        Map<String, Object> dashboard = Map.of(
            "statistics", statistics,
            "recentAlerts", recentAlerts,
            "criticalAlerts", criticalAlerts,
            "totalActiveAlerts", activeAlerts.size(),
            "unacknowledgedAlerts", activeAlerts.stream()
                .filter(alert -> !alert.isAcknowledged() && !alert.isAutoResolved())
                .count(),
            "lastUpdated", LocalDateTime.now().toString()
        );

        return ResponseEntity.ok(dashboard);
    }
}
