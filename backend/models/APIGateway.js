//
/**
 * ============================================
 * 🌐 API Gateway Management - إدارة بوابة API
 * ============================================
 */

const mongoose = require('mongoose');

// API Endpoint Schema
const APIEndpointSchema = new mongoose.Schema({
  endpointId: {
    type: String,
    required: true,
    unique: true
  },
  path: {
    type: String,
    required: true
  },
  method: {
    type: String,
    enum: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS', 'HEAD'],
    required: true
  },
  version: {
    type: String,
    default: 'v1'
  },
  name: String,
  description: String,
  tags: [String],
  category: {
    type: String,
    enum: ['core', 'operations', 'management', 'analytics', 'integration', 'portal', 'system']
  },
  authentication: {
    required: { type: Boolean, default: true },
    methods: [{ type: String, enum: ['jwt', 'api_key', 'oauth', 'basic'] }],
    scopes: [String]
  },
  authorization: {
    required: { type: Boolean, default: true },
    roles: [String],
    permissions: [String]
  },
  request: {
    contentTypes: [String],
    parameters: [{
      name: String,
      in: { type: String, enum: ['query', 'path', 'header', 'body'] },
      type: String,
      required: Boolean,
      description: String,
      example: mongoose.Schema.Types.Mixed
    }],
    body: mongoose.Schema.Types.Mixed, // JSON Schema
    maxSize: { type: Number, default: 10 * 1024 * 1024 } // 10MB
  },
  response: {
    success: {
      statusCode: { type: Number, default: 200 },
      schema: mongoose.Schema.Types.Mixed,
      example: mongoose.Schema.Types.Mixed
    },
    errors: [{
      statusCode: Number,
      description: String,
      schema: mongoose.Schema.Types.Mixed
    }]
  },
  throttling: {
    enabled: { type: Boolean, default: true },
    rateLimit: { type: Number, default: 1000 },
    burstLimit: { type: Number, default: 200 },
    quota: {
      period: { type: String, enum: ['day', 'week', 'month'] },
      limit: Number
    }
  },
  caching: {
    enabled: { type: Boolean, default: false },
    ttl: { type: Number, default: 300 }, // seconds
    varyBy: [String], // query params to vary cache
    invalidateOn: [String] // events that invalidate cache
  },
  transformation: {
    request: {
      enabled: { type: Boolean, default: false },
      mapping: mongoose.Schema.Types.Mixed
    },
    response: {
      enabled: { type: Boolean, default: false },
      mapping: mongoose.Schema.Types.Mixed,
      format: { type: String, enum: ['json', 'xml', 'csv'] }
    }
  },
  backend: {
    type: { type: String, enum: ['internal', 'external', 'lambda', 'microservice'] },
    service: String,
    timeout: { type: Number, default: 30000 }, // ms
    retry: {
      attempts: { type: Number, default: 3 },
      backoff: { type: Number, default: 1000 } // ms
    },
    circuitBreaker: {
      enabled: { type: Boolean, default: true },
      threshold: { type: Number, default: 50 }, // error %
      resetTimeout: { type: Number, default: 60000 } // ms
    }
  },
  monitoring: {
    enabled: { type: Boolean, default: true },
    logLevel: { type: String, default: 'info' },
    alertOnError: { type: Boolean, default: true },
    alertOnSlow: { type: Boolean, default: true },
    slowThreshold: { type: Number, default: 1000 } // ms
  },
  status: {
    type: String,
    enum: ['active', 'deprecated', 'beta', 'draft'],
    default: 'draft'
  },
  usage: {
    totalCalls: { type: Number, default: 0 },
    totalErrors: { type: Number, default: 0 },
    averageLatency: { type: Number, default: 0 },
    lastCalledAt: Date
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

APIEndpointSchema.index({ company: 1, path: 1, method: 1 });
APIEndpointSchema.index({ endpointId: 1 });

// API Key Schema
const APIKeySchema = new mongoose.Schema({
  keyId: {
    type: String,
    required: true,
    unique: true
  },
  name: String,
  key: {
    type: String,
    required: true,
    unique: true
  },
  hashedKey: String,
  type: {
    type: String,
    enum: ['server', 'client', 'mobile', 'partner', 'test'],
    default: 'server'
  },
  owner: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  permissions: {
    endpoints: [String],
    read: [String],
    write: [String],
    allowedIPs: [String],
    allowedOrigins: [String],
    allowedReferrers: [String]
  },
  throttling: {
    rateLimit: { type: Number, default: 1000 },
    burstLimit: { type: Number, default: 200 },
    dailyQuota: { type: Number, default: 10000 },
    monthlyQuota: { type: Number, default: 100000 }
  },
  usage: {
    today: { type: Number, default: 0 },
    thisMonth: { type: Number, default: 0 },
    total: { type: Number, default: 0 },
    lastReset: {
      type: Date,
      default: Date.now
    }
  },
  status: {
    type: String,
    enum: ['active', 'suspended', 'revoked', 'expired'],
    default: 'active'
  },
  expiresAt: Date,
  lastUsedAt: Date,
  lastUsedIp: String,
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

// API Usage Log Schema
const APIUsageLogSchema = new mongoose.Schema({
  requestId: {
    type: String,
    required: true
  },
  timestamp: {
    type: Date,
    default: Date.now
  },
  endpoint: {
    id: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'APIEndpoint'
    },
    path: String,
    method: String
  },
  apiKey: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'APIKey'
  },
  client: {
    user: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    ip: String,
    userAgent: String,
    origin: String,
    referer: String
  },
  request: {
    headers: mongoose.Schema.Types.Mixed,
    query: mongoose.Schema.Types.Mixed,
    body: mongoose.Schema.Types.Mixed,
    size: Number
  },
  response: {
    statusCode: Number,
    headers: mongoose.Schema.Types.Mixed,
    body: mongoose.Schema.Types.Mixed,
    size: Number,
    time: Number // ms
  },
  metrics: {
    latency: Number, // ms
    upstreamLatency: Number,
    cacheHit: Boolean,
    rateLimited: Boolean
  },
  error: {
    code: String,
    message: String,
    stack: String
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

APIUsageLogSchema.index({ company: 1, timestamp: -1 });
APIUsageLogSchema.index({ 'endpoint.path': 1, 'endpoint.method': 1 });
APIUsageLogSchema.index({ apiKey: 1 });

// Webhook Schema
const WebhookSchema = new mongoose.Schema({
  webhookId: {
    type: String,
    required: true,
    unique: true
  },
  name: String,
  url: {
    type: String,
    required: true
  },
  events: [{
    type: String,
    enum: ['shipment.created', 'shipment.updated', 'shipment.delivered', 'shipment.cancelled', 'driver.assigned', 'driver.location', 'invoice.paid', 'payment.received', 'document.uploaded', 'alert.triggered', 'custom']
  }],
  secret: String, // For signature verification
  headers: mongoose.Schema.Types.Mixed,
  active: { type: Boolean, default: true },
  retry: {
    maxAttempts: { type: Number, default: 3 },
    backoffMultiplier: { type: Number, default: 2 }
  },
  filtering: {
    conditions: [{
      field: String,
      operator: { type: String, enum: ['eq', 'ne', 'gt', 'lt', 'contains', 'regex'] },
      value: mongoose.Schema.Types.Mixed
    }]
  },
  deliveryHistory: [{
    event: String,
    payload: mongoose.Schema.Types.Mixed,
    status: { type: String, enum: ['success', 'failed', 'retrying'] },
    responseStatus: Number,
    responseBody: String,
    attempts: Number,
    timestamp: Date,
    duration: Number
  }],
  stats: {
    totalDeliveries: { type: Number, default: 0 },
    successful: { type: Number, default: 0 },
    failed: { type: Number, default: 0 },
    averageLatency: { type: Number, default: 0 }
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

module.exports = {
  APIEndpoint: mongoose.model('APIEndpoint', APIEndpointSchema),
  APIKey: mongoose.model('APIKey', APIKeySchema),
  APIUsageLog: mongoose.model('APIUsageLog', APIUsageLogSchema),
  Webhook: mongoose.model('Webhook', WebhookSchema)
};
