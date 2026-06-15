//
/**
 * ============================================
 * 💾 Cache Management Routes - إدارة التخزين المؤقت
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { CacheEntry, CacheStats, CacheWarmingJob, CacheInvalidationRule } = require('../models/Cache');
const logger = require('../utils/logger');

// Cache Stats
// @route   GET /api/v1/cache/stats
// @desc    Get cache statistics
// @access  Private (Admin, Developer)
router.get('/stats', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const latest = await CacheStats.findOne({
      company: req.user.company
    }).sort({ timestamp: -1 });

    // Calculate current stats
    const stats = await Promise.all([
      CacheEntry.countDocuments({
        company: req.user.company,
        invalidated: false,
        expiresAt: { $gt: new Date() }
      }),

      CacheEntry.aggregate([
        {
          $match: {
            company: req.user.company._id,
            invalidated: false
          }
        },
        { $group: { _id: null, totalSize: { $sum: '$metadata.size' } } }
      ]),

      CacheEntry.find({
        company: req.user.company,
        invalidated: false
      })
        .sort({ accessCount: -1 })
        .limit(10)
        .select('key tags accessCount accessedAt expiresAt')
    ]);

    res.json({
      success: true,
      data: {
        current: latest,
        calculated: {
          totalKeys: stats[0],
          memoryUsage: stats[1][0]?.totalSize || 0,
          topKeys: stats[2]
        }
      }
    });

  } catch (error) {
    logger.error('Get cache stats error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/cache/entries
// @desc    Get cache entries
// @access  Private (Admin, Developer)
router.get('/entries', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const { tag, search, page = 1, limit = 50 } = req.query;

    let query = {
      company: req.user.company,
      invalidated: false
    };

    if (tag) query.tags = tag;
    if (search) query.key = { $regex: search, $options: 'i' };

    const entries = await CacheEntry.find(query)
      .select('key tags accessCount accessedAt expiresAt metadata.size')
      .sort({ accessCount: -1 })
      .skip((page - 1) * limit)
      .limit(limit * 1);

    const total = await CacheEntry.countDocuments(query);

    res.json({
      success: true,
      count: entries.length,
      pagination: {
        page: parseInt(page),
        pages: Math.ceil(total / limit),
        total
      },
      data: entries
    });

  } catch (error) {
    logger.error('Get cache entries error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/cache/entries/:key
// @desc    Get cache entry details
// @access  Private (Admin, Developer)
router.get('/entries/:key', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const entry = await CacheEntry.findOne({
      key: req.params.key,
      company: req.user.company
    });

    if (!entry) {
      return res.status(404).json({ success: false, message: 'Cache entry not found' });
    }

    res.json({
      success: true,
      data: entry
    });

  } catch (error) {
    logger.error('Get cache entry error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   DELETE /api/v1/cache/entries/:key
// @desc    Invalidate cache entry
// @access  Private (Admin)
router.delete('/entries/:key', protect, authorize(['admin']), async (req, res) => {
  try {
    const entry = await CacheEntry.findOneAndUpdate(
      { key: req.params.key, company: req.user.company },
      {
        invalidated: true,
        invalidatedAt: new Date(),
        invalidatedBy: req.user.id
      },
      { new: true }
    );

    if (!entry) {
      return res.status(404).json({ success: false, message: 'Cache entry not found' });
    }

    res.json({
      success: true,
      message: 'Cache entry invalidated'
    });

  } catch (error) {
    logger.error('Invalidate cache entry error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/cache/invalidate
// @desc    Invalidate cache by tag or pattern
// @access  Private (Admin)
router.post('/invalidate', protect, authorize(['admin']), async (req, res) => {
  try {
    const { tag, pattern } = req.body;

    let query = {
      company: req.user.company,
      invalidated: false
    };

    if (tag) query.tags = tag;
    if (pattern) query.key = { $regex: pattern };

    const result = await CacheEntry.updateMany(
      query,
      {
        invalidated: true,
        invalidatedAt: new Date(),
        invalidatedBy: req.user.id
      }
    );

    res.json({
      success: true,
      message: `${result.modifiedCount} cache entries invalidated`
    });

  } catch (error) {
    logger.error('Batch invalidate cache error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/cache/clear
// @desc    Clear all cache
// @access  Private (Admin)
router.post('/clear', protect, authorize(['admin']), async (req, res) => {
  try {
    const result = await CacheEntry.updateMany(
      { company: req.user.company, invalidated: false },
      {
        invalidated: true,
        invalidatedAt: new Date(),
        invalidatedBy: 'admin_clear_all'
      }
    );

    res.json({
      success: true,
      message: `All cache cleared (${result.modifiedCount} entries)`
    });

  } catch (error) {
    logger.error('Clear cache error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Cache Warming Jobs
// @route   GET /api/v1/cache/warming
// @desc    Get cache warming jobs
// @access  Private (Admin, Developer)
router.get('/warming', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const jobs = await CacheWarmingJob.find({ company: req.user.company })
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: jobs.length,
      data: jobs
    });

  } catch (error) {
    logger.error('Get warming jobs error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/cache/warming
// @desc    Create cache warming job
// @access  Private (Admin, Developer)
router.post('/warming', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const jobId = `WARM-${Date.now()}`;

    const job = await CacheWarmingJob.create({
      ...req.body,
      jobId,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: job
    });

  } catch (error) {
    logger.error('Create warming job error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/cache/warming/:id/run
// @desc    Run cache warming job
// @access  Private (Admin)
router.post('/warming/:id/run', protect, authorize(['admin']), async (req, res) => {
  try {
    const job = await CacheWarmingJob.findOneAndUpdate(
      { _id: req.params.id, company: req.user.company },
      { status: 'running' },
      { new: true }
    );

    if (!job) {
      return res.status(404).json({ success: false, message: 'Warming job not found' });
    }

    // TODO: Execute warming job

    res.json({
      success: true,
      message: 'Cache warming job started'
    });

  } catch (error) {
    logger.error('Run warming job error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Invalidation Rules
// @route   GET /api/v1/cache/rules
// @desc    Get invalidation rules
// @access  Private (Admin, Developer)
router.get('/rules', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const rules = await CacheInvalidationRule.find({ company: req.user.company })
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: rules.length,
      data: rules
    });

  } catch (error) {
    logger.error('Get invalidation rules error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/cache/rules
// @desc    Create invalidation rule
// @access  Private (Admin, Developer)
router.post('/rules', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const ruleId = `RULE-${Date.now()}`;

    const rule = await CacheInvalidationRule.create({
      ...req.body,
      ruleId,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: rule
    });

  } catch (error) {
    logger.error('Create invalidation rule error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Dashboard
// @route   GET /api/v1/cache/dashboard
// @desc    Cache dashboard
// @access  Private (Admin)
router.get('/dashboard', protect, authorize(['admin']), async (req, res) => {
  try {
    const stats = await Promise.all([
      // Total entries
      CacheEntry.countDocuments({
        company: req.user.company,
        invalidated: false,
        expiresAt: { $gt: new Date() }
      }),

      // Expiring soon (next hour)
      CacheEntry.countDocuments({
        company: req.user.company,
        invalidated: false,
        expiresAt: {
          $gt: new Date(),
          $lte: new Date(Date.now() + 60 * 60 * 1000)
        }
      }),

      // Most accessed
      CacheEntry.find({
        company: req.user.company,
        invalidated: false
      })
        .sort({ accessCount: -1 })
        .limit(5)
        .select('key accessCount accessedAt'),

      // By tags
      CacheEntry.aggregate([
        {
          $match: {
            company: req.user.company._id,
            invalidated: false
          }
        },
        { $unwind: '$tags' },
        { $group: { _id: '$tags', count: { $sum: 1 } } },
        { $sort: { count: -1 } },
        { $limit: 10 }
      ]),

      // Jobs
      CacheWarmingJob.countDocuments({ company: req.user.company }),
      CacheInvalidationRule.countDocuments({ company: req.user.company })
    ]);

    res.json({
      success: true,
      data: {
        total: stats[0],
        expiringSoon: stats[1],
        topKeys: stats[2],
        byTag: stats[3],
        jobs: stats[4],
        rules: stats[5]
      }
    });

  } catch (error) {
    logger.error('Cache dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
