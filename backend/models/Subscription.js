//
/**
 * ============================================
 * 💎 Subscription & Pricing Model
 * نظام الاشتراكات والأسعار
 * ============================================
 */

const mongoose = require('mongoose');

const PlanSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true,
    enum: ['basic', 'standard', 'premium', 'enterprise']
  },
  displayName: {
    type: String,
    required: true
  },
  description: String,
  
  // Pricing
  price: {
    monthly: { type: Number, required: true },
    yearly: { type: Number, required: true },
    currency: { type: String, default: 'SAR' }
  },
  
  // Features
  features: {
    maxShipments: { type: Number, default: 100 },
    maxDrivers: { type: Number, default: 5 },
    maxVehicles: { type: Number, default: 5 },
    maxBranches: { type: Number, default: 1 },
    maxUsers: { type: Number, default: 10 },
    
    // Feature flags
    hasTemperatureMonitoring: { type: Boolean, default: false },
    hasRouteOptimization: { type: Boolean, default: false },
    hasAIAnalytics: { type: Boolean, default: false },
    hasWhiteLabel: { type: Boolean, default: false },
    hasAPIAccess: { type: Boolean, default: false },
    hasPrioritySupport: { type: Boolean, default: false },
    hasCustomIntegrations: { type: Boolean, default: false },
    hasAdvancedReporting: { type: Boolean, default: false }
  },
  
  // Limits
  limits: {
    storageGB: { type: Number, default: 10 },
    apiCallsPerDay: { type: Number, default: 1000 },
    smsNotifications: { type: Number, default: 100 },
    emailNotifications: { type: Number, default: 1000 }
  },
  
  // Status
  isActive: { type: Boolean, default: true },
  isPublic: { type: Boolean, default: true },
  displayOrder: { type: Number, default: 0 },
  
  createdAt: { type: Date, default: Date.now },
  updatedAt: { type: Date, default: Date.now }
});

const SubscriptionSchema = new mongoose.Schema({
  // Company/Organization
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  
  // Plan Details
  plan: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Plan',
    required: true
  },
  planCode: String, // Denormalized for quick access
  
  // Billing Cycle
  billingCycle: {
    type: String,
    enum: ['monthly', 'yearly'],
    default: 'monthly'
  },
  
  // Status
  status: {
    type: String,
    enum: ['active', 'trial', 'suspended', 'cancelled', 'expired'],
    default: 'trial'
  },
  
  // Trial
  trial: {
    isTrial: { type: Boolean, default: true },
    trialEndsAt: Date,
    trialDuration: { type: Number, default: 14 } // days
  },
  
  // Dates
  currentPeriodStart: { type: Date, default: Date.now },
  currentPeriodEnd: { type: Date },
  
  // Payment
  payment: {
    method: {
      type: String,
      enum: ['card', 'bank_transfer', 'paypal', 'apple_pay', 'stc_pay']
    },
    last4: String,
    brand: String,
    expiryMonth: Number,
    expiryYear: Number
  },
  
  // Usage Tracking
  usage: {
    shipments: { type: Number, default: 0 },
    drivers: { type: Number, default: 0 },
    vehicles: { type: Number, default: 0 },
    storageUsed: { type: Number, default: 0 }, // GB
    apiCalls: { type: Number, default: 0 },
    smsSent: { type: Number, default: 0 },
    emailsSent: { type: Number, default: 0 }
  },
  
  // Usage Limits Override (optional)
  customLimits: {
    maxShipments: Number,
    maxDrivers: Number,
    maxVehicles: Number,
    storageGB: Number
  },
  
  // Payment History
  invoices: [{
    invoiceNumber: String,
    amount: Number,
    status: {
      type: String,
      enum: ['pending', 'paid', 'failed', 'refunded']
    },
    paidAt: Date,
    pdfUrl: String
  }],
  
  // Cancellation
  cancellation: {
    requestedAt: Date,
    reason: String,
    willCancelAt: Date,
    cancelledBy: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    }
  },
  
  // Auto-renewal
  autoRenew: { type: Boolean, default: true },
  
  // Grace period
  gracePeriod: {
    endsAt: Date,
    isInGracePeriod: { type: Boolean, default: false }
  },
  
  createdAt: { type: Date, default: Date.now },
  updatedAt: { type: Date, default: Date.now }
});

// Pre-save
SubscriptionSchema.pre('save', function(next) {
  this.updatedAt = Date.now();
  
  // Calculate period end based on billing cycle
  if (!this.currentPeriodEnd || this.isModified('currentPeriodStart')) {
    const end = new Date(this.currentPeriodStart);
    if (this.billingCycle === 'yearly') {
      end.setFullYear(end.getFullYear() + 1);
    } else {
      end.setMonth(end.getMonth() + 1);
    }
    this.currentPeriodEnd = end;
  }
  
  next();
});

// Methods
SubscriptionSchema.methods.isFeatureAvailable = function(featureName) {
  // Check if feature is available in plan or custom override
  const planFeatures = this.plan?.features || {};
  return planFeatures[featureName] === true;
};

SubscriptionSchema.methods.checkUsageLimit = function(resource) {
  const plan = this.plan;
  const customLimit = this.customLimits?.[resource];
  const planLimit = plan?.features?.[`max${resource.charAt(0).toUpperCase() + resource.slice(1)}`];
  const limit = customLimit || planLimit || 0;
  
  const currentUsage = this.usage[resource] || 0;
  
  return {
    used: currentUsage,
    limit: limit,
    remaining: Math.max(0, limit - currentUsage),
    percentage: limit > 0 ? (currentUsage / limit) * 100 : 0,
    isExceeded: currentUsage >= limit
  };
};

SubscriptionSchema.methods.incrementUsage = async function(resource, amount = 1) {
  this.usage[resource] = (this.usage[resource] || 0) + amount;
  await this.save();
  return this.checkUsageLimit(resource);
};

module.exports = {
  Plan: mongoose.model('Plan', PlanSchema),
  Subscription: mongoose.model('Subscription', SubscriptionSchema)
};
