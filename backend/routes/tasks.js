//
/**
 * ============================================
 * ✅ Task Management Routes - إدارة المهام
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { Task, Project, TimeEntry } = require('../models/TaskManagement');
const logger = require('../utils/logger');

// Tasks
// @route   GET /api/v1/tasks
// @desc    Get tasks
// @access  Private
router.get('/', protect, async (req, res) => {
  try {
    const { status, priority, assignedTo, type, project, page = 1, limit = 20 } = req.query;

    let query = { company: req.user.company };
    if (status) query.status = status;
    if (priority) query.priority = priority;
    if (assignedTo) query.assignedTo = assignedTo;
    if (type) query.type = type;
    if (project) query.project = project;

    const tasks = await Task.find(query)
      .populate('assignedTo', 'firstName lastName avatar')
      .populate('assignedBy', 'firstName lastName')
      .populate('reporter', 'firstName lastName')
      .populate('parent', 'title')
      .sort({ priority: -1, dueDate: 1, createdAt: -1 })
      .skip((page - 1) * limit)
      .limit(limit * 1);

    const total = await Task.countDocuments(query);

    res.json({
      success: true,
      count: tasks.length,
      pagination: {
        page: parseInt(page),
        pages: Math.ceil(total / limit),
        total
      },
      data: tasks
    });

  } catch (error) {
    logger.error('Get tasks error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/tasks/my-tasks
// @desc    Get my assigned tasks
// @access  Private
router.get('/my-tasks', protect, async (req, res) => {
  try {
    const { status, priority } = req.query;

    let query = {
      company: req.user.company,
      assignedTo: req.user.id
    };

    if (status) query.status = status;
    if (priority) query.priority = priority;

    const tasks = await Task.find(query)
      .populate('assignedBy', 'firstName lastName')
      .populate('reporter', 'firstName lastName')
      .sort({ priority: -1, dueDate: 1 });

    // Group by status
    const grouped = {
      todo: tasks.filter(t => t.status === 'todo'),
      in_progress: tasks.filter(t => t.status === 'in_progress'),
      review: tasks.filter(t => t.status === 'review'),
      completed: tasks.filter(t => t.status === 'completed')
    };

    res.json({
      success: true,
      count: tasks.length,
      grouped,
      data: tasks
    });

  } catch (error) {
    logger.error('Get my tasks error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/tasks/:id
// @desc    Get task details
// @access  Private
router.get('/:id', protect, async (req, res) => {
  try {
    const task = await Task.findOne({
      _id: req.params.id,
      company: req.user.company
    })
      .populate('assignedTo', 'firstName lastName email phone')
      .populate('assignedBy', 'firstName lastName')
      .populate('reporter', 'firstName lastName')
      .populate('watchers', 'firstName lastName')
      .populate('subtasks')
      .populate('comments.user', 'firstName lastName avatar')
      .populate('history.changedBy', 'firstName lastName');

    if (!task) {
      return res.status(404).json({ success: false, message: 'Task not found' });
    }

    res.json({
      success: true,
      data: task
    });

  } catch (error) {
    logger.error('Get task error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/tasks
// @desc    Create task
// @access  Private
router.post('/', protect, async (req, res) => {
  try {
    const taskId = `TASK-${Date.now()}`;

    const task = await Task.create({
      ...req.body,
      taskId,
      assignedBy: req.user.id,
      company: req.user.company
    });

    await task.populate('assignedTo', 'firstName lastName');
    await task.populate('assignedBy', 'firstName lastName');

    res.status(201).json({
      success: true,
      data: task
    });

  } catch (error) {
    logger.error('Create task error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/tasks/:id
// @desc    Update task
// @access  Private
router.put('/:id', protect, async (req, res) => {
  try {
    const task = await Task.findOne({
      _id: req.params.id,
      company: req.user.company
    });

    if (!task) {
      return res.status(404).json({ success: false, message: 'Task not found' });
    }

    // Track changes for history
    const changes = [];
    for (const [key, value] of Object.entries(req.body)) {
      if (task[key] !== undefined && task[key] !== value) {
        changes.push({
          field: key,
          oldValue: task[key],
          newValue: value,
          changedBy: req.user.id,
          changedAt: new Date()
        });
      }
    }

    if (changes.length > 0) {
      task.history.push(...changes);
    }

    // Update status change timestamps
    if (req.body.status && req.body.status === 'in_progress' && !task.startDate) {
      task.startDate = new Date();
    }
    if (req.body.status && req.body.status === 'completed') {
      task.completedAt = new Date();
    }

    Object.assign(task, req.body, { updatedAt: new Date() });
    await task.save();

    await task.populate('assignedTo', 'firstName lastName');
    await task.populate('assignedBy', 'firstName lastName');

    res.json({
      success: true,
      data: task
    });

  } catch (error) {
    logger.error('Update task error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/tasks/:id/comments
// @desc    Add comment to task
// @access  Private
router.post('/:id/comments', protect, async (req, res) => {
  try {
    const { text, attachments } = req.body;

    const task = await Task.findOneAndUpdate(
      { _id: req.params.id, company: req.user.company },
      {
        $push: {
          comments: {
            user: req.user.id,
            text,
            attachments,
            createdAt: new Date()
          }
        },
        updatedAt: new Date()
      },
      { new: true }
    ).populate('comments.user', 'firstName lastName avatar');

    if (!task) {
      return res.status(404).json({ success: false, message: 'Task not found' });
    }

    res.json({
      success: true,
      data: task.comments[task.comments.length - 1]
    });

  } catch (error) {
    logger.error('Add task comment error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/tasks/:id/checklist
// @desc    Add/update checklist item
// @access  Private
router.post('/:id/checklist', protect, async (req, res) => {
  try {
    const { item, completed, index } = req.body;

    const task = await Task.findOne({
      _id: req.params.id,
      company: req.user.company
    });

    if (!task) {
      return res.status(404).json({ success: false, message: 'Task not found' });
    }

    if (index !== undefined && task.checklist[index]) {
      // Update existing
      task.checklist[index].completed = completed;
      if (completed) {
        task.checklist[index].completedAt = new Date();
        task.checklist[index].completedBy = req.user.id;
      }
    } else {
      // Add new
      task.checklist.push({ item, completed: false });
    }

    await task.save();

    res.json({
      success: true,
      data: task.checklist
    });

  } catch (error) {
    logger.error('Update checklist error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Projects
// @route   GET /api/v1/tasks/projects
// @desc    Get projects
// @access  Private
router.get('/projects', protect, async (req, res) => {
  try {
    const { status, owner } = req.query;

    let query = { company: req.user.company };
    if (status) query.status = status;
    if (owner) query.owner = owner;

    const projects = await Project.find(query)
      .populate('owner', 'firstName lastName')
      .populate('members.user', 'firstName lastName')
      .sort({ updatedAt: -1 });

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

// @route   POST /api/v1/tasks/projects
// @desc    Create project
// @access  Private
router.post('/projects', protect, async (req, res) => {
  try {
    const projectId = `PROJ-${Date.now()}`;

    const project = await Project.create({
      ...req.body,
      projectId,
      owner: req.user.id,
      members: [{ user: req.user.id, role: 'owner', joinedAt: new Date() }],
      company: req.user.company
    });

    await project.populate('owner', 'firstName lastName');

    res.status(201).json({
      success: true,
      data: project
    });

  } catch (error) {
    logger.error('Create project error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Dashboard
// @route   GET /api/v1/tasks/dashboard
// @desc    Task dashboard
// @access  Private
router.get('/dashboard', protect, async (req, res) => {
  try {
    const stats = await Promise.all([
      // My tasks summary
      Task.aggregate([
        {
          $match: {
            company: req.user.company._id,
            assignedTo: req.user._id,
            status: { $ne: 'completed' }
          }
        },
        {
          $group: {
            _id: '$status',
            count: { $sum: 1 }
          }
        }
      ]),

      // Tasks by priority
      Task.aggregate([
        {
          $match: {
            company: req.user.company._id,
            assignedTo: req.user._id,
            status: { $ne: 'completed' }
          }
        },
        {
          $group: {
            _id: '$priority',
            count: { $sum: 1 }
          }
        }
      ]),

      // Overdue tasks
      Task.countDocuments({
        company: req.user.company,
        assignedTo: req.user.id,
        dueDate: { $lt: new Date() },
        status: { $nin: ['completed', 'cancelled'] }
      }),

      // Today's due tasks
      Task.countDocuments({
        company: req.user.company,
        assignedTo: req.user.id,
        dueDate: {
          $gte: new Date(new Date().setHours(0, 0, 0, 0)),
          $lt: new Date(new Date().setHours(23, 59, 59, 999))
        },
        status: { $ne: 'completed' }
      }),

      // Recent tasks
      Task.find({
        company: req.user.company,
        assignedTo: req.user.id
      })
        .populate('assignedBy', 'firstName lastName')
        .select('title status priority dueDate type')
        .sort({ updatedAt: -1 })
        .limit(10)
    ]);

    res.json({
      success: true,
      data: {
        byStatus: stats[0],
        byPriority: stats[1],
        overdue: stats[2],
        dueToday: stats[3],
        recent: stats[4]
      }
    });

  } catch (error) {
    logger.error('Task dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
