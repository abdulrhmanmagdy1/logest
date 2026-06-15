//
/**
 * ============================================
 * ✅ Quality Management Routes - إدارة الجودة
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { QualityIncident, Audit, KPI, SOP } = require('../models/Quality');
const logger = require('../utils/logger');

// @route   GET /api/v1/quality/incidents
// @desc    Get quality incidents
// @access  Private (Quality, Admin, Supervisor)
router.get('/incidents', protect, authorize(['quality', 'admin', 'supervisor']), async (req, res) => {
  try {
    const { status, severity, type } = req.query;
    
    let query = { company: req.user.company };
    if (status) query.status = status;
    if (severity) query.severity = severity;
    if (type) query.type = type;

    const incidents = await QualityIncident.find(query)
      .populate('relatedShipment', 'trackingNumber')
      .populate('relatedCustomer', 'firstName lastName companyName')
      .populate('createdBy', 'firstName lastName')
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: incidents.length,
      data: incidents
    });

  } catch (error) {
    logger.error('Get incidents error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/quality/incidents
// @desc    Create quality incident
// @access  Private
router.post('/incidents', protect, async (req, res) => {
  try {
    const incidentNumber = `INC-${Date.now()}`;
    
    const incident = await QualityIncident.create({
      ...req.body,
      incidentNumber,
      company: req.user.company,
      createdBy: req.user.id
    });

    res.status(201).json({
      success: true,
      data: incident
    });

  } catch (error) {
    logger.error('Create incident error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/quality/incidents/:id/resolve
// @desc    Resolve incident
// @access  Private (Quality, Admin)
router.put('/incidents/:id/resolve', protect, authorize(['quality', 'admin']), async (req, res) => {
  try {
    const { rootCause, correctiveActions, preventiveActions, lessonsLearned } = req.body;

    const incident = await QualityIncident.findByIdAndUpdate(
      req.params.id,
      {
        status: 'resolved',
        rootCause,
        correctiveActions,
        preventiveActions,
        lessonsLearned,
        closedBy: req.user.id,
        closedAt: new Date(),
        updatedAt: new Date()
      },
      { new: true }
    );

    if (!incident) {
      return res.status(404).json({ success: false, message: 'Incident not found' });
    }

    res.json({
      success: true,
      message: 'Incident resolved',
      data: incident
    });

  } catch (error) {
    logger.error('Resolve incident error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/quality/audits
// @desc    Get audits
// @access  Private (Quality, Admin)
router.get('/audits', protect, authorize(['quality', 'admin']), async (req, res) => {
  try {
    const { status, type, standard } = req.query;
    
    let query = { company: req.user.company };
    if (status) query.status = status;
    if (type) query.type = type;
    if (standard) query.standard = standard;

    const audits = await Audit.find(query)
      .sort({ dates.planned: 1 });

    res.json({
      success: true,
      count: audits.length,
      data: audits
    });

  } catch (error) {
    logger.error('Get audits error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/quality/audits
// @desc    Schedule audit
// @access  Private (Quality, Admin)
router.post('/audits', protect, authorize(['quality', 'admin']), async (req, res) => {
  try {
    const auditNumber = `AUD-${Date.now()}`;
    
    const audit = await Audit.create({
      ...req.body,
      auditNumber,
      company: req.user.company,
      createdBy: req.user.id
    });

    res.status(201).json({
      success: true,
      data: audit
    });

  } catch (error) {
    logger.error('Create audit error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/quality/kpis
// @desc    Get KPIs
// @access  Private
router.get('/kpis', protect, async (req, res) => {
  try {
    const { category } = req.query;
    
    let query = { company: req.user.company };
    if (category) query.category = category;

    const kpis = await KPI.find(query);

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

// @route   POST /api/v1/quality/kpis/:id/measure
// @desc    Add KPI measurement
// @access  Private (Quality, Admin)
router.post('/kpis/:id/measure', protect, authorize(['quality', 'admin']), async (req, res) => {
  try {
    const { period, value, target, notes } = req.body;

    const kpi = await KPI.findByIdAndUpdate(
      req.params.id,
      {
        $push: {
          measurements: {
            period,
            value,
            target,
            trend: value >= target ? 'up' : 'down',
            notes
          }
        }
      },
      { new: true }
    );

    if (!kpi) {
      return res.status(404).json({ success: false, message: 'KPI not found' });
    }

    res.json({
      success: true,
      data: kpi
    });

  } catch (error) {
    logger.error('Add measurement error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/quality/sops
// @desc    Get SOPs
// @access  Private
router.get('/sops', protect, async (req, res) => {
  try {
    const { category, status } = req.query;
    
    let query = { company: req.user.company };
    if (category) query.category = category;
    if (status) query.status = status;

    const sops = await SOP.find(query)
      .populate('createdBy', 'firstName lastName')
      .populate('approvedBy', 'firstName lastName');

    res.json({
      success: true,
      count: sops.length,
      data: sops
    });

  } catch (error) {
    logger.error('Get SOPs error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/quality/dashboard
// @desc    Quality dashboard
// @access  Private (Quality, Admin)
router.get('/dashboard', protect, authorize(['quality', 'admin', 'supervisor']), async (req, res) => {
  try {
    const today = new Date();
    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

    const stats = await Promise.all([
      // Incidents by severity
      QualityIncident.aggregate([
        { $match: { company: req.user.company._id } },
        { $group: { _id: '$severity', count: { $sum: 1 } } }
      ]),
      
      // Incidents by status
      QualityIncident.aggregate([
        { $match: { company: req.user.company._id } },
        { $group: { _id: '$status', count: { $sum: 1 } } }
      ]),
      
      // Monthly trend
      QualityIncident.countDocuments({
        company: req.user.company,
        createdAt: { $gte: startOfMonth }
      }),
      
      // Open audits
      Audit.countDocuments({
        company: req.user.company,
        status: { $in: ['planned', 'in_progress'] }
      }),
      
      // Average resolution time
      QualityIncident.aggregate([
        { $match: { status: 'closed', closedAt: { $exists: true } } },
        {
          $project: {
            resolutionTime: { $subtract: ['$closedAt', '$createdAt'] }
          }
        },
        {
          $group: {
            _id: null,
            avgResolutionTime: { $avg: '$resolutionTime' }
          }
        }
      ])
    ]);

    res.json({
      success: true,
      data: {
        incidentsBySeverity: stats[0],
        incidentsByStatus: stats[1],
        monthlyIncidents: stats[2],
        openAudits: stats[3],
        avgResolutionTime: stats[4][0]?.avgResolutionTime || 0
      }
    });

  } catch (error) {
    logger.error('Quality dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
