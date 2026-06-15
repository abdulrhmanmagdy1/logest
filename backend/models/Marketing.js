//
/**
 * ============================================
 * 📢 Marketing & Campaigns - التسويق والحملات
 * ============================================
 */

const mongoose = require('mongoose');

// Marketing Campaign Schema
const MarketingCampaignSchema = new mongoose.Schema({
  campaignId: {
    type: String,
    required: true,
    unique: true
  },
  name: {
    type: String,
    required: true
  },
  type: {
    type: String,
    enum: ['email', 'social_media', 'content', 'event', 'webinar', 'advertisement', 'referral', 'loyalty', 'seasonal', 'product_launch'],
    required: true
  },
  objective: {
    type: String,
    enum: ['brand_awareness', 'lead_generation', 'customer_acquisition', 'customer_retention', 'sales', 'engagement'],
    required: true
  },
  target: {
    audience: [{
      type: String,
      enum: ['new_customers', 'existing_customers', 'corporates', 'sme', 'individuals', 'logistics_managers', 'decision_makers']
    }],
    segments: [String],
    demographics: {
      regions: [String],
      industries: [String],
      companySize: [String]
    },
    estimatedReach: Number
  },
  schedule: {
    startDate: { type: Date, required: true },
    endDate: { type: Date, required: true },
    timezone: { type: String, default: 'Asia/Riyadh' }
  },
  budget: {
    allocated: { type: Number, required: true },
    spent: { type: Number, default: 0 },
    remaining: Number,
    currency: { type: String, default: 'SAR' }
  },
  content: {
    subject: String,
    headline: String,
    body: String,
    callToAction: String,
    landingPage: String,
    creativeAssets: [{
      type: { type: String, enum: ['image', 'video', 'banner', 'infographic', 'brochure'] },
      url: String,
      size: String
    }]
  },
  channels: [{
    name: String,
    type: {
      type: String,
      enum: ['email', 'sms', 'whatsapp', 'linkedin', 'twitter', 'facebook', 'instagram', 'google_ads', 'display', 'outdoor']
    },
    budget: Number,
    status: {
      type: String,
      enum: ['planned', 'active', 'paused', 'completed'],
      default: 'planned'
    }
  }],
  tracking: {
    utmSource: String,
    utmMedium: String,
    utmCampaign: String,
    trackingPixels: [String]
  },
  automation: {
    enabled: { type: Boolean, default: false },
    trigger: String,
    sequence: [{
      step: Number,
      delay: Number, // hours
      action: String,
      condition: String
    }]
  },
  performance: {
    impressions: { type: Number, default: 0 },
    clicks: { type: Number, default: 0 },
    ctr: { type: Number, default: 0 },
    conversions: { type: Number, default: 0 },
    leads: { type: Number, default: 0 },
    customers: { type: Number, default: 0 },
    revenue: { type: Number, default: 0 },
    roi: { type: Number, default: 0 },
    costPerLead: { type: Number, default: 0 },
    costPerAcquisition: { type: Number, default: 0 }
  },
  abTesting: {
    enabled: { type: Boolean, default: false },
    variants: [{
      name: String,
      subject: String,
      content: String,
      sendPercentage: Number,
      results: {
        sent: Number,
        opens: Number,
        clicks: Number,
        conversions: Number
      }
    }],
    winner: String
  },
  status: {
    type: String,
    enum: ['draft', 'scheduled', 'active', 'paused', 'completed', 'cancelled'],
    default: 'draft'
  },
  team: {
    owner: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    members: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
    agency: String
  },
  approval: {
    status: {
      type: String,
      enum: ['pending', 'approved', 'rejected'],
      default: 'pending'
    },
    approvedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    approvedAt: Date,
    comments: String
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

MarketingCampaignSchema.index({ company: 1, status: 1 });
MarketingCampaignSchema.index({ 'schedule.startDate': 1 });

// Content Calendar Schema
const ContentCalendarSchema = new mongoose.Schema({
  date: {
    type: Date,
    required: true
  },
  items: [{
    time: String,
    title: String,
    description: String,
    type: {
      type: String,
      enum: ['post', 'story', 'email', 'blog', 'video', 'webinar', 'ad']
    },
    channel: String,
    campaign: { type: mongoose.Schema.Types.ObjectId, ref: 'MarketingCampaign' },
    status: {
      type: String,
      enum: ['planned', 'in_review', 'approved', 'scheduled', 'published'],
      default: 'planned'
    },
    assignedTo: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    assets: [String],
    notes: String
  }],
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

// Landing Page Schema
const LandingPageSchema = new mongoose.Schema({
  slug: {
    type: String,
    required: true,
    unique: true
  },
  title: String,
  headline: String,
  subheadline: String,
  content: {
    hero: {
      image: String,
      video: String,
      text: String
    },
    features: [{
      icon: String,
      title: String,
      description: String
    }],
    testimonials: [{
      name: String,
      company: String,
      text: String,
      rating: Number,
      avatar: String
    }],
    pricing: [{
      plan: String,
      price: Number,
      period: String,
      features: [String],
      cta: String
    }],
    faq: [{
      question: String,
      answer: String
    }]
  },
  form: {
    fields: [{
      name: String,
      type: String,
      required: Boolean,
      placeholder: String
    }],
    submitButton: String,
    successMessage: String
  },
  seo: {
    metaTitle: String,
    metaDescription: String,
    keywords: [String],
    ogImage: String
  },
  tracking: {
    analyticsId: String,
    conversionGoals: [String],
    heatmapEnabled: { type: Boolean, default: false }
  },
  stats: {
    visits: { type: Number, default: 0 },
    uniqueVisitors: { type: Number, default: 0 },
    conversions: { type: Number, default: 0 },
    conversionRate: { type: Number, default: 0 },
    averageTime: { type: Number, default: 0 },
    bounceRate: { type: Number, default: 0 }
  },
  status: {
    type: String,
    enum: ['draft', 'published', 'archived'],
    default: 'draft'
  },
  campaign: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'MarketingCampaign'
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

// Referral Program Schema
const ReferralProgramSchema = new mongoose.Schema({
  name: String,
  description: String,
  rules: {
    referrerReward: {
      type: { type: String, enum: ['discount', 'credit', 'cash', 'service'] },
      value: Number,
      currency: String,
      description: String
    },
    refereeReward: {
      type: { type: String, enum: ['discount', 'credit', 'service'] },
      value: Number,
      description: String
    },
    minimumSpend: Number,
    validityDays: Number
  },
  tracking: {
    codePrefix: String,
    uniqueLinks: { type: Boolean, default: true }
  },
  participants: [{
    user: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    referralCode: String,
    referralsMade: { type: Number, default: 0 },
    successfulReferrals: { type: Number, default: 0 },
    rewardsEarned: { type: Number, default: 0 },
    joinedAt: { type: Date, default: Date.now }
  }],
  status: {
    type: String,
    enum: ['draft', 'active', 'paused', 'ended'],
    default: 'draft'
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

module.exports = {
  MarketingCampaign: mongoose.model('MarketingCampaign', MarketingCampaignSchema),
  ContentCalendar: mongoose.model('ContentCalendar', ContentCalendarSchema),
  LandingPage: mongoose.model('LandingPage', LandingPageSchema),
  ReferralProgram: mongoose.model('ReferralProgram', ReferralProgramSchema)
};
