const express = require('express');
const router = express.Router();
const { protect } = require('../middleware/auth');
const Chat = require('../models/Chat');
const logger = require('../utils/logger');

// GET /api/v1/chat — list conversations for current user
router.get('/', protect, async (req, res) => {
  try {
    const chats = await Chat.find({
      participants: req.user._id,
    })
      .populate('participants', 'name email avatar')
      .sort({ updatedAt: -1 })
      .limit(50);
    res.json({ success: true, data: chats });
  } catch (error) {
    logger.error('Get chats error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// GET /api/v1/chat/:id — get single conversation with messages
router.get('/:id', protect, async (req, res) => {
  try {
    const chat = await Chat.findById(req.params.id).populate(
      'participants',
      'name email avatar'
    );
    if (!chat) return res.status(404).json({ success: false, message: 'Chat not found' });
    res.json({ success: true, data: chat });
  } catch (error) {
    logger.error('Get chat error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// POST /api/v1/chat — create or retrieve conversation
router.post('/', protect, async (req, res) => {
  try {
    const { participantId } = req.body;
    const participants = [req.user._id, participantId].sort();
    let chat = await Chat.findOne({ participants: { $all: participants }, isGroup: false });
    if (!chat) {
      chat = await Chat.create({ participants, createdBy: req.user._id });
    }
    res.status(201).json({ success: true, data: chat });
  } catch (error) {
    logger.error('Create chat error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
