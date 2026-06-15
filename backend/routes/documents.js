const express = require('express');
const router = express.Router();
const { protect } = require('../middleware/auth');
const Document = require('../models/Document');
const logger = require('../utils/logger');

// GET /api/v1/documents
router.get('/', protect, async (req, res) => {
  try {
    const documents = await Document.find({ company: req.user.company })
      .populate('uploadedBy', 'name email')
      .sort({ createdAt: -1 });
    res.json({ success: true, count: documents.length, data: documents });
  } catch (error) {
    logger.error('Get documents error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// GET /api/v1/documents/:id
router.get('/:id', protect, async (req, res) => {
  try {
    const doc = await Document.findById(req.params.id);
    if (!doc) return res.status(404).json({ success: false, message: 'Document not found' });
    res.json({ success: true, data: doc });
  } catch (error) {
    logger.error('Get document error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// POST /api/v1/documents
router.post('/', protect, async (req, res) => {
  try {
    const doc = await Document.create({
      ...req.body,
      uploadedBy: req.user._id,
      company: req.user.company,
    });
    res.status(201).json({ success: true, data: doc });
  } catch (error) {
    logger.error('Create document error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// DELETE /api/v1/documents/:id
router.delete('/:id', protect, async (req, res) => {
  try {
    const doc = await Document.findByIdAndDelete(req.params.id);
    if (!doc) return res.status(404).json({ success: false, message: 'Document not found' });
    res.json({ success: true, message: 'Document deleted' });
  } catch (error) {
    logger.error('Delete document error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
