const express = require('express');
const router = express.Router();
const { protect } = require('../middleware/auth');
const logger = require('../utils/logger');

// GET /api/v1/dashboard/stats
router.get('/stats', protect, async (req, res) => {
  try {
    const Shipment  = require('../models/Shipment');
    const Truck     = require('../models/Truck');
    const User      = require('../models/User');
    const Invoice   = require('../models/Invoice');

    const company = req.user.company;

    const now       = new Date();
    const monthStart = new Date(now.getFullYear(), now.getMonth(), 1);

    const [
      totalShipments,
      activeShipments,
      totalTrucks,
      activeTrucks,
      totalDrivers,
      monthlyShipments,
      revenueAgg,
    ] = await Promise.all([
      Shipment.countDocuments(company ? { company } : {}),
      Shipment.countDocuments({ ...(company && { company }), status: { $in: ['in_transit', 'picked_up'] } }),
      Truck.countDocuments(company ? { company } : {}),
      Truck.countDocuments({ ...(company && { company }), status: 'active' }),
      User.countDocuments({ ...(company && { company }), role: 'driver', status: 'active' }),
      Shipment.countDocuments({ ...(company && { company }), createdAt: { $gte: monthStart } }),
      Invoice.aggregate([
        { $match: { ...(company && { company }), status: 'paid', createdAt: { $gte: monthStart } } },
        { $group: { _id: null, total: { $sum: '$totalAmount' } } },
      ]),
    ]);

    res.json({
      success: true,
      data: {
        totalShipments,
        activeShipments,
        totalTrucks,
        activeTrucks,
        totalDrivers,
        monthlyShipments,
        monthlyRevenue: revenueAgg[0]?.total ?? 0,
      },
    });
  } catch (error) {
    logger.error('Dashboard stats error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
