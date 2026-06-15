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
import java.util.concurrent.ConcurrentHashMap;

/**
 * Shipment workflow engine for enterprise-grade system
 * Controls all shipment states with controlled transitions and logging
 */
@Slf4j
@Component
public class ShipmentWorkflowEngine {

    // Repository dependencies
    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ActivityLogService activityLogService;
    private final WebSocketSessionManager webSocketSessionManager;
    private final NotificationService notificationService;

    // Workflow state management
    private final Map<String, ShipmentWorkflowState> activeWorkflows = new ConcurrentHashMap<>();
    private final Map<ShipmentState, Set<ShipmentState>> validTransitions;
    private final Map<ShipmentState, Set<UserRole>> statePermissions;

    @Autowired
    public ShipmentWorkflowEngine(ShipmentRepository shipmentRepository,
                               UserRepository userRepository,
                               VehicleRepository vehicleRepository,
                               ActivityLogService activityLogService,
                               WebSocketSessionManager webSocketSessionManager,
                               NotificationService notificationService) {
        this.shipmentRepository = shipmentRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.activityLogService = activityLogService;
        this.webSocketSessionManager = webSocketSessionManager;
        this.notificationService = notificationService;
        
        initializeWorkflowRules();
    }

    /**
     * Initialize workflow rules and permissions
     */
    private void initializeWorkflowRules() {
        // Define valid state transitions
        validTransitions = Map.of(
            ShipmentState.CREATED, Set.of(ShipmentState.ACCEPTED, ShipmentState.CANCELLED),
            ShipmentState.ACCEPTED, Set.of(ShipmentState.PREPARING, ShipmentState.CANCELLED),
            ShipmentState.PREPARING, Set.of(ShipmentState.LOADED, ShipmentState.CANCELLED),
            ShipmentState.LOADED, Set.of(ShipmentState.IN_TRANSIT, ShipmentState.CANCELLED),
            ShipmentState.IN_TRANSIT, Set.of(ShipmentState.ARRIVED, ShipmentState.DELIVERED, ShipmentState.DELAYED),
            ShipmentState.ARRIVED, Set.of(ShipmentState.DELIVERED, ShipmentState.DELAYED),
            ShipmentState.DELIVERED, Set.of(ShipmentState.COMPLETED),
            ShipmentState.CANCELLED, Set.of(ShipmentState.CANCELLED_CONFIRMED),
            ShipmentState.DELAYED, Set.of(ShipmentState.IN_TRANSIT, ShipmentState.CANCELLED),
            ShipmentState.COMPLETED, Set.of(),
            ShipmentState.CANCELLED_CONFIRMED, Set.of()
        );

        // Define role-based permissions for state transitions
        statePermissions = Map.of(
            ShipmentState.CREATED, Set.of(UserRole.CUSTOMER, UserRole.ADMIN, UserRole.SUPERVISOR),
            ShipmentState.ACCEPTED, Set.of(UserRole.SUPERVISOR, UserRole.ADMIN),
            ShipmentState.PREPARING, Set.of(UserRole.DRIVER, UserRole.SUPERVISOR, UserRole.ADMIN),
            ShipmentState.LOADED, Set.of(UserRole.DRIVER, UserRole.SUPERVISOR, UserRole.ADMIN),
            ShipmentState.IN_TRANSIT, Set.of(UserRole.DRIVER, UserRole.SUPERVISOR, UserRole.ADMIN, UserRole.CUSTOMER),
            ShipmentState.ARRIVED, Set.of(UserRole.DRIVER, UserRole.SUPERVISOR, UserRole.ADMIN, UserRole.CUSTOMER),
            ShipmentState.DELIVERED, Set.of(UserRole.DRIVER, UserRole.SUPERVISOR, UserRole.ADMIN, UserRole.CUSTOMER),
            ShipmentState.DELAYED, Set.of(UserRole.DRIVER, UserRole.SUPERVISOR, UserRole.ADMIN, UserRole.CUSTOMER),
            ShipmentState.CANCELLED, Set.of(UserRole.CUSTOMER, UserRole.SUPERVISOR, UserRole.ADMIN),
            ShipmentState.COMPLETED, Set.of(UserRole.ADMIN, UserRole.SUPERVISOR),
            ShipmentState.CANCELLED_CONFIRMED, Set.of(UserRole.ADMIN, UserRole.SUPERVISOR)
        );

        log.info("Shipment workflow engine initialized with {} states and {} transition rules", 
                validTransitions.size(), validTransitions.values().stream().mapToInt(Set::size).sum());
    }

    /**
     * Initialize workflow for a new shipment
     */
    public CompletableFuture<WorkflowResult> initializeWorkflow(Shipment shipment) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Initializing workflow for shipment: {}", shipment.getId());

                ShipmentWorkflowState workflowState = ShipmentWorkflowState.builder()
                        .shipmentId(shipment.getId())
                        .currentState(shipment.getStatus())
                        .previousState(shipment.getStatus())
                        .workflowId("WF-" + shipment.getId() + "-" + System.currentTimeMillis())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .transitionHistory(new ArrayList<>())
                        .active(true)
                        .build();

                activeWorkflows.put(shipment.getId(), workflowState);

                // Log initialization
                logTransition(shipment.getId(), shipment.getStatus(), shipment.getStatus(), 
                           "WORKFLOW_INITIALIZED", "SYSTEM", "Workflow initialized");

                // Notify relevant parties
                notifyStateChange(shipment, shipment.getStatus(), "WORKFLOW_INITIALIZED");

                return WorkflowResult.builder()
                        .success(true)
                        .workflowId(workflowState.getWorkflowId())
                        .currentState(shipment.getStatus())
                        .message("Workflow initialized successfully")
                        .timestamp(LocalDateTime.now())
                        .build();

            } catch (Exception e) {
                log.error("Error initializing workflow for shipment {}: {}", shipment.getId(), e.getMessage(), e);
                return WorkflowResult.builder()
                        .success(false)
                        .error("Failed to initialize workflow: " + e.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build();
            }
        });
    }

    /**
     * Transition shipment to new state
     */
    public CompletableFuture<WorkflowResult> transitionShipmentState(
            Long shipmentId, ShipmentState newState, String userId, String reason) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Transitioning shipment {} from state to {}", shipmentId, newState);

                // Get current workflow state
                ShipmentWorkflowState workflowState = activeWorkflows.get(shipmentId.toString());
                if (workflowState == null) {
                    return WorkflowResult.builder()
                            .success(false)
                            .error("No active workflow found for shipment: " + shipmentId)
                            .timestamp(LocalDateTime.now())
                            .build();
                }

                ShipmentState currentState = workflowState.getCurrentState();

                // Validate transition
                TransitionValidationResult validation = validateTransition(shipmentId, currentState, newState, userId);
                if (!validation.isValid()) {
                    return WorkflowResult.builder()
                            .success(false)
                            .error(validation.getErrorMessage())
                            .errorCode(validation.getErrorCode())
                            .timestamp(LocalDateTime.now())
                            .build();
                }

                // Get shipment
                Optional<Shipment> shipmentOpt = shipmentRepository.findById(shipmentId);
                if (shipmentOpt.isEmpty()) {
                    return WorkflowResult.builder()
                            .success(false)
                            .error("Shipment not found: " + shipmentId)
                            .timestamp(LocalDateTime.now())
                            .build();
                }

                Shipment shipment = shipmentOpt.get();

                // Execute state transition
                StateTransitionResult transitionResult = executeStateTransition(shipment, currentState, newState, userId, reason);
                if (!transitionResult.isSuccess()) {
                    return WorkflowResult.builder()
                            .success(false)
                            .error(transitionResult.getErrorMessage())
                            .timestamp(LocalDateTime.now())
                            .build();
                }

                // Update workflow state
                updateWorkflowState(workflowState, newState, userId, reason);

                // Save shipment
                shipmentRepository.save(shipment);

                // Log transition
                logTransition(shipmentId, currentState, newState, reason, userId, "STATE_TRANSITION");

                // Notify state change
                notifyStateChange(shipment, newState, reason);

                // Execute state-specific actions
                executeStateSpecificActions(shipment, newState);

                return WorkflowResult.builder()
                        .success(true)
                        .workflowId(workflowState.getWorkflowId())
                        .previousState(currentState)
                        .currentState(newState)
                        .message("State transition completed successfully")
                        .timestamp(LocalDateTime.now())
                        .build();

            } catch (Exception e) {
                log.error("Error transitioning shipment state: {}", e.getMessage(), e);
                return WorkflowResult.builder()
                        .success(false)
                        .error("Failed to transition state: " + e.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build();
            }
        });
    }

    /**
     * Validate state transition
     */
    private TransitionValidationResult validateTransition(Long shipmentId, ShipmentState currentState, 
                                                  ShipmentState newState, String userId) {
        try {
            // Check if transition is valid
            Set<ShipmentState> allowedTransitions = validTransitions.get(currentState);
            if (allowedTransitions == null || !allowedTransitions.contains(newState)) {
                return TransitionValidationResult.builder()
                        .valid(false)
                        .errorCode("INVALID_TRANSITION")
                        .errorMessage("Invalid state transition from " + currentState + " to " + newState)
                        .build();
            }

            // Check user permissions
            Set<UserRole> allowedRoles = statePermissions.get(newState);
            if (allowedRoles == null) {
                return TransitionValidationResult.builder()
                        .valid(false)
                        .errorCode("NO_PERMISSIONS_DEFINED")
                        .errorMessage("No permissions defined for state: " + newState)
                        .build();
            }

            // Get user role
            Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
            if (userOpt.isEmpty()) {
                return TransitionValidationResult.builder()
                        .valid(false)
                        .errorCode("USER_NOT_FOUND")
                        .errorMessage("User not found: " + userId)
                        .build();
            }

            UserRole userRole = userOpt.get().getRole();
            if (!allowedRoles.contains(userRole)) {
                return TransitionValidationResult.builder()
                        .valid(false)
                        .errorCode("INSUFFICIENT_PERMISSIONS")
                        .errorMessage("User role " + userRole + " not authorized for state transition to " + newState)
                        .build();
            }

            // Check business rules
            BusinessRuleValidationResult businessValidation = validateBusinessRules(shipmentId, currentState, newState);
            if (!businessValidation.isValid()) {
                return TransitionValidationResult.builder()
                        .valid(false)
                        .errorCode("BUSINESS_RULE_VIOLATION")
                        .errorMessage(businessValidation.getErrorMessage())
                        .build();
            }

            return TransitionValidationResult.builder()
                    .valid(true)
                    .build();

        } catch (Exception e) {
            log.error("Error validating transition: {}", e.getMessage(), e);
            return TransitionValidationResult.builder()
                    .valid(false)
                    .errorCode("VALIDATION_ERROR")
                    .errorMessage("Validation error: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Execute state transition
     */
    private StateTransitionResult executeStateTransition(Shipment shipment, ShipmentState currentState, 
                                                   ShipmentState newState, String userId, String reason) {
        try {
            ShipmentState previousState = shipment.getStatus();
            
            // Update shipment state
            shipment.setStatus(newState);
            shipment.setUpdatedAt(LocalDateTime.now());

            // Execute state-specific transition logic
            switch (newState) {
                case ACCEPTED:
                    return executeAcceptTransition(shipment, userId, reason);
                case PREPARING:
                    return executePreparingTransition(shipment, userId, reason);
                case LOADED:
                    return executeLoadedTransition(shipment, userId, reason);
                case IN_TRANSIT:
                    return executeInTransitTransition(shipment, userId, reason);
                case ARRIVED:
                    return executeArrivedTransition(shipment, userId, reason);
                case DELIVERED:
                    return executeDeliveredTransition(shipment, userId, reason);
                case DELAYED:
                    return executeDelayedTransition(shipment, userId, reason);
                case CANCELLED:
                    return executeCancelledTransition(shipment, userId, reason);
                case COMPLETED:
                    return executeCompletedTransition(shipment, userId, reason);
                default:
                    return StateTransitionResult.builder()
                            .success(true)
                            .message("State transition completed")
                            .build();
            }

        } catch (Exception e) {
            log.error("Error executing state transition: {}", e.getMessage(), e);
            return StateTransitionResult.builder()
                    .success(false)
                    .errorMessage("Failed to execute transition: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Execute ACCEPTED state transition
     */
    private StateTransitionResult executeAcceptTransition(Shipment shipment, String userId, String reason) {
        try {
            // Assign driver if not already assigned
            if (shipment.getDriverId() == null) {
                // Find available driver
                List<User> availableDrivers = userRepository.findByRoleAndActive(UserRole.DRIVER, true);
                if (!availableDrivers.isEmpty()) {
                    return StateTransitionResult.builder()
                            .success(false)
                            .errorMessage("No available drivers found")
                            .build();
                }
                
                // Assign first available driver (in real implementation, use smart assignment)
                shipment.setDriverId(availableDrivers.get(0).getId());
                shipment.setVehicleId(availableDrivers.get(0).getVehicleId());
            }

            // Set accepted timestamp
            shipment.setAcceptedAt(LocalDateTime.now());
            shipment.setAcceptedBy(userId);

            return StateTransitionResult.builder()
                    .success(true)
                    .message("Shipment accepted and driver assigned")
                    .build();

        } catch (Exception e) {
            log.error("Error executing accept transition: {}", e.getMessage(), e);
            return StateTransitionResult.builder()
                    .success(false)
                    .errorMessage("Failed to execute accept transition: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Execute PREPARING state transition
     */
    private StateTransitionResult executePreparingTransition(Shipment shipment, String userId, String reason) {
        try {
            // Update preparation details
            shipment.setPreparationStartedAt(LocalDateTime.now());
            shipment.setPreparationBy(userId);

            // Update vehicle status to PREPARING
            if (shipment.getVehicleId() != null) {
                Optional<Vehicle> vehicleOpt = vehicleRepository.findById(shipment.getVehicleId());
                if (vehicleOpt.isPresent()) {
                    Vehicle vehicle = vehicleOpt.get();
                    vehicle.setStatus(VehicleStatus.PREPARING);
                    vehicle.setUpdatedAt(LocalDateTime.now());
                    vehicleRepository.save(vehicle);
                }
            }

            return StateTransitionResult.builder()
                    .success(true)
                    .message("Shipment preparation started")
                    .build();

        } catch (Exception e) {
            log.error("Error executing preparing transition: {}", e.getMessage(), e);
            return StateTransitionResult.builder()
                    .success(false)
                    .errorMessage("Failed to execute preparing transition: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Execute LOADED state transition
     */
    private StateTransitionResult executeLoadedTransition(Shipment shipment, String userId, String reason) {
        try {
            // Update loading details
            shipment.setLoadedAt(LocalDateTime.now());
            shipment.setLoadedBy(userId);

            // Update vehicle status to LOADED
            if (shipment.getVehicleId() != null) {
                Optional<Vehicle> vehicleOpt = vehicleRepository.findById(shipment.getVehicleId());
                if (vehicleOpt.isPresent()) {
                    Vehicle vehicle = vehicleOpt.get();
                    vehicle.setStatus(VehicleStatus.LOADED);
                    vehicle.setUpdatedAt(LocalDateTime.now());
                    vehicleRepository.save(vehicle);
                }
            }

            return StateTransitionResult.builder()
                    .success(true)
                    .message("Shipment loaded successfully")
                    .build();

        } catch (Exception e) {
            log.error("Error executing loaded transition: {}", e.getMessage(), e);
            return StateTransitionResult.builder()
                    .success(false)
                    .errorMessage("Failed to execute loaded transition: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Execute IN_TRANSIT state transition
     */
    private StateTransitionResult executeInTransitTransition(Shipment shipment, String userId, String reason) {
        try {
            // Update transit details
            shipment.setInTransitAt(LocalDateTime.now());
            shipment.setInTransitBy(userId);

            // Update vehicle status to IN_TRANSIT
            if (shipment.getVehicleId() != null) {
                Optional<Vehicle> vehicleOpt = vehicleRepository.findById(shipment.getVehicleId());
                if (vehicleOpt.isPresent()) {
                    Vehicle vehicle = vehicleOpt.get();
                    vehicle.setStatus(VehicleStatus.IN_TRANSIT);
                    vehicle.setUpdatedAt(LocalDateTime.now());
                    vehicleRepository.save(vehicle);
                }
            }

            return StateTransitionResult.builder()
                    .success(true)
                    .message("Shipment is now in transit")
                    .build();

        } catch (Exception e) {
            log.error("Error executing in transit transition: {}", e.getMessage(), e);
            return StateTransitionResult.builder()
                    .success(false)
                    .errorMessage("Failed to execute in transit transition: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Execute ARRIVED state transition
     */
    private StateTransitionResult executeArrivedTransition(Shipment shipment, String userId, String reason) {
        try {
            // Update arrival details
            shipment.setArrivedAt(LocalDateTime.now());
            shipment.setArrivedBy(userId);

            // Update vehicle status to ARRIVED
            if (shipment.getVehicleId() != null) {
                Optional<Vehicle> vehicleOpt = vehicleRepository.findById(shipment.getVehicleId());
                if (vehicleOpt.isPresent()) {
                    Vehicle vehicle = vehicleOpt.get();
                    vehicle.setStatus(VehicleStatus.ARRIVED);
                    vehicle.setUpdatedAt(LocalDateTime.now());
                    vehicleRepository.save(vehicle);
                }
            }

            return StateTransitionResult.builder()
                    .success(true)
                    .message("Shipment arrived at destination")
                    .build();

        } catch (Exception e) {
            log.error("Error executing arrived transition: {}", e.getMessage(), e);
            return StateTransitionResult.builder()
                    .success(false)
                    .errorMessage("Failed to execute arrived transition: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Execute DELIVERED state transition
     */
    private StateTransitionResult executeDeliveredTransition(Shipment shipment, String userId, String reason) {
        try {
            // Update delivery details
            shipment.setDeliveredAt(LocalDateTime.now());
            shipment.setDeliveredBy(userId);

            // Update vehicle status to AVAILABLE
            if (shipment.getVehicleId() != null) {
                Optional<Vehicle> vehicleOpt = vehicleRepository.findById(shipment.getVehicleId());
                if (vehicleOpt.isPresent()) {
                    Vehicle vehicle = vehicleOpt.get();
                    vehicle.setStatus(VehicleStatus.AVAILABLE);
                    vehicle.setUpdatedAt(LocalDateTime.now());
                    vehicleRepository.save(vehicle);
                }
            }

            return StateTransitionResult.builder()
                    .success(true)
                    .message("Shipment delivered successfully")
                    .build();

        } catch (Exception e) {
            log.error("Error executing delivered transition: {}", e.getMessage(), e);
            return StateTransitionResult.builder()
                    .success(false)
                    .errorMessage("Failed to execute delivered transition: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Execute DELAYED state transition
     */
    private StateTransitionResult executeDelayedTransition(Shipment shipment, String userId, String reason) {
        try {
            // Update delay details
            shipment.setDelayedAt(LocalDateTime.now());
            shipment.setDelayedBy(userId);
            shipment.setDelayReason(reason);

            return StateTransitionResult.builder()
                    .success(true)
                    .message("Shipment marked as delayed")
                    .build();

        } catch (Exception e) {
            log.error("Error executing delayed transition: {}", e.getMessage(), e);
            return StateTransitionResult.builder()
                    .success(false)
                    .errorMessage("Failed to execute delayed transition: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Execute CANCELLED state transition
     */
    private StateTransitionResult executeCancelledTransition(Shipment shipment, String userId, String reason) {
        try {
            // Update cancellation details
            shipment.setCancelledAt(LocalDateTime.now());
            shipment.setCancelledBy(userId);
            shipment.setCancellationReason(reason);

            // Update vehicle status to AVAILABLE
            if (shipment.getVehicleId() != null) {
                Optional<Vehicle> vehicleOpt = vehicleRepository.findById(shipment.getVehicleId());
                if (vehicleOpt.isPresent()) {
                    Vehicle vehicle = vehicleOpt.get();
                    vehicle.setStatus(VehicleStatus.AVAILABLE);
                    vehicle.setUpdatedAt(LocalDateTime.now());
                    vehicleRepository.save(vehicle);
                }
            }

            return StateTransitionResult.builder()
                    .success(true)
                    .message("Shipment cancelled successfully")
                    .build();

        } catch (Exception e) {
            log.error("Error executing cancelled transition: {}", e.getMessage(), e);
            return StateTransitionResult.builder()
                    .success(false)
                    .errorMessage("Failed to execute cancelled transition: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Execute COMPLETED state transition
     */
    private StateTransitionResult executeCompletedTransition(Shipment shipment, String userId, String reason) {
        try {
            // Update completion details
            shipment.setCompletedAt(LocalDateTime.now());
            shipment.setCompletedBy(userId);

            return StateTransitionResult.builder()
                    .success(true)
                    .message("Shipment workflow completed")
                    .build();

        } catch (Exception e) {
            log.error("Error executing completed transition: {}", e.getMessage(), e);
            return StateTransitionResult.builder()
                    .success(false)
                    .errorMessage("Failed to execute completed transition: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Validate business rules for state transition
     */
    private BusinessRuleValidationResult validateBusinessRules(Long shipmentId, ShipmentState currentState, ShipmentState newState) {
        try {
            // Get shipment
            Optional<Shipment> shipmentOpt = shipmentRepository.findById(shipmentId);
            if (shipmentOpt.isEmpty()) {
                return BusinessRuleValidationResult.builder()
                        .valid(false)
                        .errorMessage("Shipment not found")
                        .build();
            }

            Shipment shipment = shipmentOpt.get();

            // Business rule: Cannot transition to IN_TRANSIT without driver assignment
            if (newState == ShipmentState.IN_TRANSIT && shipment.getDriverId() == null) {
                return BusinessRuleValidationResult.builder()
                        .valid(false)
                        .errorMessage("Cannot transition to IN_TRANSIT without driver assignment")
                        .build();
            }

            // Business rule: Cannot transition to DELIVERED without ARRIVED
            if (newState == ShipmentState.DELIVERED && currentState != ShipmentState.ARRIVED) {
                return BusinessRuleValidationResult.builder()
                        .valid(false)
                        .errorMessage("Cannot transition to DELIVERED without ARRIVED state")
                        .build();
            }

            // Business rule: Cannot transition to LOADED without PREPARING
            if (newState == ShipmentState.LOADED && currentState != ShipmentState.PREPARING) {
                return BusinessRuleValidationResult.builder()
                        .valid(false)
                        .errorMessage("Cannot transition to LOADED without PREPARING state")
                        .build();
            }

            return BusinessRuleValidationResult.builder()
                    .valid(true)
                    .build();

        } catch (Exception e) {
            log.error("Error validating business rules: {}", e.getMessage(), e);
            return BusinessRuleValidationResult.builder()
                    .valid(false)
                    .errorMessage("Business rule validation error: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Update workflow state
     */
    private void updateWorkflowState(ShipmentWorkflowState workflowState, ShipmentState newState, 
                                  String userId, String reason) {
        try {
            // Create transition record
            StateTransition transition = StateTransition.builder()
                    .fromState(workflowState.getCurrentState())
                    .toState(newState)
                    .userId(userId)
                    .reason(reason)
                    .timestamp(LocalDateTime.now())
                    .build();

            // Update workflow state
            workflowState.setPreviousState(workflowState.getCurrentState());
            workflowState.setCurrentState(newState);
            workflowState.setUpdatedAt(LocalDateTime.now());
            workflowState.getTransitionHistory().add(transition);

            // Update active workflows
            activeWorkflows.put(workflowState.getShipmentId(), workflowState);

        } catch (Exception e) {
            log.error("Error updating workflow state: {}", e.getMessage(), e);
        }
    }

    /**
     * Log state transition
     */
    private void logTransition(Long shipmentId, ShipmentState fromState, ShipmentState toState, 
                             String reason, String userId, String action) {
        try {
            Map<String, Object> metadata = Map.of(
                    "shipmentId", shipmentId,
                    "fromState", fromState,
                    "toState", toState,
                    "reason", reason,
                    "userId", userId,
                    "action", action,
                    "timestamp", LocalDateTime.now()
            );

            activityLogService.logActivity(
                    Long.valueOf(userId),
                    ActivityType.SHIPMENT_STATE_TRANSITION,
                    "SHIPMENT_WORKFLOW",
                    shipmentId.toString(),
                    action,
                    metadata
            );

        } catch (Exception e) {
            log.error("Error logging transition: {}", e.getMessage(), e);
        }
    }

    /**
     * Notify state change
     */
    private void notifyStateChange(Shipment shipment, ShipmentState newState, String reason) {
        try {
            // Create notification message
            String message = String.format("Shipment %s status changed to %s", 
                    shipment.getTrackingNumber(), newState);

            // Send notifications to relevant parties
            List<User> notificationRecipients = getNotificationRecipients(shipment, newState);
            
            for (User recipient : notificationRecipients) {
                notificationService.sendPushNotification(recipient.getId(),
                        NotificationMessageDTO.builder()
                                .title("Shipment Status Update")
                                .message(message)
                                .type(NotificationType.SHIPMENT)
                                .data(Map.of(
                                        "shipmentId", shipment.getId(),
                                        "trackingNumber", shipment.getTrackingNumber(),
                                        "newState", newState,
                                        "reason", reason
                                ))
                                .build());
            }

            // Send real-time WebSocket updates
            Map<String, Object> wsMessage = Map.of(
                    "type", "SHIPMENT_STATE_CHANGE",
                    "shipmentId", shipment.getId(),
                    "trackingNumber", shipment.getTrackingNumber(),
                    "newState", newState,
                    "reason", reason,
                    "timestamp", LocalDateTime.now()
            );

            webSocketSessionManager.sendToRole("CUSTOMER", wsMessage);
            webSocketSessionManager.sendToRole("SUPERVISOR", wsMessage);
            webSocketSessionManager.sendToRole("ADMIN", wsMessage);

        } catch (Exception e) {
            log.error("Error notifying state change: {}", e.getMessage(), e);
        }
    }

    /**
     * Execute state-specific actions
     */
    private void executeStateSpecificActions(Shipment shipment, ShipmentState newState) {
        try {
            switch (newState) {
                case IN_TRANSIT:
                    // Start real-time tracking
                    startRealTimeTracking(shipment);
                    break;
                case ARRIVED:
                    // Notify customer of arrival
                    sendArrivalNotification(shipment);
                    break;
                case DELIVERED:
                    // Generate delivery report
                    generateDeliveryReport(shipment);
                    break;
                case DELAYED:
                    // Create delay alert
                    createDelayAlert(shipment);
                    break;
                case COMPLETED:
                    // Archive workflow
                    archiveWorkflow(shipment.getId());
                    break;
            }

        } catch (Exception e) {
            log.error("Error executing state-specific actions: {}", e.getMessage(), e);
        }
    }

    /**
     * Get notification recipients for state change
     */
    private List<User> getNotificationRecipients(Shipment shipment, ShipmentState newState) {
        List<User> recipients = new ArrayList<>();

        try {
            // Always include customer
            if (shipment.getCustomerId() != null) {
                userRepository.findById(shipment.getCustomerId()).ifPresent(recipients::add);
            }

            // Include driver if assigned
            if (shipment.getDriverId() != null) {
                userRepository.findById(shipment.getDriverId()).ifPresent(recipients::add);
            }

            // Include supervisors and admins for certain states
            if (newState == ShipmentState.DELAYED || newState == ShipmentState.CANCELLED) {
                recipients.addAll(userRepository.findByRole(UserRole.SUPERVISOR));
                recipients.addAll(userRepository.findByRole(UserRole.ADMIN));
            }

        } catch (Exception e) {
            log.error("Error getting notification recipients: {}", e.getMessage(), e);
        }

        return recipients;
    }

    /**
     * Start real-time tracking
     */
    private void startRealTimeTracking(Shipment shipment) {
        try {
            // Initialize real-time tracking for shipment
            Map<String, Object> trackingData = Map.of(
                    "shipmentId", shipment.getId(),
                    "trackingNumber", shipment.getTrackingNumber(),
                    "driverId", shipment.getDriverId(),
                    "vehicleId", shipment.getVehicleId(),
                    "startLocation", shipment.getPickupLocation(),
                    "endLocation", shipment.getDeliveryLocation(),
                    "startTime", LocalDateTime.now()
            );

            webSocketSessionManager.sendToRole("SUPERVISOR", Map.of(
                    "type", "REAL_TIME_TRACKING_STARTED",
                    "data", trackingData
            ));

        } catch (Exception e) {
            log.error("Error starting real-time tracking: {}", e.getMessage(), e);
        }
    }

    /**
     * Send arrival notification
     */
    private void sendArrivalNotification(Shipment shipment) {
        try {
            if (shipment.getCustomerId() != null) {
                notificationService.sendPushNotification(shipment.getCustomerId(),
                        NotificationMessageDTO.builder()
                                .title("Shipment Arrived")
                                .message("Your shipment " + shipment.getTrackingNumber() + " has arrived at destination")
                                .type(NotificationType.SHIPMENT)
                                .data(Map.of(
                                        "shipmentId", shipment.getId(),
                                        "trackingNumber", shipment.getTrackingNumber()
                                ))
                                .build());
            }

        } catch (Exception e) {
            log.error("Error sending arrival notification: {}", e.getMessage(), e);
        }
    }

    /**
     * Generate delivery report
     */
    private void generateDeliveryReport(Shipment shipment) {
        try {
            Map<String, Object> reportData = Map.of(
                    "shipmentId", shipment.getId(),
                    "trackingNumber", shipment.getTrackingNumber(),
                    "deliveredAt", shipment.getDeliveredAt(),
                    "deliveredBy", shipment.getDeliveredBy(),
                    "totalDuration", calculateTotalDuration(shipment),
                    "onTimeDelivery", isOnTimeDelivery(shipment)
            );

            // Store report (in real implementation, save to database or file system)
            log.info("Delivery report generated for shipment {}: {}", shipment.getId(), reportData);

        } catch (Exception e) {
            log.error("Error generating delivery report: {}", e.getMessage(), e);
        }
    }

    /**
     * Create delay alert
     */
    private void createDelayAlert(Shipment shipment) {
        try {
            Map<String, Object> alertData = Map.of(
                    "shipmentId", shipment.getId(),
                    "trackingNumber", shipment.getTrackingNumber(),
                    "delayedAt", shipment.getDelayedAt(),
                    "delayReason", shipment.getDelayReason(),
                    "estimatedDelay", calculateEstimatedDelay(shipment)
            );

            // Send alert to supervisors
            webSocketSessionManager.sendToRole("SUPERVISOR", Map.of(
                    "type", "DELAY_ALERT",
                    "data", alertData
            ));

        } catch (Exception e) {
            log.error("Error creating delay alert: {}", e.getMessage(), e);
        }
    }

    /**
     * Archive workflow
     */
    private void archiveWorkflow(Long shipmentId) {
        try {
            ShipmentWorkflowState workflowState = activeWorkflows.remove(shipmentId.toString());
            if (workflowState != null) {
                workflowState.setActive(false);
                workflowState.setArchivedAt(LocalDateTime.now());
                
                // Archive workflow (in real implementation, save to database)
                log.info("Workflow archived for shipment: {}", shipmentId);
            }

        } catch (Exception e) {
            log.error("Error archiving workflow: {}", e.getMessage(), e);
        }
    }

    /**
     * Calculate total duration
     */
    private Duration calculateTotalDuration(Shipment shipment) {
        try {
            if (shipment.getCreatedAt() != null && shipment.getDeliveredAt() != null) {
                return Duration.between(shipment.getCreatedAt(), shipment.getDeliveredAt());
            }
            return Duration.ZERO;
        } catch (Exception e) {
            log.error("Error calculating total duration: {}", e.getMessage(), e);
            return Duration.ZERO;
        }
    }

    /**
     * Check if delivery is on time
     */
    private boolean isOnTimeDelivery(Shipment shipment) {
        try {
            if (shipment.getEstimatedDelivery() != null && shipment.getDeliveredAt() != null) {
                return !shipment.getDeliveredAt().isAfter(shipment.getEstimatedDelivery());
            }
            return true;
        } catch (Exception e) {
            log.error("Error checking on-time delivery: {}", e.getMessage(), e);
            return true;
        }
    }

    /**
     * Calculate estimated delay
     */
    private Duration calculateEstimatedDelay(Shipment shipment) {
        try {
            if (shipment.getEstimatedDelivery() != null && shipment.getDelayedAt() != null) {
                return Duration.between(shipment.getEstimatedDelivery(), shipment.getDelayedAt());
            }
            return Duration.ZERO;
        } catch (Exception e) {
            log.error("Error calculating estimated delay: {}", e.getMessage(), e);
            return Duration.ZERO;
        }
    }

    /**
     * Get workflow state for shipment
     */
    public ShipmentWorkflowState getWorkflowState(Long shipmentId) {
        return activeWorkflows.get(shipmentId.toString());
    }

    /**
     * Get all active workflows
     */
    public Map<String, ShipmentWorkflowState> getAllActiveWorkflows() {
        return new HashMap<>(activeWorkflows);
    }

    /**
     * Get workflow statistics
     */
    public WorkflowStatistics getWorkflowStatistics() {
        try {
            Map<ShipmentState, Long> stateCounts = new HashMap<>();
            Map<ShipmentState, Long> transitionCounts = new HashMap<>();

            for (ShipmentWorkflowState workflow : activeWorkflows.values()) {
                stateCounts.merge(workflow.getCurrentState(), 1L, Long::sum);
                
                for (StateTransition transition : workflow.getTransitionHistory()) {
                    transitionCounts.merge(transition.getToState(), 1L, Long::sum);
                }
            }

            return WorkflowStatistics.builder()
                    .totalActiveWorkflows(activeWorkflows.size())
                    .stateDistribution(stateCounts)
                    .transitionDistribution(transitionCounts)
                    .lastUpdated(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Error getting workflow statistics: {}", e.getMessage(), e);
            return WorkflowStatistics.builder()
                    .totalActiveWorkflows(0)
                    .stateDistribution(new HashMap<>())
                    .transitionDistribution(new HashMap<>())
                    .lastUpdated(LocalDateTime.now())
                    .build();
        }
    }

    // Result classes
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class WorkflowResult {
        private Boolean success;
        private String workflowId;
        private ShipmentState previousState;
        private ShipmentState currentState;
        private String message;
        private String error;
        private String errorCode;
        private LocalDateTime timestamp;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TransitionValidationResult {
        private Boolean valid;
        private String errorMessage;
        private String errorCode;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class BusinessRuleValidationResult {
        private Boolean valid;
        private String errorMessage;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class StateTransitionResult {
        private Boolean success;
        private String message;
        private String errorMessage;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class WorkflowStatistics {
        private Integer totalActiveWorkflows;
        private Map<ShipmentState, Long> stateDistribution;
        private Map<ShipmentState, Long> transitionDistribution;
        private LocalDateTime lastUpdated;
    }
}
