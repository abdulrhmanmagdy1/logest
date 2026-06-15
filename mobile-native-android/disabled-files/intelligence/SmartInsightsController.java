// // // // // // // package com.edham.logistics.intelligence;

import com.edham.logistics.dto.UnifiedResponseDTO;
import com.edham.logistics.model.Shipment;
import com.edham.logistics.intelligence.SmartInsightsService.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Smart Insights Controller - Business Intelligence API
 * Provides predictions, recommendations, and system insights
 */
@RestController
@RequestMapping("/api/v1/intelligence")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class SmartInsightsController {

    private final SmartInsightsService smartInsightsService;

    @Autowired
    public SmartInsightsController(SmartInsightsService smartInsightsService) {
        this.smartInsightsService = smartInsightsService;
    }

    /**
     * Predict delay for a specific shipment
     */
    @PostMapping("/predict-delay/{shipmentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<UnifiedResponseDTO<DelayPrediction>> predictShipmentDelay(
            @PathVariable Long shipmentId) {
        
        try {
            log.debug("Predicting delay for shipment: {}", shipmentId);
            
            // In a real implementation, you would fetch the shipment from repository
            Shipment shipment = new Shipment(); // Placeholder
            shipment.setId(shipmentId);
            shipment.setTrackingNumber("TRK" + shipmentId);
            
            DelayPrediction prediction = smartInsightsService.predictDelayedShipment(shipment);
            
            log.info("Delay prediction generated for shipment {}: {}% probability", 
                    shipmentId, prediction.getDelayProbability() * 100);
            
            return ResponseEntity.ok(
                    UnifiedResponseDTO.<DelayPrediction>builder()
                            .success(true)
                            .data(prediction)
                            .message("Delay prediction generated successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
            
        } catch (Exception e) {
            log.error("Error predicting delay for shipment: {}", shipmentId, e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<DelayPrediction>builder()
                            .success(false)
                            .error("Failed to predict delay: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Suggest driver allocation for a shipment
     */
    @PostMapping("/suggest-driver/{shipmentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<UnifiedResponseDTO<DriverAllocationSuggestion>> suggestDriverAllocation(
            @PathVariable Long shipmentId) {
        
        try {
            log.debug("Generating driver allocation suggestion for shipment: {}", shipmentId);
            
            // In a real implementation, you would fetch the shipment from repository
            Shipment shipment = new Shipment(); // Placeholder
            shipment.setId(shipmentId);
            shipment.setTrackingNumber("TRK" + shipmentId);
            shipment.setOrigin("Riyadh");
            shipment.setDestination("Jeddah");
            
            DriverAllocationSuggestion suggestion = smartInsightsService.suggestDriverAllocation(shipment);
            
            log.info("Driver allocation suggestion generated for shipment: {} candidates", 
                    shipmentId, suggestion.getCandidates().size());
            
            return ResponseEntity.ok(
                    UnifiedResponseDTO.<DriverAllocationSuggestion>builder()
                            .success(true)
                            .data(suggestion)
                            .message("Driver allocation suggestion generated successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
            
        } catch (Exception e) {
            log.error("Error generating driver allocation suggestion for shipment: {}", shipmentId, e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<DriverAllocationSuggestion>builder()
                            .success(false)
                            .error("Failed to generate driver allocation suggestion: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Detect inefficient routes
     */
    @GetMapping("/inefficient-routes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<UnifiedResponseDTO<List<InefficientRoute>>> detectInefficientRoutes() {
        
        try {
            log.debug("Detecting inefficient routes");
            
            List<InefficientRoute> inefficientRoutes = smartInsightsService.detectInefficientRoutes();
            
            log.info("Detected {} inefficient routes", inefficientRoutes.size());
            
            return ResponseEntity.ok(
                    UnifiedResponseDTO.<List<InefficientRoute>>builder()
                            .success(true)
                            .data(inefficientRoutes)
                            .message("Inefficient routes detected successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
            
        } catch (Exception e) {
            log.error("Error detecting inefficient routes", e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<List<InefficientRoute>>builder()
                            .success(false)
                            .error("Failed to detect inefficient routes: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Detect system bottlenecks
     */
    @GetMapping("/system-bottlenecks")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<SystemBottlenecks>> detectSystemBottlenecks() {
        
        try {
            log.debug("Detecting system bottlenecks");
            
            SystemBottlenecks bottlenecks = smartInsightsService.detectSystemBottlenecks();
            
            log.info("Detected {} system bottlenecks, health score: {}", 
                    bottlenecks.getBottlenecks().size(), bottlenecks.getSystemHealthScore());
            
            return ResponseEntity.ok(
                    UnifiedResponseDTO.<SystemBottlenecks>builder()
                            .success(true)
                            .data(bottlenecks)
                            .message("System bottlenecks detected successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
            
        } catch (Exception e) {
            log.error("Error detecting system bottlenecks", e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<SystemBottlenecks>builder()
                            .success(false)
                            .error("Failed to detect system bottlenecks: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Get comprehensive insights dashboard
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<Map<String, Object>>> getInsightsDashboard() {
        
        try {
            log.debug("Generating insights dashboard");
            
            // Get all insights
            List<InefficientRoute> inefficientRoutes = smartInsightsService.detectInefficientRoutes();
            SystemBottlenecks systemBottlenecks = smartInsightsService.detectSystemBottlenecks();
            
            // Create dashboard data
            Map<String, Object> dashboard = Map.of(
                    "inefficientRoutes", Map.of(
                            "count", inefficientRoutes.size(),
                            "data", inefficientRoutes,
                            "worstRoute", inefficientRoutes.isEmpty() ? null : inefficientRoutes.get(0)
                    ),
                    "systemBottlenecks", Map.of(
                            "count", systemBottlenecks.getBottlenecks().size(),
                            "data", systemBottlenecks.getBottlenecks(),
                            "healthScore", systemBottlenecks.getSystemHealthScore(),
                            "criticalBottlenecks", systemBottlenecks.getBottlenecks().stream()
                                    .filter(b -> b.getSeverity() > 0.7)
                                    .count()
                    ),
                    "summary", Map.of(
                            "totalIssues", inefficientRoutes.size() + systemBottlenecks.getBottlenecks().size(),
                            "systemStatus", getSystemStatus(systemBottlenecks.getSystemHealthScore()),
                            "lastAnalyzed", systemBottlenecks.getAnalyzedAt(),
                            "recommendations", generateDashboardRecommendations(inefficientRoutes, systemBottlenecks)
                    )
            );
            
            log.info("Insights dashboard generated with {} routes and {} bottlenecks", 
                    inefficientRoutes.size(), systemBottlenecks.getBottlenecks().size());
            
            return ResponseEntity.ok(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(true)
                            .data(dashboard)
                            .message("Insights dashboard generated successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
            
        } catch (Exception e) {
            log.error("Error generating insights dashboard", e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(false)
                            .error("Failed to generate insights dashboard: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Get prediction for multiple shipments
     */
    @PostMapping("/predict-delays/batch")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<UnifiedResponseDTO<Map<String, DelayPrediction>>> predictMultipleDelays(
            @RequestBody List<Long> shipmentIds) {
        
        try {
            log.debug("Predicting delays for {} shipments", shipmentIds.size());
            
            Map<String, DelayPrediction> predictions = new java.util.HashMap<>();
            
            for (Long shipmentId : shipmentIds) {
                Shipment shipment = new Shipment(); // Placeholder
                shipment.setId(shipmentId);
                shipment.setTrackingNumber("TRK" + shipmentId);
                
                DelayPrediction prediction = smartInsightsService.predictDelayedShipment(shipment);
                predictions.put(shipment.getTrackingNumber(), prediction);
            }
            
            log.info("Generated delay predictions for {} shipments", predictions.size());
            
            return ResponseEntity.ok(
                    UnifiedResponseDTO.<Map<String, DelayPrediction>>builder()
                            .success(true)
                            .data(predictions)
                            .message("Batch delay predictions generated successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
            
        } catch (Exception e) {
            log.error("Error predicting delays for multiple shipments", e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<Map<String, DelayPrediction>>builder()
                            .success(false)
                            .error("Failed to predict delays: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Get route optimization suggestions
     */
    @GetMapping("/route-optimization")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
    public ResponseEntity<UnifiedResponseDTO<Map<String, Object>>> getRouteOptimizationSuggestions() {
        
        try {
            log.debug("Generating route optimization suggestions");
            
            List<InefficientRoute> inefficientRoutes = smartInsightsService.detectInefficientRoutes();
            
            // Group issues by type
            Map<String, List<InefficientRoute>> issuesByType = new java.util.HashMap<>();
            for (InefficientRoute route : inefficientRoutes) {
                for (String issue : route.getIssues()) {
                    issuesByType.computeIfAbsent(issue, k -> new ArrayList<>()).add(route);
                }
            }
            
            // Generate optimization suggestions
            List<Map<String, Object>> suggestions = new ArrayList<>();
            for (Map.Entry<String, List<InefficientRoute>> entry : issuesByType.entrySet()) {
                String issueType = entry.getKey();
                List<InefficientRoute> routes = entry.getValue();
                
                Map<String, Object> suggestion = Map.of(
                        "issueType", issueType,
                        "affectedRoutes", routes.size(),
                        "routes", routes.stream().map(InefficientRoute::getRoute).collect(java.util.stream.Collectors.toList()),
                        "recommendation", generateOptimizationRecommendation(issueType, routes),
                        "priority", getOptimizationPriority(issueType, routes.size())
                );
                suggestions.add(suggestion);
            }
            
            Map<String, Object> optimizationData = Map.of(
                    "totalInefficientRoutes", inefficientRoutes.size(),
                    "issueTypes", issuesByType.size(),
                    "suggestions", suggestions,
                    "estimatedImprovement", calculateEstimatedImprovement(inefficientRoutes)
            );
            
            log.info("Generated {} route optimization suggestions", suggestions.size());
            
            return ResponseEntity.ok(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(true)
                            .data(optimizationData)
                            .message("Route optimization suggestions generated successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
            
        } catch (Exception e) {
            log.error("Error generating route optimization suggestions", e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(false)
                            .error("Failed to generate route optimization suggestions: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Get performance insights
     */
    @GetMapping("/performance-insights")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<Map<String, Object>>> getPerformanceInsights() {
        
        try {
            log.debug("Generating performance insights");
            
            SystemBottlenecks bottlenecks = smartInsightsService.detectSystemBottlenecks();
            
            // Analyze performance trends
            Map<String, Object> performanceInsights = Map.of(
                    "systemHealth", Map.of(
                            "score", bottlenecks.getSystemHealthScore(),
                            "status", getSystemStatus(bottlenecks.getSystemHealthScore()),
                            "trend", "STABLE" // Would calculate from historical data
                    ),
                    "bottleneckAnalysis", Map.of(
                            "totalBottlenecks", bottlenecks.getBottlenecks().size(),
                            "criticalBottlenecks", bottlenecks.getBottlenecks().stream()
                                    .filter(b -> b.getSeverity() > 0.7)
                                    .count(),
                            "mostCommonType", getMostCommonBottleneckType(bottlenecks.getBottlenecks()),
                            "averageSeverity", bottlenecks.getBottlenecks().stream()
                                    .mapToDouble(Bottleneck::getSeverity)
                                    .average()
                                    .orElse(0.0)
                    ),
                    "recommendations", generatePerformanceRecommendations(bottlenecks),
                    "alerts", generatePerformanceAlerts(bottlenecks)
            );
            
            log.info("Performance insights generated with health score: {}", 
                    bottlenecks.getSystemHealthScore());
            
            return ResponseEntity.ok(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(true)
                            .data(performanceInsights)
                            .message("Performance insights generated successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
            
        } catch (Exception e) {
            log.error("Error generating performance insights", e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(false)
                            .error("Failed to generate performance insights: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Test smart insights functionality
     */
    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<Map<String, Object>>> testSmartInsights() {
        
        try {
            log.debug("Testing smart insights functionality");
            
            // Test delay prediction
            Shipment testShipment = new Shipment();
            testShipment.setId(1L);
            testShipment.setTrackingNumber("TEST001");
            testShipment.setOrigin("Riyadh");
            testShipment.setDestination("Jeddah");
            
            DelayPrediction delayPrediction = smartInsightsService.predictDelayedShipment(testShipment);
            
            // Test driver allocation
            DriverAllocationSuggestion driverSuggestion = smartInsightsService.suggestDriverAllocation(testShipment);
            
            // Test route efficiency
            List<InefficientRoute> inefficientRoutes = smartInsightsService.detectInefficientRoutes();
            
            // Test bottleneck detection
            SystemBottlenecks bottlenecks = smartInsightsService.detectSystemBottlenecks();
            
            Map<String, Object> testResults = Map.of(
                    "delayPrediction", Map.of(
                            "success", delayPrediction != null,
                            "probability", delayPrediction.getDelayProbability(),
                            "factors", delayPrediction.getFactors().size()
                    ),
                    "driverAllocation", Map.of(
                            "success", driverSuggestion != null,
                            "candidates", driverSuggestion.getCandidates().size(),
                            "topScore", driverSuggestion.getCandidates().isEmpty() ? 0.0 : 
                                       driverSuggestion.getCandidates().get(0).getScore()
                    ),
                    "routeAnalysis", Map.of(
                            "success", inefficientRoutes != null,
                            "inefficientRoutes", inefficientRoutes.size()
                    ),
                    "bottleneckDetection", Map.of(
                            "success", bottlenecks != null,
                            "bottlenecks", bottlenecks.getBottlenecks().size(),
                            "healthScore", bottlenecks.getSystemHealthScore()
                    ),
                    "overallStatus", "TESTS_PASSED"
            );
            
            log.info("Smart insights test completed successfully");
            
            return ResponseEntity.ok(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(true)
                            .data(testResults)
                            .message("Smart insights test completed successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
            
        } catch (Exception e) {
            log.error("Error testing smart insights", e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(false)
                            .error("Smart insights test failed: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    // Helper methods
    private String getSystemStatus(double healthScore) {
        if (healthScore >= 0.9) return "EXCELLENT";
        if (healthScore >= 0.7) return "GOOD";
        if (healthScore >= 0.5) return "FAIR";
        if (healthScore >= 0.3) return "POOR";
        return "CRITICAL";
    }

    private List<String> generateDashboardRecommendations(List<InefficientRoute> routes, SystemBottlenecks bottlenecks) {
        List<String> recommendations = new ArrayList<>();
        
        if (routes.size() > 5) {
            recommendations.add("Consider route optimization for multiple inefficient routes");
        }
        
        if (bottlenecks.getSystemHealthScore() < 0.7) {
            recommendations.add("System health is below optimal - address critical bottlenecks");
        }
        
        long criticalBottlenecks = bottlenecks.getBottlenecks().stream()
                .filter(b -> b.getSeverity() > 0.7)
                .count();
        
        if (criticalBottlenecks > 2) {
            recommendations.add("Multiple critical bottlenecks detected - prioritize immediate action");
        }
        
        return recommendations;
    }

    private String generateOptimizationRecommendation(String issueType, List<InefficientRoute> routes) {
        switch (issueType) {
            case "Low average speed":
                return "Review traffic patterns and consider alternative routes";
            case "Low on-time delivery rate":
                return "Adjust delivery time estimates and allocate more resources";
            case "Long average delivery time":
                return "Optimize routing and consider driver training";
            default:
                return "Analyze route-specific factors and implement targeted improvements";
        }
    }

    private String getOptimizationPriority(String issueType, int affectedRoutes) {
        if (affectedRoutes > 10) return "HIGH";
        if (affectedRoutes > 5) return "MEDIUM";
        return "LOW";
    }

    private Map<String, Object> calculateEstimatedImprovement(List<InefficientRoute> routes) {
        double currentEfficiency = routes.stream()
                .mapToDouble(InefficientRoute::getEfficiencyScore)
                .average()
                .orElse(0.0);
        
        double targetEfficiency = 0.85; // Target efficiency
        double improvement = (targetEfficiency - currentEfficiency) / currentEfficiency * 100;
        
        return Map.of(
                "currentEfficiency", currentEfficiency,
                "targetEfficiency", targetEfficiency,
                "estimatedImprovement", Math.max(0, improvement),
                "timeToImplement", "2-4 weeks"
        );
    }

    private String getMostCommonBottleneckType(List<Bottleneck> bottlenecks) {
        if (bottlenecks.isEmpty()) return "NONE";
        
        Map<String, Long> typeCounts = bottlenecks.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        Bottleneck::getType, java.util.stream.Collectors.counting()));
        
        return typeCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("NONE");
    }

    private List<String> generatePerformanceRecommendations(SystemBottlenecks bottlenecks) {
        List<String> recommendations = new ArrayList<>();
        
        for (Bottleneck bottleneck : bottlenecks.getBottlenecks()) {
            if (bottleneck.getSeverity() > 0.7) {
                recommendations.add(bottleneck.getRecommendation());
            }
        }
        
        if (recommendations.isEmpty()) {
            recommendations.add("System is performing well - continue monitoring");
        }
        
        return recommendations;
    }

    private List<Map<String, Object>> generatePerformanceAlerts(SystemBottlenecks bottlenecks) {
        List<Map<String, Object>> alerts = new ArrayList<>();
        
        for (Bottleneck bottleneck : bottlenecks.getBottlenecks()) {
            if (bottleneck.getSeverity() > 0.8) {
                Map<String, Object> alert = Map.of(
                        "type", "CRITICAL",
                        "message", bottleneck.getDescription(),
                        "severity", bottleneck.getSeverity(),
                        "recommendation", bottleneck.getRecommendation()
                );
                alerts.add(alert);
            }
        }
        
        return alerts;
    }
}
