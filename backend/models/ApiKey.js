//
/**
 * ============================================
 * 🔑 API Key Model - مفاتيح API للتكامل الخارجي
 * ============================================
 */

const mongoose = require('mongoose');
const crypto = require('crypto');

const ApiKeySchema = new mongoose.Schema({
  // Owner
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  
  // Key Details
  name: {
    type: String,
    required: true
  },
  
  description: String,
  
  // API Key (hashed for security)
  keyHash: {
    type: String,
    required: true,
    unique: true
  },
  
  // Key prefix for identification (e.g., "ak_live_")
  keyPrefix: {
    type: String,
    required: true
  },
  
  // Last 4 characters for display
  keyLast4: {
    type: String,
    required: true
  },
  
  // Environment
  environment: {
    type: String,
    enum: ['test', 'live'],
    default: 'test'
  },
  
  // Permissions
  permissions: [{
    type: String,
    enum: [
      'shipments.read',
      'shipments.write',
      'shipments.delete',
      'drivers.read',
      'drivers.write',
      'invoices.read',
      'invoices.write',
      'tracking.read',
      'webhooks.read',
      'webhooks.write',
      'analytics.read',
      'documents.read',
      'all'
    ]
  }],
  
  // Rate Limiting
  rateLimit: {
    requestsPerSecond: { type: Number, default: 10 },
    requestsPerMinute: { type: Number, default: 600 },
    requestsPerHour: { type: Number, default: 10000 },
    requestsPerDay: { type: Number, default: 100000 }
  },
  
  // IP Whitelist (optional)
  allowedIPs: [String],
  
  // Allowed Origins (CORS)
  allowedOrigins: [String],
  
  // Usage Statistics
  usage: {
    totalRequests: { type: Number, default: 0 },
    lastUsedAt: Date,
    requestsToday: { type: Number, default: 0 },
    requestsThisMonth: { type: Number, default: 0 },
    requestsByEndpoint: [{
      endpoint: String,
      count: { type: Number, default: 0 }
    }]
  },
  
  // Status
  status: {
    type: String,
    enum: ['active', 'revoked', 'expired', 'suspended'],
    default: 'active'
  },
  
  // Expiration
  expiresAt: Date,
  
  // Last rotated
  lastRotatedAt: {
    type: Date,
    default: Date.now
  },
  
  // Created by
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
ApiKeySchema.index({ keyHash: 1 });
ApiKeySchema.index({ company: 1, status: 1 });
ApiKeySchema.index({ keyPrefix: 1, keyLast4: 1 });

// Pre-save
ApiKeySchema.pre('save', function(next) {
  this.updatedAt = Date.now();
  next();
});

// Static: Generate new API key
ApiKeySchema.statics.generateKey = function() {
  const prefix = 'ak_live_';
  const randomPart = crypto.randomBytes(32).toString('hex');
  const key = `${prefix}${randomPart}`;
  
  const hash = crypto
    .createHash('sha256')
    .update(key)
    .digest('hex');
  
  return {
    key, // Return once to user
    hash, // Store in DB
    last4: key.slice(-4)
  };
};

// Static: Verify API key
ApiKeySchema.statics.verifyKey = async function(apiKey) {
  const hash = crypto
    .createHash('sha256')
    .update(apiKey)
    .digest('hex');
  
  const keyRecord = await this.findOne({
    keyHash: hash,
    status: 'active',
    $or: [
      { expiresAt: { $exists: false } },
      { expiresAt: { $gt: new Date() } }
    ]
  });
  
  return keyRecord;
};

// Method: Check permission
ApiKeySchema.methods.hasPermission = function(permission) {
  return this.permissions.includes('all') || this.permissions.includes(permission);
};

// Method: Record usage
ApiKeySchema.methods.recordUsage = async function(endpoint) {
  this.usage.totalRequests += 1;
  this.usage.lastUsedAt = new Date();
  this.usage.requestsToday += 1;
  this.usage.requestsThisMonth += 1;
  
  // Update endpoint count
  const existingEndpoint = this.usage.requestsByEndpoint.find(
    e => e.endpoint === endpoint
  );
  
  if (existingEndpoint) {
    existingEndpoint.count += 1;
  } else {
    this.usage.requestsByEndpoint.push({ endpoint, count: 1 });
  }
  
  await this.save();
};

// Method: Check rate limit
ApiKeySchema.methods.checkRateLimit = function() {
  const now = new Date();
  
  // Reset daily counter if it's a new day
  const lastUsed = this.usage.lastUsedAt;
  if (lastUsed && lastUsed.getDate() !== now.getDate()) {
    this.usage.requestsToday = 0;
  }
  
  // Check limits
  if (this.usage.requestsToday >= this.rateLimit.requestsPerDay) {
    return { allowed: false, reason: 'daily_limit_exceeded' };
  }
  
  // More granular checks would require Redis/cache
  
  return { allowed: true };
};

// Method: Rotate key
ApiKeySchema.methods.rotate = async function() {
  const newKeyData = ApiKeySchema.statics.generateKey();
  
  this.keyHash = newKeyData.hash;
  this.keyLast4 = newKeyData.last4;
  this.lastRotatedAt = new Date();
  
  await this.save();
  
  return {
    key: newKeyData.key, // Return only once
    rotatedAt: this.lastRotatedAt
  };
};

module.exports = mongoose.model('ApiKey', ApiKeySchema);
