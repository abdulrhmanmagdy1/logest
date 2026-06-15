// package com.edham.logistics.security;

import com.edham.logistics.dto.UnifiedResponseDTO;
import com.edham.logistics.model.*;
import com.edham.logistics.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Role-Based Security Controller for enterprise-grade RBAC
 * Provides security management endpoints with role-based access control
 */
@RestController
@RequestMapping("/api/v1/security")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class RoleBasedSecurityController {

    private final PermissionManager permissionManager;
    private final RoleBasedAccessControl rbac;
    private final UserRepository userRepository;

    @Autowired
    public RoleBasedSecurityController(PermissionManager permissionManager,
                                    RoleBasedAccessControl rbac,
                                    UserRepository userRepository) {
        this.permissionManager = permissionManager;
        this.rbac = rbac;
        this.userRepository = userRepository;
    }

    /**
     * Get current user permissions
     */
    @GetMapping("/permissions/current")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<UnifiedResponseDTO<Map<String, Object>>> getCurrentUserPermissions() {
        try {
            // Get current user ID (from security context)
            Long userId = getCurrentUserId();
            
            if (userId == null) {
                return ResponseEntity.badRequest().body(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(false)
                            .error("User not authenticated")
                            .timestamp(LocalDateTime.now())
                            .build()
                );
            }

            Set<Permission> permissions = permissionManager.getUserPermissions(userId);
            UserRole userRole = permissionManager.getUserRole(userId);

            Map<String, Object> responseData = Map.of(
                    "userId", userId,
                    "role", userRole,
                    "permissions", permissions,
                    "permissionCount", permissions.size(),
                    "roleUIConfiguration", permissionManager.getRoleUIConfiguration(userRole)
            );

            return ResponseEntity.ok(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(true)
                            .data(responseData)
                            .message("User permissions retrieved successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error getting current user permissions: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(false)
                            .error("Failed to retrieve permissions: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Check specific permission
     */
    @PostMapping("/permissions/check")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<UnifiedResponseDTO<Boolean>> checkPermission(
            @RequestBody PermissionCheckRequest request) {
        try {
            Long userId = getCurrentUserId();
            
            if (userId == null) {
                return ResponseEntity.badRequest().body(
                        UnifiedResponseDTO.<Boolean>builder()
                                .success(false)
                                .error("User not authenticated")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
            }

            boolean hasPermission = permissionManager.hasPermission(userId, request.getPermission());

            return ResponseEntity.ok(
                    UnifiedResponseDTO.<Boolean>builder()
                            .success(true)
                            .data(hasPermission)
                            .message("Permission check completed")
                            .timestamp(LocalDateTime.now())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error checking permission: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<Boolean>builder()
                            .success(false)
                            .error("Failed to check permission: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Check multiple permissions
     */
    @PostMapping("/permissions/check-multiple")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<UnifiedResponseDTO<Map<String, Boolean>>> checkMultiplePermissions(
            @RequestBody MultiplePermissionCheckRequest request) {
        try {
            Long userId = getCurrentUserId();
            
            if (userId == null) {
                return ResponseEntity.badRequest().body(
                        UnifiedResponseDTO.<Map<String, Boolean>>builder()
                                .success(false)
                                .error("User not authenticated")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
            }

            Map<String, Boolean> results = new HashMap<>();
            
            for (Permission permission : request.getPermissions()) {
                results.put(permission.name(), permissionManager.hasPermission(userId, permission));
            }

            return ResponseEntity.ok(
                    UnifiedResponseDTO.<Map<String, Boolean>>builder()
                            .success(true)
                            .data(results)
                            .message("Multiple permissions check completed")
                            .timestamp(LocalDateTime.now())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error checking multiple permissions: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<Map<String, Boolean>>builder()
                            .success(false)
                            .error("Failed to check permissions: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Check endpoint access
     */
    @PostMapping("/permissions/check-endpoint")
    @PreAuthorize("hasAnyRole('CLIENT', 'DRIVER', 'ADMIN', 'ACCOUNTANT')")
    public ResponseEntity<UnifiedResponseDTO<Boolean>> checkEndpointAccess(
            @RequestBody EndpointAccessRequest request) {
        try {
            Long userId = getCurrentUserId();
            
            if (userId == null) {
                return ResponseEntity.badRequest().body(
                        UnifiedResponseDTO.<Boolean>builder()
                                .success(false)
                                .error("User not authenticated")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
            }

            boolean canAccess = permissionManager.canAccessEndpoint(
                    userId, request.getEndpoint(), request.getMethod()
            );

            return ResponseEntity.ok(
                    UnifiedResponseDTO.<Boolean>builder()
                            .success(true)
                            .data(canAccess)
                            .message("Endpoint access check completed")
                            .timestamp(LocalDateTime.now())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error checking endpoint access: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<Boolean>builder()
                            .success(false)
                            .error("Failed to check endpoint access: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Get role permissions (Admin only)
     */
    @GetMapping("/permissions/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<Set<Permission>>> getRolePermissions(
            @PathVariable UserRole role) {
        try {
            Set<Permission> permissions = rbac.getRolePermissions(role);

            return ResponseEntity.ok(
                    UnifiedResponseDTO.<Set<Permission>>builder()
                            .success(true)
                            .data(permissions)
                            .message("Role permissions retrieved successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error getting role permissions: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<Set<Permission>>builder()
                            .success(false)
                            .error("Failed to retrieve role permissions: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Get all roles (Admin only)
     */
    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<List<UserRole>>> getAllRoles() {
        try {
            List<UserRole> roles = rbac.getAllRoles();

            return ResponseEntity.ok(
                    UnifiedResponseDTO.<List<UserRole>>builder()
                            .success(true)
                            .data(roles)
                            .message("All roles retrieved successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error getting all roles: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<List<UserRole>>builder()
                            .success(false)
                            .error("Failed to retrieve roles: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Get permission statistics (Admin only)
     */
    @GetMapping("/permissions/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<PermissionManager.PermissionStatistics>> getPermissionStatistics() {
        try {
            PermissionManager.PermissionStatistics statistics = permissionManager.getPermissionStatistics();

            return ResponseEntity.ok(
                    UnifiedResponseDTO.<PermissionManager.PermissionStatistics>builder()
                            .success(true)
                            .data(statistics)
                            .message("Permission statistics retrieved successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error getting permission statistics: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<PermissionManager.PermissionStatistics>builder()
                            .success(false)
                            .error("Failed to retrieve statistics: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Refresh user permissions cache (Admin only)
     */
    @PostMapping("/permissions/refresh/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<String>> refreshUserPermissions(
            @PathVariable Long userId) {
        try {
            permissionManager.refreshUserPermissions(userId);

            return ResponseEntity.ok(
                    UnifiedResponseDTO.<String>builder()
                            .success(true)
                            .data("Permissions refreshed for user: " + userId)
                            .message("User permissions refreshed successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error refreshing user permissions: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<String>builder()
                            .success(false)
                            .error("Failed to refresh permissions: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Clear all permissions cache (Admin only)
     */
    @PostMapping("/permissions/clear-cache")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<String>> clearAllPermissionsCache() {
        try {
            permissionManager.clearAllPermissionsCache();

            return ResponseEntity.ok(
                    UnifiedResponseDTO.<String>builder()
                            .success(true)
                            .data("All permissions cache cleared")
                            .message("Permissions cache cleared successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error clearing permissions cache: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<String>builder()
                            .success(false)
                            .error("Failed to clear cache: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Validate user access (Admin only)
     */
    @PostMapping("/access/validate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<PermissionManager.AccessValidationResult>> validateUserAccess(
            @RequestBody AccessValidationRequest request) {
        try {
            PermissionManager.AccessValidationResult result = permissionManager.validateAccess(
                    request.getUserId(), request.getResource(), request.getAction()
            );

            return ResponseEntity.ok(
                    UnifiedResponseDTO.<PermissionManager.AccessValidationResult>builder()
                            .success(true)
                            .data(result)
                            .message("Access validation completed")
                            .timestamp(LocalDateTime.now())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error validating user access: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<PermissionManager.AccessValidationResult>builder()
                            .success(false)
                            .error("Failed to validate access: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Get accessible endpoints for role (Admin only)
     */
    @GetMapping("/endpoints/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<Set<String>>> getAccessibleEndpoints(
            @PathVariable UserRole role) {
        try {
            PermissionManager.RoleUIConfiguration config = permissionManager.getRoleUIConfiguration(role);

            return ResponseEntity.ok(
                    UnifiedResponseDTO.<Set<String>>builder()
                            .success(true)
                            .data(config.getAccessibleEndpoints())
                            .message("Accessible endpoints retrieved successfully")
                            .timestamp(LocalDateTime.now())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error getting accessible endpoints: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<Set<String>>builder()
                            .success(false)
                            .error("Failed to retrieve endpoints: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    /**
     * Test RBAC system (Admin only)
     */
    @GetMapping("/test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UnifiedResponseDTO<Map<String, Object>>> testRBACSystem() {
        try {
            Map<String, Object> testResults = new HashMap<>();

            // Test permission checking
            Long currentUserId = getCurrentUserId();
            if (currentUserId != null) {
                testResults.put("currentUserPermissions", permissionManager.getUserPermissions(currentUserId));
                testResults.put("currentUserRole", permissionManager.getUserRole(currentUserId));
            }

            // Test role configurations
            for (UserRole role : UserRole.values()) {
                PermissionManager.RoleUIConfiguration config = permissionManager.getRoleUIConfiguration(role);
                testResults.put("role_" + role.name(), Map.of(
                        "permissions", config.getPermissions().size(),
                        "endpoints", config.getAccessibleEndpoints().size(),
                        "resources", config.getAccessibleResources().size()
                ));
            }

            // Test statistics
            testResults.put("statistics", permissionManager.getPermissionStatistics());

            testResults.put("systemStatus", "RBAC_SYSTEM_ACTIVE");
            testResults.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(true)
                            .data(testResults)
                            .message("RBAC system test completed")
                            .timestamp(LocalDateTime.now())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error testing RBAC system: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(
                    UnifiedResponseDTO.<Map<String, Object>>builder()
                            .success(false)
                            .error("Failed to test RBAC system: " + e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        }
    }

    // Helper methods
    private Long getCurrentUserId() {
        try {
            // Get user ID from security context
            return 1L; // Placeholder - in real implementation, get from Spring Security context
        } catch (Exception e) {
            log.error("Error getting current user ID: {}", e.getMessage(), e);
            return null;
        }
    }

    // Request DTOs
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PermissionCheckRequest {
        private Permission permission;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class MultiplePermissionCheckRequest {
        private Set<Permission> permissions;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class EndpointAccessRequest {
        private String endpoint;
        private String method;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class AccessValidationRequest {
        private Long userId;
        private String resource;
        private String action;
    }
}
