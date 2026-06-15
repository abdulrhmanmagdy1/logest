/**
 * ============================================
 * 📱 ScanLog Model - نظام إدهام
 * Records every QR / barcode scan event
 * ============================================
 */

const mongoose = require('mongoose');

const ScanLogSchema = new mongoose.Schema(
  {
    type: {
      type: String,
      enum: ['shipment', 'invoice', 'tracking', 'other'],
      required: true,
      default: 'shipment',
    },
    // Generic reference — populated based on `type`
    referenceId: {
      type: mongoose.Schema.Types.ObjectId,
    },
    // Dedicated shipment ref so .populate('shipment') works in history route
    shipment: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Shipment',
    },
    scanner: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true,
    },
    location: {
      type: {
        type: String,
        enum: ['Point'],
        default: 'Point',
      },
      coordinates: {
        type: [Number],
        default: [0, 0],
      },
      address: String,
    },
    status: {
      type: String,
      trim: true,
    },
    action: {
      type: String,
      trim: true,
      default: 'verified',
    },
  },
  {
    timestamps: true,
  }
);

// Auto-populate `shipment` from `referenceId` when type is shipment
ScanLogSchema.pre('save', function (next) {
  if (this.type === 'shipment' && this.referenceId && !this.shipment) {
    this.shipment = this.referenceId;
  }
  next();
});

ScanLogSchema.index({ scanner: 1, createdAt: -1 });
ScanLogSchema.index({ shipment: 1 });
ScanLogSchema.index({ type: 1, createdAt: -1 });

module.exports = mongoose.model('ScanLog', ScanLogSchema);
