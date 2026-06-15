const express = require('express');
const router = express.Router();
const { protect } = require('../middleware/auth');
const Review = require('../models/Review');
const logger = require('../utils/logger');

// GET /api/v1/reviews
router.get('/', protect, async (req, res) => {
  try {
    const reviews = await Review.find({ company: req.user.company })
      .populate('reviewer', 'name email')
      .sort({ createdAt: -1 });
    res.json({ success: true, count: reviews.length, data: reviews });
  } catch (error) {
    logger.error('Get reviews error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// GET /api/v1/reviews/:id
router.get('/:id', protect, async (req, res) => {
  try {
    const review = await Review.findById(req.params.id);
    if (!review) return res.status(404).json({ success: false, message: 'Review not found' });
    res.json({ success: true, data: review });
  } catch (error) {
    logger.error('Get review error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// POST /api/v1/reviews
router.post('/', protect, async (req, res) => {
  try {
    const review = await Review.create({
      ...req.body,
      reviewer: req.user._id,
      company: req.user.company,
    });
    res.status(201).json({ success: true, data: review });
  } catch (error) {
    logger.error('Create review error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
