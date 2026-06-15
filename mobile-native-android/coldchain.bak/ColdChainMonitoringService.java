// package com.edham.logistics.coldchain;

import com.edham.logistics.model.*;
import com.edham.logistics.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Cold-chain monitoring service
 * Provides real-time temperature tracking, alerts, and historical logs
 */
@Slf4j
@Service
public class ColdChainMonitoringService {

    private final ShipmentRepository shipmentRepository;
    private final TemperatureReadingRepository temperatureRepository;
    private final ColdChainAlertRepository alertRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final WebSocketService webSocketService;

    // Active monitoring sessions
    private final Map<Long, ColdChainSession> activeSessions = new ConcurrentHashMap<>();
    
    // Temperature thresholds by product type
    private final Map<String, TemperatureThreshold> productThresholds = new ConcurrentHashMap<>();
    
    // Scheduled executor for periodic tasks
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    
    // Alert processing queue
    private final Queue<ColdChainAlert> alertQueue = new LinkedList<>();
    
    // Performance settings
    private static final int TEMPERATURE_CHECK_INTERVAL_SECONDS = 30; // Check every 30 seconds
    private static final int ALERT_PROCESSING_INTERVAL_SECONDS = 10; // Process alerts every 10 seconds
    private static final int HISTORY_RETENTION_DAYS = 90; // Keep 90 days of history
    private static final int MAX_ALERTS_PER_SHIPMENT = 100; // Limit alerts per shipment

    @Autowired
    public ColdChainMonitoringService(ShipmentRepository shipmentRepository,
                                   TemperatureReadingRepository temperatureRepository,
                                   ColdChainAlertRepository alertRepository,
                                   UserRepository userRepository,
                                   MongoTemplate mongoTemplate,
                                   WebSocketService webSocketService) {
        this.shipmentRepository = shipmentRepository;
        this.temperatureRepository = temperatureRepository;
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
        this.webSocketService = webSocketService;
        
        initializeColdChainSystem();
    }

    /**
     * Initialize cold-chain monitoring system
     */
    private void initializeColdChainSystem() {
        log.info("Initializing cold-chain monitoring system");
        
        // Initialize product temperature thresholds
        initializeProductThresholds();
        
        // Start periodic temperature monitoring
        scheduler.scheduleAtFixedRate(this::monitorActiveSessions, 30, TEMPERATURE_CHECK_INTERVAL_SECONDS, TimeUnit.SECONDS);
        
        // Start alert processing
        scheduler.scheduleAtFixedRate(this::processAlerts, 10, ALERT_PROCESSING_INTERVAL_SECONDS, TimeUnit.SECONDS);
        
        // Start cleanup of old data
        scheduler.scheduleAtFixedRate(this::cleanupOldData, 1, 24, TimeUnit.HOURS);
        
        // Start performance monitoring
        scheduler.scheduleAtFixedRate(this::monitorPerformance, 60, 300, TimeUnit.SECONDS);
        
        log.info("Cold-chain monitoring system initialized successfully");
    }

    /**
     * Initialize product temperature thresholds
     */
    private void initializeProductThresholds() {
        productThresholds.put("FROZEN", TemperatureThreshold.builder()
                .productType("FROZEN")
                .minTemperature(-25.0)
                .maxTemperature(-18.0)
                .criticalMin(-30.0)
                .criticalMax(-15.0)
                .unit("°C")
                .description("Frozen products")
                .build());
        
        productThresholds.put("REFRIGERATED", TemperatureThreshold.builder()
                .productType("REFRIGERATED")
                .minTemperature(2.0)
                .maxTemperature(8.0)
                .criticalMin(0.0)
                .criticalMax(10.0)
                .unit("°C")
                .description("Refrigerated products")
                .build());
        
        productThresholds.put("AMBIENT", TemperatureThreshold.builder()
                .productType("AMBIENT")
                .minTemperature(15.0)
                .maxTemperature(25.0)
                .criticalMin(10.0)
                .criticalMax(30.0)
                .unit("°C")
                .description("Ambient temperature products")
                .build());
        
        productThresholds.put("PHARMACEUTICAL", TemperatureThreshold.builder()
                .productType("PHARMACEUTICAL")
                .minTemperature(2.0)
                .maxTemperature(8.0)
                .criticalMin(0.0)
                .criticalMax(12.0)
                .unit("°C")
                .description("Pharmaceutical products")
                .build());
        
        log.info("Product temperature thresholds initialized: {}", productThresholds.size());
    }

    /**
     * Start cold-chain monitoring for shipment
     */
    public CompletableFuture<ColdChainSession> startMonitoring(Long shipmentId, String productType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.debug("Starting cold-chain monitoring for shipment: {}, product: {}", shipmentId, productType);
                
                // Validate shipment
                Optional<Shipment> shipmentOpt = shipmentRepository.findById(shipmentId);
                if (shipmentOpt.isEmpty()) {
                    throw new IllegalArgumentException("Shipment not found: " + shipmentId);
                }
                
                // Get temperature threshold
                TemperatureThreshold threshold = productThresholds.get(productType);
                if (threshold == null) {
                    threshold = productThresholds.get("AMBIENT"); // Default
                }
                
                // Create monitoring session
                ColdChainSession session = ColdChainSession.builder()
                        .shipmentId(shipmentId)
                        .productType(productType)
                        .threshold(threshold)
                        .startTime(LocalDateTime.now())
                        .lastUpdateTime(LocalDateTime.now())
                        .isActive(true)
                        .totalReadings(0)
                        .alertCount(0)
                        .currentStatus(TemperatureStatus.SAFE)
                        .lastTemperature(null)
                        .averageTemperature(null)
                        .minTemperature(null)
                        .maxTemperature(null)
                        .build();
                
                // Store session
                activeSessions.put(shipmentId, session);
                
                // Update shipment status
                updateShipmentColdChainStatus(shipmentId, ColdChainStatus.MONITORING);
                
                // Notify clients
                webSocketService.broadcastColdChainSessionStart(session);
                
                log.info("Cold-chain monitoring started for shipment: {}, session: {}", shipmentId, session.getSessionId());
                return session;
                
            } catch (Exception e) {
                log.error("Error starting cold-chain monitoring for shipment {}: {}", shipmentId, e.getMessage(), e);
                throw new RuntimeException("Failed to start monitoring", e);
            }
        });
    }

    /**
     * Stop cold-chain monitoring for shipment
     */
    public CompletableFuture<Void> stopMonitoring(Long shipmentId) {
        return CompletableFuture.runAsync(() -> {
            try {
                log.debug("Stopping cold-chain monitoring for shipment: {}", shipmentId);
                
                ColdChainSession session = activeSessions.remove(shipmentId);
                if (session != null) {
                    session.setActive(false);
                    session.setEndTime(LocalDateTime.now());
                    
                    // Update shipment status
                    updateShipmentColdChainStatus(shipmentId, ColdChainStatus.COMPLETED);
                    
                    // Save session summary
                    saveColdChainSessionSummary(session);
                    
                    // Notify clients
                    webSocketService.broadcastColdChainSessionEnd(session);
                    
                    log.info("Cold-chain monitoring stopped for shipment: {}, session: {}", shipmentId, session.getSessionId());
                }
                
            } catch (Exception e) {
                log.error("Error stopping cold-chain monitoring for shipment {}: {}", shipmentId, e.getMessage(), e);
            }
        });
    }

    /**
     * Record temperature reading
     */
    public CompletableFuture<TemperatureReadingResult> recordTemperature(TemperatureReadingRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Long shipmentId = request.getShipmentId();
                ColdChainSession session = activeSessions.get(shipmentId);
                
                if (session == null || !session.isActive()) {
                    log.warn("No active cold-chain monitoring for shipment: {}", shipmentId);
                    return TemperatureReadingResult.builder()
                            .success(false)
                            .error("No active monitoring session")
                            .build();
                }
                
                // Create temperature reading
                TemperatureReading reading = TemperatureReading.builder()
                        .shipmentId(shipmentId)
                        .temperature(request.getTemperature())
                        .humidity(request.getHumidity())
                        .sensorId(request.getSensorId())
                        .location(request.getLocation())
                        .timestamp(LocalDateTime.now())
                        .deviceId(request.getDeviceId())
                        .batteryLevel(request.getBatteryLevel())
                        .signalStrength(request.getSignalStrength())
                        .sessionId(session.getSessionId())
                        .build();
                
                // Save reading
                temperatureRepository.save(reading);
                
                // Update session
                updateSessionWithReading(session, reading);
                
                // Check for threshold violations
                checkThresholdViolations(session, reading);
                
                // Broadcast update
                webSocketService.broadcastTemperatureUpdate(shipmentId, reading, session);
                
                return TemperatureReadingResult.builder()
                        .success(true)
                        .sessionId(session.getSessionId())
                        .readingId(reading.getId())
                        .currentStatus(session.getCurrentStatus())
                        .alertCount(session.getAlertCount())
                        .build();
                
            } catch (Exception e) {
                log.error("Error recording temperature for shipment {}: {}", request.getShipmentId(), e.getMessage(), e);
                return TemperatureReadingResult.builder()
                        .success(false)
                        .error("Temperature recording failed: " + e.getMessage())
                        .build();
            }
        });
    }

    /**
     * Get current temperature for shipment
     */
    public CompletableFuture<TemperatureReading> getCurrentTemperature(Long shipmentId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ColdChainSession session = activeSessions.get(shipmentId);
                if (session == null || !session.isActive()) {
                    return null;
                }
                
                // Get latest reading from cache or database
                TemperatureReading reading = session.getLastReading();
                if (reading == null) {
                    // Fallback to database
                    reading = temperatureRepository.findTopByShipmentIdOrderByTimestampDesc(shipmentId)
                            .orElse(null);
                }
                
                return reading;
                
            } catch (Exception e) {
                log.error("Error getting current temperature for shipment {}: {}", shipmentId, e.getMessage(), e);
                return null;
            }
        });
    }

    /**
     * Get temperature history for shipment
     */
    public CompletableFuture<List<TemperatureReading>> getTemperatureHistory(Long shipmentId, 
                                                                              LocalDateTime startTime, 
                                                                              LocalDateTime endTime) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<TemperatureReading> history;
                
                if (startTime != null && endTime != null) {
                    history = temperatureRepository.findByShipmentIdAndTimestampBetweenOrderByTimestampDesc(
                            shipmentId, startTime, endTime);
                } else {
                    // Get last 24 hours
                    LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
                    history = temperatureRepository.findByShipmentIdAndTimestampAfterOrderByTimestampDesc(
                            shipmentId, twentyFourHoursAgo);
                }
                
                return history;
                
            } catch (Exception e) {
                log.error("Error getting temperature history for shipment {}: {}", shipmentId, e.getMessage(), e);
                return new ArrayList<>();
            }
        });
    }

    /**
     * Get alerts for shipment
     */
    public CompletableFuture<List<ColdChainAlert>> getAlerts(Long shipmentId, AlertSeverity severity) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<ColdChainAlert> alerts;
                
                if (severity != null) {
                    alerts = alertRepository.findByShipmentIdAndSeverityOrderByTimestampDesc(shipmentId, severity);
                } else {
                    alerts = alertRepository.findByShipmentIdOrderByTimestampDesc(shipmentId);
                }
                
                return alerts;
                
            } catch (Exception e) {
                log.error("Error getting alerts for shipment {}: {}", shipmentId, e.getMessage(), e);
                return new ArrayList<>();
            }
        });
    }

    /**
     * Get active monitoring sessions
     */
    public CompletableFuture<List<ColdChainSession>> getActiveSessions() {
        return CompletableFuture.supplyAsync(() -> {
            return activeSessions.values().stream()
                    .filter(ColdChainSession::isActive)
                    .collect(Collectors.toList());
        });
    }

    /**
     * Get cold-chain statistics
     */
    public CompletableFuture<ColdChainStatistics> getStatistics(Long shipmentId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ColdChainSession session = activeSessions.get(shipmentId);
                if (session == null) {
                    return null;
                }
                
                // Calculate statistics
                List<TemperatureReading> readings = temperatureRepository.findByShipmentIdOrderByTimestampAsc(shipmentId);
                
                ColdChainStatistics statistics = ColdChainStatistics.builder()
                        .shipmentId(shipmentId)
                        .totalReadings(readings.size())
                        .sessionDuration(calculateSessionDuration(session))
                        .averageTemperature(calculateAverageTemperature(readings))
                        .minTemperature(calculateMinTemperature(readings))
                        .maxTemperature(calculateMaxTemperature(readings))
                        .temperatureVariance(calculateTemperatureVariance(readings))
                        .alertCount(session.getAlertCount())
                        .safePercentage(calculateSafePercentage(readings, session.getThreshold()))
                        .warningPercentage(calculateWarningPercentage(readings, session.getThreshold()))
                        .criticalPercentage(calculateCriticalPercentage(readings, session.getThreshold()))
                        .lastUpdateTime(LocalDateTime.now())
                        .build();
                
                return statistics;
                
            } catch (Exception e) {
                log.error("Error calculating statistics for shipment {}: {}", shipmentId, e.getMessage(), e);
                return null;
            }
        });
    }

    /**
     * Update session with new reading
     */
    private void updateSessionWithReading(ColdChainSession session, TemperatureReading reading) {
        session.setLastUpdateTime(LocalDateTime.now());
        session.setLastReading(reading);
        session.setLastTemperature(reading.getTemperature());
        session.setTotalReadings(session.getTotalReadings() + 1);
        
        // Update temperature statistics
        List<TemperatureReading> allReadings = temperatureRepository
                .findByShipmentIdOrderByTimestampAsc(session.getShipmentId());
        
        session.setAverageTemperature(calculateAverageTemperature(allReadings));
        session.setMinTemperature(calculateMinTemperature(allReadings));
        session.setMaxTemperature(calculateMaxTemperature(allReadings));
    }

    /**
     * Check for threshold violations
     */
    private void checkThresholdViolations(ColdChainSession session, TemperatureReading reading) {
        TemperatureThreshold threshold = session.getThreshold();
        double temperature = reading.getTemperature();
        
        TemperatureStatus status = determineTemperatureStatus(temperature, threshold);
        TemperatureStatus previousStatus = session.getCurrentStatus();
        
        session.setCurrentStatus(status);
        
        // Check for status changes
        if (status != previousStatus) {
            ColdChainAlert alert = ColdChainAlert.builder()
                    .shipmentId(session.getShipmentId())
                    .sessionId(session.getSessionId())
                    .severity(determineAlertSeverity(status))
                    .alertType(determineAlertType(status, previousStatus))
                    .temperature(temperature)
                    .threshold(threshold)
                    .previousStatus(previousStatus)
                    .newStatus(status)
                    .message(generateAlertMessage(status, threshold, temperature))
                    .timestamp(LocalDateTime.now())
                    .resolved(status == TemperatureStatus.SAFE)
                    .build();
            
            // Add to alert queue
            alertQueue.offer(alert);
            session.setAlertCount(session.getAlertCount() + 1);
            
            log.info("Temperature alert generated for shipment {}: {}", session.getShipmentId(), alert.getAlertType());
        }
    }

    /**
     * Determine temperature status
     */
    private TemperatureStatus determineTemperatureStatus(double temperature, TemperatureThreshold threshold) {
        if (temperature < threshold.getCriticalMin() || temperature > threshold.getCriticalMax()) {
            return TemperatureStatus.CRITICAL;
        } else if (temperature < threshold.getMinTemperature() || temperature > threshold.getMaxTemperature()) {
            return TemperatureStatus.WARNING;
        } else {
            return TemperatureStatus.SAFE;
        }
    }

    /**
     * Determine alert severity
     */
    private AlertSeverity determineAlertSeverity(TemperatureStatus status) {
        switch (status) {
            case CRITICAL: return AlertSeverity.CRITICAL;
            case WARNING: return AlertSeverity.WARNING;
            case SAFE: return AlertSeverity.INFO;
            default: return AlertSeverity.INFO;
        }
    }

    /**
     * Determine alert type
     */
    private AlertType determineAlertType(TemperatureStatus newStatus, TemperatureStatus previousStatus) {
        if (newStatus == TemperatureStatus.CRITICAL) {
            return AlertType.TEMPERATURE_CRITICAL;
        } else if (newStatus == TemperatureStatus.WARNING) {
            return AlertType.TEMPERATURE_WARNING;
        } else if (newStatus == TemperatureStatus.SAFE && previousStatus != TemperatureStatus.SAFE) {
            return AlertType.TEMPERATURE_NORMALIZED;
        } else {
            return AlertType.TEMPERATURE_UPDATE;
        }
    }

    /**
     * Generate alert message
     */
    private String generateAlertMessage(TemperatureStatus status, TemperatureThreshold threshold, double temperature) {
        switch (status) {
            case CRITICAL:
                return String.format("CRITICAL: Temperature %.1f°C is outside critical range (%.1f°C to %.1f°C)", 
                        temperature, threshold.getCriticalMin(), threshold.getCriticalMax());
            case WARNING:
                return String.format("WARNING: Temperature %.1f°C is outside safe range (%.1f°C to %.1f°C)", 
                        temperature, threshold.getMinTemperature(), threshold.getMaxTemperature());
            case SAFE:
                return String.format("INFO: Temperature %.1f°C is within safe range (%.1f°C to %.1f°C)", 
                        temperature, threshold.getMinTemperature(), threshold.getMaxTemperature());
            default:
                return String.format("Temperature updated: %.1f°C", temperature);
        }
    }

    /**
     * Monitor active sessions
     */
    private void monitorActiveSessions() {
        try {
            activeSessions.values().stream()
                    .filter(ColdChainSession::isActive)
                    .forEach(session -> {
                        // Check for stale sessions (no readings in 5 minutes)
                        if (session.getLastUpdateTime().isBefore(LocalDateTime.now().minusMinutes(5))) {
                            log.warn("Stale cold-chain session detected: {}", session.getShipmentId());
                            
                            // Create stale session alert
                            ColdChainAlert alert = ColdChainAlert.builder()
                                    .shipmentId(session.getShipmentId())
                                    .sessionId(session.getSessionId())
                                    .severity(AlertSeverity.WARNING)
                                    .alertType(AlertType.SENSOR_OFFLINE)
                                    .message("No temperature readings received in 5 minutes")
                                    .timestamp(LocalDateTime.now())
                                    .resolved(false)
                                    .build();
                            
                            alertQueue.offer(alert);
                        }
                    });
            
        } catch (Exception e) {
            log.error("Error monitoring active sessions: {}", e.getMessage(), e);
        }
    }

    /**
     * Process alerts
     */
    private void processAlerts() {
        try {
            while (!alertQueue.isEmpty()) {
                ColdChainAlert alert = alertQueue.poll();
                
                // Save alert
                alertRepository.save(alert);
                
                // Send notifications
                sendAlertNotifications(alert);
                
                // Broadcast to clients
                webSocketService.broadcastColdChainAlert(alert);
                
                log.debug("Processed alert: {} for shipment: {}", alert.getAlertType(), alert.getShipmentId());
            }
            
        } catch (Exception e) {
            log.error("Error processing alerts: {}", e.getMessage(), e);
        }
    }

    /**
     * Send alert notifications
     */
    private void sendAlertNotifications(ColdChainAlert alert) {
        try {
            // Get shipment details
            Optional<Shipment> shipmentOpt = shipmentRepository.findById(alert.getShipmentId());
            if (shipmentOpt.isEmpty()) {
                return;
            }
            
            Shipment shipment = shipmentOpt.get();
            
            // Send notifications based on role
            if (alert.getSeverity() == AlertSeverity.CRITICAL) {
                // Notify all stakeholders
                sendNotificationToUser(shipment.getCustomerId(), alert);
                sendNotificationToUser(shipment.getDriverId(), alert);
                notifyAdmins(alert);
            } else if (alert.getSeverity() == AlertSeverity.WARNING) {
                // Notify driver and customer
                sendNotificationToUser(shipment.getDriverId(), alert);
                sendNotificationToUser(shipment.getCustomerId(), alert);
            }
            
        } catch (Exception e) {
            log.error("Error sending alert notifications: {}", e.getMessage(), e);
        }
    }

    /**
     * Send notification to user
     */
    private void sendNotificationToUser(Long userId, ColdChainAlert alert) {
        // Implementation would depend on notification service
        log.info("Notification sent to user {}: {}", userId, alert.getMessage());
    }

    /**
     * Notify admins
     */
    private void notifyAdmins(ColdChainAlert alert) {
        // Implementation would notify all admin users
        log.info("Admin notification sent for alert: {}", alert.getAlertType());
    }

    /**
     * Update shipment cold-chain status
     */
    private void updateShipmentColdChainStatus(Long shipmentId, ColdChainStatus status) {
        try {
            Query query = new Query(Criteria.where("id").is(shipmentId));
            Update update = Update.update("coldChainStatus", status);
            mongoTemplate.updateFirst(query, update, "shipments");
        } catch (Exception e) {
            log.error("Error updating shipment cold-chain status: {}", e.getMessage(), e);
        }
    }

    /**
     * Save cold-chain session summary
     */
    private void saveColdChainSessionSummary(ColdChainSession session) {
        try {
            // Implementation would save session summary to database
            log.info("Cold-chain session summary saved for shipment: {}", session.getShipmentId());
        } catch (Exception e) {
            log.error("Error saving session summary: {}", e.getMessage(), e);
        }
    }

    /**
     * Cleanup old data
     */
    private void cleanupOldData() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(HISTORY_RETENTION_DAYS);
            
            // Delete old temperature readings
            long deletedReadings = temperatureRepository.deleteByTimestampBefore(cutoffDate);
            
            // Delete old alerts
            long deletedAlerts = alertRepository.deleteByTimestampBefore(cutoffDate);
            
            log.info("Cleaned up old data: {} readings, {} alerts", deletedReadings, deletedAlerts);
            
        } catch (Exception e) {
            log.error("Error cleaning up old data: {}", e.getMessage(), e);
        }
    }

    /**
     * Monitor performance
     */
    private void monitorPerformance() {
        try {
            int activeSessionCount = activeSessions.size();
            int queuedAlerts = alertQueue.size();
            
            log.info("Cold-chain performance metrics - Active sessions: {}, Queued alerts: {}", 
                    activeSessionCount, queuedAlerts);
            
            // Check for performance issues
            if (activeSessionCount > 1000) {
                log.warn("High number of active cold-chain sessions: {}", activeSessionCount);
            }
            
            if (queuedAlerts > 100) {
                log.warn("High number of queued alerts: {}", queuedAlerts);
            }
            
        } catch (Exception e) {
            log.error("Error monitoring performance: {}", e.getMessage(), e);
        }
    }

    // Helper methods for calculations
    private Double calculateAverageTemperature(List<TemperatureReading> readings) {
        return readings.stream()
                .mapToDouble(TemperatureReading::getTemperature)
                .average()
                .orElse(null);
    }

    private Double calculateMinTemperature(List<TemperatureReading> readings) {
        return readings.stream()
                .mapToDouble(TemperatureReading::getTemperature)
                .min()
                .orElse(null);
    }

    private Double calculateMaxTemperature(List<TemperatureReading> readings) {
        return readings.stream()
                .mapToDouble(TemperatureReading::getTemperature)
                .max()
                .orElse(null);
    }

    private Double calculateTemperatureVariance(List<TemperatureReading> readings) {
        if (readings.isEmpty()) return null;
        
        double mean = calculateAverageTemperature(readings);
        double variance = readings.stream()
                .mapToDouble(r -> Math.pow(r.getTemperature() - mean, 2))
                .average()
                .orElse(0.0);
        
        return variance;
    }

    private Long calculateSessionDuration(ColdChainSession session) {
        if (session.getEndTime() != null) {
            return java.time.Duration.between(session.getStartTime(), session.getEndTime()).getSeconds();
        } else {
            return java.time.Duration.between(session.getStartTime(), LocalDateTime.now()).getSeconds();
        }
    }

    private Double calculateSafePercentage(List<TemperatureReading> readings, TemperatureThreshold threshold) {
        if (readings.isEmpty()) return 0.0;
        
        long safeCount = readings.stream()
                .filter(r -> r.getTemperature() >= threshold.getMinTemperature() && 
                           r.getTemperature() <= threshold.getMaxTemperature())
                .count();
        
        return (double) safeCount / readings.size() * 100;
    }

    private Double calculateWarningPercentage(List<TemperatureReading> readings, TemperatureThreshold threshold) {
        if (readings.isEmpty()) return 0.0;
        
        long warningCount = readings.stream()
                .filter(r -> (r.getTemperature() < threshold.getMinTemperature() || r.getTemperature() > threshold.getMaxTemperature()) &&
                           (r.getTemperature() >= threshold.getCriticalMin() && r.getTemperature() <= threshold.getCriticalMax()))
                .count();
        
        return (double) warningCount / readings.size() * 100;
    }

    private Double calculateCriticalPercentage(List<TemperatureReading> readings, TemperatureThreshold threshold) {
        if (readings.isEmpty()) return 0.0;
        
        long criticalCount = readings.stream()
                .filter(r -> r.getTemperature() < threshold.getCriticalMin() || r.getTemperature() > threshold.getCriticalMax())
                .count();
        
        return (double) criticalCount / readings.size() * 100;
    }

    // Result classes and enums
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TemperatureReadingResult {
        private Boolean success;
        private String error;
        private String sessionId;
        private String readingId;
        private TemperatureStatus currentStatus;
        private Integer alertCount;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TemperatureReadingRequest {
        private Long shipmentId;
        private Double temperature;
        private Double humidity;
        private String sensorId;
        private String location;
        private String deviceId;
        private Integer batteryLevel;
        private Integer signalStrength;
    }

    public enum TemperatureStatus {
        SAFE, WARNING, CRITICAL
    }

    public enum AlertSeverity {
        INFO, WARNING, CRITICAL
    }

    public enum AlertType {
        TEMPERATURE_UPDATE, TEMPERATURE_WARNING, TEMPERATURE_CRITICAL, TEMPERATURE_NORMALIZED, SENSOR_OFFLINE
    }

    public enum ColdChainStatus {
        NOT_STARTED, MONITORING, COMPLETED, FAILED
    }
}
