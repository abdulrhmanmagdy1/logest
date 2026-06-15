package com.edham.logistics.security;

/**
 * Permission enumeration for Role-Based Access Control (RBAC)
 * Defines all possible permissions in the system
 */
public enum Permission {
    
    // Shipment permissions
    CREATE_SHIPMENT("Create new shipments"),
    VIEW_SHIPMENTS("View all shipments"),
    VIEW_OWN_SHIPMENTS("View own shipments"),
    UPDATE_SHIPMENT("Update shipment details"),
    DELETE_SHIPMENT("Delete shipments"),
    TRACK_SHIPMENT("Track shipments"),
    TRACK_OWN_SHIPMENTS("Track own shipments"),
    CANCEL_SHIPMENT("Cancel shipments"),
    CANCEL_OWN_SHIPMENT("Cancel own shipments"),
    
    // Driver permissions
    VIEW_DRIVERS("View all drivers"),
    MANAGE_DRIVERS("Manage drivers"),
    UPDATE_DRIVER_LOCATION("Update driver location"),
    UPDATE_DRIVER_STATUS("Update driver status"),
    ASSIGN_DRIVER("Assign drivers to shipments"),
    
    // User permissions
    VIEW_USERS("View all users"),
    MANAGE_USERS("Manage users"),
    CREATE_USER("Create new users"),
    UPDATE_USER("Update user details"),
    DELETE_USER("Delete users"),
    VIEW_OWN_PROFILE("View own profile"),
    UPDATE_OWN_PROFILE("Update own profile"),
    
    // Financial permissions
    VIEW_INVOICES("View all invoices"),
    VIEW_OWN_INVOICES("View own invoices"),
    MANAGE_INVOICES("Manage invoices"),
    CREATE_INVOICE("Create invoices"),
    UPDATE_INVOICE("Update invoices"),
    DELETE_INVOICE("Delete invoices"),
    
    VIEW_PAYMENTS("View all payments"),
    VIEW_OWN_PAYMENTS("View own payments"),
    MANAGE_PAYMENTS("Manage payments"),
    PROCESS_PAYMENT("Process payments"),
    MAKE_PAYMENT("Make payments"),
    REFUND_PAYMENT("Process refunds"),
    
    // Report permissions
    VIEW_REPORTS("View all reports"),
    VIEW_OWN_REPORTS("View own reports"),
    GENERATE_REPORTS("Generate reports"),
    EXPORT_REPORTS("Export reports"),
    VIEW_FINANCIAL_REPORTS("View financial reports"),
    GENERATE_FINANCIAL_REPORTS("Generate financial reports"),
    EXPORT_FINANCIAL_DATA("Export financial data"),
    
    // Tax permissions
    VIEW_TAX_REPORTS("View tax reports"),
    MANAGE_TAX_SETTINGS("Manage tax settings"),
    CALCULATE_TAX("Calculate tax"),
    
    // Analytics permissions
    VIEW_ANALYTICS("View analytics"),
    VIEW_FINANCIAL_ANALYTICS("View financial analytics"),
    VIEW_OPERATIONAL_ANALYTICS("View operational analytics"),
    
    // System permissions
    SYSTEM_ADMINISTRATION("System administration"),
    MANAGE_ROLES("Manage user roles"),
    MANAGE_PERMISSIONS("Manage permissions"),
    VIEW_SYSTEM_LOGS("View system logs"),
    SYSTEM_BACKUP("System backup"),
    SYSTEM_RESTORE("System restore"),
    
    // Vehicle permissions
    VIEW_VEHICLES("View all vehicles"),
    MANAGE_VEHICLES("Manage vehicles"),
    ASSIGN_VEHICLE("Assign vehicles to drivers"),
    UPDATE_VEHICLE_STATUS("Update vehicle status"),
    VIEW_VEHICLE_MAINTENANCE("View vehicle maintenance"),
    MANAGE_VEHICLE_MAINTENANCE("Manage vehicle maintenance"),
    
    // Workshop permissions
    VIEW_WORKSHOP("View workshop operations"),
    MANAGE_WORKSHOP("Manage workshop operations"),
    VIEW_MAINTENANCE_SCHEDULE("View maintenance schedule"),
    MANAGE_MAINTENANCE_SCHEDULE("Manage maintenance schedule"),
    VIEW_PARTS_INVENTORY("View parts inventory"),
    MANAGE_PARTS_INVENTORY("Manage parts inventory"),
    
    // Earnings permissions
    VIEW_EARNINGS("View all earnings"),
    VIEW_OWN_EARNINGS("View own earnings"),
    MANAGE_EARNINGS("Manage earnings"),
    CALCULATE_EARNINGS("Calculate earnings"),
    
    // Schedule permissions
    VIEW_SCHEDULE("View all schedules"),
    VIEW_OWN_SCHEDULE("View own schedule"),
    MANAGE_SCHEDULE("Manage schedules"),
    UPDATE_SCHEDULE("Update schedules"),
    
    // Notification permissions
    VIEW_NOTIFICATIONS("View all notifications"),
    VIEW_OWN_NOTIFICATIONS("View own notifications"),
    MANAGE_NOTIFICATIONS("Manage notifications"),
    SEND_NOTIFICATIONS("Send notifications"),
    
    // Rating permissions
    VIEW_RATINGS("View all ratings"),
    RATE_SHIPMENT("Rate shipments"),
    MANAGE_RATINGS("Manage ratings"),
    
    // Address permissions
    VIEW_ADDRESSES("View all addresses"),
    MANAGE_ADDRESSES("Manage addresses"),
    VIEW_OWN_ADDRESSES("View own addresses"),
    MANAGE_OWN_ADDRESSES("Manage own addresses"),
    
    // Wallet permissions
    VIEW_WALLET("View all wallets"),
    VIEW_OWN_WALLET("View own wallet"),
    MANAGE_WALLET("Manage wallets"),
    UPDATE_WALLET("Update wallet balance"),
    
    // Workflow permissions
    INITIALIZE_WORKFLOW("Initialize workflow"),
    TRANSITION_WORKFLOW("Transition workflow state"),
    VIEW_WORKFLOW_STATE("View workflow state"),
    VIEW_WORKFLOW_HISTORY("View workflow history"),
    CANCEL_WORKFLOW("Cancel workflow"),
    FORCE_WORKFLOW_TRANSITION("Force workflow transition"),
    
    // Emergency permissions
    VIEW_EMERGENCIES("View emergencies"),
    MANAGE_EMERGENCIES("Manage emergencies"),
    CREATE_EMERGENCY("Create emergency"),
    UPDATE_EMERGENCY("Update emergency"),
    
    // Location permissions
    VIEW_LOCATIONS("View all locations"),
    UPDATE_LOCATIONS("Update locations"),
    TRACK_LOCATIONS("Track locations"),
    
    // File permissions
    VIEW_FILES("View all files"),
    UPLOAD_FILES("Upload files"),
    DELETE_FILES("Delete files"),
    MANAGE_FILES("Manage files"),
    
    // Configuration permissions
    VIEW_CONFIGURATIONS("View configurations"),
    MANAGE_CONFIGURATIONS("Manage configurations"),
    UPDATE_CONFIGURATIONS("Update configurations"),
    
    // Audit permissions
    VIEW_AUDIT_LOGS("View audit logs"),
    MANAGE_AUDIT_LOGS("Manage audit logs"),
    EXPORT_AUDIT_LOGS("Export audit logs");
    
    private final String description;
    
    Permission(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return this.name();
    }
}
