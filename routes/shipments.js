/**
 * ============================================
 * 📦 Shipments Routes - نظام إدهام
 * Edham Logistics - Shipment API with Controllers
 * ============================================
 */

const express = require('express');
const router = express.Router();
const ShipmentController = require('../controllers/shipmentController');
const { auth, authorize } = require('../middleware/auth');
const { validateShipment } = require('../middleware/validation');
const { ROLES } = require('../config/constants');

// Get statistics
router.get('/statistics', auth, ShipmentController.getStatistics);

// Get all shipments
router.get('/', auth, ShipmentController.getAll);

// Get single shipment
router.get('/:id', auth, ShipmentController.getById);

// Create shipment
router.post('/',
  auth,
  authorize(ROLES.CLIENT, ROLES.SUPERVISOR, ROLES.ADMIN),
  validateShipment,
  ShipmentController.create
);

// Update shipment
router.put('/:id',
  auth,
  authorize(ROLES.SUPERVISOR, ROLES.ADMIN),
  ShipmentController.update
);

// Update shipment status
router.patch('/:id/status',
  auth,
  ShipmentController.updateStatus
);

// Delete shipment
router.delete('/:id',
  auth,
  authorize(ROLES.ADMIN),
  ShipmentController.delete
);

module.exports = router;
