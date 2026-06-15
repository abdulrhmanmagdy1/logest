/**
 * ============================================
 * ⛽ Oil Schedule Controller - نظام إدهام
 * Edham Logistics - Oil Change Schedule Controller
 * ============================================
 */

const OilSchedule = require('../models/OilSchedule');
const Truck = require('../models/Truck');
const { MESSAGES, HTTP_STATUS } = require('../config/constants');
const logger = require('../utils/logger');

class OilScheduleController {
  /**
   * Get all oil schedules
   */
  static async getAll(req, res) {
    try {
      const { page = 1, limit = 20, truckId, status } = req.query;
      
      let query = { deletedAt: null };
      if (truckId) query.truck = truckId;
      if (status) query.status = status;

      const schedules = await OilSchedule.find(query)
        .populate('truck', 'truckNumber plateNumber model')
        .skip((page - 1) * limit)
        .limit(parseInt(limit))
        .sort({ nextChangeKilometers: 1 });

      const total = await OilSchedule.countDocuments(query);

      res.json({
        success: true,
        data: schedules,
        pagination: { total, page: parseInt(page), limit: parseInt(limit) }
      });
    } catch (error) {
      logger.error('Get oil schedules error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get single oil schedule
   */
  static async getById(req, res) {
    try {
      const schedule = await OilSchedule.findById(req.params.id)
        .populate('truck', 'truckNumber plateNumber model');

      if (!schedule || schedule.deletedAt) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      res.json({
        success: true,
        data: schedule
      });
    } catch (error) {
      logger.error('Get oil schedule error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get oil schedule by truck
   */
  static async getByTruck(req, res) {
    try {
      const { truckId } = req.params;
      
      const schedule = await OilSchedule.findOne({ truck: truckId, deletedAt: null })
        .populate('truck', 'truckNumber plateNumber model');

      if (!schedule) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: 'No oil schedule found for this truck'
        });
      }

      res.json({
        success: true,
        data: schedule
      });
    } catch (error) {
      logger.error('Get oil schedule by truck error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Create oil schedule
   */
  static async create(req, res) {
    try {
      const { truck, oilType, lastChangeKilometers, nextChangeKilometers, intervalKilometers, lastChangeDate, notes } = req.body;

      // Check if schedule already exists for truck
      const existing = await OilSchedule.findOne({ truck, deletedAt: null });
      if (existing) {
        return res.status(HTTP_STATUS.CONFLICT).json({
          success: false,
          message: 'Oil schedule already exists for this truck'
        });
      }

      const schedule = new OilSchedule({
        truck,
        oilType,
        lastChangeKilometers,
        nextChangeKilometers,
        intervalKilometers,
        lastChangeDate: lastChangeDate || new Date(),
        notes,
        status: 'active'
      });

      await schedule.save();
      await schedule.populate('truck');

      logger.success('Oil schedule created', { truck });

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: MESSAGES.CREATED,
        data: schedule
      });
    } catch (error) {
      logger.error('Create oil schedule error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Update oil schedule
   */
  static async update(req, res) {
    try {
      const schedule = await OilSchedule.findByIdAndUpdate(
        req.params.id,
        { ...req.body, updatedAt: new Date() },
        { new: true, runValidators: true }
      ).populate('truck');

      if (!schedule) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      logger.success('Oil schedule updated', { scheduleId: req.params.id });

      res.json({
        success: true,
        message: MESSAGES.UPDATED,
        data: schedule
      });
    } catch (error) {
      logger.error('Update oil schedule error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Record oil change
   */
  static async recordChange(req, res) {
    try {
      const { currentKilometers, oilType, cost, notes } = req.body;

      const schedule = await OilSchedule.findById(req.params.id);
      
      if (!schedule || schedule.deletedAt) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      // Update schedule with new change
      schedule.lastChangeKilometers = currentKilometers;
      schedule.nextChangeKilometers = currentKilometers + schedule.intervalKilometers;
      schedule.lastChangeDate = new Date();
      if (oilType) schedule.oilType = oilType;
      
      await schedule.save();

      // Update truck maintenance status
      await Truck.findByIdAndUpdate(schedule.truck, {
        maintenanceStatus: 'completed',
        lastMaintenanceDate: new Date()
      });

      logger.success('Oil change recorded', { scheduleId: req.params.id });

      res.json({
        success: true,
        message: 'Oil change recorded successfully',
        data: schedule
      });
    } catch (error) {
      logger.error('Record oil change error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Delete oil schedule (Soft Delete)
   */
  static async delete(req, res) {
    try {
      const schedule = await OilSchedule.findByIdAndUpdate(
        req.params.id,
        { deletedAt: new Date() },
        { new: true }
      );

      if (!schedule) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      logger.success('Oil schedule deleted', { scheduleId: req.params.id });

      res.json({
        success: true,
        message: MESSAGES.DELETED
      });
    } catch (error) {
      logger.error('Delete oil schedule error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get upcoming oil changes
   */
  static async getUpcoming(req, res) {
    try {
      const { threshold = 500 } = req.query; // Alert threshold in km

      const trucks = await Truck.find({ deletedAt: null });
      const alerts = [];

      for (const truck of trucks) {
        const schedule = await OilSchedule.findOne({ truck: truck._id, deletedAt: null });
        if (schedule) {
          const remainingKm = schedule.nextChangeKilometers - truck.totalKilometers;
          if (remainingKm <= threshold) {
            alerts.push({
              truck: truck.toObject(),
              schedule,
              remainingKm,
              isOverdue: remainingKm <= 0
            });
          }
        }
      }

      // Sort by urgency (overdue first, then by remaining km)
      alerts.sort((a, b) => a.remainingKm - b.remainingKm);

      res.json({
        success: true,
        data: alerts,
        total: alerts.length
      });
    } catch (error) {
      logger.error('Get upcoming oil changes error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }
}

module.exports = OilScheduleController;
