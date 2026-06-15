/**
 * ============================================
 * 📍 Tracking Routes - نظام إدهام
 * Edham Logistics - Driver Tracking System
 * ============================================
 */

const express = require('express');
const router = express.Router();
const TrackingController = require('../controllers/trackingController');
const { auth, authorize } = require('../middleware/auth');
const { validateLocation } = require('../middleware/validation');
const { ROLES } = require('../config/constants');

// Get driver current location
router.get('/driver/location',
  auth,
  authorize(ROLES.DRIVER),
  TrackingController.getCurrentLocation
);

// Update driver location (automatic GPS)
router.post('/driver/location',
  auth,
  authorize(ROLES.DRIVER),
  validateLocation,
  TrackingController.updateLocation
);

// Add manual location entry
router.post('/driver/manual-location',
  auth,
  authorize(ROLES.DRIVER),
  validateLocation,
  TrackingController.addManualLocation
);

// Get driver location history
router.get('/driver/history',
  auth,
  authorize(ROLES.DRIVER, ROLES.SUPERVISOR, ROLES.CLIENT),
  TrackingController.getLocationHistory
);

// Get live tracking for shipment
router.get('/shipment/:shipmentId/live',
  auth,
  authorize(ROLES.SUPERVISOR, ROLES.CLIENT),
  TrackingController.getLiveTracking
);

// Get all active drivers locations (for supervisor)
router.get('/drivers/active',
  auth,
  authorize(ROLES.SUPERVISOR),
  TrackingController.getActiveDrivers
);

// Start/Stop tracking session
router.post('/driver/session',
  auth,
  authorize(ROLES.DRIVER),
  TrackingController.toggleTrackingSession
);

// Get tracking statistics
router.get('/statistics',
  auth,
  authorize(ROLES.SUPERVISOR, ROLES.ADMIN),
  TrackingController.getStatistics
);

// Get route history for driver
router.get('/driver/routes',
  auth,
  authorize(ROLES.DRIVER, ROLES.SUPERVISOR),
  TrackingController.getRouteHistory
);

// Export tracking data
router.get('/export/:format',
  auth,
  authorize(ROLES.SUPERVISOR, ROLES.ADMIN),
  TrackingController.exportTrackingData
);

module.exports = router;
