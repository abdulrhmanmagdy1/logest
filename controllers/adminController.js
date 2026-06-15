/**
 * ============================================
 * 📊 Edham Logistics - Admin Controller
 * نظام إدهام - متحكم الإدارة
 * ============================================
 */

const Order = require('../models/Order');
const Driver = require('../models/Driver');
const Truck = require('../models/Truck');
const Maintenance = require('../models/Maintenance');
const Payment = require('../models/Payment');
const User = require('../models/User');
const logger = require('../utils/logger');

class AdminController {
  /**
   * Supervisor Dashboard - Fleet Management
   * GET /api/v1/admin/supervisor/dashboard
   */
  static async getSupervisorDashboard(req, res) {
    try {
      // Get fleet statistics
      const fleetStats = await Driver.aggregate([
        {
          $match: {
            status: { $in: ['ON_DUTY', 'OFF_DUTY'] },
            is_deleted: false
          }
        },
        {
          $group: {
            _id: null,
            total_drivers: { $sum: 1 },
            active_drivers: {
              $sum: { $cond: [{ $eq: ['$status', 'ON_DUTY'] }, 1, 0] }
            },
            available_drivers: {
              $sum: { $cond: [{ $eq: ['$is_available', true] }, 1, 0] }
            }
          }
        }
      ]);

      // Get active orders
      const activeOrders = await Order.find({
        status: { $in: ['ASSIGNED', 'PICKUP_CONFIRMED', 'IN_TRANSIT'] },
        is_deleted: false
      })
      .populate('driver.driver_id', 'name phone')
      .populate('customer_id', 'name')
      .sort({ created_at: -1 });

      // Get pending orders for dispatch
      const pendingOrders = await Order.find({
        status: { $in: ['CONFIRMED', 'SEARCHING_FOR_DRIVER'] },
        is_deleted: false
      })
      .populate('customer_id', 'name phone')
      .sort({ created_at: -1 });

      // Get available drivers with locations
      const availableDrivers = await Driver.find({
        status: 'ON_DUTY',
        is_available: true,
        'current_task.task_state': 'IDLE',
        is_deleted: false
      })
      .populate('assigned_vehicle.truck_id', 'plate_number model type')
      .select('name phone current_location current_task performance');

      // Get fleet locations for live map
      const fleetLocations = await Driver.find({
        status: 'ON_DUTY',
        'current_location.coordinates': { $ne: [0, 0] },
        is_deleted: false
      })
      .populate('assigned_vehicle.truck_id', 'plate_number type')
      .select('name current_location status is_available current_task');

      res.json({
        success: true,
        data: {
          fleet_stats: fleetStats[0] || { total_drivers: 0, active_drivers: 0, available_drivers: 0 },
          active_orders,
          pending_orders: pendingOrders,
          available_drivers,
          fleet_locations: fleetLocations
        }
      });

    } catch (error) {
      logger.error('Error getting supervisor dashboard:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Find nearby drivers for order dispatch
   * GET /api/v1/admin/supervisor/nearby-drivers/:orderId
   */
  static async getNearbyDrivers(req, res) {
    try {
      const { orderId } = req.params;
      
      const order = await Order.findById(orderId);
      if (!order) {
        return res.status(404).json({
          success: false,
          message: 'Order not found'
        });
      }

      const pickupLocation = order.route.pickup.location.coordinates;
      const vehicleType = order.vehicle.type;

      // Find nearby drivers using geospatial query
      const nearbyDrivers = await Driver.aggregate([
        {
          $match: {
            status: 'ON_DUTY',
            is_available: true,
            'current_task.task_state': 'IDLE',
            is_deleted: false
          }
        },
        {
          $geoNear: {
            near: {
              type: 'Point',
              coordinates: pickupLocation
            },
            distanceField: 'distance',
            maxDistance: 50000, // 50km radius
            spherical: true,
            query: {
              'work_schedule.service_areas': {
                $geoIntersects: {
                  $geometry: {
                    type: 'Point',
                    coordinates: pickupLocation
                  }
                }
              }
            }
          }
        },
        {
          $lookup: {
            from: 'trucks',
            localField: 'assigned_vehicle.truck_id',
            foreignField: '_id',
            as: 'vehicle'
          }
        },
        {
          $unwind: '$vehicle'
        },
        {
          $match: {
            'vehicle.type': vehicleType,
            'vehicle.status': 'AVAILABLE'
          }
        },
        {
          $project: {
            name: 1,
            phone: 1,
            current_location: 1,
            distance: 1,
            performance: {
              total_orders: 1,
              completed_orders: 1,
              average_rating: 1,
              on_time_delivery_rate: 1
            },
            vehicle: {
              plate_number: 1,
              model: 1,
              type: 1,
              capacity: 1
            }
          }
        },
        {
          $sort: {
            distance: 1,
            'performance.average_rating': -1,
            'performance.on_time_delivery_rate': -1
          }
        },
        {
          $limit: 3
        }
      ]);

      res.json({
        success: true,
        data: {
          nearby_drivers: nearbyDrivers,
          order_info: {
            order_number: order.order_number,
            pickup_location: order.route.pickup.address,
            vehicle_type: vehicleType,
            estimated_distance_km: order.route.distance_km
          }
        }
      });

    } catch (error) {
      logger.error('Error getting nearby drivers:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Assign order to driver
   * POST /api/v1/admin/supervisor/assign-order
   */
  static async assignOrderToDriver(req, res) {
    try {
      const { orderId, driverId } = req.body;

      const order = await Order.findById(orderId);
      if (!order) {
        return res.status(404).json({
          success: false,
          message: 'Order not found'
        });
      }

      const driver = await Driver.findById(driverId);
      if (!driver) {
        return res.status(404).json({
          success: false,
          message: 'Driver not found'
        });
      }

      // Assign order to driver
      await driver.assignTask(orderId, new Date(Date.now() + 2 * 60 * 60 * 1000)); // 2 hours ETA
      
      // Update order
      order.driver.driver_id = driverId;
      order.driver.truck_id = driver.assigned_vehicle.truck_id;
      order.driver.assigned_at = new Date();
      order.status = 'SEARCHING_FOR_DRIVER';
      
      await order.save();

      // Broadcast to driver
      const socketService = require('../services/socketService');
      const driverSocketId = socketService.connectedClients.get(driverId);
      if (driverSocketId) {
        socketService.io.to(driverSocketId).emit('task_assigned', {
          order_id: order._id,
          order_number: order.order_number,
          route: order.route,
          cargo: order.cargo,
          invoice: order.invoice,
          customer: order.customer_id
        });
      }

      res.json({
        success: true,
        message: 'Order assigned successfully',
        data: {
          order_number: order.order_number,
          driver_name: driver.name,
          estimated_completion: driver.current_task.estimated_completion
        }
      });

    } catch (error) {
      logger.error('Error assigning order:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Accountant Dashboard - Financial Management
   * GET /api/v1/admin/accountant/dashboard
   */
  static async getAccountantDashboard(req, res) {
    try {
      const { startDate, endDate, paymentStatus } = req.query;

      // Build date filter
      const dateFilter = {};
      if (startDate || endDate) {
        dateFilter.created_at = {};
        if (startDate) dateFilter.created_at.$gte = new Date(startDate);
        if (endDate) dateFilter.created_at.$lte = new Date(endDate);
      }

      // Build payment status filter
      const statusFilter = {};
      if (paymentStatus) {
        statusFilter['invoice.payment_status'] = paymentStatus;
      }

      // Financial metrics aggregation
      const financialMetrics = await Order.aggregate([
        {
          $match: {
            ...dateFilter,
            ...statusFilter,
            is_deleted: false
          }
        },
        {
          $group: {
            _id: null,
            total_revenue: { $sum: '$invoice.total_amount' },
            total_orders: { $sum: 1 },
            paid_orders: {
              $sum: { $cond: [{ $eq: ['$invoice.payment_status', 'PAID'] }, 1, 0] }
            },
            pending_orders: {
              $sum: { $cond: [{ $eq: ['$invoice.payment_status', 'PENDING'] }, 1, 0] }
            },
            failed_orders: {
              $sum: { $cond: [{ $eq: ['$invoice.payment_status', 'FAILED'] }, 1, 0] }
            },
            total_tax: { $sum: '$invoice.tax_amount' },
            total_discounts: { $sum: { $sum: '$invoice.discounts.amount' } }
          }
        }
      ]);

      // Daily revenue trend
      const dailyRevenue = await Order.aggregate([
        {
          $match: {
            ...dateFilter,
            ...statusFilter,
            is_deleted: false
          }
        },
        {
          $group: {
            _id: {
              $dateToString: {
                format: '%Y-%m-%d',
                date: '$created_at'
              }
            },
            revenue: { $sum: '$invoice.total_amount' },
            orders: { $sum: 1 },
            paid_revenue: {
              $sum: {
                $cond: [{ $eq: ['$invoice.payment_status', 'PAID'] }, '$invoice.total_amount', 0]
              }
            }
          }
        },
        {
          $sort: { _id: 1 }
        }
      ]);

      // Payment method distribution
      const paymentMethods = await Order.aggregate([
        {
          $match: {
            ...dateFilter,
            ...statusFilter,
            is_deleted: false
          }
        },
        {
          $group: {
            _id: '$invoice.payment_method',
            count: { $sum: 1 },
            total: { $sum: '$invoice.total_amount' }
          }
        }
      ]);

      // Recent invoices with details
      const recentInvoices = await Order.find({
        ...dateFilter,
        ...statusFilter,
        is_deleted: false
      })
      .populate('customer_id', 'name email phone')
      .populate('driver.driver_id', 'name')
      .sort({ created_at: -1 })
      .limit(50);

      res.json({
        success: true,
        data: {
          financial_metrics: financialMetrics[0] || {
            total_revenue: 0,
            total_orders: 0,
            paid_orders: 0,
            pending_orders: 0,
            failed_orders: 0,
            total_tax: 0,
            total_discounts: 0
          },
          daily_revenue: dailyRevenue,
          payment_methods: paymentMethods,
          recent_invoices: recentInvoices
        }
      });

    } catch (error) {
      logger.error('Error getting accountant dashboard:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Generate invoice PDF
   * GET /api/v1/admin/accountant/invoice/:orderId/pdf
   */
  static async generateInvoicePDF(req, res) {
    try {
      const { orderId } = req.params;

      const order = await Order.findById(orderId)
        .populate('customer_id', 'name email phone address')
        .populate('driver.driver_id', 'name phone')
        .populate('driver.truck_id', 'plate_number model');

      if (!order) {
        return res.status(404).json({
          success: false,
          message: 'Order not found'
        });
      }

      // In a real implementation, generate PDF using a library like puppeteer or pdfkit
      // For now, return invoice data that can be used to generate PDF on frontend
      const invoiceData = {
        invoice_number: `INV-${order.order_number}`,
        order_number: order.order_number,
        date: order.created_at,
        customer: order.customer_id,
        driver: order.driver.driver_id,
        truck: order.driver.truck_id,
        route: order.route,
        items: order.invoice.items,
        subtotal: order.invoice.subtotal,
        tax_amount: order.invoice.tax_amount,
        total_amount: order.invoice.total_amount,
        payment_status: order.invoice.payment_status,
        payment_method: order.invoice.payment_method
      };

      res.json({
        success: true,
        data: invoiceData
      });

    } catch (error) {
      logger.error('Error generating invoice PDF:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Workshop Dashboard - Fleet Maintenance
   * GET /api/v1/admin/workshop/dashboard
   */
  static async getWorkshopDashboard(req, res) {
    try {
      // Fleet health statistics
      const fleetHealth = await Truck.aggregate([
        {
          $match: { isDeleted: false }
        },
        {
          $group: {
            _id: null,
            total_trucks: { $sum: 1 },
            available_trucks: {
              $sum: { $cond: [{ $eq: ['$status', 'available'] }, 1, 0] }
            },
            in_use_trucks: {
              $sum: { $cond: [{ $eq: ['$status', 'in_use'] }, 1, 0] }
            },
            maintenance_trucks: {
              $sum: { $cond: [{ $eq: ['$status', 'maintenance'] }, 1, 0] }
            },
            out_of_service_trucks: {
              $sum: { $cond: [{ $eq: ['$status', 'out_of_service'] }, 1, 0] }
            }
          }
        }
      ]);

      // Maintenance due alerts
      const maintenanceAlerts = await Truck.aggregate([
        {
          $match: { isDeleted: false }
        },
        {
          $addFields: {
            days_until_oil_change: {
              $dateDiff: {
                startDate: new Date(),
                endDate: '$nextOilChange',
                unit: 'day'
              }
            },
            days_until_insurance: {
              $dateDiff: {
                startDate: new Date(),
                endDate: '$insuranceExpiry',
                unit: 'day'
              }
            },
            days_until_registration: {
              $dateDiff: {
                startDate: new Date(),
                endDate: '$registrationExpiry',
                unit: 'day'
              }
            }
          }
        },
        {
          $addFields: {
            needs_attention: {
              $or: [
                { $lte: ['$days_until_oil_change', 7] },
                { $lte: ['$days_until_insurance', 30] },
                { $lte: ['$days_until_registration', 30] },
                { $eq: ['$status', 'maintenance'] },
                { $eq: ['$status', 'out_of_service'] }
              ]
            }
          }
        },
        {
          $match: { needs_attention: true }
        },
        {
          $project: {
            truckNumber: 1,
            plateNumber: 1,
            model: 1,
            status: 1,
            days_until_oil_change: 1,
            days_until_insurance: 1,
            days_until_registration: 1,
            currentOdometer: 1,
            tires: 1
          }
        },
        {
          $sort: {
            days_until_oil_change: 1,
            days_until_insurance: 1,
            days_until_registration: 1
          }
        }
      ]);

      // Scheduled maintenance
      const scheduledMaintenance = await Maintenance.find({
        status: { $in: ['scheduled', 'in_progress'] },
        isDeleted: false
      })
      .populate('truck', 'truckNumber plateNumber model')
      .populate('performedBy', 'name')
      .sort({ scheduledDate: 1 });

      // Maintenance cost trends
      const maintenanceCosts = await Maintenance.aggregate([
        {
          $match: {
            status: 'completed',
            completedDate: { $exists: true },
            isDeleted: false
          }
        },
        {
          $group: {
            _id: {
              $dateToString: {
                format: '%Y-%m',
                date: '$completedDate'
              }
            },
            total_cost: { $sum: '$actualCost' },
            maintenance_count: { $sum: 1 }
          }
        },
        {
          $sort: { _id: 1 }
        }
      ]);

      // Parts inventory (mock data - in real implementation, would come from inventory model)
      const partsInventory = [
        { name: 'إطارات أمامية', quantity: 45, min_stock: 20, unit_cost: 850 },
        { name: 'إطارات خلفية', quantity: 32, min_stock: 20, unit_cost: 950 },
        { name: 'زيت محرك', quantity: 120, min_stock: 50, unit_cost: 180 },
        { name: 'فلاتر زيت', quantity: 85, min_stock: 40, unit_cost: 45 },
        { name: 'فرامل', quantity: 28, min_stock: 15, unit_cost: 320 }
      ];

      res.json({
        success: true,
        data: {
          fleet_health: fleetHealth[0] || {
            total_trucks: 0,
            available_trucks: 0,
            in_use_trucks: 0,
            maintenance_trucks: 0,
            out_of_service_trucks: 0
          },
          maintenance_alerts: maintenanceAlerts,
          scheduled_maintenance: scheduledMaintenance,
          maintenance_costs: maintenanceCosts,
          parts_inventory: partsInventory
        }
      });

    } catch (error) {
      logger.error('Error getting workshop dashboard:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Schedule maintenance
   * POST /api/v1/admin/workshop/maintenance
   */
  static async scheduleMaintenance(req, res) {
    try {
      const {
        truckId,
        type,
        title,
        description,
        priority,
        scheduledDate,
        estimatedDuration,
        estimatedCost,
        mechanic,
        serviceCenter
      } = req.body;

      const maintenance = new Maintenance({
        truck: truckId,
        type,
        title,
        description,
        priority,
        scheduledDate: new Date(scheduledDate),
        estimatedDuration,
        estimatedCost,
        mechanic,
        serviceCenter,
        performedBy: req.user.id
      });

      await maintenance.save();

      // Update truck status if maintenance is urgent
      if (priority === 'urgent') {
        await Truck.findByIdAndUpdate(truckId, { status: 'maintenance' });
      }

      res.json({
        success: true,
        message: 'Maintenance scheduled successfully',
        data: {
          maintenance_number: maintenance.maintenanceNumber,
          scheduled_date: maintenance.scheduledDate
        }
      });

    } catch (error) {
      logger.error('Error scheduling maintenance:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Complete maintenance
   * PATCH /api/v1/admin/workshop/maintenance/:id/complete
   */
  static async completeMaintenance(req, res) {
    try {
      const { id } = req.params;
      const { actualCost, actualDuration, odometerReading, partsUsed, notes } = req.body;

      const maintenance = await Maintenance.findById(id);
      if (!maintenance) {
        return res.status(404).json({
          success: false,
          message: 'Maintenance record not found'
        });
      }

      maintenance.status = 'completed';
      maintenance.completedDate = new Date();
      maintenance.actualCost = actualCost;
      maintenance.actualDuration = actualDuration;
      maintenance.odometerReading = odometerReading;
      maintenance.partsUsed = partsUsed || [];
      maintenance.notes = notes;

      await maintenance.save();

      // Update truck status and odometer
      await Truck.findByIdAndUpdate(maintenance.truck, {
        status: 'available',
        currentOdometer: odometerReading,
        lastOilChange: maintenance.type === 'oil_change' ? new Date() : undefined,
        lastOilChangeKm: maintenance.type === 'oil_change' ? odometerReading : undefined
      });

      res.json({
        success: true,
        message: 'Maintenance completed successfully'
      });

    } catch (error) {
      logger.error('Error completing maintenance:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Get all users for admin dashboard
   */
  static async getAllUsers(req, res) {
    try {
      const users = await User.find({ is_deleted: false }).select('-password');
      res.json({ success: true, data: users });
    } catch (error) {
      logger.error('Error fetching users:', error);
      res.status(500).json({ success: false, message: 'Internal server error', error: error.message });
    }
  }

  /**
   * Get all orders for admin dashboard
   */
  static async getAllOrders(req, res) {
    try {
      const orders = await Order.find({ is_deleted: false })
        .populate('customer_id', 'name email phone')
        .populate('driver.driver_id', 'name phone')
        .sort({ created_at: -1 });
      res.json({ success: true, data: orders });
    } catch (error) {
      logger.error('Error fetching orders:', error);
      res.status(500).json({ success: false, message: 'Internal server error', error: error.message });
    }
  }

  /**
   * Get all drivers for admin dashboard
   */
  static async getAllDrivers(req, res) {
    try {
      const drivers = await Driver.find({ is_deleted: false })
        .populate('assigned_vehicle.truck_id', 'plate_number model')
        .sort({ created_at: -1 });
      res.json({ success: true, data: drivers });
    } catch (error) {
      logger.error('Error fetching drivers:', error);
      res.status(500).json({ success: false, message: 'Internal server error', error: error.message });
    }
  }

  /**
   * Get all trucks for admin dashboard
   */
  static async getAllTrucks(req, res) {
    try {
      const trucks = await Truck.find({ is_deleted: false }).sort({ created_at: -1 });
      res.json({ success: true, data: trucks });
    } catch (error) {
      logger.error('Error fetching trucks:', error);
      res.status(500).json({ success: false, message: 'Internal server error', error: error.message });
    }
  }

  /**
   * Get all maintenance records for admin dashboard
   */
  static async getAllMaintenance(req, res) {
    try {
      const maintenances = await Maintenance.find({ is_deleted: false })
        .populate('truck', 'plateNumber model')
        .sort({ scheduledDate: -1 });
      res.json({ success: true, data: maintenances });
    } catch (error) {
      logger.error('Error fetching maintenance records:', error);
      res.status(500).json({ success: false, message: 'Internal server error', error: error.message });
    }
  }

  /**
   * Send notification (placeholder implementation)
   */
  static async sendNotification(req, res) {
    try {
      logger.info('Admin sendNotification request', { body: req.body, user: req.user.id });
      res.json({ success: true, message: 'Notification request received. Notification service implementation is pending.' });
    } catch (error) {
      logger.error('Error sending notification:', error);
      res.status(500).json({ success: false, message: 'Internal server error', error: error.message });
    }
  }

  /**
   * Get system statistics
   * GET /api/v1/admin/system/stats
   */
  static async getSystemStats(req, res) {
    try {
      const [
        userStats,
        orderStats,
        driverStats,
        truckStats,
        revenueStats
      ] = await Promise.all([
        // User statistics
        User.aggregate([
          {
            $group: {
              _id: '$type',
              count: { $sum: 1 }
            }
          }
        ]),
        
        // Order statistics
        Order.aggregate([
          {
            $group: {
              _id: '$status',
              count: { $sum: 1 }
            }
          }
        ]),
        
        // Driver statistics
        Driver.aggregate([
          {
            $group: {
              _id: '$status',
              count: { $sum: 1 }
            }
          }
        ]),
        
        // Truck statistics
        Truck.aggregate([
          {
            $group: {
              _id: '$status',
              count: { $sum: 1 }
            }
          }
        ]),
        
        // Revenue statistics (last 30 days)
        Order.aggregate([
          {
            $match: {
              created_at: { $gte: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000) },
              'invoice.payment_status': 'PAID'
            }
          },
          {
            $group: {
              _id: null,
              total_revenue: { $sum: '$invoice.total_amount' },
              order_count: { $sum: 1 }
            }
          }
        ])
      ]);

      res.json({
        success: true,
        data: {
          user_stats: userStats,
          order_stats: orderStats,
          driver_stats: driverStats,
          truck_stats: truckStats,
          revenue_stats: revenueStats[0] || { total_revenue: 0, order_count: 0 }
        }
      });

    } catch (error) {
      logger.error('Error getting system stats:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }
}

module.exports = AdminController;
