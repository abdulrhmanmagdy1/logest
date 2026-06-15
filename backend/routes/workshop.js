//
/**
 * ============================================
 * 🔧 Workshop Routes - نظام إدهام
 * Fleet maintenance and workshop endpoints
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const Truck = require('../models/Truck');
const Maintenance = require('../models/Maintenance');
const User = require('../models/User');
const logger = require('../utils/logger');

// @route   GET /api/v1/workshop/dashboard
// @desc    Get workshop dashboard stats
// @access  Private (Workshop, Admin, Supervisor)
router.get('/dashboard', protect, authorize('workshop', 'admin', 'supervisor'), async (req, res) => {
  try {
    // Fleet health stats
    const fleetStats = await Truck.aggregate([
      {
        $group: {
          _id: '$status',
          count: { $sum: 1 }
        }
      }
    ]);

    // Pending maintenance requests
    const pendingRequests = await Maintenance.countDocuments({
      status: { $in: ['pending', 'approved'] }
    });

    // In progress maintenance
    const inProgress = await Maintenance.countDocuments({
      status: 'in_progress'
    });

    // Critical alerts (overdue or urgent)
    const criticalAlerts = await Maintenance.countDocuments({
      $or: [
        { priority: 'critical', status: { $nin: ['completed', 'cancelled'] } },
        { scheduledDate: { $lt: new Date() }, status: { $nin: ['completed', 'cancelled'] } }
      ]
    });

    // Parts inventory alerts
    const lowStockParts = await Maintenance.aggregate([
      { $unwind: '$partsUsed' },
      {
        $group: {
          _id: '$partsUsed.partId',
          name: { $first: '$partsUsed.name' },
          totalUsed: { $sum: '$partsUsed.quantity' }
        }
      }
    ]);

    // Recent requests
    const recentRequests = await Maintenance.find()
      .sort({ createdAt: -1 })
      .limit(10)
      .populate('truck', 'plateNumber make model')
      .populate('requestedBy', 'firstName lastName');

    res.json({
      success: true,
      data: {
        fleetHealth: fleetStats.reduce((acc, stat) => {
          acc[stat._id] = stat.count;
          return acc;
        }, {}),
        pendingRequests,
        inProgress,
        criticalAlerts,
        recentRequests,
        lowStockParts
      }
    });
  } catch (error) {
    logger.error('Get workshop dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/workshop/maintenance
// @desc    Get all maintenance records
// @access  Private (Workshop, Admin, Supervisor)
router.get('/maintenance', protect, authorize('workshop', 'admin', 'supervisor'), async (req, res) => {
  try {
    const { status, truck, priority, page = 1, limit = 20 } = req.query;
    
    let query = {};
    if (status) query.status = status;
    if (truck) query.truck = truck;
    if (priority) query.priority = priority;

    const records = await Maintenance.find(query)
      .populate('truck', 'plateNumber make model type year')
      .populate('requestedBy', 'firstName lastName')
      .populate('assignedTo', 'firstName lastName')
      .sort({ createdAt: -1 })
      .limit(limit * 1)
      .skip((page - 1) * limit);

    const count = await Maintenance.countDocuments(query);

    res.json({
      success: true,
      count: records.length,
      total: count,
      totalPages: Math.ceil(count / limit),
      currentPage: page,
      data: records
    });
  } catch (error) {
    logger.error('Get maintenance records error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/workshop/maintenance
// @desc    Create maintenance request
// @access  Private (Workshop, Driver, Admin, Supervisor)
router.post('/maintenance', protect, authorize('workshop', 'driver', 'admin', 'supervisor'), async (req, res) => {
  try {
    const {
      truck,
      type,
      category,
      description,
      priority,
      scheduledDate,
      estimatedCost,
      notes
    } = req.body;

    // Validate truck exists
    const truckExists = await Truck.findById(truck);
    if (!truckExists) {
      return res.status(404).json({ success: false, message: 'المركبة غير موجودة' });
    }

    const maintenance = await Maintenance.create({
      truck,
      type,
      category,
      description,
      priority: priority || 'normal',
      scheduledDate: scheduledDate ? new Date(scheduledDate) : new Date(),
      estimatedCost,
      notes,
      requestedBy: req.user.id,
      status: 'pending'
    });

    // Update truck status
    await Truck.findByIdAndUpdate(truck, { status: 'maintenance' });

    await maintenance.populate('truck requestedBy');

    res.status(201).json({
      success: true,
      message: 'تم إنشاء طلب الصيانة بنجاح',
      data: maintenance
    });
  } catch (error) {
    logger.error('Create maintenance request error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/workshop/maintenance/:id/assign
// @desc    Assign technician to maintenance
// @access  Private (Workshop, Admin, Supervisor)
router.put('/maintenance/:id/assign', protect, authorize('workshop', 'admin', 'supervisor'), async (req, res) => {
  try {
    const { technicianId } = req.body;

    const maintenance = await Maintenance.findByIdAndUpdate(
      req.params.id,
      {
        assignedTo: technicianId,
        status: 'in_progress',
        startedAt: new Date()
      },
      { new: true }
    );

    if (!maintenance) {
      return res.status(404).json({ success: false, message: 'طلب الصيانة غير موجود' });
    }

    await maintenance.populate('truck assignedTo');

    res.json({
      success: true,
      message: 'تم تعيين الفني بنجاح',
      data: maintenance
    });
  } catch (error) {
    logger.error('Assign technician error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/workshop/maintenance/:id/complete
// @desc    Complete maintenance
// @access  Private (Workshop, Admin, Supervisor)
router.put('/maintenance/:id/complete', protect, authorize('workshop', 'admin', 'supervisor'), async (req, res) => {
  try {
    const {
      actualCost,
      partsUsed,
      workDescription,
      nextMaintenanceDate,
      notes
    } = req.body;

    const maintenance = await Maintenance.findById(req.params.id);
    if (!maintenance) {
      return res.status(404).json({ success: false, message: 'طلب الصيانة غير موجود' });
    }

    // Update maintenance
    maintenance.status = 'completed';
    maintenance.completedAt = new Date();
    maintenance.actualCost = actualCost;
    maintenance.partsUsed = partsUsed || [];
    maintenance.workDescription = workDescription;
    maintenance.nextMaintenanceDate = nextMaintenanceDate ? new Date(nextMaintenanceDate) : null;
    maintenance.notes = notes;
    maintenance.completedBy = req.user.id;

    await maintenance.save();

    // Update truck status back to active
    await Truck.findByIdAndUpdate(maintenance.truck, { status: 'active' });

    // Update truck's last maintenance
    await Truck.findByIdAndUpdate(maintenance.truck, {
      'maintenance.lastService': new Date(),
      'maintenance.nextService': nextMaintenanceDate ? new Date(nextMaintenanceDate) : null
    });

    res.json({
      success: true,
      message: 'تم إكمال الصيانة بنجاح',
      data: maintenance
    });
  } catch (error) {
    logger.error('Complete maintenance error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/workshop/schedule
// @desc    Get maintenance schedule
// @access  Private (Workshop, Admin, Supervisor)
router.get('/schedule', protect, authorize('workshop', 'admin', 'supervisor'), async (req, res) => {
  try {
    const { startDate, endDate, technician } = req.query;
    
    let query = {
      scheduledDate: {
        $gte: startDate ? new Date(startDate) : new Date(),
        $lte: endDate ? new Date(endDate) : new Date(Date.now() + 30 * 24 * 60 * 60 * 1000)
      }
    };

    if (technician) query.assignedTo = technician;

    const schedule = await Maintenance.find(query)
      .populate('truck', 'plateNumber make model')
      .populate('assignedTo', 'firstName lastName')
      .sort({ scheduledDate: 1 });

    res.json({
      success: true,
      count: schedule.length,
      data: schedule
    });
  } catch (error) {
    logger.error('Get schedule error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/workshop/technicians
// @desc    Get available technicians
// @access  Private (Workshop, Admin, Supervisor)
router.get('/technicians', protect, authorize('workshop', 'admin', 'supervisor'), async (req, res) => {
  try {
    const technicians = await User.find({
      role: 'employee',
      'employeeInfo.department': 'maintenance',
      status: 'active'
    }).select('firstName lastName phone employeeInfo');

    // Get current workload for each technician
    const techniciansWithWorkload = await Promise.all(technicians.map(async (tech) => {
      const activeJobs = await Maintenance.countDocuments({
        assignedTo: tech._id,
        status: 'in_progress'
      });

      return {
        ...tech.toObject(),
        activeJobs,
        isAvailable: activeJobs < 3 // Available if less than 3 active jobs
      };
    }));

    res.json({
      success: true,
      count: techniciansWithWorkload.length,
      data: techniciansWithWorkload
    });
  } catch (error) {
    logger.error('Get technicians error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/workshop/alerts
// @desc    Get maintenance alerts
// @access  Private (Workshop, Admin, Supervisor)
router.get('/alerts', protect, authorize('workshop', 'admin', 'supervisor'), async (req, res) => {
  try {
    const alerts = [];

    // Overdue maintenance
    const overdue = await Maintenance.find({
      scheduledDate: { $lt: new Date() },
      status: { $nin: ['completed', 'cancelled'] }
    }).populate('truck', 'plateNumber make model');

    overdue.forEach(m => {
      alerts.push({
        type: 'overdue',
        severity: 'high',
        title: 'صيانة متأخرة',
        description: `المركبة ${m.truck.plateNumber} لديها صيانة متأخرة`,
        maintenanceId: m._id,
        truckId: m.truck._id,
        date: m.scheduledDate
      });
    });

    // Trucks needing maintenance soon
    const upcoming = await Truck.find({
      'maintenance.nextService': {
        $gte: new Date(),
        $lte: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000)
      },
      status: 'active'
    });

    upcoming.forEach(t => {
      alerts.push({
        type: 'upcoming',
        severity: 'medium',
        title: 'صيانة قادمة',
        description: `المركبة ${t.plateNumber} تحتاج صيانة خلال 7 أيام`,
        truckId: t._id,
        date: t.maintenance.nextService
      });
    });

    // Critical failures
    const critical = await Maintenance.find({
      priority: 'critical',
      status: { $nin: ['completed', 'cancelled'] }
    }).populate('truck', 'plateNumber make model');

    critical.forEach(m => {
      alerts.push({
        type: 'critical',
        severity: 'critical',
        title: 'عطل حرج',
        description: `المركبة ${m.truck.plateNumber}: ${m.description}`,
        maintenanceId: m._id,
        truckId: m.truck._id,
        date: m.createdAt
      });
    });

    res.json({
      success: true,
      count: alerts.length,
      data: alerts.sort((a, b) => {
        const severityOrder = { critical: 0, high: 1, medium: 2, low: 3 };
        return severityOrder[a.severity] - severityOrder[b.severity];
      })
    });
  } catch (error) {
    logger.error('Get alerts error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/workshop/trucks/:id/history
// @desc    Get maintenance history for a truck
// @access  Private (Workshop, Admin, Supervisor)
router.get('/trucks/:id/history', protect, authorize('workshop', 'admin', 'supervisor'), async (req, res) => {
  try {
    const history = await Maintenance.find({ truck: req.params.id })
      .populate('assignedTo', 'firstName lastName')
      .populate('completedBy', 'firstName lastName')
      .sort({ createdAt: -1 });

    const summary = await Maintenance.aggregate([
      { $match: { truck: new require('mongoose').Types.ObjectId(req.params.id) } },
      {
        $group: {
          _id: null,
          totalCost: { $sum: '$actualCost' },
          totalServices: { $sum: 1 },
          completedServices: {
            $sum: { $cond: [{ $eq: ['$status', 'completed'] }, 1, 0] }
          }
        }
      }
    ]);

    res.json({
      success: true,
      data: {
        history,
        summary: summary[0] || { totalCost: 0, totalServices: 0, completedServices: 0 }
      }
    });
  } catch (error) {
    logger.error('Get truck history error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
