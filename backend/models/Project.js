//
/**
 * ============================================
 * 📊 Project Management - إدارة المشاريع
 * ============================================
 */

const mongoose = require('mongoose');

const ProjectSchema = new mongoose.Schema({
  projectNumber: {
    type: String,
    required: true,
    unique: true
  },
  name: {
    type: String,
    required: true
  },
  description: String,
  type: {
    type: String,
    enum: ['warehouse_setup', 'fleet_expansion', 'system_implementation', 'process_improvement', 'facility_upgrade', 'compliance', 'other'],
    required: true
  },
  priority: {
    type: String,
    enum: ['low', 'medium', 'high', 'critical'],
    default: 'medium'
  },
  status: {
    type: String,
    enum: ['initiation', 'planning', 'execution', 'monitoring', 'closing', 'on_hold', 'cancelled'],
    default: 'initiation'
  },
  health: {
    type: String,
    enum: ['green', 'amber', 'red'],
    default: 'green'
  },
  dates: {
    startDate: Date,
    targetEndDate: Date,
    actualEndDate: Date,
    baseline: {
      startDate: Date,
      endDate: Date
    }
  },
  budget: {
    allocated: Number,
    spent: { type: Number, default: 0 },
    remaining: Number,
    currency: { type: String, default: 'SAR' }
  },
  scope: {
    objectives: [String],
    deliverables: [String],
    inScope: [String],
    outOfScope: [String],
    assumptions: [String],
    constraints: [String]
  },
  stakeholders: [{
    user: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    role: {
      type: String,
      enum: ['sponsor', 'project_manager', 'team_member', 'consultant', 'vendor', 'client']
    },
    responsibility: String,
    authority: String
  }],
  team: {
    projectManager: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    members: [{
      user: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
      role: String,
      allocation: Number, // percentage
      startDate: Date,
      endDate: Date
    }]
  },
  tasks: [{
    taskId: String,
    name: String,
    description: String,
    type: {
      type: String,
      enum: ['milestone', 'summary', 'task', 'subtask']
    },
    phase: String,
    assignee: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    planned: {
      startDate: Date,
      endDate: Date,
      duration: Number,
      effort: Number
    },
    actual: {
      startDate: Date,
      endDate: Date,
      duration: Number,
      effort: Number
    },
    dependencies: [String],
    predecessors: [String],
    successors: [String],
    status: {
      type: String,
      enum: ['not_started', 'in_progress', 'completed', 'delayed', 'blocked'],
      default: 'not_started'
    },
    completion: {
      type: Number,
      default: 0,
      min: 0,
      max: 100
    },
    priority: {
      type: String,
      enum: ['low', 'medium', 'high', 'critical']
    },
    deliverables: [String],
    risks: [String],
    issues: [String],
    notes: String
  }],
  milestones: [{
    name: String,
    description: String,
    plannedDate: Date,
    actualDate: Date,
    status: {
      type: String,
      enum: ['planned', 'achieved', 'missed', 'at_risk'],
      default: 'planned'
    },
    deliverables: [String],
    dependencies: [String]
  }],
  risks: [{
    risk: { type: mongoose.Schema.Types.ObjectId, ref: 'Risk' },
    probability: String,
    impact: String,
    mitigation: String,
    status: String
  }],
  issues: [{
    issueNumber: String,
    title: String,
    description: String,
    severity: {
      type: String,
      enum: ['low', 'medium', 'high', 'critical']
    },
    raisedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    raisedAt: Date,
    assignedTo: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    resolution: String,
    resolvedAt: Date,
    status: {
      type: String,
      enum: ['open', 'in_progress', 'resolved', 'closed'],
      default: 'open'
    }
  }],
  changeRequests: [{
    crNumber: String,
    title: String,
    description: String,
    type: {
      type: String,
      enum: ['scope', 'schedule', 'budget', 'quality', 'resource', 'risk']
    },
    impact: {
      scope: String,
      schedule: String,
      budget: String,
      quality: String
    },
    requestedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    requestedAt: Date,
    approvedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    approvedAt: Date,
    status: {
      type: String,
      enum: ['submitted', 'under_review', 'approved', 'rejected', 'implemented'],
      default: 'submitted'
    }
  }],
  documents: [{
    name: String,
    type: {
      type: String,
      enum: ['charter', 'plan', 'requirement', 'design', 'report', 'other']
    },
    url: String,
    version: String,
    uploadedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    uploadedAt: Date
  }],
  meetings: [{
    type: {
      type: String,
      enum: ['kickoff', 'status', 'review', 'steering', 'risk', 'other']
    },
    date: Date,
    attendees: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
    agenda: String,
    minutes: String,
    actionItems: [{
      description: String,
      assignedTo: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
      dueDate: Date,
      status: String
    }],
    attachments: [String]
  }],
  progress: {
    overallCompletion: { type: Number, default: 0 },
    scheduleVariance: Number,
    costVariance: Number,
    spi: Number, // Schedule Performance Index
    cpi: Number, // Cost Performance Index
    earnedValue: Number,
    plannedValue: Number,
    actualCost: Number
  },
  quality: {
    checklists: [{
      name: String,
      items: [{
        description: String,
        completed: Boolean,
        verifiedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
        verifiedAt: Date
      }]
    }],
    reviews: [{
      type: String,
      date: Date,
      findings: String,
      recommendations: [String]
    }]
  },
  lessonsLearned: [{
    category: {
      type: String,
      enum: ['technical', 'process', 'communication', 'management', 'other']
    },
    whatWentWell: String,
    whatWentWrong: String,
    recommendations: String,
    recordedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    recordedAt: Date
  }],
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

ProjectSchema.index({ company: 1, status: 1 });
ProjectSchema.index({ 'team.projectManager': 1 });
ProjectSchema.index({ priority: 1, health: 1 });

// Calculate overall completion
ProjectSchema.methods.calculateProgress = function() {
  if (!this.tasks || this.tasks.length === 0) return 0;
  
  const totalWeight = this.tasks.length;
  const completedWeight = this.tasks.reduce((sum, task) => {
    return sum + (task.completion / 100);
  }, 0);
  
  return Math.round((completedWeight / totalWeight) * 100);
};

// Check if project is on track
ProjectSchema.methods.checkHealth = function() {
  const delayedTasks = this.tasks.filter(t => t.status === 'delayed').length;
  const totalTasks = this.tasks.length;
  
  if (totalTasks === 0) return 'green';
  
  const delayPercentage = (delayedTasks / totalTasks) * 100;
  
  if (delayPercentage > 30) return 'red';
  if (delayPercentage > 10) return 'amber';
  return 'green';
};

module.exports.Project = mongoose.model('Project', ProjectSchema);

// Timesheet Schema
const TimesheetSchema = new mongoose.Schema({
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  week: {
    startDate: Date,
    endDate: Date
  },
  entries: [{
    date: Date,
    project: { type: mongoose.Schema.Types.ObjectId, ref: 'Project' },
    task: String,
    hours: Number,
    description: String,
    category: {
      type: String,
      enum: ['regular', 'overtime', 'weekend', 'holiday']
    },
    billable: { type: Boolean, default: true }
  }],
  status: {
    type: String,
    enum: ['draft', 'submitted', 'approved', 'rejected'],
    default: 'draft'
  },
  totalHours: {
    regular: Number,
    overtime: Number,
    weekend: Number,
    total: Number
  },
  submittedAt: Date,
  approvedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
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

module.exports.Timesheet = mongoose.model('Timesheet', TimesheetSchema);
