/**
 * ============================================
 * 📦 Shipment Controller - نظام إدهام
 * Edham Logistics - Shipment Management Controller
 * ============================================
 */

const Shipment = require('../models/Shipment');
const Helpers = require('../utils/helpers');
const { MESSAGES, HTTP_STATUS, SHIPMENT_STATUS, PAGINATION } = require('../config/constants');
const logger = require('../utils/logger');

class ShipmentController {
  /**
   * Get all shipments with pagination
   */
  static async getAll(req, res) {
    try {
      const { page = PAGINATION.DEFAULT_PAGE, limit = PAGINATION.DEFAULT_LIMIT, status, clientId } = req.query;
      const skip = (page - 1) * limit;

      let query = { deletedAt: null };
      if (status) query.status = status;
      if (clientId) query.client = clientId;

      const shipments = await Shipment.find(query)
        .populate('client', 'name email phone')
        .populate('driver', 'name phone')
        .populate('truck', 'truckNumber plateNumber')
        .skip(skip)
        .limit(Math.min(parseInt(limit), PAGINATION.MAX_LIMIT))
        .sort({ createdAt: -1 });

      const total = await Shipment.countDocuments(query);

      res.json({
        success: true,
        data: shipments,
        pagination: {
          total,
          page: parseInt(page),
          limit: parseInt(limit),
          pages: Math.ceil(total / limit)
        }
      });
    } catch (error) {
      logger.error('Get shipments error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get single shipment by ID
   */
  static async getById(req, res) {
    try {
      const shipment = await Shipment.findById(req.params.id)
        .populate('client', 'name email phone')
        .populate('driver', 'name phone')
        .populate('truck', 'truckNumber plateNumber')
        .populate('supervisor', 'name');

      if (!shipment || shipment.deletedAt) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      res.json({
        success: true,
        data: shipment
      });
    } catch (error) {
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Create new shipment
   */
  static async create(req, res) {
    try {
      const {
        description,
        weight,
        quantity,
        pickupLocation,
        deliveryLocation,
        estimatedCost,
        specialInstructions
      } = req.body;

      const shipmentNumber = Helpers.generateShipmentNumber();

      const shipment = new Shipment({
        shipmentNumber,
        client: req.user.role === 'client' ? req.user._id : req.body.client,
        description,
        weight,
        quantity,
        pickupLocation,
        deliveryLocation,
        estimatedCost,
        specialInstructions,
        status: SHIPMENT_STATUS.PENDING
      });

      await shipment.save();
      await shipment.populate(['client', 'driver', 'truck']);

      logger.success('Shipment created', { shipmentNumber, client: req.user._id });

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: MESSAGES.CREATED,
        data: shipment
      });
    } catch (error) {
      logger.error('Create shipment error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Update shipment
   */
  static async update(req, res) {
    try {
      const shipment = await Shipment.findByIdAndUpdate(
        req.params.id,
        { ...req.body, updatedAt: new Date() },
        { new: true, runValidators: true }
      ).populate(['client', 'driver', 'truck']);

      if (!shipment) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      logger.success('Shipment updated', { shipmentId: req.params.id });

      res.json({
        success: true,
        message: MESSAGES.UPDATED,
        data: shipment
      });
    } catch (error) {
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Update shipment status
   */
  static async updateStatus(req, res) {
    try {
      const { status, notes } = req.body;

      if (!Object.values(SHIPMENT_STATUS).includes(status)) {
        return res.status(HTTP_STATUS.BAD_REQUEST).json({
          success: false,
          message: 'Invalid status'
        });
      }

      const shipment = await Shipment.findByIdAndUpdate(
        req.params.id,
        {
          status,
          notes,
          updatedAt: new Date(),
          ...(status === SHIPMENT_STATUS.DELIVERED && { actualDeliveryDate: new Date() }),
          ...(status === SHIPMENT_STATUS.IN_TRANSIT && { actualPickupDate: new Date() })
        },
        { new: true }
      ).populate(['client', 'driver']);

      if (!shipment) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      logger.success('Shipment status updated', { shipmentId: req.params.id, status });

      res.json({
        success: true,
        message: MESSAGES.UPDATED,
        data: shipment
      });
    } catch (error) {
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Delete shipment (Soft Delete)
   */
  static async delete(req, res) {
    try {
      const shipment = await Shipment.findByIdAndUpdate(
        req.params.id,
        { deletedAt: new Date() },
        { new: true }
      );

      if (!shipment) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      logger.success('Shipment deleted', { shipmentId: req.params.id });

      res.json({
        success: true,
        message: MESSAGES.DELETED
      });
    } catch (error) {
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get shipment statistics
   */
  static async getStatistics(req, res) {
    try {
      const { timeRange = 'month' } = req.query;
      const startDate = this.getStartDate(timeRange);

      const total = await Shipment.countDocuments({ createdAt: { $gte: startDate }, deletedAt: null });
      const completed = await Shipment.countDocuments({ 
        status: SHIPMENT_STATUS.DELIVERED, 
        createdAt: { $gte: startDate },
        deletedAt: null 
      });
      const pending = await Shipment.countDocuments({ 
        status: SHIPMENT_STATUS.PENDING,
        createdAt: { $gte: startDate },
        deletedAt: null 
      });
      const inTransit = await Shipment.countDocuments({ 
        status: SHIPMENT_STATUS.IN_TRANSIT,
        createdAt: { $gte: startDate },
        deletedAt: null 
      });

      res.json({
        success: true,
        data: {
          total,
          completed,
          pending,
          inTransit,
          completionRate: total > 0 ? Math.round((completed / total) * 100) : 0
        }
      });
    } catch (error) {
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  static getStartDate(timeRange) {
    const now = new Date();
    switch (timeRange) {
      case 'week':
        return new Date(now.setDate(now.getDate() - 7));
      case 'month':
        return new Date(now.setMonth(now.getMonth() - 1));
      case 'year':
        return new Date(now.setFullYear(now.getFullYear() - 1));
      default:
        return new Date(now.setDate(now.getDate() - 7));
    }
  }
}

module.exports = ShipmentController;
