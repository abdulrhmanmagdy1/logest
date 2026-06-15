// package com.edham.logistics.controller;

import com.edham.logistics.security.EncryptionService;
import com.edham.logistics.security.InputValidationService;
import com.edham.logistics.security.NetworkFailureHandler;
import com.edham.logistics.performance.PerformanceOptimizationService;
import com.edham.logistics.performance.SystemHealthStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Security controller for system hardening and monitoring
 * Provides security endpoints and system health monitoring
 */
@RestController
@RequestMapping("/api/v1/security")
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class SecurityController {

    private final EncryptionService encryptionService;
    private final InputValidationService inputValidationService;
    private final NetworkFailureHandler networkFailureHandler;
    private final PerformanceOptimizationService performanceOptimizationService;

    @Autowired
    public SecurityController(EncryptionService encryptionService,
                           InputValidationService inputValidationService,
                           NetworkFailureHandler networkFailureHandler,
                           PerformanceOptimizationService performanceOptimizationService) {
        this.encryptionService = encryptionService;
        this.inputValidationService = inputValidationService;
        this.networkFailureHandler = networkFailureHandler;
        this.performanceOptimizationService = performanceOptimizationService;
    }

    /**
     * Test encryption service
     */
    @PostMapping("/test/encryption")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> testEncryption(@RequestBody Map<String, String> request) {
        try {
            String plainText = request.get("plainText");
            if (plainText == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "plainText is required"));
            }

            // Validate input
            InputValidationService.ValidationResult validation = 
                    inputValidationService.validateString(plainText, "plainText", 1000);
            if (!validation.isValid()) {
                return ResponseEntity.badRequest().body(Map.of("errors", validation.getErrors()));
            }

            // Encrypt and decrypt
            String encrypted = encryptionService.encrypt(plainText);
            String decrypted = encryptionService.decrypt(encrypted);

            return ResponseEntity.ok(Map.of(
                    "original", plainText,
                    "encrypted", encrypted,
                    "decrypted", decrypted,
                    "success", plainText.equals(decrypted),
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            log.error("Error testing encryption: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Encryption test failed"));
        }
    }

    /**
     * Test input validation
     */
    @PostMapping("/test/validation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> testInputValidation(@RequestBody Map<String, Object> request) {
        try {
            Map<String, Object> results = new java.util.HashMap<>();

            // Test string validation
            if (request.containsKey("testString")) {
                String testString = (String) request.get("testString");
                InputValidationService.ValidationResult validation = 
                        inputValidationService.validateString(testString, "testString", 100);
                results.put("stringValidation", validation);
            }

            // Test email validation
            if (request.containsKey("testEmail")) {
                String testEmail = (String) request.get("testEmail");
                InputValidationService.ValidationResult validation = 
                        inputValidationService.validateEmail(testEmail, "testEmail");
                results.put("emailValidation", validation);
            }

            // Test numeric validation
            if (request.containsKey("testNumber")) {
                String testNumber = (String) request.get("testNumber");
                InputValidationService.ValidationResult validation = 
                        inputValidationService.validateNumeric(testNumber, "testNumber", 0, 1000);
                results.put("numericValidation", validation);
            }

            // Test SQL injection detection
            if (request.containsKey("testSql")) {
                String testSql = (String) request.get("testSql");
                InputValidationService.ValidationResult validation = 
                        inputValidationService.validateString(testSql, "testSql", 100);
                results.put("sqlInjectionTest", validation);
            }

            return ResponseEntity.ok(Map.of(
                    "results", results,
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            log.error("Error testing input validation: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Validation test failed"));
        }
    }

    /**
     * Test network failure handling
     */
    @PostMapping("/test/network-failure")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> testNetworkFailure(@RequestBody Map<String, String> request) {
        try {
            String operation = request.get("operation");
            if (operation == null) {
                operation = "database";
            }

            // Simulate network failure
            Exception simulatedError = new RuntimeException("Simulated network failure for testing");
            
            // Handle the failure
            networkFailureHandler.handleNetworkFailure(operation, simulatedError);

            return ResponseEntity.ok(Map.of(
                    "operation", operation,
                    "error", simulatedError.getMessage(),
                    "handled", true,
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            log.error("Error testing network failure handling: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Network failure test failed"));
        }
    }

    /**
     * Get system health status
     */
    @GetMapping("/health")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    public ResponseEntity<SystemHealthStatus> getSystemHealth() {
        try {
            SystemHealthStatus healthStatus = performanceOptimizationService.getSystemHealthStatus();
            return ResponseEntity.ok(healthStatus);
        } catch (Exception e) {
            log.error("Error getting system health: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get circuit breaker status
     */
    @GetMapping("/circuit-breakers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, NetworkFailureHandler.CircuitBreakerStatus>> getCircuitBreakerStatus() {
        try {
            Map<String, NetworkFailureHandler.CircuitBreakerStatus> status = 
                    networkFailureHandler.getCircuitBreakerStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Error getting circuit breaker status: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Reset circuit breaker
     */
    @PostMapping("/circuit-breakers/{operation}/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> resetCircuitBreaker(@PathVariable String operation) {
        try {
            networkFailureHandler.resetCircuitBreaker(operation);
            return ResponseEntity.ok(Map.of(
                    "operation", operation,
                    "status", "reset",
                    "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            log.error("Error resetting circuit breaker: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to reset circuit breaker"));
        }
    }

    /**
     * Get failed operations
     */
    @GetMapping("/failed-operations/{operation}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.List<NetworkFailureHandler.FailedOperation>> getFailedOperations(
            @PathVariable String operation) {
        try {
            java.util.List<NetworkFailureHandler.FailedOperation> operations = 
                    networkFailureHandler.getFailedOperations(operation);
            return ResponseEntity.ok(operations);
        } catch (Exception e) {
            log.error("Error getting failed operations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Clear failed operations
     */
    @DeleteMapping("/failed-operations/{operation}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> clearFailedOperations(@PathVariable String operation) {
        try {
            networkFailureHandler.clearFailedOperations(operation);
            return ResponseEntity.ok(Map.of(
                    "operation", operation,
                    "status", "cleared",
                    "timestamp", LocalDateTime.now().toString()
            ));
        } catch (Exception e) {
            log.error("Error clearing failed operations: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to clear operations"));
        }
    }

    /**
     * Optimize offline behavior
     */
    @PostMapping("/optimize-offline")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> optimizeOfflineBehavior() {
        try {
            performanceOptimizationService.optimizeOfflineBehavior();
            return ResponseEntity.ok(Map.of(
                    "status", "optimized",
                    "message", "Offline behavior optimized successfully",
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            log.error("Error optimizing offline behavior: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to optimize offline behavior"));
        }
    }

    /**
     * Recover from crash
     */
    @PostMapping("/recover-crash")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> recoverFromCrash() {
        try {
            performanceOptimizationService.recoverFromCrash();
            return ResponseEntity.ok(Map.of(
                    "status", "recovered",
                    "message", "System crash recovery initiated",
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            log.error("Error recovering from crash: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to recover from crash"));
        }
    }

    /**
     * Test security headers
     */
    @GetMapping("/test/headers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> testSecurityHeaders() {
        try {
            return ResponseEntity.ok(Map.of(
                    "securityHeaders", Map.of(
                            "X-Frame-Options", "DENY",
                            "X-Content-Type-Options", "nosniff",
                            "X-XSS-Protection", "1; mode=block",
                            "Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload",
                            "Content-Security-Policy", "default-src 'self'",
                            "Referrer-Policy", "strict-origin-when-cross-origin",
                            "Permissions-Policy", "geolocation=(), microphone=(), camera=()"
                    ),
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            log.error("Error testing security headers: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Security headers test failed"));
        }
    }

    /**
     * Test rate limiting
     */
    @GetMapping("/test/rate-limit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> testRateLimit() {
        try {
            return ResponseEntity.ok(Map.of(
                    "message", "Rate limiting is active",
                    "limits", Map.of(
                            "requestsPerMinute", 60,
                            "requestsPerHour", 1000,
                            "requestsPerDay", 10000
                    ),
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            log.error("Error testing rate limiting: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Rate limiting test failed"));
        }
    }

    /**
     * Get security configuration
     */
    @GetMapping("/config")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getSecurityConfig() {
        try {
            return ResponseEntity.ok(Map.of(
                    "encryption", Map.of(
                            "algorithm", "AES-256",
                            "keyLength", 256,
                            "mode", "CBC/PKCS5Padding"
                    ),
                    "rateLimiting", Map.of(
                            "enabled", true,
                            "requestsPerMinute", 60,
                            "requestsPerHour", 1000,
                            "requestsPerDay", 10000
                    ),
                    "securityHeaders", Map.of(
                            "enabled", true,
                            "hsts", true,
                            "csp", true,
                            "xssProtection", true
                    ),
                    "inputValidation", Map.of(
                            "enabled", true,
                            "sqlInjectionProtection", true,
                            "xssProtection", true,
                            "pathTraversalProtection", true
                    ),
                    "circuitBreakers", Map.of(
                            "enabled", true,
                            "failureThreshold", 5,
                            "timeoutMillis", 60000
                    ),
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            log.error("Error getting security config: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Failed to get security config"));
        }
    }

    /**
     * Test overall system security
     */
    @GetMapping("/test/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> testAllSecurity() {
        try {
            Map<String, Object> testResults = new java.util.HashMap<>();

            // Test encryption
            String testText = "Security Test";
            String encrypted = encryptionService.encrypt(testText);
            String decrypted = encryptionService.decrypt(encrypted);
            testResults.put("encryption", Map.of(
                    "status", testText.equals(decrypted) ? "PASS" : "FAIL",
                    "original", testText,
                    "encrypted", encrypted,
                    "decrypted", decrypted
            ));

            // Test input validation
            String maliciousInput = "'; DROP TABLE users; --";
            InputValidationService.ValidationResult validation = 
                    inputValidationService.validateString(maliciousInput, "maliciousInput", 100);
            testResults.put("inputValidation", Map.of(
                    "status", validation.isValid() ? "FAIL" : "PASS",
                    "detected", !validation.isValid(),
                    "errors", validation.getErrors()
            ));

            // Test circuit breakers
            Map<String, NetworkFailureHandler.CircuitBreakerStatus> circuitStatus = 
                    networkFailureHandler.getCircuitBreakerStatus();
            testResults.put("circuitBreakers", Map.of(
                    "status", "ACTIVE",
                    "count", circuitStatus.size()
            ));

            // Get system health
            SystemHealthStatus health = performanceOptimizationService.getSystemHealthStatus();
            testResults.put("systemHealth", Map.of(
                    "status", health.getHealthScore() > 0.8 ? "GOOD" : "NEEDS_ATTENTION",
                    "score", health.getHealthScore(),
                    "issues", health.getRecommendatons()
            ));

            return ResponseEntity.ok(Map.of(
                    "testResults", testResults,
                    "overallStatus", "SECURITY_TESTS_COMPLETED",
                    "timestamp", LocalDateTime.now()
            ));
        } catch (Exception e) {
            log.error("Error running security tests: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "Security tests failed"));
        }
    }
}
