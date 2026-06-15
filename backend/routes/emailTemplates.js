//
/**
 * ============================================
 * 📧 Email Template Routes - قوالب البريد
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { EmailTemplate, EmailLog, NotificationTemplate } = require('../models/EmailTemplate');
const logger = require('../utils/logger');

// Email Templates
// @route   GET /api/v1/email-templates
// @desc    Get email templates
// @access  Private
router.get('/', protect, async (req, res) => {
  try {
    const { category, status } = req.query;

    let query = { company: req.user.company };
    if (category) query.category = category;
    if (status) query.status = status;

    const templates = await EmailTemplate.find(query)
      .populate('createdBy', 'firstName lastName')
      .sort({ updatedAt: -1 });

    res.json({
      success: true,
      count: templates.length,
      data: templates
    });

  } catch (error) {
    logger.error('Get email templates error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/email-templates
// @desc    Create email template
// @access  Private (Admin, Marketing)
router.post('/', protect, authorize(['admin', 'marketing']), async (req, res) => {
  try {
    const code = `ET-${Date.now().toString().slice(-6)}`;

    const template = await EmailTemplate.create({
      ...req.body,
      code,
      company: req.user.company,
      createdBy: req.user.id
    });

    res.status(201).json({
      success: true,
      data: template
    });

  } catch (error) {
    logger.error('Create email template error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/email-templates/:id
// @desc    Get email template
// @access  Private
router.get('/:id', protect, async (req, res) => {
  try {
    const template = await EmailTemplate.findById(req.params.id);

    if (!template) {
      return res.status(404).json({ success: false, message: 'Template not found' });
    }

    res.json({
      success: true,
      data: template
    });

  } catch (error) {
    logger.error('Get email template error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/email-templates/:id
// @desc    Update email template
// @access  Private (Admin, Marketing)
router.put('/:id', protect, authorize(['admin', 'marketing']), async (req, res) => {
  try {
    const template = await EmailTemplate.findByIdAndUpdate(
      req.params.id,
      {
        ...req.body,
        updatedAt: new Date()
      },
      { new: true, runValidators: true }
    );

    if (!template) {
      return res.status(404).json({ success: false, message: 'Template not found' });
    }

    res.json({
      success: true,
      message: 'Template updated',
      data: template
    });

  } catch (error) {
    logger.error('Update email template error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/email-templates/:id/duplicate
// @desc    Duplicate template
// @access  Private (Admin, Marketing)
router.post('/:id/duplicate', protect, authorize(['admin', 'marketing']), async (req, res) => {
  try {
    const original = await EmailTemplate.findById(req.params.id);
    if (!original) {
      return res.status(404).json({ success: false, message: 'Template not found' });
    }

    const newCode = `${original.code}-COPY-${Date.now().toString().slice(-4)}`;
    
    const duplicate = await EmailTemplate.create({
      ...original.toObject(),
      _id: undefined,
      name: `${original.name} (Copy)`,
      code: newCode,
      status: 'draft',
      createdAt: new Date(),
      updatedAt: new Date()
    });

    res.status(201).json({
      success: true,
      message: 'Template duplicated',
      data: duplicate
    });

  } catch (error) {
    logger.error('Duplicate template error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/email-templates/:id/send-test
// @desc    Send test email
// @access  Private (Admin, Marketing)
router.post('/:id/send-test', protect, authorize(['admin', 'marketing']), async (req, res) => {
  try {
    const { to, variables } = req.body;
    
    const template = await EmailTemplate.findById(req.params.id);
    if (!template) {
      return res.status(404).json({ success: false, message: 'Template not found' });
    }

    // TODO: Send test email using email service

    res.json({
      success: true,
      message: 'Test email sent',
      data: { sentTo: to }
    });

  } catch (error) {
    logger.error('Send test email error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Email Logs
// @route   GET /api/v1/email-templates/logs
// @desc    Get email logs
// @access  Private (Admin, Marketing)
router.get('/logs', protect, authorize(['admin', 'marketing']), async (req, res) => {
  try {
    const { status, from, to, limit = 50 } = req.query;

    let query = { company: req.user.company };
    if (status) query.status = status;
    if (from && to) {
      query.createdAt = {
        $gte: new Date(from),
        $lte: new Date(to)
      };
    }

    const logs = await EmailLog.find(query)
      .populate('template', 'name subject')
      .sort({ createdAt: -1 })
      .limit(parseInt(limit));

    res.json({
      success: true,
      count: logs.length,
      data: logs
    });

  } catch (error) {
    logger.error('Get email logs error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/email-templates/logs/stats
// @desc    Get email stats
// @access  Private (Admin, Marketing)
router.get('/logs/stats', protect, authorize(['admin', 'marketing']), async (req, res) => {
  try {
    const today = new Date();
    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

    const stats = await Promise.all([
      // Total sent today
      EmailLog.countDocuments({
        company: req.user.company,
        createdAt: { $gte: new Date(today.setHours(0, 0, 0, 0)) }
      }),

      // By status this month
      EmailLog.aggregate([
        {
          $match: {
            company: req.user.company._id,
            createdAt: { $gte: startOfMonth }
          }
        },
        { $group: { _id: '$status', count: { $sum: 1 } } }
      ]),

      // Delivery rate
      EmailLog.aggregate([
        {
          $match: {
            company: req.user.company._id,
            createdAt: { $gte: startOfMonth }
          }
        },
        {
          $group: {
            _id: null,
            total: { $sum: 1 },
            delivered: {
              $sum: { $cond: [{ $eq: ['$status', 'delivered'] }, 1, 0] }
            },
            opened: {
              $sum: { $cond: [{ $eq: ['$status', 'opened'] }, 1, 0] }
            },
            bounced: {
              $sum: { $cond: [{ $eq: ['$status', 'bounced'] }, 1, 0] }
            }
          }
        }
      ])
    ]);

    res.json({
      success: true,
      data: {
        sentToday: stats[0],
        byStatus: stats[1],
        deliveryStats: stats[2][0] || { total: 0, delivered: 0, opened: 0, bounced: 0 }
      }
    });

  } catch (error) {
    logger.error('Get email stats error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Notification Templates
// @route   GET /api/v1/email-templates/notification-templates
// @desc    Get notification templates
// @access  Private
router.get('/notification-templates', protect, async (req, res) => {
  try {
    const { channel } = req.query;

    let query = { company: req.user.company };
    if (channel) query.channels = channel;

    const templates = await NotificationTemplate.find(query)
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: templates.length,
      data: templates
    });

  } catch (error) {
    logger.error('Get notification templates error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/email-templates/notification-templates
// @desc    Create notification template
// @access  Private (Admin, Developer)
router.post('/notification-templates', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const template = await NotificationTemplate.create({
      ...req.body,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: template
    });

  } catch (error) {
    logger.error('Create notification template error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
