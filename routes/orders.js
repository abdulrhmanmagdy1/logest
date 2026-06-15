/**
 * ============================================
 * 📦 Edham Logistics - Order Routes
 * نظام إدهام - مسارات الطلبات
 * ============================================
 */

const express = require('express');
const router = express.Router();
const OrderController = require('../controllers/orderController');
const { authenticate, authorize } = require('../middleware/auth');
const { validateOrder, validatePriceCalculation } = require('../middleware/validation');
const rateLimit = require('express-rate-limit');

// Rate limiting for order creation
const orderCreationLimit = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 5, // Limit each IP to 5 orders per windowMs
  message: {
    success: false,
    message: 'Too many order creation attempts, please try again later'
  }
});

// Calculate price for potential order
// POST /api/v1/orders/calculate-price
router.post('/calculate-price', 
  authenticate,
  validatePriceCalculation,
  OrderController.calculatePrice
);

// Create new order
// POST /api/v1/orders
router.post('/', 
  authenticate,
  orderCreationLimit,
  validateOrder,
  OrderController.createOrder
);

// Get customer orders
// GET /api/v1/orders/customer
router.get('/customer', 
  authenticate,
  OrderController.getCustomerOrders
);

// Get order details
// GET /api/v1/orders/:id
router.get('/:id', 
  authenticate,
  OrderController.getOrderDetails
);

// Cancel order
// POST /api/v1/orders/:id/cancel
router.post('/:id/cancel', 
  authenticate,
  OrderController.cancelOrder
);

// Track order
// GET /api/v1/orders/:id/track
router.get('/:id/track', 
  authenticate,
  OrderController.trackOrder
);

// Admin routes (require admin role)
// GET /api/v1/orders/admin/all
router.get('/admin/all', 
  authenticate,
  authorize(['admin', 'supervisor']),
  OrderController.getAllOrders
);

// POST /api/v1/orders/admin/:id/assign-driver
router.post('/admin/:id/assign-driver', 
  authenticate,
  authorize(['admin', 'supervisor']),
  OrderController.assignDriver
);

// POST /api/v1/orders/admin/:id/update-status
router.post('/admin/:id/update-status', 
  authenticate,
  authorize(['admin', 'supervisor']),
  OrderController.updateOrderStatus
);

module.exports = router;
