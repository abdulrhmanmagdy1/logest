/**
 * ============================================
 * ✅ Validators - نظام إدهام
 * Input validation utilities for client app
 * ============================================
 */

/**
 * Validate email address
 * @param {string} email - Email to validate
 * @returns {boolean} Is valid
 */
export const isValidEmail = (email) => {
  if (!email) return false;
  
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return emailRegex.test(email);
};

/**
 * Validate Saudi phone number
 * @param {string} phone - Phone number to validate
 * @returns {boolean} Is valid
 */
export const isValidPhone = (phone) => {
  if (!phone) return false;
  
  const phoneRegex = /^(05|5)\d{8}$/;
  return phoneRegex.test(phone.replace(/\s/g, ''));
};

/**
 * Validate password
 * @param {string} password - Password to validate
 * @param {Object} options - Validation options
 * @returns {Object} Validation result
 */
export const validatePassword = (password, options = {}) => {
  const {
    minLength = 8,
    requireUppercase = true,
    requireLowercase = true,
    requireNumbers = true,
    requireSpecialChars = false,
  } = options;
  
  const errors = [];
  
  if (!password || password.length < minLength) {
    errors.push(`كلمة المرور يجب أن تكون ${minLength} أحرف على الأقل`);
  }
  
  if (requireUppercase && !/[A-Z]/.test(password)) {
    errors.push('يجب أن تحتوي على حرف كبير');
  }
  
  if (requireLowercase && !/[a-z]/.test(password)) {
    errors.push('يجب أن تحتوي على حرف صغير');
  }
  
  if (requireNumbers && !/\d/.test(password)) {
    errors.push('يجب أن تحتوي على رقم');
  }
  
  if (requireSpecialChars && !/[!@#$%^&*(),.?":{}|<>]/.test(password)) {
    errors.push('يجب أن تحتوي على رمز خاص');
  }
  
  return {
    isValid: errors.length === 0,
    errors,
  };
};

/**
 * Validate required field
 * @param {*} value - Value to validate
 * @returns {boolean} Is valid
 */
export const isRequired = (value) => {
  if (value === null || value === undefined) return false;
  if (typeof value === 'string') return value.trim().length > 0;
  if (Array.isArray(value)) return value.length > 0;
  return true;
};

/**
 * Validate minimum length
 * @param {string} value - Value to validate
 * @param {number} min - Minimum length
 * @returns {boolean} Is valid
 */
export const minLength = (value, min) => {
  if (!value) return false;
  return value.length >= min;
};

/**
 * Validate maximum length
 * @param {string} value - Value to validate
 * @param {number} max - Maximum length
 * @returns {boolean} Is valid
 */
export const maxLength = (value, max) => {
  if (!value) return true;
  return value.length <= max;
};

/**
 * Validate number range
 * @param {number} value - Value to validate
 * @param {number} min - Minimum value
 * @param {number} max - Maximum value
 * @returns {boolean} Is valid
 */
export const isInRange = (value, min, max) => {
  if (value === null || value === undefined) return false;
  const num = Number(value);
  return !isNaN(num) && num >= min && num <= max;
};

/**
 * Validate positive number
 * @param {number} value - Value to validate
 * @returns {boolean} Is valid
 */
export const isPositiveNumber = (value) => {
  if (value === null || value === undefined) return false;
  const num = Number(value);
  return !isNaN(num) && num > 0;
};

/**
 * Validate URL
 * @param {string} url - URL to validate
 * @returns {boolean} Is valid
 */
export const isValidUrl = (url) => {
  if (!url) return false;
  
  try {
    new URL(url);
    return true;
  } catch {
    return false;
  }
};

/**
 * Validate Saudi ID number (Iqama/ID)
 * @param {string} id - ID number to validate
 * @returns {boolean} Is valid
 */
export const isValidSaudiId = (id) => {
  if (!id) return false;
  
  const idRegex = /^\d{10}$/;
  return idRegex.test(id);
};

/**
 * Validate VAT number (Saudi)
 * @param {string} vat - VAT number to validate
 * @returns {boolean} Is valid
 */
export const isValidVatNumber = (vat) => {
  if (!vat) return false;
  
  const vatRegex = /^3\d{13}3$/;
  return vatRegex.test(vat);
};

/**
 * Validate commercial registration number (Saudi)
 * @param {string} cr - CR number to validate
 * @returns {boolean} Is valid
 */
export const isValidCommercialRegistration = (cr) => {
  if (!cr) return false;
  
  const crRegex = /^\d{10}$/;
  return crRegex.test(cr);
};

/**
 * Validate tracking number format
 * @param {string} trackingNumber - Tracking number to validate
 * @returns {boolean} Is valid
 */
export const isValidTrackingNumber = (trackingNumber) => {
  if (!trackingNumber) return false;
  
  // Basic tracking number validation (alphanumeric, 8-20 chars)
  const trackingRegex = /^[A-Z0-9]{8,20}$/i;
  return trackingRegex.test(trackingNumber);
};

/**
 * Validate file size
 * @param {File} file - File to validate
 * @param {number} maxSizeMB - Maximum size in MB
 * @returns {boolean} Is valid
 */
export const isValidFileSize = (file, maxSizeMB = 10) => {
  if (!file) return false;
  
  const maxSizeBytes = maxSizeMB * 1024 * 1024;
  return file.size <= maxSizeBytes;
};

/**
 * Validate file type
 * @param {File} file - File to validate
 * @param {Array<string>} allowedTypes - Allowed MIME types
 * @returns {boolean} Is valid
 */
export const isValidFileType = (file, allowedTypes = []) => {
  if (!file) return false;
  
  if (allowedTypes.length === 0) return true;
  
  return allowedTypes.includes(file.type);
};

/**
 * Validate image file
 * @param {File} file - File to validate
 * @returns {boolean} Is valid
 */
export const isValidImage = (file) => {
  if (!file) return false;
  
  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
  return isValidFileType(file, allowedTypes);
};

/**
 * Validate date range
 * @param {string|Date} startDate - Start date
 * @param {string|Date} endDate - End date
 * @returns {boolean} Is valid
 */
export const isValidDateRange = (startDate, endDate) => {
  const start = new Date(startDate);
  const end = new Date(endDate);
  
  return start <= end;
};

/**
 * Validate form data
 * @param {Object} data - Form data
 * @param {Object} rules - Validation rules
 * @returns {Object} Validation result
 */
export const validateForm = (data, rules) => {
  const errors = {};
  let isValid = true;
  
  for (const [field, validators] of Object.entries(rules)) {
    const value = data[field];
    
    for (const validator of validators) {
      const result = validator(value);
      
      if (result !== true) {
        errors[field] = typeof result === 'string' ? result : 'حقل غير صالح';
        isValid = false;
        break;
      }
    }
  }
  
  return { isValid, errors };
};

/**
 * Create required validator
 * @param {string} message - Error message
 * @returns {Function} Validator function
 */
export const required = (message = 'هذا الحقل مطلوب') => {
  return (value) => isRequired(value) || message;
};

/**
 * Create email validator
 * @param {string} message - Error message
 * @returns {Function} Validator function
 */
export const email = (message = 'البريد الإلكتروني غير صالح') => {
  return (value) => isValidEmail(value) || message;
};

/**
 * Create phone validator
 * @param {string} message - Error message
 * @returns {Function} Validator function
 */
export const phone = (message = 'رقم الجوال غير صالح') => {
  return (value) => isValidPhone(value) || message;
};

/**
 * Create min length validator
 * @param {number} length - Minimum length
 * @param {string} message - Error message
 * @returns {Function} Validator function
 */
export const minLengthValidator = (length, message) => {
  return (value) => {
    if (!message) {
      message = `يجب أن يكون ${length} أحرف على الأقل`;
    }
    return minLength(value, length) || message;
  };
};

/**
 * Create max length validator
 * @param {number} length - Maximum length
 * @param {string} message - Error message
 * @returns {Function} Validator function
 */
export const maxLengthValidator = (length, message) => {
  return (value) => {
    if (!message) {
      message = `يجب أن لا يتجاوز ${length} حرف`;
    }
    return maxLength(value, length) || message;
  };
};
