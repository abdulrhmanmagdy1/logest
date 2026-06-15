//
/**
 * ============================================
 * 📊 Monitoring Routes - المراقبة والرصد
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { PerformanceMetric, LogEntry, AlertRule, AlertInstance, Trace, UptimeCheck } = require('../models/Monitoring');
const logger = require('../utils/logger');

// Performance Metrics
// @route   GET /api/v1/monitoring/metrics
// @desc    Get performance metrics
// @access  Private (Admin)
router.get('/metrics', protect, authorize(['admin']), async (req, res) => {
  try {
    const { category, name, from, to } = req.query;

    let query = { company: req.user.company };
    if (category) query.category = category;
    if (name) query.name = name;
    if (from && to) {
      query.timestamp = {
        $gte: new Date(from),
        $lte: new Date(to)
      };
    }

    const metrics = await PerformanceMetric.find(query)
      .sort({ timestamp: -1 })
      .limit(1000);

    res.json({
      success: true,
      count: metrics.length,
      data: metrics
    });

  } catch (error) {
    logger.error('Get metrics error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/monitoring/metrics/aggregate
// @desc    Get aggregated metrics
// @access  Private (Admin)
router.get('/metrics/aggregate', protect, authorize(['admin']), async (req, res) => {
  try {
    const { category, name, from, to, interval = '1h' } = req.query;

    const aggregation = await PerformanceMetric.aggregate([
      {
        $match: {
          company: req.user.company._id,
          category: category || { $exists: true },
          name: name || { $exists: true },
          timestamp: {
            $gte: new Date(from || Date.now() - 24 * 60 * 60 * 1000),
            $lte: new Date(to || Date.now())
          }
        }
      },
      {
        $group: {
          _id: {
            category: '$category',
            name: '$name',
            interval: {
              $dateTrunc: {
                date: '$timestamp',
                unit: interval.replace(/\d+/, ''),
                binSize: parseInt(interval) || 1
              }
            }
          },
          avg: { $avg: '$value' },
          min: { $min: '$value' },
          max: { $max: '$value' },
          sum: { $sum: '$value' },
          count: { $sum: 1 },
          p95: { $percentile: { p: [0.95], input: '$value', method: 'approximate' } },
          p99: { $percentile: { p: [0.99], input: '$value', method: 'approximate' } }
        }
      },
      { $sort: { '_id.interval': 1 } }
    ]);

    res.json({
      success: true,
      data: aggregation
    });

  } catch (error) {
    logger.error('Aggregate metrics error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Logs
// @route   GET /api/v1/monitoring/logs
// @desc    Get system logs
// @access  Private (Admin)
router.get('/logs', protect, authorize(['admin']), async (req, res) => {
  try {
    const { level, service, from, to, search, page = 1, limit = 100 } = req.query;

    let query = { company: req.user.company };
    if (level) query.level = level;
    if (service) query.service = service;
    if (from && to) {
      query.timestamp = {
        $gte: new Date(from),
        $lte: new Date(to)
      };
    }
    if (search) {
      query.$or = [
        { message: { $regex: search, $options: 'i' } },
        { 'context.traceId': search },
        { 'context.requestId': search }
      ];
    }

    const logs = await LogEntry.find(query)
      .sort({ timestamp: -1 })
      .limit(limit * 1)
      .skip((page - 1) * limit);

    const count = await LogEntry.countDocuments(query);

    res.json({
      success: true,
      count,
      pagination: {
        page: parseInt(page),
        pages: Math.ceil(count / limit),
        total: count
      },
      data: logs
    });

  } catch (error) {
    logger.error('Get logs error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/monitoring/logs
// @desc    Create log entry (for external services)
// @access  Private (with API key)
router.post('/logs', async (req, res) => {
  try {
    // TODO: Verify API key
    const log = await LogEntry.create({
      ...req.body,
      timestamp: new Date()
    });

    res.status(201).json({
      success: true,
      data: log
    });

  } catch (error) {
    logger.error('Create log error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Alerts
// @route   GET /api/v1/monitoring/alerts/rules
// @desc    Get alert rules
// @access  Private (Admin)
router.get('/alerts/rules', protect, authorize(['admin']), async (req, res) => {
  try {
    const { enabled } = req.query;

    let query = { company: req.user.company };
    if (enabled !== undefined) query.enabled = enabled === 'true';

    const rules = await AlertRule.find(query)
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: rules.length,
      data: rules
    });

  } catch (error) {
    logger.error('Get alert rules error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/monitoring/alerts/rules
// @desc    Create alert rule
// @access  Private (Admin)
router.post('/alerts/rules', protect, authorize(['admin']), async (req, res) => {
  try {
    const ruleId = `ALERT-${Date.now()}`;

    const rule = await AlertRule.create({
      ...req.body,
      ruleId,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: rule
    });

  } catch (error) {
    logger.error('Create alert rule error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/monitoring/alerts/instances
// @desc    Get alert instances
// @access  Private (Admin)
router.get('/alerts/instances', protect, authorize(['admin']), async (req, res) => {
  try {
    const { status, severity } = req.query;

    let query = { company: req.user.company };
    if (status) query.status = status;
    if (severity) query.severity = severity;

    const alerts = await AlertInstance.find(query)
      .populate('rule', 'name condition')
      .populate('acknowledgedBy', 'firstName lastName')
      .sort({ startedAt: -1 });

    res.json({
      success: true,
      count: alerts.length,
      data: alerts
    });

  } catch (error) {
    logger.error('Get alert instances error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/monitoring/alerts/:id/acknowledge
// @desc    Acknowledge alert
// @access  Private (Admin)
router.post('/alerts/:id/acknowledge', protect, authorize(['admin']), async (req, res) => {
  try {
    const { note } = req.body;

    const alert = await AlertInstance.findOneAndUpdate(
      { _id: req.params.id, company: req.user.company },
      {
        status: 'acknowledged',
        acknowledgedBy: req.user.id,
        acknowledgedAt: new Date(),
        acknowledgedNote: note
      },
      { new: true }
    );

    if (!alert) {
      return res.status(404).json({ success: false, message: 'Alert not found' });
    }

    res.json({
      success: true,
      data: alert
    });

  } catch (error) {
    logger.error('Acknowledge alert error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/monitoring/alerts/:id/resolve
// @desc    Resolve alert
// @access  Private (Admin)
router.post('/alerts/:id/resolve', protect, authorize(['admin']), async (req, res) => {
  try {
    const alert = await AlertInstance.findOneAndUpdate(
      { _id: req.params.id, company: req.user.company },
      {
        status: 'resolved',
        resolvedBy: req.user.id,
        resolvedAt: new Date()
      },
      { new: true }
    );

    if (!alert) {
      return res.status(404).json({ success: false, message: 'Alert not found' });
    }

    res.json({
      success: true,
      data: alert
    });

  } catch (error) {
    logger.error('Resolve alert error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Traces
// @route   GET /api/v1/monitoring/traces
// @desc    Get distributed traces
// @access  Private (Admin, Developer)
router.get('/traces', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const { service, from, to, status } = req.query;

    let query = { company: req.user.company };
    if (service) query.services = service;
    if (status) query.status = status;
    if (from && to) {
      query.timestamp = {
        $gte: new Date(from),
        $lte: new Date(to)
      };
    }

    const traces = await Trace.find(query)
      .sort({ timestamp: -1 })
      .limit(100);

    res.json({
      success: true,
      count: traces.length,
      data: traces
    });

  } catch (error) {
    logger.error('Get traces error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/monitoring/traces/:traceId
// @desc    Get trace details
// @access  Private (Admin, Developer)
router.get('/traces/:traceId', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const trace = await Trace.findOne({
      traceId: req.params.traceId,
      company: req.user.company
    });

    if (!trace) {
      return res.status(404).json({ success: false, message: 'Trace not found' });
    }

    res.json({
      success: true,
      data: trace
    });

  } catch (error) {
    logger.error('Get trace error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Uptime Checks
// @route   GET /api/v1/monitoring/uptime
// @desc    Get uptime checks
// @access  Private (Admin)
router.get('/uptime', protect, authorize(['admin']), async (req, res) => {
  try {
    const { status } = req.query;

    let query = { company: req.user.company };
    if (status) query.status = status;

    const checks = await UptimeCheck.find(query)
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: checks.length,
      data: checks
    });

  } catch (error) {
    logger.error('Get uptime checks error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/monitoring/uptime
// @desc    Create uptime check
// @access  Private (Admin)
router.post('/uptime', protect, authorize(['admin']), async (req, res) => {
  try {
    const checkId = `UP-${Date.now()}`;

    const check = await UptimeCheck.create({
      ...req.body,
      checkId,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: check
    });

  } catch (error) {
    logger.error('Create uptime check error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/monitoring/uptime/:id/check
// @desc    Run manual uptime check
// @access  Private (Admin)
router.post('/uptime/:id/check', protect, authorize(['admin']), async (req, res) => {
  try {
    const check = await UptimeCheck.findOne({
      _id: req.params.id,
      company: req.user.company
    });

    if (!check) {
      return res.status(404).json({ success: false, message: 'Check not found' });
    }

    // TODO: Run manual check

    res.json({
      success: true,
      message: 'Uptime check triggered'
    });

  } catch (error) {
    logger.error('Manual uptime check error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Dashboard
// @route   GET /api/v1/monitoring/dashboard
// @desc    Monitoring dashboard
// @access  Private (Admin)
router.get('/dashboard', protect, authorize(['admin']), async (req, res) => {
  try {
    const now = new Date();
    const last24h = new Date(now - 24 * 60 * 60 * 1000);
    const last7d = new Date(now - 7 * 24 * 60 * 60 * 1000);

    const stats = await Promise.all([
      // Active alerts
      AlertInstance.countDocuments({
        company: req.user.company,
        status: { $in: ['firing', 'acknowledged'] }
      }),

      // Alerts by severity
      AlertInstance.aggregate([
        {
          $match: {
            company: req.user.company._id,
            status: { $in: ['firing', 'acknowledged'] }
          }
        },
        { $group: { _id: '$severity', count: { $sum: 1 } } }
      ]),

      // Error logs last 24h
      LogEntry.countDocuments({
        company: req.user.company,
        level: { $in: ['error', 'fatal'] },
        timestamp: { $gte: last24h }
      }),

      // Down services
      UptimeCheck.countDocuments({
        company: req.user.company,
        status: 'down'
      }),

      // Services status
      UptimeCheck.find({ company: req.user.company })
        .select('name status uptime lastCheck responseTime')
        .sort({ status: 1 }),

      // Top errors
      LogEntry.aggregate([
        {
          $match: {
            company: req.user.company._id,
            level: { $in: ['error', 'fatal'] },
            timestamp: { $gte: last24h }
          }
        },
        {
          $group: {
            _id: { service: '$service', message: '$message' },
            count: { $sum: 1 },
            lastOccurrence: { $max: '$timestamp' }
          }
        },
        { $sort: { count: -1 } },
        { $limit: 10 }
      ])
    ]);

    res.json({
      success: true,
      data: {
        alerts: {
          active: stats[0],
          bySeverity: stats[1]
        },
        logs: {
          errors24h: stats[2]
        },
        uptime: {
          downServices: stats[3],
          services: stats[4]
        },
        topErrors: stats[5]
      }
    });

  } catch (error) {
    logger.error('Monitoring dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
