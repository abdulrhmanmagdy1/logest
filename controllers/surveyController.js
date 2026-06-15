/**
 * ============================================
 * 📝 Survey Controller - نظام إدهام
 * Edham Logistics - Driver Survey Controller
 * ============================================
 */

const Survey = require('../models/Survey');
const { MESSAGES, HTTP_STATUS, ROLES } = require('../config/constants');
const logger = require('../utils/logger');

class SurveyController {
  /**
   * Get all surveys
   */
  static async getAll(req, res) {
    try {
      const { page = 1, limit = 20, driverId, shipmentId } = req.query;
      
      let query = { deletedAt: null };
      if (driverId) query.driver = driverId;
      if (shipmentId) query.shipment = shipmentId;

      const surveys = await Survey.find(query)
        .populate('driver', 'name email phone')
        .populate('shipment', 'shipmentNumber')
        .skip((page - 1) * limit)
        .limit(parseInt(limit))
        .sort({ submittedAt: -1 });

      const total = await Survey.countDocuments(query);

      res.json({
        success: true,
        data: surveys,
        pagination: { total, page: parseInt(page), limit: parseInt(limit) }
      });
    } catch (error) {
      logger.error('Get surveys error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get single survey
   */
  static async getById(req, res) {
    try {
      const survey = await Survey.findById(req.params.id)
        .populate('driver', 'name email phone')
        .populate('shipment', 'shipmentNumber');

      if (!survey || survey.deletedAt) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      res.json({
        success: true,
        data: survey
      });
    } catch (error) {
      logger.error('Get survey error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get surveys by driver
   */
  static async getByDriver(req, res) {
    try {
      const { driverId } = req.params;
      const { page = 1, limit = 20 } = req.query;

      const surveys = await Survey.find({ driver: driverId, deletedAt: null })
        .populate('shipment', 'shipmentNumber')
        .skip((page - 1) * limit)
        .limit(parseInt(limit))
        .sort({ submittedAt: -1 });

      const total = await Survey.countDocuments({ driver: driverId, deletedAt: null });

      res.json({
        success: true,
        data: surveys,
        pagination: { total, page: parseInt(page), limit: parseInt(limit) }
      });
    } catch (error) {
      logger.error('Get surveys by driver error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Create survey (Driver only)
   */
  static async create(req, res) {
    try {
      // Check if user is driver or admin
      if (req.user.role !== ROLES.DRIVER && req.user.role !== ROLES.ADMIN) {
        return res.status(HTTP_STATUS.FORBIDDEN).json({
          success: false,
          message: 'Only drivers can submit surveys'
        });
      }

      const { vehicleCondition, cargoCondition, routeCondition, comments, shipment } = req.body;

      const surveyNumber = `SRV-${Date.now()}`;
      
      const survey = new Survey({
        surveyNumber,
        driver: req.user._id,
        shipment,
        vehicleCondition,
        cargoCondition,
        routeCondition,
        comments,
        submittedAt: new Date()
      });

      await survey.save();
      await survey.populate(['driver', 'shipment']);

      logger.success('Survey submitted', { surveyNumber, driver: req.user._id });

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: 'Survey submitted successfully',
        data: survey
      });
    } catch (error) {
      logger.error('Create survey error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get survey statistics
   */
  static async getStatistics(req, res) {
    try {
      const [total, byVehicleCondition, byCargoCondition] = await Promise.all([
        Survey.countDocuments({ deletedAt: null }),
        Survey.aggregate([
          { $match: { deletedAt: null } },
          { $group: { _id: '$vehicleCondition', count: { $sum: 1 } } }
        ]),
        Survey.aggregate([
          { $match: { deletedAt: null } },
          { $group: { _id: '$cargoCondition', count: { $sum: 1 } } }
        ])
      ]);

      res.json({
        success: true,
        data: {
          total,
          byVehicleCondition,
          byCargoCondition
        }
      });
    } catch (error) {
      logger.error('Get survey statistics error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Delete survey (Soft Delete)
   */
  static async delete(req, res) {
    try {
      const survey = await Survey.findByIdAndUpdate(
        req.params.id,
        { deletedAt: new Date() },
        { new: true }
      );

      if (!survey) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      logger.success('Survey deleted', { surveyId: req.params.id });

      res.json({
        success: true,
        message: MESSAGES.DELETED
      });
    } catch (error) {
      logger.error('Delete survey error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }
}

module.exports = SurveyController;
