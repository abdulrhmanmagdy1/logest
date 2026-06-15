const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const SupportTicket = require('../models/SupportTicket');
const logger = require('../utils/logger');

// GET /api/v1/tickets
router.get('/', protect, async (req, res) => {
  try {
    const query = { company: req.user.company };
    if (!['admin', 'manager'].includes(req.user.role)) query.createdBy = req.user._id;
    const tickets = await SupportTicket.find(query)
      .populate('createdBy', 'name email')
      .sort({ createdAt: -1 });
    res.json({ success: true, count: tickets.length, data: tickets });
  } catch (error) {
    logger.error('Get tickets error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// GET /api/v1/tickets/:id
router.get('/:id', protect, async (req, res) => {
  try {
    const ticket = await SupportTicket.findById(req.params.id).populate('createdBy', 'name email');
    if (!ticket) return res.status(404).json({ success: false, message: 'Ticket not found' });
    res.json({ success: true, data: ticket });
  } catch (error) {
    logger.error('Get ticket error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// POST /api/v1/tickets
router.post('/', protect, async (req, res) => {
  try {
    const ticket = await SupportTicket.create({
      ...req.body,
      createdBy: req.user._id,
      company: req.user.company,
    });
    res.status(201).json({ success: true, data: ticket });
  } catch (error) {
    logger.error('Create ticket error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// PUT /api/v1/tickets/:id
router.put('/:id', protect, async (req, res) => {
  try {
    const ticket = await SupportTicket.findByIdAndUpdate(req.params.id, req.body, {
      new: true,
      runValidators: true,
    });
    if (!ticket) return res.status(404).json({ success: false, message: 'Ticket not found' });
    res.json({ success: true, data: ticket });
  } catch (error) {
    logger.error('Update ticket error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
