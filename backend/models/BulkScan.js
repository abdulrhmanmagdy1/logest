/**
 * ============================================
 * 📦 BulkScan Model - نظام إدهام
 * Records batch scan operations (loading / unloading)
 * ============================================
 */

const mongoose = require('mongoose');

const BulkScanSchema = new mongoose.Schema(
  {
    operation: {
      type: String,
      enum: ['loading', 'unloading', 'inventory', 'dispatch', 'other'],
      default: 'other',
    },
    driver: {
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
    totalScanned: {
      type: Number,
      default: 0,
    },
    successful: {
      type: Number,
      default: 0,
    },
    failed: {
      type: Number,
      default: 0,
    },
    shipments: [
      {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Shipment',
      },
    ],
  },
  {
    timestamps: true,
  }
);

BulkScanSchema.index({ driver: 1, createdAt: -1 });
BulkScanSchema.index({ operation: 1 });

module.exports = mongoose.model('BulkScan', BulkScanSchema);
