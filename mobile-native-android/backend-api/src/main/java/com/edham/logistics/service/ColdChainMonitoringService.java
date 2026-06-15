package com.edham.logistics.service;

import com.edham.logistics.model.*;
import com.edham.logistics.repository.*;
import com.edham.logistics.websocket.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
public class ColdChainMonitoringService {

    private final ShipmentRepository shipmentRepository;
    private final TemperatureReadingRepository temperatureRepository;
    private final ColdChainAlertRepository alertRepository;
    private final UserRepository userRepository;
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
    private static final int TEMPERATURE_CHECK_INTERVAL_SECONDS = 30;
    private static final int ALERT_PROCESSING_INTERVAL_SECONDS = 10;
    private static final int HISTORY_RETENTION_DAYS = 90;

    @Autowired
    public ColdChainMonitoringService(ShipmentRepository shipmentRepository,
                                   TemperatureReadingRepository temperatureRepository,
                                   ColdChainAlertRepository alertRepository,
                                   UserRepository userRepository,
                                   WebSocketService webSocketService) {
        this.shipmentRepository = shipmentRepository;
        this.temperatureRepository = temperatureRepository;
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
        this.webSocketService = webSocketService;
        
        initializeColdChainSystem();
    }

    private void initializeColdChainSystem() {
        log.info("Initializing cold-chain monitoring system");
        initializeProductThresholds();
        scheduler.scheduleAtFixedRate(this::monitorActiveSessions, 30, TEMPERATURE_CHECK_INTERVAL_SECONDS, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::processAlerts, 10, ALERT_PROCESSING_INTERVAL_SECONDS, TimeUnit.SECONDS);
        scheduler.scheduleAtFixedRate(this::cleanupOldData, 1, 24, TimeUnit.HOURS);
        log.info("Cold-chain monitoring system initialized successfully");
    }

    private void initializeProductThresholds() {
        productThresholds.put("FROZEN", TemperatureThreshold.builder()
                .productType("FROZEN").minTemperature(-25.0).maxTemperature(-18.0)
                .criticalMin(-30.0).criticalMax(-15.0).unit("°C").description("Frozen products").build());
        
        productThresholds.put("REFRIGERATED", TemperatureThreshold.builder()
                .productType("REFRIGERATED").minTemperature(2.0).maxTemperature(8.0)
                .criticalMin(0.0).criticalMax(10.0).unit("°C").description("Refrigerated products").build());
        
        productThresholds.put("AMBIENT", TemperatureThreshold.builder()
                .productType("AMBIENT").minTemperature(15.0).maxTemperature(25.0)
                .criticalMin(10.0).criticalMax(30.0).unit("°C").description("Ambient products").build());
        
        productThresholds.put("PHARMACEUTICAL", TemperatureThreshold.builder()
                .productType("PHARMACEUTICAL").minTemperature(2.0).maxTemperature(8.0)
                .criticalMin(0.0).criticalMax(12.0).unit("°C").description("Pharmaceuticals").build());
    }

    public CompletableFuture<ColdChainSession> startMonitoring(Long shipmentId, String productType) {
        return CompletableFuture.supplyAsync(() -> {
            Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new IllegalArgumentException("Shipment not found: " + shipmentId));
            
            TemperatureThreshold threshold = productThresholds.getOrDefault(productType, productThresholds.get("AMBIENT"));
            
            ColdChainSession session = ColdChainSession.builder()
                    .shipmentId(shipmentId)
                    .productType(productType)
                    .threshold(threshold)
                    .startTime(LocalDateTime.now())
                    .lastUpdateTime(LocalDateTime.now())
                    .active(true)
                    .totalReadings(0)
                    .alertCount(0)
                    .currentStatus(ColdChainAlert.TemperatureStatus.SAFE)
                    .build();
            
            activeSessions.put(shipmentId, session);
            shipment.setColdChainStatus("MONITORING");
            shipmentRepository.save(shipment);
            
            // Broadcast via WebSocket if needed
            return session;
        });
    }

    public CompletableFuture<Void> stopMonitoring(Long shipmentId) {
        return CompletableFuture.runAsync(() -> {
            ColdChainSession session = activeSessions.remove(shipmentId);
            if (session != null) {
                session.setActive(false);
                session.setEndTime(LocalDateTime.now());
                Shipment shipment = shipmentRepository.findById(shipmentId).orElse(null);
                if (shipment != null) {
                    shipment.setColdChainStatus("COMPLETED");
                    shipmentRepository.save(shipment);
                }
            }
        });
    }

    public void recordTemperature(Long shipmentId, Double temperature, Double humidity) {
        ColdChainSession session = activeSessions.get(shipmentId);
        if (session == null || !session.isActive()) return;

        TemperatureReading reading = TemperatureReading.builder()
                .shipmentId(shipmentId)
                .temperature(temperature)
                .humidity(humidity)
                .timestamp(LocalDateTime.now())
                .sessionId(session.getSessionId())
                .build();
        
        temperatureRepository.save(reading);
        updateSessionWithReading(session, reading);
        checkThresholdViolations(session, reading);
    }

    public List<ColdChainAlert> getAlertsByDateRange(LocalDateTime start, LocalDateTime end) {
        return alertRepository.findByTimestampBetween(start, end);
    }

    private void updateSessionWithReading(ColdChainSession session, TemperatureReading reading) {
        session.setLastUpdateTime(LocalDateTime.now());
        session.setLastReading(reading);
        session.setLastTemperature(reading.getTemperature());
        session.setTotalReadings(session.getTotalReadings() + 1);
        
        List<TemperatureReading> allReadings = temperatureRepository.findByShipmentIdOrderByTimestampAsc(session.getShipmentId());
        session.setAverageTemperature(allReadings.stream().mapToDouble(TemperatureReading::getTemperature).average().orElse(0.0));
        session.setMinTemperature(allReadings.stream().mapToDouble(TemperatureReading::getTemperature).min().orElse(0.0));
        session.setMaxTemperature(allReadings.stream().mapToDouble(TemperatureReading::getTemperature).max().orElse(0.0));
    }

    private void checkThresholdViolations(ColdChainSession session, TemperatureReading reading) {
        TemperatureThreshold threshold = session.getThreshold();
        double temp = reading.getTemperature();
        
        ColdChainAlert.TemperatureStatus status = determineTemperatureStatus(temp, threshold);
        ColdChainAlert.TemperatureStatus previousStatus = session.getCurrentStatus();
        session.setCurrentStatus(status);
        
        if (status != previousStatus) {
            ColdChainAlert alert = ColdChainAlert.builder()
                    .shipmentId(session.getShipmentId())
                    .sessionId(session.getSessionId())
                    .severity(determineAlertSeverity(status))
                    .alertType(determineAlertType(status, previousStatus))
                    .temperature(temp)
                    .previousStatus(previousStatus)
                    .newStatus(status)
                    .message(generateAlertMessage(status, threshold, temp))
                    .timestamp(LocalDateTime.now())
                    .resolved(status == ColdChainAlert.TemperatureStatus.SAFE)
                    .build();
            
            alertQueue.offer(alert);
            session.setAlertCount(session.getAlertCount() + 1);
        }
    }

    private ColdChainAlert.TemperatureStatus determineTemperatureStatus(double temp, TemperatureThreshold threshold) {
        if (temp < threshold.getCriticalMin() || temp > threshold.getCriticalMax()) return ColdChainAlert.TemperatureStatus.CRITICAL;
        if (temp < threshold.getMinTemperature() || temp > threshold.getMaxTemperature()) return ColdChainAlert.TemperatureStatus.WARNING;
        return ColdChainAlert.TemperatureStatus.SAFE;
    }

    private ColdChainAlert.AlertSeverity determineAlertSeverity(ColdChainAlert.TemperatureStatus status) {
        if (status == ColdChainAlert.TemperatureStatus.CRITICAL) return ColdChainAlert.AlertSeverity.CRITICAL;
        if (status == ColdChainAlert.TemperatureStatus.WARNING) return ColdChainAlert.AlertSeverity.WARNING;
        return ColdChainAlert.AlertSeverity.INFO;
    }

    private ColdChainAlert.AlertType determineAlertType(ColdChainAlert.TemperatureStatus newStatus, ColdChainAlert.TemperatureStatus prevStatus) {
        if (newStatus == ColdChainAlert.TemperatureStatus.CRITICAL) return ColdChainAlert.AlertType.TEMPERATURE_CRITICAL;
        if (newStatus == ColdChainAlert.TemperatureStatus.WARNING) return ColdChainAlert.AlertType.TEMPERATURE_WARNING;
        if (newStatus == ColdChainAlert.TemperatureStatus.SAFE && prevStatus != ColdChainAlert.TemperatureStatus.SAFE) return ColdChainAlert.AlertType.TEMPERATURE_NORMALIZED;
        return ColdChainAlert.AlertType.TEMPERATURE_UPDATE;
    }

    private String generateAlertMessage(ColdChainAlert.TemperatureStatus status, TemperatureThreshold threshold, double temp) {
        return String.format("%s: Temperature %.1f°C (Range: %.1f to %.1f)", status, temp, threshold.getMinTemperature(), threshold.getMaxTemperature());
    }

    private void monitorActiveSessions() {
        activeSessions.values().stream().filter(ColdChainSession::isActive).forEach(session -> {
            if (session.getLastUpdateTime().isBefore(LocalDateTime.now().minusMinutes(5))) {
                ColdChainAlert alert = ColdChainAlert.builder()
                        .shipmentId(session.getShipmentId()).sessionId(session.getSessionId())
                        .severity(ColdChainAlert.AlertSeverity.WARNING).alertType(ColdChainAlert.AlertType.SENSOR_OFFLINE)
                        .message("No temperature readings received in 5 minutes").timestamp(LocalDateTime.now()).resolved(false).build();
                alertQueue.offer(alert);
            }
        });
    }

    private void processAlerts() {
        while (!alertQueue.isEmpty()) {
            ColdChainAlert alert = alertQueue.poll();
            alertRepository.save(alert);
            // webSocketService.broadcastColdChainAlert(alert);
        }
    }

    private void cleanupOldData() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(HISTORY_RETENTION_DAYS);
        temperatureRepository.deleteByTimestampBefore(cutoff);
        alertRepository.deleteByTimestampBefore(cutoff);
    }
}
