//
/**
 * ============================================
 * 🏭 Data Warehouse - مستودع البيانات
 * ============================================
 */

const mongoose = require('mongoose');

// Data Warehouse Schema
const DataWarehouseSchema = new mongoose.Schema({
  warehouseId: {
    type: String,
    required: true,
    unique: true
  },
  name: String,
  description: String,
  tables: [{
    name: String,
    schema: mongoose.Schema.Types.Mixed,
    records: Number,
    size: Number, // MB
    lastUpdated: Date
  }],
  etlJobs: [{
    jobId: String,
    name: String,
    source: String,
    destination: String,
    schedule: String,
    lastRun: Date,
    status: { type: String, enum: ['idle', 'running', 'completed', 'failed'] },
    recordsProcessed: Number
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

// Data Mart Schema
const DataMartSchema = new mongoose.Schema({
  martId: {
    type: String,
    required: true,
    unique: true
  },
  name: String,
  type: {
    type: String,
    enum: ['sales', 'operations', 'finance', 'customer', 'inventory']
  },
  dimensions: [{
    name: String,
    type: String,
    attributes: [String]
  }],
  facts: [{
    name: String,
    measures: [{
      name: String,
      aggregation: { type: String, enum: ['sum', 'count', 'avg', 'min', 'max'] }
    }]
  }],
  refreshSchedule: String,
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

module.exports = {
  DataWarehouse: mongoose.model('DataWarehouse', DataWarehouseSchema),
  DataMart: mongoose.model('DataMart', DataMartSchema)
};
