/**
 * ============================================
 * 📱 Edham Logistics - Mobile API Controller
 * نظام إدهام - متحكم تطبيقات الموبايل
 * ============================================
 */

const Order = require('../models/Order');
const Driver = require('../models/Driver');
const User = require('../models/User');
const Payment = require('../models/Payment');
const logger = require('../utils/logger');
const multer = require('multer');
const path = require('path');
const fs = require('fs');

// Configure multer for file uploads
const storage = multer.memoryStorage();
const upload = multer({
  storage,
  limits: {
    fileSize: 10 * 1024 * 1024, // 10MB max file size
    files: 5 // Max 5 files per request
  },
  fileFilter: (req, file, cb) => {
    const allowedTypes = ['image/jpeg', 'image/png', 'image/jpg', 'application/pdf'];
    if (allowedTypes.includes(file.mimetype)) {
      cb(null, true);
    } else {
      cb(new Error('Invalid file type. Only JPEG, PNG, and PDF files are allowed.'), false);
    }
  }
});

class MobileController {
  /**
   * Mobile Authentication
   * POST /api/v1/mobile/auth/login
   */
  static async mobileLogin(req, res) {
    try {
      const { email, password, deviceInfo } = req.body;

      // Find user with role-specific data
      const user = await User.findOne({ email, isActive: true })
        .populate('customer_details')
        .populate('driver_details');

      if (!user || !(await user.comparePassword(password))) {
        return res.status(401).json({
          success: false,
          error: {
            code: 'AUTHENTICATION_FAILED',
            message: 'Invalid email or password'
          }
        });
      }

      // Check if account is locked
      if (user.isLocked()) {
        return res.status(423).json({
          success: false,
          error: {
            code: 'ACCOUNT_LOCKED',
            message: 'Account is temporarily locked due to failed login attempts'
          }
        });
      }

      // Update last login and device info
      user.lastLogin = new Date();
      if (deviceInfo) {
        user.deviceInfo = deviceInfo;
      }
      await user.save();

      // Reset login attempts
      await user.resetLoginAttempts();

      // Generate JWT token with role and permissions
      const token = user.generateAuthToken();

      // Get role-specific permissions
      const permissions = MobileController.getRolePermissions(user.role);

      // Get role configuration for mobile UI
      const roleConfig = MobileController.getRoleConfig(user.role);

      res.json({
        success: true,
        data: {
          user: {
            id: user._id,
            name: user.name,
            email: user.email,
            role: user.role,
            phone: user.phone,
            avatar: user.avatar,
            department: user.department
          },
          token,
          permissions,
          roleConfig,
          expiresIn: process.env.JWT_EXPIRES_IN || '24h'
        }
      });

    } catch (error) {
      logger.error('Mobile login error:', error);
      res.status(500).json({
        success: false,
        error: {
          code: 'INTERNAL_ERROR',
          message: 'Internal server error'
        }
      });
    }
  }

  /**
   * Get role permissions
   */
  static getRolePermissions(role) {
    const permissions = {
      'CUSTOMER': ['create_orders', 'view_own_orders', 'track_orders', 'make_payments', 'rate_drivers'],
      'DRIVER': ['view_assigned_tasks', 'update_location', 'update_task_status', 'upload_proof_of_delivery', 'view_earnings'],
      'SUPERVISOR': ['view_all_orders', 'assign_drivers', 'monitor_fleet', 'view_analytics', 'manage_drivers'],
      'ACCOUNTANT': ['view_invoices', 'manage_payments', 'view_financial_reports', 'export_data'],
      'WORKSHOP': ['view_maintenance', 'schedule_maintenance', 'manage_parts', 'view_fleet_health'],
      'ADMIN': ['all_permissions']
    };

    return permissions[role] || [];
  }

  /**
   * Get role configuration for mobile UI
   */
  static getRoleConfig(role) {
    const configs = {
      'CUSTOMER': {
        primaryColor: '#3b82f6',
        navigation: [
          { id: 'dashboard', label: 'الرئيسية', icon: 'home' },
          { id: 'booking', label: 'طلب حمولة', icon: 'package' },
          { id: 'tracking', label: 'التتبع', icon: 'map-pin' },
          { id: 'history', label: 'السجل', icon: 'clock' },
          { id: 'profile', label: 'الملف الشخصي', icon: 'user' }
        ]
      },
      'DRIVER': {
        primaryColor: '#10b981',
        navigation: [
          { id: 'dashboard', label: 'لوحة التحكم', icon: 'home' },
          { id: 'tasks', label: 'المهام', icon: 'package' },
          { id: 'location', label: 'الموقع', icon: 'navigation' },
          { id: 'earnings', label: 'الأرباح', icon: 'dollar-sign' },
          { id: 'profile', label: 'الملف الشخصي', icon: 'user' }
        ]
      },
      'SUPERVISOR': {
        primaryColor: '#8b5cf6',
        navigation: [
          { id: 'dashboard', label: 'غرفة العمليات', icon: 'home' },
          { id: 'fleet', label: 'الأسطول', icon: 'truck' },
          { id: 'dispatch', label: 'توزيع الطلبات', icon: 'package' },
          { id: 'drivers', label: 'السائقين', icon: 'users' },
          { id: 'analytics', label: 'التحليلات', icon: 'bar-chart' }
        ]
      },
      'ACCOUNTANT': {
        primaryColor: '#f59e0b',
        navigation: [
          { id: 'dashboard', label: 'لوحة المالية', icon: 'home' },
          { id: 'invoices', label: 'الفواتير', icon: 'file-text' },
          { id: 'payments', label: 'المدفوعات', icon: 'credit-card' },
          { id: 'reports', label: 'التقارير', icon: 'bar-chart' },
          { id: 'tax', label: 'الضرائب', icon: 'file-text' }
        ]
      },
      'WORKSHOP': {
        primaryColor: '#ef4444',
        navigation: [
          { id: 'dashboard', label: 'لوحة الصيانة', icon: 'home' },
          { id: 'fleet', label: 'حالة الأسطول', icon: 'truck' },
          { id: 'maintenance', label: 'الصيانة', icon: 'wrench' },
          { id: 'parts', label: 'القطع', icon: 'package' },
          { id: 'alerts', label: 'التنبيهات', icon: 'alert-circle' }
        ]
      },
      'ADMIN': {
        primaryColor: '#dc2626',
        navigation: [
          { id: 'dashboard', label: 'لوحة التحكم', icon: 'home' },
          { id: 'supervisor', label: 'المشرفين', icon: 'shield' },
          { id: 'accountant', label: 'المحاسبة', icon: 'dollar-sign' },
          { id: 'workshop', label: 'الورشة', icon: 'wrench' },
          { id: 'settings', label: 'الإعدادات', icon: 'settings' }
        ]
      }
    };

    return configs[role] || configs['CUSTOMER'];
  }

  /**
   * Customer: Create Order
   * POST /api/v1/mobile/customer/orders
   */
  static async createMobileOrder(req, res) {
    try {
      const { route, vehicle, cargo, scheduling } = req.body;
      const userId = req.user.id;

      // Create order with mobile-specific fields
      const order = new Order({
        customer_id: userId,
        route: {
          pickup: {
            address: route.pickup.address,
            location: {
              type: 'Point',
              coordinates: route.pickup.coordinates
            },
            contact: route.pickup.contact
          },
          dropoff: {
            address: route.dropoff.address,
            location: {
              type: 'Point',
              coordinates: route.dropoff.coordinates
            },
            contact: route.dropoff.contact
          },
          distance_km: MobileController.calculateDistance(
            route.pickup.coordinates,
            route.dropoff.coordinates
          )
        },
        vehicle: {
          type: vehicle.type,
          capacity: vehicle.capacity,
          preferredSize: vehicle.preferredSize
        },
        cargo: {
          type: cargo.type,
          weight: cargo.weight,
          description: cargo.description,
          specialRequirements: cargo.specialRequirements || []
        },
        scheduling: {
          pickupTime: new Date(scheduling.pickupTime),
          deliveryTime: new Date(scheduling.deliveryTime),
          flexible: scheduling.flexible || false
        },
        status: 'CONFIRMED',
        source: 'mobile_app'
      });

      // Calculate pricing
      const pricing = await MobileController.calculateOrderPricing(order);
      order.invoice = pricing;

      await order.save();

      // Broadcast to available drivers
      const socketService = require('../services/socketService');
      socketService.broadcastToRole('DRIVER', 'new_order', {
        order_id: order._id,
        order_number: order.order_number,
        route: order.route,
        vehicle: order.vehicle,
        cargo: order.cargo,
        pricing: order.invoice
      });

      res.status(201).json({
        success: true,
        data: {
          order: {
            id: order._id,
            order_number: order.order_number,
            status: order.status,
            route: order.route,
            vehicle: order.vehicle,
            cargo: order.cargo,
            scheduling: order.scheduling,
            invoice: order.invoice,
            created_at: order.created_at
          }
        }
      });

    } catch (error) {
      logger.error('Create mobile order error:', error);
      res.status(500).json({
        success: false,
        error: {
          code: 'ORDER_CREATION_FAILED',
          message: 'Failed to create order'
        }
      });
    }
  }

  /**
   * Customer: Get Orders
   * GET /api/v1/mobile/customer/orders
   */
  static async getCustomerOrders(req, res) {
    try {
      const { status, page = 1, limit = 10 } = req.query;
      const userId = req.user.id;

      // Build filter
      const filter = { customer_id: userId };
      if (status) {
        filter.status = status;
      }

      // Get orders with pagination
      const orders = await Order.find(filter)
        .populate('driver.driver_id', 'name phone')
        .sort({ created_at: -1 })
        .limit(limit * 1)
        .skip((page - 1) * limit);

      const total = await Order.countDocuments(filter);

      res.json({
        success: true,
        data: {
          orders: orders.map(order => ({
            id: order._id,
            order_number: order.order_number,
            status: order.status,
            route: order.route,
            vehicle: order.vehicle,
            cargo: order.cargo,
            scheduling: order.scheduling,
            invoice: order.invoice,
            driver: order.driver,
            created_at: order.created_at,
            updated_at: order.updated_at
          })),
          pagination: {
            current: parseInt(page),
            total: Math.ceil(total / limit),
            count: orders.length,
            totalCount: total
          }
        }
      });

    } catch (error) {
      logger.error('Get customer orders error:', error);
      res.status(500).json({
        success: false,
        error: {
          code: 'FETCH_ORDERS_FAILED',
          message: 'Failed to fetch orders'
        }
      });
    }
  }

  /**
   * Customer: Track Order
   * GET /api/v1/mobile/customer/orders/:orderId/tracking
   */
  static async trackOrder(req, res) {
    try {
      const { orderId } = req.params;
      const userId = req.user.id;

      const order = await Order.findOne({
        _id: orderId,
        customer_id: userId
      })
      .populate('driver.driver_id', 'name phone current_location')
      .populate('tracking.events');

      if (!order) {
        return res.status(404).json({
          success: false,
          error: {
            code: 'ORDER_NOT_FOUND',
            message: 'Order not found'
          }
        });
      }

      // Get driver location from Redis cache
      const redis = require('../config/redis');
      const driverLocation = await redis.get(`driver_location:${order.driver?.driver_id}`);

      res.json({
        success: true,
        data: {
          order: {
            id: order._id,
            order_number: order.order_number,
            status: order.status,
            route: order.route,
            driver: order.driver,
            tracking: order.tracking,
            estimated_completion: order.scheduling?.deliveryTime,
            current_location: driverLocation ? JSON.parse(driverLocation) : null
          }
        }
      });

    } catch (error) {
      logger.error('Track order error:', error);
      res.status(500).json({
        success: false,
        error: {
          code: 'TRACKING_FAILED',
          message: 'Failed to track order'
        }
      });
    }
  }

  /**
   * Driver: Get Active Task
   * GET /api/v1/mobile/driver/active-task
   */
  static async getActiveTask(req, res) {
    try {
      const driverId = req.user.id;

      const driver = await Driver.findById(driverId)
        .populate('current_task.order_id')
        .populate('assigned_vehicle.truck_id');

      if (!driver || !driver.current_task || driver.current_task.task_state === 'IDLE') {
        return res.json({
          success: true,
          data: {
            activeTask: null,
            message: 'No active task'
          }
        });
      }

      const order = driver.current_task.order_id;

      res.json({
        success: true,
        data: {
          activeTask: {
            id: order._id,
            order_number: order.order_number,
            status: order.status,
            route: order.route,
            cargo: order.cargo,
            scheduling: order.scheduling,
            customer: order.customer_id,
            task_state: driver.current_task.task_state,
            estimated_completion: driver.current_task.estimated_completion,
            vehicle: driver.assigned_vehicle,
            progress: MobileController.calculateTaskProgress(driver.current_task)
          }
        }
      });

    } catch (error) {
      logger.error('Get active task error:', error);
      res.status(500).json({
        success: false,
        error: {
          code: 'FETCH_TASK_FAILED',
          message: 'Failed to fetch active task'
        }
      });
    }
  }

  /**
   * Driver: Update Location
   * PUT /api/v1/mobile/driver/location
   */
  static async updateDriverLocation(req, res) {
    try {
      const { coordinates, accuracy, speed, heading, timestamp } = req.body;
      const driverId = req.user.id;

      // Update driver location in database
      const driver = await Driver.findByIdAndUpdate(
        driverId,
        {
          current_location: {
            type: 'Point',
            coordinates: coordinates,
            accuracy,
            updated_at: new Date(timestamp)
          }
        },
        { new: true }
      );

      // Cache location in Redis for real-time tracking
      const redis = require('../config/redis');
      await redis.setex(
        `driver_location:${driverId}`,
        300, // 5 minutes TTL
        JSON.stringify({
          coordinates,
          accuracy,
          speed,
          heading,
          timestamp
        })
      );

      // Broadcast location update to tracking customers
      const socketService = require('../services/socketService');
      if (driver.current_task?.order_id) {
        socketService.broadcastToOrder(driver.current_task.order_id, 'location_update', {
          driver_id: driverId,
          coordinates,
          speed,
          heading,
          timestamp
        });
      }

      res.json({
        success: true,
        data: {
          location: {
            coordinates,
            accuracy,
            speed,
            heading,
            timestamp
          }
        }
      });

    } catch (error) {
      logger.error('Update driver location error:', error);
      res.status(500).json({
        success: false,
        error: {
          code: 'LOCATION_UPDATE_FAILED',
          message: 'Failed to update location'
        }
      });
    }
  }

  /**
   * Driver: Update Task Status
   * PATCH /api/v1/mobile/driver/orders/:orderId/status
   */
  static async updateTaskStatus(req, res) {
    try {
      const { orderId } = req.params;
      const { status, notes, estimatedArrival } = req.body;
      const driverId = req.user.id;

      // Validate status transition
      const validTransitions = {
        'TASK_ASSIGNED': ['TASK_ACCEPTED', 'TASK_REJECTED'],
        'TASK_ACCEPTED': ['HEADING_TO_PICKUP'],
        'HEADING_TO_PICKUP': ['ARRIVED_AT_PICKUP'],
        'ARRIVED_AT_PICKUP': ['PICKUP_CONFIRMED'],
        'PICKUP_CONFIRMED': ['HEADING_TO_DROPOFF'],
        'HEADING_TO_DROPOFF': ['ARRIVED_AT_DROPOFF'],
        'ARRIVED_AT_DROPOFF': ['DELIVERY_CONFIRMED'],
        'DELIVERY_CONFIRMED': ['TASK_COMPLETED']
      };

      const driver = await Driver.findById(driverId);
      const currentStatus = driver.current_task?.task_state;

      if (currentStatus && !validTransitions[currentStatus]?.includes(status)) {
        return res.status(400).json({
          success: false,
          error: {
            code: 'INVALID_STATUS_TRANSITION',
            message: `Cannot transition from ${currentStatus} to ${status}`
          }
        });
      }

      // Update driver task state
      await driver.updateTaskState(status, estimatedArrival);

      // Update order status
      const orderStatusMap = {
        'TASK_ACCEPTED': 'ASSIGNED',
        'HEADING_TO_PICKUP': 'IN_TRANSIT',
        'ARRIVED_AT_PICKUP': 'AT_PICKUP',
        'PICKUP_CONFIRMED': 'PICKED_UP',
        'HEADING_TO_DROPOFF': 'IN_TRANSIT',
        'ARRIVED_AT_DROPOFF': 'AT_DROPOFF',
        'DELIVERY_CONFIRMED': 'DELIVERED',
        'TASK_COMPLETED': 'COMPLETED'
      };

      const order = await Order.findByIdAndUpdate(
        orderId,
        {
          status: orderStatusMap[status] || status,
          'tracking.$.status': status,
          'tracking.$.timestamp': new Date(),
          'tracking.$.notes': notes,
          'tracking.$.location': driver.current_location
        },
        { new: true }
      );

      // Broadcast status update
      const socketService = require('../services/socketService');
      socketService.broadcastToOrder(orderId, 'order_status_changed', {
        order_id: orderId,
        status: orderStatusMap[status] || status,
        driver_id: driverId,
        timestamp: new Date(),
        notes
      });

      res.json({
        success: true,
        data: {
          order: {
            id: order._id,
            status: order.status,
            tracking: order.tracking
          },
          task: {
            state: status,
            estimated_completion: estimatedArrival
          }
        }
      });

    } catch (error) {
      logger.error('Update task status error:', error);
      res.status(500).json({
        success: false,
        error: {
          code: 'STATUS_UPDATE_FAILED',
          message: 'Failed to update task status'
        }
      });
    }
  }

  /**
   * Driver: Upload Proof of Delivery
   * POST /api/v1/mobile/driver/orders/:orderId/proof
   */
  static async uploadProofOfDelivery(req, res) {
    try {
      const { orderId } = req.params;
      const { notes, recipientName, signature } = req.body;
      const driverId = req.user.id;
      const files = req.files;

      if (!files || files.length === 0) {
        return res.status(400).json({
          success: false,
          error: {
            code: 'NO_FILES_UPLOADED',
            message: 'At least one file must be uploaded'
          }
        });
      }

      // Save files (in production, use cloud storage)
      const uploadedFiles = [];
      for (const file of files) {
        const fileName = `proof_${orderId}_${Date.now()}_${file.originalname}`;
        const filePath = path.join('uploads', 'proof', fileName);
        
        // Ensure directory exists
        const dir = path.dirname(filePath);
        if (!fs.existsSync(dir)) {
          fs.mkdirSync(dir, { recursive: true });
        }
        
        fs.writeFileSync(filePath, file.buffer);
        uploadedFiles.push({
          name: file.originalname,
          path: filePath,
          size: file.size,
          type: file.mimetype,
          url: `/uploads/proof/${fileName}`
        });
      }

      // Update order with proof of delivery
      const order = await Order.findByIdAndUpdate(
        orderId,
        {
          'proof_of_delivery': {
            uploaded_at: new Date(),
            uploaded_by: driverId,
            files: uploadedFiles,
            notes: notes,
            recipient_name: recipientName,
            signature: signature
          },
          status: 'COMPLETED'
        },
        { new: true }
      );

      // Complete driver task
      const driver = await Driver.findById(driverId);
      await driver.updateTaskState('TASK_COMPLETED');

      // Broadcast completion
      const socketService = require('../services/socketService');
      socketService.broadcastToOrder(orderId, 'order_completed', {
        order_id: orderId,
        completed_at: new Date(),
        proof_of_delivery: order.proof_of_delivery
      });

      res.json({
        success: true,
        data: {
          order: {
            id: order._id,
            status: order.status,
            proof_of_delivery: order.proof_of_delivery
          }
        }
      });

    } catch (error) {
      logger.error('Upload proof of delivery error:', error);
      res.status(500).json({
        success: false,
        error: {
          code: 'UPLOAD_FAILED',
          message: 'Failed to upload proof of delivery'
        }
      });
    }
  }

  /**
   * Sync Offline Data
   * POST /api/v1/mobile/sync/offline
   */
  static async syncOfflineData(req, res) {
    try {
      const { data } = req.body;
      const userId = req.user.id;
      const results = [];

      for (const item of data) {
        try {
          switch (item.type) {
            case 'location_update':
              await MobileController.syncLocationUpdate(userId, item.data);
              results.push({ type: item.type, status: 'success' });
              break;

            case 'task_status_update':
              await MobileController.syncTaskStatusUpdate(userId, item.data);
              results.push({ type: item.type, status: 'success' });
              break;

            default:
              results.push({ type: item.type, status: 'skipped', reason: 'Unknown type' });
          }
        } catch (error) {
          results.push({ type: item.type, status: 'failed', error: error.message });
        }
      }

      res.json({
        success: true,
        data: {
          synced: results.filter(r => r.status === 'success').length,
          failed: results.filter(r => r.status === 'failed').length,
          results
        }
      });

    } catch (error) {
      logger.error('Sync offline data error:', error);
      res.status(500).json({
        success: false,
        error: {
          code: 'SYNC_FAILED',
          message: 'Failed to sync offline data'
        }
      });
    }
  }

  /**
   * Helper: Calculate distance between two points
   */
  static calculateDistance(coord1, coord2) {
    // Haversine formula
    const R = 6371; // Earth's radius in kilometers
    const dLat = (coord2[1] - coord1[1]) * Math.PI / 180;
    const dLon = (coord2[0] - coord1[0]) * Math.PI / 180;
    const a = 
      Math.sin(dLat/2) * Math.sin(dLat/2) +
      Math.cos(coord1[1] * Math.PI / 180) * Math.cos(coord2[1] * Math.PI / 180) *
      Math.sin(dLon/2) * Math.sin(dLon/2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    return R * c;
  }

  /**
   * Helper: Calculate order pricing
   */
  static async calculateOrderPricing(order) {
    const basePrice = 100; // Base price in SAR
    const distanceKm = order.route.distance_km;
    const weightMultiplier = order.cargo.weight / 1000; // Price per ton
    const vehicleMultiplier = order.vehicle.type === 'refrigerated' ? 1.5 : 1.0;

    const subtotal = basePrice + (distanceKm * 2) + (weightMultiplier * 50) * vehicleMultiplier;
    const taxAmount = subtotal * 0.15; // 15% tax
    const totalAmount = subtotal + taxAmount;

    return {
      subtotal: Math.round(subtotal * 100) / 100,
      tax_amount: Math.round(taxAmount * 100) / 100,
      total_amount: Math.round(totalAmount * 100) / 100,
      currency: 'SAR',
      payment_status: 'PENDING',
      payment_method: 'CREDIT_CARD',
      items: [{
        description: `Transportation - ${order.vehicle.type}`,
        quantity: 1,
        unit_price: basePrice,
        total_price: basePrice
      }, {
        description: `Distance (${distanceKm} km)`,
        quantity: distanceKm,
        unit_price: 2,
        total_price: distanceKm * 2
      }, {
        description: `Weight (${order.cargo.weight} kg)`,
        quantity: order.cargo.weight / 1000,
        unit_price: 50,
        total_price: weightMultiplier * 50
      }]
    };
  }

  /**
   * Helper: Calculate task progress
   */
  static calculateTaskProgress(task) {
    const states = ['IDLE', 'TASK_ASSIGNED', 'TASK_ACCEPTED', 'HEADING_TO_PICKUP', 
                     'ARRIVED_AT_PICKUP', 'PICKUP_CONFIRMED', 'HEADING_TO_DROPOFF', 
                     'ARRIVED_AT_DROPOFF', 'DELIVERY_CONFIRMED', 'TASK_COMPLETED'];
    
    const currentIndex = states.indexOf(task.task_state);
    return Math.round((currentIndex / (states.length - 1)) * 100);
  }

  /**
   * Helper: Sync location update
   */
  static async syncLocationUpdate(userId, data) {
    await Driver.findByIdAndUpdate(userId, {
      current_location: {
        type: 'Point',
        coordinates: data.coordinates,
        accuracy: data.accuracy,
        updated_at: new Date(data.timestamp)
      }
    });

    // Cache in Redis
    const redis = require('../config/redis');
    await redis.setex(
      `driver_location:${userId}`,
      300,
      JSON.stringify(data)
    );
  }

  /**
   * Helper: Sync task status update
   */
  static async syncTaskStatusUpdate(userId, data) {
    const driver = await Driver.findById(userId);
    await driver.updateTaskState(data.status, data.estimatedArrival);

    // Update order
    const orderStatusMap = {
      'TASK_ACCEPTED': 'ASSIGNED',
      'HEADING_TO_PICKUP': 'IN_TRANSIT',
      'ARRIVED_AT_PICKUP': 'AT_PICKUP',
      'PICKUP_CONFIRMED': 'PICKED_UP',
      'HEADING_TO_DROPOFF': 'IN_TRANSIT',
      'ARRIVED_AT_DROPOFF': 'AT_DROPOFF',
      'DELIVERY_CONFIRMED': 'DELIVERED',
      'TASK_COMPLETED': 'COMPLETED'
    };

    await Order.findByIdAndUpdate(
      data.orderId,
      {
        status: orderStatusMap[data.status] || data.status,
        'tracking.$.status': data.status,
        'tracking.$.timestamp': new Date(data.timestamp)
      }
    );
  }
}

module.exports = MobileController;
