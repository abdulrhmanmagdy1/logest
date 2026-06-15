/**
 * ============================================
 * 👤 Users Routes - نظام إدهام
 * User management endpoints
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { body, query } = require('express-validator');
const { protect, authorize } = require('../middleware/auth');
const User = require('../models/User');
const logger = require('../utils/logger');

// Validation helper
const handleValidationErrors = (req, res, next) => {
  const errors = require('express-validator').validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ success: false, errors: errors.array() });
  }
  next();
};

// @route   GET /api/v1/users
// @desc    Get all users
// @access  Private (Admin, Supervisor)
router.get('/', protect, authorize('admin', 'supervisor'), async (req, res) => {
  try {
    const { role, status, search, page = 1, limit = 20 } = req.query;
    
    const filter = {};
    if (role) filter.role = role;
    if (status) filter.status = status;
    if (search) {
      filter.$or = [
        { firstName: { $regex: search, $options: 'i' } },
        { lastName: { $regex: search, $options: 'i' } },
        { email: { $regex: search, $options: 'i' } },
        { phone: { $regex: search, $options: 'i' } }
      ];
    }

    const skip = (parseInt(page) - 1) * parseInt(limit);
    
    const users = await User.find(filter)
      .select('-password')
      .sort('-createdAt')
      .skip(skip)
      .limit(parseInt(limit));

    const total = await User.countDocuments(filter);

    res.json({
      success: true,
      count: users.length,
      total,
      page: parseInt(page),
      pages: Math.ceil(total / parseInt(limit)),
      data: users
    });
  } catch (error) {
    logger.error('Get users error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/users/:id
// @desc    Get single user
// @access  Private
router.get('/:id', protect, async (req, res) => {
  try {
    const user = await User.findById(req.params.id).select('-password');
    
    if (!user) {
      return res.status(404).json({ success: false, message: 'User not found' });
    }

    // Check authorization
    if (req.user.role !== 'admin' && req.user.role !== 'supervisor' && 
        req.user.id !== req.params.id) {
      return res.status(403).json({ success: false, message: 'Not authorized' });
    }

    res.json({ success: true, data: user });
  } catch (error) {
    logger.error('Get user error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/users
// @desc    Create new user
// @access  Private (Admin, Supervisor)
router.post('/', protect, authorize('admin', 'supervisor'), [
  body('firstName').trim().notEmpty(),
  body('lastName').trim().notEmpty(),
  body('email').isEmail(),
  body('phone').matches(/^(05|5)\d{8}$/),
  body('password').isLength({ min: 8 }),
  body('role').isIn(['client', 'driver', 'employee', 'supervisor', 'admin', 'accountant'])
], handleValidationErrors, async (req, res) => {
  try {
    const { email, phone } = req.body;
    
    // Check if exists
    const exists = await User.findOne({ $or: [{ email }, { phone }] });
    if (exists) {
      return res.status(400).json({ success: false, message: 'User already exists' });
    }

    const user = await User.create({ ...req.body, status: 'active' });
    
    logger.info(`New user created: ${email} by ${req.user.id}`);
    
    res.status(201).json({
      success: true,
      message: 'User created successfully',
      data: { ...user.toObject(), password: undefined }
    });
  } catch (error) {
    logger.error('Create user error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/users/:id
// @desc    Update user
// @access  Private
router.put('/:id', protect, async (req, res) => {
  try {
    let user = await User.findById(req.params.id);
    if (!user) {
      return res.status(404).json({ success: false, message: 'User not found' });
    }

    // Authorization
    const canUpdate = req.user.role === 'admin' || req.user.role === 'supervisor' || 
                      req.user.id === req.params.id;
    if (!canUpdate) {
      return res.status(403).json({ success: false, message: 'Not authorized' });
    }

    // Admin only fields
    const allowedUpdates = ['firstName', 'lastName', 'phone', 'avatar', 'address', 
                           'companyName', 'notificationSettings', 'driverInfo'];
    if (['admin', 'supervisor'].includes(req.user.role)) {
      allowedUpdates.push('role', 'status', 'idNumber', 'commercialRegistration', 'vatNumber');
    }

    allowedUpdates.forEach(field => {
      if (req.body[field] !== undefined) user[field] = req.body[field];
    });

    await user.save();
    
    logger.info(`User updated: ${req.params.id} by ${req.user.id}`);
    
    res.json({
      success: true,
      message: 'User updated successfully',
      data: { ...user.toObject(), password: undefined }
    });
  } catch (error) {
    logger.error('Update user error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   DELETE /api/v1/users/:id
// @desc    Delete user
// @access  Private (Admin only)
router.delete('/:id', protect, authorize('admin'), async (req, res) => {
  try {
    const user = await User.findById(req.params.id);
    if (!user) {
      return res.status(404).json({ success: false, message: 'User not found' });
    }

    await user.deleteOne();
    
    logger.info(`User deleted: ${req.params.id} by ${req.user.id}`);
    
    res.json({ success: true, message: 'User deleted successfully' });
  } catch (error) {
    logger.error('Delete user error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/users/:id/status
// @desc    Update user status
// @access  Private (Admin, Supervisor)
router.put('/:id/status', protect, authorize('admin', 'supervisor'), [
  body('status').isIn(['active', 'inactive', 'suspended', 'pending'])
], handleValidationErrors, async (req, res) => {
  try {
    const user = await User.findById(req.params.id);
    if (!user) {
      return res.status(404).json({ success: false, message: 'User not found' });
    }

    user.status = req.body.status;
    await user.save();

    logger.info(`User ${req.params.id} status changed to ${req.body.status}`);

    res.json({
      success: true,
      message: `User status updated to ${req.body.status}`,
      data: { ...user.toObject(), password: undefined }
    });
  } catch (error) {
    logger.error('Update status error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
