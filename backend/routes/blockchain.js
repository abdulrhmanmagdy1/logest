//
/**
 * ============================================
 * ⛓️ Blockchain Routes - سلسلة الكتل
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { BlockchainTransaction, SmartContract, BlockchainWallet, SupplyChainEvent } = require('../models/Blockchain');
const logger = require('../utils/logger');

// Transactions
// @route   GET /api/v1/blockchain/transactions
// @desc    Get blockchain transactions
// @access  Private
router.get('/transactions', protect, async (req, res) => {
  try {
    const { type, status, entityId, from, to } = req.query;

    let query = { company: req.user.company };
    if (type) query.type = type;
    if (status) query.status = status;
    if (entityId) query.entityId = entityId;
    if (from && to) {
      query.timestamp = {
        $gte: new Date(from),
        $lte: new Date(to)
      };
    }

    const transactions = await BlockchainTransaction.find(query)
      .populate('from.entity', 'firstName lastName name')
      .populate('to.entity', 'firstName lastName name')
      .sort({ timestamp: -1 });

    res.json({
      success: true,
      count: transactions.length,
      data: transactions
    });

  } catch (error) {
    logger.error('Get blockchain transactions error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/blockchain/transactions/:hash
// @desc    Get transaction by hash
// @access  Private
router.get('/transactions/:hash', protect, async (req, res) => {
  try {
    const transaction = await BlockchainTransaction.findOne({
      transactionHash: req.params.hash,
      company: req.user.company
    })
      .populate('from.entity', 'firstName lastName name')
      .populate('to.entity', 'firstName lastName name');

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

// @route   POST /api/v1/blockchain/transactions
// @desc    Create blockchain transaction record
// @access  Private (Developer, Admin)
router.post('/transactions', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const transaction = await BlockchainTransaction.create({
      ...req.body,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: transaction
    });

  } catch (error) {
    logger.error('Create blockchain transaction error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Smart Contracts
// @route   GET /api/v1/blockchain/contracts
// @desc    Get smart contracts
// @access  Private
router.get('/contracts', protect, async (req, res) => {
  try {
    const { status, type } = req.query;

    let query = { company: req.user.company };
    if (status) query.status = status;
    if (type) query.type = type;

    const contracts = await SmartContract.find(query);

    res.json({
      success: true,
      count: contracts.length,
      data: contracts
    });

  } catch (error) {
    logger.error('Get contracts error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/blockchain/contracts
// @desc    Deploy/register smart contract
// @access  Private (Developer, Admin)
router.post('/contracts', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const contract = await SmartContract.create({
      ...req.body,
      deployment: {
        ...req.body.deployment,
        deployedBy: req.user.id
      },
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: contract
    });

  } catch (error) {
    logger.error('Create contract error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Wallets
// @route   GET /api/v1/blockchain/wallets
// @desc    Get wallets
// @access  Private
router.get('/wallets', protect, async (req, res) => {
  try {
    const { type } = req.query;

    let query = { company: req.user.company };
    if (type) query.type = type;

    const wallets = await BlockchainWallet.find(query)
      .populate('owner.entity', 'firstName lastName name');

    res.json({
      success: true,
      count: wallets.length,
      data: wallets
    });

  } catch (error) {
    logger.error('Get wallets error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/blockchain/wallets
// @desc    Create wallet
// @access  Private (Admin)
router.post('/wallets', protect, authorize(['admin']), async (req, res) => {
  try {
    // TODO: Generate wallet using blockchain service
    const address = `0x${Date.now().toString(16)}${Math.random().toString(16).slice(2, 10)}`;

    const wallet = await BlockchainWallet.create({
      ...req.body,
      address,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: wallet
    });

  } catch (error) {
    logger.error('Create wallet error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Supply Chain Events
// @route   GET /api/v1/blockchain/events
// @desc    Get supply chain events
// @access  Private
router.get('/events', protect, async (req, res) => {
  try {
    const { shipment, type } = req.query;

    let query = { company: req.user.company };
    if (shipment) query.shipment = shipment;
    if (type) query.eventType = type;

    const events = await SupplyChainEvent.find(query)
      .populate('shipment', 'trackingNumber')
      .populate('actor', 'firstName lastName name')
      .sort({ timestamp: 1 });

    res.json({
      success: true,
      count: events.length,
      data: events
    });

  } catch (error) {
    logger.error('Get events error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/blockchain/events
// @desc    Record supply chain event
// @access  Private
router.post('/events', protect, async (req, res) => {
  try {
    const eventId = `SC-${Date.now()}`;

    const event = await SupplyChainEvent.create({
      ...req.body,
      eventId,
      actor: req.user.id,
      actorModel: 'User',
      actorName: `${req.user.firstName} ${req.user.lastName}`,
      company: req.user.company
    });

    // TODO: Record on blockchain

    res.status(201).json({
      success: true,
      data: event
    });

  } catch (error) {
    logger.error('Create event error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Verify document/transaction
// @route   POST /api/v1/blockchain/verify
// @desc    Verify blockchain record
// @access  Public
router.post('/verify', async (req, res) => {
  try {
    const { transactionHash, documentHash } = req.body;

    let query = {};
    if (transactionHash) query.transactionHash = transactionHash;
    if (documentHash) query['data.documentHash'] = documentHash;

    const transaction = await BlockchainTransaction.findOne(query);

    if (!transaction) {
      return res.json({
        success: true,
        verified: false,
        message: 'Record not found on blockchain'
      });
    }

    res.json({
      success: true,
      verified: true,
      data: {
        transactionHash: transaction.transactionHash,
        blockNumber: transaction.blockNumber,
        timestamp: transaction.timestamp,
        confirmations: transaction.confirmations,
        status: transaction.status
      }
    });

  } catch (error) {
    logger.error('Verify error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Dashboard
// @route   GET /api/v1/blockchain/dashboard
// @desc    Blockchain dashboard
// @access  Private
router.get('/dashboard', protect, async (req, res) => {
  try {
    const stats = await Promise.all([
      // Total transactions
      BlockchainTransaction.countDocuments({ company: req.user.company }),

      // Transactions by type
      BlockchainTransaction.aggregate([
        { $match: { company: req.user.company._id } },
        { $group: { _id: '$type', count: { $sum: 1 } } }
      ]),

      // Active wallets
      BlockchainWallet.countDocuments({ company: req.user.company }),

      // Deployed contracts
      SmartContract.countDocuments({
        company: req.user.company,
        status: 'deployed'
      }),

      // Recent transactions
      BlockchainTransaction.find({ company: req.user.company })
        .populate('from.entity', 'firstName lastName name')
        .populate('to.entity', 'firstName lastName name')
        .sort({ timestamp: -1 })
        .limit(10)
    ]);

    res.json({
      success: true,
      data: {
        totalTransactions: stats[0],
        transactionsByType: stats[1],
        activeWallets: stats[2],
        deployedContracts: stats[3],
        recentTransactions: stats[4]
      }
    });

  } catch (error) {
    logger.error('Blockchain dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Shipment blockchain trace
// @route   GET /api/v1/blockchain/trace/:shipmentId
// @desc    Get blockchain trace for shipment
// @access  Private
router.get('/trace/:shipmentId', protect, async (req, res) => {
  try {
    const events = await SupplyChainEvent.find({
      shipment: req.params.shipmentId,
      company: req.user.company
    })
      .populate('actor', 'firstName lastName name')
      .sort({ timestamp: 1 });

    const transactions = await BlockchainTransaction.find({
      entityId: req.params.shipmentId,
      company: req.user.company
    }).sort({ timestamp: 1 });

    res.json({
      success: true,
      data: {
        events,
        transactions,
        chainOfCustody: events.map(e => ({
          timestamp: e.timestamp,
          event: e.eventType,
          actor: e.actorName,
          location: e.location,
          verified: e.blockchain.verified,
          transactionHash: e.blockchain.transactionHash
        }))
      }
    });

  } catch (error) {
    logger.error('Trace error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
