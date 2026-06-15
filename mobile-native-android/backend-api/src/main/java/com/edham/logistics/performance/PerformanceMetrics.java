package com.edham.logistics.performance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Performance metrics data structure
 * Tracks system performance indicators
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceMetrics {
    private Long heapUsed;
    private Long heapMax;
    private Long nonHeapUsed;
    private Long nonHeapMax;
    private Long gcTime;
    private Long gcCollections;
    private Double averageResponseTime;
    private Integer requestCount;
    private Integer successCount;
    private Integer failureCount;
    private LocalDateTime timestamp;
    private Map<String, Object> additionalMetrics;
    private List<String> performanceWarnings;
    private Double cpuUsage;
    private Double memoryUsagePercent;
    private Long totalMemory;
    private Long freeMemory;
    private Integer activeThreads;
    private Integer peakThreads;
    private Long uptime;
    private Double systemLoad;
    
    // Performance calculation methods
    public Double getSuccessRate() {
        return requestCount > 0 ? (double) successCount / requestCount : 0.0;
    }
    
    public Double getFailureRate() {
        return requestCount > 0 ? (double) failureCount / requestCount : 0.0;
    }
    
    public Double getGcEfficiency() {
        return gcCollections > 0 ? (double) gcTime / gcCollections : 0.0;
    }
    
    public Double getMemoryEfficiency() {
        return heapMax > 0 ? (double) heapUsed / heapMax : 0.0;
    }
    
    public boolean isHealthy() {
        return memoryUsagePercent < 0.8 && 
               cpuUsage < 0.8 && 
               getSuccessRate() > 0.95 &&
               getGcEfficiency() < 100.0;
    }
    
    public boolean needsAttention() {
        return memoryUsagePercent > 0.9 || 
               cpuUsage > 0.9 || 
               getSuccessRate() < 0.9 ||
               getGcEfficiency() > 200.0;
    }
    
    public List<String> getPerformanceIssues() {
        List<String> issues = new java.util.ArrayList<>();
        
        if (memoryUsagePercent > 0.8) {
            issues.add("High memory usage: " + String.format("%.1f%%", memoryUsagePercent * 100));
        }
        
        if (cpuUsage > 0.8) {
            issues.add("High CPU usage: " + String.format("%.1f%%", cpuUsage * 100));
        }
        
        if (getSuccessRate() < 0.95) {
            issues.add("Low success rate: " + String.format("%.1f%%", getSuccessRate() * 100));
        }
        
        if (getGcEfficiency() > 100.0) {
            issues.add("High GC time: " + String.format("%.2fms", getGcEfficiency()));
        }
        
        return issues;
    }
}
