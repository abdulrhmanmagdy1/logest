//
/**
 * ============================================
 * 🏢 Branch Model - نظام إدارة الفروع
 * ============================================
 */

const mongoose = require('mongoose');

const BranchSchema = new mongoose.Schema({
  // Basic Info
  name: {
    type: String,
    required: true
  },
  
  code: {
    type: String,
    required: true,
    unique: true
  },
  
  type: {
    type: String,
    enum: ['headquarters', 'branch', 'warehouse', 'hub', 'station'],
    default: 'branch'
  },
  
  // Location
  location: {
    address: {
      street: String,
      city: { type: String, required: true },
      region: String,
      postalCode: String,
      country: { type: String, default: 'SA' }
    },
    coordinates: {
      lat: { type: Number, required: true },
      lng: { type: Number, required: true }
    },
    timezone: {
      type: String,
      default: 'Asia/Riyadh'
    }
  },
  
  // Contact
  contact: {
    phone: String,
    email: String,
    managerName: String,
    managerPhone: String
  },
  
  // Operating Hours
  operatingHours: {
    monday: { open: String, close: String, isOpen: Boolean },
    tuesday: { open: String, close: String, isOpen: Boolean },
    wednesday: { open: String, close: String, isOpen: Boolean },
    thursday: { open: String, close: String, isOpen: Boolean },
    friday: { open: String, close: String, isOpen: Boolean },
    saturday: { open: String, close: String, isOpen: Boolean },
    sunday: { open: String, close: String, isOpen: Boolean }
  },
  
  // Capacity
  capacity: {
    trucks: { type: Number, default: 0 },
    drivers: { type: Number, default: 0 },
    storageVolume: { value: Number, unit: String }, // m³
    parkingSpaces: Number
  },
  
  // Services Offered
  services: [{
    type: String,
    enum: [
      'pickup',
      'delivery',
      'storage',
      'cross_docking',
      'packaging',
      'cold_storage',
      'maintenance'
    ]
  }],
  
  // Status
  status: {
    type: String,
    enum: ['active', 'inactive', 'maintenance', 'closed'],
    default: 'active'
  },
  
  // Settings
  settings: {
    currency: { type: String, default: 'SAR' },
    language: { type: String, default: 'ar' },
    dateFormat: { type: String, default: 'DD/MM/YYYY' },
    autoAssignDrivers: { type: Boolean, default: false },
    requireApproval: { type: Boolean, default: true }
  },
  
  // Hierarchy
  parent: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Branch'
  },
  
  // Regional Settings
  region: {
    name: String,
    manager: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    }
  },
  
  // Financial
  financial: {
    budget: Number,
    costCenter: String,
    accountingCode: String
  },
  
  // Metadata
  createdAt: {
    type: Date,
    default: Date.now
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
});

// Indexes
BranchSchema.index({ code: 1 });
BranchSchema.index({ 'location.address.city': 1 });
BranchSchema.index({ status: 1 });
BranchSchema.index({ parent: 1 });

// Pre-save
BranchSchema.pre('save', function(next) {
  this.updatedAt = Date.now();
  next();
});

// Methods
BranchSchema.methods.getFullAddress = function() {
  const { street, city, region, postalCode } = this.location.address;
  return `${street}, ${city}, ${region} ${postalCode}`;
};

BranchSchema.methods.isOpen = function(date = new Date()) {
  const days = ['sunday', 'monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday'];
  const day = days[date.getDay()];
  const hours = this.operatingHours[day];
  
  if (!hours || !hours.isOpen) return false;
  
  const current = date.getHours() * 60 + date.getMinutes();
  const open = parseInt(hours.open.split(':')[0]) * 60 + parseInt(hours.open.split(':')[1]);
  const close = parseInt(hours.close.split(':')[0]) * 60 + parseInt(hours.close.split(':')[1]);
  
  return current >= open && current <= close;
};

module.exports = mongoose.model('Branch', BranchSchema);
