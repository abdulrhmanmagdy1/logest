/**
 * ============================================
 * 🔐 Auth Routes - نظام إدهام
 * Authentication and authorization endpoints
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { body, validationResult } = require('express-validator');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const { protect } = require('../middleware/auth');
const User = require('../models/User');
const logger = require('../utils/logger');

// Validation middleware helper
const handleValidationErrors = (req, res, next) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({
      success: false,
      errors: errors.array()
    });
  }
  next();
};

// @route   POST /api/v1/auth/register
// @desc    Register new user
// @access  Public
router.post('/register', [
  body('firstName').trim().notEmpty().withMessage('First name is required'),
  body('lastName').trim().notEmpty().withMessage('Last name is required'),
  body('email').isEmail().withMessage('Please provide a valid email'),
  body('phone').matches(/^(05|5)\d{8}$/).withMessage('Please provide a valid Saudi phone number'),
  body('password').isLength({ min: 8 }).withMessage('Password must be at least 8 characters'),
  body('role').optional().isIn(['client', 'driver']).withMessage('Invalid role')
], handleValidationErrors, async (req, res) => {
  try {
    const { firstName, lastName, email, phone, password, role, companyName } = req.body;

    // Check if user exists
    const userExists = await User.findOne({ $or: [{ email }, { phone }] });
    if (userExists) {
      return res.status(400).json({
        success: false,
        message: 'User already exists with this email or phone'
      });
    }

    // Create user
    const user = await User.create({
      firstName,
      lastName,
      email,
      phone,
      password,
      role: role || 'client',
      companyName,
      status: 'pending'
    });

    // Generate token
    const token = user.getSignedJwtToken();

    logger.info(`New user registered: ${email}`);

    res.status(201).json({
      success: true,
      message: 'Registration successful',
      token,
      user: {
        id: user._id,
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email,
        phone: user.phone,
        role: user.role,
        status: user.status
      }
    });
  } catch (error) {
    logger.error('Registration error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error'
    });
  }
});

// @route   POST /api/v1/auth/login
// @desc    Login user
// @access  Public
router.post('/login', [
  body('email').optional().isEmail().withMessage('Please provide a valid email'),
  body('phone').optional().notEmpty().withMessage('Phone is required'),
  body('password').notEmpty().withMessage('Password is required')
], handleValidationErrors, async (req, res) => {
  try {
    const { email, phone, password } = req.body;

    // Validate that either email or phone is provided
    if (!email && !phone) {
      return res.status(400).json({
        success: false,
        message: 'Please provide email or phone'
      });
    }

    // Find user
    const query = email ? { email } : { phone };
    const user = await User.findOne(query).select('+password');

    if (!user) {
      return res.status(401).json({
        success: false,
        message: 'Invalid credentials'
      });
    }

    // Check if account is locked
    if (user.isLocked) {
      return res.status(401).json({
        success: false,
        message: 'Account is temporarily locked. Please try again later'
      });
    }

    // Check password
    const isMatch = await user.matchPassword(password);

    if (!isMatch) {
      await user.incrementLoginAttempts();
      return res.status(401).json({
        success: false,
        message: 'Invalid credentials'
      });
    }

    // Reset login attempts
    if (user.loginAttempts > 0) {
      await user.updateOne({
        $set: { loginAttempts: 0 },
        $unset: { lockUntil: 1 }
      });
    }

    // Update last login
    user.lastLogin = new Date();
    await user.save();

    // Generate token
    const token = user.getSignedJwtToken();

    logger.info(`User logged in: ${user.email || user.phone}`);

    res.json({
      success: true,
      message: 'Login successful',
      token,
      user: {
        id: user._id,
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email,
        phone: user.phone,
        role: user.role,
        status: user.status,
        driverInfo: user.driverInfo
      }
    });
  } catch (error) {
    logger.error('Login error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error'
    });
  }
});

// @route   GET /api/v1/auth/me
// @desc    Get current logged in user
// @access  Private
router.get('/me', protect, async (req, res) => {
  try {
    const user = await User.findById(req.user.id);

    res.json({
      success: true,
      user: {
        id: user._id,
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email,
        phone: user.phone,
        role: user.role,
        status: user.status,
        avatar: user.avatar,
        companyName: user.companyName,
        driverInfo: user.driverInfo,
        address: user.address,
        notificationSettings: user.notificationSettings,
        createdAt: user.createdAt
      }
    });
  } catch (error) {
    logger.error('Get me error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error'
    });
  }
});

// @route   POST /api/v1/auth/logout
// @desc    Logout user
// @access  Private
router.post('/logout', protect, async (req, res) => {
  // In a stateless JWT system, client-side token removal handles logout
  // But we can clear any server-side state if needed
  
  logger.info(`User logged out: ${req.user._id}`);
  
  res.json({
    success: true,
    message: 'Logout successful'
  });
});

// @route   PUT /api/v1/auth/update-password
// @desc    Update password
// @access  Private
router.put('/update-password', protect, [
  body('currentPassword').notEmpty().withMessage('Current password is required'),
  body('newPassword').isLength({ min: 8 }).withMessage('New password must be at least 8 characters')
], handleValidationErrors, async (req, res) => {
  try {
    const { currentPassword, newPassword } = req.body;

    const user = await User.findById(req.user.id).select('+password');

    // Check current password
    const isMatch = await user.matchPassword(currentPassword);
    if (!isMatch) {
      return res.status(401).json({
        success: false,
        message: 'Current password is incorrect'
      });
    }

    // Update password
    user.password = newPassword;
    await user.save();

    // Generate new token
    const token = user.getSignedJwtToken();

    logger.info(`Password updated for user: ${user._id}`);

    res.json({
      success: true,
      message: 'Password updated successfully',
      token
    });
  } catch (error) {
    logger.error('Update password error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error'
    });
  }
});

// @route   POST /api/v1/auth/forgot-password
// @desc    Forgot password - send reset email
// @access  Public
router.post('/forgot-password', [
  body('email').isEmail().withMessage('Please provide a valid email')
], handleValidationErrors, async (req, res) => {
  try {
    const user = await User.findOne({ email: req.body.email });

    if (!user) {
      return res.status(404).json({
        success: false,
        message: 'User not found'
      });
    }

    // Generate reset token
    const resetToken = user.createPasswordResetToken();
    await user.save();

    // TODO: Send email with reset link
    // For now, just return the token (in production, send via email)
    
    logger.info(`Password reset requested for: ${user.email}`);

    res.json({
      success: true,
      message: 'Password reset link sent to email',
      resetToken // Remove in production
    });
  } catch (error) {
    logger.error('Forgot password error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error'
    });
  }
});

// @route   POST /api/v1/auth/refresh-token
// @desc    Refresh JWT token
// @access  Private
router.post('/refresh-token', protect, async (req, res) => {
  try {
    const user = await User.findById(req.user.id);
    const token = user.getSignedJwtToken();

    res.json({
      success: true,
      token
    });
  } catch (error) {
    logger.error('Refresh token error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error'
    });
  }
});

// @route   GET /api/v1/auth/seed-demo
// @desc    Create demo users (idempotent — safe to call multiple times)
// @access  Public
router.get('/seed-demo', async (req, res) => {
  const demoUsers = [
    { firstName: 'مدير',    lastName: 'النظام',   email: 'admin@edham.com',       phone: '0500000001', role: 'admin',       password: 'Supervisor@2026' },
    { firstName: 'مشرف',    lastName: 'العمليات', email: 'supervisor@edham.com',   phone: '0500000002', role: 'supervisor',  password: 'Supervisor@2026' },
    { firstName: 'محاسب',   lastName: 'النظام',   email: 'accountant@edham.com',   phone: '0500000003', role: 'accountant',  password: 'Supervisor@2026' },
    { firstName: 'سائق',    lastName: 'النظام',   email: 'driver@edham.com',       phone: '0500000004', role: 'driver',      password: 'Supervisor@2026' },
    { firstName: 'فني',     lastName: 'الورشة',   email: 'workshop@edham.com',     phone: '0500000005', role: 'employee',    password: 'Supervisor@2026' },
    { firstName: 'عميل',    lastName: 'تجريبي',   email: 'customer@edham.com',     phone: '0500000006', role: 'client',      password: 'Supervisor@2026' },
  ];

  try {
    const results = [];

    for (const userData of demoUsers) {
      const existing = await User.findOne({ email: userData.email });
      if (existing) {
        results.push({ email: userData.email, status: 'already_exists' });
        continue;
      }

      await User.create({
        firstName:       userData.firstName,
        lastName:        userData.lastName,
        email:           userData.email,
        phone:           userData.phone,
        password:        userData.password,
        role:            userData.role,
        status:          'active',
        isEmailVerified: true,
      });

      results.push({ email: userData.email, status: 'created' });
    }

    const created = results.filter(r => r.status === 'created').length;
    const skipped = results.filter(r => r.status === 'already_exists').length;

    res.json({
      success: true,
      message: `Demo users ready — ${created} created, ${skipped} already existed.`,
      details: results,
    });
  } catch (error) {
    logger.error('Seed demo error:', error);
    res.status(500).json({ success: false, message: 'Seed failed: ' + error.message });
  }
});

module.exports = router;
