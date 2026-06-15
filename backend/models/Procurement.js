//
/**
 * ============================================
 * 🛒 Procurement Management - إدارة المشتريات
 * ============================================
 */

const mongoose = require('mongoose');

// Purchase Requisition Schema
const PurchaseRequisitionSchema = new mongoose.Schema({
  prNumber: {
    type: String,
    required: true,
    unique: true
  },
  title: {
    type: String,
    required: true
  },
  description: String,
  department: {
    type: String,
    enum: ['operations', 'maintenance', 'warehouse', 'it', 'hr', 'finance', 'admin', 'fleet'],
    required: true
  },
  category: {
    type: String,
    enum: ['equipment', 'spare_parts', 'fuel', 'services', 'software', 'office_supplies', 'uniform', 'safety', 'other'],
    required: true
  },
  priority: {
    type: String,
    enum: ['low', 'medium', 'high', 'urgent'],
    default: 'medium'
  },
  items: [{
    itemCode: String,
    description: { type: String, required: true },
    specifications: String,
    quantity: { type: Number, required: true },
    unitOfMeasure: String,
    estimatedUnitPrice: Number,
    estimatedTotal: Number,
    requiredBy: Date,
    notes: String
  }],
  totalEstimatedValue: Number,
  currency: { type: String, default: 'SAR' },
  justification: String,
  requestedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  requestDate: {
    type: Date,
    default: Date.now
  },
  requiredDate: Date,
  approvalFlow: [{
    level: Number,
    approver: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    role: String,
    status: {
      type: String,
      enum: ['pending', 'approved', 'rejected', 'delegated'],
      default: 'pending'
    },
    comments: String,
    actionDate: Date
  }],
  status: {
    type: String,
    enum: ['draft', 'submitted', 'under_review', 'approved', 'rejected', 'converted_to_po', 'cancelled'],
    default: 'draft'
  },
  convertedPO: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'PurchaseOrder'
  },
  attachments: [String],
  notes: String,
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
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

PurchaseRequisitionSchema.index({ company: 1, status: 1 });
PurchaseRequisitionSchema.index({ department: 1 });

// Purchase Order Schema
const PurchaseOrderSchema = new mongoose.Schema({
  poNumber: {
    type: String,
    required: true,
    unique: true
  },
  poType: {
    type: String,
    enum: ['standard', 'blanket', 'framework', 'emergency', 'service'],
    default: 'standard'
  },
  requisition: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'PurchaseRequisition'
  },
  vendor: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Vendor',
    required: true
  },
  vendorContact: String,
  items: [{
    itemCode: String,
    description: { type: String, required: true },
    specifications: String,
    quantity: { type: Number, required: true },
    unitOfMeasure: String,
    unitPrice: Number,
    discount: {
      percentage: Number,
      amount: Number
    },
    tax: {
      percentage: Number,
      amount: Number
    },
    total: Number,
    deliveryDate: Date,
    receivedQuantity: { type: Number, default: 0 },
    status: {
      type: String,
      enum: ['pending', 'partial', 'received', 'rejected'],
      default: 'pending'
    }
  }],
  totals: {
    subtotal: Number,
    discount: Number,
    tax: Number,
    shipping: Number,
    other: Number,
    total: Number
  },
  currency: { type: String, default: 'SAR' },
  paymentTerms: {
    method: {
      type: String,
      enum: ['credit_card', 'bank_transfer', 'cash', 'cheque', 'credit_30', 'credit_60', 'credit_90'],
      default: 'credit_30'
    },
    advancePercentage: Number,
    installments: [{
      percentage: Number,
      dueDate: Date,
      amount: Number,
      paid: { type: Boolean, default: false }
    }]
  },
  delivery: {
    terms: {
      type: String,
      enum: ['ex_works', 'fob', 'cif', 'ddp', 'dap', 'ddu']
    },
    address: String,
    warehouse: { type: mongoose.Schema.Types.ObjectId, ref: 'Warehouse' },
    requiredDate: Date,
    partialShipment: { type: Boolean, default: false }
  },
  incoterms: String,
  status: {
    type: String,
    enum: ['draft', 'sent', 'acknowledged', 'confirmed', 'partially_received', 'received', 'cancelled', 'closed'],
    default: 'draft'
  },
  approvals: [{
    approver: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    role: String,
    status: String,
    comments: String,
    date: Date
  }],
  receipt: {
    receivedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    receivedDate: Date,
    inspectionResult: {
      type: String,
      enum: ['accepted', 'rejected', 'partial']
    },
    notes: String
  },
  invoices: [{
    invoiceNumber: String,
    invoiceDate: Date,
    amount: Number,
    status: {
      type: String,
      enum: ['pending', 'verified', 'paid', 'disputed']
    },
    paymentDate: Date
  }],
  notes: String,
  termsAndConditions: String,
  attachments: [String],
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  createdBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
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

PurchaseOrderSchema.index({ company: 1, status: 1 });
PurchaseOrderSchema.index({ vendor: 1 });
PurchaseOrderSchema.index({ 'delivery.requiredDate': 1 });

// Vendor Schema
const VendorSchema = new mongoose.Schema({
  vendorCode: {
    type: String,
    required: true,
    unique: true
  },
  name: {
    type: String,
    required: true
  },
  legalName: String,
  type: {
    type: String,
    enum: ['manufacturer', 'distributor', 'wholesaler', 'retailer', 'service_provider', 'contractor', 'consultant'],
    required: true
  },
  category: [{
    type: String,
    enum: ['equipment', 'spare_parts', 'fuel', 'tires', 'services', 'software', 'office_supplies', 'maintenance', 'insurance', 'leasing']
  }],
  contact: {
    primaryContact: String,
    email: String,
    phone: String,
    mobile: String,
    fax: String,
    website: String
  },
  address: {
    street: String,
    city: String,
    state: String,
    postalCode: String,
    country: { type: String, default: 'SA' }
  },
  taxInfo: {
    taxId: String,
    vatNumber: String,
    taxCertificate: String
  },
  banking: {
    bankName: String,
    accountNumber: String,
    iban: String,
    swiftCode: String
  },
  registration: {
    crNumber: String, // Commercial Registration
    chamberOfCommerce: String,
    registeredAt: Date,
    expiryDate: Date
  },
  classification: {
    status: {
      type: String,
      enum: ['active', 'inactive', 'suspended', 'blacklisted'],
      default: 'active'
    },
    riskLevel: {
      type: String,
      enum: ['low', 'medium', 'high'],
      default: 'medium'
    },
    tier: {
      type: String,
      enum: ['preferred', 'approved', 'provisional', 'pending'],
      default: 'pending'
    }
  },
  evaluation: {
    overallScore: { type: Number, min: 0, max: 100 },
    qualityScore: { type: Number, min: 0, max: 100 },
    deliveryScore: { type: Number, min: 0, max: 100 },
    priceScore: { type: Number, min: 0, max: 100 },
    serviceScore: { type: Number, min: 0, max: 100 },
    lastEvaluation: Date,
    nextEvaluation: Date
  },
  contracts: [{
    contractNumber: String,
    startDate: Date,
    endDate: Date,
    value: Number,
    currency: { type: String, default: 'SAR' }
  }],
  performance: {
    totalOrders: { type: Number, default: 0 },
    totalSpend: { type: Number, default: 0 },
    onTimeDelivery: { type: Number, default: 0 }, // percentage
    qualityAcceptance: { type: Number, default: 0 }, // percentage
    disputes: { type: Number, default: 0 },
    averageLeadTime: { type: Number, default: 0 }
  },
  certifications: [{
    name: String,
    issuedBy: String,
    issuedDate: Date,
    expiryDate: Date,
    documentUrl: String
  }],
  documents: [{
    type: String,
    name: String,
    fileUrl: String,
    uploadedAt: Date
  }],
  notes: String,
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  createdBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
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

VendorSchema.index({ company: 1, 'classification.status': 1 });
VendorSchema.index({ vendorCode: 1 });

// Quote/RFQ Schema
const QuoteSchema = new mongoose.Schema({
  rfqNumber: {
    type: String,
    required: true,
    unique: true
  },
  title: String,
  description: String,
  category: String,
  items: [{
    description: String,
    specifications: String,
    quantity: Number,
    unitOfMeasure: String
  }],
  vendors: [{
    vendor: { type: mongoose.Schema.Types.ObjectId, ref: 'Vendor' },
    invitedAt: Date,
    quoteReceived: { type: Boolean, default: false },
    quoteAmount: Number,
    currency: { type: String, default: 'SAR' },
    deliveryTime: Number, // days
    validityDate: Date,
    terms: String,
    attachments: [String],
    selected: { type: Boolean, default: false }
  }],
  deadline: Date,
  status: {
    type: String,
    enum: ['draft', 'sent', 'quotes_received', 'under_evaluation', 'awarded', 'cancelled'],
    default: 'draft'
  },
  evaluationCriteria: [{
    name: String,
    weight: Number
  }],
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  createdAt: {
    type: Date,
    default: Date.now
  }
});

module.exports = {
  PurchaseRequisition: mongoose.model('PurchaseRequisition', PurchaseRequisitionSchema),
  PurchaseOrder: mongoose.model('PurchaseOrder', PurchaseOrderSchema),
  Vendor: mongoose.model('Vendor', VendorSchema),
  Quote: mongoose.model('Quote', QuoteSchema)
};
