//
/**
 * ============================================
 * 🌍 Multi-Region Routes - دعم المناطق المتعددة
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { Region, RegionHealth, DataResidencyRule, FailoverConfig } = require('../models/MultiRegion');
const logger = require('../utils/logger');

// Regions
// @route   GET /api/v1/regions
// @desc    Get all regions
// @access  Private (Admin)
router.get('/', protect, authorize(['admin']), async (req, res) => {
  try {
    const regions = await Region.find({ company: req.user.company })
      .sort({ priority: 1, name: 1 });

    res.json({
      success: true,
      count: regions.length,
      data: regions
    });

  } catch (error) {
    logger.error('Get regions error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/regions
// @desc    Create region
// @access  Private (Admin, Super Admin)
router.post('/', protect, authorize(['admin']), async (req, res) => {
  try {
    const regionId = `REG-${Date.now()}`;

    const region = await Region.create({
      ...req.body,
      regionId,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: region
    });

  } catch (error) {
    logger.error('Create region error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/regions/:id/health
// @desc    Get region health
// @access  Private (Admin)
router.get('/:id/health', protect, authorize(['admin']), async (req, res) => {
  try {
    const region = await Region.findOne({
      _id: req.params.id,
      company: req.user.company
    });

    if (!region) {
      return res.status(404).json({ success: false, message: 'Region not found' });
    }

    const latest = await RegionHealth.findOne({
      region: region._id,
      company: req.user.company
    }).sort({ timestamp: -1 });

    const history = await RegionHealth.find({
      region: region._id,
      company: req.user.company,
      timestamp: { $gte: new Date(Date.now() - 24 * 60 * 60 * 1000) }
    })
      .select('timestamp status metrics.latency.avg')
      .sort({ timestamp: 1 });

    res.json({
      success: true,
      data: {
        region: {
          id: region._id,
          name: region.name,
          code: region.code,
          status: region.status
        },
        current: latest,
        history
      }
    });

  } catch (error) {
    logger.error('Get region health error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Data Residency
// @route   GET /api/v1/regions/residency
// @desc    Get data residency rules
// @access  Private (Admin, Compliance)
router.get('/residency', protect, authorize(['admin', 'compliance']), async (req, res) => {
  try {
    const rules = await DataResidencyRule.find({ company: req.user.company })
      .sort({ entity: 1 });

    res.json({
      success: true,
      count: rules.length,
      data: rules
    });

  } catch (error) {
    logger.error('Get residency rules error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/regions/residency
// @desc    Create data residency rule
// @access  Private (Admin, Compliance)
router.post('/residency', protect, authorize(['admin', 'compliance']), async (req, res) => {
  try {
    const ruleId = `RES-${Date.now()}`;

    const rule = await DataResidencyRule.create({
      ...req.body,
      ruleId,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: rule
    });

  } catch (error) {
    logger.error('Create residency rule error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Failover
// @route   GET /api/v1/regions/failover
// @desc    Get failover configuration
// @access  Private (Admin)
router.get('/failover', protect, authorize(['admin']), async (req, res) => {
  try {
    let config = await FailoverConfig.findOne({ company: req.user.company });

    if (!config) {
      config = {
        enabled: false,
        strategy: 'active_passive',
        automatic: true
      };
    }

    res.json({
      success: true,
      data: config
    });

  } catch (error) {
    logger.error('Get failover config error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/regions/failover
// @desc    Update failover configuration
// @access  Private (Admin)
router.put('/failover', protect, authorize(['admin']), async (req, res) => {
  try {
    const config = await FailoverConfig.findOneAndUpdate(
      { company: req.user.company },
      req.body,
      { new: true, upsert: true }
    );

    res.json({
      success: true,
      data: config
    });

  } catch (error) {
    logger.error('Update failover config error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/regions/failover/trigger
// @desc    Trigger manual failover
// @access  Private (Admin, Super Admin)
router.post('/failover/trigger', protect, authorize(['admin']), async (req, res) => {
  try {
    const { from, to, reason } = req.body;

    // TODO: Execute failover

    res.json({
      success: true,
      message: `Failover triggered from ${from} to ${to}`,
      data: {
        from,
        to,
        reason,
        timestamp: new Date(),
        manual: true
      }
    });

  } catch (error) {
    logger.error('Trigger failover error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Dashboard
// @route   GET /api/v1/regions/dashboard
// @desc    Multi-region dashboard
// @access  Private (Admin)
router.get('/dashboard', protect, authorize(['admin']), async (req, res) => {
  try {
    const regions = await Region.find({ company: req.user.company });

    const regionIds = regions.map(r => r._id);

    const latestHealth = await RegionHealth.find({
      region: { $in: regionIds },
      company: req.user.company
    })
      .sort({ timestamp: -1 })
      .limit(regions.length)
      .populate('region', 'name code location');

    // Count by status
    const statusCounts = await RegionHealth.aggregate([
      {
        $match: {
          company: req.user.company._id,
          region: { $in: regionIds }
        }
      },
      {
        $sort: { timestamp: -1 }
      },
      {
        $group: {
          _id: '$region',
          latestStatus: { $first: '$status' }
        }
      },
      {
        $group: {
          _id: '$latestStatus',
          count: { $sum: 1 }
        }
      }
    ]);

    // Residency rules
    const residencyRules = await DataResidencyRule.countDocuments({
      company: req.user.company
    });

    res.json({
      success: true,
      data: {
        regions: regions.map(r => ({
          id: r._id,
          name: r.name,
          code: r.code,
          status: r.status,
          location: r.location,
          compliance: r.compliance
        })),
        health: latestHealth.map(h => ({
          region: h.region,
          status: h.status,
          timestamp: h.timestamp,
          latency: h.metrics?.latency?.avg,
          errorRate: h.metrics?.throughput?.errors > 0
            ? (h.metrics.throughput.errors / h.metrics.throughput.requests) * 100
            : 0
        })),
        statusSummary: statusCounts,
        compliance: {
          residencyRules,
          regionsWithCompliance: regions.filter(r => r.compliance.dataResidency).length
        }
      }
    });

  } catch (error) {
    logger.error('Region dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
