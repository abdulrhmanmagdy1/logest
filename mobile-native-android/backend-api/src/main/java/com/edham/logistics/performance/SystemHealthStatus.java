package com.edham.logistics.performance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * System health status data structure
 * Comprehensive health monitoring for the logistics system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemHealthStatus {
    private Double healthScore;
    private String status; // EXCELLENT, GOOD, FAIR, POOR, CRITICAL
    private Double successRate;
    private Double averageResponseTime;
    private Double memoryUsagePercent;
    private Double cpuUsagePercent;
    private Long totalRequests;
    private Long failedRequests;
    private LocalDateTime lastUpdated;
    private List<String> recommendatons;
    private List<SystemComponent> components;
    private List<PerformanceAlert> alerts;
    private Map<String, Object> detailedMetrics;
    private Boolean isHealthy;
    private String uptime;
    private Long activeConnections;
    private Integer activeThreads;
    private Long uptimeMillis;
    private Double diskUsagePercent;
    private Integer databaseConnections;
    private Integer cacheHitRate;
    private Long gcCount;
    private Double gcTime;
    
    public boolean isHealthy() {
        return healthScore != null && healthScore >= 0.8;
    }
    
    public boolean needsAttention() {
        return healthScore != null && healthScore < 0.6;
    }
    
    public boolean isCritical() {
        return healthScore != null && healthScore < 0.4;
    }
    
    /**
     * System component health
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemComponent {
        private String name;
        private String status;
        private Double healthScore;
        private String lastCheck;
        private List<String> issues;
        private Map<String, Object> metrics;
        private Boolean isHealthy;
        private Long responseTime;
        private String version;
        private String uptime;
    }
    
    /**
     * Performance alert
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PerformanceAlert {
        private String alertId;
        private String type; // MEMORY, CPU, RESPONSE_TIME, ERROR_RATE, CONNECTION
        private String severity; // LOW, MEDIUM, HIGH, CRITICAL
        private String title;
        private String description;
        private LocalDateTime timestamp;
        private String component;
        private Double threshold;
        private Double actualValue;
        private Boolean acknowledged;
        private String acknowledgedBy;
        private LocalDateTime acknowledgedAt;
        private String recommendedAction;
        private Boolean resolved;
        private LocalDateTime resolvedAt;
        private String resolutionNotes;
    }
}
