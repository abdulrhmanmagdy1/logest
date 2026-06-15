/**
 * ============================================
 * 📊 Analytics Routes - نظام إدهام
 * Edham Logistics - Analytics API with Controllers
 * ============================================
 */

const express = require('express');
const router = express.Router();
const AnalyticsController = require('../controllers/analyticsController');
const { auth } = require('../middleware/auth');

// Dashboard metrics
router.get('/dashboard', auth, AnalyticsController.getDashboardMetrics);

// Monthly report
router.get('/monthly-report', auth, AnalyticsController.getMonthlyReport);

// Driver performance
router.get('/driver-performance', auth, AnalyticsController.getDriverPerformance);

// Shipments by city
router.get('/shipments-by-city', auth, AnalyticsController.getShipmentsByCity);

// Revenue breakdown
router.get('/revenue-breakdown', auth, AnalyticsController.getRevenueBreakdown);

module.exports = router;
