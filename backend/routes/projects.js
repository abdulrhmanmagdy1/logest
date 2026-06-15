//
/**
 * ============================================
 * 📊 Project Management Routes - إدارة المشاريع
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { Project, Timesheet } = require('../models/Project');
const logger = require('../utils/logger');

// @route   GET /api/v1/projects
// @desc    Get all projects
// @access  Private
router.get('/', protect, async (req, res) => {
  try {
    const { status, priority, assignedTo } = req.query;
    
    let query = { company: req.user.company };
    if (status) query.status = status;
    if (priority) query.priority = priority;
    if (assignedTo) {
      query['$or'] = [
        { 'team.projectManager': assignedTo },
        { 'team.members.user': assignedTo }
      ];
    }

    const projects = await Project.find(query)
      .populate('team.projectManager', 'firstName lastName')
      .populate('stakeholders.user', 'firstName lastName')
      .sort({ priority: -1, 'dates.startDate': -1 });

    res.json({
      success: true,
      count: projects.length,
      data: projects
    });

  } catch (error) {
    logger.error('Get projects error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/projects
// @desc    Create project
// @access  Private (PMO, Admin)
router.post('/', protect, authorize(['admin', 'supervisor']), async (req, res) => {
  try {
    const projectNumber = `PRJ-${Date.now()}`;
    
    const project = await Project.create({
      ...req.body,
      projectNumber,
      company: req.user.company,
      createdBy: req.user.id
    });

    res.status(201).json({
      success: true,
      data: project
    });

  } catch (error) {
    logger.error('Create project error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/projects/:id
// @desc    Get project details
// @access  Private
router.get('/:id', protect, async (req, res) => {
  try {
    const project = await Project.findById(req.params.id)
      .populate('team.projectManager', 'firstName lastName email')
      .populate('team.members.user', 'firstName lastName')
      .populate('stakeholders.user', 'firstName lastName')
      .populate('risks.risk', 'title riskNumber');

    if (!project) {
      return res.status(404).json({ success: false, message: 'Project not found' });
    }

    res.json({
      success: true,
      data: project
    });

  } catch (error) {
    logger.error('Get project error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/projects/:id/tasks
// @desc    Add/update task
// @access  Private (PM, Team Lead)
router.put('/:id/tasks', protect, async (req, res) => {
  try {
    const { task } = req.body;

    const project = await Project.findById(req.params.id);
    if (!project) {
      return res.status(404).json({ success: false, message: 'Project not found' });
    }

    // Check if user is PM or has edit rights
    const isPM = project.team.projectManager?.toString() === req.user.id;
    if (!isPM && req.user.role !== 'admin') {
      return res.status(403).json({ success: false, message: 'Not authorized' });
    }

    if (task.taskId) {
      // Update existing task
      const taskIndex = project.tasks.findIndex(t => t.taskId === task.taskId);
      if (taskIndex >= 0) {
        project.tasks[taskIndex] = { ...project.tasks[taskIndex], ...task };
      } else {
        project.tasks.push(task);
      }
    } else {
      // Add new task
      task.taskId = `TASK-${project.tasks.length + 1}`;
      project.tasks.push(task);
    }

    // Recalculate progress
    project.progress.overallCompletion = project.calculateProgress();
    project.health = project.checkHealth();
    project.updatedAt = new Date();

    await project.save();

    res.json({
      success: true,
      data: project
    });

  } catch (error) {
    logger.error('Update task error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/projects/:id/status
// @desc    Update project status
// @access  Private (PM, Admin)
router.put('/:id/status', protect, async (req, res) => {
  try {
    const { status, health } = req.body;

    const project = await Project.findByIdAndUpdate(
      req.params.id,
      {
        status,
        health,
        updatedAt: new Date()
      },
      { new: true }
    );

    if (!project) {
      return res.status(404).json({ success: false, message: 'Project not found' });
    }

    res.json({
      success: true,
      data: project
    });

  } catch (error) {
    logger.error('Update status error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/projects/:id/gantt
// @desc    Get project Gantt data
// @access  Private
router.get('/:id/gantt', protect, async (req, res) => {
  try {
    const project = await Project.findById(req.params.id);

    if (!project) {
      return res.status(404).json({ success: false, message: 'Project not found' });
    }

    const ganttData = project.tasks.map(task => ({
      id: task.taskId,
      name: task.name,
      start: task.planned.startDate,
      end: task.planned.endDate,
      progress: task.completion,
      status: task.status,
      assignee: task.assignee,
      dependencies: task.dependencies,
      type: task.type
    }));

    res.json({
      success: true,
      data: ganttData
    });

  } catch (error) {
    logger.error('Get Gantt error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Timesheets
// @route   POST /api/v1/projects/timesheets
// @desc    Submit timesheet
// @access  Private
router.post('/timesheets', protect, async (req, res) => {
  try {
    const { week, entries } = req.body;

    const timesheet = await Timesheet.create({
      user: req.user.id,
      week,
      entries,
      status: 'draft',
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: timesheet
    });

  } catch (error) {
    logger.error('Submit timesheet error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/projects/timesheets
// @desc    Get timesheets
// @access  Private (HR, PM, Admin)
router.get('/timesheets', protect, authorize(['hr', 'admin', 'supervisor']), async (req, res) => {
  try {
    const { user, status, week } = req.query;
    
    let query = { company: req.user.company };
    if (user) query.user = user;
    if (status) query.status = status;
    if (week) query['week.startDate'] = new Date(week);

    const timesheets = await Timesheet.find(query)
      .populate('user', 'firstName lastName')
      .populate('entries.project', 'name projectNumber');

    res.json({
      success: true,
      count: timesheets.length,
      data: timesheets
    });

  } catch (error) {
    logger.error('Get timesheets error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/projects/dashboard
// @desc    Projects dashboard
// @access  Private
router.get('/dashboard', protect, async (req, res) => {
  try {
    const stats = await Promise.all([
      // Projects by status
      Project.aggregate([
        { $match: { company: req.user.company._id } },
        { $group: { _id: '$status', count: { $sum: 1 } } }
      ]),
      
      // Projects by priority
      Project.aggregate([
        { $match: { company: req.user.company._id } },
        { $group: { _id: '$priority', count: { $sum: 1 } } }
      ]),
      
      // Health status
      Project.aggregate([
        { $match: { company: req.user.company._id } },
        { $group: { _id: '$health', count: { $sum: 1 } } }
      ]),
      
      // Active projects count
      Project.countDocuments({
        company: req.user.company,
        status: { $nin: ['closed', 'cancelled'] }
      }),
      
      // Total budget vs spent
      Project.aggregate([
        { $match: { company: req.user.company._id } },
        {
          $group: {
            _id: null,
            totalBudget: { $sum: '$budget.allocated' },
            totalSpent: { $sum: '$budget.spent' }
          }
        }
      ])
    ]);

    res.json({
      success: true,
      data: {
        byStatus: stats[0],
        byPriority: stats[1],
        byHealth: stats[2],
        activeProjects: stats[3],
        budget: stats[4][0] || { totalBudget: 0, totalSpent: 0 }
      }
    });

  } catch (error) {
    logger.error('Projects dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
