const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const ApiKey = require('../models/ApiKey');
const crypto = require('crypto');
const logger = require('../utils/logger');

// GET /api/v1/api-keys
router.get('/', protect, authorize(['admin']), async (req, res) => {
  try {
    const keys = await ApiKey.find({ company: req.user.company }).select('-key').sort({ createdAt: -1 });
    res.json({ success: true, count: keys.length, data: keys });
  } catch (error) {
    logger.error('Get API keys error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// POST /api/v1/api-keys — generate new key
router.post('/', protect, authorize(['admin']), async (req, res) => {
  try {
    const rawKey = crypto.randomBytes(32).toString('hex');
    const apiKey = await ApiKey.create({
      ...req.body,
      key: rawKey,
      createdBy: req.user._id,
      company: req.user.company,
    });
    // Return the raw key once — it won't be shown again
    res.status(201).json({ success: true, data: { ...apiKey.toObject(), key: rawKey } });
  } catch (error) {
    logger.error('Create API key error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// DELETE /api/v1/api-keys/:id
router.delete('/:id', protect, authorize(['admin']), async (req, res) => {
  try {
    const key = await ApiKey.findByIdAndDelete(req.params.id);
    if (!key) return res.status(404).json({ success: false, message: 'API key not found' });
    res.json({ success: true, message: 'API key revoked' });
  } catch (error) {
    logger.error('Delete API key error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
