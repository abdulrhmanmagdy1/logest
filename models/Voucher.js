/**
 * ============================================
 * 🧾 Voucher Model - نظام إدهام
 * Edham Logistics - Receipt & Voucher Schema
 * ============================================
 */

const mongoose = require('mongoose');
const crypto = require('crypto');

const voucherSchema = new mongoose.Schema({
  // Sequential serial number for anti-fraud
  serialNumber: {
    type: String,
    required: true,
    unique: true,
    index: true
  },
  
  // Voucher type
  type: {
    type: String,
    enum: ['receipt', 'payment', 'transfer'],
    required: true,
    index: true
  },
  
  // Client information
  clientId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true,
    index: true
  },
  
  // Financial information
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
  
  // Description and details
  description: {
    type: String,
    required: true,
    trim: true,
    maxlength: 500
  },
  
  // Payment method
  paymentMethod: {
    type: String,
    enum: ['cash', 'bank_transfer', 'check', 'balance', 'card'],
    required: true
  },
  
  // Reference number (bank transfer, check, etc.)
  referenceNumber: {
    type: String,
    trim: true
  },
  
  // Items/services list
  items: [{
    description: String,
    quantity: Number,
    unitPrice: Number,
    totalPrice: Number
  }],
  
  // Voucher status
  status: {
    type: String,
    enum: ['pending', 'completed', 'cancelled', 'failed'],
    default: 'pending',
    index: true
  },
  
  // Notes and additional information
  notes: {
    type: String,
    maxlength: 1000
  },
  
  // Approval workflow
  approvedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  approvedAt: {
    type: Date
  },
  
  // Cancellation details
  cancelledBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  cancelledAt: {
    type: Date
  },
  
  // Created by
  createdBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  
  // Verification hash for anti-fraud
  verificationHash: {
    type: String,
    index: true
  },
  
  // Metadata
  metadata: {
    type: mongoose.Schema.Types.Mixed,
    default: {}
  },
  
  // Attachments
  attachments: [{
    filename: String,
    originalName: String,
    path: String,
    mimeType: String,
    size: Number,
    uploadedAt: {
      type: Date,
      default: Date.now
    }
  }],
  
  // QR code for verification
  qrCode: {
    type: String
  }
}, {
  timestamps: true,
  toJSON: { virtuals: true },
  toObject: { virtuals: true }
});

// Indexes for performance
voucherSchema.index({ clientId: 1, createdAt: -1 });
voucherSchema.index({ type: 1, status: 1, createdAt: -1 });
voucherSchema.index({ serialNumber: 1, verificationHash: 1 });
voucherSchema.index({ createdBy: 1, createdAt: -1 });

// Virtual for formatted amount
voucherSchema.virtual('formattedAmount').get(function() {
  return `${this.amount.toLocaleString()} ${this.currency}`;
});

// Virtual for status in Arabic
voucherSchema.virtual('statusAr').get(function() {
  const statusMap = {
    pending: 'معلق',
    completed: 'مكتمل',
    cancelled: 'ملغي',
    failed: 'فشل'
  };
  return statusMap[this.status] || this.status;
});

// Virtual for type in Arabic
voucherSchema.virtual('typeAr').get(function() {
  const typeMap = {
    receipt: 'سند قبض',
    payment: 'سند صرف',
    transfer: 'حوالة'
  };
  return typeMap[this.type] || this.type;
});

// Pre-save middleware to generate serial number and verification hash
voucherSchema.pre('save', async function(next) {
  if (this.isNew) {
    try {
      // Generate serial number if not provided
      if (!this.serialNumber) {
        this.serialNumber = await this.constructor.generateSerialNumber(this.type);
      }
      
      // Generate verification hash for anti-fraud
      this.verificationHash = this.generateVerificationHash();
      
      // Generate QR code
      this.qrCode = `https://edham.logistics/verify/${this.serialNumber}?hash=${this.verificationHash}`;
    } catch (error) {
      return next(error);
    }
  }
  next();
});

// Static methods
voucherSchema.statics = {
  /**
   * Generate unique serial number for voucher type
   */
  async generateSerialNumber(type) {
    const year = new Date().getFullYear();
    const typePrefix = {
      receipt: 'RCP',
      payment: 'PAY',
      transfer: 'TRF'
    }[type] || 'VOU';
    
    const count = await this.countDocuments({
      serialNumber: { $regex: `^${typePrefix}-${year}` }
    });
    
    return `${typePrefix}-${year}-${String(count + 1).padStart(6, '0')}`;
  },
  
  /**
   * Get voucher statistics
   */
  async getStatistics(filters = {}) {
    const matchQuery = { ...filters };
    
    const [
      totalAmount,
      totalCount,
      typeStats,
      statusStats,
      monthlyStats
    ] = await Promise.all([
      this.aggregate([
        { $match: { ...matchQuery, status: 'completed' } },
        { $group: { _id: null, total: { $sum: '$amount' } } }
      ]),
      this.countDocuments(matchQuery),
      this.aggregate([
        { $match: matchQuery },
        { $group: { _id: '$type', count: { $sum: 1 }, total: { $sum: '$amount' } } }
      ]),
      this.aggregate([
        { $match: matchQuery },
        { $group: { _id: '$status', count: { $sum: 1 } } }
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
      totalAmount: totalAmount[0]?.total || 0,
      totalCount,
      byType: typeStats,
      byStatus: statusStats,
      monthly: monthlyStats
    };
  },
  
  /**
   * Get client vouchers with pagination
   */
  async getClientVouchers(clientId, options = {}) {
    const {
      page = 1,
      limit = 20,
      type,
      status,
      startDate,
      endDate
    } = options;
    
    const query = { clientId };
    
    if (type) query.type = type;
    if (status) query.status = status;
    if (startDate || endDate) {
      query.createdAt = {};
      if (startDate) query.createdAt.$gte = new Date(startDate);
      if (endDate) query.createdAt.$lte = new Date(endDate);
    }
    
    const skip = (page - 1) * limit;
    
    const [vouchers, total] = await Promise.all([
      this.find(query)
        .populate('createdBy', 'name email')
        .populate('approvedBy', 'name email')
        .sort({ createdAt: -1 })
        .skip(skip)
        .limit(limit),
      this.countDocuments(query)
    ]);
    
    return {
      vouchers,
      pagination: {
        page,
        limit,
        total,
        pages: Math.ceil(total / limit)
      }
    };
  },
  
  /**
   * Verify voucher authenticity
   */
  async verifyVoucher(serialNumber, verificationCode) {
    const voucher = await this.findOne({ serialNumber });
    
    if (!voucher) {
      return { valid: false, reason: 'السند غير موجود' };
    }
    
    if (voucher.verificationHash !== verificationCode) {
      return { valid: false, reason: 'رمز التحقق غير صحيح' };
    }
    
    return { valid: true, voucher };
  }
};

// Instance methods
voucherSchema.methods = {
  /**
   * Generate verification hash for anti-fraud
   */
  generateVerificationHash() {
    const data = `${this.serialNumber}-${this.amount}-${this.type}-${this.createdAt.getTime()}`;
    return crypto.createHash('sha256').update(data).digest('hex').substring(0, 16);
  },
  
  /**
   * Approve voucher
   */
  async approve(approvedBy) {
    if (this.status !== 'pending') {
      throw new Error('لا يمكن اعتماد سند غير معلق');
    }
    
    this.status = 'completed';
    this.approvedBy = approvedBy;
    this.approvedAt = new Date();
    
    await this.save();
    return this;
  },
  
  /**
   * Cancel voucher
   */
  async cancel(cancelledBy, reason) {
    if (this.status === 'cancelled') {
      throw new Error('السند ملغي بالفعل');
    }
    
    this.status = 'cancelled';
    this.cancelledBy = cancelledBy;
    this.cancelledAt = new Date();
    this.metadata.cancellationReason = reason;
    
    await this.save();
    return this;
  },
  
  /**
   * Add attachment
   */
  async addAttachment(attachmentData) {
    this.attachments.push({
      ...attachmentData,
      uploadedAt: new Date()
    });
    await this.save();
    return this.attachments[this.attachments.length - 1];
  },
  
  /**
   * Generate printable version
   */
  generatePrintableData() {
    return {
      serialNumber: this.serialNumber,
      type: this.typeAr,
      amount: this.formattedAmount,
      clientName: this.clientId?.name || 'N/A',
      description: this.description,
      paymentMethod: this.paymentMethod,
      referenceNumber: this.referenceNumber,
      status: this.statusAr,
      createdAt: this.createdAt.toLocaleDateString('ar-SA'),
      createdBy: this.createdBy?.name || 'N/A',
      approvedBy: this.approvedBy?.name || 'N/A',
      approvedAt: this.approvedAt?.toLocaleDateString('ar-SA'),
      qrCode: this.qrCode,
      verificationHash: this.verificationHash
    };
  }
};

module.exports = mongoose.model('Voucher', voucherSchema);
