/**
 * ============================================
 * 👥 Client Model - نظام إدهام الاحترافي
 * Edham Logistics - Advanced Client Management
 * ============================================
 */

const mongoose = require('mongoose');

const clientSchema = new mongoose.Schema({
  // Basic Information
  name: {
    type: String,
    required: true,
    trim: true,
    maxlength: 100
  },
  
  email: {
    type: String,
    required: true,
    unique: true,
    lowercase: true,
    trim: true
  },
  
  phone: {
    type: String,
    required: true,
    trim: true
  },
  
  // Company Information
  company: {
    name: {
      type: String,
      trim: true,
      maxlength: 200
    },
    industry: {
      type: String,
      enum: [
        'manufacturing',
        'retail',
        'construction',
        'healthcare',
        'education',
        'technology',
        'logistics',
        'food_beverage',
        'automotive',
        'pharmaceutical',
        'telecommunications',
        'other'
      ]
    },
    size: {
      type: String,
      enum: ['startup', 'small', 'medium', 'large', 'enterprise']
    },
    registrationNumber: {
      type: String,
      trim: true
    },
    taxNumber: {
      type: String,
      trim: true
    },
    website: {
      type: String,
      trim: true
    },
    description: {
      type: String,
      maxlength: 1000
    }
  },
  
  // Address Information
  addresses: [{
    type: {
      type: String,
      enum: ['main', 'billing', 'shipping', 'branch'],
      default: 'main'
    },
    street: {
      type: String,
      required: true,
      trim: true
    },
    city: {
      type: String,
      required: true,
      trim: true
    },
    state: {
      type: String,
      trim: true
    },
    postalCode: {
      type: String,
      trim: true
    },
    country: {
      type: String,
      required: true,
      default: 'SA'
    },
    isDefault: {
      type: Boolean,
      default: false
    },
    coordinates: {
      latitude: Number,
      longitude: Number
    }
  }],
  
  // Client Status and Classification
  status: {
    type: String,
    enum: ['prospect', 'active', 'inactive', 'suspended', 'churned'],
    default: 'prospect',
    index: true
  },
  
  priority: {
    type: String,
    enum: ['low', 'medium', 'high', 'vip'],
    default: 'medium'
  },
  
  segment: {
    type: String,
    enum: ['individual', 'small_business', 'corporate', 'enterprise'],
    default: 'individual'
  },
  
  // Financial Information
  creditLimit: {
    type: Number,
    default: 0,
    min: 0
  },
  
  paymentTerms: {
    type: String,
    enum: ['cod', 'net_7', 'net_15', 'net_30', 'net_60', 'custom'],
    default: 'net_30'
  },
  
  pricingTier: {
    type: String,
    enum: ['standard', 'premium', 'enterprise'],
    default: 'standard'
  },
  
  // Relationships
  primaryContactId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Contact'
  },
  
  assignedUserId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    index: true
  },
  
  parentClientId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Client'
  },
  
  // Communication Preferences
  communicationPreferences: {
    email: {
      type: Boolean,
      default: true
    },
    sms: {
      type: Boolean,
      default: true
    },
    phone: {
      type: Boolean,
      default: false
    },
    newsletter: {
      type: Boolean,
      default: true
    },
    marketing: {
      type: Boolean,
      default: false
    },
    language: {
      type: String,
      enum: ['ar', 'en'],
      default: 'ar'
    }
  },
  
  // Activity Tracking
  lastInteractionAt: {
    type: Date,
    index: true
  },
  
  lastInteractionType: {
    type: String,
    enum: ['email', 'phone', 'meeting', 'visit', 'survey', 'support', 'other']
  },
  
  interactionCount: {
    type: Number,
    default: 0
  },
  
  // Metrics and KPIs
  metrics: {
    totalRevenue: {
      type: Number,
      default: 0
    },
    totalShipments: {
      type: Number,
      default: 0
    },
    averageOrderValue: {
      type: Number,
      default: 0
    },
    satisfactionScore: {
      type: Number,
      min: 1,
      max: 5,
      default: 0
    },
    retentionRate: {
      type: Number,
      default: 0
    },
    lifetimeValue: {
      type: Number,
      default: 0
    }
  },
  
  // Tags and Categories
  tags: [{
    type: String,
    trim: true
  }],
  
  customFields: [{
    name: {
      type: String,
      required: true
    },
    value: {
      type: mongoose.Schema.Types.Mixed,
      required: true
    },
    type: {
      type: String,
      enum: ['text', 'number', 'date', 'boolean', 'select'],
      default: 'text'
    }
  }],
  
  // System Fields
  isActive: {
    type: Boolean,
    default: true,
    index: true
  },
  
  createdBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  
  updatedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  
  // Soft Delete
  deletedAt: {
    type: Date
  },
  
  deletedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  }
}, {
  timestamps: true,
  toJSON: { virtuals: true },
  toObject: { virtuals: true }
});

// Indexes for performance
clientSchema.index({ email: 1 }, { unique: true });
clientSchema.index({ phone: 1 });
clientSchema.index({ 'company.name': 'text', name: 'text' });
clientSchema.index({ status: 1, isActive: 1 });
clientSchema.index({ assignedUserId: 1, status: 1 });
clientSchema.index({ createdAt: -1 });
clientSchema.index({ lastInteractionAt: -1 });

// Virtuals
clientSchema.virtual('fullAddress').get(function() {
  const mainAddress = this.addresses.find(addr => addr.isDefault || addr.type === 'main');
  if (!mainAddress) return '';
  
  return `${mainAddress.street}, ${mainAddress.city}, ${mainAddress.country}`;
});

clientSchema.virtual('age').get(function() {
  const now = new Date();
  const created = new Date(this.createdAt);
  return Math.floor((now - created) / (365.25 * 24 * 60 * 60 * 1000));
});

clientSchema.virtual('statusAr').get(function() {
  const statusMap = {
    prospect: 'عميل محتمل',
    active: 'نشط',
    inactive: 'غير نشط',
    suspended: 'معلق',
    churned: 'منسحب'
  };
  return statusMap[this.status] || this.status;
});

clientSchema.virtual('priorityAr').get(function() {
  const priorityMap = {
    low: 'منخفضة',
    medium: 'متوسطة',
    high: 'مرتفعة',
    vip: 'VIP'
  };
  return priorityMap[this.priority] || this.priority;
});

// Pre-save middleware
clientSchema.pre('save', async function(next) {
  // Ensure only one default address
  if (this.isModified('addresses')) {
    const defaultAddresses = this.addresses.filter(addr => addr.isDefault);
    if (defaultAddresses.length > 1) {
      this.addresses.forEach(addr => {
        if (addr.type !== 'main') {
          addr.isDefault = false;
        }
      });
    }
  }
  
  // Update interaction count if lastInteractionAt changed
  if (this.isModified('lastInteractionAt') && this.lastInteractionAt) {
    this.interactionCount = (this.interactionCount || 0) + 1;
  }
  
  next();
});

// Static methods
clientSchema.statics = {
  /**
   * Get active clients with pagination
   */
  async getActiveClients(options = {}) {
    const {
      page = 1,
      limit = 20,
      search,
      status,
      industry,
      size,
      assignedUserId,
      sortBy = 'createdAt',
      sortOrder = 'desc'
    } = options;
    
    const query = { isActive: true, deletedAt: { $exists: false } };
    
    if (search) {
      query.$or = [
        { name: { $regex: search, $options: 'i' } },
        { email: { $regex: search, $options: 'i' } },
        { phone: { $regex: search, $options: 'i' } },
        { 'company.name': { $regex: search, $options: 'i' } }
      ];
    }
    
    if (status) query.status = status;
    if (industry) query['company.industry'] = industry;
    if (size) query['company.size'] = size;
    if (assignedUserId) query.assignedUserId = assignedUserId;
    
    const skip = (page - 1) * limit;
    const sort = { [sortBy]: sortOrder === 'desc' ? -1 : 1 };
    
    const [clients, total] = await Promise.all([
      this.find(query)
        .populate('primaryContactId', 'name email phone')
        .populate('assignedUserId', 'name email')
        .sort(sort)
        .skip(skip)
        .limit(parseInt(limit)),
      this.countDocuments(query)
    ]);
    
    return {
      clients,
      pagination: {
        page,
        limit,
        total,
        pages: Math.ceil(total / limit)
      }
    };
  },
  
  /**
   * Get client statistics
   */
  async getStatistics(filters = {}) {
    const matchQuery = { isActive: true, deletedAt: { $exists: false } };
    
    const [
      totalClients,
      activeClients,
      newClientsThisMonth,
      clientsByIndustry,
      clientsBySize,
      clientsByStatus,
      topClientsByRevenue,
      averageMetrics
    ] = await Promise.all([
      this.countDocuments(matchQuery),
      this.countDocuments({ ...matchQuery, status: 'active' }),
      this.countDocuments({
        ...matchQuery,
        createdAt: { $gte: new Date(new Date().setDate(1)) }
      }),
      this.aggregate([
        { $match: matchQuery },
        { $group: { _id: '$company.industry', count: { $sum: 1 } } }
      ]),
      this.aggregate([
        { $match: matchQuery },
        { $group: { _id: '$company.size', count: { $sum: 1 } } }
      ]),
      this.aggregate([
        { $match: matchQuery },
        { $group: { _id: '$status', count: { $sum: 1 } } }
      ]),
      this.aggregate([
        { $match: matchQuery },
        { $sort: { 'metrics.totalRevenue': -1 } },
        { $limit: 10 },
        {
          $project: {
            name: 1,
            'company.name': 1,
            'metrics.totalRevenue': 1,
            'metrics.totalShipments': 1,
            'metrics.lifetimeValue': 1
          }
        }
      ]),
      this.aggregate([
        { $match: matchQuery },
        {
          $group: {
            _id: null,
            avgRevenue: { $avg: '$metrics.totalRevenue' },
            avgShipments: { $avg: '$metrics.totalShipments' },
            avgSatisfaction: { $avg: '$metrics.satisfactionScore' },
            totalRevenue: { $sum: '$metrics.totalRevenue' },
            totalShipments: { $sum: '$metrics.totalShipments' }
          }
        }
      ])
    ]);
    
    return {
      total: totalClients,
      active: activeClients,
      newThisMonth: newClientsThisMonth,
      growthRate: activeClients > 0 ? ((newClientsThisMonth / activeClients) * 100).toFixed(1) : 0,
      byIndustry: clientsByIndustry,
      bySize: clientsBySize,
      byStatus: clientsByStatus,
      topClients: topClientsByRevenue,
      average: averageMetrics[0] || {}
    };
  },
  
  /**
   * Soft delete client
   */
  async softDelete(clientId, deletedBy) {
    return await this.findByIdAndUpdate(
      clientId,
      { 
        deletedAt: new Date(),
        deletedBy,
        isActive: false
      }
    );
  },
  
  /**
   * Restore deleted client
   */
  async restore(clientId) {
    return await this.findByIdAndUpdate(
      clientId,
      { 
        deletedAt: undefined,
        deletedBy: undefined,
        isActive: true
      }
    );
  },
  
  /**
   * Update client metrics
   */
  async updateMetrics(clientId, metrics) {
    const updateData = {};
    
    if (metrics.revenue) {
      updateData['$inc'] = { 'metrics.totalRevenue': metrics.revenue };
    }
    
    if (metrics.shipments) {
      updateData['$inc'] = { 'metrics.totalShipments': metrics.shipments };
    }
    
    if (metrics.satisfactionScore) {
      updateData['metrics.satisfactionScore'] = metrics.satisfactionScore;
    }
    
    if (Object.keys(updateData).length > 0) {
      await this.findByIdAndUpdate(clientId, updateData);
    }
  }
};

// Instance methods
clientSchema.methods = {
  /**
   * Add interaction
   */
  async addInteraction(interactionData) {
    this.lastInteractionAt = new Date();
    this.lastInteractionType = interactionData.type;
    this.interactionCount = (this.interactionCount || 0) + 1;
    
    await this.save();
    return this;
  },
  
  /**
   * Update status
   */
  async updateStatus(status, updatedBy) {
    this.status = status;
    this.updatedBy = updatedBy;
    
    await this.save();
    return this;
  },
  
  /**
   * Calculate lifetime value
   */
  async calculateLifetimeValue() {
    // Complex calculation based on revenue, retention, and predictive analytics
    const baseValue = this.metrics.totalRevenue || 0;
    const retentionMultiplier = (this.metrics.retentionRate || 0) / 100;
    const frequencyMultiplier = Math.log1p(this.metrics.totalShipments || 1);
    
    this.metrics.lifetimeValue = baseValue * (1 + retentionMultiplier) * frequencyMultiplier;
    await this.save();
    
    return this.metrics.lifetimeValue;
  },
  
  /**
   * Get primary address
   */
  getPrimaryAddress() {
    return this.addresses.find(addr => addr.isDefault || addr.type === 'main') || this.addresses[0];
  },
  
  /**
   * Add custom field
   */
  addCustomField(name, value, type = 'text') {
    // Remove existing field with same name
    this.customFields = this.customFields.filter(field => field.name !== name);
    
    // Add new field
    this.customFields.push({ name, value, type });
    return this;
  },
  
  /**
   * Get formatted client data
   */
  getFormattedData() {
    return {
      id: this._id,
      name: this.name,
      email: this.email,
      phone: this.phone,
      company: this.company,
      primaryAddress: this.getPrimaryAddress(),
      status: this.statusAr,
      priority: this.priorityAr,
      metrics: this.metrics,
      tags: this.tags,
      createdAt: this.createdAt,
      lastInteraction: this.lastInteractionAt
    };
  }
};

module.exports = mongoose.model('Client', clientSchema);
