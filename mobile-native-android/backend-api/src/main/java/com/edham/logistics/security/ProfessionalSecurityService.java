package com.edham.logistics.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.KeySpec;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Base64;

/**
 * Professional Security Service for Application Hardening
 */
@Service
@Transactional
public class ProfessionalSecurityService {

    @Value("${app.security.encryption.key:defaultEncryptionKey1234567890123456}")
    private String encryptionKey;

    @Value("${app.security.jwt.secret:defaultJwtSecretKeyForProfessionalSecurity}")
    private String jwtSecret;

    @Value("${app.security.jwt.expiration:3600000}")
    private long jwtExpirationMs;

    @Value("${app.security.rate.limit.requests:100}")
    private int rateLimitRequests;

    @Value("${app.security.rate.limit.window:60000}")
    private int rateLimitWindowMs;

    @Value("${app.security.login.max.attempts:5}")
    private int maxLoginAttempts;

    @Value("${app.security.login.lockout.duration:900000}")
    private long lockoutDurationMs;

    // In-memory stores for rate limiting and login attempts
    private final Map<String, RateLimitInfo> rateLimitStore = new ConcurrentHashMap<>();
    private final Map<String, LoginAttemptInfo> loginAttemptStore = new ConcurrentHashMap<>();
    private final Map<String, DeviceBindingInfo> deviceBindingStore = new ConcurrentHashMap<>();
    private final Map<String, TokenBlacklist> tokenBlacklist = new ConcurrentHashMap<>();

    // Encryption constants
    private static final String AES_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String AES_KEY_ALGORITHM = "AES";
    private static final int AES_KEY_LENGTH = 256;
    private static final int IV_LENGTH = 16;
    private static final String HASH_ALGORITHM = "SHA-256";
    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int PBKDF2_ITERATIONS = 10000;
    private static final int SALT_LENGTH = 32;

    /**
     * Encryption/Decryption Result
     */
    public static class EncryptionResult {
        private String encryptedData;
        private String iv;
        private String salt;

        public EncryptionResult(String encryptedData, String iv, String salt) {
            this.encryptedData = encryptedData;
            this.iv = iv;
            this.salt = salt;
        }

        // Getters
        public String getEncryptedData() { return encryptedData; }
        public String getIv() { return iv; }
        public String getSalt() { return salt; }
    }

    /**
     * Rate Limit Information
     */
    public static class RateLimitInfo {
        private final String identifier;
        private int requestCount;
        private long windowStart;
        private long lastReset;

        public RateLimitInfo(String identifier) {
            this.identifier = identifier;
            this.requestCount = 0;
            this.windowStart = System.currentTimeMillis();
            this.lastReset = System.currentTimeMillis();
        }

        // Getters and setters
        public String getIdentifier() { return identifier; }
        public int getRequestCount() { return requestCount; }
        public void setRequestCount(int requestCount) { this.requestCount = requestCount; }
        public long getWindowStart() { return windowStart; }
        public void setWindowStart(long windowStart) { this.windowStart = windowStart; }
        public long getLastReset() { return lastReset; }
        public void setLastReset(long lastReset) { this.lastReset = lastReset; }
    }

    /**
     * Login Attempt Information
     */
    public static class LoginAttemptInfo {
        private final String identifier;
        private int attemptCount;
        private LocalDateTime lastAttempt;
        private LocalDateTime lockoutUntil;
        private boolean isLocked;

        public LoginAttemptInfo(String identifier) {
            this.identifier = identifier;
            this.attemptCount = 0;
            this.lastAttempt = LocalDateTime.now();
            this.lockoutUntil = null;
            this.isLocked = false;
        }

        // Getters and setters
        public String getIdentifier() { return identifier; }
        public int getAttemptCount() { return attemptCount; }
        public void setAttemptCount(int attemptCount) { this.attemptCount = attemptCount; }
        public LocalDateTime getLastAttempt() { return lastAttempt; }
        public void setLastAttempt(LocalDateTime lastAttempt) { this.lastAttempt = lastAttempt; }
        public LocalDateTime getLockoutUntil() { return lockoutUntil; }
        public void setLockoutUntil(LocalDateTime lockoutUntil) { this.lockoutUntil = lockoutUntil; }
        public boolean isLocked() { return isLocked; }
        public void setLocked(boolean locked) { isLocked = locked; }
    }

    /**
     * Device Binding Information
     */
    public static class DeviceBindingInfo {
        private final String userId;
        private final String deviceId;
        private final String deviceFingerprint;
        private final LocalDateTime boundAt;
        private boolean isActive;
        private String lastUsed;

        public DeviceBindingInfo(String userId, String deviceId, String deviceFingerprint) {
            this.userId = userId;
            this.deviceId = deviceId;
            this.deviceFingerprint = deviceFingerprint;
            this.boundAt = LocalDateTime.now();
            this.isActive = true;
            this.lastUsed = LocalDateTime.now().toString();
        }

        // Getters and setters
        public String getUserId() { return userId; }
        public String getDeviceId() { return deviceId; }
        public String getDeviceFingerprint() { return deviceFingerprint; }
        public LocalDateTime getBoundAt() { return boundAt; }
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }
        public String getLastUsed() { return lastUsed; }
        public void setLastUsed(String lastUsed) { this.lastUsed = lastUsed; }
    }

    /**
     * Token Blacklist Information
     */
    public static class TokenBlacklist {
        private final String tokenId;
        private final LocalDateTime blacklistedAt;
        private final LocalDateTime expiresAt;
        private final String reason;

        public TokenBlacklist(String tokenId, LocalDateTime expiresAt, String reason) {
            this.tokenId = tokenId;
            this.blacklistedAt = LocalDateTime.now();
            this.expiresAt = expiresAt;
            this.reason = reason;
        }

        // Getters
        public String getTokenId() { return tokenId; }
        public LocalDateTime getBlacklistedAt() { return blacklistedAt; }
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public String getReason() { return reason; }
    }

    /**
     * Security Event
     */
    public static class SecurityEvent {
        private final String eventId;
        private final String eventType;
        private final String identifier;
        private final String description;
        private final LocalDateTime timestamp;
        private final String ipAddress;
        private final String userAgent;
        private final Map<String, Object> metadata;

        public SecurityEvent(String eventType, String identifier, String description, 
                           String ipAddress, String userAgent, Map<String, Object> metadata) {
            this.eventId = UUID.randomUUID().toString();
            this.eventType = eventType;
            this.identifier = identifier;
            this.description = description;
            this.timestamp = LocalDateTime.now();
            this.ipAddress = ipAddress;
            this.userAgent = userAgent;
            this.metadata = metadata != null ? metadata : new HashMap<>();
        }

        // Getters
        public String getEventId() { return eventId; }
        public String getEventType() { return eventType; }
        public String getIdentifier() { return identifier; }
        public String getDescription() { return description; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getIpAddress() { return ipAddress; }
        public String getUserAgent() { return userAgent; }
        public Map<String, Object> getMetadata() { return metadata; }
    }

    /**
     * Encrypt sensitive data using AES-256
     */
    public EncryptionResult encryptSensitiveData(String data) throws Exception {
        try {
            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Derive key from password and salt
            SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            KeySpec spec = new PBEKeySpec(encryptionKey.toCharArray(), salt, PBKDF2_ITERATIONS, AES_KEY_LENGTH);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), AES_KEY_ALGORITHM);

            // Generate random IV
            byte[] iv = new byte[IV_LENGTH];
            random.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            // Encrypt data
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Return encrypted result
            return new EncryptionResult(
                Base64.getEncoder().encodeToString(encryptedData),
                Base64.getEncoder().encodeToString(iv),
                Base64.getEncoder().encodeToString(salt)
            );
        } catch (Exception e) {
            throw new SecurityException("Failed to encrypt sensitive data", e);
        }
    }

    /**
     * Decrypt sensitive data using AES-256
     */
    public String decryptSensitiveData(String encryptedData, String iv, String salt) throws Exception {
        try {
            // Decode base64 components
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] ivBytes = Base64.getDecoder().decode(iv);
            byte[] saltBytes = Base64.getDecoder().decode(salt);

            // Derive key from password and salt
            SecretKeyFactory factory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
            KeySpec spec = new PBEKeySpec(encryptionKey.toCharArray(), saltBytes, PBKDF2_ITERATIONS, AES_KEY_LENGTH);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), AES_KEY_ALGORITHM);

            // Create IV spec
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            // Decrypt data
            Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            byte[] decryptedData = cipher.doFinal(encryptedBytes);

            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new SecurityException("Failed to decrypt sensitive data", e);
        }
    }

    /**
     * Generate secure hash for sensitive data
     */
    public String generateSecureHash(String data, String salt) throws Exception {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            String saltedData = data + salt;
            byte[] hashBytes = digest.digest(saltedData.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new SecurityException("Failed to generate secure hash", e);
        }
    }

    /**
     * Generate random salt
     */
    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Check rate limit for API requests
     */
    public boolean checkRateLimit(String identifier) {
        long currentTime = System.currentTimeMillis();
        RateLimitInfo rateLimitInfo = rateLimitStore.computeIfAbsent(identifier, RateLimitInfo::new);

        // Reset window if expired
        if (currentTime - rateLimitInfo.getLastReset() >= rateLimitWindowMs) {
            rateLimitInfo.setRequestCount(0);
            rateLimitInfo.setWindowStart(currentTime);
            rateLimitInfo.setLastReset(currentTime);
        }

        // Check if rate limit exceeded
        if (rateLimitInfo.getRequestCount() >= rateLimitRequests) {
            logSecurityEvent("RATE_LIMIT_EXCEEDED", identifier, 
                "Rate limit exceeded: " + rateLimitInfo.getRequestCount() + " requests", null, null, null);
            return false;
        }

        // Increment request count
        rateLimitInfo.setRequestCount(rateLimitInfo.getRequestCount() + 1);
        return true;
    }

    /**
     * Handle login attempt
     */
    public LoginAttemptInfo handleLoginAttempt(String identifier, boolean success, String ipAddress, String userAgent) {
        LoginAttemptInfo attemptInfo = loginAttemptStore.computeIfAbsent(identifier, LoginAttemptInfo::new);
        
        attemptInfo.setLastAttempt(LocalDateTime.now());

        if (success) {
            // Reset on successful login
            attemptInfo.setAttemptCount(0);
            attemptInfo.setLocked(false);
            attemptInfo.setLockoutUntil(null);
            
            logSecurityEvent("LOGIN_SUCCESS", identifier, "Successful login", ipAddress, userAgent, null);
        } else {
            // Increment failed attempts
            attemptInfo.setAttemptCount(attemptInfo.getAttemptCount() + 1);

            // Check if should lock account
            if (attemptInfo.getAttemptCount() >= maxLoginAttempts) {
                attemptInfo.setLocked(true);
                attemptInfo.setLockoutUntil(LocalDateTime.now().plusNanos(lockoutDurationMs * 1_000_000));
                
                logSecurityEvent("ACCOUNT_LOCKED", identifier, 
                    "Account locked due to multiple failed attempts: " + attemptInfo.getAttemptCount(), 
                    ipAddress, userAgent, Map.of("lockoutDuration", lockoutDurationMs));
            } else {
                logSecurityEvent("LOGIN_FAILED", identifier, 
                    "Failed login attempt: " + attemptInfo.getAttemptCount() + "/" + maxLoginAttempts, 
                    ipAddress, userAgent, null);
            }
        }

        return attemptInfo;
    }

    /**
     * Check if account is locked
     */
    public boolean isAccountLocked(String identifier) {
        LoginAttemptInfo attemptInfo = loginAttemptStore.get(identifier);
        if (attemptInfo == null) {
            return false;
        }

        // Check if lockout has expired
        if (attemptInfo.isLocked() && attemptInfo.getLockoutUntil() != null) {
            if (LocalDateTime.now().isAfter(attemptInfo.getLockoutUntil())) {
                attemptInfo.setLocked(false);
                attemptInfo.setLockoutUntil(null);
                attemptInfo.setAttemptCount(0);
                return false;
            }
        }

        return attemptInfo.isLocked();
    }

    /**
     * Bind device to admin account
     */
    public DeviceBindingInfo bindDeviceToAccount(String userId, String deviceId, String deviceFingerprint) {
        DeviceBindingInfo bindingInfo = new DeviceBindingInfo(userId, deviceId, deviceFingerprint);
        deviceBindingStore.put(userId + ":" + deviceId, bindingInfo);
        
        logSecurityEvent("DEVICE_BOUND", userId, 
            "Device bound to account: " + deviceId, null, null, 
            Map.of("deviceId", deviceId, "fingerprint", deviceFingerprint));
        
        return bindingInfo;
    }

    /**
     * Verify device binding
     */
    public boolean verifyDeviceBinding(String userId, String deviceId, String deviceFingerprint) {
        String key = userId + ":" + deviceId;
        DeviceBindingInfo bindingInfo = deviceBindingStore.get(key);
        
        if (bindingInfo == null || !bindingInfo.isActive()) {
            logSecurityEvent("DEVICE_BINDING_FAILED", userId, 
                "Device binding verification failed: " + deviceId, null, null, 
                Map.of("deviceId", deviceId, "fingerprint", deviceFingerprint));
            return false;
        }

        // Verify fingerprint (simplified - in production, use more sophisticated matching)
        boolean isValid = deviceFingerprint.equals(bindingInfo.getDeviceFingerprint());
        
        if (!isValid) {
            logSecurityEvent("DEVICE_FINGERPRINT_MISMATCH", userId, 
                "Device fingerprint mismatch: " + deviceId, null, null, 
                Map.of("deviceId", deviceId, "expectedFingerprint", bindingInfo.getDeviceFingerprint(), 
                       "actualFingerprint", deviceFingerprint));
        } else {
            bindingInfo.setLastUsed(LocalDateTime.now().toString());
        }

        return isValid;
    }

    /**
     * Generate device fingerprint
     */
    public String generateDeviceFingerprint(String userAgent, String ipAddress, Map<String, String> additionalData) {
        try {
            // Combine various device characteristics
            StringBuilder fingerprintData = new StringBuilder();
            fingerprintData.append(userAgent != null ? userAgent : "");
            fingerprintData.append(ipAddress != null ? ipAddress : "");
            
            if (additionalData != null) {
                additionalData.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> fingerprintData.append(entry.getKey()).append(entry.getValue()));
            }

            // Generate hash
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(fingerprintData.toString().getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new SecurityException("Failed to generate device fingerprint", e);
        }
    }

    /**
     * Blacklist token
     */
    public void blacklistToken(String tokenId, LocalDateTime expiresAt, String reason) {
        TokenBlacklist blacklistInfo = new TokenBlacklist(tokenId, expiresAt, reason);
        tokenBlacklist.put(tokenId, blacklistInfo);
        
        logSecurityEvent("TOKEN_BLACKLISTED", tokenId, 
            "Token blacklisted: " + reason, null, null, 
            Map.of("expiresAt", expiresAt.toString()));
    }

    /**
     * Check if token is blacklisted
     */
    public boolean isTokenBlacklisted(String tokenId) {
        TokenBlacklist blacklistInfo = tokenBlacklist.get(tokenId);
        if (blacklistInfo == null) {
            return false;
        }

        // Check if blacklist entry has expired
        if (LocalDateTime.now().isAfter(blacklistInfo.getExpiresAt())) {
            tokenBlacklist.remove(tokenId);
            return false;
        }

        return true;
    }

    /**
     * Handle token expiration
     */
    public void handleTokenExpiration(String tokenId, String userId) {
        blacklistToken(tokenId, LocalDateTime.now().plusDays(1), "Token expired naturally");
        
        logSecurityEvent("TOKEN_EXPIRED", userId, 
            "Token expired: " + tokenId, null, null, 
            Map.of("tokenId", tokenId));
    }

    /**
     * Validate token expiration
     */
    public boolean validateTokenExpiration(String tokenId, LocalDateTime issuedAt) {
        if (issuedAt == null) {
            return false;
        }

        LocalDateTime expirationTime = issuedAt.plusNanos(jwtExpirationMs * 1_000_000);
        return LocalDateTime.now().isBefore(expirationTime);
    }

    /**
     * Generate secure random token
     */
    public String generateSecureToken() {
        SecureRandom random = new SecureRandom();
        byte[] tokenBytes = new byte[32];
        random.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }

    /**
     * Validate password strength
     */
    public boolean validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        // Check for at least one uppercase letter
        if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            return false;
        }

        // Check for at least one lowercase letter
        if (!Pattern.compile("[a-z]").matcher(password).find()) {
            return false;
        }

        // Check for at least one digit
        if (!Pattern.compile("[0-9]").matcher(password).find()) {
            return false;
        }

        // Check for at least one special character
        if (!Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]").matcher(password).find()) {
            return false;
        }

        return true;
    }

    /**
     * Sanitize input to prevent injection attacks
     */
    public String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }

        // Remove potentially dangerous characters
        return input.replaceAll("[<>\"'&]", "");
    }

    /**
     * Log security event
     */
    public void logSecurityEvent(String eventType, String identifier, String description, 
                               String ipAddress, String userAgent, Map<String, Object> metadata) {
        SecurityEvent event = new SecurityEvent(eventType, identifier, description, ipAddress, userAgent, metadata);
        
        // In production, this would be sent to a security logging system
        System.out.println("SECURITY_EVENT: " + event.getEventType() + " - " + event.getDescription() + 
                          " at " + event.getTimestamp());
        
        // Additional processing for critical events
        if ("ACCOUNT_LOCKED".equals(eventType) || "RATE_LIMIT_EXCEEDED".equals(eventType)) {
            handleCriticalSecurityEvent(event);
        }
    }

    /**
     * Handle critical security events
     */
    private void handleCriticalSecurityEvent(SecurityEvent event) {
        // In production, this would trigger alerts, notifications, etc.
        System.err.println("CRITICAL_SECURITY_EVENT: " + event.getEventType() + 
                          " - " + event.getDescription() + 
                          " for identifier: " + event.getIdentifier());
    }

    /**
     * Get security statistics
     */
    public Map<String, Object> getSecurityStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Rate limiting stats
        stats.put("activeRateLimits", rateLimitStore.size());
        stats.put("rateLimitRequests", rateLimitRequests);
        stats.put("rateLimitWindowMs", rateLimitWindowMs);
        
        // Login attempt stats
        stats.put("activeLoginAttempts", loginAttemptStore.size());
        stats.put("maxLoginAttempts", maxLoginAttempts);
        stats.put("lockoutDurationMs", lockoutDurationMs);
        
        // Device binding stats
        stats.put("activeDeviceBindings", deviceBindingStore.size());
        
        // Token blacklist stats
        stats.put("blacklistedTokens", tokenBlacklist.size());
        
        // Clean up expired entries
        cleanupExpiredEntries();
        
        return stats;
    }

    /**
     * Clean up expired entries
     */
    private void cleanupExpiredEntries() {
        long currentTime = System.currentTimeMillis();
        
        // Clean up expired rate limit entries
        rateLimitStore.entrySet().removeIf(entry -> 
            currentTime - entry.getValue().getLastReset() > rateLimitWindowMs * 2);
        
        // Clean up expired login attempts
        loginAttemptStore.entrySet().removeIf(entry -> {
            LoginAttemptInfo info = entry.getValue();
            return info.getLockoutUntil() != null && 
                   LocalDateTime.now().isAfter(info.getLockoutUntil().plusDays(1));
        });
        
        // Clean up expired token blacklist entries
        tokenBlacklist.entrySet().removeIf(entry -> 
            LocalDateTime.now().isAfter(entry.getValue().getExpiresAt()));
    }

    /**
     * Get security configuration
     */
    public Map<String, Object> getSecurityConfiguration() {
        Map<String, Object> config = new HashMap<>();
        
        config.put("encryptionAlgorithm", AES_ALGORITHM);
        config.put("keyLength", AES_KEY_LENGTH);
        config.put("hashAlgorithm", HASH_ALGORITHM);
        config.put("pbkdf2Iterations", PBKDF2_ITERATIONS);
        config.put("jwtExpirationMs", jwtExpirationMs);
        config.put("rateLimitRequests", rateLimitRequests);
        config.put("rateLimitWindowMs", rateLimitWindowMs);
        config.put("maxLoginAttempts", maxLoginAttempts);
        config.put("lockoutDurationMs", lockoutDurationMs);
        
        return config;
    }

    /**
     * Test encryption/decryption
     */
    public boolean testEncryption() {
        try {
            String testData = "This is a test for encryption";
            EncryptionResult encrypted = encryptSensitiveData(testData);
            String decrypted = decryptSensitiveData(encrypted.getEncryptedData(), 
                                                encrypted.getIv(), encrypted.getSalt());
            return testData.equals(decrypted);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Clear all security data (for testing)
     */
    public void clearAllSecurityData() {
        rateLimitStore.clear();
        loginAttemptStore.clear();
        deviceBindingStore.clear();
        tokenBlacklist.clear();
    }
}
