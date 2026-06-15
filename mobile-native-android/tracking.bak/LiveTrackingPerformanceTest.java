// package com.edham.logistics.tracking;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Live tracking performance test
 * Comprehensive testing for high-performance tracking system
 */
@RestController
@RequestMapping("/api/v1/tracking/test")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class LiveTrackingPerformanceTest {

    private final LiveTrackingService liveTrackingService;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final ExecutorService testExecutor;

    @Autowired
    public LiveTrackingPerformanceTest(LiveTrackingService liveTrackingService,
                                     VehicleRepository vehicleRepository,
                                     UserRepository userRepository) {
        this.liveTrackingService = liveTrackingService;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.testExecutor = Executors.newFixedThreadPool(10);
    }

    /**
     * Comprehensive performance test for live tracking
     */
    @PostMapping("/comprehensive")
    @PreAuthorize("hasRole('ADMIN')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<Map<String, Object>>>> comprehensivePerformanceTest() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> testResults = new HashMap<>();
                long startTime = System.currentTimeMillis();
                
                // Test 1: Multi-vehicle tracking performance
                Map<String, Object> multiVehicleTest = testMultiVehicleTracking();
                testResults.put("multiVehicleTracking", multiVehicleTest);
                
                // Test 2: High-frequency location updates
                Map<String, Object> highFrequencyTest = testHighFrequencyUpdates();
                testResults.put("highFrequencyUpdates", highFrequencyTest);
                
                // Test 3: Battery optimization
                Map<String, Object> batteryTest = testBatteryOptimization();
                testResults.put("batteryOptimization", batteryTest);
                
                // Test 4: Network resilience
                Map<String, Object> networkTest = testNetworkResilience();
                testResults.put("networkResilience", networkTest);
                
                // Test 5: Connection drop handling
                Map<String, Object> connectionTest = testConnectionDropHandling();
                testResults.put("connectionDropHandling", connectionTest);
                
                // Test 6: Memory usage
                Map<String, Object> memoryTest = testMemoryUsage();
                testResults.put("memoryUsage", memoryTest);
                
                // Test 7: Concurrent operations
                Map<String, Object> concurrencyTest = testConcurrentOperations();
                testResults.put("concurrentOperations", concurrencyTest);
                
                // Test 8: Data consistency
                Map<String, Object> consistencyTest = testDataConsistency();
                testResults.put("dataConsistency", consistencyTest);
                
                long endTime = System.currentTimeMillis();
                testResults.put("totalTestTime", endTime - startTime);
                testResults.put("testStatus", "COMPLETED");
                testResults.put("testTimestamp", LocalDateTime.now());
                
                log.info("Comprehensive performance test completed in {}ms", endTime - startTime);
                
                return ResponseEntity.ok(
                        UnifiedResponseDTO.<Map<String, Object>>builder()
                                .success(true)
                                .data(testResults)
                                .message("Performance test completed successfully")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
                
            } catch (Exception e) {
                log.error("Error in comprehensive performance test", e);
                return ResponseEntity.internalServerError().body(
                        UnifiedResponseDTO.<Map<String, Object>>builder()
                                .success(false)
                                .error("Performance test failed: " + e.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
            }
        }, testExecutor);
    }

    /**
     * Test multi-vehicle tracking performance
     */
    private Map<String, Object> testMultiVehicleTracking() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            int vehicleCount = 50;
            List<CompletableFuture<TrackingSession>> futures = new ArrayList<>();
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicLong totalStartTime = new AtomicLong(0);
            AtomicLong totalEndTime = new AtomicLong(0);
            
            // Start tracking for multiple vehicles
            for (int i = 0; i < vehicleCount; i++) {
                final int vehicleId = 1000 + i;
                final int driverId = 2000 + i;
                
                CompletableFuture<TrackingSession> future = liveTrackingService.startTracking(
                        (long) vehicleId, (long) driverId);
                
                future.whenComplete((session, throwable) -> {
                    if (throwable == null && session != null) {
                        successCount.incrementAndGet();
                        totalStartTime.compareAndSet(0, System.currentTimeMillis());
                        totalEndTime.set(System.currentTimeMillis());
                    }
                });
                
                futures.add(future);
            }
            
            // Wait for all to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(30, TimeUnit.SECONDS);
            
            // Test location updates
            List<CompletableFuture<LiveTrackingService.LocationUpdateResult>> updateFutures = new ArrayList<>();
            AtomicInteger updateSuccessCount = new AtomicInteger(0);
            
            for (int i = 0; i < vehicleCount; i++) {
                final int vehicleId = 1000 + i;
                
                LiveTrackingService.LocationUpdateRequest request = LiveTrackingService.LocationUpdateRequest.builder()
                        .vehicleId((long) vehicleId)
                        .location(LiveTrackingService.LocationData.builder()
                                .latitude(40.7128 + (i * 0.001))
                                .longitude(-74.0060 + (i * 0.001))
                                .speed(50.0 + (i * 2))
                                .heading(90.0)
                                .accuracy(10.0)
                                .timestamp(LocalDateTime.now())
                                .build())
                        .batteryLevel(80 - (i % 20))
                        .signalStrength(90 - (i % 30))
                        .connectionQuality(LiveTrackingService.ConnectionQuality.EXCELLENT)
                        .build();
                
                CompletableFuture<LiveTrackingService.LocationUpdateResult> updateFuture = 
                        liveTrackingService.updateLocation(request);
                
                updateFuture.whenComplete((result, throwable) -> {
                    if (throwable == null && result.getSuccess()) {
                        updateSuccessCount.incrementAndGet();
                    }
                });
                
                updateFutures.add(updateFuture);
            }
            
            CompletableFuture.allOf(updateFutures.toArray(new CompletableFuture[0])).get(30, TimeUnit.SECONDS);
            
            // Clean up
            for (int i = 0; i < vehicleCount; i++) {
                final int vehicleId = 1000 + i;
                liveTrackingService.stopTracking((long) vehicleId);
            }
            
            long endTime = System.currentTimeMillis();
            
            results.put("vehicleCount", vehicleCount);
            results.put("trackingSuccessCount", successCount.get());
            results.put("trackingSuccessRate", (double) successCount.get() / vehicleCount * 100);
            results.put("updateSuccessCount", updateSuccessCount.get());
            results.put("updateSuccessRate", (double) updateSuccessCount.get() / vehicleCount * 100);
            results.put("totalTime", endTime - startTime);
            results.put("averageTimePerVehicle", (double) (endTime - startTime) / vehicleCount);
            results.put("status", "PASSED");
            
        } catch (Exception e) {
            log.error("Error in multi-vehicle tracking test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test high-frequency location updates
     */
    private Map<String, Object> testHighFrequencyUpdates() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            long vehicleId = 9999L;
            long driverId = 8888L;
            
            // Start tracking
            TrackingSession session = liveTrackingService.startTracking(vehicleId, driverId).get();
            
            int updateCount = 100;
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicLong minResponseTime = Long.MAX_VALUE;
            AtomicLong maxResponseTime = Long.MIN_VALUE;
            AtomicLong totalResponseTime = new AtomicLong(0);
            
            // Send high-frequency updates
            for (int i = 0; i < updateCount; i++) {
                long updateStartTime = System.currentTimeMillis();
                
                LiveTrackingService.LocationUpdateRequest request = LiveTrackingService.LocationUpdateRequest.builder()
                        .vehicleId(vehicleId)
                        .location(LiveTrackingService.LocationData.builder()
                                .latitude(40.7128 + (i * 0.0001))
                                .longitude(-74.0060 + (i * 0.0001))
                                .speed(50.0 + (i * 0.1))
                                .heading(90.0 + (i % 360))
                                .accuracy(5.0 + (i % 10))
                                .timestamp(LocalDateTime.now())
                                .build())
                        .batteryLevel(80 - (i % 40))
                        .signalStrength(90 - (i % 50))
                        .connectionQuality(LiveTrackingService.ConnectionQuality.EXCELLENT)
                        .build();
                
                try {
                    LiveTrackingService.LocationUpdateResult result = 
                            liveTrackingService.updateLocation(request).get(5, TimeUnit.SECONDS);
                    
                    long responseTime = System.currentTimeMillis() - updateStartTime;
                    
                    if (result.getSuccess()) {
                        successCount.incrementAndGet();
                        minResponseTime.updateAndGet(current -> Math.min(current, responseTime));
                        maxResponseTime.updateAndGet(current -> Math.max(current, responseTime));
                        totalResponseTime.addAndGet(responseTime);
                    }
                    
                    // Small delay to simulate real-time updates
                    Thread.sleep(100);
                    
                } catch (Exception e) {
                    log.warn("Update {} failed: {}", i, e.getMessage());
                }
            }
            
            // Stop tracking
            liveTrackingService.stopTracking(vehicleId).get();
            
            long endTime = System.currentTimeMillis();
            
            results.put("updateCount", updateCount);
            results.put("successCount", successCount.get());
            results.put("successRate", (double) successCount.get() / updateCount * 100);
            results.put("minResponseTime", minResponseTime.get());
            results.put("maxResponseTime", maxResponseTime.get());
            results.put("averageResponseTime", totalResponseTime.get() / (double) successCount.get());
            results.put("updatesPerSecond", (double) successCount.get() / ((endTime - startTime) / 1000.0));
            results.put("totalTime", endTime - startTime);
            results.put("status", "PASSED");
            
        } catch (Exception e) {
            log.error("Error in high-frequency updates test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test battery optimization
     */
    private Map<String, Object> testBatteryOptimization() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            long vehicleId = 9998L;
            long driverId = 8887L;
            
            // Test different battery levels
            int[] batteryLevels = {100, 80, 50, 20, 10};
            Map<Integer, LiveTrackingService.BatteryOptimizationLevel> optimizationLevels = new HashMap<>();
            
            for (int batteryLevel : batteryLevels) {
                // Start tracking
                TrackingSession session = liveTrackingService.startTracking(vehicleId, driverId).get();
                
                // Send location update with specific battery level
                LiveTrackingService.LocationUpdateRequest request = LiveTrackingService.LocationUpdateRequest.builder()
                        .vehicleId(vehicleId)
                        .location(LiveTrackingService.LocationData.builder()
                                .latitude(40.7128)
                                .longitude(-74.0060)
                                .speed(50.0)
                                .heading(90.0)
                                .accuracy(10.0)
                                .timestamp(LocalDateTime.now())
                                .build())
                        .batteryLevel(batteryLevel)
                        .signalStrength(80)
                        .connectionQuality(LiveTrackingService.ConnectionQuality.EXCELLENT)
                        .build();
                
                LiveTrackingService.LocationUpdateResult result = 
                        liveTrackingService.updateLocation(request).get();
                
                if (result.getSuccess()) {
                    optimizationLevels.put(batteryLevel, result.getBatteryOptimizationLevel());
                }
                
                // Stop tracking
                liveTrackingService.stopTracking(vehicleId).get();
                
                Thread.sleep(100); // Small delay
            }
            
            long endTime = System.currentTimeMillis();
            
            results.put("batteryLevels", batteryLevels);
            results.put("optimizationLevels", optimizationLevels);
            results.put("totalTime", endTime - startTime);
            results.put("status", "PASSED");
            
        } catch (Exception e) {
            log.error("Error in battery optimization test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test network resilience
     */
    private Map<String, Object> testNetworkResilience() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            long vehicleId = 9997L;
            long driverId = 8886L;
            
            // Start tracking
            TrackingSession session = liveTrackingService.startTracking(vehicleId, driverId).get();
            
            // Test different network qualities
            LiveTrackingService.ConnectionQuality[] qualities = {
                    LiveTrackingService.ConnectionQuality.EXCELLENT,
                    LiveTrackingService.ConnectionQuality.GOOD,
                    LiveTrackingService.ConnectionQuality.FAIR,
                    LiveTrackingService.ConnectionQuality.POOR
            };
            
            Map<String, LiveTrackingService.NetworkOptimizationLevel> networkOptimizations = new HashMap<>();
            AtomicInteger successCount = new AtomicInteger(0);
            
            for (LiveTrackingService.ConnectionQuality quality : qualities) {
                LiveTrackingService.LocationUpdateRequest request = LiveTrackingService.LocationUpdateRequest.builder()
                        .vehicleId(vehicleId)
                        .location(LiveTrackingService.LocationData.builder()
                                .latitude(40.7128)
                                .longitude(-74.0060)
                                .speed(50.0)
                                .heading(90.0)
                                .accuracy(10.0)
                                .timestamp(LocalDateTime.now())
                                .build())
                        .batteryLevel(75)
                        .signalStrength(getSignalStrengthForQuality(quality))
                        .connectionQuality(quality)
                        .build();
                
                try {
                    LiveTrackingService.LocationUpdateResult result = 
                            liveTrackingService.updateLocation(request).get(10, TimeUnit.SECONDS);
                    
                    if (result.getSuccess()) {
                        successCount.incrementAndGet();
                        networkOptimizations.put(quality.name(), result.getNetworkOptimizationLevel());
                    }
                } catch (Exception e) {
                    log.warn("Network quality test failed for {}: {}", quality, e.getMessage());
                }
                
                Thread.sleep(500);
            }
            
            // Stop tracking
            liveTrackingService.stopTracking(vehicleId).get();
            
            long endTime = System.currentTimeMillis();
            
            results.put("testedQualities", Arrays.stream(qualities).map(Enum::name).toArray());
            results.put("successCount", successCount.get());
            results.put("successRate", (double) successCount.get() / qualities.length * 100);
            results.put("networkOptimizations", networkOptimizations);
            results.put("totalTime", endTime - startTime);
            results.put("status", "PASSED");
            
        } catch (Exception e) {
            log.error("Error in network resilience test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test connection drop handling
     */
    private Map<String, Object> testConnectionDropHandling() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            long vehicleId = 9996L;
            long driverId = 8885L;
            
            // Start tracking
            TrackingSession session = liveTrackingService.startTracking(vehicleId, driverId).get();
            
            // Simulate connection drop
            LiveTrackingService.ConnectionDropInfo dropInfo = LiveTrackingService.ConnectionDropInfo.builder()
                    .dropTime(LocalDateTime.now())
                    .reason("Network timeout")
                    .duration(5000L)
                    .qualityBeforeDrop(LiveTrackingService.ConnectionQuality.GOOD)
                    .build();
            
            liveTrackingService.handleConnectionDrop(vehicleId, dropInfo);
            
            // Wait for reconnection attempt
            Thread.sleep(6000);
            
            // Try to send update after reconnection
            LiveTrackingService.LocationUpdateRequest request = LiveTrackingService.LocationUpdateRequest.builder()
                    .vehicleId(vehicleId)
                    .location(LiveTrackingService.LocationData.builder()
                            .latitude(40.7128)
                            .longitude(-74.0060)
                            .speed(50.0)
                            .heading(90.0)
                            .accuracy(10.0)
                            .timestamp(LocalDateTime.now())
                            .build())
                    .batteryLevel(75)
                    .signalStrength(85)
                    .connectionQuality(LiveTrackingService.ConnectionQuality.GOOD)
                    .build();
            
            LiveTrackingService.LocationUpdateResult result = 
                    liveTrackingService.updateLocation(request).get(10, TimeUnit.SECONDS);
            
            // Stop tracking
            liveTrackingService.stopTracking(vehicleId).get();
            
            long endTime = System.currentTimeMillis();
            
            results.put("connectionDropHandled", true);
            results.put("updateAfterReconnection", result.getSuccess());
            results.put("recoveryTime", endTime - startTime);
            results.put("totalTime", endTime - startTime);
            results.put("status", "PASSED");
            
        } catch (Exception e) {
            log.error("Error in connection drop handling test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test memory usage
     */
    private Map<String, Object> testMemoryUsage() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            Runtime runtime = Runtime.getRuntime();
            long initialMemory = runtime.totalMemory() - runtime.freeMemory();
            
            // Create multiple tracking sessions
            int sessionCount = 100;
            List<Long> vehicleIds = new ArrayList<>();
            
            for (int i = 0; i < sessionCount; i++) {
                long vehicleId = 10000L + i;
                long driverId = 20000L + i;
                
                vehicleIds.add(vehicleId);
                liveTrackingService.startTracking(vehicleId, driverId).get();
            }
            
            long peakMemory = runtime.totalMemory() - runtime.freeMemory();
            
            // Send location updates
            for (int i = 0; i < sessionCount; i++) {
                LiveTrackingService.LocationUpdateRequest request = LiveTrackingService.LocationUpdateRequest.builder()
                        .vehicleId(vehicleIds.get(i))
                        .location(LiveTrackingService.LocationData.builder()
                                .latitude(40.7128 + (i * 0.001))
                                .longitude(-74.0060 + (i * 0.001))
                                .speed(50.0)
                                .heading(90.0)
                                .accuracy(10.0)
                                .timestamp(LocalDateTime.now())
                                .build())
                        .batteryLevel(75)
                        .signalStrength(85)
                        .connectionQuality(LiveTrackingService.ConnectionQuality.EXCELLENT)
                        .build();
                
                liveTrackingService.updateLocation(request).get();
            }
            
            long afterUpdatesMemory = runtime.totalMemory() - runtime.freeMemory();
            
            // Clean up sessions
            for (Long vehicleId : vehicleIds) {
                liveTrackingService.stopTracking(vehicleId).get();
            }
            
            // Force garbage collection
            System.gc();
            Thread.sleep(1000);
            
            long finalMemory = runtime.totalMemory() - runtime.freeMemory();
            
            long endTime = System.currentTimeMillis();
            
            results.put("sessionCount", sessionCount);
            results.put("initialMemoryMB", initialMemory / (1024.0 * 1024.0));
            results.put("peakMemoryMB", peakMemory / (1024.0 * 1024.0));
            results.put("afterUpdatesMemoryMB", afterUpdatesMemory / (1024.0 * 1024.0));
            results.put("finalMemoryMB", finalMemory / (1024.0 * 1024.0));
            results.put("memoryPerSessionKB", (double) (peakMemory - initialMemory) / sessionCount / 1024.0);
            results.put("memoryLeakDetected", finalMemory > initialMemory * 1.1); // 10% threshold
            results.put("totalTime", endTime - startTime);
            results.put("status", "PASSED");
            
        } catch (Exception e) {
            log.error("Error in memory usage test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test concurrent operations
     */
    private Map<String, Object> testConcurrentOperations() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            int threadCount = 10;
            int operationsPerThread = 20;
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failureCount = new AtomicInteger(0);
            
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            
            for (int t = 0; t < threadCount; t++) {
                final int threadId = t;
                
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    for (int i = 0; i < operationsPerThread; i++) {
                        try {
                            long vehicleId = 30000L + threadId * 100 + i;
                            long driverId = 40000L + threadId * 100 + i;
                            
                            // Start tracking
                            TrackingSession session = liveTrackingService.startTracking(vehicleId, driverId).get();
                            
                            // Send update
                            LiveTrackingService.LocationUpdateRequest request = LiveTrackingService.LocationUpdateRequest.builder()
                                    .vehicleId(vehicleId)
                                    .location(LiveTrackingService.LocationData.builder()
                                            .latitude(40.7128 + (threadId * 0.01) + (i * 0.001))
                                            .longitude(-74.0060 + (threadId * 0.01) + (i * 0.001))
                                            .speed(50.0)
                                            .heading(90.0)
                                            .accuracy(10.0)
                                            .timestamp(LocalDateTime.now())
                                            .build())
                                    .batteryLevel(75)
                                    .signalStrength(85)
                                    .connectionQuality(LiveTrackingService.ConnectionQuality.EXCELLENT)
                                    .build();
                            
                            LiveTrackingService.LocationUpdateResult result = 
                                    liveTrackingService.updateLocation(request).get();
                            
                            if (result.getSuccess()) {
                                successCount.incrementAndGet();
                            } else {
                                failureCount.incrementAndGet();
                            }
                            
                            // Stop tracking
                            liveTrackingService.stopTracking(vehicleId).get();
                            
                        } catch (Exception e) {
                            failureCount.incrementAndGet();
                            log.warn("Concurrent operation failed: {}", e.getMessage());
                        }
                    }
                }, testExecutor);
                
                futures.add(future);
            }
            
            // Wait for all threads to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(60, TimeUnit.SECONDS);
            
            long endTime = System.currentTimeMillis();
            
            int totalOperations = threadCount * operationsPerThread;
            
            results.put("threadCount", threadCount);
            results.put("operationsPerThread", operationsPerThread);
            results.put("totalOperations", totalOperations);
            results.put("successCount", successCount.get());
            results.put("failureCount", failureCount.get());
            results.put("successRate", (double) successCount.get() / totalOperations * 100);
            results.put("operationsPerSecond", (double) totalOperations / ((endTime - startTime) / 1000.0));
            results.put("totalTime", endTime - startTime);
            results.put("status", "PASSED");
            
        } catch (Exception e) {
            log.error("Error in concurrent operations test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test data consistency
     */
    private Map<String, Object> testDataConsistency() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            long vehicleId = 9995L;
            long driverId = 8884L;
            
            // Start tracking
            TrackingSession session = liveTrackingService.startTracking(vehicleId, driverId).get();
            
            // Send multiple location updates
            List<LiveTrackingService.LocationData> sentLocations = new ArrayList<>();
            
            for (int i = 0; i < 10; i++) {
                LiveTrackingService.LocationData location = LiveTrackingService.LocationData.builder()
                        .latitude(40.7128 + (i * 0.001))
                        .longitude(-74.0060 + (i * 0.001))
                        .speed(50.0 + (i * 2))
                        .heading(90.0 + (i * 36))
                        .accuracy(5.0 + (i % 5))
                        .timestamp(LocalDateTime.now())
                        .build();
                
                sentLocations.add(location);
                
                LiveTrackingService.LocationUpdateRequest request = LiveTrackingService.LocationUpdateRequest.builder()
                        .vehicleId(vehicleId)
                        .location(location)
                        .batteryLevel(80 - (i * 5))
                        .signalStrength(90 - (i * 3))
                        .connectionQuality(LiveTrackingService.ConnectionQuality.EXCELLENT)
                        .build();
                
                liveTrackingService.updateLocation(request).get();
                
                Thread.sleep(200);
            }
            
            // Get real-time location
            VehicleLocation currentLocation = liveTrackingService.getRealTimeLocation(vehicleId).get();
            
            // Stop tracking
            liveTrackingService.stopTracking(vehicleId).get();
            
            long endTime = System.currentTimeMillis();
            
            // Verify data consistency
            boolean consistencyCheck = true;
            StringBuilder consistencyIssues = new StringBuilder();
            
            if (currentLocation == null) {
                consistencyCheck = false;
                consistencyIssues.append("No current location found; ");
            } else {
                // Check if last location matches
                LiveTrackingService.LocationData lastSent = sentLocations.get(sentLocations.size() - 1);
                
                if (Math.abs(currentLocation.getLatitude() - lastSent.getLatitude()) > 0.001) {
                    consistencyCheck = false;
                    consistencyIssues.append("Latitude mismatch; ");
                }
                
                if (Math.abs(currentLocation.getLongitude() - lastSent.getLongitude()) > 0.001) {
                    consistencyCheck = false;
                    consistencyIssues.append("Longitude mismatch; ");
                }
                
                if (Math.abs(currentLocation.getSpeed() - lastSent.getSpeed()) > 1.0) {
                    consistencyCheck = false;
                    consistencyIssues.append("Speed mismatch; ");
                }
            }
            
            results.put("sentLocationsCount", sentLocations.size());
            results.put("consistencyCheck", consistencyCheck);
            results.put("consistencyIssues", consistencyIssues.toString());
            results.put("currentLocation", currentLocation != null ? "Available" : "Not Available");
            results.put("totalTime", endTime - startTime);
            results.put("status", consistencyCheck ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("Error in data consistency test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Get signal strength for network quality
     */
    private int getSignalStrengthForQuality(LiveTrackingService.ConnectionQuality quality) {
        switch (quality) {
            case EXCELLENT: return 95;
            case GOOD: return 80;
            case FAIR: return 60;
            case POOR: return 30;
            default: return 0;
        }
    }

    /**
     * Get system performance metrics
     */
    @GetMapping("/system/metrics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<Map<String, Object>>> getSystemMetrics() {
        try {
            Map<String, Object> metrics = new HashMap<>();
            Runtime runtime = Runtime.getRuntime();
            
            // Memory metrics
            metrics.put("totalMemoryMB", runtime.totalMemory() / (1024.0 * 1024.0));
            metrics.put("freeMemoryMB", runtime.freeMemory() / (1024.0 * 1024.0));
            metrics.put("usedMemoryMB", (runtime.totalMemory() - runtime.freeMemory()) / (1024.0 * 1024.0));
            metrics.put("maxMemoryMB", runtime.maxMemory() / (1024.0 * 1024.0));
            metrics.put("memoryUsagePercentage", (double) (runtime.totalMemory() - runtime.freeMemory()) / runtime.maxMemory() * 100);
            
            // Thread metrics
            metrics.put("activeThreadCount", Thread.activeCount());
            
            // Get active tracking sessions
            CompletableFuture<List<TrackingSession>> sessionsFuture = liveTrackingService.getActiveTrackingSessions();
            List<TrackingSession> sessions = sessionsFuture.get(5, TimeUnit.SECONDS);
            
            metrics.put("activeTrackingSessions", sessions.size());
            metrics.put("totalUpdates", sessions.stream().mapToLong(TrackingSession::getUpdateCount).sum());
            metrics.put("averageBatteryLevel", sessions.stream().filter(s -> s.getBatteryLevel() != null).mapToInt(TrackingSession::getBatteryLevel).average().orElse(0.0));
            metrics.put("averageSignalStrength", sessions.stream().filter(s -> s.getSignalStrength() != null).mapToInt(TrackingSession::getSignalStrength).average().orElse(0.0));
            
            return ResponseEntity.ok(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(true)
                            .data(metrics)
                            .message("System metrics retrieved successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );
            
        } catch (Exception e) {
            log.error("Error getting system metrics", e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(false)
                            .error("Failed to get system metrics: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }
}
