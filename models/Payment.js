/**
 * ============================================
 * 💳 Payment Model - نظام إدهام
 * Edham Logistics - Payment Management
 * ============================================
 */

const mongoose = require('mongoose');

const paymentSchema = new mongoose.Schema({
  // Payment Information
  amount: {
    type: Number,
    required: true,
    min: 0
  },
  currency: {
    type: String,
    default: 'SAR',
    enum: ['SAR', 'USD', 'EUR']
  },
  method: {
    type: String,
    required: true,
    enum: ['cash', 'bank_transfer', 'credit_card', 'stripe', 'check', 'wallet']
  },
  status: {
    type: String,
    default: 'pending',
    enum: ['pending', 'completed', 'failed', 'cancelled', 'refunded']
  },
  
  // Related Information
  shipment: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Shipment'
  },
  invoice: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Invoice'
  },
  client: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  
  // Payment Details
  transactionId: String,
  gateway: String,
  gatewayResponse: mongoose.Schema.Types.Mixed,
  
  // Bank Transfer Details
  bankName: String,
  accountNumber: String,
  iban: String,
  transferDate: Date,
  receiptNumber: String,
  
  // Check Details
  checkNumber: String,
  checkDate: Date,
  bankName: String,
  
  // Credit Card Details (encrypted)
  cardLast4: String,
  cardBrand: String,
  
  // Timestamps
  createdAt: {
    type: Date,
    default: Date.now
  },
  completedAt: Date,
  failedAt: Date,
  cancelledAt: Date,
  refundedAt: Date,
  
  // Audit Fields
  createdBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  approvedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  notes: String,
  
  // Soft Delete
  deletedAt: Date
}, {
  timestamps: true,
  toJSON: { virtuals: true },
  toObject: { virtuals: true }
});

// Indexes
paymentSchema.index({ client: 1, createdAt: -1 });
paymentSchema.index({ shipment: 1 });
paymentSchema.index({ invoice: 1 });
paymentSchema.index({ status: 1 });
paymentSchema.index({ transactionId: 1 });

// Virtuals
paymentSchema.virtual('isCompleted').get(function() {
  return this.status === 'completed';
});

paymentSchema.virtual('isPending').get(function() {
  return this.status === 'pending';
});

paymentSchema.virtual('isFailed').get(function() {
  return this.status === 'failed';
});

// Static Methods
paymentSchema.statics.getClientPayments = function(clientId, options = {}) {
  const { page = 1, limit = 20, status, method } = options;
  
  let query = { 
    client: clientId,
    deletedAt: null
  };
  
  if (status) query.status = status;
  if (method) query.method = method;
  
  return this.find(query)
    .populate('shipment', 'trackingNumber')
    .populate('invoice', 'invoiceNumber')
    .sort({ createdAt: -1 })
    .skip((page - 1) * limit)
    .limit(limit);
};

paymentSchema.statics.getPaymentStatistics = function(filters = {}) {
  const { startDate, endDate, clientId } = filters;
  
  let matchStage = { deletedAt: null };
  if (startDate || endDate) {
    matchStage.createdAt = {};
    if (startDate) matchStage.createdAt.$gte = startDate;
    if (endDate) matchStage.createdAt.$lte = endDate;
  }
  if (clientId) matchStage.client = clientId;
  
  return this.aggregate([
    { $match: matchStage },
    {
      $group: {
        _id: null,
        totalAmount: { $sum: '$amount' },
        completedAmount: {
          $sum: { $cond: [{ $eq: ['$status', 'completed'] }, '$amount', 0] }
        },
        pendingAmount: {
          $sum: { $cond: [{ $eq: ['$status', 'pending'] }, '$amount', 0] }
        },
        totalCount: { $sum: 1 },
        completedCount: {
          $sum: { $cond: [{ $eq: ['$status', 'completed'] }, 1, 0] }
        },
        pendingCount: {
          $sum: { $cond: [{ $eq: ['$status', 'pending'] }, 1, 0] }
        }
      }
    }
  ]);
};

// Instance Methods
paymentSchema.methods.complete = function(approvedBy, notes) {
  this.status = 'completed';
  this.completedAt = new Date();
  this.approvedBy = approvedBy;
  if (notes) this.notes = notes;
  return this.save();
};

paymentSchema.methods.fail = function(reason) {
  this.status = 'failed';
  this.failedAt = new Date();
  this.notes = reason;
  return this.save();
};

paymentSchema.methods.cancel = function(reason) {
  this.status = 'cancelled';
  this.cancelledAt = new Date();
  this.notes = reason;
  return this.save();
};

paymentSchema.methods.refund = function(amount, reason) {
  this.status = 'refunded';
  this.refundedAt = new Date();
  this.amount = amount;
  this.notes = reason;
  return this.save();
};

module.exports = mongoose.model('Payment', paymentSchema);
