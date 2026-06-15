/**
 * ============================================
 * 📦 Edham Logistics - Order Controller
 * نظام إدهام - متحكم الطلبات
 * ============================================
 */

const Order = require('../models/Order');
const User = require('../models/User');
const Driver = require('../models/Driver');
const Truck = require('../models/Truck');
const logger = require('../utils/logger');

class OrderController {
  /**
   * Calculate price for a potential order
   * POST /api/v1/orders/calculate-price
   */
  static async calculatePrice(req, res) {
    try {
      const {
        pickup_location,
        dropoff_location,
        vehicle_type,
        helpers_count,
        insurance_enabled,
        special_handling,
        promo_code
      } = req.body;

      // Validate required fields
      if (!pickup_location || !dropoff_location || !vehicle_type) {
        return res.status(400).json({
          success: false,
          message: 'Missing required fields: pickup_location, dropoff_location, vehicle_type'
        });
      }

      // Calculate distance using geospatial calculation
      const distance_km = await calculateDistance(pickup_location, dropoff_location);
      
      // Get vehicle pricing
      const vehiclePricing = await getVehiclePricing(vehicle_type);
      
      // Prepare order data for calculation
      const orderData = {
        route: {
          distance_km: distance_km,
          estimated_duration_minutes: Math.ceil(distance_km * 2.5) // Average 40 km/h
        },
        vehicle: vehiclePricing,
        services: {
          helpers: {
            count: helpers_count || 0,
            rate_per_helper: 60 // SAR 60 per helper
          },
          insurance: {
            enabled: insurance_enabled || false,
            coverage_amount: 10000,
            premium_rate: 0.02 // 2% of coverage
          },
          special_handling: special_handling || {}
        },
        discounts: promo_code ? await validatePromoCode(promo_code) : []
      };

      // Calculate price using Order model static method
      const pricing = await Order.calculatePrice(orderData);

      res.json({
        success: true,
        data: {
          distance_km: distance_km,
          estimated_duration_minutes: orderData.route.estimated_duration_minutes,
          pricing: pricing,
          breakdown: {
            base_cost: pricing.base_cost,
            helpers_cost: pricing.helper_cost,
            insurance_cost: pricing.insurance_cost,
            handling_fees: pricing.handling_fees,
            subtotal: pricing.subtotal,
            discounts: pricing.total_discount,
            tax: pricing.tax_amount,
            total: pricing.total_amount
          }
        }
      });

    } catch (error) {
      logger.error('Error calculating order price:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Create a new order
   * POST /api/v1/orders
   */
  static async createOrder(req, res) {
    try {
      const {
        pickup_address,
        pickup_location,
        dropoff_address,
        dropoff_location,
        pickup_date,
        pickup_time,
        vehicle_type,
        cargo_details,
        services,
        payment_method,
        promo_code
      } = req.body;

      // Validate required fields
      const requiredFields = [
        'pickup_address', 'pickup_location', 'dropoff_address', 
        'dropoff_location', 'pickup_date', 'pickup_time', 
        'vehicle_type', 'cargo_details'
      ];

      for (const field of requiredFields) {
        if (!req.body[field]) {
          return res.status(400).json({
            success: false,
            message: `Missing required field: ${field}`
          });
        }
      }

      // Calculate distance and pricing
      const distance_km = await calculateDistance(pickup_location, dropoff_location);
      const vehiclePricing = await getVehiclePricing(vehicle_type);

      const orderData = {
        customer_id: req.user.id, // From authentication middleware
        route: {
          pickup: {
            address: pickup_address,
            location: {
              type: 'Point',
              coordinates: [pickup_location.lng, pickup_location.lat]
            }
          },
          dropoff: {
            address: dropoff_address,
            location: {
              type: 'Point',
              coordinates: [dropoff_location.lng, dropoff_location.lat]
            }
          },
          distance_km: distance_km,
          estimated_duration_minutes: Math.ceil(distance_km * 2.5)
        },
        scheduling: {
          pickup_date: new Date(pickup_date),
          pickup_time: pickup_time
        },
        vehicle: vehiclePricing,
        cargo: cargo_details,
        services: services || {},
        invoice: {
          payment_method: payment_method
        }
      };

      // Calculate pricing
      const pricing = await Order.calculatePrice(orderData);
      orderData.invoice = {
        ...orderData.invoice,
        items: [
          {
            description: `Transportation (${vehiclePricing.type})`,
            quantity: distance_km,
            unit_price: vehiclePricing.pricing.base_rate_per_km,
            total: pricing.base_cost
          }
        ],
        subtotal: pricing.subtotal,
        tax_amount: pricing.tax_amount,
        total_amount: pricing.total_amount
      };

      // Apply promo code if provided
      if (promo_code) {
        const discount = await validatePromoCode(promo_code);
        if (discount.length > 0) {
          orderData.invoice.discounts = discount;
        }
      }

      // Create order
      const order = new Order(orderData);
      await order.save();

      // Add tracking event
      await order.addTrackingEvent('ORDER_CREATED', 'Order created successfully');

      logger.info(`New order created: ${order.order_number} by customer ${req.user.id}`);

      res.status(201).json({
        success: true,
        message: 'Order created successfully',
        data: {
          order_id: order._id,
          order_number: order.order_number,
          tracking_number: order.tracking.tracking_number,
          status: order.status,
          total_amount: order.invoice.total_amount,
          estimated_delivery: new Date(
            new Date(pickup_date).getTime() + (order.route.estimated_duration_minutes * 60000)
          )
        }
      });

    } catch (error) {
      logger.error('Error creating order:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Get customer orders
   * GET /api/v1/orders/customer
   */
  static async getCustomerOrders(req, res) {
    try {
      const { page = 1, limit = 10, status } = req.query;
      const skip = (page - 1) * limit;

      const query = { customer_id: req.user.id };
      if (status) {
        query.status = status;
      }

      const orders = await Order.find(query)
        .sort({ created_at: -1 })
        .skip(skip)
        .limit(parseInt(limit))
        .populate('driver.driver_id', 'name phone rating')
        .populate('driver.truck_id', 'plate_number model');

      const total = await Order.countDocuments(query);

      res.json({
        success: true,
        data: {
          orders: orders.map(order => ({
            id: order._id,
            order_number: order.order_number,
            tracking_number: order.tracking.tracking_number,
            status: order.status,
            route: {
              from: order.route.pickup.address,
              to: order.route.dropoff.address,
              distance_km: order.route.distance_km
            },
            scheduling: {
              pickup_date: order.scheduling.pickup_date,
              pickup_time: order.scheduling.pickup_time
            },
            vehicle: order.vehicle.type,
            invoice: {
              total_amount: order.invoice.total_amount,
              payment_status: order.invoice.payment_status
            },
            driver: order.driver.driver_id ? {
              name: order.driver.driver_id.name,
              phone: order.driver.driver_id.phone,
              rating: order.driver.driver_id.rating,
              truck_plate: order.driver.truck_id?.plate_number
            } : null,
            progress_percentage: order.progress_percentage,
            created_at: order.created_at
          })),
          pagination: {
            current_page: parseInt(page),
            total_pages: Math.ceil(total / limit),
            total_orders: total,
            has_next: skip + limit < total
          }
        }
      });

    } catch (error) {
      logger.error('Error getting customer orders:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Get order details
   * GET /api/v1/orders/:id
   */
  static async getOrderDetails(req, res) {
    try {
      const { id } = req.params;

      const order = await Order.findOne({
        _id: id,
        customer_id: req.user.id
      })
      .populate('driver.driver_id', 'name phone rating avatar')
      .populate('driver.truck_id', 'plate_number model year')
      .populate('created_by', 'name email');

      if (!order) {
        return res.status(404).json({
          success: false,
          message: 'Order not found'
        });
      }

      res.json({
        success: true,
        data: {
          order: {
            id: order._id,
            order_number: order.order_number,
            tracking_number: order.tracking.tracking_number,
            status: order.status,
            route: order.route,
            scheduling: order.scheduling,
            vehicle: order.vehicle,
            cargo: order.cargo,
            services: order.services,
            invoice: order.invoice,
            driver: order.driver.driver_id ? {
              id: order.driver.driver_id._id,
              name: order.driver.driver_id.name,
              phone: order.driver.driver_id.phone,
              rating: order.driver.driver_id.rating,
              avatar: order.driver.driver_id.avatar,
              truck: order.driver.truck_id ? {
                plate_number: order.driver.truck_id.plate_number,
                model: order.driver.truck_id.model,
                year: order.driver.truck_id.year
              } : null,
              assigned_at: order.driver.assigned_at,
              estimated_arrival_time: order.driver.estimated_arrival_time
            } : null,
            tracking: {
              current_location: order.tracking.current_location,
              last_updated: order.tracking.last_updated,
              events: order.tracking.events
            },
            rating: order.rating,
            progress_percentage: order.progress_percentage,
            created_at: order.created_at,
            updated_at: order.updated_at
          }
        }
      });

    } catch (error) {
      logger.error('Error getting order details:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Cancel order
   * POST /api/v1/orders/:id/cancel
   */
  static async cancelOrder(req, res) {
    try {
      const { id } = req.params;
      const { reason } = req.body;

      const order = await Order.findOne({
        _id: id,
        customer_id: req.user.id
      });

      if (!order) {
        return res.status(404).json({
          success: false,
          message: 'Order not found'
        });
      }

      if (!order.can_be_cancelled) {
        return res.status(400).json({
          success: false,
          message: 'Order cannot be cancelled at this stage'
        });
      }

      order.status = 'CANCELLED';
      order.communication.internal_notes = reason || 'Cancelled by customer';
      await order.save();

      await order.addTrackingEvent('CANCELLED', 'Order cancelled by customer');

      logger.info(`Order ${order.order_number} cancelled by customer ${req.user.id}`);

      res.json({
        success: true,
        message: 'Order cancelled successfully'
      });

    } catch (error) {
      logger.error('Error cancelling order:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Track order
   * GET /api/v1/orders/:id/track
   */
  static async trackOrder(req, res) {
    try {
      const { id } = req.params;

      const order = await Order.findOne({
        _id: id,
        customer_id: req.user.id
      })
      .populate('driver.driver_id', 'name phone rating')
      .populate('driver.truck_id', 'plate_number model');

      if (!order) {
        return res.status(404).json({
          success: false,
          message: 'Order not found'
        });
      }

      res.json({
        success: true,
        data: {
          tracking_number: order.tracking.tracking_number,
          status: order.status,
          current_location: order.tracking.current_location,
          last_updated: order.tracking.last_updated,
          progress_percentage: order.progress_percentage,
          estimated_delivery: order.driver.estimated_arrival_time,
          driver: order.driver.driver_id ? {
            name: order.driver.driver_id.name,
            phone: order.driver.driver_id.phone,
            rating: order.driver.driver_id.rating,
            truck_plate: order.driver.truck_id?.plate_number
          } : null,
          events: order.tracking.events.slice(-10).reverse() // Last 10 events
        }
      });

    } catch (error) {
      logger.error('Error tracking order:', error);
      res.status(500).json({
        success: false,
        message: 'Internal server error',
        error: error.message
      });
    }
  }

  /**
   * Get all orders for admin users
   */
  static async getAllOrders(req, res) {
    try {
      const orders = await Order.find({ is_deleted: false })
        .populate('customer_id', 'name email phone')
        .populate('driver.driver_id', 'name phone')
        .sort({ created_at: -1 });

      res.json({ success: true, data: orders });
    } catch (error) {
      logger.error('Error fetching all orders:', error);
      res.status(500).json({ success: false, message: 'Internal server error', error: error.message });
    }
  }

  /**
   * Assign a driver to an order
   */
  static async assignDriver(req, res) {
    try {
      const { id } = req.params;
      const { driverId } = req.body;

      if (!driverId) {
        return res.status(400).json({ success: false, message: 'Driver ID is required' });
      }

      const order = await Order.findById(id);
      const driver = await Driver.findById(driverId);

      if (!order || !driver) {
        return res.status(404).json({ success: false, message: 'Order or driver not found' });
      }

      order.driver = order.driver || {};
      order.driver.driver_id = driver._id;
      order.driver.status = 'ASSIGNED';
      order.status = 'ASSIGNED';
      await order.save();

      res.json({ success: true, message: 'Driver assigned successfully', data: order });
    } catch (error) {
      logger.error('Error assigning driver:', error);
      res.status(500).json({ success: false, message: 'Internal server error', error: error.message });
    }
  }

  /**
   * Update the status of an order
   */
  static async updateOrderStatus(req, res) {
    try {
      const { id } = req.params;
      const { status, notes } = req.body;

      const order = await Order.findById(id);
      if (!order) {
        return res.status(404).json({ success: false, message: 'Order not found' });
      }

      if (status) {
        order.status = status;
      }
      if (notes) {
        order.communication = order.communication || {};
        order.communication.admin_notes = notes;
      }

      await order.save();

      res.json({ success: true, message: 'Order status updated successfully', data: order });
    } catch (error) {
      logger.error('Error updating order status:', error);
      res.status(500).json({ success: false, message: 'Internal server error', error: error.message });
    }
  }
}

/**
 * Helper function to calculate distance between two points
 */
async function calculateDistance(pickup, dropoff) {
  // Using Haversine formula for distance calculation
  const R = 6371; // Earth's radius in kilometers
  const lat1 = pickup.lat * Math.PI / 180;
  const lat2 = dropoff.lat * Math.PI / 180;
  const deltaLat = (dropoff.lat - pickup.lat) * Math.PI / 180;
  const deltaLon = (dropoff.lng - pickup.lng) * Math.PI / 180;

  const a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) +
    Math.cos(lat1) * Math.cos(lat2) *
    Math.sin(deltaLon/2) * Math.sin(deltaLon/2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

  return Math.round(R * c * 100) / 100; // Round to 2 decimal places
}

/**
 * Helper function to get vehicle pricing
 */
async function getVehiclePricing(vehicleType) {
  const vehicleConfigs = {
    'CARGO_TRUCK': {
      type: 'CARGO_TRUCK',
      capacity_kg: 5000,
      dimensions: { length: 6, width: 2.5, height: 3 },
      pricing: {
        base_rate_per_km: 2.5,
        minimum_charge: 50
      }
    },
    'PICKUP_VAN': {
      type: 'PICKUP_VAN',
      capacity_kg: 1500,
      dimensions: { length: 4, width: 2, height: 2 },
      pricing: {
        base_rate_per_km: 1.8,
        minimum_charge: 30
      }
    },
    'LIGHT_TRUCK': {
      type: 'LIGHT_TRUCK',
      capacity_kg: 3000,
      dimensions: { length: 5, width: 2.2, height: 2.5 },
      pricing: {
        base_rate_per_km: 2.0,
        minimum_charge: 40
      }
    },
    'HEAVY_TRUCK': {
      type: 'HEAVY_TRUCK',
      capacity_kg: 10000,
      dimensions: { length: 8, width: 3, height: 4 },
      pricing: {
        base_rate_per_km: 3.5,
        minimum_charge: 80
      }
    },
    'MOTORCYCLE': {
      type: 'MOTORCYCLE',
      capacity_kg: 50,
      dimensions: { length: 1, width: 0.5, height: 0.8 },
      pricing: {
        base_rate_per_km: 1.2,
        minimum_charge: 20
      }
    }
  };

  return vehicleConfigs[vehicleType] || vehicleConfigs['CARGO_TRUCK'];
}

/**
 * Helper function to validate promo code
 */
async function validatePromoCode(code) {
  // Mock promo codes - in real implementation, this would query database
  const promoCodes = {
    'WELCOME10': { amount: 0, percentage: 10, description: 'Welcome discount - 10% off' },
    'SAVE50': { amount: 50, percentage: 0, description: 'Fixed discount - SAR 50' },
    'FLASH20': { amount: 0, percentage: 20, description: 'Flash sale - 20% off' }
  };

  return promoCodes[code] ? [promoCodes[code]] : [];
}

module.exports = OrderController;
