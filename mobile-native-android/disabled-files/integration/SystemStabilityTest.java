package com.edham.logistics.integration;

import com.edham.logistics.dto.UnifiedResponseDTO;
import com.edham.logistics.model.*;
import com.edham.logistics.repository.*;
import com.edham.logistics.service.*;
import com.edham.logistics.intelligence.SmartInsightsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * System Stability Test Suite
 * Tests system stability under various conditions and ensures no crashes with invalid inputs
 */
@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
public class SystemStabilityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShipmentRepository shipmentRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private VehicleRepository vehicleRepository;

    @MockBean
    private EmergencyRepository emergencyRepository;

    @MockBean
    private InvoiceRepository invoiceRepository;

    @Autowired
    private SmartInsightsService smartInsightsService;

    /**
     * Test API stability with invalid inputs
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testApiStability_InvalidInputs() throws Exception {
        // Test with invalid shipment ID
        mockMvc.perform(post("/api/v1/intelligence/predict-delay/invalid")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Test with negative shipment ID
        mockMvc.perform(post("/api/v1/intelligence/predict-delay/-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Test with null shipment ID
        mockMvc.perform(post("/api/v1/intelligence/predict-delay/null")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Test with extremely large shipment ID
        mockMvc.perform(post("/api/v1/intelligence/predict-delay/999999999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Test system stability under concurrent load
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testConcurrentLoad_Stability() throws Exception {
        int concurrentRequests = 50;
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        ExecutorService executor = Executors.newFixedThreadPool(10);

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < concurrentRequests; i++) {
            final int requestId = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    mockMvc.perform(get("/api/v1/intelligence/inefficient-routes")
                            .contentType(MediaType.APPLICATION_JSON))
                            .andExpect(status().isOk());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    System.err.println("Request " + requestId + " failed: " + e.getMessage());
                }
            }, executor);
            futures.add(future);
        }

        // Wait for all requests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get(60, TimeUnit.SECONDS);

        executor.shutdown();

        // Verify system stability
        assertTrue(successCount.get() > concurrentRequests * 0.8, 
                "At least 80% of requests should succeed");
        System.out.println("Concurrent load test: " + successCount.get() + " successful, " + 
                          errorCount.get() + " failed");
    }

    /**
     * Test memory stability with large datasets
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testMemoryStability_LargeDatasets() throws Exception {
        // Mock large dataset
        List<Shipment> largeShipmentList = createLargeShipmentList(1000);
        when(shipmentRepository.findByStatusAndCreatedAtAfter(any(), any()))
                .thenReturn(largeShipmentList);
        when(shipmentRepository.findByOriginAndDestination(anyString(), anyString()))
                .thenReturn(largeShipmentList);

        // Test system doesn't crash with large dataset
        mockMvc.perform(get("/api/v1/intelligence/inefficient-routes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Verify memory usage is reasonable
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        double memoryUsagePercent = (double) usedMemory / maxMemory * 100;

        assertTrue(memoryUsagePercent < 80.0, 
                "Memory usage should be less than 80% of max memory");
        System.out.println("Memory usage: " + String.format("%.2f", memoryUsagePercent) + "%");
    }

    /**
     * Test error handling coverage
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testErrorHandling_Coverage() throws Exception {
        // Test database connection failure
        when(shipmentRepository.findByStatusAndCreatedAtAfter(any(), any()))
                .thenThrow(new RuntimeException("Database connection failed"));

        mockMvc.perform(get("/api/v1/intelligence/inefficient-routes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());

        // Test repository null pointer
        when(shipmentRepository.findByStatusAndCreatedAtAfter(any(), any()))
                .thenThrow(new NullPointerException("Repository is null"));

        mockMvc.perform(get("/api/v1/intelligence/inefficient-routes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));

        // Test invalid data format
        when(shipmentRepository.findByStatusAndCreatedAtAfter(any(), any()))
                .thenReturn(createInvalidShipmentData());

        mockMvc.perform(get("/api/v1/intelligence/inefficient-routes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Should handle gracefully
                .andExpect(jsonPath("$.success").value(true));
    }

    /**
     * Test timeout handling
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testTimeoutHandling() throws Exception {
        // Simulate slow database response
        when(shipmentRepository.findByStatusAndCreatedAtAfter(any(), any()))
                .thenAnswer(invocation -> {
                    Thread.sleep(10000); // 10 second delay
                    return new ArrayList<>();
                });

        // Test should timeout gracefully (actual timeout depends on configuration)
        long startTime = System.currentTimeMillis();
        
        try {
            mockMvc.perform(get("/api/v1/intelligence/inefficient-routes")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            // Expected to timeout
            long duration = System.currentTimeMillis() - startTime;
            assertTrue(duration < 30000, "Should timeout within 30 seconds");
            System.out.println("Request timed out after " + duration + "ms");
        }
    }

    /**
     * Test data validation edge cases
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDataValidation_EdgeCases() throws Exception {
        // Test with extreme values
        Shipment extremeShipment = createExtremeShipment();
        when(shipmentRepository.findById(anyLong()))
                .thenReturn(Optional.of(extremeShipment));

        mockMvc.perform(post("/api/v1/intelligence/predict-delay/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Test with null values
        Shipment nullShipment = createNullShipment();
        when(shipmentRepository.findById(anyLong()))
                .thenReturn(Optional.of(nullShipment));

        mockMvc.perform(post("/api/v1/intelligence/predict-delay/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Test with empty collections
        when(userRepository.findByRoleAndStatus(any(), any()))
                .thenReturn(new ArrayList<>());

        mockMvc.perform(post("/api/v1/intelligence/suggest-driver/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.candidates").isEmpty());
    }

    /**
     * Test security under stress
     */
    @Test
    void testSecurity_UnderStress() throws Exception {
        // Test unauthorized access
        mockMvc.perform(get("/api/v1/intelligence/inefficient-routes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        // Test with invalid roles
        mockMvc.perform(get("/api/v1/intelligence/inefficient-routes")
                .header("Authorization", "Bearer invalid-token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        // Test SQL injection attempts
        mockMvc.perform(post("/api/v1/intelligence/predict-delay/1'; DROP TABLE shipments; --")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        // Test XSS attempts
        mockMvc.perform(post("/api/v1/intelligence/predict-delay/1<script>alert('xss')</script>")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Test resource cleanup
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testResourceCleanup() throws Exception {
        // Monitor resource usage before and after operations
        Runtime runtime = Runtime.getRuntime();
        
        // Force garbage collection
        System.gc();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        int initialThreads = Thread.activeCount();

        // Perform multiple operations
        for (int i = 0; i < 100; i++) {
            mockMvc.perform(get("/api/v1/intelligence/inefficient-routes")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        // Force garbage collection again
        System.gc();
        Thread.sleep(1000); // Allow time for cleanup
        
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        int finalThreads = Thread.activeCount();

        // Verify no significant memory leaks
        long memoryIncrease = finalMemory - initialMemory;
        double memoryIncreasePercent = (double) memoryIncrease / initialMemory * 100;

        assertTrue(memoryIncreasePercent < 50.0, 
                "Memory increase should be less than 50%");
        assertTrue(finalThreads - initialThreads < 10, 
                "Thread count increase should be less than 10");

        System.out.println("Memory increase: " + String.format("%.2f", memoryIncreasePercent) + "%");
        System.out.println("Thread increase: " + (finalThreads - initialThreads));
    }

    /**
     * Test system recovery after failures
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testSystemRecovery() throws Exception {
        // Simulate intermittent failures
        AtomicInteger callCount = new AtomicInteger(0);
        when(shipmentRepository.findByStatusAndCreatedAtAfter(any(), any()))
                .thenAnswer(invocation -> {
                    int count = callCount.incrementAndGet();
                    if (count % 3 == 0) {
                        throw new RuntimeException("Simulated failure " + count);
                    }
                    return new ArrayList<>();
                });

        int totalRequests = 30;
        int successCount = 0;
        int failureCount = 0;

        for (int i = 0; i < totalRequests; i++) {
            try {
                mockMvc.perform(get("/api/v1/intelligence/inefficient-routes")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk());
                successCount++;
            } catch (Exception e) {
                failureCount++;
            }
        }

        // System should recover from failures
        assertTrue(successCount > totalRequests * 0.5, 
                "System should recover and succeed on at least 50% of requests");
        System.out.println("Recovery test: " + successCount + " successful, " + 
                          failureCount + " failed");
    }

    /**
     * Test configuration stability
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testConfigurationStability() throws Exception {
        // Test with missing configuration properties
        mockMvc.perform(get("/api/v1/intelligence/test")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Test with invalid configuration
        // This would require custom test configuration setup
        mockMvc.perform(get("/api/v1/intelligence/inefficient-routes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    /**
     * Test logging and monitoring stability
     */
    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testLoggingStability() throws Exception {
        // Test that logging doesn't cause performance issues
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            mockMvc.perform(get("/api/v1/intelligence/inefficient-routes")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        }

        long duration = System.currentTimeMillis() - startTime;
        double averageTime = (double) duration / 100;

        assertTrue(averageTime < 1000.0, 
                "Average request time should be less than 1 second with logging");
        System.out.println("Average request time with logging: " + 
                          String.format("%.2f", averageTime) + "ms");
    }

    // Helper methods
    private List<Shipment> createLargeShipmentList(int size) {
        List<Shipment> shipments = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Shipment shipment = new Shipment();
            shipment.setId((long) i);
            shipment.setTrackingNumber("LARGE" + i);
            shipment.setOrigin("Origin" + (i % 100));
            shipment.setDestination("Destination" + (i % 100));
            shipment.setStatus(ShipmentStatus.DELIVERED);
            shipment.setCreatedAt(LocalDateTime.now().minusDays(i % 365));
            shipments.add(shipment);
        }
        return shipments;
    }

    private List<Shipment> createInvalidShipmentData() {
        List<Shipment> shipments = new ArrayList<>();
        
        // Create shipments with null/invalid data
        Shipment invalidShipment = new Shipment();
        invalidShipment.setId(null);
        invalidShipment.setTrackingNumber(null);
        invalidShipment.setOrigin(null);
        invalidShipment.setDestination(null);
        invalidShipment.setStatus(null);
        shipments.add(invalidShipment);
        
        return shipments;
    }

    private Shipment createExtremeShipment() {
        Shipment shipment = new Shipment();
        shipment.setId(Long.MAX_VALUE);
        shipment.setTrackingNumber(String.join("", Collections.nCopies(1000, "X"))); // Very long string
        shipment.setOrigin(String.join("", Collections.nCopies(500, "O")));
        shipment.setDestination(String.join("", Collections.nCopies(500, "D")));
        shipment.setDistance(Double.MAX_VALUE);
        shipment.setShippingCost(new BigDecimal("999999999999.99"));
        shipment.setCreatedAt(LocalDateTime.now().minusYears(100));
        return shipment;
    }

    private Shipment createNullShipment() {
        Shipment shipment = new Shipment();
        shipment.setId(1L);
        // All other fields are null
        return shipment;
    }
}
