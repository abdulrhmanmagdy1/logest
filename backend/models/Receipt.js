/**
 * ============================================
 * 📄 Receipt Model - نظام إدهام
 * Delivery confirmation receipt schema
 * ============================================
 */

const mongoose = require('mongoose');

const ReceiptSchema = new mongoose.Schema({
  // Receipt Number
  receiptNumber: {
    type: String,
    required: true,
    unique: true
  },
  
  // References
  shipment: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Shipment',
    required: true
  },
  invoice: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Invoice'
  },
  
  // Receipt Type
  type: {
    type: String,
    enum: ['delivery', 'pickup', 'transfer', 'return'],
    default: 'delivery'
  },
  
  // Delivery Details
  deliveredBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  deliveredAt: {
    type: Date,
    required: true
  },
  
  // Location
  location: {
    address: String,
    coordinates: {
      lat: Number,
      lng: Number
    }
  },
  
  // Recipient
  recipient: {
    name: {
      type: String,
      required: true
    },
    phone: String,
    idNumber: String,
    relation: String // Relationship to client
  },
  
  // Confirmation
  signature: {
    imageUrl: String,
    timestamp: Date
  },
  
  // Cargo Condition
  condition: {
    type: String,
    enum: ['excellent', 'good', 'fair', 'damaged'],
    required: true
  },
  conditionNotes: String,
  
  // Photos
  photos: [{
    url: String,
    type: {
      type: String,
      enum: ['cargo', 'signature', 'location', 'condition', 'other']
    },
    timestamp: {
      type: Date,
      default: Date.now
    }
  }],
  
  // Items Verification
  items: [{
    description: String,
    expectedQuantity: Number,
    actualQuantity: Number,
    condition: {
      type: String,
      enum: ['good', 'damaged', 'missing']
    },
    notes: String
  }],
  
  // Temperature Log (for refrigerated cargo)
  temperatureLog: [{
    timestamp: Date,
    temperature: Number,
    location: {
      lat: Number,
      lng: Number
    }
  }],
  
  // Notes
  driverNotes: String,
  recipientNotes: String,
  
  // Verification
  verifiedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  verifiedAt: Date,
  
  // Status
  status: {
    type: String,
    enum: ['pending', 'verified', 'disputed', 'resolved'],
    default: 'pending'
  },
  
  // Dispute (if any)
  dispute: {
    raised: {
      type: Boolean,
      default: false
    },
    raisedAt: Date,
    reason: String,
    description: String,
    resolvedAt: Date,
    resolution: String,
    resolvedBy: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    }
  },
  
  // PDF Document
  pdfUrl: String,
  
  // Metadata
  createdBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
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
ReceiptSchema.index({ receiptNumber: 1 });
ReceiptSchema.index({ shipment: 1 });
ReceiptSchema.index({ deliveredBy: 1 });
ReceiptSchema.index({ deliveredAt: -1 });

// Generate receipt number
ReceiptSchema.pre('save', async function(next) {
  if (!this.receiptNumber) {
    const date = new Date();
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    
    // Get count of receipts this month
    const count = await this.constructor.countDocuments({
      createdAt: {
        $gte: new Date(year, date.getMonth(), 1),
        $lt: new Date(year, date.getMonth() + 1, 1)
      }
    });
    
    const sequence = String(count + 1).padStart(4, '0');
    this.receiptNumber = `RCP-${year}${month}-${sequence}`;
  }
  
  this.updatedAt = Date.now();
  next();
});

module.exports = mongoose.model('Receipt', ReceiptSchema);
