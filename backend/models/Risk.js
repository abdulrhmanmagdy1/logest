//
/**
 * ============================================
 * ⚠️ Risk Management - إدارة المخاطر
 * ============================================
 */

const mongoose = require('mongoose');

const RiskSchema = new mongoose.Schema({
  riskNumber: {
    type: String,
    required: true,
    unique: true
  },
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    required: true
  },
  category: {
    type: String,
    enum: ['operational', 'financial', 'strategic', 'compliance', 'reputational', 'safety', 'environmental', 'technology', 'supply_chain'],
    required: true
  },
  source: {
    type: String,
    enum: ['internal', 'external', 'customer', 'supplier', 'regulatory', 'market', 'natural']
  },
  likelihood: {
    type: String,
    enum: ['rare', 'unlikely', 'possible', 'likely', 'almost_certain'],
    required: true
  },
  impact: {
    type: String,
    enum: ['insignificant', 'minor', 'moderate', 'major', 'catastrophic'],
    required: true
  },
  riskLevel: {
    type: String,
    enum: ['low', 'medium', 'high', 'extreme']
  },
  inherentRisk: {
    likelihood: String,
    impact: String,
    score: Number
  },
  controls: [{
    description: String,
    type: {
      type: String,
      enum: ['preventive', 'detective', 'corrective']
    },
    effectiveness: {
      type: String,
      enum: ['ineffective', 'partially_effective', 'effective', 'highly_effective']
    },
    owner: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    implementationDate: Date,
    reviewDate: Date
  }],
  residualRisk: {
    likelihood: String,
    impact: String,
    score: Number
  },
  treatment: {
    strategy: {
      type: String,
      enum: ['accept', 'mitigate', 'transfer', 'avoid'],
      default: 'mitigate'
    },
    actionPlan: String,
    budget: Number,
    deadline: Date,
    owner: { type: mongoose.Schema.Types.ObjectId, ref: 'User' }
  },
  status: {
    type: String,
    enum: ['identified', 'assessed', 'treatment_planned', 'under_treatment', 'monitored', 'closed'],
    default: 'identified'
  },
  relatedIncidents: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'QualityIncident'
  }],
  monitoring: {
    frequency: {
      type: String,
      enum: ['daily', 'weekly', 'monthly', 'quarterly', 'annually']
    },
    nextReview: Date,
    lastReview: Date,
    trends: [{
      date: Date,
      riskLevel: String,
      notes: String
    }]
  },
  keyRiskIndicators: [{
    name: String,
    threshold: Number,
    currentValue: Number,
    status: {
      type: String,
      enum: ['green', 'amber', 'red']
    }
  }],
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

RiskSchema.index({ company: 1, status: 1 });
RiskSchema.index({ riskLevel: 1 });
RiskSchema.index({ category: 1 });

RiskSchema.pre('save', function(next) {
  // Calculate risk level based on likelihood and impact
  const likelihoodScores = { rare: 1, unlikely: 2, possible: 3, likely: 4, almost_certain: 5 };
  const impactScores = { insignificant: 1, minor: 2, moderate: 3, major: 4, catastrophic: 5 };
  
  const score = likelihoodScores[this.likelihood] * impactScores[this.impact];
  
  if (score <= 4) this.riskLevel = 'low';
  else if (score <= 9) this.riskLevel = 'medium';
  else if (score <= 14) this.riskLevel = 'high';
  else this.riskLevel = 'extreme';
  
  next();
});

module.exports.Risk = mongoose.model('Risk', RiskSchema);

// Risk Assessment Schema
const RiskAssessmentSchema = new mongoose.Schema({
  assessmentNumber: {
    type: String,
    required: true,
    unique: true
  },
  title: String,
  type: {
    type: String,
    enum: ['annual', 'quarterly', 'project', 'incident_triggered', 'change_triggered'],
    required: true
  },
  scope: String,
  methodology: String,
  assessors: [{
    user: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    role: String
  }],
  startDate: Date,
  endDate: Date,
  risksIdentified: [{
    risk: { type: mongoose.Schema.Types.ObjectId, ref: 'Risk' },
    identifiedAt: Date,
    notes: String
  }],
  summary: {
    totalRisks: Number,
    lowRisks: Number,
    mediumRisks: Number,
    highRisks: Number,
    extremeRisks: Number,
    topRisks: [{
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Risk'
    }]
  },
  report: {
    executiveSummary: String,
    recommendations: [String],
    actionPlan: String
  },
  status: {
    type: String,
    enum: ['planned', 'in_progress', 'completed', 'approved'],
    default: 'planned'
  },
  approvedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  approvedAt: Date,
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

module.exports.RiskAssessment = mongoose.model('RiskAssessment', RiskAssessmentSchema);

// Business Continuity Plan Schema
const BCPSchema = new mongoose.Schema({
  planNumber: {
    type: String,
    required: true,
    unique: true
  },
  title: {
    type: String,
    required: true
  },
  version: String,
  scope: String,
  criticalProcesses: [{
    name: String,
    rto: Number, // Recovery Time Objective (hours)
    rpo: Number, // Recovery Point Objective (hours)
    priority: Number,
    dependencies: [String],
    alternateProcedures: String
  }],
  impactScenarios: [{
    scenario: String,
    description: String,
    probability: String,
    impact: String,
    response: String
  }],
  responseTeam: [{
    role: String,
    responsibility: String,
    primaryContact: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    backupContact: { type: mongoose.Schema.Types.ObjectId, ref: 'User' }
  }],
  communicationPlan: {
    internal: [String],
    external: [String],
    templates: [String]
  },
  recoveryProcedures: [{
    step: Number,
    description: String,
    responsible: String,
    estimatedTime: Number
  }],
  testing: {
    lastTestDate: Date,
    testResults: String,
    nextTestDate: Date,
    testFrequency: String
  },
  status: {
    type: String,
    enum: ['draft', 'approved', 'active', 'under_review', 'obsolete'],
    default: 'draft'
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

module.exports.BCP = mongoose.model('BCP', BCPSchema);
