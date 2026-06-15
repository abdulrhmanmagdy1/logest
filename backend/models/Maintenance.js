/**
 * ============================================
 * 🔧 Maintenance Model - نظام إدهام
 * Fleet maintenance request records
 * ============================================
 */

const mongoose = require('mongoose');

const MaintenanceSchema = new mongoose.Schema(
  {
    truck: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Truck',
      required: true,
    },
    type: {
      type: String,
      enum: ['preventive', 'corrective', 'emergency', 'inspection', 'oil_change', 'tire', 'brake', 'electrical', 'other'],
      required: true,
      default: 'corrective',
    },
    category: {
      type: String,
      enum: ['engine', 'transmission', 'brakes', 'tires', 'electrical', 'body', 'hvac', 'fuel_system', 'suspension', 'other'],
      default: 'other',
    },
    description: {
      type: String,
      required: true,
      trim: true,
    },
    priority: {
      type: String,
      enum: ['low', 'normal', 'high', 'critical'],
      default: 'normal',
    },
    status: {
      type: String,
      enum: ['pending', 'approved', 'in_progress', 'completed', 'cancelled'],
      default: 'pending',
    },

    // Scheduling
    scheduledDate: {
      type: Date,
      default: Date.now,
    },
    startedAt: Date,
    completedAt: Date,
    nextMaintenanceDate: Date,

    // People
    requestedBy: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
    },
    assignedTo: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
    },
    completedBy: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
    },

    // Costs
    estimatedCost: {
      type: Number,
      default: 0,
    },
    actualCost: {
      type: Number,
      default: 0,
    },

    // Parts
    partsUsed: [
      {
        partId: String,
        name: String,
        quantity: { type: Number, default: 1 },
        unitCost: { type: Number, default: 0 },
      },
    ],

    // Work details
    workDescription: {
      type: String,
      trim: true,
    },
    notes: {
      type: String,
      trim: true,
    },
  },
  {
    timestamps: true,
  }
);

// Index for common query patterns used in workshop.js
MaintenanceSchema.index({ status: 1 });
MaintenanceSchema.index({ priority: 1, status: 1 });
MaintenanceSchema.index({ truck: 1, createdAt: -1 });
MaintenanceSchema.index({ scheduledDate: 1, status: 1 });
MaintenanceSchema.index({ assignedTo: 1, status: 1 });

module.exports = mongoose.model('Maintenance', MaintenanceSchema);
