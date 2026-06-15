/**
 * ============================================
 * 🧾 Vouchers Routes - نظام إدهام
 * Edham Logistics - Receipt & Voucher Management
 * ============================================
 */

const express = require('express');
const router = express.Router();
const VoucherController = require('../controllers/voucherController');
const { auth, authorize } = require('../middleware/auth');
const { validateVoucher } = require('../middleware/validation');
const { ROLES } = require('../config/constants');

// Get all vouchers with filtering
router.get('/',
  auth,
  authorize(ROLES.ACCOUNTANT, ROLES.ADMIN),
  VoucherController.getAll
);

// Get voucher by serial number
router.get('/:serialNumber',
  auth,
  authorize(ROLES.ACCOUNTANT, ROLES.ADMIN, ROLES.CLIENT, ROLES.SUPERVISOR),
  VoucherController.getBySerialNumber
);

// Create receipt voucher (سند قبض)
router.post('/receipt',
  auth,
  authorize(ROLES.ACCOUNTANT, ROLES.ADMIN),
  validateVoucher,
  VoucherController.createReceiptVoucher
);

// Create payment voucher (سند صرف)
router.post('/payment',
  auth,
  authorize(ROLES.ACCOUNTANT, ROLES.ADMIN),
  validateVoucher,
  VoucherController.createPaymentVoucher
);

// Create transfer voucher (حولات)
router.post('/transfer',
  auth,
  authorize(ROLES.ACCOUNTANT, ROLES.ADMIN),
  validateVoucher,
  VoucherController.createTransferVoucher
);

// Update voucher status
router.patch('/:id/status',
  auth,
  authorize(ROLES.ACCOUNTANT, ROLES.ADMIN),
  VoucherController.updateStatus
);

// Cancel voucher
router.patch('/:id/cancel',
  auth,
  authorize(ROLES.ACCOUNTANT, ROLES.ADMIN),
  VoucherController.cancelVoucher
);

// Get voucher statistics
router.get('/statistics/summary',
  auth,
  authorize(ROLES.ACCOUNTANT, ROLES.ADMIN),
  VoucherController.getStatistics
);

// Get client debts and receivables
router.get('/debts/:clientId',
  auth,
  authorize(ROLES.ACCOUNTANT, ROLES.ADMIN),
  VoucherController.getClientDebts
);

// Export vouchers to PDF/Excel
router.get('/export/:format',
  auth,
  authorize(ROLES.ACCOUNTANT, ROLES.ADMIN),
  VoucherController.exportVouchers
);

// Verify voucher authenticity (anti-fraud)
router.post('/verify',
  auth,
  VoucherController.verifyVoucher
);

// Get voucher template
router.get('/template/:type',
  auth,
  authorize(ROLES.ACCOUNTANT, ROLES.ADMIN),
  VoucherController.getTemplate
);

module.exports = router;
