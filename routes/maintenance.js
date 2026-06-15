/**
 * ============================================
 * 🔧 Maintenance Routes - نظام إدهام
 * Edham Logistics - Maintenance API
 * ============================================
 */

const express = require('express');
const router = express.Router();
const MaintenanceController = require('../controllers/maintenanceController');
const { auth, authorize } = require('../middleware/auth');
const { ROLES } = require('../config/constants');

// Get all maintenance records
router.get('/', auth, MaintenanceController.getAll);

// Get maintenance statistics
router.get('/statistics', auth, authorize(ROLES.ADMIN, ROLES.SUPERVISOR), MaintenanceController.getStatistics);

// Get maintenance alerts
router.get('/alerts', auth, authorize(ROLES.ADMIN, ROLES.SUPERVISOR), MaintenanceController.getAlerts);

// Create maintenance record
router.post('/', auth, authorize(ROLES.SUPERVISOR, ROLES.ADMIN), MaintenanceController.create);

// Get maintenance by truck
router.get('/truck/:truckId', auth, MaintenanceController.getByTruck);

// Get single maintenance record
router.get('/:id', auth, MaintenanceController.getById);

// Update maintenance record
router.put('/:id', auth, authorize(ROLES.SUPERVISOR, ROLES.ADMIN), MaintenanceController.update);

// Complete maintenance
router.patch('/:id/complete', auth, authorize(ROLES.SUPERVISOR, ROLES.ADMIN), MaintenanceController.complete);

module.exports = router;
