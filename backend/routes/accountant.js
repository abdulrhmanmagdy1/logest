//
/**
 * ============================================
 * 💰 Accountant Routes - نظام إدهام
 * Financial and accounting endpoints
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const Invoice = require('../models/Invoice');
const Shipment = require('../models/Shipment');
const User = require('../models/User');
const logger = require('../utils/logger');

// @route   GET /api/v1/accountant/dashboard
// @desc    Get accountant dashboard stats
// @access  Private (Accountant, Admin)
router.get('/dashboard', protect, authorize('accountant', 'admin', 'supervisor'), async (req, res) => {
  try {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

    // Today's revenue
    const todayRevenue = await Invoice.aggregate([
      {
        $match: {
          createdAt: { $gte: today },
          status: { $in: ['paid', 'partially_paid'] }
        }
      },
      { $group: { _id: null, total: { $sum: '$amountPaid' } } }
    ]);

    // Pending invoices count
    const pendingInvoices = await Invoice.countDocuments({ status: 'pending' });

    // Unpaid debts (clients with pending payments)
    const debts = await Invoice.aggregate([
      { $match: { status: { $in: ['pending', 'partially_paid', 'overdue'] } } },
      {
        $group: {
          _id: '$client',
          totalDebt: { $sum: '$balance' }
        }
      },
      { $match: { totalDebt: { $gt: 0 } } }
    ]);

    // Driver settlements pending
    const driverSettlements = await User.countDocuments({
      role: 'driver',
      'driverInfo.pendingSettlement': { $gt: 0 }
    });

    // Recent transactions
    const recentTransactions = await Invoice.find()
      .sort({ createdAt: -1 })
      .limit(10)
      .populate('client', 'firstName lastName company.name')
      .populate('shipment', 'trackingNumber');

    res.json({
      success: true,
      data: {
        todayRevenue: todayRevenue[0]?.total || 0,
        pendingInvoices,
        totalDebts: debts.length,
        debtsAmount: debts.reduce((sum, d) => sum + d.totalDebt, 0),
        pendingSettlements: driverSettlements,
        recentTransactions
      }
    });
  } catch (error) {
    logger.error('Get accountant dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/accountant/invoices
// @desc    Get all invoices with filters
// @access  Private (Accountant, Admin)
router.get('/invoices', protect, authorize('accountant', 'admin'), async (req, res) => {
  try {
    const { status, client, startDate, endDate, page = 1, limit = 20 } = req.query;
    
    let query = {};
    if (status) query.status = status;
    if (client) query.client = client;
    if (startDate && endDate) {
      query.createdAt = {
        $gte: new Date(startDate),
        $lte: new Date(endDate)
      };
    }

    const invoices = await Invoice.find(query)
      .populate('client', 'firstName lastName email company.name')
      .populate('shipment', 'trackingNumber cargo.type')
      .populate('createdBy', 'firstName lastName')
      .sort({ createdAt: -1 })
      .limit(limit * 1)
      .skip((page - 1) * limit);

    const count = await Invoice.countDocuments(query);

    res.json({
      success: true,
      count: invoices.length,
      total: count,
      totalPages: Math.ceil(count / limit),
      currentPage: page,
      data: invoices
    });
  } catch (error) {
    logger.error('Get invoices error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/accountant/invoices
// @desc    Create new invoice
// @access  Private (Accountant, Admin)
router.post('/invoices', protect, authorize('accountant', 'admin'), async (req, res) => {
  try {
    const { client, shipment, items, dueDate, notes } = req.body;

    // Calculate totals
    const subtotal = items.reduce((sum, item) => sum + (item.quantity * item.unitPrice), 0);
    const tax = subtotal * 0.15; // 15% VAT
    const total = subtotal + tax;

    const invoice = await Invoice.create({
      client,
      shipment,
      invoiceNumber: `INV-${Date.now()}`,
      items,
      subtotal,
      tax,
      total,
      balance: total,
      dueDate: dueDate ? new Date(dueDate) : new Date(Date.now() + 30 * 24 * 60 * 60 * 1000),
      status: 'pending',
      notes,
      createdBy: req.user.id
    });

    await invoice.populate('client shipment');

    res.status(201).json({
      success: true,
      message: 'تم إنشاء الفاتورة بنجاح',
      data: invoice
    });
  } catch (error) {
    logger.error('Create invoice error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/accountant/invoices/:id/payment
// @desc    Record payment for invoice
// @access  Private (Accountant, Admin)
router.post('/invoices/:id/payment', protect, authorize('accountant', 'admin'), async (req, res) => {
  try {
    const { amount, method, reference, notes } = req.body;
    
    const invoice = await Invoice.findById(req.params.id);
    if (!invoice) {
      return res.status(404).json({ success: false, message: 'الفاتورة غير موجودة' });
    }

    // Add payment
    invoice.payments.push({
      amount,
      method,
      reference,
      notes,
      date: new Date(),
      receivedBy: req.user.id
    });

    // Update invoice status
    invoice.amountPaid += amount;
    invoice.balance = invoice.total - invoice.amountPaid;
    
    if (invoice.balance <= 0) {
      invoice.status = 'paid';
    } else if (invoice.amountPaid > 0) {
      invoice.status = 'partially_paid';
    }

    await invoice.save();

    res.json({
      success: true,
      message: 'تم تسجيل الدفعة بنجاح',
      data: invoice
    });
  } catch (error) {
    logger.error('Record payment error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/accountant/debts
// @desc    Get all client debts
// @access  Private (Accountant, Admin)
router.get('/debts', protect, authorize('accountant', 'admin'), async (req, res) => {
  try {
    const debts = await Invoice.aggregate([
      { $match: { status: { $in: ['pending', 'partially_paid', 'overdue'] } } },
      {
        $group: {
          _id: '$client',
          totalDebt: { $sum: '$balance' },
          invoices: { $sum: 1 },
          oldestDueDate: { $min: '$dueDate' }
        }
      },
      { $match: { totalDebt: { $gt: 0 } } },
      { $sort: { totalDebt: -1 } }
    ]);

    // Populate client info
    await User.populate(debts, { path: '_id', select: 'firstName lastName company.name phone' });

    res.json({
      success: true,
      count: debts.length,
      totalAmount: debts.reduce((sum, d) => sum + d.totalDebt, 0),
      data: debts
    });
  } catch (error) {
    logger.error('Get debts error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/accountant/driver-settlements
// @desc    Get driver settlements
// @access  Private (Accountant, Admin)
router.get('/driver-settlements', protect, authorize('accountant', 'admin'), async (req, res) => {
  try {
    const { status = 'pending' } = req.query;

    const drivers = await User.find({
      role: 'driver',
      'driverInfo.pendingSettlement': status === 'pending' ? { $gt: 0 } : { $gte: 0 }
    }).select('firstName lastName driverInfo');

    // Get trip details for each driver
    const settlements = await Promise.all(drivers.map(async (driver) => {
      const trips = await Shipment.find({
        driver: driver._id,
        status: { $in: ['delivered', 'completed'] },
        'pricing.driverSettlement.status': status
      }).select('trackingNumber pricing.driverSettlement createdAt');

      return {
        driver: {
          id: driver._id,
          name: `${driver.firstName} ${driver.lastName}`,
          rating: driver.driverInfo?.rating || 0,
          totalTrips: driver.driverInfo?.totalTrips || 0
        },
        pendingAmount: driver.driverInfo?.pendingSettlement || 0,
        trips: trips.length,
        tripDetails: trips
      };
    }));

    res.json({
      success: true,
      data: settlements.filter(s => s.pendingAmount > 0 || status !== 'pending')
    });
  } catch (error) {
    logger.error('Get driver settlements error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/accountant/driver-settlements/:driverId
// @desc    Process driver settlement
// @access  Private (Accountant, Admin)
router.post('/driver-settlements/:driverId', protect, authorize('accountant', 'admin'), async (req, res) => {
  try {
    const { amount, method, reference, notes } = req.body;
    const driverId = req.params.driverId;

    const driver = await User.findById(driverId);
    if (!driver || driver.role !== 'driver') {
      return res.status(404).json({ success: false, message: 'السائق غير موجود' });
    }

    // Create settlement record
    const settlement = {
      amount,
      method,
      reference,
      notes,
      date: new Date(),
      processedBy: req.user.id
    };

    // Update driver info
    driver.driverInfo.settlements = driver.driverInfo.settlements || [];
    driver.driverInfo.settlements.push(settlement);
    driver.driverInfo.pendingSettlement = (driver.driverInfo.pendingSettlement || 0) - amount;
    driver.driverInfo.totalEarnings = (driver.driverInfo.totalEarnings || 0) + amount;

    await driver.save();

    res.json({
      success: true,
      message: 'تمت تسوية السائق بنجاح',
      data: settlement
    });
  } catch (error) {
    logger.error('Process settlement error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/accountant/reports/financial
// @desc    Get financial reports
// @access  Private (Accountant, Admin)
router.get('/reports/financial', protect, authorize('accountant', 'admin'), async (req, res) => {
  try {
    const { startDate, endDate, groupBy = 'day' } = req.query;
    
    const start = startDate ? new Date(startDate) : new Date(Date.now() - 30 * 24 * 60 * 60 * 1000);
    const end = endDate ? new Date(endDate) : new Date();

    let dateFormat;
    switch (groupBy) {
      case 'month':
        dateFormat = '%Y-%m';
        break;
      case 'week':
        dateFormat = '%Y-W%U';
        break;
      default:
        dateFormat = '%Y-%m-%d';
    }

    const revenue = await Invoice.aggregate([
      {
        $match: {
          createdAt: { $gte: start, $lte: end },
          status: { $in: ['paid', 'partially_paid'] }
        }
      },
      {
        $group: {
          _id: { $dateToString: { format: dateFormat, date: '$createdAt' } },
          revenue: { $sum: '$amountPaid' },
          invoices: { $sum: 1 }
        }
      },
      { $sort: { _id: 1 } }
    ]);

    // Get expense data (driver settlements)
    const expenses = await User.aggregate([
      { $match: { role: 'driver' } },
      { $unwind: '$driverInfo.settlements' },
      {
        $match: {
          'driverInfo.settlements.date': { $gte: start, $lte: end }
        }
      },
      {
        $group: {
          _id: { $dateToString: { format: dateFormat, date: '$driverInfo.settlements.date' } },
          expenses: { $sum: '$driverInfo.settlements.amount' }
        }
      },
      { $sort: { _id: 1 } }
    ]);

    res.json({
      success: true,
      data: {
        period: { start, end },
        revenue,
        expenses,
        summary: {
          totalRevenue: revenue.reduce((sum, r) => sum + r.revenue, 0),
          totalExpenses: expenses.reduce((sum, e) => sum + e.expenses, 0),
          totalInvoices: revenue.reduce((sum, r) => sum + r.invoices, 0)
        }
      }
    });
  } catch (error) {
    logger.error('Get financial report error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
