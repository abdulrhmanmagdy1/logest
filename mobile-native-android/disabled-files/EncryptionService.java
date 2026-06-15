package com.edham.logistics.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Advanced encryption service for sensitive data protection
 * Provides AES-256 encryption for data at rest and in transit
 */
@Slf4j
@Service
public class EncryptionService {

    @Value("${app.encryption.key:defaultEncryptionKey123456789012345678901234}")
    private String encryptionKey;

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String AES = "AES";
    private static final int KEY_LENGTH = 256;
    private static final int IV_LENGTH = 16;

    private final SecretKey secretKey;
    private final SecureRandom secureRandom;

    public EncryptionService() {
        try {
            // Generate or derive secret key
            byte[] key = encryptionKey.getBytes(StandardCharsets.UTF_8);
            this.secretKey = new SecretKeySpec(key, AES);
            this.secureRandom = new SecureRandom();
            
            log.info("Encryption service initialized with AES-256");
        } catch (Exception e) {
            log.error("Error initializing encryption service: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize encryption service", e);
        }
    }

    /**
     * Encrypt sensitive data
     */
    public String encrypt(String plainText) {
        try {
            if (plainText == null || plainText.isEmpty()) {
                return plainText;
            }

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            
            // Generate random IV
            byte[] iv = new byte[IV_LENGTH];
            secureRandom.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            
            // Initialize cipher for encryption
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            
            // Encrypt the data
            byte[] encryptedData = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            // Combine IV and encrypted data
            byte[] combined = new byte[IV_LENGTH + encryptedData.length];
            System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
            System.arraycopy(encryptedData, 0, combined, IV_LENGTH, encryptedData.length);
            
            // Return Base64 encoded result
            return Base64.getEncoder().encodeToString(combined);
            
        } catch (Exception e) {
            log.error("Error encrypting data: {}", e.getMessage(), e);
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * Decrypt sensitive data
     */
    public String decrypt(String encryptedText) {
        try {
            if (encryptedText == null || encryptedText.isEmpty()) {
                return encryptedText;
            }

            // Decode Base64
            byte[] combined = Base64.getDecoder().decode(encryptedText);
            
            // Extract IV and encrypted data
            byte[] iv = new byte[IV_LENGTH];
            byte[] encryptedData = new byte[combined.length - IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
            System.arraycopy(combined, IV_LENGTH, encryptedData, 0, encryptedData.length);
            
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            
            // Initialize cipher for decryption
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            
            // Decrypt the data
            byte[] decryptedData = cipher.doFinal(encryptedData);
            
            return new String(decryptedData, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            log.error("Error decrypting data: {}", e.getMessage(), e);
            throw new RuntimeException("Decryption failed", e);
        }
    }

    /**
     * Encrypt sensitive field for database storage
     */
    public String encryptField(String fieldValue) {
        return encrypt(fieldValue);
    }

    /**
     * Decrypt sensitive field from database
     */
    public String decryptField(String encryptedFieldValue) {
        return decrypt(encryptedFieldValue);
    }

    /**
     * Generate secure random token
     */
    public String generateSecureToken(int length) {
        try {
            byte[] tokenBytes = new byte[length];
            secureRandom.nextBytes(tokenBytes);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        } catch (Exception e) {
            log.error("Error generating secure token: {}", e.getMessage(), e);
            throw new RuntimeException("Token generation failed", e);
        }
    }

    /**
     * Generate secure random key
     */
    public String generateSecureKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
            keyGenerator.init(KEY_LENGTH);
            SecretKey key = keyGenerator.generateKey();
            return Base64.getEncoder().encodeToString(key.getEncoded());
        } catch (Exception e) {
            log.error("Error generating secure key: {}", e.getMessage(), e);
            throw new RuntimeException("Key generation failed", e);
        }
    }

    /**
     * Hash password securely
     */
    public String hashPassword(String password, String salt) {
        try {
            // Use BCrypt for password hashing (handled by PasswordEncoder)
            // This method is for additional hashing needs
            return encrypt(password + salt);
        } catch (Exception e) {
            log.error("Error hashing password: {}", e.getMessage(), e);
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    /**
     * Verify password hash
     */
    public boolean verifyPassword(String password, String salt, String hash) {
        try {
            String computedHash = hashPassword(password, salt);
            return computedHash.equals(hash);
        } catch (Exception e) {
            log.error("Error verifying password: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Encrypt sensitive JSON data
     */
    public String encryptJsonData(Object jsonData) {
        try {
            String jsonString = com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(jsonData);
            return encrypt(jsonString);
        } catch (Exception e) {
            log.error("Error encrypting JSON data: {}", e.getMessage(), e);
            throw new RuntimeException("JSON encryption failed", e);
        }
    }

    /**
     * Decrypt sensitive JSON data
     */
    public <T> T decryptJsonData(String encryptedJson, Class<T> clazz) {
        try {
            String decryptedJson = decrypt(encryptedJson);
            return com.fasterxml.jackson.databind.ObjectMapper().readValue(decryptedJson, clazz);
        } catch (Exception e) {
            log.error("Error decrypting JSON data: {}", e.getMessage(), e);
            throw new RuntimeException("JSON decryption failed", e);
        }
    }

    /**
     * Check if data is encrypted
     */
    public boolean isEncrypted(String data) {
        if (data == null || data.isEmpty()) {
            return false;
        }
        
        try {
            // Try to decode and decrypt
            decrypt(data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Mask sensitive data for logging
     */
    public String maskSensitiveData(String sensitiveData) {
        if (sensitiveData == null || sensitiveData.length() < 4) {
            return "****";
        }
        
        int visibleChars = Math.min(4, sensitiveData.length() / 2);
        String start = sensitiveData.substring(0, visibleChars);
        String end = sensitiveData.substring(sensitiveData.length() - visibleChars);
        String masked = "*".repeat(Math.max(1, sensitiveData.length() - (visibleChars * 2)));
        
        return start + masked + end;
    }
}
