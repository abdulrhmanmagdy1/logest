/**
 * ============================================
 * 🛣️ Trips Routes - نظام إدهام
 * Edham Logistics - Trip Management API
 * ============================================
 */

const express = require('express');
const router = express.Router();
const TripController = require('../controllers/tripController');
const { auth, authorize } = require('../middleware/auth');
const { ROLES } = require('../config/constants');

// Get all trips
router.get('/', auth, TripController.getAll);

// Get active trips
router.get('/active/all', auth, authorize(ROLES.ADMIN, ROLES.SUPERVISOR), TripController.getActive);

// Get trips by employee
router.get('/employee/:employeeId', auth, TripController.getByEmployee);

// Create new trip
router.post('/', auth, TripController.create);

// Start trip
router.patch('/:id/start', auth, TripController.start);

// Update trip location
router.patch('/:id/location', auth, TripController.updateLocation);

// Pause trip
router.patch('/:id/pause', auth, TripController.pause);

// Resume trip
router.patch('/:id/resume', auth, TripController.resume);

// Complete trip
router.patch('/:id/complete', auth, TripController.complete);

// Cancel trip
router.patch('/:id/cancel', auth, TripController.cancel);

// Get single trip
router.get('/:id', auth, TripController.getById);

// Delete trip
router.delete('/:id', auth, authorize(ROLES.ADMIN), TripController.delete);

module.exports = router;
