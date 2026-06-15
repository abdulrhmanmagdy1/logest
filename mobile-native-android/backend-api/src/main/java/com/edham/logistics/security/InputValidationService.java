package com.edham.logistics.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Comprehensive input validation service for security
 * Validates all inputs to prevent injection attacks
 */
@Slf4j
@Service
public class InputValidationService {

    private final Validator validator;
    
    // Security patterns
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(?i)(union|select|insert|update|delete|drop|create|alter|exec|script|javascript|vbscript|onload|onerror|onclick)", 
        Pattern.CASE_INSENSITIVE);
    
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "(?i)(<script|</script|<iframe|</iframe|<object|</object|<embed|</embed|javascript:|vbscript:|onload=|onerror=|onclick=)", 
        Pattern.CASE_INSENSITIVE);
    
    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(
        "(?i)(\\.\\./|\\.\\.\\/|\\.\\.|%2e%2e%2f|%2e%2e%5c)", 
        Pattern.CASE_INSENSITIVE);
    
    private static final Pattern COMMAND_INJECTION_PATTERN = Pattern.compile(
        "(?i)(\\|\\||&&|;|\\$\\(|\\`|\\$\\{|\\$\\{)", 
        Pattern.CASE_INSENSITIVE);

    public InputValidationService() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    /**
     * Validate and sanitize string input
     */
    public ValidationResult validateString(String input, String fieldName, int maxLength) {
        ValidationResult result = new ValidationResult();
        result.setValid(true);
        
        if (input == null) {
            result.setValid(false);
            result.addError(fieldName, "Input cannot be null");
            return result;
        }

        // Check length
        if (input.length() > maxLength) {
            result.setValid(false);
            result.addError(fieldName, "Input exceeds maximum length of " + maxLength);
        }

        // Check for SQL injection
        if (SQL_INJECTION_PATTERN.matcher(input).find()) {
            result.setValid(false);
            result.addError(fieldName, "Input contains potentially malicious SQL code");
        }

        // Check for XSS
        if (XSS_PATTERN.matcher(input).find()) {
            result.setValid(false);
            result.addError(fieldName, "Input contains potentially malicious script code");
        }

        // Check for path traversal
        if (PATH_TRAVERSAL_PATTERN.matcher(input).find()) {
            result.setValid(false);
            result.addError(fieldName, "Input contains potentially malicious path traversal");
        }

        // Check for command injection
        if (COMMAND_INJECTION_PATTERN.matcher(input).find()) {
            result.setValid(false);
            result.addError(fieldName, "Input contains potentially malicious command injection");
        }

        // Check for null bytes
        if (input.contains("\0")) {
            result.setValid(false);
            result.addError(fieldName, "Input contains null bytes");
        }

        return result;
    }

    /**
     * Validate email address
     */
    public ValidationResult validateEmail(String email, String fieldName) {
        ValidationResult result = validateString(email, fieldName, 255);
        
        if (!result.isValid()) {
            return result;
        }

        // Email format validation
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            result.setValid(false);
            result.addError(fieldName, "Invalid email format");
        }

        return result;
    }

    /**
     * Validate phone number
     */
    public ValidationResult validatePhone(String phone, String fieldName) {
        ValidationResult result = validateString(phone, fieldName, 20);
        
        if (!result.isValid()) {
            return result;
        }

        // Phone format validation (international format)
        String phoneRegex = "^\\+?[0-9]{10,15}$";
        if (!phone.matches(phoneRegex)) {
            result.setValid(false);
            result.addError(fieldName, "Invalid phone number format");
        }

        return result;
    }

    /**
     * Validate numeric input
     */
    public ValidationResult validateNumeric(String input, String fieldName, double minValue, double maxValue) {
        ValidationResult result = validateString(input, fieldName, 50);
        
        if (!result.isValid()) {
            return result;
        }

        try {
            double value = Double.parseDouble(input);
            
            if (value < minValue) {
                result.setValid(false);
                result.addError(fieldName, "Value must be at least " + minValue);
            }
            
            if (value > maxValue) {
                result.setValid(false);
                result.addError(fieldName, "Value must be at most " + maxValue);
            }
            
        } catch (NumberFormatException e) {
            result.setValid(false);
            result.addError(fieldName, "Invalid numeric format");
        }

        return result;
    }

    /**
     * Validate date input
     */
    public ValidationResult validateDate(String input, String fieldName) {
        ValidationResult result = validateString(input, fieldName, 50);
        
        if (!result.isValid()) {
            return result;
        }

        // Date format validation (ISO format)
        try {
            java.time.LocalDate.parse(input);
        } catch (java.time.format.DateTimeParseException e) {
            result.setValid(false);
            result.addError(fieldName, "Invalid date format. Use YYYY-MM-DD");
        }

        return result;
    }

    /**
     * Validate ID input
     */
    public ValidationResult validateId(String input, String fieldName) {
        ValidationResult result = validateString(input, fieldName, 50);
        
        if (!result.isValid()) {
            return result;
        }

        // ID should be numeric
        if (!input.matches("^[0-9]+$")) {
            result.setValid(false);
            result.addError(fieldName, "ID must be numeric");
        }

        return result;
    }

    /**
     * Validate pagination parameters
     */
    public ValidationResult validatePagination(Integer page, Integer size) {
        ValidationResult result = new ValidationResult();
        result.setValid(true);

        if (page != null && page < 0) {
            result.setValid(false);
            result.addError("page", "Page number must be non-negative");
        }

        if (size != null) {
            if (size < 1) {
                result.setValid(false);
                result.addError("size", "Page size must be at least 1");
            }
            
            if (size > 1000) {
                result.setValid(false);
                result.addError("size", "Page size cannot exceed 1000");
            }
        }

        return result;
    }

    /**
     * Validate file upload
     */
    public ValidationResult validateFileUpload(String fileName, String contentType, long fileSize) {
        ValidationResult result = new ValidationResult();
        result.setValid(true);

        // Validate file name
        if (fileName != null) {
            ValidationResult fileNameResult = validateString(fileName, "fileName", 255);
            if (!fileNameResult.isValid()) {
                result.setValid(false);
                result.getErrors().putAll(fileNameResult.getErrors());
            }

            // Check for dangerous file extensions
            String[] dangerousExtensions = {".exe", ".bat", ".cmd", ".scr", ".pif", ".com"};
            for (String ext : dangerousExtensions) {
                if (fileName.toLowerCase().endsWith(ext)) {
                    result.setValid(false);
                    result.addError("fileName", "File type not allowed");
                    break;
                }
            }
        }

        // Validate content type
        if (contentType != null) {
            String[] allowedTypes = {
                "image/jpeg", "image/png", "image/gif", "application/pdf",
                "text/plain", "application/json", "application/xml"
            };
            
            boolean allowedType = Arrays.asList(allowedTypes).contains(contentType);
            if (!allowedType) {
                result.setValid(false);
                result.addError("contentType", "Content type not allowed");
            }
        }

        // Validate file size (10MB max)
        if (fileSize > 10 * 1024 * 1024) {
            result.setValid(false);
            result.addError("fileSize", "File size cannot exceed 10MB");
        }

        return result;
    }

    /**
     * Validate JWT token
     */
    public ValidationResult validateJwtToken(String token) {
        ValidationResult result = new ValidationResult();
        result.setValid(true);

        if (token == null || token.trim().isEmpty()) {
            result.setValid(false);
            result.addError("token", "Token cannot be null or empty");
            return result;
        }

        // Check token format
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            result.setValid(false);
            result.addError("token", "Invalid token format");
            return result;
        }

        try {
            // Try to decode header and payload
            java.util.Base64.getDecoder().decode(parts[0]);
            java.util.Base64.getDecoder().decode(parts[1]);
        } catch (IllegalArgumentException e) {
            result.setValid(false);
            result.addError("token", "Invalid token encoding");
        }

        return result;
    }

    /**
     * Validate object using Bean Validation
     */
    public <T> ValidationResult validateObject(T object) {
        ValidationResult result = new ValidationResult();
        result.setValid(true);

        if (object == null) {
            result.setValid(false);
            result.addError("object", "Object cannot be null");
            return result;
        }

        Set<ConstraintViolation<T>> violations = validator.validate(object);
        
        for (ConstraintViolation<T> violation : violations) {
            String fieldName = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            result.addError(fieldName, message);
        }

        result.setValid(violations.isEmpty());
        return result;
    }

    /**
     * Sanitize input string
     */
    public String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }

        // Remove null bytes
        String sanitized = input.replace("\0", "");
        
        // Remove potentially dangerous characters
        sanitized = sanitized.replaceAll("[<>\"'&]", "");
        
        // Trim whitespace
        sanitized = sanitized.trim();
        
        return sanitized;
    }

    /**
     * Escape SQL input
     */
    public String escapeSql(String input) {
        if (input == null) {
            return null;
        }

        // Basic SQL escaping (in production, use prepared statements)
        return input.replace("'", "''")
                   .replace("\"", "\\\"")
                   .replace("\\", "\\\\")
                   .replace("%", "\\%")
                   .replace("_", "\\_");
    }

    /**
     * Validate API key
     */
    public ValidationResult validateApiKey(String apiKey) {
        ValidationResult result = validateString(apiKey, "apiKey", 255);
        
        if (!result.isValid()) {
            return result;
        }

        // API key format validation
        if (!apiKey.matches("^[A-Za-z0-9_-]{20,}$")) {
            result.setValid(false);
            result.addError("apiKey", "Invalid API key format");
        }

        return result;
    }

    /**
     * Validate coordinates
     */
    public ValidationResult validateCoordinates(Double latitude, Double longitude) {
        ValidationResult result = new ValidationResult();
        result.setValid(true);

        if (latitude != null) {
            if (latitude < -90 || latitude > 90) {
                result.setValid(false);
                result.addError("latitude", "Latitude must be between -90 and 90");
            }
        }

        if (longitude != null) {
            if (longitude < -180 || longitude > 180) {
                result.setValid(false);
                result.addError("longitude", "Longitude must be between -180 and 180");
            }
        }

        return result;
    }

    /**
     * Validate search query
     */
    public ValidationResult validateSearchQuery(String query) {
        ValidationResult result = validateString(query, "query", 500);
        
        if (!result.isValid()) {
            return result;
        }

        // Additional search-specific validation
        if (query.length() < 2) {
            result.setValid(false);
            result.addError("query", "Search query must be at least 2 characters");
        }

        return result;
    }

    /**
     * Validate batch input
     */
    public ValidationResult validateBatchInput(List<?> items, String fieldName, int maxSize) {
        ValidationResult result = new ValidationResult();
        result.setValid(true);

        if (items == null) {
            result.setValid(false);
            result.addError(fieldName, "Batch input cannot be null");
            return result;
        }

        if (items.size() > maxSize) {
            result.setValid(false);
            result.addError(fieldName, "Batch size cannot exceed " + maxSize);
        }

        return result;
    }

    /**
     * Result class for validation
     */
    public static class ValidationResult {
        private boolean valid;
        private Map<String, String> errors = new HashMap<>();

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public Map<String, String> getErrors() {
            return errors;
        }

        public void addError(String field, String message) {
            this.errors.put(field, message);
        }
    }
}
