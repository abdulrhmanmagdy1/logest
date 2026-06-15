/**
 * ============================================
 * 🔧 Middleware Index - نظام إدهام
 * Edham Logistics - All Middleware Export
 * ============================================
 */

const auth = require('./auth');
const databaseSecurity = require('./databaseSecurity');
const performance = require('./performance');
const security = require('./security');
const upload = require('./upload');
const validate = require('./validate');

module.exports = {
  // Auth & Authorization
  auth,
  authorize: auth.authorize,
  authorizeLevel: auth.authorizeLevel,
  canManageUser: auth.canManageUser,
  canAccessFinancial: auth.canAccessFinancial,
  canManageTasks: auth.canManageTasks,
  canManageMaintenance: auth.canManageMaintenance,
  isDriver: auth.isDriver,
  validateRequest: auth.validateRequest,
  rateLimit: auth.rateLimit,
  errorHandler: auth.errorHandler,
  ROLE_LEVELS: auth.ROLE_LEVELS,
  
  // Database Security
  databaseSecurity,
  
  // Performance
  performanceTracker: performance.performanceTracker,
  memoryMonitor: performance.memoryMonitor,
  requestSizeLimiter: performance.requestSizeLimiter,
  responseSizeLimiter: performance.responseSizeLimiter,
  
  // Security
  security: {
    limiter: security.limiter,
    authLimiter: security.authLimiter,
    securityHeaders: security.securityHeaders,
    corsOptions: security.corsOptions,
    sanitizeInput: security.sanitizeInput,
    requestLogger: security.requestLogger
  },
  
  // Upload
  upload,
  
  // Validation
  validate: validate.validate,
  schemas: validate.schemas
};
