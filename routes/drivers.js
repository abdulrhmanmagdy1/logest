/**
 * ============================================
 * 🚛 Edham Logistics - Driver Routes
 * نظام إدهام - مسارات السائقين
 * ============================================
 */

const express = require('express');
const router = express.Router();
const DriverController = require('../controllers/driverController');
const { authenticate, authorize } = require('../middleware/auth');
const { validateDriverLocation, validateSurvey } = require('../middleware/validation');
const rateLimit = require('express-rate-limit');

// Rate limiting for location updates
const locationUpdateLimit = rateLimit({
  windowMs: 60 * 1000, // 1 minute
  max: 30, // Limit each IP to 30 location updates per minute
  message: {
    success: false,
    message: 'Too many location updates, please try again later'
  }
});

// Rate limiting for status updates
const statusUpdateLimit = rateLimit({
  windowMs: 60 * 1000, // 1 minute
  max: 10, // Limit each IP to 10 status updates per minute
  message: {
    success: false,
    message: 'Too many status updates, please try again later'
  }
});

// Get driver profile and current status
// GET /api/v1/drivers/profile
router.get('/profile', 
  authenticate,
  authorize(['driver']),
  DriverController.getDriverProfile
);

// Update driver availability status
// PUT /api/v1/drivers/status
router.put('/status', 
  authenticate,
  authorize(['driver']),
  statusUpdateLimit,
  DriverController.updateDriverStatus
);

// Get current active task
// GET /api/v1/drivers/active-task
router.get('/active-task', 
  authenticate,
  authorize(['driver']),
  DriverController.getActiveTask
);

// Accept or reject assigned task
// POST /api/v1/drivers/task-response
router.post('/task-response', 
  authenticate,
  authorize(['driver']),
  DriverController.respondToTask
);

// Update task state (state machine transitions)
// PATCH /api/v1/orders/:id/status
router.patch('/orders/:id/status', 
  authenticate,
  authorize(['driver']),
  statusUpdateLimit,
  DriverController.updateOrderStatus
);

// Upload proof of delivery documents
// POST /api/v1/orders/:id/proof-of-delivery
router.post('/orders/:id/proof-of-delivery', 
  authenticate,
  authorize(['driver']),
  DriverController.uploadProofOfDelivery
);

// Submit post-task survey
// POST /api/v1/orders/:id/survey
router.post('/orders/:id/survey', 
  authenticate,
  authorize(['driver']),
  validateSurvey,
  DriverController.submitSurvey
);

// Update driver location
// PUT /api/v1/drivers/location
router.put('/location', 
  authenticate,
  authorize(['driver']),
  locationUpdateLimit,
  validateDriverLocation,
  DriverController.updateLocation
);

// Get driver performance metrics
// GET /api/v1/drivers/performance
router.get('/performance', 
  authenticate,
  authorize(['driver']),
  DriverController.getPerformance
);

// Sync offline data
// POST /api/v1/drivers/sync-offline
router.post('/sync-offline', 
  authenticate,
  authorize(['driver']),
  DriverController.syncOfflineData
);

// Admin routes (require admin role)
// GET /api/v1/drivers/admin/all
router.get('/admin/all', 
  authenticate,
  authorize(['admin', 'supervisor']),
  DriverController.getAllDrivers
);

// GET /api/v1/drivers/admin/available
router.get('/admin/available', 
  authenticate,
  authorize(['admin', 'supervisor']),
  DriverController.getAvailableDrivers
);

// POST /api/v1/drivers/admin/:id/assign-task
router.post('/admin/:id/assign-task', 
  authenticate,
  authorize(['admin', 'supervisor']),
  DriverController.assignTaskToDriver
);

// PUT /api/v1/drivers/admin/:id/suspend
router.put('/admin/:id/suspend', 
  authenticate,
  authorize(['admin']),
  DriverController.suspendDriver
);

// PUT /api/v1/drivers/admin/:id/activate
router.put('/admin/:id/activate', 
  authenticate,
  authorize(['admin']),
  DriverController.activateDriver
);

module.exports = router;
