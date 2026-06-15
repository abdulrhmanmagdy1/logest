/**
 * ============================================
 * 🔔 Notifications Routes - نظام إدهام
 * Notification management endpoints
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { body } = require('express-validator');
const { protect, authorize } = require('../middleware/auth');
const Notification = require('../models/Notification');
const logger = require('../utils/logger');

// Validation helper
const handleValidationErrors = (req, res, next) => {
  const errors = require('express-validator').validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ success: false, errors: errors.array() });
  }
  next();
};

// @route   GET /api/v1/notifications
// @desc    Get user notifications
// @access  Private
router.get('/', protect, async (req, res) => {
  try {
    const { read, type, limit = 20, page = 1 } = req.query;
    
    const filter = { user: req.user.id };
    if (read !== undefined) filter.read = read === 'true';
    if (type) filter.type = type;

    const skip = (parseInt(page) - 1) * parseInt(limit);

    const notifications = await Notification.find(filter)
      .sort('-createdAt')
      .skip(skip)
      .limit(parseInt(limit));

    const total = await Notification.countDocuments(filter);
    const unreadCount = await Notification.countDocuments({ user: req.user.id, read: false });

    res.json({
      success: true,
      count: notifications.length,
      total,
      unreadCount,
      page: parseInt(page),
      pages: Math.ceil(total / parseInt(limit)),
      data: notifications
    });
  } catch (error) {
    logger.error('Get notifications error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/notifications/unread-count
// @desc    Get unread notification count
// @access  Private
router.get('/unread-count', protect, async (req, res) => {
  try {
    const count = await Notification.countDocuments({ user: req.user.id, read: false });
    res.json({ success: true, count });
  } catch (error) {
    logger.error('Get unread count error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/notifications/:id/read
// @desc    Mark notification as read
// @access  Private
router.put('/:id/read', protect, async (req, res) => {
  try {
    const notification = await Notification.findOne({
      _id: req.params.id,
      user: req.user.id
    });

    if (!notification) {
      return res.status(404).json({ success: false, message: 'Notification not found' });
    }

    await notification.markAsRead();

    res.json({ success: true, message: 'Notification marked as read' });
  } catch (error) {
    logger.error('Mark as read error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/notifications/read-all
// @desc    Mark all notifications as read
// @access  Private
router.put('/read-all', protect, async (req, res) => {
  try {
    await Notification.updateMany(
      { user: req.user.id, read: false },
      { read: true, readAt: new Date() }
    );

    res.json({ success: true, message: 'All notifications marked as read' });
  } catch (error) {
    logger.error('Mark all as read error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   DELETE /api/v1/notifications/:id
// @desc    Delete notification
// @access  Private
router.delete('/:id', protect, async (req, res) => {
  try {
    const notification = await Notification.findOne({
      _id: req.params.id,
      user: req.user.id
    });

    if (!notification) {
      return res.status(404).json({ success: false, message: 'Notification not found' });
    }

    await notification.deleteOne();

    res.json({ success: true, message: 'Notification deleted' });
  } catch (error) {
    logger.error('Delete notification error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   DELETE /api/v1/notifications
// @desc    Delete all read notifications
// @access  Private
router.delete('/', protect, async (req, res) => {
  try {
    const result = await Notification.deleteMany({
      user: req.user.id,
      read: true
    });

    res.json({ 
      success: true, 
      message: `${result.deletedCount} notifications deleted` 
    });
  } catch (error) {
    logger.error('Delete all notifications error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/notifications/send
// @desc    Send notification (admin only)
// @access  Private (Admin, Supervisor)
router.post('/send', protect, authorize('admin', 'supervisor'), [
  body('userId').isMongoId(),
  body('title').trim().notEmpty(),
  body('message').trim().notEmpty(),
  body('type').isIn([
    'shipment_created', 'shipment_updated', 'shipment_delivered',
    'invoice_generated', 'payment_received', 'system_alert', 'announcement'
  ])
], handleValidationErrors, async (req, res) => {
  try {
    const { userId, title, message, type, data } = req.body;

    const notification = await Notification.create({
      user: userId,
      type,
      title,
      message,
      data,
      createdBy: req.user.id,
      channels: {
        inApp: { sent: true, sentAt: new Date() }
      }
    });

    logger.info(`Notification sent to user ${userId} by ${req.user.id}`);

    res.status(201).json({
      success: true,
      message: 'Notification sent successfully',
      data: notification
    });
  } catch (error) {
    logger.error('Send notification error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/notifications/preferences
// @desc    Update notification preferences
// @access  Private
router.put('/preferences', protect, [
  body('email').optional().isBoolean(),
  body('sms').optional().isBoolean(),
  body('push').optional().isBoolean()
], handleValidationErrors, async (req, res) => {
  try {
    const User = require('../models/User');
    const user = await User.findById(req.user.id);

    if (req.body.email !== undefined) user.notificationSettings.email = req.body.email;
    if (req.body.sms !== undefined) user.notificationSettings.sms = req.body.sms;
    if (req.body.push !== undefined) user.notificationSettings.push = req.body.push;

    await user.save();

    res.json({
      success: true,
      message: 'Notification preferences updated',
      data: user.notificationSettings
    });
  } catch (error) {
    logger.error('Update preferences error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
