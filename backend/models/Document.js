//
/**
 * ============================================
 * 📄 Document Model - نظام إدارة المستندات
 * ============================================
 */

const mongoose = require('mongoose');

const DocumentSchema = new mongoose.Schema({
  // Document Info
  title: {
    type: String,
    required: true
  },
  description: String,
  
  // Document Type
  type: {
    type: String,
    enum: [
      'contract',
      'invoice',
      'receipt',
      'shipment_proof',
      'delivery_proof',
      'driver_license',
      'truck_registration',
      'insurance',
      'maintenance_record',
      'inspection_report',
      'other'
    ],
    required: true
  },
  
  // File Info
  file: {
    originalName: String,
    fileName: String,
    path: String,
    url: String,
    mimeType: String,
    size: Number,
    extension: String
  },
  
  // Related To
  relatedTo: {
    model: {
      type: String,
      enum: ['User', 'Shipment', 'Truck', 'Invoice', 'Maintenance', 'Contract']
    },
    id: mongoose.Schema.Types.ObjectId
  },
  
  // E-Signature
  signatures: [{
    signer: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    name: String,
    role: String,
    signature: {
      type: String, // Base64 signature image or text
      ip: String,
      userAgent: String
    },
    signedAt: {
      type: Date,
      default: Date.now
    },
    status: {
      type: String,
      enum: ['pending', 'signed', 'rejected'],
      default: 'pending'
    }
  }],
  
  // Signature Workflow
  signatureWorkflow: {
    enabled: {
      type: Boolean,
      default: false
    },
    requiredSigners: [{
      user: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User'
      },
      role: String,
      order: Number
    }],
    completedAt: Date,
    expiresAt: Date
  },
  
  // OCR Data (if applicable)
  ocrData: {
    extractedText: String,
    extractedFields: mongoose.Schema.Types.Mixed,
    confidence: Number,
    processedAt: Date
  },
  
  // QR Code / Barcode
  barcode: {
    type: {
      type: String,
      enum: ['qr', 'barcode', 'datamatrix']
    },
    value: String,
    imageUrl: String
  },
  
  // Access Control
  access: {
    public: {
      type: Boolean,
      default: false
    },
    allowedUsers: [{
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    }],
    allowedRoles: [{
      type: String,
      enum: ['admin', 'supervisor', 'accountant', 'driver', 'client', 'workshop']
    }]
  },
  
  // Version Control
  version: {
    current: {
      type: Number,
      default: 1
    },
    history: [{
      version: Number,
      fileName: String,
      path: String,
      updatedBy: {
        type: mongoose.Schema.Types.ObjectId,
        ref: 'User'
      },
      updatedAt: Date,
      reason: String
    }]
  },
  
  // Status
  status: {
    type: String,
    enum: ['draft', 'active', 'archived', 'expired', 'deleted'],
    default: 'active'
  },
  
  // Tags
  tags: [String],
  
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
DocumentSchema.index({ type: 1, status: 1 });
DocumentSchema.index({ 'relatedTo.model': 1, 'relatedTo.id': 1 });
DocumentSchema.index({ 'signatures.signer': 1 });
DocumentSchema.index({ tags: 1 });

// Pre-save
DocumentSchema.pre('save', function(next) {
  this.updatedAt = Date.now();
  next();
});

// Methods
DocumentSchema.methods.addSignature = async function(signerId, signatureData, ip, userAgent) {
  const signer = this.signatures.find(s => s.signer.toString() === signerId);
  
  if (signer) {
    signer.signature = signatureData;
    signer.ip = ip;
    signer.userAgent = userAgent;
    signer.status = 'signed';
    signer.signedAt = new Date();
  } else {
    this.signatures.push({
      signer: signerId,
      signature: signatureData,
      ip,
      userAgent,
      status: 'signed',
      signedAt: new Date()
    });
  }
  
  // Check if all required signers have signed
  if (this.signatureWorkflow.enabled) {
    const allSigned = this.signatureWorkflow.requiredSigners.every(required => {
      const sig = this.signatures.find(s => s.signer.toString() === required.user.toString());
      return sig && sig.status === 'signed';
    });
    
    if (allSigned) {
      this.signatureWorkflow.completedAt = new Date();
    }
  }
  
  return await this.save();
};

DocumentSchema.methods.isAccessibleBy = function(userId, userRole) {
  if (this.access.public) return true;
  if (this.access.allowedUsers.includes(userId)) return true;
  if (this.access.allowedRoles.includes(userRole)) return true;
  return false;
};

module.exports = mongoose.model('Document', DocumentSchema);
