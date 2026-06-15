//
/**
 * ============================================
 * 🛒 Procurement Routes - إدارة المشتريات
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { PurchaseRequisition, PurchaseOrder, Vendor, Quote } = require('../models/Procurement');
const logger = require('../utils/logger');

// @route   GET /api/v1/procurement/requisitions
// @desc    Get purchase requisitions
// @access  Private (Procurement, Admin)
router.get('/requisitions', protect, authorize(['admin', 'supervisor', 'procurement']), async (req, res) => {
  try {
    const { status, department, priority } = req.query;
    
    let query = { company: req.user.company };
    if (status) query.status = status;
    if (department) query.department = department;
    if (priority) query.priority = priority;

    const requisitions = await PurchaseRequisition.find(query)
      .populate('requestedBy', 'firstName lastName')
      .populate('convertedPO', 'poNumber')
      .sort({ requestDate: -1 });

    res.json({
      success: true,
      count: requisitions.length,
      data: requisitions
    });

  } catch (error) {
    logger.error('Get requisitions error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/procurement/requisitions
// @desc    Create purchase requisition
// @access  Private
router.post('/requisitions', protect, async (req, res) => {
  try {
    const prNumber = `PR-${Date.now()}`;
    
    // Calculate total
    const items = req.body.items || [];
    const totalEstimatedValue = items.reduce((sum, item) => 
      sum + ((item.estimatedUnitPrice || 0) * (item.quantity || 0)), 0);

    const requisition = await PurchaseRequisition.create({
      ...req.body,
      prNumber,
      totalEstimatedValue,
      requestedBy: req.user.id,
      company: req.user.company,
      status: 'submitted'
    });

    res.status(201).json({
      success: true,
      message: 'Requisition submitted',
      data: requisition
    });

  } catch (error) {
    logger.error('Create requisition error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/procurement/requisitions/:id/approve
// @desc    Approve requisition
// @access  Private (Manager, Admin)
router.put('/requisitions/:id/approve', protect, authorize(['admin', 'supervisor']), async (req, res) => {
  try {
    const { comments } = req.body;

    const requisition = await PurchaseRequisition.findByIdAndUpdate(
      req.params.id,
      {
        status: 'approved',
        $push: {
          approvalFlow: {
            level: 1,
            approver: req.user.id,
            role: req.user.role,
            status: 'approved',
            comments,
            actionDate: new Date()
          }
        }
      },
      { new: true }
    );

    if (!requisition) {
      return res.status(404).json({ success: false, message: 'Requisition not found' });
    }

    res.json({
      success: true,
      message: 'Requisition approved',
      data: requisition
    });

  } catch (error) {
    logger.error('Approve requisition error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Purchase Orders
// @route   GET /api/v1/procurement/purchase-orders
// @desc    Get purchase orders
// @access  Private (Procurement, Admin, Accountant)
router.get('/purchase-orders', protect, authorize(['admin', 'supervisor', 'procurement', 'accountant']), async (req, res) => {
  try {
    const { status, vendor, from, to } = req.query;
    
    let query = { company: req.user.company };
    if (status) query.status = status;
    if (vendor) query.vendor = vendor;
    if (from && to) {
      query.createdAt = {
        $gte: new Date(from),
        $lte: new Date(to)
      };
    }

    const pos = await PurchaseOrder.find(query)
      .populate('vendor', 'name vendorCode')
      .populate('requisition', 'prNumber')
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: pos.length,
      data: pos
    });

  } catch (error) {
    logger.error('Get POs error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/procurement/purchase-orders
// @desc    Create purchase order
// @access  Private (Procurement, Admin)
router.post('/purchase-orders', protect, authorize(['admin', 'supervisor', 'procurement']), async (req, res) => {
  try {
    const poNumber = `PO-${Date.now()}`;
    
    // Calculate totals
    const items = req.body.items || [];
    const subtotal = items.reduce((sum, item) => {
      const itemTotal = (item.unitPrice * item.quantity);
      const discount = itemTotal * ((item.discount?.percentage || 0) / 100);
      return sum + (itemTotal - discount);
    }, 0);
    
    const total = subtotal + (req.body.totals?.tax || 0) + (req.body.totals?.shipping || 0);

    const po = await PurchaseOrder.create({
      ...req.body,
      poNumber,
      totals: {
        ...req.body.totals,
        subtotal,
        total
      },
      status: 'sent',
      createdBy: req.user.id,
      company: req.user.company
    });

    // Update requisition status if linked
    if (req.body.requisition) {
      await PurchaseRequisition.findByIdAndUpdate(
        req.body.requisition,
        { status: 'converted_to_po', convertedPO: po._id }
      );
    }

    res.status(201).json({
      success: true,
      message: 'Purchase order created',
      data: po
    });

  } catch (error) {
    logger.error('Create PO error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/procurement/purchase-orders/:id/receive
// @desc    Record goods receipt
// @access  Private (Warehouse, Procurement)
router.put('/purchase-orders/:id/receive', protect, authorize(['admin', 'supervisor', 'procurement', 'warehouse_staff']), async (req, res) => {
  try {
    const { items, inspectionResult, notes } = req.body;

    const po = await PurchaseOrder.findById(req.params.id);
    if (!po) {
      return res.status(404).json({ success: false, message: 'PO not found' });
    }

    // Update received quantities
    items.forEach(receivedItem => {
      const poItem = po.items.find(i => i._id.toString() === receivedItem.itemId);
      if (poItem) {
        poItem.receivedQuantity += receivedItem.quantity;
        if (poItem.receivedQuantity >= poItem.quantity) {
          poItem.status = 'received';
        } else {
          poItem.status = 'partial';
        }
      }
    });

    // Check if all items received
    const allReceived = po.items.every(i => i.status === 'received');
    const partialReceived = po.items.some(i => i.status === 'partial');
    
    po.status = allReceived ? 'received' : (partialReceived ? 'partially_received' : po.status);
    po.receipt = {
      receivedBy: req.user.id,
      receivedDate: new Date(),
      inspectionResult,
      notes
    };

    await po.save();

    res.json({
      success: true,
      message: 'Goods received recorded',
      data: po
    });

  } catch (error) {
    logger.error('Receive goods error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Vendors
// @route   GET /api/v1/procurement/vendors
// @desc    Get vendors
// @access  Private
router.get('/vendors', protect, async (req, res) => {
  try {
    const { status, category, tier } = req.query;
    
    let query = { company: req.user.company };
    if (status) query['classification.status'] = status;
    if (category) query.category = { $in: [category] };
    if (tier) query['classification.tier'] = tier;

    const vendors = await Vendor.find(query)
      .sort({ 'evaluation.overallScore': -1 });

    res.json({
      success: true,
      count: vendors.length,
      data: vendors
    });

  } catch (error) {
    logger.error('Get vendors error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/procurement/vendors
// @desc    Add vendor
// @access  Private (Procurement, Admin)
router.post('/vendors', protect, authorize(['admin', 'supervisor', 'procurement']), async (req, res) => {
  try {
    const vendorCode = `V-${Date.now().toString().slice(-6)}`;
    
    const vendor = await Vendor.create({
      ...req.body,
      vendorCode,
      company: req.user.company,
      createdBy: req.user.id
    });

    res.status(201).json({
      success: true,
      data: vendor
    });

  } catch (error) {
    logger.error('Create vendor error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/procurement/vendors/:id/evaluate
// @desc    Evaluate vendor
// @access  Private (Procurement, Admin)
router.put('/vendors/:id/evaluate', protect, authorize(['admin', 'supervisor', 'procurement']), async (req, res) => {
  try {
    const { qualityScore, deliveryScore, priceScore, serviceScore, comments } = req.body;

    const overallScore = Math.round((qualityScore + deliveryScore + priceScore + serviceScore) / 4);

    const vendor = await Vendor.findByIdAndUpdate(
      req.params.id,
      {
        evaluation: {
          qualityScore,
          deliveryScore,
          priceScore,
          serviceScore,
          overallScore,
          lastEvaluation: new Date(),
          nextEvaluation: new Date(Date.now() + 180 * 24 * 60 * 60 * 1000) // 6 months
        },
        'classification.tier': overallScore >= 80 ? 'preferred' : (overallScore >= 60 ? 'approved' : 'provisional'),
        updatedAt: new Date()
      },
      { new: true }
    );

    if (!vendor) {
      return res.status(404).json({ success: false, message: 'Vendor not found' });
    }

    res.json({
      success: true,
      message: 'Vendor evaluated',
      data: vendor
    });

  } catch (error) {
    logger.error('Evaluate vendor error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Dashboard
// @route   GET /api/v1/procurement/dashboard
// @desc    Procurement dashboard
// @access  Private (Procurement, Admin)
router.get('/dashboard', protect, authorize(['admin', 'supervisor', 'procurement', 'accountant']), async (req, res) => {
  try {
    const today = new Date();
    const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

    const stats = await Promise.all([
      // Pending requisitions
      PurchaseRequisition.countDocuments({
        company: req.user.company,
        status: { $in: ['submitted', 'under_review'] }
      }),
      
      // Open POs
      PurchaseOrder.countDocuments({
        company: req.user.company,
        status: { $in: ['sent', 'acknowledged', 'confirmed', 'partially_received'] }
      }),
      
      // Active vendors
      Vendor.countDocuments({
        company: req.user.company,
        'classification.status': 'active'
      }),
      
      // Monthly spend
      PurchaseOrder.aggregate([
        {
          $match: {
            company: req.user.company._id,
            createdAt: { $gte: startOfMonth }
          }
        },
        {
          $group: {
            _id: null,
            totalSpend: { $sum: '$totals.total' }
          }
        }
      ]),
      
      // POs by status
      PurchaseOrder.aggregate([
        { $match: { company: req.user.company._id } },
        { $group: { _id: '$status', count: { $sum: 1 }, value: { $sum: '$totals.total' } } }
      ]),
      
      // Top vendors by spend
      PurchaseOrder.aggregate([
        { $match: { company: req.user.company._id } },
        {
          $group: {
            _id: '$vendor',
            totalSpend: { $sum: '$totals.total' },
            orders: { $sum: 1 }
          }
        },
        { $sort: { totalSpend: -1 } },
        { $limit: 5 },
        {
          $lookup: {
            from: 'vendors',
            localField: '_id',
            foreignField: '_id',
            as: 'vendor'
          }
        },
        { $unwind: '$vendor' }
      ])
    ]);

    res.json({
      success: true,
      data: {
        pendingRequisitions: stats[0],
        openPOs: stats[1],
        activeVendors: stats[2],
        monthlySpend: stats[3][0]?.totalSpend || 0,
        posByStatus: stats[4],
        topVendors: stats[5]
      }
    });

  } catch (error) {
    logger.error('Procurement dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
