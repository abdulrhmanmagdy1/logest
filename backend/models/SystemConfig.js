//
/**
 * ============================================
 * ⚙️ System Configuration - إعدادات النظام
 * ============================================
 */

const mongoose = require('mongoose');

// System Configuration Schema
const SystemConfigSchema = new mongoose.Schema({
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true,
    unique: true
  },
  // General Settings
  general: {
    timezone: { type: String, default: 'Asia/Riyadh' },
    dateFormat: { type: String, default: 'YYYY-MM-DD' },
    timeFormat: { type: String, default: '24h' },
    currency: { type: String, default: 'SAR' },
    language: { type: String, default: 'ar' },
    defaultWeightUnit: { type: String, default: 'kg' },
    defaultDistanceUnit: { type: String, default: 'km' }
  },
  
  // Branding
  branding: {
    logo: String,
    favicon: String,
    primaryColor: { type: String, default: '#1a73e8' },
    secondaryColor: { type: String, default: '#34a853' },
    accentColor: { type: String, default: '#fbbc04' },
    customCss: String,
    emailHeader: String,
    emailFooter: String,
    customDomain: String,
    portalTitle: String,
    portalSubtitle: String
  },
  
  // Notifications
  notifications: {
    email: {
      enabled: { type: Boolean, default: true },
      fromName: String,
      fromEmail: String,
      replyTo: String,
      smtp: {
        host: String,
        port: Number,
        secure: Boolean,
        auth: {
          user: String,
          pass: String
        }
      }
    },
    sms: {
      enabled: { type: Boolean, default: false },
      provider: {
        type: String,
        enum: ['twilio', 'messagebird', 'vonage', 'custom']
      },
      config: mongoose.Schema.Types.Mixed
    },
    push: {
      enabled: { type: Boolean, default: true },
      firebase: {
        serverKey: String,
        senderId: String
      },
      apns: {
        cert: String,
        key: String,
        passphrase: String
      }
    },
    whatsapp: {
      enabled: { type: Boolean, default: false },
      provider: String,
      config: mongoose.Schema.Types.Mixed
    }
  },
  
  // Security
  security: {
    passwordPolicy: {
      minLength: { type: Number, default: 8 },
      requireUppercase: { type: Boolean, default: true },
      requireLowercase: { type: Boolean, default: true },
      requireNumbers: { type: Boolean, default: true },
      requireSpecialChars: { type: Boolean, default: true },
      expiryDays: { type: Number, default: 90 },
      historyCount: { type: Number, default: 5 }
    },
    session: {
      timeout: { type: Number, default: 30 }, // minutes
      concurrentLimit: { type: Number, default: 5 },
      enforceSingleSession: { type: Boolean, default: false }
    },
    twoFactor: {
      enabled: { type: Boolean, default: false },
      required: { type: Boolean, default: false },
      methods: [{ type: String, enum: ['app', 'sms', 'email'] }]
    },
    ipWhitelist: [String],
    apiSecurity: {
      rateLimit: { type: Number, default: 1000 },
      requireApiKey: { type: Boolean, default: true }
    }
  },
  
  // Tracking
  tracking: {
    updateInterval: { type: Number, default: 60 }, // seconds
    geofence: {
      enabled: { type: Boolean, default: true },
      radius: { type: Number, default: 500 }, // meters
      alerts: {
        enter: { type: Boolean, default: true },
        exit: { type: Boolean, default: true }
      }
    },
    notifications: {
      statusChange: { type: Boolean, default: true },
      delay: { type: Boolean, default: true },
      delivery: { type: Boolean, default: true },
      exception: { type: Boolean, default: true }
    }
  },
  
  // Pricing
  pricing: {
    autoCalculate: { type: Boolean, default: true },
    basePrice: { type: Number, default: 0 },
    factors: {
      distance: { enabled: Boolean, multiplier: Number },
      weight: { enabled: Boolean, multiplier: Number },
      volume: { enabled: Boolean, multiplier: Number },
      fuelSurcharge: { enabled: Boolean, percentage: Number },
      tolls: { enabled: Boolean },
      customs: { enabled: Boolean }
    },
    tax: {
      enabled: { type: Boolean, default: true },
      rate: { type: Number, default: 15 }, // VAT %
      inclusive: { type: Boolean, default: false }
    }
  },
  
  // Automation
  automation: {
    autoAssign: {
      enabled: { type: Boolean, default: false },
      criteria: [{
        type: String,
        enum: ['location', 'capacity', 'availability', 'rating', 'cost']
      }]
    },
    autoDispatch: {
      enabled: { type: Boolean, default: false },
      buffer: { type: Number, default: 30 } // minutes
    },
    autoConfirm: {
      enabled: { type: Boolean, default: false },
      delay: { type: Number, default: 5 } // minutes
    },
    workflows: {
      enabled: { type: Boolean, default: true },
      defaultWorkflow: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Workflow'
      }
    }
  },
  
  // Integrations
  integrations: {
    mapProvider: {
      type: String,
      enum: ['google', 'mapbox', 'here', 'osm'],
      apiKey: String
    },
    paymentGateway: {
      type: String,
      enum: ['stripe', 'paypal', 'square', 'custom'],
      config: mongoose.Schema.Types.Mixed
    },
    erp: {
      type: String,
      enum: ['sap', 'oracle', 'dynamics', 'odoo', 'none'],
      config: mongoose.Schema.Types.Mixed
    },
    accounting: {
      type: String,
      enum: ['quickbooks', 'xero', 'freshbooks', 'none'],
      config: mongoose.Schema.Types.Mixed
    }
  },
  
  // Compliance
  compliance: {
    gdpr: {
      enabled: { type: Boolean, default: true },
      dataRetentionDays: { type: Number, default: 2555 }, // 7 years
      anonymizationEnabled: { type: Boolean, default: true }
    },
    audit: {
      enabled: { type: Boolean, default: true },
      logLevel: { type: String, default: 'info' },
      retentionDays: { type: Number, default: 365 }
    }
  },
  
  // Maintenance
  maintenance: {
    mode: { type: Boolean, default: false },
    message: String,
    scheduled: {
      enabled: { type: Boolean, default: false },
      startTime: Date,
      endTime: Date
    }
  },
  
  updatedAt: {
    type: Date,
    default: Date.now
  },
  updatedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  }
});

SystemConfigSchema.index({ company: 1 });

// Feature Flags Schema
const FeatureFlagSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true,
    unique: true
  },
  description: String,
  enabled: {
    type: Boolean,
    default: false
  },
  rollout: {
    type: String,
    enum: ['all', 'percentage', 'users', 'companies'],
    default: 'all'
  },
  rolloutPercentage: {
    type: Number,
    min: 0,
    max: 100,
    default: 100
  },
  allowedUsers: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  }],
  allowedCompanies: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company'
  }],
  dependencies: [String], // Other feature flags required
  expiresAt: Date,
  createdAt: {
    type: Date,
    default: Date.now
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
});

// System Health Schema
const SystemHealthSchema = new mongoose.Schema({
  timestamp: {
    type: Date,
    default: Date.now
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company'
  },
  metrics: {
    api: {
      requests: Number,
      errors: Number,
      latency: Number, // ms
      rate: Number // requests/sec
    },
    database: {
      connections: Number,
      slowQueries: Number,
      replicationLag: Number
    },
    cache: {
      hitRate: Number,
      evictions: Number,
      memory: Number
    },
    queue: {
      size: Number,
      processed: Number,
      failed: Number
    },
    storage: {
      used: Number,
      total: Number,
      growthRate: Number
    }
  },
  services: [{
    name: String,
    status: {
      type: String,
      enum: ['healthy', 'degraded', 'down']
    },
    responseTime: Number,
    lastChecked: Date,
    message: String
  }],
  alerts: [{
    severity: {
      type: String,
      enum: ['info', 'warning', 'critical']
    },
    message: String,
    metric: String,
    threshold: Number,
    value: Number,
    acknowledged: { type: Boolean, default: false }
  }]
});

SystemHealthSchema.index({ company: 1, timestamp: -1 });

module.exports = {
  SystemConfig: mongoose.model('SystemConfig', SystemConfigSchema),
  FeatureFlag: mongoose.model('FeatureFlag', FeatureFlagSchema),
  SystemHealth: mongoose.model('SystemHealth', SystemHealthSchema)
};
