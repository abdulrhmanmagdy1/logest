// ============================================
// 🔐 Auth Controller
// ============================================

const User = require('../models/User');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const logger = require('../utils/logger');

// Generate JWT Token
const generateToken = (id) => {
  return jwt.sign({ id }, process.env.JWT_SECRET, {
    expiresIn: process.env.JWT_EXPIRES_IN || '7d'
  });
};

// @desc    Register new user
// @route   POST /api/v1/auth/register
// @access  Public
exports.register = async (req, res) => {
  try {
    const { firstName, lastName, email, password, phone, role, companyName } = req.body;

    // Check if user exists
    const userExists = await User.findOne({ email });
    if (userExists) {
      return res.status(400).json({
        success: false,
        message: 'البريد الإلكتروني مسجل مسبقاً'
      });
    }

    // Create user
    const user = await User.create({
      firstName,
      lastName,
      email,
      password,
      phone,
      role: role || 'client',
      company: companyName ? { name: companyName } : undefined
    });

    // Generate token
    const token = generateToken(user._id);

    res.status(201).json({
      success: true,
      token,
      user: {
        id: user._id,
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email,
        role: user.role,
        status: user.status
      }
    });
  } catch (error) {
    logger.error('Registration error:', error);
    res.status(500).json({
      success: false,
      message: 'حدث خطأ في التسجيل'
    });
  }
};

// @desc    Login user
// @route   POST /api/v1/auth/login
// @access  Public
exports.login = async (req, res) => {
  try {
    const { email, password } = req.body;

    // Check for user
    const user = await User.findOne({ email }).select('+password');
    if (!user) {
      return res.status(401).json({
        success: false,
        message: 'بيانات الدخول غير صحيحة'
      });
    }

    // Check if password matches
    const isMatch = await user.matchPassword(password);
    if (!isMatch) {
      return res.status(401).json({
        success: false,
        message: 'بيانات الدخول غير صحيحة'
      });
    }

    // Check if user is active
    if (user.status !== 'active') {
      return res.status(401).json({
        success: false,
        message: 'الحساب غير مفعل'
      });
    }

    // Update last login
    user.lastLogin = new Date();
    await user.save();

    // Generate token
    const token = generateToken(user._id);

    res.json({
      success: true,
      token,
      user: {
        id: user._id,
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email,
        phone: user.phone,
        role: user.role,
        status: user.status,
        company: user.company,
        lastLogin: user.lastLogin
      }
    });
  } catch (error) {
    logger.error('Login error:', error);
    res.status(500).json({
      success: false,
      message: 'حدث خطأ في تسجيل الدخول'
    });
  }
};

// @desc    Get current user
// @route   GET /api/v1/auth/me
// @access  Private
exports.getMe = async (req, res) => {
  try {
    const user = await User.findById(req.user.id);
    res.json({
      success: true,
      user
    });
  } catch (error) {
    logger.error('Get me error:', error);
    res.status(500).json({
      success: false,
      message: 'حدث خطأ'
    });
  }
};

// @desc    Update profile
// @route   PUT /api/v1/auth/update-profile
// @access  Private
exports.updateProfile = async (req, res) => {
  try {
    const { firstName, lastName, phone, avatar } = req.body;
    
    const user = await User.findByIdAndUpdate(
      req.user.id,
      { firstName, lastName, phone, avatar },
      { new: true, runValidators: true }
    );

    res.json({
      success: true,
      user
    });
  } catch (error) {
    logger.error('Update profile error:', error);
    res.status(500).json({
      success: false,
      message: 'حدث خطأ في تحديث الملف الشخصي'
    });
  }
};

// @desc    Change password
// @route   PUT /api/v1/auth/change-password
// @access  Private
exports.changePassword = async (req, res) => {
  try {
    const { currentPassword, newPassword } = req.body;
    
    const user = await User.findById(req.user.id).select('+password');
    
    // Check current password
    const isMatch = await user.matchPassword(currentPassword);
    if (!isMatch) {
      return res.status(401).json({
        success: false,
        message: 'كلمة المرور الحالية غير صحيحة'
      });
    }

    user.password = newPassword;
    await user.save();

    res.json({
      success: true,
      message: 'تم تغيير كلمة المرور بنجاح'
    });
  } catch (error) {
    logger.error('Change password error:', error);
    res.status(500).json({
      success: false,
      message: 'حدث خطأ'
    });
  }
};
