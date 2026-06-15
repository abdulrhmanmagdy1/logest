/**
 * ============================================
 * 📊 Reports Routes - نظام إدهام
 * Analytics and reporting endpoints
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const Shipment = require('../models/Shipment');
const Invoice = require('../models/Invoice');
const User = require('../models/User');
const logger = require('../utils/logger');

// @route   GET /api/v1/reports/dashboard
// @desc    Get admin dashboard stats
// @access  Private (Admin, Supervisor, Accountant)
router.get('/dashboard', protect, authorize('admin', 'supervisor', 'accountant'), async (req, res) => {
  try {
    const today = new Date();
    const startOfDay = new Date(today.setHours(0, 0, 0, 0));
    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
    const startOfYear = new Date(today.getFullYear(), 0, 1);

    // Shipment stats
    const shipmentStats = await Shipment.aggregate([
      {
        $group: {
          _id: '$status',
          count: { $sum: 1 },
          revenue: { $sum: '$pricing.total' }
        }
      }
    ]);

    // Today's shipments
    const todayShipments = await Shipment.countDocuments({
      createdAt: { $gte: startOfDay }
    });

    // Monthly revenue
    const monthlyRevenue = await Invoice.aggregate([
      {
        $match: {
          status: { $in: ['paid', 'partial'] },
          issueDate: { $gte: startOfMonth }
        }
      },
      {
        $group: {
          _id: null,
          total: { $sum: '$payment.paidAmount' }
        }
      }
    ]);

    // Driver stats
    const driverStats = await User.aggregate([
      { $match: { role: 'driver', status: 'active' } },
      {
        $group: {
          _id: null,
          total: { $sum: 1 },
          available: {
            $sum: { $cond: [{ $eq: ['$driverInfo.isAvailable', true] }, 1, 0] }
          }
        }
      }
    ]);

    // Recent activity
    const recentShipments = await Shipment.find()
      .populate('client', 'firstName lastName')
      .populate('driver', 'firstName lastName')
      .sort('-createdAt')
      .limit(10);

    res.json({
      success: true,
      data: {
        shipments: {
          today: todayShipments,
          byStatus: shipmentStats
        },
        revenue: {
          monthly: monthlyRevenue[0]?.total || 0
        },
        drivers: driverStats[0] || { total: 0, available: 0 },
        recentActivity: recentShipments
      }
    });
  } catch (error) {
    logger.error('Get dashboard stats error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/reports/shipments
// @desc    Get shipment reports
// @access  Private (Admin, Supervisor)
router.get('/shipments', protect, authorize('admin', 'supervisor'), async (req, res) => {
  try {
    const { startDate, endDate, groupBy = 'day' } = req.query;
    
    const match = {};
    if (startDate || endDate) {
      match.createdAt = {};
      if (startDate) match.createdAt.$gte = new Date(startDate);
      if (endDate) match.createdAt.$lte = new Date(endDate);
    }

    const groupFormat = groupBy === 'month' ? '%Y-%m' : '%Y-%m-%d';

    const report = await Shipment.aggregate([
      { $match: match },
      {
        $group: {
          _id: { $dateToString: { format: groupFormat, date: '$createdAt' } },
          totalShipments: { $sum: 1 },
          completed: {
            $sum: { $cond: [{ $eq: ['$status', 'completed'] }, 1, 0] }
          },
          cancelled: {
            $sum: { $cond: [{ $eq: ['$status', 'cancelled'] }, 1, 0] }
          },
          revenue: { $sum: '$pricing.total' }
        }
      },
      { $sort: { _id: 1 } }
    ]);

    res.json({ success: true, data: report });
  } catch (error) {
    logger.error('Get shipment report error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/reports/drivers
// @desc    Get driver performance report
// @access  Private (Admin, Supervisor)
router.get('/drivers', protect, authorize('admin', 'supervisor'), async (req, res) => {
  try {
    const driverStats = await Shipment.aggregate([
      {
        $match: {
          driver: { $exists: true, $ne: null },
          status: { $in: ['delivered', 'completed'] }
        }
      },
      {
        $group: {
          _id: '$driver',
          totalTrips: { $sum: 1 },
          totalEarnings: { $sum: '$pricing.basePrice' },
          avgDeliveryTime: { $avg: { $subtract: ['$delivery.actualDate', '$pickup.actualDate'] } }
        }
      },
      {
        $lookup: {
          from: 'users',
          localField: '_id',
          foreignField: '_id',
          as: 'driverInfo'
        }
      },
      { $unwind: '$driverInfo' },
      {
        $project: {
          driverName: { $concat: ['$driverInfo.firstName', ' ', '$driverInfo.lastName'] },
          totalTrips: 1,
          totalEarnings: 1,
          avgDeliveryTime: 1,
          rating: '$driverInfo.driverInfo.rating'
        }
      },
      { $sort: { totalTrips: -1 } }
    ]);

    res.json({ success: true, data: driverStats });
  } catch (error) {
    logger.error('Get driver report error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/reports/clients
// @desc    Get client activity report
// @access  Private (Admin, Supervisor, Accountant)
router.get('/clients', protect, authorize('admin', 'supervisor', 'accountant'), async (req, res) => {
  try {
    const clientStats = await Shipment.aggregate([
      {
        $group: {
          _id: '$client',
          totalShipments: { $sum: 1 },
          totalSpent: { $sum: '$pricing.total' },
          lastShipment: { $max: '$createdAt' }
        }
      },
      {
        $lookup: {
          from: 'users',
          localField: '_id',
          foreignField: '_id',
          as: 'clientInfo'
        }
      },
      { $unwind: '$clientInfo' },
      {
        $project: {
          clientName: { $concat: ['$clientInfo.firstName', ' ', '$clientInfo.lastName'] },
          companyName: '$clientInfo.companyName',
          totalShipments: 1,
          totalSpent: 1,
          lastShipment: 1
        }
      },
      { $sort: { totalSpent: -1 } }
    ]);

    res.json({ success: true, data: clientStats });
  } catch (error) {
    logger.error('Get client report error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/reports/revenue
// @desc    Get revenue report
// @access  Private (Admin, Accountant)
router.get('/revenue', protect, authorize('admin', 'accountant'), async (req, res) => {
  try {
    const { year = new Date().getFullYear() } = req.query;

    const revenueByMonth = await Invoice.aggregate([
      {
        $match: {
          status: { $in: ['paid', 'partial'] },
          issueDate: {
            $gte: new Date(year, 0, 1),
            $lte: new Date(year, 11, 31)
          }
        }
      },
      {
        $group: {
          _id: { $month: '$issueDate' },
          revenue: { $sum: '$payment.paidAmount' },
          invoices: { $sum: 1 }
        }
      },
      { $sort: { _id: 1 } }
    ]);

    res.json({ success: true, data: revenueByMonth });
  } catch (error) {
    logger.error('Get revenue report error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
