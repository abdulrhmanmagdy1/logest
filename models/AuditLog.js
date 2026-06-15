const mongoose = require('mongoose');

const auditLogSchema = new mongoose.Schema({
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  action: {
    type: String,
    required: true,
    enum: ['create', 'update', 'delete', 'login', 'logout', 'view', 'export', 'import']
  },
  entity: {
    type: String,
    required: true
  },
  entityId: {
    type: mongoose.Schema.Types.ObjectId
  },
  changes: {
    type: Object,
    default: {}
  },
  ipAddress: {
    type: String
  },
  userAgent: {
    type: String
  },
  status: {
    type: String,
    enum: ['success', 'failure', 'pending'],
    default: 'success'
  },
  errorMessage: {
    type: String
  },
  metadata: {
    type: Object,
    default: {}
  },
  createdAt: {
    type: Date,
    default: Date.now
  }
});

auditLogSchema.index({ user: 1, createdAt: -1 });
auditLogSchema.index({ entity: 1, createdAt: -1 });
auditLogSchema.index({ action: 1, createdAt: -1 });

module.exports = mongoose.model('AuditLog', auditLogSchema);
