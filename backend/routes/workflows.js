//
/**
 * ============================================
 * ⚙️ Workflow Automation Routes - أتمتة سير العمل
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { Workflow, WorkflowExecution, AutomationRule } = require('../models/Workflow');
const logger = require('../utils/logger');

// @route   GET /api/v1/workflows
// @desc    Get workflows
// @access  Private
router.get('/', protect, async (req, res) => {
  try {
    const { category, status, type } = req.query;
    
    let query = { company: req.user.company };
    if (category) query.category = category;
    if (status === 'active') query['settings.enabled'] = true;
    if (status === 'inactive') query['settings.enabled'] = false;
    if (type) query['trigger.type'] = type;

    const workflows = await Workflow.find(query)
      .populate('createdBy', 'firstName lastName')
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: workflows.length,
      data: workflows
    });

  } catch (error) {
    logger.error('Get workflows error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/workflows
// @desc    Create workflow
// @access  Private (Admin, Developer)
router.post('/', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const workflow = await Workflow.create({
      ...req.body,
      company: req.user.company,
      createdBy: req.user.id
    });

    res.status(201).json({
      success: true,
      message: 'Workflow created',
      data: workflow
    });

  } catch (error) {
    logger.error('Create workflow error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/workflows/:id
// @desc    Get workflow details
// @access  Private
router.get('/:id', protect, async (req, res) => {
  try {
    const workflow = await Workflow.findById(req.params.id);

    if (!workflow) {
      return res.status(404).json({ success: false, message: 'Workflow not found' });
    }

    // Get recent executions
    const executions = await WorkflowExecution.find({ workflow: workflow._id })
      .sort({ startTime: -1 })
      .limit(10);

    res.json({
      success: true,
      data: {
        ...workflow.toObject(),
        recentExecutions: executions
      }
    });

  } catch (error) {
    logger.error('Get workflow error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/workflows/:id
// @desc    Update workflow
// @access  Private (Admin, Developer)
router.put('/:id', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const workflow = await Workflow.findByIdAndUpdate(
      req.params.id,
      {
        ...req.body,
        updatedAt: new Date()
      },
      { new: true, runValidators: true }
    );

    if (!workflow) {
      return res.status(404).json({ success: false, message: 'Workflow not found' });
    }

    res.json({
      success: true,
      message: 'Workflow updated',
      data: workflow
    });

  } catch (error) {
    logger.error('Update workflow error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/workflows/:id/toggle
// @desc    Enable/disable workflow
// @access  Private (Admin, Developer)
router.put('/:id/toggle', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const workflow = await Workflow.findById(req.params.id);

    if (!workflow) {
      return res.status(404).json({ success: false, message: 'Workflow not found' });
    }

    workflow.settings.enabled = !workflow.settings.enabled;
    workflow.updatedAt = new Date();
    await workflow.save();

    res.json({
      success: true,
      message: `Workflow ${workflow.settings.enabled ? 'enabled' : 'disabled'}`,
      data: workflow
    });

  } catch (error) {
    logger.error('Toggle workflow error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/workflows/:id/execute
// @desc    Execute workflow manually
// @access  Private (Admin, Developer)
router.post('/:id/execute', protect, authorize(['admin', 'developer']), async (req, res) => {
  try {
    const { triggerData } = req.body;

    const workflow = await Workflow.findById(req.params.id);

    if (!workflow) {
      return res.status(404).json({ success: false, message: 'Workflow not found' });
    }

    // Create execution record
    const execution = await WorkflowExecution.create({
      workflow: workflow._id,
      triggerData,
      status: 'pending',
      company: req.user.company,
      triggeredBy: req.user.id,
      ipAddress: req.ip,
      userAgent: req.headers['user-agent']
    });

    // TODO: Trigger actual workflow execution via service

    res.json({
      success: true,
      message: 'Workflow execution triggered',
      data: execution
    });

  } catch (error) {
    logger.error('Execute workflow error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/workflows/:id/executions
// @desc    Get workflow execution history
// @access  Private
router.get('/:id/executions', protect, async (req, res) => {
  try {
    const { status, from, to, limit = 50 } = req.query;

    let query = { workflow: req.params.id };
    if (status) query.status = status;
    if (from && to) {
      query.startTime = {
        $gte: new Date(from),
        $lte: new Date(to)
      };
    }

    const executions = await WorkflowExecution.find(query)
      .sort({ startTime: -1 })
      .limit(parseInt(limit));

    res.json({
      success: true,
      count: executions.length,
      data: executions
    });

  } catch (error) {
    logger.error('Get executions error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Automation Rules
// @route   GET /api/v1/workflows/automation-rules
// @desc    Get automation rules
// @access  Private
router.get('/automation-rules', protect, async (req, res) => {
  try {
    const { entity, active } = req.query;

    let query = { company: req.user.company };
    if (entity) query.entity = entity;
    if (active !== undefined) query.active = active === 'true';

    const rules = await AutomationRule.find(query).sort({ createdAt: -1 });

    res.json({
      success: true,
      count: rules.length,
      data: rules
    });

  } catch (error) {
    logger.error('Get automation rules error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/workflows/automation-rules
// @desc    Create automation rule
// @access  Private (Admin)
router.post('/automation-rules', protect, authorize(['admin']), async (req, res) => {
  try {
    const rule = await AutomationRule.create({
      ...req.body,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: rule
    });

  } catch (error) {
    logger.error('Create automation rule error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Dashboard
// @route   GET /api/v1/workflows/dashboard
// @desc    Workflow dashboard
// @access  Private
router.get('/dashboard', protect, async (req, res) => {
  try {
    const today = new Date();
    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

    const stats = await Promise.all([
      // Active workflows
      Workflow.countDocuments({
        company: req.user.company,
        'settings.enabled': true
      }),

      // Total workflows
      Workflow.countDocuments({ company: req.user.company }),

      // Today's executions
      WorkflowExecution.countDocuments({
        company: req.user.company,
        startTime: {
          $gte: new Date(today.setHours(0, 0, 0, 0))
        }
      }),

      // Monthly executions
      WorkflowExecution.countDocuments({
        company: req.user.company,
        startTime: { $gte: startOfMonth }
      }),

      // Execution status breakdown
      WorkflowExecution.aggregate([
        {
          $match: {
            company: req.user.company._id,
            startTime: { $gte: startOfMonth }
          }
        },
        { $group: { _id: '$status', count: { $sum: 1 } } }
      ]),

      // Top workflows by execution count
      WorkflowExecution.aggregate([
        {
          $match: {
            company: req.user.company._id,
            startTime: { $gte: startOfMonth }
          }
        },
        {
          $group: {
            _id: '$workflow',
            executions: { $sum: 1 },
            successful: {
              $sum: { $cond: [{ $eq: ['$status', 'completed'] }, 1, 0] }
            }
          }
        },
        { $sort: { executions: -1 } },
        { $limit: 5 },
        {
          $lookup: {
            from: 'workflows',
            localField: '_id',
            foreignField: '_id',
            as: 'workflow'
          }
        },
        { $unwind: '$workflow' },
        {
          $project: {
            name: '$workflow.name',
            category: '$workflow.category',
            executions: 1,
            successful: 1,
            successRate: { $divide: ['$successful', '$executions'] }
          }
        }
      ])
    ]);

    res.json({
      success: true,
      data: {
        activeWorkflows: stats[0],
        totalWorkflows: stats[1],
        todayExecutions: stats[2],
        monthlyExecutions: stats[3],
        executionsByStatus: stats[4],
        topWorkflows: stats[5]
      }
    });

  } catch (error) {
    logger.error('Workflow dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
