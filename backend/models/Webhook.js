//
/**
 * ============================================
 * 🪝 Webhook Model - نظام الويب هوك للتكامل الخارجي
 * ============================================
 */

const mongoose = require('mongoose');
const crypto = require('crypto');

const WebhookSchema = new mongoose.Schema({
  // Owner
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  
  // Basic Info
  name: {
    type: String,
    required: true
  },
  
  description: String,
  
  // Endpoint
  url: {
    type: String,
    required: true
  },
  
  // Events to listen to
  events: [{
    type: String,
    enum: [
      'shipment.created',
      'shipment.updated',
      'shipment.status_changed',
      'shipment.delivered',
      'shipment.cancelled',
      'invoice.created',
      'invoice.paid',
      'invoice.overdue',
      'driver.assigned',
      'driver.location_updated',
      'temperature.alert',
      'document.signed',
      'payment.received',
      'user.created',
      'user.updated',
      '*'
    ]
  }],
  
  // Security
  secret: {
    type: String,
    default: () => crypto.randomBytes(32).toString('hex')
  },
  
  headers: [{
    key: String,
    value: String
  }],
  
  // Status
  status: {
    type: String,
    enum: ['active', 'paused', 'disabled', 'failed'],
    default: 'active'
  },
  
  // Retry Configuration
  retryConfig: {
    maxRetries: { type: Number, default: 5 },
    retryDelay: { type: Number, default: 5000 }, // ms
    exponentialBackoff: { type: Boolean, default: true }
  },
  
  // Statistics
  stats: {
    totalDeliveries: { type: Number, default: 0 },
    successfulDeliveries: { type: Number, default: 0 },
    failedDeliveries: { type: Number, default: 0 },
    lastDelivery: Date,
    lastFailure: Date,
    lastError: String
  },
  
  // Rate Limiting
  rateLimit: {
    requestsPerSecond: { type: Number, default: 10 },
    burstSize: { type: Number, default: 20 }
  },
  
  // Metadata
  createdAt: { type: Date, default: Date.now },
  updatedAt: { type: Date, default: Date.now }
});

// Webhook Delivery Log
const WebhookDeliverySchema = new mongoose.Schema({
  webhook: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Webhook',
    required: true
  },
  
  event: {
    type: String,
    required: true
  },
  
  payload: {
    type: mongoose.Schema.Types.Mixed,
    required: true
  },
  
  // Delivery Status
  status: {
    type: String,
    enum: ['pending', 'delivered', 'failed', 'retrying'],
    default: 'pending'
  },
  
  // HTTP Details
  request: {
    method: { type: String, default: 'POST' },
    headers: mongoose.Schema.Types.Mixed,
    body: mongoose.Schema.Types.Mixed,
    timestamp: { type: Date, default: Date.now }
  },
  
  response: {
    statusCode: Number,
    headers: mongoose.Schema.Types.Mixed,
    body: String,
    responseTime: Number, // ms
    timestamp: Date
  },
  
  // Retry History
  attempts: [{
    timestamp: Date,
    statusCode: Number,
    error: String
  }],
  
  // Error Details
  error: {
    message: String,
    code: String,
    stack: String
  },
  
  createdAt: { type: Date, default: Date.now }
});

// Indexes
WebhookSchema.index({ company: 1, status: 1 });
WebhookSchema.index({ events: 1 });
WebhookDeliverySchema.index({ webhook: 1, createdAt: -1 });
WebhookDeliverySchema.index({ status: 1 });

// Pre-save
WebhookSchema.pre('save', function(next) {
  this.updatedAt = Date.now();
  next();
});

// Methods
WebhookSchema.methods.generateSignature = function(payload) {
  return crypto
    .createHmac('sha256', this.secret)
    .update(JSON.stringify(payload))
    .digest('hex');
};

WebhookSchema.methods.verifySignature = function(payload, signature) {
  const expected = this.generateSignature(payload);
  return crypto.timingSafeEqual(
    Buffer.from(signature),
    Buffer.from(expected)
  );
};

module.exports = {
  Webhook: mongoose.model('Webhook', WebhookSchema),
  WebhookDelivery: mongoose.model('WebhookDelivery', WebhookDeliverySchema)
};
