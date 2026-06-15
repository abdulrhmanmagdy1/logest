//
/**
 * ============================================
 * 🏢 Multi-Tenancy Company Management - إدارة الشركات
 * ============================================
 */

const mongoose = require('mongoose');

// Company Schema
const CompanySchema = new mongoose.Schema({
  name: {
    type: String,
    required: true,
    trim: true
  },
  legalName: String,
  code: {
    type: String,
    required: true,
    unique: true,
    uppercase: true
  },
  type: {
    type: String,
    enum: ['main', 'subsidiary', 'branch', 'franchise', 'partner'],
    default: 'main'
  },
  parentCompany: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company'
  },
  branding: {
    logo: String,
    favicon: String,
    primaryColor: { type: String, default: '#1a73e8' },
    secondaryColor: { type: String, default: '#34a853' },
    customDomain: String,
    emailTemplates: {
      header: String,
      footer: String,
      colors: {
        background: String,
        text: String,
        button: String
      }
    }
  },
  contact: {
    email: { type: String, required: true },
    phone: String,
    website: String,
    supportEmail: String,
    supportPhone: String
  },
  address: {
    street: String,
    city: String,
    state: String,
    postalCode: String,
    country: { type: String, default: 'SA' },
    coordinates: {
      lat: Number,
      lng: Number
    }
  },
  registration: {
    crNumber: String, // Commercial Registration
    vatNumber: String,
    taxId: String,
    industry: String,
    foundedDate: Date,
    employees: Number
  },
  billing: {
    address: {
      street: String,
      city: String,
      state: String,
      postalCode: String,
      country: String
    },
    currency: { type: String, default: 'SAR' },
    timezone: { type: String, default: 'Asia/Riyadh' },
    taxRate: { type: Number, default: 15 }
  },
  settings: {
    language: { type: String, default: 'ar' },
    dateFormat: { type: String, default: 'DD/MM/YYYY' },
    timeFormat: { type: String, default: '24h' },
    measurementUnit: { type: String, default: 'metric' },
    distanceUnit: { type: String, default: 'km' },
    weightUnit: { type: String, default: 'kg' },
    volumeUnit: { type: String, default: 'cbm' }
  },
  modules: {
    shipments: { enabled: { type: Boolean, default: true }, plan: String },
    warehouse: { enabled: { type: Boolean, default: false }, plan: String },
    crm: { enabled: { type: Boolean, default: false }, plan: String },
    hr: { enabled: { type: Boolean, default: false }, plan: String },
    finance: { enabled: { type: Boolean, default: true }, plan: String },
    procurement: { enabled: { type: Boolean, default: false }, plan: String },
    fleet: { enabled: { type: Boolean, default: true }, plan: String },
    projects: { enabled: { type: Boolean, default: false }, plan: String },
    quality: { enabled: { type: Boolean, default: false }, plan: String },
    marketing: { enabled: { type: Boolean, default: false }, plan: String },
    analytics: { enabled: { type: Boolean, default: true }, plan: String }
  },
  limits: {
    maxUsers: { type: Number, default: 10 },
    maxDrivers: { type: Number, default: 20 },
    maxVehicles: { type: Number, default: 20 },
    maxWarehouses: { type: Number, default: 3 },
    storageGB: { type: Number, default: 10 },
    apiCallsPerMonth: { type: Number, default: 10000 }
  },
  subscription: {
    plan: {
      type: String,
      enum: ['trial', 'basic', 'professional', 'enterprise', 'custom'],
      default: 'trial'
    },
    status: {
      type: String,
      enum: ['active', 'suspended', 'cancelled', 'expired'],
      default: 'active'
    },
    startDate: Date,
    endDate: Date,
    autoRenew: { type: Boolean, default: true },
    paymentMethod: String,
    billingCycle: {
      type: String,
      enum: ['monthly', 'quarterly', 'annually'],
      default: 'monthly'
    },
    amount: Number,
    currency: { type: String, default: 'SAR' }
  },
  usage: {
    currentUsers: { type: Number, default: 0 },
    currentDrivers: { type: Number, default: 0 },
    currentVehicles: { type: Number, default: 0 },
    currentWarehouses: { type: Number, default: 0 },
    storageUsedGB: { type: Number, default: 0 },
    apiCallsThisMonth: { type: Number, default: 0 },
    lastResetDate: { type: Date, default: Date.now }
  },
  integrations: [{
    name: String,
    type: String,
    config: mongoose.Schema.Types.Mixed,
    status: {
      type: String,
      enum: ['active', 'inactive', 'error'],
      default: 'active'
    },
    connectedAt: Date
  }],
  security: {
    ipWhitelist: [String],
    require2FA: { type: Boolean, default: false },
    passwordPolicy: {
      minLength: { type: Number, default: 8 },
      requireUppercase: { type: Boolean, default: true },
      requireNumbers: { type: Boolean, default: true },
      requireSymbols: { type: Boolean, default: false },
      expiryDays: { type: Number, default: 90 }
    },
    sessionTimeout: { type: Number, default: 30 },
    allowedDomains: [String]
  },
  features: {
    customFields: [{
      name: String,
      type: { type: String, enum: ['text', 'number', 'date', 'boolean', 'select'] },
      options: [String],
      required: Boolean,
      entity: { type: String, enum: ['shipment', 'driver', 'vehicle', 'customer', 'invoice'] }
    }],
    workflows: [{
      name: String,
      trigger: String,
      conditions: [mongoose.Schema.Types.Mixed],
      actions: [mongoose.Schema.Types.Mixed],
      active: { type: Boolean, default: true }
    }]
  },
  status: {
    type: String,
    enum: ['active', 'suspended', 'inactive', 'pending_verification'],
    default: 'pending_verification'
  },
  verified: {
    type: Boolean,
    default: false
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

CompanySchema.index({ code: 1 });
CompanySchema.index({ status: 1 });
CompanySchema.index({ 'subscription.status': 1 });

// Branch Schema
const BranchSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true
  },
  code: {
    type: String,
    required: true
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  type: {
    type: String,
    enum: ['headquarters', 'regional', 'branch', 'warehouse', 'depot'],
    default: 'branch'
  },
  address: {
    street: String,
    city: { type: String, required: true },
    state: String,
    postalCode: String,
    country: { type: String, default: 'SA' },
    coordinates: {
      lat: Number,
      lng: Number
    }
  },
  contact: {
    manager: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    phone: String,
    email: String
  },
  operatingHours: {
    monday: { open: String, close: String, closed: Boolean },
    tuesday: { open: String, close: String, closed: Boolean },
    wednesday: { open: String, close: String, closed: Boolean },
    thursday: { open: String, close: String, closed: Boolean },
    friday: { open: String, close: String, closed: Boolean },
    saturday: { open: String, close: String, closed: Boolean },
    sunday: { open: String, close: String, closed: Boolean }
  },
  capacity: {
    vehicles: Number,
    drivers: Number,
    staff: Number
  },
  services: [String],
  isActive: {
    type: Boolean,
    default: true
  },
  createdAt: {
    type: Date,
    default: Date.now
  }
});

BranchSchema.index({ company: 1, code: 1 });

// Department Schema
const DepartmentSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true
  },
  code: {
    type: String,
    required: true
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  branch: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Branch'
  },
  manager: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  description: String,
  budget: {
    annual: Number,
    spent: { type: Number, default: 0 },
    currency: { type: String, default: 'SAR' }
  },
  isActive: {
    type: Boolean,
    default: true
  }
});

DepartmentSchema.index({ company: 1, code: 1 });

module.exports = {
  Company: mongoose.model('Company', CompanySchema),
  Branch: mongoose.model('Branch', BranchSchema),
  Department: mongoose.model('Department', DepartmentSchema)
};
