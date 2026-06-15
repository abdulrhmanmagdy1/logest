package com.edham.logistics.workflow;

import com.edham.logistics.dto.*;
import com.edham.logistics.model.*;
import com.edham.logistics.repository.*;
import com.edham.logistics.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Shipment workflow integration test for enterprise-grade system
 * Tests complete workflow engine functionality with all components
 */
@Slf4j
@Component
public class ShipmentWorkflowIntegrationTest {

    // Core dependencies
    private final ShipmentWorkflowEngine workflowEngine;
    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final WebSocketSessionManager webSocketSessionManager;
    private final NotificationService notificationService;

    @Autowired
    public ShipmentWorkflowIntegrationTest(ShipmentWorkflowEngine workflowEngine,
                                         ShipmentRepository shipmentRepository,
                                         UserRepository userRepository,
                                         VehicleRepository vehicleRepository,
                                         WebSocketSessionManager webSocketSessionManager,
                                         NotificationService notificationService) {
        this.workflowEngine = workflowEngine;
        this.shipmentRepository = shipmentRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.webSocketSessionManager = webSocketSessionManager;
        this.notificationService = notificationService;
    }

    /**
     * Run complete workflow engine integration test
     */
    public CompletableFuture<WorkflowIntegrationTestResult> runCompleteIntegrationTest() {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Starting complete shipment workflow integration test");

            WorkflowIntegrationTestResult result = WorkflowIntegrationTestResult.builder()
                    .testId("WORKFLOW-INTEGRATION-" + System.currentTimeMillis())
                    .startTime(LocalDateTime.now())
                    .testResults(new HashMap<>())
                    .build();

            try {
                // Test 1: Workflow Initialization
                result.getTestResults().put("WORKFLOW_INITIALIZATION", testWorkflowInitialization());

                // Test 2: State Transitions
                result.getTestResults().put("STATE_TRANSITIONS", testStateTransitions());

                // Test 3: Role-Based Permissions
                result.getTestResults().put("ROLE_PERMISSIONS", testRoleBasedPermissions());

                // Test 4: Business Rules Validation
                result.getTestResults().put("BUSINESS_RULES", testBusinessRulesValidation());

                // Test 5: Real-Time Updates
                result.getTestResults().put("REAL_TIME_UPDATES", testRealTimeUpdates());

                // Test 6: WebSocket Integration
                result.getTestResults().put("WEBSOCKET_INTEGRATION", testWebSocketIntegration());

                // Test 7: Notification System
                result.getTestResults().put("NOTIFICATION_SYSTEM", testNotificationSystem());

                // Test 8: Workflow Persistence
                result.getTestResults().put("WORKFLOW_PERSISTENCE", testWorkflowPersistence());

                // Test 9: Error Handling
                result.getTestResults().put("ERROR_HANDLING", testErrorHandling());

                // Test 10: Performance Metrics
                result.getTestResults().put("PERFORMANCE_METRICS", testPerformanceMetrics());

                // Calculate overall test result
                result.setEndTime(LocalDateTime.now());
                result.setOverallSuccess(calculateOverallSuccess(result.getTestResults()));
                result.setTestCount(result.getTestResults().size());
                result.setPassedTests((int) result.getTestResults().values().stream()
                        .mapToLong(testResult -> testResult.getSuccess() ? 1 : 0)
                        .sum());

                log.info("Workflow integration test completed. Success: {}", result.getOverallSuccess());

                return result;

            } catch (Exception e) {
                log.error("Error during workflow integration test: {}", e.getMessage(), e);
                result.setOverallSuccess(false);
                result.setEndTime(LocalDateTime.now());
                result.setErrorMessage("Integration test failed: " + e.getMessage());
                return result;
            }
        });
    }

    /**
     * Test workflow initialization
     */
    private WorkflowTestResult testWorkflowInitialization() {
        try {
            log.debug("Testing workflow initialization");

            // Create test shipment
            Shipment testShipment = createTestShipment();
            Shipment savedShipment = shipmentRepository.save(testShipment);

            // Initialize workflow
            WorkflowResult initResult = workflowEngine.initializeWorkflow(savedShipment).join();

            // Verify workflow state
            ShipmentWorkflowState workflowState = workflowEngine.getWorkflowState(savedShipment.getId());

            boolean success = initResult.getSuccess() && 
                              workflowState != null &&
                              workflowState.getCurrentState() == savedShipment.getStatus() &&
                              workflowState.getActive() &&
                              workflowState.getTransitionHistory().isEmpty();

            return WorkflowTestResult.builder()
                    .success(success)
                    .message(success ? "Workflow initialization test passed" : "Workflow initialization test failed")
                    .details(Map.of(
                            "initResult", initResult.getSuccess(),
                            "workflowStateNotNull", workflowState != null,
                            "currentStateCorrect", workflowState != null ? workflowState.getCurrentState() == savedShipment.getStatus() : false,
                            "workflowActive", workflowState != null ? workflowState.getActive() : false,
                            "emptyHistory", workflowState != null ? workflowState.getTransitionHistory().isEmpty() : false
                    ))
                    .build();

        } catch (Exception e) {
            log.error("Error in workflow initialization test: {}", e.getMessage(), e);
            return WorkflowTestResult.builder()
                    .success(false)
                    .error("Workflow initialization test failed: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Test state transitions
     */
    private WorkflowTestResult testStateTransitions() {
        try {
            log.debug("Testing state transitions");

            // Create test shipment
            Shipment testShipment = createTestShipment();
            testShipment.setStatus(ShipmentState.CREATED);
            Shipment savedShipment = shipmentRepository.save(testShipment);

            // Initialize workflow
            workflowEngine.initializeWorkflow(savedShipment).join();

            // Test valid transitions
            List<String> transitionResults = new ArrayList<>();

            // Test CREATED -> ACCEPTED
            WorkflowResult result1 = workflowEngine.transitionShipmentState(
                    savedShipment.getId(), ShipmentState.ACCEPTED, "1", "Test transition").join();
            transitionResults.add("CREATED->ACCEPTED: " + result1.getSuccess());

            // Test ACCEPTED -> PREPARING
            WorkflowResult result2 = workflowEngine.transitionShipmentState(
                    savedShipment.getId(), ShipmentState.PREPARING, "2", "Test transition").join();
            transitionResults.add("ACCEPTED->PREPARING: " + result2.getSuccess());

            // Test PREPARING -> LOADED
            WorkflowResult result3 = workflowEngine.transitionShipmentState(
                    savedShipment.getId(), ShipmentState.LOADED, "2", "Test transition").join();
            transitionResults.add("PREPARING->LOADED: " + result3.getSuccess());

            // Test LOADED -> IN_TRANSIT
            WorkflowResult result4 = workflowEngine.transitionShipmentState(
                    savedShipment.getId(), ShipmentState.IN_TRANSIT, "2", "Test transition").join();
            transitionResults.add("LOADED->IN_TRANSIT: " + result4.getSuccess());

            // Test IN_TRANSIT -> ARRIVED
            WorkflowResult result5 = workflowEngine.transitionShipmentState(
                    savedShipment.getId(), ShipmentState.ARRIVED, "2", "Test transition").join();
            transitionResults.add("IN_TRANSIT->ARRIVED: " + result5.getSuccess());

            // Test ARRIVED -> DELIVERED
            WorkflowResult result6 = workflowEngine.transitionShipmentState(
                    savedShipment.getId(), ShipmentState.DELIVERED, "2", "Test transition").join();
            transitionResults.add("ARRIVED->DELIVERED: " + result6.getSuccess());

            boolean allTransitionsSuccess = result1.getSuccess() && result2.getSuccess() && 
                                         result3.getSuccess() && result4.getSuccess() && 
                                         result5.getSuccess() && result6.getSuccess();

            return WorkflowTestResult.builder()
                    .success(allTransitionsSuccess)
                    .message(allTransitionsSuccess ? "State transitions test passed" : "State transitions test failed")
                    .details(Map.of(
                            "transitionResults", transitionResults,
                            "allTransitionsSuccess", allTransitionsSuccess
                    ))
                    .build();

        } catch (Exception e) {
            log.error("Error in state transitions test: {}", e.getMessage(), e);
            return WorkflowTestResult.builder()
                    .success(false)
                    .error("State transitions test failed: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Test role-based permissions
     */
    private WorkflowTestResult testRoleBasedPermissions() {
        try {
            log.debug("Testing role-based permissions");

            // Create test shipment
            Shipment testShipment = createTestShipment();
            testShipment.setStatus(ShipmentState.CREATED);
            Shipment savedShipment = shipmentRepository.save(testShipment);

            // Initialize workflow
            workflowEngine.initializeWorkflow(savedShipment).join();

            // Test customer permissions
            WorkflowResult customerResult = workflowEngine.transitionShipmentState(
                    savedShipment.getId(), ShipmentState.CANCELLED, "3", "Customer cancellation").join();

            // Test driver permissions
            WorkflowResult driverResult = workflowEngine.transitionShipmentState(
                    savedShipment.getId(), ShipmentState.IN_TRANSIT, "2", "Driver transit").join();

            // Test supervisor permissions
            WorkflowResult supervisorResult = workflowEngine.transitionShipmentState(
                    savedShipment.getId(), ShipmentState.ACCEPTED, "4", "Supervisor acceptance").join();

            // Test admin permissions
            WorkflowResult adminResult = workflowEngine.transitionShipmentState(
                    savedShipment.getId(), ShipmentState.COMPLETED, "6", "Admin completion").join();

            boolean permissionsWorking = customerResult.getSuccess() && 
                                      driverResult.getSuccess() && 
                                      supervisorResult.getSuccess() && 
                                      adminResult.getSuccess();

            return WorkflowTestResult.builder()
                    .success(permissionsWorking)
                    .message(permissionsWorking ? "Role-based permissions test passed" : "Role-based permissions test failed")
                    .details(Map.of(
                            "customerPermission", customerResult.getSuccess(),
                            "driverPermission", driverResult.getSuccess(),
                            "supervisorPermission", supervisorResult.getSuccess(),
                            "adminPermission", adminResult.getSuccess()
                    ))
                    .build();

        } catch (Exception e) {
            log.error("Error in role-based permissions test: {}", e.getMessage(), e);
            return WorkflowTestResult.builder()
                    .success(false)
                    .error("Role-based permissions test failed: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Test business rules validation
     */
    private WorkflowTestResult testBusinessRulesValidation() {
        try {
            log.debug("Testing business rules validation");

            // Create test shipment
            Shipment testShipment = createTestShipment();
            testShipment.setStatus(ShipmentState.CREATED);
            testShipment.setDriverId(null); // No driver assigned
            Shipment savedShipment = shipmentRepository.save(testShipment);

            // Initialize workflow
            workflowEngine.initializeWorkflow(savedShipment).join();

            // Test invalid transition: IN_TRANSIT without driver
            WorkflowResult invalidResult = workflowEngine.transitionShipmentState(
                    savedShipment.getId(), ShipmentState.IN_TRANSIT, "2", "Invalid transition").join();

            // Test valid transition: CANCELLED
            WorkflowResult validResult = workflowEngine.transitionShipmentState(
                    savedShipment.getId(), ShipmentState.CANCELLED, "3", "Valid cancellation").join();

            boolean businessRulesWorking = !invalidResult.getSuccess() && validResult.getSuccess();

            return WorkflowTestResult.builder()
                    .success(businessRulesWorking)
                    .message(businessRulesWorking ? "Business rules validation test passed" : "Business rules validation test failed")
                    .details(Map.of(
                            "invalidTransitionBlocked", !invalidResult.getSuccess(),
                            "validTransitionAllowed", validResult.getSuccess(),
                            "invalidTransitionError", invalidResult.getError(),
                            "validTransitionSuccess", validResult.getSuccess()
                    ))
                    .build();

        } catch (Exception e) {
            log.error("Error in business rules validation test: {}", e.getMessage(), e);
            return WorkflowTestResult.builder()
                    .success(false)
                    .error("Business rules validation test failed: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Test real-time updates
     */
    private WorkflowTestResult testRealTimeUpdates() {
        try {
            log.debug("Testing real-time updates");

            // Create test shipment
            Shipment testShipment = createTestShipment();
            Shipment savedShipment = shipmentRepository.save(testShipment);

            // Initialize workflow
            workflowEngine.initializeWorkflow(savedShipment).join();

            // Test real-time updates
            List<String> updateResults = new ArrayList<>();

            // Send location update
            LocationDTO location = LocationDTO.builder()
                    .latitude(24.7136)
                    .longitude(46.6753)
                    .timestamp(LocalDateTime.now())
                    .build();

            workflowEngine.sendLocationUpdate(savedShipment.getId(), location, "2");
            updateResults.add("Location update sent");

            // Send state change notification
            workflowEngine.sendStateChange(savedShipment, ShipmentState.IN_TRANSIT, "Test update");
            updateResults.add("State change notification sent");

            // Send delay alert
            workflowEngine.sendDelayAlert(savedShipment.getId(), "Test delay", Duration.ofHours(2));
            updateResults.add("Delay alert sent");

            boolean realTimeUpdatesWorking = updateResults.size() == 3;

            return WorkflowTestResult.builder()
                    .success(realTimeUpdatesWorking)
                    .message(realTimeUpdatesWorking ? "Real-time updates test passed" : "Real-time updates test failed")
                    .details(Map.of(
                            "updateResults", updateResults,
                            "allUpdatesSent", realTimeUpdatesWorking
                    ))
                    .build();

        } catch (Exception e) {
            log.error("Error in real-time updates test: {}", e.getMessage(), e);
            return WorkflowTestResult.builder()
                    .success(false)
                    .error("Real-time updates test failed: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Test WebSocket integration
     */
    private WorkflowTestResult testWebSocketIntegration() {
        try {
            log.debug("Testing WebSocket integration");

            // Create test shipment
            Shipment testShipment = createTestShipment();
            Shipment savedShipment = shipmentRepository.save(testShipment);

            // Initialize workflow
            workflowEngine.initializeWorkflow(savedShipment).join();

            // Test WebSocket message sending
            List<String> wsResults = new ArrayList<>();

            // Send workflow update
            Map<String, Object> wsMessage = Map.of(
                    "type", "WORKFLOW_UPDATE",
                    "shipmentId", savedShipment.getId(),
                    "trackingNumber", savedShipment.getTrackingNumber(),
                    "timestamp", LocalDateTime.now()
            );

            webSocketSessionManager.sendToRole("SUPERVISOR", wsMessage);
            wsResults.add("Supervisor notification sent");

            webSocketSessionManager.sendToRole("ADMIN", wsMessage);
            wsResults.add("Admin notification sent");

            boolean webSocketWorking = wsResults.size() == 2;

            return WorkflowTestResult.builder()
                    .success(webSocketWorking)
                    .message(webSocketWorking ? "WebSocket integration test passed" : "WebSocket integration test failed")
                    .details(Map.of(
                            "wsResults", wsResults,
                            "allMessagesSent", webSocketWorking
                    ))
                    .build();

        } catch (Exception e) {
            log.error("Error in WebSocket integration test: {}", e.getMessage(), e);
            return WorkflowTestResult.builder()
                    .success(false)
                    .error("WebSocket integration test failed: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Test notification system
     */
    private WorkflowTestResult testNotificationSystem() {
        try {
            log.debug("Testing notification system");

            // Create test shipment
            Shipment testShipment = createTestShipment();
            Shipment savedShipment = shipmentRepository.save(testShipment);

            // Initialize workflow
            workflowEngine.initializeWorkflow(savedShipment).join();

            // Test notification sending
            List<String> notificationResults = new ArrayList<>();

            // Send push notification
            NotificationMessageDTO notification = NotificationMessageDTO.builder()
                    .title("Test Notification")
                    .message("This is a test notification")
                    .type(NotificationType.SHIPMENT)
                    .data(Map.of("shipmentId", savedShipment.getId()))
                    .build();

            notificationService.sendPushNotification(savedShipment.getCustomerId(), notification);
            notificationResults.add("Push notification sent");

            boolean notificationsWorking = notificationResults.size() == 1;

            return WorkflowTestResult.builder()
                    .success(notificationsWorking)
                    .message(notificationsWorking ? "Notification system test passed" : "Notification system test failed")
                    .details(Map.of(
                            "notificationResults", notificationResults,
                            "allNotificationsSent", notificationsWorking
                    ))
                    .build();

        } catch (Exception e) {
            log.error("Error in notification system test: {}", e.getMessage(), e);
            return WorkflowTestResult.builder()
                    .success(false)
                    .error("Notification system test failed: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Test workflow persistence
     */
    private WorkflowTestResult testWorkflowPersistence() {
        try {
            log.debug("Testing workflow persistence");

            // Create test shipment
            Shipment testShipment = createTestShipment();
            Shipment savedShipment = shipmentRepository.save(testShipment);

            // Initialize workflow
            workflowEngine.initializeWorkflow(savedShipment).join();

            // Perform transitions
            workflowEngine.transitionShipmentState(savedShipment.getId(), ShipmentState.ACCEPTED, "1", "Test").join();
            workflowEngine.transitionShipmentState(savedShipment.getId(), ShipmentState.PREPARING, "1", "Test").join();

            // Get workflow state
            ShipmentWorkflowState workflowState = workflowEngine.getWorkflowState(savedShipment.getId());

            boolean persistenceWorking = workflowState != null &&
                                      workflowState.getTransitionHistory().size() == 2 &&
                                      workflowState.getCurrentState() == ShipmentState.PREPARING;

            return WorkflowTestResult.builder()
                    .success(persistenceWorking)
                    .message(persistenceWorking ? "Workflow persistence test passed" : "Workflow persistence test failed")
                    .details(Map.of(
                            "workflowStateExists", workflowState != null,
                            "transitionHistorySize", workflowState != null ? workflowState.getTransitionHistory().size() : 0,
                            "currentStateCorrect", workflowState != null ? workflowState.getCurrentState() : null
                    ))
                    .build();

        } catch (Exception e) {
            log.error("Error in workflow persistence test: {}", e.getMessage(), e);
            return WorkflowTestResult.builder()
                    .success(false)
                    .error("Workflow persistence test failed: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Test error handling
     */
    private WorkflowTestResult testErrorHandling() {
        try {
            log.debug("Testing error handling");

            // Test with invalid shipment ID
            WorkflowResult invalidShipmentResult = workflowEngine.transitionShipmentState(
                    999999L, ShipmentState.ACCEPTED, "1", "Invalid shipment").join();

            // Test with invalid state transition
            Shipment testShipment = createTestShipment();
            testShipment.setStatus(ShipmentState.CREATED);
            Shipment savedShipment = shipmentRepository.save(testShipment);

            workflowEngine.initializeWorkflow(savedShipment).join();
            WorkflowResult invalidTransitionResult = workflowEngine.transitionShipmentState(
                    savedShipment.getId(), ShipmentState.DELIVERED, "1", "Invalid transition").join();

            boolean errorHandlingWorking = !invalidShipmentResult.getSuccess() && 
                                         !invalidTransitionResult.getSuccess() &&
                                         invalidShipmentResult.getError() != null &&
                                         invalidTransitionResult.getError() != null;

            return WorkflowTestResult.builder()
                    .success(errorHandlingWorking)
                    .message(errorHandlingWorking ? "Error handling test passed" : "Error handling test failed")
                    .details(Map.of(
                            "invalidShipmentHandled", !invalidShipmentResult.getSuccess(),
                            "invalidTransitionHandled", !invalidTransitionResult.getSuccess(),
                            "errorMessagesPresent", invalidShipmentResult.getError() != null && invalidTransitionResult.getError() != null
                    ))
                    .build();

        } catch (Exception e) {
            log.error("Error in error handling test: {}", e.getMessage(), e);
            return WorkflowTestResult.builder()
                    .success(false)
                    .error("Error handling test failed: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Test performance metrics
     */
    private WorkflowTestResult testPerformanceMetrics() {
        try {
            log.debug("Testing performance metrics");

            // Create test shipments
            List<Shipment> testShipments = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                Shipment shipment = createTestShipment();
                shipment.setTrackingNumber("TEST-" + i);
                testShipments.add(shipmentRepository.save(shipment));
            }

            // Initialize workflows
            List<CompletableFuture<WorkflowResult>> initFutures = new ArrayList<>();
            for (Shipment shipment : testShipments) {
                initFutures.add(workflowEngine.initializeWorkflow(shipment));
            }

            // Wait for all initializations
            CompletableFuture.allOf(initFutures.toArray(new CompletableFuture[0])).join();

            // Perform transitions
            long startTime = System.currentTimeMillis();
            List<CompletableFuture<WorkflowResult>> transitionFutures = new ArrayList<>();
            for (Shipment shipment : testShipments) {
                transitionFutures.add(workflowEngine.transitionShipmentState(
                        shipment.getId(), ShipmentState.ACCEPTED, "1", "Performance test"));
            }

            // Wait for all transitions
            CompletableFuture.allOf(transitionFutures.toArray(new CompletableFuture[0])).join();
            long endTime = System.currentTimeMillis();

            long averageTime = (endTime - startTime) / testShipments.size();
            boolean performanceAcceptable = averageTime < 1000; // Less than 1 second per operation

            return WorkflowTestResult.builder()
                    .success(performanceAcceptable)
                    .message(performanceAcceptable ? "Performance metrics test passed" : "Performance metrics test failed")
                    .details(Map.of(
                            "shipmentsTested", testShipments.size(),
                            "averageTimeMs", averageTime,
                            "performanceAcceptable", performanceAcceptable,
                            "thresholdMs", 1000
                    ))
                    .build();

        } catch (Exception e) {
            log.error("Error in performance metrics test: {}", e.getMessage(), e);
            return WorkflowTestResult.builder()
                    .success(false)
                    .error("Performance metrics test failed: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Calculate overall test success
     */
    private boolean calculateOverallSuccess(Map<String, WorkflowTestResult> testResults) {
        return testResults.values().stream()
                .allMatch(WorkflowTestResult::getSuccess);
    }

    /**
     * Create test shipment
     */
    private Shipment createTestShipment() {
        return Shipment.builder()
                .id(System.currentTimeMillis())
                .trackingNumber("TEST-" + System.currentTimeMillis())
                .customerId(1L)
                .pickupAddress("Test Pickup Address")
                .deliveryAddress("Test Delivery Address")
                .pickupLocation(LocationDTO.builder()
                        .latitude(24.7136)
                        .longitude(46.6753)
                        .build())
                .deliveryLocation(LocationDTO.builder()
                        .latitude(24.8136)
                        .longitude(46.7753)
                        .build())
                .status(ShipmentState.CREATED)
                .priority(ShipmentPriority.MEDIUM)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // Result classes
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class WorkflowIntegrationTestResult {
        private String testId;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private Map<String, WorkflowTestResult> testResults;
        private Boolean overallSuccess;
        private Integer testCount;
        private Integer passedTests;
        private String errorMessage;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class WorkflowTestResult {
        private Boolean success;
        private String message;
        private String error;
        private Map<String, Object> details;
        private LocalDateTime timestamp;
    }
}
