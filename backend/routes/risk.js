//
/**
 * ============================================
 * ⚠️ Risk Management Routes - إدارة المخاطر
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { Risk, RiskAssessment, BCP } = require('../models/Risk');
const logger = require('../utils/logger');

// @route   GET /api/v1/risks
// @desc    Get all risks
// @access  Private
router.get('/', protect, async (req, res) => {
  try {
    const { status, category, riskLevel } = req.query;
    
    let query = { company: req.user.company };
    if (status) query.status = status;
    if (category) query.category = category;
    if (riskLevel) query.riskLevel = riskLevel;

    const risks = await Risk.find(query)
      .populate('controls.owner', 'firstName lastName')
      .populate('treatment.owner', 'firstName lastName')
      .populate('createdBy', 'firstName lastName')
      .sort({ riskLevel: -1, createdAt: -1 });

    res.json({
      success: true,
      count: risks.length,
      data: risks
    });

  } catch (error) {
    logger.error('Get risks error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/risks
// @desc    Create risk
// @access  Private (Risk Manager, Admin)
router.post('/', protect, authorize(['admin', 'supervisor']), async (req, res) => {
  try {
    const riskNumber = `RISK-${Date.now()}`;
    
    const risk = await Risk.create({
      ...req.body,
      riskNumber,
      company: req.user.company,
      createdBy: req.user.id
    });

    res.status(201).json({
      success: true,
      data: risk
    });

  } catch (error) {
    logger.error('Create risk error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/risks/matrix
// @desc    Get risk matrix data
// @access  Private
router.get('/matrix', protect, async (req, res) => {
  try {
    const matrix = await Risk.aggregate([
      { $match: { company: req.user.company._id } },
      {
        $group: {
          _id: {
            likelihood: '$likelihood',
            impact: '$impact'
          },
          count: { $sum: 1 },
          risks: { $push: '$title' }
        }
      }
    ]);

    const riskLevels = await Risk.aggregate([
      { $match: { company: req.user.company._id } },
      {
        $group: {
          _id: '$riskLevel',
          count: { $sum: 1 }
        }
      }
    ]);

    res.json({
      success: true,
      data: {
        matrix,
        riskLevels
      }
    });

  } catch (error) {
    logger.error('Get risk matrix error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/risks/:id/controls
// @desc    Add risk control
// @access  Private (Risk Manager, Admin)
router.put('/:id/controls', protect, authorize(['admin', 'supervisor']), async (req, res) => {
  try {
    const risk = await Risk.findByIdAndUpdate(
      req.params.id,
      {
        $push: { controls: req.body },
        updatedAt: new Date()
      },
      { new: true }
    );

    if (!risk) {
      return res.status(404).json({ success: false, message: 'Risk not found' });
    }

    res.json({
      success: true,
      data: risk
    });

  } catch (error) {
    logger.error('Add control error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/risks/assessments
// @desc    Get risk assessments
// @access  Private (Risk Manager, Admin)
router.get('/assessments', protect, authorize(['admin', 'supervisor']), async (req, res) => {
  try {
    const assessments = await RiskAssessment.find({ company: req.user.company })
      .populate('assessors.user', 'firstName lastName')
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: assessments.length,
      data: assessments
    });

  } catch (error) {
    logger.error('Get assessments error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/risks/assessments
// @desc    Create risk assessment
// @access  Private (Risk Manager, Admin)
router.post('/assessments', protect, authorize(['admin', 'supervisor']), async (req, res) => {
  try {
    const assessmentNumber = `RA-${Date.now()}`;
    
    const assessment = await RiskAssessment.create({
      ...req.body,
      assessmentNumber,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: assessment
    });

  } catch (error) {
    logger.error('Create assessment error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/risks/bcp
// @desc    Get business continuity plans
// @access  Private
router.get('/bcp', protect, async (req, res) => {
  try {
    const plans = await BCP.find({ company: req.user.company })
      .populate('responseTeam.primaryContact', 'firstName lastName')
      .populate('responseTeam.backupContact', 'firstName lastName');

    res.json({
      success: true,
      count: plans.length,
      data: plans
    });

  } catch (error) {
    logger.error('Get BCP error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/risks/bcp
// @desc    Create BCP
// @access  Private (Risk Manager, Admin)
router.post('/bcp', protect, authorize(['admin', 'supervisor']), async (req, res) => {
  try {
    const planNumber = `BCP-${Date.now()}`;
    
    const bcp = await BCP.create({
      ...req.body,
      planNumber,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: bcp
    });

  } catch (error) {
    logger.error('Create BCP error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/risks/dashboard
// @desc    Risk dashboard
// @access  Private
router.get('/dashboard', protect, async (req, res) => {
  try {
    const stats = await Promise.all([
      // Risk by level
      Risk.aggregate([
        { $match: { company: req.user.company._id } },
        { $group: { _id: '$riskLevel', count: { $sum: 1 } } }
      ]),
      
      // Risk by category
      Risk.aggregate([
        { $match: { company: req.user.company._id } },
        { $group: { _id: '$category', count: { $sum: 1 } } }
      ]),
      
      // Open high/extreme risks
      Risk.countDocuments({
        company: req.user.company,
        riskLevel: { $in: ['high', 'extreme'] },
        status: { $ne: 'closed' }
      }),
      
      // Treatment status
      Risk.aggregate([
        { $match: { company: req.user.company._id } },
        { $group: { _id: '$status', count: { $sum: 1 } } }
      ])
    ]);

    res.json({
      success: true,
      data: {
        byLevel: stats[0],
        byCategory: stats[1],
        highRiskCount: stats[2],
        byStatus: stats[3]
      }
    });

  } catch (error) {
    logger.error('Risk dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
