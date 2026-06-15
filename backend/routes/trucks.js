/**
 * ============================================
 * 🚛 Trucks Routes - نظام إدهام
 * Fleet management endpoints
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { body } = require('express-validator');
const { protect, authorize } = require('../middleware/auth');
const Truck = require('../models/Truck');
const logger = require('../utils/logger');

// Validation helper
const handleValidationErrors = (req, res, next) => {
  const errors = require('express-validator').validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ success: false, errors: errors.array() });
  }
  next();
};

// @route   GET /api/v1/trucks
// @desc    Get all trucks
// @access  Private (Admin, Supervisor, Employee)
router.get('/', protect, authorize('admin', 'supervisor', 'employee'), async (req, res) => {
  try {
    const { status, type, assigned, search, page = 1, limit = 20 } = req.query;
    
    const filter = {};
    if (status) filter.status = status;
    if (type) filter.type = type;
    if (search) {
      filter.$or = [
        { plateNumber: { $regex: search, $options: 'i' } },
        { make: { $regex: search, $options: 'i' } },
        { model: { $regex: search, $options: 'i' } }
      ];
    }

    const skip = (parseInt(page) - 1) * parseInt(limit);
    
    let query = Truck.find(filter).sort('-createdAt').skip(skip).limit(parseInt(limit));
    
    if (assigned === 'true') {
      query = query.populate('assignedDriver', 'firstName lastName phone');
    }
    
    const trucks = await query;
    const total = await Truck.countDocuments(filter);

    res.json({
      success: true,
      count: trucks.length,
      total,
      page: parseInt(page),
      pages: Math.ceil(total / parseInt(limit)),
      data: trucks
    });
  } catch (error) {
    logger.error('Get trucks error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/trucks/available
// @desc    Get available trucks
// @access  Private
router.get('/available', protect, async (req, res) => {
  try {
    const trucks = await Truck.find({
      status: 'active',
      assignedDriver: null
    }).select('plateNumber type capacity refrigeration');

    res.json({ success: true, count: trucks.length, data: trucks });
  } catch (error) {
    logger.error('Get available trucks error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/trucks/:id
// @desc    Get single truck
// @access  Private
router.get('/:id', protect, async (req, res) => {
  try {
    const truck = await Truck.findById(req.params.id)
      .populate('assignedDriver', 'firstName lastName phone driverInfo');
    
    if (!truck) {
      return res.status(404).json({ success: false, message: 'Truck not found' });
    }

    res.json({ success: true, data: truck });
  } catch (error) {
    logger.error('Get truck error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/trucks
// @desc    Create new truck
// @access  Private (Admin, Supervisor)
router.post('/', protect, authorize('admin', 'supervisor'), [
  body('plateNumber').trim().notEmpty(),
  body('make').trim().notEmpty(),
  body('model').trim().notEmpty(),
  body('year').isInt({ min: 1990, max: new Date().getFullYear() + 1 }),
  body('type').isIn(['small', 'medium', 'large', 'xl', 'trailer']),
  body('capacity.weight.value').isNumeric(),
  body('refrigeration.minTemperature').isNumeric(),
  body('refrigeration.maxTemperature').isNumeric()
], handleValidationErrors, async (req, res) => {
  try {
    // Check if plate number exists
    const exists = await Truck.findOne({ plateNumber: req.body.plateNumber });
    if (exists) {
      return res.status(400).json({ success: false, message: 'Truck with this plate number already exists' });
    }

    const truck = await Truck.create(req.body);
    
    logger.info(`New truck created: ${truck.plateNumber} by ${req.user.id}`);
    
    res.status(201).json({
      success: true,
      message: 'Truck created successfully',
      data: truck
    });
  } catch (error) {
    logger.error('Create truck error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/trucks/:id
// @desc    Update truck
// @access  Private (Admin, Supervisor)
router.put('/:id', protect, authorize('admin', 'supervisor'), async (req, res) => {
  try {
    let truck = await Truck.findById(req.params.id);
    if (!truck) {
      return res.status(404).json({ success: false, message: 'Truck not found' });
    }

    // Update fields
    const allowedUpdates = ['make', 'model', 'year', 'capacity', 'refrigeration', 
                           'status', 'fuel', 'equipment', 'documents', 'notes', 'maintenance'];
    
    // Check plate number uniqueness if changing
    if (req.body.plateNumber && req.body.plateNumber !== truck.plateNumber) {
      const exists = await Truck.findOne({ plateNumber: req.body.plateNumber });
      if (exists) {
        return res.status(400).json({ success: false, message: 'Plate number already in use' });
      }
      allowedUpdates.push('plateNumber');
    }

    allowedUpdates.forEach(field => {
      if (req.body[field] !== undefined) truck[field] = req.body[field];
    });

    await truck.save();
    
    logger.info(`Truck updated: ${truck.plateNumber} by ${req.user.id}`);
    
    res.json({ success: true, message: 'Truck updated successfully', data: truck });
  } catch (error) {
    logger.error('Update truck error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/trucks/:id/assign
// @desc    Assign driver to truck
// @access  Private (Admin, Supervisor)
router.put('/:id/assign', protect, authorize('admin', 'supervisor'), [
  body('driverId').optional().isMongoId()
], handleValidationErrors, async (req, res) => {
  try {
    const truck = await Truck.findById(req.params.id);
    if (!truck) {
      return res.status(404).json({ success: false, message: 'Truck not found' });
    }

    truck.assignedDriver = req.body.driverId || null;
    await truck.save();

    logger.info(`Driver ${req.body.driverId || 'unassigned'} assigned to truck ${truck.plateNumber}`);

    res.json({ success: true, message: 'Driver assignment updated', data: truck });
  } catch (error) {
    logger.error('Assign driver error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   DELETE /api/v1/trucks/:id
// @desc    Delete truck
// @access  Private (Admin only)
router.delete('/:id', protect, authorize('admin'), async (req, res) => {
  try {
    const truck = await Truck.findById(req.params.id);
    if (!truck) {
      return res.status(404).json({ success: false, message: 'Truck not found' });
    }

    await truck.deleteOne();
    
    logger.info(`Truck deleted: ${truck.plateNumber} by ${req.user.id}`);
    
    res.json({ success: true, message: 'Truck deleted successfully' });
  } catch (error) {
    logger.error('Delete truck error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/trucks/:id/maintenance
// @desc    Get maintenance history
// @access  Private
router.get('/:id/maintenance', protect, async (req, res) => {
  try {
    const truck = await Truck.findById(req.params.id).select('maintenance plateNumber');
    if (!truck) {
      return res.status(404).json({ success: false, message: 'Truck not found' });
    }

    res.json({
      success: true,
      data: {
        plateNumber: truck.plateNumber,
        currentMileage: truck.maintenance.currentMileage,
        lastMaintenance: truck.maintenance.lastMaintenance,
        nextMaintenance: truck.maintenance.nextMaintenance,
        history: truck.maintenance.maintenanceHistory
      }
    });
  } catch (error) {
    logger.error('Get maintenance error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
