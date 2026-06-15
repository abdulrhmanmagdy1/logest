//
/**
 * ============================================
 * 📋 Audit Log Model - نظام السجلات والتدقيق
 * ============================================
 */

const mongoose = require('mongoose');

const AuditLogSchema = new mongoose.Schema({
  // Action Info
  action: {
    type: String,
    required: true,
    enum: [
      'CREATE',
      'READ',
      'UPDATE',
      'DELETE',
      'LOGIN',
      'LOGOUT',
      'EXPORT',
      'IMPORT',
      'APPROVE',
      'REJECT',
      'ASSIGN',
      'COMPLETE',
      'CANCEL',
      'DOWNLOAD',
      'SHARE',
      'PRINT'
    ]
  },
  
  entity: {
    type: String,
    required: true,
    enum: [
      'User',
      'Shipment',
      'Truck',
      'Invoice',
      'Driver',
      'Client',
      'Maintenance',
      'Document',
      'Chat',
      'Ticket',
      'FuelRecord',
      'SensorData',
      'Review',
      'Payment',
      'Report'
    ]
  },
  
  entityId: {
    type: mongoose.Schema.Types.ObjectId
  },
  
  // User Info
  user: {
    id: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    email: String,
    role: String,
    ip: String,
    userAgent: String
  },
  
  // Request Details
  request: {
    method: String,
    url: String,
    params: mongoose.Schema.Types.Mixed,
    query: mongoose.Schema.Types.Mixed,
    body: mongoose.Schema.Types.Mixed
  },
  
  // Data Changes (for UPDATE actions)
  changes: {
    before: mongoose.Schema.Types.Mixed,
    after: mongoose.Schema.Types.Mixed,
    fields: [String]
  },
  
  // Result
  result: {
    success: {
      type: Boolean,
      default: true
    },
    statusCode: Number,
    errorMessage: String,
    errorCode: String
  },
  
  // Performance
  performance: {
    duration: Number, // milliseconds
    memory: Number, // bytes
    timestamp: Date
  },
  
  // Metadata
  sessionId: String,
  correlationId: String,
  
  createdAt: {
    type: Date,
    default: Date.now
  }
});

// Indexes for fast querying
AuditLogSchema.index({ createdAt: -1 });
AuditLogSchema.index({ 'user.id': 1, createdAt: -1 });
AuditLogSchema.index({ entity: 1, entityId: 1 });
AuditLogSchema.index({ action: 1, createdAt: -1 });
AuditLogSchema.index({ 'result.success': 1 });

// Static: Log an action
AuditLogSchema.statics.log = async function(data) {
  try {
    const log = new this(data);
    await log.save();
    return log;
  } catch (error) {
    console.error('Audit log error:', error);
    // Don't throw - audit logging should not break the application
  }
};

// Static: Get user activity
AuditLogSchema.statics.getUserActivity = async function(userId, options = {}) {
  const { startDate, endDate, limit = 50 } = options;
  
  let query = { 'user.id': userId };
  
  if (startDate && endDate) {
    query.createdAt = { $gte: startDate, $lte: endDate };
  }
  
  return await this.find(query)
    .sort({ createdAt: -1 })
    .limit(limit);
};

// Static: Get entity history
AuditLogSchema.statics.getEntityHistory = async function(entity, entityId) {
  return await this.find({ entity, entityId })
    .sort({ createdAt: -1 });
};

// Static: Get security report
AuditLogSchema.statics.getSecurityReport = async function(startDate, endDate) {
  return await this.aggregate([
    {
      $match: {
        createdAt: { $gte: startDate, $lte: endDate }
      }
    },
    {
      $group: {
        _id: '$action',
        count: { $sum: 1 },
        success: { $sum: { $cond: ['$result.success', 1, 0] } },
        failed: { $sum: { $cond: ['$result.success', 0, 1] } }
      }
    }
  ]);
};

module.exports = mongoose.model('AuditLog', AuditLogSchema);
