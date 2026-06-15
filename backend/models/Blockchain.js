//
/**
 * ============================================
 * ⛓️ Blockchain - سلسلة الكتل للشفافية
 * ============================================
 */

const mongoose = require('mongoose');

// Blockchain Transaction Schema
const BlockchainTransactionSchema = new mongoose.Schema({
  transactionHash: {
    type: String,
    required: true,
    unique: true
  },
  blockNumber: Number,
  blockHash: String,
  timestamp: {
    type: Date,
    default: Date.now
  },
  type: {
    type: String,
    enum: ['shipment_created', 'shipment_status_update', 'delivery_confirmed', 'document_verified', 'payment_made', 'contract_signed', 'transfer_ownership', 'custom'],
    required: true
  },
  entityType: {
    type: String,
    enum: ['Shipment', 'Invoice', 'Contract', 'Document', 'Certificate'],
    required: true
  },
  entityId: {
    type: mongoose.Schema.Types.ObjectId,
    required: true
  },
  entityReference: String, // External reference like tracking number
  from: {
    address: String, // Blockchain address
    entity: {
      type: mongoose.Schema.Types.ObjectId,
      refPath: 'entityModel'
    },
    entityModel: {
      type: String,
      enum: ['User', 'Company', 'Driver', 'Customer']
    }
  },
  to: {
    address: String,
    entity: {
      type: mongoose.Schema.Types.ObjectId,
      refPath: 'entityModel'
    },
    entityModel: {
      type: String,
      enum: ['User', 'Company', 'Driver', 'Customer']
    }
  },
  data: {
    action: String,
    previousState: mongoose.Schema.Types.Mixed,
    newState: mongoose.Schema.Types.Mixed,
    metadata: mongoose.Schema.Types.Mixed,
    ipfsHash: String, // IPFS hash for large data
    documentHash: String, // Hash of attached document
    signatures: [{
      signer: String,
      signature: String,
      timestamp: Date
    }]
  },
  smartContract: {
    address: String,
    name: String,
    method: String,
    params: mongoose.Schema.Types.Mixed,
    events: [{
      name: String,
      data: mongoose.Schema.Types.Mixed
    }]
  },
  network: {
    type: String,
    enum: ['ethereum', 'polygon', 'hyperledger', 'quorum', 'stellar', 'custom'],
    default: 'ethereum'
  },
  gas: {
    used: Number,
    price: Number,
    total: Number
  },
  confirmations: {
    type: Number,
    default: 0
  },
  status: {
    type: String,
    enum: ['pending', 'confirmed', 'failed', 'orphaned'],
    default: 'pending'
  },
  verification: {
    isVerified: { type: Boolean, default: false },
    verifiedAt: Date,
    verifiedBy: String,
    merkleRoot: String,
    proof: [String]
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

BlockchainTransactionSchema.index({ company: 1, type: 1 });
BlockchainTransactionSchema.index({ transactionHash: 1 });
BlockchainTransactionSchema.index({ entityId: 1 });
BlockchainTransactionSchema.index({ 'from.address': 1 });

// Smart Contract Schema
const SmartContractSchema = new mongoose.Schema({
  name: String,
  description: String,
  address: {
    type: String,
    required: true
  },
  network: {
    type: String,
    enum: ['ethereum', 'polygon', 'hyperledger', 'quorum', 'stellar', 'custom']
  },
  abi: mongoose.Schema.Types.Mixed, // Contract ABI
  bytecode: String,
  version: String,
  type: {
    type: String,
    enum: ['shipment_tracking', 'payment_escrow', 'document_registry', 'supply_chain', 'custom']
  },
  deployment: {
    txHash: String,
    blockNumber: Number,
    deployedAt: Date,
    deployedBy: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    }
  },
  functions: [{
    name: String,
    type: { type: String, enum: ['read', 'write'] },
    inputs: [{
      name: String,
      type: String
    }],
    outputs: [{
      name: String,
      type: String
    }]
  }],
  events: [{
    name: String,
    signature: String,
    parameters: [{
      name: String,
      type: String,
      indexed: Boolean
    }]
  }],
  status: {
    type: String,
    enum: ['draft', 'deployed', 'paused', 'deprecated'],
    default: 'draft'
  },
  usage: {
    totalCalls: { type: Number, default: 0 },
    lastCallAt: Date
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

// Wallet Schema
const BlockchainWalletSchema = new mongoose.Schema({
  address: {
    type: String,
    required: true,
    unique: true
  },
  type: {
    type: String,
    enum: ['company', 'user', 'contract', 'system'],
    required: true
  },
  owner: {
    entity: {
      type: mongoose.Schema.Types.ObjectId,
      refPath: 'ownerModel'
    },
    model: {
      type: String,
      enum: ['User', 'Company', 'Driver']
    }
  },
  network: {
    type: String,
    enum: ['ethereum', 'polygon', 'hyperledger', 'quorum', 'stellar', 'custom']
  },
  balance: {
    native: { type: Number, default: 0 },
    tokens: [{
      symbol: String,
      address: String,
      balance: Number,
      decimals: Number
    }]
  },
  keys: {
    // Encrypted keys stored securely
    encryptedPrivateKey: String,
    publicKey: String,
    mnemonic: String
  },
  permissions: {
    canSend: { type: Boolean, default: true },
    canReceive: { type: Boolean, default: true },
    maxTransaction: Number,
    dailyLimit: Number,
    allowedContracts: [String]
  },
  transactions: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'BlockchainTransaction'
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

// Supply Chain Event (for tracking)
const SupplyChainEventSchema = new mongoose.Schema({
  eventId: {
    type: String,
    required: true,
    unique: true
  },
  shipment: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Shipment',
    required: true
  },
  eventType: {
    type: String,
    enum: ['pickup', 'departure', 'arrival', 'custom_clearance', 'inspection', 'transfer', 'delivery', 'signature', 'exception'],
    required: true
  },
  location: {
    name: String,
    address: String,
    coordinates: {
      lat: Number,
      lng: Number
    }
  },
  timestamp: {
    type: Date,
    required: true
  },
  actor: {
    type: mongoose.Schema.Types.ObjectId,
    refPath: 'actorModel'
  },
  actorModel: {
    type: String,
    enum: ['User', 'Driver', 'Company']
  },
  actorName: String,
  evidence: {
    photos: [String],
    documents: [String],
    signatures: [String],
    iotData: mongoose.Schema.Types.ObjectId
  },
  blockchain: {
    transactionHash: String,
    verified: Boolean,
    verifiedAt: Date
  },
  previousEvent: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'SupplyChainEvent'
  },
  nextEvent: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'SupplyChainEvent'
  },
  metadata: mongoose.Schema.Types.Mixed,
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

SupplyChainEventSchema.index({ shipment: 1, timestamp: -1 });
SupplyChainEventSchema.index({ blockchain: 1 });

module.exports = {
  BlockchainTransaction: mongoose.models.BlockchainTransaction || mongoose.model('BlockchainTransaction', BlockchainTransactionSchema),
  SmartContract: mongoose.models.SmartContract || mongoose.model('SmartContract', SmartContractSchema),
  BlockchainWallet: mongoose.models.BlockchainWallet || mongoose.model('BlockchainWallet', BlockchainWalletSchema),
  SupplyChainEvent: mongoose.models.SupplyChainEvent || mongoose.model('SupplyChainEvent', SupplyChainEventSchema)
};
