/**
 * ============================================
 * 🚛 Truck Model - نظام إدهام
 * Truck/Fleet management schema
 * ============================================
 */

const mongoose = require('mongoose');

const TruckSchema = new mongoose.Schema({
  // Basic Info
  plateNumber: {
    type: String,
    required: [true, 'Please add a plate number'],
    unique: true,
    trim: true
  },
  
  // Truck Details
  make: {
    type: String,
    required: true
  },
  model: {
    type: String,
    required: true
  },
  year: {
    type: Number,
    required: true,
    min: 1990,
    max: new Date().getFullYear() + 1
  },
  vin: {
    type: String,
    unique: true,
    sparse: true
  },
  
  // Specifications
  type: {
    type: String,
    enum: ['small', 'medium', 'large', 'xl', 'trailer'],
    required: true
  },
  capacity: {
    weight: {
      value: { type: Number, required: true },
      unit: { type: String, enum: ['kg', 'ton'], default: 'ton' }
    },
    volume: {
      value: Number,
      unit: { type: String, enum: ['m3', 'l'], default: 'm3' }
    }
  },
  
  // Refrigeration
  refrigeration: {
    hasRefrigeration: {
      type: Boolean,
      default: true
    },
    unitModel: String,
    minTemperature: {
      type: Number,
      default: -25
    },
    maxTemperature: {
      type: Number,
      default: 25
    },
    lastService: Date,
    nextService: Date
  },
  
  // Status
  status: {
    type: String,
    enum: ['active', 'maintenance', 'inactive', 'retired', 'on_trip'],
    default: 'active'
  },
  
  // Current Assignment
  assignedDriver: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    default: null
  },
  currentShipment: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Shipment',
    default: null
  },
  currentLocation: {
    lat: Number,
    lng: Number,
    updatedAt: Date
  },
  
  // Maintenance
  maintenance: {
    lastMaintenance: Date,
    nextMaintenance: Date,
    maintenanceInterval: {
      type: Number,
      default: 10000 // km
    },
    currentMileage: {
      type: Number,
      default: 0
    },
    maintenanceHistory: [{
      date: Date,
      type: String,
      description: String,
      cost: Number,
      performedBy: String
    }]
  },
  
  // Documents
  documents: {
    registration: {
      number: String,
      expiryDate: Date,
      documentUrl: String
    },
    insurance: {
      provider: String,
      policyNumber: String,
      expiryDate: Date,
      documentUrl: String
    },
    inspection: {
      lastDate: Date,
      nextDate: Date,
      result: {
        type: String,
        enum: ['pass', 'fail', 'pending']
      },
      documentUrl: String
    }
  },
  
  // Fuel & Efficiency
  fuel: {
    type: {
      type: String,
      enum: ['diesel', 'gasoline', 'electric', 'hybrid'],
      default: 'diesel'
    },
    tankCapacity: Number, // liters
    averageConsumption: Number // km per liter
  },
  
  // Equipment
  equipment: [{
    name: String,
    type: String,
    installedDate: Date,
    status: {
      type: String,
      enum: ['active', 'inactive', 'damaged'],
      default: 'active'
    }
  }],
  
  // GPS Tracker
  gpsDevice: {
    deviceId: String,
    provider: String,
    installedDate: Date,
    status: {
      type: String,
      enum: ['active', 'inactive', 'error'],
      default: 'active'
    },
    lastPing: Date
  },
  
  // Metadata
  notes: String,
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
TruckSchema.index({ plateNumber: 1 });
TruckSchema.index({ status: 1 });
TruckSchema.index({ assignedDriver: 1 });
TruckSchema.index({ 'documents.registration.expiryDate': 1 });
TruckSchema.index({ 'documents.insurance.expiryDate': 1 });

// Update timestamp
TruckSchema.pre('save', function(next) {
  this.updatedAt = Date.now();
  next();
});

// Check if truck needs maintenance
TruckSchema.methods.needsMaintenance = function() {
  if (!this.maintenance.nextMaintenance) return false;
  
  const now = new Date();
  return now >= this.maintenance.nextMaintenance ||
         this.maintenance.currentMileage >= this.maintenance.lastMaintenance + this.maintenance.maintenanceInterval;
};

// Check if documents are expired
TruckSchema.methods.checkDocumentExpiry = function() {
  const now = new Date();
  const alerts = [];
  
  if (this.documents.registration?.expiryDate && now > this.documents.registration.expiryDate) {
    alerts.push('Registration expired');
  }
  
  if (this.documents.insurance?.expiryDate && now > this.documents.insurance.expiryDate) {
    alerts.push('Insurance expired');
  }
  
  if (this.documents.inspection?.nextDate && now > this.documents.inspection.nextDate) {
    alerts.push('Inspection due');
  }
  
  return alerts;
};

module.exports = mongoose.model('Truck', TruckSchema);
