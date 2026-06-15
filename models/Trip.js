/**
 * ============================================
 * 🚗 Trip Model - نظام إدهام
 * Edham Logistics - Trip Schema
 * ============================================
 */

const mongoose = require('mongoose');

const tripSchema = new mongoose.Schema({
  // Trip Identification
  tripNumber: {
    type: String,
    required: true,
    unique: true,
    trim: true
  },
  
  // Related Entities
  shipment: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Shipment'
  },
  driver: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  truck: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Truck',
    required: true
  },
  
  // Locations
  startPoint: {
    address: String,
    latitude: Number,
    longitude: Number,
    timestamp: {
      type: Date,
      default: Date.now
    }
  },
  endPoint: {
    address: String,
    latitude: Number,
    longitude: Number,
    timestamp: Date
  },
  destination: {
    address: String,
    latitude: Number,
    longitude: Number
  },
  
  // Status
  status: {
    type: String,
    enum: ['scheduled', 'started', 'in_progress', 'paused', 'completed', 'cancelled'],
    default: 'scheduled'
  },
  statusHistory: [{
    status: String,
    timestamp: {
      type: Date,
      default: Date.now
    },
    location: {
      latitude: Number,
      longitude: Number
    }
  }],
  
  // Timing
  startTime: {
    type: Date
  },
  endTime: {
    type: Date
  },
  estimatedDuration: {
    type: Number // in minutes
  },
  actualDuration: {
    type: Number // in minutes
  },
  
  // Distance & Route
  distance: {
    type: Number // in km
  },
  route: [{
    latitude: Number,
    longitude: Number,
    timestamp: Date,
    speed: Number,
    heading: Number
  }],
  
  // Odometer & Fuel
  odometerStart: {
    type: Number
  },
  odometerEnd: {
    type: Number
  },
  fuelConsumption: {
    type: Number // in liters
  },
  fuelCost: {
    type: Number
  },
  
  // Purpose & Notes
  purpose: {
    type: String
  },
  notes: {
    type: String
  },
  
  // Delivery Proof
  deliveryProof: [{
    type: String, // File URL
    uploadedAt: {
      type: Date,
      default: Date.now
    }
  }],
  recipientName: {
    type: String
  },
  recipientSignature: {
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
tripSchema.index({ tripNumber: 1 });
tripSchema.index({ driver: 1 });
tripSchema.index({ truck: 1 });
tripSchema.index({ shipment: 1 });
tripSchema.index({ status: 1 });
tripSchema.index({ startTime: -1 });

// Generate trip number before save
tripSchema.pre('save', async function(next) {
  if (!this.tripNumber) {
    const year = new Date().getFullYear();
    const count = await this.constructor.countDocuments();
    this.tripNumber = `TRP-${year}-${String(count + 1).padStart(6, '0')}`;
  }
  next();
});

module.exports = mongoose.model('Trip', tripSchema);
