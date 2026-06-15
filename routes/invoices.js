/**
 * ============================================
 * 💰 Invoices Routes - نظام إدهام
 * Edham Logistics - Invoice API with Controllers
 * ============================================
 */

const express = require('express');
const router = express.Router();
const InvoiceController = require('../controllers/invoiceController');
const { auth, authorize } = require('../middleware/auth');
const { validateInvoice, validatePayment } = require('../middleware/validation');
const { ROLES } = require('../config/constants');

// Get all invoices
router.get('/', auth, InvoiceController.getAll);

// Get invoice statistics
router.get('/statistics', auth, InvoiceController.getStatistics);

// Create invoice
router.post('/',
  auth,
  authorize(ROLES.ACCOUNTANT, ROLES.ADMIN),
  validateInvoice,
  InvoiceController.create
);

// Record payment
router.post('/:id/payment',
  auth,
  authorize(ROLES.ACCOUNTANT, ROLES.ADMIN),
  validatePayment,
  InvoiceController.recordPayment
);

// Verify invoice (public)
router.post('/verify', InvoiceController.verify);

module.exports = router;
