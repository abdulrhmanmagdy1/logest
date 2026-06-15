package com.edham.logistics.model;

/**
 * Maintenance types for categorizing vehicle service activities
 */
public enum MaintenanceType {
    // Preventive Maintenance
    ROUTINE_INSPECTION,
    OIL_CHANGE,
    TIRE_ROTATION,
    BRAKE_INSPECTION,
    FLUID_CHECK,
    FILTER_REPLACEMENT,
    BATTERY_CHECK,
    BELT_INSPECTION,
    
    // Corrective Maintenance
    ENGINE_REPAIR,
    TRANSMISSION_REPAIR,
    BRAKE_REPAIR,
    TIRE_REPAIR,
    ELECTRICAL_REPAIR,
    SUSPENSION_REPAIR,
    EXHAUST_REPAIR,
    COOLING_SYSTEM_REPAIR,
    
    // Predictive Maintenance
    CONDITION_MONITORING,
    VIBRATION_ANALYSIS,
    OIL_ANALYSIS,
    THERMOGRAPHY,
    ULTRASONIC_TESTING,
    
    // Emergency Maintenance
    BREAKDOWN_REPAIR,
    EMERGENCY_SERVICE,
    ROADSIDE_ASSISTANCE,
    TOWING_SERVICE,
    
    // Scheduled Maintenance
    PERIODIC_SERVICE,
    SEASONAL_SERVICE,
    REGISTRATION_RENEWAL,
    INSPECTION_SERVICE,
    
    // Specialized Maintenance
    DIAGNOSTIC_TESTING,
    COMPUTER_DIAGNOSTICS,
    EMISSIONS_TESTING,
    SAFETY_INSPECTION,
    PERFORMANCE_TUNING,
    
    // Fleet Maintenance
    FLEET_SERVICE,
    MULTI_VEHICLE_SERVICE,
    PREVENTIVE_FLEET_SERVICE,
    
    // Body and Interior
    BODY_REPAIR,
    PAINT_WORK,
    INTERIOR_REPAIR,
    WINDSHIELD_REPLACEMENT,
    DETAILING_SERVICE,
    
    // Heavy Maintenance
    OVERHAUL_SERVICE,
    MAJOR_REPAIR,
    REBUILD_SERVICE,
    RESTORATION_SERVICE,
    
    // Miscellaneous
    CUSTOM_MODIFICATION,
    ACCESSORY_INSTALLATION,
    UPHOLSTERY_SERVICE,
    AIR_CONDITIONING_SERVICE
}
