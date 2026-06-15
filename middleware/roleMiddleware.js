/**
 * ============================================
 * 🛡️ Edham Logistics - Role-Based Access Control Middleware
 * نظام إدهام - برمجيات وسيطة للتحكم في الوصول بناءً على الدور
 * ============================================
 */

const jwt = require('jsonwebtoken');
const User = require('../models/User');

/**
 * Enhanced Authentication Middleware with Role Support
 * تحسين المصادقة مع دعم الأدوار
 */
const authenticateWithRole = async (req, res, next) => {
  try {
    // Get token from header
    const authHeader = req.header('Authorization');
    
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      return res.status(401).json({
        success: false,
        message: 'Access denied. No token provided.',
        error: 'MISSING_TOKEN'
      });
    }

    const token = authHeader.substring(7); // Remove 'Bearer ' prefix

    // Verify token
    const decoded = jwt.verify(token, process.env.JWT_SECRET);
    
    // Get user with role-specific data
    const user = await User.findById(decoded.id)
      .populate('customer_details')
      .populate('driver_details');

    if (!user) {
      return res.status(401).json({
        success: false,
        message: 'Invalid token. User not found.',
        error: 'USER_NOT_FOUND'
      });
    }

    // Check if user is active
    if (!user.isActive) {
      return res.status(401).json({
        success: false,
        message: 'Account is deactivated.',
        error: 'ACCOUNT_DEACTIVATED'
      });
    }

    // Check if user is locked
    if (user.isLocked()) {
      return res.status(401).json({
        success: false,
        message: 'Account is temporarily locked due to failed login attempts.',
        error: 'ACCOUNT_LOCKED'
      });
    }

    // Add user and role information to request
    req.user = user;
    req.userRole = user.role;
    req.userId = user._id;

    next();
  } catch (error) {
    if (error.name === 'JsonWebTokenError') {
      return res.status(401).json({
        success: false,
        message: 'Invalid token.',
        error: 'INVALID_TOKEN'
      });
    }

    if (error.name === 'TokenExpiredError') {
      return res.status(401).json({
        success: false,
        message: 'Token expired.',
        error: 'TOKEN_EXPIRED'
      });
    }

    console.error('Authentication error:', error);
    return res.status(500).json({
      success: false,
      message: 'Internal server error during authentication.',
      error: 'INTERNAL_ERROR'
    });
  }
};

/**
 * Role-Based Access Control Middleware
 * برمجية وسيطة للتحكم في الوصول بناءً على الدور
 */
const requireRole = (...allowedRoles) => {
  return (req, res, next) => {
    // Check if user is authenticated
    if (!req.user || !req.userRole) {
      return res.status(401).json({
        success: false,
        message: 'Authentication required.',
        error: 'AUTHENTICATION_REQUIRED'
      });
    }

    // Check if user's role is allowed
    if (!allowedRoles.includes(req.userRole)) {
      return res.status(403).json({
        success: false,
        message: `Access denied. Required role(s): ${allowedRoles.join(', ')}. Your role: ${req.userRole}`,
        error: 'INSUFFICIENT_PERMISSIONS',
        requiredRoles: allowedRoles,
        userRole: req.userRole
      });
    }

    // Add role-specific context to request
    req.roleContext = {
      isCustomer: req.userRole === 'CUSTOMER',
      isDriver: req.userRole === 'DRIVER',
      isSupervisor: req.userRole === 'SUPERVISOR',
      isAccountant: req.userRole === 'ACCOUNTANT',
      isWorkshop: req.userRole === 'WORKSHOP',
      isAdmin: req.userRole === 'ADMIN',
      hasAdminAccess: ['SUPERVISOR', 'ACCOUNTANT', 'WORKSHOP', 'ADMIN'].includes(req.userRole),
      hasFullAccess: req.userRole === 'ADMIN'
    };

    next();
  };
};

/**
 * Resource Ownership Check Middleware
 * برمجية وسيطة للتحقق من ملكية الموارد
 */
const requireOwnership = (resourceType, resourceIdParam = 'id') => {
  return async (req, res, next) => {
    try {
      const resourceId = req.params[resourceIdParam];
      const userId = req.userId;
      const userRole = req.userRole;

      // Admins can access all resources
      if (userRole === 'ADMIN') {
        return next();
      }

      // Check ownership based on resource type and role
      let hasAccess = false;

      switch (resourceType) {
        case 'order':
          // Customers can only access their own orders
          if (userRole === 'CUSTOMER') {
            const Order = require('../models/Order');
            const order = await Order.findById(resourceId);
            hasAccess = order && order.customer_id.toString() === userId;
          }
          // Drivers can only access orders assigned to them
          else if (userRole === 'DRIVER') {
            const Order = require('../models/Order');
            const order = await Order.findById(resourceId);
            hasAccess = order && order.driver?.driver_id?.toString() === userId;
          }
          // Supervisors can access all orders in their jurisdiction
          else if (userRole === 'SUPERVISOR') {
            hasAccess = true; // Supervisors have access to all orders
          }
          break;

        case 'driver':
          // Users can only access their own driver profile
          if (userRole === 'DRIVER') {
            const Driver = require('../models/Driver');
            const driver = await Driver.findById(resourceId);
            hasAccess = driver && driver.user_id.toString() === userId;
          }
          // Admins and supervisors can access all drivers
          else if (['SUPERVISOR', 'ADMIN'].includes(userRole)) {
            hasAccess = true;
          }
          break;

        case 'maintenance':
          // Workshop managers can access all maintenance records
          if (userRole === 'WORKSHOP') {
            hasAccess = true;
          }
          // Drivers can only access maintenance for their assigned truck
          else if (userRole === 'DRIVER') {
            const Maintenance = require('../models/Maintenance');
            const Driver = require('../models/Driver');
            const maintenance = await Maintenance.findById(resourceId);
            if (maintenance) {
              const driver = await Driver.findById(userId);
              hasAccess = driver && driver.assigned_vehicle?.truck_id?.toString() === maintenance.truck.toString();
            }
          }
          break;

        default:
          hasAccess = false;
      }

      if (!hasAccess) {
        return res.status(403).json({
          success: false,
          message: 'Access denied. You do not have permission to access this resource.',
          error: 'RESOURCE_ACCESS_DENIED',
          resourceType,
          resourceId
        });
      }

      next();
    } catch (error) {
      console.error('Ownership check error:', error);
      return res.status(500).json({
        success: false,
        message: 'Internal server error during ownership check.',
        error: 'INTERNAL_ERROR'
      });
    }
  };
};

/**
 * Department-Based Access Control
 * التحكم في الوصول بناءً على القسم
 */
const requireDepartment = (...allowedDepartments) => {
  return (req, res, next) => {
    if (!req.user || !req.user.department) {
      return res.status(403).json({
        success: false,
        message: 'Department information required.',
        error: 'DEPARTMENT_REQUIRED'
      });
    }

    if (!allowedDepartments.includes(req.user.department)) {
      return res.status(403).json({
        success: false,
        message: `Access denied. Required department(s): ${allowedDepartments.join(', ')}`,
        error: 'INSUFFICIENT_DEPARTMENT_PERMISSIONS'
      });
    }

    next();
  };
};

/**
 * Feature Flag Middleware
 * برمجية وسيطة للتحكم في الميزات
 */
const requireFeature = (featureName) => {
  return (req, res, next) => {
    // Check if feature is enabled for user's role
    const roleFeatures = {
      'CUSTOMER': ['booking', 'tracking', 'payments', 'messaging'],
      'DRIVER': ['location_tracking', 'task_management', 'proof_of_delivery', 'messaging'],
      'SUPERVISOR': ['fleet_management', 'order_dispatch', 'driver_management', 'analytics'],
      'ACCOUNTANT': ['invoicing', 'payments', 'financial_reports', 'tax_management'],
      'WORKSHOP': ['maintenance_scheduling', 'fleet_health', 'parts_inventory', 'service_alerts'],
      'ADMIN': ['all_features']
    };

    const userFeatures = roleFeatures[req.userRole] || [];
    
    if (req.userRole === 'ADMIN' || userFeatures.includes('all_features') || userFeatures.includes(featureName)) {
      return next();
    }

    return res.status(403).json({
      success: false,
      message: `Feature '${featureName}' is not available for your role.`,
      error: 'FEATURE_NOT_AVAILABLE',
      featureName,
      userRole: req.userRole
    });
  };
};

/**
 * Rate Limiting by Role
 * تحديد معدل الطلبات بناءً على الدور
 */
const roleRateLimit = (limits = {}) => {
  const defaultLimits = {
    'CUSTOMER': { windowMs: 15 * 60 * 1000, max: 100 }, // 15 minutes, 100 requests
    'DRIVER': { windowMs: 15 * 60 * 1000, max: 200 }, // 15 minutes, 200 requests (location updates)
    'SUPERVISOR': { windowMs: 15 * 60 * 1000, max: 150 }, // 15 minutes, 150 requests
    'ACCOUNTANT': { windowMs: 15 * 60 * 1000, max: 80 }, // 15 minutes, 80 requests
    'WORKSHOP': { windowMs: 15 * 60 * 1000, max: 60 }, // 15 minutes, 60 requests
    'ADMIN': { windowMs: 15 * 60 * 1000, max: 300 } // 15 minutes, 300 requests
  };

  const roleLimits = { ...defaultLimits, ...limits };

  return (req, res, next) => {
    const userRole = req.userRole || 'ANONYMOUS';
    const limit = roleLimits[userRole] || roleLimits['CUSTOMER'];

    // In a real implementation, you would use a rate limiting library like express-rate-limit
    // For now, we'll just add the limit information to the request
    req.rateLimit = limit;
    next();
  };
};

/**
 * Audit Logging Middleware
 * برمجية وسيطة لتسجيل المراجعات
 */
const auditLog = (action, resourceType) => {
  return (req, res, next) => {
    // Store audit information
    req.audit = {
      action,
      resourceType,
      userId: req.userId,
      userRole: req.userRole,
      timestamp: new Date(),
      ip: req.ip,
      userAgent: req.get('User-Agent'),
      endpoint: req.originalUrl,
      method: req.method
    };

    // Log the action (in a real implementation, you'd save to database)
    console.log(`AUDIT: ${req.userRole} ${req.userId} performed ${action} on ${resourceType}`);

    next();
  };
};

module.exports = {
  authenticateWithRole,
  requireRole,
  requireOwnership,
  requireDepartment,
  requireFeature,
  roleRateLimit,
  auditLog
};
