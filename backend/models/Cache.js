//
/**
 * ============================================
 * 💾 Cache Management - إدارة التخزين المؤقت
 * ============================================
 */

const mongoose = require('mongoose');

// Cache Entry Schema
const CacheEntrySchema = new mongoose.Schema({
  key: {
    type: String,
    required: true,
    unique: true
  },
  value: mongoose.Schema.Types.Mixed,
  tags: [String],
  metadata: {
    size: Number, // bytes
    compression: String,
    encoding: String
  },
  ttl: {
    type: Number, // seconds
    default: 300
  },
  expiresAt: {
    type: Date,
    required: true
  },
  accessedAt: {
    type: Date,
    default: Date.now
  },
  accessCount: {
    type: Number,
    default: 0
  },
  source: {
    type: String,
    enum: ['database', 'api', 'calculation', 'external']
  },
  invalidated: {
    type: Boolean,
    default: false
  },
  invalidatedAt: Date,
  invalidatedBy: String, // event or user
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

CacheEntrySchema.index({ company: 1, key: 1 });
CacheEntrySchema.index({ company: 1, tags: 1 });
CacheEntrySchema.index({ expiresAt: 1 });
CacheEntrySchema.index({ company: 1, accessCount: -1 });

// Cache Stats Schema
const CacheStatsSchema = new mongoose.Schema({
  timestamp: {
    type: Date,
    default: Date.now
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  metrics: {
    totalKeys: Number,
    memoryUsage: Number, // bytes
    hitRate: Number, // percentage
    missRate: Number,
    evicted: Number,
    expired: Number,
    invalidated: Number
  },
  byType: [{
    type: String,
    count: Number,
    hitRate: Number
  }],
  byTag: [{
    tag: String,
    count: Number,
    hitRate: Number
  }],
  topKeys: [{
    key: String,
    hits: Number,
    lastAccessed: Date
  }],
  slowQueries: [{
    key: String,
    fetchTime: Number
  }]
});

CacheStatsSchema.index({ company: 1, timestamp: -1 });

// Cache Warming Job Schema
const CacheWarmingJobSchema = new mongoose.Schema({
  jobId: {
    type: String,
    required: true,
    unique: true
  },
  name: String,
  description: String,
  type: {
    type: String,
    enum: ['scheduled', 'on_demand', 'event_driven']
  },
  keys: [{
    pattern: String,
    priority: Number,
    ttl: Number
  }],
  schedule: {
    enabled: { type: Boolean, default: false },
    cron: String,
    timezone: String
  },
  status: {
    type: String,
    enum: ['idle', 'running', 'completed', 'failed'],
    default: 'idle'
  },
  lastRun: {
    startedAt: Date,
    completedAt: Date,
    keysWarmed: Number,
    keysFailed: Number,
    duration: Number
  },
  history: [{
    startedAt: Date,
    completedAt: Date,
    status: String,
    keysWarmed: Number,
    keysFailed: Number
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

// Cache Invalidation Rule Schema
const CacheInvalidationRuleSchema = new mongoose.Schema({
  ruleId: {
    type: String,
    required: true,
    unique: true
  },
  name: String,
  trigger: {
    event: String, // e.g., 'shipment.updated', 'user.profile.changed'
    entity: String,
    conditions: [{
      field: String,
      operator: String,
      value: mongoose.Schema.Types.Mixed
    }]
  },
  action: {
    type: {
      type: String,
      enum: ['invalidate_key', 'invalidate_tag', 'invalidate_pattern', 'warm_cache']
    },
    target: String,
    keys: [String],
    tags: [String],
    pattern: String
  },
  cascade: {
    enabled: { type: Boolean, default: false },
    relatedEntities: [String]
  },
  enabled: { type: Boolean, default: true },
  executedCount: { type: Number, default: 0 },
  lastExecutedAt: Date,
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

module.exports = {
  CacheEntry: mongoose.model('CacheEntry', CacheEntrySchema),
  CacheStats: mongoose.model('CacheStats', CacheStatsSchema),
  CacheWarmingJob: mongoose.model('CacheWarmingJob', CacheWarmingJobSchema),
  CacheInvalidationRule: mongoose.model('CacheInvalidationRule', CacheInvalidationRuleSchema)
};
