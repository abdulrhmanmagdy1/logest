//
/**
 * ============================================
 * ⚙️ System Routes - إدارة النظام والمراقبة
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { SystemConfig, FeatureFlag, SystemHealth } = require('../models/SystemConfig');
const logger = require('../utils/logger');

// System Config
// @route   GET /api/v1/system/config
// @desc    Get system configuration
// @access  Private (Admin)
router.get('/config', protect, authorize(['admin']), async (req, res) => {
  try {
    let config = await SystemConfig.findOne({ company: req.user.company });

    if (!config) {
      // Create default config
      config = await SystemConfig.create({
        company: req.user.company,
        general: {
          timezone: 'Asia/Riyadh',
          language: 'ar',
          currency: 'SAR'
        }
      });
    }

    // Remove sensitive data
    const safeConfig = config.toObject();
    delete safeConfig.notifications?.email?.smtp?.auth?.pass;
    delete safeConfig.security?.twoFactor?.backupCodes;
    delete safeConfig.integrations?.mapProvider?.apiKey;

    res.json({
      success: true,
      data: safeConfig
    });

  } catch (error) {
    logger.error('Get system config error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/system/config
// @desc    Update system configuration
// @access  Private (Admin)
router.put('/config', protect, authorize(['admin']), async (req, res) => {
  try {
    const config = await SystemConfig.findOneAndUpdate(
      { company: req.user.company },
      {
        ...req.body,
        updatedAt: new Date(),
        updatedBy: req.user.id
      },
      { new: true, upsert: true }
    );

    res.json({
      success: true,
      message: 'Configuration updated',
      data: config
    });

  } catch (error) {
    logger.error('Update system config error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Feature Flags
// @route   GET /api/v1/system/features
// @desc    Get feature flags
// @access  Private (Admin)
router.get('/features', protect, authorize(['admin']), async (req, res) => {
  try {
    const flags = await FeatureFlag.find();

    res.json({
      success: true,
      count: flags.length,
      data: flags
    });

  } catch (error) {
    logger.error('Get feature flags error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/system/features
// @desc    Create feature flag
// @access  Private (Admin)
router.post('/features', protect, authorize(['admin']), async (req, res) => {
  try {
    const flag = await FeatureFlag.create(req.body);

    res.status(201).json({
      success: true,
      data: flag
    });

  } catch (error) {
    logger.error('Create feature flag error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/system/features/:id
// @desc    Update feature flag
// @access  Private (Admin)
router.put('/features/:id', protect, authorize(['admin']), async (req, res) => {
  try {
    const flag = await FeatureFlag.findByIdAndUpdate(
      req.params.id,
      {
        ...req.body,
        updatedAt: new Date()
      },
      { new: true }
    );

    res.json({
      success: true,
      data: flag
    });

  } catch (error) {
    logger.error('Update feature flag error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Check feature availability
// @route   GET /api/v1/system/features/check/:name
// @desc    Check if feature is enabled
// @access  Private
router.get('/features/check/:name', protect, async (req, res) => {
  try {
    const flag = await FeatureFlag.findOne({ name: req.params.name });

    if (!flag) {
      return res.json({
        success: true,
        enabled: false,
        message: 'Feature not found'
      });
    }

    let enabled = flag.enabled;

    // Check rollout
    if (enabled && flag.rollout === 'percentage') {
      const userHash = req.user.id.toString().split('').reduce((a, b) => {
        a = ((a << 5) - a) + b.charCodeAt(0);
        return a & a;
      }, 0);
      const userPercentile = Math.abs(userHash) % 100;
      enabled = userPercentile < flag.rolloutPercentage;
    }

    if (enabled && flag.rollout === 'users') {
      enabled = flag.allowedUsers.includes(req.user.id);
    }

    if (enabled && flag.rollout === 'companies') {
      enabled = flag.allowedCompanies.includes(req.user.company);
    }

    res.json({
      success: true,
      enabled,
      feature: flag
    });

  } catch (error) {
    logger.error('Check feature error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// System Health
// @route   GET /api/v1/system/health
// @desc    Get system health
// @access  Private (Admin)
router.get('/health', protect, authorize(['admin']), async (req, res) => {
  try {
    const latest = await SystemHealth.findOne({
      company: req.user.company
    }).sort({ timestamp: -1 });

    const last24h = await SystemHealth.find({
      company: req.user.company,
      timestamp: { $gte: new Date(Date.now() - 24 * 60 * 60 * 1000) }
    }).sort({ timestamp: -1 });

    // Calculate averages
    const avgMetrics = last24h.length > 0 ? {
      apiLatency: last24h.reduce((a, b) => a + b.metrics.api.latency, 0) / last24h.length,
      errorRate: last24h.reduce((a, b) => a + (b.metrics.api.errors / b.metrics.api.requests), 0) / last24h.length * 100,
      cacheHitRate: last24h.reduce((a, b) => a + b.metrics.cache.hitRate, 0) / last24h.length,
      dbConnections: last24h.reduce((a, b) => a + b.metrics.database.connections, 0) / last24h.length
    } : null;

    // Check service statuses
    const services = latest?.services || [];
    const healthyServices = services.filter(s => s.status === 'healthy').length;
    const degradedServices = services.filter(s => s.status === 'degraded').length;
    const downServices = services.filter(s => s.status === 'down').length;

    res.json({
      success: true,
      data: {
        status: downServices > 0 ? 'critical' : degradedServices > 0 ? 'warning' : 'healthy',
        timestamp: latest?.timestamp,
        services: {
          total: services.length,
          healthy: healthyServices,
          degraded: degradedServices,
          down: downServices
        },
        metrics: latest?.metrics,
        averages: avgMetrics,
        alerts: latest?.alerts || []
      }
    });

  } catch (error) {
    logger.error('Get health error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/system/health/history
// @desc    Get health history
// @access  Private (Admin)
router.get('/health/history', protect, authorize(['admin']), async (req, res) => {
  try {
    const { hours = 24 } = req.query;

    const history = await SystemHealth.find({
      company: req.user.company,
      timestamp: { $gte: new Date(Date.now() - hours * 60 * 60 * 1000) }
    })
      .select('timestamp metrics services')
      .sort({ timestamp: 1 });

    res.json({
      success: true,
      count: history.length,
      data: history
    });

  } catch (error) {
    logger.error('Get health history error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// System Stats
// @route   GET /api/v1/system/stats
// @desc    Get system statistics
// @access  Private (Admin)
router.get('/stats', protect, authorize(['admin']), async (req, res) => {
  try {
    const { Shipment } = require('../models/Shipment');
    const { User } = require('../models/User');
    const { Invoice } = require('../models/Invoice');

    const today = new Date();
    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

    const stats = await Promise.all([
      // Users
      User.countDocuments({ company: req.user.company }),

      // Active users today
      User.countDocuments({
        company: req.user.company,
        'lastLogin': { $gte: new Date(today.setHours(0, 0, 0, 0)) }
      }),

      // Shipments this month
      Shipment.countDocuments({
        company: req.user.company,
        createdAt: { $gte: startOfMonth }
      }),

      // Revenue this month
      Invoice.aggregate([
        {
          $match: {
            company: req.user.company._id,
            status: 'paid',
            paidAt: { $gte: startOfMonth }
          }
        },
        { $group: { _id: null, total: { $sum: '$total' } } }
      ]),

      // System uptime
      SystemHealth.countDocuments({
        company: req.user.company,
        timestamp: { $gte: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000) }
      })
    ]);

    res.json({
      success: true,
      data: {
        users: {
          total: stats[0],
          activeToday: stats[1]
        },
        shipments: {
          thisMonth: stats[2]
        },
        revenue: {
          thisMonth: stats[3][0]?.total || 0
        },
        system: {
          uptimeChecks: stats[4],
          daysMonitored: 30
        }
      }
    });

  } catch (error) {
    logger.error('Get stats error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Maintenance Mode
// @route   POST /api/v1/system/maintenance
// @desc    Toggle maintenance mode
// @access  Private (Admin, Super Admin)
router.post('/maintenance', protect, authorize(['admin']), async (req, res) => {
  try {
    const { enabled, message, scheduled } = req.body;

    const config = await SystemConfig.findOneAndUpdate(
      { company: req.user.company },
      {
        'maintenance.mode': enabled,
        'maintenance.message': message,
        'maintenance.scheduled': scheduled,
        updatedAt: new Date(),
        updatedBy: req.user.id
      },
      { new: true }
    );

    // TODO: Broadcast maintenance mode to all clients

    res.json({
      success: true,
      message: enabled ? 'Maintenance mode enabled' : 'Maintenance mode disabled',
      data: config.maintenance
    });

  } catch (error) {
    logger.error('Toggle maintenance error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Clear Cache
// @route   POST /api/v1/system/clear-cache
// @desc    Clear system cache
// @access  Private (Admin)
router.post('/clear-cache', protect, authorize(['admin']), async (req, res) => {
  try {
    // TODO: Clear Redis cache

    res.json({
      success: true,
      message: 'Cache cleared successfully'
    });

  } catch (error) {
    logger.error('Clear cache error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Export System Data
// @route   POST /api/v1/system/export
// @desc    Export system data
// @access  Private (Admin)
router.post('/export', protect, authorize(['admin']), async (req, res) => {
  try {
    const { entities, format } = req.body;

    // TODO: Export data

    res.json({
      success: true,
      message: 'Export started',
      data: {
        jobId: `EXPORT-${Date.now()}`,
        status: 'processing'
      }
    });

  } catch (error) {
    logger.error('Export error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
