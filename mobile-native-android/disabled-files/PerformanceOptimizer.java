package com.edham.logistics.performance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Backend Performance Optimizer for System-wide Performance Enhancement
 */
@Service
@Transactional
public class PerformanceOptimizer {

    @Autowired
    private DataSource dataSource;

    @Value("${app.performance.cache.size:1000}")
    private int cacheSize;

    @Value("${app.performance.query.timeout:30000}")
    private long queryTimeoutMs;

    @Value("${app.performance.batch.size:100}")
    private int batchSize;

    // Cache management
    private final Map<String, Object> queryCache = new ConcurrentHashMap<>();
    private final Map<String, Long> cacheTimestamps = new ConcurrentHashMap<>();
    private final Map<String, Object> resultCache = new ConcurrentHashMap<>();
    
    // Connection pool
    private final BlockingQueue<Connection> connectionPool = new LinkedBlockingQueue<>();
    private final Set<Connection> activeConnections = ConcurrentHashMap.newKeySet();
    private final int maxConnections = 20;
    
    // Performance monitoring
    private final PerformanceMetrics metrics = new PerformanceMetrics();
    private final ScheduledExecutorService monitoringExecutor = Executors.newScheduledThreadPool(2);
    private final ExecutorService queryExecutor = Executors.newFixedThreadPool(10);
    
    // Lazy loading
    private final LazyLoadManager lazyLoadManager = new LazyLoadManager();
    
    // Memory optimization
    private final Map<String, WeakReference<Object>> weakReferences = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleanupExecutor = Executors.newScheduledThreadPool(1);

    /**
     * Initialize performance optimizations
     */
    public void initialize() {
        // Initialize connection pool
        initializeConnectionPool();
        
        // Start performance monitoring
        startPerformanceMonitoring();
        
        // Start cache cleanup
        startCacheCleanup();
        
        // Initialize lazy loading
        lazyLoadManager.initialize();
    }

    /**
     * Optimized query execution with caching
     */
    public <T> T executeOptimizedQuery(String query, Object[] params, Class<T> resultType) {
        String cacheKey = generateCacheKey(query, params);
        
        // Check cache first
        @SuppressWarnings("unchecked")
        T cachedResult = (T) queryCache.get(cacheKey);
        if (cachedResult != null && !isCacheExpired(cacheKey)) {
            metrics.recordCacheHit();
            return cachedResult;
        }
        
        metrics.recordCacheMiss();
        
        // Execute query with connection pooling
        Connection connection = null;
        try {
            connection = getConnection();
            long startTime = System.currentTimeMillis();
            
            PreparedStatement statement = connection.prepareStatement(query);
            setStatementParameters(statement, params);
            
            ResultSet resultSet = statement.executeQuery();
            T result = mapResultSetToObject(resultSet, resultType);
            
            long executionTime = System.currentTimeMillis() - startTime;
            metrics.recordQueryExecution(executionTime);
            
            // Cache result
            cacheResult(cacheKey, result);
            
            return result;
        } catch (Exception e) {
            metrics.recordQueryError();
            throw new RuntimeException("Query execution failed", e);
        } finally {
            returnConnection(connection);
        }
    }

    /**
     * Batch query execution for large datasets
     */
    public <T> List<T> executeBatchQuery(String query, Object[] params, Class<T> resultType) {
        List<T> results = new ArrayList<>();
        Connection connection = null;
        
        try {
            connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(query);
            setStatementParameters(statement, params);
            
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                T result = mapResultSetToObject(resultSet, resultType);
                results.add(result);
            }
            
            metrics.recordBatchExecution(results.size());
            return results;
        } catch (Exception e) {
            metrics.recordQueryError();
            throw new RuntimeException("Batch query execution failed", e);
        } finally {
            returnConnection(connection);
        }
    }

    /**
     * Lazy loading for large datasets
     */
    public <T> LazyList<T> createLazyList(String query, Object[] params, Class<T> resultType, int pageSize) {
        return new LazyList<>(query, params, resultType, pageSize, this);
    }

    /**
     * Memory optimization
     */
    public void cacheData(String key, Object data) {
        if (queryCache.size() >= cacheSize) {
            evictOldestCacheEntries();
        }
        
        queryCache.put(key, data);
        cacheTimestamps.put(key, System.currentTimeMillis());
        weakReferences.put(key, new WeakReference<>(data));
    }

    @SuppressWarnings("unchecked")
    public <T> T getCachedData(String key) {
        Object cached = queryCache.get(key);
        if (cached != null) {
            return (T) cached;
        }
        
        WeakReference<Object> weakRef = weakReferences.get(key);
        if (weakRef != null) {
            return (T) weakRef.get();
        }
        
        return null;
    }

    /**
     * Performance metrics
     */
    public PerformanceReport getPerformanceReport() {
        return metrics.generateReport();
    }

    /**
     * Connection pool management
     */
    private void initializeConnectionPool() {
        try {
            for (int i = 0; i < maxConnections; i++) {
                Connection connection = dataSource.getConnection();
                connectionPool.offer(connection);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize connection pool", e);
        }
    }

    private Connection getConnection() throws InterruptedException {
        Connection connection = connectionPool.poll(5, TimeUnit.SECONDS);
        if (connection == null) {
            throw new RuntimeException("No available connections in pool");
        }
        
        activeConnections.add(connection);
        return connection;
    }

    private void returnConnection(Connection connection) {
        if (connection != null && activeConnections.contains(connection)) {
            activeConnections.remove(connection);
            connectionPool.offer(connection);
        }
    }

    /**
     * Cache management
     */
    private String generateCacheKey(String query, Object[] params) {
        StringBuilder keyBuilder = new StringBuilder(query);
        if (params != null) {
            for (Object param : params) {
                keyBuilder.append(":").append(param != null ? param.toString() : "null");
            }
        }
        return keyBuilder.toString();
    }

    private boolean isCacheExpired(String cacheKey) {
        Long timestamp = cacheTimestamps.get(cacheKey);
        return timestamp == null || (System.currentTimeMillis() - timestamp) > 300000; // 5 minutes
    }

    private void cacheResult(String cacheKey, Object result) {
        queryCache.put(cacheKey, result);
        cacheTimestamps.put(cacheKey, System.currentTimeMillis());
    }

    private void evictOldestCacheEntries() {
        // Remove oldest 10% of entries
        int entriesToRemove = cacheSize / 10;
        List<String> keysToRemove = new ArrayList<>();
        
        cacheTimestamps.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .limit(entriesToRemove)
            .forEach(entry -> keysToRemove.add(entry.getKey()));
        
        keysToRemove.forEach(key -> {
            queryCache.remove(key);
            cacheTimestamps.remove(key);
            weakReferences.remove(key);
        });
    }

    /**
     * Performance monitoring
     */
    private void startPerformanceMonitoring() {
        monitoringExecutor.scheduleAtFixedRate(() -> {
            try {
                collectSystemMetrics();
            } catch (Exception e) {
                metrics.recordMonitoringError();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private void startCacheCleanup() {
        cleanupExecutor.scheduleAtFixedRate(() -> {
            try {
                cleanupExpiredCache();
                cleanupWeakReferences();
            } catch (Exception e) {
                metrics.recordCleanupError();
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    private void collectSystemMetrics() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
        int memoryUsagePercent = (int) ((usedMemory * 100.0) / maxMemory);
        
        metrics.recordMemoryUsage(memoryUsagePercent);
        metrics.recordActiveConnections(activeConnections.size());
        metrics.recordCacheSize(queryCache.size());
    }

    private void cleanupExpiredCache() {
        long currentTime = System.currentTimeMillis();
        List<String> expiredKeys = new ArrayList<>();
        
        cacheTimestamps.entrySet().stream()
            .filter(entry -> currentTime - entry.getValue() > 300000) // 5 minutes
            .forEach(entry -> expiredKeys.add(entry.getKey()));
        
        expiredKeys.forEach(key -> {
            queryCache.remove(key);
            cacheTimestamps.remove(key);
            weakReferences.remove(key);
        });
    }

    private void cleanupWeakReferences() {
        weakReferences.entrySet().removeIf(entry -> entry.getValue().get() == null);
    }

    /**
     * Utility methods
     */
    private void setStatementParameters(PreparedStatement statement, Object[] params) throws Exception {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T mapResultSetToObject(ResultSet resultSet, Class<T> resultType) throws Exception {
        // Simplified mapping - in real implementation, use proper ORM or mapper
        if (resultType == String.class) {
            return (T) resultSet.getString(1);
        } else if (resultType == Integer.class) {
            return (T) Integer.valueOf(resultSet.getInt(1));
        } else if (resultType == Long.class) {
            return (T) Long.valueOf(resultSet.getLong(1));
        } else if (resultType == Double.class) {
            return (T) Double.valueOf(resultSet.getDouble(1));
        } else {
            // For complex objects, use reflection or mapping framework
            return null;
        }
    }

    /**
     * Performance metrics class
     */
    private static class PerformanceMetrics {
        private final AtomicLong cacheHits = new AtomicLong(0);
        private final AtomicLong cacheMisses = new AtomicLong(0);
        private final AtomicLong queryErrors = new AtomicLong(0);
        private final AtomicLong monitoringErrors = new AtomicLong(0);
        private final AtomicLong cleanupErrors = new AtomicLong(0);
        private final List<Long> queryExecutionTimes = new CopyOnWriteArrayList<>();
        private final List<Integer> memoryUsageHistory = new CopyOnWriteArrayList<>();
        private final List<Integer> activeConnectionsHistory = new CopyOnWriteArrayList<>();
        private final List<Integer> cacheSizeHistory = new CopyOnWriteArrayList<>();
        private final AtomicLong batchExecutions = new AtomicLong(0);

        public void recordCacheHit() {
            cacheHits.incrementAndGet();
        }

        public void recordCacheMiss() {
            cacheMisses.incrementAndGet();
        }

        public void recordQueryError() {
            queryErrors.incrementAndGet();
        }

        public void recordMonitoringError() {
            monitoringErrors.incrementAndGet();
        }

        public void recordCleanupError() {
            cleanupErrors.incrementAndGet();
        }

        public void recordQueryExecution(long executionTimeMs) {
            queryExecutionTimes.add(executionTimeMs);
            if (queryExecutionTimes.size() > 100) {
                queryExecutionTimes.remove(0);
            }
        }

        public void recordMemoryUsage(int usagePercent) {
            memoryUsageHistory.add(usagePercent);
            if (memoryUsageHistory.size() > 100) {
                memoryUsageHistory.remove(0);
            }
        }

        public void recordActiveConnections(int count) {
            activeConnectionsHistory.add(count);
            if (activeConnectionsHistory.size() > 100) {
                activeConnectionsHistory.remove(0);
            }
        }

        public void recordCacheSize(int size) {
            cacheSizeHistory.add(size);
            if (cacheSizeHistory.size() > 100) {
                cacheSizeHistory.remove(0);
            }
        }

        public void recordBatchExecution(int resultSize) {
            batchExecutions.incrementAndGet();
        }

        public PerformanceReport generateReport() {
            double cacheHitRate = cacheHits.get() + cacheMisses.get() > 0 ?
                (cacheHits.get() * 100.0) / (cacheHits.get() + cacheMisses.get()) : 0.0;

            double avgQueryTime = queryExecutionTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

            double avgMemoryUsage = memoryUsageHistory.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

            double avgActiveConnections = activeConnectionsHistory.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

            double avgCacheSize = cacheSizeHistory.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

            return new PerformanceReport(
                cacheHitRate,
                avgQueryTime,
                avgMemoryUsage,
                avgActiveConnections,
                avgCacheSize,
                queryErrors.get(),
                batchExecutions.get()
            );
        }
    }

    /**
     * Lazy load manager
     */
    private static class LazyLoadManager {
        private final Map<String, CompletableFuture<?>> loadingTasks = new ConcurrentHashMap<>();
        private final ExecutorService executor = Executors.newFixedThreadPool(5);

        public void initialize() {
            // Initialize lazy loading system
        }

        public <T> CompletableFuture<T> loadData(String key, Supplier<T> dataLoader) {
            @SuppressWarnings("unchecked")
            CompletableFuture<T> existingTask = (CompletableFuture<T>) loadingTasks.get(key);
            if (existingTask != null) {
                return existingTask;
            }

            CompletableFuture<T> task = CompletableFuture.supplyAsync(dataLoader, executor);
            loadingTasks.put(key, task);

            task.whenComplete((result, throwable) -> {
                loadingTasks.remove(key);
            });

            return task;
        }
    }

    /**
     * Lazy list implementation
     */
    public static class LazyList<T> implements List<T> {
        private final String query;
        private final Object[] params;
        private final Class<T> resultType;
        private final int pageSize;
        private final PerformanceOptimizer optimizer;
        private final List<T> cache = new ArrayList<>();
        private volatile boolean fullyLoaded = false;
        private volatile int totalCount = 0;

        public LazyList(String query, Object[] params, Class<T> resultType, int pageSize, PerformanceOptimizer optimizer) {
            this.query = query;
            this.params = params;
            this.resultType = resultType;
            this.pageSize = pageSize;
            this.optimizer = optimizer;
        }

        @Override
        public T get(int index) {
            ensureLoaded(index);
            return cache.get(index);
        }

        @Override
        public int size() {
            if (!fullyLoaded && totalCount == 0) {
                loadPage(0); // Load first page to get count
            }
            return totalCount;
        }

        private void ensureLoaded(int index) {
            if (index < cache.size()) {
                return; // Already loaded
            }

            int pageToLoad = (index / pageSize) * pageSize;
            loadPage(pageToLoad);
        }

        private void loadPage(int offset) {
            // In real implementation, modify query with LIMIT and OFFSET
            String paginatedQuery = query + " LIMIT " + pageSize + " OFFSET " + offset;
            List<T> pageData = optimizer.executeBatchQuery(paginatedQuery, params, resultType);
            
            synchronized (cache) {
                if (offset == 0) {
                    cache.clear();
                    totalCount = pageData.size(); // Estimate, real implementation would get COUNT
                }
                
                for (int i = 0; i < pageData.size() && offset + i < totalCount; i++) {
                    if (offset + i < cache.size()) {
                        cache.set(offset + i, pageData.get(i));
                    } else {
                        cache.add(pageData.get(i));
                    }
                }
            }
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                private int currentIndex = 0;

                @Override
                public boolean hasNext() {
                    return currentIndex < size();
                }

                @Override
                public T next() {
                    return get(currentIndex++);
                }
            };
        }

        // Other List methods...
        @Override
        public boolean isEmpty() {
            return size() == 0;
        }

        @Override
        public boolean contains(Object o) {
            for (int i = 0; i < size(); i++) {
                if (Objects.equals(get(i), o)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Object[] toArray() {
            Object[] array = new Object[size()];
            for (int i = 0; i < size(); i++) {
                array[i] = get(i);
            }
            return array;
        }
    }

    /**
     * Performance report data class
     */
    public static class PerformanceReport {
        private final double cacheHitRate;
        private final double averageQueryTime;
        private final double averageMemoryUsage;
        private final double averageActiveConnections;
        private final double averageCacheSize;
        private final long queryErrors;
        private final long batchExecutions;

        public PerformanceReport(double cacheHitRate, double averageQueryTime, double averageMemoryUsage,
                              double averageActiveConnections, double averageCacheSize, long queryErrors, long batchExecutions) {
            this.cacheHitRate = cacheHitRate;
            this.averageQueryTime = averageQueryTime;
            this.averageMemoryUsage = averageMemoryUsage;
            this.averageActiveConnections = averageActiveConnections;
            this.averageCacheSize = averageCacheSize;
            this.queryErrors = queryErrors;
            this.batchExecutions = batchExecutions;
        }

        // Getters
        public double getCacheHitRate() { return cacheHitRate; }
        public double getAverageQueryTime() { return averageQueryTime; }
        public double getAverageMemoryUsage() { return averageMemoryUsage; }
        public double getAverageActiveConnections() { return averageActiveConnections; }
        public double getAverageCacheSize() { return averageCacheSize; }
        public long getQueryErrors() { return queryErrors; }
        public long getBatchExecutions() { return batchExecutions; }
    }

    /**
     * Cleanup
     */
    public void cleanup() {
        monitoringExecutor.shutdown();
        cleanupExecutor.shutdown();
        lazyLoadManager.executor.shutdown();
        queryExecutor.shutdown();
        
        try {
            if (!monitoringExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                monitoringExecutor.shutdownNow();
            }
            if (!cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                cleanupExecutor.shutdownNow();
            }
            if (!lazyLoadManager.executor.awaitTermination(5, TimeUnit.SECONDS)) {
                lazyLoadManager.executor.shutdownNow();
            }
            if (!queryExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                queryExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Close all connections
        connectionPool.forEach(connection -> {
            try {
                connection.close();
            } catch (Exception e) {
                // Ignore close errors
            }
        });
        
        queryCache.clear();
        cacheTimestamps.clear();
        weakReferences.clear();
    }
}
