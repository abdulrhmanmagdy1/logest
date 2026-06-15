//
/**
 * ============================================
 * 👤 Customer Portal - بوابة العملاء
 * ============================================
 */

const mongoose = require('mongoose');

// Portal User Schema (Customer-specific user data)
const PortalUserSchema = new mongoose.Schema({
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true,
    unique: true
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  portalRole: {
    type: String,
    enum: ['admin', 'manager', 'user', 'viewer'],
    default: 'user'
  },
  permissions: {
    canCreateShipments: { type: Boolean, default: true },
    canViewInvoices: { type: Boolean, default: true },
    canPayOnline: { type: Boolean, default: true },
    canViewReports: { type: Boolean, default: true },
    canManageUsers: { type: Boolean, default: false },
    canManageAddresses: { type: Boolean, default: true },
    canRequestQuotes: { type: Boolean, default: true },
    canAccessApi: { type: Boolean, default: false }
  },
  preferences: {
    language: { type: String, default: 'en' },
    timezone: { type: String, default: 'Asia/Riyadh' },
    currency: { type: String, default: 'SAR' },
    notifications: {
      email: { type: Boolean, default: true },
      sms: { type: Boolean, default: false },
      push: { type: Boolean, default: true }
    },
    dashboard: {
      widgets: [String],
      layout: mongoose.Schema.Types.Mixed
    }
  },
  access: {
    lastLogin: Date,
    lastLoginIp: String,
    loginCount: { type: Number, default: 0 },
    failedLogins: { type: Number, default: 0 },
    lockedUntil: Date,
    apiKey: String,
    apiSecret: String
  },
  savedAddresses: [{
    name: String,
    type: { type: String, enum: ['pickup', 'delivery', 'billing'] },
    isDefault: Boolean,
    address: {
      street: String,
      city: String,
      state: String,
      postalCode: String,
      country: { type: String, default: 'SA' },
      coordinates: {
        lat: Number,
        lng: Number
      }
    },
    contact: {
      name: String,
      phone: String,
      email: String,
      instructions: String
    }
  }],
  savedItems: [{
    name: String,
    description: String,
    category: String,
    weight: Number,
    dimensions: {
      length: Number,
      width: Number,
      height: Number
    },
    value: Number,
    hsCode: String
  }],
  apiUsage: {
    monthlyCalls: { type: Number, default: 0 },
    totalCalls: { type: Number, default: 0 },
    lastCallAt: Date,
    rateLimit: { type: Number, default: 1000 }
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

// Saved Quote Schema
const SavedQuoteSchema = new mongoose.Schema({
  quoteId: {
    type: String,
    required: true,
    unique: true
  },
  customer: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  shipmentDetails: {
    origin: {
      country: String,
      city: String,
      postalCode: String
    },
    destination: {
      country: String,
      city: String,
      postalCode: String
    },
    weight: Number,
    dimensions: {
      length: Number,
      width: Number,
      height: Number
    },
    packages: Number,
    serviceType: String,
    declaredValue: Number
  },
  quotes: [{
    service: String,
    carrier: String,
    transitTime: String,
    price: {
      amount: Number,
      currency: String
    },
    features: [String],
    validUntil: Date
  }],
  selectedQuote: Number, // index of selected quote
  status: {
    type: String,
    enum: ['pending', 'selected', 'expired', 'converted'],
    default: 'pending'
  },
  convertedToShipment: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Shipment'
  },
  expiresAt: Date,
  createdAt: {
    type: Date,
    default: Date.now
  }
});

// Shipment Template Schema
const ShipmentTemplateSchema = new mongoose.Schema({
  name: String,
  description: String,
  customer: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  template: {
    origin: {
      name: String,
      address: mongoose.Schema.Types.Mixed,
      contact: mongoose.Schema.Types.Mixed
    },
    destination: {
      name: String,
      address: mongoose.Schema.Types.Mixed,
      contact: mongoose.Schema.Types.Mixed
    },
    serviceType: String,
    packageType: String,
    weight: Number,
    dimensions: mongoose.Schema.Types.Mixed,
    declaredValue: Number,
    insurance: Boolean,
    specialInstructions: String,
    pickupInstructions: String,
    deliveryInstructions: String
  },
  usage: {
    count: { type: Number, default: 0 },
    lastUsed: Date
  },
  isActive: { type: Boolean, default: true },
  createdAt: {
    type: Date,
    default: Date.now
  }
});

// Customer Activity Log Schema
const CustomerActivitySchema = new mongoose.Schema({
  customer: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  activity: {
    type: {
      type: String,
      enum: ['login', 'logout', 'shipment_created', 'shipment_viewed', 'invoice_paid', 'quote_requested', 'address_saved', 'settings_changed', 'api_call', 'download_report']
    },
    details: mongoose.Schema.Types.Mixed,
    ip: String,
    userAgent: String
  },
  timestamp: {
    type: Date,
    default: Date.now
  }
});

CustomerActivitySchema.index({ customer: 1, timestamp: -1 });

// Support Ticket Schema (Customer-specific)
const CustomerTicketSchema = new mongoose.Schema({
  ticketId: {
    type: String,
    required: true,
    unique: true
  },
  customer: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  relatedShipment: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Shipment'
  },
  category: {
    type: String,
    enum: ['shipment_inquiry', 'billing', 'technical', 'complaint', 'suggestion', 'other']
  },
  priority: {
    type: String,
    enum: ['low', 'medium', 'high', 'urgent'],
    default: 'medium'
  },
  subject: String,
  description: String,
  status: {
    type: String,
    enum: ['open', 'in_progress', 'waiting_customer', 'resolved', 'closed'],
    default: 'open'
  },
  messages: [{
    sender: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    senderType: {
      type: String,
      enum: ['customer', 'agent', 'system']
    },
    message: String,
    attachments: [String],
    timestamp: {
      type: Date,
      default: Date.now
    }
  }],
  assignedTo: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  resolution: {
    resolvedAt: Date,
    resolvedBy: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    solution: String,
    satisfaction: Number // 1-5 rating
  },
  sla: {
    responseDeadline: Date,
    resolutionDeadline: Date,
    firstResponseAt: Date,
    breached: { type: Boolean, default: false }
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

module.exports = {
  PortalUser: mongoose.model('PortalUser', PortalUserSchema),
  SavedQuote: mongoose.model('SavedQuote', SavedQuoteSchema),
  ShipmentTemplate: mongoose.model('ShipmentTemplate', ShipmentTemplateSchema),
  CustomerActivity: mongoose.model('CustomerActivity', CustomerActivitySchema),
  CustomerTicket: mongoose.model('CustomerTicket', CustomerTicketSchema)
};
