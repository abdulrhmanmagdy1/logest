//
/**
 * ============================================
 * 📧 Email Templates - قوالب البريد الإلكتروني
 * ============================================
 */

const mongoose = require('mongoose');

// Email Template Schema
const EmailTemplateSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true
  },
  code: {
    type: String,
    required: true,
    unique: true
  },
  category: {
    type: String,
    enum: ['transactional', 'marketing', 'notification', 'reminder', 'report', 'onboarding', 'system'],
    required: true
  },
  subject: {
    type: String,
    required: true
  },
  content: {
    html: {
      type: String,
      required: true
    },
    text: String,
    mjml: String // MJML source for responsive emails
  },
  design: {
    layout: {
      type: String,
      enum: ['default', 'clean', 'modern', 'corporate', 'minimal'],
      default: 'default'
    },
    headerImage: String,
    footerText: String,
    primaryColor: String,
    secondaryColor: String,
    fontFamily: { type: String, default: 'Arial, sans-serif' },
    direction: { type: String, enum: ['ltr', 'rtl'], default: 'rtl' }
  },
  variables: [{
    name: String,
    type: {
      type: String,
      enum: ['string', 'number', 'date', 'boolean', 'array', 'object']
    },
    required: { type: Boolean, default: false },
    defaultValue: mongoose.Schema.Types.Mixed,
    description: String,
    example: String
  }],
  previewData: mongoose.Schema.Types.Mixed, // Sample data for preview
  attachments: [{
    filename: String,
    path: String,
    contentType: String,
    size: Number
  }],
  settings: {
    trackOpens: { type: Boolean, default: true },
    trackClicks: { type: Boolean, default: true },
    inlineCss: { type: Boolean, default: true },
    minifyHtml: { type: Boolean, default: true },
    addUnsubscribeLink: { type: Boolean, default: false },
    replyTo: String,
    fromName: String,
    fromEmail: String
  },
  translations: [{
    language: {
      type: String,
      enum: ['en', 'ar', 'ur', 'bn', 'tl', 'fr']
    },
    subject: String,
    html: String,
    text: String
  }],
  usage: {
    sentCount: { type: Number, default: 0 },
    openCount: { type: Number, default: 0 },
    clickCount: { type: Number, default: 0 },
    lastSent: Date
  },
  status: {
    type: String,
    enum: ['draft', 'active', 'archived'],
    default: 'draft'
  },
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

EmailTemplateSchema.index({ company: 1, category: 1 });
EmailTemplateSchema.index({ code: 1, company: 1 });

// Email Log Schema
const EmailLogSchema = new mongoose.Schema({
  template: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'EmailTemplate'
  },
  to: [String],
  cc: [String],
  bcc: [String],
  from: String,
  subject: String,
  content: {
    html: String,
    text: String
  },
  status: {
    type: String,
    enum: ['pending', 'queued', 'sending', 'sent', 'delivered', 'opened', 'clicked', 'bounced', 'failed', 'complained', 'unsubscribed'],
    default: 'pending'
  },
  provider: {
    name: String,
    messageId: String,
    response: mongoose.Schema.Types.Mixed
  },
  tracking: {
    messageId: String,
    opens: [{
      timestamp: Date,
      ip: String,
      userAgent: String,
      location: {
        country: String,
        city: String
      }
    }],
    clicks: [{
      timestamp: Date,
      url: String,
      ip: String,
      userAgent: String
    }],
    bouncedAt: Date,
    bounceReason: String,
    complainedAt: Date,
    unsubscribedAt: Date
  },
  metadata: {
    campaign: String,
    category: String,
    tags: [String],
    customFields: mongoose.Schema.Types.Mixed
  },
  error: {
    message: String,
    code: String,
    stack: String
  },
  scheduledAt: Date,
  sentAt: Date,
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

EmailLogSchema.index({ company: 1, status: 1 });
EmailLogSchema.index({ 'tracking.messageId': 1 });
EmailLogSchema.index({ createdAt: -1 });

// Notification Template Schema (in-app, push, sms)
const NotificationTemplateSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true
  },
  code: {
    type: String,
    required: true
  },
  channels: [{
    type: String,
    enum: ['push', 'in_app', 'sms', 'whatsapp']
  }],
  title: String,
  body: {
    type: String,
    required: true
  },
  data: mongoose.Schema.Types.Mixed, // payload for push notifications
  actions: [{
    label: String,
    action: String,
    url: String
  }],
  icon: String,
  image: String,
  sound: String,
  priority: {
    type: String,
    enum: ['low', 'normal', 'high', 'urgent'],
    default: 'normal'
  },
  variables: [{
    name: String,
    type: String,
    description: String
  }],
  translations: [{
    language: String,
    title: String,
    body: String
  }],
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  status: {
    type: String,
    enum: ['draft', 'active'],
    default: 'draft'
  },
  createdAt: {
    type: Date,
    default: Date.now
  }
});

NotificationTemplateSchema.index({ company: 1, code: 1 });

module.exports = {
  EmailTemplate: mongoose.model('EmailTemplate', EmailTemplateSchema),
  EmailLog: mongoose.model('EmailLog', EmailLogSchema),
  NotificationTemplate: mongoose.model('NotificationTemplate', NotificationTemplateSchema)
};
