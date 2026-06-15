/**
 * ============================================
 * 🌐 API Gateway - نظام إدهام الاحترافي
 * Edham Logistics - Enterprise API Gateway
 * ============================================
 */

const rateLimit = require('express-rate-limit');
const helmet = require('helmet');
const cors = require('cors');
const compression = require('compression');
const { v4: uuidv4 } = require('uuid');
const logger = require('../utils/logger');
const { MESSAGES } = require('../config/constants');

class APIGateway {
  constructor() {
    this.requestId = null;
    this.startTime = null;
    this.user = null;
  }

  /**
   * Configure API gateway middleware
   */
  static configure() {
    const middlewares = [];

    // Security headers
    middlewares.push(helmet({
      contentSecurityPolicy: {
        directives: {
          defaultSrc: ["'self'"],
          styleSrc: ["'self'", "'unsafe-inline'"],
          scriptSrc: ["'self'"],
          imgSrc: ["'self'", "data:", "https:"],
        },
      },
      hsts: {
        maxAge: 31536000,
        includeSubDomains: true,
        preload: true
      }
    }));

    // CORS configuration
    middlewares.push(cors({
      origin: (origin, callback) => {
        const allowedOrigins = process.env.ALLOWED_ORIGINS?.split(',') || [
          'http://localhost:3000',
          'https://edham.logistics',
          'https://app.edham.logistics'
        ];
        
        if (!origin || allowedOrigins.includes(origin)) {
          callback(null, true);
        } else {
          callback(new Error('Not allowed by CORS'));
        }
      },
      credentials: true,
      methods: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS'],
      allowedHeaders: [
        'Origin', 'X-Requested-With', 'Content-Type', 'Accept',
        'Authorization', 'X-API-Key', 'X-Client-Version',
        'X-Request-ID', 'X-Device-ID', 'X-Platform'
      ],
      exposedHeaders: [
        'X-Request-ID', 'X-Rate-Limit-Limit', 'X-Rate-Limit-Remaining',
        'X-Rate-Limit-Reset', 'X-Response-Time', 'X-API-Version'
      ]
    }));

    // Compression
    middlewares.push(compression({
      filter: (req, res) => {
        if (req.headers['x-no-compression']) {
          return false;
        }
        return compression.filter(req, res);
      },
      threshold: 1024,
      level: 6
    }));

    // Request ID and logging
    middlewares.push(this.requestLogger.bind(this));

    // Rate limiting
    middlewares.push(this.createRateLimiter());

    // API versioning
    middlewares.push(this.apiVersioning.bind(this));

    // Request validation
    middlewares.push(this.requestValidation.bind(this));

    // Authentication middleware
    middlewares.push(this.authentication.bind(this));

    // Authorization middleware
    middlewares.push(this.authorization.bind(this));

    // Response enhancement
    middlewares.push(this.responseEnhancer.bind(this));

    return middlewares;
  }

  /**
   * Request logger middleware
   */
  static requestLogger(req, res, next) {
    req.requestId = req.headers['x-request-id'] || uuidv4();
    req.startTime = Date.now();

    const logData = {
      requestId: req.requestId,
      method: req.method,
      url: req.url,
      ip: req.ip,
      userAgent: req.get('User-Agent'),
      timestamp: new Date().toISOString(),
      headers: {
        'x-client-version': req.get('X-Client-Version'),
        'x-device-id': req.get('X-Device-ID'),
        'x-platform': req.get('X-Platform')
      }
    };

    logger.info('API Request', logData);

    // Log response
    const originalSend = res.send;
    res.send = function(data) {
      const responseTime = Date.now() - req.startTime;
      
      logger.info('API Response', {
        requestId: req.requestId,
        statusCode: res.statusCode,
        responseTime: `${responseTime}ms`,
        contentLength: res.get('Content-Length') || 0
      });

      res.set('X-Response-Time', responseTime);
      originalSend.call(this, data);
    };

    next();
  }

  /**
   * Create rate limiter with different strategies
   */
  static createRateLimiter() {
    const createLimiter = (windowMs, max, message) => rateLimit({
      windowMs,
      max,
      message: {
        success: false,
        error: message,
        retryAfter: Math.ceil(windowMs / 1000)
      },
      standardHeaders: true,
      legacyHeaders: false,
      keyGenerator: (req) => {
        // Different limits for different user types
        if (req.user?.role === 'admin') return `admin_${req.ip}`;
        if (req.user?.role === 'client') return `client_${req.user.id}`;
        return `anonymous_${req.ip}`;
      }
    });

    return {
      // Global rate limit
      global: createLimiter(15 * 60 * 1000, 1000, 'Too many requests from this IP'),
      
      // Auth endpoints
      auth: createLimiter(15 * 60 * 1000, 10, 'Too many authentication attempts'),
      
      // API endpoints
      api: createLimiter(1 * 60 * 1000, 100, 'API rate limit exceeded'),
      
      // Upload endpoints
      upload: createLimiter(60 * 60 * 1000, 50, 'Upload rate limit exceeded'),
      
      // Report endpoints
      report: createLimiter(5 * 60 * 1000, 20, 'Report generation rate limit exceeded')
    };
  }

  /**
   * API versioning middleware
   */
  static apiVersioning(req, res, next) {
    const version = req.headers['x-api-version'] || req.query.version || 'v1';
    const supportedVersions = ['v1', 'v2'];

    if (!supportedVersions.includes(version)) {
      return res.status(400).json({
        success: false,
        error: 'Unsupported API version',
        supportedVersions
      });
    }

    req.apiVersion = version;
    req.apiVersionNumber = parseInt(version.replace('v', ''));
    res.set('X-API-Version', version);

    next();
  }

  /**
   * Request validation middleware
   */
  static requestValidation(req, res, next) {
    // Validate content type
    const contentType = req.get('Content-Type');
    const validContentTypes = [
      'application/json',
      'multipart/form-data',
      'application/x-www-form-urlencoded'
    ];

    if (req.method !== 'GET' && req.method !== 'DELETE' && !validContentTypes.includes(contentType?.split(';')[0])) {
      return res.status(415).json({
        success: false,
        error: 'Unsupported Media Type',
        supportedTypes: validContentTypes
      });
    }

    // Validate request size
    const contentLength = parseInt(req.get('Content-Length') || '0');
    const maxRequestSize = 10 * 1024 * 1024; // 10MB

    if (contentLength > maxRequestSize) {
      return res.status(413).json({
        success: false,
        error: 'Request Entity Too Large',
        maxSize: `${maxRequestSize} bytes`
      });
    }

    // JSON parsing with error handling
    if (contentType?.includes('application/json')) {
      try {
        req.body = JSON.parse(req.body);
      } catch (error) {
        return res.status(400).json({
          success: false,
          error: 'Invalid JSON',
          details: error.message
        });
      }
    }

    next();
  }

  /**
   * Authentication middleware
   */
  static authentication(req, res, next) {
    const authHeader = req.get('Authorization');
    const apiKey = req.get('X-API-Key');

    // Skip authentication for public endpoints
    const publicEndpoints = [
      '/api/v1/auth/login',
      '/api/v1/auth/register',
      '/api/v1/auth/forgot-password',
      '/api/v1/public',
      '/api/docs',
      '/api/health'
    ];

    const isPublicEndpoint = publicEndpoints.some(endpoint => req.path.startsWith(endpoint));

    if (isPublicEndpoint) {
      return next();
    }

    // JWT Token authentication
    if (authHeader && authHeader.startsWith('Bearer ')) {
      const token = authHeader.substring(7);
      
      try {
        const jwt = require('jsonwebtoken');
        const decoded = jwt.verify(token, process.env.JWT_SECRET);
        req.user = decoded;
        req.authType = 'jwt';
        return next();
      } catch (error) {
        logger.warn('Invalid JWT token', { error: error.message, requestId: req.requestId });
      }
    }

    // API Key authentication
    if (apiKey) {
      try {
        // Validate API key against database
        const APIKey = require('../models/APIKey');
        const validKey = await APIKey.findOne({ 
          key: apiKey, 
          isActive: true,
          expiresAt: { $gt: new Date() }
        }).populate('userId');

        if (validKey) {
          req.user = validKey.userId;
          req.apiKey = validKey;
          req.authType = 'api_key';
          return next();
        }
      } catch (error) {
        logger.error('API key validation error', { error: error.message, requestId: req.requestId });
      }
    }

    // No valid authentication
    res.set('WWW-Authenticate', 'Bearer realm="Edham Logistics API"');
    return res.status(401).json({
      success: false,
      error: 'Authentication required',
      requestId: req.requestId
    });
  }

  /**
   * Authorization middleware
   */
  static authorization(req, res, next) {
    // Skip authorization for user profile endpoints
    const skipAuth = [
      '/api/v1/auth/profile',
      '/api/v1/auth/change-password'
    ];

    if (skipAuth.some(endpoint => req.path.includes(endpoint))) {
      return next();
    }

    // Role-based access control
    const rolePermissions = {
      admin: ['*'],
      supervisor: [
        'shipments:*', 'drivers:*', 'trucks:*', 'reports:*',
        'tracking:*', 'maintenance:*', 'analytics:read'
      ],
      accountant: [
        'payments:*', 'vouchers:*', 'transactions:*', 'reports:read',
        'analytics:read'
      ],
      client: [
        'shipments:create', 'shipments:own', 'payments:own',
        'profile:own'
      ],
      driver: [
        'location:update', 'shipments:assigned', 'profile:own',
        'notifications:own'
      ],
      workshop: [
        'maintenance:*', 'vehicles:*', 'parts:*', 'inventory:*'
      ]
    };

    const userRole = req.user?.role;
    if (!userRole || !rolePermissions[userRole]) {
      return res.status(403).json({
        success: false,
        error: 'Insufficient permissions',
        requestId: req.requestId
      });
    }

    // Check specific endpoint permissions
    const requiredPermission = this.getRequiredPermission(req.method, req.path);
    if (requiredPermission && !this.hasPermission(rolePermissions[userRole], requiredPermission)) {
      return res.status(403).json({
        success: false,
        error: 'Access denied',
        requiredPermission,
        requestId: req.requestId
      });
    }

    req.permissions = rolePermissions[userRole];
    next();
  }

  /**
   * Get required permission for endpoint
   */
  static getRequiredPermission(method, path) {
    // Extract resource and action from path and method
    const pathParts = path.split('/').filter(part => part && !part.startsWith(':'));
    const resource = pathParts[2]; // /api/v1/{resource}
    
    const actionMap = {
      'GET': 'read',
      'POST': 'create',
      'PUT': 'update',
      'PATCH': 'update',
      'DELETE': 'delete'
    };

    const action = actionMap[method];
    return action && resource ? `${resource}:${action}` : null;
  }

  /**
   * Check if user has permission
   */
  static hasPermission(permissions, requiredPermission) {
    if (permissions.includes('*')) return true;
    
    // Check for exact match
    if (permissions.includes(requiredPermission)) return true;
    
    // Check for wildcard permissions
    const [resource] = requiredPermission.split(':');
    return permissions.some(permission => {
      const [permResource, permAction] = permission.split(':');
      return permResource === resource && permAction === '*';
    });
  }

  /**
   * Response enhancement middleware
   */
  static responseEnhancer(req, res, next) {
    // Standardize response format
    const originalJson = res.json;
    res.json = function(data) {
      const standardizedResponse = {
        success: true,
        data: data,
        meta: {
          requestId: req.requestId,
          timestamp: new Date().toISOString(),
          version: req.apiVersion || 'v1',
          pagination: data.pagination || null
        }
      };

      // Remove pagination from data if it exists in meta
      if (data.pagination) {
        delete data.pagination;
      }

      originalJson.call(this, standardizedResponse);
    };

    // Error response handler
    const originalStatus = res.status;
    res.status = function(code) {
      return function(data) {
        const errorResponse = {
          success: false,
          error: data,
          meta: {
            requestId: req.requestId,
            timestamp: new Date().toISOString(),
            version: req.apiVersion || 'v1'
          }
        };

        originalStatus.call(this, code).json(errorResponse);
      };
    };

    next();
  }

  /**
   * API health check middleware
   */
  static healthCheck(req, res) {
    const health = {
      status: 'healthy',
      timestamp: new Date().toISOString(),
      version: process.env.API_VERSION || '2.0.0',
      environment: process.env.NODE_ENV || 'development',
      uptime: process.uptime(),
      services: {
        database: 'connected',
        redis: 'connected',
        queue: 'connected',
        storage: 'connected'
      },
      metrics: {
        requests: {
          total: 0,
          successful: 0,
          failed: 0
        },
        responseTime: {
          average: 0,
          p95: 0,
          p99: 0
        }
      }
    };

    res.json(health);
  }

  /**
   * Generate API documentation
   */
  static generateDocumentation() {
    return {
      openapi: '3.0.0',
      info: {
        title: 'Edham Logistics API',
        description: 'Professional logistics management system API',
        version: '2.0.0',
        contact: {
          name: 'API Support',
          email: 'api@edham.logistics',
          url: 'https://edham.logistics/support'
        },
        license: {
          name: 'MIT',
          url: 'https://opensource.org/licenses/MIT'
        }
      },
      servers: [
        {
          url: 'https://api.edham.logistics/v1',
          description: 'Production server'
        },
        {
          url: 'https://staging-api.edham.logistics/v1',
          description: 'Staging server'
        },
        {
          url: 'http://localhost:3001/api/v1',
          description: 'Development server'
        }
      ],
      security: [
        {
          bearerAuth: {
            type: 'http',
            scheme: 'bearer',
            bearerFormat: 'JWT'
          }
        },
        {
          apiKeyAuth: {
            type: 'apiKey',
            in: 'header',
            name: 'X-API-Key'
          }
        }
      ],
      paths: {
        // This would be populated with actual endpoint documentation
      },
      components: {
        schemas: {
          User: {
            type: 'object',
            properties: {
              id: { type: 'string', format: 'uuid' },
              name: { type: 'string', minLength: 2, maxLength: 100 },
              email: { type: 'string', format: 'email' },
              role: { 
                type: 'string', 
                enum: ['admin', 'supervisor', 'accountant', 'client', 'driver', 'workshop'] 
              },
              isActive: { type: 'boolean' }
            }
          },
          Shipment: {
            type: 'object',
            properties: {
              id: { type: 'string', format: 'uuid' },
              trackingNumber: { type: 'string', pattern: '^[A-Z0-9]{10}$' },
              status: { 
                type: 'string', 
                enum: ['pending', 'assigned', 'in_transit', 'delivered', 'cancelled'] 
              },
              origin: {
                type: 'object',
                properties: {
                  address: { type: 'string' },
                  city: { type: 'string' },
                  coordinates: {
                    type: 'object',
                    properties: {
                      latitude: { type: 'number', minimum: -90, maximum: 90 },
                      longitude: { type: 'number', minimum: -180, maximum: 180 }
                    }
                  }
                }
              },
              destination: {
                type: 'object',
                properties: {
                  address: { type: 'string' },
                  city: { type: 'string' },
                  coordinates: {
                    type: 'object',
                    properties: {
                      latitude: { type: 'number', minimum: -90, maximum: 90 },
                      longitude: { type: 'number', minimum: -180, maximum: 180 }
                    }
                  }
                }
              }
            }
          }
        }
      }
    };
  }
}

module.exports = APIGateway;
