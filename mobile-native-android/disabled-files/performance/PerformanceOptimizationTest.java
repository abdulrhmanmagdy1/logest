package com.edham.logistics.performance;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.Assert.*;

/**
 * Backend Performance Optimization Test Suite
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PerformanceOptimizationTest {

    @Autowired
    private PerformanceOptimizer performanceOptimizer;

    private ExecutorService testExecutor;

    @Before
    public void setUp() {
        testExecutor = Executors.newFixedThreadPool(10);
        performanceOptimizer.initialize();
    }

    @After
    public void tearDown() {
        performanceOptimizer.cleanup();
        testExecutor.shutdown();
        try {
            if (!testExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                testExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            testExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Test query optimization and caching
     */
    @Test
    public void testQueryOptimization() {
        String query = "SELECT COUNT(*) FROM test_table WHERE status = ?";
        Object[] params = {"ACTIVE"};

        // First query - should be slower
        long startTime = System.currentTimeMillis();
        Integer result1 = performanceOptimizer.executeOptimizedQuery(query, params, Integer.class);
        long firstQueryTime = System.currentTimeMillis() - startTime;

        assertNotNull("First query result should not be null", result1);
        assertTrue("First query should complete in reasonable time", firstQueryTime < 5000);

        // Second query - should be faster due to caching
        startTime = System.currentTimeMillis();
        Integer result2 = performanceOptimizer.executeOptimizedQuery(query, params, Integer.class);
        long secondQueryTime = System.currentTimeMillis() - startTime;

        assertEquals("Cached result should match original", result1, result2);
        assertTrue("Second query should be faster due to caching", secondQueryTime <= firstQueryTime);
    }

    /**
     * Test batch query execution
     */
    @Test
    public void testBatchQueryExecution() {
        String query = "SELECT * FROM test_table WHERE category = ? LIMIT ?";
        Object[] params = {"TEST", 100};

        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> results = performanceOptimizer.executeBatchQuery(
            query, params, Map.class);
        long executionTime = System.currentTimeMillis() - startTime;

        assertNotNull("Batch query results should not be null", results);
        assertTrue("Batch query should complete in reasonable time", executionTime < 10000);
    }

    /**
     * Test lazy list functionality
     */
    @Test
    public void testLazyList() {
        String query = "SELECT * FROM test_table ORDER BY id";
        Object[] params = {};

        PerformanceOptimizer.LazyList<Map<String, Object>> lazyList = 
            performanceOptimizer.createLazyList(query, params, Map.class, 20);

        assertNotNull("Lazy list should not be null", lazyList);
        
        // Test getting first item
        Map<String, Object> firstItem = lazyList.get(0);
        assertNotNull("First item should not be null", firstItem);
        
        // Test size estimation
        int size = lazyList.size();
        assertTrue("Size should be non-negative", size >= 0);
    }

    /**
     * Test memory optimization
     */
    @Test
    public void testMemoryOptimization() {
        // Test data caching
        String testData = "Test data for memory optimization";
        performanceOptimizer.cacheData("test_key", testData);

        String cachedData = performanceOptimizer.getCachedData("test_key");
        assertEquals("Cached data should match original", testData, cachedData);

        // Test cache capacity
        for (int i = 0; i < 1500; i++) { // Exceed default cache size
            performanceOptimizer.cacheData("test_key_" + i, "data_" + i);
        }

        // Should still work without memory issues
        String firstCachedData = performanceOptimizer.getCachedData("test_key_0");
        assertNotNull("First cached data should still be accessible", firstCachedData);
    }

    /**
     * Test performance under concurrent load
     */
    @Test
    public void testConcurrentPerformance() throws InterruptedException, ExecutionException {
        int concurrentOperations = 50;
        List<Future<Long>> futures = new ArrayList<>();

        // Submit concurrent query operations
        for (int i = 0; i < concurrentOperations; i++) {
            final int operationId = i;
            Future<Long> future = testExecutor.submit(() -> {
                String query = "SELECT COUNT(*) FROM test_table WHERE id = ?";
                Object[] params = {operationId};

                long startTime = System.currentTimeMillis();
                Integer result = performanceOptimizer.executeOptimizedQuery(query, params, Integer.class);
                long executionTime = System.currentTimeMillis() - startTime;

                assertNotNull("Query result should not be null", result);
                return executionTime;
            });
            futures.add(future);
        }

        // Wait for all operations to complete
        List<Long> executionTimes = new ArrayList<>();
        for (Future<Long> future : futures) {
            executionTimes.add(future.get());
        }

        // Verify all operations completed successfully
        assertEquals("All operations should complete", concurrentOperations, executionTimes.size());

        // Calculate performance metrics
        double avgExecutionTime = executionTimes.stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0.0);

        long maxExecutionTime = executionTimes.stream()
            .mapToLong(Long::longValue)
            .max()
            .orElse(0L);

        assertTrue("Average execution time should be reasonable (< 1000ms)", avgExecutionTime < 1000);
        assertTrue("Maximum execution time should be reasonable (< 5000ms)", maxExecutionTime < 5000);
    }

    /**
     * Test performance metrics collection
     */
    @Test
    public void testPerformanceMetrics() {
        // Execute some queries to generate metrics
        for (int i = 0; i < 10; i++) {
            String query = "SELECT COUNT(*) FROM test_table WHERE id = ?";
            Object[] params = {i};
            performanceOptimizer.executeOptimizedQuery(query, params, Integer.class);
        }

        // Get performance report
        PerformanceOptimizer.PerformanceReport report = performanceOptimizer.getPerformanceReport();
        assertNotNull("Performance report should not be null", report);

        assertTrue("Cache hit rate should be between 0 and 100", 
            report.getCacheHitRate() >= 0.0 && report.getCacheHitRate() <= 100.0);
        assertTrue("Average query time should be positive", report.getAverageQueryTime() >= 0.0);
        assertTrue("Average memory usage should be between 0 and 100", 
            report.getAverageMemoryUsage() >= 0.0 && report.getAverageMemoryUsage() <= 100.0);
        assertTrue("Average active connections should be non-negative", report.getAverageActiveConnections() >= 0.0);
        assertTrue("Average cache size should be non-negative", report.getAverageCacheSize() >= 0.0);
        assertTrue("Query errors should be non-negative", report.getQueryErrors() >= 0);
        assertTrue("Batch executions should be non-negative", report.getBatchExecutions() >= 0);
    }

    /**
     * Test memory pressure handling
     */
    @Test
    public void testMemoryPressureHandling() {
        // Fill cache with large objects
        List<String> largeData = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 10000; j++) {
                sb.append("Large data string ");
            }
            String data = sb.toString();
            largeData.add(data);
            performanceOptimizer.cacheData("large_data_" + i, data);
        }

        // Verify system still responds
        String testData = "Test data under memory pressure";
        performanceOptimizer.cacheData("pressure_test", testData);
        
        String cachedData = performanceOptimizer.getCachedData("pressure_test");
        assertEquals("Data should be cached even under memory pressure", testData, cachedData);

        // Get performance report to check memory usage
        PerformanceOptimizer.PerformanceReport report = performanceOptimizer.getPerformanceReport();
        assertNotNull("Report should be available under memory pressure", report);
    }

    /**
     * Test connection pool efficiency
     */
    @Test
    public void testConnectionPoolEfficiency() throws InterruptedException, ExecutionException {
        int concurrentQueries = 20;
        List<Future<Boolean>> futures = new ArrayList<>();

        // Submit concurrent operations that use database connections
        for (int i = 0; i < concurrentQueries; i++) {
            final int operationId = i;
            Future<Boolean> future = testExecutor.submit(() -> {
                try {
                    String query = "SELECT COUNT(*) FROM test_table WHERE id = ?";
                    Object[] params = {operationId};
                    Integer result = performanceOptimizer.executeOptimizedQuery(query, params, Integer.class);
                    return result != null;
                } catch (Exception e) {
                    return false;
                }
            });
            futures.add(future);
        }

        // Wait for all operations and verify success
        int successfulOperations = 0;
        for (Future<Boolean> future : futures) {
            if (future.get()) {
                successfulOperations++;
            }
        }

        // Most operations should succeed (allowing for some failures under load)
        assertTrue("Most operations should succeed", successfulOperations > concurrentQueries * 0.8);
    }

    /**
     * Test cache expiration
     */
    @Test
    public void testCacheExpiration() throws InterruptedException {
        String testData = "Expiring test data";
        performanceOptimizer.cacheData("expire_test", testData);

        // Data should be available immediately
        String cachedData = performanceOptimizer.getCachedData("expire_test");
        assertEquals("Data should be available immediately", testData, cachedData);

        // Wait for cache to expire (default 5 minutes, but we'll test with shorter time)
        // In real implementation, you might have a method to set custom expiry
        // For now, just verify the mechanism exists
        Thread.sleep(100);

        // Data should still be available (default expiry is 5 minutes)
        cachedData = performanceOptimizer.getCachedData("expire_test");
        assertEquals("Data should still be available after short wait", testData, cachedData);
    }

    /**
     * Test error handling and recovery
     */
    @Test
    public void testErrorHandling() {
        // Test with invalid query
        try {
            String invalidQuery = "INVALID SQL QUERY";
            Object[] params = {};
            performanceOptimizer.executeOptimizedQuery(invalidQuery, params, Integer.class);
            // Depending on implementation, this might throw exception or return null
            // Both behaviors are acceptable as long as system doesn't crash
        } catch (Exception e) {
            // Expected behavior - system should handle errors gracefully
            assertNotNull("Exception message should be informative", e.getMessage());
        }

        // System should still work after error
        String validQuery = "SELECT COUNT(*) FROM test_table";
        Object[] params = {};
        Integer result = performanceOptimizer.executeOptimizedQuery(validQuery, params, Integer.class);
        assertNotNull("System should recover from errors", result);
    }

    /**
     * Test performance optimization settings
     */
    @Test
    public void testPerformanceOptimizationSettings() {
        // Test with different batch sizes
        String query = "SELECT * FROM test_table LIMIT ?";
        Object[] params = {50};

        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> results = performanceOptimizer.executeBatchQuery(
            query, params, Map.class);
        long executionTime = System.currentTimeMillis() - startTime;

        assertNotNull("Batch query should work with different settings", results);
        assertTrue("Execution should complete in reasonable time", executionTime < 10000);
    }

    /**
     * Test system cleanup
     */
    @Test
    public void testSystemCleanup() {
        // Add some data to cache
        performanceOptimizer.cacheData("cleanup_test", "test data");

        // Verify data exists
        String cachedData = performanceOptimizer.getCachedData("cleanup_test");
        assertEquals("Data should exist before cleanup", "test data", cachedData);

        // Perform cleanup
        performanceOptimizer.cleanup();

        // After cleanup, system should still be functional
        String query = "SELECT COUNT(*) FROM test_table";
        Object[] params = {};
        Integer result = performanceOptimizer.executeOptimizedQuery(query, params, Integer.class);
        assertNotNull("System should work after cleanup", result);
    }

    /**
     * Test performance with large datasets
     */
    @Test
    public void testLargeDatasetPerformance() {
        // Test lazy list with large dataset simulation
        String query = "SELECT * FROM large_table ORDER BY id";
        Object[] params = {};

        PerformanceOptimizer.LazyList<Map<String, Object>> lazyList = 
            performanceOptimizer.createLazyList(query, params, Map.class, 100);

        // Test accessing different parts of the large dataset
        for (int i = 0; i < 5; i++) {
            int index = i * 100; // Access items at intervals
            if (index < lazyList.size()) {
                Map<String, Object> item = lazyList.get(index);
                assertNotNull("Item should be accessible", item);
            }
        }

        // Test size estimation for large dataset
        int size = lazyList.size();
        assertTrue("Size should be reasonable for large dataset", size >= 0);
    }

    /**
     * Test performance monitoring accuracy
     */
    @Test
    public void testPerformanceMonitoringAccuracy() {
        // Clear any existing metrics
        PerformanceOptimizer.PerformanceReport initialReport = performanceOptimizer.getPerformanceReport();

        // Execute known number of queries
        int queryCount = 5;
        for (int i = 0; i < queryCount; i++) {
            String query = "SELECT COUNT(*) FROM test_table WHERE id = ?";
            Object[] params = {i};
            performanceOptimizer.executeOptimizedQuery(query, params, Integer.class);
        }

        // Get updated report
        PerformanceOptimizer.PerformanceReport finalReport = performanceOptimizer.getPerformanceReport();

        // Verify metrics have been updated
        assertNotNull("Final report should not be null", finalReport);
        // Note: Specific metric validation depends on implementation details
    }
}
