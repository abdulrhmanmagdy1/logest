// package com.edham.logistics.coldchain;

import com.edham.logistics.model.Shipment;
import com.edham.logistics.model.User;
import com.edham.logistics.notification.UnifiedNotificationService;
import com.edham.logistics.repository.ShipmentRepository;
import com.edham.logistics.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Smart Cooling Alert System with Multi-level Alerts, Auto Escalation, and Predictive Analytics
 */
@Service
public class SmartCoolingAlertService {

    @Autowired
    private ColdChainMonitoringService coldChainMonitoringService;
    
    @Autowired
    private UnifiedNotificationService notificationService;
    
    @Autowired
    private ShipmentRepository shipmentRepository;
    
    @Autowired
    private UserRepository userRepository;

    // Alert level definitions
    public enum AlertLevel {
        WARNING("تحذير", 1, "#F59E0B", 30),    // 30 minutes to escalate
        CRITICAL("حرج", 2, "#EF4444", 15),     // 15 minutes to escalate
        EMERGENCY("طوارئ", 3, "#DC2626", 5);   // 5 minutes to escalate

        private final String arabicName;
        private final int severity;
        private final String color;
        private final int escalationMinutes;

        AlertLevel(String arabicName, int severity, String color, int escalationMinutes) {
            this.arabicName = arabicName;
            this.severity = severity;
            this.color = color;
            this.escalationMinutes = escalationMinutes;
        }

        public String getArabicName() { return arabicName; }
        public int getSeverity() { return severity; }
        public String getColor() { return color; }
        public int getEscalationMinutes() { return escalationMinutes; }
    }

    // Alert status tracking
    private final Map<String, SmartCoolingAlert> activeAlerts = new ConcurrentHashMap<>();
    private final Map<String, List<TemperatureReading>> recentReadings = new ConcurrentHashMap<>();
    private final Map<String, CoolingFailurePrediction> failurePredictions = new ConcurrentHashMap<>();

    /**
     * Smart Cooling Alert with comprehensive information
     */
    public static class SmartCoolingAlert {
        private String alertId;
        private String shipmentId;
        private AlertLevel level;
        private double currentTemperature;
        private double thresholdTemperature;
        private String productType;
        private LocalDateTime createdAt;
        private LocalDateTime lastEscalated;
        private int escalationCount;
        private boolean acknowledged;
        private String acknowledgedBy;
        private LocalDateTime acknowledgedAt;
        private List<String> notifiedUsers;
        private String alertReason;
        private double temperatureDeviation;
        private int durationMinutes;
        private boolean autoResolved;

        // Constructors, getters, and setters
        public SmartCoolingAlert() {}

        public SmartCoolingAlert(String shipmentId, AlertLevel level, double currentTemp, 
                                double thresholdTemp, String productType, String reason) {
            this.alertId = UUID.randomUUID().toString();
            this.shipmentId = shipmentId;
            this.level = level;
            this.currentTemperature = currentTemp;
            this.thresholdTemperature = thresholdTemp;
            this.productType = productType;
            this.createdAt = LocalDateTime.now();
            this.escalationCount = 0;
            this.acknowledged = false;
            this.notifiedUsers = new ArrayList<>();
            this.alertReason = reason;
            this.temperatureDeviation = Math.abs(currentTemp - thresholdTemp);
            this.autoResolved = false;
        }

        // Getters and setters...
        public String getAlertId() { return alertId; }
        public void setAlertId(String alertId) { this.alertId = alertId; }
        public String getShipmentId() { return shipmentId; }
        public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }
        public AlertLevel getLevel() { return level; }
        public void setLevel(AlertLevel level) { this.level = level; }
        public double getCurrentTemperature() { return currentTemperature; }
        public void setCurrentTemperature(double currentTemperature) { this.currentTemperature = currentTemperature; }
        public double getThresholdTemperature() { return thresholdTemperature; }
        public void setThresholdTemperature(double thresholdTemperature) { this.thresholdTemperature = thresholdTemperature; }
        public String getProductType() { return productType; }
        public void setProductType(String productType) { this.productType = productType; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getLastEscalated() { return lastEscalated; }
        public void setLastEscalated(LocalDateTime lastEscalated) { this.lastEscalated = lastEscalated; }
        public int getEscalationCount() { return escalationCount; }
        public void setEscalationCount(int escalationCount) { this.escalationCount = escalationCount; }
        public boolean isAcknowledged() { return acknowledged; }
        public void setAcknowledged(boolean acknowledged) { this.acknowledged = acknowledged; }
        public String getAcknowledgedBy() { return acknowledgedBy; }
        public void setAcknowledgedBy(String acknowledgedBy) { this.acknowledgedBy = acknowledgedBy; }
        public LocalDateTime getAcknowledgedAt() { return acknowledgedAt; }
        public void setAcknowledgedAt(LocalDateTime acknowledgedAt) { this.acknowledgedAt = acknowledgedAt; }
        public List<String> getNotifiedUsers() { return notifiedUsers; }
        public void setNotifiedUsers(List<String> notifiedUsers) { this.notifiedUsers = notifiedUsers; }
        public String getAlertReason() { return alertReason; }
        public void setAlertReason(String alertReason) { this.alertReason = alertReason; }
        public double getTemperatureDeviation() { return temperatureDeviation; }
        public void setTemperatureDeviation(double temperatureDeviation) { this.temperatureDeviation = temperatureDeviation; }
        public int getDurationMinutes() { return durationMinutes; }
        public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
        public boolean isAutoResolved() { return autoResolved; }
        public void setAutoResolved(boolean autoResolved) { this.autoResolved = autoResolved; }
    }

    /**
     * Temperature Reading for historical analysis
     */
    public static class TemperatureReading {
        private LocalDateTime timestamp;
        private double temperature;
        private String sensorId;
        private double batteryLevel;
        private String location;

        public TemperatureReading(LocalDateTime timestamp, double temperature, 
                                 String sensorId, double batteryLevel, String location) {
            this.timestamp = timestamp;
            this.temperature = temperature;
            this.sensorId = sensorId;
            this.batteryLevel = batteryLevel;
            this.location = location;
        }

        // Getters and setters...
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
        public String getSensorId() { return sensorId; }
        public void setSensorId(String sensorId) { this.sensorId = sensorId; }
        public double getBatteryLevel() { return batteryLevel; }
        public void setBatteryLevel(double batteryLevel) { this.batteryLevel = batteryLevel; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
    }

    /**
     * Cooling Failure Prediction with risk assessment
     */
    public static class CoolingFailurePrediction {
        private String shipmentId;
        private double riskScore; // 0.0 - 1.0
        private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL
        private LocalDateTime predictedFailureTime;
        private List<String> riskFactors;
        private double confidenceLevel;
        private String recommendation;
        private LocalDateTime lastUpdated;

        public CoolingFailurePrediction(String shipmentId) {
            this.shipmentId = shipmentId;
            this.riskScore = 0.0;
            this.riskLevel = "LOW";
            this.riskFactors = new ArrayList<>();
            this.confidenceLevel = 0.0;
            this.recommendation = "";
            this.lastUpdated = LocalDateTime.now();
        }

        // Getters and setters...
        public String getShipmentId() { return shipmentId; }
        public void setShipmentId(String shipmentId) { this.shipmentId = shipmentId; }
        public double getRiskScore() { return riskScore; }
        public void setRiskScore(double riskScore) { this.riskScore = riskScore; }
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
        public LocalDateTime getPredictedFailureTime() { return predictedFailureTime; }
        public void setPredictedFailureTime(LocalDateTime predictedFailureTime) { this.predictedFailureTime = predictedFailureTime; }
        public List<String> getRiskFactors() { return riskFactors; }
        public void setRiskFactors(List<String> riskFactors) { this.riskFactors = riskFactors; }
        public double getConfidenceLevel() { return confidenceLevel; }
        public void setConfidenceLevel(double confidenceLevel) { this.confidenceLevel = confidenceLevel; }
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
        public LocalDateTime getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    }

    /**
     * Process new temperature reading and trigger alerts if necessary
     */
    @Async
    public void processTemperatureReading(String shipmentId, double temperature, 
                                       String productType, String sensorId, 
                                       double batteryLevel, String location) {
        try {
            // Store temperature reading
            TemperatureReading reading = new TemperatureReading(
                LocalDateTime.now(), temperature, sensorId, batteryLevel, location
            );
            
            recentReadings.computeIfAbsent(shipmentId, k -> new ArrayList<>()).add(reading);
            
            // Keep only last 100 readings per shipment
            List<TemperatureReading> readings = recentReadings.get(shipmentId);
            if (readings.size() > 100) {
                readings.remove(0);
            }

            // Check temperature thresholds
            Optional<Shipment> shipment = shipmentRepository.findById(shipmentId);
            if (shipment.isPresent()) {
                checkTemperatureThresholds(shipmentId, temperature, productType, shipment.get());
                
                // Update failure prediction
                updateFailurePrediction(shipmentId, readings);
            }

        } catch (Exception e) {
            // Log error
            System.err.println("Error processing temperature reading: " + e.getMessage());
        }
    }

    /**
     * Check temperature thresholds and trigger appropriate alerts
     */
    private void checkTemperatureThresholds(String shipmentId, double temperature, 
                                         String productType, Shipment shipment) {
        double warningThreshold = getWarningThreshold(productType);
        double criticalThreshold = getCriticalThreshold(productType);
        double emergencyThreshold = getEmergencyThreshold(productType);

        AlertLevel alertLevel = null;
        String alertReason = "";

        if (temperature >= emergencyThreshold) {
            alertLevel = AlertLevel.EMERGENCY;
            alertReason = "درجة الحرارة في مستوى الطوارئ";
        } else if (temperature >= criticalThreshold) {
            alertLevel = AlertLevel.CRITICAL;
            alertReason = "درجة الحرارة في المستوى الحرج";
        } else if (temperature >= warningThreshold) {
            alertLevel = AlertLevel.WARNING;
            alertReason = "درجة الحرارة في مستوى التحذير";
        }

        if (alertLevel != null) {
            createOrUpdateAlert(shipmentId, alertLevel, temperature, 
                              warningThreshold, productType, alertReason);
        } else {
            // Check if existing alerts should be auto-resolved
            autoResolveAlerts(shipmentId, temperature);
        }
    }

    /**
     * Create new alert or update existing alert with higher severity
     */
    private void createOrUpdateAlert(String shipmentId, AlertLevel level, double temperature,
                                   double threshold, String productType, String reason) {
        SmartCoolingAlert existingAlert = activeAlerts.get(shipmentId);
        
        if (existingAlert == null || existingAlert.getLevel().getSeverity() < level.getSeverity()) {
            SmartCoolingAlert alert = new SmartCoolingAlert(shipmentId, level, temperature, 
                                                          threshold, productType, reason);
            alert.setDurationMinutes(0);
            activeAlerts.put(shipmentId, alert);
            
            // Send immediate notifications
            sendAlertNotifications(alert, false);
        } else if (existingAlert != null) {
            // Update existing alert
            existingAlert.setCurrentTemperature(temperature);
            existingAlert.setTemperatureDeviation(Math.abs(temperature - threshold));
            
            // Update duration
            long duration = ChronoUnit.MINUTES.between(existingAlert.getCreatedAt(), LocalDateTime.now());
            existingAlert.setDurationMinutes((int) duration);
        }
    }

    /**
     * Auto-resolve alerts when temperature returns to normal
     */
    private void autoResolveAlerts(String shipmentId, double temperature) {
        SmartCoolingAlert alert = activeAlerts.get(shipmentId);
        if (alert != null && !alert.isAcknowledged()) {
            double normalThreshold = getNormalThreshold(alert.getProductType());
            
            if (temperature <= normalThreshold) {
                alert.setAutoResolved(true);
                
                // Send resolution notification
                sendResolutionNotification(alert, temperature);
                
                // Remove from active alerts after 5 minutes
                CompletableFuture.delayedExecutor(5, java.util.concurrent.TimeUnit.MINUTES)
                    .execute(() -> activeAlerts.remove(shipmentId));
            }
        }
    }

    /**
     * Send alert notifications to appropriate users
     */
    @Async
    private void sendAlertNotifications(SmartCoolingAlert alert, boolean isEscalation) {
        try {
            // Get shipment details
            Optional<Shipment> shipment = shipmentRepository.findById(alert.getShipmentId());
            if (!shipment.isPresent()) return;

            Shipment shipmentData = shipment.get();
            
            // Determine notification recipients based on alert level
            List<User> recipients = getAlertRecipients(alert.getLevel(), shipmentData);
            
            // Build notification message
            String title = String.format("🌡️ %s - درجة حرارة غير طبيعية", alert.getLevel().getArabicName());
            String message = buildAlertMessage(alert, shipmentData);
            
            // Send notifications
            for (User recipient : recipients) {
                notificationService.sendNotification(
                    recipient.getId(),
                    title,
                    message,
                    "COLD_CHAIN",
                    alert.getLevel().getSeverity() == 3 ? "EMERGENCY" : 
                    alert.getLevel().getSeverity() == 2 ? "CRITICAL" : "WARNING",
                    Map.of(
                        "alertId", alert.getAlertId(),
                        "shipmentId", alert.getShipmentId(),
                        "temperature", String.valueOf(alert.getCurrentTemperature()),
                        "threshold", String.valueOf(alert.getThresholdTemperature()),
                        "level", alert.getLevel().name(),
                        "isEscalation", String.valueOf(isEscalation)
                    )
                );
                
                alert.getNotifiedUsers().add(recipient.getId());
            }

        } catch (Exception e) {
            System.err.println("Error sending alert notifications: " + e.getMessage());
        }
    }

    /**
     * Send resolution notification
     */
    @Async
    private void sendResolutionNotification(SmartCoolingAlert alert, double normalTemperature) {
        try {
            String title = "✅ تم حل مشكلة درجة الحرارة";
            String message = String.format(
                "تم عودة درجة الحرارة للشحنة %s إلى المستوى الطبيعي (%.1f°C). " +
                "التنبيه الذي تم إرساله في %s تم حله تلقائياً.",
                alert.getShipmentId(),
                normalTemperature,
                alert.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
            );

            // Send to all previously notified users
            for (String userId : alert.getNotifiedUsers()) {
                notificationService.sendNotification(
                    userId,
                    title,
                    message,
                    "COLD_CHAIN",
                    "INFO",
                    Map.of(
                        "alertId", alert.getAlertId(),
                        "shipmentId", alert.getShipmentId(),
                        "resolvedTemperature", String.valueOf(normalTemperature),
                        "autoResolved", "true"
                    )
                );
            }

        } catch (Exception e) {
            System.err.println("Error sending resolution notification: " + e.getMessage());
        }
    }

    /**
     * Get alert recipients based on alert level and shipment
     */
    private List<User> getAlertRecipients(AlertLevel level, Shipment shipment) {
        List<User> recipients = new ArrayList<>();
        
        // Always notify driver
        if (shipment.getDriverId() != null) {
            userRepository.findById(shipment.getDriverId()).ifPresent(recipients::add);
        }
        
        // Notify customer
        if (shipment.getCustomerId() != null) {
            userRepository.findById(shipment.getCustomerId()).ifPresent(recipients::add);
        }
        
        // For higher severity alerts, notify supervisors and admins
        if (level.getSeverity() >= AlertLevel.CRITICAL.getSeverity()) {
            userRepository.findByRole("SUPERVISOR").forEach(recipients::add);
        }
        
        if (level.getSeverity() >= AlertLevel.EMERGENCY.getSeverity()) {
            userRepository.findByRole("ADMIN").forEach(recipients::add);
        }
        
        return recipients;
    }

    /**
     * Build comprehensive alert message
     */
    private String buildAlertMessage(SmartCoolingAlert alert, Shipment shipment) {
        StringBuilder message = new StringBuilder();
        
        message.append(String.format("الشحنة: %s\n", shipment.getId()));
        message.append(String.format("المنتج: %s\n", alert.getProductType()));
        message.append(String.format("درجة الحرارة الحالية: %.1f°C\n", alert.getCurrentTemperature()));
        message.append(String.format("الحد الأدنى المسموح: %.1f°C\n", alert.getThresholdTemperature()));
        message.append(String.format("انحراف درجة الحرارة: %.1f°C\n", alert.getTemperatureDeviation()));
        
        if (alert.getDurationMinutes() > 0) {
            message.append(String.format("المدة: %d دقيقة\n", alert.getDurationMinutes()));
        }
        
        message.append(String.format("\n%s\n", alert.getAlertReason()));
        
        // Add recommendations based on alert level
        message.append("\nالتوصيات:\n");
        switch (alert.getLevel()) {
            case WARNING:
                message.append("• راقب درجة الحرارة عن كثب\n");
                message.append("• تحقق من عمل نظام التبريد\n");
                break;
            case CRITICAL:
                message.append("• اتخذ إجراء فوري لتبريد الشحنة\n");
                message.append("• أبلغ المشرف عن الموقف\n");
                message.append("• جهز خطة طوارئ\n");
                break;
            case EMERGENCY:
                message.append("• استدعاء فريق الطوارئ فوراً\n");
                message.append("• نقل المنتجات إلى مكان بارد\n");
                message.append("• توثيق جميع الإجراءات المتخذة\n");
                break;
        }
        
        return message.toString();
    }

    /**
     * Update cooling failure prediction using machine learning approach
     */
    private void updateFailurePrediction(String shipmentId, List<TemperatureReading> readings) {
        if (readings.size() < 10) return; // Need sufficient data for prediction

        CoolingFailurePrediction prediction = failurePredictions.computeIfAbsent(
            shipmentId, CoolingFailurePrediction::new
        );

        // Analyze temperature trends
        List<Double> temperatures = readings.stream()
            .map(TemperatureReading::getTemperature)
            .collect(Collectors.toList());

        // Calculate risk factors
        List<String> riskFactors = new ArrayList<>();
        double riskScore = 0.0;

        // Factor 1: Temperature trend analysis
        double trend = calculateTemperatureTrend(temperatures);
        if (trend > 0.5) {
            riskScore += 0.3;
            riskFactors.add("ارتفاع مستمر في درجة الحرارة");
        }

        // Factor 2: Temperature volatility
        double volatility = calculateTemperatureVolatility(temperatures);
        if (volatility > 2.0) {
            riskScore += 0.2;
            riskFactors.add("تقلبات شديدة في درجة الحرارة");
        }

        // Factor 3: Sensor battery level
        double avgBatteryLevel = readings.stream()
            .mapToDouble(TemperatureReading::getBatteryLevel)
            .average()
            .orElse(100.0);
        
        if (avgBatteryLevel < 20) {
            riskScore += 0.25;
            riskFactors.add("بطارية المستشعر منخفضة");
        }

        // Factor 4: Recent alerts
        SmartCoolingAlert alert = activeAlerts.get(shipmentId);
        if (alert != null && alert.getLevel().getSeverity() >= AlertLevel.CRITICAL.getSeverity()) {
            riskScore += 0.25;
            riskFactors.add("تنبيهات حرجة نشطة");
        }

        // Update prediction
        prediction.setRiskScore(Math.min(riskScore, 1.0));
        prediction.setRiskFactors(riskFactors);
        prediction.setLastUpdated(LocalDateTime.now());

        // Determine risk level
        if (riskScore >= 0.8) {
            prediction.setRiskLevel("CRITICAL");
            prediction.setRecommendation("اتخذ إجراء فوري لمنع فشل التبريد");
            prediction.setPredictedFailureTime(LocalDateTime.now().plusHours(2));
        } else if (riskScore >= 0.6) {
            prediction.setRiskLevel("HIGH");
            prediction.setRecommendation("راقب النظام عن كثب واستعد للإجراءات الوقائية");
            prediction.setPredictedFailureTime(LocalDateTime.now().plusHours(6));
        } else if (riskScore >= 0.3) {
            prediction.setRiskLevel("MEDIUM");
            prediction.setRecommendation("تحقق من نظام التبريد وصيانته");
            prediction.setPredictedFailureTime(LocalDateTime.now().plusHours(12));
        } else {
            prediction.setRiskLevel("LOW");
            prediction.setRecommendation("الوضع طبيعي، استمر في المراقبة");
        }

        // Calculate confidence based on data quality
        prediction.setConfidenceLevel(Math.min(readings.size() / 50.0, 1.0));
    }

    /**
     * Calculate temperature trend (positive means increasing)
     */
    private double calculateTemperatureTrend(List<Double> temperatures) {
        if (temperatures.size() < 2) return 0.0;

        // Simple linear regression
        double n = temperatures.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        for (int i = 0; i < temperatures.size(); i++) {
            sumX += i;
            sumY += temperatures.get(i);
            sumXY += i * temperatures.get(i);
            sumX2 += i * i;
        }

        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        return slope;
    }

    /**
     * Calculate temperature volatility (standard deviation)
     */
    private double calculateTemperatureVolatility(List<Double> temperatures) {
        if (temperatures.size() < 2) return 0.0;

        double mean = temperatures.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = temperatures.stream()
            .mapToDouble(temp -> Math.pow(temp - mean, 2))
            .average()
            .orElse(0.0);

        return Math.sqrt(variance);
    }

    /**
     * Scheduled task to check for alert escalations
     */
    @Scheduled(fixedRate = 60000) // Every minute
    public void checkAlertEscalations() {
        LocalDateTime now = LocalDateTime.now();
        
        activeAlerts.values().stream()
            .filter(alert -> !alert.isAcknowledged() && !alert.isAutoResolved())
            .forEach(alert -> {
                long minutesSinceCreation = ChronoUnit.MINUTES.between(alert.getCreatedAt(), now);
                long minutesSinceLastEscalation = alert.getLastEscalated() != null ? 
                    ChronoUnit.MINUTES.between(alert.getLastEscalated(), now) : minutesSinceCreation;

                if (minutesSinceLastEscalation >= alert.getLevel().getEscalationMinutes()) {
                    escalateAlert(alert);
                }
            });
    }

    /**
     * Escalate alert to higher level users
     */
    private void escalateAlert(SmartCoolingAlert alert) {
        alert.setEscalationCount(alert.getEscalationCount() + 1);
        alert.setLastEscalated(LocalDateTime.now());

        // Escalate to higher level if needed
        if (alert.getEscalationCount() >= 2 && alert.getLevel() != AlertLevel.EMERGENCY) {
            // Upgrade alert level
            AlertLevel newLevel = alert.getLevel() == AlertLevel.WARNING ? 
                AlertLevel.CRITICAL : AlertLevel.EMERGENCY;
            alert.setLevel(newLevel);
        }

        // Send escalation notifications
        sendAlertNotifications(alert, true);

        // Log escalation
        System.out.println(String.format("Alert %s escalated to level %s (escalation count: %d)",
            alert.getAlertId(), alert.getLevel().name(), alert.getEscalationCount()));
    }

    /**
     * Get temperature thresholds based on product type
     */
    private double getWarningThreshold(String productType) {
        switch (productType.toUpperCase()) {
            case "FROZEN": return -15.0;
            case "REFRIGERATED": return 8.0;
            case "AMBIENT": return 25.0;
            case "PHARMACEUTICAL": return 8.0;
            default: return 25.0;
        }
    }

    private double getCriticalThreshold(String productType) {
        switch (productType.toUpperCase()) {
            case "FROZEN": return -12.0;
            case "REFRIGERATED": return 10.0;
            case "AMBIENT": return 30.0;
            case "PHARMACEUTICAL": return 12.0;
            default: return 30.0;
        }
    }

    private double getEmergencyThreshold(String productType) {
        switch (productType.toUpperCase()) {
            case "FROZEN": return -10.0;
            case "REFRIGERATED": return 15.0;
            case "AMBIENT": return 35.0;
            case "PHARMACEUTICAL": return 15.0;
            default: return 35.0;
        }
    }

    private double getNormalThreshold(String productType) {
        switch (productType.toUpperCase()) {
            case "FROZEN": return -18.0;
            case "REFRIGERATED": return 4.0;
            case "AMBIENT": return 20.0;
            case "PHARMACEUTICAL": return 5.0;
            default: return 20.0;
        }
    }

    // Public API methods
    public List<SmartCoolingAlert> getActiveAlerts() {
        return new ArrayList<>(activeAlerts.values());
    }

    public SmartCoolingAlert getAlert(String alertId) {
        return activeAlerts.values().stream()
            .filter(alert -> alert.getAlertId().equals(alertId))
            .findFirst()
            .orElse(null);
    }

    public void acknowledgeAlert(String alertId, String userId) {
        SmartCoolingAlert alert = activeAlerts.values().stream()
            .filter(a -> a.getAlertId().equals(alertId))
            .findFirst()
            .orElse(null);

        if (alert != null) {
            alert.setAcknowledged(true);
            alert.setAcknowledgedBy(userId);
            alert.setAcknowledgedAt(LocalDateTime.now());

            // Send acknowledgment notification
            sendAcknowledgmentNotification(alert, userId);
        }
    }

    @Async
    private void sendAcknowledgmentNotification(SmartCoolingAlert alert, String userId) {
        try {
            String title = "✅ تم الاعتراف بالتنبيه";
            String message = String.format(
                "قام المستخدم %s بالاعتراف بتنبيه درجة الحرارة للشحنة %s",
                userId, alert.getShipmentId()
            );

            // Notify all previously notified users about acknowledgment
            for (String notifiedUserId : alert.getNotifiedUsers()) {
                if (!notifiedUserId.equals(userId)) {
                    notificationService.sendNotification(
                        notifiedUserId,
                        title,
                        message,
                        "COLD_CHAIN",
                        "INFO",
                        Map.of(
                            "alertId", alert.getAlertId(),
                            "shipmentId", alert.getShipmentId(),
                            "acknowledgedBy", userId
                        )
                    );
                }
            }

        } catch (Exception e) {
            System.err.println("Error sending acknowledgment notification: " + e.getMessage());
        }
    }

    public List<TemperatureReading> getTemperatureHistory(String shipmentId, int hours) {
        List<TemperatureReading> readings = recentReadings.getOrDefault(shipmentId, new ArrayList<>());
        LocalDateTime cutoff = LocalDateTime.now().minusHours(hours);
        
        return readings.stream()
            .filter(reading -> reading.getTimestamp().isAfter(cutoff))
            .sorted((r1, r2) -> r2.getTimestamp().compareTo(r1.getTimestamp()))
            .collect(Collectors.toList());
    }

    public CoolingFailurePrediction getFailurePrediction(String shipmentId) {
        return failurePredictions.get(shipmentId);
    }

    public Map<String, Object> getAlertStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalActiveAlerts", activeAlerts.size());
        stats.put("warningAlerts", activeAlerts.values().stream()
            .filter(alert -> alert.getLevel() == AlertLevel.WARNING).count());
        stats.put("criticalAlerts", activeAlerts.values().stream()
            .filter(alert -> alert.getLevel() == AlertLevel.CRITICAL).count());
        stats.put("emergencyAlerts", activeAlerts.values().stream()
            .filter(alert -> alert.getLevel() == AlertLevel.EMERGENCY).count());
        stats.put("acknowledgedAlerts", activeAlerts.values().stream()
            .filter(SmartCoolingAlert::isAcknowledged).count());
        stats.put("autoResolvedAlerts", activeAlerts.values().stream()
            .filter(SmartCoolingAlert::isAutoResolved).count());
        
        // Risk level distribution
        Map<String, Long> riskDistribution = failurePredictions.values().stream()
            .collect(Collectors.groupingBy(
                CoolingFailurePrediction::getRiskLevel,
                Collectors.counting()
            ));
        stats.put("riskLevelDistribution", riskDistribution);
        
        return stats;
    }
}
