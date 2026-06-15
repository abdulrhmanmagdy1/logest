//
/**
 * ============================================
 * 🤖 AI Assistant & Voice - المساعد الذكي والصوتي
 * ============================================
 */

const mongoose = require('mongoose');

// Conversation Schema
const ConversationSchema = new mongoose.Schema({
  conversationId: {
    type: String,
    required: true,
    unique: true
  },
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  channel: {
    type: String,
    enum: ['web', 'mobile', 'voice', 'whatsapp', 'telegram', 'slack'],
    required: true
  },
  context: {
    entityType: String,
    entityId: mongoose.Schema.Types.ObjectId,
    intent: String,
    lastAction: String
  },
  messages: [{
    role: { type: String, enum: ['user', 'assistant', 'system'] },
    content: String,
    type: { type: String, enum: ['text', 'voice', 'image', 'file'] },
    intent: String,
    entities: mongoose.Schema.Types.Mixed,
    confidence: Number,
    timestamp: { type: Date, default: Date.now },
    processing: {
      duration: Number,
      model: String,
      tokens: Number
    }
  }],
  status: {
    type: String,
    enum: ['active', 'completed', 'escalated', 'abandoned'],
    default: 'active'
  },
  satisfaction: {
    rating: Number,
    feedback: String
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  createdAt: {
    type: Date,
    default: Date.now
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
});

// Voice Command Schema
const VoiceCommandSchema = new mongoose.Schema({
  commandId: {
    type: String,
    required: true,
    unique: true
  },
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  audio: {
    url: String,
    duration: Number,
    language: String,
    transcript: String
  },
  command: {
    intent: String,
    entities: mongoose.Schema.Types.Mixed,
    action: String,
    confidence: Number
  },
  response: {
    text: String,
    audio: String,
    actionTaken: Boolean,
    result: mongoose.Schema.Types.Mixed
  },
  status: {
    type: String,
    enum: ['processing', 'success', 'failed', 'ambiguous'],
    default: 'processing'
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  createdAt: {
    type: Date,
    default: Date.now
  }
});

// AI Training Data Schema
const AITrainingDataSchema = new mongoose.Schema({
  dataId: {
    type: String,
    required: true,
    unique: true
  },
  type: {
    type: String,
    enum: ['intent', 'entity', 'conversation', 'correction', 'feedback']
  },
  input: String,
  expected: mongoose.Schema.Types.Mixed,
  actual: mongoose.Schema.Types.Mixed,
  model: String,
  confidence: Number,
  approved: { type: Boolean, default: false },
  approvedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  usage: {
    count: { type: Number, default: 0 },
    successRate: { type: Number, default: 0 }
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  createdAt: {
    type: Date,
    default: Date.now
  }
});

module.exports = {
  AIConversation: mongoose.model('AIConversation', ConversationSchema),
  VoiceCommand: mongoose.model('VoiceCommand', VoiceCommandSchema),
  AITrainingData: mongoose.model('AITrainingData', AITrainingDataSchema)
};
