//
/**
 * ============================================
 * ✅ Task Management - إدارة المهام والمشاريع
 * ============================================
 */

const mongoose = require('mongoose');

// Task Schema
const TaskSchema = new mongoose.Schema({
  taskId: {
    type: String,
    required: true,
    unique: true
  },
  title: {
    type: String,
    required: true
  },
  description: String,
  type: {
    type: String,
    enum: ['general', 'shipment', 'maintenance', 'delivery', 'customer_service', 'administrative', 'development', 'urgent'],
    default: 'general'
  },
  status: {
    type: String,
    enum: ['backlog', 'todo', 'in_progress', 'review', 'completed', 'cancelled', 'blocked'],
    default: 'todo'
  },
  priority: {
    type: String,
    enum: ['low', 'medium', 'high', 'urgent', 'critical'],
    default: 'medium'
  },
  assignedTo: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  assignedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  reporter: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  dueDate: Date,
  estimatedHours: Number,
  actualHours: Number,
  startDate: Date,
  completedAt: Date,
  relatedEntity: {
    type: { type: String, enum: ['shipment', 'customer', 'vehicle', 'driver', 'invoice'] },
    id: mongoose.Schema.Types.ObjectId,
    reference: String
  },
  checklist: [{
    item: String,
    completed: { type: Boolean, default: false },
    completedAt: Date,
    completedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' }
  }],
  attachments: [{
    name: String,
    url: String,
    type: String,
    size: Number,
    uploadedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    uploadedAt: Date
  }],
  tags: [String],
  comments: [{
    user: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    text: String,
    attachments: [String],
    createdAt: { type: Date, default: Date.now }
  }],
  history: [{
    field: String,
    oldValue: mongoose.Schema.Types.Mixed,
    newValue: mongoose.Schema.Types.Mixed,
    changedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    changedAt: { type: Date, default: Date.now }
  }],
  watchers: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  }],
  reminders: [{
    type: { type: String, enum: ['email', 'push', 'sms'] },
    before: Number, // minutes before due date
    sent: { type: Boolean, default: false }
  }],
  parent: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Task'
  },
  subtasks: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Task'
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

TaskSchema.index({ company: 1, status: 1, assignedTo: 1 });
TaskSchema.index({ company: 1, priority: 1, dueDate: 1 });

// Project Schema
const ProjectSchema = new mongoose.Schema({
  projectId: {
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
    enum: ['operational', 'strategic', 'technical', 'compliance', 'internal'],
    default: 'operational'
  },
  status: {
    type: String,
    enum: ['planning', 'active', 'on_hold', 'completed', 'cancelled'],
    default: 'planning'
  },
  priority: {
    type: String,
    enum: ['low', 'medium', 'high'],
    default: 'medium'
  },
  owner: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  members: [{
    user: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    role: { type: String, enum: ['owner', 'manager', 'member', 'viewer'] },
    joinedAt: Date
  }],
  startDate: Date,
  targetEndDate: Date,
  actualEndDate: Date,
  budget: {
    allocated: Number,
    spent: { type: Number, default: 0 },
    currency: { type: String, default: 'SAR' }
  },
  milestones: [{
    name: String,
    description: String,
    targetDate: Date,
    completed: { type: Boolean, default: false },
    completedAt: Date
  }],
  progress: {
    type: Number,
    default: 0,
    min: 0,
    max: 100
  },
  tasks: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Task'
  }],
  tags: [String],
  color: { type: String, default: '#1a73e8' },
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

// Time Tracking Schema
const TimeEntrySchema = new mongoose.Schema({
  entryId: {
    type: String,
    required: true,
    unique: true
  },
  task: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Task',
    required: true
  },
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  description: String,
  startTime: {
    type: Date,
    required: true
  },
  endTime: Date,
  duration: Number, // minutes
  billable: {
    type: Boolean,
    default: true
  },
  hourlyRate: Number,
  cost: Number,
  source: {
    type: String,
    enum: ['manual', 'timer', 'auto']
  },
  approved: {
    by: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    at: Date,
    status: { type: String, enum: ['pending', 'approved', 'rejected'], default: 'pending' }
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
  Task: mongoose.model('Task', TaskSchema),
  Project: mongoose.model('Project', ProjectSchema),
  TimeEntry: mongoose.model('TimeEntry', TimeEntrySchema)
};
