//
/**
 * ============================================
 * 🔒 Security Middleware - نظام الأمان المتقدم
 * ============================================
 */

const rateLimit = require('express-rate-limit');
const helmet = require('helmet');
const cors = require('cors');
const mongoSanitize = require('express-mongo-sanitize');
const xss = require('xss-clean');
const hpp = require('hpp');
const csrf = require('csurf');
const logger = require('../utils/logger');

// Rate limiting configurations
const authLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 5, // 5 attempts
  message: 'Too many authentication attempts, please try again after 15 minutes',
  standardHeaders: true,
  legacyHeaders: false,
  handler: (req, res) => {
    logger.warn(`Rate limit exceeded for IP: ${req.ip} on auth endpoint`);
    res.status(429).json({
      success: false,
      message: 'Too many attempts, please try again later'
    });
  }
});

const apiLimiter = rateLimit({
  windowMs: 60 * 1000, // 1 minute
  max: 100, // 100 requests per minute
  message: 'Too many requests from this IP',
  standardHeaders: true,
  legacyHeaders: false
});

const strictLimiter = rateLimit({
  windowMs: 60 * 1000, // 1 minute
  max: 10, // 10 requests per minute for sensitive operations
  message: 'Too many requests, please slow down'
});

// CORS configuration
const corsOptions = {
  origin: (origin, callback) => {
    const allowedOrigins = [
      process.env.CLIENT_URL,
      'http://localhost:3000',
      'http://localhost:5000',
      'capacitor://localhost',
      'ionic://localhost'
    ];
    
    // Allow requests with no origin (mobile apps, curl, etc.)
    if (!origin || allowedOrigins.includes(origin)) {
      callback(null, true);
    } else {
      logger.warn(`CORS blocked request from: ${origin}`);
      callback(new Error('Not allowed by CORS'));
    }
  },
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS'],
  allowedHeaders: [
    'Content-Type',
    'Authorization',
    'X-Requested-With',
    'Accept',
    'Origin',
    'X-CSRF-Token'
  ],
  credentials: true,
  maxAge: 86400 // 24 hours
};

// Security headers with Helmet
const helmetConfig = {
  contentSecurityPolicy: {
    directives: {
      defaultSrc: ["'self'"],
      connectSrc: ["'self'", process.env.CLIENT_URL, 'https://*.googleapis.com'],
      imgSrc: ["'self'", 'data:', 'https:', 'blob:'],
      scriptSrc: ["'self'", "'unsafe-inline'"],
      styleSrc: ["'self'", "'unsafe-inline'", 'https://fonts.googleapis.com'],
      fontSrc: ["'self'", 'https://fonts.gstatic.com'],
      objectSrc: ["'none'"],
      upgradeInsecureRequests: []
    }
  },
  crossOriginEmbedderPolicy: false, // Allow embedding for mobile apps
  hsts: {
    maxAge: 31536000,
    includeSubDomains: true,
    preload: true
  }
};

// Input sanitization
const sanitizeInputs = (req, res, next) => {
  // Remove special characters that could be used for injection
  if (req.body) {
    Object.keys(req.body).forEach(key => {
      if (typeof req.body[key] === 'string') {
        // Remove potential NoSQL injection operators
        req.body[key] = req.body[key].replace(/\$[a-zA-Z]+/g, '');
      }
    });
  }
  next();
};

// Request validation
const validateRequest = (schema) => {
  return (req, res, next) => {
    const { error } = schema.validate(req.body);
    if (error) {
      return res.status(400).json({
        success: false,
        message: 'Validation error',
        errors: error.details.map(d => d.message)
      });
    }
    next();
  };
};

// Suspicious activity detection
const detectSuspiciousActivity = (req, res, next) => {
  const suspiciousPatterns = [
    /<(script|img|iframe|object|embed)/i,
    /(javascript|data):/i,
    /union\s+select/i,
    /exec\s*\(/i,
    /system\s*\(/i
  ];
  
  const checkValue = (value) => {
    if (typeof value !== 'string') return false;
    return suspiciousPatterns.some(pattern => pattern.test(value));
  };
  
  const checkObject = (obj) => {
    for (const key in obj) {
      if (typeof obj[key] === 'object' && obj[key] !== null) {
        if (checkObject(obj[key])) return true;
      } else if (checkValue(obj[key])) {
        return true;
      }
    }
    return false;
  };
  
  if (checkObject(req.body) || checkObject(req.query) || checkObject(req.params)) {
    logger.warn(`Suspicious activity detected from IP: ${req.ip}`, {
      url: req.url,
      body: req.body,
      headers: req.headers
    });
    
    return res.status(403).json({
      success: false,
      message: 'Suspicious activity detected'
    });
  }
  
  next();
};

// Audit logging middleware
const auditLog = async (req, res, next) => {
  const originalSend = res.json;
  
  res.json = function(data) {
    // Log the response
    if (req.user && !req.url.includes('/login') && !req.url.includes('/register')) {
      const AuditLog = require('../models/AuditLog');
      
      AuditLog.log({
        action: req.method === 'POST' ? 'CREATE' : req.method === 'PUT' ? 'UPDATE' : req.method === 'DELETE' ? 'DELETE' : 'READ',
        entity: req.url.split('/')[2]?.charAt(0).toUpperCase() + req.url.split('/')[2]?.slice(1) || 'Unknown',
        entityId: req.params.id,
        user: {
          id: req.user.id,
          email: req.user.email,
          role: req.user.role,
          ip: req.ip,
          userAgent: req.headers['user-agent']
        },
        request: {
          method: req.method,
          url: req.originalUrl,
          params: req.params,
          query: req.query,
          body: req.method !== 'GET' ? req.body : undefined
        },
        result: {
          success: data.success !== false,
          statusCode: res.statusCode
        }
      }).catch(err => logger.error('Audit log error:', err));
    }
    
    originalSend.call(this, data);
  };
  
  next();
};

// IP whitelist middleware
const ipWhitelist = (allowedIPs) => {
  return (req, res, next) => {
    const clientIP = req.ip || req.connection.remoteAddress;
    
    if (!allowedIPs.includes(clientIP)) {
      logger.warn(`Unauthorized IP access attempt: ${clientIP}`);
      return res.status(403).json({
        success: false,
        message: 'Access denied'
      });
    }
    
    next();
  };
};

// Two-factor authentication check
const require2FA = async (req, res, next) => {
  if (req.user.twoFactorEnabled && !req.session.twoFactorVerified) {
    return res.status(403).json({
      success: false,
      message: 'Two-factor authentication required',
      requires2FA: true
    });
  }
  next();
};

// Export all security middleware
module.exports = {
  authLimiter,
  apiLimiter,
  strictLimiter,
  corsOptions,
  helmetConfig,
  sanitizeInputs,
  validateRequest,
  detectSuspiciousActivity,
  auditLog,
  ipWhitelist,
  require2FA,
  helmet: helmet(helmetConfig),
  cors: cors(corsOptions),
  mongoSanitize: mongoSanitize(),
  xss: xss(),
  hpp: hpp(),
  csrf: csrf({ cookie: true })
};
