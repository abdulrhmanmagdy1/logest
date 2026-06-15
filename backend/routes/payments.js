//
/**
 * ============================================
 * 💳 Payments Routes - نقاط نهاية الدفع
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { body } = require('express-validator');
const { protect } = require('../middleware/auth');
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

// @route   POST /api/v1/payments/process
// @desc    Process a payment
// @access  Private
router.post('/process', protect, [
  body('amount').isFloat({ min: 0.01 }),
  body('currency').isLength({ min: 3, max: 3 }),
  body('method').isIn(['card', 'bank_transfer', 'paypal', 'apple_pay', 'stc_pay']),
  body('invoiceId').optional().isMongoId()
], handleValidationErrors, async (req, res) => {
  try {
    const { amount, currency, method, token, invoiceId, saveCard } = req.body;

    const payment = await paymentService.processPayment({
      amount,
      currency,
      method,
      token,
      customer: {
        id: req.user.id,
        email: req.user.email,
        name: `${req.user.firstName} ${req.user.lastName}`,
        companyId: req.user.id
      },
      invoiceId,
      saveCard
    });

    res.json({
      success: true,
      message: 'Payment processed successfully',
      data: payment
    });
  } catch (error) {
    logger.error('Process payment error:', error);
    res.status(500).json({ 
      success: false, 
      message: 'Payment processing failed',
      error: error.message 
    });
  }
});

// @route   POST /api/v1/payments/refund
// @desc    Process a refund
// @access  Private (Admin, Accountant)
router.post('/refund', protect, [
  body('paymentId').notEmpty(),
  body('amount').optional().isFloat({ min: 0.01 }),
  body('reason').optional().trim()
], handleValidationErrors, async (req, res) => {
  try {
    const { paymentId, amount, reason } = req.body;

    const refund = await paymentService.processRefund(paymentId, amount, reason);

    res.json({
      success: true,
      message: 'Refund processed successfully',
      data: refund
    });
  } catch (error) {
    logger.error('Process refund error:', error);
    res.status(500).json({ 
      success: false, 
      message: 'Refund processing failed' 
    });
  }
});

// @route   GET /api/v1/payments/history
// @desc    Get payment history
// @access  Private
router.get('/history', protect, async (req, res) => {
  try {
    const { limit = 10 } = req.query;

    const history = await paymentService.getPaymentHistory(
      req.user.stripeCustomerId,
      parseInt(limit)
    );

    res.json({
      success: true,
      count: history.length,
      data: history
    });
  } catch (error) {
    logger.error('Get payment history error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/payments/setup-intent
// @desc    Create setup intent for saving card
// @access  Private
router.get('/setup-intent', protect, async (req, res) => {
  try {
    const stripe = require('stripe')(process.env.STRIPE_SECRET_KEY);

    // Get or create customer
    let customerId = req.user.stripeCustomerId;
    if (!customerId) {
      const customer = await stripe.customers.create({
        email: req.user.email,
        name: `${req.user.firstName} ${req.user.lastName}`
      });
      customerId = customer.id;
      
      // Update user with stripe customer id
      const User = require('../models/User');
      await User.findByIdAndUpdate(req.user.id, { stripeCustomerId: customer.id });
    }

    const setupIntent = await stripe.setupIntents.create({
      customer: customerId,
      usage: 'off_session'
    });

    res.json({
      success: true,
      clientSecret: setupIntent.client_secret
    });
  } catch (error) {
    logger.error('Setup intent error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/payments/saved-cards
// @desc    Get saved payment methods
// @access  Private
router.get('/saved-cards', protect, async (req, res) => {
  try {
    if (!req.user.stripeCustomerId) {
      return res.json({ success: true, data: [] });
    }

    const stripe = require('stripe')(process.env.STRIPE_SECRET_KEY);
    
    const paymentMethods = await stripe.paymentMethods.list({
      customer: req.user.stripeCustomerId,
      type: 'card'
    });

    const cards = paymentMethods.data.map(pm => ({
      id: pm.id,
      brand: pm.card.brand,
      last4: pm.card.last4,
      expiryMonth: pm.card.exp_month,
      expiryYear: pm.card.exp_year,
      isDefault: pm.id === req.user.defaultPaymentMethod
    }));

    res.json({
      success: true,
      count: cards.length,
      data: cards
    });
  } catch (error) {
    logger.error('Get saved cards error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   DELETE /api/v1/payments/saved-cards/:cardId
// @desc    Remove saved card
// @access  Private
router.delete('/saved-cards/:cardId', protect, async (req, res) => {
  try {
    const stripe = require('stripe')(process.env.STRIPE_SECRET_KEY);
    
    await stripe.paymentMethods.detach(req.params.cardId);

    res.json({
      success: true,
      message: 'Card removed successfully'
    });
  } catch (error) {
    logger.error('Remove card error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/payments/webhook
// @desc    Stripe webhook handler
// @access  Public (Stripe only)
router.post('/webhook', express.raw({ type: 'application/json' }), async (req, res) => {
  try {
    const stripe = require('stripe')(process.env.STRIPE_SECRET_KEY);
    const sig = req.headers['stripe-signature'];
    
    let event;
    try {
      event = stripe.webhooks.constructEvent(
        req.body,
        sig,
        process.env.STRIPE_WEBHOOK_SECRET
      );
    } catch (err) {
      logger.error('Webhook signature verification failed:', err.message);
      return res.status(400).send(`Webhook Error: ${err.message}`);
    }

    // Handle the event
    await paymentService.handleWebhook(event);

    res.json({ received: true });
  } catch (error) {
    logger.error('Webhook error:', error);
    res.status(500).json({ success: false, message: 'Webhook processing failed' });
  }
});

module.exports = router;
