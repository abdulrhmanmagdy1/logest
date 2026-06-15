//
/**
 * ============================================
 * 📑 Contract & Tender Routes - العقود والمناقصات
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const Contract = require('../models/Contract');
const { Tender } = require('../models/Contract');
const logger = require('../utils/logger');

// @route   GET /api/v1/contracts
// @desc    Get all contracts
// @access  Private
router.get('/', protect, async (req, res) => {
  try {
    const { status, type, expiryFrom, expiryTo } = req.query;
    
    let query = { company: req.user.company };
    if (status) query.status = status;
    if (type) query.type = type;
    if (expiryFrom || expiryTo) {
      query.expiryDate = {};
      if (expiryFrom) query.expiryDate.$gte = new Date(expiryFrom);
      if (expiryTo) query.expiryDate.$lte = new Date(expiryTo);
    }

    const contracts = await Contract.find(query)
      .sort({ expiryDate: 1 });

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

// @route   POST /api/v1/contracts
// @desc    Create contract
// @access  Private (Admin, Legal)
router.post('/', protect, authorize(['admin', 'supervisor']), async (req, res) => {
  try {
    const contract = await Contract.create({
      ...req.body,
      company: req.user.company,
      createdBy: req.user.id
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

// @route   GET /api/v1/contracts/expiring
// @desc    Get expiring contracts
// @access  Private
router.get('/expiring', protect, async (req, res) => {
  try {
    const { days = 30 } = req.query;
    const expiryDate = new Date();
    expiryDate.setDate(expiryDate.getDate() + parseInt(days));

    const contracts = await Contract.find({
      company: req.user.company,
      status: 'active',
      expiryDate: { $lte: expiryDate }
    }).sort({ expiryDate: 1 });

    res.json({
      success: true,
      count: contracts.length,
      data: contracts.map(c => ({
        ...c.toObject(),
        daysUntilExpiry: c.daysUntilExpiry()
      }))
    });

  } catch (error) {
    logger.error('Get expiring contracts error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/contracts/:id/sign
// @desc    Sign contract
// @access  Private
router.post('/:id/sign', protect, async (req, res) => {
  try {
    const contract = await Contract.findById(req.params.id);

    if (!contract) {
      return res.status(404).json({ success: false, message: 'Contract not found' });
    }

    const { party, ipAddress } = req.body;

    if (party === 'firstParty') {
      contract.signatures.firstParty = {
        signedBy: req.user.id,
        signedAt: new Date(),
        ipAddress
      };
    } else if (party === 'secondParty') {
      contract.signatures.secondParty = {
        signedBy: req.user.id,
        signedAt: new Date(),
        ipAddress
      };
    }

    // Check if both parties signed
    if (contract.signatures.firstParty.signedBy && contract.signatures.secondParty.signedBy) {
      contract.status = 'active';
    } else {
      contract.status = 'pending_signature';
    }

    await contract.save();

    res.json({
      success: true,
      message: 'Contract signed successfully',
      data: contract
    });

  } catch (error) {
    logger.error('Sign contract error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Tenders
// @route   GET /api/v1/contracts/tenders
// @desc    Get all tenders
// @access  Private
router.get('/tenders', protect, async (req, res) => {
  try {
    const { status, category } = req.query;
    
    let query = { company: req.user.company };
    if (status) query.status = status;
    if (category) query.category = category;

    const tenders = await Tender.find(query)
      .populate('bids.bidder', 'name')
      .populate('awardedTo.company', 'name')
      .sort({ 'timeline.submissionDeadline': 1 });

    res.json({
      success: true,
      count: tenders.length,
      data: tenders
    });

  } catch (error) {
    logger.error('Get tenders error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/contracts/tenders
// @desc    Create tender
// @access  Private (Admin, Procurement)
router.post('/tenders', protect, authorize(['admin', 'supervisor']), async (req, res) => {
  try {
    const tender = await Tender.create({
      ...req.body,
      company: req.user.company,
      createdBy: req.user.id
    });

    res.status(201).json({
      success: true,
      data: tender
    });

  } catch (error) {
    logger.error('Create tender error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/contracts/tenders/:id/bid
// @desc    Submit bid
// @access  Private
router.post('/tenders/:id/bid', protect, async (req, res) => {
  try {
    const { amount, technicalProposal, commercialProposal, validityPeriod } = req.body;

    const tender = await Tender.findByIdAndUpdate(
      req.params.id,
      {
        $push: {
          bids: {
            bidder: req.user.company,
            amount,
            technicalProposal,
            commercialProposal,
            validityPeriod,
            submittedAt: new Date()
          }
        }
      },
      { new: true }
    );

    if (!tender) {
      return res.status(404).json({ success: false, message: 'Tender not found' });
    }

    res.json({
      success: true,
      message: 'Bid submitted successfully',
      data: tender
    });

  } catch (error) {
    logger.error('Submit bid error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/contracts/tenders/:id/award
// @desc    Award tender
// @access  Private (Admin)
router.put('/tenders/:id/award', protect, authorize(['admin']), async (req, res) => {
  try {
    const { bidderId, amount } = req.body;

    const tender = await Tender.findByIdAndUpdate(
      req.params.id,
      {
        status: 'awarded',
        awardedTo: {
          company: bidderId,
          amount,
          awardedAt: new Date()
        }
      },
      { new: true }
    );

    if (!tender) {
      return res.status(404).json({ success: false, message: 'Tender not found' });
    }

    res.json({
      success: true,
      message: 'Tender awarded successfully',
      data: tender
    });

  } catch (error) {
    logger.error('Award tender error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
