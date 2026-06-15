//
/**
 * ============================================
 * 💎 Subscriptions Routes - نقاط نهاية الاشتراكات
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { body, param } = require('express-validator');
const { protect, authorize } = require('../middleware/auth');
const { Plan, Subscription } = require('../models/Subscription');
const paymentService = require('../services/paymentService');
const logger = require('../utils/logger');

// Validation helper
const handleValidationErrors = (req, res, next) => {
  const errors = require('express-validator').validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ success: false, errors: errors.array() });
  }
  next();
};

// @route   GET /api/v1/subscriptions/plans
// @desc    Get all available plans
// @access  Public
router.get('/plans', async (req, res) => {
  try {
    const plans = await Plan.find({ isActive: true, isPublic: true })
      .sort({ displayOrder: 1 });

    res.json({
      success: true,
      count: plans.length,
      data: plans
    });
  } catch (error) {
    logger.error('Get plans error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/subscriptions/current
// @desc    Get current company subscription
// @access  Private
router.get('/current', protect, async (req, res) => {
  try {
    const subscription = await Subscription.findOne({ company: req.user.id })
      .populate('plan', 'name displayName price features')
      .sort({ createdAt: -1 });

    if (!subscription) {
      return res.json({
        success: true,
        data: null,
        message: 'No active subscription'
      });
    }

    // Check usage limits
    const usageLimits = {
      shipments: subscription.checkUsageLimit('shipments'),
      drivers: subscription.checkUsageLimit('drivers'),
      vehicles: subscription.checkUsageLimit('vehicles')
    };

    res.json({
      success: true,
      data: {
        subscription,
        usageLimits
      }
    });
  } catch (error) {
    logger.error('Get subscription error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/subscriptions/subscribe
// @desc    Subscribe to a plan
// @access  Private
router.post('/subscribe', protect, [
  body('planId').isMongoId(),
  body('billingCycle').isIn(['monthly', 'yearly']),
  body('paymentMethod').isIn(['card', 'bank_transfer', 'paypal', 'stc_pay'])
], handleValidationErrors, async (req, res) => {
  try {
    const { planId, billingCycle, paymentMethod, paymentToken } = req.body;

    // Get plan
    const plan = await Plan.findById(planId);
    if (!plan || !plan.isActive) {
      return res.status(404).json({ success: false, message: 'Plan not found' });
    }

    // Calculate amount
    const amount = billingCycle === 'yearly' ? plan.price.yearly : plan.price.monthly;

    // Check for existing subscription
    const existingSub = await Subscription.findOne({ company: req.user.id });
    if (existingSub && existingSub.status === 'active') {
      return res.status(400).json({
        success: false,
        message: 'Active subscription exists. Please cancel first or upgrade.'
      });
    }

    // Process payment
    const payment = await paymentService.processPayment({
      amount,
      currency: plan.price.currency,
      method: paymentMethod,
      token: paymentToken,
      customer: {
        id: req.user.id,
        email: req.user.email,
        name: `${req.user.firstName} ${req.user.lastName}`,
        companyId: req.user.id
      },
      metadata: {
        planId,
        billingCycle
      }
    });

    if (!payment.success) {
      return res.status(400).json({
        success: false,
        message: 'Payment failed'
      });
    }

    // Create subscription
    const subscription = await Subscription.create({
      company: req.user.id,
      plan: planId,
      planCode: plan.name,
      billingCycle,
      status: 'active',
      trial: {
        isTrial: false,
        trialEndsAt: null
      },
      currentPeriodStart: new Date(),
      payment: {
        method: paymentMethod,
        last4: payment.last4
      }
    });

    res.status(201).json({
      success: true,
      message: 'Subscription created successfully',
      data: subscription
    });
  } catch (error) {
    logger.error('Subscribe error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/subscriptions/upgrade
// @desc    Upgrade subscription plan
// @access  Private
router.post('/upgrade', protect, [
  body('newPlanId').isMongoId()
], handleValidationErrors, async (req, res) => {
  try {
    const { newPlanId } = req.body;

    const subscription = await Subscription.findOne({ company: req.user.id });
    if (!subscription) {
      return res.status(404).json({ success: false, message: 'No active subscription' });
    }

    const newPlan = await Plan.findById(newPlanId);
    if (!newPlan) {
      return res.status(404).json({ success: false, message: 'Plan not found' });
    }

    // Update subscription
    subscription.plan = newPlanId;
    subscription.planCode = newPlan.name;
    await subscription.save();

    res.json({
      success: true,
      message: 'Subscription upgraded successfully',
      data: subscription
    });
  } catch (error) {
    logger.error('Upgrade subscription error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/subscriptions/cancel
// @desc    Cancel subscription
// @access  Private
router.post('/cancel', protect, [
  body('immediate').optional().isBoolean(),
  body('reason').optional().trim()
], handleValidationErrors, async (req, res) => {
  try {
    const { immediate = false, reason } = req.body;

    const subscription = await Subscription.findOne({ company: req.user.id });
    if (!subscription) {
      return res.status(404).json({ success: false, message: 'No active subscription' });
    }

    subscription.cancellation = {
      requestedAt: new Date(),
      reason,
      willCancelAt: immediate ? new Date() : subscription.currentPeriodEnd,
      cancelledBy: req.user.id
    };
    subscription.autoRenew = false;

    if (immediate) {
      subscription.status = 'cancelled';
    }

    await subscription.save();

    res.json({
      success: true,
      message: immediate ? 'Subscription cancelled' : 'Subscription will cancel at period end',
      data: subscription
    });
  } catch (error) {
    logger.error('Cancel subscription error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/subscriptions/invoices
// @desc    Get billing history
// @access  Private
router.get('/invoices', protect, async (req, res) => {
  try {
    const subscription = await Subscription.findOne({ company: req.user.id })
      .select('invoices');

    if (!subscription) {
      return res.status(404).json({ success: false, message: 'No subscription found' });
    }

    res.json({
      success: true,
      count: subscription.invoices.length,
      data: subscription.invoices
    });
  } catch (error) {
    logger.error('Get invoices error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/subscriptions/usage
// @desc    Get usage statistics
// @access  Private
router.get('/usage', protect, async (req, res) => {
  try {
    const subscription = await Subscription.findOne({ company: req.user.id })
      .populate('plan', 'features limits');

    if (!subscription) {
      return res.status(404).json({ success: false, message: 'No subscription found' });
    }

    const usageStats = {
      shipments: subscription.checkUsageLimit('shipments'),
      drivers: subscription.checkUsageLimit('drivers'),
      vehicles: subscription.checkUsageLimit('vehicles'),
      storage: {
        used: subscription.usage.storageUsed,
        limit: subscription.plan.limits.storageGB,
        percentage: (subscription.usage.storageUsed / subscription.plan.limits.storageGB) * 100
      },
      apiCalls: {
        today: subscription.usage.apiCalls,
        limit: subscription.plan.limits.apiCallsPerDay
      }
    };

    res.json({
      success: true,
      data: {
        usage: subscription.usage,
        limits: usageStats,
        periodStart: subscription.currentPeriodStart,
        periodEnd: subscription.currentPeriodEnd
      }
    });
  } catch (error) {
    logger.error('Get usage error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
