//
/**
 * ============================================
 * 📡 EDI Routes - تبادل البيانات الإلكتروني
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { EDITransaction, TradingPartner, EDIMapping } = require('../models/EDI');
const logger = require('../utils/logger');

// Trading Partners
// @route   GET /api/v1/edi/trading-partners
// @desc    Get trading partners
// @access  Private
router.get('/trading-partners', protect, async (req, res) => {
  try {
    const { status, type } = req.query;

    let query = { company: req.user.company };
    if (status) query.status = status;
    if (type) query.type = type;

    const partners = await TradingPartner.find(query)
      .sort({ name: 1 });

    res.json({
      success: true,
      count: partners.length,
      data: partners
    });

  } catch (error) {
    logger.error('Get trading partners error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/edi/trading-partners
// @desc    Add trading partner
// @access  Private (Admin)
router.post('/trading-partners', protect, authorize(['admin']), async (req, res) => {
  try {
    const partnerId = `TP-${Date.now().toString().slice(-6)}`;

    const partner = await TradingPartner.create({
      ...req.body,
      partnerId,
      company: req.user.company,
      status: 'pending'
    });

    res.status(201).json({
      success: true,
      data: partner
    });

  } catch (error) {
    logger.error('Create trading partner error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/edi/trading-partners/:id/test
// @desc    Update test results
// @access  Private (Admin, Developer)
router.put('/trading-partners/:id/test', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const { testResults } = req.body;

    const partner = await TradingPartner.findByIdAndUpdate(
      req.params.id,
      {
        testResults,
        status: 'testing',
        updatedAt: new Date()
      },
      { new: true }
    );

    if (!partner) {
      return res.status(404).json({ success: false, message: 'Trading partner not found' });
    }

    res.json({
      success: true,
      data: partner
    });

  } catch (error) {
    logger.error('Update test results error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/edi/trading-partners/:id/activate
// @desc    Activate trading partner
// @access  Private (Admin)
router.put('/trading-partners/:id/activate', protect, authorize(['admin']), async (req, res) => {
  try {
    const partner = await TradingPartner.findByIdAndUpdate(
      req.params.id,
      {
        status: 'active',
        'testMode': false,
        updatedAt: new Date()
      },
      { new: true }
    );

    if (!partner) {
      return res.status(404).json({ success: false, message: 'Trading partner not found' });
    }

    res.json({
      success: true,
      message: 'Trading partner activated',
      data: partner
    });

  } catch (error) {
    logger.error('Activate trading partner error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// EDI Transactions
// @route   GET /api/v1/edi/transactions
// @desc    Get EDI transactions
// @access  Private
router.get('/transactions', protect, async (req, res) => {
  try {
    const { type, status, direction, from, to, partner } = req.query;

    let query = { company: req.user.company };
    if (type) query.type = type;
    if (status) query.status = status;
    if (direction) query.direction = direction;
    if (partner) query.tradingPartner = partner;
    if (from && to) {
      query.createdAt = {
        $gte: new Date(from),
        $lte: new Date(to)
      };
    }

    const transactions = await EDITransaction.find(query)
      .populate('tradingPartner', 'name partnerId')
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: transactions.length,
      data: transactions
    });

  } catch (error) {
    logger.error('Get EDI transactions error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/edi/transactions/:id
// @desc    Get transaction details
// @access  Private
router.get('/transactions/:id', protect, async (req, res) => {
  try {
    const transaction = await EDITransaction.findById(req.params.id)
      .populate('tradingPartner', 'name partnerId edi');

    if (!transaction) {
      return res.status(404).json({ success: false, message: 'Transaction not found' });
    }

    res.json({
      success: true,
      data: transaction
    });

  } catch (error) {
    logger.error('Get transaction error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/edi/transactions
// @desc    Create outbound EDI transaction
// @access  Private (Developer, Admin)
router.post('/transactions', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const transactionId = `EDI-${Date.now()}`;

    const transaction = await EDITransaction.create({
      ...req.body,
      transactionId,
      direction: 'outgoing',
      status: 'received',
      company: req.user.company
    });

    // TODO: Trigger EDI processing

    res.status(201).json({
      success: true,
      data: transaction
    });

  } catch (error) {
    logger.error('Create EDI transaction error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/edi/inbound
// @desc    Receive inbound EDI (webhook)
// @access  Public (with signature verification)
router.post('/inbound', async (req, res) => {
  try {
    const { partnerId, data, format } = req.body;

    // Verify trading partner
    const partner = await TradingPartner.findOne({ partnerId });
    if (!partner) {
      return res.status(401).json({ success: false, message: 'Invalid trading partner' });
    }

    // Verify signature if required
    // TODO: Implement signature verification

    const transactionId = `EDI-${Date.now()}`;

    const transaction = await EDITransaction.create({
      transactionId,
      type: req.body.type || '997',
      direction: 'incoming',
      tradingPartner: partner._id,
      rawData: {
        format: format || 'X12',
        content: data,
        originalFilename: req.body.filename
      },
      status: 'received',
      company: partner.company
    });

    // TODO: Trigger async parsing and processing

    res.status(201).json({
      success: true,
      transactionId: transaction.transactionId,
      status: 'received'
    });

  } catch (error) {
    logger.error('Inbound EDI error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/edi/transactions/:id/reprocess
// @desc    Reprocess failed transaction
// @access  Private (Developer, Admin)
router.post('/transactions/:id/reprocess', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const transaction = await EDITransaction.findById(req.params.id);

    if (!transaction) {
      return res.status(404).json({ success: false, message: 'Transaction not found' });
    }

    transaction.status = 'received';
    transaction.errorDetails = null;
    transaction.processing = {
      startedAt: new Date(),
      steps: []
    };
    await transaction.save();

    // TODO: Trigger reprocessing

    res.json({
      success: true,
      message: 'Transaction queued for reprocessing',
      data: transaction
    });

  } catch (error) {
    logger.error('Reprocess transaction error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Mappings
// @route   GET /api/v1/edi/mappings
// @desc    Get EDI mappings
// @access  Private
router.get('/mappings', protect, async (req, res) => {
  try {
    const { standard, transactionType } = req.query;

    let query = { company: req.user.company };
    if (standard) query.standard = standard;
    if (transactionType) query.transactionType = transactionType;

    const mappings = await EDIMapping.find(query);

    res.json({
      success: true,
      count: mappings.length,
      data: mappings
    });

  } catch (error) {
    logger.error('Get mappings error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Dashboard
// @route   GET /api/v1/edi/dashboard
// @desc    EDI dashboard
// @access  Private
router.get('/dashboard', protect, async (req, res) => {
  try {
    const today = new Date();
    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

    const stats = await Promise.all([
      // Active trading partners
      TradingPartner.countDocuments({
        company: req.user.company,
        status: 'active'
      }),

      // Today's transactions
      EDITransaction.countDocuments({
        company: req.user.company,
        createdAt: { $gte: new Date(today.setHours(0, 0, 0, 0)) }
      }),

      // Monthly transactions by type
      EDITransaction.aggregate([
        {
          $match: {
            company: req.user.company._id,
            createdAt: { $gte: startOfMonth }
          }
        },
        {
          $group: {
            _id: '$type',
            count: { $sum: 1 },
            incoming: {
              $sum: { $cond: [{ $eq: ['$direction', 'incoming'] }, 1, 0] }
            },
            outgoing: {
              $sum: { $cond: [{ $eq: ['$direction', 'outgoing'] }, 1, 0] }
            }
          }
        }
      ]),

      // Transaction status breakdown
      EDITransaction.aggregate([
        { $match: { company: req.user.company._id } },
        { $group: { _id: '$status', count: { $sum: 1 } } }
      ]),

      // Recent errors
      EDITransaction.find({
        company: req.user.company,
        status: { $in: ['error', 'rejected'] }
      })
        .populate('tradingPartner', 'name')
        .sort({ createdAt: -1 })
        .limit(5)
    ]);

    res.json({
      success: true,
      data: {
        activePartners: stats[0],
        todayTransactions: stats[1],
        monthlyByType: stats[2],
        statusBreakdown: stats[3],
        recentErrors: stats[4]
      }
    });

  } catch (error) {
    logger.error('EDI dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
