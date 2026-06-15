//
/**
 * ============================================
 * 👤 Customer Portal Routes - بوابة العملاء
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { PortalUser, SavedQuote, ShipmentTemplate, CustomerActivity, CustomerTicket } = require('../models/CustomerPortal');
const { Shipment } = require('../models/Shipment');
const { Invoice } = require('../models/Invoice');
const logger = require('../utils/logger');

// Portal User Profile
// @route   GET /api/v1/portal/profile
// @desc    Get portal user profile
// @access  Private (Customer)
router.get('/profile', protect, authorize(['customer']), async (req, res) => {
  try {
    let profile = await PortalUser.findOne({
      user: req.user.id,
      company: req.user.company
    });

    if (!profile) {
      // Create profile if doesn't exist
      profile = await PortalUser.create({
        user: req.user.id,
        company: req.user.company,
        portalRole: 'user'
      });
    }

    res.json({
      success: true,
      data: profile
    });

  } catch (error) {
    logger.error('Get portal profile error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/portal/profile
// @desc    Update portal profile
// @access  Private (Customer)
router.put('/profile', protect, authorize(['customer']), async (req, res) => {
  try {
    const profile = await PortalUser.findOneAndUpdate(
      { user: req.user.id, company: req.user.company },
      {
        ...req.body,
        updatedAt: new Date()
      },
      { new: true, upsert: true }
    );

    res.json({
      success: true,
      data: profile
    });

  } catch (error) {
    logger.error('Update portal profile error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Saved Addresses
// @route   GET /api/v1/portal/addresses
// @desc    Get saved addresses
// @access  Private (Customer)
router.get('/addresses', protect, authorize(['customer']), async (req, res) => {
  try {
    const profile = await PortalUser.findOne({
      user: req.user.id,
      company: req.user.company
    });

    res.json({
      success: true,
      data: profile?.savedAddresses || []
    });

  } catch (error) {
    logger.error('Get addresses error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/portal/addresses
// @desc    Add saved address
// @access  Private (Customer)
router.post('/addresses', protect, authorize(['customer']), async (req, res) => {
  try {
    const profile = await PortalUser.findOneAndUpdate(
      { user: req.user.id, company: req.user.company },
      {
        $push: { savedAddresses: req.body },
        updatedAt: new Date()
      },
      { new: true }
    );

    res.status(201).json({
      success: true,
      data: profile.savedAddresses
    });

  } catch (error) {
    logger.error('Add address error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Shipments
// @route   GET /api/v1/portal/shipments
// @desc    Get customer shipments
// @access  Private (Customer)
router.get('/shipments', protect, authorize(['customer']), async (req, res) => {
  try {
    const { status, from, to, page = 1, limit = 20 } = req.query;

    let query = { customer: req.user.id };
    if (status) query.status = status;
    if (from && to) {
      query.createdAt = {
        $gte: new Date(from),
        $lte: new Date(to)
      };
    }

    const shipments = await Shipment.find(query)
      .populate('driver', 'firstName lastName phone')
      .populate('vehicle', 'licensePlate')
      .sort({ createdAt: -1 })
      .limit(limit * 1)
      .skip((page - 1) * limit);

    const count = await Shipment.countDocuments(query);

    res.json({
      success: true,
      count,
      pagination: {
        page: parseInt(page),
        pages: Math.ceil(count / limit),
        total: count
      },
      data: shipments
    });

  } catch (error) {
    logger.error('Get customer shipments error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/portal/shipments
// @desc    Create shipment from portal
// @access  Private (Customer)
router.post('/shipments', protect, authorize(['customer']), async (req, res) => {
  try {
    // Check permission
    const profile = await PortalUser.findOne({
      user: req.user.id,
      company: req.user.company
    });

    if (!profile?.permissions?.canCreateShipments) {
      return res.status(403).json({
        success: false,
        message: 'Permission denied'
      });
    }

    const trackingNumber = `SHP-${Date.now()}`;

    const shipment = await Shipment.create({
      ...req.body,
      trackingNumber,
      customer: req.user.id,
      status: 'pending',
      source: 'portal'
    });

    // Log activity
    await CustomerActivity.create({
      customer: req.user.id,
      company: req.user.company,
      activity: {
        type: 'shipment_created',
        details: { shipmentId: shipment._id, trackingNumber }
      }
    });

    res.status(201).json({
      success: true,
      data: shipment
    });

  } catch (error) {
    logger.error('Create shipment error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Quotes
// @route   GET /api/v1/portal/quotes
// @desc    Get saved quotes
// @access  Private (Customer)
router.get('/quotes', protect, authorize(['customer']), async (req, res) => {
  try {
    const quotes = await SavedQuote.find({
      customer: req.user.id,
      company: req.user.company
    }).sort({ createdAt: -1 });

    res.json({
      success: true,
      count: quotes.length,
      data: quotes
    });

  } catch (error) {
    logger.error('Get quotes error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/portal/quotes
// @desc    Request quote
// @access  Private (Customer)
router.post('/quotes', protect, authorize(['customer']), async (req, res) => {
  try {
    const quoteId = `QT-${Date.now()}`;

    const quote = await SavedQuote.create({
      ...req.body,
      quoteId,
      customer: req.user.id,
      company: req.user.company,
      expiresAt: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000) // 7 days
    });

    // Log activity
    await CustomerActivity.create({
      customer: req.user.id,
      company: req.user.company,
      activity: {
        type: 'quote_requested',
        details: { quoteId: quote._id }
      }
    });

    res.status(201).json({
      success: true,
      data: quote
    });

  } catch (error) {
    logger.error('Create quote error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Invoices
// @route   GET /api/v1/portal/invoices
// @desc    Get customer invoices
// @access  Private (Customer)
router.get('/invoices', protect, authorize(['customer']), async (req, res) => {
  try {
    const { status, page = 1, limit = 20 } = req.query;

    let query = { customer: req.user.id };
    if (status) query.status = status;

    const invoices = await Invoice.find(query)
      .populate('shipment', 'trackingNumber')
      .sort({ createdAt: -1 })
      .limit(limit * 1)
      .skip((page - 1) * limit);

    const count = await Invoice.countDocuments(query);

    res.json({
      success: true,
      count,
      pagination: {
        page: parseInt(page),
        pages: Math.ceil(count / limit),
        total: count
      },
      data: invoices
    });

  } catch (error) {
    logger.error('Get invoices error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Support Tickets
// @route   GET /api/v1/portal/tickets
// @desc    Get customer tickets
// @access  Private (Customer)
router.get('/tickets', protect, authorize(['customer']), async (req, res) => {
  try {
    const { status } = req.query;

    let query = { customer: req.user.id, company: req.user.company };
    if (status) query.status = status;

    const tickets = await CustomerTicket.find(query)
      .populate('relatedShipment', 'trackingNumber')
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: tickets.length,
      data: tickets
    });

  } catch (error) {
    logger.error('Get tickets error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/portal/tickets
// @desc    Create support ticket
// @access  Private (Customer)
router.post('/tickets', protect, authorize(['customer']), async (req, res) => {
  try {
    const ticketId = `TKT-${Date.now()}`;

    const ticket = await CustomerTicket.create({
      ...req.body,
      ticketId,
      customer: req.user.id,
      company: req.user.company,
      messages: [{
        sender: req.user.id,
        senderType: 'customer',
        message: req.body.description,
        timestamp: new Date()
      }]
    });

    res.status(201).json({
      success: true,
      data: ticket
    });

  } catch (error) {
    logger.error('Create ticket error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/portal/tickets/:id/messages
// @desc    Add message to ticket
// @access  Private (Customer)
router.post('/tickets/:id/messages', protect, authorize(['customer']), async (req, res) => {
  try {
    const { message } = req.body;

    const ticket = await CustomerTicket.findOneAndUpdate(
      { _id: req.params.id, customer: req.user.id },
      {
        $push: {
          messages: {
            sender: req.user.id,
            senderType: 'customer',
            message,
            timestamp: new Date()
          }
        },
        status: 'open',
        updatedAt: new Date()
      },
      { new: true }
    );

    if (!ticket) {
      return res.status(404).json({ success: false, message: 'Ticket not found' });
    }

    res.json({
      success: true,
      data: ticket
    });

  } catch (error) {
    logger.error('Add message error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Dashboard
// @route   GET /api/v1/portal/dashboard
// @desc    Customer dashboard
// @access  Private (Customer)
router.get('/dashboard', protect, authorize(['customer']), async (req, res) => {
  try {
    const today = new Date();
    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

    const stats = await Promise.all([
      // Active shipments
      Shipment.countDocuments({
        customer: req.user.id,
        status: { $nin: ['delivered', 'cancelled'] }
      }),

      // Total shipments this month
      Shipment.countDocuments({
        customer: req.user.id,
        createdAt: { $gte: startOfMonth }
      }),

      // Pending invoices
      Invoice.aggregate([
        { $match: { customer: req.user._id, status: 'pending' } },
        { $group: { _id: null, total: { $sum: '$total' }, count: { $sum: 1 } } }
      ]),

      // Recent shipments
      Shipment.find({ customer: req.user.id })
        .sort({ createdAt: -1 })
        .limit(5),

      // Open tickets
      CustomerTicket.countDocuments({
        customer: req.user.id,
        status: { $in: ['open', 'in_progress'] }
      })
    ]);

    res.json({
      success: true,
      data: {
        activeShipments: stats[0],
        monthlyShipments: stats[1],
        pendingInvoices: stats[2][0] || { total: 0, count: 0 },
        recentShipments: stats[3],
        openTickets: stats[4]
      }
    });

  } catch (error) {
    logger.error('Customer dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Tracking (Public endpoint)
// @route   GET /api/v1/portal/track/:trackingNumber
// @desc    Track shipment (no auth required)
// @access  Public
router.get('/track/:trackingNumber', async (req, res) => {
  try {
    const shipment = await Shipment.findOne({
      trackingNumber: req.params.trackingNumber
    })
      .select('trackingNumber status origin destination timeline estimatedDelivery currentLocation driver')
      .populate('driver', 'firstName lastName');

    if (!shipment) {
      return res.status(404).json({ success: false, message: 'Shipment not found' });
    }

    res.json({
      success: true,
      data: shipment
    });

  } catch (error) {
    logger.error('Track error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
