/**
 * ============================================
 * 🚦 Rate Limiter Middleware - نظام إدهام
 * Edham Logistics - Rate Limiting
 * ============================================
 */

const rateLimit = require('express-rate-limit');
const { MESSAGES } = require('../config/constants');

// General rate limiter for all API routes
const generalLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100, // Limit each IP to 100 requests per windowMs
  message: {
    success: false,
    message: MESSAGES.TOO_MANY_REQUESTS
  },
  standardHeaders: true,
  legacyHeaders: false,
  skip: () => process.env.NODE_ENV === 'test',
});

// Stricter rate limiter for authentication routes
const authLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 5, // Limit each IP to 5 requests per windowMs
  message: {
    success: false,
    message: 'محاولات تسجيل دخول كثيرة جداً، يرجى المحاولة بعد 15 دقيقة'
  },
  standardHeaders: true,
  legacyHeaders: false,
  skip: () => process.env.NODE_ENV === 'test',
});

// Rate limiter for password reset
const passwordResetLimiter = rateLimit({
  windowMs: 60 * 60 * 1000, // 1 hour
  max: 3, // Limit each IP to 3 requests per hour
  message: {
    success: false,
    message: 'محاولات إعادة تعيين كلمة المرور كثيرة جداً، يرجى المحاولة بعد ساعة'
  },
  standardHeaders: true,
  legacyHeaders: false,
  skip: () => process.env.NODE_ENV === 'test',
});

module.exports = {
  generalLimiter,
  authLimiter,
  passwordResetLimiter
};
