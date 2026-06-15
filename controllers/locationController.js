/**
 * ============================================
 * 📍 Location Controller - نظام إدهام
 * Edham Logistics - Driver Location Tracking Controller
 * ============================================
 */

const Location = require('../models/Location');
const { MESSAGES, HTTP_STATUS } = require('../config/constants');
const logger = require('../utils/logger');

class LocationController {
  /**
   * Save driver location
   */
  static async save(req, res) {
    try {
      const { driverId, latitude, longitude, speed, heading, accuracy } = req.body;

      const location = await Location.create({
        driverId,
        latitude,
        longitude,
        speed,
        heading,
        accuracy,
        timestamp: new Date()
      });

      logger.success('Location saved', { driverId, latitude, longitude });

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: MESSAGES.CREATED,
        data: location
      });
    } catch (error) {
      logger.error('Save location error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get latest location for driver
   */
  static async getLatest(req, res) {
    try {
      const { driverId } = req.params;

      const location = await Location.findOne({ driverId })
        .sort({ createdAt: -1 });

      if (!location) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      res.json({
        success: true,
        data: location
      });
    } catch (error) {
      logger.error('Get latest location error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get location history for driver
   */
  static async getHistory(req, res) {
    try {
      const { driverId } = req.params;
      const { startDate, endDate, limit = 100 } = req.query;

      let query = { driverId };
      
      if (startDate && endDate) {
        query.createdAt = {
          $gte: new Date(startDate),
          $lte: new Date(endDate)
        };
      }

      const locations = await Location.find(query)
        .sort({ createdAt: -1 })
        .limit(parseInt(limit));

      res.json({
        success: true,
        data: locations,
        total: locations.length
      });
    } catch (error) {
      logger.error('Get location history error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get all active drivers locations
   */
  static async getAllActive(req, res) {
    try {
      // Get latest location for each driver
      const locations = await Location.aggregate([
        {
          $sort: { createdAt: -1 }
        },
        {
          $group: {
            _id: '$driverId',
            latestLocation: { $first: '$$ROOT' }
          }
        },
        {
          $replaceRoot: { newRoot: '$latestLocation' }
        }
      ]);

      res.json({
        success: true,
        data: locations,
        total: locations.length
      });
    } catch (error) {
      logger.error('Get all active locations error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }
}

module.exports = LocationController;
