/**
 * ============================================
 * 📦 Asset Controller - نظام إدهام الاحترافي
 * Edham Logistics - Asset & Inventory Management
 * ============================================
 */

const { MESSAGES, HTTP_STATUS } = require('../config/constants');
const logger = require('../utils/logger');
const Asset = require('../models/Asset');
const Inventory = require('../models/Inventory');
const Maintenance = require('../models/Maintenance');
const Transaction = require('../models/Transaction');

class AssetController {
  /**
   * Get assets with filtering and pagination
   */
  static async getAssets(req, res) {
    try {
      const {
        page = 1,
        limit = 20,
        search,
        category,
        status,
        location,
        assignedTo,
        sortBy = 'createdAt',
        sortOrder = 'desc'
      } = req.query;

      const query = {};
      
      if (search) {
        query.$or = [
          { name: { $regex: search, $options: 'i' } },
          { serialNumber: { $regex: search, $options: 'i' } },
          { tag: { $regex: search, $options: 'i' } },
          { description: { $regex: search, $options: 'i' } }
        ];
      }
      
      if (category) query.category = category;
      if (status) query.status = status;
      if (location) query['location.name'] = { $regex: location, $options: 'i' };
      if (assignedTo) query.assignedTo = assignedTo;

      const skip = (page - 1) * limit;
      const sort = { [sortBy]: sortOrder === 'desc' ? -1 : 1 };

      const [assets, total] = await Promise.all([
        Asset.find(query)
          .populate('category', 'name description')
          .populate('assignedTo', 'name email')
          .populate('location', 'name address')
          .sort(sort)
          .skip(skip)
          .limit(parseInt(limit)),
        Asset.countDocuments(query)
      ]);

      res.json({
        success: true,
        assets,
        pagination: {
          page: parseInt(page),
          limit: parseInt(limit),
          total,
          pages: Math.ceil(total / limit)
        }
      });
    } catch (error) {
      logger.error('Error getting assets:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Create new asset
   */
  static async createAsset(req, res) {
    try {
      const assetData = {
        ...req.body,
        createdBy: req.user.id,
        status: 'available'
      };

      // Check for duplicate serial number
      if (assetData.serialNumber) {
        const existingAsset = await Asset.findOne({ 
          serialNumber: assetData.serialNumber 
        });

        if (existingAsset) {
          return res.status(HTTP_STATUS.CONFLICT).json({
            success: false,
            message: 'الرقم التسلسلي مسجل بالفعل'
          });
        }
      }

      // Generate asset tag if not provided
      if (!assetData.tag) {
        assetData.tag = this.generateAssetTag(assetData.category);
      }

      const asset = new Asset(assetData);
      await asset.save();

      logger.success('Asset created', { assetId: asset._id });

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: 'تم إنشاء الأصل بنجاح',
        asset: await asset.populate('category assignedTo location')
      });
    } catch (error) {
      logger.error('Error creating asset:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Update asset
   */
  static async updateAsset(req, res) {
    try {
      const { id } = req.params;
      const updateData = {
        ...req.body,
        updatedBy: req.user.id,
        updatedAt: new Date()
      };

      const asset = await Asset.findByIdAndUpdate(
        id,
        updateData,
        { new: true, runValidators: true }
      ).populate('category assignedTo location');

      if (!asset) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: 'الأصل غير موجود'
        });
      }

      // Create audit log
      await this.createAssetLog(id, 'updated', req.user.id, updateData);

      logger.info('Asset updated', { assetId: id });

      res.json({
        success: true,
        message: 'تم تحديث الأصل بنجاح',
        asset
      });
    } catch (error) {
      logger.error('Error updating asset:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get asset details
   */
  static async getAssetDetails(req, res) {
    try {
      const { id } = req.params;

      const [asset, maintenanceHistory, logs] = await Promise.all([
        Asset.findById(id)
          .populate('category', 'name description')
          .populate('assignedTo', 'name email phone')
          .populate('location', 'name address coordinates')
          .populate('createdBy', 'name email')
          .populate('updatedBy', 'name email'),
        Maintenance.find({ assetId: id })
          .populate('performedBy', 'name email')
          .sort({ createdAt: -1 })
          .limit(10),
        this.getAssetLogs(id)
      ]);

      if (!asset) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: 'الأصل غير موجود'
        });
      }

      // Calculate asset metrics
      const metrics = await this.calculateAssetMetrics(id);

      res.json({
        success: true,
        asset: {
          ...asset.toObject(),
          maintenanceHistory,
          logs,
          metrics
        }
      });
    } catch (error) {
      logger.error('Error getting asset details:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get inventory with filtering
   */
  static async getInventory(req, res) {
    try {
      const {
        page = 1,
        limit = 20,
        search,
        category,
        location,
        status,
        lowStock = false,
        expiry = false
      } = req.query;

      const query = {};
      
      if (search) {
        query.$or = [
          { name: { $regex: search, $options: 'i' } },
          { sku: { $regex: search, $options: 'i' } },
          { description: { $regex: search, $options: 'i' } }
        ];
      }
      
      if (category) query.category = category;
      if (location) query['location.name'] = { $regex: location, $options: 'i' };
      if (status) query.status = status;
      
      if (lowStock === 'true') {
        query.$expr = { $lte: ['$currentStock', '$minStockLevel'] };
      }
      
      if (expiry === 'true') {
        query.expiryDate = { 
          $lte: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000) // 30 days
        };
      }

      const skip = (page - 1) * limit;

      const [inventory, total] = await Promise.all([
        Inventory.find(query)
          .populate('category', 'name')
          .populate('location', 'name')
          .populate('supplier', 'name email')
          .sort({ name: 1 })
          .skip(skip)
          .limit(parseInt(limit)),
        Inventory.countDocuments(query)
      ]);

      res.json({
        success: true,
        inventory,
        pagination: {
          page: parseInt(page),
          limit: parseInt(limit),
          total,
          pages: Math.ceil(total / limit)
        }
      });
    } catch (error) {
      logger.error('Error getting inventory:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Update inventory stock
   */
  static async updateInventoryStock(req, res) {
    try {
      const { id } = req.params;
      const { quantity, operation, reason, reference } = req.body;

      const inventory = await Inventory.findById(id);
      if (!inventory) {
        return res.status(HTTP_STATUS.NOT_FOUND).json({
          success: false,
          message: 'المخزون غير موجود'
        });
      }

      const previousStock = inventory.currentStock;
      let newStock = previousStock;

      switch (operation) {
        case 'add':
          newStock = previousStock + quantity;
          break;
        case 'subtract':
          newStock = Math.max(0, previousStock - quantity);
          break;
        case 'set':
          newStock = quantity;
          break;
        default:
          return res.status(HTTP_STATUS.BAD_REQUEST).json({
            success: false,
            message: 'عملية غير صالحة'
          });
      }

      // Update inventory
      inventory.currentStock = newStock;
      inventory.lastStockUpdate = new Date();
      inventory.updatedBy = req.user.id;

      await inventory.save();

      // Create stock movement record
      await this.createStockMovement(id, {
        operation,
        quantity,
        previousStock,
        newStock,
        reason,
        reference,
        performedBy: req.user.id
      });

      // Check if stock is low
      const isLowStock = newStock <= inventory.minStockLevel;
      
      logger.info('Inventory stock updated', { 
        inventoryId: id,
        operation,
        previousStock,
        newStock,
        isLowStock
      });

      res.json({
        success: true,
        message: 'تم تحديث المخزون بنجاح',
        inventory: {
          ...inventory.toObject(),
          previousStock,
          stockChange: newStock - previousStock,
          isLowStock
        }
      });
    } catch (error) {
      logger.error('Error updating inventory stock:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get asset categories
   */
  static async getAssetCategories(req, res) {
    try {
      const categories = await Asset.distinct('category');
      
      res.json({
        success: true,
        categories
      });
    } catch (error) {
      logger.error('Error getting asset categories:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get asset locations
   */
  static async getAssetLocations(req, res) {
    try {
      const locations = await Asset.distinct('location.name');
      
      res.json({
        success: true,
        locations
      });
    } catch (error) {
      logger.error('Error getting asset locations:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Get asset statistics
   */
  static async getAssetStatistics(req, res) {
    try {
      const [
        totalAssets,
        assetsByCategory,
        assetsByStatus,
        assetsByLocation,
        maintenanceStats,
        depreciationStats
      ] = await Promise.all([
        Asset.countDocuments(),
        Asset.aggregate([
          { $group: { _id: '$category', count: { $sum: 1 } } }
        ]),
        Asset.aggregate([
          { $group: { _id: '$status', count: { $sum: 1 } } }
        ]),
        Asset.aggregate([
          { $group: { _id: '$location.name', count: { $sum: 1 } } }
        ]),
        this.getMaintenanceStatistics(),
        this.getDepreciationStatistics()
      ]);

      const totalValue = await Asset.aggregate([
        { $group: { _id: null, total: { $sum: '$purchaseValue' } } }
      ]);

      res.json({
        success: true,
        statistics: {
          total: totalAssets,
          totalValue: totalValue[0]?.total || 0,
          byCategory: assetsByCategory,
          byStatus: assetsByStatus,
          byLocation: assetsByLocation,
          maintenance: maintenanceStats,
          depreciation: depreciationStats
        }
      });
    } catch (error) {
      logger.error('Error getting asset statistics:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Generate asset report
   */
  static async generateAssetReport(req, res) {
    try {
      const {
        category,
        status,
        location,
        assignedTo,
        format = 'json',
        includeMaintenance = false,
        includeDepreciation = false
      } = req.query;

      const query = {};
      if (category) query.category = category;
      if (status) query.status = status;
      if (location) query['location.name'] = location;
      if (assignedTo) query.assignedTo = assignedTo;

      const assets = await Asset.find(query)
        .populate('category', 'name')
        .populate('assignedTo', 'name email')
        .populate('location', 'name address');

      let reportData = {
        title: 'تقرير الأصول',
        generatedAt: new Date(),
        filters: { category, status, location, assignedTo },
        assets: assets
      };

      if (includeMaintenance === 'true') {
        const maintenanceData = await this.getMaintenanceReportData(assets);
        reportData.maintenance = maintenanceData;
      }

      if (includeDepreciation === 'true') {
        const depreciationData = await this.getDepreciationReportData(assets);
        reportData.depreciation = depreciationData;
      }

      if (format === 'excel') {
        const buffer = await this.generateAssetExcelReport(reportData);
        res.setHeader('Content-Type', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
        res.setHeader('Content-Disposition', `attachment; filename="asset-report-${Date.now()}.xlsx"`);
        res.send(buffer);
      } else if (format === 'pdf') {
        const buffer = await this.generateAssetPDFReport(reportData);
        res.setHeader('Content-Type', 'application/pdf');
        res.setHeader('Content-Disposition', `attachment; filename="asset-report-${Date.now()}.pdf"`);
        res.send(buffer);
      } else {
        res.json({
          success: true,
          report: reportData
        });
      }
    } catch (error) {
      logger.error('Error generating asset report:', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.INTERNAL_ERROR
      });
    }
  }

  /**
   * Generate asset tag
   */
  generateAssetTag(category) {
    const prefix = category ? category.substring(0, 3).toUpperCase() : 'AST';
    const timestamp = Date.now().toString(36).toUpperCase();
    const random = Math.random().toString(36).substring(2, 6).toUpperCase();
    return `${prefix}-${timestamp}-${random}`;
  }

  /**
   * Create asset log
   */
  async createAssetLog(assetId, action, userId, details) {
    try {
      const AssetLog = require('../models/AssetLog');
      
      const log = new AssetLog({
        assetId,
        action,
        userId,
        details,
        timestamp: new Date()
      });

      await log.save();
    } catch (error) {
      logger.error('Error creating asset log:', error);
    }
  }

  /**
   * Get asset logs
   */
  async getAssetLogs(assetId) {
    try {
      const AssetLog = require('../models/AssetLog');
      
      return await AssetLog.find({ assetId })
        .populate('userId', 'name email')
        .sort({ timestamp: -1 })
        .limit(50);
    } catch (error) {
      logger.error('Error getting asset logs:', error);
      return [];
    }
  }

  /**
   * Calculate asset metrics
   */
  async calculateAssetMetrics(assetId) {
    try {
      const [
        totalMaintenanceCost,
        maintenanceCount,
        downtimeHours,
        utilizationRate
      ] = await Promise.all([
        Maintenance.aggregate([
          { $match: { assetId } },
          { $group: { _id: null, total: { $sum: '$cost' } } }
        ]),
        Maintenance.countDocuments({ assetId }),
        this.calculateDowntimeHours(assetId),
        this.calculateUtilizationRate(assetId)
      ]);

      return {
        totalMaintenanceCost: totalMaintenanceCost[0]?.total || 0,
        maintenanceCount,
        downtimeHours,
        utilizationRate,
        lastMaintenance: await this.getLastMaintenanceDate(assetId)
      };
    } catch (error) {
      logger.error('Error calculating asset metrics:', error);
      return {};
    }
  }

  /**
   * Create stock movement record
   */
  async createStockMovement(inventoryId, movementData) {
    try {
      const StockMovement = require('../models/StockMovement');
      
      const movement = new StockMovement({
        inventoryId,
        ...movementData
      });

      await movement.save();
    } catch (error) {
      logger.error('Error creating stock movement:', error);
    }
  }

  /**
   * Get maintenance statistics
   */
  async getMaintenanceStatistics() {
    try {
      const [
        totalMaintenance,
        maintenanceByType,
        maintenanceByStatus,
        upcomingMaintenance,
        totalCost
      ] = await Promise.all([
        Maintenance.countDocuments(),
        Maintenance.aggregate([
          { $group: { _id: '$type', count: { $sum: 1 } } }
        ]),
        Maintenance.aggregate([
          { $group: { _id: '$status', count: { $sum: 1 } } }
        ]),
        Maintenance.countDocuments({
          status: 'scheduled',
          scheduledDate: { $lte: new Date(Date.now() + 7 * 24 * 60 * 60 * 1000) }
        }),
        Maintenance.aggregate([
          { $group: { _id: null, total: { $sum: '$cost' } } }
        ])
      ]);

      return {
        total: totalMaintenance,
        byType: maintenanceByType,
        byStatus: maintenanceByStatus,
        upcoming: upcomingMaintenance,
        totalCost: totalCost[0]?.total || 0
      };
    } catch (error) {
      logger.error('Error getting maintenance statistics:', error);
      return {};
    }
  }

  /**
   * Get depreciation statistics
   */
  async getDepreciationStatistics() {
    try {
      const assets = await Asset.find({
        purchaseDate: { $exists: true },
        purchaseValue: { $exists: true },
        depreciationRate: { $exists: true }
      });

      let totalDepreciation = 0;
      let currentValue = 0;

      assets.forEach(asset => {
        const yearsSincePurchase = (Date.now() - new Date(asset.purchaseDate).getTime()) / (365.25 * 24 * 60 * 60 * 1000);
        const depreciation = asset.purchaseValue * (asset.depreciationRate / 100) * Math.min(yearsSincePurchase, asset.usefulLife || 10);
        
        totalDepreciation += depreciation;
        currentValue += (asset.purchaseValue - depreciation);
      });

      return {
        totalDepreciation,
        currentValue,
        originalValue: assets.reduce((sum, asset) => sum + asset.purchaseValue, 0)
      };
    } catch (error) {
      logger.error('Error getting depreciation statistics:', error);
      return {};
    }
  }

  /**
   * Calculate downtime hours
   */
  async calculateDowntimeHours(assetId) {
    try {
      const maintenanceRecords = await Maintenance.find({
        assetId,
        status: 'completed',
        downtime: { $exists: true }
      });

      return maintenanceRecords.reduce((total, record) => total + (record.downtime || 0), 0);
    } catch (error) {
      logger.error('Error calculating downtime:', error);
      return 0;
    }
  }

  /**
   * Calculate utilization rate
   */
  async calculateUtilizationRate(assetId) {
    try {
      // This would integrate with usage tracking systems
      // For now, return a placeholder calculation
      const asset = await Asset.findById(assetId);
      
      if (!asset || !asset.category) {
        return 0;
      }

      // Different utilization calculations based on asset category
      const categoryUtilization = {
        'vehicle': 0.75, // 75% average vehicle utilization
        'equipment': 0.60, // 60% average equipment utilization
        'building': 0.85, // 85% average building utilization
        'it': 0.90 // 90% average IT utilization
      };

      return categoryUtilization[asset.category] || 0.70;
    } catch (error) {
      logger.error('Error calculating utilization:', error);
      return 0;
    }
  }

  /**
   * Get last maintenance date
   */
  async getLastMaintenanceDate(assetId) {
    try {
      const lastMaintenance = await Maintenance.findOne({ assetId })
        .sort({ completedDate: -1 })
        .select('completedDate');

      return lastMaintenance?.completedDate || null;
    } catch (error) {
      logger.error('Error getting last maintenance date:', error);
      return null;
    }
  }

  /**
   * Generate asset Excel report
   */
  async generateAssetExcelReport(reportData) {
    const ExcelJS = require('exceljs');
    const workbook = new ExcelJS.Workbook();
    const worksheet = workbook.addWorksheet('الأصول');

    // Add headers
    worksheet.columns = [
      { header: 'الاسم', key: 'name', width: 20 },
      { header: 'الرقم التسلسلي', key: 'serialNumber', width: 20 },
      { header: 'العلامة', key: 'tag', width: 15 },
      { header: 'الفئة', key: 'category.name', width: 15 },
      { header: 'الحالة', key: 'status', width: 15 },
      { header: 'الموقع', key: 'location.name', width: 20 },
      { header: 'المسند إليه', key: 'assignedTo.name', width: 20 },
      { header: 'قيمة الشراء', key: 'purchaseValue', width: 15 },
      { header: 'تاريخ الشراء', key: 'purchaseDate', width: 15 }
    ];

    // Add data
    worksheet.addRows(reportData.assets);

    return await workbook.xlsx.writeBuffer();
  }

  /**
   * Generate asset PDF report
   */
  async generateAssetPDFReport(reportData) {
    const PDFDocument = require('pdfkit');
    
    return new Promise((resolve, reject) => {
      try {
        const doc = new PDFDocument();
        
        // Title
        doc.fontSize(20).text(reportData.title, { align: 'center' });
        doc.moveDown();
        
        // Generation date
        doc.fontSize(12).text(`تم الإنشاء: ${reportData.generatedAt.toLocaleString('ar-SA')}`, { align: 'center' });
        doc.moveDown(2);

        // Assets table
        doc.fontSize(14).text('الأصول', { underline: true });
        doc.moveDown();

        reportData.assets.forEach((asset, index) => {
          doc.fontSize(10).text(`${index + 1}. ${asset.name}`);
          doc.fontSize(9).text(`  الرقم التسلسلي: ${asset.serialNumber || 'N/A'}`);
          doc.fontSize(9).text(`  الفئة: ${asset.category?.name || 'N/A'}`);
          doc.fontSize(9).text(`  الحالة: ${asset.status}`);
          doc.fontSize(9).text(`  الموقع: ${asset.location?.name || 'N/A'}`);
          doc.moveDown();
        });

        doc.end();
        resolve(Buffer.concat([]));
      } catch (error) {
        reject(error);
      }
    });
  }
}

module.exports = AssetController;
