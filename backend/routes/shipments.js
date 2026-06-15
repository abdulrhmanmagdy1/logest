/**
 * ============================================
 * 📦 Shipments Routes - نظام إدهام
 * Shipment/Cargo management endpoints
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { body, query, validationResult } = require('express-validator');
const { protect, authorize } = require('../middleware/auth');
const Shipment = require('../models/Shipment');
const Notification = require('../models/Notification');
const logger = require('../utils/logger');
const { io } = require('../server');

// Validation helper
const handleValidationErrors = (req, res, next) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({
      success: false,
      errors: errors.array()
    });
  }
  next();
};

// @route   GET /api/v1/shipments
// @desc    Get all shipments (with filters)
// @access  Private
router.get('/', protect, async (req, res) => {
  try {
    const {
      status,
      client,
      driver,
      startDate,
      endDate,
      trackingNumber,
      page = 1,
      limit = 20,
      sort = '-createdAt'
    } = req.query;

    // Build filter
    const filter = {};
    
    // Role-based filtering
    if (req.user.role === 'client') {
      filter.client = req.user.id;
    } else if (req.user.role === 'driver') {
      filter.driver = req.user.id;
    }
    
    // Apply additional filters
    if (status) filter.status = status;
    if (client && ['admin', 'supervisor', 'employee'].includes(req.user.role)) {
      filter.client = client;
    }
    if (driver && ['admin', 'supervisor'].includes(req.user.role)) {
      filter.driver = driver;
    }
    if (trackingNumber) {
      filter.trackingNumber = { $regex: trackingNumber, $options: 'i' };
    }
    if (startDate || endDate) {
      filter.createdAt = {};
      if (startDate) filter.createdAt.$gte = new Date(startDate);
      if (endDate) filter.createdAt.$lte = new Date(endDate);
    }

    // Pagination
    const skip = (parseInt(page) - 1) * parseInt(limit);

    // Execute query
    const shipments = await Shipment.find(filter)
      .populate('client', 'firstName lastName phone companyName')
      .populate('driver', 'firstName lastName phone driverInfo.rating')
      .populate('truck', 'plateNumber type')
      .sort(sort)
      .skip(skip)
      .limit(parseInt(limit));

    // Get total count
    const total = await Shipment.countDocuments(filter);

    res.json({
      success: true,
      count: shipments.length,
      total,
      page: parseInt(page),
      pages: Math.ceil(total / parseInt(limit)),
      data: shipments
    });
  } catch (error) {
    logger.error('Get shipments error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error'
    });
  }
});

// @route   GET /api/v1/shipments/:id
// @desc    Get single shipment
// @access  Private
router.get('/:id', protect, async (req, res) => {
  try {
    const shipment = await Shipment.findById(req.params.id)
      .populate('client', 'firstName lastName phone email companyName address')
      .populate('driver', 'firstName lastName phone email driverInfo')
      .populate('truck', 'plateNumber type capacity refrigeration')
      .populate('createdBy', 'firstName lastName');

    if (!shipment) {
      return res.status(404).json({
        success: false,
        message: 'Shipment not found'
      });
    }

    // Check authorization
    if (req.user.role === 'client' && shipment.client._id.toString() !== req.user.id) {
      return res.status(403).json({
        success: false,
        message: 'Not authorized to view this shipment'
      });
    }

    if (req.user.role === 'driver' && shipment.driver?._id.toString() !== req.user.id) {
      return res.status(403).json({
        success: false,
        message: 'Not authorized to view this shipment'
      });
    }

    res.json({
      success: true,
      data: shipment
    });
  } catch (error) {
    logger.error('Get shipment error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error'
    });
  }
});

// @route   POST /api/v1/shipments
// @desc    Create new shipment
// @access  Private (Client, Admin, Employee)
router.post('/', protect, authorize('client', 'admin', 'employee', 'supervisor'), [
  body('cargo.type').notEmpty().withMessage('Cargo type is required'),
  body('cargo.description').notEmpty().withMessage('Cargo description is required'),
  body('cargo.weight.value').isNumeric().withMessage('Weight must be a number'),
  body('pickup.address.street').notEmpty().withMessage('Pickup street is required'),
  body('pickup.address.city').notEmpty().withMessage('Pickup city is required'),
  body('delivery.address.street').notEmpty().withMessage('Delivery street is required'),
  body('delivery.address.city').notEmpty().withMessage('Delivery city is required')
], handleValidationErrors, async (req, res) => {
  try {
    const shipmentData = {
      ...req.body,
      client: req.user.role === 'client' ? req.user.id : req.body.client,
      createdBy: req.user.id
    };

    const shipment = await Shipment.create(shipmentData);

    // Populate and return
    await shipment.populate('client', 'firstName lastName phone');

    // Create notification for client
    await Notification.create({
      user: shipment.client,
      type: 'shipment_created',
      title: 'تم إنشاء طلب نقل جديد',
      message: `تم إنشاء طلب النقل رقم ${shipment.trackingNumber} بنجاح`,
      data: {
        shipmentId: shipment._id,
        trackingNumber: shipment.trackingNumber
      }
    });

    logger.info(`New shipment created: ${shipment.trackingNumber} by ${req.user.id}`);

    res.status(201).json({
      success: true,
      message: 'Shipment created successfully',
      data: shipment
    });
  } catch (error) {
    logger.error('Create shipment error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error'
    });
  }
});

// @route   PUT /api/v1/shipments/:id
// @desc    Update shipment
// @access  Private
router.put('/:id', protect, async (req, res) => {
  try {
    let shipment = await Shipment.findById(req.params.id);

    if (!shipment) {
      return res.status(404).json({
        success: false,
        message: 'Shipment not found'
      });
    }

    // Check authorization
    const isAuthorized = 
      req.user.role === 'admin' ||
      req.user.role === 'supervisor' ||
      (req.user.role === 'client' && shipment.client.toString() === req.user.id && 
       ['pending', 'confirmed'].includes(shipment.status)) ||
      (req.user.role === 'driver' && shipment.driver?.toString() === req.user.id);

    if (!isAuthorized) {
      return res.status(403).json({
        success: false,
        message: 'Not authorized to update this shipment'
      });
    }

    // Update fields
    const allowedUpdates = ['cargo', 'pickup', 'delivery', 'priority', 'notes', 'clientNotes'];
    if (['admin', 'supervisor'].includes(req.user.role)) {
      allowedUpdates.push('driver', 'truck', 'pricing', 'status');
    }

    allowedUpdates.forEach(field => {
      if (req.body[field] !== undefined) {
        shipment[field] = req.body[field];
      }
    });

    shipment.updatedBy = req.user.id;
    await shipment.save();

    // Populate and return
    await shipment.populate('client driver truck');

    logger.info(`Shipment updated: ${shipment.trackingNumber} by ${req.user.id}`);

    res.json({
      success: true,
      message: 'Shipment updated successfully',
      data: shipment
    });
  } catch (error) {
    logger.error('Update shipment error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error'
    });
  }
});

// @route   PUT /api/v1/shipments/:id/status
// @desc    Update shipment status
// @access  Private
router.put('/:id/status', protect, [
  body('status').isIn([
    'pending', 'confirmed', 'assigned', 'in_transit', 'at_pickup', 
    'picked_up', 'on_the_way', 'at_delivery', 'delivered', 
    'completed', 'cancelled'
  ]).withMessage('Invalid status'),
  body('notes').optional().isString()
], handleValidationErrors, async (req, res) => {
  try {
    const { status, notes, location } = req.body;

    let shipment = await Shipment.findById(req.params.id);

    if (!shipment) {
      return res.status(404).json({
        success: false,
        message: 'Shipment not found'
      });
    }

    // Update status
    shipment.status = status;
    
    // Add status history entry
    shipment.statusHistory.push({
      status,
      timestamp: new Date(),
      location,
      notes,
      updatedBy: req.user.id
    });

    await shipment.save();

    // Emit real-time update
    io.to(`shipment:${shipment._id}`).emit('status_update', {
      shipmentId: shipment._id,
      status,
      timestamp: new Date(),
      location
    });

    // Create notifications
    const notificationTitle = {
      'confirmed': 'تم تأكيد الطلب',
      'assigned': 'تم تعيين سائق',
      'picked_up': 'تم استلام الشحنة',
      'on_the_way': 'الشحنة في الطريق',
      'delivered': 'تم تسليم الشحنة',
      'completed': 'تم إكمال الطلب',
      'cancelled': 'تم إلغاء الطلب'
    }[status];

    if (notificationTitle) {
      // Notify client
      await Notification.create({
        user: shipment.client,
        type: `shipment_${status}`,
        title: notificationTitle,
        message: `تحديث حالة الشحنة ${shipment.trackingNumber}: ${notificationTitle}`,
        data: {
          shipmentId: shipment._id,
          trackingNumber: shipment.trackingNumber,
          status
        }
      });
    }

    logger.info(`Shipment ${shipment.trackingNumber} status updated to ${status}`);

    res.json({
      success: true,
      message: 'Status updated successfully',
      data: shipment
    });
  } catch (error) {
    logger.error('Update status error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error'
    });
  }
});

// @route   DELETE /api/v1/shipments/:id
// @desc    Delete shipment
// @access  Private (Admin only)
router.delete('/:id', protect, authorize('admin'), async (req, res) => {
  try {
    const shipment = await Shipment.findById(req.params.id);

    if (!shipment) {
      return res.status(404).json({
        success: false,
        message: 'Shipment not found'
      });
    }

    await shipment.deleteOne();

    logger.info(`Shipment deleted: ${shipment.trackingNumber} by ${req.user.id}`);

    res.json({
      success: true,
      message: 'Shipment deleted successfully'
    });
  } catch (error) {
    logger.error('Delete shipment error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error'
    });
  }
});

// @route   GET /api/v1/shipments/:id/tracking
// @desc    Get shipment tracking updates
// @access  Public (with tracking number) or Private
router.get('/:id/tracking', async (req, res) => {
  try {
    const shipment = await Shipment.findById(req.params.id)
      .select('trackingNumber status statusHistory trackingUpdates pickup delivery route');

    if (!shipment) {
      return res.status(404).json({
        success: false,
        message: 'Shipment not found'
      });
    }

    res.json({
      success: true,
      data: {
        trackingNumber: shipment.trackingNumber,
        status: shipment.status,
        statusHistory: shipment.statusHistory,
        trackingUpdates: shipment.trackingUpdates,
        pickup: shipment.pickup,
        delivery: shipment.delivery,
        route: shipment.route
      }
    });
  } catch (error) {
    logger.error('Get tracking error:', error);
    res.status(500).json({
      success: false,
      message: 'Server error'
    });
  }
});

module.exports = router;
