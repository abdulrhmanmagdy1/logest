/**
 * ============================================
 * 🔧 Maintenance Model - نظام إدهام
 * Edham Logistics - Maintenance Schema
 * ============================================
 */

const mongoose = require('mongoose');

const maintenanceSchema = new mongoose.Schema({
  // Maintenance Identification
  maintenanceNumber: {
    type: String,
    required: true,
    unique: true,
    trim: true
  },
  
  // Related Truck
  truck: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Truck',
    required: true
  },
  
  // Maintenance Type
  type: {
    type: String,
    enum: ['oil_change', 'tire_change', 'brake_service', 'engine_repair', 'refrigeration_service', 'inspection', 'routine', 'emergency', 'other'],
    required: true
  },
  
  // Description
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    required: true
  },
  
  // Priority
  priority: {
    type: String,
    enum: ['low', 'normal', 'high', 'urgent'],
    default: 'normal'
  },
  
  // Status
  status: {
    type: String,
    enum: ['scheduled', 'in_progress', 'completed', 'cancelled', 'on_hold'],
    default: 'scheduled'
  },
  
  // Timing
  scheduledDate: {
    type: Date,
    required: true
  },
  completedDate: {
    type: Date
  },
  estimatedDuration: {
    type: Number // in hours
  },
  actualDuration: {
    type: Number // in hours
  },
  
  // Cost
  estimatedCost: {
    type: Number,
    min: 0
  },
  actualCost: {
    type: Number,
    min: 0
  },
  
  // Service Provider
  mechanic: {
    type: String
  },
  serviceCenter: {
    type: String
  },
  performedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  
  // Parts Used
  partsUsed: [{
    name: {
      type: String,
      required: true
    },
    partNumber: String,
    quantity: {
      type: Number,
      required: true,
      min: 0
    },
    unitCost: {
      type: Number,
      min: 0
    },
    totalCost: {
      type: Number,
      min: 0
    }
  }],
  
  // Odometer Reading
  odometerReading: {
    type: Number
  },
  
  // Notes & Attachments
  notes: {
    type: String
  },
  attachments: [{
    type: String, // File URL
    name: String,
    uploadedAt: {
      type: Date,
      default: Date.now
    }
  }],
  
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
maintenanceSchema.index({ maintenanceNumber: 1 });
maintenanceSchema.index({ truck: 1 });
maintenanceSchema.index({ status: 1 });
maintenanceSchema.index({ scheduledDate: 1 });
maintenanceSchema.index({ type: 1 });

// Generate maintenance number before save
maintenanceSchema.pre('save', async function(next) {
  if (!this.maintenanceNumber) {
    const year = new Date().getFullYear();
    const count = await this.constructor.countDocuments();
    this.maintenanceNumber = `MNT-${year}-${String(count + 1).padStart(6, '0')}`;
  }
  next();
});

module.exports = mongoose.model('Maintenance', maintenanceSchema);
