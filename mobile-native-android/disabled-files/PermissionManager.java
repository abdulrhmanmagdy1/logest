// package com.edham.logistics.security;

import com.edham.logistics.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central permission manager for enterprise-grade RBAC system
 * Manages permissions, roles, and access control policies
 */
@Slf4j
@Component
public class PermissionManager {

    private final RoleBasedAccessControl rbac;
    private final UserRepository userRepository;
    
    // Permission cache for performance
    private final Map<Long, Set<Permission>> userPermissionCache = new ConcurrentHashMap<>();
    private final Map<Long, Long> lastPermissionUpdate = new ConcurrentHashMap<>();

    @Autowired
    public PermissionManager(RoleBasedAccessControl rbac, 
                           UserRepository userRepository) {
        this.rbac = rbac;
        this.userRepository = userRepository;
    }

    /**
     * Check if user has specific permission
     */
    public boolean hasPermission(Long userId, Permission permission) {
        try {
            // Check cache first
            Set<Permission> cachedPermissions = getCachedPermissions(userId);
            if (cachedPermissions != null) {
                return cachedPermissions.contains(permission);
            }

            // Fallback to RBAC check
            return rbac.hasPermission(userId, permission);

        } catch (Exception e) {
            log.error("Error checking permission {} for user {}: {}", permission, userId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Check if user has any of the specified permissions
     */
    public boolean hasAnyPermission(Long userId, Permission... permissions) {
        try {
            Set<Permission> cachedPermissions = getCachedPermissions(userId);
            if (cachedPermissions != null) {
                return Arrays.stream(permissions).anyMatch(cachedPermissions::contains);
            }

            return rbac.hasAnyPermission(userId, permissions);

        } catch (Exception e) {
            log.error("Error checking any permissions for user {}: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Check if user has all of the specified permissions
     */
    public boolean hasAllPermissions(Long userId, Permission... permissions) {
        try {
            Set<Permission> cachedPermissions = getCachedPermissions(userId);
            if (cachedPermissions != null) {
                return Arrays.stream(permissions).allMatch(cachedPermissions::contains);
            }

            return rbac.hasAllPermissions(userId, permissions);

        } catch (Exception e) {
            log.error("Error checking all permissions for user {}: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Check if user can access specific endpoint
     */
    public boolean canAccessEndpoint(Long userId, String endpoint, String method) {
        try {
            return rbac.canAccessEndpoint(userId, endpoint, method);

        } catch (Exception e) {
            log.error("Error checking endpoint access for user {}: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Check if user can access resource with specific action
     */
    public boolean canAccessResource(Long userId, String resourceType, String action) {
        try {
            return rbac.canAccessResource(userId, resourceType, action);

        } catch (Exception e) {
            log.error("Error checking resource access for user {}: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Check if user can access their own resource
     */
    public boolean canAccessOwnResource(Long userId, String resourceType, Long resourceId) {
        try {
            return rbac.canAccessOwnResource(userId, resourceType, resourceId);

        } catch (Exception e) {
            log.error("Error checking own resource access for user {}: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Get user permissions with caching
     */
    public Set<Permission> getUserPermissions(Long userId) {
        try {
            return getCachedPermissions(userId);

        } catch (Exception e) {
            log.error("Error getting user permissions: {}", e.getMessage(), e);
            return Collections.emptySet();
        }
    }

    /**
     * Get user role
     */
    public UserRole getUserRole(Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            return userOpt.map(User::getRole).orElse(null);

        } catch (Exception e) {
            log.error("Error getting user role: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Refresh user permissions cache
     */
    public void refreshUserPermissions(Long userId) {
        try {
            Set<Permission> permissions = rbac.getUserPermissions(userId);
            userPermissionCache.put(userId, permissions);
            lastPermissionUpdate.put(userId, System.currentTimeMillis());
            
            log.debug("Refreshed permissions cache for user: {}", userId);

        } catch (Exception e) {
            log.error("Error refreshing user permissions: {}", e.getMessage(), e);
        }
    }

    /**
     * Clear user permissions cache
     */
    public void clearUserPermissionsCache(Long userId) {
        userPermissionCache.remove(userId);
        lastPermissionUpdate.remove(userId);
        
        log.debug("Cleared permissions cache for user: {}", userId);
    }

    /**
     * Clear all permissions cache
     */
    public void clearAllPermissionsCache() {
        userPermissionCache.clear();
        lastPermissionUpdate.clear();
        
        log.info("Cleared all permissions cache");
    }

    /**
     * Get cached permissions with TTL
     */
    private Set<Permission> getCachedPermissions(Long userId) {
        Long lastUpdate = lastPermissionUpdate.get(userId);
        long currentTime = System.currentTimeMillis();
        
        // Cache TTL: 5 minutes
        long cacheTTL = 5 * 60 * 1000;
        
        if (lastUpdate != null && (currentTime - lastUpdate) < cacheTTL) {
            return userPermissionCache.get(userId);
        }
        
        // Refresh cache
        Set<Permission> permissions = rbac.getUserPermissions(userId);
        userPermissionCache.put(userId, permissions);
        lastPermissionUpdate.put(userId, currentTime);
        
        return permissions;
    }

    /**
     * Validate user access with detailed result
     */
    public AccessValidationResult validateAccess(Long userId, String resource, String action) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return AccessValidationResult.builder()
                        .valid(false)
                        .error("User not found")
                        .build();
            }

            User user = userOpt.get();
            Set<Permission> permissions = getUserPermissions(userId);
            
            // Check resource access
            boolean canAccess = canAccessResource(userId, resource, action);
            
            return AccessValidationResult.builder()
                    .valid(canAccess)
                    .userId(userId)
                    .userRole(user.getRole())
                    .resource(resource)
                    .action(action)
                    .userPermissions(permissions)
                    .timestamp(System.currentTimeMillis())
                    .build();

        } catch (Exception e) {
            log.error("Error validating access: {}", e.getMessage(), e);
            return AccessValidationResult.builder()
                    .valid(false)
                    .error("Validation error: " + e.getMessage())
                    .build();
        }
    }

    /**
     * Get role-based UI configuration
     */
    public RoleUIConfiguration getRoleUIConfiguration(UserRole role) {
        try {
            Set<Permission> permissions = rbac.getRolePermissions(role);
            
            return RoleUIConfiguration.builder()
                    .role(role)
                    .permissions(permissions)
                    .accessibleEndpoints(getAccessibleEndpoints(role))
                    .accessibleResources(getAccessibleResources(role))
                    .uiComponents(getUIComponents(role))
                    .navigationItems(getNavigationItems(role))
                    .build();

        } catch (Exception e) {
            log.error("Error getting role UI configuration: {}", e.getMessage(), e);
            return RoleUIConfiguration.builder()
                    .role(role)
                    .permissions(Collections.emptySet())
                    .build();
        }
    }

    /**
     * Get accessible endpoints for role
     */
    private Set<String> getAccessibleEndpoints(UserRole role) {
        Set<Permission> permissions = rbac.getRolePermissions(role);
        Set<String> endpoints = new HashSet<>();
        
        Map<String, Set<String>> endpointPerms = rbac.getAllEndpointPermissions();
        
        for (Map.Entry<String, Set<String>> entry : endpointPerms.entrySet()) {
            for (String perm : entry.getValue()) {
                if (permissions.stream().anyMatch(p -> p.name().equals(perm))) {
                    endpoints.add(entry.getKey());
                    break;
                }
            }
        }
        
        return endpoints;
    }

    /**
     * Get accessible resources for role
     */
    private Set<String> getAccessibleResources(UserRole role) {
        Set<Permission> permissions = rbac.getRolePermissions(role);
        Set<String> resources = new HashSet<>();
        
        Map<String, Set<Permission>> resourcePerms = rbac.getAllResourcePermissions();
        
        for (Map.Entry<String, Set<Permission>> entry : resourcePerms.entrySet()) {
            if (!Collections.disjoint(permissions, entry.getValue())) {
                resources.add(entry.getKey());
            }
        }
        
        return resources;
    }

    /**
     * Get UI components for role
     */
    private Set<String> getUIComponents(UserRole role) {
        Set<String> components = new HashSet<>();
        
        switch (role) {
            case CLIENT:
                components.addAll(Arrays.asList(
                        "ShipmentList", "CreateShipment", "TrackShipment", 
                        "Profile", "Wallet", "Notifications"
                ));
                break;
            case DRIVER:
                components.addAll(Arrays.asList(
                        "TaskList", "LocationTracker", "ShipmentStatus", 
                        "Earnings", "Schedule", "Profile"
                ));
                break;
            case ADMIN:
                components.addAll(Arrays.asList(
                        "UserManagement", "RoleManagement", "PermissionManagement",
                        "SystemConfiguration", "Reports", "Dashboard"
                ));
                break;
            case ACCOUNTANT:
                components.addAll(Arrays.asList(
                        "InvoiceManagement", "PaymentManagement", "FinancialReports",
                        "TaxReports", "Analytics", "Dashboard"
                ));
                break;
        }
        
        return components;
    }

    /**
     * Get navigation items for role
     */
    private List<NavigationItem> getNavigationItems(UserRole role) {
        List<NavigationItem> items = new ArrayList<>();
        
        switch (role) {
            case CLIENT:
                items.addAll(Arrays.asList(
                        NavigationItem.builder().name("Dashboard").icon("dashboard").path("/dashboard").build(),
                        NavigationItem.builder().name("Shipments").icon("package").path("/shipments").build(),
                        NavigationItem.builder().name("Track").icon("track").path("/track").build(),
                        NavigationItem.builder().name("Profile").icon("person").path("/profile").build(),
                        NavigationItem.builder().name("Wallet").icon("wallet").path("/wallet").build()
                ));
                break;
            case DRIVER:
                items.addAll(Arrays.asList(
                        NavigationItem.builder().name("Dashboard").icon("dashboard").path("/dashboard").build(),
                        NavigationItem.builder().name("Tasks").icon("task").path("/tasks").build(),
                        NavigationItem.builder().name("Location").icon("location").path("/location").build(),
                        NavigationItem.builder().name("Earnings").icon("earnings").path("/earnings").build(),
                        NavigationItem.builder().name("Schedule").icon("schedule").path("/schedule").build()
                ));
                break;
            case ADMIN:
                items.addAll(Arrays.asList(
                        NavigationItem.builder().name("Dashboard").icon("dashboard").path("/admin/dashboard").build(),
                        NavigationItem.builder().name("Users").icon("people").path("/admin/users").build(),
                        NavigationItem.builder().name("Roles").icon("security").path("/admin/roles").build(),
                        NavigationItem.builder().name("Permissions").icon("lock").path("/admin/permissions").build(),
                        NavigationItem.builder().name("System").icon("settings").path("/admin/system").build(),
                        NavigationItem.builder().name("Reports").icon("analytics").path("/admin/reports").build()
                ));
                break;
            case ACCOUNTANT:
                items.addAll(Arrays.asList(
                        NavigationItem.builder().name("Dashboard").icon("dashboard").path("/accountant/dashboard").build(),
                        NavigationItem.builder().name("Invoices").icon("receipt").path("/accountant/invoices").build(),
                        NavigationItem.builder().name("Payments").icon("payment").path("/accountant/payments").build(),
                        NavigationItem.builder().name("Reports").icon("analytics").path("/accountant/reports").build(),
                        NavigationItem.builder().name("Tax").icon("tax").path("/accountant/tax").build()
                ));
                break;
        }
        
        return items;
    }

    /**
     * Get permission statistics
     */
    public PermissionStatistics getPermissionStatistics() {
        try {
            Map<String, Set<Permission>> allRolePerms = rbac.getAllRolePermissions();
            Map<String, Set<String>> allEndpointPerms = rbac.getAllEndpointPermissions();
            Map<String, Set<Permission>> allResourcePerms = rbac.getAllResourcePermissions();
            
            return PermissionStatistics.builder()
                    .totalRoles(allRolePerms.size())
                    .totalPermissions(Permission.values().length)
                    .totalEndpoints(allEndpointPerms.size())
                    .totalResources(allResourcePerms.size())
                    .cachedUsers(userPermissionCache.size())
                    .roleDistribution(getRoleDistribution())
                    .permissionDistribution(getPermissionDistribution())
                    .lastUpdated(System.currentTimeMillis())
                    .build();

        } catch (Exception e) {
            log.error("Error getting permission statistics: {}", e.getMessage(), e);
            return PermissionStatistics.builder().build();
        }
    }

    /**
     * Get role distribution
     */
    private Map<UserRole, Long> getRoleDistribution() {
        Map<UserRole, Long> distribution = new HashMap<>();
        
        try {
            List<User> allUsers = userRepository.findAll();
            
            for (User user : allUsers) {
                distribution.merge(user.getRole(), 1L, Long::sum);
            }

        } catch (Exception e) {
            log.error("Error getting role distribution: {}", e.getMessage(), e);
        }
        
        return distribution;
    }

    /**
     * Get permission distribution
     */
    private Map<Permission, Long> getPermissionDistribution() {
        Map<Permission, Long> distribution = new HashMap<>();
        
        try {
            Map<String, Set<Permission>> allRolePerms = rbac.getAllRolePermissions();
            
            for (Set<Permission> permissions : allRolePerms.values()) {
                for (Permission permission : permissions) {
                    distribution.merge(permission, 1L, Long::sum);
                }
            }

        } catch (Exception e) {
            log.error("Error getting permission distribution: {}", e.getMessage(), e);
        }
        
        return distribution;
    }

    // Result classes
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class AccessValidationResult {
        private Boolean valid;
        private String error;
        private Long userId;
        private UserRole userRole;
        private String resource;
        private String action;
        private Set<Permission> userPermissions;
        private Long timestamp;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class RoleUIConfiguration {
        private UserRole role;
        private Set<Permission> permissions;
        private Set<String> accessibleEndpoints;
        private Set<String> accessibleResources;
        private Set<String> uiComponents;
        private List<NavigationItem> navigationItems;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class NavigationItem {
        private String name;
        private String icon;
        private String path;
        private Set<String> requiredPermissions;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PermissionStatistics {
        private Integer totalRoles;
        private Integer totalPermissions;
        private Integer totalEndpoints;
        private Integer totalResources;
        private Integer cachedUsers;
        private Map<UserRole, Long> roleDistribution;
        private Map<Permission, Long> permissionDistribution;
        private Long lastUpdated;
    }
}
