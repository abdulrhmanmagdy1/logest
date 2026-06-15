//
/**
 * ============================================
 * 🔍 Search Routes - نقاط نهاية البحث
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const searchService = require('../services/searchService');
const logger = require('../utils/logger');

// @route   GET /api/v1/search
// @desc    Global search across all entities
// @access  Private
router.get('/', protect, async (req, res) => {
  try {
    const { q: query, page = 1, limit = 20 } = req.query;

    if (!query) {
      return res.status(400).json({
        success: false,
        message: 'Search query is required'
      });
    }

    const results = await searchService.globalSearch(query, {
      page: parseInt(page),
      limit: parseInt(limit)
    });

    res.json(results);

  } catch (error) {
    logger.error('Global search error:', error);
    res.status(500).json({ success: false, message: 'Search failed' });
  }
});

// @route   GET /api/v1/search/shipments
// @desc    Search shipments
// @access  Private
router.get('/shipments', protect, async (req, res) => {
  try {
    const { q: query, status, city, dateFrom, dateTo, page = 1, limit = 20 } = req.query;

    const filters = {};
    if (status) filters.status = status;
    if (city) filters.city = city;
    if (dateFrom || dateTo) {
      filters.dateRange = {
        start: dateFrom,
        end: dateTo
      };
    }

    const results = await searchService.searchShipments(query, filters, {
      page: parseInt(page),
      limit: parseInt(limit)
    });

    res.json(results);

  } catch (error) {
    logger.error('Shipment search error:', error);
    res.status(500).json({ success: false, message: 'Search failed' });
  }
});

// @route   GET /api/v1/search/suggest
// @desc    Auto-suggest search
// @access  Private
router.get('/suggest', protect, async (req, res) => {
  try {
    const { q: query, field = 'trackingNumber' } = req.query;

    if (!query || query.length < 2) {
      return res.json({
        success: true,
        suggestions: []
      });
    }

    const suggestions = await searchService.suggest(query, field);

    res.json(suggestions);

  } catch (error) {
    logger.error('Search suggest error:', error);
    res.status(500).json({ success: false, message: 'Suggest failed' });
  }
});

// @route   GET /api/v1/search/analytics
// @desc    Get search analytics (Admin only)
// @access  Private (Admin)
router.get('/analytics', protect, authorize(['admin']), async (req, res) => {
  try {
    const { startDate, endDate } = req.query;

    const analytics = await searchService.getSearchAnalytics(
      new Date(startDate || Date.now() - 30 * 24 * 60 * 60 * 1000),
      new Date(endDate || Date.now())
    );

    res.json(analytics);

  } catch (error) {
    logger.error('Search analytics error:', error);
    res.status(500).json({ success: false, message: 'Analytics failed' });
  }
});

// @route   POST /api/v1/search/reindex
// @desc    Reindex all data (Admin only)
// @access  Private (Admin)
router.post('/reindex', protect, authorize(['admin']), async (req, res) => {
  try {
    const result = await searchService.reindexAll();

    res.json({
      success: true,
      message: 'Reindex completed',
      ...result
    });

  } catch (error) {
    logger.error('Reindex error:', error);
    res.status(500).json({ success: false, message: 'Reindex failed' });
  }
});

// Saved Searches
// @route   GET /api/v1/search/saved
// @desc    Get saved searches
// @access  Private
router.get('/saved', protect, async (req, res) => {
  try {
    const { SavedSearch } = require('../models/SearchIndex');
    const searches = await SavedSearch.find({
      $or: [
        { owner: req.user.id },
        { 'sharedWith.user': req.user.id },
        { isPublic: true, company: req.user.company }
      ],
      company: req.user.company
    }).populate('owner', 'firstName lastName');

    res.json({
      success: true,
      count: searches.length,
      data: searches
    });

  } catch (error) {
    logger.error('Get saved searches error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/search/saved
// @desc    Create saved search
// @access  Private
router.post('/saved', protect, async (req, res) => {
  try {
    const { SavedSearch } = require('../models/SearchIndex');
    const searchId = `SEARCH-${Date.now()}`;

    const saved = await SavedSearch.create({
      ...req.body,
      searchId,
      owner: req.user.id,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: saved
    });

  } catch (error) {
    logger.error('Create saved search error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/search/saved/:id/execute
// @desc    Execute saved search
// @access  Private
router.post('/saved/:id/execute', protect, async (req, res) => {
  try {
    const { SavedSearch } = require('../models/SearchIndex');
    const saved = await SavedSearch.findOne({
      _id: req.params.id,
      company: req.user.company
    });

    if (!saved) {
      return res.status(404).json({ success: false, message: 'Saved search not found' });
    }

    // Update usage
    saved.usage.executed += 1;
    saved.usage.lastExecuted = new Date();
    await saved.save();

    // Execute search
    const results = await searchService.globalSearch(saved.query, {
      filters: saved.filters,
      sort: saved.sort,
      ...req.body
    });

    res.json({
      success: true,
      data: results
    });

  } catch (error) {
    logger.error('Execute saved search error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Search Suggestions & Autocomplete
// @route   GET /api/v1/search/suggestions
// @desc    Get search suggestions
// @access  Private
router.get('/suggestions', protect, async (req, res) => {
  try {
    const { q, type } = req.query;

    if (!q || q.length < 2) {
      return res.json({ success: true, suggestions: [] });
    }

    const { SearchQuery } = require('../models/SearchIndex');

    // Get similar queries from history
    const suggestions = await SearchQuery.aggregate([
      {
        $match: {
          company: req.user.company._id,
          query: { $regex: q, $options: 'i' },
          query: { $ne: q }
        }
      },
      { $group: { _id: '$query', count: { $sum: 1 } } },
      { $sort: { count: -1 } },
      { $limit: 10 },
      { $project: { query: '$_id', count: 1, _id: 0 } }
    ]);

    res.json({
      success: true,
      data: suggestions
    });

  } catch (error) {
    logger.error('Get suggestions error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Search Analytics
// @route   GET /api/v1/search/analytics
// @desc    Get search analytics
// @access  Private (Admin)
router.get('/analytics', protect, authorize(['admin']), async (req, res) => {
  try {
    const { from, to } = req.query;

    const { SearchQuery, SearchAnalytics } = require('../models/SearchIndex');

    const stats = await Promise.all([
      // Total searches
      SearchQuery.countDocuments({
        company: req.user.company,
        timestamp: { $gte: new Date(from || Date.now() - 30 * 24 * 60 * 60 * 1000) }
      }),

      // Top queries
      SearchQuery.aggregate([
        {
          $match: {
            company: req.user.company._id,
            timestamp: { $gte: new Date(from || Date.now() - 7 * 24 * 60 * 60 * 1000) }
          }
        },
        { $group: { _id: '$query', count: { $sum: 1 } } },
        { $sort: { count: -1 } },
        { $limit: 20 }
      ]),

      // No result queries
      SearchQuery.find({
        company: req.user.company,
        'results.total': 0,
        timestamp: { $gte: new Date(from || Date.now() - 7 * 24 * 60 * 60 * 1000) }
      })
        .select('query timestamp')
        .sort({ timestamp: -1 })
        .limit(20),

      // Recent analytics
      SearchAnalytics.find({
        company: req.user.company,
        date: { $gte: new Date(from || Date.now() - 30 * 24 * 60 * 60 * 1000) }
      }).sort({ date: -1 }).limit(30)
    ]);

    res.json({
      success: true,
      data: {
        totalSearches: stats[0],
        topQueries: stats[1],
        noResultQueries: stats[2],
        analytics: stats[3]
      }
    });

  } catch (error) {
    logger.error('Get search analytics error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
