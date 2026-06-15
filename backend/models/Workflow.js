//
/**
 * ============================================
 * ⚙️ Workflow Automation - أتمتة سير العمل
 * ============================================
 */

const mongoose = require('mongoose');

// Workflow Schema
const WorkflowSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true
  },
  description: String,
  category: {
    type: String,
    enum: ['shipment', 'approval', 'notification', 'integration', 'custom'],
    required: true
  },
  trigger: {
    type: {
      type: String,
      enum: ['event', 'schedule', 'manual', 'webhook', 'condition'],
      required: true
    },
    entity: {
      type: String,
      enum: ['shipment', 'driver', 'vehicle', 'invoice', 'payment', 'user', 'order', 'ticket', 'custom']
    },
    event: String, // created, updated, status_changed, etc.
    schedule: {
      frequency: {
        type: String,
        enum: ['once', 'hourly', 'daily', 'weekly', 'monthly']
      },
      at: String, // time
      timezone: String,
      daysOfWeek: [Number], // 0-6
      daysOfMonth: [Number] // 1-31
    },
    webhook: {
      url: String,
      method: { type: String, default: 'POST' },
      headers: mongoose.Schema.Types.Mixed
    },
    condition: {
      field: String,
      operator: {
        type: String,
        enum: ['equals', 'not_equals', 'contains', 'greater_than', 'less_than', 'in', 'not_in', 'exists']
      },
      value: mongoose.Schema.Types.Mixed
    }
  },
  conditions: {
    match: {
      type: String,
      enum: ['all', 'any'],
      default: 'all'
    },
    rules: [{
      field: String,
      operator: {
        type: String,
        enum: ['equals', 'not_equals', 'contains', 'starts_with', 'ends_with', 'greater_than', 'less_than', 'greater_than_or_equal', 'less_than_or_equal', 'in', 'not_in', 'between', 'is_empty', 'is_not_empty', 'regex']
      },
      value: mongoose.Schema.Types.Mixed,
      value2: mongoose.Schema.Types.Mixed // for between
    }]
  },
  actions: [{
    type: {
      type: String,
      enum: ['notification', 'email', 'sms', 'webhook', 'update_field', 'create_record', 'delete_record', 'assign_user', 'change_status', 'calculate', 'delay', 'condition', 'loop', 'api_call', 'script'],
      required: true
    },
    order: { type: Number, default: 0 },
    name: String,
    config: mongoose.Schema.Types.Mixed,
    // Notification action
    notification: {
      channels: [{
        type: String,
        enum: ['email', 'sms', 'push', 'in_app', 'whatsapp']
      }],
      recipients: [{
        type: {
          type: String,
          enum: ['user', 'role', 'email', 'phone', 'dynamic']
        },
        value: String,
        field: String // for dynamic
      }],
      template: String,
      subject: String,
      message: String,
      variables: mongoose.Schema.Types.Mixed
    },
    // Webhook action
    webhook: {
      url: String,
      method: { type: String, default: 'POST' },
      headers: mongoose.Schema.Types.Mixed,
      body: mongoose.Schema.Types.Mixed,
      timeout: { type: Number, default: 30000 },
      retryAttempts: { type: Number, default: 3 }
    },
    // Field update action
    updateField: {
      entity: String,
      field: String,
      value: mongoose.Schema.Types.Mixed,
      valueType: {
        type: String,
        enum: ['static', 'field', 'expression', 'function']
      }
    },
    // Create record action
    createRecord: {
      entity: String,
      fields: [{
        name: String,
        value: mongoose.Schema.Types.Mixed,
        valueType: String
      }],
      assignTo: String
    },
    // API call action
    apiCall: {
      endpoint: String,
      method: { type: String, default: 'POST' },
      headers: mongoose.Schema.Types.Mixed,
      params: mongoose.Schema.Types.Mixed,
      body: mongoose.Schema.Types.Mixed
    },
    // Delay action
    delay: {
      duration: Number, // minutes
      until: Date,
      field: String // dynamic date field
    },
    // Condition action (for branching)
    condition: {
      rules: mongoose.Schema.Types.Mixed,
      trueActions: [Number], // indices of actions to execute if true
      falseActions: [Number] // indices of actions to execute if false
    },
    onError: {
      action: {
        type: String,
        enum: ['continue', 'stop', 'retry', 'notify']
      },
      notification: mongoose.Schema.Types.Mixed
    }
  }],
  settings: {
    enabled: { type: Boolean, default: true },
    logging: { type: Boolean, default: true },
    executionLimit: { type: Number, default: 1000 }, // per day
    concurrentExecution: { type: Boolean, default: false },
    retryOnFailure: { type: Boolean, default: true },
    retryAttempts: { type: Number, default: 3 },
    retryDelay: { type: Number, default: 5 } // minutes
  },
  variables: [{
    name: String,
    type: {
      type: String,
      enum: ['string', 'number', 'boolean', 'date', 'array', 'object']
    },
    defaultValue: mongoose.Schema.Types.Mixed
  }],
  stats: {
    totalExecutions: { type: Number, default: 0 },
    successfulExecutions: { type: Number, default: 0 },
    failedExecutions: { type: Number, default: 0 },
    lastExecution: Date,
    lastSuccess: Date,
    lastFailure: Date,
    averageExecutionTime: { type: Number, default: 0 } // ms
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

WorkflowSchema.index({ company: 1, category: 1 });
WorkflowSchema.index({ 'trigger.type': 1 });
WorkflowSchema.index({ 'settings.enabled': 1 });

// Workflow Execution Log Schema
const WorkflowExecutionSchema = new mongoose.Schema({
  workflow: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Workflow',
    required: true
  },
  triggerData: mongoose.Schema.Types.Mixed,
  status: {
    type: String,
    enum: ['pending', 'running', 'completed', 'failed', 'cancelled'],
    default: 'pending'
  },
  steps: [{
    actionIndex: Number,
    actionName: String,
    actionType: String,
    status: {
      type: String,
      enum: ['pending', 'running', 'completed', 'failed', 'skipped']
    },
    startTime: Date,
    endTime: Date,
    duration: Number, // ms
    input: mongoose.Schema.Types.Mixed,
    output: mongoose.Schema.Types.Mixed,
    error: {
      message: String,
      stack: String,
      code: String
    }
  }],
  variables: mongoose.Schema.Types.Mixed,
  result: mongoose.Schema.Types.Mixed,
  error: {
    message: String,
    stack: String,
    step: Number
  },
  startTime: {
    type: Date,
    default: Date.now
  },
  endTime: Date,
  duration: Number, // ms
  triggeredBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  ipAddress: String,
  userAgent: String
});

WorkflowExecutionSchema.index({ workflow: 1, status: 1 });
WorkflowExecutionSchema.index({ startTime: -1 });

// Automation Rule Schema (simpler workflows)
const AutomationRuleSchema = new mongoose.Schema({
  name: String,
  description: String,
  entity: {
    type: String,
    enum: ['shipment', 'driver', 'vehicle', 'invoice', 'payment', 'user', 'order', 'ticket'],
    required: true
  },
  condition: {
    field: String,
    operator: String,
    value: mongoose.Schema.Types.Mixed
  },
  action: {
    type: {
      type: String,
      enum: ['email', 'notification', 'webhook', 'status_change', 'assign']
    },
    config: mongoose.Schema.Types.Mixed
  },
  active: { type: Boolean, default: true },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

module.exports = {
  Workflow: mongoose.model('Workflow', WorkflowSchema),
  WorkflowExecution: mongoose.model('WorkflowExecution', WorkflowExecutionSchema),
  AutomationRule: mongoose.model('AutomationRule', AutomationRuleSchema)
};
