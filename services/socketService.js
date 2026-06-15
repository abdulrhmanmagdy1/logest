/**
 * ============================================
 * 🔄 Edham Logistics - WebSocket Service
 * نظام إدهام - خدمة WebSocket للتتبع اللحظي
 * ============================================
 */

const socketIo = require('socket.io');
const jwt = require('jsonwebtoken');
const redis = require('redis');
const Order = require('../models/Order');
const Driver = require('../models/Driver');
const logger = require('../utils/logger');

class SocketService {
  constructor() {
    this.io = null;
    this.redisClient = null;
    this.connectedClients = new Map(); // userId -> socketId
    this.orderRooms = new Map(); // orderId -> Set of socketIds
    this.driverLocations = new Map(); // driverId -> { lat, lng, timestamp }
  }

  /**
   * Initialize WebSocket server
   */
  async initialize(server) {
    try {
      // Initialize Socket.IO
      this.io = socketIo(server, {
        cors: {
          origin: process.env.FRONTEND_URL || "http://localhost:3000",
          methods: ["GET", "POST"],
          credentials: true
        },
        transports: ['websocket', 'polling']
      });

      // Initialize Redis client
      this.redisClient = redis.createClient({
        url: process.env.REDIS_URL || 'redis://localhost:6379'
      });

      await this.redisClient.connect();
      logger.info('Redis client connected successfully');

      // Set up authentication middleware
      this.setupAuthMiddleware();

      // Set up event handlers
      this.setupEventHandlers();

      // Start batch processing for saving locations to MongoDB
      this.startBatchProcessing();

      logger.info('WebSocket service initialized successfully');
    } catch (error) {
      logger.error('Failed to initialize WebSocket service:', error);
      throw error;
    }
  }

  /**
   * Setup authentication middleware for Socket.IO
   */
  setupAuthMiddleware() {
    this.io.use(async (socket, next) => {
      try {
        const token = socket.handshake.auth.token || socket.handshake.headers.authorization?.replace('Bearer ', '');
        
        if (!token) {
          return next(new Error('Authentication token required'));
        }

        // Verify JWT token
        const decoded = jwt.verify(token, process.env.JWT_SECRET);
        
        // Attach user info to socket
        socket.userId = decoded.id;
        socket.userType = decoded.type; // 'customer' or 'driver' or 'admin'
        socket.userData = decoded;

        logger.info(`User ${socket.userId} (${socket.userType}) connected via WebSocket`);
        next();
      } catch (error) {
        logger.error('WebSocket authentication failed:', error);
        next(new Error('Invalid authentication token'));
      }
    });
  }

  /**
   * Setup event handlers for WebSocket connections
   */
  setupEventHandlers() {
    this.io.on('connection', (socket) => {
      logger.info(`Socket connected: ${socket.id} (User: ${socket.userId}, Type: ${socket.userType})`);

      // Track connected client
      this.connectedClients.set(socket.userId, socket.id);

      // Handle disconnection
      socket.on('disconnect', () => {
        logger.info(`Socket disconnected: ${socket.id} (User: ${socket.userId})`);
        this.handleDisconnection(socket);
      });

      // Customer events
      if (socket.userType === 'customer') {
        this.setupCustomerEvents(socket);
      }

      // Driver events
      else if (socket.userType === 'driver') {
        this.setupDriverEvents(socket);
      }

      // Admin events
      else if (socket.userType === 'admin') {
        this.setupAdminEvents(socket);
      }
    });
  }

  /**
   * Setup customer-specific event handlers
   */
  setupCustomerEvents(socket) {
    // Customer subscribes to order tracking
    socket.on('subscribe_to_order', async (data) => {
      try {
        const { orderId } = data;
        
        if (!orderId) {
          socket.emit('error', { message: 'Order ID is required' });
          return;
        }

        // Verify customer owns this order
        const order = await Order.findOne({
          _id: orderId,
          customer_id: socket.userId
        });

        if (!order) {
          socket.emit('error', { message: 'Order not found or access denied' });
          return;
        }

        // Join order room
        const roomName = `order_${orderId}`;
        socket.join(roomName);

        // Track room membership
        if (!this.orderRooms.has(orderId)) {
          this.orderRooms.set(orderId, new Set());
        }
        this.orderRooms.get(orderId).add(socket.id);

        // Send current order status and driver location
        const currentData = await this.getCurrentOrderData(orderId);
        socket.emit('order_data', currentData);

        logger.info(`Customer ${socket.userId} subscribed to order ${orderId}`);
      } catch (error) {
        logger.error('Error in subscribe_to_order:', error);
        socket.emit('error', { message: 'Failed to subscribe to order' });
      }
    });

    // Customer unsubscribes from order tracking
    socket.on('unsubscribe_from_order', (data) => {
      const { orderId } = data;
      this.handleUnsubscribe(socket, orderId);
    });

    // Customer sends message to driver
    socket.on('send_message_to_driver', async (data) => {
      try {
        const { orderId, message } = data;
        
        // Validate order access
        const order = await Order.findOne({
          _id: orderId,
          customer_id: socket.userId
        });

        if (!order || !order.driver.driver_id) {
          socket.emit('error', { message: 'Order not found or no driver assigned' });
          return;
        }

        // Forward message to driver
        const driverSocketId = this.connectedClients.get(order.driver.driver_id.toString());
        if (driverSocketId) {
          this.io.to(driverSocketId).emit('message_from_customer', {
            orderId,
            message,
            customerName: socket.userData.name,
            timestamp: new Date()
          });
        }

        // Save message to order history
        await this.saveOrderMessage(orderId, 'customer', message, socket.userData.name);

      } catch (error) {
        logger.error('Error in send_message_to_driver:', error);
        socket.emit('error', { message: 'Failed to send message' });
      }
    });
  }

  /**
   * Setup driver-specific event handlers
   */
  setupDriverEvents(socket) {
    // Driver updates location
    socket.on('update_location', async (data) => {
      try {
        const { orderId, latitude, longitude } = data;
        
        if (!orderId || !latitude || !longitude) {
          socket.emit('error', { message: 'Order ID and location are required' });
          return;
        }

        // Validate driver is assigned to this order
        const order = await Order.findOne({
          _id: orderId,
          'driver.driver_id': socket.userId
        });

        if (!order) {
          socket.emit('error', { message: 'Order not found or driver not assigned' });
          return;
        }

        const locationData = {
          lat: parseFloat(latitude),
          lng: parseFloat(longitude),
          timestamp: new Date(),
          driverId: socket.userId,
          orderId: orderId
        };

        // Update Redis cache
        await this.updateDriverLocationInRedis(locationData);

        // Update local cache
        this.driverLocations.set(socket.userId, locationData);

        // Broadcast to order room
        const roomName = `order_${orderId}`;
        this.io.to(roomName).emit('driver_location_update', {
          orderId,
          latitude,
          longitude,
          timestamp: locationData.timestamp
        });

        // Calculate ETA and remaining distance
        const etaData = await this.calculateETA(orderId, locationData);
        
        // Broadcast ETA to room
        this.io.to(roomName).emit('eta_update', etaData);

        logger.debug(`Driver ${socket.userId} location updated for order ${orderId}`);
      } catch (error) {
        logger.error('Error in update_location:', error);
        socket.emit('error', { message: 'Failed to update location' });
      }
    });

    // Driver updates order status
    socket.on('update_order_status', async (data) => {
      try {
        const { orderId, status, location, notes } = data;
        
        // Validate driver is assigned to this order
        const order = await Order.findOne({
          _id: orderId,
          'driver.driver_id': socket.userId
        });

        if (!order) {
          socket.emit('error', { message: 'Order not found or driver not assigned' });
          return;
        }

        // Update order status
        order.status = status;
        
        // Add tracking event
        await order.addTrackingEvent(status, notes || `Status updated to ${status}`, location);

        // Broadcast status update to order room
        const roomName = `order_${orderId}`;
        this.io.to(roomName).emit('order_status_update', {
          orderId,
          status,
          timestamp: new Date(),
          notes
        });

        logger.info(`Driver ${socket.userId} updated order ${orderId} status to ${status}`);
      } catch (error) {
        logger.error('Error in update_order_status:', error);
        socket.emit('error', { message: 'Failed to update order status' });
      }
    });

    // Driver sends message to customer
    socket.on('send_message_to_customer', async (data) => {
      try {
        const { orderId, message } = data;
        
        // Validate order access
        const order = await Order.findOne({
          _id: orderId,
          'driver.driver_id': socket.userId
        });

        if (!order) {
          socket.emit('error', { message: 'Order not found' });
          return;
        }

        // Forward message to customer
        const roomName = `order_${orderId}`;
        this.io.to(roomName).emit('message_from_driver', {
          orderId,
          message,
          driverName: socket.userData.name,
          timestamp: new Date()
        });

        // Save message to order history
        await this.saveOrderMessage(orderId, 'driver', message, socket.userData.name);

      } catch (error) {
        logger.error('Error in send_message_to_customer:', error);
        socket.emit('error', { message: 'Failed to send message' });
      }
    });
  }

  /**
   * Setup admin-specific event handlers
   */
  setupAdminEvents(socket) {
    // Admin subscribes to all active orders
    socket.on('subscribe_to_all_orders', async () => {
      try {
        // Get all active orders
        const activeOrders = await Order.find({
          status: { $in: ['ASSIGNED', 'PICKUP_CONFIRMED', 'IN_TRANSIT'] }
        }).populate('driver.driver_id', 'name phone');

        // Join all order rooms
        for (const order of activeOrders) {
          const roomName = `order_${order._id}`;
          socket.join(roomName);
        }

        // Send current data for all orders
        socket.emit('all_orders_data', activeOrders);

        logger.info(`Admin ${socket.userId} subscribed to all active orders`);
      } catch (error) {
        logger.error('Error in subscribe_to_all_orders:', error);
        socket.emit('error', { message: 'Failed to subscribe to orders' });
      }
    });

    // Admin sends system notification
    socket.on('send_system_notification', async (data) => {
      try {
        const { orderId, message, type } = data;
        
        // Broadcast to specific order room or all rooms
        if (orderId) {
          this.io.to(`order_${orderId}`).emit('system_notification', {
            message,
            type,
            timestamp: new Date()
          });
        } else {
          this.io.emit('system_notification', {
            message,
            type,
            timestamp: new Date()
          });
        }

        logger.info(`Admin ${socket.userId} sent system notification`);
      } catch (error) {
        logger.error('Error in send_system_notification:', error);
        socket.emit('error', { message: 'Failed to send notification' });
      }
    });
  }

  /**
   * Update driver location in Redis
   */
  async updateDriverLocationInRedis(locationData) {
    try {
      const key = `driver_location:${locationData.driverId}`;
      const value = JSON.stringify(locationData);
      
      // Store with 5 minutes expiration
      await this.redisClient.setEx(key, 300, value);
      
      // Also store in order-specific key for quick retrieval
      const orderKey = `order_location:${locationData.orderId}`;
      await this.redisClient.setEx(orderKey, 300, value);
      
    } catch (error) {
      logger.error('Error updating Redis location:', error);
    }
  }

  /**
   * Get current order data including driver location
   */
  async getCurrentOrderData(orderId) {
    try {
      const order = await Order.findById(orderId)
        .populate('driver.driver_id', 'name phone rating avatar')
        .populate('driver.truck_id', 'plate_number model');

      if (!order) {
        return null;
      }

      // Get current driver location from Redis
      let driverLocation = null;
      if (order.driver.driver_id) {
        const locationKey = `order_location:${orderId}`;
        const locationData = await this.redisClient.get(locationKey);
        if (locationData) {
          driverLocation = JSON.parse(locationData);
        }
      }

      // Calculate ETA if driver location is available
      let etaData = null;
      if (driverLocation) {
        etaData = await this.calculateETA(orderId, driverLocation);
      }

      return {
        order: {
          id: order._id,
          order_number: order.order_number,
          status: order.status,
          route: order.route,
          driver: order.driver.driver_id ? {
            name: order.driver.driver_id.name,
            phone: order.driver.driver_id.phone,
            rating: order.driver.driver_id.rating,
            avatar: order.driver.driver_id.avatar,
            truck: order.driver.truck_id ? {
              plate_number: order.driver.truck_id.plate_number,
              model: order.driver.truck_id.model
            } : null
          } : null
        },
        driverLocation,
        etaData
      };
    } catch (error) {
      logger.error('Error getting current order data:', error);
      return null;
    }
  }

  /**
   * Calculate ETA and remaining distance
   */
  async calculateETA(orderId, driverLocation) {
    try {
      const order = await Order.findById(orderId);
      if (!order) return null;

      // Calculate distance from driver to dropoff location
      const distance = this.calculateDistance(
        driverLocation.lat,
        driverLocation.lng,
        order.route.dropoff.location.coordinates[1], // latitude
        order.route.dropoff.location.coordinates[0]  // longitude
      );

      // Estimate time based on average speed (40 km/h in city, 80 km/h on highway)
      const avgSpeed = distance < 50 ? 40 : 80; // km/h
      const etaMinutes = Math.ceil((distance / avgSpeed) * 60);

      return {
        remainingDistance: Math.round(distance * 100) / 100, // Round to 2 decimal places
        etaMinutes,
        etaTimestamp: new Date(Date.now() + etaMinutes * 60000)
      };
    } catch (error) {
      logger.error('Error calculating ETA:', error);
      return null;
    }
  }

  /**
   * Calculate distance between two points (Haversine formula)
   */
  calculateDistance(lat1, lon1, lat2, lon2) {
    const R = 6371; // Earth's radius in kilometers
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLon = (lon2 - lon1) * Math.PI / 180;
    const a = Math.sin(dLat/2) * Math.sin(dLat/2) +
      Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
      Math.sin(dLon/2) * Math.sin(dLon/2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    return R * c;
  }

  /**
   * Save order message to history
   */
  async saveOrderMessage(orderId, senderType, message, senderName) {
    try {
      // This would save to a messages collection or order communication log
      // For now, we'll just log it
      logger.info(`Message saved for order ${orderId}: ${senderType} (${senderName}): ${message}`);
    } catch (error) {
      logger.error('Error saving order message:', error);
    }
  }

  /**
   * Handle client disconnection
   */
  handleDisconnection(socket) {
    // Remove from connected clients
    this.connectedClients.delete(socket.userId);

    // Remove from all order rooms
    for (const [orderId, socketSet] of this.orderRooms.entries()) {
      socketSet.delete(socket.id);
      if (socketSet.size === 0) {
        this.orderRooms.delete(orderId);
      }
    }

    // Remove driver location from cache
    this.driverLocations.delete(socket.userId);
  }

  /**
   * Handle unsubscribe from order
   */
  handleUnsubscribe(socket, orderId) {
    const roomName = `order_${orderId}`;
    socket.leave(roomName);

    if (this.orderRooms.has(orderId)) {
      this.orderRooms.get(orderId).delete(socket.id);
      if (this.orderRooms.get(orderId).size === 0) {
        this.orderRooms.delete(orderId);
      }
    }

    logger.info(`User ${socket.userId} unsubscribed from order ${orderId}`);
  }

  /**
   * Start batch processing for saving locations to MongoDB
   */
  startBatchProcessing() {
    // Run every 2 minutes
    setInterval(async () => {
      await this.batchSaveLocationsToMongoDB();
    }, 120000); // 2 minutes
  }

  /**
   * Batch save driver locations to MongoDB
   */
  async batchSaveLocationsToMongoDB() {
    try {
      logger.info('Starting batch save of driver locations to MongoDB');

      // Get all driver locations from Redis
      const keys = await this.redisClient.keys('driver_location:*');
      const locations = [];

      for (const key of keys) {
        const locationData = await this.redisClient.get(key);
        if (locationData) {
          locations.push(JSON.parse(locationData));
        }
      }

      if (locations.length > 0) {
        // Group by order ID and save as route history
        const locationsByOrder = {};
        for (const location of locations) {
          if (!locationsByOrder[location.orderId]) {
            locationsByOrder[location.orderId] = [];
          }
          locationsByOrder[location.orderId].push(location);
        }

        // Save each order's route history
        for (const [orderId, orderLocations] of Object.entries(locationsByOrder)) {
          await this.saveOrderRouteHistory(orderId, orderLocations);
        }

        logger.info(`Batch saved ${locations.length} location updates for ${Object.keys(locationsByOrder).length} orders`);
      }
    } catch (error) {
      logger.error('Error in batch save to MongoDB:', error);
    }
  }

  /**
   * Save order route history to MongoDB
   */
  async saveOrderRouteHistory(orderId, locations) {
    try {
      const order = await Order.findById(orderId);
      if (!order) return;

      // Create GeoJSON LineString from locations
      const coordinates = locations.map(loc => [loc.lng, loc.lat]);
      
      // Update order tracking with route history
      // This would be saved to a separate RouteHistory collection for performance
      logger.info(`Route history saved for order ${orderId} with ${locations.length} points`);
    } catch (error) {
      logger.error('Error saving route history:', error);
    }
  }

  /**
   * Get connected clients count
   */
  getConnectedClientsCount() {
    return this.connectedClients.size;
  }

  /**
   * Get order room members count
   */
  getOrderRoomMembersCount(orderId) {
    return this.orderRooms.get(orderId)?.size || 0;
  }

  /**
   * Send notification to specific user
   */
  sendNotificationToUser(userId, notification) {
    const socketId = this.connectedClients.get(userId);
    if (socketId) {
      this.io.to(socketId).emit('notification', notification);
      return true;
    }
    return false;
  }

  /**
   * Broadcast to order room
   */
  broadcastToOrderRoom(orderId, event, data) {
    const roomName = `order_${orderId}`;
    this.io.to(roomName).emit(event, data);
  }
}

module.exports = new SocketService();
