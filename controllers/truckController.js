/**
 * ============================================
 * 🚛 Truck Controller - نظام إدهام
 * Edham Logistics - Fleet Management Controller
 * ============================================
 */

const Truck = require('../models/Truck');
const OilSchedule = require('../models/OilSchedule');
const { MESSAGES, HTTP_STATUS, TRUCK_STATUS } = require('../config/constants');
const logger = require('../utils/logger');

class TruckController {
  /**
   * Get all trucks
   */
  static async getAll(req, res) {
    try {
      const { status, page = 1, limit = 20 } = req.query;
      let query = { deletedAt: null };
      
      if (status) query.status = status;

      const trucks = await Truck.find(query)
        .populate('driver', 'name phone')
        .skip((page - 1) * limit)
        .limit(parseInt(limit))
        .sort({ createdAt: -1 });

      const total = await Truck.countDocuments(query);

      res.json({
        success: true,
        data: trucks,
        pagination: { total, page: parseInt(page), limit: parseInt(limit) }
      });
    } catch (error) {
      logger.error('Get trucks error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get single truck
   */
  static async getById(req, res) {
    try {
      const truck = await Truck.findById(req.params.id)
        .populate('driver', 'name phone');

      if (!truck || truck.deletedAt) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      res.json({
        success: true,
        data: truck
      });
    } catch (error) {
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Create new truck
   */
  static async create(req, res) {
    try {
      const truck = new Truck(req.body);
      await truck.save();

      logger.success('Truck created', { truckId: truck._id });

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: MESSAGES.CREATED,
        data: truck
      });
    } catch (error) {
      logger.error('Create truck error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Update truck
   */
  static async update(req, res) {
    try {
      const truck = await Truck.findByIdAndUpdate(
        req.params.id,
        { ...req.body, updatedAt: new Date() },
        { new: true, runValidators: true }
      );

      if (!truck) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      logger.success('Truck updated', { truckId: req.params.id });

      res.json({
        success: true,
        message: MESSAGES.UPDATED,
        data: truck
      });
    } catch (error) {
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Update truck location
   */
  static async updateLocation(req, res) {
    try {
      const { latitude, longitude } = req.body;
      
      const truck = await Truck.findByIdAndUpdate(
        req.params.id,
        {
          currentLocation: { latitude, longitude, updatedAt: new Date() }
        },
        { new: true }
      );

      if (!truck) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      res.json({
        success: true,
        message: 'Location updated',
        data: truck
      });
    } catch (error) {
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Update kilometers
   */
  static async updateKilometers(req, res) {
    try {
      const { distance } = req.body;
      
      const truck = await Truck.findById(req.params.id);
      if (!truck) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      truck.totalKilometers += distance;
      const currentKilometers = truck.totalKilometers;

      // Check oil schedule
      const oilSchedule = await OilSchedule.findOne({ truck: truck._id });
      if (oilSchedule && currentKilometers >= oilSchedule.nextChangeKilometers) {
        truck.maintenanceStatus = 'due';
      }

      await truck.save();

      logger.info('Kilometers updated', { truckId: req.params.id, distance });

      res.json({
        success: true,
        message: 'Kilometers updated',
        data: truck
      });
    } catch (error) {
      logger.error('Update kilometers error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Delete truck (Soft Delete)
   */
  static async delete(req, res) {
    try {
      const truck = await Truck.findByIdAndUpdate(
        req.params.id,
        { deletedAt: new Date() },
        { new: true }
      );

      if (!truck) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      logger.success('Truck deleted', { truckId: req.params.id });

      res.json({
        success: true,
        message: MESSAGES.DELETED
      });
    } catch (error) {
      logger.error('Delete truck error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get fleet statistics
   */
  static async getFleetStatistics(req, res) {
    try {
      const [total, active, maintenance, outOfService] = await Promise.all([
        Truck.countDocuments({ deletedAt: null }),
        Truck.countDocuments({ status: TRUCK_STATUS.ACTIVE, deletedAt: null }),
        Truck.countDocuments({ status: TRUCK_STATUS.MAINTENANCE, deletedAt: null }),
        Truck.countDocuments({ status: TRUCK_STATUS.OUT_OF_SERVICE, deletedAt: null })
      ]);

      const totalCapacity = await Truck.aggregate([
        { $match: { deletedAt: null } },
        { $group: { _id: null, total: { $sum: '$capacity' } } }
      ]);

      const totalKilometers = await Truck.aggregate([
        { $match: { deletedAt: null } },
        { $group: { _id: null, total: { $sum: '$totalKilometers' } } }
      ]);

      res.json({
        success: true,
        data: {
          total,
          active,
          maintenance,
          outOfService,
          utilization: Math.round((active / total) * 100) || 0,
          totalCapacity: totalCapacity[0]?.total || 0,
          totalKilometers: totalKilometers[0]?.total || 0
        }
      });
    } catch (error) {
      logger.error('Get fleet statistics error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }
}

module.exports = TruckController;
