/**
 * ============================================
 * 📋 Audit Log Controller - نظام إدهام
 * Edham Logistics - Audit Trail Controller
 * ============================================
 */

const AuditLog = require('../models/AuditLog');
const { MESSAGES, HTTP_STATUS } = require('../config/constants');
const logger = require('../utils/logger');

class AuditLogController {
  /**
   * Get all audit logs with filters
   */
  static async getAll(req, res) {
    try {
      const { userId, action, entity, startDate, endDate, limit = 100, page = 1 } = req.query;
      
      let query = {};
      
      if (userId) query.user = userId;
      if (action) query.action = action;
      if (entity) query.entity = entity;
      if (startDate && endDate) {
        query.createdAt = {
          $gte: new Date(startDate),
          $lte: new Date(endDate)
        };
      }
      
      const logs = await AuditLog.find(query)
        .populate('user', 'name email role')
        .skip((page - 1) * limit)
        .sort({ createdAt: -1 })
        .limit(parseInt(limit));
      
      const total = await AuditLog.countDocuments(query);

      res.json({
        success: true,
        data: logs,
        pagination: { total, page: parseInt(page), limit: parseInt(limit) }
      });
    } catch (error) {
      logger.error('Get audit logs error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get audit log statistics
   */
  static async getStats(req, res) {
    try {
      const stats = await AuditLog.aggregate([
        {
          $group: {
            _id: '$action',
            count: { $sum: 1 }
          }
        },
        {
          $sort: { count: -1 }
        }
      ]);
      
      const totalLogs = await AuditLog.countDocuments();
      const todayLogs = await AuditLog.countDocuments({
        createdAt: {
          $gte: new Date(new Date().setHours(0, 0, 0, 0))
        }
      });

      // Get recent activity (last 7 days)
      const last7Days = await AuditLog.aggregate([
        {
          $match: {
            createdAt: {
              $gte: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000)
            }
          }
        },
        {
          $group: {
            _id: {
              date: { $dateToString: { format: '%Y-%m-%d', date: '$createdAt' } },
              action: '$action'
            },
            count: { $sum: 1 }
          }
        },
        {
          $sort: { '_id.date': -1 }
        }
      ]);
      
      res.json({
        success: true,
        data: {
          byAction: stats,
          total: totalLogs,
          today: todayLogs,
          last7Days
        }
      });
    } catch (error) {
      logger.error('Get audit log stats error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Create audit log
   */
  static async create(req, res) {
    try {
      const { user, action, entity, entityId, description, status, ipAddress, metadata } = req.body;

      const log = new AuditLog({
        user,
        action,
        entity,
        entityId,
        description,
        status,
        ipAddress,
        metadata,
        createdAt: new Date()
      });

      await log.save();
      await log.populate('user', 'name email role');

      res.status(HTTP_STATUS.CREATED).json({
        success: true,
        message: MESSAGES.CREATED,
        data: log
      });
    } catch (error) {
      logger.error('Create audit log error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Export audit logs to CSV
   */
  static async export(req, res) {
    try {
      const { startDate, endDate } = req.query;
      
      let query = {};
      if (startDate && endDate) {
        query.createdAt = {
          $gte: new Date(startDate),
          $lte: new Date(endDate)
        };
      }
      
      const logs = await AuditLog.find(query)
        .populate('user', 'name email role')
        .sort({ createdAt: -1 });
      
      // Create CSV with proper headers
      const csv = [
        'التاريخ,المستخدم,البريد الإلكتروني,الإجراء,الكائن,الحالة,عنوان IP,التفاصيل',
        ...logs.map(log => {
          const date = new Date(log.createdAt).toLocaleString('ar-SA');
          const userName = log.user?.name || 'غير معروف';
          const email = log.user?.email || 'N/A';
          const action = log.action || '';
          const entity = log.entity || '';
          const status = log.status || '';
          const ip = log.ipAddress || 'N/A';
          const details = log.description || '';
          return `${date},${userName},${email},${action},${entity},${status},${ip},"${details}"`;
        })
      ].join('\n');
      
      res.setHeader('Content-Type', 'text/csv; charset=utf-8');
      res.setHeader('Content-Disposition', `attachment; filename=audit-logs-${Date.now()}.csv`);
      res.send('\uFEFF' + csv); // BOM for Arabic support
    } catch (error) {
      logger.error('Export audit logs error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }

  /**
   * Get entity history
   */
  static async getEntityHistory(req, res) {
    try {
      const { entity, entityId } = req.params;
      const { limit = 50 } = req.query;

      const logs = await AuditLog.find({ entity, entityId })
        .populate('user', 'name email role')
        .sort({ createdAt: -1 })
        .limit(parseInt(limit));

      res.json({
        success: true,
        data: logs,
        total: logs.length
      });
    } catch (error) {
      logger.error('Get entity history error', error);
      res.status(HTTP_STATUS.INTERNAL_ERROR).json({
        success: false,
        message: MESSAGES.ERROR
      });
    }
  }
}

module.exports = AuditLogController;
