/**
 * ============================================
 * 🔩 Spare Part Controller - نظام إدهام
 * Edham Logistics - Spare Parts Inventory Controller
 * ============================================
 */

const SparePart = require('../models/SparePart');
const { MESSAGES, HTTP_STATUS } = require('../config/constants');
const logger = require('../utils/logger');

class SparePartController {
  /**
   * Get all spare parts
   */
  static async getAll(req, res) {
    try {
      const { page = 1, limit = 20, truckId, category, lowStock } = req.query;
      
      let query = { deletedAt: null };
      if (truckId) query.truck = truckId;
      if (category) query.category = category;
      if (lowStock === 'true') query.$expr = { $lte: ['$quantity', '$minQuantity'] };

      const parts = await SparePart.find(query)
        .populate('truck', 'truckNumber plateNumber')
        .skip((page - 1) * limit)
        .limit(parseInt(limit))
        .sort({ createdAt: -1 });

      const total = await SparePart.countDocuments(query);

      res.json({
        success: true,
        data: parts,
        pagination: { total, page: parseInt(page), limit: parseInt(limit) }
      });
    } catch (error) {
      logger.error('Get spare parts error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get single spare part
   */
  static async getById(req, res) {
    try {
      const part = await SparePart.findById(req.params.id)
        .populate('truck', 'truckNumber plateNumber model');

      if (!part || part.deletedAt) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      res.json({
        success: true,
        data: part
      });
    } catch (error) {
      logger.error('Get spare part error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Create spare part
   */
  static async create(req, res) {
    try {
      const { name, partNumber, category, quantity, minQuantity, price, truck, supplier } = req.body;

      const part = new SparePart({
        name,
        partNumber,
        category,
        quantity,
        minQuantity: minQuantity || 1,
        price,
        truck,
        supplier,
        status: quantity > (minQuantity || 1) ? 'available' : 'low_stock'
      });

      await part.save();
      await part.populate('truck');

      logger.success('Spare part created', { partNumber });

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: MESSAGES.CREATED,
        data: part
      });
    } catch (error) {
      logger.error('Create spare part error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Update spare part
   */
  static async update(req, res) {
    try {
      const { quantity, minQuantity } = req.body;
      
      // Auto-update status based on quantity
      let status;
      if (quantity !== undefined) {
        const threshold = minQuantity || 1;
        status = quantity <= threshold ? 'low_stock' : 'available';
      }

      const part = await SparePart.findByIdAndUpdate(
        req.params.id,
        { ...req.body, status, updatedAt: new Date() },
        { new: true, runValidators: true }
      ).populate('truck');

      if (!part) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      logger.success('Spare part updated', { partId: req.params.id });

      res.json({
        success: true,
        message: MESSAGES.UPDATED,
        data: part
      });
    } catch (error) {
      logger.error('Update spare part error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Delete spare part (Soft Delete)
   */
  static async delete(req, res) {
    try {
      const part = await SparePart.findByIdAndUpdate(
        req.params.id,
        { deletedAt: new Date() },
        { new: true }
      );

      if (!part) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      logger.success('Spare part deleted', { partId: req.params.id });

      res.json({
        success: true,
        message: MESSAGES.DELETED
      });
    } catch (error) {
      logger.error('Delete spare part error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Update stock quantity
   */
  static async updateStock(req, res) {
    try {
      const { quantity, operation } = req.body;

      const part = await SparePart.findById(req.params.id);
      
      if (!part || part.deletedAt) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      let newQuantity;
      switch (operation) {
        case 'add':
          newQuantity = part.quantity + quantity;
          break;
        case 'subtract':
          newQuantity = part.quantity - quantity;
          break;
        case 'set':
          newQuantity = quantity;
          break;
        default:
          return res.status(HTTP_STATUS.BAD_REQUEST).json({
            success: false,
            message: 'Invalid operation. Use add, subtract, or set'
          });
      }

      part.quantity = newQuantity;
      part.status = newQuantity <= part.minQuantity ? 'low_stock' : 'available';
      await part.save();

      logger.success('Stock updated', { partId: req.params.id, newQuantity });

      res.json({
        success: true,
        message: 'Stock updated successfully',
        data: part
      });
    } catch (error) {
      logger.error('Update stock error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get low stock items
   */
  static async getLowStock(req, res) {
    try {
      const parts = await SparePart.find({
        $expr: { $lte: ['$quantity', '$minQuantity'] },
        deletedAt: null
      }).populate('truck', 'truckNumber plateNumber');

      res.json({
        success: true,
        data: parts,
        total: parts.length
      });
    } catch (error) {
      logger.error('Get low stock error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }
}

module.exports = SparePartController;
