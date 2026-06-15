//
/**
 * ============================================
 * 🎫 Support Ticket Model - نظام الدعم الفني
 * ============================================
 */

const mongoose = require('mongoose');

const TicketMessageSchema = new mongoose.Schema({
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
    enum: ['text', 'image', 'document', 'system'],
    default: 'text'
  },
  attachments: [{
    type: {
      type: String,
      enum: ['image', 'document', 'video']
    },
    url: String,
    name: String,
    size: Number
  }],
  isInternal: {
    type: Boolean,
    default: false
  },
  createdAt: {
    type: Date,
    default: Date.now
  }
});

const SupportTicketSchema = new mongoose.Schema({
  // Ticket Info
  ticketNumber: {
    type: String,
    unique: true,
    required: true
  },
  
  // Requester
  requester: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  
  // Category
  category: {
    type: String,
    enum: [
      'general_inquiry',
      'technical_issue',
      'billing',
      'shipment_issue',
      'driver_issue',
      'account_access',
      'feature_request',
      'complaint',
      'refund_request',
      'other'
    ],
    required: true
  },
  
  subCategory: String,
  
  // Priority
  priority: {
    type: String,
    enum: ['low', 'medium', 'high', 'urgent', 'critical'],
    default: 'medium'
  },
  
  // Subject & Description
  subject: {
    type: String,
    required: true,
    maxlength: 200
  },
  description: {
    type: String,
    required: true,
    maxlength: 5000
  },
  
  // Related To
  relatedTo: {
    model: {
      type: String,
      enum: ['Shipment', 'Invoice', 'User', 'Truck', 'Payment']
    },
    id: mongoose.Schema.Types.ObjectId,
    details: mongoose.Schema.Types.Mixed
  },
  
  // Status
  status: {
    type: String,
    enum: ['open', 'in_progress', 'pending_customer', 'pending_internal', 'resolved', 'closed', 'escalated'],
    default: 'open'
  },
  
  // Assignment
  assignedTo: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  assignedTeam: {
    type: String,
    enum: ['technical', 'billing', 'operations', 'management', 'workshop']
  },
  
  // Messages
  messages: [TicketMessageSchema],
  
  // SLA
  sla: {
    responseTime: Number, // minutes
    resolutionTime: Number, // hours
    breachResponse: Boolean,
    breachResolution: Boolean,
    deadlineResponse: Date,
    deadlineResolution: Date
  },
  
  // Tags
  tags: [String],
  
  // Satisfaction
  satisfaction: {
    rating: {
      type: Number,
      min: 1,
      max: 5
    },
    feedback: String,
    submittedAt: Date
  },
  
  // Resolution
  resolution: {
    summary: String,
    actionTaken: String,
    resolvedBy: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    resolvedAt: Date
  },
  
  // Audit Trail
  auditLog: [{
    action: String,
    performedBy: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    performedAt: {
      type: Date,
      default: Date.now
    },
    details: mongoose.Schema.Types.Mixed
  }],
  
  // Metadata
  createdAt: {
    type: Date,
    default: Date.now
  },
  updatedAt: {
    type: Date,
    default: Date.now
  },
  closedAt: Date
});

// Indexes
SupportTicketSchema.index({ ticketNumber: 1 });
SupportTicketSchema.index({ requester: 1, createdAt: -1 });
SupportTicketSchema.index({ assignedTo: 1, status: 1 });
SupportTicketSchema.index({ category: 1, priority: 1 });
SupportTicketSchema.index({ status: 1, 'sla.deadlineResponse': 1 });
SupportTicketSchema.index({ createdAt: -1 });

// Pre-save: Generate ticket number
SupportTicketSchema.pre('save', async function(next) {
  if (this.isNew && !this.ticketNumber) {
    const date = new Date();
    const count = await mongoose.model('SupportTicket').countDocuments({
      createdAt: {
        $gte: new Date(date.getFullYear(), date.getMonth(), 1)
      }
    });
    this.ticketNumber = `TICKET-${date.getFullYear()}${String(date.getMonth() + 1).padStart(2, '0')}-${String(count + 1).padStart(4, '0')}`;
  }
  
  this.updatedAt = Date.now();
  next();
});

// Methods
SupportTicketSchema.methods.addMessage = async function(senderId, content, type = 'text', attachments = [], isInternal = false) {
  this.messages.push({
    sender: senderId,
    content,
    type,
    attachments,
    isInternal,
    createdAt: new Date()
  });
  
  // Update status based on who replied
  if (this.status === 'pending_customer' && !isInternal) {
    this.status = 'in_progress';
  }
  
  this.auditLog.push({
    action: 'message_added',
    performedBy: senderId,
    performedAt: new Date(),
    details: { type, isInternal }
  });
  
  return await this.save();
};

SupportTicketSchema.methods.assign = async function(userId, team, assignedBy) {
  this.assignedTo = userId;
  this.assignedTeam = team;
  this.status = 'in_progress';
  
  this.auditLog.push({
    action: 'assigned',
    performedBy: assignedBy,
    performedAt: new Date(),
    details: { userId, team }
  });
  
  return await this.save();
};

SupportTicketSchema.methods.resolve = async function(summary, actionTaken, userId) {
  this.status = 'resolved';
  this.resolution = {
    summary,
    actionTaken,
    resolvedBy: userId,
    resolvedAt: new Date()
  };
  
  this.auditLog.push({
    action: 'resolved',
    performedBy: userId,
    performedAt: new Date(),
    details: { summary }
  });
  
  return await this.save();
};

SupportTicketSchema.methods.close = async function(userId, reason) {
  this.status = 'closed';
  this.closedAt = new Date();
  
  this.auditLog.push({
    action: 'closed',
    performedBy: userId,
    performedAt: new Date(),
    details: { reason }
  });
  
  return await this.save();
};

SupportTicketSchema.methods.addSatisfaction = async function(rating, feedback) {
  this.satisfaction = {
    rating,
    feedback,
    submittedAt: new Date()
  };
  return await this.save();
};

module.exports = mongoose.model('SupportTicket', SupportTicketSchema);
