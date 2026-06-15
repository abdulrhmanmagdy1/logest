/**
 * ============================================
 * 📝 Client Logger - نظام إدهام
 * Edham Logistics - Frontend Logger Utility
 * ============================================
 */

const isDevelopment = process.env.NODE_ENV === 'development';

const logger = {
  info: (message, data) => {
    if (isDevelopment) {
      console.log(`%c📘 [INFO] ${message}`, 'color: #3498db; font-weight: bold;', data || '');
    }
  },

  success: (message, data) => {
    if (isDevelopment) {
      console.log(`%c✅ [SUCCESS] ${message}`, 'color: #27ae60; font-weight: bold;', data || '');
    }
  },

  warning: (message, data) => {
    if (isDevelopment) {
      console.warn(`%c⚠️ [WARNING] ${message}`, 'color: #f39c12; font-weight: bold;', data || '');
    }
  },

  error: (message, error) => {
    // Always log errors, even in production
    console.error(`%c❌ [ERROR] ${message}`, 'color: #e74c3c; font-weight: bold;', error || '');
    
    // Send to error tracking service in production
    if (!isDevelopment && window.errorTracker) {
      window.errorTracker.capture(message, error);
    }
  },

  debug: (message, data) => {
    if (isDevelopment) {
      console.log(`%c🐛 [DEBUG] ${message}`, 'color: #9b59b6; font-weight: bold;', data || '');
    }
  }
};

export default logger;
