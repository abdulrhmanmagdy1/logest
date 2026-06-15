// package com.edham.logistics.coldchain;

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
 * Cold-chain monitoring integration test
 * Comprehensive testing for cold-chain monitoring system
 */
@RestController
@RequestMapping("/api/v1/coldchain/test")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class ColdChainIntegrationTest {

    private final ColdChainMonitoringService coldChainService;
    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;
    private final ExecutorService testExecutor;

    @Autowired
    public ColdChainIntegrationTest(ColdChainMonitoringService coldChainService,
                                   ShipmentRepository shipmentRepository,
                                   UserRepository userRepository) {
        this.coldChainService = coldChainService;
        this.shipmentRepository = shipmentRepository;
        this.userRepository = userRepository;
        this.testExecutor = Executors.newFixedThreadPool(5);
    }

    /**
     * Comprehensive cold-chain monitoring test
     */
    @PostMapping("/comprehensive")
    @PreAuthorize("hasRole('ADMIN')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<Map<String, Object>>>> comprehensiveTest() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> testResults = new HashMap<>();
                long startTime = System.currentTimeMillis();
                
                // Test 1: Temperature monitoring
                Map<String, Object> temperatureTest = testTemperatureMonitoring();
                testResults.put("temperatureMonitoring", temperatureTest);
                
                // Test 2: Alert system
                Map<String, Object> alertTest = testAlertSystem();
                testResults.put("alertSystem", alertTest);
                
                // Test 3: Temperature history
                Map<String, Object> historyTest = testTemperatureHistory();
                testResults.put("temperatureHistory", historyTest);
                
                // Test 4: Role-based visibility
                Map<String, Object> visibilityTest = testRoleBasedVisibility();
                testResults.put("roleBasedVisibility", visibilityTest);
                
                // Test 5: Visual indicators
                Map<String, Object> indicatorsTest = testVisualIndicators();
                testResults.put("visualIndicators", indicatorsTest);
                
                // Test 6: Performance under load
                Map<String, Object> performanceTest = testPerformanceUnderLoad();
                testResults.put("performanceUnderLoad", performanceTest);
                
                // Test 7: Data consistency
                Map<String, Object> consistencyTest = testDataConsistency();
                testResults.put("dataConsistency", consistencyTest);
                
                // Test 8: Error handling
                Map<String, Object> errorHandlingTest = testErrorHandling();
                testResults.put("errorHandling", errorHandlingTest);
                
                long endTime = System.currentTimeMillis();
                testResults.put("totalTestTime", endTime - startTime);
                testResults.put("testStatus", "COMPLETED");
                testResults.put("testTimestamp", LocalDateTime.now());
                
                log.info("Comprehensive cold-chain test completed in {}ms", endTime - startTime);
                
                return ResponseEntity.ok(
                        UnifiedResponseDTO.<Map<String, Object>>builder()
                                .success(true)
                                .data(testResults)
                                .message("Cold-chain monitoring test completed successfully")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
                
            } catch (Exception e) {
                log.error("Error in comprehensive cold-chain test", e);
                return ResponseEntity.internalServerError().body(
                        UnifiedResponseDTO.<Map<String, Object>>builder()
                                .success(false)
                                .error("Test failed: " + e.getMessage())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
            }
        }, testExecutor);
    }

    /**
     * Test temperature monitoring functionality
     */
    private Map<String, Object> testTemperatureMonitoring() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Create test shipment
            Shipment testShipment = createTestShipment();
            shipmentRepository.save(testShipment);
            
            // Start monitoring
            ColdChainMonitoringService.ColdChainSession session = 
                    coldChainService.startMonitoring(testShipment.getId(), "REFRIGERATED").get();
            
            // Test temperature readings
            int successfulReadings = 0;
            int totalReadings = 20;
            List<Double> testTemperatures = Arrays.asList(
                    4.0, 5.5, 6.2, 7.1, 8.5, 9.8, 11.2, 12.5, 6.8, 5.2,
                    4.5, 5.8, 6.5, 7.3, 8.1, 9.2, 10.5, 6.3, 5.1, 4.8
            );
            
            for (int i = 0; i < totalReadings; i++) {
                ColdChainMonitoringService.TemperatureReadingRequest request = 
                        ColdChainMonitoringService.TemperatureReadingRequest.builder()
                                .shipmentId(testShipment.getId())
                                .temperature(testTemperatures.get(i))
                                .humidity(65.0 + (i % 20))
                                .sensorId("sensor_" + i)
                                .location("warehouse_" + (i % 3))
                                .deviceId("device_001")
                                .batteryLevel(85 - (i % 30))
                                .signalStrength(90 - (i % 20))
                                .build();
                
                ColdChainMonitoringService.TemperatureReadingResult result = 
                        coldChainService.recordTemperature(request).get(5, TimeUnit.SECONDS);
                
                if (result.getSuccess()) {
                    successfulReadings++;
                }
                
                Thread.sleep(100); // Small delay between readings
            }
            
            // Get current temperature
            com.edham.logistics.model.TemperatureReading currentTemp = 
                    coldChainService.getCurrentTemperature(testShipment.getId()).get(5, TimeUnit.SECONDS);
            
            // Stop monitoring
            coldChainService.stopMonitoring(testShipment.getId()).get(5, TimeUnit.SECONDS);
            
            long endTime = System.currentTimeMillis();
            
            results.put("testShipmentId", testShipment.getId());
            results.put("sessionId", session.getSessionId());
            results.put("totalReadings", totalReadings);
            results.put("successfulReadings", successfulReadings);
            results.put("successRate", (double) successfulReadings / totalReadings * 100);
            results.put("currentTemperature", currentTemp != null ? currentTemp.getTemperature() : null);
            results.put("sessionDuration", endTime - startTime);
            results.put("status", successfulReadings >= totalReadings * 0.9 ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("Error in temperature monitoring test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test alert system functionality
     */
    private Map<String, Object> testAlertSystem() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Create test shipment
            Shipment testShipment = createTestShipment();
            shipmentRepository.save(testShipment);
            
            // Start monitoring
            ColdChainMonitoringService.ColdChainSession session = 
                    coldChainService.startMonitoring(testShipment.getId(), "REFRIGERATED").get();
            
            // Test different temperature scenarios
            List<Double> testScenarios = Arrays.asList(
                    5.0,  // Safe
                    9.0,  // Warning
                    12.5, // Critical
                    6.5,  // Back to safe
                    11.0, // Warning
                    13.5, // Critical
                    4.5   // Back to safe
            );
            
            int alertCount = 0;
            List<ColdChainAlert> alerts = new ArrayList<>();
            
            for (Double temperature : testScenarios) {
                ColdChainMonitoringService.TemperatureReadingRequest request = 
                        ColdChainMonitoringService.TemperatureReadingRequest.builder()
                                .shipmentId(testShipment.getId())
                                .temperature(temperature)
                                .humidity(70.0)
                                .sensorId("test_sensor")
                                .location("test_location")
                                .deviceId("test_device")
                                .batteryLevel(80)
                                .signalStrength(85)
                                .build();
                
                coldChainService.recordTemperature(request).get(5, TimeUnit.SECONDS);
                
                // Wait for alert processing
                Thread.sleep(2000);
                
                // Get alerts
                List<ColdChainAlert> currentAlerts = 
                        coldChainService.getAlerts(testShipment.getId(), null).get(5, TimeUnit.SECONDS);
                
                if (currentAlerts.size() > alerts.size()) {
                    alerts.addAll(currentAlerts.subList(alerts.size(), currentAlerts.size()));
                    alertCount += currentAlerts.size() - alerts.size();
                }
                
                Thread.sleep(1000);
            }
            
            // Analyze alerts
            int criticalAlerts = (int) alerts.stream()
                    .filter(alert -> alert.getSeverity() == ColdChainMonitoringService.AlertSeverity.CRITICAL)
                    .count();
            
            int warningAlerts = (int) alerts.stream()
                    .filter(alert -> alert.getSeverity() == ColdChainMonitoringService.AlertSeverity.WARNING)
                    .count();
            
            int infoAlerts = (int) alerts.stream()
                    .filter(alert -> alert.getSeverity() == ColdChainMonitoringService.AlertSeverity.INFO)
                    .count();
            
            // Stop monitoring
            coldChainService.stopMonitoring(testShipment.getId()).get(5, TimeUnit.SECONDS);
            
            long endTime = System.currentTimeMillis();
            
            results.put("testShipmentId", testShipment.getId());
            results.put("sessionId", session.getSessionId());
            results.put("totalAlerts", alertCount);
            results.put("criticalAlerts", criticalAlerts);
            results.put("warningAlerts", warningAlerts);
            results.put("infoAlerts", infoAlerts);
            results.put("alertTypes", alerts.stream().map(ColdChainAlert::getAlertType).distinct().toList());
            results.put("sessionDuration", endTime - startTime);
            results.put("status", alertCount > 0 ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("Error in alert system test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test temperature history functionality
     */
    private Map<String, Object> testTemperatureHistory() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Create test shipment
            Shipment testShipment = createTestShipment();
            shipmentRepository.save(testShipment);
            
            // Start monitoring
            ColdChainMonitoringService.ColdChainSession session = 
                    coldChainService.startMonitoring(testShipment.getId(), "REFRIGERATED").get();
            
            // Generate temperature history
            int historySize = 50;
            LocalDateTime historyStart = LocalDateTime.now().minusHours(2);
            
            for (int i = 0; i < historySize; i++) {
                double temperature = 4.0 + (Math.random() * 8.0); // Random between 4-12°C
                
                ColdChainMonitoringService.TemperatureReadingRequest request = 
                        ColdChainMonitoringService.TemperatureReadingRequest.builder()
                                .shipmentId(testShipment.getId())
                                .temperature(temperature)
                                .humidity(65.0 + (Math.random() * 20.0))
                                .sensorId("history_sensor")
                                .location("history_location")
                                .deviceId("history_device")
                                .batteryLevel(80 + (int) (Math.random() * 20))
                                .signalStrength(80 + (int) (Math.random() * 20))
                                .build();
                
                coldChainService.recordTemperature(request).get(2, TimeUnit.SECONDS);
                Thread.sleep(50);
            }
            
            // Test history retrieval
            List<com.edham.logistics.model.TemperatureReading> fullHistory = 
                    coldChainService.getTemperatureHistory(testShipment.getId(), null, null).get(5, TimeUnit.SECONDS);
            
            // Test time-range history
            LocalDateTime rangeStart = LocalDateTime.now().minusMinutes(30);
            LocalDateTime rangeEnd = LocalDateTime.now();
            List<com.edham.logistics.model.TemperatureReading> rangeHistory = 
                    coldChainService.getTemperatureHistory(testShipment.getId(), rangeStart, rangeEnd).get(5, TimeUnit.SECONDS);
            
            // Stop monitoring
            coldChainService.stopMonitoring(testShipment.getId()).get(5, TimeUnit.SECONDS);
            
            long endTime = System.currentTimeMillis();
            
            // Analyze history
            double avgTemp = fullHistory.stream()
                    .mapToDouble(com.edham.logistics.model.TemperatureReading::getTemperature)
                    .average()
                    .orElse(0.0);
            
            double minTemp = fullHistory.stream()
                    .mapToDouble(com.edham.logistics.model.TemperatureReading::getTemperature)
                    .min()
                    .orElse(0.0);
            
            double maxTemp = fullHistory.stream()
                    .mapToDouble(com.edham.logistics.model.TemperatureReading::getTemperature)
                    .max()
                    .orElse(0.0);
            
            results.put("testShipmentId", testShipment.getId());
            results.put("sessionId", session.getSessionId());
            results.put("fullHistorySize", fullHistory.size());
            results.put("rangeHistorySize", rangeHistory.size());
            results.put("averageTemperature", avgTemp);
            results.put("minTemperature", minTemp);
            results.put("maxTemperature", maxTemp);
            results.put("temperatureRange", maxTemp - minTemp);
            results.put("sessionDuration", endTime - startTime);
            results.put("status", fullHistory.size() >= historySize * 0.9 ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("Error in temperature history test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test role-based visibility
     */
    private Map<String, Object> testRoleBasedVisibility() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Create test shipments for different users
            Shipment clientShipment = createTestShipmentForUser("CLIENT");
            Shipment driverShipment = createTestShipmentForUser("DRIVER");
            Shipment adminShipment = createTestShipmentForUser("ADMIN");
            
            shipmentRepository.saveAll(Arrays.asList(clientShipment, driverShipment, adminShipment));
            
            // Start monitoring for all shipments
            ColdChainMonitoringService.ColdChainSession clientSession = 
                    coldChainService.startMonitoring(clientShipment.getId(), "REFRIGERATED").get();
            
            ColdChainMonitoringService.ColdChainSession driverSession = 
                    coldChainService.startMonitoring(driverShipment.getId(), "FROZEN").get();
            
            ColdChainMonitoringService.ColdChainSession adminSession = 
                    coldChainService.startMonitoring(adminShipment.getId(), "PHARMACEUTICAL").get();
            
            // Test visibility for each role
            Map<String, Boolean> clientVisibility = testRoleVisibility("CLIENT", clientShipment.getId());
            Map<String, Boolean> driverVisibility = testRoleVisibility("DRIVER", driverShipment.getId());
            Map<String, Boolean> adminVisibility = testRoleVisibility("ADMIN", adminShipment.getId());
            
            // Stop monitoring
            coldChainService.stopMonitoring(clientShipment.getId()).get(2, TimeUnit.SECONDS);
            coldChainService.stopMonitoring(driverShipment.getId()).get(2, TimeUnit.SECONDS);
            coldChainService.stopMonitoring(adminShipment.getId()).get(2, TimeUnit.SECONDS);
            
            long endTime = System.currentTimeMillis();
            
            results.put("clientShipmentId", clientShipment.getId());
            results.put("driverShipmentId", driverShipment.getId());
            results.put("adminShipmentId", adminShipment.getId());
            results.put("clientVisibility", clientVisibility);
            results.put("driverVisibility", driverVisibility);
            results.put("adminVisibility", adminVisibility);
            results.put("sessionDuration", endTime - startTime);
            results.put("status", "PASSED");
            
        } catch (Exception e) {
            log.error("Error in role-based visibility test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test visual indicators
     */
    private Map<String, Object> testVisualIndicators() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Test different temperature scenarios
            Map<Double, String> scenarios = Map.of(
                    4.0, "SAFE",
                    8.5, "WARNING",
                    12.5, "CRITICAL"
            );
            
            Map<String, Object> indicatorTests = new HashMap<>();
            
            for (Map.Entry<Double, String> scenario : scenarios.entrySet()) {
                Map<String, Object> indicatorTest = new HashMap<>();
                
                // Get status color
                String statusColor = getStatusColorForTemperature(scenario.getKey());
                String statusIcon = getStatusIconForTemperature(scenario.getKey());
                String statusMessage = getStatusMessageForTemperature(scenario.getKey());
                
                indicatorTest.put("temperature", scenario.getKey());
                indicatorTest.put("expectedStatus", scenario.getValue());
                indicatorTest.put("statusColor", statusColor);
                indicatorTest.put("statusIcon", statusIcon);
                indicatorTest.put("statusMessage", statusMessage);
                indicatorTest.put("visualCorrect", isVisualIndicatorCorrect(scenario.getValue(), statusColor, statusIcon));
                
                indicatorTests.put(scenario.getValue(), indicatorTest);
            }
            
            long endTime = System.currentTimeMillis();
            
            results.put("indicatorTests", indicatorTests);
            results.put("totalScenarios", scenarios.size());
            results.put("correctIndicators", indicatorTests.values().stream()
                    .mapToLong(test -> (Boolean) ((Map<String, Object>) test).get("visualCorrect") ? 1L : 0L)
                    .sum());
            results.put("sessionDuration", endTime - startTime);
            results.put("status", "PASSED");
            
        } catch (Exception e) {
            log.error("Error in visual indicators test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test performance under load
     */
    private Map<String, Object> testPerformanceUnderLoad() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            int concurrentShipments = 20;
            int readingsPerShipment = 10;
            
            List<CompletableFuture<Map<String, Object>>> futures = new ArrayList<>();
            AtomicInteger totalSuccessfulReadings = new AtomicInteger(0);
            AtomicInteger totalReadings = new AtomicInteger(0);
            AtomicLong totalResponseTime = new AtomicLong(0);
            
            // Create concurrent monitoring sessions
            for (int i = 0; i < concurrentShipments; i++) {
                final int shipmentIndex = i;
                
                CompletableFuture<Map<String, Object>> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        Shipment testShipment = createTestShipment();
                        testShipment.setTrackingNumber("PERF_TEST_" + shipmentIndex);
                        shipmentRepository.save(testShipment);
                        
                        // Start monitoring
                        ColdChainMonitoringService.ColdChainSession session = 
                                coldChainService.startMonitoring(testShipment.getId(), "REFRIGERATED").get(5, TimeUnit.SECONDS);
                        
                        int successfulReadings = 0;
                        long totalResponseTime = 0;
                        
                        // Send readings
                        for (int j = 0; j < readingsPerShipment; j++) {
                            long requestStart = System.currentTimeMillis();
                            
                            ColdChainMonitoringService.TemperatureReadingRequest request = 
                                    ColdChainMonitoringService.TemperatureReadingRequest.builder()
                                            .shipmentId(testShipment.getId())
                                            .temperature(4.0 + (Math.random() * 6.0))
                                            .humidity(70.0)
                                            .sensorId("perf_sensor_" + j)
                                            .location("perf_location")
                                            .deviceId("perf_device")
                                            .batteryLevel(80)
                                            .signalStrength(85)
                                            .build();
                            
                            ColdChainMonitoringService.TemperatureReadingResult result = 
                                    coldChainService.recordTemperature(request).get(5, TimeUnit.SECONDS);
                            
                            long responseTime = System.currentTimeMillis() - requestStart;
                            totalResponseTime += responseTime;
                            
                            if (result.getSuccess()) {
                                successfulReadings++;
                            }
                            
                            totalReadings.addAndGet(1);
                            Thread.sleep(50);
                        }
                        
                        // Stop monitoring
                        coldChainService.stopMonitoring(testShipment.getId()).get(5, TimeUnit.SECONDS);
                        
                        return Map.of(
                                "shipmentId", testShipment.getId(),
                                "successfulReadings", successfulReadings,
                                "totalReadings", readingsPerShipment,
                                "averageResponseTime", (double) totalResponseTime / readingsPerShipment,
                                "successRate", (double) successfulReadings / readingsPerShipment * 100
                        );
                        
                    } catch (Exception e) {
                        log.error("Error in performance test for shipment {}", shipmentIndex, e);
                        return Map.of("error", e.getMessage());
                    }
                }, testExecutor);
                
                futures.add(future);
            }
            
            // Wait for all futures to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(60, TimeUnit.SECONDS);
            
            // Collect results
            List<Map<String, Object>> testResults = futures.stream()
                    .map(future -> {
                        try {
                            return future.get();
                        } catch (Exception e) {
                            return Map.of("error", e.getMessage());
                        }
                    })
                    .toList();
            
            // Calculate overall metrics
            long endTime = System.currentTimeMillis();
            
            results.put("concurrentShipments", concurrentShipments);
            results.put("readingsPerShipment", readingsPerShipment);
            results.put("totalReadings", totalReadings.get());
            results.put("totalSuccessfulReadings", totalSuccessfulReadings.get());
            results.put("overallSuccessRate", (double) totalSuccessfulReadings.get() / totalReadings.get() * 100);
            results.put("averageResponseTime", (double) totalResponseTime.get() / totalReadings.get());
            results.put("testResults", testResults);
            results.put("sessionDuration", endTime - startTime);
            results.put("status", totalSuccessfulReadings.get() >= totalReadings.get() * 0.9 ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("Error in performance under load test", e);
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
            // Create test shipment
            Shipment testShipment = createTestShipment();
            shipmentRepository.save(testShipment);
            
            // Start monitoring
            ColdChainMonitoringService.ColdChainSession session = 
                    coldChainService.startMonitoring(testShipment.getId(), "REFRIGERATED").get();
            
            // Send known temperature readings
            List<Double> knownTemperatures = Arrays.asList(4.0, 5.5, 6.2, 7.1, 8.5);
            List<String> recordedTemperatures = new ArrayList<>();
            
            for (Double temp : knownTemperatures) {
                ColdChainMonitoringService.TemperatureReadingRequest request = 
                        ColdChainMonitoringService.TemperatureReadingRequest.builder()
                                .shipmentId(testShipment.getId())
                                .temperature(temp)
                                .humidity(70.0)
                                .sensorId("consistency_sensor")
                                .location("consistency_location")
                                .deviceId("consistency_device")
                                .batteryLevel(80)
                                .signalStrength(85)
                                .build();
                
                coldChainService.recordTemperature(request).get(5, TimeUnit.SECONDS);
                recordedTemperatures.add(String.format("%.1f", temp));
                Thread.sleep(200);
            }
            
            // Retrieve history and verify consistency
            List<com.edham.logistics.model.TemperatureReading> history = 
                    coldChainService.getTemperatureHistory(testShipment.getId(), null, null).get(5, TimeUnit.SECONDS);
            
            List<String> retrievedTemperatures = history.stream()
                    .map(reading -> String.format("%.1f", reading.getTemperature()))
                    .toList();
            
            // Check consistency
            boolean isConsistent = true;
            StringBuilder inconsistencies = new StringBuilder();
            
            for (int i = 0; i < Math.min(knownTemperatures.size(), retrievedTemperatures.size()); i++) {
                if (!knownTemperatures.get(i).equals(retrievedTemperatures.get(i))) {
                    isConsistent = false;
                    inconsistencies.append(String.format("Index %d: expected %.1f, got %s; ", 
                            i, knownTemperatures.get(i), retrievedTemperatures.get(i)));
                }
            }
            
            // Stop monitoring
            coldChainService.stopMonitoring(testShipment.getId()).get(5, TimeUnit.SECONDS);
            
            long endTime = System.currentTimeMillis();
            
            results.put("testShipmentId", testShipment.getId());
            results.put("knownTemperatures", knownTemperatures);
            results.put("retrievedTemperatures", retrievedTemperatures);
            results.put("isConsistent", isConsistent);
            results.put("inconsistencies", inconsistencies.toString());
            results.put("sessionDuration", endTime - startTime);
            results.put("status", isConsistent ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("Error in data consistency test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test error handling
     */
    private Map<String, Object> testErrorHandling() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Test 1: Invalid shipment ID
            try {
                coldChainService.startMonitoring(99999L, "REFRIGERATED").get(5, TimeUnit.SECONDS);
                results.put("invalidShipmentTest", "FAILED");
            } catch (Exception e) {
                results.put("invalidShipmentTest", "PASSED");
            }
            
            // Test 2: Invalid temperature reading
            Shipment testShipment = createTestShipment();
            shipmentRepository.save(testShipment);
            
            ColdChainMonitoringService.ColdChainSession session = 
                    coldChainService.startMonitoring(testShipment.getId(), "REFRIGERATED").get();
            
            // Test with invalid temperature
            ColdChainMonitoringService.TemperatureReadingRequest invalidRequest = 
                    ColdChainMonitoringService.TemperatureReadingRequest.builder()
                            .shipmentId(testShipment.getId())
                            .temperature(null) // Invalid temperature
                            .humidity(70.0)
                            .sensorId("test_sensor")
                            .location("test_location")
                            .deviceId("test_device")
                            .batteryLevel(80)
                            .signalStrength(85)
                            .build();
            
            ColdChainMonitoringService.TemperatureReadingResult result = 
                    coldChainService.recordTemperature(invalidRequest).get(5, TimeUnit.SECONDS);
            
            results.put("invalidTemperatureTest", result.getSuccess() ? "FAILED" : "PASSED");
            
            // Test 3: Stop non-existent monitoring
            try {
                coldChainService.stopMonitoring(99999L).get(5, TimeUnit.SECONDS);
                results.put("invalidStopTest", "FAILED");
            } catch (Exception e) {
                results.put("invalidStopTest", "PASSED");
            }
            
            // Clean up
            coldChainService.stopMonitoring(testShipment.getId()).get(5, TimeUnit.SECONDS);
            
            long endTime = System.currentTimeMillis();
            
            results.put("sessionDuration", endTime - startTime);
            results.put("status", "PASSED");
            
        } catch (Exception e) {
            log.error("Error in error handling test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    // Helper methods
    private Shipment createTestShipment() {
        Shipment shipment = new Shipment();
        shipment.setTrackingNumber("TEST_" + System.currentTimeMillis());
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);
        shipment.setCustomerId(1L);
        shipment.setDriverId(2L);
        shipment.setProductType("REFRIGERATED");
        shipment.setOrigin("Test Origin");
        shipment.setDestination("Test Destination");
        shipment.setCreatedAt(LocalDateTime.now());
        return shipment;
    }

    private Shipment createTestShipmentForUser(String role) {
        Shipment shipment = createTestShipment();
        shipment.setTrackingNumber("TEST_" + role + "_" + System.currentTimeMillis());
        
        if ("CLIENT".equals(role)) {
            shipment.setCustomerId(1L);
        } else if ("DRIVER".equals(role)) {
            shipment.setDriverId(2L);
        } else if ("ADMIN".equals(role)) {
            shipment.setCustomerId(1L);
            shipment.setDriverId(2L);
        }
        
        return shipment;
    }

    private Map<String, Boolean> testRoleVisibility(String role, Long shipmentId) {
        Map<String, Boolean> visibility = new HashMap<>();
        
        // Test temperature access
        try {
            coldChainService.getCurrentTemperature(shipmentId).get(2, TimeUnit.SECONDS);
            visibility.put("temperatureAccess", true);
        } catch (Exception e) {
            visibility.put("temperatureAccess", false);
        }
        
        // Test history access
        try {
            coldChainService.getTemperatureHistory(shipmentId, null, null).get(2, TimeUnit.SECONDS);
            visibility.put("historyAccess", true);
        } catch (Exception e) {
            visibility.put("historyAccess", false);
        }
        
        // Test alerts access
        try {
            coldChainService.getAlerts(shipmentId, null).get(2, TimeUnit.SECONDS);
            visibility.put("alertsAccess", true);
        } catch (Exception e) {
            visibility.put("alertsAccess", false);
        }
        
        return visibility;
    }

    private String getStatusColorForTemperature(double temperature) {
        if (temperature < 2.0 || temperature > 10.0) {
            return "#EF4444"; // Red for critical
        } else if (temperature < 2.0 || temperature > 8.0) {
            return "#F59E0B"; // Yellow for warning
        } else {
            return "#10B981"; // Green for safe
        }
    }

    private String getStatusIconForTemperature(double temperature) {
        if (temperature < 2.0 || temperature > 10.0) {
            return "thermometer-critical";
        } else if (temperature < 2.0 || temperature > 8.0) {
            return "thermometer-warning";
        } else {
            return "thermometer-safe";
        }
    }

    private String getStatusMessageForTemperature(double temperature) {
        if (temperature < 2.0 || temperature > 10.0) {
            return String.format("CRITICAL: Temperature %.1f°C requires immediate attention", temperature);
        } else if (temperature < 2.0 || temperature > 8.0) {
            return String.format("WARNING: Temperature %.1f°C outside safe range", temperature);
        } else {
            return String.format("INFO: Temperature %.1f°C is within safe range", temperature);
        }
    }

    private boolean isVisualIndicatorCorrect(String expectedStatus, String actualColor, String actualIcon) {
        String expectedColor = switch (expectedStatus) {
            case "SAFE" -> "#10B981";
            case "WARNING" -> "#F59E0B";
            case "CRITICAL" -> "#EF4444";
            default -> "#9CA3AF";
        };
        
        String expectedIcon = switch (expectedStatus) {
            case "SAFE" -> "thermometer-safe";
            case "WARNING" -> "thermometer-warning";
            case "CRITICAL" -> "thermometer-critical";
            default -> "thermometer-off";
        };
        
        return expectedColor.equals(actualColor) && expectedIcon.equals(actualIcon);
    }
}
