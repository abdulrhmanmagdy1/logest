package com.edham.logistics.workflow;

import com.edham.logistics.dto.*;
import com.edham.logistics.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Real-time workflow updates for enterprise-grade system
 * Provides WebSocket-based real-time UI updates for shipment workflow changes
 */
@Slf4j
@Component
public class RealTimeWorkflowUpdates {

    private final SimpMessagingTemplate messagingTemplate;
    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;
    private final Map<String, Set<String>> userSubscriptions = new ConcurrentHashMap<>();
    private final Map<String, WorkflowUpdate> lastUpdates = new ConcurrentHashMap<>();

    @Autowired
    public RealTimeWorkflowUpdates(SimpMessagingTemplate messagingTemplate,
                                   ShipmentRepository shipmentRepository,
                                   UserRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.shipmentRepository = shipmentRepository;
        this.userRepository = userRepository;
    }

    /**
     * Send workflow state update to subscribed users
     */
    public void sendWorkflowStateUpdate(Long shipmentId, ShipmentState fromState, 
                                        ShipmentState toState, String userId, String reason) {
        try {
            log.debug("Sending workflow state update for shipment: {}", shipmentId);

            // Get shipment details
            Optional<Shipment> shipmentOpt = shipmentRepository.findById(shipmentId);
            if (shipmentOpt.isEmpty()) {
                log.warn("Shipment not found for workflow update: {}", shipmentId);
                return;
            }

            Shipment shipment = shipmentOpt.get();

            // Create workflow update
            WorkflowUpdate update = WorkflowUpdate.builder()
                    .updateId(UUID.randomUUID().toString())
                    .shipmentId(shipmentId)
                    .trackingNumber(shipment.getTrackingNumber())
                    .fromState(fromState)
                    .toState(toState)
                    .userId(userId)
                    .reason(reason)
                    .timestamp(LocalDateTime.now())
                    .updateType(WorkflowUpdateType.STATE_TRANSITION)
                    .build();

            // Store last update
            lastUpdates.put(shipmentId.toString(), update);

            // Send to different channels based on state
            sendToAppropriateChannels(update, shipment);

        } catch (Exception e) {
            log.error("Error sending workflow state update: {}", e.getMessage(), e);
        }
    }

    /**
     * Send workflow initialization update
     */
    public void sendWorkflowInitialization(Long shipmentId, ShipmentState initialState) {
        try {
            log.debug("Sending workflow initialization for shipment: {}", shipmentId);

            // Get shipment details
            Optional<Shipment> shipmentOpt = shipmentRepository.findById(shipmentId);
            if (shipmentOpt.isEmpty()) {
                log.warn("Shipment not found for workflow initialization: {}", shipmentId);
                return;
            }

            Shipment shipment = shipmentOpt.get();

            // Create initialization update
            WorkflowUpdate update = WorkflowUpdate.builder()
                    .updateId(UUID.randomUUID().toString())
                    .shipmentId(shipmentId)
                    .trackingNumber(shipment.getTrackingNumber())
                    .fromState(initialState)
                    .toState(initialState)
                    .userId("SYSTEM")
                    .reason("Workflow initialized")
                    .timestamp(LocalDateTime.now())
                    .updateType(WorkflowUpdateType.WORKFLOW_INITIALIZED)
                    .build();

            // Store last update
            lastUpdates.put(shipmentId.toString(), update);

            // Send to all relevant channels
            sendToAllRelevantChannels(update, shipment);

        } catch (Exception e) {
            log.error("Error sending workflow initialization: {}", e.getMessage(), e);
        }
    }

    /**
     * Send workflow completion update
     */
    public void sendWorkflowCompletion(Long shipmentId, ShipmentState finalState) {
        try {
            log.debug("Sending workflow completion for shipment: {}", shipmentId);

            // Get shipment details
            Optional<Shipment> shipmentOpt = shipmentRepository.findById(shipmentId);
            if (shipmentOpt.isEmpty()) {
                log.warn("Shipment not found for workflow completion: {}", shipmentId);
                return;
            }

            Shipment shipment = shipmentOpt.get();

            // Create completion update
            WorkflowUpdate update = WorkflowUpdate.builder()
                    .updateId(UUID.randomUUID().toString())
                    .shipmentId(shipmentId)
                    .trackingNumber(shipment.getTrackingNumber())
                    .fromState(finalState)
                    .toState(finalState)
                    .userId("SYSTEM")
                    .reason("Workflow completed")
                    .timestamp(LocalDateTime.now())
                    .updateType(WorkflowUpdateType.WORKFLOW_COMPLETED)
                    .build();

            // Store last update
            lastUpdates.put(shipmentId.toString(), update);

            // Send to all relevant channels
            sendToAllRelevantChannels(update, shipment);

        } catch (Exception e) {
            log.error("Error sending workflow completion: {}", e.getMessage(), e);
        }
    }

    /**
     * Send real-time location update
     */
    public void sendLocationUpdate(Long shipmentId, LocationDTO location, String userId) {
        try {
            log.debug("Sending location update for shipment: {}", shipmentId);

            // Get shipment details
            Optional<Shipment> shipmentOpt = shipmentRepository.findById(shipmentId);
            if (shipmentOpt.isEmpty()) {
                log.warn("Shipment not found for location update: {}", shipmentId);
                return;
            }

            Shipment shipment = shipmentOpt.get();

            // Create location update
            WorkflowUpdate update = WorkflowUpdate.builder()
                    .updateId(UUID.randomUUID().toString())
                    .shipmentId(shipmentId)
                    .trackingNumber(shipment.getTrackingNumber())
                    .userId(userId)
                    .reason("Location update")
                    .timestamp(LocalDateTime.now())
                    .updateType(WorkflowUpdateType.LOCATION_UPDATE)
                    .location(location)
                    .build();

            // Send to tracking channel
            messagingTemplate.convertAndSend("/topic/shipment-tracking/" + shipmentId, update);

            // Send to supervisor channel
            messagingTemplate.convertAndSend("/topic/supervisor-dashboard", update);

        } catch (Exception e) {
            log.error("Error sending location update: {}", e.getMessage(), e);
        }
    }

    /**
     * Send delay alert
     */
    public void sendDelayAlert(Long shipmentId, String delayReason, Duration estimatedDelay) {
        try {
            log.debug("Sending delay alert for shipment: {}", shipmentId);

            // Get shipment details
            Optional<Shipment> shipmentOpt = shipmentRepository.findById(shipmentId);
            if (shipmentOpt.isEmpty()) {
                log.warn("Shipment not found for delay alert: {}", shipmentId);
                return;
            }

            Shipment shipment = shipmentOpt.get();

            // Create delay alert
            WorkflowUpdate update = WorkflowUpdate.builder()
                    .updateId(UUID.randomUUID().toString())
                    .shipmentId(shipmentId)
                    .trackingNumber(shipment.getTrackingNumber())
                    .userId("SYSTEM")
                    .reason(delayReason)
                    .timestamp(LocalDateTime.now())
                    .updateType(WorkflowUpdateType.DELAY_ALERT)
                    .estimatedDelay(estimatedDelay)
                    .priority(AlertPriority.HIGH)
                    .build();

            // Send to alert channels
            messagingTemplate.convertAndSend("/topic/delay-alerts", update);
            messagingTemplate.convertAndSend("/topic/supervisor-dashboard", update);

            // Send to customer
            if (shipment.getCustomerId() != null) {
                messagingTemplate.convertAndSend("/topic/customer/" + shipment.getCustomerId(), update);
            }

        } catch (Exception e) {
            log.error("Error sending delay alert: {}", e.getMessage(), e);
        }
    }

    /**
     * Send emergency notification
     */
    public void sendEmergencyNotification(Long shipmentId, String emergencyType, String description) {
        try {
            log.debug("Sending emergency notification for shipment: {}", shipmentId);

            // Get shipment details
            Optional<Shipment> shipmentOpt = shipmentRepository.findById(shipmentId);
            if (shipmentOpt.isEmpty()) {
                log.warn("Shipment not found for emergency notification: {}", shipmentId);
                return;
            }

            Shipment shipment = shipmentOpt.get();

            // Create emergency notification
            WorkflowUpdate update = WorkflowUpdate.builder()
                    .updateId(UUID.randomUUID().toString())
                    .shipmentId(shipmentId)
                    .trackingNumber(shipment.getTrackingNumber())
                    .userId("SYSTEM")
                    .reason(description)
                    .timestamp(LocalDateTime.now())
                    .updateType(WorkflowUpdateType.EMERGENCY_ALERT)
                    .emergencyType(emergencyType)
                    .priority(AlertPriority.CRITICAL)
                    .build();

            // Send to all emergency channels
            messagingTemplate.convertAndSend("/topic/emergency-alerts", update);
            messagingTemplate.convertAndSend("/topic/supervisor-dashboard", update);
            messagingTemplate.convertAndSend("/topic/admin-dashboard", update);

        } catch (Exception e) {
            log.error("Error sending emergency notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Send performance metrics update
     */
    public void sendPerformanceMetrics(Map<String, Object> metrics) {
        try {
            log.debug("Sending performance metrics update");

            // Create metrics update
            WorkflowUpdate update = WorkflowUpdate.builder()
                    .updateId(UUID.randomUUID().toString())
                    .userId("SYSTEM")
                    .reason("Performance metrics update")
                    .timestamp(LocalDateTime.now())
                    .updateType(WorkflowUpdateType.PERFORMANCE_METRICS)
                    .metrics(metrics)
                    .build();

            // Send to admin and supervisor channels
            messagingTemplate.convertAndSend("/topic/performance-metrics", update);
            messagingTemplate.convertAndSend("/topic/admin-dashboard", update);

        } catch (Exception e) {
            log.error("Error sending performance metrics: {}", e.getMessage(), e);
        }
    }

    /**
     * Send user to specific channels based on update type
     */
    private void sendToAppropriateChannels(WorkflowUpdate update, Shipment shipment) {
        try {
            // Always send to shipment-specific channel
            messagingTemplate.convertAndSend("/topic/shipment/" + shipment.getId(), update);

            // Send to customer channel
            if (shipment.getCustomerId() != null) {
                messagingTemplate.convertAndSend("/topic/customer/" + shipment.getCustomerId(), update);
            }

            // Send to driver channel
            if (shipment.getDriverId() != null) {
                messagingTemplate.convertAndSend("/topic/driver/" + shipment.getDriverId(), update);
            }

            // Send to supervisor dashboard
            messagingTemplate.convertAndSend("/topic/supervisor-dashboard", update);

            // Send to admin dashboard for critical updates
            if (update.getPriority() == AlertPriority.HIGH || update.getPriority() == AlertPriority.CRITICAL) {
                messagingTemplate.convertAndSend("/topic/admin-dashboard", update);
            }

        } catch (Exception e) {
            log.error("Error sending to appropriate channels: {}", e.getMessage(), e);
        }
    }

    /**
     * Send to all relevant channels
     */
    private void sendToAllRelevantChannels(WorkflowUpdate update, Shipment shipment) {
        try {
            // Send to all shipment-related channels
            sendToAppropriateChannels(update, shipment);

            // Send to general dashboard channels
            messagingTemplate.convertAndSend("/topic/all-shipments", update);
            messagingTemplate.convertAndSend("/topic/workflow-updates", update);

        } catch (Exception e) {
            log.error("Error sending to all relevant channels: {}", e.getMessage(), e);
        }
    }

    /**
     * Subscribe user to shipment updates
     */
    public void subscribeUserToShipment(String userId, Long shipmentId) {
        try {
            log.debug("Subscribing user {} to shipment {}", userId, shipmentId);

            userSubscriptions.computeIfAbsent(userId, k -> new HashSet<>()).add(shipmentId.toString());

            // Send last update if available
            WorkflowUpdate lastUpdate = lastUpdates.get(shipmentId.toString());
            if (lastUpdate != null) {
                messagingTemplate.convertAndSend("/topic/user/" + userId, lastUpdate);
            }

        } catch (Exception e) {
            log.error("Error subscribing user to shipment: {}", e.getMessage(), e);
        }
    }

    /**
     * Unsubscribe user from shipment updates
     */
    public void unsubscribeUserFromShipment(String userId, Long shipmentId) {
        try {
            log.debug("Unsubscribing user {} from shipment {}", userId, shipmentId);

            Set<String> subscriptions = userSubscriptions.get(userId);
            if (subscriptions != null) {
                subscriptions.remove(shipmentId.toString());
                if (subscriptions.isEmpty()) {
                    userSubscriptions.remove(userId);
                }
            }

        } catch (Exception e) {
            log.error("Error unsubscribing user from shipment: {}", e.getMessage(), e);
        }
    }

    /**
     * Get user subscriptions
     */
    public Set<String> getUserSubscriptions(String userId) {
        return userSubscriptions.getOrDefault(userId, new HashSet<>());
    }

    /**
     * Send heartbeat to keep connections alive
     */
    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void sendHeartbeat() {
        try {
            WorkflowUpdate heartbeat = WorkflowUpdate.builder()
                    .updateId(UUID.randomUUID().toString())
                    .userId("SYSTEM")
                    .reason("Heartbeat")
                    .timestamp(LocalDateTime.now())
                    .updateType(WorkflowUpdateType.HEARTBEAT)
                    .build();

            messagingTemplate.convertAndSend("/topic/heartbeat", heartbeat);

        } catch (Exception e) {
            log.error("Error sending heartbeat: {}", e.getMessage(), e);
        }
    }

    /**
     * Cleanup old updates
     */
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void cleanupOldUpdates() {
        try {
            LocalDateTime cutoff = LocalDateTime.now().minusHours(1);
            
            lastUpdates.entrySet().removeIf(entry -> {
                WorkflowUpdate update = entry.getValue();
                return update.getTimestamp().isBefore(cutoff);
            });

            log.debug("Cleaned up old workflow updates");

        } catch (Exception e) {
            log.error("Error cleaning up old updates: {}", e.getMessage(), e);
        }
    }

    /**
     * Get workflow statistics for real-time dashboard
     */
    public Map<String, Object> getWorkflowStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            // Count active workflows by state
            Map<ShipmentState, Long> stateCounts = new HashMap<>();
            for (WorkflowUpdate update : lastUpdates.values()) {
                if (update.getToState() != null) {
                    stateCounts.merge(update.getToState(), 1L, Long::sum);
                }
            }
            
            statistics.put("activeWorkflows", lastUpdates.size());
            statistics.put("stateDistribution", stateCounts);
            statistics.put("totalSubscriptions", userSubscriptions.values().stream().mapToInt(Set::size).sum());
            statistics.put("lastUpdated", LocalDateTime.now());
            
            return statistics;

        } catch (Exception e) {
            log.error("Error getting workflow statistics: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    // Data classes
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class WorkflowUpdate {
        private String updateId;
        private Long shipmentId;
        private String trackingNumber;
        private ShipmentState fromState;
        private ShipmentState toState;
        private String userId;
        private String reason;
        private LocalDateTime timestamp;
        private WorkflowUpdateType updateType;
        private LocationDTO location;
        private Duration estimatedDelay;
        private AlertPriority priority;
        private String emergencyType;
        private Map<String, Object> metrics;
    }

    public enum WorkflowUpdateType {
        STATE_TRANSITION,
        WORKFLOW_INITIALIZED,
        WORKFLOW_COMPLETED,
        LOCATION_UPDATE,
        DELAY_ALERT,
        EMERGENCY_ALERT,
        PERFORMANCE_METRICS,
        HEARTBEAT
    }

    public enum AlertPriority {
        LOW, MEDIUM, HIGH, CRITICAL
    }
}
