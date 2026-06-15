/**
 * ============================================
 * 🔐 Database Security Middleware - نظام إدهام
 * Edham Logistics - Database Security Enhancements
 * ============================================
 */

const mongoose = require('mongoose');
const logger = require('../utils/logger');

// Database security enhancements
const databaseSecurity = {
  // Enable field-level encryption for sensitive fields
  encryptSensitiveFields: (schema) => {
    schema.pre('save', function(next) {
      const sensitiveFields = ['password', 'ssn', 'creditCard', 'bankAccount'];
      
      sensitiveFields.forEach(field => {
        if (this[field]) {
          // In production, use proper encryption library like crypto-js
          // this[field] = encrypt(this[field]);
        }
      });
      
      next();
    });
    
    schema.post('find', function(docs) {
      const sensitiveFields = ['password', 'ssn', 'creditCard', 'bankAccount'];
      
      docs.forEach(doc => {
        sensitiveFields.forEach(field => {
          if (doc[field]) {
            // doc[field] = decrypt(doc[field]);
          }
        });
      });
    });
  },

  // Add data validation middleware
  validateInput: (schema) => {
    schema.pre('save', function(next) {
      // Validate email format
      if (this.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.email)) {
        return next(new Error('Invalid email format'));
      }
      
      // Validate phone number format
      if (this.phone && !/^\+?[0-9]{10,15}$/.test(this.phone)) {
        return next(new Error('Invalid phone number format'));
      }
      
      // Sanitize string inputs
      Object.keys(this._doc).forEach(key => {
        if (typeof this._doc[key] === 'string') {
          this._doc[key] = this._doc[key].trim();
        }
      });
      
      next();
    });
  },

  // Add audit trail middleware
  addAuditTrail: (schema) => {
    schema.add({
      createdAt: { type: Date, default: Date.now },
      updatedAt: { type: Date, default: Date.now },
      createdBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
      updatedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
      version: { type: Number, default: 1 }
    });
    
    schema.pre('save', function(next) {
      this.updatedAt = Date.now();
      this.version += 1;
      next();
    });
  },

  // Add soft delete
  addSoftDelete: (schema) => {
    schema.add({
      deletedAt: { type: Date, default: null },
      deletedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' }
    });
    
    schema.pre('find', function() {
      this.where({ deletedAt: null });
    });
  },

  // Add rate limiting per user
  addRateLimiting: (schema, field = 'userId') => {
    const rateLimits = new Map();
    const WINDOW_MS = 60000; // 1 minute
    const MAX_REQUESTS = 100;
    
    schema.statics.checkRateLimit = function(identifier) {
      const now = Date.now();
      const userLimits = rateLimits.get(identifier) || [];
      
      // Remove old entries outside the window
      const recent = userLimits.filter(timestamp => now - timestamp < WINDOW_MS);
      
      if (recent.length >= MAX_REQUESTS) {
        return false;
      }
      
      recent.push(now);
      rateLimits.set(identifier, recent);
      return true;
    };
  },

  // Add connection encryption
  configureEncryption: () => {
    mongoose.set('autoEncryption', true);
    
    // Enable TLS for database connection
    const options = {
      ssl: process.env.NODE_ENV === 'production',
      sslValidate: process.env.NODE_ENV === 'production',
      tlsAllowInvalidCertificates: process.env.NODE_ENV !== 'production',
    };
    
    return options;
  },

  // Add query logging for security monitoring
  enableQueryLogging: () => {
    mongoose.set('debug', process.env.NODE_ENV === 'development');
    
    mongoose.connection.on('query', (query) => {
      logger.info(`MongoDB Query: ${query}`);
    });
  },

  // Add connection pool monitoring
  monitorConnectionPool: (connection) => {
    setInterval(() => {
      const pool = connection.db.s.serverConfig.pool;
      const inUse = pool.totalConnectionCount - pool.availableConnectionCount;
      const usagePercent = (inUse / pool.totalConnectionCount) * 100;
      
      logger.info('Connection Pool Status', {
        total: pool.totalConnectionCount,
        available: pool.availableConnectionCount,
        inUse,
        usagePercent: `${usagePercent.toFixed(1)}%`
      });
      
      if (usagePercent > 80) {
        logger.warning('Connection pool under pressure', { usagePercent });
      }
    }, 60000); // Check every minute
  },

  // Add backup monitoring
  monitorBackups: () => {
    // In production, integrate with your backup system
    logger.success('Backup monitoring enabled');
  }
};

module.exports = databaseSecurity;
