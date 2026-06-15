/**
 * ============================================
 * 💰 Transaction Model - نظام إدهام
 * Edham Logistics - Transaction Schema
 * ============================================
 */

const mongoose = require('mongoose');

const transactionSchema = new mongoose.Schema({
  userId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true,
    index: true
  },
  orderId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Shipment',
    required: false
  },
  type: {
    type: String,
    enum: ['credit', 'debit', 'payment', 'pending', 'refund'],
    required: true,
    index: true
  },
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
  description: {
    type: String,
    required: true,
    trim: true
  },
  status: {
    type: String,
    enum: ['pending', 'completed', 'failed', 'cancelled'],
    default: 'completed',
    index: true
  },
  paymentMethod: {
    type: String,
    enum: ['balance', 'card', 'cash', 'bank_transfer'],
    required: false
  },
  paymentIntentId: {
    type: String,
    required: false,
    index: true
  },
  items: [{
    serviceId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Service'
    },
    name: String,
    price: Number,
    quantity: Number
  }],
  metadata: {
    type: mongoose.Schema.Types.Mixed,
    default: {}
  },
  // Sequential serial number for receipts
  serialNumber: {
    type: String,
    unique: true,
    sparse: true,
    index: true
  },
  // For receipts and invoices
  receiptUrl: {
    type: String
  },
  invoiceUrl: {
    type: String
  },
  // Related entities
  createdBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  approvedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  }
}, {
  timestamps: true,
  toJSON: { virtuals: true },
  toObject: { virtuals: true }
});

// Indexes for performance
transactionSchema.index({ userId: 1, createdAt: -1 });
transactionSchema.index({ status: 1, createdAt: -1 });
transactionSchema.index({ paymentMethod: 1, createdAt: -1 });
transactionSchema.index({ serialNumber: 1 });

// Virtual for formatted amount
transactionSchema.virtual('formattedAmount').get(function() {
  return `${this.amount.toLocaleString()} ${this.currency}`;
});

// Pre-save middleware to generate serial number
transactionSchema.pre('save', async function(next) {
  if (this.isNew && !this.serialNumber) {
    try {
      const year = new Date().getFullYear();
      const count = await this.constructor.countDocuments({
        serialNumber: { $regex: `^TRX-${year}` }
      });
      this.serialNumber = `TRX-${year}-${String(count + 1).padStart(6, '0')}`;
    } catch (error) {
      return next(error);
    }
  }
  next();
});

// Static methods
transactionSchema.statics = {
  /**
   * Get user balance
   */
  async getUserBalance(userId) {
    const result = await this.aggregate([
      {
        $match: {
          userId: mongoose.Types.ObjectId(userId),
          status: 'completed'
        }
      },
      {
        $group: {
          _id: null,
          credits: {
            $sum: {
              $cond: [{ $in: ['$type', ['credit', 'refund']] }, '$amount', 0]
            }
          },
          debits: {
            $sum: {
              $cond: [{ $in: ['$type', ['debit', 'payment']] }, '$amount', 0]
            }
          }
        }
      }
    ]);

    const balance = result.length > 0 ? result[0].credits - result[0].debits : 0;
    return Math.max(0, balance);
  },

  /**
   * Get transaction statistics
   */
  async getStatistics(filters = {}) {
    const matchQuery = { ...filters };
    
    const [
      totalRevenue,
      totalTransactions,
      paymentMethodStats,
      typeStats,
      monthlyStats
    ] = await Promise.all([
      this.aggregate([
        { $match: { ...matchQuery, status: 'completed' } },
        { $group: { _id: null, total: { $sum: '$amount' } } }
      ]),
      this.countDocuments(matchQuery),
      this.aggregate([
        { $match: { ...matchQuery, status: 'completed' } },
        { $group: { _id: '$paymentMethod', count: { $sum: 1 }, total: { $sum: '$amount' } } }
      ]),
      this.aggregate([
        { $match: { ...matchQuery, status: 'completed' } },
        { $group: { _id: '$type', count: { $sum: 1 }, total: { $sum: '$amount' } } }
      ]),
      this.aggregate([
        { $match: { ...matchQuery, status: 'completed' } },
        {
          $group: {
            _id: {
              year: { $year: '$createdAt' },
              month: { $month: '$createdAt' }
            },
            total: { $sum: '$amount' },
            count: { $sum: 1 }
          }
        },
        { $sort: { '_id.year': 1, '_id.month': 1 } }
      ])
    ]);

    return {
      totalRevenue: totalRevenue[0]?.total || 0,
      totalTransactions,
      paymentMethods: paymentMethodStats,
      types: typeStats,
      monthlyStats
    };
  },

  /**
   * Get user transactions with pagination
   */
  async getUserTransactions(userId, options = {}) {
    const {
      page = 1,
      limit = 20,
      status,
      type,
      paymentMethod,
      startDate,
      endDate
    } = options;

    const query = { userId };
    
    if (status) query.status = status;
    if (type) query.type = type;
    if (paymentMethod) query.paymentMethod = paymentMethod;
    if (startDate || endDate) {
      query.createdAt = {};
      if (startDate) query.createdAt.$gte = new Date(startDate);
      if (endDate) query.createdAt.$lte = new Date(endDate);
    }

    const skip = (page - 1) * limit;

    const [transactions, total] = await Promise.all([
      this.find(query)
        .populate('orderId', 'trackingNumber status')
        .sort({ createdAt: -1 })
        .skip(skip)
        .limit(limit),
      this.countDocuments(query)
    ]);

    return {
      transactions,
      pagination: {
        page,
        limit,
        total,
        pages: Math.ceil(total / limit)
      }
    };
  },

  /**
   * Create receipt transaction
   */
  async createReceipt(data) {
    const transaction = new this({
      ...data,
      status: 'completed'
    });
    
    await transaction.save();
    return transaction;
  },

  /**
   * Create payment transaction
   */
  async createPayment(data) {
    const transaction = new this({
      ...data,
      type: 'payment',
      status: 'pending'
    });
    
    await transaction.save();
    return transaction;
  }
};

// Instance methods
transactionSchema.methods = {
  /**
   * Mark as completed
   */
  async complete(approvedBy) {
    this.status = 'completed';
    if (approvedBy) this.approvedBy = approvedBy;
    await this.save();
    return this;
  },

  /**
   * Mark as failed
   */
  async fail(reason) {
    this.status = 'failed';
    this.metadata.failureReason = reason;
    await this.save();
    return this;
  },

  /**
   * Generate receipt URL
   */
  generateReceiptUrl() {
    return `/receipts/${this.serialNumber}`;
  },

  /**
   * Generate invoice URL
   */
  generateInvoiceUrl() {
    return `/invoices/${this.serialNumber}`;
  }
};

module.exports = mongoose.model('Transaction', transactionSchema);
