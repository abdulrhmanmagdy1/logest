/**
 * ============================================
 * 💰 Invoice Model - نظام إدهام
 * Edham Logistics - Invoice Schema
 * ============================================
 */

const mongoose = require('mongoose');

const invoiceSchema = new mongoose.Schema({
  // Invoice Identification
  invoiceNumber: {
    type: String,
    required: true,
    unique: true,
    trim: true
  },
  serialNumber: {
    type: String,
    required: true,
    unique: true,
    index: true
  },
  verificationCode: {
    type: String,
    required: true,
    unique: true
  },
  qrCode: {
    type: String
  },
  
  // Verification (ZATCA Compliance)
  isVerified: {
    type: Boolean,
    default: false
  },
  verificationAttempts: {
    type: Number,
    default: 0
  },
  lastVerifiedAt: {
    type: Date
  },
  
  // Related Entities
  shipment: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Shipment'
  },
  client: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  
  // Financial Details
  subtotal: {
    type: Number,
    required: true,
    min: 0
  },
  tax: {
    type: Number,
    default: 0,
    min: 0
  },
  taxRate: {
    type: Number,
    default: 0.15 // 15% VAT for Saudi Arabia
  },
  total: {
    type: Number,
    required: true,
    min: 0
  },
  amountPaid: {
    type: Number,
    default: 0,
    min: 0
  },
  balanceDue: {
    type: Number,
    min: 0
  },
  currency: {
    type: String,
    default: 'SAR'
  },
  
  // Status
  status: {
    type: String,
    enum: ['draft', 'pending', 'partial', 'paid', 'overdue', 'cancelled'],
    default: 'pending'
  },
  
  // Timing
  dueDate: {
    type: Date,
    required: true
  },
  paidDate: {
    type: Date
  },
  
  // Payment Details
  paymentMethod: {
    type: String,
    enum: ['cash', 'card', 'bank_transfer', 'mada', 'stc_pay', 'check']
  },
  paymentReference: {
    type: String
  },
  
  // Line Items
  items: [{
    description: {
      type: String,
      required: true
    },
    quantity: {
      type: Number,
      required: true,
      min: 0
    },
    unitPrice: {
      type: Number,
      required: true,
      min: 0
    },
    total: {
      type: Number,
      required: true,
      min: 0
    }
  }],
  
  // Notes & Additional Info
  notes: {
    type: String
  },
  terms: {
    type: String
  },
  
  // Created By
  accountant: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
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
invoiceSchema.index({ invoiceNumber: 1 });
invoiceSchema.index({ serialNumber: 1 });
invoiceSchema.index({ client: 1 });
invoiceSchema.index({ status: 1 });
invoiceSchema.index({ dueDate: 1 });

// Calculate balance due before save
invoiceSchema.pre('save', function(next) {
  this.balanceDue = this.total - this.amountPaid;
  next();
});

// Generate invoice number before save
invoiceSchema.pre('save', async function(next) {
  if (!this.invoiceNumber) {
    const year = new Date().getFullYear();
    const month = String(new Date().getMonth() + 1).padStart(2, '0');
    const count = await this.constructor.countDocuments({
      createdAt: {
        $gte: new Date(year, new Date().getMonth(), 1),
        $lt: new Date(year, new Date().getMonth() + 1, 1)
      }
    });
    this.invoiceNumber = `INV-${year}${month}-${String(count + 1).padStart(4, '0')}`;
  }
  next();
});

module.exports = mongoose.model('Invoice', invoiceSchema);
