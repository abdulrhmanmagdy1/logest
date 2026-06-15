// ============================================
// 📦 Shipment Controller
// ============================================

const Shipment = require('../models/Shipment');
const User = require('../models/User');
const { io } = require('../server');
const logger = require('../utils/logger');

// Generate tracking number
const generateTrackingNumber = () => {
  const prefix = 'EDH';
  const timestamp = Date.now().toString(36).toUpperCase();
  const random = Math.random().toString(36).substring(2, 5).toUpperCase();
  return `${prefix}-${timestamp}${random}`;
};

// @desc    Create new shipment
// @route   POST /api/v1/shipments
// @access  Private (Client)
exports.createShipment = async (req, res) => {
  try {
    const { cargo, pickup, delivery, scheduledDate } = req.body;
    
    // Generate tracking number
    const trackingNumber = generateTrackingNumber();
    
    // Calculate price (simplified)
    const basePrice = 100;
    const weightPrice = cargo.weight?.value ? cargo.weight.value * 0.5 : 0;
    const total = basePrice + weightPrice;
    
    const shipment = await Shipment.create({
      trackingNumber,
      cargo,
      pickup,
      delivery,
      createdBy: req.user.id,
      status: 'pending',
      pricing: {
        basePrice,
        weightPrice,
        total
      },
      scheduledDate
    });

    res.status(201).json({
      success: true,
      message: 'تم إنشاء الشحنة بنجاح',
      data: shipment
    });
  } catch (error) {
    logger.error('Create shipment error:', error);
    res.status(500).json({
      success: false,
      message: 'حدث خطأ في إنشاء الشحنة'
    });
  }
};

// @desc    Get all shipments
// @route   GET /api/v1/shipments
// @access  Private
exports.getShipments = async (req, res) => {
  try {
    const { status, page = 1, limit = 10 } = req.query;
    
    let query = {};
    
    // Filter by role
    if (req.user.role === 'client') {
      query.createdBy = req.user.id;
    } else if (req.user.role === 'driver') {
      query.assignedDriver = req.user.id;
    }
    
    // Filter by status
    if (status) {
      query.status = status;
    }
    
    const shipments = await Shipment.find(query)
      .populate('createdBy', 'firstName lastName email phone')
      .populate('assignedDriver', 'firstName lastName phone')
      .populate('assignedTruck', 'plateNumber make model')
      .sort({ createdAt: -1 })
      .limit(limit * 1)
      .skip((page - 1) * limit);
    
    const count = await Shipment.countDocuments(query);

    res.json({
      success: true,
      count: shipments.length,
      total: count,
      totalPages: Math.ceil(count / limit),
      currentPage: page,
      data: shipments
    });
  } catch (error) {
    logger.error('Get shipments error:', error);
    res.status(500).json({
      success: false,
      message: 'حدث خطأ في جلب الشحنات'
    });
  }
};

// @desc    Get single shipment
// @route   GET /api/v1/shipments/:id
// @access  Private
exports.getShipment = async (req, res) => {
  try {
    const shipment = await Shipment.findById(req.params.id)
      .populate('createdBy', 'firstName lastName email phone company')
      .populate('assignedDriver', 'firstName lastName phone avatar')
      .populate('assignedTruck', 'plateNumber make model type capacity');
    
    if (!shipment) {
      return res.status(404).json({
        success: false,
        message: 'الشحنة غير موجودة'
      });
    }

    // Check authorization
    if (req.user.role === 'client' && shipment.createdBy._id.toString() !== req.user.id) {
      return res.status(403).json({
        success: false,
        message: 'غير مصرح لك بعرض هذه الشحنة'
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
      message: 'حدث خطأ'
    });
  }
};

// @desc    Track shipment by tracking number
// @route   GET /api/v1/shipments/tracking/:trackingNumber
// @access  Public
exports.trackShipment = async (req, res) => {
  try {
    const shipment = await Shipment.findOne({
      trackingNumber: req.params.trackingNumber.toUpperCase()
    })
      .populate('assignedDriver', 'firstName lastName phone')
      .populate('statusHistory.updatedBy', 'firstName lastName role');
    
    if (!shipment) {
      return res.status(404).json({
        success: false,
        message: 'رقم التتبع غير موجود'
      });
    }

    res.json({
      success: true,
      data: shipment
    });
  } catch (error) {
    logger.error('Track shipment error:', error);
    res.status(500).json({
      success: false,
      message: 'حدث خطأ في التتبع'
    });
  }
};

// @desc    Update shipment status
// @route   PUT /api/v1/shipments/:id/status
// @access  Private (Driver, Admin, Supervisor)
exports.updateStatus = async (req, res) => {
  try {
    const { status, location, notes } = req.body;
    
    const shipment = await Shipment.findById(req.params.id);
    
    if (!shipment) {
      return res.status(404).json({
        success: false,
        message: 'الشحنة غير موجودة'
      });
    }

    // Update status
    shipment.status = status;
    shipment.statusHistory.push({
      status,
      timestamp: new Date(),
      location,
      updatedBy: req.user.id,
      notes
    });

    // Update actual dates based on status
    if (status === 'picked_up') {
      shipment.pickup.actualDate = new Date();
    } else if (status === 'delivered') {
      shipment.delivery.actualDate = new Date();
    }

    await shipment.save();

    // Emit real-time update
    io.to(`shipment:${shipment._id}`).emit('status_update', {
      shipmentId: shipment._id,
      trackingNumber: shipment.trackingNumber,
      status,
      timestamp: new Date(),
      location
    });

    res.json({
      success: true,
      message: 'تم تحديث الحالة بنجاح',
      data: shipment
    });
  } catch (error) {
    logger.error('Update status error:', error);
    res.status(500).json({
      success: false,
      message: 'حدث خطأ في تحديث الحالة'
    });
  }
};

// @desc    Assign driver to shipment
// @route   PUT /api/v1/shipments/:id/assign
// @access  Private (Admin, Supervisor)
exports.assignDriver = async (req, res) => {
  try {
    const { driverId, truckId } = req.body;
    
    const shipment = await Shipment.findByIdAndUpdate(
      req.params.id,
      {
        assignedDriver: driverId,
        assignedTruck: truckId,
        status: 'assigned'
      },
      { new: true }
    );

    if (!shipment) {
      return res.status(404).json({
        success: false,
        message: 'الشحنة غير موجودة'
      });
    }

    // Notify driver
    io.to(`user:${driverId}`).emit('new_assignment', {
      shipmentId: shipment._id,
      trackingNumber: shipment.trackingNumber
    });

    res.json({
      success: true,
      message: 'تم تعيين السائق بنجاح',
      data: shipment
    });
  } catch (error) {
    logger.error('Assign driver error:', error);
    res.status(500).json({
      success: false,
      message: 'حدث خطأ'
    });
  }
};

// @desc    Cancel shipment
// @route   PUT /api/v1/shipments/:id/cancel
// @access  Private (Client, Admin)
exports.cancelShipment = async (req, res) => {
  try {
    const shipment = await Shipment.findById(req.params.id);
    
    if (!shipment) {
      return res.status(404).json({
        success: false,
        message: 'الشحنة غير موجودة'
      });
    }

    // Check if can be cancelled
    if (['delivered', 'completed', 'cancelled'].includes(shipment.status)) {
      return res.status(400).json({
        success: false,
        message: 'لا يمكن إلغاء الشحنة في هذه الحالة'
      });
    }

    shipment.status = 'cancelled';
    shipment.statusHistory.push({
      status: 'cancelled',
      timestamp: new Date(),
      updatedBy: req.user.id,
      notes: 'تم الإلغاء من قبل المستخدم'
    });

    await shipment.save();

    res.json({
      success: true,
      message: 'تم إلغاء الشحنة بنجاح',
      data: shipment
    });
  } catch (error) {
    logger.error('Cancel shipment error:', error);
    res.status(500).json({
      success: false,
      message: 'حدث خطأ'
    });
  }
};
