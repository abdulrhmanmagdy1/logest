/**
 * ============================================
 * 🧾 Invoice Model - نظام إدهام
 * Invoice and billing schema
 * ============================================
 */

const mongoose = require('mongoose');

const InvoiceSchema = new mongoose.Schema({
  // Invoice Number
  invoiceNumber: {
    type: String,
    required: true,
    unique: true
  },
  
  // References
  client: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  shipments: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Shipment'
  }],
  
  // Invoice Type
  type: {
    type: String,
    enum: ['single', 'consolidated', 'recurring', 'credit_note', 'debit_note'],
    default: 'single'
  },
  
  // Dates
  issueDate: {
    type: Date,
    default: Date.now
  },
  dueDate: {
    type: Date,
    required: true
  },
  paidDate: Date,
  
  // Line Items
  items: [{
    description: {
      type: String,
      required: true
    },
    shipment: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Shipment'
    },
    quantity: {
      type: Number,
      default: 1
    },
    unitPrice: {
      type: Number,
      required: true
    },
    discount: {
      type: Number,
      default: 0
    },
    tax: {
      type: Number,
      default: 0
    },
    total: {
      type: Number,
      required: true
    }
  }],
  
  // Totals
  subtotal: {
    type: Number,
    required: true
  },
  discount: {
    type: Number,
    default: 0
  },
  taxRate: {
    type: Number,
    default: 15 // Saudi VAT
  },
  taxAmount: {
    type: Number,
    default: 0
  },
  shippingCost: {
    type: Number,
    default: 0
  },
  total: {
    type: Number,
    required: true
  },
  
  // Currency
  currency: {
    type: String,
    default: 'SAR'
  },
  
  // Status
  status: {
    type: String,
    enum: ['draft', 'sent', 'viewed', 'paid', 'partial', 'overdue', 'cancelled', 'refunded'],
    default: 'draft'
  },
  
  // Payment
  payment: {
    method: {
      type: String,
      enum: ['cash', 'credit_card', 'bank_transfer', 'cheque', 'wallet', 'online'],
      default: null
    },
    reference: String,
    transactionId: String,
    paidAmount: {
      type: Number,
      default: 0
    },
    remainingAmount: {
      type: Number,
      default: 0
    },
    payments: [{
      amount: Number,
      method: String,
      reference: String,
      date: {
        type: Date,
        default: Date.now
      },
      notes: String
    }]
  },
  
  // Client Info (snapshot at time of invoice)
  clientInfo: {
    name: String,
    email: String,
    phone: String,
    address: {
      street: String,
      city: String,
      region: String,
      zipCode: String
    },
    vatNumber: String
  },
  
  // Company Info
  companyInfo: {
    name: { type: String, default: 'شركة إدهام للنقل المبرد' },
    address: String,
    phone: String,
    email: String,
    vatNumber: String,
    commercialRegistration: String,
    logo: String
  },
  
  // Notes
  notes: String,
  termsAndConditions: String,
  
  // Reminders
  reminders: {
    sent: [{
      type: {
        type: String,
        enum: ['before_due', 'on_due', 'after_due']
      },
      date: Date,
      method: String
    }],
    nextReminder: Date
  },
  
  // Documents
  pdfUrl: String,
  
  // Metadata
  createdBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  sentBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  sentAt: Date,
  viewedAt: Date,
  createdAt: {
    type: Date,
    default: Date.now
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
});

// Indexes
InvoiceSchema.index({ invoiceNumber: 1 });
InvoiceSchema.index({ client: 1, createdAt: -1 });
InvoiceSchema.index({ status: 1 });
InvoiceSchema.index({ dueDate: 1 });

// Generate invoice number
InvoiceSchema.pre('save', async function(next) {
  if (!this.invoiceNumber) {
    const date = new Date();
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    
    // Get count of invoices this month
    const count = await this.constructor.countDocuments({
      createdAt: {
        $gte: new Date(year, date.getMonth(), 1),
        $lt: new Date(year, date.getMonth() + 1, 1)
      }
    });
    
    const sequence = String(count + 1).padStart(4, '0');
    this.invoiceNumber = `INV-${year}${month}-${sequence}`;
  }
  
  // Calculate totals
  this.subtotal = this.items.reduce((sum, item) => sum + item.total, 0);
  this.taxAmount = (this.subtotal - this.discount) * (this.taxRate / 100);
  this.total = this.subtotal - this.discount + this.taxAmount + this.shippingCost;
  
  // Update remaining amount
  this.payment.remainingAmount = this.total - this.payment.paidAmount;
  
  this.updatedAt = Date.now();
  next();
});

// Check if invoice is overdue
InvoiceSchema.methods.isOverdue = function() {
  if (this.status === 'paid' || this.status === 'cancelled') {
    return false;
  }
  
  const now = new Date();
  return now > this.dueDate;
};

// Get days overdue
InvoiceSchema.methods.getDaysOverdue = function() {
  if (!this.isOverdue()) return 0;
  
  const now = new Date();
  const diffTime = Math.abs(now - this.dueDate);
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
};

// Add payment
InvoiceSchema.methods.addPayment = function(amount, method, reference, notes = '') {
  this.payment.payments.push({
    amount,
    method,
    reference,
    notes,
    date: new Date()
  });
  
  this.payment.paidAmount += amount;
  this.payment.remainingAmount = this.total - this.payment.paidAmount;
  
  // Update status
  if (this.payment.remainingAmount <= 0) {
    this.status = 'paid';
    this.paidDate = new Date();
    this.payment.method = method;
  } else if (this.payment.paidAmount > 0) {
    this.status = 'partial';
  }
  
  return this.save();
};

module.exports = mongoose.model('Invoice', InvoiceSchema);
