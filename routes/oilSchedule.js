/**
 * ============================================
 * ⛽ Oil Schedule Routes - نظام إدهام
 * Edham Logistics - Oil Change Schedule API
 * ============================================
 */

const express = require('express');
const router = express.Router();
const OilScheduleController = require('../controllers/oilScheduleController');
const { auth, authorize } = require('../middleware/auth');
const { ROLES } = require('../config/constants');

// Get all oil schedules
router.get('/', auth, OilScheduleController.getAll);

// Get upcoming oil changes
router.get('/upcoming', auth, authorize(ROLES.SUPERVISOR, ROLES.ADMIN), OilScheduleController.getUpcoming);

// Get oil schedule by truck
router.get('/truck/:truckId', auth, OilScheduleController.getByTruck);

// Create oil schedule
router.post('/', auth, authorize(ROLES.SUPERVISOR, ROLES.ADMIN), OilScheduleController.create);

// Get single oil schedule
router.get('/:id', auth, OilScheduleController.getById);

// Update oil schedule
router.put('/:id', auth, authorize(ROLES.SUPERVISOR, ROLES.ADMIN), OilScheduleController.update);

// Record oil change
router.post('/:id/record-change', auth, authorize(ROLES.SUPERVISOR, ROLES.ADMIN), OilScheduleController.recordChange);

// Delete oil schedule
router.delete('/:id', auth, authorize(ROLES.SUPERVISOR, ROLES.ADMIN), OilScheduleController.delete);

module.exports = router;
