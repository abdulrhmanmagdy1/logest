/**
 * ============================================
 * 📍 Locations Routes - نظام إدهام
 * Edham Logistics - Driver Location Tracking API
 * ============================================
 */

const express = require('express');
const router = express.Router();
const LocationController = require('../controllers/locationController');
const { auth, authorize } = require('../middleware/auth');
const { ROLES } = require('../config/constants');

// Save driver location
router.post('/', auth, LocationController.save);

// Get all active drivers locations
router.get('/active', auth, authorize(ROLES.ADMIN, ROLES.SUPERVISOR), LocationController.getAllActive);

// Get latest location for driver
router.get('/latest/:driverId', auth, LocationController.getLatest);

// Get location history for driver
router.get('/history/:driverId', auth, LocationController.getHistory);

module.exports = router;
