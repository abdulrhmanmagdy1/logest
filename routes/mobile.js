/**
 * ============================================
 * 📱 Edham Logistics - Mobile API Routes
 * نظام إدهام - مسارات تطبيقات الموبايل
 * ============================================
 */

const express = require('express');
const router = express.Router();
const MobileController = require('../controllers/mobileController');
const { authenticateWithRole, requireRole, requireOwnership } = require('../middleware/roleMiddleware');
const { validate } = require('../middleware/validation');
const { rateLimit } = require('express-rate-limit');
const multer = require('multer');

// Configure file upload for mobile
const upload = multer({
  storage: multer.memoryStorage(),
  limits: {
    fileSize: 10 * 1024 * 1024, // 10MB
    files: 5 // Max 5 files
  },
  fileFilter: (req, file, cb) => {
    const allowedTypes = ['image/jpeg', 'image/png', 'image/jpg', 'application/pdf'];
    if (allowedTypes.includes(file.mimetype)) {
      cb(null, true);
    } else {
      cb(new Error('Invalid file type'), false);
    }
  }
});

// Mobile-specific rate limiting
const mobileRateLimit = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 1000, // 1000 requests per window
  message: {
    success: false,
    error: {
      code: 'RATE_LIMIT_EXCEEDED',
      message: 'Too many requests, please try again later'
    }
  },
  keyGenerator: (req) => `${req.user?.id || req.ip}:${req.path}`
});

// Location updates have higher rate limit
const locationRateLimit = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 5000, // 5000 location updates per window
  message: {
    success: false,
    error: {
      code: 'LOCATION_RATE_LIMIT_EXCEEDED',
      message: 'Too many location updates, please try again later'
    }
  },
  keyGenerator: (req) => `${req.user?.id || req.ip}:location`
});

// ==================== Authentication Routes ====================

// Mobile login with device info
router.post('/auth/login', 
  mobileRateLimit,
  MobileController.mobileLogin
);

// Refresh token
router.post('/auth/refresh', 
  mobileRateLimit,
  authenticateWithRole,
  MobileController.refreshToken
);

// Logout
router.post('/auth/logout', 
  mobileRateLimit,
  authenticateWithRole,
  MobileController.logout
);

// ==================== Customer Routes ====================

// Create order (customer only)
router.post('/customer/orders',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('CUSTOMER'),
  MobileController.createMobileOrder
);

// Get customer orders
router.get('/customer/orders',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('CUSTOMER'),
  MobileController.getCustomerOrders
);

// Track specific order
router.get('/customer/orders/:orderId/tracking',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('CUSTOMER'),
  requireOwnership('order'),
  MobileController.trackOrder
);

// Cancel order
router.patch('/customer/orders/:orderId/cancel',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('CUSTOMER'),
  requireOwnership('order'),
  MobileController.cancelOrder
);

// ==================== Driver Routes ====================

// Get active task
router.get('/driver/active-task',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('DRIVER'),
  MobileController.getActiveTask
);

// Update location (higher rate limit)
router.put('/driver/location',
  locationRateLimit,
  authenticateWithRole,
  requireRole('DRIVER'),
  MobileController.updateDriverLocation
);

// Update task status
router.patch('/driver/orders/:orderId/status',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('DRIVER'),
  requireOwnership('order'),
  MobileController.updateTaskStatus
);

// Upload proof of delivery
router.post('/driver/orders/:orderId/proof',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('DRIVER'),
  requireOwnership('order'),
  upload.array('files', 5),
  MobileController.uploadProofOfDelivery
);

// Get earnings
router.get('/driver/earnings',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('DRIVER'),
  MobileController.getDriverEarnings
);

// Get task history
router.get('/driver/task-history',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('DRIVER'),
  MobileController.getTaskHistory
);

// ==================== Supervisor Routes ====================

// Get fleet status
router.get('/supervisor/fleet',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('SUPERVISOR', 'ADMIN'),
  MobileController.getFleetStatus
);

// Get all orders for dispatch
router.get('/supervisor/orders',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('SUPERVISOR', 'ADMIN'),
  MobileController.getOrdersForDispatch
);

// Assign driver to order
router.post('/supervisor/assign-driver',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('SUPERVISOR', 'ADMIN'),
  MobileController.assignDriverToOrder
);

// Get driver performance
router.get('/supervisor/drivers/:driverId/performance',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('SUPERVISOR', 'ADMIN'),
  MobileController.getDriverPerformance
);

// ==================== Accountant Routes ====================

// Get invoices
router.get('/accountant/invoices',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('ACCOUNTANT', 'ADMIN'),
  MobileController.getInvoices
);

// Get payment summary
router.get('/accountant/payments/summary',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('ACCOUNTANT', 'ADMIN'),
  MobileController.getPaymentSummary
);

// Get financial reports
router.get('/accountant/reports',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('ACCOUNTANT', 'ADMIN'),
  MobileController.getFinancialReports
);

// ==================== Workshop Routes ====================

// Get maintenance schedule
router.get('/workshop/maintenance',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('WORKSHOP', 'ADMIN'),
  MobileController.getMaintenanceSchedule
);

// Get fleet health
router.get('/workshop/fleet-health',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('WORKSHOP', 'ADMIN'),
  MobileController.getFleetHealth
);

// Schedule maintenance
router.post('/workshop/maintenance',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('WORKSHOP', 'ADMIN'),
  MobileController.scheduleMaintenance
);

// Get parts inventory
router.get('/workshop/parts',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('WORKSHOP', 'ADMIN'),
  MobileController.getPartsInventory
);

// ==================== Admin Routes ====================

// Get system statistics
router.get('/admin/stats',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('ADMIN'),
  MobileController.getSystemStats
);

// Manage users
router.get('/admin/users',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('ADMIN'),
  MobileController.getUsers
);

router.post('/admin/users',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('ADMIN'),
  MobileController.createUser
);

router.patch('/admin/users/:userId',
  mobileRateLimit,
  authenticateWithRole,
  requireRole('ADMIN'),
  MobileController.updateUser
);

// ==================== Payment Routes ====================

// Create payment intent
router.post('/payments/create-intent',
  mobileRateLimit,
  authenticateWithRole,
  MobileController.createPaymentIntent
);

// Confirm payment
router.post('/payments/confirm',
  mobileRateLimit,
  authenticateWithRole,
  MobileController.confirmPayment
);

// Get payment history
router.get('/payments/history',
  mobileRateLimit,
  authenticateWithRole,
  MobileController.getPaymentHistory
);

// ==================== File Upload Routes ====================

// Upload documents
router.post('/upload',
  mobileRateLimit,
  authenticateWithRole,
  upload.single('file'),
  MobileController.uploadDocument
);

// Get uploaded files
router.get('/files/:fileId',
  mobileRateLimit,
  authenticateWithRole,
  MobileController.getFile
);

// ==================== Notification Routes ====================

// Get notifications
router.get('/notifications',
  mobileRateLimit,
  authenticateWithRole,
  MobileController.getNotifications
);

// Mark notification as read
router.patch('/notifications/:notificationId/read',
  mobileRateLimit,
  authenticateWithRole,
  MobileController.markNotificationRead
);

// Send push notification token
router.post('/notifications/push-token',
  mobileRateLimit,
  authenticateWithRole,
  MobileController.updatePushToken
);

// ==================== Sync Routes ====================

// Sync offline data
router.post('/sync/offline',
  mobileRateLimit,
  authenticateWithRole,
  MobileController.syncOfflineData
);

// Get sync status
router.get('/sync/status',
  mobileRateLimit,
  authenticateWithRole,
  MobileController.getSyncStatus
);

// ==================== Utility Routes ====================

// Get app configuration
router.get('/config',
  mobileRateLimit,
  MobileController.getAppConfig
);

// Get cities list
router.get('/cities',
  mobileRateLimit,
  MobileController.getCities
);

// Get vehicle types
router.get('/vehicle-types',
  mobileRateLimit,
  MobileController.getVehicleTypes
);

// Get cargo types
router.get('/cargo-types',
  mobileRateLimit,
  MobileController.getCargoTypes
);

// Validate address
router.post('/validate-address',
  mobileRateLimit,
  MobileController.validateAddress
);

// Calculate price estimate
router.post('/calculate-price',
  mobileRateLimit,
  MobileController.calculatePriceEstimate
);

// ==================== WebSocket Routes ====================

// These are handled by Socket.IO, but we document them here for API completeness

// WebSocket events:
// - subscribe_to_order
// - unsubscribe_from_order
// - update_location
// - order_status_changed
// - new_message
// - driver_assigned
// - payment_completed

// ==================== Error Handling ====================

// 404 handler for mobile routes
router.use('*', (req, res) => {
  res.status(404).json({
    success: false,
    error: {
      code: 'ENDPOINT_NOT_FOUND',
      message: 'Mobile API endpoint not found',
      path: req.originalUrl,
      method: req.method
    }
  });
});

// Global error handler for mobile routes
router.use((error, req, res, next) => {
  logger.error('Mobile API error:', error);

  // Handle multer errors
  if (error instanceof multer.MulterError) {
    let message = 'File upload error';
    if (error.code === 'LIMIT_FILE_SIZE') {
      message = 'File size too large (max 10MB)';
    } else if (error.code === 'LIMIT_FILE_COUNT') {
      message = 'Too many files (max 5 files)';
    } else if (error.code === 'LIMIT_UNEXPECTED_FILE') {
      message = 'Unexpected file field';
    }

    return res.status(400).json({
      success: false,
      error: {
        code: 'FILE_UPLOAD_ERROR',
        message
      }
    });
  }

  // Handle validation errors
  if (error.name === 'ValidationError') {
    return res.status(400).json({
      success: false,
      error: {
        code: 'VALIDATION_ERROR',
        message: error.message,
        details: error.details
      }
    });
  }

  // Handle JWT errors
  if (error.name === 'JsonWebTokenError') {
    return res.status(401).json({
      success: false,
      error: {
        code: 'INVALID_TOKEN',
        message: 'Invalid authentication token'
      }
    });
  }

  if (error.name === 'TokenExpiredError') {
    return res.status(401).json({
      success: false,
      error: {
        code: 'TOKEN_EXPIRED',
        message: 'Authentication token expired'
      }
    });
  }

  // Default error response
  res.status(500).json({
    success: false,
    error: {
      code: 'INTERNAL_ERROR',
      message: 'Internal server error'
    }
  });
});

module.exports = router;
