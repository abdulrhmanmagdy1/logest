/**
 * ============================================
 * 🚛 Truck Model - نظام إدهام
 * Edham Logistics - Truck Schema
 * ============================================
 */

const mongoose = require('mongoose');

const truckSchema = new mongoose.Schema({
  // Truck Identification
  truckNumber: {
    type: String,
    required: true,
    unique: true,
    trim: true
  },
  plateNumber: {
    type: String,
    required: true,
    unique: true,
    trim: true
  },
  
  // Vehicle Details
  model: {
    type: String,
    required: true
  },
  make: {
    type: String,
    required: true
  },
  year: {
    type: Number,
    min: 1990,
    max: new Date().getFullYear() + 1
  },
  vin: {
    type: String,
    unique: true
  },
  
  // Capacity & Specifications
  capacity: {
    type: Number,
    min: 0
  },
  capacityUnit: {
    type: String,
    enum: ['kg', 'tons', 'cubic_meters'],
    default: 'kg'
  },
  truckType: {
    type: String,
    enum: ['refrigerated', 'dry_van', 'flatbed', 'tanker', 'box_truck', 'pickup'],
    default: 'dry_van'
  },
  fuelType: {
    type: String,
    enum: ['diesel', 'petrol', 'electric', 'hybrid'],
    default: 'diesel'
  },
  
  // Assigned Driver
  driver: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  
  // Status
  status: {
    type: String,
    enum: ['available', 'in_use', 'maintenance', 'out_of_service', 'in_transit'],
    default: 'available'
  },
  
  // Location Tracking
  currentLocation: {
    latitude: Number,
    longitude: Number,
    updatedAt: Date,
    accuracy: Number
  },
  
  // Maintenance - Oil
  lastOilChange: {
    type: Date
  },
  lastOilChangeKm: {
    type: Number,
    default: 0
  },
  nextOilChange: {
    type: Date
  },
  nextOilChangeKm: {
    type: Number
  },
  currentOdometer: {
    type: Number,
    default: 0
  },
  
  // Tires
  tires: [{
    position: {
      type: String,
      enum: ['front_left', 'front_right', 'rear_outer_left', 'rear_outer_right', 'rear_inner_left', 'rear_inner_right', 'spare']
    },
    brand: String,
    installDate: Date,
    treadDepth: {
      type: Number,
      min: 0,
      max: 20
    },
    lastRotation: Date
  }],
  
  // Refrigeration (for refrigerated trucks)
  refrigerationUnit: {
    type: String
  },
  lastRefrigerationService: {
    type: Date
  },
  refrigerationStatus: {
    type: String,
    enum: ['working', 'needs_service', 'broken'],
    default: 'working'
  },
  
  // Insurance & Registration
  insuranceExpiry: {
    type: Date
  },
  registrationExpiry: {
    type: Date
  },
  
  // Cost Tracking
  purchaseDate: {
    type: Date
  },
  purchaseCost: {
    type: Number,
    min: 0
  },
  
  // Notes
  notes: {
    type: String
  },
  
  // Soft Delete
  isDeleted: {
    type: Boolean,
    default: false
  },
  deletedAt: {
    type: Date,
    default: null
  }
}, {
  timestamps: true
});

// Indexes
truckSchema.index({ truckNumber: 1 });
truckSchema.index({ plateNumber: 1 });
truckSchema.index({ status: 1 });
truckSchema.index({ driver: 1 });
truckSchema.index({ 'currentLocation.latitude': 1, 'currentLocation.longitude': 1 });

// Method to check if maintenance is due
truckSchema.methods.isMaintenanceDue = function() {
  const now = new Date();
  const sevenDaysFromNow = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000);
  
  // Check oil change
  if (this.nextOilChange && this.nextOilChange <= sevenDaysFromNow) {
    return true;
  }
  
  // Check tires
  const wornTires = this.tires.filter(tire => tire.treadDepth < 3.0);
  if (wornTires.length > 0) {
    return true;
  }
  
  // Check insurance
  if (this.insuranceExpiry && this.insuranceExpiry <= sevenDaysFromNow) {
    return true;
  }
  
  // Check registration
  if (this.registrationExpiry && this.registrationExpiry <= sevenDaysFromNow) {
    return true;
  }
  
  return false;
};

module.exports = mongoose.model('Truck', truckSchema);
