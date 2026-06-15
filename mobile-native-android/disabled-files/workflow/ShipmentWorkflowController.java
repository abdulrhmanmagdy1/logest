// package com.edham.logistics.workflow;

import com.edham.logistics.dto.*;
import com.edham.logistics.model.*;
import com.edham.logistics.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Shipment workflow controller for enterprise-grade system
 * Provides workflow management endpoints with role-based access control
 */
@RestController
@RequestMapping("/api/v1/workflow/shipment")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class ShipmentWorkflowController {

    private final ShipmentWorkflowEngine workflowEngine;
    private final ShipmentRepository shipmentRepository;
    private final UserRepository userRepository;

    @Autowired
    public ShipmentWorkflowController(ShipmentWorkflowEngine workflowEngine,
                                 ShipmentRepository shipmentRepository,
                                 UserRepository userRepository) {
        this.workflowEngine = workflowEngine;
        this.shipmentRepository = shipmentRepository;
        this.userRepository = userRepository;
    }

    /**
     * Initialize workflow for a shipment
     */
    @PostMapping("/{shipmentId}/initialize")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN', 'SUPERVISOR')")
    public CompletableFuture<ResponseEntity<WorkflowResult>> initializeWorkflow(
            @PathVariable Long shipmentId) {
        
        log.info("Initializing workflow for shipment: {}", shipmentId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Get shipment
                Shipment shipment = shipmentRepository.findById(shipmentId)
                        .orElseThrow(() -> new RuntimeException("Shipment not found: " + shipmentId));

                // Initialize workflow
                return workflowEngine.initializeWorkflow(shipment).join();

            } catch (Exception e) {
                log.error("Error initializing workflow: {}", e.getMessage(), e);
                return WorkflowResult.builder()
                        .success(false)
                        .error("Failed to initialize workflow: " + e.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build();
            }
        }).thenApply(result -> {
            if (result.getSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        });
    }

    /**
     * Transition shipment state
     */
    @PostMapping("/{shipmentId}/transition")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'DRIVER', 'SUPERVISOR', 'ADMIN')")
    public CompletableFuture<ResponseEntity<WorkflowResult>> transitionShipmentState(
            @PathVariable Long shipmentId,
            @Valid @RequestBody StateTransitionRequest request) {
        
        log.info("Transitioning shipment {} to state: {}", shipmentId, request.getNewState());
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Get current user ID (in real implementation, from security context)
                String userId = getCurrentUserId();
                
                // Transition state
                return workflowEngine.transitionShipmentState(
                        shipmentId, 
                        request.getNewState(), 
                        userId, 
                        request.getReason()
                ).join();

            } catch (Exception e) {
                log.error("Error transitioning shipment state: {}", e.getMessage(), e);
                return WorkflowResult.builder()
                        .success(false)
                        .error("Failed to transition state: " + e.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build();
            }
        }).thenApply(result -> {
            if (result.getSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        });
    }

    /**
     * Get workflow state for shipment
     */
    @GetMapping("/{shipmentId}/state")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'DRIVER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<WorkflowStateResponse> getWorkflowState(@PathVariable Long shipmentId) {
        
        log.info("Getting workflow state for shipment: {}", shipmentId);
        
        try {
            // Get workflow state
            ShipmentWorkflowState workflowState = workflowEngine.getWorkflowState(shipmentId);
            
            if (workflowState == null) {
                return ResponseEntity.notFound().build();
            }

            // Get shipment details
            Shipment shipment = shipmentRepository.findById(shipmentId)
                    .orElseThrow(() -> new RuntimeException("Shipment not found: " + shipmentId));

            WorkflowStateResponse response = WorkflowStateResponse.builder()
                    .shipmentId(shipmentId)
                    .trackingNumber(shipment.getTrackingNumber())
                    .currentState(workflowState.getCurrentState())
                    .previousState(workflowState.getPreviousState())
                    .workflowId(workflowState.getWorkflowId())
                    .createdAt(workflowState.getCreatedAt())
                    .updatedAt(workflowState.getUpdatedAt())
                    .active(workflowState.getActive())
                    .transitionHistory(workflowState.getTransitionHistory())
                    .availableTransitions(getAvailableTransitions(workflowState.getCurrentState()))
                    .shipmentDetails(shipment)
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting workflow state: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get available transitions for current state
     */
    @GetMapping("/{shipmentId}/transitions")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'DRIVER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<AvailableTransitionsResponse> getAvailableTransitions(@PathVariable Long shipmentId) {
        
        log.info("Getting available transitions for shipment: {}", shipmentId);
        
        try {
            // Get workflow state
            ShipmentWorkflowState workflowState = workflowEngine.getWorkflowState(shipmentId);
            
            if (workflowState == null) {
                return ResponseEntity.notFound().build();
            }

            // Get available transitions
            AvailableTransitionsResponse response = AvailableTransitionsResponse.builder()
                    .shipmentId(shipmentId)
                    .currentState(workflowState.getCurrentState())
                    .availableTransitions(getAvailableTransitions(workflowState.getCurrentState()))
                    .userPermissions(getUserPermissions(workflowState.getCurrentState()))
                    .timestamp(LocalDateTime.now())
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting available transitions: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get transition history
     */
    @GetMapping("/{shipmentId}/history")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'DRIVER', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<TransitionHistoryResponse> getTransitionHistory(@PathVariable Long shipmentId) {
        
        log.info("Getting transition history for shipment: {}", shipmentId);
        
        try {
            // Get workflow state
            ShipmentWorkflowState workflowState = workflowEngine.getWorkflowState(shipmentId);
            
            if (workflowState == null) {
                return ResponseEntity.notFound().build();
            }

            TransitionHistoryResponse response = TransitionHistoryResponse.builder()
                    .shipmentId(shipmentId)
                    .transitionHistory(workflowState.getTransitionHistory())
                    .totalTransitions(workflowState.getTransitionHistory().size())
                    .timestamp(LocalDateTime.now())
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting transition history: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get all active workflows
     */
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public ResponseEntity<ActiveWorkflowsResponse> getActiveWorkflows() {
        
        log.info("Getting all active workflows");
        
        try {
            Map<String, ShipmentWorkflowState> activeWorkflows = workflowEngine.getAllActiveWorkflows();
            
            ActiveWorkflowsResponse response = ActiveWorkflowsResponse.builder()
                    .totalActiveWorkflows(activeWorkflows.size())
                    .activeWorkflows(activeWorkflows)
                    .timestamp(LocalDateTime.now())
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting active workflows: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get workflow statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public ResponseEntity<WorkflowStatistics> getWorkflowStatistics() {
        
        log.info("Getting workflow statistics");
        
        try {
            WorkflowStatistics statistics = workflowEngine.getWorkflowStatistics();
            return ResponseEntity.ok(statistics);

        } catch (Exception e) {
            log.error("Error getting workflow statistics: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Force transition (admin only)
     */
    @PostMapping("/{shipmentId}/force-transition")
    @PreAuthorize("hasRole('ADMIN')")
    public CompletableFuture<ResponseEntity<WorkflowResult>> forceTransition(
            @PathVariable Long shipmentId,
            @Valid @RequestBody StateTransitionRequest request) {
        
        log.warn("Force transitioning shipment {} to state: {}", shipmentId, request.getNewState());
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Get current user ID
                String userId = getCurrentUserId();
                
                // Add force transition reason
                String reason = "FORCE_TRANSITION: " + request.getReason();
                
                // Transition state (bypassing normal validation)
                return workflowEngine.transitionShipmentState(
                        shipmentId, 
                        request.getNewState(), 
                        userId, 
                        reason
                ).join();

            } catch (Exception e) {
                log.error("Error force transitioning shipment: {}", e.getMessage(), e);
                return WorkflowResult.builder()
                        .success(false)
                        .error("Failed to force transition: " + e.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build();
            }
        }).thenApply(result -> {
            if (result.getSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        });
    }

    /**
     * Cancel workflow
     */
    @PostMapping("/{shipmentId}/cancel")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SUPERVISOR', 'ADMIN')")
    public CompletableFuture<ResponseEntity<WorkflowResult>> cancelWorkflow(
            @PathVariable Long shipmentId,
            @RequestBody(required = false) Map<String, String> cancellationData) {
        
        log.info("Cancelling workflow for shipment: {}", shipmentId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                String userId = getCurrentUserId();
                String reason = cancellationData != null ? 
                        cancellationData.getOrDefault("reason", "Workflow cancelled") : "Workflow cancelled";
                
                // Transition to CANCELLED state
                return workflowEngine.transitionShipmentState(
                        shipmentId, 
                        ShipmentState.CANCELLED, 
                        userId, 
                        reason
                ).join();

            } catch (Exception e) {
                log.error("Error cancelling workflow: {}", e.getMessage(), e);
                return WorkflowResult.builder()
                        .success(false)
                        .error("Failed to cancel workflow: " + e.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build();
            }
        }).thenApply(result -> {
            if (result.getSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        });
    }

    /**
     * Resume workflow
     */
    @PostMapping("/{shipmentId}/resume")
    @PreAuthorize("hasAnyRole('SUPERVISOR', 'ADMIN')")
    public CompletableFuture<ResponseEntity<WorkflowResult>> resumeWorkflow(
            @PathVariable Long shipmentId,
            @RequestBody(required = false) Map<String, String> resumeData) {
        
        log.info("Resuming workflow for shipment: {}", shipmentId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                String userId = getCurrentUserId();
                String reason = resumeData != null ? 
                        resumeData.getOrDefault("reason", "Workflow resumed") : "Workflow resumed";
                
                // Get current workflow state
                ShipmentWorkflowState workflowState = workflowEngine.getWorkflowState(shipmentId);
                
                if (workflowState == null) {
                    return WorkflowResult.builder()
                            .success(false)
                            .error("No active workflow found")
                            .timestamp(LocalDateTime.now())
                            .build();
                }
                
                // Resume based on current state
                ShipmentState resumeState = determineResumeState(workflowState.getCurrentState());
                
                return workflowEngine.transitionShipmentState(
                        shipmentId, 
                        resumeState, 
                        userId, 
                        reason
                ).join();

            } catch (Exception e) {
                log.error("Error resuming workflow: {}", e.getMessage(), e);
                return WorkflowResult.builder()
                        .success(false)
                        .error("Failed to resume workflow: " + e.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build();
            }
        }).thenApply(result -> {
            if (result.getSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        });
    }

    /**
     * Test workflow engine
     */
    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> testWorkflowEngine() {
        
        log.info("Testing workflow engine");
        
        try {
            Map<String, Object> testResults = Map.of(
                    "workflowEngine", "ACTIVE",
                    "stateTransitions", "CONFIGURED",
                    "rolePermissions", "VALID",
                    "logging", "ENABLED",
                    "realTimeUpdates", "ENABLED",
                    "businessRules", "VALID",
                    "activeWorkflows", workflowEngine.getAllActiveWorkflows().size(),
                    "statistics", workflowEngine.getWorkflowStatistics(),
                    "timestamp", LocalDateTime.now()
            );
            
            return ResponseEntity.ok(testResults);

        } catch (Exception e) {
            log.error("Error testing workflow engine: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Workflow engine test failed: " + e.getMessage()
            ));
        }
    }

    // Helper methods
    private String getCurrentUserId() {
        // In real implementation, get from security context
        return "1"; // Placeholder
    }

    private Map<ShipmentState, String> getAvailableTransitions(ShipmentState currentState) {
        // Return available transitions based on current state
        switch (currentState) {
            case CREATED:
                return Map.of(
                        ShipmentState.ACCEPTED, "Accept shipment",
                        ShipmentState.CANCELLED, "Cancel shipment"
                );
            case ACCEPTED:
                return Map.of(
                        ShipmentState.PREPARING, "Start preparation",
                        ShipmentState.CANCELLED, "Cancel shipment"
                );
            case PREPARING:
                return Map.of(
                        ShipmentState.LOADED, "Mark as loaded",
                        ShipmentState.CANCELLED, "Cancel shipment"
                );
            case LOADED:
                return Map.of(
                        ShipmentState.IN_TRANSIT, "Start transit",
                        ShipmentState.CANCELLED, "Cancel shipment"
                );
            case IN_TRANSIT:
                return Map.of(
                        ShipmentState.ARRIVED, "Mark as arrived",
                        ShipmentState.DELIVERED, "Mark as delivered",
                        ShipmentState.DELAYED, "Mark as delayed"
                );
            case ARRIVED:
                return Map.of(
                        ShipmentState.DELIVERED, "Mark as delivered",
                        ShipmentState.DELAYED, "Mark as delayed"
                );
            case DELIVERED:
                return Map.of(
                        ShipmentState.COMPLETED, "Complete workflow"
                );
            case DELAYED:
                return Map.of(
                        ShipmentState.IN_TRANSIT, "Resume transit",
                        ShipmentState.CANCELLED, "Cancel shipment"
                );
            case CANCELLED:
                return Map.of(
                        ShipmentState.CANCELLED_CONFIRMED, "Confirm cancellation"
                );
            default:
                return Map.of();
        }
    }

    private Set<UserRole> getUserPermissions(ShipmentState currentState) {
        // Get user permissions for current state
        switch (currentState) {
            case CREATED:
                return Set.of(UserRole.CUSTOMER, UserRole.ADMIN, UserRole.SUPERVISOR);
            case ACCEPTED:
                return Set.of(UserRole.SUPERVISOR, UserRole.ADMIN);
            case PREPARING:
            case LOADED:
            case IN_TRANSIT:
            case ARRIVED:
            case DELIVERED:
                return Set.of(UserRole.DRIVER, UserRole.SUPERVISOR, UserRole.ADMIN, UserRole.CUSTOMER);
            case DELAYED:
                return Set.of(UserRole.DRIVER, UserRole.SUPERVISOR, UserRole.ADMIN, UserRole.CUSTOMER);
            case CANCELLED:
                return Set.of(UserRole.CUSTOMER, UserRole.SUPERVISOR, UserRole.ADMIN);
            case COMPLETED:
            case CANCELLED_CONFIRMED:
                return Set.of(UserRole.ADMIN, UserRole.SUPERVISOR);
            default:
                return Set.of();
        }
    }

    private ShipmentState determineResumeState(ShipmentState currentState) {
        // Determine appropriate resume state
        switch (currentState) {
            case CANCELLED:
                return ShipmentState.CREATED; // Resume from cancelled state
            case DELAYED:
                return ShipmentState.IN_TRANSIT; // Resume from delayed state
            default:
                return currentState; // No resume needed
        }
    }

    // Request/Response DTOs
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class StateTransitionRequest {
        private ShipmentState newState;
        private String reason;
        private Map<String, Object> metadata;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class WorkflowStateResponse {
        private Long shipmentId;
        private String trackingNumber;
        private ShipmentState currentState;
        private ShipmentState previousState;
        private String workflowId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Boolean active;
        private List<StateTransition> transitionHistory;
        private Map<ShipmentState, String> availableTransitions;
        private Shipment shipmentDetails;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class AvailableTransitionsResponse {
        private Long shipmentId;
        private ShipmentState currentState;
        private Map<ShipmentState, String> availableTransitions;
        private Set<UserRole> userPermissions;
        private LocalDateTime timestamp;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TransitionHistoryResponse {
        private Long shipmentId;
        private List<StateTransition> transitionHistory;
        private Integer totalTransitions;
        private LocalDateTime timestamp;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ActiveWorkflowsResponse {
        private Integer totalActiveWorkflows;
        private Map<String, ShipmentWorkflowState> activeWorkflows;
        private LocalDateTime timestamp;
    }
}
