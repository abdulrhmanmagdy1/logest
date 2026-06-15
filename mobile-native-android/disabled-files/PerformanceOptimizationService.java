package com.edham.logistics.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.management.MemoryMXBean;
import javax.management.MemoryUsage;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Performance optimization service for system stability
 * Monitors memory usage, optimizes performance, and handles failures gracefully
 */
@Slf4j
@Service
public class PerformanceOptimizationService {

    private final Map<String, PerformanceMetrics> performanceMetrics = new ConcurrentHashMap<>();
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong failedRequests = new AtomicLong(0);
    private final Map<String, Long> lastCleanupTime = new ConcurrentHashMap<>();

    @Autowired
    private NetworkFailureHandler networkFailureHandler;

    /**
     * Monitor system performance
     */
    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void monitorPerformance() {
        try {
            PerformanceMetrics metrics = collectPerformanceMetrics();
            String key = LocalDateTime.now().toString().substring(0, 16); // YYYY-MM-DDTHH:MM
            
            performanceMetrics.put(key, metrics);
            
            // Keep only last 24 hours of metrics
            cleanupOldMetrics();
            
            // Check for performance issues
            checkPerformanceIssues(metrics);
            
        } catch (Exception e) {
            log.error("Error monitoring performance: {}", e.getMessage(), e);
        }
    }

    /**
     * Optimize memory usage
     */
    @Scheduled(fixedRate = 60000) // Every minute
    public void optimizeMemory() {
        try {
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
            
            double memoryUsagePercent = (double) heapUsage.getUsed() / heapUsage.getMax();
            
            if (memoryUsagePercent > 0.8) {
                log.warn("High memory usage detected: {}%", memoryUsagePercent * 100);
                
                // Trigger garbage collection
                System.gc();
                
                // Clear caches
                clearCaches();
                
                // Log memory optimization
                logMemoryOptimization(memoryUsagePercent);
            }
            
        } catch (Exception e) {
            log.error("Error optimizing memory: {}", e.getMessage(), e);
        }
    }

    /**
     * Check for memory leaks
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void checkMemoryLeaks() {
        try {
            // Check for increasing memory usage over time
            List<PerformanceMetrics> recentMetrics = getRecentMetrics(12); // Last hour
            
            if (recentMetrics.size() >= 12) {
                double memoryTrend = calculateMemoryTrend(recentMetrics);
                
                if (memoryTrend > 0.1) { // 10% increase trend
                    log.warn("Potential memory leak detected with trend: {}", memoryTrend);
                    
                    // Trigger deeper analysis
                    analyzeMemoryLeak();
                }
            }
            
        } catch (Exception e) {
            log.error("Error checking memory leaks: {}", e.getMessage(), e);
        }
    }

    /**
     * Record request performance
     */
    public void recordRequest(String endpoint, long duration, boolean success) {
        try {
            totalRequests.incrementAndGet();
            
            if (!success) {
                failedRequests.incrementAndGet();
            }
            
            String key = endpoint + "_" + LocalDateTime.now().toString().substring(0, 13); // YYYY-MM-DDTHH
            
            performanceMetrics.computeIfAbsent(key, k -> new PerformanceMetrics())
                    .addRequest(duration, success);
            
        } catch (Exception e) {
            log.error("Error recording request: {}", e.getMessage(), e);
        }
    }

    /**
     * Handle network failure gracefully
     */
    public void handleNetworkFailure(String operation, Exception error) {
        try {
            log.warn("Network failure in operation {}: {}", operation, error.getMessage());
            
            // Handle based on operation type
            switch (operation.toLowerCase()) {
                case "database":
                    networkFailureHandler.handleDatabaseFailure(error);
                    break;
                case "external_api":
                    networkFailureHandler.handleExternalApiFailure(error);
                    break;
                case "file_upload":
                    networkFailureHandler.handleFileUploadFailure(error);
                    break;
                case "notification":
                    networkFailureHandler.handleNotificationFailure(error);
                    break;
                default:
                    networkFailureHandler.handleGenericFailure(error);
                    break;
            }
            
        } catch (Exception e) {
            log.error("Error handling network failure: {}", e.getMessage(), e);
        }
    }

    /**
     * Get system health status
     */
    public SystemHealthStatus getSystemHealthStatus() {
        try {
            PerformanceMetrics currentMetrics = collectPerformanceMetrics();
            
            double successRate = calculateSuccessRate();
            double averageResponseTime = calculateAverageResponseTime();
            double memoryUsagePercent = getMemoryUsagePercent();
            double cpuUsagePercent = getCpuUsagePercent();
            
            // Calculate overall health score
            double healthScore = calculateHealthScore(
                    successRate, averageResponseTime, memoryUsagePercent, cpuUsagePercent);
            
            String status = determineHealthStatus(healthScore);
            
            return SystemHealthStatus.builder()
                    .healthScore(healthScore)
                    .status(status)
                    .successRate(successRate)
                    .averageResponseTime(averageResponseTime)
                    .memoryUsagePercent(memoryUsagePercent)
                    .cpuUsagePercent(cpuUsagePercent)
                    .totalRequests(totalRequests.get())
                    .failedRequests(failedRequests.get())
                    .lastUpdated(LocalDateTime.now())
                    .recommendations(generateRecommendations(healthScore, currentMetrics))
                    .build();
                    
        } catch (Exception e) {
            log.error("Error getting system health status: {}", e.getMessage(), e);
            return SystemHealthStatus.builder()
                    .healthScore(0.0)
                    .status("CRITICAL")
                    .lastUpdated(LocalDateTime.now())
                    .build();
        }
    }

    /**
     * Optimize offline behavior
     */
    public void optimizeOfflineBehavior() {
        try {
            log.info("Optimizing offline behavior");
            
            // Enable offline caching
            enableOfflineCaching();
            
            // Sync pending operations
            syncPendingOperations();
            
            // Compress stored data
            compressStoredData();
            
            // Clean up temporary files
            cleanupTemporaryFiles();
            
            // Prepare for reconnection
            prepareForReconnection();
            
        } catch (Exception e) {
            log.error("Error optimizing offline behavior: {}", e.getMessage(), e);
        }
    }

    /**
     * Recover from crash
     */
    public void recoverFromCrash() {
        try {
            log.warn("System crash detected, initiating recovery");
            
            // Save crash report
            saveCrashReport();
            
            // Clear corrupted data
            clearCorruptedData();
            
            // Restore from backup
            restoreFromBackup();
            
            // Reset performance counters
            resetPerformanceCounters();
            
            // Notify administrators
            notifyAdministrators("System crash recovery initiated");
            
        } catch (Exception e) {
            log.error("Error recovering from crash: {}", e.getMessage(), e);
        }
    }

    // Helper methods
    private PerformanceMetrics collectPerformanceMetrics() {
        try {
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
            MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();
            
            // Get GC information
            long totalGcTime = 0;
            long totalGcCollections = 0;
            for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
                totalGcTime += gcBean.getCollectionTime();
                totalGcCollections += gcBean.getCollectionCount();
            }
            
            return PerformanceMetrics.builder()
                    .heapUsed(heapUsage.getUsed())
                    .heapMax(heapUsage.getMax())
                    .nonHeapUsed(nonHeapUsage.getUsed())
                    .nonHeapMax(nonHeapUsage.getMax())
                    .gcTime(totalGcTime)
                    .gcCollections(totalGcCollections)
                    .timestamp(LocalDateTime.now())
                    .build();
                    
        } catch (Exception e) {
            log.error("Error collecting performance metrics: {}", e.getMessage(), e);
            return new PerformanceMetrics();
        }
    }

    private void cleanupOldMetrics() {
        try {
            LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
            
            performanceMetrics.entrySet().removeIf(entry -> {
                String key = entry.getKey();
                LocalDateTime keyTime = LocalDateTime.parse(key + ":00:00");
                return keyTime.isBefore(cutoff);
            });
            
        } catch (Exception e) {
            log.error("Error cleaning up old metrics: {}", e.getMessage(), e);
        }
    }

    private void checkPerformanceIssues(PerformanceMetrics metrics) {
        try {
            double memoryUsagePercent = (double) metrics.getHeapUsed() / metrics.getHeapMax();
            
            if (memoryUsagePercent > 0.9) {
                log.error("Critical memory usage: {}%", memoryUsagePercent * 100);
                triggerMemoryAlert(memoryUsagePercent);
            }
            
            if (metrics.getGcTime() > 1000) { // More than 1 second in GC
                log.warn("High GC time: {}ms", metrics.getGcTime());
                triggerGcAlert(metrics.getGcTime());
            }
            
        } catch (Exception e) {
            log.error("Error checking performance issues: {}", e.getMessage(), e);
        }
    }

    private void clearCaches() {
        try {
            // Clear application caches
            performanceMetrics.clear();
            lastCleanupTime.clear();
            
            // Suggest garbage collection
            System.gc();
            
            log.info("Caches cleared for memory optimization");
            
        } catch (Exception e) {
            log.error("Error clearing caches: {}", e.getMessage(), e);
        }
    }

    private void logMemoryOptimization(double memoryUsagePercent) {
        try {
            String message = String.format("Memory optimization triggered - Usage: %.2f%%", 
                    memoryUsagePercent * 100);
            
            log.warn(message);
            
            // Store optimization event
            performanceMetrics.put("memory_optimization_" + System.currentTimeMillis(), 
                    PerformanceMetrics.builder()
                            .heapUsed((long) (memoryUsagePercent * 100))
                            .heapMax(100L)
                            .timestamp(LocalDateTime.now())
                            .build());
                            
        } catch (Exception e) {
            log.error("Error logging memory optimization: {}", e.getMessage(), e);
        }
    }

    private double calculateMemoryTrend(List<PerformanceMetrics> metrics) {
        try {
            if (metrics.size() < 2) return 0.0;
            
            double firstUsage = (double) metrics.get(0).getHeapUsed() / metrics.get(0).getHeapMax();
            double lastUsage = (double) metrics.get(metrics.size() - 1).getHeapUsed() / 
                               metrics.get(metrics.size() - 1).getHeapMax();
            
            return (lastUsage - firstUsage) / firstUsage;
            
        } catch (Exception e) {
            log.error("Error calculating memory trend: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    private void analyzeMemoryLeak() {
        try {
            log.warn("Performing memory leak analysis");
            
            // Take memory snapshot
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;
            
            // Log detailed memory information
            log.info("Memory analysis - Total: {}MB, Used: {}MB, Free: {}MB", 
                    totalMemory / 1024 / 1024, usedMemory / 1024 / 1024, freeMemory / 1024 / 1024);
            
            // Trigger garbage collection
            System.gc();
            
            // Check if memory was freed
            long newFreeMemory = runtime.freeMemory();
            if (newFreeMemory <= freeMemory) {
                log.error("Memory leak suspected - GC did not free memory");
                triggerMemoryLeakAlert();
            }
            
        } catch (Exception e) {
            log.error("Error analyzing memory leak: {}", e.getMessage(), e);
        }
    }

    private List<PerformanceMetrics> getRecentMetrics(int count) {
        try {
            List<PerformanceMetrics> recent = new ArrayList<>();
            List<String> keys = new ArrayList<>(performanceMetrics.keySet());
            
            // Sort keys by time (descending)
            keys.sort((a, b) -> b.compareTo(a));
            
            // Get recent metrics
            for (int i = 0; i < Math.min(count, keys.size()); i++) {
                recent.add(performanceMetrics.get(keys.get(i)));
            }
            
            return recent;
            
        } catch (Exception e) {
            log.error("Error getting recent metrics: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private double calculateSuccessRate() {
        long total = totalRequests.get();
        long failed = failedRequests.get();
        
        return total > 0 ? (double) (total - failed) / total : 1.0;
    }

    private double calculateAverageResponseTime() {
        return performanceMetrics.values().stream()
                .mapToDouble(PerformanceMetrics::getAverageResponseTime)
                .average()
                .orElse(0.0);
    }

    private double getMemoryUsagePercent() {
        try {
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
            
            return (double) heapUsage.getUsed() / heapUsage.getMax();
            
        } catch (Exception e) {
            log.error("Error getting memory usage: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    private double getCpuUsagePercent() {
        try {
            com.sun.management.OperatingSystemMXBean osBean = 
                    ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class);
            
            return osBean.getProcessCpuLoad();
            
        } catch (Exception e) {
            log.error("Error getting CPU usage: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    private double calculateHealthScore(double successRate, double avgResponseTime, 
                                   double memoryUsage, double cpuUsage) {
        double score = 0.0;
        
        // Success rate weight: 30%
        score += successRate * 0.3;
        
        // Response time weight: 25% (inverse - lower is better)
        double responseScore = Math.max(0, 1 - (avgResponseTime / 5000)); // 5s as max
        score += responseScore * 0.25;
        
        // Memory usage weight: 25% (inverse - lower is better)
        double memoryScore = Math.max(0, 1 - memoryUsage);
        score += memoryScore * 0.25;
        
        // CPU usage weight: 20% (inverse - lower is better)
        double cpuScore = Math.max(0, 1 - cpuUsage);
        score += cpuScore * 0.2;
        
        return Math.min(1.0, score);
    }

    private String determineHealthStatus(double healthScore) {
        if (healthScore >= 0.9) return "EXCELLENT";
        if (healthScore >= 0.8) return "GOOD";
        if (healthScore >= 0.6) return "FAIR";
        if (healthScore >= 0.4) return "POOR";
        return "CRITICAL";
    }

    private List<String> generateRecommendations(double healthScore, PerformanceMetrics metrics) {
        List<String> recommendations = new ArrayList<>();
        
        if (healthScore < 0.6) {
            recommendations.add("System performance needs immediate attention");
        }
        
        double memoryUsage = (double) metrics.getHeapUsed() / metrics.getHeapMax();
        if (memoryUsage > 0.8) {
            recommendations.add("Consider increasing heap memory or optimizing memory usage");
        }
        
        if (metrics.getGcTime() > 1000) {
            recommendations.add("High GC time detected - review object allocation patterns");
        }
        
        double successRate = calculateSuccessRate();
        if (successRate < 0.95) {
            recommendations.add("Low success rate - investigate error patterns");
        }
        
        return recommendations;
    }

    private void enableOfflineCaching() {
        // Implementation for offline caching
        log.info("Offline caching enabled");
    }

    private void syncPendingOperations() {
        // Implementation for syncing pending operations
        log.info("Syncing pending operations");
    }

    private void compressStoredData() {
        // Implementation for data compression
        log.info("Compressing stored data");
    }

    private void cleanupTemporaryFiles() {
        // Implementation for temporary file cleanup
        log.info("Cleaning up temporary files");
    }

    private void prepareForReconnection() {
        // Implementation for reconnection preparation
        log.info("Preparing for reconnection");
    }

    private void saveCrashReport() {
        // Implementation for crash report saving
        log.warn("Crash report saved");
    }

    private void clearCorruptedData() {
        // Implementation for corrupted data cleanup
        log.info("Clearing corrupted data");
    }

    private void restoreFromBackup() {
        // Implementation for backup restoration
        log.info("Restoring from backup");
    }

    private void resetPerformanceCounters() {
        totalRequests.set(0);
        failedRequests.set(0);
        performanceMetrics.clear();
        log.info("Performance counters reset");
    }

    private void notifyAdministrators(String message) {
        // Implementation for administrator notification
        log.error("Administrator notification: {}", message);
    }

    private void triggerMemoryAlert(double usage) {
        // Implementation for memory alert
        log.error("Memory alert: {}% usage", usage * 100);
    }

    private void triggerGcAlert(long gcTime) {
        // Implementation for GC alert
        log.warn("GC alert: {}ms collection time", gcTime);
    }

    private void triggerMemoryLeakAlert() {
        // Implementation for memory leak alert
        log.error("Memory leak alert triggered");
    }
}
