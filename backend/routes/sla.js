//
/**
 * ============================================
 * ⏱️ SLA Management Routes - إدارة اتفاقيات الخدمة
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { SLAPolicy, SLAInstance, SLAReport } = require('../models/SLA');
const logger = require('../utils/logger');

// @route   GET /api/v1/sla/policies
// @desc    Get SLA policies
// @access  Private
router.get('/policies', protect, async (req, res) => {
  try {
    const { type, status } = req.query;

    let query = { company: req.user.company };
    if (type) query.type = type;
    if (status) query.status = status;

    const policies = await SLAPolicy.find(query)
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: policies.length,
      data: policies
    });

  } catch (error) {
    logger.error('Get SLA policies error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/sla/policies
// @desc    Create SLA policy
// @access  Private (Admin, Operations)
router.post('/policies', protect, authorize(['admin', 'supervisor', 'operations']), async (req, res) => {
  try {
    const policy = await SLAPolicy.create({
      ...req.body,
      company: req.user.company,
      createdBy: req.user.id
    });

    res.status(201).json({
      success: true,
      data: policy
    });

  } catch (error) {
    logger.error('Create SLA policy error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/sla/policies/:id
// @desc    Get policy details
// @access  Private
router.get('/policies/:id', protect, async (req, res) => {
  try {
    const policy = await SLAPolicy.findById(req.params.id);

    if (!policy) {
      return res.status(404).json({ success: false, message: 'Policy not found' });
    }

    res.json({
      success: true,
      data: policy
    });

  } catch (error) {
    logger.error('Get policy error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/sla/policies/:id
// @desc    Update SLA policy
// @access  Private (Admin, Operations)
router.put('/policies/:id', protect, authorize(['admin', 'supervisor', 'operations']), async (req, res) => {
  try {
    const policy = await SLAPolicy.findByIdAndUpdate(
      req.params.id,
      {
        ...req.body,
        updatedAt: new Date()
      },
      { new: true, runValidators: true }
    );

    if (!policy) {
      return res.status(404).json({ success: false, message: 'Policy not found' });
    }

    res.json({
      success: true,
      message: 'Policy updated',
      data: policy
    });

  } catch (error) {
    logger.error('Update policy error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/sla/policies/:id/activate
// @desc    Activate SLA policy
// @access  Private (Admin)
router.put('/policies/:id/activate', protect, authorize(['admin']), async (req, res) => {
  try {
    const policy = await SLAPolicy.findByIdAndUpdate(
      req.params.id,
      {
        status: 'active',
        effectiveDate: new Date(),
        updatedAt: new Date()
      },
      { new: true }
    );

    if (!policy) {
      return res.status(404).json({ success: false, message: 'Policy not found' });
    }

    res.json({
      success: true,
      message: 'Policy activated',
      data: policy
    });

  } catch (error) {
    logger.error('Activate policy error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// SLA Instances
// @route   GET /api/v1/sla/instances
// @desc    Get SLA instances
// @access  Private
router.get('/instances', protect, async (req, res) => {
  try {
    const { status, entity, priority } = req.query;

    let query = { company: req.user.company };
    if (status) query.status = status;
    if (entity) query['entity.type'] = entity;
    if (priority) query.priority = priority;

    const instances = await SLAInstance.find(query)
      .populate('policy', 'name type')
      .sort({ 'targets.deadline': 1 });

    res.json({
      success: true,
      count: instances.length,
      data: instances
    });

  } catch (error) {
    logger.error('Get SLA instances error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/sla/instances/:id
// @desc    Get instance details
// @access  Private
router.get('/instances/:id', protect, async (req, res) => {
  try {
    const instance = await SLAInstance.findById(req.params.id)
      .populate('policy', 'name targets')
      .populate('breaches.acknowledgedBy', 'firstName lastName')
      .populate('escalations.notifiedUsers', 'firstName lastName');

    if (!instance) {
      return res.status(404).json({ success: false, message: 'Instance not found' });
    }

    res.json({
      success: true,
      data: instance
    });

  } catch (error) {
    logger.error('Get instance error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/sla/instances/:id/acknowledge
// @desc    Acknowledge breach
// @access  Private
router.put('/instances/:id/acknowledge', protect, async (req, res) => {
  try {
    const { breachIndex, reason } = req.body;

    const instance = await SLAInstance.findById(req.params.id);
    if (!instance) {
      return res.status(404).json({ success: false, message: 'Instance not found' });
    }

    if (instance.breaches[breachIndex]) {
      instance.breaches[breachIndex].acknowledged = true;
      instance.breaches[breachIndex].acknowledgedBy = req.user.id;
      instance.breaches[breachIndex].acknowledgedAt = new Date();
      instance.breaches[breachIndex].reason = reason;
      await instance.save();
    }

    res.json({
      success: true,
      message: 'Breach acknowledged'
    });

  } catch (error) {
    logger.error('Acknowledge breach error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// SLA Reports
// @route   GET /api/v1/sla/reports
// @desc    Get SLA reports
// @access  Private
router.get('/reports', protect, async (req, res) => {
  try {
    const { policy, from, to } = req.query;

    let query = { company: req.user.company };
    if (policy) query.policy = policy;
    if (from && to) {
      query['period.start'] = { $gte: new Date(from) };
      query['period.end'] = { $lte: new Date(to) };
    }

    const reports = await SLAReport.find(query)
      .populate('policy', 'name type')
      .sort({ 'period.start': -1 });

    res.json({
      success: true,
      count: reports.length,
      data: reports
    });

  } catch (error) {
    logger.error('Get SLA reports error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Dashboard
// @route   GET /api/v1/sla/dashboard
// @desc    SLA dashboard
// @access  Private
router.get('/dashboard', protect, async (req, res) => {
  try {
    const today = new Date();
    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

    const stats = await Promise.all([
      // Active policies
      SLAPolicy.countDocuments({
        company: req.user.company,
        status: 'active'
      }),

      // Active SLA instances
      SLAInstance.countDocuments({
        company: req.user.company,
        status: 'active'
      }),

      // At risk instances
      SLAInstance.countDocuments({
        company: req.user.company,
        status: 'active',
        'targets.status': 'at_risk'
      }),

      // Breached instances this month
      SLAInstance.countDocuments({
        company: req.user.company,
        'breaches.breachedAt': { $gte: startOfMonth }
      }),

      // Compliance by policy
      SLAInstance.aggregate([
        {
          $match: {
            company: req.user.company._id,
            createdAt: { $gte: startOfMonth }
          }
        },
        {
          $group: {
            _id: '$policy',
            total: { $sum: 1 },
            met: {
              $sum: { $cond: [{ $eq: ['$status', 'met'] }, 1, 0] }
            },
            breached: {
              $sum: { $cond: [{ $eq: ['$status', 'breached'] }, 1, 0] }
            }
          }
        },
        {
          $lookup: {
            from: 'slapolicies',
            localField: '_id',
            foreignField: '_id',
            as: 'policy'
          }
        },
        { $unwind: '$policy' },
        {
          $project: {
            name: '$policy.name',
            type: '$policy.type',
            total: 1,
            met: 1,
            breached: 1,
            compliance: { $multiply: [{ $divide: ['$met', '$total'] }, 100] }
          }
        }
      ]),

      // Today's breaches
      SLAInstance.find({
        company: req.user.company,
        'breaches.breachedAt': {
          $gte: new Date(today.setHours(0, 0, 0, 0))
        }
      })
        .populate('policy', 'name')
        .sort({ 'breaches.breachedAt': -1 })
        .limit(10)
    ]);

    res.json({
      success: true,
      data: {
        activePolicies: stats[0],
        activeInstances: stats[1],
        atRisk: stats[2],
        monthlyBreaches: stats[3],
        complianceByPolicy: stats[4],
        todayBreaches: stats[5]
      }
    });

  } catch (error) {
    logger.error('SLA dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
