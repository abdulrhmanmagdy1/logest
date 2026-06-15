//
/**
 * ============================================
 * 💬 Chat Model - نظام الدردشة والمحادثات
 * ============================================
 */

const mongoose = require('mongoose');

const MessageSchema = new mongoose.Schema({
  sender: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  content: {
    type: String,
    required: true
  },
  type: {
    type: String,
    enum: ['text', 'image', 'document', 'location', 'voice', 'system'],
    default: 'text'
  },
  attachments: [{
    type: {
      type: String,
      enum: ['image', 'document', 'audio', 'video']
    },
    url: String,
    name: String,
    size: Number,
    mimeType: String
  }],
  location: {
    lat: Number,
    lng: Number,
    address: String
  },
  readBy: [{
    user: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    readAt: {
      type: Date,
      default: Date.now
    }
  }],
  createdAt: {
    type: Date,
    default: Date.now
  }
});

const ChatSchema = new mongoose.Schema({
  // Chat Type
  type: {
    type: String,
    enum: ['direct', 'group', 'support', 'shipment'],
    required: true
  },
  
  // Participants
  participants: [{
    user: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true
    },
    role: {
      type: String,
      enum: ['admin', 'member', 'support'],
      default: 'member'
    },
    joinedAt: {
      type: Date,
      default: Date.now
    },
    lastRead: {
      type: Date,
      default: Date.now
    }
  }],
  
  // Related Data
  relatedData: {
    shipment: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Shipment'
    },
    order: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Order'
    },
    supportTicket: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'SupportTicket'
    }
  },
  
  // Chat Info
  title: String,
  description: String,
  avatar: String,
  
  // Messages
  messages: [MessageSchema],
  
  // Last Message
  lastMessage: {
    content: String,
    sender: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    type: String,
    timestamp: Date
  },
  
  // Settings
  settings: {
    isArchived: {
      type: Boolean,
      default: false
    },
    isMuted: {
      type: Boolean,
      default: false
    },
    notifications: {
      type: Boolean,
      default: true
    }
  },
  
  // Status
  status: {
    type: String,
    enum: ['active', 'closed', 'archived'],
    default: 'active'
  },
  
  // Metadata
  createdBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
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

// Indexes
ChatSchema.index({ participants: 1 });
ChatSchema.index({ 'relatedData.shipment': 1 });
ChatSchema.index({ 'relatedData.supportTicket': 1 });
ChatSchema.index({ status: 1, updatedAt: -1 });

// Pre-save middleware
ChatSchema.pre('save', function(next) {
  this.updatedAt = Date.now();
  next();
});

// Methods
ChatSchema.methods.addMessage = async function(senderId, content, type = 'text', attachments = [], location = null) {
  const message = {
    sender: senderId,
    content,
    type,
    attachments,
    location,
    createdAt: new Date()
  };
  
  this.messages.push(message);
  this.lastMessage = {
    content: type === 'text' ? content : `[${type}]`,
    sender: senderId,
    type,
    timestamp: new Date()
  };
  
  await this.save();
  return message;
};

ChatSchema.methods.markAsRead = async function(userId) {
  const participant = this.participants.find(p => p.user.toString() === userId);
  if (participant) {
    participant.lastRead = new Date();
    await this.save();
  }
};

ChatSchema.methods.getUnreadCount = function(userId) {
  const participant = this.participants.find(p => p.user.toString() === userId);
  if (!participant) return 0;
  
  return this.messages.filter(m => 
    m.sender.toString() !== userId && 
    m.createdAt > participant.lastRead
  ).length;
};

module.exports = mongoose.model('Chat', ChatSchema);
