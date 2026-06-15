package com.edham.logistics.workflow;

import com.edham.logistics.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Shipment workflow data transfer objects
 * Defines all workflow-related DTOs for the shipment workflow engine
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ShipmentWorkflowState {
    private String shipmentId;
    private ShipmentState currentState;
    private ShipmentState previousState;
    private String workflowId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime archivedAt;
    private Boolean active;
    private List<StateTransition> transitionHistory;
    private Map<String, Object> metadata;
    private String assignedUserId;
    private String assignedRoleId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class StateTransition {
    private String transitionId;
    private ShipmentState fromState;
    private ShipmentState toState;
    private String userId;
    private String userRole;
    private String reason;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
    private Boolean isValid;
    private String errorMessage;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WorkflowValidationRequest {
    private Long shipmentId;
    private ShipmentState proposedState;
    private String userId;
    private String reason;
    private Map<String, Object> context;
    private Boolean forceTransition;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WorkflowValidationResponse {
    private Boolean valid;
    private String errorMessage;
    private String errorCode;
    private List<String> validationErrors;
    private Map<String, Object> validationDetails;
    private LocalDateTime timestamp;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WorkflowTransitionRequest {
    private Long shipmentId;
    private ShipmentState targetState;
    private String userId;
    private String reason;
    private Map<String, Object> transitionData;
    private Boolean skipValidation;
    private Boolean forceTransition;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WorkflowTransitionResponse {
    private Boolean success;
    private String workflowId;
    private ShipmentState previousState;
    private ShipmentState currentState;
    private String message;
    private String errorMessage;
    private String errorCode;
    private LocalDateTime timestamp;
    private Map<String, Object> transitionDetails;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WorkflowStateRequest {
    private Long shipmentId;
    private String userId;
    private String userRole;
    private Boolean includeHistory;
    private Boolean includeMetadata;
    private Boolean includeAvailableTransitions;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WorkflowStateResponse {
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
    private Set<UserRole> userPermissions;
    private Shipment shipmentDetails;
    private Map<String, Object> workflowMetadata;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AvailableTransitionsRequest {
    private Long shipmentId;
    private String userId;
    private String userRole;
    private ShipmentState currentState;
    private Boolean includePermissions;
    private Boolean includeBusinessRules;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AvailableTransitionsResponse {
    private Long shipmentId;
    private ShipmentState currentState;
    private Map<ShipmentState, String> availableTransitions;
    private Map<ShipmentState, TransitionPermission> transitionPermissions;
    private Set<UserRole> userPermissions;
    private Map<String, Object> businessRuleValidations;
    private LocalDateTime timestamp;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class TransitionPermission {
    private ShipmentState fromState;
    private ShipmentState toState;
    private Set<UserRole> allowedRoles;
    private List<String> requiredConditions;
    private List<String> businessRules;
    private Boolean requiresApproval;
    private String approvalRole;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class TransitionHistoryRequest {
    private Long shipmentId;
    private String userId;
    private ShipmentState fromState;
    private ShipmentState toState;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer limit;
    private Integer offset;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class TransitionHistoryResponse {
    private Long shipmentId;
    private List<StateTransition> transitionHistory;
    private Integer totalTransitions;
    private Integer totalCount;
    private Boolean hasNext;
    private Boolean hasPrevious;
    private LocalDateTime timestamp;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ActiveWorkflowsRequest {
    private String userId;
    private String userRole;
    private ShipmentState currentState;
    private LocalDateTime createdAfter;
    private LocalDateTime createdBefore;
    private Integer limit;
    private Integer offset;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class ActiveWorkflowsResponse {
    private Integer totalActiveWorkflows;
    private Map<String, ShipmentWorkflowState> activeWorkflows;
    private Map<ShipmentState, Long> stateDistribution;
    private Map<String, Long> roleDistribution;
    private LocalDateTime lastUpdated;
    private LocalDateTime timestamp;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WorkflowStatisticsRequest {
    private String userId;
    private String userRole;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<ShipmentState> states;
    private Boolean includeTransitions;
    private Boolean includePerformance;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WorkflowStatisticsResponse {
    private Map<ShipmentState, Long> stateDistribution;
    private Map<ShipmentState, Long> transitionDistribution;
    private Map<String, Object> performanceMetrics;
    private Map<String, Object> businessRuleViolations;
    private Map<String, Object> userPerformance;
    private Long totalWorkflows;
    private Double averageTransitionTime;
    private Double successRate;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private LocalDateTime timestamp;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WorkflowActionRequest {
    private Long shipmentId;
    private String actionType;
    private String userId;
    private String reason;
    private Map<String, Object> actionData;
    private Boolean requireApproval;
    private String approvalRole;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WorkflowActionResponse {
    private Boolean success;
    private String actionId;
    private String message;
    private String errorMessage;
    private LocalDateTime timestamp;
    private Map<String, Object> actionResults;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WorkflowEventDTO {
    private String eventId;
    private String eventType;
    private Long shipmentId;
    private String trackingNumber;
    private ShipmentState fromState;
    private ShipmentState toState;
    private String userId;
    private String userRole;
    private String reason;
    private LocalDateTime timestamp;
    private Map<String, Object> eventData;
    private Boolean broadcastToRoles;
    private List<String> targetRoles;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WorkflowNotificationDTO {
    private String notificationId;
    private String type;
    private String title;
    private String message;
    private Long shipmentId;
    private String trackingNumber;
    private ShipmentState currentState;
    private String userId;
    private String userRole;
    private LocalDateTime timestamp;
    private Map<String, Object> notificationData;
    private List<String> recipients;
    private String priority;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WorkflowAuditLogDTO {
    private String logId;
    private Long shipmentId;
    private String workflowId;
    private String action;
    private ShipmentState fromState;
    private ShipmentState toState;
    private String userId;
    private String userRole;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime timestamp;
    private Boolean success;
    private String errorMessage;
    private Map<String, Object> auditData;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WorkflowConfigurationDTO {
    private String configId;
    private String configType;
    private String configName;
    private Map<ShipmentState, Set<ShipmentState>> stateTransitions;
    private Map<ShipmentState, Set<UserRole>> statePermissions;
    private Map<String, Object> businessRules;
    private Boolean active;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WorkflowTestRequest {
    private String testType;
    private Long shipmentId;
    private ShipmentState fromState;
    private ShipmentState toState;
    private String userId;
    private String userRole;
    private Map<String, Object> testData;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WorkflowTestResponse {
    private Boolean success;
    private String testId;
    private String testType;
    private Map<String, Object> testResults;
    private List<String> validationErrors;
    private List<String> businessRuleViolations;
    private LocalDateTime timestamp;
    private Map<String, Object> performanceMetrics;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WorkflowBulkOperationRequest {
    private String operationType;
    private List<Long> shipmentIds;
    private ShipmentState targetState;
    private String userId;
    private String reason;
    private Map<String, Object> operationData;
    private Boolean validateAll;
    private Boolean stopOnFirstError;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WorkflowBulkOperationResponse {
    private String operationId;
    private String operationType;
    private Integer totalShipments;
    private Integer successfulTransitions;
    private Integer failedTransitions;
    private List<Map<String, Object>> errors;
    private List<Map<String, Object>> results;
    private Boolean completed;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String requestId;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WorkflowRealTimeUpdateDTO {
    private String updateId;
    private Long shipmentId;
    private String trackingNumber;
    private ShipmentState currentState;
    private ShipmentState previousState;
    private String userId;
    private String userRole;
    private LocalDateTime timestamp;
    private Map<String, Object> updateData;
    private String updateType;
    private Boolean broadcastToAll;
    private List<String> targetRoles;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class WorkflowMetricsDTO {
    private String metricId;
    private String metricType;
    private Long shipmentId;
    private ShipmentState state;
    private Double duration;
    private LocalDateTime timestamp;
    private Map<String, Object> metricData;
    private String userId;
    private String userRole;
}

// Enums for workflow states and actions
enum WorkflowActionType {
    TRANSITION_STATE,
    CANCEL_WORKFLOW,
    RESUME_WORKFLOW,
    FORCE_TRANSITION,
    VALIDATE_TRANSITION,
    GET_HISTORY,
    GET_STATISTICS,
    BULK_OPERATION,
    TEST_WORKFLOW
}

enum WorkflowEventType {
    STATE_TRANSITION,
    WORKFLOW_INITIALIZED,
    WORKFLOW_COMPLETED,
    WORKFLOW_CANCELLED,
    WORKFLOW_RESUMED,
    BUSINESS_RULE_VIOLATION,
    TRANSITION_VALIDATION_FAILED,
    FORCE_TRANSITION_EXECUTED,
    BULK_OPERATION_COMPLETED
}

enum WorkflowNotificationType {
    STATE_CHANGE,
    TRANSITION_COMPLETED,
    WORKFLOW_CANCELLED,
    WORKFLOW_COMPLETED,
    BUSINESS_RULE_VIOLATION,
    DELAY_ALERT,
    ESCALATION_REQUIRED,
    APPROVAL_REQUIRED
}

enum WorkflowTestType {
    STATE_TRANSITION_VALIDATION,
    BUSINESS_RULE_VALIDATION,
    PERMISSION_VALIDATION,
    PERFORMANCE_TEST,
    STRESS_TEST,
    INTEGRATION_TEST
}
