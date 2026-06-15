/**
 * ============================================
 * 🛡️ Middleware Index - نظام إدهام
 * Centralized middleware export
 * ============================================
 */

const { protect, authorize, optionalAuth } = require('./auth');
const errorHandler = require('./errorHandler');
const { 
  apiLimiter, 
  authLimiter, 
  createShipmentLimiter, 
  uploadLimiter,
  trackingLimiter 
} = require('./rateLimiter');

module.exports = {
  protect,
  authorize,
  optionalAuth,
  errorHandler,
  apiLimiter,
  authLimiter,
  createShipmentLimiter,
  uploadLimiter,
  trackingLimiter
};
