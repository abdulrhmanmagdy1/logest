//
/**
 * ============================================
 * ✅ Quality Management - إدارة الجودة (ISO)
 * ============================================
 */

const mongoose = require('mongoose');

// Quality Incident Schema
const QualityIncidentSchema = new mongoose.Schema({
  incidentNumber: {
    type: String,
    required: true,
    unique: true
  },
  type: {
    type: String,
    enum: ['cargo_damage', 'temperature_breach', 'late_delivery', 'wrong_delivery', 'documentation_error', 'customer_complaint', 'audit_finding', 'other'],
    required: true
  },
  severity: {
    type: String,
    enum: ['low', 'medium', 'high', 'critical'],
    required: true
  },
  relatedShipment: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Shipment'
  },
  relatedCustomer: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  description: {
    type: String,
    required: true
  },
  rootCause: {
    category: {
      type: String,
      enum: ['process', 'equipment', 'human_error', 'external', 'system', 'training', 'communication']
    },
    description: String
  },
  impact: {
    financial: Number,
    operational: String,
    reputational: String,
    customerImpact: String
  },
  immediateAction: {
    taken: String,
    takenBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    takenAt: Date
  },
  correctiveActions: [{
    description: String,
    assignedTo: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    dueDate: Date,
    status: {
      type: String,
      enum: ['pending', 'in_progress', 'completed', 'overdue'],
      default: 'pending'
    },
    completedAt: Date,
    evidence: String
  }],
  preventiveActions: [{
    description: String,
    assignedTo: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    dueDate: Date,
    status: {
      type: String,
      enum: ['pending', 'in_progress', 'completed'],
      default: 'pending'
    },
    completedAt: Date
  }],
  status: {
    type: String,
    enum: ['open', 'under_investigation', 'action_required', 'resolved', 'closed'],
    default: 'open'
  },
  closedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  closedAt: Date,
  lessonsLearned: String,
  attachments: [String],
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

QualityIncidentSchema.index({ company: 1, status: 1 });
QualityIncidentSchema.index({ severity: 1 });

// Audit Schema
const AuditSchema = new mongoose.Schema({
  auditNumber: {
    type: String,
    required: true,
    unique: true
  },
  type: {
    type: String,
    enum: ['internal', 'external', 'certification', 'supplier', 'compliance', 'process'],
    required: true
  },
  standard: {
    type: String,
    enum: ['ISO9001', 'ISO14001', 'ISO45001', 'GDP', 'HACCP', 'custom'],
    required: true
  },
  title: {
    type: String,
    required: true
  },
  scope: String,
  auditor: {
    internal: { type: Boolean, default: true },
    auditorName: String,
    auditorCompany: String,
    auditorContact: String
  },
  dates: {
    planned: Date,
    actual: Date,
    completed: Date,
    nextAudit: Date
  },
  location: {
    warehouse: { type: mongoose.Schema.Types.ObjectId, ref: 'Warehouse' },
    other: String
  },
  checklist: [{
    category: String,
    requirement: String,
    evidence: String,
    finding: {
      type: String,
      enum: ['conform', 'non_conform_major', 'non_conform_minor', 'observation', 'opportunity']
    },
    notes: String
  }],
  findings: {
    conform: { type: Number, default: 0 },
    nonConformMajor: { type: Number, default: 0 },
    nonConformMinor: { type: Number, default: 0 },
    observations: { type: Number, default: 0 }
  },
  score: {
    total: Number,
    percentage: Number
  },
  status: {
    type: String,
    enum: ['planned', 'in_progress', 'completed', 'report_issued', 'closed'],
    default: 'planned'
  },
  report: {
    executiveSummary: String,
    recommendations: [String],
    reportUrl: String,
    issuedAt: Date
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
  }
});

AuditSchema.index({ company: 1, status: 1 });
AuditSchema.index({ type: 1, standard: 1 });

// KPI Schema
const KPISchema = new mongoose.Schema({
  name: {
    type: String,
    required: true
  },
  category: {
    type: String,
    enum: ['operational', 'financial', 'customer', 'quality', 'safety', 'environmental'],
    required: true
  },
  unit: String,
  target: {
    min: Number,
    max: Number,
    target: Number
  },
  frequency: {
    type: String,
    enum: ['daily', 'weekly', 'monthly', 'quarterly', 'yearly'],
    default: 'monthly'
  },
  measurements: [{
    period: String,
    value: Number,
    target: Number,
    achievement: Number,
    trend: { type: String, enum: ['up', 'down', 'stable'] },
    notes: String
  }],
  responsible: {
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

// SOP Schema (Standard Operating Procedures)
const SOPSchema = new mongoose.Schema({
  sopNumber: {
    type: String,
    required: true,
    unique: true
  },
  title: {
    type: String,
    required: true
  },
  version: {
    type: String,
    default: '1.0'
  },
  category: {
    type: String,
    enum: ['operations', 'safety', 'quality', 'maintenance', 'hr', 'finance', 'it']
  },
  department: String,
  purpose: String,
  scope: String,
  responsibility: String,
  procedure: [{
    step: Number,
    description: String,
    responsible: String,
    attachments: [String]
  }],
  relatedDocuments: [{
    name: String,
    url: String
  }],
  status: {
    type: String,
    enum: ['draft', 'under_review', 'approved', 'obsolete'],
    default: 'draft'
  },
  approvedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  approvedAt: Date,
  reviewDate: Date,
  attachments: [String],
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

module.exports = {
  QualityIncident: mongoose.model('QualityIncident', QualityIncidentSchema),
  Audit: mongoose.model('Audit', AuditSchema),
  KPI: mongoose.model('KPI', KPISchema),
  SOP: mongoose.model('SOP', SOPSchema)
};
