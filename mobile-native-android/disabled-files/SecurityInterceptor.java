package com.edham.logistics.security;

import com.edham.logistics.model.User;
import com.edham.logistics.repository.UserRepository;
import com.edham.logistics.service.JwtTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * Security interceptor for Role-Based Access Control (RBAC)
 * Intercepts all requests to check permissions
 */
@Slf4j
@Component
public class SecurityInterceptor implements HandlerInterceptor {

    private final PermissionManager permissionManager;
    private final UserRepository userRepository;
    private final JwtTokenService jwtTokenService;

    // Public endpoints that don't require authentication
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/reset-password",
            "/api/v1/auth/refresh-token",
            "/api/v1/health",
            "/api/v1/public",
            "/swagger-ui",
            "/v3/api-docs"
    );

    @Autowired
    public SecurityInterceptor(PermissionManager permissionManager,
                           UserRepository userRepository,
                           JwtTokenService jwtTokenService) {
        this.permissionManager = permissionManager;
        this.userRepository = userRepository;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String path = request.getRequestURI();
            String method = request.getMethod();

            log.debug("Security interceptor: {} {}", method, path);

            // Skip public endpoints
            if (isPublicEndpoint(path)) {
                log.debug("Public endpoint access: {}", path);
                return true;
            }

            // Get JWT token from header
            String token = extractToken(request);
            if (token == null) {
                log.warn("No token provided for: {}", path);
                sendUnauthorizedResponse(response, "Authentication token required");
                return false;
            }

            // Validate token and get user
            User user = validateTokenAndGetUser(token);
            if (user == null) {
                log.warn("Invalid token for: {}", path);
                sendUnauthorizedResponse(response, "Invalid authentication token");
                return false;
            }

            // Check endpoint permissions
            if (!permissionManager.canAccessEndpoint(user.getId(), path, method)) {
                log.warn("Access denied for user {} (role: {}) to: {} {}", 
                        user.getId(), user.getRole(), method, path);
                sendForbiddenResponse(response, "Insufficient permissions");
                return false;
            }

            // Set user in context
            SecurityContextHolder.getContext().setAuthentication(
                    new UserAuthentication(user, token)
            );

            log.debug("Access granted for user {} (role: {}) to: {} {}", 
                    user.getId(), user.getRole(), method, path);
            return true;

        } catch (Exception e) {
            log.error("Security interceptor error: {}", e.getMessage(), e);
            sendInternalServerErrorResponse(response, "Security check failed");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Clear security context
        SecurityContextHolder.clearContext();
    }

    /**
     * Check if endpoint is public
     */
    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    /**
     * Extract JWT token from request
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        // Also check for token in query parameter (for WebSocket connections)
        String tokenParam = request.getParameter("token");
        if (tokenParam != null && !tokenParam.isEmpty()) {
            return tokenParam;
        }
        
        return null;
    }

    /**
     * Validate JWT token and get user
     */
    private User validateTokenAndGetUser(String token) {
        try {
            // Validate token
            if (!jwtTokenService.validateToken(token)) {
                return null;
            }

            // Get user ID from token
            Long userId = jwtTokenService.getUserIdFromToken(token);
            if (userId == null) {
                return null;
            }

            // Get user from database
            return userRepository.findById(userId).orElse(null);

        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Send unauthorized response
     */
    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(String.format(
                "{\"success\": false, \"error\": \"UNAUTHORIZED\", \"message\": \"%s\"}", 
                message
        ));
    }

    /**
     * Send forbidden response
     */
    private void sendForbiddenResponse(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(String.format(
                "{\"success\": false, \"error\": \"FORBIDDEN\", \"message\": \"%s\"}", 
                message
        ));
    }

    /**
     * Send internal server error response
     */
    private void sendInternalServerErrorResponse(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("application/json");
        response.getWriter().write(String.format(
                "{\"success\": false, \"error\": \"INTERNAL_SERVER_ERROR\", \"message\": \"%s\"}", 
                message
        ));
    }
}
