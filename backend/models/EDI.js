//
/**
 * ============================================
 * 📡 EDI - Electronic Data Interchange
 * ============================================
 */

const mongoose = require('mongoose');

// EDI Transaction Schema
const EDITransactionSchema = new mongoose.Schema({
  transactionId: {
    type: String,
    required: true,
    unique: true
  },
  type: {
    type: String,
    enum: ['850', '855', '856', '810', '812', '820', '830', '832', '840', '943', '944', '945', '997', '999'],
    required: true
  },
  // EDI X12 standard codes:
  // 850 - Purchase Order
  // 855 - PO Acknowledgment
  // 856 - ASN (Advanced Ship Notice)
  // 810 - Invoice
  // 812 - Credit/Debit Adjustment
  // 820 - Payment Order/Remittance
  // 830 - Planning Schedule
  // 832 - Price/Sales Catalog
  // 840 - Request for Quotation
  // 943 - Warehouse Stock Transfer
  // 944 - Warehouse Stock Transfer Receipt
  // 945 - Warehouse Shipping Advice
  // 997 - Functional Acknowledgment
  // 999 - Implementation Acknowledgment
  
  direction: {
    type: String,
    enum: ['incoming', 'outgoing'],
    required: true
  },
  tradingPartner: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'TradingPartner',
    required: true
  },
  referenceNumber: String,
  relatedDocuments: [{
    type: mongoose.Schema.Types.ObjectId,
    refPath: 'documentType'
  }],
  documentType: {
    type: String,
    enum: ['Shipment', 'Invoice', 'PurchaseOrder', 'Warehouse']
  },
  rawData: {
    format: {
      type: String,
      enum: ['X12', 'EDIFACT', 'XML', 'JSON', 'CSV']
    },
    content: String,
    originalFilename: String
  },
  parsedData: mongoose.Schema.Types.Mixed,
  status: {
    type: String,
    enum: ['received', 'parsing', 'validated', 'processing', 'completed', 'error', 'rejected'],
    default: 'received'
  },
  validation: {
    isValid: Boolean,
    errors: [{
      segment: String,
      element: String,
      error: String,
      severity: {
        type: String,
        enum: ['warning', 'error', 'fatal']
      }
    }],
    warnings: [String]
  },
  processing: {
    startedAt: Date,
    completedAt: Date,
    duration: Number, // seconds
    steps: [{
      name: String,
      status: String,
      timestamp: Date,
      details: mongoose.Schema.Types.Mixed
    }]
  },
  acknowledgments: [{
    type: {
      type: String,
      enum: ['997', '999', '855', 'custom']
    },
    reference: String,
    status: {
      type: String,
      enum: ['pending', 'sent', 'received', 'accepted', 'rejected']
    },
    sentAt: Date,
    receivedAt: Date,
    content: String
  }],
  errorDetails: {
    code: String,
    message: String,
    segment: String,
    position: Number,
    stack: String,
    retryCount: { type: Number, default: 0 }
  },
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

EDITransactionSchema.index({ company: 1, type: 1 });
EDITransactionSchema.index({ tradingPartner: 1 });
EDITransactionSchema.index({ status: 1 });

// Trading Partner Schema
const TradingPartnerSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true
  },
  partnerId: {
    type: String,
    required: true,
    unique: true
  },
  type: {
    type: String,
    enum: ['supplier', 'customer', 'carrier', '3pl', 'warehouse', 'broker'],
    required: true
  },
  contact: {
    name: String,
    email: String,
    phone: String,
    technicalContact: {
      name: String,
      email: String,
      phone: String
    }
  },
  edi: {
    standard: {
      type: String,
      enum: ['X12', 'EDIFACT', 'TRADACOMS', 'ODETTE'],
      default: 'X12'
    },
    version: String, // e.g., "004010"
    communication: {
      protocol: {
        type: String,
        enum: ['AS2', 'SFTP', 'FTPS', 'FTP', 'HTTP', 'HTTPS', 'VAN']
      },
      config: {
        // AS2
        as2Id: String,
        as2Url: String,
        certificate: String,
        // SFTP/FTPS/FTP
        host: String,
        port: Number,
        username: String,
        password: String,
        privateKey: String,
        path: String,
        // HTTP/HTTPS
        endpoint: String,
        apiKey: String,
        // VAN
        vanId: String,
        vanProvider: String
      }
    },
    capabilities: [{
      type: String,
      enum: ['850', '855', '856', '810', '812', '820', '830', '832', '840', '943', '944', '945', '997', '999']
    }],
    supportedFormats: [{
      type: String,
      enum: ['X12', 'EDIFACT', 'XML', 'JSON', 'CSV']
    }],
    mappingRules: [{
      transactionType: String,
      inboundMap: String, // path to mapping file
      outboundMap: String,
      customTransformations: [String]
    }]
  },
  security: {
    encryption: {
      enabled: { type: Boolean, default: true },
      algorithm: { type: String, default: 'AES256' },
      certificate: String
    },
    signature: {
      enabled: { type: Boolean, default: true },
      algorithm: { type: String, default: 'SHA256' },
      certificate: String
    },
    mdnRequired: { type: Boolean, default: true },
    mdnSigned: { type: Boolean, default: true }
  },
  testMode: {
    type: Boolean,
    default: true
  },
  status: {
    type: String,
    enum: ['pending', 'testing', 'active', 'suspended', 'inactive'],
    default: 'pending'
  },
  testResults: {
    inboundTests: [{
      transactionType: String,
      status: String,
      testedAt: Date,
      errors: [String]
    }],
    outboundTests: [{
      transactionType: String,
      status: String,
      testedAt: Date,
      errors: [String]
    }],
    certificationDate: Date
  },
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

TradingPartnerSchema.index({ company: 1, partnerId: 1 });

// EDI Mapping Schema
const EDIMappingSchema = new mongoose.Schema({
  name: String,
  version: String,
  standard: {
    type: String,
    enum: ['X12', 'EDIFACT']
  },
  transactionType: String,
  direction: {
    type: String,
    enum: ['inbound', 'outbound']
  },
  sourceFormat: String,
  targetFormat: String,
  segments: [{
    segmentId: String,
    name: String,
    description: String,
    required: Boolean,
    maxOccurrences: Number,
    elements: [{
      position: Number,
      name: String,
      description: String,
      dataType: {
        type: String,
        enum: ['string', 'number', 'date', 'time', 'decimal', 'integer']
      },
      minLength: Number,
      maxLength: Number,
      required: Boolean,
      defaultValue: String,
      validation: {
        pattern: String,
        values: [String]
      },
      mapTo: String // field in target system
    }]
  }],
  transformations: [{
    name: String,
    condition: String,
    action: String,
    targetField: String,
    sourceField: String
  }],
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company'
  }
});

module.exports = {
  EDITransaction: mongoose.model('EDITransaction', EDITransactionSchema),
  TradingPartner: mongoose.model('TradingPartner', TradingPartnerSchema),
  EDIMapping: mongoose.model('EDIMapping', EDIMappingSchema)
};
