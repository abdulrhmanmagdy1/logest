//
/**
 * ============================================
 * 📋 Form Builder - منشئ النماذج
 * ============================================
 */

const mongoose = require('mongoose');

// Form Schema
const FormSchema = new mongoose.Schema({
  formId: {
    type: String,
    required: true,
    unique: true
  },
  name: {
    type: String,
    required: true
  },
  description: String,
  category: {
    type: String,
    enum: ['shipment', 'feedback', 'survey', 'registration', 'application', 'custom'],
    default: 'custom'
  },
  type: {
    type: String,
    enum: ['public', 'private', 'restricted'],
    default: 'private'
  },
  status: {
    type: String,
    enum: ['draft', 'published', 'archived'],
    default: 'draft'
  },
  settings: {
    allowMultipleSubmissions: { type: Boolean, default: false },
    requireAuthentication: { type: Boolean, default: true },
    showProgressBar: { type: Boolean, default: true },
    redirectAfterSubmit: {
      enabled: { type: Boolean, default: false },
      url: String,
      message: String
    },
    emailNotifications: {
      enabled: { type: Boolean, default: false },
      recipients: [String],
      template: String
    },
    webhooks: [{
      url: String,
      events: [String],
      headers: mongoose.Schema.Types.Mixed
    }],
    styling: {
      theme: { type: String, default: 'default' },
      primaryColor: String,
      backgroundColor: String,
      logo: String,
      customCss: String
    },
    validation: {
      captcha: { type: Boolean, default: false },
      honeypot: { type: Boolean, default: false }
    }
  },
  layout: {
    steps: [{
      id: String,
      title: String,
      description: String,
      fields: [String] // field IDs
    }],
    singlePage: { type: Boolean, default: true }
  },
  fields: [{
    id: {
      type: String,
      required: true
    },
    type: {
      type: String,
      enum: ['text', 'textarea', 'email', 'phone', 'number', 'date', 'time', 'datetime', 'select', 'multiselect', 'checkbox', 'radio', 'toggle', 'file', 'image', 'signature', 'rating', 'matrix', 'section', 'page_break'],
      required: true
    },
    label: String,
    placeholder: String,
    description: String,
    required: { type: Boolean, default: false },
    validation: {
      minLength: Number,
      maxLength: Number,
      pattern: String,
      min: Number,
      max: Number,
      custom: String
    },
    options: [{
      label: String,
      value: String,
      selected: Boolean
    }],
    conditional: {
      enabled: { type: Boolean, default: false },
      field: String,
      operator: {
        type: String,
        enum: ['equals', 'not_equals', 'contains', 'not_contains', 'greater_than', 'less_than', 'is_empty', 'is_not_empty']
      },
      value: mongoose.Schema.Types.Mixed
    },
    calculation: {
      enabled: { type: Boolean, default: false },
      formula: String,
      variables: [String]
    },
    appearance: {
      width: { type: String, default: 'full' }, // full, half, third
      align: { type: String, default: 'left' },
      hidden: { type: Boolean, default: false }
    },
    defaultValue: mongoose.Schema.Types.Mixed,
    order: Number
  }],
  integrations: [{
    type: {
      type: String,
      enum: ['crm', 'email', 'webhook', 'database', 'zapier']
    },
    config: mongoose.Schema.Types.Mixed,
    enabled: { type: Boolean, default: false }
  }],
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
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

FormSchema.index({ company: 1, status: 1 });
FormSchema.index({ formId: 1 });

// Form Submission Schema
const FormSubmissionSchema = new mongoose.Schema({
  submissionId: {
    type: String,
    required: true,
    unique: true
  },
  form: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Form',
    required: true
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  submittedBy: {
    user: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    email: String,
    name: String,
    ip: String,
    userAgent: String
  },
  data: mongoose.Schema.Types.Mixed,
  files: [{
    fieldId: String,
    filename: String,
    originalName: String,
    mimeType: String,
    size: Number,
    path: String,
    url: String
  }],
  calculations: [{
    fieldId: String,
    result: mongoose.Schema.Types.Mixed
  }],
  score: {
    total: Number,
    max: Number,
    percentage: Number
  },
  status: {
    type: String,
    enum: ['pending', 'processing', 'completed', 'flagged', 'spam'],
    default: 'pending'
  },
  review: {
    reviewedBy: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    reviewedAt: Date,
    notes: String,
    rating: Number
  },
  workflow: {
    stage: String,
    assignedTo: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    history: [{
      stage: String,
      timestamp: Date,
      user: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User'
      }
    }]
  },
  relatedTo: {
    entityType: String,
    entityId: mongoose.Schema.Types.ObjectId
  },
  metadata: {
    timeSpent: Number, // seconds
    pageViews: Number,
    startedAt: Date,
    submittedAt: {
      type: Date,
      default: Date.now
    },
    referrer: String,
    utm: {
      source: String,
      medium: String,
      campaign: String,
      term: String,
      content: String
    }
  }
});

FormSubmissionSchema.index({ form: 1, submittedAt: -1 });
FormSubmissionSchema.index({ company: 1, status: 1 });

// Form Analytics Schema
const FormAnalyticsSchema = new mongoose.Schema({
  form: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Form',
    required: true
  },
  date: {
    type: Date,
    required: true
  },
  views: { type: Number, default: 0 },
  starts: { type: Number, default: 0 },
  submissions: { type: Number, default: 0 },
  abandonment: { type: Number, default: 0 },
  conversionRate: { type: Number, default: 0 },
  averageTime: { type: Number, default: 0 }, // seconds
  deviceBreakdown: {
    desktop: { type: Number, default: 0 },
    mobile: { type: Number, default: 0 },
    tablet: { type: Number, default: 0 }
  },
  sourceBreakdown: mongoose.Schema.Types.Mixed,
  fieldAnalytics: [{
    fieldId: String,
    views: Number,
    interactions: Number,
    abandonmentAt: Number
  }],
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

FormAnalyticsSchema.index({ form: 1, date: -1 });

module.exports = {
  Form: mongoose.model('Form', FormSchema),
  FormSubmission: mongoose.model('FormSubmission', FormSubmissionSchema),
  FormAnalytics: mongoose.model('FormAnalytics', FormAnalyticsSchema)
};
