/**
 * ============================================
 * ⏱️ Rate Limiter Middleware - نظام إدهام
 * API rate limiting and protection
 * ============================================
 */

const rateLimit = require('express-rate-limit');
const logger = require('../utils/logger');

// General API rate limiter
const apiLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100, // Limit each IP to 100 requests per windowMs
  message: {
    success: false,
    message: 'Too many requests from this IP, please try again after 15 minutes'
  },
  standardHeaders: true,
  legacyHeaders: false,
  handler: (req, res, next, options) => {
    logger.warn(`Rate limit exceeded for IP: ${req.ip}`);
    res.status(429).json(options.message);
  }
});

// Auth rate limiter (stricter for login/register)
const authLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 5, // 5 attempts per 15 minutes
  skipSuccessfulRequests: true,
  message: {
    success: false,
    message: 'Too many authentication attempts, please try again after 15 minutes'
  },
  handler: (req, res, next, options) => {
    logger.warn(`Auth rate limit exceeded for IP: ${req.ip}`);
    res.status(429).json(options.message);
  }
});

// Create shipment limiter
const createShipmentLimiter = rateLimit({
  windowMs: 60 * 60 * 1000, // 1 hour
  max: 20, // 20 shipments per hour per IP
  message: {
    success: false,
    message: 'You have exceeded the shipment creation limit. Please try again later.'
  }
});

// Upload limiter
const uploadLimiter = rateLimit({
  windowMs: 60 * 60 * 1000, // 1 hour
  max: 50, // 50 uploads per hour
  message: {
    success: false,
    message: 'Upload limit exceeded. Please try again later.'
  }
});

// Tracking endpoint limiter (public tracking can be accessed more frequently)
const trackingLimiter = rateLimit({
  windowMs: 5 * 60 * 1000, // 5 minutes
  max: 30, // 30 tracking requests per 5 minutes
  message: {
    success: false,
    message: 'Too many tracking requests. Please try again later.'
  }
});

module.exports = {
  apiLimiter,
  authLimiter,
  createShipmentLimiter,
  uploadLimiter,
  trackingLimiter
};
