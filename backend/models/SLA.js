//
/**
 * ============================================
 * ⏱️ SLA Management - إدارة اتفاقيات مستوى الخدمة
 * ============================================
 */

const mongoose = require('mongoose');

// SLA Policy Schema
const SLAPolicySchema = new mongoose.Schema({
  name: {
    type: String,
    required: true
  },
  description: String,
  type: {
    type: String,
    enum: ['shipment_delivery', 'customer_response', 'issue_resolution', 'pickup_time', 'system_uptime', 'custom'],
    required: true
  },
  appliesTo: {
    entities: [{
      type: String,
      enum: ['shipment', 'ticket', 'customer', 'route', 'service']
    }],
    customerTiers: [{
      type: String,
      enum: ['standard', 'premium', 'enterprise', 'vip']
    }],
    serviceTypes: [String],
    regions: [String],
    routes: [String],
    customFilters: mongoose.Schema.Types.Mixed
  },
  targets: [{
    name: String,
    metric: {
      type: String,
      enum: ['response_time', 'resolution_time', 'delivery_time', 'uptime_percentage', 'first_contact_resolution', 'customer_satisfaction']
    },
    operator: {
      type: String,
      enum: ['less_than', 'less_than_or_equal', 'greater_than', 'greater_than_or_equal', 'equals']
    },
    targetValue: Number,
    unit: {
      type: String,
      enum: ['minutes', 'hours', 'days', 'percentage', 'count']
    },
    warningThreshold: Number, // percentage of target (e.g., 80%)
    breachThreshold: Number, // percentage of target (e.g., 100%)
    priority: {
      type: String,
      enum: ['low', 'medium', 'high', 'urgent'],
      default: 'medium'
    },
    businessHours: {
      type: Boolean,
      default: true
    },
    calendar: String // which calendar to use
  }],
  businessHours: {
    timezone: { type: String, default: 'Asia/Riyadh' },
    workDays: [{ type: Number, min: 0, max: 6 }], // 0 = Sunday, 6 = Saturday
    workHours: {
      start: { type: String, default: '08:00' },
      end: { type: String, default: '17:00' }
    },
    holidays: [Date],
    excludeWeekends: { type: Boolean, default: true }
  },
  escalations: [{
    level: Number,
    afterMinutes: Number,
    notifyUsers: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
    notifyRoles: [String],
    actions: [{
      type: {
        type: String,
        enum: ['email', 'sms', 'notification', 'reassign', 'supervisor_alert', 'api_call']
      },
      config: mongoose.Schema.Types.Mixed
    }],
    message: String
  }],
  penalties: {
    enabled: { type: Boolean, default: false },
    type: {
      type: String,
      enum: ['fixed', 'percentage', 'tiered', 'credits']
    },
    rules: [{
      breachPercentage: Number, // how much over target
      penaltyAmount: Number,
      currency: { type: String, default: 'SAR' },
      description: String
    }]
  },
  notifications: {
    warningEnabled: { type: Boolean, default: true },
    breachEnabled: { type: Boolean, default: true },
    warningTemplate: String,
    breachTemplate: String,
    recipients: [{
      type: String,
      enum: ['customer', 'manager', 'assignee', 'team']
    }]
  },
  reporting: {
    trackMetrics: { type: Boolean, default: true },
    reportFrequency: {
      type: String,
      enum: ['daily', 'weekly', 'monthly', 'quarterly']
    },
    recipients: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }]
  },
  status: {
    type: String,
    enum: ['draft', 'active', 'paused', 'archived'],
    default: 'draft'
  },
  effectiveDate: Date,
  expiryDate: Date,
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
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

SLAPolicySchema.index({ company: 1, type: 1 });
SLAPolicySchema.index({ status: 1 });

// SLA Instance Schema (tracks SLA for individual items)
const SLAInstanceSchema = new mongoose.Schema({
  policy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'SLAPolicy',
    required: true
  },
  entity: {
    type: {
      type: String,
      enum: ['shipment', 'ticket', 'customer', 'driver', 'vehicle'],
      required: true
    },
    id: {
      type: mongoose.Schema.Types.ObjectId,
      required: true
    },
    reference: String // tracking number, ticket number, etc.
  },
  priority: {
    type: String,
    enum: ['low', 'medium', 'high', 'urgent']
  },
  status: {
    type: String,
    enum: ['active', 'paused', 'breached', 'met', 'cancelled'],
    default: 'active'
  },
  targets: [{
    metric: String,
    targetValue: Number,
    targetUnit: String,
    deadline: Date,
    warningTime: Date,
    elapsedTime: Number, // minutes
    remainingTime: Number, // minutes
    percentageComplete: Number,
    status: {
      type: String,
      enum: ['on_track', 'at_risk', 'breached', 'met']
    },
    breachedAt: Date,
    metAt: Date
  }],
  timeline: [{
    timestamp: Date,
    event: String,
    description: String,
    user: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    elapsedMinutes: Number
  }],
  breaches: [{
    target: String,
    targetDeadline: Date,
    breachedAt: Date,
    breachedByMinutes: Number,
    severity: {
      type: String,
      enum: ['minor', 'major', 'critical']
    },
    acknowledged: { type: Boolean, default: false },
    acknowledgedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    acknowledgedAt: Date,
    reason: String
  }],
  escalations: [{
    level: Number,
    triggeredAt: Date,
    notifiedUsers: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
    actions: [String],
    resolvedAt: Date
  }],
  metrics: {
    responseTime: Number, // minutes
    resolutionTime: Number, // minutes
    firstContactTime: Number, // minutes
    holdTime: Number, // minutes
    workingTime: Number, // minutes
    pauseCount: { type: Number, default: 0 },
    escalationCount: { type: Number, default: 0 }
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
  },
  resolvedAt: Date
});

SLAInstanceSchema.index({ company: 1, 'entity.type': 1, 'entity.id': 1 });
SLAInstanceSchema.index({ policy: 1, status: 1 });
SLAInstanceSchema.index({ 'targets.deadline': 1 });

// SLA Report Schema
const SLAReportSchema = new mongoose.Schema({
  policy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'SLAPolicy',
    required: true
  },
  period: {
    start: Date,
    end: Date
  },
  summary: {
    totalInstances: Number,
    metCount: Number,
    breachedCount: Number,
    compliancePercentage: Number,
    averageResolutionTime: Number,
    averageResponseTime: Number
  },
  byPriority: [{
    priority: String,
    total: Number,
    met: Number,
    breached: Number,
    compliance: Number
  }],
  byTarget: [{
    target: String,
    total: Number,
    met: Number,
    breached: Number,
    averageTime: Number
  }],
  trends: [{
    date: Date,
    met: Number,
    breached: Number,
    compliance: Number
  }],
  topBreaches: [{
    instance: { type: mongoose.Schema.Types.ObjectId, ref: 'SLAInstance' },
    entity: mongoose.Schema.Types.Mixed,
    breach: mongoose.Schema.Types.Mixed
  }],
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  generatedAt: {
    type: Date,
    default: Date.now
  }
});

module.exports = {
  SLAPolicy: mongoose.model('SLAPolicy', SLAPolicySchema),
  SLAInstance: mongoose.model('SLAInstance', SLAInstanceSchema),
  SLAReport: mongoose.model('SLAReport', SLAReportSchema)
};
