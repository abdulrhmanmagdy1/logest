//
/**
 * ============================================
 * 👥 CRM Routes - إدارة علاقات العملاء
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { Lead, Opportunity, Campaign, Interaction } = require('../models/CRM');
const logger = require('../utils/logger');

// @route   GET /api/v1/crm/leads
// @desc    Get all leads
// @access  Private (Sales, Admin)
router.get('/leads', protect, authorize(['sales', 'admin', 'supervisor']), async (req, res) => {
  try {
    const { status, priority, assignedTo, source } = req.query;
    
    let query = {};
    if (status) query.status = status;
    if (priority) query.priority = priority;
    if (assignedTo) query.assignedTo = assignedTo;
    if (source) query.source = source;

    const leads = await Lead.find(query)
      .populate('assignedTo', 'firstName lastName email')
      .populate('customerId', 'firstName lastName email')
      .sort({ priority: -1, createdAt: -1 });

    res.json({
      success: true,
      count: leads.length,
      data: leads
    });

  } catch (error) {
    logger.error('Get leads error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/crm/leads
// @desc    Create new lead
// @access  Private (Sales, Admin)
router.post('/leads', protect, authorize(['sales', 'admin']), async (req, res) => {
  try {
    const lead = await Lead.create({
      ...req.body,
      createdAt: new Date()
    });

    res.status(201).json({
      success: true,
      data: lead
    });

  } catch (error) {
    logger.error('Create lead error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/crm/leads/:id/convert
// @desc    Convert lead to customer
// @access  Private (Sales, Admin)
router.put('/leads/:id/convert', protect, authorize(['sales', 'admin']), async (req, res) => {
  try {
    const { customerId } = req.body;
    
    const lead = await Lead.findByIdAndUpdate(
      req.params.id,
      {
        status: 'won',
        convertedToCustomer: true,
        customerId,
        updatedAt: new Date()
      },
      { new: true }
    );

    if (!lead) {
      return res.status(404).json({ success: false, message: 'Lead not found' });
    }

    res.json({
      success: true,
      message: 'Lead converted successfully',
      data: lead
    });

  } catch (error) {
    logger.error('Convert lead error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/crm/opportunities
// @desc    Get all opportunities
// @access  Private (Sales, Admin)
router.get('/opportunities', protect, authorize(['sales', 'admin', 'supervisor']), async (req, res) => {
  try {
    const { stage, customer, assignedTo } = req.query;
    
    let query = {};
    if (stage) query.stage = stage;
    if (customer) query.customer = customer;
    if (assignedTo) query.assignedTo = assignedTo;

    const opportunities = await Opportunity.find(query)
      .populate('customer', 'firstName lastName email companyName')
      .populate('assignedTo', 'firstName lastName')
      .populate('lead', 'firstName lastName email')
      .sort({ 'value.amount': -1 });

    // Calculate pipeline totals
    const pipeline = await Opportunity.aggregate([
      { $match: { stage: { $nin: ['closed_won', 'closed_lost'] } } },
      {
        $group: {
          _id: '$stage',
          count: { $sum: 1 },
          value: { $sum: '$value.amount' }
        }
      }
    ]);

    res.json({
      success: true,
      count: opportunities.length,
      pipeline,
      data: opportunities
    });

  } catch (error) {
    logger.error('Get opportunities error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/crm/opportunities
// @desc    Create opportunity
// @access  Private (Sales, Admin)
router.post('/opportunities', protect, authorize(['sales', 'admin']), async (req, res) => {
  try {
    const opportunity = await Opportunity.create(req.body);

    res.status(201).json({
      success: true,
      data: opportunity
    });

  } catch (error) {
    logger.error('Create opportunity error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/crm/opportunities/:id/stage
// @desc    Update opportunity stage
// @access  Private (Sales, Admin)
router.put('/opportunities/:id/stage', protect, authorize(['sales', 'admin']), async (req, res) => {
  try {
    const { stage, probability } = req.body;
    
    const updateData = { stage, updatedAt: new Date() };
    if (probability) updateData.probability = probability;
    
    if (stage === 'closed_won' || stage === 'closed_lost') {
      updateData.actualCloseDate = new Date();
    }

    const opportunity = await Opportunity.findByIdAndUpdate(
      req.params.id,
      updateData,
      { new: true }
    );

    if (!opportunity) {
      return res.status(404).json({ success: false, message: 'Opportunity not found' });
    }

    res.json({
      success: true,
      data: opportunity
    });

  } catch (error) {
    logger.error('Update opportunity stage error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/crm/dashboard
// @desc    CRM dashboard stats
// @access  Private (Sales, Admin)
router.get('/dashboard', protect, authorize(['sales', 'admin', 'supervisor']), async (req, res) => {
  try {
    const today = new Date();
    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

    // Lead stats
    const leadStats = await Lead.aggregate([
      {
        $group: {
          _id: '$status',
          count: { $sum: 1 }
        }
      }
    ]);

    // Opportunity stats
    const opportunityStats = await Opportunity.aggregate([
      {
        $group: {
          _id: '$stage',
          count: { $sum: 1 },
          value: { $sum: '$value.amount' }
        }
      }
    ]);

    // Monthly conversion
    const monthlyConversions = await Lead.countDocuments({
      status: 'won',
      updatedAt: { $gte: startOfMonth }
    });

    // Pipeline value
    const pipelineValue = await Opportunity.aggregate([
      { $match: { stage: { $nin: ['closed_won', 'closed_lost'] } } },
      { $group: { _id: null, total: { $sum: '$value.amount' } } }
    ]);

    // Won deals this month
    const wonDeals = await Opportunity.aggregate([
      {
        $match: {
          stage: 'closed_won',
          actualCloseDate: { $gte: startOfMonth }
        }
      },
      {
        $group: {
          _id: null,
          count: { $sum: 1 },
          value: { $sum: '$value.amount' }
        }
      }
    ]);

    // Top performers
    const topPerformers = await Opportunity.aggregate([
      {
        $match: {
          stage: 'closed_won',
          actualCloseDate: { $gte: startOfMonth }
        }
      },
      {
        $group: {
          _id: '$assignedTo',
          deals: { $sum: 1 },
          revenue: { $sum: '$value.amount' }
        }
      },
      { $sort: { revenue: -1 } },
      { $limit: 5 }
    ]);

    res.json({
      success: true,
      data: {
        leadStats,
        opportunityStats,
        monthlyConversions,
        pipelineValue: pipelineValue[0]?.total || 0,
        wonDeals: wonDeals[0] || { count: 0, value: 0 },
        topPerformers
      }
    });

  } catch (error) {
    logger.error('CRM dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/crm/interactions
// @desc    Log customer interaction
// @access  Private
router.post('/interactions', protect, async (req, res) => {
  try {
    const interaction = await Interaction.create({
      ...req.body,
      createdAt: new Date()
    });

    res.status(201).json({
      success: true,
      data: interaction
    });

  } catch (error) {
    logger.error('Create interaction error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/crm/interactions/:customerId
// @desc    Get customer interaction history
// @access  Private
router.get('/interactions/:customerId', protect, async (req, res) => {
  try {
    const interactions = await Interaction.find({ customer: req.params.customerId })
      .populate('assignedTo', 'firstName lastName')
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: interactions.length,
      data: interactions
    });

  } catch (error) {
    logger.error('Get interactions error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
