//
/**
 * ============================================
 * 📚 Knowledge Base Routes - قاعدة المعرفة
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { KBCategory, KBArticle, FAQ, KBGlossary } = require('../models/KnowledgeBase');
const logger = require('../utils/logger');

// Categories
// @route   GET /api/v1/kb/categories
// @desc    Get knowledge base categories
// @access  Public/Private
router.get('/categories', async (req, res) => {
  try {
    const { parent, includeArticles } = req.query;

    let query = {};
    if (parent) query.parent = parent || null;

    if (req.user?.company) {
      query.company = req.user.company;
    }

    let categories = await KBCategory.find(query)
      .populate('children', 'name slug icon articleCount')
      .sort({ order: 1, name: 1 });

    // If includeArticles requested
    if (includeArticles === 'true' && req.user?.company) {
      categories = await Promise.all(categories.map(async (cat) => {
        const articles = await KBArticle.find({
          category: cat._id,
          status: 'published',
          company: req.user.company
        })
          .select('title slug summary analytics.views')
          .sort({ 'analytics.views': -1 })
          .limit(5);

        return { ...cat.toObject(), articles };
      }));
    }

    res.json({
      success: true,
      count: categories.length,
      data: categories
    });

  } catch (error) {
    logger.error('Get KB categories error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/kb/categories
// @desc    Create category
// @access  Private (Admin, Content Manager)
router.post('/categories', protect, authorize(['admin', 'content_manager']), async (req, res) => {
  try {
    const categoryId = `KB-CAT-${Date.now()}`;

    const category = await KBCategory.create({
      ...req.body,
      categoryId,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: category
    });

  } catch (error) {
    logger.error('Create KB category error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Articles
// @route   GET /api/v1/kb/articles
// @desc    Get knowledge base articles
// @access  Public/Private
router.get('/articles', async (req, res) => {
  try {
    const { category, status = 'published', search, page = 1, limit = 20 } = req.query;

    let query = {};
    if (category) query.category = category;
    if (status) query.status = status;
    if (search) {
      query.$or = [
        { title: { $regex: search, $options: 'i' } },
        { summary: { $regex: search, $options: 'i' } },
        { keywords: { $in: [new RegExp(search, 'i')] } }
      ];
    }

    if (req.user?.company) {
      query.company = req.user.company;
    }

    const articles = await KBArticle.find(query)
      .populate('category', 'name slug')
      .populate('author', 'firstName lastName')
      .select('-content')
      .sort({ 'analytics.views': -1, updatedAt: -1 })
      .skip((page - 1) * limit)
      .limit(limit * 1);

    const total = await KBArticle.countDocuments(query);

    res.json({
      success: true,
      count: articles.length,
      pagination: {
        page: parseInt(page),
        pages: Math.ceil(total / limit),
        total
      },
      data: articles
    });

  } catch (error) {
    logger.error('Get KB articles error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/kb/articles/:id
// @desc    Get article details
// @access  Public/Private
router.get('/articles/:id', async (req, res) => {
  try {
    const query = { _id: req.params.id };
    if (req.user?.company) query.company = req.user.company;

    const article = await KBArticle.findOne(query)
      .populate('category', 'name slug parent')
      .populate('author', 'firstName lastName')
      .populate('relatedArticles', 'title slug summary');

    if (!article) {
      return res.status(404).json({ success: false, message: 'Article not found' });
    }

    // Increment view count
    article.analytics.views += 1;
    article.analytics.lastViewed = new Date();
    await article.save();

    res.json({
      success: true,
      data: article
    });

  } catch (error) {
    logger.error('Get KB article error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/kb/articles
// @desc    Create article
// @access  Private (Admin, Content Manager)
router.post('/articles', protect, authorize(['admin', 'content_manager']), async (req, res) => {
  try {
    const articleId = `KB-ART-${Date.now()}`;

    const article = await KBArticle.create({
      ...req.body,
      articleId,
      author: req.user.id,
      company: req.user.company,
      version: {
        current: 1,
        history: [{
          version: 1,
          content: req.body.content,
          author: req.user.id,
          changes: 'Initial version',
          timestamp: new Date()
        }]
      }
    });

    res.status(201).json({
      success: true,
      data: article
    });

  } catch (error) {
    logger.error('Create KB article error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/kb/articles/:id
// @desc    Update article
// @access  Private (Admin, Content Manager)
router.put('/articles/:id', protect, authorize(['admin', 'content_manager']), async (req, res) => {
  try {
    const article = await KBArticle.findOne({
      _id: req.params.id,
      company: req.user.company
    });

    if (!article) {
      return res.status(404).json({ success: false, message: 'Article not found' });
    }

    // Save to history if content changed
    if (req.body.content && req.body.content !== article.content) {
      const newVersion = article.version.current + 1;
      article.version.history.push({
        version: newVersion,
        content: req.body.content,
        author: req.user.id,
        changes: req.body.changes || 'Updated',
        timestamp: new Date()
      });
      article.version.current = newVersion;
    }

    Object.assign(article, req.body, { updatedAt: new Date() });
    await article.save();

    res.json({
      success: true,
      data: article
    });

  } catch (error) {
    logger.error('Update KB article error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/kb/articles/:id/feedback
// @desc    Submit article feedback
// @access  Public/Private
router.post('/articles/:id/feedback', async (req, res) => {
  try {
    const { helpful, comment, rating } = req.body;

    const article = await KBArticle.findOne({
      _id: req.params.id,
      status: 'published'
    });

    if (!article) {
      return res.status(404).json({ success: false, message: 'Article not found' });
    }

    if (helpful !== undefined) {
      if (helpful) {
        article.feedback.helpful += 1;
      } else {
        article.feedback.notHelpful += 1;
      }
    }

    if (comment || rating) {
      article.feedback.comments.push({
        user: req.user?.id,
        comment,
        rating,
        timestamp: new Date()
      });
    }

    await article.save();

    res.json({
      success: true,
      message: 'Feedback submitted'
    });

  } catch (error) {
    logger.error('KB article feedback error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// FAQ
// @route   GET /api/v1/kb/faqs
// @desc    Get FAQs
// @access  Public/Private
router.get('/faqs', async (req, res) => {
  try {
    const { category, popular, limit = 20 } = req.query;

    let query = {};
    if (category) query.category = category;
    if (popular === 'true') query.isPopular = true;

    if (req.user?.company) {
      query.company = req.user.company;
    }

    const faqs = await FAQ.find(query)
      .populate('category', 'name')
      .sort({ order: 1, 'analytics.views': -1 })
      .limit(limit * 1);

    res.json({
      success: true,
      count: faqs.length,
      data: faqs
    });

  } catch (error) {
    logger.error('Get FAQs error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/kb/faqs
// @desc    Create FAQ
// @access  Private (Admin, Content Manager)
router.post('/faqs', protect, authorize(['admin', 'content_manager']), async (req, res) => {
  try {
    const faqId = `KB-FAQ-${Date.now()}`;

    const faq = await FAQ.create({
      ...req.body,
      faqId,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: faq
    });

  } catch (error) {
    logger.error('Create FAQ error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Search Knowledge Base
// @route   GET /api/v1/kb/search
// @desc    Search knowledge base
// @access  Public/Private
router.get('/search', async (req, res) => {
  try {
    const { q, type = 'all', page = 1, limit = 20 } = req.query;

    if (!q) {
      return res.status(400).json({ success: false, message: 'Query required' });
    }

    const searchRegex = new RegExp(q, 'i');
    const companyQuery = req.user?.company ? { company: req.user.company } : {};

    const results = { articles: [], faqs: [], glossary: [] };

    // Search articles
    if (type === 'all' || type === 'articles') {
      results.articles = await KBArticle.find({
        ...companyQuery,
        status: 'published',
        $or: [
          { title: searchRegex },
          { summary: searchRegex },
          { content: searchRegex },
          { tags: { $in: [searchRegex] } },
          { keywords: { $in: [searchRegex] } }
        ]
      })
        .select('title slug summary category')
        .populate('category', 'name slug')
        .limit(limit * 1);
    }

    // Search FAQs
    if (type === 'all' || type === 'faqs') {
      results.faqs = await FAQ.find({
        ...companyQuery,
        $or: [
          { question: searchRegex },
          { answer: searchRegex }
        ]
      })
        .select('question answer category')
        .populate('category', 'name')
        .limit(limit * 1);
    }

    // Search glossary
    if (type === 'all' || type === 'glossary') {
      results.glossary = await KBGlossary.find({
        ...companyQuery,
        $or: [
          { term: searchRegex },
          { definition: searchRegex }
        ]
      }).limit(limit * 1);
    }

    const total = results.articles.length + results.faqs.length + results.glossary.length;

    res.json({
      success: true,
      count: total,
      query: q,
      data: results
    });

  } catch (error) {
    logger.error('Search KB error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Dashboard
// @route   GET /api/v1/kb/dashboard
// @desc    Knowledge base dashboard
// @access  Private (Admin, Content Manager)
router.get('/dashboard', protect, authorize(['admin', 'content_manager']), async (req, res) => {
  try {
    const stats = await Promise.all([
      // Categories
      KBCategory.countDocuments({ company: req.user.company }),

      // Articles
      KBArticle.countDocuments({ company: req.user.company }),

      // Published articles
      KBArticle.countDocuments({ company: req.user.company, status: 'published' }),

      // Draft articles
      KBArticle.countDocuments({ company: req.user.company, status: 'draft' }),

      // FAQs
      FAQ.countDocuments({ company: req.user.company }),

      // Total views
      KBArticle.aggregate([
        { $match: { company: req.user.company._id } },
        { $group: { _id: null, total: { $sum: '$analytics.views' } } }
      ]),

      // Top articles
      KBArticle.find({ company: req.user.company })
        .select('title slug analytics.views')
        .sort({ 'analytics.views': -1 })
        .limit(5),

      // Recent articles
      KBArticle.find({ company: req.user.company })
        .populate('author', 'firstName lastName')
        .select('title status createdAt updatedAt')
        .sort({ createdAt: -1 })
        .limit(5)
    ]);

    res.json({
      success: true,
      data: {
        overview: {
          categories: stats[0],
          articles: stats[1],
          published: stats[2],
          drafts: stats[3],
          faqs: stats[4],
          totalViews: stats[5][0]?.total || 0
        },
        topArticles: stats[6],
        recentArticles: stats[7]
      }
    });

  } catch (error) {
    logger.error('KB dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
