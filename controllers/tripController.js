/**
 * ============================================
 * 🛣️ Trip Controller - نظام إدهام
 * Edham Logistics - Trip Management Controller
 * ============================================
 */

const Trip = require('../models/Trip');
const Helpers = require('../utils/helpers');
const { MESSAGES, HTTP_STATUS } = require('../config/constants');
const logger = require('../utils/logger');

class TripController {
  /**
   * Get all trips
   */
  static async getAll(req, res) {
    try {
      const { page = 1, limit = 20, status, employeeId } = req.query;
      
      let query = { deletedAt: null };
      if (status) query.status = status;
      if (employeeId) query.employee = employeeId;

      const trips = await Trip.find(query)
        .populate('employee', 'name email phone')
        .populate('vehicle', 'vehicleNumber plateNumber model')
        .skip((page - 1) * limit)
        .limit(parseInt(limit))
        .sort({ createdAt: -1 });

      const total = await Trip.countDocuments(query);

      res.json({
        success: true,
        data: trips,
        pagination: { total, page: parseInt(page), limit: parseInt(limit) }
      });
    } catch (error) {
      logger.error('Get trips error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get trips by employee
   */
  static async getByEmployee(req, res) {
    try {
      const { employeeId } = req.params;
      const { page = 1, limit = 20 } = req.query;

      const trips = await Trip.find({ employee: employeeId, deletedAt: null })
        .populate('vehicle', 'vehicleNumber plateNumber model')
        .skip((page - 1) * limit)
        .limit(parseInt(limit))
        .sort({ createdAt: -1 });

      const total = await Trip.countDocuments({ employee: employeeId, deletedAt: null });

      res.json({
        success: true,
        data: trips,
        pagination: { total, page: parseInt(page), limit: parseInt(limit) }
      });
    } catch (error) {
      logger.error('Get trips by employee error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get active trips
   */
  static async getActive(req, res) {
    try {
      const trips = await Trip.find({ 
        status: { $in: ['started', 'in_progress'] },
        deletedAt: null 
      })
        .populate('employee', 'name phone')
        .populate('vehicle', 'vehicleNumber plateNumber')
        .sort({ createdAt: -1 });

      res.json({
        success: true,
        data: trips
      });
    } catch (error) {
      logger.error('Get active trips error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get single trip
   */
  static async getById(req, res) {
    try {
      const trip = await Trip.findById(req.params.id)
        .populate('employee', 'name email phone')
        .populate('vehicle', 'vehicleNumber plateNumber model');

      if (!trip || trip.deletedAt) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      res.json({
        success: true,
        data: trip
      });
    } catch (error) {
      logger.error('Get trip error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Create new trip
   */
  static async create(req, res) {
    try {
      const tripNumber = Helpers.generateTripNumber();
      
      const trip = new Trip({
        ...req.body,
        tripNumber,
        employee: req.user._id,
        startPoint: {
          ...req.body.startPoint,
          timestamp: new Date()
        },
        status: 'pending'
      });

      await trip.save();
      await trip.populate(['employee', 'vehicle']);

      logger.success('Trip created', { tripNumber, employee: req.user._id });

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: MESSAGES.CREATED,
        data: trip
      });
    } catch (error) {
      logger.error('Create trip error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Start trip
   */
  static async start(req, res) {
    try {
      const trip = await Trip.findByIdAndUpdate(
        req.params.id,
        {
          status: 'started',
          startTime: new Date(),
          startPoint: {
            ...req.body,
            timestamp: new Date()
          },
          odometerStart: req.body.odometerStart
        },
        { new: true }
      ).populate(['employee', 'vehicle']);

      if (!trip) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      logger.success('Trip started', { tripId: req.params.id });

      res.json({
        success: true,
        message: 'Trip started',
        data: trip
      });
    } catch (error) {
      logger.error('Start trip error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Update trip location (real-time tracking)
   */
  static async updateLocation(req, res) {
    try {
      const { latitude, longitude, speed } = req.body;
      
      const trip = await Trip.findById(req.params.id);
      
      if (!trip || trip.deletedAt) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      trip.route.push({
        latitude,
        longitude,
        timestamp: new Date(),
        speed
      });

      trip.currentLocation = {
        latitude,
        longitude,
        updatedAt: new Date()
      };

      await trip.save();

      res.json({
        success: true,
        message: 'Location updated',
        data: trip
      });
    } catch (error) {
      logger.error('Update location error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Complete trip
   */
  static async complete(req, res) {
    try {
      const { latitude, longitude, address, odometerEnd, fuelConsumption } = req.body;
      
      const trip = await Trip.findById(req.params.id);
      
      if (!trip || trip.deletedAt) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      const endTime = new Date();
      const duration = endTime - trip.startTime;

      trip.status = 'completed';
      trip.endTime = endTime;
      trip.endPoint = {
        latitude,
        longitude,
        address,
        timestamp: endTime
      };
      trip.odometerEnd = odometerEnd;
      trip.fuelConsumption = fuelConsumption;
      trip.duration = duration;
      trip.distance = odometerEnd - trip.odometerStart;

      await trip.save();
      await trip.populate(['employee', 'vehicle']);

      logger.success('Trip completed', { tripId: req.params.id });

      res.json({
        success: true,
        message: 'Trip completed',
        data: trip
      });
    } catch (error) {
      logger.error('Complete trip error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Pause trip
   */
  static async pause(req, res) {
    try {
      const trip = await Trip.findByIdAndUpdate(
        req.params.id,
        { status: 'paused' },
        { new: true }
      );

      if (!trip) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      res.json({
        success: true,
        message: 'Trip paused',
        data: trip
      });
    } catch (error) {
      logger.error('Pause trip error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Resume trip
   */
  static async resume(req, res) {
    try {
      const trip = await Trip.findByIdAndUpdate(
        req.params.id,
        { status: 'in_progress' },
        { new: true }
      );

      if (!trip) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      res.json({
        success: true,
        message: 'Trip resumed',
        data: trip
      });
    } catch (error) {
      logger.error('Resume trip error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Cancel trip
   */
  static async cancel(req, res) {
    try {
      const trip = await Trip.findByIdAndUpdate(
        req.params.id,
        { status: 'cancelled' },
        { new: true }
      );

      if (!trip) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      logger.success('Trip cancelled', { tripId: req.params.id });

      res.json({
        success: true,
        message: 'Trip cancelled',
        data: trip
      });
    } catch (error) {
      logger.error('Cancel trip error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Delete trip (Soft Delete)
   */
  static async delete(req, res) {
    try {
      const trip = await Trip.findByIdAndUpdate(
        req.params.id,
        { deletedAt: new Date() },
        { new: true }
      );

      if (!trip) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      logger.success('Trip deleted', { tripId: req.params.id });

      res.json({
        success: true,
        message: MESSAGES.DELETED
      });
    } catch (error) {
      logger.error('Delete trip error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }
}

module.exports = TripController;
