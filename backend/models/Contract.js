//
/**
 * ============================================
 * 📑 Contract Management - إدارة العقود
 * ============================================
 */

const mongoose = require('mongoose');

const ContractSchema = new mongoose.Schema({
  contractNumber: {
    type: String,
    required: true,
    unique: true
  },
  title: {
    type: String,
    required: true
  },
  type: {
    type: String,
    enum: ['service', 'transportation', 'leasing', 'purchase', 'maintenance', 'insurance', 'partnership', 'employment'],
    required: true
  },
  parties: {
    firstParty: {
      name: { type: String, required: true },
      company: { type: mongoose.Schema.Types.ObjectId, ref: 'Company' },
      representative: String,
      contact: {
        email: String,
        phone: String,
        address: String
      }
    },
    secondParty: {
      name: { type: String, required: true },
      company: { type: mongoose.Schema.Types.ObjectId, ref: 'Company' },
      representative: String,
      contact: {
        email: String,
        phone: String,
        address: String
      }
    }
  },
  effectiveDate: {
    type: Date,
    required: true
  },
  expiryDate: {
    type: Date,
    required: true
  },
  duration: {
    value: Number,
    unit: { type: String, enum: ['days', 'months', 'years'] }
  },
  value: {
    amount: Number,
    currency: { type: String, default: 'SAR' },
    paymentTerms: String
  },
  scope: {
    description: { type: String, required: true },
    services: [String],
    exclusions: [String],
    deliverables: [String]
  },
  terms: {
    renewal: {
      autoRenew: { type: Boolean, default: false },
      renewalPeriod: String,
      noticePeriod: Number // days
    },
    termination: {
      terminationClause: String,
      noticePeriod: Number, // days
      penalties: String
    },
    liability: {
      limit: Number,
      insurance: Boolean
    },
    confidentiality: {
      ndaRequired: Boolean,
      duration: String
    },
    disputeResolution: {
      method: { type: String, enum: ['negotiation', 'mediation', 'arbitration', 'litigation'] },
      jurisdiction: String
    }
  },
  attachments: [{
    name: String,
    fileUrl: String,
    uploadedAt: { type: Date, default: Date.now }
  }],
  status: {
    type: String,
    enum: ['draft', 'under_review', 'pending_signature', 'active', 'expired', 'terminated', 'renewed', 'cancelled'],
    default: 'draft'
  },
  signatures: {
    firstParty: {
      signedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
      signedAt: Date,
      ipAddress: String
    },
    secondParty: {
      signedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
      signedAt: Date,
      ipAddress: String
    }
  },
  approvalChain: [{
    approver: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    role: String,
    status: { type: String, enum: ['pending', 'approved', 'rejected'] },
    comments: String,
    actionDate: Date
  }],
  amendments: [{
    amendmentNumber: String,
    description: String,
    effectiveDate: Date,
    approvedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    attachments: [String],
    createdAt: { type: Date, default: Date.now }
  }],
  reminders: [{
    type: { type: String, enum: ['expiry', 'payment', 'review', 'renewal'] },
    date: Date,
    sent: { type: Boolean, default: false }
  }],
  createdBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
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

ContractSchema.index({ company: 1, status: 1 });
ContractSchema.index({ contractNumber: 1 });
ContractSchema.index({ expiryDate: 1 });

ContractSchema.methods.isExpired = function() {
  return new Date() > this.expiryDate;
};

ContractSchema.methods.daysUntilExpiry = function() {
  const diff = this.expiryDate - new Date();
  return Math.ceil(diff / (1000 * 60 * 60 * 24));
};

module.exports = mongoose.model('Contract', ContractSchema);

// Tender Schema
const TenderSchema = new mongoose.Schema({
  tenderNumber: {
    type: String,
    required: true,
    unique: true
  },
  title: {
    type: String,
    required: true
  },
  description: String,
  type: {
    type: String,
    enum: ['open', 'limited', 'negotiated', 'emergency', 'framework'],
    default: 'open'
  },
  category: {
    type: String,
    enum: ['transportation', 'warehousing', 'packaging', 'fuel', 'equipment', 'technology', 'services']
  },
  estimatedValue: {
    min: Number,
    max: Number,
    currency: { type: String, default: 'SAR' }
  },
  duration: {
    startDate: Date,
    endDate: Date
  },
  requirements: {
    technical: [String],
    commercial: [String],
    financial: [String],
    documents: [String]
  },
  evaluationCriteria: [{
    name: String,
    weight: Number,
    description: String
  }],
  timeline: {
    publicationDate: Date,
    preBidMeeting: Date,
    siteVisit: Date,
    clarificationDeadline: Date,
    submissionDeadline: { type: Date, required: true },
    openingDate: Date,
    evaluationPeriod: Number, // days
    awardDate: Date,
    contractSigning: Date
  },
  attachments: [{
    name: String,
    fileUrl: String,
    type: String
  }],
  status: {
    type: String,
    enum: ['draft', 'published', 'under_evaluation', 'awarded', 'cancelled', 'on_hold'],
    default: 'draft'
  },
  bids: [{
    bidder: { type: mongoose.Schema.Types.ObjectId, ref: 'Company' },
    amount: Number,
    currency: { type: String, default: 'SAR' },
    validityPeriod: Number, // days
    technicalProposal: String,
    commercialProposal: String,
    attachments: [String],
    submittedAt: Date,
    status: {
      type: String,
      enum: ['submitted', 'under_review', 'accepted', 'rejected'],
      default: 'submitted'
    },
    score: {
      technical: Number,
      commercial: Number,
      financial: Number,
      total: Number
    }
  }],
  awardedTo: {
    company: { type: mongoose.Schema.Types.ObjectId, ref: 'Company' },
    amount: Number,
    awardedAt: Date
  },
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
  }
});

TenderSchema.index({ company: 1, status: 1 });
TenderSchema.index({ submissionDeadline: 1 });

module.exports.Tender = mongoose.model('Tender', TenderSchema);
