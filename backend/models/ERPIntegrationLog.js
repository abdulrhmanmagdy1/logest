const mongoose = require('mongoose');

const ERPIntegrationLogSchema = new mongoose.Schema(
  {
    company: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Company',
      required: true,
    },
    entityType: {
      type: String,
      trim: true,
    },
    entityId: {
      type: mongoose.Schema.Types.ObjectId,
    },
    operation: {
      type: String,
      enum: ['create', 'update', 'delete', 'sync'],
      default: 'sync',
    },
    status: {
      type: String,
      enum: ['success', 'failed', 'pending'],
      default: 'pending',
    },
    erpId: {
      type: String,
      trim: true,
    },
    error: {
      type: String,
      trim: true,
    },
    retryCount: {
      type: Number,
      default: 0,
    },
    timestamp: {
      type: Date,
      default: Date.now,
    },
  },
  {
    timestamps: true,
  }
);

ERPIntegrationLogSchema.index({ company: 1, timestamp: -1 });
ERPIntegrationLogSchema.index({ company: 1, status: 1, timestamp: -1 });
ERPIntegrationLogSchema.index({ entityType: 1, entityId: 1 });

module.exports = mongoose.model('ERPIntegrationLog', ERPIntegrationLogSchema);
