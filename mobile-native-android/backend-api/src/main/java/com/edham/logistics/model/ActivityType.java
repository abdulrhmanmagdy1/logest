package com.edham.logistics.model;

/**
 * Activity types for audit logging
 */
public enum ActivityType {
    EMERGENCY_ALERT,
    LOGIN,
    LOGOUT,
    SHIPMENT_CREATED,
    SHIPMENT_UPDATED,
    SHIPMENT_DELIVERED,
    DRIVER_LOCATION_UPDATE,
    PAYMENT_PROCESSED,
    INVOICE_GENERATED,
    VEHICLE_MAINTENANCE,
    SYSTEM_CONFIG_CHANGED
}
