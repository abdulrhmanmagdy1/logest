package com.edham.logistics.controller;

import com.edham.logistics.dto.UserRequest;
import com.edham.logistics.dto.UserResponse;
import com.edham.logistics.dto.UserUpdateRequest;
import com.edham.logistics.service.UserService;
import com.edham.logistics.util.ApiResponse;
import com.edham.logistics.util.PaginatedResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * User Controller
 * Handles user management operations
 */
@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get all users (paginated)
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            PaginatedResponse<UserResponse> response = userService.getAllUsers(
                pageable, role, status, search);
            
            return ResponseEntity.ok(
                ApiResponse.<PaginatedResponse<UserResponse>>builder()
                    .success(true)
                    .message("Users retrieved successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<PaginatedResponse<UserResponse>>builder()
                    .success(false)
                    .message("Failed to retrieve users: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        try {
            UserResponse response = userService.getUserById(id);
            
            return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                    .success(true)
                    .message("User retrieved successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<UserResponse>builder()
                    .success(false)
                    .message("User not found: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Create new user
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody UserRequest userRequest,
            HttpServletRequest request) {
        
        try {
            UserResponse response = userService.createUser(userRequest, request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<UserResponse>builder()
                    .success(true)
                    .message("User created successfully")
                    .data(response)
                    .build()
                );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<UserResponse>builder()
                    .success(false)
                    .message("Failed to create user: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Update user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest updateRequest,
            HttpServletRequest request) {
        
        try {
            UserResponse response = userService.updateUser(id, updateRequest, request);
            
            return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                    .success(true)
                    .message("User updated successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<UserResponse>builder()
                    .success(false)
                    .message("Failed to update user: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Delete user
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id, null);
            
            return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                    .success(true)
                    .message("User deleted successfully")
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to delete user: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get current user profile
     */
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(HttpServletRequest request) {
        try {
            UserResponse response = userService.getCurrentUser(request);
            
            return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                    .success(true)
                    .message("Profile retrieved successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.<UserResponse>builder()
                    .success(false)
                    .message("Failed to retrieve profile: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Update current user profile
     */
    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Valid @RequestBody UserUpdateRequest updateRequest,
            HttpServletRequest request) {
        
        try {
            UserResponse response = userService.updateProfile(updateRequest, request);
            
            return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                    .success(true)
                    .message("Profile updated successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<UserResponse>builder()
                    .success(false)
                    .message("Failed to update profile: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Change user status (activate/deactivate)
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> changeUserStatus(
            @PathVariable Long id,
            @RequestParam boolean active,
            HttpServletRequest request) {
        
        try {
            UserResponse response = userService.changeUserStatus(id, active, request);
            
            return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                    .success(true)
                    .message("User status updated successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<UserResponse>builder()
                    .success(false)
                    .message("Failed to update user status: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Change user role
     */
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> changeUserRole(
            @PathVariable Long id,
            @RequestParam String role,
            HttpServletRequest request) {
        
        try {
            UserResponse response = userService.changeUserRole(id, role, request);
            
            return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                    .success(true)
                    .message("User role updated successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<UserResponse>builder()
                    .success(false)
                    .message("Failed to update user role: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get users by role
     */
    @GetMapping("/by-role/{role}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<UserResponse>>> getUsersByRole(
            @PathVariable String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            PaginatedResponse<UserResponse> response = userService.getUsersByRole(role, pageable);
            
            return ResponseEntity.ok(
                ApiResponse.<PaginatedResponse<UserResponse>>builder()
                    .success(true)
                    .message("Users by role retrieved successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<PaginatedResponse<UserResponse>>builder()
                    .success(false)
                    .message("Failed to retrieve users by role: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get users by organization
     */
    @GetMapping("/by-organization/{orgId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<UserResponse>>> getUsersByOrganization(
            @PathVariable Long orgId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            PaginatedResponse<UserResponse> response = userService.getUsersByOrganization(orgId, pageable);
            
            return ResponseEntity.ok(
                ApiResponse.<PaginatedResponse<UserResponse>>builder()
                    .success(true)
                    .message("Users by organization retrieved successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<PaginatedResponse<UserResponse>>builder()
                    .success(false)
                    .message("Failed to retrieve users by organization: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Search users
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<UserResponse>>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String role) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            PaginatedResponse<UserResponse> response = userService.searchUsers(query, role, pageable);
            
            return ResponseEntity.ok(
                ApiResponse.<PaginatedResponse<UserResponse>>builder()
                    .success(true)
                    .message("Search completed successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<PaginatedResponse<UserResponse>>builder()
                    .success(false)
                    .message("Search failed: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get user statistics
     */
    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getUserStats() {
        try {
            Object stats = userService.getUserStatistics();
            
            return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                    .success(true)
                    .message("Statistics retrieved successfully")
                    .data(stats)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<Object>builder()
                    .success(false)
                    .message("Failed to retrieve statistics: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get user activity logs
     */
    @GetMapping("/{id}/activity")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<PaginatedResponse<Object>>> getUserActivity(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            PaginatedResponse<Object> response = userService.getUserActivity(id, pageable);
            
            return ResponseEntity.ok(
                ApiResponse.<PaginatedResponse<Object>>builder()
                    .success(true)
                    .message("User activity retrieved successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.<PaginatedResponse<Object>>builder()
                    .success(false)
                    .message("Failed to retrieve user activity: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Reset user password
     */
    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> resetUserPassword(
            @PathVariable Long id,
            HttpServletRequest request) {
        
        try {
            userService.resetUserPassword(id, request);
            
            return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                    .success(true)
                    .message("Password reset successfully")
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Failed to reset password: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Export users to CSV
     */
    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<byte[]> exportUsers(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {
        
        try {
            byte[] csvData = userService.exportUsersToCsv(role, status);
            
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=users.csv")
                .header("Content-Type", "text/csv")
                .body(csvData);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
        }
    }
}
