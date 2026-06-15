/**
 * ============================================
 * 🔐 Authentication Middleware - نظام إدهام
 * Edham Logistics - Auth & Authorization
 * ============================================
 */

const jwt = require('jsonwebtoken');
const User = require('../models/User');
const logger = require('../utils/logger');
const { HTTP_STATUS, MESSAGES, ROLES } = require('../config/constants');
const { CAPABILITY_ROLES } = require('../config/permissions');

// Role hierarchy (higher number = more permissions)
const ROLE_LEVELS = {
  [ROLES.CLIENT]: 1,
  [ROLES.DRIVER]: 2,
  [ROLES.EMPLOYEE]: 3,
  [ROLES.MAINTENANCE]: 4,
  [ROLES.ACCOUNTANT]: 5,
  [ROLES.SUPERVISOR]: 6,
  [ROLES.ADMIN]: 7,
  [ROLES.SUPER_ADMIN]: 8
};

/**
 * Authenticate user via JWT token
 */
const auth = async (req, res, next) => {
  try {
    const token = req.header('Authorization')?.replace('Bearer ', '');
    
    if (!token) {
      return res.status(HTTP_STATUS.UNAUTHORIZED).json({ 
        success: false,
        message: MESSAGES.UNAUTHORIZED 
      });
    }

    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    const user = await User.findById(decoded.id).select('-password');

    if (!user) {
      return res.status(HTTP_STATUS.UNAUTHORIZED).json({ 
        success: false,
        message: MESSAGES.USER_NOT_FOUND 
      });
    }

    if (!user.isActive) {
      return res.status(HTTP_STATUS.FORBIDDEN).json({ 
        success: false,
        message: MESSAGES.ACCOUNT_DEACTIVATED 
      });
    }

    if (user.isDeleted) {
      return res.status(HTTP_STATUS.UNAUTHORIZED).json({ 
        success: false,
        message: MESSAGES.ACCOUNT_DELETED 
      });
    }

    req.user = user;
    req.user.roleLevel = ROLE_LEVELS[user.role] || 0;
    next();
  } catch (error) {
    logger.error('Auth middleware error', error);
    res.status(HTTP_STATUS.UNAUTHORIZED).json({ 
      success: false,
      message: MESSAGES.INVALID_TOKEN 
    });
  }
};

/**
 * Check if user has specific role(s)
 */
const authorize = (...roles) => {
  return (req, res, next) => {
    if (!roles.includes(req.user.role)) {
      logger.warning('Unauthorized access attempt', { 
        userId: req.user._id, 
        role: req.user.role,
        requiredRoles: roles 
      });
      return res.status(HTTP_STATUS.FORBIDDEN).json({ 
        success: false,
        message: MESSAGES.UNAUTHORIZED 
      });
    }
    next();
  };
};

/**
 * Check if user has minimum role level
 */
const authorizeLevel = (minLevel) => {
  return (req, res, next) => {
    if (req.user.roleLevel < minLevel) {
      return res.status(HTTP_STATUS.FORBIDDEN).json({ 
        success: false,
        message: MESSAGES.UNAUTHORIZED 
      });
    }
    next();
  };
};

/**
 * Capability-based authorization for domain modules.
 */
const authorizeCapability = (capability) => {
  return (req, res, next) => {
    const allowedRoles = CAPABILITY_ROLES[capability] || [];
    if (!allowedRoles.includes(req.user.role)) {
      return res.status(HTTP_STATUS.FORBIDDEN).json({
        success: false,
        message: MESSAGES.UNAUTHORIZED
      });
    }
    next();
  };
};

/**
 * Check if user can manage another user (higher role level)
 */
const canManageUser = (req, res, next) => {
  const targetUserId = req.params.userId || req.body.userId;
  
  // If trying to manage self, allow
  if (targetUserId === req.user._id.toString()) {
    return next();
  }

  // Super admin can manage everyone
  if (req.user.role === ROLES.SUPER_ADMIN) {
    return next();
  }

  // Admin can manage everyone except super admin
  if (req.user.role === ROLES.ADMIN) {
    return next();
  }

  // Supervisor can manage drivers, clients, employees, and maintenance
  if (req.user.role === ROLES.SUPERVISOR) {
    return next();
  }

  return res.status(HTTP_STATUS.FORBIDDEN).json({ 
    success: false,
    message: MESSAGES.UNAUTHORIZED 
  });
};

/**
 * Check if user can access financial data
 */
const canAccessFinancial = (req, res, next) => {
  const allowedRoles = [ROLES.ACCOUNTANT, ROLES.SUPERVISOR, ROLES.ADMIN, ROLES.SUPER_ADMIN];
  if (!allowedRoles.includes(req.user.role)) {
    return res.status(HTTP_STATUS.FORBIDDEN).json({ 
      success: false,
      message: MESSAGES.UNAUTHORIZED 
    });
  }
  next();
};

/**
 * Check if user can manage tasks/shipments
 */
const canManageTasks = (req, res, next) => {
  const allowedRoles = [ROLES.SUPERVISOR, ROLES.ADMIN, ROLES.SUPER_ADMIN];
  if (!allowedRoles.includes(req.user.role)) {
    return res.status(HTTP_STATUS.FORBIDDEN).json({ 
      success: false,
      message: MESSAGES.UNAUTHORIZED 
    });
  }
  next();
};

/**
 * Check if user can manage maintenance
 */
const canManageMaintenance = (req, res, next) => {
  const allowedRoles = [ROLES.MAINTENANCE, ROLES.SUPERVISOR, ROLES.ADMIN, ROLES.SUPER_ADMIN];
  if (!allowedRoles.includes(req.user.role)) {
    return res.status(HTTP_STATUS.FORBIDDEN).json({ 
      success: false,
      message: MESSAGES.UNAUTHORIZED 
    });
  }
  next();
};

/**
 * Check if user is a driver
 */
const isDriver = (req, res, next) => {
  if (req.user.role !== ROLES.DRIVER) {
    return res.status(HTTP_STATUS.FORBIDDEN).json({ 
      success: false,
      message: MESSAGES.DRIVERS_ONLY 
    });
  }
  next();
};

/**
 * Request Validation Middleware using Joi
 */
const validateRequest = (schema) => {
  return (req, res, next) => {
    const { error, value } = schema.validate(req.body, {
      abortEarly: false,
      stripUnknown: true
    });
    
    if (error) {
      const errors = error.details.map(detail => ({
        field: detail.path[0],
        message: detail.message
      }));
      
      logger.warning('Validation error', { errors, path: req.path });
      
      return res.status(HTTP_STATUS.BAD_REQUEST).json({ 
        success: false,
        message: MESSAGES.VALIDATION_ERROR,
        errors
      });
    }
    
    req.body = value;
    next();
  };
};

/**
 * Rate Limiting Middleware (In-Memory)
 * For production, use Redis-based rate limiting
 */
const requestStore = new Map();

const rateLimit = (maxRequests = 100, windowMs = 15 * 60 * 1000) => {
  return (req, res, next) => {
    const key = req.ip || req.connection.remoteAddress;
    const now = Date.now();
    
    if (!requestStore.has(key)) {
      requestStore.set(key, []);
    }
    
    const userRequests = requestStore.get(key).filter(time => now - time < windowMs);
    
    if (userRequests.length >= maxRequests) {
      logger.warning('Rate limit exceeded', { ip: key, path: req.path });
      return res.status(429).json({ 
        success: false,
        message: MESSAGES.TOO_MANY_REQUESTS,
        retryAfter: Math.ceil(windowMs / 1000)
      });
    }
    
    userRequests.push(now);
    requestStore.set(key, userRequests);
    
    // Add rate limit headers
    res.setHeader('X-RateLimit-Limit', maxRequests);
    res.setHeader('X-RateLimit-Remaining', Math.max(0, maxRequests - userRequests.length));
    
    next();
  };
};

/**
 * Global Error Handler Middleware
 */
const errorHandler = (err, req, res, next) => {
  logger.error('Error occurred', err);
  
  const statusCode = err.statusCode || err.status || HTTP_STATUS.INTERNAL_ERROR;
  const message = err.message || MESSAGES.ERROR;
  
  // Mongoose validation error
  if (err.name === 'ValidationError') {
    const errors = Object.values(err.errors).map(e => ({
      field: e.path,
      message: e.message
    }));
    
    return res.status(HTTP_STATUS.BAD_REQUEST).json({
      success: false,
      message: MESSAGES.VALIDATION_ERROR,
      errors
    });
  }
  
  // Mongoose duplicate key error
  if (err.code === 11000) {
    const field = Object.keys(err.keyValue)[0];
    return res.status(HTTP_STATUS.CONFLICT).json({
      success: false,
      message: `${field} ${MESSAGES.ALREADY_EXISTS}`,
      code: 'DUPLICATE_KEY_ERROR'
    });
  }
  
  // JWT errors
  if (err.name === 'JsonWebTokenError') {
    return res.status(HTTP_STATUS.UNAUTHORIZED).json({
      success: false,
      message: MESSAGES.INVALID_TOKEN,
      code: 'INVALID_TOKEN'
    });
  }
  
  if (err.name === 'TokenExpiredError') {
    return res.status(HTTP_STATUS.UNAUTHORIZED).json({
      success: false,
      message: MESSAGES.TOKEN_EXPIRED,
      code: 'TOKEN_EXPIRED'
    });
  }
  
  res.status(statusCode).json({
    success: false,
    message,
    code: err.code || 'INTERNAL_ERROR',
    ...(process.env.NODE_ENV === 'development' && { 
      stack: err.stack,
      details: err 
    })
  });
};

module.exports = { 
  auth, 
  authenticate: auth,
  authorize, 
  authorizeCapability,
  authorizeLevel, 
  canManageUser, 
  canAccessFinancial, 
  canManageTasks,
  canManageMaintenance,
  isDriver,
  validateRequest,
  rateLimit,
  errorHandler,
  ROLE_LEVELS 
};
