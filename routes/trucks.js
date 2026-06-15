/**
 * ============================================
 * 🚛 Trucks Routes - نظام إدهام
 * Edham Logistics - Fleet API with Controllers
 * ============================================
 */

const express = require('express');
const router = express.Router();
const TruckController = require('../controllers/truckController');
const { auth, authorize } = require('../middleware/auth');
const { validateTruck } = require('../middleware/validation');
const { ROLES } = require('../config/constants');

// Get fleet statistics
router.get('/statistics', TruckController.getFleetStatistics);

// Get all trucks
router.get('/', auth, TruckController.getAll);

// Get single truck
router.get('/:id', auth, TruckController.getById);

// Create truck
router.post('/',
  auth,
  authorize(ROLES.SUPERVISOR, ROLES.ADMIN),
  validateTruck,
  TruckController.create
);

// Update truck
router.put('/:id',
  auth,
  authorize(ROLES.SUPERVISOR, ROLES.ADMIN),
  TruckController.update
);

// Update truck location
router.patch('/:id/location',
  auth,
  TruckController.updateLocation
);

// Update kilometers
router.patch('/:id/kilometers',
  auth,
  TruckController.updateKilometers
);

// Delete truck
router.delete('/:id',
  auth,
  authorize(ROLES.ADMIN),
  TruckController.delete
);

module.exports = router;
