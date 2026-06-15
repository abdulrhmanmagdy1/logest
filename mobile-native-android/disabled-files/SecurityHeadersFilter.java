package com.edham.logistics.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Security headers filter for comprehensive protection
 * Adds security headers to prevent common attacks
 */
@Slf4j
@Component
public class SecurityHeadersFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        // Prevent clickjacking
        response.setHeader("X-Frame-Options", "DENY");
        
        // Prevent MIME type sniffing
        response.setHeader("X-Content-Type-Options", "nosniff");
        
        // Enable XSS protection
        response.setHeader("X-XSS-Protection", "1; mode=block");
        
        // Force HTTPS
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload");
        
        // Content Security Policy
        response.setHeader("Content-Security-Policy", 
            "default-src 'self'; " +
            "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
            "style-src 'self' 'unsafe-inline'; " +
            "img-src 'self' data: https:; " +
            "font-src 'self'; " +
            "connect-src 'self'; " +
            "frame-ancestors 'none'; " +
            "base-uri 'self'; " +
            "form-action 'self';"
        );
        
        // Referrer policy
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // Permissions policy
        response.setHeader("Permissions-Policy", 
            "geolocation=(), " +
            "microphone=(), " +
            "camera=(), " +
            "payment=(), " +
            "usb=(), " +
            "magnetometer=(), " +
            "gyroscope=(), " +
            "accelerometer=()"
        );
        
        // Server information hiding
        response.setHeader("Server", "Edham-Logistics");
        response.setHeader("X-Powered-By", "Edham-Logistics");
        
        // Cache control for sensitive endpoints
        if (isSensitiveEndpoint(request.getRequestURI())) {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
        }
        
        // CORS headers for API
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Expose-Headers", 
            "Authorization, Content-Type, X-Total-Count, X-Page-Count");
        
        // Security headers for APIs
        if (request.getRequestURI().startsWith("/api/")) {
            response.setHeader("X-API-Version", "v1");
            response.setHeader("X-Content-Security-Policy", "default-src 'self'");
        }
        
        filterChain.doFilter(request, response);
    }

    private boolean isSensitiveEndpoint(String uri) {
        return uri.contains("/auth/") ||
               uri.contains("/admin/") ||
               uri.contains("/profile/") ||
               uri.contains("/settings/") ||
               uri.contains("/payment/") ||
               uri.contains("/sensitive/");
    }
}
