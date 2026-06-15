/**
 * ============================================
 * 🔧 Maintenance Controller - نظام إدهام
 * Edham Logistics - Maintenance Management Controller
 * ============================================
 */

const Maintenance = require('../models/Maintenance');
const OilSchedule = require('../models/OilSchedule');
const Truck = require('../models/Truck');
const SparePart = require('../models/SparePart');
const Helpers = require('../utils/helpers');
const { MESSAGES, HTTP_STATUS, MAINTENANCE_TYPE, TRUCK_STATUS } = require('../config/constants');
const logger = require('../utils/logger');

class MaintenanceController {
  /**
   * Get all maintenance records
   */
  static async getAll(req, res) {
    try {
      const { page = 1, limit = 20, truckId, type, status } = req.query;
      
      let query = { deletedAt: null };
      if (truckId) query.truck = truckId;
      if (type) query.type = type;
      if (status) query.status = status;

      const maintenance = await Maintenance.find(query)
        .populate('truck', 'truckNumber plateNumber model')
        .skip((page - 1) * limit)
        .limit(parseInt(limit))
        .sort({ createdAt: -1 });

      const total = await Maintenance.countDocuments(query);

      res.json({
        success: true,
        data: maintenance,
        pagination: { total, page: parseInt(page), limit: parseInt(limit) }
      });
    } catch (error) {
      logger.error('Get maintenance error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get single maintenance record
   */
  static async getById(req, res) {
    try {
      const maintenance = await Maintenance.findById(req.params.id)
        .populate('truck', 'truckNumber plateNumber model');

      if (!maintenance || maintenance.deletedAt) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      res.json({
        success: true,
        data: maintenance
      });
    } catch (error) {
      logger.error('Get maintenance error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get maintenance by truck
   */
  static async getByTruck(req, res) {
    try {
      const { truckId } = req.params;
      const { page = 1, limit = 20 } = req.query;

      const maintenance = await Maintenance.find({ truck: truckId, deletedAt: null })
        .populate('truck', 'truckNumber plateNumber')
        .skip((page - 1) * limit)
        .limit(parseInt(limit))
        .sort({ createdAt: -1 });

      const total = await Maintenance.countDocuments({ truck: truckId, deletedAt: null });

      res.json({
        success: true,
        data: maintenance,
        pagination: { total, page: parseInt(page), limit: parseInt(limit) }
      });
    } catch (error) {
      logger.error('Get maintenance by truck error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Create maintenance record
   */
  static async create(req, res) {
    try {
      const { truck, type, description, cost, scheduledDate, parts } = req.body;

      const maintenanceNumber = Helpers.generateMaintenanceNumber();

      const maintenance = new Maintenance({
        maintenanceNumber,
        truck,
        type,
        description,
        cost,
        scheduledDate,
        parts,
        status: 'scheduled'
      });

      await maintenance.save();
      await maintenance.populate('truck');

      // Update truck status
      await Truck.findByIdAndUpdate(truck, {
        status: TRUCK_STATUS.MAINTENANCE,
        maintenanceStatus: 'scheduled'
      });

      logger.success('Maintenance created', { maintenanceNumber });

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: MESSAGES.CREATED,
        data: maintenance
      });
    } catch (error) {
      logger.error('Create maintenance error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Update maintenance record
   */
  static async update(req, res) {
    try {
      const maintenance = await Maintenance.findByIdAndUpdate(
        req.params.id,
        { ...req.body, updatedAt: new Date() },
        { new: true, runValidators: true }
      ).populate('truck');

      if (!maintenance) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      logger.success('Maintenance updated', { maintenanceId: req.params.id });

      res.json({
        success: true,
        message: MESSAGES.UPDATED,
        data: maintenance
      });
    } catch (error) {
      logger.error('Update maintenance error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Complete maintenance
   */
  static async complete(req, res) {
    try {
      const { actualCost, technician, notes, parts } = req.body;

      const maintenance = await Maintenance.findByIdAndUpdate(
        req.params.id,
        {
          status: 'completed',
          completedDate: new Date(),
          cost: actualCost || 0,
          technician,
          notes
        },
        { new: true }
      ).populate('truck');

      if (!maintenance) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      // Update truck
      const truck = await Truck.findById(maintenance.truck._id);
      truck.maintenanceStatus = 'good';
      truck.lastMaintenanceDate = new Date();

      // Update oil schedule if it's oil change
      if (maintenance.type === MAINTENANCE_TYPE.OIL_CHANGE) {
        const oilSchedule = await OilSchedule.findOne({ truck: truck._id });
        if (oilSchedule) {
          oilSchedule.lastChangeDate = new Date();
          oilSchedule.lastChangeKilometers = truck.currentKilometers || 0;
          oilSchedule.nextChangeDate = new Date(Date.now() + 90 * 24 * 60 * 60 * 1000);
          oilSchedule.nextChangeKilometers = (truck.currentKilometers || 0) + 5000;
          await oilSchedule.save();
        }
      }

      // Update spare parts inventory
      if (parts && parts.length > 0) {
        for (const part of parts) {
          await SparePart.findByIdAndUpdate(part.id, {
            $inc: { quantity: -part.quantity }
          });
        }
      }

      await truck.save();

      logger.success('Maintenance completed', { maintenanceNumber: maintenance.maintenanceNumber });

      res.json({
        success: true,
        message: 'تم إكمال الصيانة بنجاح',
        data: maintenance
      });
    } catch (error) {
      logger.error('Complete maintenance error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get maintenance alerts
   */
  static async getAlerts(req, res) {
    try {
      const trucks = await Truck.find({
        $or: [
          { maintenanceStatus: { $in: ['warning', 'due', 'urgent'] } },
          { nextMaintenanceDate: { $lte: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000) } }
        ],
        deletedAt: null
      }).populate('driver', 'name phone');

      const alerts = await Promise.all(trucks.map(async (truck) => {
        const pendingMaintenance = await Maintenance.countDocuments({
          truck: truck._id,
          status: 'pending',
          deletedAt: null
        });

        const oilSchedule = await OilSchedule.findOne({ truck: truck._id });

        return {
          truck: truck.toObject(),
          pendingMaintenance,
          oilScheduleInfo: oilSchedule,
          daysUntilMaintenance: truck.nextMaintenanceDate
            ? Math.ceil((truck.nextMaintenanceDate - new Date()) / (1000 * 60 * 60 * 24))
            : null
        };
      }));

      res.json({
        success: true,
        data: alerts
      });
    } catch (error) {
      logger.error('Get maintenance alerts error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get maintenance statistics
   */
  static async getStatistics(req, res) {
    try {
      const [total, scheduled, inProgress, completed, totalCost] = await Promise.all([
        Maintenance.countDocuments({ deletedAt: null }),
        Maintenance.countDocuments({ status: 'scheduled', deletedAt: null }),
        Maintenance.countDocuments({ status: 'in_progress', deletedAt: null }),
        Maintenance.countDocuments({ status: 'completed', deletedAt: null }),
        Maintenance.aggregate([
          { $match: { status: 'completed', deletedAt: null } },
          { $group: { _id: null, total: { $sum: '$actualCost' } } }
        ])
      ]);

      res.json({
        success: true,
        data: {
          total,
          scheduled,
          inProgress,
          completed,
          totalCost: totalCost[0]?.total || 0
        }
      });
    } catch (error) {
      logger.error('Get maintenance statistics error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }
}

module.exports = MaintenanceController;
