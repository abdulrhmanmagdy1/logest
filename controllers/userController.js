/**
 * ============================================
 * 👤 User Controller - نظام إدهام
 * Edham Logistics - User Management Controller
 * ============================================
 */

const User = require('../models/User');
const { MESSAGES, HTTP_STATUS, ROLES } = require('../config/constants');
const logger = require('../utils/logger');

class UserController {
  /**
   * Get all users
   */
  static async getAll(req, res) {
    try {
      const { page = 1, limit = 20, role, isActive } = req.query;
      
      let query = { isDeleted: { $ne: true } };
      if (role) query.role = role;
      if (isActive !== undefined) query.isActive = isActive === 'true';

      const users = await User.find(query)
        .select('-password')
        .skip((page - 1) * limit)
        .limit(parseInt(limit))
        .sort({ createdAt: -1 });

      const total = await User.countDocuments(query);

      res.json({
        success: true,
        data: users,
        pagination: { total, page: parseInt(page), limit: parseInt(limit) }
      });
    } catch (error) {
      logger.error('Get users error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get single user
   */
  static async getById(req, res) {
    try {
      const user = await User.findById(req.params.id).select('-password');
      
      if (!user || user.isDeleted) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      res.json({
        success: true,
        data: user
      });
    } catch (error) {
      logger.error('Get user error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Update user
   */
  static async update(req, res) {
    try {
      const { name, phone, role, department, isActive, address, city } = req.body;

      const user = await User.findByIdAndUpdate(
        req.params.id,
        { name, phone, role, department, isActive, address, city, updatedAt: new Date() },
        { new: true, runValidators: true }
      ).select('-password');

      if (!user) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      logger.success('User updated', { userId: req.params.id });

      res.json({
        success: true,
        message: MESSAGES.UPDATED,
        data: user
      });
    } catch (error) {
      logger.error('Update user error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Delete user (Soft Delete)
   */
  static async delete(req, res) {
    try {
      const user = await User.findByIdAndUpdate(
        req.params.id,
        { isDeleted: true, deletedAt: new Date(), isActive: false },
        { new: true }
      );

      if (!user) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      logger.success('User deleted', { userId: req.params.id });

      res.json({
        success: true,
        message: MESSAGES.DELETED
      });
    } catch (error) {
      logger.error('Delete user error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get users by role
   */
  static async getByRole(req, res) {
    try {
      const { role } = req.params;
      const { page = 1, limit = 20 } = req.query;

      const users = await User.find({ role, isDeleted: { $ne: true } })
        .select('-password')
        .skip((page - 1) * limit)
        .limit(parseInt(limit))
        .sort({ createdAt: -1 });

      const total = await User.countDocuments({ role, isDeleted: { $ne: true } });

      res.json({
        success: true,
        data: users,
        pagination: { total, page: parseInt(page), limit: parseInt(limit) }
      });
    } catch (error) {
      logger.error('Get users by role error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }
}

module.exports = UserController;
