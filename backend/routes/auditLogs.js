const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const AuditLog = require('../models/AuditLog');
const logger = require('../utils/logger');

// GET /api/v1/audit-logs
router.get('/', protect, authorize(['admin', 'compliance', 'manager']), async (req, res) => {
  try {
    const { page = 1, limit = 50, action, from, to } = req.query;
    const query = { company: req.user.company };
    if (action) query.action = action;
    if (from && to) query.timestamp = { $gte: new Date(from), $lte: new Date(to) };

    const logs = await AuditLog.find(query)
      .populate('performedBy.user', 'name email')
      .sort({ timestamp: -1 })
      .skip((page - 1) * limit)
      .limit(Number(limit));

    const total = await AuditLog.countDocuments(query);
    res.json({ success: true, count: logs.length, total, data: logs });
  } catch (error) {
    logger.error('Get audit logs error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// GET /api/v1/audit-logs/:id
router.get('/:id', protect, authorize(['admin', 'compliance']), async (req, res) => {
  try {
    const log = await AuditLog.findById(req.params.id).populate('performedBy.user', 'name email');
    if (!log) return res.status(404).json({ success: false, message: 'Audit log not found' });
    res.json({ success: true, data: log });
  } catch (error) {
    logger.error('Get audit log error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
