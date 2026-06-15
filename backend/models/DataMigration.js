//
/**
 * ============================================
 * 📦 Data Migration - نظام استيراد وتصدير البيانات
 * ============================================
 */

const mongoose = require('mongoose');

// Data Import Job Schema
const DataImportSchema = new mongoose.Schema({
  importId: {
    type: String,
    required: true,
    unique: true
  },
  type: {
    type: String,
    enum: ['shipments', 'customers', 'drivers', 'vehicles', 'invoices', 'products', 'inventory', 'contacts', 'employees'],
    required: true
  },
  source: {
    format: {
      type: String,
      enum: ['csv', 'excel', 'json', 'xml', 'database']
    },
    filename: String,
    originalName: String,
    size: Number,
    encoding: String
  },
  mapping: {
    fields: [{
      source: String,
      target: String,
      transform: String,
      default: mongoose.Schema.Types.Mixed
    }],
    skipHeader: { type: Boolean, default: true },
    delimiter: { type: String, default: ',' },
    dateFormat: { type: String, default: 'YYYY-MM-DD' }
  },
  validation: {
    rules: [{
      field: String,
      type: String,
      required: Boolean,
      unique: Boolean,
      min: Number,
      max: Number,
      pattern: String
    }]
  },
  status: {
    type: String,
    enum: ['pending', 'validating', 'processing', 'completed', 'failed', 'cancelled'],
    default: 'pending'
  },
  progress: {
    total: { type: Number, default: 0 },
    processed: { type: Number, default: 0 },
    succeeded: { type: Number, default: 0 },
    failed: { type: Number, default: 0 },
    percentage: { type: Number, default: 0 }
  },
  results: {
    created: [{ type: mongoose.Schema.Types.ObjectId }],
    updated: [{ type: mongoose.Schema.Types.ObjectId }],
    skipped: [{
      row: Number,
      reason: String,
      data: mongoose.Schema.Types.Mixed
    }],
    errors: [{
      row: Number,
      field: String,
      value: mongoose.Schema.Types.Mixed,
      message: String,
      type: {
        type: String,
        enum: ['validation', 'duplicate', 'not_found', 'system']
      }
    }]
  },
  options: {
    skipDuplicates: { type: Boolean, default: true },
    updateExisting: { type: Boolean, default: false },
    dryRun: { type: Boolean, default: false },
    batchSize: { type: Number, default: 100 },
    notifyOnComplete: { type: Boolean, default: true }
  },
  initiatedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  startedAt: Date,
  completedAt: Date,
  duration: Number, // seconds
  createdAt: {
    type: Date,
    default: Date.now
  }
});

// Data Export Job Schema
const DataExportSchema = new mongoose.Schema({
  exportId: {
    type: String,
    required: true,
    unique: true
  },
  type: {
    type: String,
    enum: ['shipments', 'customers', 'drivers', 'vehicles', 'invoices', 'analytics', 'custom'],
    required: true
  },
  format: {
    type: String,
    enum: ['csv', 'excel', 'pdf', 'json', 'xml'],
    default: 'excel'
  },
  query: mongoose.Schema.Types.Mixed, // MongoDB query
  filters: {
    dateRange: {
      from: Date,
      to: Date
    },
    status: [String],
    fields: [String],
    conditions: [mongoose.Schema.Types.Mixed]
  },
  columns: [{
    field: String,
    header: String,
    width: Number,
    format: String
  }],
  status: {
    type: String,
    enum: ['pending', 'processing', 'completed', 'failed', 'cancelled'],
    default: 'pending'
  },
  progress: {
    total: { type: Number, default: 0 },
    processed: { type: Number, default: 0 },
    percentage: { type: Number, default: 0 }
  },
  result: {
    filename: String,
    path: String,
    url: String,
    size: Number,
    records: Number,
    expiresAt: Date
  },
  options: {
    includeHeaders: { type: Boolean, default: true },
    compressed: { type: Boolean, default: false },
    encrypted: { type: Boolean, default: false },
    password: String,
    notifyOnComplete: { type: Boolean, default: true }
  },
  initiatedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  startedAt: Date,
  completedAt: Date,
  duration: Number,
  createdAt: {
    type: Date,
    default: Date.now
  }
});

// Data Sync Schema (for external systems)
const DataSyncSchema = new mongoose.Schema({
  syncId: {
    type: String,
    required: true,
    unique: true
  },
  name: String,
  type: {
    type: String,
    enum: ['erp', 'crm', 'accounting', 'warehouse', 'custom']
  },
  direction: {
    type: String,
    enum: ['inbound', 'outbound', 'bidirectional']
  },
  source: {
    system: String,
    connection: {
      type: {
        type: String,
        enum: ['api', 'database', 'file', 'webhook', 'sftp']
      },
      config: mongoose.Schema.Types.Mixed
    }
  },
  entities: [{
    name: String,
    sourceTable: String,
    targetCollection: String,
    mapping: mongoose.Schema.Types.Mixed,
    syncMode: {
      type: String,
      enum: ['full', 'incremental', 'change_detection']
    },
    keyField: String,
    lastSyncValue: mongoose.Schema.Types.Mixed
  }],
  schedule: {
    enabled: { type: Boolean, default: false },
    frequency: {
      type: String,
      enum: ['manual', 'hourly', 'daily', 'weekly', 'custom']
    },
    cron: String,
    timezone: String
  },
  status: {
    type: String,
    enum: ['active', 'paused', 'error', 'disabled'],
    default: 'active'
  },
  lastRun: {
    startedAt: Date,
    completedAt: Date,
    status: String,
    records: Number,
    errors: Number
  },
  history: [{
    startedAt: Date,
    completedAt: Date,
    status: String,
    records: Number,
    errors: [String]
  }],
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
  DataImport: mongoose.model('DataImport', DataImportSchema),
  DataExport: mongoose.model('DataExport', DataExportSchema),
  DataSync: mongoose.model('DataSync', DataSyncSchema)
};
