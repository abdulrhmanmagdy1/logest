//
/**
 * ============================================
 * 🪝 Webhooks Routes - نقاط نهاية الويب هوك
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { Webhook, WebhookDelivery } = require('../models/Webhook');
const crypto = require('crypto');
const logger = require('../utils/logger');

// @route   GET /api/v1/webhooks
// @desc    Get company webhooks
// @access  Private
router.get('/', protect, async (req, res) => {
  try {
    const webhooks = await Webhook.find({ company: req.user.id })
      .select('-secret')
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

// @route   POST /api/v1/webhooks
// @desc    Create new webhook
// @access  Private
router.post('/', protect, [
  body('name').trim().notEmpty(),
  body('url').isURL(),
  body('events').isArray()
], async (req, res) => {
  try {
    const { name, url, description, events, headers } = req.body;

    // Validate URL is accessible
    try {
      new URL(url);
    } catch {
      return res.status(400).json({
        success: false,
        message: 'Invalid webhook URL'
      });
    }

    const webhook = await Webhook.create({
      company: req.user.id,
      name,
      url,
      description,
      events,
      headers: headers || [],
      createdBy: req.user.id
    });

    // Return the secret only once
    res.status(201).json({
      success: true,
      message: 'Webhook created successfully',
      data: {
        webhook: {
          _id: webhook._id,
          name: webhook.name,
          url: webhook.url,
          events: webhook.events,
          status: webhook.status
        },
        secret: webhook.secret // Show only once
      }
    });
  } catch (error) {
    logger.error('Create webhook error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/webhooks/:id/deliveries
// @desc    Get webhook delivery history
// @access  Private
router.get('/:id/deliveries', protect, async (req, res) => {
  try {
    const webhook = await Webhook.findOne({
      _id: req.params.id,
      company: req.user.id
    });

    if (!webhook) {
      return res.status(404).json({ success: false, message: 'Webhook not found' });
    }

    const deliveries = await WebhookDelivery.find({ webhook: req.params.id })
      .sort({ createdAt: -1 })
      .limit(50);

    res.json({
      success: true,
      count: deliveries.length,
      data: deliveries
    });
  } catch (error) {
    logger.error('Get webhook deliveries error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/webhooks/:id/test
// @desc    Test webhook with sample payload
// @access  Private
router.post('/:id/test', protect, async (req, res) => {
  try {
    const webhook = await Webhook.findOne({
      _id: req.params.id,
      company: req.user.id
    });

    if (!webhook) {
      return res.status(404).json({ success: false, message: 'Webhook not found' });
    }

    // Send test event
    const testPayload = {
      event: 'test.webhook',
      timestamp: new Date().toISOString(),
      data: {
        message: 'This is a test webhook event'
      }
    };

    // Trigger webhook delivery (async)
    const webhookService = require('../services/webhookService');
    webhookService.triggerWebhook(webhook, testPayload);

    res.json({
      success: true,
      message: 'Test webhook triggered'
    });
  } catch (error) {
    logger.error('Test webhook error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/webhooks/verify
// @desc    Verify webhook signature (for testing)
// @access  Public
router.post('/verify', (req, res) => {
  try {
    const { payload, secret, signature } = req.body;

    const expectedSignature = crypto
      .createHmac('sha256', secret)
      .update(JSON.stringify(payload))
      .digest('hex');

    const isValid = crypto.timingSafeEqual(
      Buffer.from(signature),
      Buffer.from(expectedSignature)
    );

    res.json({
      success: true,
      isValid,
      expectedSignature
    });
  } catch (error) {
    res.status(400).json({ success: false, message: 'Invalid verification request' });
  }
});

// @route   PUT /api/v1/webhooks/:id
// @desc    Update webhook
// @access  Private
router.put('/:id', protect, async (req, res) => {
  try {
    const { name, url, events, status, headers } = req.body;

    const webhook = await Webhook.findOneAndUpdate(
      { _id: req.params.id, company: req.user.id },
      {
        name,
        url,
        events,
        status,
        headers,
        updatedAt: Date.now()
      },
      { new: true }
    ).select('-secret');

    if (!webhook) {
      return res.status(404).json({ success: false, message: 'Webhook not found' });
    }

    res.json({
      success: true,
      message: 'Webhook updated',
      data: webhook
    });
  } catch (error) {
    logger.error('Update webhook error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   DELETE /api/v1/webhooks/:id
// @desc    Delete webhook
// @access  Private
router.delete('/:id', protect, async (req, res) => {
  try {
    const webhook = await Webhook.findOneAndDelete({
      _id: req.params.id,
      company: req.user.id
    });

    if (!webhook) {
      return res.status(404).json({ success: false, message: 'Webhook not found' });
    }

    // Delete associated deliveries
    await WebhookDelivery.deleteMany({ webhook: req.params.id });

    res.json({ success: true, message: 'Webhook deleted' });
  } catch (error) {
    logger.error('Delete webhook error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
