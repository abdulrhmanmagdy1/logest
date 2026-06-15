//
/**
 * ============================================
 * 📅 Calendar Routes - التقويم والجدولة
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { CalendarEvent, Availability, ScheduleTemplate } = require('../models/Calendar');
const logger = require('../utils/logger');

// Events
// @route   GET /api/v1/calendar/events
// @desc    Get calendar events
// @access  Private
router.get('/events', protect, async (req, res) => {
  try {
    const { start, end, type, view = 'month' } = req.query;

    let query = { company: req.user.company };

    // Date range
    if (start && end) {
      query.$or = [
        { start: { $gte: new Date(start), $lte: new Date(end) } },
        { end: { $gte: new Date(start), $lte: new Date(end) } },
        { start: { $lte: new Date(start) }, end: { $gte: new Date(end) } }
      ];
    }

    if (type) query.type = type;

    // For user-specific calendar, show events they're attending or organizing
    query.$or = [
      { organizer: req.user.id },
      { 'attendees.user': req.user.id },
      { visibility: 'public' }
    ];

    const events = await CalendarEvent.find(query)
      .populate('organizer', 'firstName lastName avatar')
      .populate('attendees.user', 'firstName lastName avatar')
      .sort({ start: 1 });

    res.json({
      success: true,
      count: events.length,
      data: events
    });

  } catch (error) {
    logger.error('Get calendar events error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/calendar/events
// @desc    Create event
// @access  Private
router.post('/events', protect, async (req, res) => {
  try {
    const eventId = `EVT-${Date.now()}`;

    const event = await CalendarEvent.create({
      ...req.body,
      eventId,
      organizer: req.user.id,
      company: req.user.company
    });

    await event.populate('organizer', 'firstName lastName');
    await event.populate('attendees.user', 'firstName lastName');

    res.status(201).json({
      success: true,
      data: event
    });

  } catch (error) {
    logger.error('Create calendar event error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/calendar/events/:id
// @desc    Update event
// @access  Private
router.put('/events/:id', protect, async (req, res) => {
  try {
    const event = await CalendarEvent.findOneAndUpdate(
      { _id: req.params.id, company: req.user.company },
      { ...req.body, updatedAt: new Date() },
      { new: true }
    )
      .populate('organizer', 'firstName lastName')
      .populate('attendees.user', 'firstName lastName');

    if (!event) {
      return res.status(404).json({ success: false, message: 'Event not found' });
    }

    res.json({
      success: true,
      data: event
    });

  } catch (error) {
    logger.error('Update calendar event error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   DELETE /api/v1/calendar/events/:id
// @desc    Delete/cancel event
// @access  Private
router.delete('/events/:id', protect, async (req, res) => {
  try {
    const event = await CalendarEvent.findOne({
      _id: req.params.id,
      company: req.user.company
    });

    if (!event) {
      return res.status(404).json({ success: false, message: 'Event not found' });
    }

    // Only organizer or admin can delete
    if (event.organizer.toString() !== req.user.id && req.user.role !== 'admin') {
      return res.status(403).json({ success: false, message: 'Not authorized' });
    }

    event.status = 'cancelled';
    await event.save();

    res.json({
      success: true,
      message: 'Event cancelled'
    });

  } catch (error) {
    logger.error('Cancel calendar event error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/calendar/events/:id/respond
// @desc    Respond to event invitation
// @access  Private
router.post('/events/:id/respond', protect, async (req, res) => {
  try {
    const { status, notes } = req.body;

    const event = await CalendarEvent.findOneAndUpdate(
      {
        _id: req.params.id,
        company: req.user.company,
        'attendees.user': req.user.id
      },
      {
        $set: {
          'attendees.$.status': status,
          'attendees.$.notes': notes,
          'attendees.$.responseTime': new Date()
        }
      },
      { new: true }
    );

    if (!event) {
      return res.status(404).json({ success: false, message: 'Event not found or not invited' });
    }

    res.json({
      success: true,
      message: `Response recorded: ${status}`
    });

  } catch (error) {
    logger.error('Event response error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Availability
// @route   GET /api/v1/calendar/availability
// @desc    Get availability
// @access  Private
router.get('/availability', protect, async (req, res) => {
  try {
    const { userId, from, to } = req.query;
    const targetUser = userId || req.user.id;

    const availability = await Availability.find({
      user: targetUser,
      company: req.user.company,
      date: {
        $gte: new Date(from),
        $lte: new Date(to)
      }
    }).sort({ date: 1 });

    res.json({
      success: true,
      count: availability.length,
      data: availability
    });

  } catch (error) {
    logger.error('Get availability error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/calendar/availability
// @desc    Set availability
// @access  Private
router.post('/availability', protect, async (req, res) => {
  try {
    const { date, slots, isWorkingDay } = req.body;

    const availability = await Availability.findOneAndUpdate(
      {
        user: req.user.id,
        company: req.user.company,
        date: new Date(date)
      },
      {
        user: req.user.id,
        company: req.user.company,
        date: new Date(date),
        slots,
        isWorkingDay,
        ...req.body
      },
      { upsert: true, new: true }
    );

    res.json({
      success: true,
      data: availability
    });

  } catch (error) {
    logger.error('Set availability error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Schedule Templates
// @route   GET /api/v1/calendar/templates
// @desc    Get schedule templates
// @access  Private
router.get('/templates', protect, async (req, res) => {
  try {
    const { type } = req.query;

    let query = { company: req.user.company, isActive: true };
    if (type) query.type = type;

    const templates = await ScheduleTemplate.find(query)
      .populate('assignments.user', 'firstName lastName')
      .sort({ name: 1 });

    res.json({
      success: true,
      count: templates.length,
      data: templates
    });

  } catch (error) {
    logger.error('Get schedule templates error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/calendar/templates
// @desc    Create schedule template
// @access  Private (Admin, Manager)
router.post('/templates', protect, authorize(['admin', 'manager']), async (req, res) => {
  try {
    const templateId = `TMPL-${Date.now()}`;

    const template = await ScheduleTemplate.create({
      ...req.body,
      templateId,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: template
    });

  } catch (error) {
    logger.error('Create schedule template error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Dashboard
// @route   GET /api/v1/calendar/dashboard
// @desc    Calendar dashboard
// @access  Private
router.get('/dashboard', protect, async (req, res) => {
  try {
    const today = new Date();
    const startOfDay = new Date(today.setHours(0, 0, 0, 0));
    const endOfDay = new Date(today.setHours(23, 59, 59, 999));
    const startOfWeek = new Date(today - today.getDay() * 24 * 60 * 60 * 1000);
    const endOfWeek = new Date(startOfWeek + 7 * 24 * 60 * 60 * 1000);

    const stats = await Promise.all([
      // Today's events
      CalendarEvent.countDocuments({
        company: req.user.company,
        $or: [
          { organizer: req.user.id },
          { 'attendees.user': req.user.id }
        ],
        start: { $gte: startOfDay, $lte: endOfDay },
        status: { $ne: 'cancelled' }
      }),

      // This week's events
      CalendarEvent.countDocuments({
        company: req.user.company,
        $or: [
          { organizer: req.user.id },
          { 'attendees.user': req.user.id }
        ],
        start: { $gte: startOfWeek, $lte: endOfWeek },
        status: { $ne: 'cancelled' }
      }),

      // Upcoming events
      CalendarEvent.find({
        company: req.user.company,
        $or: [
          { organizer: req.user.id },
          { 'attendees.user': req.user.id }
        ],
        start: { $gte: new Date() },
        status: { $ne: 'cancelled' }
      })
        .populate('organizer', 'firstName lastName')
        .select('title start end type location')
        .sort({ start: 1 })
        .limit(5),

      // Pending invitations
      CalendarEvent.countDocuments({
        company: req.user.company,
        'attendees.user': req.user.id,
        'attendees.status': 'pending',
        start: { $gte: new Date() },
        status: { $ne: 'cancelled' }
      })
    ]);

    res.json({
      success: true,
      data: {
        today: stats[0],
        thisWeek: stats[1],
        upcoming: stats[2],
        pendingInvitations: stats[3]
      }
    });

  } catch (error) {
    logger.error('Calendar dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
