/**
 * ============================================
 * 🚛 Drivers Routes - نظام إدهام
 * Driver-specific endpoints
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const User = require('../models/User');
const Shipment = require('../models/Shipment');
const logger = require('../utils/logger');

// @route   GET /api/v1/drivers/available
// @desc    Get available drivers
// @access  Private (Admin, Supervisor)
router.get('/available', protect, authorize('admin', 'supervisor'), async (req, res) => {
  try {
    const drivers = await User.find({
      role: 'driver',
      status: 'active',
      'driverInfo.isAvailable': true
    }).select('firstName lastName phone driverInfo');

    res.json({ success: true, count: drivers.length, data: drivers });
  } catch (error) {
    logger.error('Get available drivers error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/drivers/dashboard
// @desc    Get driver dashboard stats
// @access  Private (Driver)
router.get('/dashboard', protect, async (req, res) => {
  try {
    if (req.user.role !== 'driver') {
      return res.status(403).json({ success: false, message: 'Driver access only' });
    }

    const today = new Date();
    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

    // Active shipment
    const activeShipment = await Shipment.findOne({
      driver: req.user.id,
      status: { $in: ['assigned', 'in_transit', 'at_pickup', 'picked_up', 'on_the_way', 'at_delivery'] }
    }).select('trackingNumber status pickup delivery cargo');

    // Today's trips
    const todayTrips = await Shipment.countDocuments({
      driver: req.user.id,
      'pickup.scheduledDate': {
        $gte: new Date(today.setHours(0, 0, 0, 0)),
        $lt: new Date(today.setHours(23, 59, 59, 999))
      }
    });

    // Monthly stats
    const monthlyStats = await Shipment.aggregate([
      {
        $match: {
          driver: new require('mongoose').Types.ObjectId(req.user.id),
          status: { $in: ['delivered', 'completed'] },
          createdAt: { $gte: startOfMonth }
        }
      },
      {
        $group: {
          _id: null,
          totalTrips: { $sum: 1 },
          totalEarnings: { $sum: '$pricing.basePrice' }
        }
      }
    ]);

    // Rating
    const driver = await User.findById(req.user.id).select('driverInfo.rating driverInfo.totalTrips');

    res.json({
      success: true,
      data: {
        activeShipment,
        todayTrips,
        monthlyStats: monthlyStats[0] || { totalTrips: 0, totalEarnings: 0 },
        rating: driver?.driverInfo?.rating || 0,
        totalTrips: driver?.driverInfo?.totalTrips || 0
      }
    });
  } catch (error) {
    logger.error('Get driver dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/drivers/:id/stats
// @desc    Get driver detailed stats
// @access  Private
router.get('/:id/stats', protect, async (req, res) => {
  try {
    const driverId = req.params.id;

    // Authorization
    if (req.user.role === 'driver' && req.user.id !== driverId) {
      return res.status(403).json({ success: false, message: 'Not authorized' });
    }

    const stats = await Shipment.aggregate([
      { $match: { driver: new require('mongoose').Types.ObjectId(driverId) } },
      {
        $group: {
          _id: '$status',
          count: { $sum: 1 }
        }
      }
    ]);

    const monthlyDeliveries = await Shipment.aggregate([
      {
        $match: {
          driver: new require('mongoose').Types.ObjectId(driverId),
          status: 'completed'
        }
      },
      {
        $group: {
          _id: { $dateToString: { format: '%Y-%m', date: '$createdAt' } },
          count: { $sum: 1 }
        }
      },
      { $sort: { _id: -1 } },
      { $limit: 6 }
    ]);

    res.json({
      success: true,
      data: {
        statusBreakdown: stats,
        monthlyDeliveries
      }
    });
  } catch (error) {
    logger.error('Get driver stats error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/drivers/location
// @desc    Update driver location
// @access  Private (Driver)
router.put('/location', protect, async (req, res) => {
  try {
    if (req.user.role !== 'driver') {
      return res.status(403).json({ success: false, message: 'Driver access only' });
    }

    const { lat, lng } = req.body;

    await User.findByIdAndUpdate(req.user.id, {
      'driverInfo.currentLocation': {
        lat,
        lng,
        updatedAt: new Date()
      }
    });

    res.json({ success: true, message: 'Location updated' });
  } catch (error) {
    logger.error('Update driver location error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/drivers/availability
// @desc    Update driver availability
// @access  Private (Driver)
router.put('/availability', protect, async (req, res) => {
  try {
    if (req.user.role !== 'driver') {
      return res.status(403).json({ success: false, message: 'Driver access only' });
    }

    const { isAvailable } = req.body;

    await User.findByIdAndUpdate(req.user.id, {
      'driverInfo.isAvailable': isAvailable
    });

    res.json({ success: true, message: `Availability set to ${isAvailable}` });
  } catch (error) {
    logger.error('Update availability error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
