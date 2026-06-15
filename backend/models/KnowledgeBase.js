//
/**
 * ============================================
 * 📚 Knowledge Base - قاعدة المعرفة والمساعدة
 * ============================================
 */

const mongoose = require('mongoose');

// Category Schema
const KBCategorySchema = new mongoose.Schema({
  categoryId: {
    type: String,
    required: true,
    unique: true
  },
  name: {
    type: String,
    required: true
  },
  slug: String,
  description: String,
  icon: String,
  color: String,
  parent: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'KBCategory'
  },
  children: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'KBCategory'
  }],
  order: { type: Number, default: 0 },
  isPublic: { type: Boolean, default: true },
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

// Article Schema
const KBArticleSchema = new mongoose.Schema({
  articleId: {
    type: String,
    required: true,
    unique: true
  },
  title: {
    type: String,
    required: true
  },
  slug: String,
  summary: String,
  content: {
    type: String,
    required: true
  },
  contentType: {
    type: String,
    enum: ['markdown', 'html', 'richtext'],
    default: 'richtext'
  },
  category: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'KBCategory',
    required: true
  },
  tags: [String],
  keywords: [String],
  author: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  status: {
    type: String,
    enum: ['draft', 'review', 'published', 'archived'],
    default: 'draft'
  },
  visibility: {
    type: String,
    enum: ['public', 'internal', 'customers', 'partners'],
    default: 'public'
  },
  attachments: [{
    name: String,
    url: String,
    type: String,
    size: Number
  }],
  relatedArticles: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'KBArticle'
  }],
  translations: [{
    language: String,
    title: String,
    content: String
  }],
  seo: {
    metaTitle: String,
    metaDescription: String,
    keywords: [String]
  },
  feedback: {
    helpful: { type: Number, default: 0 },
    notHelpful: { type: Number, default: 0 },
    comments: [{
      user: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
      comment: String,
      rating: Number,
      timestamp: { type: Date, default: Date.now }
    }]
  },
  analytics: {
    views: { type: Number, default: 0 },
    uniqueViews: { type: Number, default: 0 },
    avgReadTime: Number,
    lastViewed: Date
  },
  version: {
    current: { type: Number, default: 1 },
    history: [{
      version: Number,
      content: String,
      author: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
      changes: String,
      timestamp: Date
    }]
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  publishedAt: Date,
  createdAt: {
    type: Date,
    default: Date.now
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
});

KBArticleSchema.index({ company: 1, status: 1, category: 1 });
KBArticleSchema.index({ company: 1, 'analytics.views': -1 });

// FAQ Schema
const FAQSchema = new mongoose.Schema({
  faqId: {
    type: String,
    required: true,
    unique: true
  },
  question: {
    type: String,
    required: true
  },
  answer: {
    type: String,
    required: true
  },
  category: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'KBCategory'
  },
  order: { type: Number, default: 0 },
  isPopular: { type: Boolean, default: false },
  translations: [{
    language: String,
    question: String,
    answer: String
  }],
  analytics: {
    views: { type: Number, default: 0 },
    helpful: { type: Number, default: 0 },
    notHelpful: { type: Number, default: 0 }
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

// Glossary Schema
const KBGlossarySchema = new mongoose.Schema({
  term: {
    type: String,
    required: true
  },
  slug: String,
  definition: {
    type: String,
    required: true
  },
  category: String,
  relatedTerms: [String],
  translations: [{
    language: String,
    term: String,
    definition: String
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
  KBCategory: mongoose.model('KBCategory', KBCategorySchema),
  KBArticle: mongoose.model('KBArticle', KBArticleSchema),
  FAQ: mongoose.model('FAQ', FAQSchema),
  KBGlossary: mongoose.model('KBGlossary', KBGlossarySchema)
};
