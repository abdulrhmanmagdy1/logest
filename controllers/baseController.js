/**
 * ============================================
 * 🎯 Base Controller - نظام إدهام
 * Edham Logistics - Base Controller Class
 * ============================================
 * Provides common CRUD operations for all controllers
 */

const logger = require('../utils/logger');
const { HTTP_STATUS, MESSAGES } = require('../config/constants');

class BaseController {
  /**
   * @param {mongoose.Model} model - Mongoose model
   * @param {string} modelName - Name of the model for logging
   * @param {Array} populateFields - Fields to populate on queries
   */
  constructor(model, modelName, populateFields = []) {
    this.model = model;
    this.modelName = modelName;
    this.populateFields = populateFields;
  }

  /**
   * Build query with filters
   */
  buildQuery(filters = {}) {
    let query = { deletedAt: null };
    
    Object.keys(filters).forEach(key => {
      if (filters[key] !== undefined && filters[key] !== null && filters[key] !== '') {
        query[key] = filters[key];
      }
    });
    
    return query;
  }

  /**
   * Get all records with pagination
   */
  async getAll(req, res, additionalFilters = {}) {
    try {
      const { page = 1, limit = 20, sort = '-createdAt', ...filters } = req.query;
      
      const query = this.buildQuery({ ...filters, ...additionalFilters });
      
      let dbQuery = this.model.find(query);
      
      // Populate related fields
      this.populateFields.forEach(field => {
        dbQuery = dbQuery.populate(field);
      });
      
      const [data, total] = await Promise.all([
        dbQuery
          .skip((page - 1) * limit)
          .limit(parseInt(limit))
          .sort(sort)
          .exec(),
        this.model.countDocuments(query)
      ]);

      res.json({
        success: true,
        data,
        pagination: {
          total,
          page: parseInt(page),
          limit: parseInt(limit),
          pages: Math.ceil(total / limit)
        }
      });
    } catch (error) {
      logger.error(`Get all ${this.modelName} error`, error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get single record by ID
   */
  async getById(req, res, id = req.params.id) {
    try {
      let query = this.model.findById(id);
      
      this.populateFields.forEach(field => {
        query = query.populate(field);
      });
      
      const data = await query.exec();

      if (!data || data.deletedAt) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      res.json({
        success: true,
        data
      });
    } catch (error) {
      logger.error(`Get ${this.modelName} by id error`, error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Create new record
   */
  async create(req, res, additionalData = {}) {
    try {
      const data = new this.model({
        ...req.body,
        ...additionalData
      });

      await data.save();
      
      // Populate after save
      this.populateFields.forEach(field => {
        data.populate(field);
      });
      await data.execPopulate?.();

      logger.success(`${this.modelName} created`, { id: data._id });

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: MESSAGES.CREATED,
        data
      });
    } catch (error) {
      logger.error(`Create ${this.modelName} error`, error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Update record
   */
  async update(req, res, id = req.params.id) {
    try {
      const data = await this.model.findByIdAndUpdate(
        id,
        { ...req.body, updatedAt: new Date() },
        { new: true, runValidators: true }
      );

      if (!data) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      logger.success(`${this.modelName} updated`, { id });

      res.json({
        success: true,
        message: MESSAGES.UPDATED,
        data
      });
    } catch (error) {
      logger.error(`Update ${this.modelName} error`, error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Soft delete record
   */
  async delete(req, res, id = req.params.id) {
    try {
      const data = await this.model.findByIdAndUpdate(
        id,
        { deletedAt: new Date() },
        { new: true }
      );

      if (!data) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: MESSAGES.NOT_FOUND
        });
      }

      logger.success(`${this.modelName} deleted`, { id });

      res.json({
        success: true,
        message: MESSAGES.DELETED
      });
    } catch (error) {
      logger.error(`Delete ${this.modelName} error`, error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get statistics
   */
  async getStatistics(req, res, groupBy = null) {
    try {
      const stats = await this.model.countDocuments({ deletedAt: null });
      
      let groupStats = [];
      if (groupBy) {
        groupStats = await this.model.aggregate([
          { $match: { deletedAt: null } },
          { $group: { _id: `$${groupBy}`, count: { $sum: 1 } } }
        ]);
      }

      res.json({
        success: true,
        data: {
          total: stats,
          byGroup: groupStats
        }
      });
    } catch (error) {
      logger.error(`Get ${this.modelName} statistics error`, error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }
}

module.exports = BaseController;
