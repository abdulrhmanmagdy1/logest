//
/**
 * ============================================
 * 👥 CRM Model - إدارة علاقات العملاء
 * ============================================
 */

const mongoose = require('mongoose');

// Lead Schema
const LeadSchema = new mongoose.Schema({
  firstName: {
    type: String,
    required: true,
    trim: true
  },
  lastName: {
    type: String,
    required: true,
    trim: true
  },
  email: {
    type: String,
    lowercase: true
  },
  phone: String,
  company: String,
  jobTitle: String,
  source: {
    type: String,
    enum: ['website', 'referral', 'social_media', 'email_campaign', 'cold_call', 'trade_show', 'other'],
    default: 'other'
  },
  status: {
    type: String,
    enum: ['new', 'contacted', 'qualified', 'proposal', 'negotiation', 'won', 'lost'],
    default: 'new'
  },
  priority: {
    type: String,
    enum: ['low', 'medium', 'high', 'hot'],
    default: 'medium'
  },
  estimatedValue: {
    amount: Number,
    currency: { type: String, default: 'SAR' }
  },
  assignedTo: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  notes: String,
  activities: [{
    type: {
      type: String,
      enum: ['call', 'email', 'meeting', 'note', 'task']
    },
    description: String,
    date: { type: Date, default: Date.now },
    user: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    }
  }],
  tags: [String],
  customFields: mongoose.Schema.Types.Mixed,
  convertedToCustomer: {
    type: Boolean,
    default: false
  },
  customerId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
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

LeadSchema.index({ status: 1, priority: 1 });
LeadSchema.index({ assignedTo: 1, status: 1 });

// Opportunity Schema
const OpportunitySchema = new mongoose.Schema({
  name: {
    type: String,
    required: true
  },
  customer: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  lead: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Lead'
  },
  stage: {
    type: String,
    enum: ['prospecting', 'qualification', 'needs_analysis', 'value_proposition', 'id_decision_makers', 'proposal', 'negotiation', 'closed_won', 'closed_lost'],
    default: 'prospecting'
  },
  probability: {
    type: Number,
    min: 0,
    max: 100,
    default: 10
  },
  value: {
    amount: { type: Number, required: true },
    currency: { type: String, default: 'SAR' }
  },
  expectedCloseDate: Date,
  actualCloseDate: Date,
  products: [{
    name: String,
    quantity: Number,
    unitPrice: Number,
    total: Number
  }],
  assignedTo: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  competitors: [String],
  lossReason: {
    type: String,
    enum: ['price', 'features', 'competitor', 'timing', 'budget', 'other']
  },
  activities: [{
    type: {
      type: String,
      enum: ['call', 'email', 'meeting', 'demo', 'proposal_sent', 'note']
    },
    description: String,
    date: { type: Date, default: Date.now },
    user: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    }
  }],
  notes: String,
  createdAt: {
    type: Date,
    default: Date.now
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
});

OpportunitySchema.index({ stage: 1, assignedTo: 1 });
OpportunitySchema.index({ customer: 1, stage: 1 });

// Campaign Schema
const CampaignSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true
  },
  type: {
    type: String,
    enum: ['email', 'social_media', 'event', 'webinar', 'advertisement', 'content', 'other']
  },
  status: {
    type: String,
    enum: ['planning', 'active', 'paused', 'completed', 'cancelled'],
    default: 'planning'
  },
  startDate: Date,
  endDate: Date,
  budget: {
    allocated: Number,
    spent: { type: Number, default: 0 }
  },
  target: {
    audience: String,
    expectedLeads: Number,
    expectedConversions: Number
  },
  actual: {
    leads: { type: Number, default: 0 },
    conversions: { type: Number, default: 0 },
    revenue: { type: Number, default: 0 }
  },
  content: {
    subject: String,
    body: String,
    attachments: [String]
  },
  recipients: [{
    lead: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Lead'
    },
    email: String,
    sentAt: Date,
    openedAt: Date,
    clickedAt: Date,
    bounced: { type: Boolean, default: false }
  }],
  createdBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
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

// Customer Interaction Schema
const InteractionSchema = new mongoose.Schema({
  customer: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  type: {
    type: String,
    enum: ['call', 'email', 'meeting', 'chat', 'visit', 'support_ticket', 'survey'],
    required: true
  },
  direction: {
    type: String,
    enum: ['inbound', 'outbound'],
    required: true
  },
  subject: String,
  content: String,
  sentiment: {
    type: String,
    enum: ['positive', 'neutral', 'negative']
  },
  duration: Number, // in minutes for calls
  outcome: {
    type: String,
    enum: ['successful', 'unsuccessful', 'follow_up_required', 'no_answer', 'scheduled']
  },
  followUpDate: Date,
  assignedTo: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  attachments: [String],
  relatedShipment: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Shipment'
  },
  relatedInvoice: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Invoice'
  },
  createdAt: {
    type: Date,
    default: Date.now
  }
});

InteractionSchema.index({ customer: 1, createdAt: -1 });
InteractionSchema.index({ assignedTo: 1, followUpDate: 1 });

module.exports = {
  Lead: mongoose.model('Lead', LeadSchema),
  Opportunity: mongoose.model('Opportunity', OpportunitySchema),
  Campaign: mongoose.model('Campaign', CampaignSchema),
  Interaction: mongoose.model('Interaction', InteractionSchema)
};
