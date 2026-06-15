package com.edham.logistics.model;

/**
 * Maintenance status for tracking service progress
 */
public enum MaintenanceStatus {
    SCHEDULED,
    CONFIRMED,
    IN_PROGRESS,
    AWAITING_PARTS,
    ON_HOLD,
    COMPLETED,
    CANCELLED,
    REJECTED,
    APPROVED,
    INSPECTION_REQUIRED,
    QUALITY_CHECK,
    FOLLOW_UP_REQUIRED,
    CLOSED
}
