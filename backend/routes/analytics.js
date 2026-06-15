//
/**
 * ============================================
 * 📊 Analytics & Reports Routes - نظام التحليلات
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const Shipment = require('../models/Shipment');
const User = require('../models/User');
const Truck = require('../models/Truck');
const Invoice = require('../models/Invoice');
const SensorReading = require('../models/SensorData').SensorReading;
const logger = require('../utils/logger');

// @route   GET /api/v1/analytics/dashboard
// @desc    Get comprehensive dashboard analytics
// @access  Private (Admin, CEO, Supervisor)
router.get('/dashboard', protect, authorize('admin', 'ceo', 'supervisor'), async (req, res) => {
  try {
    const { period = '30days' } = req.query;
    
    // Calculate date range
    const endDate = new Date();
    const startDate = new Date();
    
    switch (period) {
      case '7days':
        startDate.setDate(startDate.getDate() - 7);
        break;
      case '30days':
        startDate.setDate(startDate.getDate() - 30);
        break;
      case '90days':
        startDate.setDate(startDate.getDate() - 90);
        break;
      case '1year':
        startDate.setFullYear(startDate.getFullYear() - 1);
        break;
      default:
        startDate.setDate(startDate.getDate() - 30);
    }

    // Parallel queries for performance
    const [
      shipmentStats,
      revenueStats,
      driverStats,
      fleetStats,
      customerStats
    ] = await Promise.all([
      // Shipment Statistics
      Shipment.aggregate([
        {
          $match: {
            createdAt: { $gte: startDate, $lte: endDate }
          }
        },
        {
          $group: {
            _id: null,
            total: { $sum: 1 },
            delivered: {
              $sum: { $cond: [{ $eq: ['$status', 'delivered'] }, 1, 0] }
            },
            inTransit: {
              $sum: { $cond: [{ $eq: ['$status', 'in_transit'] }, 1, 0] }
            },
            pending: {
              $sum: { $cond: [{ $eq: ['$status', 'pending'] }, 1, 0] }
            },
            cancelled: {
              $sum: { $cond: [{ $eq: ['$status', 'cancelled'] }, 1, 0] }
            },
            avgDeliveryTime: { $avg: '$delivery.actualDuration' }
          }
        }
      ]),

      // Revenue Statistics
      Invoice.aggregate([
        {
          $match: {
            createdAt: { $gte: startDate, $lte: endDate },
            status: { $in: ['paid', 'partially_paid'] }
          }
        },
        {
          $group: {
            _id: null,
            totalRevenue: { $sum: '$total' },
            totalPaid: { $sum: '$amountPaid' },
            totalPending: { $sum: '$balance' },
            invoiceCount: { $sum: 1 }
          }
        }
      ]),

      // Driver Performance
      User.aggregate([
        {
          $match: {
            role: 'driver',
            createdAt: { $lte: endDate }
          }
        },
        {
          $group: {
            _id: null,
            totalDrivers: { $sum: 1 },
            activeDrivers: {
              $sum: { $cond: [{ $eq: ['$status', 'active'] }, 1, 0] }
            },
            avgRating: { $avg: '$driverInfo.rating' },
            totalTrips: { $sum: '$driverInfo.totalTrips' }
          }
        }
      ]),

      // Fleet Statistics
      Truck.aggregate([
        {
          $match: {
            createdAt: { $lte: endDate }
          }
        },
        {
          $group: {
            _id: '$status',
            count: { $sum: 1 }
          }
        }
      ]),

      // Customer Statistics
      User.aggregate([
        {
          $match: {
            role: 'client',
            createdAt: { $gte: startDate, $lte: endDate }
          }
        },
        {
          $group: {
            _id: null,
            newCustomers: { $sum: 1 }
          }
        }
      ])
    ]);

    // Daily trends for charts
    const dailyTrends = await Shipment.aggregate([
      {
        $match: {
          createdAt: { $gte: startDate, $lte: endDate }
        }
      },
      {
        $group: {
          _id: {
            $dateToString: { format: '%Y-%m-%d', date: '$createdAt' }
          },
          shipments: { $sum: 1 },
          revenue: { $sum: '$pricing.total' }
        }
      },
      { $sort: { _id: 1 } }
    ]);

    res.json({
      success: true,
      data: {
        period: { start: startDate, end: endDate },
        summary: {
          shipments: shipmentStats[0] || { total: 0, delivered: 0, inTransit: 0, pending: 0 },
          revenue: revenueStats[0] || { totalRevenue: 0, totalPaid: 0, totalPending: 0 },
          drivers: driverStats[0] || { totalDrivers: 0, activeDrivers: 0, avgRating: 0 },
          fleet: fleetStats.reduce((acc, stat) => {
            acc[stat._id] = stat.count;
            return acc;
          }, {}),
          customers: customerStats[0] || { newCustomers: 0 }
        },
        trends: dailyTrends
      }
    });
  } catch (error) {
    logger.error('Dashboard analytics error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/analytics/performance
// @desc    Get performance analytics by various dimensions
// @access  Private (Admin, CEO)
router.get('/performance', protect, authorize('admin', 'ceo'), async (req, res) => {
  try {
    const { dimension = 'driver', period = '30days' } = req.query;
    
    const endDate = new Date();
    const startDate = new Date();
    startDate.setDate(startDate.getDate() - parseInt(period));

    let performanceData;

    switch (dimension) {
      case 'driver':
        performanceData = await Shipment.aggregate([
          {
            $match: {
              createdAt: { $gte: startDate, $lte: endDate },
              driver: { $exists: true }
            }
          },
          {
            $group: {
              _id: '$driver',
              totalShipments: { $sum: 1 },
              delivered: {
                $sum: { $cond: [{ $eq: ['$status', 'delivered'] }, 1, 0] }
              },
              onTimeDelivery: {
                $sum: {
                  $cond: [
                    { $lte: ['$delivery.actualDate', '$delivery.scheduledDate'] },
                    1,
                    0
                  ]
                }
              },
              totalRevenue: { $sum: '$pricing.total' },
              avgDeliveryTime: { $avg: '$delivery.actualDuration' }
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
              totalShipments: 1,
              delivered: 1,
              onTimeDelivery: 1,
              totalRevenue: 1,
              avgDeliveryTime: 1,
              onTimeRate: {
                $multiply: [
                  { $divide: ['$onTimeDelivery', '$delivered'] },
                  100
                ]
              }
            }
          },
          { $sort: { totalRevenue: -1 } },
          { $limit: 20 }
        ]);
        break;

      case 'route':
        performanceData = await Shipment.aggregate([
          {
            $match: {
              createdAt: { $gte: startDate, $lte: endDate }
            }
          },
          {
            $group: {
              _id: {
                from: '$pickup.address.city',
                to: '$delivery.address.city'
              },
              totalShipments: { $sum: 1 },
              avgPrice: { $avg: '$pricing.total' },
              avgDistance: { $avg: '$route.distance.value' },
              avgDuration: { $avg: '$route.duration.value' }
            }
          },
          { $sort: { totalShipments: -1 } },
          { $limit: 20 }
        ]);
        break;

      case 'customer':
        performanceData = await Shipment.aggregate([
          {
            $match: {
              createdAt: { $gte: startDate, $lte: endDate }
            }
          },
          {
            $group: {
              _id: '$createdBy',
              totalShipments: { $sum: 1 },
              totalSpent: { $sum: '$pricing.total' },
              avgShipmentValue: { $avg: '$pricing.total' }
            }
          },
          {
            $lookup: {
              from: 'users',
              localField: '_id',
              foreignField: '_id',
              as: 'customerInfo'
            }
          },
          { $unwind: '$customerInfo' },
          {
            $project: {
              customerName: { $concat: ['$customerInfo.firstName', ' ', '$customerInfo.lastName'] },
              company: '$customerInfo.company.name',
              totalShipments: 1,
              totalSpent: 1,
              avgShipmentValue: 1
            }
          },
          { $sort: { totalSpent: -1 } },
          { $limit: 20 }
        ]);
        break;

      default:
        performanceData = [];
    }

    res.json({
      success: true,
      dimension,
      period,
      data: performanceData
    });
  } catch (error) {
    logger.error('Performance analytics error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/analytics/fleet
// @desc    Get fleet utilization analytics
// @access  Private (Admin, CEO, Supervisor)
router.get('/fleet', protect, authorize('admin', 'ceo', 'supervisor'), async (req, res) => {
  try {
    const { period = '30days' } = req.query;
    
    const endDate = new Date();
    const startDate = new Date();
    startDate.setDate(startDate.getDate() - parseInt(period));

    // Fleet utilization by truck
    const utilization = await Shipment.aggregate([
      {
        $match: {
          createdAt: { $gte: startDate, $lte: endDate },
          truck: { $exists: true }
        }
      },
      {
        $group: {
          _id: '$truck',
          totalTrips: { $sum: 1 },
          totalDistance: { $sum: '$route.distance.value' },
          totalRevenue: { $sum: '$pricing.total' },
          avgLoad: { $avg: '$cargo.weight.value' }
        }
      },
      {
        $lookup: {
          from: 'trucks',
          localField: '_id',
          foreignField: '_id',
          as: 'truckInfo'
        }
      },
      { $unwind: '$truckInfo' },
      {
        $project: {
          plateNumber: '$truckInfo.plateNumber',
          make: '$truckInfo.make',
          model: '$truckInfo.model',
          type: '$truckInfo.type',
          totalTrips: 1,
          totalDistance: 1,
          totalRevenue: 1,
          avgLoad: 1,
          efficiency: {
            $divide: ['$totalRevenue', '$totalDistance']
          }
        }
      },
      { $sort: { totalRevenue: -1 } }
    ]);

    // Maintenance analytics
    const maintenanceStats = await Truck.aggregate([
      {
        $match: {
          'maintenance.history': { $exists: true, $ne: [] }
        }
      },
      {
        $project: {
          plateNumber: 1,
          maintenanceCost: {
            $sum: '$maintenance.history.cost'
          },
          maintenanceCount: {
            $size: '$maintenance.history'
          }
        }
      }
    ]);

    res.json({
      success: true,
      data: {
        utilization,
        maintenance: maintenanceStats
      }
    });
  } catch (error) {
    logger.error('Fleet analytics error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/analytics/temperature
// @desc    Get temperature monitoring analytics
// @access  Private (Admin, CEO, Supervisor, Client)
router.get('/temperature', protect, async (req, res) => {
  try {
    const { shipment, truck, period = '24hours' } = req.query;
    
    const endDate = new Date();
    const startDate = new Date();
    
    switch (period) {
      case '1hour':
        startDate.setHours(startDate.getHours() - 1);
        break;
      case '24hours':
        startDate.setDate(startDate.getDate() - 1);
        break;
      case '7days':
        startDate.setDate(startDate.getDate() - 7);
        break;
      default:
        startDate.setDate(startDate.getDate() - 1);
    }

    let query = {
      sensorType: 'temperature',
      recordedAt: { $gte: startDate, $lte: endDate }
    };

    if (shipment) query['relatedTo.shipment'] = shipment;
    if (truck) query['relatedTo.truck'] = truck;

    // Temperature statistics
    const stats = await SensorReading.aggregate([
      { $match: query },
      {
        $group: {
          _id: '$sensorId',
          avgTemp: { $avg: '$temperature.value' },
          minTemp: { $min: '$temperature.value' },
          maxTemp: { $max: '$temperature.value' },
          readings: { $sum: 1 },
          violations: {
            $sum: {
              $cond: [
                { $or: [
                  { $lt: ['$temperature.value', -25] },
                  { $gt: ['$temperature.value', 25] }
                ]},
                1,
                0
              ]
            }
          }
        }
      }
    ]);

    // Temperature trend
    const trend = await SensorReading.aggregate([
      { $match: query },
      {
        $group: {
          _id: {
            $dateToString: { format: '%Y-%m-%d %H:00', date: '$recordedAt' }
          },
          avgTemp: { $avg: '$temperature.value' },
          minTemp: { $min: '$temperature.value' },
          maxTemp: { $max: '$temperature.value' }
        }
      },
      { $sort: { _id: 1 } }
    ]);

    res.json({
      success: true,
      period: { start: startDate, end: endDate },
      stats,
      trend
    });
  } catch (error) {
    logger.error('Temperature analytics error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/analytics/reports
// @desc    Generate custom report
// @access  Private (Admin, CEO, Accountant)
router.post('/reports', protect, authorize('admin', 'ceo', 'accountant'), async (req, res) => {
  try {
    const { 
      reportType, 
      startDate, 
      endDate, 
      filters = {},
      format = 'json'
    } = req.body;

    // Generate report based on type
    let reportData;
    
    switch (reportType) {
      case 'financial':
        reportData = await generateFinancialReport(startDate, endDate, filters);
        break;
      case 'operational':
        reportData = await generateOperationalReport(startDate, endDate, filters);
        break;
      case 'fleet':
        reportData = await generateFleetReport(startDate, endDate, filters);
        break;
      case 'customer':
        reportData = await generateCustomerReport(startDate, endDate, filters);
        break;
      default:
        return res.status(400).json({ success: false, message: 'Invalid report type' });
    }

    res.json({
      success: true,
      reportType,
      generatedAt: new Date(),
      data: reportData
    });
  } catch (error) {
    logger.error('Report generation error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Helper functions for report generation
async function generateFinancialReport(startDate, endDate, filters) {
  return await Invoice.aggregate([
    {
      $match: {
        createdAt: { $gte: new Date(startDate), $lte: new Date(endDate) }
      }
    },
    {
      $group: {
        _id: {
          month: { $month: '$createdAt' },
          year: { $year: '$createdAt' }
        },
        totalRevenue: { $sum: '$total' },
        totalPaid: { $sum: '$amountPaid' },
        totalPending: { $sum: '$balance' },
        invoiceCount: { $sum: 1 }
      }
    },
    { $sort: { '_id.year': -1, '_id.month': -1 } }
  ]);
}

async function generateOperationalReport(startDate, endDate, filters) {
  return await Shipment.aggregate([
    {
      $match: {
        createdAt: { $gte: new Date(startDate), $lte: new Date(endDate) }
      }
    },
    {
      $group: {
        _id: '$status',
        count: { $sum: 1 },
        avgDeliveryTime: { $avg: '$delivery.actualDuration' },
        totalRevenue: { $sum: '$pricing.total' }
      }
    }
  ]);
}

async function generateFleetReport(startDate, endDate, filters) {
  return await Truck.aggregate([
    {
      $lookup: {
        from: 'shipments',
        localField: '_id',
        foreignField: 'truck',
        as: 'trips'
      }
    },
    {
      $project: {
        plateNumber: 1,
        make: 1,
        model: 1,
        status: 1,
        totalTrips: { $size: '$trips' },
        utilization: {
          $multiply: [
            { $divide: [{ $size: '$trips' }, 30] },
            100
          ]
        }
      }
    }
  ]);
}

async function generateCustomerReport(startDate, endDate, filters) {
  return await User.aggregate([
    {
      $match: {
        role: 'client',
        createdAt: { $gte: new Date(startDate), $lte: new Date(endDate) }
      }
    },
    {
      $lookup: {
        from: 'shipments',
        localField: '_id',
        foreignField: 'createdBy',
        as: 'shipments'
      }
    },
    {
      $project: {
        name: { $concat: ['$firstName', ' ', '$lastName'] },
        company: '$company.name',
        shipmentCount: { $size: '$shipments' },
        totalValue: { $sum: '$shipments.pricing.total' }
      }
    }
  ]);
}

// ML Routes
// @route   GET /api/v1/analytics/ml/models
// @desc    Get ML models
// @access  Private (Admin, Developer)
router.get('/ml/models', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const { MLModel } = require('../models/AdvancedAnalytics');
    const models = await MLModel.find({ company: req.user.company });

    res.json({
      success: true,
      count: models.length,
      data: models
    });

  } catch (error) {
    logger.error('Get ML models error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/analytics/ml/predict
// @desc    Make prediction
// @access  Private
router.post('/ml/predict', protect, async (req, res) => {
  try {
    const { modelId, input } = req.body;
    const { Prediction } = require('../models/AdvancedAnalytics');

    const predictionId = `PRED-${Date.now()}`;

    // TODO: Call ML service
    const prediction = await Prediction.create({
      predictionId,
      model: modelId,
      input,
      output: { predicted_value: 0 },
      company: req.user.company,
      'metadata.user': req.user.id
    });

    res.json({
      success: true,
      data: prediction
    });

  } catch (error) {
    logger.error('Prediction error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/analytics/anomalies
// @desc    Get detected anomalies
// @access  Private (Admin)
router.get('/anomalies', protect, authorize(['admin']), async (req, res) => {
  try {
    const { Anomaly } = require('../models/AdvancedAnalytics');
    const { status, severity } = req.query;

    let query = { company: req.user.company };
    if (status) query.status = status;
    if (severity) query.severity = severity;

    const anomalies = await Anomaly.find(query)
      .populate('model', 'name type')
      .sort({ detectedAt: -1 });

    res.json({
      success: true,
      count: anomalies.length,
      data: anomalies
    });

  } catch (error) {
    logger.error('Get anomalies error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/analytics/forecasts
// @desc    Get demand forecasts
// @access  Private
router.get('/forecasts', protect, async (req, res) => {
  try {
    const { DemandForecast } = require('../models/AdvancedAnalytics');
    const forecasts = await DemandForecast.find({
      company: req.user.company,
      status: 'ready'
    }).populate('model', 'name');

    res.json({
      success: true,
      count: forecasts.length,
      data: forecasts
    });

  } catch (error) {
    logger.error('Get forecasts error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/analytics/recommendations
// @desc    Get AI recommendations
// @access  Private
router.get('/recommendations', protect, async (req, res) => {
  try {
    const { Recommendation } = require('../models/AdvancedAnalytics');
    const recommendations = await Recommendation.find({
      company: req.user.company,
      status: { $in: ['pending', 'viewed'] }
    })
      .populate('model', 'name')
      .sort({ priority: -1, createdAt: -1 })
      .limit(20);

    res.json({
      success: true,
      count: recommendations.length,
      data: recommendations
    });

  } catch (error) {
    logger.error('Get recommendations error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
