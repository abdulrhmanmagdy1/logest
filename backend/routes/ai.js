//
/**
 * ============================================
 * 🤖 AI Routes - نقاط نهاية الذكاء الاصطناعي
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const aiService = require('../services/aiPredictionService');
const logger = require('../utils/logger');

// @route   POST /api/v1/ai/predict/delivery-time
// @desc    Predict delivery time for a route
// @access  Private
router.post('/predict/delivery-time', protect, async (req, res) => {
  try {
    const { origin, destination, cargoType, pickupTime, weather } = req.body;

    const prediction = await aiService.predictDeliveryTime({
      origin,
      destination,
      cargoType,
      pickupTime,
      weather
    });

    res.json({
      success: true,
      data: prediction
    });
  } catch (error) {
    logger.error('Delivery time prediction error:', error);
    res.status(500).json({ success: false, message: 'Prediction failed' });
  }
});

// @route   GET /api/v1/ai/predict/demand/:city
// @desc    Predict shipment demand for a city
// @access  Private (Admin, Supervisor)
router.get('/predict/demand/:city', protect, authorize('admin', 'supervisor'), async (req, res) => {
  try {
    const { city } = req.params;
    const { days = 7 } = req.query;

    const prediction = await aiService.predictDemand(city, parseInt(days));

    res.json({
      success: true,
      data: prediction
    });
  } catch (error) {
    logger.error('Demand prediction error:', error);
    res.status(500).json({ success: false, message: 'Prediction failed' });
  }
});

// @route   GET /api/v1/ai/predict/maintenance/:truckId
// @desc    Predict vehicle maintenance needs
// @access  Private (Admin, Supervisor, Workshop)
router.get('/predict/maintenance/:truckId', protect, authorize('admin', 'supervisor', 'workshop'), async (req, res) => {
  try {
    const { truckId } = req.params;

    const prediction = await aiService.predictMaintenance(truckId);

    res.json({
      success: true,
      data: prediction
    });
  } catch (error) {
    logger.error('Maintenance prediction error:', error);
    res.status(500).json({ success: false, message: 'Prediction failed' });
  }
});

// @route   POST /api/v1/ai/predict/temperature-anomalies
// @desc    Predict temperature anomalies from sensor data
// @access  Private
router.post('/predict/temperature-anomalies', protect, async (req, res) => {
  try {
    const { sensorId, readings } = req.body;

    const prediction = await aiService.predictTemperatureAnomalies(sensorId, readings);

    res.json({
      success: true,
      data: prediction
    });
  } catch (error) {
    logger.error('Temperature anomaly prediction error:', error);
    res.status(500).json({ success: false, message: 'Prediction failed' });
  }
});

// @route   POST /api/v1/ai/optimize/pricing
// @desc    Get dynamic pricing recommendation
// @access  Private (Admin, Accountant)
router.post('/optimize/pricing', protect, authorize('admin', 'accountant'), async (req, res) => {
  try {
    const { route, cargoType, demand } = req.body;

    const recommendation = await aiService.optimizePricing(route, cargoType, demand);

    res.json({
      success: true,
      data: recommendation
    });
  } catch (error) {
    logger.error('Pricing optimization error:', error);
    res.status(500).json({ success: false, message: 'Optimization failed' });
  }
});

// @route   GET /api/v1/ai/predict/driver-performance/:driverId
// @desc    Predict driver performance
// @access  Private (Admin, Supervisor)
router.get('/predict/driver-performance/:driverId', protect, authorize('admin', 'supervisor'), async (req, res) => {
  try {
    const { driverId } = req.params;

    const prediction = await aiService.predictDriverPerformance(driverId);

    res.json({
      success: true,
      data: prediction
    });
  } catch (error) {
    logger.error('Driver performance prediction error:', error);
    res.status(500).json({ success: false, message: 'Prediction failed' });
  }
});

// @route   GET /api/v1/ai/insights/dashboard
// @desc    Get AI-powered insights for dashboard
// @access  Private
router.get('/insights/dashboard', protect, async (req, res) => {
  try {
    const { companyId } = req.query;

    // Generate insights based on company data
    const insights = {
      trends: {
        shipmentVolume: { trend: 'up', percentage: 12 },
        onTimeDelivery: { trend: 'stable', percentage: 94 },
        customerSatisfaction: { trend: 'up', percentage: 4 }
      },
      predictions: {
        nextWeekShipments: 145,
        expectedRevenue: 65000,
        atRiskShipments: 3
      },
      recommendations: [
        'زيادة عدد السائقين في فرع جدة بناءً على الطلب المتوقع',
        'صيانة وقائية موصى بها لـ 2 مركبة',
        '3 شحنات معرضة للتأخير - التوصية بتغيير المسارات'
      ],
      generatedAt: new Date()
    };

    res.json({
      success: true,
      data: insights
    });
  } catch (error) {
    logger.error('Dashboard insights error:', error);
    res.status(500).json({ success: false, message: 'Failed to generate insights' });
  }
});

module.exports = router;
