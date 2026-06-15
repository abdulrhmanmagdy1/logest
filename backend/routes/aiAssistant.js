//
/**
 * ============================================
 * 🤖 AI Assistant Routes - المساعد الذكي
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { AIConversation, VoiceCommand, AITrainingData } = require('../models/AIAssistant');
const logger = require('../utils/logger');

// Conversations
// @route   GET /api/v1/ai/conversations
// @desc    Get user's conversations
// @access  Private
router.get('/conversations', protect, async (req, res) => {
  try {
    const { status, page = 1, limit = 20 } = req.query;

    let query = {
      user: req.user.id,
      company: req.user.company
    };
    if (status) query.status = status;

    const conversations = await AIConversation.find(query)
      .select('conversationId channel status context lastMessage createdAt updatedAt')
      .sort({ updatedAt: -1 })
      .skip((page - 1) * limit)
      .limit(limit * 1);

    res.json({
      success: true,
      count: conversations.length,
      data: conversations
    });

  } catch (error) {
    logger.error('Get AI conversations error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/ai/conversations
// @desc    Create new conversation
// @access  Private
router.post('/conversations', protect, async (req, res) => {
  try {
    const conversationId = `AI-${Date.now()}`;

    const conversation = await AIConversation.create({
      ...req.body,
      conversationId,
      user: req.user.id,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: conversation
    });

  } catch (error) {
    logger.error('Create AI conversation error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/ai/conversations/:id/message
// @desc    Send message to AI
// @access  Private
router.post('/conversations/:id/message', protect, async (req, res) => {
  try {
    const { content, type = 'text' } = req.body;

    const conversation = await AIConversation.findOne({
      conversationId: req.params.id,
      user: req.user.id,
      company: req.user.company
    });

    if (!conversation) {
      return res.status(404).json({ success: false, message: 'Conversation not found' });
    }

    // Add user message
    conversation.messages.push({
      role: 'user',
      content,
      type,
      timestamp: new Date()
    });

    // TODO: Call AI service to generate response
    const aiResponse = {
      role: 'assistant',
      content: 'This is an AI response placeholder',
      type: 'text',
      timestamp: new Date()
    };

    conversation.messages.push(aiResponse);
    conversation.updatedAt = new Date();
    await conversation.save();

    res.json({
      success: true,
      data: aiResponse
    });

  } catch (error) {
    logger.error('Send AI message error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Voice Commands
// @route   POST /api/v1/ai/voice
// @desc    Process voice command
// @access  Private
router.post('/voice', protect, async (req, res) => {
  try {
    const commandId = `VOICE-${Date.now()}`;

    const command = await VoiceCommand.create({
      ...req.body,
      commandId,
      user: req.user.id,
      company: req.user.company
    });

    // TODO: Process voice with speech-to-text and NLP

    res.status(201).json({
      success: true,
      data: command
    });

  } catch (error) {
    logger.error('Process voice command error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Quick Actions
// @route   POST /api/v1/ai/quick-action
// @desc    Quick AI actions
// @access  Private
router.post('/quick-action', protect, async (req, res) => {
  try {
    const { action, data } = req.body;

    // Supported actions
    const supportedActions = [
      'track_shipment',
      'create_shipment',
      'find_driver',
      'check_schedule',
      'report_issue',
      'get_report',
      'summarize',
      'translate',
      'calculate_route'
    ];

    if (!supportedActions.includes(action)) {
      return res.status(400).json({
        success: false,
        message: 'Unsupported action'
      });
    }

    // TODO: Execute action

    res.json({
      success: true,
      action,
      result: { message: 'Action executed' }
    });

  } catch (error) {
    logger.error('Quick action error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Analytics
// @route   GET /api/v1/ai/analytics
// @desc    AI usage analytics
// @access  Private (Admin)
router.get('/analytics', protect, authorize(['admin']), async (req, res) => {
  try {
    const { from, to } = req.query;

    const stats = await Promise.all([
      // Total conversations
      AIConversation.countDocuments({
        company: req.user.company,
        createdAt: { $gte: new Date(from || Date.now() - 30 * 24 * 60 * 60 * 1000) }
      }),

      // Conversations by channel
      AIConversation.aggregate([
        {
          $match: {
            company: req.user.company._id,
            createdAt: { $gte: new Date(from || Date.now() - 30 * 24 * 60 * 60 * 1000) }
          }
        },
        { $group: { _id: '$channel', count: { $sum: 1 } } }
      ]),

      // Satisfaction ratings
      AIConversation.aggregate([
        {
          $match: {
            company: req.user.company._id,
            'satisfaction.rating': { $exists: true }
          }
        },
        {
          $group: {
            _id: null,
            avgRating: { $avg: '$satisfaction.rating' },
            count: { $sum: 1 }
          }
        }
      ]),

      // Voice commands
      VoiceCommand.countDocuments({
        company: req.user.company,
        createdAt: { $gte: new Date(from || Date.now() - 30 * 24 * 60 * 60 * 1000) }
      })
    ]);

    res.json({
      success: true,
      data: {
        totalConversations: stats[0],
        byChannel: stats[1],
        satisfaction: stats[2][0],
        voiceCommands: stats[3]
      }
    });

  } catch (error) {
    logger.error('AI analytics error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
