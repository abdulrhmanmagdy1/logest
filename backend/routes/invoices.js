/**
 * ============================================
 * 🧾 Invoices Routes - نظام إدهام
 * Invoice and billing endpoints
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { body } = require('express-validator');
const { protect, authorize } = require('../middleware/auth');
const Invoice = require('../models/Invoice');
const Shipment = require('../models/Shipment');
const logger = require('../utils/logger');

// Validation helper
const handleValidationErrors = (req, res, next) => {
  const errors = require('express-validator').validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ success: false, errors: errors.array() });
  }
  next();
};

// @route   GET /api/v1/invoices
// @desc    Get all invoices
// @access  Private
router.get('/', protect, async (req, res) => {
  try {
    const { status, client, startDate, endDate, page = 1, limit = 20 } = req.query;
    
    const filter = {};
    
    // Role-based filtering
    if (req.user.role === 'client') {
      filter.client = req.user.id;
    } else if (['accountant', 'admin', 'supervisor'].includes(req.user.role)) {
      if (client) filter.client = client;
    }
    
    if (status) filter.status = status;
    if (startDate || endDate) {
      filter.issueDate = {};
      if (startDate) filter.issueDate.$gte = new Date(startDate);
      if (endDate) filter.issueDate.$lte = new Date(endDate);
    }

    const skip = (parseInt(page) - 1) * parseInt(limit);
    
    const invoices = await Invoice.find(filter)
      .populate('client', 'firstName lastName companyName')
      .populate('shipments', 'trackingNumber status')
      .sort('-issueDate')
      .skip(skip)
      .limit(parseInt(limit));

    const total = await Invoice.countDocuments(filter);

    // Calculate summary
    const summary = await Invoice.aggregate([
      { $match: filter },
      {
        $group: {
          _id: null,
          totalAmount: { $sum: '$total' },
          paidAmount: { $sum: '$payment.paidAmount' },
          pendingAmount: { $sum: '$payment.remainingAmount' }
        }
      }
    ]);

    res.json({
      success: true,
      count: invoices.length,
      total,
      page: parseInt(page),
      pages: Math.ceil(total / parseInt(limit)),
      summary: summary[0] || { totalAmount: 0, paidAmount: 0, pendingAmount: 0 },
      data: invoices
    });
  } catch (error) {
    logger.error('Get invoices error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/invoices/:id
// @desc    Get single invoice
// @access  Private
router.get('/:id', protect, async (req, res) => {
  try {
    const invoice = await Invoice.findById(req.params.id)
      .populate('client', 'firstName lastName email phone companyName address vatNumber')
      .populate('shipments');
    
    if (!invoice) {
      return res.status(404).json({ success: false, message: 'Invoice not found' });
    }

    // Check authorization
    if (req.user.role === 'client' && invoice.client._id.toString() !== req.user.id) {
      return res.status(403).json({ success: false, message: 'Not authorized' });
    }

    res.json({ success: true, data: invoice });
  } catch (error) {
    logger.error('Get invoice error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/invoices
// @desc    Create invoice
// @access  Private (Admin, Accountant, Supervisor)
router.post('/', protect, authorize('admin', 'accountant', 'supervisor'), [
  body('client').isMongoId(),
  body('shipments').isArray(),
  body('dueDate').isISO8601(),
  body('items').isArray()
], handleValidationErrors, async (req, res) => {
  try {
    const { client, shipments, items, dueDate, notes } = req.body;
    
    // Validate shipments belong to client
    const shipmentDocs = await Shipment.find({
      _id: { $in: shipments },
      client: client
    });
    
    if (shipmentDocs.length !== shipments.length) {
      return res.status(400).json({ 
        success: false, 
        message: 'Some shipments do not belong to this client or do not exist' 
      });
    }

    // Create invoice
    const invoice = await Invoice.create({
      client,
      shipments,
      items,
      dueDate: new Date(dueDate),
      notes,
      createdBy: req.user.id,
      status: 'draft'
    });

    // Update shipments with invoice reference
    await Shipment.updateMany(
      { _id: { $in: shipments } },
      { 
        'invoice.number': invoice.invoiceNumber,
        'invoice.generatedAt': invoice.issueDate,
        'invoice.dueDate': invoice.dueDate
      }
    );

    logger.info(`Invoice created: ${invoice.invoiceNumber} by ${req.user.id}`);
    
    res.status(201).json({
      success: true,
      message: 'Invoice created successfully',
      data: invoice
    });
  } catch (error) {
    logger.error('Create invoice error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/invoices/:id/status
// @desc    Update invoice status
// @access  Private
router.put('/:id/status', protect, [
  body('status').isIn(['draft', 'sent', 'paid', 'partial', 'cancelled'])
], handleValidationErrors, async (req, res) => {
  try {
    const invoice = await Invoice.findById(req.params.id);
    if (!invoice) {
      return res.status(404).json({ success: false, message: 'Invoice not found' });
    }

    // Authorization
    const canUpdate = ['admin', 'accountant', 'supervisor'].includes(req.user.role) ||
                      (req.user.role === 'client' && invoice.client.toString() === req.user.id && 
                       req.body.status === 'paid');
    
    if (!canUpdate) {
      return res.status(403).json({ success: false, message: 'Not authorized' });
    }

    invoice.status = req.body.status;
    
    if (req.body.status === 'sent') {
      invoice.sentAt = new Date();
      invoice.sentBy = req.user.id;
    }
    
    await invoice.save();

    logger.info(`Invoice ${invoice.invoiceNumber} status updated to ${req.body.status}`);

    res.json({ success: true, message: 'Invoice status updated', data: invoice });
  } catch (error) {
    logger.error('Update invoice status error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/invoices/:id/payment
// @desc    Add payment to invoice
// @access  Private (Admin, Accountant)
router.post('/:id/payment', protect, authorize('admin', 'accountant'), [
  body('amount').isFloat({ min: 0 }),
  body('method').isIn(['cash', 'credit_card', 'bank_transfer', 'cheque', 'wallet', 'online']),
  body('reference').optional().trim()
], handleValidationErrors, async (req, res) => {
  try {
    const { amount, method, reference, notes } = req.body;
    
    const invoice = await Invoice.findById(req.params.id);
    if (!invoice) {
      return res.status(404).json({ success: false, message: 'Invoice not found' });
    }

    // Add payment
    await invoice.addPayment(amount, method, reference, notes);

    logger.info(`Payment of ${amount} added to invoice ${invoice.invoiceNumber}`);

    res.json({ 
      success: true, 
      message: 'Payment added successfully', 
      data: invoice 
    });
  } catch (error) {
    logger.error('Add payment error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/invoices/dashboard
// @desc    Get invoice dashboard stats
// @access  Private (Admin, Accountant, Supervisor)
router.get('/dashboard/stats', protect, authorize('admin', 'accountant', 'supervisor'), async (req, res) => {
  try {
    const today = new Date();
    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
    const endOfMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0);

    const stats = await Invoice.aggregate([
      {
        $group: {
          _id: null,
          totalInvoices: { $sum: 1 },
          totalRevenue: { $sum: '$total' },
          paidAmount: { $sum: '$payment.paidAmount' },
          pendingAmount: { $sum: '$payment.remainingAmount' },
          overdueInvoices: {
            $sum: {
              $cond: [
                { $and: [
                  { $lt: ['$dueDate', today] },
                  { $ne: ['$status', 'paid'] },
                  { $ne: ['$status', 'cancelled'] }
                ]},
                1,
                0
              ]
            }
          }
        }
      }
    ]);

    const monthlyRevenue = await Invoice.aggregate([
      {
        $match: {
          issueDate: { $gte: startOfMonth, $lte: endOfMonth },
          status: { $in: ['paid', 'partial'] }
        }
      },
      {
        $group: {
          _id: null,
          revenue: { $sum: '$payment.paidAmount' }
        }
      }
    ]);

    const statusCounts = await Invoice.aggregate([
      {
        $group: {
          _id: '$status',
          count: { $sum: 1 },
          amount: { $sum: '$total' }
        }
      }
    ]);

    res.json({
      success: true,
      data: {
        overview: stats[0] || {},
        monthlyRevenue: monthlyRevenue[0]?.revenue || 0,
        statusBreakdown: statusCounts
      }
    });
  } catch (error) {
    logger.error('Get dashboard stats error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
