/**
 * ============================================
 * 📊 Edham Logistics - Admin Routes
 * نظام إدهام - مسارات الإدارة
 * ============================================
 */

const express = require('express');
const router = express.Router();
const AdminController = require('../controllers/adminController');
const { authenticate, authorize } = require('../middleware/auth');
const { validateMaintenance, validateOrderAssignment } = require('../middleware/validation');

// Supervisor Dashboard Routes
// GET /api/v1/admin/supervisor/dashboard
router.get('/supervisor/dashboard', 
  authenticate,
  authorize(['admin', 'supervisor']),
  AdminController.getSupervisorDashboard
);

// GET /api/v1/admin/supervisor/nearby-drivers/:orderId
router.get('/supervisor/nearby-drivers/:orderId', 
  authenticate,
  authorize(['admin', 'supervisor']),
  AdminController.getNearbyDrivers
);

// POST /api/v1/admin/supervisor/assign-order
router.post('/supervisor/assign-order', 
  authenticate,
  authorize(['admin', 'supervisor']),
  validateOrderAssignment,
  AdminController.assignOrderToDriver
);

// Accountant Dashboard Routes
// GET /api/v1/admin/accountant/dashboard
router.get('/accountant/dashboard', 
  authenticate,
  authorize(['admin', 'accountant']),
  AdminController.getAccountantDashboard
);

// GET /api/v1/admin/accountant/invoice/:orderId/pdf
router.get('/accountant/invoice/:orderId/pdf', 
  authenticate,
  authorize(['admin', 'accountant']),
  AdminController.generateInvoicePDF
);

// Workshop Dashboard Routes
// GET /api/v1/admin/workshop/dashboard
router.get('/workshop/dashboard', 
  authenticate,
  authorize(['admin', 'supervisor', 'workshop_manager']),
  AdminController.getWorkshopDashboard
);

// POST /api/v1/admin/workshop/maintenance
router.post('/workshop/maintenance', 
  authenticate,
  authorize(['admin', 'supervisor', 'workshop_manager']),
  validateMaintenance,
  AdminController.scheduleMaintenance
);

// PATCH /api/v1/admin/workshop/maintenance/:id/complete
router.patch('/workshop/maintenance/:id/complete', 
  authenticate,
  authorize(['admin', 'supervisor', 'workshop_manager']),
  AdminController.completeMaintenance
);

// System Statistics
// GET /api/v1/admin/system/stats
router.get('/system/stats', 
  authenticate,
  authorize(['admin', 'supervisor']),
  AdminController.getSystemStats
);

// Additional Admin Routes
// GET /api/v1/admin/users
router.get('/users', 
  authenticate,
  authorize(['admin']),
  AdminController.getAllUsers
);

// GET /api/v1/admin/orders
router.get('/orders', 
  authenticate,
  authorize(['admin', 'supervisor']),
  AdminController.getAllOrders
);

// GET /api/v1/admin/drivers
router.get('/drivers', 
  authenticate,
  authorize(['admin', 'supervisor']),
  AdminController.getAllDrivers
);

// GET /api/v1/admin/trucks
router.get('/trucks', 
  authenticate,
  authorize(['admin', 'supervisor', 'workshop_manager']),
  AdminController.getAllTrucks
);

// GET /api/v1/admin/maintenance
router.get('/maintenance', 
  authenticate,
  authorize(['admin', 'supervisor', 'workshop_manager']),
  AdminController.getAllMaintenance
);

// POST /api/v1/admin/notifications/send
router.post('/notifications/send', 
  authenticate,
  authorize(['admin', 'supervisor']),
  AdminController.sendNotification
);

module.exports = router;
