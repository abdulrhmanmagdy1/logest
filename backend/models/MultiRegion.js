//
/**
 * ============================================
 * 🌍 Multi-Region Support - دعم المناطق المتعددة
 * ============================================
 */

const mongoose = require('mongoose');

// Region Schema
const RegionSchema = new mongoose.Schema({
  regionId: {
    type: String,
    required: true,
    unique: true
  },
  name: String,
  code: {
    type: String,
    required: true
  },
  location: {
    country: String,
    city: String,
    timezone: String,
    coordinates: {
      lat: Number,
      lng: Number
    }
  },
  status: {
    type: String,
    enum: ['active', 'maintenance', 'offline', 'planned'],
    default: 'active'
  },
  infrastructure: {
    servers: [{
      id: String,
      type: { type: String, enum: ['web', 'app', 'db', 'cache', 'queue', 'storage'] },
      status: String,
      ip: String,
      specs: {
        cpu: String,
        memory: String,
        storage: String
      }
    }],
    loadBalancer: {
      enabled: Boolean,
      type: String,
      endpoints: [String]
    },
    cdn: {
      enabled: Boolean,
      provider: String,
      endpoints: [String]
    }
  },
  replication: {
    enabled: { type: Boolean, default: false },
    sourceRegion: String,
    lag: Number, // seconds
    lastSync: Date
  },
  routing: {
    geolocation: [String], // Countries routed to this region
    latency: mongoose.Schema.Types.Mixed, // Country -> latency mapping
    priority: { type: Number, default: 1 }
  },
  compliance: {
    dataResidency: { type: Boolean, default: false },
    gdprCompliant: { type: Boolean, default: false },
    certifications: [String]
  },
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

// Region Health Schema
const RegionHealthSchema = new mongoose.Schema({
  region: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Region',
    required: true
  },
  timestamp: {
    type: Date,
    default: Date.now
  },
  status: {
    type: String,
    enum: ['healthy', 'degraded', 'down', 'unknown']
  },
  metrics: {
    latency: {
      avg: Number,
      p95: Number,
      p99: Number
    },
    throughput: {
      requests: Number,
      errors: Number
    },
    capacity: {
      cpu: Number, // percentage
      memory: Number,
      disk: Number,
      connections: Number
    },
    replication: {
      lag: Number,
      lagging: Boolean
    }
  },
  alerts: [{
    severity: String,
    message: String,
    metric: String,
    value: Number,
    threshold: Number
  }],
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

RegionHealthSchema.index({ region: 1, timestamp: -1 });

// Data Residency Rule Schema
const DataResidencyRuleSchema = new mongoose.Schema({
  ruleId: {
    type: String,
    required: true,
    unique: true
  },
  name: String,
  entity: {
    type: String,
    required: true
  },
  countries: [String],
  action: {
    type: String,
    enum: ['store', 'replicate', 'backup', 'process']
  },
  constraint: {
    type: String,
    enum: ['must_reside', 'cannot_leave', 'can_replicate', 'local_processing']
  },
  regions: [String],
  enabled: { type: Boolean, default: true },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

// Failover Config Schema
const FailoverConfigSchema = new mongoose.Schema({
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  enabled: { type: Boolean, default: false },
  strategy: {
    type: String,
    enum: ['active_passive', 'active_active', 'hybrid']
  },
  automatic: { type: Boolean, default: true },
  thresholds: {
    latency: Number, // ms
    errorRate: Number, // percentage
    downtime: Number // minutes
  },
  primaryRegion: String,
  secondaryRegions: [String],
  healthCheck: {
    interval: { type: Number, default: 30 }, // seconds
    timeout: { type: Number, default: 10 },
    endpoints: [String]
  },
  lastFailover: {
    from: String,
    to: String,
    reason: String,
    timestamp: Date,
    duration: Number
  },
  history: [{
    from: String,
    to: String,
    reason: String,
    timestamp: Date,
    duration: Number,
    manual: Boolean
  }]
});

module.exports = {
  Region: mongoose.model('Region', RegionSchema),
  RegionHealth: mongoose.model('RegionHealth', RegionHealthSchema),
  DataResidencyRule: mongoose.model('DataResidencyRule', DataResidencyRuleSchema),
  FailoverConfig: mongoose.model('FailoverConfig', FailoverConfigSchema)
};
