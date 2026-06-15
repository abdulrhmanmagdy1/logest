/**
 * ============================================
 * 🔐 Auth Controller - نظام إدهام
 * Edham Logistics - Authentication Controller
 * ============================================
 */

const User = require('../models/User');
const { MESSAGES, HTTP_STATUS } = require('../config/constants');
const logger = require('../utils/logger');
const AuthService = require('../modules/auth/auth.service');

class AuthController {
  /**
   * Register new user
   */
  static async register(req, res) {
    try {
      const result = await AuthService.register(req.body);
      if (!result.ok) {
        return res.status(result.statusCode).json({ success: false, message: result.message });
      }
      logger.success('User registered', { email: result.user.email, role: result.user.role });
      res.status(result.statusCode).json({
        success: true,
        message: result.message,
        token: result.token,
        user: result.user
      });
    } catch (error) {
      logger.error('Registration error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR,
        error: error.message
      });
    }
  }

  /**
   * Login user
   */
  static async login(req, res) {
    try {
      const result = await AuthService.login(req.body);
      if (!result.ok) {
        return res.status(result.statusCode).json({ success: false, message: result.message });
      }
      logger.success('User logged in', { email: result.user.email, role: result.user.role });
      res.status(result.statusCode).json({
        success: true,
        message: result.message,
        token: result.token,
        user: result.user
      });
    } catch (error) {
      logger.error('Login error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get current user
   */
  static async getCurrentUser(req, res) {
    try {
      const user = await User.findById(req.user._id).select('-password');
      res.json({
        success: true,
        data: user
      });
    } catch (error) {
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Update user profile
   */
  static async updateProfile(req, res) {
    try {
      const { name, phone, address, city, avatar } = req.body;

      const user = await User.findByIdAndUpdate(
        req.user._id,
        { name, phone, address, city, avatar, updatedAt: new Date() },
        { new: true, runValidators: true }
      ).select('-password');

      logger.success('Profile updated', { userId: req.user._id });

      res.json({
        success: true,
        message: MESSAGES.UPDATED,
        data: user
      });
    } catch (error) {
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Change password
   */
  static async changePassword(req, res) {
    try {
      const { currentPassword, newPassword } = req.body;

      const user = await User.findById(req.user._id).select('+password');

      const isValid = await user.comparePassword(currentPassword);
      if (!isValid) {
        return res.status(HTTP_STATUS.UNAUTHORIZED).json({
          success: false,
          message: 'Current password is incorrect'
        });
      }

      user.password = newPassword;
      await user.save();

      logger.success('Password changed', { userId: req.user._id });

      res.json({
        success: true,
        message: 'Password changed successfully'
      });
    } catch (error) {
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }
}

module.exports = AuthController;
