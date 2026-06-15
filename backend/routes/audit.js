//
/**
 * ============================================
 * 📋 Audit Routes - نظام السجلات والتدقيق
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const AuditLog = require('../models/AuditLog');
const logger = require('../utils/logger');

// @route   GET /api/v1/audit
// @desc    Get audit logs
// @access  Private (Admin, Compliance)
router.get('/', protect, authorize(['admin', 'compliance']), async (req, res) => {
  try {
    const { 
      action, 
      entity, 
      user, 
      from, 
      to, 
      severity, 
      page = 1, 
      limit = 100,
      search
    } = req.query;

    let query = { company: req.user.company };
    
    if (action) query.action = action;
    if (entity) query.entity = entity;
    if (user) query.performedBy.user = user;
    if (severity) query.severity = severity;
    
    if (from && to) {
      query.timestamp = {
        $gte: new Date(from),
        $lte: new Date(to)
      };
    }
    
    if (search) {
      query.$or = [
        { 'details.description': { $regex: search, $options: 'i' } },
        { 'performedBy.name': { $regex: search, $options: 'i' } },
        { entityId: { $regex: search, $options: 'i' } }
      ];
    }

    const total = await AuditLog.countDocuments(query);
    
    const logs = await AuditLog.find(query)
      .populate('performedBy.user', 'firstName lastName email role')
      .sort({ timestamp: -1 })
      .skip((page - 1) * limit)
      .limit(limit * 1);

    res.json({
      success: true,
      count: logs.length,
      pagination: {
        page: parseInt(page),
        pages: Math.ceil(total / limit),
        total,
        limit: parseInt(limit)
      },
      data: logs
    });

  } catch (error) {
    logger.error('Get audit logs error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/audit/:id
// @desc    Get audit log details
// @access  Private (Admin, Compliance)
router.get('/:id', protect, authorize(['admin', 'compliance']), async (req, res) => {
  try {
    const log = await AuditLog.findOne({
      _id: req.params.id,
      company: req.user.company
    }).populate('performedBy.user', 'firstName lastName email role department');

    if (!log) {
      return res.status(404).json({ success: false, message: 'Audit log not found' });
    }

    res.json({
      success: true,
      data: log
    });

  } catch (error) {
    logger.error('Get audit log error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/audit/stats/summary
// @desc    Get audit statistics
// @access  Private (Admin, Compliance)
router.get('/stats/summary', protect, authorize(['admin', 'compliance']), async (req, res) => {
  try {
    const { from, to } = req.query;
    
    let dateQuery = { company: req.user.company };
    if (from && to) {
      dateQuery.timestamp = {
        $gte: new Date(from),
        $lte: new Date(to)
      };
    } else {
      // Default to last 30 days
      dateQuery.timestamp = {
        $gte: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000)
      };
    }

    const stats = await Promise.all([
      // Total actions
      AuditLog.countDocuments(dateQuery),

      // Actions by type
      AuditLog.aggregate([
        { $match: dateQuery },
        { $group: { _id: '$action', count: { $sum: 1 } } },
        { $sort: { count: -1 } }
      ]),

      // Actions by entity
      AuditLog.aggregate([
        { $match: dateQuery },
        { $group: { _id: '$entity', count: { $sum: 1 } } },
        { $sort: { count: -1 } }
      ]),

      // Top users
      AuditLog.aggregate([
        { $match: dateQuery },
        {
          $group: {
            _id: '$performedBy.user',
            count: { $sum: 1 },
            name: { $first: '$performedBy.name' }
          }
        },
        { $sort: { count: -1 } },
        { $limit: 10 }
      ]),

      // Security events
      AuditLog.countDocuments({
        ...dateQuery,
        $or: [
          { action: { $in: ['LOGIN', 'LOGOUT'] } },
          { severity: { $in: ['high', 'critical'] } }
        ]
      }),

      // Hourly distribution
      AuditLog.aggregate([
        { $match: dateQuery },
        {
          $group: {
            _id: { $hour: '$timestamp' },
            count: { $sum: 1 }
          }
        },
        { $sort: { _id: 1 } }
      ])
    ]);

    res.json({
      success: true,
      data: {
        total: stats[0],
        byAction: stats[1],
        byEntity: stats[2],
        topUsers: stats[3],
        securityEvents: stats[4],
        hourlyDistribution: stats[5]
      }
    });

  } catch (error) {
    logger.error('Get audit stats error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/audit/stats/user/:userId
// @desc    Get user activity audit
// @access  Private (Admin, Compliance)
router.get('/stats/user/:userId', protect, authorize(['admin', 'compliance']), async (req, res) => {
  try {
    const { from, to } = req.query;
    
    let dateQuery = {
      company: req.user.company,
      'performedBy.user': req.params.userId
    };
    
    if (from && to) {
      dateQuery.timestamp = {
        $gte: new Date(from),
        $lte: new Date(to)
      };
    }

    const stats = await Promise.all([
      // Total actions
      AuditLog.countDocuments(dateQuery),

      // Actions by type
      AuditLog.aggregate([
        { $match: dateQuery },
        { $group: { _id: '$action', count: { $sum: 1 } } },
        { $sort: { count: -1 } }
      ]),

      // Session activity
      AuditLog.countDocuments({
        ...dateQuery,
        action: { $in: ['LOGIN', 'LOGOUT'] }
      }),

      // Recent activity
      AuditLog.find(dateQuery)
        .select('action entity entityId timestamp details')
        .sort({ timestamp: -1 })
        .limit(50)
    ]);

    res.json({
      success: true,
      data: {
        userId: req.params.userId,
        total: stats[0],
        byAction: stats[1],
        sessionActivity: stats[2],
        recentActivity: stats[3]
      }
    });

  } catch (error) {
    logger.error('Get user audit error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/audit/stats/entity/:entity/:entityId
// @desc    Get entity audit trail
// @access  Private (Admin, Compliance)
router.get('/stats/entity/:entity/:entityId', protect, authorize(['admin', 'compliance']), async (req, res) => {
  try {
    const logs = await AuditLog.find({
      company: req.user.company,
      entity: req.params.entity,
      entityId: req.params.entityId
    })
      .populate('performedBy.user', 'firstName lastName email')
      .sort({ timestamp: -1 });

    res.json({
      success: true,
      count: logs.length,
      data: logs
    });

  } catch (error) {
    logger.error('Get entity audit error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/audit/export
// @desc    Export audit logs
// @access  Private (Admin, Compliance)
router.post('/export', protect, authorize(['admin', 'compliance']), async (req, res) => {
  try {
    const { from, to, format = 'csv', filters } = req.body;

    let query = { company: req.user.company };
    
    if (from && to) {
      query.timestamp = {
        $gte: new Date(from),
        $lte: new Date(to)
      };
    }

    if (filters) {
      if (filters.action) query.action = filters.action;
      if (filters.entity) query.entity = filters.entity;
      if (filters.user) query['performedBy.user'] = filters.user;
    }

    // Create export job
    const exportId = `AUDIT-EXPORT-${Date.now()}`;

    // TODO: Process export asynchronously

    res.json({
      success: true,
      message: 'Audit export started',
      data: {
        exportId,
        status: 'processing',
        format,
        estimatedRecords: await AuditLog.countDocuments(query)
      }
    });

  } catch (error) {
    logger.error('Export audit error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/audit/compliance/report
// @desc    Generate compliance report
// @access  Private (Admin, Compliance)
router.get('/compliance/report', protect, authorize(['admin', 'compliance']), async (req, res) => {
  try {
    const { from, to, type = 'gdpr' } = req.query;
    
    let dateQuery = { company: req.user.company };
    if (from && to) {
      dateQuery.timestamp = {
        $gte: new Date(from),
        $lte: new Date(to)
      };
    } else {
      dateQuery.timestamp = {
        $gte: new Date(Date.now() - 90 * 24 * 60 * 60 * 1000)
      };
    }

    const report = await Promise.all([
      // Data access events
      AuditLog.countDocuments({
        ...dateQuery,
        action: { $in: ['READ', 'EXPORT', 'DOWNLOAD', 'SHARE'] }
      }),

      // Data modification events
      AuditLog.countDocuments({
        ...dateQuery,
        action: { $in: ['CREATE', 'UPDATE', 'DELETE'] }
      }),

      // Access denied events
      AuditLog.countDocuments({
        ...dateQuery,
        'details.result': 'denied'
      }),

      // Failed authentication
      AuditLog.countDocuments({
        ...dateQuery,
        action: 'LOGIN',
        'details.success': false
      }),

      // Administrative actions
      AuditLog.find({
        ...dateQuery,
        'performedBy.role': { $in: ['admin', 'super_admin'] }
      })
        .select('action entity timestamp performedBy details')
        .sort({ timestamp: -1 })
        .limit(100)
    ]);

    res.json({
      success: true,
      data: {
        period: { from: dateQuery.timestamp.$gte, to: to || new Date() },
        type,
        summary: {
          dataAccessEvents: report[0],
          dataModificationEvents: report[1],
          accessDeniedEvents: report[2],
          failedAuthentications: report[3]
        },
        administrativeActions: report[4]
      }
    });

  } catch (error) {
    logger.error('Get compliance report error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
