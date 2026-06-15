# Edham Logistics Backend - Performance Optimization Guide

## 📋 Overview

This comprehensive guide covers performance optimization strategies for the Edham Logistics backend API system, focusing on database optimization, caching strategies, application performance, and monitoring.

## 🗄️ Database Optimization

### PostgreSQL Configuration

#### Memory Settings
```sql
-- postgresql.conf optimizations
-- Memory allocation
shared_buffers = 256MB                    -- 25% of RAM
effective_cache_size = 1GB                   -- 75% of RAM
work_mem = 4MB                               -- Per query memory
maintenance_work_mem = 64MB                   -- Maintenance operations
checkpoint_completion_target = 0.9             -- Smoother checkpoints
wal_buffers = 16MB                            -- WAL buffer size
```

#### Connection Management
```sql
-- Connection optimization
max_connections = 200                         -- Maximum connections
shared_preload_libraries = 'pg_stat_statements' -- Performance monitoring
track_activity_query_size = 2048               -- Track query statistics
track_counts = on                             -- Track row counts
track_io_timing = on                          -- Track I/O timing
```

#### Query Optimization
```sql
-- Create optimized indexes
CREATE INDEX CONCURRENTLY idx_shipments_status_created 
ON shipments(status, created_at DESC);

CREATE INDEX CONCURRENTLY idx_shipments_customer_status 
ON shipments(customer_id, status);

CREATE INDEX CONCURRENTLY idx_tracking_shipment_timestamp 
ON tracking_events(shipment_id, timestamp DESC);

-- Partial indexes for common queries
CREATE INDEX CONCURRENTLY idx_active_shipments 
ON shipments(id) WHERE status IN ('PENDING', 'ASSIGNED', 'IN_TRANSIT');

-- Composite indexes for complex queries
CREATE INDEX CONCURRENTLY idx_shipments_composite 
ON shipments(status, customer_id, created_at DESC, cost DESC);
```

#### Materialized Views
```sql
-- Materialized view for dashboard data
CREATE MATERIALIZED VIEW mv_shipment_dashboard AS
SELECT 
    s.status,
    COUNT(*) as count,
    SUM(s.cost) as total_cost,
    AVG(s.cost) as avg_cost,
    DATE(s.created_at) as date
FROM shipments s
GROUP BY s.status, DATE(s.created_at);

-- Refresh strategy
CREATE OR REPLACE FUNCTION refresh_dashboard_data()
RETURNS void AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY mv_shipment_dashboard;
END;
$$ LANGUAGE plpgsql;

-- Schedule refresh every 5 minutes
SELECT cron.schedule(
    'refresh-dashboard',
    '*/5 * * * *',
    $$SELECT refresh_dashboard_data()$$
);
```

### Database Partitioning

#### Time-based Partitioning
```sql
-- Partition tracking_events by month
CREATE TABLE tracking_events (
    id BIGSERIAL,
    shipment_id BIGINT REFERENCES shipments(id),
    event_type VARCHAR(50),
    description TEXT,
    location POINT,
    timestamp TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
) PARTITION BY RANGE (timestamp);

-- Create monthly partitions
CREATE TABLE tracking_events_2023_12 
PARTITION OF tracking_events
FOR VALUES FROM ('2023-12-01') TO ('2024-01-01');

CREATE TABLE tracking_events_2024_01 
PARTITION OF tracking_events
FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

-- Automated partition creation
CREATE OR REPLACE FUNCTION create_monthly_partitions()
RETURNS void AS $$
DECLARE
    start_date DATE;
    end_date DATE;
    partition_name TEXT;
BEGIN
    -- Create partitions for next 3 months
    FOR i IN 0..2 LOOP
        start_date := DATE_TRUNC('month', CURRENT_DATE + INTERVAL '1 month' * i);
        end_date := start_date + INTERVAL '1 month';
        partition_name := 'tracking_events_' || TO_CHAR(start_date, 'YYYY_MM');
        
        EXECUTE format('CREATE TABLE IF NOT EXISTS %I PARTITION OF tracking_events FOR VALUES FROM (%L) TO (%L)',
                     partition_name, start_date, end_date);
    END LOOP;
END;
$$ LANGUAGE plpgsql;
```

### Query Performance Analysis

#### Slow Query Monitoring
```sql
-- Enable pg_stat_statements
CREATE EXTENSION IF NOT EXISTS pg_stat_statements;

-- Find slow queries
SELECT 
    query,
    calls,
    total_time,
    mean_time,
    rows,
    100.0 * shared_blks_hit / nullif(shared_blks_hit + shared_blks_read, 0) AS hit_percent
FROM pg_stat_statements
ORDER BY mean_time DESC
LIMIT 10;

-- Query execution plan analysis
EXPLAIN (ANALYZE, BUFFERS, FORMAT JSON)
SELECT s.*, c.name as customer_name
FROM shipments s
JOIN customers c ON s.customer_id = c.id
WHERE s.status = 'PENDING'
AND s.created_at >= CURRENT_DATE - INTERVAL '7 days'
ORDER BY s.created_at DESC;
```

## 🚀 Application Performance

### Spring Boot Optimization

#### JVM Configuration
```bash
# Production JVM settings
JAVA_OPTS="-Xms2g -Xmx4g"
JAVA_OPTS="$JAVA_OPTS -XX:+UseG1GC"
JAVA_OPTS="$JAVA_OPTS -XX:MaxGCPauseMillis=200"
JAVA_OPTS="$JAVA_OPTS -XX:+UseStringDeduplication"
JAVA_OPTS="$JAVA_OPTS -XX:+OptimizeStringConcat"
JAVA_OPTS="$JAVA_OPTS -XX:+UseCompressedOops"
JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=prod"
```

#### Connection Pool Optimization
```yaml
# application-prod.yml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 600000
      max-lifetime: 1800000
      connection-timeout: 30000
      leak-detection-threshold: 60000
      validation-timeout: 5000
      pool-name: "EdhamHikariPool"
```

#### JPA Optimization
```yaml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 50
          order_inserts: true
          order_updates: true
        cache:
          use_second_level_cache: true
          use_query_cache: true
          region.factory_class: org.hibernate.cache.ehcache.EhCacheRegionFactory
        connection:
          provider_disables_autocommit: true
        generate_statistics: false
        session:
          events:
            log:
              insert: false
              update: false
              delete: false
```

### Async Processing

#### Async Service Implementation
```java
@Service
public class AsyncShipmentService {
    
    @Async("shipmentExecutor")
    public CompletableFuture<Void> processShipmentAsync(Long shipmentId) {
        // Process shipment asynchronously
        return CompletableFuture.completedFuture(null);
    }
    
    @EventListener
    @Async
    public void handleShipmentCreated(ShipmentCreatedEvent event) {
        // Send notifications asynchronously
        notificationService.sendShipmentCreatedNotification(event.getShipment());
    }
}

@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean("shipmentExecutor")
    public Executor shipmentExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Shipment-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
```

### Caching Strategy

#### Redis Configuration
```yaml
spring:
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5
        max-wait: 2000ms
  cache:
    type: redis
    redis:
      time-to-live: 3600000  # 1 hour
      cache-null-values: false
      key-prefix: "edham:"
      use-key-prefix: true
```

#### Cache Implementation
```java
@Service
public class ShipmentService {
    
    @Cacheable(value = "shipments", key = "#id")
    public Shipment getShipment(Long id) {
        return shipmentRepository.findById(id)
            .orElseThrow(() -> new ShipmentNotFoundException(id));
    }
    
    @CacheEvict(value = "shipments", key = "#shipment.id")
    public Shipment updateShipment(Shipment shipment) {
        return shipmentRepository.save(shipment);
    }
    
    @CacheEvict(value = "shipments", allEntries = true)
    public void clearShipmentCache() {
        // Clear all shipment cache
    }
    
    @Cacheable(value = "customerShipments", key = "#customerId")
    public List<Shipment> getCustomerShipments(Long customerId) {
        return shipmentRepository.findByCustomerId(customerId);
    }
}
```

#### Cache Warming Strategy
```java
@Component
public class CacheWarmer {
    
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void warmCache() {
        // Warm frequently accessed data
        warmActiveShipments();
        warmCustomerData();
        warmDriverData();
    }
    
    private void warmActiveShipments() {
        List<Shipment> activeShipments = shipmentRepository.findActiveShipments();
        activeShipments.forEach(shipment -> {
            // Pre-load into cache
            shipmentService.getShipment(shipment.getId());
        });
    }
}
```

## 📊 Monitoring & Metrics

### Performance Metrics Collection

#### Micrometer Configuration
```java
@Configuration
public class MetricsConfig {
    
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
    
    @Bean
    public CountedAspect countedAspect(MeterRegistry registry) {
        return new CountedAspect(registry);
    }
}

@Service
public class ShipmentService {
    
    @Timed(name = "shipment.create", description = "Time taken to create shipment")
    @Counted(name = "shipment.create.count", description = "Number of shipments created")
    public Shipment createShipment(CreateShipmentRequest request) {
        // Implementation
    }
    
    @Timed(name = "shipment.search", description = "Time taken to search shipments")
    public Page<Shipment> searchShipments(ShipmentSearchCriteria criteria) {
        // Implementation
    }
}
```

#### Custom Metrics
```java
@Component
public class CustomMetrics {
    
    private final Counter shipmentCounter;
    private final Timer shipmentTimer;
    private final Gauge activeShipmentsGauge;
    
    public CustomMetrics(MeterRegistry registry) {
        this.shipmentCounter = Counter.builder("shipments.created")
            .description("Number of shipments created")
            .register(registry);
            
        this.shipmentTimer = Timer.builder("shipment.processing.time")
            .description("Time taken to process shipments")
            .register(registry);
            
        this.activeShipmentsGauge = Gauge.builder("shipments.active")
            .description("Number of active shipments")
            .register(registry, this, CustomMetrics::getActiveShipmentsCount);
    }
    
    public void recordShipmentCreated() {
        shipmentCounter.increment();
    }
    
    public Timer.Sample startShipmentTimer() {
        return Timer.start(shipmentTimer);
    }
    
    private double getActiveShmentsCount() {
        return shipmentRepository.countActiveShipments();
    }
}
```

### Performance Monitoring

#### Health Check Implementation
```java
@Component
public class PerformanceHealthIndicator implements HealthIndicator {
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder();
        
        // Check database performance
        try (Connection connection = dataSource.getConnection()) {
            long startTime = System.currentTimeMillis();
            connection.createStatement().execute("SELECT 1");
            long responseTime = System.currentTimeMillis() - startTime;
            
            if (responseTime > 1000) {
                builder.down().withDetail("database", "Slow response: " + responseTime + "ms");
            } else {
                builder.up().withDetail("database", "OK (" + responseTime + "ms)");
            }
        } catch (Exception e) {
            builder.down().withDetail("database", "Connection failed: " + e.getMessage());
        }
        
        // Check Redis performance
        try {
            long startTime = System.currentTimeMillis();
            redisTemplate.opsForValue().set("health-check", "test", 10, TimeUnit.SECONDS);
            redisTemplate.opsForValue().get("health-check");
            long responseTime = System.currentTimeMillis() - startTime;
            
            if (responseTime > 500) {
                builder.down().withDetail("redis", "Slow response: " + responseTime + "ms");
            } else {
                builder.up().withDetail("redis", "OK (" + responseTime + "ms)");
            }
        } catch (Exception e) {
            builder.down().withDetail("redis", "Connection failed: " + e.getMessage());
        }
        
        return builder.build();
    }
}
```

## 🔧 Code Optimization

### Efficient Data Access

#### Repository Optimization
```java
@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    
    // Use @Query for complex queries
    @Query("SELECT s FROM Shipment s WHERE s.customer.id = :customerId " +
           "AND s.status IN :statuses ORDER BY s.createdAt DESC")
    Page<Shipment> findByCustomerIdAndStatusIn(
        @Param("customerId") Long customerId,
        @Param("statuses") List<ShipmentStatus> statuses,
        Pageable pageable);
    
    // Use native queries for performance-critical operations
    @Query(value = "SELECT s.* FROM shipments s WHERE s.status = 'PENDING' " +
                   "ORDER BY s.created_at ASC LIMIT :limit FOR UPDATE SKIP LOCKED",
           nativeQuery = true)
    List<Shipment> findPendingShipmentsForUpdate(@Param("limit") int limit);
    
    // Use EntityGraph for lazy loading optimization
    @EntityGraph(attributePaths = {"customer", "driver", "trackingEvents"})
    Optional<Shipment> findByIdWithDetails(Long id);
}
```

#### DTO Projection
```java
// Use projections to reduce data transfer
public interface ShipmentProjection {
    Long getId();
    String getTrackingNumber();
    ShipmentStatus getStatus();
    BigDecimal getCost();
    LocalDateTime getCreatedAt();
    
    @Value("#{target.customer.name}")
    String getCustomerName();
    
    @Value("#{target.driver != null ? target.driver.name : null}")
    String getDriverName();
}

// Repository method returning projection
@Query("SELECT s.id as id, s.trackingNumber as trackingNumber, " +
       "s.status as status, s.cost as cost, s.createdAt as createdAt, " +
       "s.customer.name as customerName, " +
       "s.driver.name as driverName " +
       "FROM Shipment s WHERE s.customer.id = :customerId")
Page<ShipmentProjection> findShipmentProjectionsByCustomer(
    @Param("customerId") Long customerId, Pageable pageable);
```

### Batch Processing

#### Batch Operations
```java
@Service
@Transactional
public class BatchShipmentService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public void updateShipmentStatusesBatch(List<Long> shipmentIds, ShipmentStatus status) {
        // Batch update for better performance
        String sql = "UPDATE shipments SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, status.name());
                ps.setLong(2, shipmentIds.get(i));
            }
            
            @Override
            public int getBatchSize() {
                return shipmentIds.size();
            }
        });
    }
    
    public void insertTrackingEventsBatch(List<TrackingEvent> events) {
        String sql = "INSERT INTO tracking_events (shipment_id, event_type, " +
                   "description, timestamp, created_at) VALUES (?, ?, ?, ?, ?)";
        
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                TrackingEvent event = events.get(i);
                ps.setLong(1, event.getShipmentId());
                ps.setString(2, event.getEventType().name());
                ps.setString(3, event.getDescription());
                ps.setTimestamp(4, Timestamp.valueOf(event.getTimestamp()));
                ps.setTimestamp(5, Timestamp.valueOf(event.getCreatedAt()));
            }
            
            @Override
            public int getBatchSize() {
                return events.size();
            }
        });
    }
}
```

## 🌐 Network Optimization

### HTTP Client Optimization

#### Connection Pooling
```java
@Configuration
public class HttpClientConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = 
            new HttpComponentsClientHttpRequestFactory();
        
        // Connection pool configuration
        PoolingHttpClientConnectionManager connectionManager = 
            new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(50);
        connectionManager.setValidateAfterInactivity(2000);
        
        // Request configuration
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(5000)
            .setSocketTimeout(10000)
            .setConnectionRequestTimeout(5000)
            .build();
        
        CloseableHttpClient httpClient = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig)
            .build();
        
        factory.setHttpClient(httpClient);
        return new RestTemplate(factory);
    }
}
```

### Response Compression

#### Gzip Configuration
```yaml
server:
  compression:
    enabled: true
    mime-types: text/html,text/xml,text/plain,text/css,text/javascript,application/javascript,application/json
    min-response-size: 1024
  http2:
    enabled: true
```

## 📈 Performance Testing

### Load Testing Script
```java
@Component
public class LoadTestRunner {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    public void runLoadTest() {
        int threadCount = 50;
        int requestsPerThread = 100;
        String baseUrl = "http://localhost:8080/api/v1";
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < requestsPerThread; j++) {
                        // Test shipment creation
                        CreateShipmentRequest request = new CreateShipmentRequest();
                        // Set request data
                        
                        long startTime = System.currentTimeMillis();
                        ResponseEntity<Shipment> response = restTemplate.postForEntity(
                            baseUrl + "/shipments", request, Shipment.class);
                        long endTime = System.currentTimeMillis();
                        
                        // Log response time
                        System.out.println("Request time: " + (endTime - startTime) + "ms");
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        executor.shutdown();
    }
}
```

## 🔍 Performance Profiling

### JVM Profiling
```bash
# Enable JVM profiling
JAVA_OPTS="$JAVA_OPTS -XX:+FlightRecorder"
JAVA_OPTS="$JAVA_OPTS -XX:StartFlightRecording=duration=60s,filename=profile.jfr"
JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCDetails"
JAVA_OPTS="$JAVA_OPTS -XX:+PrintGCTimeStamps"
JAVA_OPTS="$JAVA_OPTS -Xloggc:gc.log"
```

### Application Profiling
```java
@Aspect
@Component
public class PerformanceProfiler {
    
    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public Object profileControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            
            // Log performance metrics
            log.info("Method {}.{} executed in {}ms", 
                    className, methodName, (endTime - startTime));
            
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("Method {}.{} failed after {}ms: {}", 
                      className, methodName, (endTime - startTime), e.getMessage());
            throw e;
        }
    }
}
```

## 📊 Performance Dashboards

### Grafana Dashboard Configuration
```json
{
  "dashboard": {
    "title": "Edham Logistics Performance",
    "panels": [
      {
        "title": "API Response Time",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_request_duration_seconds_sum[5m]) / rate(http_request_duration_seconds_count[5m])",
            "legendFormat": "Average Response Time"
          }
        ]
      },
      {
        "title": "Database Performance",
        "type": "graph",
        "targets": [
          {
            "expr": "postgres_connections_active",
            "legendFormat": "Active Connections"
          }
        ]
      },
      {
        "title": "Cache Hit Rate",
        "type": "singlestat",
        "targets": [
          {
            "expr": "redis_keyspace_hits_total / (redis_keyspace_hits_total + redis_keyspace_misses_total) * 100",
            "legendFormat": "Cache Hit Rate %"
          }
        ]
      }
    ]
  }
}
```

## 🚨 Performance Alerts

### Alert Configuration
```yaml
# Prometheus alert rules
groups:
  - name: performance
    rules:
      - alert: HighResponseTime
        expr: histogram_quantile(0.95, rate(http_request_duration_seconds_bucket[5m])) > 2
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High API response time detected"
          
      - alert: DatabaseConnectionsHigh
        expr: postgres_connections_active > 150
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "Database connection pool nearly exhausted"
          
      - alert: CacheHitRateLow
        expr: redis_keyspace_hits_total / (redis_keyspace_hits_total + redis_keyspace_misses_total) * 100 < 80
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Cache hit rate is below 80%"
```

## 📋 Performance Checklist

### Database Optimization
- [ ] Proper indexing strategy implemented
- [ ] Query execution plans analyzed
- [ ] Connection pool optimized
- [ ] Materialized views for complex queries
- [ ] Partitioning for large tables
- [ ] Regular vacuum and analyze operations

### Application Optimization
- [ ] JVM parameters tuned
- [ ] Connection pooling configured
- [ ] Caching strategy implemented
- [ ] Async processing for long operations
- [ ] Batch operations for bulk data
- [ ] DTO projections for data transfer

### Monitoring & Alerting
- [ ] Performance metrics collected
- [ ] Custom metrics implemented
- [ ] Health checks configured
- [ ] Performance dashboards created
- [ ] Alert rules defined
- [ ] Load testing performed

### Network Optimization
- [ ] HTTP connection pooling
- [ ] Response compression enabled
- [ ] CDN for static resources
- [ ] HTTP/2 enabled
- [ ] Keep-alive connections
- [ ] Request/response size optimization

## 🔄 Continuous Optimization

### Performance Monitoring Pipeline
```yaml
# CI/CD performance testing
performance_test:
  stage: test
  script:
    - mvn test
    - mvn gatling:test
  artifacts:
    reports:
      junit: target/surefire-reports/*.xml
      performance: target/gatling/results/*
```

### Automated Performance Regression
```java
@Test
public void testShipmentCreationPerformance() {
    // Baseline performance test
    long startTime = System.currentTimeMillis();
    shipmentService.createShipment(createTestShipment());
    long endTime = System.currentTimeMillis();
    long responseTime = endTime - startTime;
    
    // Assert performance meets requirements
    assertThat(responseTime).isLessThan(1000); // 1 second max
}
```

---

This performance optimization guide provides comprehensive strategies for ensuring the Edham Logistics backend operates at optimal performance levels. Regular monitoring and optimization are essential for maintaining system performance as the application scales.
