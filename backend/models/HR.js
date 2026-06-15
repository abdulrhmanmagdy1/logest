//
/**
 * ============================================
 * 👔 HR & Payroll Models - الموارد البشرية والرواتب
 * ============================================
 */

const mongoose = require('mongoose');

// Employee Schema (Extended User)
const EmployeeSchema = new mongoose.Schema({
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  employeeId: {
    type: String,
    required: true,
    unique: true
  },
  department: {
    type: String,
    enum: ['operations', 'sales', 'finance', 'hr', 'it', 'warehouse', 'driver', 'maintenance'],
    required: true
  },
  position: {
    type: String,
    required: true
  },
  employmentType: {
    type: String,
    enum: ['full_time', 'part_time', 'contract', 'intern', 'seasonal'],
    default: 'full_time'
  },
  hireDate: {
    type: Date,
    required: true
  },
  terminationDate: Date,
  status: {
    type: String,
    enum: ['active', 'on_leave', 'suspended', 'terminated'],
    default: 'active'
  },
  reportingTo: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Employee'
  },
  salary: {
    basic: { type: Number, required: true },
    housing: Number,
    transportation: Number,
    otherAllowances: Number,
    currency: { type: String, default: 'SAR' }
  },
  bankAccount: {
    bankName: String,
    accountNumber: String,
    iban: String
  },
  documents: {
    contract: String,
    idCopy: String,
    passport: String,
    visa: String,
    medicalInsurance: String,
    drivingLicense: String
  },
  leaveBalance: {
    annual: { type: Number, default: 21 },
    sick: { type: Number, default: 30 },
    unpaid: { type: Number, default: 0 },
    used: {
      annual: { type: Number, default: 0 },
      sick: { type: Number, default: 0 },
      unpaid: { type: Number, default: 0 }
    }
  },
  attendance: [{
    date: Date,
    checkIn: Date,
    checkOut: Date,
    status: {
      type: String,
      enum: ['present', 'absent', 'late', 'early_leave', 'on_leave', 'holiday']
    },
    overtime: Number, // in hours
    notes: String
  }],
  performanceReviews: [{
    period: String,
    reviewer: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Employee'
    },
    ratings: {
      jobKnowledge: { type: Number, min: 1, max: 5 },
      productivity: { type: Number, min: 1, max: 5 },
      quality: { type: Number, min: 1, max: 5 },
      attendance: { type: Number, min: 1, max: 5 },
      teamwork: { type: Number, min: 1, max: 5 }
    },
    overallRating: Number,
    comments: String,
    goals: [String],
    createdAt: { type: Date, default: Date.now }
  }],
  createdAt: {
    type: Date,
    default: Date.now
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
});

EmployeeSchema.index({ department: 1, status: 1 });
EmployeeSchema.index({ employeeId: 1 });

// Payroll Schema
const PayrollSchema = new mongoose.Schema({
  employee: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Employee',
    required: true
  },
  period: {
    month: { type: Number, required: true },
    year: { type: Number, required: true }
  },
  earnings: {
    basic: Number,
    housing: Number,
    transportation: Number,
    otherAllowances: Number,
    overtime: Number,
    bonus: Number,
    totalEarnings: Number
  },
  deductions: {
    gosi: Number, // General Organization for Social Insurance (Saudi)
    incomeTax: Number,
    advance: Number,
    other: Number,
    totalDeductions: Number
  },
  netSalary: Number,
  attendance: {
    workingDays: Number,
    presentDays: Number,
    absentDays: Number,
    leaveDays: Number,
    overtimeHours: Number
  },
  status: {
    type: String,
    enum: ['draft', 'calculated', 'approved', 'paid'],
    default: 'draft'
  },
  payment: {
    method: {
      type: String,
      enum: ['bank_transfer', 'cash', 'check']
    },
    date: Date,
    reference: String
  },
  notes: String,
  createdBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  approvedBy: {
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

PayrollSchema.index({ employee: 1, 'period.month': 1, 'period.year': 1 });

// Leave Request Schema
const LeaveRequestSchema = new mongoose.Schema({
  employee: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Employee',
    required: true
  },
  type: {
    type: String,
    enum: ['annual', 'sick', 'emergency', 'unpaid', 'maternity', 'paternity', 'hajj', 'training'],
    required: true
  },
  startDate: {
    type: Date,
    required: true
  },
  endDate: {
    type: Date,
    required: true
  },
  days: {
    type: Number,
    required: true
  },
  reason: String,
  attachments: [String],
  status: {
    type: String,
    enum: ['pending', 'approved', 'rejected', 'cancelled'],
    default: 'pending'
  },
  approvedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Employee'
  },
  approvedAt: Date,
  rejectionReason: String,
  createdAt: {
    type: Date,
    default: Date.now
  }
});

LeaveRequestSchema.index({ employee: 1, status: 1 });
LeaveRequestSchema.index({ startDate: 1, endDate: 1 });

module.exports = {
  Employee: mongoose.model('Employee', EmployeeSchema),
  Payroll: mongoose.model('Payroll', PayrollSchema),
  LeaveRequest: mongoose.model('LeaveRequest', LeaveRequestSchema)
};
