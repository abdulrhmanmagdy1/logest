// // package com.edham.logistics.analytics;

import com.edham.logistics.dto.UnifiedResponseDTO;
import com.edham.logistics.model.*;
import com.edham.logistics.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Admin dashboard integration test
 * Comprehensive testing for admin dashboard analytics
 */
@RestController
@RequestMapping("/api/v1/admin/dashboard/test")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class AdminDashboardIntegrationTest {

    private final AdminDashboardService dashboardService;
    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final ColdChainMonitoringService coldChainService;
    private final ExecutorService testExecutor;

    @Autowired
    public AdminDashboardIntegrationTest(AdminDashboardService dashboardService,
                                      ShipmentRepository shipmentRepository,
                                      UserRepository userRepository,
                                      InvoiceRepository invoiceRepository,
                                      ColdChainMonitoringService coldChainService) {
        this.dashboardService = dashboardService;
        this.shipmentRepository = shipmentRepository;
        this.userRepository = userRepository;
        this.invoiceRepository = invoiceRepository;
        this.coldChainService = coldChainService;
        this.testExecutor = Executors.newFixedThreadPool(5);
    }

    /**
     * Comprehensive admin dashboard test
     */
    @PostMapping("/comprehensive")
    @PreAuthorize("hasRole('ADMIN')")
    public CompletableFuture<ResponseEntity<UnifiedResponseDTO<Map<String, Object>>>> comprehensiveTest() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> testResults = new HashMap<>();
                long startTime = System.currentTimeMillis();
                
                // Test 1: Total shipments analytics
                Map<String, Object> totalShipmentsTest = testTotalShipmentsAnalytics();
                testResults.put("totalShipmentsAnalytics", totalShipmentsTest);
                
                // Test 2: Active drivers analytics
                Map<String, Object> activeDriversTest = testActiveDriversAnalytics();
                testResults.put("activeDriversAnalytics", activeDriversTest);
                
                // Test 3: Revenue charts analytics
                Map<String, Object> revenueChartsTest = testRevenueChartsAnalytics();
                testResults.put("revenueChartsAnalytics", revenueChartsTest);
                
                // Test 4: Delayed shipments analytics
                Map<String, Object> delayedShipmentsTest = testDelayedShipmentsAnalytics();
                testResults.put("delayedShipmentsAnalytics", delayedShipmentsTest);
                
                // Test 5: Temperature violations analytics
                Map<String, Object> temperatureViolationsTest = testTemperatureViolationsAnalytics();
                testResults.put("temperatureViolationsAnalytics", temperatureViolationsTest);
                
                // Test 6: Filters functionality
                Map<String, Object> filtersTest = testFiltersFunctionality();
                testResults.put("filtersFunctionality", filtersTest);
                
                // Test 7: Performance metrics
                Map<String, Object> performanceMetricsTest = testPerformanceMetrics();
                testResults.put("performanceMetricsTest", performanceMetricsTest);
                
                // Test 8: Data consistency
                Map<String, Object> dataConsistencyTest = testDataConsistency();
                testResults.put("dataConsistencyTest", dataConsistencyTest);
                
                // Test 9: Concurrent requests
                Map<String, Object> concurrentRequestsTest = testConcurrentRequests();
                testResults.put("concurrentRequestsTest", concurrentRequestsTest);
                
                // Test 10: Export functionality
                Map<String, Object> exportFunctionalityTest = testExportFunctionality();
                testResults.put("exportFunctionalityTest", exportFunctionalityTest);
                
                long endTime = System.currentTimeMillis();
                testResults.put("totalTestTime", endTime - startTime);
                testResults.put("testStatus", "COMPLETED");
                testResults.put("testTimestamp", LocalDateTime.now());
                
                log.info("Comprehensive admin dashboard test completed in {}ms", endTime - startTime);
                
                return ResponseEntity.ok(
                        UnifiedResponseDTO.<Map<String, Object>>builder()
                                .success(true)
                                .data(testResults)
                                .message("Admin dashboard test completed successfully")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
                
            } catch (Exception e) {
                log.error("Error in comprehensive admin dashboard test", e);
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
     * Test total shipments analytics
     */
    private Map<String, Object> testTotalShipmentsAnalytics() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Create test shipments
            List<Shipment> testShipments = createTestShipments(50);
            shipmentRepository.saveAll(testShipments);
            
            // Get shipments analytics
            AdminDashboardService.ShipmentsAnalytics analytics = 
                    dashboardService.getShipmentsAnalytics(LocalDateTime.now().minusDays(7), LocalDateTime.now());
            
            // Verify total shipments
            boolean totalShipmentsCorrect = analytics.getTotalShipments() >= testShipments.size();
            
            // Verify shipments by status
            boolean shipmentsByStatusValid = analytics.getShipmentsByStatus() != null && 
                    !analytics.getShipmentsByStatus().isEmpty();
            
            // Verify shipments by time
            boolean shipmentsByTimeValid = analytics.getShipmentsByTime() != null && 
                    !analytics.getShipmentsByTime().isEmpty();
            
            // Verify top routes
            boolean topRoutesValid = analytics.getTopRoutes() != null && 
                    !analytics.getTopRoutes().isEmpty();
            
            // Verify delivery performance
            boolean deliveryPerformanceValid = analytics.getDeliveryPerformance() != null;
            
            long endTime = System.currentTimeMillis();
            
            results.put("testShipmentsCreated", testShipments.size());
            results.put("totalShipmentsReturned", analytics.getTotalShipments());
            results.put("totalShipmentsCorrect", totalShipmentsCorrect);
            results.put("shipmentsByStatusValid", shipmentsByStatusValid);
            results.put("shipmentsByTimeValid", shipmentsByTimeValid);
            results.put("topRoutesValid", topRoutesValid);
            results.put("deliveryPerformanceValid", deliveryPerformanceValid);
            results.put("testDuration", endTime - startTime);
            results.put("status", totalShipmentsCorrect && shipmentsByStatusValid && 
                    shipmentsByTimeValid && topRoutesValid && deliveryPerformanceValid ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("Error in total shipments analytics test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test active drivers analytics
     */
    private Map<String, Object> testActiveDriversAnalytics() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Create test drivers
            List<User> testDrivers = createTestDrivers(20);
            userRepository.saveAll(testDrivers);
            
            // Get drivers analytics
            AdminDashboardService.DriversAnalytics analytics = 
                    dashboardService.getDriversAnalytics(LocalDateTime.now().minusDays(7), LocalDateTime.now());
            
            // Verify total drivers
            boolean totalDriversCorrect = analytics.getTotalDrivers() >= testDrivers.size();
            
            // Verify active drivers
            boolean activeDriversValid = analytics.getActiveDrivers() >= 0;
            
            // Verify drivers by status
            boolean driversByStatusValid = analytics.getDriversByStatus() != null && 
                    !analytics.getDriversByStatus().isEmpty();
            
            // Verify driver performance
            boolean driverPerformanceValid = analytics.getDriverPerformance() != null && 
                    !analytics.getDriverPerformance().isEmpty();
            
            // Verify average deliveries per driver
            boolean averageDeliveriesValid = analytics.getAverageDeliveriesPerDriver() >= 0;
            
            long endTime = System.currentTimeMillis();
            
            results.put("testDriversCreated", testDrivers.size());
            results.put("totalDriversReturned", analytics.getTotalDrivers());
            results.put("totalDriversCorrect", totalDriversCorrect);
            results.put("activeDriversValid", activeDriversValid);
            results.put("driversByStatusValid", driversByStatusValid);
            results.put("driverPerformanceValid", driverPerformanceValid);
            results.put("averageDeliveriesValid", averageDeliveriesValid);
            results.put("testDuration", endTime - startTime);
            results.put("status", totalDriversCorrect && activeDriversValid && 
                    driversByStatusValid && driverPerformanceValid && averageDeliveriesValid ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("Error in active drivers analytics test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test revenue charts analytics
     */
    private Map<String, Object> testRevenueChartsAnalytics() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Create test invoices
            List<Invoice> testInvoices = createTestInvoices(30);
            invoiceRepository.saveAll(testInvoices);
            
            // Get revenue analytics
            AdminDashboardService.RevenueAnalytics analytics = 
                    dashboardService.getRevenueAnalytics(LocalDateTime.now().minusDays(7), LocalDateTime.now());
            
            // Verify total revenue
            boolean totalRevenueValid = analytics.getTotalRevenue() != null && 
                    analytics.getTotalRevenue().compareTo(BigDecimal.ZERO) >= 0;
            
            // Verify paid revenue
            boolean paidRevenueValid = analytics.getPaidRevenue() != null && 
                    analytics.getPaidRevenue().compareTo(BigDecimal.ZERO) >= 0;
            
            // Verify outstanding revenue
            boolean outstandingRevenueValid = analytics.getOutstandingRevenue() != null && 
                    analytics.getOutstandingRevenue().compareTo(BigDecimal.ZERO) >= 0;
            
            // Verify revenue by time
            boolean revenueByTimeValid = analytics.getRevenueByTime() != null && 
                    !analytics.getRevenueByTime().isEmpty();
            
            // Verify revenue by service
            boolean revenueByServiceValid = analytics.getRevenueByService() != null && 
                    !analytics.getRevenueByService().isEmpty();
            
            // Verify collection rate
            boolean collectionRateValid = analytics.getCollectionRate() >= 0 && 
                    analytics.getCollectionRate() <= 100;
            
            // Verify revenue growth
            boolean revenueGrowthValid = analytics.getRevenueGrowth() != null;
            
            long endTime = System.currentTimeMillis();
            
            results.put("testInvoicesCreated", testInvoices.size());
            results.put("totalRevenueValid", totalRevenueValid);
            results.put("paidRevenueValid", paidRevenueValid);
            results.put("outstandingRevenueValid", outstandingRevenueValid);
            results.put("revenueByTimeValid", revenueByTimeValid);
            results.put("revenueByServiceValid", revenueByServiceValid);
            results.put("collectionRateValid", collectionRateValid);
            results.put("revenueGrowthValid", revenueGrowthValid);
            results.put("testDuration", endTime - startTime);
            results.put("status", totalRevenueValid && paidRevenueValid && 
                    outstandingRevenueValid && revenueByTimeValid && revenueByServiceValid && 
                    collectionRateValid && revenueGrowthValid ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("Error in revenue charts analytics test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test delayed shipments analytics
     */
    private Map<String, Object> testDelayedShipmentsAnalytics() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Create test delayed shipments
            List<Shipment> delayedShipments = createTestDelayedShipments(15);
            shipmentRepository.saveAll(delayedShipments);
            
            // Get delayed shipments analytics
            AdminDashboardService.DelayedShipmentsAnalytics analytics = 
                    dashboardService.getDelayedShipmentsAnalytics(LocalDateTime.now().minusDays(7), LocalDateTime.now());
            
            // Verify total delayed shipments
            boolean totalDelayedValid = analytics.getTotalDelayedShipments() >= delayedShipments.size();
            
            // Verify delayed by reason
            boolean delayedByReasonValid = analytics.getDelayedByReason() != null && 
                    !analytics.getDelayedByReason().isEmpty();
            
            // Verify average delay hours
            boolean averageDelayValid = analytics.getAverageDelayHours() >= 0;
            
            // Verify delayed by route
            boolean delayedByRouteValid = analytics.getDelayedByRoute() != null && 
                    !analytics.getDelayedByRoute().isEmpty();
            
            // Verify delay trend
            boolean delayTrendValid = analytics.getDelayTrend() != null && 
                    !analytics.getDelayTrend().isEmpty();
            
            // Verify on-time delivery rate
            boolean onTimeDeliveryRateValid = analytics.getOnTimeDeliveryRate() >= 0 && 
                    analytics.getOnTimeDeliveryRate() <= 100;
            
            long endTime = System.currentTimeMillis();
            
            results.put("testDelayedShipmentsCreated", delayedShipments.size());
            results.put("totalDelayedValid", totalDelayedValid);
            results.put("delayedByReasonValid", delayedByReasonValid);
            results.put("averageDelayValid", averageDelayValid);
            results.put("delayedByRouteValid", delayedByRouteValid);
            results.put("delayTrendValid", delayTrendValid);
            results.put("onTimeDeliveryRateValid", onTimeDeliveryRateValid);
            results.put("testDuration", endTime - startTime);
            results.put("status", totalDelayedValid && delayedByReasonValid && 
                    averageDelayValid && delayedByRouteValid && delayTrendValid && 
                    onTimeDeliveryRateValid ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("Error in delayed shipments analytics test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test temperature violations analytics
     */
    private Map<String, Object> testTemperatureViolationsAnalytics() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Create test temperature alerts
            List<ColdChainMonitoringService.ColdChainAlert> testAlerts = createTestTemperatureAlerts(25);
            
            // Get temperature violations analytics
            AdminDashboardService.TemperatureViolationsAnalytics analytics = 
                    dashboardService.getTemperatureViolationsAnalytics(LocalDateTime.now().minusDays(7), LocalDateTime.now());
            
            // Verify total violations
            boolean totalViolationsValid = analytics.getTotalViolations() >= testAlerts.size();
            
            // Verify violations by severity
            boolean violationsBySeverityValid = analytics.getViolationsBySeverity() != null && 
                    !analytics.getViolationsBySeverity().isEmpty();
            
            // Verify violations by type
            boolean violationsByTypeValid = analytics.getViolationsByType() != null && 
                    !analytics.getViolationsByType().isEmpty();
            
            // Verify violations by product type
            boolean violationsByProductTypeValid = analytics.getViolationsByProductType() != null && 
                    !analytics.getViolationsByProductType().isEmpty();
            
            // Verify violations by time
            boolean violationsByTimeValid = analytics.getViolationsByTime() != null && 
                    !analytics.getViolationsByTime().isEmpty();
            
            // Verify top problematic shipments
            boolean topProblematicShipmentsValid = analytics.getTopProblematicShipments() != null && 
                    !analytics.getTopProblematicShipments().isEmpty();
            
            // Verify resolution rate
            boolean resolutionRateValid = analytics.getResolutionRate() >= 0 && 
                    analytics.getResolutionRate() <= 100;
            
            long endTime = System.currentTimeMillis();
            
            results.put("testAlertsCreated", testAlerts.size());
            results.put("totalViolationsValid", totalViolationsValid);
            results.put("violationsBySeverityValid", violationsBySeverityValid);
            results.put("violationsByTypeValid", violationsByTypeValid);
            results.put("violationsByProductTypeValid", violationsByProductTypeValid);
            results.put("violationsByTimeValid", violationsByTimeValid);
            results.put("topProblematicShipmentsValid", topProblematicShipmentsValid);
            results.put("resolutionRateValid", resolutionRateValid);
            results.put("testDuration", endTime - startTime);
            results.put("status", totalViolationsValid && violationsBySeverityValid && 
                    violationsByTypeValid && violationsByProductTypeValid && violationsByTimeValid && 
                    topProblematicShipmentsValid && resolutionRateValid ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("Error in temperature violations analytics test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test filters functionality
     */
    private Map<String, Object> testFiltersFunctionality() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Test daily filter
            AdminDashboardService.DashboardAnalytics dailyAnalytics = 
                    dashboardService.getDashboardAnalytics(AdminDashboardService.DateFilter.DAILY, null, null);
            boolean dailyFilterValid = dailyAnalytics != null;
            
            // Test weekly filter
            AdminDashboardService.DashboardAnalytics weeklyAnalytics = 
                    dashboardService.getDashboardAnalytics(AdminDashboardService.DateFilter.WEEKLY, null, null);
            boolean weeklyFilterValid = weeklyAnalytics != null;
            
            // Test monthly filter
            AdminDashboardService.DashboardAnalytics monthlyAnalytics = 
                    dashboardService.getDashboardAnalytics(AdminDashboardService.DateFilter.MONTHLY, null, null);
            boolean monthlyFilterValid = monthlyAnalytics != null;
            
            // Test custom filter
            LocalDateTime customStart = LocalDateTime.now().minusDays(15);
            LocalDateTime customEnd = LocalDateTime.now();
            AdminDashboardService.DashboardAnalytics customAnalytics = 
                    dashboardService.getDashboardAnalytics(AdminDashboardService.DateFilter.CUSTOM, customStart, customEnd);
            boolean customFilterValid = customAnalytics != null;
            
            // Verify date ranges
            boolean dateRangesValid = true;
            if (dailyFilterValid) {
                long dailyDuration = java.time.Duration.between(
                        dailyAnalytics.getStartDate(), dailyAnalytics.getEndDate()).toDays();
                dateRangesValid = dateRangesValid && dailyDuration <= 2; // Allow 1-2 days
            }
            
            long endTime = System.currentTimeMillis();
            
            results.put("dailyFilterValid", dailyFilterValid);
            results.put("weeklyFilterValid", weeklyFilterValid);
            results.put("monthlyFilterValid", monthlyFilterValid);
            results.put("customFilterValid", customFilterValid);
            results.put("dateRangesValid", dateRangesValid);
            results.put("testDuration", endTime - startTime);
            results.put("status", dailyFilterValid && weeklyFilterValid && 
                    monthlyFilterValid && customFilterValid && dateRangesValid ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("Error in filters functionality test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test performance metrics
     */
    private Map<String, Object> testPerformanceMetrics() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Get performance metrics
            AdminDashboardService.PerformanceMetrics metrics = 
                    dashboardService.getPerformanceMetrics(LocalDateTime.now().minusDays(7), LocalDateTime.now());
            
            // Verify system uptime
            boolean systemUptimeValid = metrics.getSystemUptime() >= 0 && metrics.getSystemUptime() <= 100;
            
            // Verify average response time
            boolean averageResponseTimeValid = metrics.getAverageResponseTime() > 0;
            
            // Verify error rate
            boolean errorRateValid = metrics.getErrorRate() >= 0 && metrics.getErrorRate() <= 100;
            
            // Verify database query time
            boolean databaseQueryTimeValid = metrics.getDatabaseQueryTime() > 0;
            
            // Verify cache hit rate
            boolean cacheHitRateValid = metrics.getCacheHitRate() >= 0 && metrics.getCacheHitRate() <= 100;
            
            // Verify active users
            boolean activeUsersValid = metrics.getActiveUsers() >= 0;
            
            // Verify total API calls
            boolean totalApiCallsValid = metrics.getTotalApiCalls() >= 0;
            
            long endTime = System.currentTimeMillis();
            
            results.put("systemUptimeValid", systemUptimeValid);
            results.put("averageResponseTimeValid", averageResponseTimeValid);
            results.put("errorRateValid", errorRateValid);
            results.put("databaseQueryTimeValid", databaseQueryTimeValid);
            results.put("cacheHitRateValid", cacheHitRateValid);
            results.put("activeUsersValid", activeUsersValid);
            results.put("totalApiCallsValid", totalApiCallsValid);
            results.put("testDuration", endTime - startTime);
            results.put("status", systemUptimeValid && averageResponseTimeValid && 
                    errorRateValid && databaseQueryTimeValid && cacheHitRateValid && 
                    activeUsersValid && totalApiCallsValid ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("Error in performance metrics test", e);
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
            // Get dashboard analytics
            AdminDashboardService.DashboardAnalytics analytics = 
                    dashboardService.getDashboardAnalytics(AdminDashboardService.DateFilter.WEEKLY, null, null);
            
            // Verify data consistency across modules
            boolean dataConsistent = true;
            
            // Check shipments consistency
            if (analytics.getShipmentsAnalytics() != null) {
                long totalShipments = analytics.getShipmentsAnalytics().getTotalShipments();
                long shipmentsByStatusSum = analytics.getShipmentsAnalytics().getShipmentsByStatus()
                        .values().stream().mapToLong(Long::longValue).sum();
                dataConsistent = dataConsistent && (totalShipments >= shipmentsByStatusSum);
            }
            
            // Check drivers consistency
            if (analytics.getDriversAnalytics() != null) {
                long totalDrivers = analytics.getDriversAnalytics().getTotalDrivers();
                long driversByStatusSum = analytics.getDriversAnalytics().getDriversByStatus()
                        .values().stream().mapToLong(Long::longValue).sum();
                dataConsistent = dataConsistent && (totalDrivers == driversByStatusSum);
            }
            
            // Check revenue consistency
            if (analytics.getRevenueAnalytics() != null) {
                BigDecimal totalRevenue = analytics.getRevenueAnalytics().getTotalRevenue();
                BigDecimal paidRevenue = analytics.getRevenueAnalytics().getPaidRevenue();
                BigDecimal outstandingRevenue = analytics.getRevenueAnalytics().getOutstandingRevenue();
                BigDecimal calculatedTotal = paidRevenue.add(outstandingRevenue);
                dataConsistent = dataConsistent && (totalRevenue.compareTo(calculatedTotal) == 0);
            }
            
            long endTime = System.currentTimeMillis();
            
            results.put("dataConsistent", dataConsistent);
            results.put("testDuration", endTime - startTime);
            results.put("status", dataConsistent ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("Error in data consistency test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test concurrent requests
     */
    private Map<String, Object> testConcurrentRequests() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            int concurrentRequests = 10;
            AtomicInteger successfulRequests = new AtomicInteger(0);
            AtomicInteger failedRequests = new AtomicInteger(0);
            AtomicLong totalResponseTime = new AtomicLong(0);
            
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            
            for (int i = 0; i < concurrentRequests; i++) {
                final int requestIndex = i;
                
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        long requestStart = System.currentTimeMillis();
                        
                        AdminDashboardService.DashboardAnalytics analytics = 
                                dashboardService.getDashboardAnalytics(AdminDashboardService.DateFilter.WEEKLY, null, null);
                        
                        long requestTime = System.currentTimeMillis() - requestStart;
                        totalResponseTime.addAndGet(requestTime);
                        
                        if (analytics != null) {
                            successfulRequests.incrementAndGet();
                        } else {
                            failedRequests.incrementAndGet();
                        }
                        
                    } catch (Exception e) {
                        failedRequests.incrementAndGet();
                        log.warn("Concurrent request {} failed: {}", requestIndex, e.getMessage());
                    }
                }, testExecutor);
                
                futures.add(future);
            }
            
            // Wait for all requests to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(60, TimeUnit.SECONDS);
            
            double successRate = (double) successfulRequests.get() / concurrentRequests * 100;
            double averageResponseTime = (double) totalResponseTime.get() / concurrentRequests;
            
            long endTime = System.currentTimeMillis();
            
            results.put("concurrentRequests", concurrentRequests);
            results.put("successfulRequests", successfulRequests.get());
            results.put("failedRequests", failedRequests.get());
            results.put("successRate", successRate);
            results.put("averageResponseTime", averageResponseTime);
            results.put("testDuration", endTime - startTime);
            results.put("status", successRate >= 95.0 ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("Error in concurrent requests test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    /**
     * Test export functionality
     */
    private Map<String, Object> testExportFunctionality() {
        Map<String, Object> results = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Test export with different filters
            boolean dailyExportValid = testExportWithFilter(AdminDashboardService.DateFilter.DAILY);
            boolean weeklyExportValid = testExportWithFilter(AdminDashboardService.DateFilter.WEEKLY);
            boolean monthlyExportValid = testExportWithFilter(AdminDashboardService.DateFilter.MONTHLY);
            boolean customExportValid = testExportWithFilter(AdminDashboardService.DateFilter.CUSTOM);
            
            // Test export data structure
            AdminDashboardService.DashboardAnalytics analytics = 
                    dashboardService.getDashboardAnalytics(AdminDashboardService.DateFilter.WEEKLY, null, null);
            boolean exportDataValid = analytics != null;
            
            long endTime = System.currentTimeMillis();
            
            results.put("dailyExportValid", dailyExportValid);
            results.put("weeklyExportValid", weeklyExportValid);
            results.put("monthlyExportValid", monthlyExportValid);
            results.put("customExportValid", customExportValid);
            results.put("exportDataValid", exportDataValid);
            results.put("testDuration", endTime - startTime);
            results.put("status", dailyExportValid && weeklyExportValid && 
                    monthlyExportValid && customExportValid && exportDataValid ? "PASSED" : "FAILED");
            
        } catch (Exception e) {
            log.error("Error in export functionality test", e);
            results.put("status", "FAILED");
            results.put("error", e.getMessage());
        }
        
        return results;
    }

    // Helper methods
    private List<Shipment> createTestShipments(int count) {
        List<Shipment> shipments = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Shipment shipment = new Shipment();
            shipment.setTrackingNumber("TEST_" + System.currentTimeMillis() + "_" + i);
            shipment.setStatus(ShipmentStatus.values()[i % ShipmentStatus.values().length]);
            shipment.setCustomerId((long) (i % 10 + 1));
            shipment.setDriverId((long) (i % 5 + 1));
            shipment.setOrigin("Test Origin " + i);
            shipment.setDestination("Test Destination " + i);
            shipment.setShippingCost(BigDecimal.valueOf(100 + i * 10));
            shipment.setCreatedAt(LocalDateTime.now().minusDays(i % 7));
            shipment.setUpdatedAt(LocalDateTime.now());
            shipments.add(shipment);
        }
        return shipments;
    }

    private List<User> createTestDrivers(int count) {
        List<User> drivers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            User driver = new User();
            driver.setName("Test Driver " + i);
            driver.setEmail("driver" + i + "@test.com");
            driver.setRole(UserRole.DRIVER);
            driver.setCreatedAt(LocalDateTime.now().minusDays(i % 30));
            driver.setUpdatedAt(LocalDateTime.now());
            drivers.add(driver);
        }
        return drivers;
    }

    private List<Invoice> createTestInvoices(int count) {
        List<Invoice> invoices = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Invoice invoice = new Invoice();
            invoice.setInvoiceNumber("INV_" + System.currentTimeMillis() + "_" + i);
            invoice.setCustomerId((long) (i % 10 + 1));
            invoice.setTotalAmount(BigDecimal.valueOf(200 + i * 50));
            invoice.setPaidAmount(BigDecimal.valueOf(i % 3 == 0 ? 0 : 150));
            invoice.setOutstandingBalance(BigDecimal.valueOf(i % 3 == 0 ? 200 : 50));
            invoice.setInvoiceDate(LocalDateTime.now().minusDays(i % 7));
            invoice.setCreatedAt(LocalDateTime.now());
            invoices.add(invoice);
        }
        return invoices;
    }

    private List<Shipment> createTestDelayedShipments(int count) {
        List<Shipment> shipments = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Shipment shipment = new Shipment();
            shipment.setTrackingNumber("DELAYED_" + System.currentTimeMillis() + "_" + i);
            shipment.setStatus(ShipmentStatus.DELIVERED);
            shipment.setCustomerId((long) (i % 10 + 1));
            shipment.setDriverId((long) (i % 5 + 1));
            shipment.setOrigin("Test Origin " + i);
            shipment.setDestination("Test Destination " + i);
            shipment.setExpectedDeliveryTime(LocalDateTime.now().minusDays(2));
            shipment.setActualDeliveryTime(LocalDateTime.now().minusDays(1));
            shipment.setDelayReason("Traffic Delay");
            shipment.setCreatedAt(LocalDateTime.now().minusDays(i % 7));
            shipment.setUpdatedAt(LocalDateTime.now());
            shipments.add(shipment);
        }
        return shipments;
    }

    private List<ColdChainMonitoringService.ColdChainAlert> createTestTemperatureAlerts(int count) {
        List<ColdChainMonitoringService.ColdChainAlert> alerts = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ColdChainMonitoringService.ColdChainAlert alert = new ColdChainMonitoringService.ColdChainAlert();
            alert.setShipmentId((long) (i % 20 + 1));
            alert.setAlertType(ColdChainMonitoringService.AlertType.values()[i % ColdChainMonitoringService.AlertType.values().length]);
            alert.setSeverity(ColdChainMonitoringService.AlertSeverity.values()[i % ColdChainMonitoringService.AlertSeverity.values().length]);
            alert.setProductType("Product Type " + (i % 5));
            alert.setTemperature(20.0 + i % 10);
            alert.setCreatedAt(LocalDateTime.now().minusHours(i % 24));
            alerts.add(alert);
        }
        return alerts;
    }

    private boolean testExportWithFilter(AdminDashboardService.DateFilter filter) {
        try {
            AdminDashboardService.DashboardAnalytics analytics = 
                    dashboardService.getDashboardAnalytics(filter, null, null);
            return analytics != null;
        } catch (Exception e) {
            log.warn("Export test failed for filter {}: {}", filter, e.getMessage());
            return false;
        }
    }
}
