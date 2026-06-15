/**
 * ============================================
 * 👤 Users Routes - نظام إدهام
 * Edham Logistics - User Management API
 * ============================================
 */

const express = require('express');
const router = express.Router();
const UserController = require('../controllers/userController');
const { auth, authorize } = require('../middleware/auth');
const { ROLES } = require('../config/constants');

// Get all users
router.get('/', auth, authorize(ROLES.ADMIN, ROLES.SUPERVISOR), UserController.getAll);

// Get users by role
router.get('/role/:role', auth, authorize(ROLES.ADMIN, ROLES.SUPERVISOR), UserController.getByRole);

// Get single user
router.get('/:id', auth, UserController.getById);

// Update user
router.put('/:id', auth, UserController.update);

// Delete user (Soft Delete)
router.delete('/:id', auth, authorize(ROLES.ADMIN), UserController.delete);

module.exports = router;
