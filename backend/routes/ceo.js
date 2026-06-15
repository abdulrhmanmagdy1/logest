const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const logger = require('../utils/logger');

// CEO dashboard — aggregated KPIs from multiple models
router.get('/dashboard', protect, authorize(['admin', 'ceo']), async (req, res) => {
  try {
    const Shipment = require('../models/Shipment');
    const Invoice = require('../models/Invoice');
    const User = require('../models/User');

    const now = new Date();
    const monthStart = new Date(now.getFullYear(), now.getMonth(), 1);

    const [totalShipments, monthlyShipments, activeDrivers] = await Promise.all([
      Shipment.countDocuments({ company: req.user.company }),
      Shipment.countDocuments({ company: req.user.company, createdAt: { $gte: monthStart } }),
      User.countDocuments({ company: req.user.company, role: 'driver', isActive: true }),
    ]);

    const revenueAgg = await Invoice.aggregate([
      { $match: { company: req.user.company, status: 'paid', createdAt: { $gte: monthStart } } },
      { $group: { _id: null, total: { $sum: '$totalAmount' } } },
    ]);

    res.json({
      success: true,
      data: {
        totalShipments,
        monthlyShipments,
        activeDrivers,
        monthlyRevenue: revenueAgg[0]?.total || 0,
      },
    });
  } catch (error) {
    logger.error('CEO dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// CEO KPI summary
router.get('/kpis', protect, authorize(['admin', 'ceo']), async (req, res) => {
  try {
    res.json({ success: true, data: { kpis: [] } });
  } catch (error) {
    logger.error('CEO KPIs error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
