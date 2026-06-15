/**
 * ============================================
 * 📋 Surveys Routes - نظام إدهام
 * Driver feedback survey endpoints
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { body } = require('express-validator');
const { protect, authorize } = require('../middleware/auth');
const Survey = require('../models/Survey');
const Shipment = require('../models/Shipment');
const User = require('../models/User');
const logger = require('../utils/logger');

// Validation helper
const handleValidationErrors = (req, res, next) => {
  const errors = require('express-validator').validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ success: false, errors: errors.array() });
  }
  next();
};

// @route   GET /api/v1/surveys
// @desc    Get all surveys
// @access  Private (Admin, Supervisor)
router.get('/', protect, authorize('admin', 'supervisor'), async (req, res) => {
  try {
    const { status, driver, shipment, page = 1, limit = 20 } = req.query;
    
    const filter = {};
    if (status) filter.status = status;
    if (driver) filter.driver = driver;
    if (shipment) filter.shipment = shipment;

    const skip = (parseInt(page) - 1) * parseInt(limit);

    const surveys = await Survey.find(filter)
      .populate('shipment', 'trackingNumber status')
      .populate('driver', 'firstName lastName driverInfo.rating')
      .populate('client', 'firstName lastName companyName')
      .sort('-createdAt')
      .skip(skip)
      .limit(parseInt(limit));

    const total = await Survey.countDocuments(filter);

    // Calculate average ratings
    const stats = await Survey.aggregate([
      { $match: { status: 'completed' } },
      {
        $group: {
          _id: null,
          avgOverall: { $avg: '$ratings.overall' },
          avgDriverProfessionalism: { $avg: '$ratings.driver.professionalism' },
          avgDriverPunctuality: { $avg: '$ratings.driver.punctuality' },
          avgCargoHandling: { $avg: '$ratings.cargo.handling' },
          totalSurveys: { $sum: 1 }
        }
      }
    ]);

    res.json({
      success: true,
      count: surveys.length,
      total,
      page: parseInt(page),
      pages: Math.ceil(total / parseInt(limit)),
      stats: stats[0] || {},
      data: surveys
    });
  } catch (error) {
    logger.error('Get surveys error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/surveys/pending
// @desc    Get pending surveys for client
// @access  Private (Client)
router.get('/pending', protect, async (req, res) => {
  try {
    if (req.user.role !== 'client') {
      return res.status(403).json({ success: false, message: 'Only clients can access pending surveys' });
    }

    const surveys = await Survey.find({
      client: req.user.id,
      status: { $in: ['pending', 'sent'] }
    })
      .populate('shipment', 'trackingNumber pickup delivery cargo')
      .populate('driver', 'firstName lastName')
      .sort('createdAt');

    res.json({
      success: true,
      count: surveys.length,
      data: surveys
    });
  } catch (error) {
    logger.error('Get pending surveys error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/surveys/driver/:driverId
// @desc    Get driver survey stats
// @access  Private
router.get('/driver/:driverId', protect, async (req, res) => {
  try {
    const driverId = req.params.driverId;

    // Authorization check
    if (req.user.role === 'driver' && req.user.id !== driverId) {
      return res.status(403).json({ success: false, message: 'Not authorized' });
    }

    const stats = await Survey.aggregate([
      { $match: { driver: new require('mongoose').Types.ObjectId(driverId), status: 'completed' } },
      {
        $group: {
          _id: null,
          totalSurveys: { $sum: 1 },
          avgOverall: { $avg: '$ratings.overall' },
          avgProfessionalism: { $avg: '$ratings.driver.professionalism' },
          avgPunctuality: { $avg: '$ratings.driver.punctuality' },
          avgCommunication: { $avg: '$ratings.driver.communication' },
          avgDriving: { $avg: '$ratings.driver.driving' }
        }
      }
    ]);

    const recentSurveys = await Survey.find({
      driver: driverId,
      status: 'completed'
    })
      .populate('shipment', 'trackingNumber')
      .populate('client', 'firstName lastName companyName')
      .sort('-completedAt')
      .limit(5);

    res.json({
      success: true,
      stats: stats[0] || {
        totalSurveys: 0,
        avgOverall: 0,
        avgProfessionalism: 0,
        avgPunctuality: 0,
        avgCommunication: 0,
        avgDriving: 0
      },
      recentSurveys
    });
  } catch (error) {
    logger.error('Get driver survey stats error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/surveys/:id
// @desc    Get single survey
// @access  Private
router.get('/:id', protect, async (req, res) => {
  try {
    const survey = await Survey.findById(req.params.id)
      .populate('shipment')
      .populate('driver', 'firstName lastName phone driverInfo')
      .populate('client', 'firstName lastName companyName')
      .populate('followUp.completedBy', 'firstName lastName');

    if (!survey) {
      return res.status(404).json({ success: false, message: 'Survey not found' });
    }

    // Check authorization
    const isAuthorized = 
      ['admin', 'supervisor'].includes(req.user.role) ||
      survey.client._id.toString() === req.user.id ||
      survey.driver._id.toString() === req.user.id;

    if (!isAuthorized) {
      return res.status(403).json({ success: false, message: 'Not authorized' });
    }

    res.json({ success: true, data: survey });
  } catch (error) {
    logger.error('Get survey error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/surveys
// @desc    Create survey request
// @access  Private (System auto-creates on delivery)
router.post('/', protect, authorize('admin', 'supervisor', 'system'), [
  body('shipment').isMongoId(),
  body('driver').isMongoId(),
  body('client').isMongoId()
], handleValidationErrors, async (req, res) => {
  try {
    // Check if survey already exists
    const existing = await Survey.findOne({ shipment: req.body.shipment });
    if (existing) {
      return res.status(400).json({ success: false, message: 'Survey already exists for this shipment' });
    }

    const survey = await Survey.create({
      ...req.body,
      status: 'pending',
      expiresAt: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000) // 7 days
    });

    logger.info(`Survey created for shipment ${req.body.shipment}`);

    res.status(201).json({
      success: true,
      message: 'Survey created successfully',
      data: survey
    });
  } catch (error) {
    logger.error('Create survey error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/surveys/:id/submit
// @desc    Submit survey response
// @access  Private (Client)
router.put('/:id/submit', protect, [
  body('ratings').isObject(),
  body('responses').optional().isArray()
], handleValidationErrors, async (req, res) => {
  try {
    const survey = await Survey.findById(req.params.id);
    if (!survey) {
      return res.status(404).json({ success: false, message: 'Survey not found' });
    }

    // Only client can submit
    if (survey.client.toString() !== req.user.id) {
      return res.status(403).json({ success: false, message: 'Not authorized' });
    }

    if (survey.status === 'completed') {
      return res.status(400).json({ success: false, message: 'Survey already completed' });
    }

    // Update survey
    survey.status = 'completed';
    survey.completedAt = new Date();
    survey.ratings = req.body.ratings;
    survey.responses = req.body.responses || [];
    survey.positiveFeedback = req.body.positiveFeedback;
    survey.negativeFeedback = req.body.negativeFeedback;
    survey.suggestions = req.body.suggestions;
    survey.wouldRecommend = req.body.wouldRecommend;
    survey.npsScore = req.body.npsScore;
    survey.ipAddress = req.ip;
    survey.userAgent = req.headers['user-agent'];

    await survey.save();

    // Update driver rating
    const driverStats = await Survey.aggregate([
      { $match: { driver: survey.driver, status: 'completed' } },
      {
        $group: {
          _id: null,
          avgOverall: { $avg: '$ratings.overall' }
        }
      }
    ]);

    if (driverStats.length > 0) {
      await User.findByIdAndUpdate(survey.driver, {
        'driverInfo.rating': Math.round(driverStats[0].avgOverall * 10) / 10
      });
    }

    logger.info(`Survey submitted: ${survey._id} for driver ${survey.driver}`);

    res.json({
      success: true,
      message: 'Thank you for your feedback!',
      data: survey
    });
  } catch (error) {
    logger.error('Submit survey error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   PUT /api/v1/surveys/:id/follow-up
// @desc    Add follow-up action
// @access  Private (Admin, Supervisor)
router.put('/:id/follow-up', protect, authorize('admin', 'supervisor'), [
  body('actionTaken').trim().notEmpty(),
  body('completed').optional().isBoolean()
], handleValidationErrors, async (req, res) => {
  try {
    const survey = await Survey.findById(req.params.id);
    if (!survey) {
      return res.status(404).json({ success: false, message: 'Survey not found' });
    }

    survey.followUp.actionTaken = req.body.actionTaken;
    survey.followUp.completed = req.body.completed || true;
    survey.followUp.completedAt = new Date();
    survey.followUp.completedBy = req.user.id;

    await survey.save();

    res.json({
      success: true,
      message: 'Follow-up action recorded',
      data: survey
    });
  } catch (error) {
    logger.error('Follow-up error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
