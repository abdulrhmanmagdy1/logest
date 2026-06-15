/**
 * ============================================
 * 📦 Shipment Model - نظام إدهام
 * Edham Logistics - Shipment Schema
 * ============================================
 */

const mongoose = require('mongoose');

const shipmentSchema = new mongoose.Schema({
  // Shipment Identification
  shipmentNumber: {
    type: String,
    required: true,
    unique: true,
    trim: true
  },
  
  // Client Information
  client: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  
  // Locations
  pickupLocation: {
    address: {
      type: String,
      required: true
    },
    latitude: {
      type: Number,
      required: true
    },
    longitude: {
      type: Number,
      required: true
    },
    contactName: String,
    contactPhone: String
  },
  deliveryLocation: {
    address: {
      type: String,
      required: true
    },
    latitude: {
      type: Number,
      required: true
    },
    longitude: {
      type: Number,
      required: true
    },
    contactName: String,
    contactPhone: String
  },
  
  // Assigned Resources
  truck: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Truck'
  },
  driver: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  supervisor: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  
  // Shipment Status
  status: {
    type: String,
    enum: ['pending', 'assigned', 'picked_up', 'in_transit', 'delivered', 'cancelled', 'delayed'],
    default: 'pending'
  },
  statusHistory: [{
    status: String,
    timestamp: {
      type: Date,
      default: Date.now
    },
    updatedBy: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    notes: String
  }],
  
  // Cargo Details
  cargoType: {
    type: String,
    enum: ['refrigerated', 'frozen', 'dry', 'hazardous', 'fragile', 'general'],
    default: 'general'
  },
  cargoDetails: {
    type: String,
    required: true
  },
  weight: {
    type: Number,
    min: 0
  },
  volume: {
    type: Number,
    min: 0
  },
  temperatureRequired: {
    type: Number, // For refrigerated shipments
    min: -30,
    max: 30
  },
  
  // Special Requirements
  specialInstructions: {
    type: String
  },
  priority: {
    type: String,
    enum: ['low', 'normal', 'high', 'urgent'],
    default: 'normal'
  },
  
  // Timing
  pickupTime: {
    type: Date
  },
  estimatedDelivery: {
    type: Date
  },
  actualDelivery: {
    type: Date
  },
  
  // Pricing
  estimatedCost: {
    type: Number,
    min: 0
  },
  actualCost: {
    type: Number,
    min: 0
  },
  
  // Attachments & Documents
  attachments: [{
    type: String, // File URL
    name: String,
    uploadedAt: {
      type: Date,
      default: Date.now
    },
    attachmentType: {
      type: String,
      enum: ['pickup_proof', 'delivery_proof', 'invoice', 'other']
    }
  }],
  
  // Tracking
  trackingNumber: {
    type: String,
    unique: true
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
shipmentSchema.index({ shipmentNumber: 1 });
shipmentSchema.index({ client: 1 });
shipmentSchema.index({ status: 1 });
shipmentSchema.index({ driver: 1 });
shipmentSchema.index({ 'pickupLocation.latitude': 1, 'pickupLocation.longitude': 1 });
shipmentSchema.index({ createdAt: -1 });

// Generate shipment number before save
shipmentSchema.pre('save', async function(next) {
  if (!this.shipmentNumber) {
    const year = new Date().getFullYear();
    const count = await this.constructor.countDocuments();
    this.shipmentNumber = `EDH-${year}-${String(count + 1).padStart(6, '0')}`;
  }
  next();
});

module.exports = mongoose.model('Shipment', shipmentSchema);
