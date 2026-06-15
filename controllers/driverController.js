/**
 * ============================================
 * 🚛 Edham Logistics - Driver Controller
 * نظام إدهام - متحكم السائقين
 * ============================================
 */

const Driver = require('../models/Driver');
const Order = require('../models/Order');
const Truck = require('../models/Truck');
const User = require('../models/User');
const logger = require('../utils/logger');
const multer = require('multer');
const path = require('path');

// Configure multer for file uploads
const storage = multer.memoryStorage();
const upload = multer({
  storage: storage,
  limits: {
    fileSize: 10 * 1024 * 1024, // 10MB limit
    files: 5 // Maximum 5 files
  },
  fileFilter: (req, file, cb) => {
    const allowedTypes = /jpeg|jpg|png|gif|pdf|doc|docx/;
    const extname = allowedTypes.test(path.extname(file.originalname).toLowerCase());
    const mimetype = allowedTypes.test(file.mimetype);
    
    if (mimetype && extname) {
      return cb(null, true);
    } else {
      cb(new Error('Invalid file type. Only images and documents are allowed.'));
    }
  }
});

class DriverController {
  /**
   * Get driver profile and current status
   * GET /api/v1/drivers/profile
   */
  static async getDriverProfile(req, res) {
    try {
      const driverId = req.user.id;
      
      const driver = await Driver.findOne({ user_id: driverId })
        .populate('assigned_vehicle.truck_id')
        .populate('current_task.order_id');
      
      if (!driver) {
        return res.status(404).json({
          success: false,
          message: 'Driver profile not found'
        });
      }
      
      res.json({
        success: true,
        data: {
          driver: {
            id: driver._id,
            status: driver.status,
            is_available: driver.is_available,
            current_location: driver.current_location,
            license_info: driver.license_info,
            assigned_vehicle: driver.assigned_vehicle.truck_id ? {
              id: driver.assigned_vehicle.truck_id._id,
              plate_number: driver.assigned_vehicle.truck_id.plate_number,
              model: driver.assigned_vehicle.truck_id.model,
              year: driver.assigned_vehicle.truck_id.year,
              capacity: driver.assigned_vehicle.truck_id.capacity
            } : null,
            current_task: driver.current_task.order_id ? {
              order_id: driver.current_task.order_id._id,
              order_number: driver.current_task.order_id.order_number,
              task_state: driver.current_task.task_state,
              task_started_at: driver.current_task.task_started_at,
              estimated_completion: driver.current_task.estimated_completion
            } : null,
            performance: driver.performance,
            work_schedule: driver.work_schedule
          }
        }
      });
      
    } catch (error) {
      logger.error('Error getting driver profile:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Update driver availability status
   * PUT /api/v1/drivers/status
   */
  static async updateDriverStatus(req, res) {
    try {
      const { is_available, status } = req.body;
      const driverId = req.user.id;
      
      const driver = await Driver.findOne({ user_id: driverId });
      
      if (!driver) {
        return res.status(404).json({
          success: false,
          message: 'Driver not found'
        });
      }
      
      await driver.setAvailability(is_available, status);
      
      // Broadcast status change to relevant parties
      const socketService = require('../services/socketService');
      socketService.broadcastToOrderRoom(
        driver.current_task?.order_id,
        'driver_status_update',
        {
          driver_id: driver._id,
          is_available: driver.is_available,
          status: driver.status,
          timestamp: new Date()
        }
      );
      
      res.json({
        success: true,
        message: 'Driver status updated successfully',
        data: {
          status: driver.status,
          is_available: driver.is_available
        }
      });
      
    } catch (error) {
      logger.error('Error updating driver status:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Get current active task
   * GET /api/v1/drivers/active-task
   */
  static async getActiveTask(req, res) {
    try {
      const driverId = req.user.id;
      
      const driver = await Driver.findOne({ user_id: driverId })
        .populate({
          path: 'current_task.order_id',
          populate: [
            {
              path: 'customer_id',
              select: 'name phone email'
            },
            {
              path: 'route.pickup.contact_person',
              select: 'name phone'
            },
            {
              path: 'route.dropoff.contact_person',
              select: 'name phone'
            }
          ]
        });
      
      if (!driver || !driver.current_task.order_id) {
        return res.json({
          success: true,
          data: {
            has_active_task: false,
            message: 'No active task'
          }
        });
      }
      
      const order = driver.current_task.order_id;
      
      res.json({
        success: true,
        data: {
          has_active_task: true,
          task: {
            order_id: order._id,
            order_number: order.order_number,
            task_state: driver.current_task.task_state,
            task_started_at: driver.current_task.task_started_at,
            estimated_completion: driver.current_task.estimated_completion,
            customer: order.customer_id,
            route: order.route,
            scheduling: order.scheduling,
            cargo: order.cargo,
            services: order.services,
            invoice: {
              total_amount: order.invoice.total_amount,
              payment_status: order.invoice.payment_status
            }
          }
        }
      });
      
    } catch (error) {
      logger.error('Error getting active task:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Accept or reject assigned task
   * POST /api/v1/drivers/task-response
   */
  static async respondToTask(req, res) {
    try {
      const { order_id, response, reason } = req.body;
      const driverId = req.user.id;
      
      if (!['accept', 'reject'].includes(response)) {
        return res.status(400).json({
          success: false,
          message: 'Invalid response. Must be "accept" or "reject"'
        });
      }
      
      const driver = await Driver.findOne({ user_id: driverId });
      
      if (!driver) {
        return res.status(404).json({
          success: false,
          message: 'Driver not found'
        });
      }
      
      if (driver.current_task.task_state !== 'TASK_ASSIGNED') {
        return res.status(400).json({
          success: false,
          message: 'No pending task to respond to'
        });
      }
      
      const order = await Order.findById(driver.current_task.order_id);
      
      if (!order) {
        return res.status(404).json({
          success: false,
          message: 'Order not found'
        });
      }
      
      if (response === 'accept') {
        await driver.acceptTask();
        order.status = 'ASSIGNED';
        await driver.assignDriver(driver._id, driver.assigned_vehicle.truck_id);
      } else {
        await driver.rejectTask(reason);
        order.status = 'SEARCHING_FOR_DRIVER';
        order.driver.driver_id = null;
        order.driver.truck_id = null;
      }
      
      await order.save();
      
      // Broadcast to customer and admin
      const socketService = require('../services/socketService');
      socketService.broadcastToOrderRoom(order._id, 'task_response', {
        order_id: order._id,
        driver_response: response,
        driver_id: driver._id,
        driver_name: req.user.name,
        reason: reason,
        timestamp: new Date()
      });
      
      res.json({
        success: true,
        message: `Task ${response}ed successfully`,
        data: {
          task_state: driver.current_task.task_state,
          order_status: order.status
        }
      });
      
    } catch (error) {
      logger.error('Error responding to task:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Update task state (state machine transitions)
   * PATCH /api/v1/orders/:id/status
   */
  static async updateOrderStatus(req, res) {
    try {
      const { id: orderId } = req.params;
      const { new_status, notes, location } = req.body;
      const driverId = req.user.id;
      
      const driver = await Driver.findOne({ user_id: driverId });
      
      if (!driver) {
        return res.status(404).json({
          success: false,
          message: 'Driver not found'
        });
      }
      
      if (driver.current_task.order_id?.toString() !== orderId) {
        return res.status(403).json({
          success: false,
          message: 'This order is not assigned to you'
        });
      }
      
      // Map frontend status to driver task state
      const statusToStateMap = {
        'HEADING_TO_PICKUP': 'HEADING_TO_PICKUP',
        'ARRIVED_AT_PICKUP': 'ARRIVED_AT_PICKUP',
        'PICKUP_CONFIRMED': 'PICKUP_CONFIRMED',
        'LOADING_COMPLETE': 'LOADING_COMPLETE',
        'HEADING_TO_DROPOFF': 'HEADING_TO_DROPOFF',
        'ARRIVED_AT_DROPOFF': 'ARRIVED_AT_DROPOFF',
        'UNLOADING_STARTED': 'UNLOADING_STARTED',
        'DELIVERY_CONFIRMED': 'DELIVERY_CONFIRMED'
      };
      
      const newTaskState = statusToStateMap[new_status];
      
      if (!newTaskState) {
        return res.status(400).json({
          success: false,
          message: 'Invalid status transition'
        });
      }
      
      await driver.updateTaskState(newTaskState, notes);
      
      // Update order status
      const order = await Order.findById(orderId);
      if (order) {
        // Map task state to order status
        const stateToOrderStatusMap = {
          'HEADING_TO_PICKUP': 'ASSIGNED',
          'ARRIVED_AT_PICKUP': 'ASSIGNED',
          'PICKUP_CONFIRMED': 'PICKUP_CONFIRMED',
          'LOADING_COMPLETE': 'PICKUP_CONFIRMED',
          'HEADING_TO_DROPOFF': 'IN_TRANSIT',
          'ARRIVED_AT_DROPOFF': 'IN_TRANSIT',
          'UNLOADING_STARTED': 'IN_TRANSIT',
          'DELIVERY_CONFIRMED': 'DELIVERED'
        };
        
        order.status = stateToOrderStatusMap[newTaskState] || order.status;
        
        // Add tracking event
        await order.addTrackingEvent(newTaskState, notes || `Status updated to ${newTaskState}`, location);
        
        await order.save();
        
        // Broadcast status update
        const socketService = require('../services/socketService');
        socketService.broadcastToOrderRoom(orderId, 'order_status_update', {
          order_id: orderId,
          status: order.status,
          task_state: newTaskState,
          timestamp: new Date(),
          notes
        });
      }
      
      res.json({
        success: true,
        message: 'Order status updated successfully',
        data: {
          task_state: driver.current_task.task_state,
          order_status: order?.status
        }
      });
      
    } catch (error) {
      logger.error('Error updating order status:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Upload proof of delivery documents
   * POST /api/v1/orders/:id/proof-of-delivery
   */
  static async uploadProofOfDelivery(req, res) {
    try {
      const { id: orderId } = req.params;
      const driverId = req.user.id;
      
      const driver = await Driver.findOne({ user_id: driverId });
      
      if (!driver) {
        return res.status(404).json({
          success: false,
          message: 'Driver not found'
        });
      }
      
      if (driver.current_task.order_id?.toString() !== orderId) {
        return res.status(403).json({
          success: false,
          message: 'This order is not assigned to you'
        });
      }
      
      if (!req.files || req.files.length === 0) {
        return res.status(400).json({
          success: false,
          message: 'No files uploaded'
        });
      }
      
      // In a real implementation, upload files to object storage (S3, MinIO, etc.)
      // For now, we'll simulate the upload and return mock URLs
      const uploadedFiles = [];
      
      for (const file of req.files) {
        // Simulate file upload to object storage
        const fileUrl = `https://storage.edham.com/proof-of-delivery/${orderId}/${Date.now()}-${file.originalname}`;
        
        uploadedFiles.push({
          original_name: file.originalname,
          file_url: fileUrl,
          file_size: file.size,
          mime_type: file.mimetype,
          uploaded_at: new Date()
        });
      }
      
      // Save file URLs to order
      const order = await Order.findById(orderId);
      if (order) {
        if (!order.communication.proof_of_delivery) {
          order.communication.proof_of_delivery = [];
        }
        
        order.communication.proof_of_delivery.push(...uploadedFiles);
        await order.save();
      }
      
      // Broadcast proof upload
      const socketService = require('../services/socketService');
      socketService.broadcastToOrderRoom(orderId, 'proof_of_delivery_uploaded', {
        order_id: orderId,
        files: uploadedFiles,
        uploaded_by: driver._id,
        timestamp: new Date()
      });
      
      res.json({
        success: true,
        message: 'Proof of delivery uploaded successfully',
        data: {
          uploaded_files: uploadedFiles
        }
      });
      
    } catch (error) {
      logger.error('Error uploading proof of delivery:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Submit post-task survey
   * POST /api/v1/orders/:id/survey
   */
  static async submitSurvey(req, res) {
    try {
      const { id: orderId } = req.params;
      const { survey_responses, overall_rating, feedback } = req.body;
      const driverId = req.user.id;
      
      const driver = await Driver.findOne({ user_id: driverId });
      
      if (!driver) {
        return res.status(404).json({
          success: false,
          message: 'Driver not found'
        });
      }
      
      const order = await Order.findById(orderId);
      
      if (!order) {
        return res.status(404).json({
          success: false,
          message: 'Order not found'
        });
      }
      
      // Save survey data
      const surveyData = {
        order_id: orderId,
        driver_id: driver._id,
        survey_responses,
        overall_rating,
        feedback,
        submitted_at: new Date()
      };
      
      // In a real implementation, save to a dedicated Surveys collection
      // For now, we'll add it to order communication notes
      if (!order.communication.internal_notes) {
        order.communication.internal_notes = '';
      }
      
      order.communication.internal_notes += `\n[Driver Survey - ${new Date().toISOString()}]\nRating: ${overall_rating}/5\nFeedback: ${feedback}\n`;
      
      await order.save();
      
      // Complete the task
      await driver.updateTaskState('TASK_COMPLETED', 'Task completed with survey submission');
      
      // Update driver performance metrics
      await driver.calculatePerformanceMetrics();
      
      res.json({
        success: true,
        message: 'Survey submitted successfully',
        data: {
          task_state: driver.current_task.task_state
        }
      });
      
    } catch (error) {
      logger.error('Error submitting survey:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Update driver location
   * PUT /api/v1/drivers/location
   */
  static async updateLocation(req, res) {
    try {
      const { latitude, longitude, speed, heading, accuracy, source } = req.body;
      const driverId = req.user.id;
      
      if (!latitude || !longitude) {
        return res.status(400).json({
          success: false,
          message: 'Latitude and longitude are required'
        });
      }
      
      const driver = await Driver.findOne({ user_id: driverId });
      
      if (!driver) {
        return res.status(404).json({
          success: false,
          message: 'Driver not found'
        });
      }
      
      await driver.updateLocation(
        [parseFloat(longitude), parseFloat(latitude)],
        speed ? parseFloat(speed) : null,
        heading ? parseFloat(heading) : null,
        accuracy ? parseFloat(accuracy) : null,
        source
      );
      
      // Update Redis cache for real-time tracking
      const socketService = require('../services/socketService');
      if (driver.current_task.order_id) {
        await socketService.updateDriverLocationInRedis({
          lat: parseFloat(latitude),
          lng: parseFloat(longitude),
          timestamp: new Date(),
          driverId: driver._id,
          orderId: driver.current_task.order_id
        });
      }
      
      res.json({
        success: true,
        message: 'Location updated successfully'
      });
      
    } catch (error) {
      logger.error('Error updating driver location:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Get driver performance metrics
   * GET /api/v1/drivers/performance
   */
  static async getPerformance(req, res) {
    try {
      const driverId = req.user.id;
      
      const driver = await Driver.findOne({ user_id: driverId });
      
      if (!driver) {
        return res.status(404).json({
          success: false,
          message: 'Driver not found'
        });
      }
      
      // Calculate additional metrics
      await driver.calculatePerformanceMetrics();
      
      res.json({
        success: true,
        data: {
          performance: driver.performance,
          task_completion_rate: driver.task_completion_rate,
          location_update_frequency: driver.location_update_frequency,
          current_streak: {
            days: 0, // Calculate based on recent completed orders
            completed_orders: driver.performance.completed_orders
          }
        }
      });
      
    } catch (error) {
      logger.error('Error getting driver performance:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Sync offline data
   * POST /api/v1/drivers/sync-offline
   */
  static async syncOfflineData(req, res) {
    try {
      const { location_updates, status_updates } = req.body;
      const driverId = req.user.id;
      
      const driver = await Driver.findOne({ user_id: driverId });
      
      if (!driver) {
        return res.status(404).json({
          success: false,
          message: 'Driver not found'
        });
      }
      
      const syncResults = {
        location_updates_synced: 0,
        status_updates_synced: 0,
        errors: []
      };
      
      // Process location updates
      if (location_updates && Array.isArray(location_updates)) {
        for (const update of location_updates) {
          try {
            await driver.updateLocation(
              [update.longitude, update.latitude],
              update.speed,
              update.heading,
              update.accuracy,
              update.source
            );
            syncResults.location_updates_synced++;
          } catch (error) {
            syncResults.errors.push({
              type: 'location_update',
              data: update,
              error: error.message
            });
          }
        }
      }
      
      // Process status updates
      if (status_updates && Array.isArray(status_updates)) {
        for (const update of status_updates) {
          try {
            const order = await Order.findById(update.order_id);
            if (order && order.driver.driver_id?.toString() === driver._id.toString()) {
              await driver.updateTaskState(update.new_status, update.notes);
              syncResults.status_updates_synced++;
            }
          } catch (error) {
            syncResults.errors.push({
              type: 'status_update',
              data: update,
              error: error.message
            });
          }
        }
      }
      
      res.json({
        success: true,
        message: 'Offline data sync completed',
        data: syncResults
      });
      
    } catch (error) {
      logger.error('Error syncing offline data:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Get all drivers for admin dashboard
   */
  static async getAllDrivers(req, res) {
    try {
      const drivers = await Driver.find({ is_deleted: false })
        .populate('user_id', 'name email phone')
        .populate('assigned_vehicle.truck_id', 'plate_number model')
        .sort({ created_at: -1 });
      res.json({ success: true, data: drivers });
    } catch (error) {
      logger.error('Error fetching drivers:', error);
      res.status(500).json({ success: false, message: 'Internal server error', error: error.message });
    }
  }

  /**
   * Get available drivers for admin dashboard
   */
  static async getAvailableDrivers(req, res) {
    try {
      const drivers = await Driver.find({ is_available: true, is_deleted: false })
        .populate('assigned_vehicle.truck_id', 'plate_number model')
        .sort({ created_at: -1 });
      res.json({ success: true, data: drivers });
    } catch (error) {
      logger.error('Error fetching available drivers:', error);
      res.status(500).json({ success: false, message: 'Internal server error', error: error.message });
    }
  }

  /**
   * Assign a task to a driver (basic implementation)
   */
  static async assignTaskToDriver(req, res) {
    try {
      const { id } = req.params;
      const { orderId } = req.body;

      if (!orderId) {
        return res.status(400).json({ success: false, message: 'Order ID is required' });
      }

      const driver = await Driver.findById(id);
      if (!driver) {
        return res.status(404).json({ success: false, message: 'Driver not found' });
      }

      driver.current_task = driver.current_task || {};
      driver.current_task.order_id = orderId;
      driver.current_task.task_state = 'ASSIGNED';
      await driver.save();

      res.json({ success: true, message: 'Task assigned to driver', data: driver });
    } catch (error) {
      logger.error('Error assigning task to driver:', error);
      res.status(500).json({ success: false, message: 'Internal server error', error: error.message });
    }
  }

  /**
   * Suspend a driver
   */
  static async suspendDriver(req, res) {
    try {
      const { id } = req.params;
      const driver = await Driver.findById(id);
      if (!driver) {
        return res.status(404).json({ success: false, message: 'Driver not found' });
      }

      driver.status = 'SUSPENDED';
      driver.is_available = false;
      await driver.save();

      res.json({ success: true, message: 'Driver suspended successfully', data: driver });
    } catch (error) {
      logger.error('Error suspending driver:', error);
      res.status(500).json({ success: false, message: 'Internal server error', error: error.message });
    }
  }

  /**
   * Activate a driver
   */
  static async activateDriver(req, res) {
    try {
      const { id } = req.params;
      const driver = await Driver.findById(id);
      if (!driver) {
        return res.status(404).json({ success: false, message: 'Driver not found' });
      }

      driver.status = 'AVAILABLE';
      driver.is_available = true;
      await driver.save();

      res.json({ success: true, message: 'Driver activated successfully', data: driver });
    } catch (error) {
      logger.error('Error activating driver:', error);
      res.status(500).json({ success: false, message: 'Internal server error', error: error.message });
    }
  }
}

module.exports = DriverController;
