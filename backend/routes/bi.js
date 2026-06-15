//
/**
 * ============================================
 * 📊 Business Intelligence Routes - الذكاء التجاري
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { Dashboard, Report, KPI, DataSource } = require('../models/BusinessIntelligence');
const logger = require('../utils/logger');

// Dashboards
// @route   GET /api/v1/bi/dashboards
// @desc    Get dashboards
// @access  Private
router.get('/dashboards', protect, async (req, res) => {
  try {
    const { category, type } = req.query;

    let query = { company: req.user.company };
    if (category) query.category = category;
    if (type) query.type = type;

    // Filter by access permissions
    query.$or = [
      { 'access.visibility': 'public' },
      { 'access.allowedUsers': req.user.id },
      { 'access.allowedRoles': req.user.role },
      { createdBy: req.user.id }
    ];

    const dashboards = await Dashboard.find(query)
      .populate('createdBy', 'firstName lastName')
      .sort({ updatedAt: -1 });

    res.json({
      success: true,
      count: dashboards.length,
      data: dashboards
    });

  } catch (error) {
    logger.error('Get dashboards error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/bi/dashboards
// @desc    Create dashboard
// @access  Private
router.post('/dashboards', protect, async (req, res) => {
  try {
    const dashboard = await Dashboard.create({
      ...req.body,
      company: req.user.company,
      createdBy: req.user.id
    });

    res.status(201).json({
      success: true,
      data: dashboard
    });

  } catch (error) {
    logger.error('Create dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/bi/dashboards/:id
// @desc    Get dashboard
// @access  Private
router.get('/dashboards/:id', protect, async (req, res) => {
  try {
    const dashboard = await Dashboard.findById(req.params.id);

    if (!dashboard) {
      return res.status(404).json({ success: false, message: 'Dashboard not found' });
    }

    res.json({
      success: true,
      data: dashboard
    });

  } catch (error) {
    logger.error('Get dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/bi/dashboards/:id
// @desc    Update dashboard
// @access  Private
router.put('/dashboards/:id', protect, async (req, res) => {
  try {
    const dashboard = await Dashboard.findByIdAndUpdate(
      req.params.id,
      {
        ...req.body,
        updatedAt: new Date()
      },
      { new: true, runValidators: true }
    );

    if (!dashboard) {
      return res.status(404).json({ success: false, message: 'Dashboard not found' });
    }

    res.json({
      success: true,
      message: 'Dashboard updated',
      data: dashboard
    });

  } catch (error) {
    logger.error('Update dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Reports
// @route   GET /api/v1/bi/reports
// @desc    Get reports
// @access  Private
router.get('/reports', protect, async (req, res) => {
  try {
    const { category, type } = req.query;

    let query = { company: req.user.company };
    if (category) query.category = category;
    if (type) query.type = type;

    const reports = await Report.find(query)
      .populate('createdBy', 'firstName lastName')
      .sort({ updatedAt: -1 });

    res.json({
      success: true,
      count: reports.length,
      data: reports
    });

  } catch (error) {
    logger.error('Get reports error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/bi/reports
// @desc    Create report
// @access  Private
router.post('/reports', protect, async (req, res) => {
  try {
    const report = await Report.create({
      ...req.body,
      company: req.user.company,
      createdBy: req.user.id
    });

    res.status(201).json({
      success: true,
      data: report
    });

  } catch (error) {
    logger.error('Create report error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/bi/reports/:id/execute
// @desc    Execute report
// @access  Private
router.post('/reports/:id/execute', protect, async (req, res) => {
  try {
    const { parameters } = req.body;

    const report = await Report.findById(req.params.id);
    if (!report) {
      return res.status(404).json({ success: false, message: 'Report not found' });
    }

    // TODO: Execute report query and return results
    // For now, return mock response

    res.json({
      success: true,
      message: 'Report executed',
      data: {
        report: report.name,
        executedAt: new Date(),
        parameters,
        results: [] // Would contain actual query results
      }
    });

  } catch (error) {
    logger.error('Execute report error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// KPIs
// @route   GET /api/v1/bi/kpis
// @desc    Get KPIs
// @access  Private
router.get('/kpis', protect, async (req, res) => {
  try {
    const { category, status } = req.query;

    let query = { company: req.user.company, isActive: true };
    if (category) query.category = category;
    if (status) query.status = status;

    const kpis = await KPI.find(query).sort({ category: 1, name: 1 });

    res.json({
      success: true,
      count: kpis.length,
      data: kpis
    });

  } catch (error) {
    logger.error('Get KPIs error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/bi/kpis
// @desc    Create KPI
// @access  Private (Admin, Analyst)
router.post('/kpis', protect, authorize(['admin', 'analyst']), async (req, res) => {
  try {
    const kpiCode = `KPI-${Date.now().toString().slice(-6)}`;

    const kpi = await KPI.create({
      ...req.body,
      code: kpiCode,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: kpi
    });

  } catch (error) {
    logger.error('Create KPI error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/bi/kpis/:id/calculate
// @desc    Calculate KPI value
// @access  Private
router.put('/kpis/:id/calculate', protect, async (req, res) => {
  try {
    const kpi = await KPI.findById(req.params.id);
    if (!kpi) {
      return res.status(404).json({ success: false, message: 'KPI not found' });
    }

    // TODO: Calculate actual KPI value based on formula
    // For now, just update with sample logic

    const newValue = Math.floor(Math.random() * 100);
    const previousValue = kpi.currentValue || 0;
    const change = previousValue !== 0 ? ((newValue - previousValue) / previousValue) * 100 : 0;

    kpi.previousValue = previousValue;
    kpi.currentValue = newValue;
    kpi.change = change;
    kpi.trend = change > 0 ? 'up' : change < 0 ? 'down' : 'stable';

    // Determine status based on thresholds
    if (kpi.thresholds) {
      if (kpi.currentValue >= kpi.thresholds.excellent) {
        kpi.status = 'excellent';
      } else if (kpi.currentValue >= kpi.thresholds.good) {
        kpi.status = 'good';
      } else if (kpi.currentValue >= kpi.thresholds.warning) {
        kpi.status = 'warning';
      } else {
        kpi.status = 'critical';
      }
    }

    kpi.history.push({
      period: new Date().toISOString().slice(0, 7), // YYYY-MM
      value: newValue,
      target: kpi.target?.value,
      calculatedAt: new Date()
    });

    kpi.updatedAt = new Date();
    await kpi.save();

    res.json({
      success: true,
      message: 'KPI calculated',
      data: kpi
    });

  } catch (error) {
    logger.error('Calculate KPI error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Data Sources
// @route   GET /api/v1/bi/data-sources
// @desc    Get data sources
// @access  Private (Admin, Developer)
router.get('/data-sources', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const sources = await DataSource.find({ company: req.user.company });

    res.json({
      success: true,
      count: sources.length,
      data: sources
    });

  } catch (error) {
    logger.error('Get data sources error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Dashboard
// @route   GET /api/v1/bi/dashboard
// @desc    BI dashboard overview
// @access  Private
router.get('/dashboard', protect, async (req, res) => {
  try {
    const today = new Date();
    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

    const stats = await Promise.all([
      // Total dashboards
      Dashboard.countDocuments({ company: req.user.company }),

      // Total reports
      Report.countDocuments({ company: req.user.company }),

      // Active KPIs
      KPI.countDocuments({ company: req.user.company, isActive: true }),

      // KPIs by status
      KPI.aggregate([
        { $match: { company: req.user.company._id } },
        { $group: { _id: '$status', count: { $sum: 1 } } }
      ]),

      // KPIs by category
      KPI.aggregate([
        { $match: { company: req.user.company._id } },
        {
          $group: {
            _id: '$category',
            count: { $sum: 1 },
            avgValue: { $avg: '$currentValue' }
          }
        }
      ])
    ]);

    res.json({
      success: true,
      data: {
        dashboards: stats[0],
        reports: stats[1],
        activeKPIs: stats[2],
        kpiStatus: stats[3],
        kpiByCategory: stats[4]
      }
    });

  } catch (error) {
    logger.error('BI dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
