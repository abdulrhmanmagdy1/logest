//
/**
 * ============================================
 * 📊 Business Intelligence - الذكاء التجاري
 * ============================================
 */

const mongoose = require('mongoose');

// Dashboard Schema
const DashboardSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true
  },
  description: String,
  type: {
    type: String,
    enum: ['executive', 'operational', 'analytical', 'custom'],
    default: 'operational'
  },
  category: {
    type: String,
    enum: ['logistics', 'finance', 'sales', 'operations', 'hr', 'fleet', 'customer', 'custom'],
    required: true
  },
  layout: {
    type: {
      type: String,
      enum: ['grid', 'free', 'tabs'],
      default: 'grid'
    },
    columns: { type: Number, default: 3 },
    rowHeight: { type: Number, default: 100 }
  },
  widgets: [{
    id: String,
    type: {
      type: String,
      enum: ['chart', 'metric', 'table', 'map', 'text', 'image', 'gauge', 'list', 'timeline', 'custom']
    },
    title: String,
    position: {
      x: Number,
      y: Number,
      w: Number,
      h: Number
    },
    dataSource: {
      type: {
        type: String,
        enum: ['api', 'query', 'function', 'static', 'widget']
      },
      endpoint: String,
      query: String,
      params: mongoose.Schema.Types.Mixed,
      refreshInterval: Number // seconds
    },
    visualization: {
      chartType: {
        type: String,
        enum: ['line', 'bar', 'column', 'pie', 'doughnut', 'area', 'scatter', 'bubble', 'radar', 'gauge', 'funnel', 'heatmap', 'table', 'kpi', 'card']
      },
      config: mongoose.Schema.Types.Mixed,
      colors: [String],
      theme: String
    },
    filters: mongoose.Schema.Types.Mixed,
    drilldown: {
      enabled: { type: Boolean, default: false },
      target: String
    },
    alerts: [{
      condition: String,
      threshold: Number,
      color: String,
      message: String
    }]
  }],
  filters: [{
    name: String,
    type: {
      type: String,
      enum: ['date_range', 'dropdown', 'multiselect', 'text', 'number', 'boolean']
    },
    field: String,
    defaultValue: mongoose.Schema.Types.Mixed,
    options: mongoose.Schema.Types.Mixed
  }],
  access: {
    visibility: {
      type: String,
      enum: ['public', 'private', 'restricted'],
      default: 'private'
    },
    allowedRoles: [String],
    allowedUsers: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }]
  },
  settings: {
    autoRefresh: { type: Boolean, default: true },
    refreshInterval: { type: Number, default: 300 }, // seconds
    showLastUpdated: { type: Boolean, default: true },
    allowExport: { type: Boolean, default: true },
    allowPrint: { type: Boolean, default: true }
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

DashboardSchema.index({ company: 1, category: 1 });

// Report Schema
const ReportSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true
  },
  description: String,
  type: {
    type: String,
    enum: ['standard', 'custom', 'scheduled', 'ad_hoc'],
    default: 'standard'
  },
  category: {
    type: String,
    enum: ['logistics', 'finance', 'sales', 'operations', 'hr', 'fleet', 'customer', 'compliance', 'custom'],
    required: true
  },
  dataSource: {
    entities: [String], // collection names
    fields: [{
      name: String,
      label: String,
      type: String,
      aggregation: {
        type: String,
        enum: ['sum', 'avg', 'count', 'min', 'max', 'none']
      }
    }],
    joins: [{
      from: String,
      to: String,
      localField: String,
      foreignField: String,
      as: String
    }],
    filters: mongoose.Schema.Types.Mixed,
    groupBy: [String],
    orderBy: [{
      field: String,
      direction: { type: String, enum: ['asc', 'desc'] }
    }],
    limit: Number
  },
  format: {
    type: {
      type: String,
      enum: ['table', 'chart', 'pivot', 'summary'],
      default: 'table'
    },
    chartType: String,
    pageSize: { type: String, default: 'A4' },
    orientation: { type: String, default: 'portrait' }
  },
  parameters: [{
    name: String,
    label: String,
    type: {
      type: String,
      enum: ['string', 'number', 'date', 'boolean', 'select', 'multiselect']
    },
    required: { type: Boolean, default: false },
    defaultValue: mongoose.Schema.Types.Mixed,
    options: mongoose.Schema.Types.Mixed
  }],
  schedule: {
    enabled: { type: Boolean, default: false },
    frequency: {
      type: String,
      enum: ['once', 'hourly', 'daily', 'weekly', 'monthly', 'quarterly']
    },
    dayOfWeek: Number,
    dayOfMonth: Number,
    time: String,
    timezone: { type: String, default: 'Asia/Riyadh' },
    recipients: [{
      email: String,
      format: { type: String, enum: ['pdf', 'excel', 'csv', 'html'] }
    }],
    lastRun: Date,
    nextRun: Date
  },
  exports: [{
    format: String,
    generatedAt: Date,
    generatedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    fileUrl: String,
    fileSize: Number
  }],
  permissions: {
    view: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
    edit: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
    roles: [String]
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

ReportSchema.index({ company: 1, category: 1 });
ReportSchema.index({ 'schedule.enabled': 1, 'schedule.nextRun': 1 });

// KPI Schema
const KPISchema = new mongoose.Schema({
  name: {
    type: String,
    required: true
  },
  code: {
    type: String,
    required: true
  },
  category: {
    type: String,
    enum: ['logistics', 'finance', 'operations', 'customer', 'fleet', 'hr', 'quality', 'safety'],
    required: true
  },
  description: String,
  unit: {
    type: String,
    enum: ['number', 'percentage', 'currency', 'time', 'distance', 'weight', 'count', 'ratio'],
    required: true
  },
  formula: {
    type: {
      type: String,
      enum: ['simple', 'complex', 'sql', 'script'],
      default: 'simple'
    },
    numerator: String,
    denominator: String,
    expression: String,
    sql: String,
    script: String
  },
  target: {
    value: Number,
    min: Number,
    max: Number,
    benchmark: Number,
    direction: {
      type: String,
      enum: ['higher_is_better', 'lower_is_better', 'target_range']
    }
  },
  thresholds: {
    excellent: Number,
    good: Number,
    warning: Number,
    critical: Number
  },
  dataSource: {
    entity: String,
    field: String,
    filters: mongoose.Schema.Types.Mixed,
    aggregation: String,
    timeField: String
  },
  frequency: {
    type: String,
    enum: ['realtime', 'hourly', 'daily', 'weekly', 'monthly', 'quarterly', 'yearly'],
    default: 'daily'
  },
  dimensions: [String], // for drilling down: region, department, etc.
  currentValue: Number,
  previousValue: Number,
  change: Number, // percentage change
  trend: {
    type: String,
    enum: ['up', 'down', 'stable', 'null']
  },
  status: {
    type: String,
    enum: ['excellent', 'good', 'warning', 'critical', 'not_calculated'],
    default: 'not_calculated'
  },
  history: [{
    period: String,
    value: Number,
    target: Number,
    calculatedAt: Date
  }],
  alerts: {
    enabled: { type: Boolean, default: false },
    onThreshold: String,
    notifyUsers: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
    emailTemplate: String
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  isActive: {
    type: Boolean,
    default: true
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

KPISchema.index({ company: 1, category: 1 });
KPISchema.index({ code: 1, company: 1 });

// Data Source/Connector Schema
const DataSourceSchema = new mongoose.Schema({
  name: String,
  type: {
    type: String,
    enum: ['database', 'api', 'file', 'webhook', 'streaming'],
    required: true
  },
  config: {
    // For database
    host: String,
    port: Number,
    database: String,
    username: String,
    password: String,
    ssl: Boolean,
    // For API
    baseUrl: String,
    headers: mongoose.Schema.Types.Mixed,
    auth: mongoose.Schema.Types.Mixed,
    // For file
    path: String,
    format: String,
    delimiter: String,
    // For streaming
    endpoint: String,
    protocol: String
  },
  entities: [{
    name: String,
    collection: String,
    fields: mongoose.Schema.Types.Mixed,
    refreshInterval: Number
  }],
  status: {
    type: String,
    enum: ['active', 'inactive', 'error'],
    default: 'inactive'
  },
  lastSync: Date,
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

module.exports = {
  Dashboard: mongoose.models.Dashboard || mongoose.model('Dashboard', DashboardSchema),
  Report: mongoose.models.Report || mongoose.model('Report', ReportSchema),
  KPI: mongoose.models.KPI || mongoose.model('KPI', KPISchema),
  DataSource: mongoose.models.DataSource || mongoose.model('DataSource', DataSourceSchema)
};
