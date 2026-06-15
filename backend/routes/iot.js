//
/**
 * ============================================
 * 📡 IoT Routes - إدارة أجهزة إنترنت الأشياء
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { IoTDevice, SensorData, IoTRule, DeviceCommand } = require('../models/IoTPlatform');
const logger = require('../utils/logger');

// Devices
// @route   GET /api/v1/iot/devices
// @desc    Get IoT devices
// @access  Private
router.get('/devices', protect, async (req, res) => {
  try {
    const { type, category, status, online } = req.query;

    let query = { company: req.user.company };
    if (type) query.type = type;
    if (category) query.category = category;
    if (online !== undefined) query['status.online'] = online === 'true';

    const devices = await IoTDevice.find(query)
      .populate('assignment.entityId')
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: devices.length,
      data: devices
    });

  } catch (error) {
    logger.error('Get IoT devices error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/iot/devices
// @desc    Register new device
// @access  Private (Admin, Developer)
router.post('/devices', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const deviceId = `IOT-${Date.now()}`;

    const device = await IoTDevice.create({
      ...req.body,
      deviceId,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: device
    });

  } catch (error) {
    logger.error('Create IoT device error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/iot/devices/:id
// @desc    Get device details
// @access  Private
router.get('/devices/:id', protect, async (req, res) => {
  try {
    const device = await IoTDevice.findById(req.params.id)
      .populate('assignment.entityId');

    if (!device) {
      return res.status(404).json({ success: false, message: 'Device not found' });
    }

    // Get latest sensor data
    const latestData = await SensorData.findOne({ device: device._id })
      .sort({ timestamp: -1 })
      .limit(1);

    res.json({
      success: true,
      data: {
        ...device.toObject(),
        latestData
      }
    });

  } catch (error) {
    logger.error('Get device error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/iot/devices/:id/assign
// @desc    Assign device to entity
// @access  Private
router.put('/devices/:id/assign', protect, async (req, res) => {
  try {
    const { entityType, entityId } = req.body;

    const device = await IoTDevice.findByIdAndUpdate(
      req.params.id,
      {
        assignment: {
          entityType,
          entityId,
          assignedAt: new Date()
        },
        updatedAt: new Date()
      },
      { new: true }
    );

    if (!device) {
      return res.status(404).json({ success: false, message: 'Device not found' });
    }

    res.json({
      success: true,
      message: 'Device assigned',
      data: device
    });

  } catch (error) {
    logger.error('Assign device error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/iot/devices/:id/config
// @desc    Update device configuration
// @access  Private
router.put('/devices/:id/config', protect, async (req, res) => {
  try {
    const device = await IoTDevice.findByIdAndUpdate(
      req.params.id,
      {
        $set: { configuration: req.body },
        updatedAt: new Date()
      },
      { new: true }
    );

    if (!device) {
      return res.status(404).json({ success: false, message: 'Device not found' });
    }

    res.json({
      success: true,
      message: 'Configuration updated',
      data: device
    });

  } catch (error) {
    logger.error('Update config error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Sensor Data
// @route   GET /api/v1/iot/devices/:id/data
// @desc    Get device sensor data
// @access  Private
router.get('/devices/:id/data', protect, async (req, res) => {
  try {
    const { from, to, limit = 100 } = req.query;

    let query = { device: req.params.id };
    if (from && to) {
      query.timestamp = {
        $gte: new Date(from),
        $lte: new Date(to)
      };
    }

    const data = await SensorData.find(query)
      .sort({ timestamp: -1 })
      .limit(parseInt(limit));

    res.json({
      success: true,
      count: data.length,
      data
    });

  } catch (error) {
    logger.error('Get sensor data error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/iot/data
// @desc    Receive sensor data (webhook)
// @access  Public (with API key)
router.post('/data', async (req, res) => {
  try {
    const { deviceId, data } = req.body;

    // Verify device
    const device = await IoTDevice.findOne({ deviceId });
    if (!device) {
      return res.status(401).json({ success: false, message: 'Invalid device' });
    }

    // Create sensor data record
    const sensorData = await SensorData.create({
      device: device._id,
      ...data,
      company: device.company
    });

    // Update device status
    await IoTDevice.findByIdAndUpdate(device._id, {
      'status.online': true,
      'status.lastSeen': new Date(),
      'telemetry.lastPayload': data,
      'telemetry.lastPayloadAt': new Date(),
      $inc: {
        'telemetry.totalMessages': 1,
        'telemetry.totalBytes': JSON.stringify(data).length
      }
    });

    // TODO: Process alerts and rules

    res.status(201).json({
      success: true,
      received: true
    });

  } catch (error) {
    logger.error('Receive sensor data error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Commands
// @route   POST /api/v1/iot/devices/:id/commands
// @desc    Send command to device
// @access  Private
router.post('/devices/:id/commands', protect, async (req, res) => {
  try {
    const device = await IoTDevice.findById(req.params.id);
    if (!device) {
      return res.status(404).json({ success: false, message: 'Device not found' });
    }

    const command = await DeviceCommand.create({
      device: device._id,
      command: req.body,
      company: req.user.company
    });

    // TODO: Send command via MQTT or appropriate protocol

    res.status(201).json({
      success: true,
      data: command
    });

  } catch (error) {
    logger.error('Send command error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/iot/devices/:id/commands
// @desc    Get device command history
// @access  Private
router.get('/devices/:id/commands', protect, async (req, res) => {
  try {
    const commands = await DeviceCommand.find({ device: req.params.id })
      .sort({ createdAt: -1 })
      .limit(50);

    res.json({
      success: true,
      data: commands
    });

  } catch (error) {
    logger.error('Get commands error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Rules
// @route   GET /api/v1/iot/rules
// @desc    Get IoT rules
// @access  Private
router.get('/rules', protect, async (req, res) => {
  try {
    const rules = await IoTRule.find({ company: req.user.company });

    res.json({
      success: true,
      count: rules.length,
      data: rules
    });

  } catch (error) {
    logger.error('Get IoT rules error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/iot/rules
// @desc    Create IoT rule
// @access  Private
router.post('/rules', protect, async (req, res) => {
  try {
    const rule = await IoTRule.create({
      ...req.body,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: rule
    });

  } catch (error) {
    logger.error('Create IoT rule error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Dashboard
// @route   GET /api/v1/iot/dashboard
// @desc    IoT dashboard
// @access  Private
router.get('/dashboard', protect, async (req, res) => {
  try {
    const stats = await Promise.all([
      // Total devices
      IoTDevice.countDocuments({ company: req.user.company }),

      // Online devices
      IoTDevice.countDocuments({
        company: req.user.company,
        'status.online': true
      }),

      // Devices by type
      IoTDevice.aggregate([
        { $match: { company: req.user.company._id } },
        { $group: { _id: '$type', count: { $sum: 1 } } }
      ]),

      // Today's sensor readings
      SensorData.countDocuments({
        company: req.user.company,
        timestamp: { $gte: new Date(new Date().setHours(0, 0, 0, 0)) }
      }),

      // Recent alerts
      SensorData.find({
        company: req.user.company,
        'alerts.0': { $exists: true }
      })
        .populate('device', 'name deviceId')
        .sort({ timestamp: -1 })
        .limit(10)
    ]);

    res.json({
      success: true,
      data: {
        totalDevices: stats[0],
        onlineDevices: stats[1],
        devicesByType: stats[2],
        todayReadings: stats[3],
        recentAlerts: stats[4]
      }
    });

  } catch (error) {
    logger.error('IoT dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Real-time telemetry (WebSocket endpoint)
// @route   GET /api/v1/iot/telemetry/:deviceId
// @desc    Get real-time telemetry
// @access  Private
router.get('/telemetry/:deviceId', protect, async (req, res) => {
  try {
    const device = await IoTDevice.findOne({
      deviceId: req.params.deviceId,
      company: req.user.company
    });

    if (!device) {
      return res.status(404).json({ success: false, message: 'Device not found' });
    }

    const telemetry = await SensorData.find({ device: device._id })
      .sort({ timestamp: -1 })
      .limit(100);

    res.json({
      success: true,
      device: {
        id: device.deviceId,
        name: device.name,
        online: device.status.online,
        lastSeen: device.status.lastSeen
      },
      telemetry
    });

  } catch (error) {
    logger.error('Get telemetry error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
