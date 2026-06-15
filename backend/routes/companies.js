//
/**
 * ============================================
 * 🏢 Company Management Routes - إدارة الشركات
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { Company, Branch, Department } = require('../models/Company');
const logger = require('../utils/logger');

// @route   GET /api/v1/companies
// @desc    Get all companies (super admin only)
// @access  Private (Super Admin)
router.get('/', protect, authorize(['super_admin']), async (req, res) => {
  try {
    const { status, plan } = req.query;
    
    let query = {};
    if (status) query.status = status;
    if (plan) query['subscription.plan'] = plan;

    const companies = await Company.find(query)
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: companies.length,
      data: companies
    });

  } catch (error) {
    logger.error('Get companies error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/companies
// @desc    Create new company
// @access  Private (Super Admin)
router.post('/', protect, authorize(['super_admin']), async (req, res) => {
  try {
    const companyCode = `EDH-${Date.now().toString().slice(-6)}`;
    
    const company = await Company.create({
      ...req.body,
      code: companyCode,
      status: 'pending_verification',
      createdBy: req.user.id
    });

    res.status(201).json({
      success: true,
      data: company
    });

  } catch (error) {
    logger.error('Create company error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/companies/my
// @desc    Get current user's company
// @access  Private
router.get('/my', protect, async (req, res) => {
  try {
    const company = await Company.findById(req.user.company);

    if (!company) {
      return res.status(404).json({ success: false, message: 'Company not found' });
    }

    res.json({
      success: true,
      data: company
    });

  } catch (error) {
    logger.error('Get my company error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/companies/my
// @desc    Update current user's company
// @access  Private (Admin, Supervisor)
router.put('/my', protect, authorize(['admin', 'supervisor']), async (req, res) => {
  try {
    const company = await Company.findByIdAndUpdate(
      req.user.company,
      {
        ...req.body,
        updatedAt: new Date()
      },
      { new: true, runValidators: true }
    );

    if (!company) {
      return res.status(404).json({ success: false, message: 'Company not found' });
    }

    res.json({
      success: true,
      message: 'Company updated',
      data: company
    });

  } catch (error) {
    logger.error('Update company error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/companies/my/branches
// @desc    Get company branches
// @access  Private
router.get('/my/branches', protect, async (req, res) => {
  try {
    const branches = await Branch.find({ company: req.user.company })
      .populate('contact.manager', 'firstName lastName')
      .sort({ name: 1 });

    res.json({
      success: true,
      count: branches.length,
      data: branches
    });

  } catch (error) {
    logger.error('Get branches error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/companies/my/branches
// @desc    Add branch
// @access  Private (Admin, Supervisor)
router.post('/my/branches', protect, authorize(['admin', 'supervisor']), async (req, res) => {
  try {
    const branch = await Branch.create({
      ...req.body,
      company: req.user.company
    });

    // Update company usage
    await Company.findByIdAndUpdate(
      req.user.company,
      { $inc: { 'usage.currentWarehouses': 1 } }
    );

    res.status(201).json({
      success: true,
      data: branch
    });

  } catch (error) {
    logger.error('Create branch error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/companies/my/departments
// @desc    Get company departments
// @access  Private
router.get('/my/departments', protect, async (req, res) => {
  try {
    const departments = await Department.find({ company: req.user.company })
      .populate('manager', 'firstName lastName')
      .populate('branch', 'name')
      .sort({ name: 1 });

    res.json({
      success: true,
      count: departments.length,
      data: departments
    });

  } catch (error) {
    logger.error('Get departments error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/companies/my/departments
// @desc    Add department
// @access  Private (Admin, Supervisor, HR)
router.post('/my/departments', protect, authorize(['admin', 'supervisor', 'hr']), async (req, res) => {
  try {
    const department = await Department.create({
      ...req.body,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: department
    });

  } catch (error) {
    logger.error('Create department error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/companies/my/settings
// @desc    Get company settings
// @access  Private
router.get('/my/settings', protect, async (req, res) => {
  try {
    const company = await Company.findById(req.user.company)
      .select('settings billing modules limits security');

    res.json({
      success: true,
      data: company
    });

  } catch (error) {
    logger.error('Get settings error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/companies/my/settings
// @desc    Update company settings
// @access  Private (Admin)
router.put('/my/settings', protect, authorize(['admin']), async (req, res) => {
  try {
    const allowedUpdates = ['settings', 'branding', 'security'];
    const updates = {};
    
    allowedUpdates.forEach(field => {
      if (req.body[field]) updates[field] = req.body[field];
    });

    const company = await Company.findByIdAndUpdate(
      req.user.company,
      { ...updates, updatedAt: new Date() },
      { new: true, runValidators: true }
    );

    res.json({
      success: true,
      message: 'Settings updated',
      data: company
    });

  } catch (error) {
    logger.error('Update settings error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/companies/my/modules
// @desc    Get available modules
// @access  Private
router.get('/my/modules', protect, async (req, res) => {
  try {
    const company = await Company.findById(req.user.company).select('modules');

    res.json({
      success: true,
      data: company.modules
    });

  } catch (error) {
    logger.error('Get modules error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/companies/my/modules/:moduleName
// @desc    Enable/disable module
// @access  Private (Admin)
router.put('/my/modules/:moduleName', protect, authorize(['admin']), async (req, res) => {
  try {
    const { moduleName } = req.params;
    const { enabled } = req.body;

    const company = await Company.findById(req.user.company);
    
    if (company.modules[moduleName]) {
      company.modules[moduleName].enabled = enabled;
      await company.save();
    }

    res.json({
      success: true,
      message: `Module ${moduleName} ${enabled ? 'enabled' : 'disabled'}`,
      data: company.modules
    });

  } catch (error) {
    logger.error('Update module error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/companies/my/usage
// @desc    Get usage statistics
// @access  Private (Admin, Supervisor)
router.get('/my/usage', protect, authorize(['admin', 'supervisor']), async (req, res) => {
  try {
    const company = await Company.findById(req.user.company)
      .select('limits usage subscription');

    const usageStats = {
      users: {
        current: company.usage.currentUsers,
        limit: company.limits.maxUsers,
        percentage: (company.usage.currentUsers / company.limits.maxUsers) * 100
      },
      drivers: {
        current: company.usage.currentDrivers,
        limit: company.limits.maxDrivers,
        percentage: (company.usage.currentDrivers / company.limits.maxDrivers) * 100
      },
      vehicles: {
        current: company.usage.currentVehicles,
        limit: company.limits.maxVehicles,
        percentage: (company.usage.currentVehicles / company.limits.maxVehicles) * 100
      },
      storage: {
        current: company.usage.storageUsedGB,
        limit: company.limits.storageGB,
        percentage: (company.usage.storageUsedGB / company.limits.storageGB) * 100
      },
      api: {
        current: company.usage.apiCallsThisMonth,
        limit: company.limits.apiCallsPerMonth,
        percentage: (company.usage.apiCallsThisMonth / company.limits.apiCallsPerMonth) * 100
      },
      subscription: company.subscription
    };

    res.json({
      success: true,
      data: usageStats
    });

  } catch (error) {
    logger.error('Get usage error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/companies/:id/verify
// @desc    Verify company
// @access  Private (Super Admin)
router.put('/:id/verify', protect, authorize(['super_admin']), async (req, res) => {
  try {
    const company = await Company.findByIdAndUpdate(
      req.params.id,
      {
        status: 'active',
        verified: true,
        updatedAt: new Date()
      },
      { new: true }
    );

    if (!company) {
      return res.status(404).json({ success: false, message: 'Company not found' });
    }

    res.json({
      success: true,
      message: 'Company verified',
      data: company
    });

  } catch (error) {
    logger.error('Verify company error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
