# Edham Logistics Backend - Security Guidelines

## 📋 Overview

This document outlines comprehensive security measures and best practices for the Edham Logistics backend API system to ensure data protection, secure authentication, and compliance with industry standards.

## 🔐 Authentication & Authorization

### JWT Token Security

#### Token Structure
```json
{
  "sub": "user_id",
  "email": "user@example.com",
  "role": "CUSTOMER|DRIVER|SUPERVISOR|ACCOUNTANT|WORKSHOP|ADMIN",
  "permissions": ["SHIPMENT_READ", "SHIPMENT_CREATE", ...],
  "organizationId": "org_id",
  "iat": 1640995200,
  "exp": 1640998800,
  "iss": "edham-logistics-api",
  "aud": "edham-logistics-client"
}
```

#### Security Configuration
```yaml
jwt:
  secret: ${JWT_SECRET} # Must be at least 256 bits
  access-expiration: 3600000 # 1 hour
  refresh-expiration: 86400000 # 24 hours
  issuer: "edham-logistics-api"
  audience: "edham-logistics-client"
  algorithm: "HS256"
```

#### Token Validation
```java
@Component
public class JwtTokenValidator {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .requireIssuer("edham-logistics-api")
                .requireAudience("edham-logistics-client")
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
    
    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

### Role-Based Access Control (RBAC)

#### Permission Matrix
```java
public enum Permission {
    // User permissions
    USER_READ, USER_CREATE, USER_UPDATE, USER_DELETE,
    
    // Shipment permissions
    SHIPMENT_READ, SHIPMENT_CREATE, SHIPMENT_UPDATE, SHIPMENT_DELETE,
    
    // Tracking permissions
    TRACKING_READ, TRACKING_CREATE, TRACKING_UPDATE,
    
    // Invoice permissions
    INVOICE_READ, INVOICE_CREATE, INVOICE_UPDATE, INVOICE_DELETE,
    
    // Analytics permissions
    ANALYTICS_READ, ANALYTICS_EXPORT,
    
    // Admin permissions
    SYSTEM_CONFIG, USER_MANAGEMENT, AUDIT_LOGS
}
```

#### Role Permissions Mapping
```java
@Component
public class RolePermissionMapper {
    
    public Set<Permission> getPermissions(Role role) {
        switch (role) {
            case CUSTOMER:
                return Set.of(
                    USER_READ, USER_UPDATE,
                    SHIPMENT_READ, SHIPMENT_CREATE,
                    TRACKING_READ,
                    INVOICE_READ
                );
                
            case DRIVER:
                return Set.of(
                    USER_READ, USER_UPDATE,
                    SHIPMENT_READ, SHIPMENT_UPDATE,
                    TRACKING_CREATE, TRACKING_UPDATE
                );
                
            case SUPERVISOR:
                return Set.of(
                    USER_READ, USER_CREATE, USER_UPDATE,
                    SHIPMENT_READ, SHIPMENT_CREATE, SHIPMENT_UPDATE,
                    TRACKING_READ, TRACKING_CREATE, TRACKING_UPDATE,
                    INVOICE_READ, INVOICE_CREATE
                );
                
            case ACCOUNTANT:
                return Set.of(
                    USER_READ,
                    SHIPMENT_READ,
                    INVOICE_READ, INVOICE_CREATE, INVOICE_UPDATE,
                    ANALYTICS_READ, ANALYTICS_EXPORT
                );
                
            case ADMIN:
                return Set.of(Permission.values()); // All permissions
        }
    }
}
```

#### Method-Level Security
```java
@RestController
@RequestMapping("/api/v1/shipments")
@PreAuthorize("hasRole('ADMIN') or (hasRole('SUPERVISOR') and hasAuthority('SHIPMENT_READ'))")
public class ShipmentController {
    
    @GetMapping
    @PreAuthorize("hasAuthority('SHIPMENT_READ')")
    public ResponseEntity<PagedResponse<Shipment>> getShipments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        // Implementation
    }
    
    @PostMapping
    @PreAuthorize("hasAuthority('SHIPMENT_CREATE')")
    public ResponseEntity<Shipment> createShipment(
            @Valid @RequestBody CreateShipmentRequest request) {
        // Implementation
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('SHIPMENT_DELETE')")
    public ResponseEntity<Void> deleteShipment(@PathVariable Long id) {
        // Implementation
    }
}
```

## 🔒 Data Protection

### Encryption

#### Sensitive Data Encryption
```java
@Component
public class DataEncryptionService {
    
    @Value("${encryption.key}")
    private String encryptionKey;
    
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    
    public String encrypt(String plaintext) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(
                Base64.getDecoder().decode(encryptionKey), "AES");
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            
            // Generate random IV
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);
            
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
            
            byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
            byte[] ciphertext = cipher.doFinal(plaintextBytes);
            
            // Combine IV + ciphertext + tag
            byte[] encrypted = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, encrypted, 0, iv.length);
            System.arraycopy(ciphertext, 0, encrypted, iv.length, ciphertext.length);
            
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new EncryptionException("Failed to encrypt data", e);
        }
    }
    
    public String decrypt(String encrypted) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(
                Base64.getDecoder().decode(encryptionKey), "AES");
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            
            byte[] encryptedBytes = Base64.getDecoder().decode(encrypted);
            
            // Extract IV
            byte[] iv = Arrays.copyOfRange(encryptedBytes, 0, GCM_IV_LENGTH);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(128);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
            
            // Extract ciphertext (excluding IV)
            byte[] ciphertext = Arrays.copyOfRange(
                encryptedBytes, GCM_IV_LENGTH, encryptedBytes.length);
            
            byte[] plaintextBytes = cipher.doFinal(ciphertext);
            return new String(plaintextBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new EncryptionException("Failed to decrypt data", e);
        }
    }
}
```

#### Database Encryption
```sql
-- Encrypt sensitive columns
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Encrypt phone numbers
ALTER TABLE users 
ADD COLUMN phone_encrypted BYTEA;

UPDATE users 
SET phone_encrypted = pgp_sym_encrypt(phone, current_setting('app.encryption_key'))
WHERE phone IS NOT NULL;

-- Create view for decrypted access
CREATE VIEW users_decrypted AS
SELECT 
    id, email, first_name, last_name, role, active,
    pgp_sym_decrypt(phone_encrypted, current_setting('app.encryption_key')) as phone,
    created_at, updated_at
FROM users;
```

### Data Masking

#### PII Data Masking
```java
@Component
public class DataMaskingService {
    
    public String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return email;
        }
        
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return email;
        }
        
        String username = parts[0];
        String domain = parts[1];
        
        // Mask all but first 2 and last 2 characters
        String maskedUsername;
        if (username.length() <= 4) {
            maskedUsername = username.charAt(0) + "*".repeat(username.length() - 1);
        } else {
            maskedUsername = username.substring(0, 2) + 
                           "*".repeat(username.length() - 4) + 
                           username.substring(username.length() - 2);
        }
        
        return maskedUsername + "@" + domain;
    }
    
    public String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) {
            return phone;
        }
        
        return phone.substring(0, 2) + 
               "*".repeat(phone.length() - 4) + 
               phone.substring(phone.length() - 2);
    }
    
    public String maskCreditCard(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return cardNumber;
        }
        
        return "*".repeat(cardNumber.length() - 4) + 
               cardNumber.substring(cardNumber.length() - 4);
    }
}
```

## 🛡️ Network Security

### HTTPS Configuration
```yaml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_KEYSTORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: edham-logistics
    protocol: TLS
    enabled-protocols: TLSv1.2, TLSv1.3
    enabled-ciphers: 
      - TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384
      - TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384
      - TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256
      - TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
```

### CORS Configuration
```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allowed origins (specific, not wildcard)
        configuration.setAllowedOrigins(Arrays.asList(
            "https://edham-logistics.com",
            "https://app.edham-logistics.com",
            "https://admin.edham-logistics.com"
        ));
        
        // Allowed methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        
        // Allowed headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With"
        ));
        
        // Allow credentials
        configuration.setAllowCredentials(true);
        
        // Expose headers
        configuration.setExposedHeaders(Arrays.asList(
            "X-Total-Count", "X-Page-Count"
        ));
        
        // Pre-flight cache duration
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = 
            new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### Security Headers
```java
@Component
public class SecurityHeaderFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Prevent clickjacking
        httpResponse.setHeader("X-Frame-Options", "DENY");
        
        // Prevent MIME type sniffing
        httpResponse.setHeader("X-Content-Type-Options", "nosniff");
        
        // Enable XSS protection
        httpResponse.setHeader("X-XSS-Protection", "1; mode=block");
        
        // Enforce HTTPS
        httpResponse.setHeader("Strict-Transport-Security", 
            "max-age=31536000; includeSubDomains; preload");
        
        // Content Security Policy
        httpResponse.setHeader("Content-Security-Policy", 
            "default-src 'self'; " +
            "script-src 'self' 'unsafe-inline'; " +
            "style-src 'self' 'unsafe-inline'; " +
            "img-src 'self' data: https:; " +
            "font-src 'self'; " +
            "connect-src 'self' https://api.edham-logistics.com");
        
        // Referrer Policy
        httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        
        chain.doFilter(request, response);
    }
}
```

## 🔍 Input Validation & Sanitization

### Request Validation
```java
@RestController
public class ShipmentController {
    
    @PostMapping
    public ResponseEntity<Shipment> createShipment(
            @Valid @RequestBody CreateShipmentRequest request) {
        
        // Additional validation
        validateShipmentRequest(request);
        
        // Sanitize input
        request.setNotes(sanitizeHtml(request.getNotes()));
        
        // Process request
        return ResponseEntity.ok(shipmentService.create(request));
    }
    
    private void validateShipmentRequest(CreateShipmentRequest request) {
        // Custom business validation
        if (request.getWeight() != null && request.getWeight() <= 0) {
            throw new ValidationException("Weight must be positive");
        }
        
        if (request.getOriginAddress() != null && 
            request.getOriginAddress().length() > 500) {
            throw new ValidationException("Origin address too long");
        }
        
        // Validate coordinates
        if (request.getOriginLatitude() != null) {
            if (!isValidLatitude(request.getOriginLatitude())) {
                throw new ValidationException("Invalid origin latitude");
            }
        }
    }
    
    private String sanitizeHtml(String input) {
        if (input == null) {
            return null;
        }
        
        // Basic HTML sanitization
        return input.replaceAll("<script[^>]*>.*?</script>", "")
                   .replaceAll("<[^>]+>", "");
    }
}
```

### SQL Injection Prevention
```java
@Repository
public class ShipmentRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // Safe parameterized query
    public List<Shipment> findByCustomerAndStatus(Long customerId, String status) {
        String sql = "SELECT * FROM shipments WHERE customer_id = ? AND status = ?";
        
        return jdbcTemplate.query(sql, new Object[]{customerId, status}, 
            (rs, rowNum) -> {
                Shipment shipment = new Shipment();
                shipment.setId(rs.getLong("id"));
                // Map other fields
                return shipment;
            });
    }
    
    // Using Criteria API for dynamic queries
    public List<Shipment> findWithCriteria(ShipmentSearchCriteria criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Shipment> query = cb.createQuery(Shipment.class);
        Root<Shipment> root = query.from(Shipment.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        if (criteria.getCustomerId() != null) {
            predicates.add(cb.equal(root.get("customer").get("id"), 
                criteria.getCustomerId()));
        }
        
        if (criteria.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
        }
        
        if (criteria.getWeightMin() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("weight"), 
                criteria.getWeightMin()));
        }
        
        query.where(predicates.toArray(new Predicate[0]));
        
        return entityManager.createQuery(query).getResultList();
    }
}
```

## 📊 Audit Logging

### Comprehensive Audit Trail
```java
@Component
public class AuditService {
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    public void logAction(String action, String entityType, Long entityId, 
                      Object oldValues, Object newValues, String userId) {
        
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setOldValues(convertToJson(oldValues));
        auditLog.setNewValues(convertToJson(newValues));
        auditLog.setUserId(userId);
        auditLog.setIpAddress(getClientIpAddress());
        auditLog.setUserAgent(getUserAgent());
        auditLog.setTimestamp(Instant.now());
        
        auditLogRepository.save(auditLog);
    }
    
    @EventListener
    public void handleShipmentCreated(ShipmentCreatedEvent event) {
        logAction("CREATE", "SHIPMENT", event.getShipment().getId(), 
               null, event.getShipment(), getCurrentUserId());
    }
    
    @EventListener
    public void handleShipmentUpdated(ShipmentUpdatedEvent event) {
        logAction("UPDATE", "SHIPMENT", event.getShipment().getId(), 
               event.getOldShipment(), event.getNewShipment(), getCurrentUserId());
    }
    
    @EventListener
    public void handleUserLogin(LoginEvent event) {
        logAction("LOGIN", "USER", event.getUser().getId(), 
               null, null, event.getUser().getId());
    }
    
    @EventListener
    public void handleUserLogout(LogoutEvent event) {
        logAction("LOGOUT", "USER", event.getUser().getId(), 
               null, null, event.getUser().getId());
    }
}
```

### Sensitive Data Logging
```java
@Component
public class SensitiveDataLogger {
    
    private static final Set<String> SENSITIVE_FIELDS = Set.of(
        "password", "creditCard", "ssn", "bankAccount"
    );
    
    public String maskSensitiveData(Object data) {
        if (data == null) {
            return null;
        }
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(data);
            
            // Mask sensitive fields
            for (String field : SENSITIVE_FIELDS) {
                json = json.replaceAll(
                    "\"" + field + "\":\"[^\"]*\"", 
                    "\"" + field + "\":\"***\""
                );
            }
            
            return json;
        } catch (Exception e) {
            log.error("Error masking sensitive data", e);
            return "{}";
        }
    }
}
```

## 🚨 Rate Limiting

### Advanced Rate Limiting
```java
@Component
public class RateLimitingService {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    public boolean checkRateLimit(String key, int maxRequests, int windowSeconds) {
        String redisKey = "rate_limit:" + key;
        
        try {
            // Get current count
            String currentCount = redisTemplate.opsForValue().get(redisKey);
            int count = currentCount != null ? Integer.parseInt(currentCount) : 0;
            
            if (count >= maxRequests) {
                return false; // Rate limit exceeded
            }
            
            // Increment counter
            if (count == 0) {
                // First request in window, set expiration
                redisTemplate.opsForValue().set(redisKey, "1", windowSeconds, TimeUnit.SECONDS);
            } else {
                // Increment existing counter
                redisTemplate.opsForValue().increment(redisKey);
            }
            
            return true;
        } catch (Exception e) {
            log.error("Rate limiting error", e);
            return true; // Fail open
        }
    }
    
    public Map<String, String> getRateLimitHeaders(String key, int maxRequests, int windowSeconds) {
        String redisKey = "rate_limit:" + key;
        String currentCount = redisTemplate.opsForValue().get(redisKey);
        int count = currentCount != null ? Integer.parseInt(currentCount) : 0;
        
        Map<String, String> headers = new HashMap<>();
        headers.put("X-RateLimit-Limit", String.valueOf(maxRequests));
        headers.put("X-RateLimit-Remaining", String.valueOf(Math.max(0, maxRequests - count)));
        headers.put("X-RateLimit-Reset", String.valueOf(
            System.currentTimeMillis() + (windowSeconds * 1000L)
        ));
        
        return headers;
    }
}
```

### Rate Limiting Filter
```java
@Component
public class RateLimitingFilter implements Filter {
    
    @Autowired
    private RateLimitingService rateLimitingService;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, 
                        FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String clientKey = getClientKey(httpRequest);
        
        // Different limits for different endpoints
        String path = httpRequest.getRequestURI();
        int maxRequests = getMaxRequests(path);
        int windowSeconds = getWindowSeconds(path);
        
        if (!rateLimitingService.checkRateLimit(clientKey, maxRequests, windowSeconds)) {
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            
            Map<String, String> headers = rateLimitingService.getRateLimitHeaders(
                clientKey, maxRequests, windowSeconds
            );
            headers.forEach(httpResponse::setHeader);
            
            httpResponse.getWriter().write(
                "{\"error\":{\"code\":\"RATE_LIMIT_EXCEEDED\",\"message\":\"Too many requests\"}}"
            );
            return;
        }
        
        // Add rate limit headers
        Map<String, String> headers = rateLimitingService.getRateLimitHeaders(
            clientKey, maxRequests, windowSeconds
        );
        headers.forEach(httpResponse::setHeader);
        
        chain.doFilter(request, response);
    }
    
    private String getClientKey(HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        String ipAddress = getClientIpAddress(request);
        
        return userId != null ? "user:" + userId : "ip:" + ipAddress;
    }
    
    private int getMaxRequests(String path) {
        if (path.contains("/auth/")) return 5; // Auth endpoints
        if (path.contains("/files/")) return 10; // File uploads
        return 100; // General API
    }
    
    private int getWindowSeconds(String path) {
        if (path.contains("/auth/")) return 60; // 1 minute for auth
        if (path.contains("/files/")) return 60; // 1 minute for uploads
        return 60; // 1 minute for general API
    }
}
```

## 🔐 Password Security

### Password Policy
```java
@Component
public class PasswordPolicy {
    
    public void validatePassword(String password) {
        if (password == null) {
            throw new PasswordValidationException("Password cannot be null");
        }
        
        // Minimum length
        if (password.length() < 8) {
            throw new PasswordValidationException("Password must be at least 8 characters");
        }
        
        // Maximum length
        if (password.length() > 128) {
            throw new PasswordValidationException("Password cannot exceed 128 characters");
        }
        
        // Complexity requirements
        if (!password.matches(".*[A-Z].*")) {
            throw new PasswordValidationException("Password must contain at least one uppercase letter");
        }
        
        if (!password.matches(".*[a-z].*")) {
            throw new PasswordValidationException("Password must contain at least one lowercase letter");
        }
        
        if (!password.matches(".*[0-9].*")) {
            throw new PasswordValidationException("Password must contain at least one digit");
        }
        
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"|,.<>/?].*")) {
            throw new PasswordValidationException("Password must contain at least one special character");
        }
        
        // Common password check
        if (isCommonPassword(password)) {
            throw new PasswordValidationException("Password is too common");
        }
    }
    
    private boolean isCommonPassword(String password) {
        List<String> commonPasswords = Arrays.asList(
            "password", "123456", "qwerty", "admin", "letmein"
        );
        
        return commonPasswords.stream()
            .anyMatch(common -> password.toLowerCase().contains(common));
    }
}
```

### Password Hashing
```java
@Component
public class PasswordService {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}

@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Strong strength
    }
}
```

## 🔍 Security Monitoring

### Intrusion Detection
```java
@Component
public class IntrusionDetectionService {
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @Scheduled(fixedRate = 60000) // Check every minute
    public void detectSuspiciousActivity() {
        
        // Detect multiple failed logins
        List<AuditLog> failedLogins = auditLogRepository
            .findByActionAndTimestampAfter("LOGIN_FAILED", 
                Instant.now().minusSeconds(300)); // Last 5 minutes
        
        Map<String, Long> failedLoginsByUser = failedLogins.stream()
            .collect(Collectors.groupingBy(
                AuditLog::getUserId, Collectors.counting()
            ));
        
        failedLoginsByUser.entrySet().stream()
            .filter(entry -> entry.getValue() >= 5)
            .forEach(entry -> {
                log.warn("Multiple failed login attempts detected for user: {}", 
                    entry.getKey());
                // Trigger security alert
                triggerSecurityAlert("MULTIPLE_FAILED_LOGINS", entry.getKey());
            });
        
        // Detect unusual access patterns
        detectUnusualAccessPatterns();
        
        // Detect privilege escalation attempts
        detectPrivilegeEscalation();
    }
    
    private void detectUnusualAccessPatterns() {
        // Detect access from unusual locations
        List<AuditLog> recentLogins = auditLogRepository
            .findByActionAndTimestampAfter("LOGIN", 
                Instant.now().minusSeconds(3600)); // Last hour
        
        Map<String, Set<String>> userLocations = recentLogins.stream()
            .collect(Collectors.groupingBy(
                AuditLog::getUserId,
                Collectors.mapping(AuditLog::getIpAddress, Collectors.toSet())
            ));
        
        userLocations.entrySet().stream()
            .filter(entry -> entry.getValue().size() > 3) // Access from >3 locations
            .forEach(entry -> {
                log.warn("Unusual access pattern detected for user: {} from locations: {}", 
                    entry.getKey(), entry.getValue());
                triggerSecurityAlert("UNUSUAL_ACCESS_PATTERN", entry.getKey());
            });
    }
    
    private void detectPrivilegeEscalation() {
        // Detect attempts to access unauthorized resources
        List<AuditLog> accessDenied = auditLogRepository
            .findByActionAndTimestampAfter("ACCESS_DENIED", 
                Instant.now().minusSeconds(300)); // Last 5 minutes
        
        if (accessDenied.size() > 10) {
            log.warn("High number of access denied attempts detected: {}", 
                accessDenied.size());
            triggerSecurityAlert("PRIVILEGE_ESCALATION_ATTEMPT", null);
        }
    }
    
    private void triggerSecurityAlert(String alertType, String userId) {
        // Send alert to security team
        // Block user account if necessary
        // Log security incident
    }
}
```

### Security Metrics
```java
@Component
public class SecurityMetricsService {
    
    @Autowired
    private MeterRegistry meterRegistry;
    
    public void recordAuthenticationAttempt(String result) {
        Counter.builder("authentication.attempts")
            .tag("result", result)
            .register(meterRegistry)
            .increment();
    }
    
    public void recordAuthorizationAttempt(String result, String resource) {
        Counter.builder("authorization.attempts")
            .tag("result", result)
            .tag("resource", resource)
            .register(meterRegistry)
            .increment();
    }
    
    public void recordRateLimitExceeded(String client) {
        Counter.builder("rate.limit.exceeded")
            .tag("client", client)
            .register(meterRegistry)
            .increment();
    }
    
    public void recordSecurityIncident(String type) {
        Counter.builder("security.incidents")
            .tag("type", type)
            .register(meterRegistry)
            .increment();
    }
}
```

## 📋 Security Checklist

### Development Security Checklist

#### Authentication
- [ ] JWT tokens are properly signed and validated
- [ ] Token expiration is enforced
- [ ] Refresh tokens are used appropriately
- [ ] Password policy is enforced
- [ ] Passwords are properly hashed
- [ ] Multi-factor authentication is implemented where needed

#### Authorization
- [ ] Role-based access control is implemented
- [ ] Resource ownership is verified
- [ ] Principle of least privilege is followed
- [ ] Method-level security is properly configured

#### Data Protection
- [ ] Sensitive data is encrypted at rest
- [ ] Data is encrypted in transit
- [ ] PII is properly masked in logs
- [ ] Data retention policies are enforced
- [ ] Backup encryption is implemented

#### Network Security
- [ ] HTTPS is enforced in production
- [ ] Security headers are properly configured
- [ ] CORS is properly configured
- [ ] Certificate pinning is implemented
- [ ] API rate limiting is enforced

#### Input Validation
- [ ] All inputs are validated
- [ ] SQL injection is prevented
- [ ] XSS is prevented
- [ ] File upload validation is implemented
- [ ] Input sanitization is performed

#### Monitoring & Logging
- [ ] Security events are logged
- [ ] Intrusion detection is implemented
- [ ] Security metrics are collected
- [ ] Log analysis is automated
- [ ] Security alerts are configured

### Production Security Checklist

#### Infrastructure
- [ ] Firewall rules are properly configured
- [ ] DDoS protection is implemented
- [ ] Load balancers are secure
- [ ] CDN is configured for security
- [ ] DNS security is implemented

#### Application Security
- [ ] Security headers are verified
- [ ] SSL/TLS configuration is valid
- [ ] Security testing is performed
- [ ] Vulnerability scanning is performed
- [ ] Penetration testing is conducted

#### Compliance
- [ ] Data protection regulations are followed
- [ ] Privacy policy is implemented
- [ ] User consent is obtained
- [ ] Data breach notification is planned
- [ ] Security documentation is maintained

## 🚨 Incident Response

### Security Incident Response Plan

#### 1. Detection
- Automated monitoring alerts
- User reports
- Security team monitoring
- Third-party security notifications

#### 2. Analysis
- Incident classification
- Impact assessment
- Root cause analysis
- Evidence preservation

#### 3. Containment
- Isolate affected systems
- Block malicious traffic
- Disable compromised accounts
- Implement temporary fixes

#### 4. Eradication
- Remove malware
- Patch vulnerabilities
- Update security configurations
- Clean compromised systems

#### 5. Recovery
- Restore systems from backup
- Verify system integrity
- Monitor for recurrence
- Document lessons learned

#### 6. Reporting
- Incident report generation
- Stakeholder notification
- Regulatory reporting (if required)
- Public communication (if needed)

## 📞 Security Contact

### Security Team
- **Security Lead**: security@edham-logistics.com
- **Incident Response**: incident@edham-logistics.com
- **Vulnerability Reports**: security@edham-logistics.com
- **Emergency**: +966-50-XXX-XXXX (24/7)

### Reporting Security Issues
If you discover a security vulnerability, please:
1. Email details to security@edham-logistics.com
2. Include steps to reproduce
3. Allow us reasonable time to fix before disclosure
4. Follow responsible disclosure practices

### Recognition Program
- Bug bounty program available
- Recognition for responsible disclosure
- Security hall of fame
- Monetary rewards for critical vulnerabilities

---

This security guidelines document provides comprehensive measures for protecting the Edham Logistics backend system. Regular security reviews and updates are essential to maintain system security and protect user data.
