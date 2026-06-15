/**
 * ============================================
 * ⛽ Fuel Routes - نظام إدارة الوقود
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const fuelController = require('../controllers/fuelController');

// ========================
// FUEL RECORDS
// ========================

// Create new fuel record
router.post('/records', protect, fuelController.createFuelRecord);

// Get all fuel records
router.get('/records', protect, fuelController.getFuelRecords);

// Get fuel record by ID
router.get('/records/:id', protect, fuelController.getFuelRecordById);

// Update fuel record
router.put('/records/:id', protect, fuelController.updateFuelRecord);

// Delete fuel record
router.delete('/records/:id', protect, fuelController.deleteFuelRecord);

// ========================
// FUEL ANALYTICS
// ========================

// Get truck fuel statistics
router.get('/analytics/truck/:truckId', protect, fuelController.getTruckFuelStats);

// Get fleet fuel summary
router.get('/analytics/fleet', protect, fuelController.getFleetFuelSummary);

// Get fuel consumption trends
router.get('/analytics/trends', protect, fuelController.getFuelConsumptionTrends);

// Get fuel expense reports
router.get('/analytics/expense-report', protect, fuelController.getFuelExpenseReport);

// Get fuel optimization recommendations
router.get('/analytics/recommendations', protect, fuelController.getFuelOptimizationRecommendations);

module.exports = router;
