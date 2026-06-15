/**
 * ============================================
 * 📊 Analytics Controller - نظام إدهام
 * Edham Logistics - Analytics & Reporting Controller
 * ============================================
 */

const Shipment = require('../models/Shipment');
const Truck = require('../models/Truck');
const User = require('../models/User');
const Invoice = require('../models/Invoice');
const { HTTP_STATUS, SHIPMENT_STATUS } = require('../config/constants');
const logger = require('../utils/logger');

class AnalyticsController {
  /**
   * Get dashboard metrics
   */
  static async getDashboardMetrics(req, res) {
    try {
      const { timeRange = 'month' } = req.query;
      const startDate = this.getStartDate(timeRange);

      // Shipments metrics
      const totalShipments = await Shipment.countDocuments({
        createdAt: { $gte: startDate },
        deletedAt: null
      });

      const completedShipments = await Shipment.countDocuments({
        status: SHIPMENT_STATUS.DELIVERED,
        createdAt: { $gte: startDate },
        deletedAt: null
      });

      const activeShipments = await Shipment.countDocuments({
        status: { $in: [SHIPMENT_STATUS.IN_TRANSIT, SHIPMENT_STATUS.ASSIGNED] },
        createdAt: { $gte: startDate },
        deletedAt: null
      });

      // Revenue metrics
      const invoiceData = await Invoice.aggregate([
        {
          $match: {
            issueDate: { $gte: startDate }
          }
        },
        {
          $group: {
            _id: null,
            totalRevenue: { $sum: '$total' },
            totalPaid: { $sum: '$amountPaid' },
            totalPending: { $sum: '$balanceDue' }
          }
        }
      ]);

      // Fleet metrics
      const activeTrucks = await Truck.countDocuments({
        status: 'active',
        deletedAt: null
      });

      const totalTrucks = await Truck.countDocuments({ deletedAt: null });

      // Average delivery time
      const deliveryStats = await Shipment.aggregate([
        {
          $match: {
            status: SHIPMENT_STATUS.DELIVERED,
            actualPickupDate: { $exists: true },
            actualDeliveryDate: { $exists: true },
            createdAt: { $gte: startDate }
          }
        },
        {
          $group: {
            _id: null,
            avgDeliveryTime: {
              $avg: {
                $subtract: ['$actualDeliveryDate', '$actualPickupDate']
              }
            },
            minDeliveryTime: {
              $min: {
                $subtract: ['$actualDeliveryDate', '$actualPickupDate']
              }
            },
            maxDeliveryTime: {
              $max: {
                $subtract: ['$actualDeliveryDate', '$actualPickupDate']
              }
            }
          }
        }
      ]);

      // On-time delivery rate
      const onTimeDeliveries = await Shipment.countDocuments({
        status: SHIPMENT_STATUS.DELIVERED,
        actualDeliveryDate: { $lte: '$scheduledDeliveryDate' },
        createdAt: { $gte: startDate }
      });

      const onTimeRate = totalShipments > 0
        ? Math.round((onTimeDeliveries / completedShipments) * 100)
        : 0;

      res.json({
        success: true,
        data: {
          shipments: {
            total: totalShipments,
            completed: completedShipments,
            active: activeShipments,
            completionRate: totalShipments > 0 ? Math.round((completedShipments / totalShipments) * 100) : 0
          },
          revenue: invoiceData[0] || { totalRevenue: 0, totalPaid: 0, totalPending: 0 },
          fleet: {
            total: totalTrucks,
            active: activeTrucks,
            utilization: totalTrucks > 0 ? Math.round((activeTrucks / totalTrucks) * 100) : 0
          },
          delivery: {
            avgTime: deliveryStats[0]?.avgDeliveryTime
              ? Math.round(deliveryStats[0].avgDeliveryTime / (1000 * 60 * 60))
              : 0,
            onTimeRate
          }
        }
      });
    } catch (error) {
      logger.error('Get dashboard metrics error', error);
      res.status(500).json({
        success: false,
        message: 'خطأ في الخادم'
      });
    }
  }

  /**
   * Get monthly report
   */
  static async getMonthlyReport(req, res) {
    try {
      const { year = new Date().getFullYear() } = req.query;

      const monthlyData = await Shipment.aggregate([
        {
          $match: {
            createdAt: {
              $gte: new Date(`${year}-01-01`),
              $lt: new Date(`${year + 1}-01-01`)
            }
          }
        },
        {
          $group: {
            _id: { $month: '$createdAt' },
            total: { $sum: 1 },
            completed: {
              $sum: { $cond: [{ $eq: ['$status', SHIPMENT_STATUS.DELIVERED] }, 1, 0] }
            },
            revenue: { $sum: '$actualCost' }
          }
        },
        { $sort: { _id: 1 } }
      ]);

      const monthNames = [
        'يناير', 'فبراير', 'مارس', 'أبريل', 'مايو', 'يونيو',
        'يوليو', 'أغسطس', 'سبتمبر', 'أكتوبر', 'نوفمبر', 'ديسمبر'
      ];

      const formattedData = monthlyData.map(m => ({
        month: monthNames[m._id - 1],
        monthNumber: m._id,
        total: m.total,
        completed: m.completed,
        revenue: m.revenue
      }));

      res.json({
        success: true,
        data: formattedData
      });
    } catch (error) {
      logger.error('Get monthly report error', error);
      res.status(500).json({
        success: false,
        message: 'خطأ في الخادم'
      });
    }
  }

  /**
   * Get driver performance
   */
  static async getDriverPerformance(req, res) {
    try {
      const drivers = await User.find({ role: 'driver', deletedAt: null });

      const driverStats = await Promise.all(drivers.map(async (driver) => {
        const totalTrips = await Shipment.countDocuments({
          driver: driver._id,
          status: SHIPMENT_STATUS.DELIVERED,
          deletedAt: null
        });

        const avgDeliveryTime = await Shipment.aggregate([
          {
            $match: {
              driver: driver._id,
              status: SHIPMENT_STATUS.DELIVERED,
              actualPickupDate: { $exists: true },
              actualDeliveryDate: { $exists: true },
              deletedAt: null
            }
          },
          {
            $group: {
              _id: null,
              avgTime: {
                $avg: {
                  $subtract: ['$actualDeliveryDate', '$actualPickupDate']
                }
              }
            }
          }
        ]);

        const onTimeDeliveries = await Shipment.countDocuments({
          driver: driver._id,
          status: SHIPMENT_STATUS.DELIVERED,
          actualDeliveryDate: { $lte: '$scheduledDeliveryDate' },
          deletedAt: null
        });

        const onTimeRate = totalTrips > 0 ? Math.round((onTimeDeliveries / totalTrips) * 100) : 0;

        return {
          driverId: driver._id,
          driverName: driver.name,
          totalTrips,
          avgDeliveryTime: avgDeliveryTime[0]?.avgTime
            ? Math.round(avgDeliveryTime[0].avgTime / (1000 * 60 * 60))
            : 0,
          onTimeRate,
          rating: (onTimeRate / 100) * 5 // out of 5
        };
      }));

      // Sort by rating
      driverStats.sort((a, b) => b.rating - a.rating);

      res.json({
        success: true,
        data: driverStats
      });
    } catch (error) {
      logger.error('Get driver performance error', error);
      res.status(500).json({
        success: false,
        message: 'خطأ في الخادم'
      });
    }
  }

  /**
   * Get shipments by city
   */
  static async getShipmentsByCity(req, res) {
    try {
      const shipmentsByCity = await Shipment.aggregate([
        {
          $match: { deletedAt: null }
        },
        {
          $group: {
            _id: '$deliveryLocation.city',
            total: { $sum: 1 },
            completed: {
              $sum: { $cond: [{ $eq: ['$status', SHIPMENT_STATUS.DELIVERED] }, 1, 0] }
            },
            revenue: { $sum: '$actualCost' }
          }
        },
        { $sort: { total: -1 } }
      ]);

      res.json({
        success: true,
        data: shipmentsByCity
      });
    } catch (error) {
      logger.error('Get shipments by city error', error);
      res.status(500).json({
        success: false,
        message: 'خطأ في الخادم'
      });
    }
  }

  /**
   * Get revenue breakdown
   */
  static async getRevenueBreakdown(req, res) {
    try {
      const { dateFrom, dateTo } = req.query;
      let query = {};

      if (dateFrom || dateTo) {
        query.issueDate = {};
        if (dateFrom) query.issueDate.$gte = new Date(dateFrom);
        if (dateTo) query.issueDate.$lte = new Date(dateTo);
      }

      const revenueByStatus = await Invoice.aggregate([
        { $match: query },
        {
          $group: {
            _id: '$status',
            total: { $sum: '$total' }
          }
        }
      ]);

      const revenueByClient = await Invoice.aggregate([
        { $match: query },
        {
          $group: {
            _id: '$client',
            total: { $sum: '$total' }
          }
        },
        { $sort: { total: -1 } },
        { $limit: 10 },
        {
          $lookup: {
            from: 'users',
            localField: '_id',
            foreignField: '_id',
            as: 'clientInfo'
          }
        }
      ]);

      res.json({
        success: true,
        data: {
          byStatus: revenueByStatus,
          topClients: revenueByClient
        }
      });
    } catch (error) {
      logger.error('Get revenue breakdown error', error);
      res.status(500).json({
        success: false,
        message: 'خطأ في الخادم'
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
        return new Date(now.setMonth(now.getMonth() - 1));
    }
  }
}

module.exports = AnalyticsController;
