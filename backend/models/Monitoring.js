//
/**
 * ============================================
 * 📊 Monitoring & Observability - المراقبة والرصد
 * ============================================
 */

const mongoose = require('mongoose');

// Performance Metric Schema
const PerformanceMetricSchema = new mongoose.Schema({
  metricId: {
    type: String,
    required: true
  },
  timestamp: {
    type: Date,
    default: Date.now
  },
  category: {
    type: String,
    enum: ['api', 'database', 'cache', 'queue', 'storage', 'memory', 'cpu', 'network'],
    required: true
  },
  name: {
    type: String,
    required: true
  },
  value: {
    type: Number,
    required: true
  },
  unit: String,
  labels: mongoose.Schema.Types.Mixed,
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

PerformanceMetricSchema.index({ company: 1, category: 1, timestamp: -1 });
PerformanceMetricSchema.index({ metricId: 1, timestamp: -1 });

// Log Entry Schema
const LogEntrySchema = new mongoose.Schema({
  timestamp: {
    type: Date,
    default: Date.now
  },
  level: {
    type: String,
    enum: ['debug', 'info', 'warn', 'error', 'fatal'],
    required: true
  },
  service: String,
  component: String,
  message: {
    type: String,
    required: true
  },
  context: {
    requestId: String,
    userId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    companyId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Company'
    },
    endpoint: String,
    method: String,
    ip: String,
    userAgent: String,
    traceId: String,
    spanId: String
  },
  metadata: mongoose.Schema.Types.Mixed,
  stack: String,
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

LogEntrySchema.index({ company: 1, timestamp: -1 });
LogEntrySchema.index({ level: 1, company: 1 });
LogEntrySchema.index({ 'context.requestId': 1 });
LogEntrySchema.index({ 'context.traceId': 1 });

// Alert Rule Schema
const AlertRuleSchema = new mongoose.Schema({
  ruleId: {
    type: String,
    required: true,
    unique: true
  },
  name: String,
  description: String,
  enabled: {
    type: Boolean,
    default: true
  },
  severity: {
    type: String,
    enum: ['info', 'warning', 'critical', 'emergency'],
    default: 'warning'
  },
  condition: {
    metric: String,
    aggregation: {
      type: String,
      enum: ['avg', 'sum', 'count', 'min', 'max', 'p95', 'p99']
    },
    operator: {
      type: String,
      enum: ['gt', 'gte', 'lt', 'lte', 'eq', 'ne']
    },
    threshold: Number,
    duration: { type: Number, default: 5 }, // minutes
    window: { type: Number, default: 5 } // evaluation window in minutes
  },
  notifications: [{
    channel: {
      type: String,
      enum: ['email', 'sms', 'slack', 'webhook', 'pagerduty', 'teams']
    },
    config: mongoose.Schema.Types.Mixed,
    enabled: { type: Boolean, default: true }
  }],
  actions: [{
    type: {
      type: String,
      enum: ['webhook', 'script', 'automation', 'incident']
    },
    config: mongoose.Schema.Types.Mixed
  }],
  cooldown: { type: Number, default: 15 }, // minutes
  autoResolve: { type: Boolean, default: true },
  resolveCondition: mongoose.Schema.Types.Mixed,
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

// Alert Instance Schema
const AlertInstanceSchema = new mongoose.Schema({
  instanceId: {
    type: String,
    required: true,
    unique: true
  },
  rule: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'AlertRule',
    required: true
  },
  severity: String,
  status: {
    type: String,
    enum: ['firing', 'acknowledged', 'resolved', 'suppressed'],
    default: 'firing'
  },
  title: String,
  description: String,
  value: Number,
  threshold: Number,
  startedAt: {
    type: Date,
    default: Date.now
  },
  acknowledgedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  acknowledgedAt: Date,
  acknowledgedNote: String,
  resolvedAt: Date,
  resolvedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  notifications: [{
    channel: String,
    sentAt: Date,
    status: String,
    error: String
  }],
  annotations: [{
    key: String,
    value: String,
    user: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    timestamp: Date
  }],
  relatedMetrics: [{
    metric: String,
    value: Number,
    timestamp: Date
  }],
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

AlertInstanceSchema.index({ company: 1, status: 1, startedAt: -1 });

// Trace Schema (Distributed Tracing)
const TraceSchema = new mongoose.Schema({
  traceId: {
    type: String,
    required: true,
    unique: true
  },
  rootSpan: String,
  timestamp: {
    type: Date,
    default: Date.now
  },
  duration: Number, // ms
  spans: [{
    spanId: String,
    parentSpanId: String,
    name: String,
    service: String,
    operation: String,
    startTime: Date,
    endTime: Date,
    duration: Number,
    tags: mongoose.Schema.Types.Mixed,
    logs: [{
      timestamp: Date,
      fields: mongoose.Schema.Types.Mixed
    }],
    status: {
      code: String,
      message: String
    }
  }],
  services: [String],
  status: {
    type: String,
    enum: ['ok', 'error', 'partial'],
    default: 'ok'
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

TraceSchema.index({ company: 1, timestamp: -1 });
TraceSchema.index({ traceId: 1 });

// Uptime Check Schema
const UptimeCheckSchema = new mongoose.Schema({
  checkId: {
    type: String,
    required: true,
    unique: true
  },
  name: String,
  type: {
    type: String,
    enum: ['http', 'https', 'tcp', 'ping', 'dns', 'ssl'],
    required: true
  },
  target: {
    url: String,
    host: String,
    port: Number,
    path: String
  },
  interval: {
    type: Number,
    default: 60 // seconds
  },
  timeout: {
    type: Number,
    default: 10 // seconds
  },
  regions: [String],
  assertions: [{
    type: {
      type: String,
      enum: ['status_code', 'response_time', 'body_contains', 'header', 'json_path', 'ssl_expiry']
    },
    target: String,
    expected: mongoose.Schema.Types.Mixed
  }],
  notifications: {
    onDown: Boolean,
    onRecovery: Boolean,
    channels: [String]
  },
  status: {
    type: String,
    enum: ['up', 'down', 'unknown'],
    default: 'unknown'
  },
  uptime: {
    last24h: Number,
    last7d: Number,
    last30d: Number,
    last90d: Number
  },
  responseTime: {
    last: Number,
    avg: Number,
    min: Number,
    max: Number,
    p95: Number,
    p99: Number
  },
  lastCheck: {
    timestamp: Date,
    status: String,
    responseTime: Number,
    error: String
  },
  history: [{
    timestamp: Date,
    status: String,
    responseTime: Number,
    region: String,
    error: String
  }],
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

UptimeCheckSchema.index({ company: 1, status: 1 });

module.exports = {
  PerformanceMetric: mongoose.model('PerformanceMetric', PerformanceMetricSchema),
  LogEntry: mongoose.model('LogEntry', LogEntrySchema),
  AlertRule: mongoose.model('AlertRule', AlertRuleSchema),
  AlertInstance: mongoose.model('AlertInstance', AlertInstanceSchema),
  Trace: mongoose.model('Trace', TraceSchema),
  UptimeCheck: mongoose.model('UptimeCheck', UptimeCheckSchema)
};
