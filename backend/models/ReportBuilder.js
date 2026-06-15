//
/**
 * ============================================
 * 📊 Report Builder - منشئ التقارير
 * ============================================
 */

const mongoose = require('mongoose');

// Custom Report Schema
const CustomReportSchema = new mongoose.Schema({
  reportId: {
    type: String,
    required: true,
    unique: true
  },
  name: {
    type: String,
    required: true
  },
  description: String,
  category: {
    type: String,
    enum: ['operational', 'financial', 'analytical', 'executive', 'custom']
  },
  dataSource: {
    type: { type: String, enum: ['shipment', 'finance', 'hr', 'vehicle', 'customer', 'warehouse', 'custom'] },
    collection: String,
    filters: mongoose.Schema.Types.Mixed
  },
  fields: [{
    name: String,
    source: String,
    alias: String,
    format: String,
    aggregation: { type: String, enum: ['none', 'sum', 'count', 'avg', 'min', 'max', 'group'] }
  }],
  filters: [{
    field: String,
    operator: { type: String, enum: ['eq', 'ne', 'gt', 'gte', 'lt', 'lte', 'in', 'nin', 'contains', 'between'] },
    value: mongoose.Schema.Types.Mixed,
    logic: { type: String, enum: ['and', 'or'], default: 'and' }
  }],
  groupBy: [String],
  sortBy: [{
    field: String,
    order: { type: String, enum: ['asc', 'desc'] }
  }],
  visualization: {
    type: { type: String, enum: ['table', 'chart', 'pivot', 'dashboard', 'map'] },
    chartType: { type: String, enum: ['bar', 'line', 'pie', 'doughnut', 'area', 'scatter'] },
    options: mongoose.Schema.Types.Mixed
  },
  schedule: {
    enabled: { type: Boolean, default: false },
    frequency: { type: String, enum: ['hourly', 'daily', 'weekly', 'monthly'] },
    dayOfWeek: Number,
    dayOfMonth: Number,
    time: String,
    recipients: [String]
  },
  isPublic: { type: Boolean, default: false },
  sharedWith: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  }],
  createdBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
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

// Report Execution Schema
const ReportExecutionSchema = new mongoose.Schema({
  executionId: {
    type: String,
    required: true,
    unique: true
  },
  report: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'CustomReport'
  },
  status: {
    type: String,
    enum: ['queued', 'running', 'completed', 'failed'],
    default: 'queued'
  },
  parameters: mongoose.Schema.Types.Mixed,
  result: {
    rows: Number,
    data: mongoose.Schema.Types.Mixed,
    summary: mongoose.Schema.Types.Mixed,
    fileUrl: String
  },
  duration: Number, // seconds
  error: String,
  executedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
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

module.exports = {
  CustomReport: mongoose.model('CustomReport', CustomReportSchema),
  ReportExecution: mongoose.model('ReportExecution', ReportExecutionSchema)
};
