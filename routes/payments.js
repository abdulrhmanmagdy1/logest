/**
 * ============================================
 * 💳 Payments Routes - نظام إدهام
 * Edham Logistics - Payment Processing API
 * ============================================
 */

const express = require('express');
const router = express.Router();
const PaymentController = require('../controllers/paymentController');
const { auth, authorize } = require('../middleware/auth');
const { validatePayment } = require('../middleware/validation');
const { ROLES } = require('../config/constants');

// Get client balance
router.get('/balance', 
  auth, 
  authorize(ROLES.CLIENT),
  PaymentController.getBalance
);

// Add funds to client balance
router.post('/add-funds',
  auth,
  authorize(ROLES.CLIENT),
  PaymentController.addFunds
);

// Process payment for order (multiple payment methods)
router.post('/process',
  auth,
  validatePayment,
  PaymentController.processPayment
);

// Get payment history
router.get('/history', auth, PaymentController.getHistory);

// Get available payment methods
router.get('/methods', auth, PaymentController.getPaymentMethods);

// Create payment intent (for credit cards)
router.post('/create-payment-intent', auth, PaymentController.createPaymentIntent);

// Confirm payment
router.post('/confirm-payment', auth, PaymentController.confirmPayment);

// Create refund
router.post('/refund', auth, PaymentController.createRefund);

// Get payment statistics (accountant/admin)
router.get('/statistics',
  auth,
  authorize(ROLES.ACCOUNTANT, ROLES.ADMIN),
  PaymentController.getStatistics
);

// Webhook handler (No auth required - Stripe sends directly)
router.post('/webhook', express.raw({ type: 'application/json' }), PaymentController.webhook);

module.exports = router;
