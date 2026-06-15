//
/**
 * ============================================
 * 🔍 Search & Discovery - البحث والاستكشاف المتقدم
 * ============================================
 */

const mongoose = require('mongoose');

// Search Index Schema
const SearchIndexSchema = new mongoose.Schema({
  entityType: {
    type: String,
    required: true,
    enum: ['shipment', 'driver', 'vehicle', 'customer', 'invoice', 'warehouse', 'document', 'ticket']
  },
  entityId: {
    type: mongoose.Schema.Types.ObjectId,
    required: true
  },
  content: {
    title: String,
    description: String,
    keywords: [String],
    tags: [String],
    metadata: mongoose.Schema.Types.Mixed
  },
  searchableText: {
    type: String,
    index: 'text' // MongoDB text index
  },
  filters: {
    status: String,
    priority: String,
    category: String,
    date: Date,
    location: String,
    tags: [String],
    custom: mongoose.Schema.Types.Mixed
  },
  boost: {
    type: Number,
    default: 1
  },
  language: {
    type: String,
    default: 'ar'
  },
  popularity: {
    views: { type: Number, default: 0 },
    clicks: { type: Number, default: 0 },
    lastViewed: Date
  },
  lastIndexed: {
    type: Date,
    default: Date.now
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

SearchIndexSchema.index({ company: 1, entityType: 1 });
SearchIndexSchema.index({ company: 1, 'filters.status': 1 });
SearchIndexSchema.index({ company: 1, 'filters.date': -1 });

// Search Query Schema
const SearchQuerySchema = new mongoose.Schema({
  queryId: {
    type: String,
    required: true,
    unique: true
  },
  query: {
    type: String,
    required: true
  },
  normalized: String,
  filters: mongoose.Schema.Types.Mixed,
  results: {
    total: Number,
    returned: Number,
    executionTime: Number // ms
  },
  suggestions: [String],
  didYouMean: String,
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  clickedResults: [{
    entityType: String,
    entityId: mongoose.Schema.Types.ObjectId,
    position: Number,
    timestamp: Date
  }],
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  timestamp: {
    type: Date,
    default: Date.now
  }
});

SearchQuerySchema.index({ company: 1, query: 'text' });
SearchQuerySchema.index({ company: 1, timestamp: -1 });

// Saved Search Schema
const SavedSearchSchema = new mongoose.Schema({
  searchId: {
    type: String,
    required: true,
    unique: true
  },
  name: String,
  description: String,
  query: String,
  filters: mongoose.Schema.Types.Mixed,
  sort: {
    field: String,
    order: { type: String, enum: ['asc', 'desc'] }
  },
  alerts: {
    enabled: { type: Boolean, default: false },
    frequency: { type: String, enum: ['immediate', 'daily', 'weekly'] },
    newResultsOnly: { type: Boolean, default: true }
  },
  owner: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  sharedWith: [{
    user: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    permissions: { type: String, enum: ['view', 'edit'] }
  }],
  isPublic: { type: Boolean, default: false },
  usage: {
    executed: { type: Number, default: 0 },
    lastExecuted: Date
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

// Search Analytics Schema
const SearchAnalyticsSchema = new mongoose.Schema({
  date: {
    type: Date,
    required: true
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  metrics: {
    totalQueries: { type: Number, default: 0 },
    uniqueQueries: { type: Number, default: 0 },
    zeroResults: { type: Number, default: 0 },
    avgResults: { type: Number, default: 0 },
    avgResponseTime: { type: Number, default: 0 },
    clickThroughRate: { type: Number, default: 0 }
  },
  topQueries: [{
    query: String,
    count: Number
  }],
  topNoResultQueries: [String],
  popularFilters: [{
    filter: String,
    count: Number
  }],
  byEntityType: [{
    type: String,
    queries: Number
  }]
});

SearchAnalyticsSchema.index({ company: 1, date: -1 });

module.exports = {
  SearchIndex: mongoose.model('SearchIndex', SearchIndexSchema),
  SearchQuery: mongoose.model('SearchQuery', SearchQuerySchema),
  SavedSearch: mongoose.model('SavedSearch', SavedSearchSchema),
  SearchAnalytics: mongoose.model('SearchAnalytics', SearchAnalyticsSchema)
};
