const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const SensorData = require('../models/SensorData');
const logger = require('../utils/logger');

// GET /api/v1/sensors — latest readings per truck
router.get('/', protect, async (req, res) => {
  try {
    const sensors = await SensorData.find({ company: req.user.company })
      .sort({ timestamp: -1 })
      .limit(100);
    res.json({ success: true, count: sensors.length, data: sensors });
  } catch (error) {
    logger.error('Get sensors error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// GET /api/v1/sensors/:truckId
router.get('/:truckId', protect, async (req, res) => {
  try {
    const sensors = await SensorData.find({ truck: req.params.truckId })
      .sort({ timestamp: -1 })
      .limit(200);
    res.json({ success: true, count: sensors.length, data: sensors });
  } catch (error) {
    logger.error('Get sensor data error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// POST /api/v1/sensors — ingest sensor reading
router.post('/', protect, async (req, res) => {
  try {
    const reading = await SensorData.create({
      ...req.body,
      company: req.user.company,
      timestamp: new Date(),
    });
    res.status(201).json({ success: true, data: reading });
  } catch (error) {
    logger.error('Create sensor reading error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
