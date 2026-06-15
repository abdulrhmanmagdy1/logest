/**
 * ============================================
 * 💼 Opportunity Model - نظام إدهام الاحترافي
 * Edham Logistics - Sales Opportunity Management
 * ============================================
 */

const mongoose = require('mongoose');

const opportunitySchema = new mongoose.Schema({
  // Basic Information
  name: {
    type: String,
    required: true,
    trim: true,
    maxlength: 200
  },
  
  description: {
    type: String,
    trim: true,
    maxlength: 2000
  },
  
  // Client Relationship
  clientId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Client',
    required: true,
    index: true
  },
  
  // Opportunity Details
  type: {
    type: String,
    enum: ['new_business', 'existing_business', 'expansion', 'renewal'],
    default: 'new_business'
  },
  
  stage: {
    type: String,
    enum: [
      'prospecting',
      'qualification',
      'needs_analysis',
      'value_proposition',
      'proposal',
      'negotiation',
      'closed_won',
      'closed_lost'
    ],
    default: 'prospecting',
    index: true
  },
  
  probability: {
    type: Number,
    min: 0,
    max: 100,
    default: 0
  },
  
  value: {
    type: Number,
    required: true,
    min: 0
  },
  
  currency: {
    type: String,
    enum: ['SAR', 'USD', 'EUR'],
    default: 'SAR'
  },
  
  // Timeline
  closingDate: {
    type: Date,
    required: true
  },
  
  expectedRevenueDate: {
    type: Date
  },
  
  // Assignment
  assignedUserId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    index: true
  },
  
  // Competition
  competitors: [{
    name: String,
    strengths: [String],
    weaknesses: [String],
    probability: Number
  }],
  
  // Products/Services
  products: [{
    productId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Product'
    },
    name: String,
    quantity: Number,
    unitPrice: Number,
    totalPrice: Number,
    description: String
  }],
  
  // Communication History
  activities: [{
    type: {
      type: String,
      enum: ['call', 'email', 'meeting', 'presentation', 'proposal', 'follow_up', 'note'],
      required: true
    },
    subject: String,
    description: String,
    userId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User',
      required: true
    },
    createdAt: {
      type: Date,
      default: Date.now
    },
    attachments: [{
      filename: String,
      originalName: String,
      path: String,
      mimeType: String,
      size: Number
    }],
    outcome: {
      type: String,
      enum: ['positive', 'negative', 'neutral']
    },
    nextAction: String,
    nextActionDate: Date
  }],
  
  // Lost Reason
  lostReason: {
    type: String,
    enum: [
      'price',
      'competition',
      'timing',
      'requirements',
      'relationship',
      'no_budget',
      'internal',
      'other'
    ]
  },
  
  lostReasonDescription: String,
  
  // Tags and Categories
  tags: [{
    type: String,
    trim: true
  }],
  
  category: {
    type: String,
    enum: ['logistics', 'transport', 'warehousing', 'consulting', 'technology', 'other'],
    default: 'logistics'
  },
  
  priority: {
    type: String,
    enum: ['low', 'medium', 'high', 'critical'],
    default: 'medium'
  },
  
  // Source Information
  source: {
    type: String,
    enum: [
      'website',
      'referral',
      'cold_call',
      'email_campaign',
      'social_media',
      'trade_show',
      'partner',
      'existing_client',
      'other'
    ]
  },
  
  sourceDetails: String,
  
  // Custom Fields
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
opportunitySchema.index({ clientId: 1, stage: 1 });
opportunitySchema.index({ assignedUserId: 1, stage: 1 });
opportunitySchema.index({ closingDate: 1 });
opportunitySchema.index({ stage: 1, probability: -1 });
opportunitySchema.index({ value: -1 });
opportunitySchema.index({ createdAt: -1 });

// Virtuals
opportunitySchema.virtual('formattedValue').get(function() {
  return `${this.value.toLocaleString()} ${this.currency}`;
});

opportunitySchema.virtual('stageAr').get(function() {
  const stageMap = {
    prospecting: 'استكشاف',
    qualification: 'تأهيل',
    needs_analysis: 'تحليل الاحتياجات',
    value_proposition: 'عرض القيمة',
    proposal: 'اقتراح',
    negotiation: 'مفاوضات',
    closed_won: 'مكتمل (فوز)',
    closed_lost: 'مكتمل (خسارة)'
  };
  return stageMap[this.stage] || this.stage;
});

opportunitySchema.virtual('priorityAr').get(function() {
  const priorityMap = {
    low: 'منخفضة',
    medium: 'متوسطة',
    high: 'مرتفعة',
    critical: 'حرجة'
  };
  return priorityMap[this.priority] || this.priority;
});

opportunitySchema.virtual('status').get(function() {
  if (this.stage === 'closed_won') return 'won';
  if (this.stage === 'closed_lost') return 'lost';
  return 'open';
});

opportunitySchema.virtual('statusAr').get(function() {
  const statusMap = {
    open: 'مفتوحة',
    won: 'مكتمل (فوز)',
    lost: 'مكتمل (خسارة)'
  };
  return statusMap[this.status] || this.status;
});

opportunitySchema.virtual('daysToClose').get(function() {
  const now = new Date();
  const closing = new Date(this.closingDate);
  const diffTime = closing - now;
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
});

opportunitySchema.virtual('isOverdue').get(function() {
  return this.daysToClose < 0 && !['closed_won', 'closed_lost'].includes(this.stage);
});

// Pre-save middleware
opportunitySchema.pre('save', async function(next) {
  // Update probability based on stage
  const stageProbabilityMap = {
    prospecting: 10,
    qualification: 25,
    needs_analysis: 40,
    value_proposition: 60,
    proposal: 75,
    negotiation: 90,
    closed_won: 100,
    closed_lost: 0
  };
  
  if (this.isModified('stage') && !this.isModified('probability')) {
    this.probability = stageProbabilityMap[this.stage] || 0;
  }
  
  // Calculate total products value
  if (this.products && this.products.length > 0) {
    const productsTotal = this.products.reduce((sum, product) => sum + (product.totalPrice || 0), 0);
    if (productsTotal > 0 && !this.isModified('value')) {
      this.value = productsTotal;
    }
  }
  
  next();
});

// Static methods
opportunitySchema.statics = {
  /**
   * Get opportunities with pipeline view
   */
  async getPipeline(filters = {}) {
    const {
      assignedUserId,
      clientId,
      stage,
      category,
      dateRange,
      page = 1,
      limit = 50
    } = filters;
    
    const query = { deletedAt: { $exists: false } };
    
    if (assignedUserId) query.assignedUserId = assignedUserId;
    if (clientId) query.clientId = clientId;
    if (stage) query.stage = stage;
    if (category) query.category = category;
    
    if (dateRange) {
      const [start, end] = dateRange.split(',');
      query.closingDate = {};
      if (start) query.closingDate.$gte = new Date(start);
      if (end) query.closingDate.$lte = new Date(end);
    }
    
    const skip = (page - 1) * limit;
    
    const [opportunities, total] = await Promise.all([
      this.find(query)
        .populate('clientId', 'name company.name')
        .populate('assignedUserId', 'name email')
        .sort({ closingDate: 1, priority: -1 })
        .skip(skip)
        .limit(parseInt(limit)),
      this.countDocuments(query)
    ]);
    
    return {
      opportunities,
      pagination: {
        page,
        limit,
        total,
        pages: Math.ceil(total / limit)
      }
    };
  },
  
  /**
   * Get pipeline statistics
   */
  async getPipelineStats(filters = {}) {
    const matchQuery = { deletedAt: { $exists: false } };
    
    if (filters.assignedUserId) matchQuery.assignedUserId = filters.assignedUserId;
    if (filters.dateRange) {
      const [start, end] = filters.dateRange.split(',');
      matchQuery.closingDate = {};
      if (start) matchQuery.closingDate.$gte = new Date(start);
      if (end) matchQuery.closingDate.$lte = new Date(end);
    }
    
    const [
      pipelineStats,
      totalValue,
      wonValue,
      lostValue,
      conversionStats,
      averageDealSize,
      averageSalesCycle
    ] = await Promise.all([
      // Pipeline by stage
      this.aggregate([
        { $match: matchQuery },
        {
          $group: {
            _id: '$stage',
            count: { $sum: 1 },
            totalValue: { $sum: '$value' },
            averageValue: { $avg: '$value' },
            averageProbability: { $avg: '$probability' }
          }
        }
      ]),
      // Total pipeline value
      this.aggregate([
        { $match: { ...matchQuery, stage: { $nin: ['closed_won', 'closed_lost'] } } },
        { $group: { _id: null, total: { $sum: '$value' } } }
      ]),
      // Won value
      this.aggregate([
        { $match: { ...matchQuery, stage: 'closed_won' } },
        { $group: { _id: null, total: { $sum: '$value' } } }
      ]),
      // Lost value
      this.aggregate([
        { $match: { ...matchQuery, stage: 'closed_lost' } },
        { $group: { _id: null, total: { $sum: '$value' } } }
      ]),
      // Conversion rates
      this.aggregate([
        { $match: matchQuery },
        {
          $group: {
            _id: null,
            total: { $sum: 1 },
            won: { $sum: { $cond: [{ $eq: ['$stage', 'closed_won'] }, 1, 0] } },
            lost: { $sum: { $cond: [{ $eq: ['$stage', 'closed_lost'] }, 1, 0] } }
          }
        }
      ]),
      // Average deal size
      this.aggregate([
        { $match: { ...matchQuery, stage: 'closed_won' } },
        { $group: { _id: null, avgSize: { $avg: '$value' } } }
      ]),
      // Average sales cycle
      this.aggregate([
        { $match: { ...matchQuery, stage: 'closed_won' } },
        {
          $group: {
            _id: null,
            avgCycle: {
              $avg: {
                $subtract: ['$closingDate', '$createdAt']
              }
            }
          }
        }
      ])
    ]);
    
    const winRate = conversionStats[0] ? 
      (conversionStats[0].won / conversionStats[0].total * 100).toFixed(1) : 0;
    
    return {
      pipeline: pipelineStats,
      totalValue: totalValue[0]?.total || 0,
      wonValue: wonValue[0]?.total || 0,
      lostValue: lostValue[0]?.total || 0,
      conversionRate: parseFloat(winRate),
      averageDealSize: averageDealSize[0]?.avgSize || 0,
      averageSalesCycleDays: averageSalesCycle[0]?.avgCycle ? 
        Math.ceil(averageSalesCycle[0].avgCycle / (1000 * 60 * 60 * 24)) : 0
    };
  },
  
  /**
   * Get forecast
   */
  async getForecast(months = 6) {
    const startDate = new Date();
    const endDate = new Date();
    endDate.setMonth(endDate.getMonth() + months);
    
    const opportunities = await this.find({
      stage: { $nin: ['closed_won', 'closed_lost'] },
      closingDate: { $gte: startDate, $lte: endDate },
      deletedAt: { $exists: false }
    })
      .populate('clientId', 'name')
      .populate('assignedUserId', 'name');
    
    // Group by month
    const forecast = {};
    for (let i = 0; i < months; i++) {
      const forecastDate = new Date();
      forecastDate.setMonth(forecastDate.getMonth() + i);
      const monthKey = forecastDate.toISOString().slice(0, 7);
      
      forecast[monthKey] = {
        month: forecastDate.toLocaleDateString('ar-SA', { month: 'long' }),
        year: forecastDate.getFullYear(),
        opportunities: [],
        totalValue: 0,
        weightedValue: 0
      };
    }
    
    opportunities.forEach(opp => {
      const monthKey = opp.closingDate.toISOString().slice(0, 7);
      if (forecast[monthKey]) {
        forecast[monthKey].opportunities.push(opp);
        forecast[monthKey].totalValue += opp.value;
        forecast[monthKey].weightedValue += opp.value * (opp.probability / 100);
      }
    });
    
    return Object.values(forecast);
  },
  
  /**
   * Soft delete opportunity
   */
  async softDelete(opportunityId, deletedBy) {
    return await this.findByIdAndUpdate(
      opportunityId,
      { 
        deletedAt: new Date(),
        deletedBy
      }
    );
  }
};

// Instance methods
opportunitySchema.methods = {
  /**
   * Add activity
   */
  async addActivity(activityData) {
    this.activities.push({
      ...activityData,
      createdAt: new Date()
    });
    
    await this.save();
    return this.activities[this.activities.length - 1];
  },
  
  /**
   * Update stage
   */
  async updateStage(stage, updatedBy, notes) {
    this.stage = stage;
    this.updatedBy = updatedBy;
    
    // Add activity
    await this.addActivity({
      type: 'note',
      subject: `تحديث المرحلة إلى: ${this.stageAr}`,
      description: notes || `تم تحديث المرحلة من ${this.stageAr}`,
      userId: updatedBy
    });
    
    return this;
  },
  
  /**
   * Close as won
   */
  async closeAsWon(closedBy, notes) {
    this.stage = 'closed_won';
    this.updatedBy = closedBy;
    
    await this.addActivity({
      type: 'note',
      subject: 'الفوز بالفرصة',
      description: notes || 'تم الفوز بالفرصة بنجاح',
      userId: closedBy,
      outcome: 'positive'
    });
    
    return this;
  },
  
  /**
   * Close as lost
   */
  async closeAsLost(reason, description, closedBy) {
    this.stage = 'closed_lost';
    this.lostReason = reason;
    this.lostReasonDescription = description;
    this.updatedBy = closedBy;
    
    await this.addActivity({
      type: 'note',
      subject: 'خسارة الفرصة',
      description: description || `تم خسارة الفرصة: ${reason}`,
      userId: closedBy,
      outcome: 'negative'
    });
    
    return this;
  },
  
  /**
   * Get formatted opportunity data
   */
  getFormattedData() {
    return {
      id: this._id,
      name: this.name,
      description: this.description,
      client: this.clientId,
      stage: this.stageAr,
      status: this.statusAr,
      probability: this.probability,
      value: this.formattedValue,
      closingDate: this.closingDate,
      daysToClose: this.daysToClose,
      isOverdue: this.isOverdue,
      assignedUser: this.assignedUserId,
      priority: this.priorityAr,
      tags: this.tags,
      createdAt: this.createdAt
    };
  }
};

module.exports = mongoose.model('Opportunity', opportunitySchema);
