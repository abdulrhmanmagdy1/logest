/**
 * ============================================
 * 🔩 Spare Parts Routes - نظام إدهام
 * Edham Logistics - Spare Parts Inventory API
 * ============================================
 */

const express = require('express');
const router = express.Router();
const SparePartController = require('../controllers/sparePartController');
const { auth, authorize } = require('../middleware/auth');
const { ROLES } = require('../config/constants');

// Get all spare parts
router.get('/', auth, SparePartController.getAll);

// Get low stock items
router.get('/low-stock', auth, authorize(ROLES.SUPERVISOR, ROLES.ADMIN), SparePartController.getLowStock);

// Create spare part
router.post('/', auth, authorize(ROLES.SUPERVISOR, ROLES.ADMIN), SparePartController.create);

// Get single spare part
router.get('/:id', auth, SparePartController.getById);

// Update spare part
router.put('/:id', auth, authorize(ROLES.SUPERVISOR, ROLES.ADMIN), SparePartController.update);

// Update stock quantity
router.patch('/:id/stock', auth, authorize(ROLES.SUPERVISOR, ROLES.ADMIN), SparePartController.updateStock);

// Delete spare part
router.delete('/:id', auth, authorize(ROLES.ADMIN), SparePartController.delete);

module.exports = router;
