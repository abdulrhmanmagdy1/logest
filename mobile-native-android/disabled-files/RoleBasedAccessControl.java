// // package com.edham.logistics.security;

import com.edham.logistics.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Role-Based Access Control (RBAC) system for enterprise-grade security
 * Centralized permission manager with role-based access control
 */
@Slf4j
@Component
public class RoleBasedAccessControl {

    private final UserRepository userRepository;
    private final Map<String, Set<Permission>> rolePermissions = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> endpointPermissions = new ConcurrentHashMap<>();
    private final Map<String, Set<Permission>> resourcePermissions = new ConcurrentHashMap<>();

    @Autowired
    public RoleBasedAccessControl(UserRepository userRepository) {
        this.userRepository = userRepository;
        initializePermissions();
    }

    /**
     * Initialize role-based permissions
     */
    private void initializePermissions() {
        log.info("Initializing Role-Based Access Control system");

        // Define permissions for each role
        Map<UserRole, Set<Permission>> permissions = new HashMap<>();

        // CLIENT permissions
        permissions.put(UserRole.CLIENT, Set.of(
                // Shipment permissions
                Permission.CREATE_SHIPMENT,
                Permission.VIEW_OWN_SHIPMENTS,
                Permission.TRACK_OWN_SHIPMENTS,
                Permission.CANCEL_OWN_SHIPMENT,
                
                // Profile permissions
                Permission.VIEW_OWN_PROFILE,
                Permission.UPDATE_OWN_PROFILE,
                
                // Payment permissions
                Permission.VIEW_OWN_PAYMENTS,
                Permission.MAKE_PAYMENT,
                
                // Notification permissions
                Permission.VIEW_OWN_NOTIFICATIONS,
                
                // Rating permissions
                Permission.RATE_SHIPMENT
        ));

        // DRIVER permissions
        permissions.put(UserRole.DRIVER, Set.of(
                // Shipment permissions
                Permission.VIEW_ASSIGNED_SHIPMENTS,
                Permission.UPDATE_SHIPMENT_STATUS,
                Permission.UPDATE_SHIPMENT_LOCATION,
                Permission.UPLOAD_PROOF_OF_DELIVERY,
                
                // Profile permissions
                Permission.VIEW_OWN_PROFILE,
                Permission.UPDATE_OWN_PROFILE,
                
                // Earnings permissions
                Permission.VIEW_OWN_EARNINGS,
                
                // Schedule permissions
                Permission.VIEW_OWN_SCHEDULE,
                
                // Notification permissions
                Permission.VIEW_OWN_NOTIFICATIONS
        ));

        // ADMIN permissions (full access)
        permissions.put(UserRole.ADMIN, Set.of(
                // All permissions
                Permission.values()
        ));

        // ACCOUNTANT permissions
        permissions.put(UserRole.ACCOUNTANT, Set.of(
                // Financial permissions
                Permission.VIEW_ALL_INVOICES,
                Permission.CREATE_INVOICE,
                Permission.UPDATE_INVOICE,
                Permission.VIEW_ALL_PAYMENTS,
                Permission.PROCESS_PAYMENT,
                
                // Report permissions
                Permission.VIEW_FINANCIAL_REPORTS,
                Permission.GENERATE_FINANCIAL_REPORTS,
                Permission.EXPORT_FINANCIAL_DATA,
                
                // Tax permissions
                Permission.VIEW_TAX_REPORTS,
                Permission.MANAGE_TAX_SETTINGS,
                
                // Analytics permissions
                Permission.VIEW_FINANCIAL_ANALYTICS
        ));

        // Store permissions in map
        for (Map.Entry<UserRole, Set<Permission>> entry : permissions.entrySet()) {
            rolePermissions.put(entry.getKey().name(), entry.getValue());
        }

        // Initialize endpoint permissions
        initializeEndpointPermissions();

        // Initialize resource permissions
        initializeResourcePermissions();

        log.info("RBAC system initialized with {} roles and {} permissions", 
                permissions.size(), Permission.values().length);
    }

    /**
     * Initialize endpoint permissions
     */
    private void initializeEndpointPermissions() {
        // Shipment endpoints
        endpointPermissions.put("/api/v1/shipments", Set.of(
                "CREATE_SHIPMENT", "VIEW_SHIPMENTS"
        ));
        
        endpointPermissions.put("/api/v1/shipments/{id}", Set.of(
                "VIEW_SHIPMENT", "UPDATE_SHIPMENT"
        ));
        
        endpointPermissions.put("/api/v1/shipments/{id}/track", Set.of(
                "TRACK_SHIPMENT"
        ));
        
        endpointPermissions.put("/api/v1/shipments/{id}/cancel", Set.of(
                "CANCEL_SHIPMENT"
        ));
        
        // Driver endpoints
        endpointPermissions.put("/api/v1/drivers", Set.of(
                "VIEW_DRIVERS", "MANAGE_DRIVERS"
        ));
        
        endpointPermissions.put("/api/v1/drivers/{id}/location", Set.of(
                "UPDATE_DRIVER_LOCATION"
        ));
        
        endpointPermissions.put("/api/v1/drivers/{id}/status", Set.of(
                "UPDATE_DRIVER_STATUS"
        ));
        
        // Admin endpoints
        endpointPermissions.put("/api/v1/admin/users", Set.of(
                "MANAGE_USERS"
        ));
        
        endpointPermissions.put("/api/v1/admin/roles", Set.of(
                "MANAGE_ROLES"
        ));
        
        endpointPermissions.put("/api/v1/admin/permissions", Set.of(
                "MANAGE_PERMISSIONS"
        ));
        
        endpointPermissions.put("/api/v1/admin/system", Set.of(
                "SYSTEM_ADMINISTRATION"
        ));
        
        // Accountant endpoints
        endpointPermissions.put("/api/v1/accountant/invoices", Set.of(
                "VIEW_INVOICES", "MANAGE_INVOICES"
        ));
        
        endpointPermissions.put("/api/v1/accountant/payments", Set.of(
                "VIEW_PAYMENTS", "MANAGE_PAYMENTS"
        ));
        
        endpointPermissions.put("/api/v1/accountant/reports", Set.of(
                "VIEW_REPORTS", "GENERATE_REPORTS"
        ));
        
        endpointPermissions.put("/api/v1/accountant/tax", Set.of(
                "VIEW_TAX_REPORTS", "MANAGE_TAX_SETTINGS"
        ));
        
        // Workflow endpoints
        endpointPermissions.put("/api/v1/workflow/shipment/{id}/initialize", Set.of(
                "INITIALIZE_WORKFLOW"
        ));
        
        endpointPermissions.put("/api/v1/workflow/shipment/{id}/transition", Set.of(
                "TRANSITION_WORKFLOW"
        ));
        
        endpointPermissions.put("/api/v1/workflow/shipment/{id}/state", Set.of(
                "VIEW_WORKFLOW_STATE"
        ));
        
        endpointPermissions.put("/api/v1/workflow/shipment/{id}/history", Set.of(
                "VIEW_WORKFLOW_HISTORY"
        ));
        
        endpointPermissions.put("/api/v1/workflow/shipment/{id}/cancel", Set.of(
                "CANCEL_WORKFLOW"
        ));
        
        endpointPermissions.put("/api/v1/workflow/shipment/{id}/force-transition", Set.of(
                "FORCE_WORKFLOW_TRANSITION"
        ));
    }

    /**
     * Initialize resource permissions
     */
    private void initializeResourcePermissions() {
        // Shipment resources
        resourcePermissions.put("shipment", Set.of(
                Permission.CREATE_SHIPMENT,
                Permission.VIEW_SHIPMENTS,
                Permission.UPDATE_SHIPMENT,
                Permission.DELETE_SHIPMENT,
                Permission.TRACK_SHIPMENT,
                Permission.CANCEL_SHIPMENT
        ));
        
        // Driver resources
        resourcePermissions.put("driver", Set.of(
                Permission.VIEW_DRIVERS,
                Permission.MANAGE_DRIVERS,
                Permission.UPDATE_DRIVER_LOCATION,
                Permission.UPDATE_DRIVER_STATUS,
                Permission.ASSIGN_DRIVER
        ));
        
        // User resources
        resourcePermissions.put("user", Set.of(
                Permission.VIEW_USERS,
                Permission.MANAGE_USERS,
                Permission.CREATE_USER,
                Permission.UPDATE_USER,
                Permission.DELETE_USER
        ));
        
        // Financial resources
        resourcePermissions.put("financial", Set.of(
                Permission.VIEW_INVOICES,
                Permission.MANAGE_INVOICES,
                Permission.VIEW_PAYMENTS,
                Permission.MANAGE_PAYMENTS,
                Permission.PROCESS_PAYMENT
        ));
        
        // Report resources
        resourcePermissions.put("report", Set.of(
                Permission.VIEW_REPORTS,
                Permission.GENERATE_REPORTS,
                Permission.EXPORT_REPORTS,
                Permission.VIEW_FINANCIAL_REPORTS,
                Permission.GENERATE_FINANCIAL_REPORTS
        ));
        
        // System resources
        resourcePermissions.put("system", Set.of(
                Permission.SYSTEM_ADMINISTRATION,
                Permission.MANAGE_ROLES,
                Permission.MANAGE_PERMISSIONS,
                Permission.VIEW_SYSTEM_LOGS,
                Permission.SYSTEM_BACKUP,
                Permission.SYSTEM_RESTORE
        ));
        
        // Workflow resources
        resourcePermissions.put("workflow", Set.of(
                Permission.INITIALIZE_WORKFLOW,
                Permission.TRANSITION_WORKFLOW,
                Permission.VIEW_WORKFLOW_STATE,
                Permission.VIEW_WORKFLOW_HISTORY,
                Permission.CANCEL_WORKFLOW,
                Permission.FORCE_WORKFLOW_TRANSITION
        ));
    }

    /**
     * Check if user has permission
     */
    public boolean hasPermission(Long userId, Permission permission) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                log.warn("User not found: {}", userId);
                return false;
            }

            User user = userOpt.get();
            Set<Permission> userPermissions = rolePermissions.get(user.getRole().name());
            
            boolean hasPermission = userPermissions != null && userPermissions.contains(permission);
            
            log.debug("Permission check for user {} (role: {}): {} = {}", 
                    userId, user.getRole(), permission, hasPermission);
            
            return hasPermission;

        } catch (Exception e) {
            log.error("Error checking permission for user {}: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Check if user has any of the specified permissions
     */
    public boolean hasAnyPermission(Long userId, Permission... permissions) {
        for (Permission permission : permissions) {
            if (hasPermission(userId, permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if user has all of the specified permissions
     */
    public boolean hasAllPermissions(Long userId, Permission... permissions) {
        for (Permission permission : permissions) {
            if (!hasPermission(userId, permission)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if user can access endpoint
     */
    public boolean canAccessEndpoint(Long userId, String endpoint, String method) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                log.warn("User not found: {}", userId);
                return false;
            }

            User user = userOpt.get();
            String endpointKey = getEndpointKey(endpoint, method);
            Set<String> requiredPermissions = endpointPermissions.get(endpointKey);
            
            if (requiredPermissions == null || requiredPermissions.isEmpty()) {
                log.debug("No permissions required for endpoint: {}", endpointKey);
                return true;
            }

            Set<Permission> userPermissions = rolePermissions.get(user.getRole().name());
            
            for (String requiredPermission : requiredPermissions) {
                Permission permission = Permission.valueOf(requiredPermission);
                if (userPermissions == null || !userPermissions.contains(permission)) {
                    log.debug("Access denied for user {} (role: {}) to {}: missing permission {}", 
                            userId, user.getRole(), endpointKey, requiredPermission);
                    return false;
                }
            }

            log.debug("Access granted for user {} (role: {}) to {}", 
                    userId, user.getRole(), endpointKey);
            return true;

        } catch (Exception e) {
            log.error("Error checking endpoint access for user {}: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Check if user can access resource
     */
    public boolean canAccessResource(Long userId, String resourceType, String action) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                log.warn("User not found: {}", userId);
                return false;
            }

            User user = userOpt.get();
            Set<Permission> resourcePerms = resourcePermissions.get(resourceType);
            
            if (resourcePerms == null || resourcePerms.isEmpty()) {
                log.debug("No permissions required for resource: {}", resourceType);
                return true;
            }

            // Convert action to permission
            Permission requiredPermission = getPermissionFromAction(resourceType, action);
            if (requiredPermission == null) {
                log.debug("No permission mapping for action: {} on resource: {}", action, resourceType);
                return false;
            }

            Set<Permission> userPermissions = rolePermissions.get(user.getRole().name());
            boolean hasPermission = userPermissions != null && userPermissions.contains(requiredPermission);

            log.debug("Resource access check for user {} (role: {}): {} {} = {}", 
                    userId, user.getRole(), resourceType, action, hasPermission);

            return hasPermission;

        } catch (Exception e) {
            log.error("Error checking resource access for user {}: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get user permissions
     */
    public Set<Permission> getUserPermissions(Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return Collections.emptySet();
            }

            User user = userOpt.get();
            return rolePermissions.getOrDefault(user.getRole().name(), Collections.emptySet());

        } catch (Exception e) {
            log.error("Error getting user permissions: {}", e.getMessage(), e);
            return Collections.emptySet();
        }
    }

    /**
     * Get role permissions
     */
    public Set<Permission> getRolePermissions(UserRole role) {
        return rolePermissions.getOrDefault(role.name(), Collections.emptySet());
    }

    /**
     * Check if user can access their own resource
     */
    public boolean canAccessOwnResource(Long userId, String resourceType, Long resourceId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return false;
            }

            User user = userOpt.get();
            Set<Permission> userPermissions = rolePermissions.get(user.getRole().name());
            
            // Check if user has permission to access their own resources
            switch (resourceType.toLowerCase()) {
                case "shipment":
                    return userPermissions.contains(Permission.VIEW_OWN_SHIPMENTS) ||
                           userPermissions.contains(Permission.VIEW_SHIPMENTS);
                case "profile":
                    return userPermissions.contains(Permission.VIEW_OWN_PROFILE);
                case "earnings":
                    return userPermissions.contains(Permission.VIEW_OWN_EARNINGS);
                case "schedule":
                    return userPermissions.contains(Permission.VIEW_OWN_SCHEDULE);
                case "notifications":
                    return userPermissions.contains(Permission.VIEW_OWN_NOTIFICATIONS);
                case "payments":
                    return userPermissions.contains(Permission.VIEW_OWN_PAYMENTS) ||
                           userPermissions.contains(Permission.VIEW_PAYMENTS);
                default:
                    return false;
            }

        } catch (Exception e) {
            log.error("Error checking own resource access: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get endpoint key for permission checking
     */
    private String getEndpointKey(String endpoint, String method) {
        // Remove path variables for pattern matching
        String normalizedEndpoint = endpoint.replaceAll("/\\d+", "{id}");
        
        // Combine method and endpoint for more specific permissions
        return method.toUpperCase() + ":" + normalizedEndpoint;
    }

    /**
     * Convert action to permission
     */
    private Permission getPermissionFromAction(String resourceType, String action) {
        String actionKey = (resourceType + "_" + action).toUpperCase();
        
        try {
            return Permission.valueOf(actionKey);
        } catch (IllegalArgumentException e) {
            // Try to find a matching permission
            for (Permission permission : Permission.values()) {
                if (permission.name().contains(action.toUpperCase()) || 
                    permission.name().contains(resourceType.toUpperCase())) {
                    return permission;
                }
            }
            return null;
        }
    }

    /**
     * Get all permissions for a role
     */
    public Map<String, Set<Permission>> getAllRolePermissions() {
        return new HashMap<>(rolePermissions);
    }

    /**
     * Get all endpoint permissions
     */
    public Map<String, Set<String>> getAllEndpointPermissions() {
        return new HashMap<>(endpointPermissions);
    }

    /**
     * Get all resource permissions
     */
    public Map<String, Set<Permission>> getAllResourcePermissions() {
        return new HashMap<>(resourcePermissions);
    }

    /**
     * Check if role exists
     */
    public boolean roleExists(String roleName) {
        try {
            UserRole.valueOf(roleName);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Get all available roles
     */
    public List<UserRole> getAllRoles() {
        return Arrays.asList(UserRole.values());
    }

    /**
     * Validate user role and permissions
     */
    public ValidationResult validateUserAccess(Long userId, String requiredRole, Set<Permission> requiredPermissions) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ValidationResult.builder()
                        .valid(false)
                        .error("User not found")
                        .build();
            }

            User user = userOpt.get();
            
            // Check role
            if (requiredRole != null && !user.getRole().name().equals(requiredRole)) {
                return ValidationResult.builder()
                        .valid(false)
                        .error("User role does not match required role")
                        .userRole(user.getRole().name())
                        .requiredRole(requiredRole)
                        .build();
            }

            // Check permissions
            Set<Permission> userPermissions = rolePermissions.get(user.getRole().name());
            if (requiredPermissions != null) {
                for (Permission permission : requiredPermissions) {
                    if (userPermissions == null || !userPermissions.contains(permission)) {
                        return ValidationResult.builder()
                                .valid(false)
                                .error("User lacks required permission: " + permission)
                                .missingPermission(permission)
                                .build();
                    }
                }
            }

            return ValidationResult.builder()
                    .valid(true)
                    .userRole(user.getRole().name())
                    .userPermissions(userPermissions)
                    .build();

        } catch (Exception e) {
            log.error("Error validating user access: {}", e.getMessage(), e);
            return ValidationResult.builder()
                    .valid(false)
                    .error("Validation error: " + e.getMessage())
                    .build();
        }
    }

    // Result classes
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ValidationResult {
        private Boolean valid;
        private String error;
        private String userRole;
        private String requiredRole;
        private Set<Permission> userPermissions;
        private Permission missingPermission;
    }
}
