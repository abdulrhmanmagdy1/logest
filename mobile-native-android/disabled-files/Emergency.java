package com.edham.logistics.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import com.edham.logistics.dto.LocationDTO;
import java.time.LocalDateTime;

/**
 * Emergency event model for critical situations
 * Supports ultra-fast emergency response and tracking
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "emergencies")
public class Emergency {

    @Id
    private String id;

    @Indexed
    private EmergencyType type;

    @Indexed
    private EmergencySeverity severity;

    @Indexed
    private Long driverId;

    @Indexed
    private Long shipmentId;

    @Indexed
    private Long vehicleId;

    private LocationDTO location;

    private String description;

    @Indexed
    private EmergencyStatus status;

    private String resolution;

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    private LocalDateTime updatedAt;

    private boolean requiresImmediateAction;

    private String assignedTo; // Admin or supervisor ID

    private LocalDateTime assignedAt;

    private String resolvedBy; // Admin or supervisor ID

    private String additionalNotes;

    private EmergencyMetadata metadata;

    // Pre-save hook to set timestamps
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    // Pre-update hook
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Check if emergency is active
     */
    public boolean isActive() {
        return status == EmergencyStatus.ACTIVE;
    }

    /**
     * Check if emergency is resolved
     */
    public boolean isResolved() {
        return status == EmergencyStatus.RESOLVED;
    }

    /**
     * Get resolution duration in minutes
     */
    public long getResolutionDurationMinutes() {
        if (resolvedAt == null || createdAt == null) {
            return 0;
        }
        return java.time.Duration.between(createdAt, resolvedAt).toMinutes();
    }

    /**
     * Check if emergency is critical
     */
    public boolean isCritical() {
        return severity == EmergencySeverity.CRITICAL || 
               type == EmergencyType.ACCIDENT ||
               type == EmergencyType.MEDICAL_EMERGENCY;
    }

    /**
     * Check if emergency requires immediate action
     */
    public boolean requiresImmediateAction() {
        return requiresImmediateAction || isCritical();
    }

    /**
     * Get emergency priority level (1-5, 1 being highest)
     */
    public int getPriorityLevel() {
        int priority = 5; // Default lowest priority

        switch (severity) {
            case CRITICAL:
                priority = 1;
                break;
            case HIGH:
                priority = 2;
                break;
            case MEDIUM:
                priority = 3;
                break;
            case LOW:
                priority = 4;
                break;
        }

        // Adjust priority based on type
        if (type == EmergencyType.ACCIDENT || type == EmergencyType.MEDICAL_EMERGENCY) {
            priority = Math.min(priority, 1); // Highest priority
        } else if (type == EmergencyType.VEHICLE_BREAKDOWN || type == EmergencyType.SECURITY_THREAT) {
            priority = Math.min(priority, 2); // High priority
        }

        return priority;
    }

    /**
     * Get emergency summary for notifications
     */
    public String getSummary() {
        return String.format("%s - %s: %s", 
                type.name(), 
                severity.name(), 
                description != null ? description.substring(0, Math.min(description.length(), 100)) : "No description");
    }

    /**
     * Check if emergency is overdue for resolution
     */
    public boolean isOverdue() {
        if (isResolved()) {
            return false;
        }

        LocalDateTime expectedResolutionTime = createdAt.plusMinutes(getExpectedResolutionTimeMinutes());
        return LocalDateTime.now().isAfter(expectedResolutionTime);
    }

    /**
     * Get expected resolution time in minutes based on severity
     */
    private int getExpectedResolutionTimeMinutes() {
        switch (severity) {
            case CRITICAL:
                return 5; // 5 minutes
            case HIGH:
                return 15; // 15 minutes
            case MEDIUM:
                return 30; // 30 minutes
            case LOW:
                return 60; // 1 hour
            default:
                return 30;
        }
    }

    /**
     * Get emergency status for display
     */
    public String getDisplayStatus() {
        if (isResolved()) {
            return "Resolved";
        } else if (isOverdue()) {
            return "Overdue";
        } else {
            return "Active";
        }
    }

    /**
     * Mark as resolved
     */
    public void markAsResolved(String resolvedBy, String resolution) {
        this.status = EmergencyStatus.RESOLVED;
        this.resolvedAt = LocalDateTime.now();
        this.resolvedBy = resolvedBy;
        this.resolution = resolution;
        this.requiresImmediateAction = false;
    }

    /**
     * Assign to admin/supervisor
     */
    public void assignTo(String assignedTo) {
        this.assignedTo = assignedTo;
        this.assignedAt = LocalDateTime.now();
    }

    /**
     * Add additional note
     */
    public void addNote(String note) {
        if (additionalNotes == null) {
            additionalNotes = note;
        } else {
            additionalNotes += "\n[" + LocalDateTime.now() + "] " + note;
        }
    }
}
