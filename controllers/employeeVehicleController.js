/**
 * ============================================
 * 🚗 Employee Vehicle Controller - نظام إدهام
 * Edham Logistics - Employee Vehicle Management Controller
 * ============================================
 */

const EmployeeVehicle = require('../models/EmployeeVehicle');
const { MESSAGES, HTTP_STATUS } = require('../config/constants');
const logger = require('../utils/logger');

class EmployeeVehicleController {
  /**
   * Get all vehicles
   */
  static async getAll(req, res) {
    try {
      const { page = 1, limit = 20, employeeId, status } = req.query;
      
      let query = { deletedAt: null };
      if (employeeId) query.employee = employeeId;
      if (status) query.status = status;

      const vehicles = await EmployeeVehicle.find(query)
        .populate('employee', 'name email phone')
        .skip((page - 1) * limit)
        .limit(parseInt(limit))
        .sort({ createdAt: -1 });

      const total = await EmployeeVehicle.countDocuments(query);

      res.json({
        success: true,
        data: vehicles,
        pagination: { total, page: parseInt(page), limit: parseInt(limit) }
      });
    } catch (error) {
      logger.error('Get vehicles error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get vehicles by employee
   */
  static async getByEmployee(req, res) {
    try {
      const { employeeId } = req.params;
      const { page = 1, limit = 20 } = req.query;

      const vehicles = await EmployeeVehicle.find({ employee: employeeId, deletedAt: null })
        .populate('employee', 'name email phone')
        .skip((page - 1) * limit)
        .limit(parseInt(limit))
        .sort({ createdAt: -1 });

      const total = await EmployeeVehicle.countDocuments({ employee: employeeId, deletedAt: null });

      res.json({
        success: true,
        data: vehicles,
        pagination: { total, page: parseInt(page), limit: parseInt(limit) }
      });
    } catch (error) {
      logger.error('Get vehicles by employee error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get single vehicle
   */
  static async getById(req, res) {
    try {
      const vehicle = await EmployeeVehicle.findById(req.params.id)
        .populate('employee', 'name email phone');

      if (!vehicle || vehicle.deletedAt) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      res.json({
        success: true,
        data: vehicle
      });
    } catch (error) {
      logger.error('Get vehicle error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Create vehicle (Admin/Supervisor)
   */
  static async create(req, res) {
    try {
      const vehicle = new EmployeeVehicle({
        ...req.body,
        status: 'active'
      });

      await vehicle.save();
      await vehicle.populate('employee');

      logger.success('Vehicle created', { vehicleId: vehicle._id });

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: MESSAGES.CREATED,
        data: vehicle
      });
    } catch (error) {
      logger.error('Create vehicle error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Register own vehicle (Employee)
   */
  static async register(req, res) {
    try {
      const vehicle = new EmployeeVehicle({
        ...req.body,
        employee: req.user._id,
        status: 'pending'
      });

      await vehicle.save();

      logger.success('Vehicle registered', { vehicleId: vehicle._id, employee: req.user._id });

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: 'Vehicle registered successfully. Awaiting approval.',
        data: vehicle
      });
    } catch (error) {
      logger.error('Register vehicle error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Update vehicle
   */
  static async update(req, res) {
    try {
      const vehicle = await EmployeeVehicle.findByIdAndUpdate(
        req.params.id,
        { ...req.body, updatedAt: new Date() },
        { new: true, runValidators: true }
      ).populate('employee');

      if (!vehicle) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      logger.success('Vehicle updated', { vehicleId: req.params.id });

      res.json({
        success: true,
        message: MESSAGES.UPDATED,
        data: vehicle
      });
    } catch (error) {
      logger.error('Update vehicle error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Update vehicle location
   */
  static async updateLocation(req, res) {
    try {
      const { latitude, longitude, address } = req.body;

      const vehicle = await EmployeeVehicle.findByIdAndUpdate(
        req.params.id,
        {
          currentLocation: {
            latitude,
            longitude,
            address,
            updatedAt: new Date()
          }
        },
        { new: true }
      );

      if (!vehicle) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      res.json({
        success: true,
        message: 'Location updated',
        data: vehicle
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
   * Update vehicle status
   */
  static async updateStatus(req, res) {
    try {
      const { status } = req.body;

      const vehicle = await EmployeeVehicle.findByIdAndUpdate(
        req.params.id,
        { status, updatedAt: new Date() },
        { new: true }
      );

      if (!vehicle) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      logger.success('Vehicle status updated', { vehicleId: req.params.id, status });

      res.json({
        success: true,
        message: 'Status updated',
        data: vehicle
      });
    } catch (error) {
      logger.error('Update status error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Delete vehicle (Soft Delete)
   */
  static async delete(req, res) {
    try {
      const vehicle = await EmployeeVehicle.findByIdAndUpdate(
        req.params.id,
        { deletedAt: new Date(), status: 'deleted' },
        { new: true }
      );

      if (!vehicle) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      logger.success('Vehicle deleted', { vehicleId: req.params.id });

      res.json({
        success: true,
        message: MESSAGES.DELETED
      });
    } catch (error) {
      logger.error('Delete vehicle error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }
}

module.exports = EmployeeVehicleController;
