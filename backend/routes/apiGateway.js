//
/**
 * ============================================
 * 🌐 API Gateway Routes - بوابة API
 * ============================================
 */

const express = require('express');
const router = express.Router();
const crypto = require('crypto');
const { protect, authorize } = require('../middleware/auth');
const { APIEndpoint, APIKey, APIUsageLog, Webhook } = require('../models/APIGateway');
const logger = require('../utils/logger');

// API Endpoints
// @route   GET /api/v1/gateway/endpoints
// @desc    Get API endpoints
// @access  Private (Admin, Developer)
router.get('/endpoints', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const { category, status, version } = req.query;

    let query = { company: req.user.company };
    if (category) query.category = category;
    if (status) query.status = status;
    if (version) query.version = version;

    const endpoints = await APIEndpoint.find(query)
      .sort({ path: 1, method: 1 });

    res.json({
      success: true,
      count: endpoints.length,
      data: endpoints
    });

  } catch (error) {
    logger.error('Get endpoints error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/gateway/endpoints
// @desc    Create API endpoint
// @access  Private (Admin, Developer)
router.post('/endpoints', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const endpointId = `EP-${Date.now()}`;

    const endpoint = await APIEndpoint.create({
      ...req.body,
      endpointId,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: endpoint
    });

  } catch (error) {
    logger.error('Create endpoint error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// API Keys
// @route   GET /api/v1/gateway/keys
// @desc    Get API keys
// @access  Private (Admin)
router.get('/keys', protect, authorize(['admin']), async (req, res) => {
  try {
    const { status, type } = req.query;

    let query = { company: req.user.company };
    if (status) query.status = status;
    if (type) query.type = type;

    const keys = await APIKey.find(query)
      .populate('owner', 'firstName lastName email')
      .sort({ createdAt: -1 });

    // Mask keys for security
    const maskedKeys = keys.map(k => {
      const obj = k.toObject();
      obj.key = obj.key.substring(0, 8) + '...' + obj.key.substring(obj.key.length - 4);
      return obj;
    });

    res.json({
      success: true,
      count: keys.length,
      data: maskedKeys
    });

  } catch (error) {
    logger.error('Get API keys error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/gateway/keys
// @desc    Create API key
// @access  Private (Admin)
router.post('/keys', protect, authorize(['admin']), async (req, res) => {
  try {
    const keyId = `KEY-${Date.now()}`;
    const key = `edham_${crypto.randomBytes(32).toString('hex')}`;
    const hashedKey = crypto.createHash('sha256').update(key).digest('hex');

    const apiKey = await APIKey.create({
      ...req.body,
      keyId,
      key,
      hashedKey,
      owner: req.user.id,
      company: req.user.company
    });

    // Return the key only once
    res.status(201).json({
      success: true,
      message: 'API key created. Store this key securely - it won\'t be shown again.',
      data: {
        keyId: apiKey.keyId,
        name: apiKey.name,
        key: key, // Only shown once
        type: apiKey.type,
        expiresAt: apiKey.expiresAt,
        permissions: apiKey.permissions,
        throttling: apiKey.throttling
      }
    });

  } catch (error) {
    logger.error('Create API key error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   DELETE /api/v1/gateway/keys/:id
// @desc    Revoke API key
// @access  Private (Admin)
router.delete('/keys/:id', protect, authorize(['admin']), async (req, res) => {
  try {
    const key = await APIKey.findOneAndUpdate(
      { _id: req.params.id, company: req.user.company },
      { status: 'revoked' },
      { new: true }
    );

    if (!key) {
      return res.status(404).json({ success: false, message: 'API key not found' });
    }

    res.json({
      success: true,
      message: 'API key revoked'
    });

  } catch (error) {
    logger.error('Revoke API key error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Webhooks
// @route   GET /api/v1/gateway/webhooks
// @desc    Get webhooks
// @access  Private (Admin, Developer)
router.get('/webhooks', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const webhooks = await Webhook.find({ company: req.user.company })
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: webhooks.length,
      data: webhooks
    });

  } catch (error) {
    logger.error('Get webhooks error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/gateway/webhooks
// @desc    Create webhook
// @access  Private (Admin, Developer)
router.post('/webhooks', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const webhookId = `WH-${Date.now()}`;
    const secret = crypto.randomBytes(32).toString('hex');

    const webhook = await Webhook.create({
      ...req.body,
      webhookId,
      secret,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: {
        ...webhook.toObject(),
        secret // Show once
      }
    });

  } catch (error) {
    logger.error('Create webhook error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/gateway/webhooks/:id/toggle
// @desc    Toggle webhook active state
// @access  Private (Admin, Developer)
router.put('/webhooks/:id/toggle', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const webhook = await Webhook.findOne({
      _id: req.params.id,
      company: req.user.company
    });

    if (!webhook) {
      return res.status(404).json({ success: false, message: 'Webhook not found' });
    }

    webhook.active = !webhook.active;
    await webhook.save();

    res.json({
      success: true,
      data: webhook
    });

  } catch (error) {
    logger.error('Toggle webhook error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/gateway/webhooks/:id/test
// @desc    Test webhook
// @access  Private (Admin, Developer)
router.post('/webhooks/:id/test', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const webhook = await Webhook.findOne({
      _id: req.params.id,
      company: req.user.company
    });

    if (!webhook) {
      return res.status(404).json({ success: false, message: 'Webhook not found' });
    }

    // TODO: Send test webhook

    res.json({
      success: true,
      message: 'Test webhook sent'
    });

  } catch (error) {
    logger.error('Test webhook error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Analytics
// @route   GET /api/v1/gateway/analytics
// @desc    Get API analytics
// @access  Private (Admin, Developer)
router.get('/analytics', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const { from, to, endpoint } = req.query;

    let dateQuery = { company: req.user.company };
    if (from && to) {
      dateQuery.timestamp = {
        $gte: new Date(from),
        $lte: new Date(to)
      };
    }
    if (endpoint) {
      dateQuery['endpoint.path'] = endpoint;
    }

    const stats = await Promise.all([
      // Total calls
      APIUsageLog.countDocuments(dateQuery),

      // Calls by endpoint
      APIUsageLog.aggregate([
        { $match: dateQuery },
        {
          $group: {
            _id: { path: '$endpoint.path', method: '$endpoint.method' },
            count: { $sum: 1 },
            avgLatency: { $avg: '$metrics.latency' },
            errors: {
              $sum: { $cond: [{ $gte: ['$response.statusCode', 400] }, 1, 0] }
            }
          }
        },
        { $sort: { count: -1 } }
      ]),

      // Status code distribution
      APIUsageLog.aggregate([
        { $match: dateQuery },
        {
          $group: {
            _id: '$response.statusCode',
            count: { $sum: 1 }
          }
        }
      ]),

      // Hourly distribution
      APIUsageLog.aggregate([
        { $match: dateQuery },
        {
          $group: {
            _id: { $hour: '$timestamp' },
            count: { $sum: 1 }
          }
        },
        { $sort: { _id: 1 } }
      ]),

      // Top API keys
      APIUsageLog.aggregate([
        { $match: dateQuery },
        { $group: { _id: '$apiKey', count: { $sum: 1 } } },
        { $sort: { count: -1 } },
        { $limit: 10 }
      ])
    ]);

    res.json({
      success: true,
      data: {
        totalCalls: stats[0],
        byEndpoint: stats[1],
        statusDistribution: stats[2],
        hourlyDistribution: stats[3],
        topAPIKeys: stats[4]
      }
    });

  } catch (error) {
    logger.error('Get API analytics error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/gateway/analytics/errors
// @desc    Get API errors
// @access  Private (Admin, Developer)
router.get('/analytics/errors', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const { from, to } = req.query;

    let query = {
      company: req.user.company,
      'response.statusCode': { $gte: 400 }
    };

    if (from && to) {
      query.timestamp = {
        $gte: new Date(from),
        $lte: new Date(to)
      };
    }

    const errors = await APIUsageLog.find(query)
      .select('timestamp endpoint response.error metrics.latency client.ip')
      .sort({ timestamp: -1 })
      .limit(100);

    res.json({
      success: true,
      count: errors.length,
      data: errors
    });

  } catch (error) {
    logger.error('Get API errors error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Dashboard
// @route   GET /api/v1/gateway/dashboard
// @desc    API gateway dashboard
// @access  Private (Admin, Developer)
router.get('/dashboard', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const today = new Date();
    const startOfDay = new Date(today.setHours(0, 0, 0, 0));

    const stats = await Promise.all([
      // Total endpoints
      APIEndpoint.countDocuments({ company: req.user.company }),

      // Active endpoints
      APIEndpoint.countDocuments({ company: req.user.company, status: 'active' }),

      // API keys
      APIKey.countDocuments({ company: req.user.company, status: 'active' }),

      // Webhooks
      Webhook.countDocuments({ company: req.user.company, active: true }),

      // Today's calls
      APIUsageLog.countDocuments({
        company: req.user.company,
        timestamp: { $gte: startOfDay }
      }),

      // Today's errors
      APIUsageLog.countDocuments({
        company: req.user.company,
        timestamp: { $gte: startOfDay },
        'response.statusCode': { $gte: 400 }
      }),

      // Average latency today
      APIUsageLog.aggregate([
        {
          $match: {
            company: req.user.company._id,
            timestamp: { $gte: startOfDay }
          }
        },
        { $group: { _id: null, avgLatency: { $avg: '$metrics.latency' } } }
      ]),

      // Top endpoints today
      APIUsageLog.aggregate([
        {
          $match: {
            company: req.user.company._id,
            timestamp: { $gte: startOfDay }
          }
        },
        {
          $group: {
            _id: { path: '$endpoint.path', method: '$endpoint.method' },
            count: { $sum: 1 }
          }
        },
        { $sort: { count: -1 } },
        { $limit: 5 }
      ])
    ]);

    res.json({
      success: true,
      data: {
        endpoints: {
          total: stats[0],
          active: stats[1]
        },
        apiKeys: stats[2],
        webhooks: stats[3],
        today: {
          calls: stats[4],
          errors: stats[5],
          errorRate: stats[4] > 0 ? ((stats[5] / stats[4]) * 100).toFixed(2) : 0,
          avgLatency: stats[6][0]?.avgLatency?.toFixed(2) || 0
        },
        topEndpoints: stats[7]
      }
    });

  } catch (error) {
    logger.error('API gateway dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
