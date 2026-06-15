//
/**
 * ============================================
 * 📢 Marketing Routes - التسويق والحملات
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { MarketingCampaign, LandingPage, ReferralProgram, ContentCalendar } = require('../models/Marketing');
const logger = require('../utils/logger');

// @route   GET /api/v1/marketing/campaigns
// @desc    Get marketing campaigns
// @access  Private (Marketing, Admin, Sales)
router.get('/campaigns', protect, authorize(['admin', 'marketing', 'sales']), async (req, res) => {
  try {
    const { status, type, objective } = req.query;
    
    let query = { company: req.user.company };
    if (status) query.status = status;
    if (type) query.type = type;
    if (objective) query.objective = objective;

    const campaigns = await MarketingCampaign.find(query)
      .populate('team.owner', 'firstName lastName')
      .sort({ 'schedule.startDate': -1 });

    res.json({
      success: true,
      count: campaigns.length,
      data: campaigns
    });

  } catch (error) {
    logger.error('Get campaigns error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/marketing/campaigns
// @desc    Create campaign
// @access  Private (Marketing, Admin)
router.post('/campaigns', protect, authorize(['admin', 'marketing']), async (req, res) => {
  try {
    const campaignId = `CMP-${Date.now().toString().slice(-6)}`;
    
    const campaign = await MarketingCampaign.create({
      ...req.body,
      campaignId,
      company: req.user.company,
      status: 'draft'
    });

    res.status(201).json({
      success: true,
      data: campaign
    });

  } catch (error) {
    logger.error('Create campaign error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/marketing/campaigns/:id/launch
// @desc    Launch campaign
// @access  Private (Marketing, Admin)
router.put('/campaigns/:id/launch', protect, authorize(['admin', 'marketing']), async (req, res) => {
  try {
    const campaign = await MarketingCampaign.findByIdAndUpdate(
      req.params.id,
      {
        status: 'active',
        'schedule.startDate': new Date(),
        updatedAt: new Date()
      },
      { new: true }
    );

    if (!campaign) {
      return res.status(404).json({ success: false, message: 'Campaign not found' });
    }

    res.json({
      success: true,
      message: 'Campaign launched',
      data: campaign
    });

  } catch (error) {
    logger.error('Launch campaign error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/marketing/campaigns/:id/stats
// @desc    Update campaign stats
// @access  Private
router.put('/campaigns/:id/stats', protect, async (req, res) => {
  try {
    const { impressions, clicks, conversions, leads, revenue } = req.body;

    const campaign = await MarketingCampaign.findById(req.params.id);
    if (!campaign) {
      return res.status(404).json({ success: false, message: 'Campaign not found' });
    }

    // Update stats
    if (impressions) campaign.performance.impressions += impressions;
    if (clicks) campaign.performance.clicks += clicks;
    if (conversions) campaign.performance.conversions += conversions;
    if (leads) campaign.performance.leads += leads;
    if (revenue) campaign.performance.revenue += revenue;

    // Calculate derived metrics
    if (campaign.performance.impressions > 0) {
      campaign.performance.ctr = (campaign.performance.clicks / campaign.performance.impressions) * 100;
    }
    if (campaign.performance.clicks > 0) {
      campaign.performance.costPerLead = campaign.budget.spent / campaign.performance.leads;
    }
    if (campaign.performance.conversions > 0) {
      campaign.performance.costPerAcquisition = campaign.budget.spent / campaign.performance.conversions;
    }
    if (campaign.budget.spent > 0) {
      campaign.performance.roi = ((campaign.performance.revenue - campaign.budget.spent) / campaign.budget.spent) * 100;
    }

    await campaign.save();

    res.json({
      success: true,
      data: campaign.performance
    });

  } catch (error) {
    logger.error('Update campaign stats error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Landing Pages
// @route   GET /api/v1/marketing/landing-pages
// @desc    Get landing pages
// @access  Private (Marketing, Admin)
router.get('/landing-pages', protect, authorize(['admin', 'marketing']), async (req, res) => {
  try {
    const { status } = req.query;
    
    let query = { company: req.user.company };
    if (status) query.status = status;

    const pages = await LandingPage.find(query)
      .populate('campaign', 'name')
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: pages.length,
      data: pages
    });

  } catch (error) {
    logger.error('Get landing pages error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/marketing/landing-pages
// @desc    Create landing page
// @access  Private (Marketing, Admin)
router.post('/landing-pages', protect, authorize(['admin', 'marketing']), async (req, res) => {
  try {
    const slug = req.body.slug || `page-${Date.now()}`;
    
    const page = await LandingPage.create({
      ...req.body,
      slug,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: page
    });

  } catch (error) {
    logger.error('Create landing page error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/marketing/landing-pages/:id/publish
// @desc    Publish landing page
// @access  Private (Marketing, Admin)
router.put('/landing-pages/:id/publish', protect, authorize(['admin', 'marketing']), async (req, res) => {
  try {
    const page = await LandingPage.findByIdAndUpdate(
      req.params.id,
      { status: 'published' },
      { new: true }
    );

    if (!page) {
      return res.status(404).json({ success: false, message: 'Landing page not found' });
    }

    res.json({
      success: true,
      message: 'Landing page published',
      data: page
    });

  } catch (error) {
    logger.error('Publish landing page error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Referral Program
// @route   GET /api/v1/marketing/referral-program
// @desc    Get referral program
// @access  Private
router.get('/referral-program', protect, async (req, res) => {
  try {
    const program = await ReferralProgram.findOne({
      company: req.user.company,
      status: { $in: ['active', 'paused'] }
    });

    if (!program) {
      return res.status(404).json({ success: false, message: 'No active referral program' });
    }

    // Get user's referral info
    const userParticipant = program.participants.find(p => 
      p.user.toString() === req.user.id
    );

    res.json({
      success: true,
      data: {
        program,
        userReferral: userParticipant || null
      }
    });

  } catch (error) {
    logger.error('Get referral program error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/marketing/referral-program/join
// @desc    Join referral program
// @access  Private
router.post('/referral-program/join', protect, async (req, res) => {
  try {
    const program = await ReferralProgram.findOne({
      company: req.user.company,
      status: 'active'
    });

    if (!program) {
      return res.status(404).json({ success: false, message: 'No active referral program' });
    }

    // Generate referral code
    const referralCode = `${req.user.firstName.slice(0, 3).toUpperCase()}${Date.now().toString().slice(-4)}`;

    program.participants.push({
      user: req.user.id,
      referralCode,
      joinedAt: new Date()
    });

    await program.save();

    res.json({
      success: true,
      message: 'Joined referral program',
      referralCode
    });

  } catch (error) {
    logger.error('Join referral program error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Dashboard
// @route   GET /api/v1/marketing/dashboard
// @desc    Marketing dashboard
// @access  Private (Marketing, Admin, Sales)
router.get('/dashboard', protect, authorize(['admin', 'marketing', 'sales']), async (req, res) => {
  try {
    const today = new Date();
    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

    const stats = await Promise.all([
      // Active campaigns
      MarketingCampaign.countDocuments({
        company: req.user.company,
        status: 'active'
      }),
      
      // Campaigns this month
      MarketingCampaign.countDocuments({
        company: req.user.company,
        'schedule.startDate': { $gte: startOfMonth }
      }),
      
      // Total leads this month
      MarketingCampaign.aggregate([
        { $match: { company: req.user.company._id } },
        { $group: { _id: null, totalLeads: { $sum: '$performance.leads' } } }
      ]),
      
      // Total revenue from campaigns
      MarketingCampaign.aggregate([
        { $match: { company: req.user.company._id } },
        { $group: { _id: null, totalRevenue: { $sum: '$performance.revenue' } } }
      ]),
      
      // Campaign performance by type
      MarketingCampaign.aggregate([
        { $match: { company: req.user.company._id } },
        {
          $group: {
            _id: '$type',
            campaigns: { $sum: 1 },
            spend: { $sum: '$budget.spent' },
            revenue: { $sum: '$performance.revenue' },
            leads: { $sum: '$performance.leads' }
          }
        }
      ]),
      
      // Top performing campaigns
      MarketingCampaign.find({ company: req.user.company })
        .sort({ 'performance.roi': -1 })
        .limit(5)
        .select('name performance.roi performance.revenue budget.spent')
    ]);

    res.json({
      success: true,
      data: {
        activeCampaigns: stats[0],
        monthlyCampaigns: stats[1],
        totalLeads: stats[2][0]?.totalLeads || 0,
        totalRevenue: stats[3][0]?.totalRevenue || 0,
        performanceByType: stats[4],
        topCampaigns: stats[5]
      }
    });

  } catch (error) {
    logger.error('Marketing dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
