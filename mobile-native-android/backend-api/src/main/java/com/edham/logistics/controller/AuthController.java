package com.edham.logistics.controller;

import com.edham.logistics.dto.LoginRequest;
import com.edham.logistics.dto.LoginResponse;
import com.edham.logistics.dto.RefreshTokenRequest;
import com.edham.logistics.dto.RegisterRequest;
import com.edham.logistics.service.AuthService;
import com.edham.logistics.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Authentication Controller
 * Handles user authentication, registration, and token management
 */
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * User login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        
        try {
            String clientIp = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            LoginResponse response = authService.authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword(),
                clientIp,
                userAgent
            );
            
            return ResponseEntity.ok(
                ApiResponse.<LoginResponse>builder()
                    .success(true)
                    .message("Login successful")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.<LoginResponse>builder()
                    .success(false)
                    .message("Invalid credentials: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * User registration
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(
            @Valid @RequestBody RegisterRequest registerRequest,
            HttpServletRequest request) {
        
        try {
            String clientIp = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            LoginResponse response = authService.register(
                registerRequest,
                clientIp,
                userAgent
            );
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<LoginResponse>builder()
                    .success(true)
                    .message("Registration successful")
                    .data(response)
                    .build()
                );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<LoginResponse>builder()
                    .success(false)
                    .message("Registration failed: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Refresh access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshRequest,
            HttpServletRequest request) {
        
        try {
            String clientIp = getClientIpAddress(request);
            
            LoginResponse response = authService.refreshToken(
                refreshRequest.getRefreshToken(),
                clientIp
            );
            
            return ResponseEntity.ok(
                ApiResponse.<LoginResponse>builder()
                    .success(true)
                    .message("Token refreshed successfully")
                    .data(response)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.<LoginResponse>builder()
                    .success(false)
                    .message("Token refresh failed: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * User logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader("Authorization") String authorization,
            HttpServletRequest request) {
        
        try {
            String token = extractTokenFromHeader(authorization);
            String clientIp = getClientIpAddress(request);
            
            authService.logout(token, clientIp);
            
            return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                    .success(true)
                    .message("Logout successful")
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Logout failed: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Validate token
     */
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(
            @RequestHeader("Authorization") String authorization) {
        
        try {
            String token = extractTokenFromHeader(authorization);
            boolean isValid = authService.validateToken(token);
            
            return ResponseEntity.ok(
                ApiResponse.<Boolean>builder()
                    .success(true)
                    .message("Token validation completed")
                    .data(isValid)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.<Boolean>builder()
                    .success(false)
                    .message("Token validation failed: " + e.getMessage())
                    .data(false)
                    .build()
                );
        }
    }

    /**
     * Request password reset
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @RequestParam String email,
            HttpServletRequest request) {
        
        try {
            String clientIp = getClientIpAddress(request);
            
            authService.forgotPassword(email, clientIp);
            
            return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                    .success(true)
                    .message("Password reset email sent")
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Password reset failed: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Reset password with token
     */
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword,
            HttpServletRequest request) {
        
        try {
            String clientIp = getClientIpAddress(request);
            
            authService.resetPassword(token, newPassword, clientIp);
            
            return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                    .success(true)
                    .message("Password reset successful")
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Password reset failed: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Change password (authenticated user)
     */
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestHeader("Authorization") String authorization,
            HttpServletRequest request) {
        
        try {
            String token = extractTokenFromHeader(authorization);
            String clientIp = getClientIpAddress(request);
            
            authService.changePassword(token, currentPassword, newPassword, clientIp);
            
            return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                    .success(true)
                    .message("Password changed successfully")
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<Void>builder()
                    .success(false)
                    .message("Password change failed: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Get current user info
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Object>> getCurrentUser(
            @RequestHeader("Authorization") String authorization) {
        
        try {
            String token = extractTokenFromHeader(authorization);
            Object userInfo = authService.getCurrentUser(token);
            
            return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                    .success(true)
                    .message("User info retrieved successfully")
                    .data(userInfo)
                    .build()
            );
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.<Object>builder()
                    .success(false)
                    .message("Failed to get user info: " + e.getMessage())
                    .build()
                );
        }
    }

    /**
     * Extract token from Authorization header
     */
    private String extractTokenFromHeader(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        throw new IllegalArgumentException("Invalid authorization header");
    }

    /**
     * Get client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
