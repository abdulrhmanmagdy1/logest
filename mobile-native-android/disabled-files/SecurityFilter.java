package com.edham.logistics.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Security Filter for API Protection
 */
@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private ProfessionalSecurityService securityService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String endpoint = request.getRequestURI();
        String method = request.getMethod();

        // Log security event for API access
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("endpoint", endpoint);
        metadata.put("method", method);
        metadata.put("queryString", request.getQueryString());

        // Check rate limiting
        String rateLimitKey = clientIp + ":" + endpoint;
        if (!securityService.checkRateLimit(rateLimitKey)) {
            response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
            response.getWriter().write("{\"error\":\"Rate limit exceeded\"}");
            securityService.logSecurityEvent("RATE_LIMIT_BLOCKED", clientIp, 
                "API access blocked due to rate limiting", clientIp, userAgent, metadata);
            return;
        }

        // Validate request headers for suspicious patterns
        if (containsSuspiciousHeaders(request)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid request headers\"}");
            securityService.logSecurityEvent("SUSPICIOUS_HEADERS", clientIp, 
                "Request contains suspicious headers", clientIp, userAgent, metadata);
            return;
        }

        // Validate request parameters
        if (containsSuspiciousParameters(request)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid request parameters\"}");
            securityService.logSecurityEvent("SUSPICIOUS_PARAMETERS", clientIp, 
                "Request contains suspicious parameters", clientIp, userAgent, metadata);
            return;
        }

        // Check for common attack patterns
        if (containsAttackPatterns(request)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\":\"Access denied\"}");
            securityService.logSecurityEvent("ATTACK_PATTERN_DETECTED", clientIp, 
                "Potential attack pattern detected", clientIp, userAgent, metadata);
            return;
        }

        // Add security headers
        addSecurityHeaders(response);

        filterChain.doFilter(request, response);
    }

    /**
     * Get client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    /**
     * Check for suspicious headers
     */
    private boolean containsSuspiciousHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);

            // Check for SQL injection patterns
            if (headerValue != null && containsSqlInjectionPatterns(headerValue)) {
                return true;
            }

            // Check for XSS patterns
            if (headerValue != null && containsXssPatterns(headerValue)) {
                return true;
            }

            // Check for suspicious header names
            if (headerName.toLowerCase().contains("script") || 
                headerName.toLowerCase().contains("alert") ||
                headerName.toLowerCase().contains("javascript")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check for suspicious parameters
     */
    private boolean containsSuspiciousParameters(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String paramName = entry.getKey();
            String[] paramValues = entry.getValue();

            // Check parameter names
            if (paramName.toLowerCase().contains("script") || 
                paramName.toLowerCase().contains("alert") ||
                paramName.toLowerCase().contains("javascript") ||
                paramName.toLowerCase().contains("union") ||
                paramName.toLowerCase().contains("select") ||
                paramName.toLowerCase().contains("drop") ||
                paramName.toLowerCase().contains("insert") ||
                paramName.toLowerCase().contains("update") ||
                paramName.toLowerCase().contains("delete")) {
                return true;
            }

            // Check parameter values
            for (String paramValue : paramValues) {
                if (paramValue != null) {
                    if (containsSqlInjectionPatterns(paramValue) || 
                        containsXssPatterns(paramValue) ||
                        containsCommandInjectionPatterns(paramValue)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Check for common attack patterns
     */
    private boolean containsAttackPatterns(HttpServletRequest request) {
        String queryString = request.getQueryString();
        String uri = request.getRequestURI();
        String userAgent = request.getHeader("User-Agent");

        // Check for path traversal
        if (uri != null && (uri.contains("../") || uri.contains("..\\") || 
            uri.contains("%2e%2e%2f") || uri.contains("%2e%2e%5c"))) {
            return true;
        }

        // Check for command injection
        if (queryString != null && containsCommandInjectionPatterns(queryString)) {
            return true;
        }

        // Check for suspicious user agents
        if (userAgent != null && (userAgent.toLowerCase().contains("sqlmap") ||
            userAgent.toLowerCase().contains("nmap") ||
            userAgent.toLowerCase().contains("nikto") ||
            userAgent.toLowerCase().contains("burp") ||
            userAgent.toLowerCase().contains("scanner"))) {
            return true;
        }

        return false;
    }

    /**
     * Check for SQL injection patterns
     */
    private boolean containsSqlInjectionPatterns(String input) {
        if (input == null) return false;
        
        String lowerInput = input.toLowerCase();
        
        return lowerInput.contains("'") ||
               lowerInput.contains("\"") ||
               lowerInput.contains("or 1=1") ||
               lowerInput.contains("and 1=1") ||
               lowerInput.contains("union select") ||
               lowerInput.contains("drop table") ||
               lowerInput.contains("insert into") ||
               lowerInput.contains("update set") ||
               lowerInput.contains("delete from") ||
               lowerInput.contains("exec(") ||
               lowerInput.contains("execute(") ||
               lowerInput.contains("sp_") ||
               lowerInput.contains("xp_") ||
               lowerInput.contains("--") ||
               lowerInput.contains("/*") ||
               lowerInput.contains("*/");
    }

    /**
     * Check for XSS patterns
     */
    private boolean containsXssPatterns(String input) {
        if (input == null) return false;
        
        String lowerInput = input.toLowerCase();
        
        return lowerInput.contains("<script") ||
               lowerInput.contains("</script") ||
               lowerInput.contains("javascript:") ||
               lowerInput.contains("vbscript:") ||
               lowerInput.contains("onload=") ||
               lowerInput.contains("onerror=") ||
               lowerInput.contains("onclick=") ||
               lowerInput.contains("onmouseover=") ||
               lowerInput.contains("onfocus=") ||
               lowerInput.contains("onblur=") ||
               lowerInput.contains("onchange=") ||
               lowerInput.contains("onsubmit=") ||
               lowerInput.contains("<iframe") ||
               lowerInput.contains("<object") ||
               lowerInput.contains("<embed") ||
               lowerInput.contains("<link") ||
               lowerInput.contains("<meta") ||
               lowerInput.contains("<style") ||
               lowerInput.contains("alert(") ||
               lowerInput.contains("confirm(") ||
               lowerInput.contains("prompt(");
    }

    /**
     * Check for command injection patterns
     */
    private boolean containsCommandInjectionPatterns(String input) {
        if (input == null) return false;
        
        String lowerInput = input.toLowerCase();
        
        return lowerInput.contains(";") ||
               lowerInput.contains("|") ||
               lowerInput.contains("&") ||
               lowerInput.contains("&&") ||
               lowerInput.contains("||") ||
               lowerInput.contains("`") ||
               lowerInput.contains("$(") ||
               lowerInput.contains("${") ||
               lowerInput.contains("nc ") ||
               lowerInput.contains("netcat") ||
               lowerInput.contains("telnet") ||
               lowerInput.contains("wget") ||
               lowerInput.contains("curl") ||
               lowerInput.contains("ping") ||
               lowerInput.contains("nslookup") ||
               lowerInput.contains("dig") ||
               lowerInput.contains("whoami") ||
               lowerInput.contains("id") ||
               lowerInput.contains("uname") ||
               lowerInput.contains("ps") ||
               lowerInput.contains("ls") ||
               lowerInput.contains("cat") ||
               lowerInput.contains("echo") ||
               lowerInput.contains("rm ") ||
               lowerInput.contains("mv ") ||
               lowerInput.contains("cp ") ||
               lowerInput.contains("chmod") ||
               lowerInput.contains("chown") ||
               lowerInput.contains("sudo") ||
               lowerInput.contains("su ");
    }

    /**
     * Add security headers to response
     */
    private void addSecurityHeaders(HttpServletResponse response) {
        // Prevent clickjacking
        response.setHeader("X-Frame-Options", "DENY");
        
        // Prevent MIME type sniffing
        response.setHeader("X-Content-Type-Options", "nosniff");
        
        // Enable XSS protection
        response.setHeader("X-XSS-Protection", "1; mode=block");
        
        // Strict Transport Security
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        
        // Content Security Policy
        response.setHeader("Content-Security-Policy", 
            "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
            "style-src 'self' 'unsafe-inline'; img-src 'self' data: https:; " +
            "font-src 'self' data:; connect-src 'self'; " +
            "frame-ancestors 'none'; form-action 'self';");
        
        // Referrer Policy
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
        // Permissions Policy
        response.setHeader("Permissions-Policy", 
            "geolocation=(), microphone=(), camera=(), payment=(), usb=(), " +
            "magnetometer=(), gyroscope=(), accelerometer=()");
        
        // Remove server information
        response.setHeader("Server", "");
        
        // Prevent caching for sensitive endpoints
        if (response.getStatus() >= 400) {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
        }
    }
}
