/**
 * ============================================
 * 🔐 Authentication Routes - نظام إدهام
 * Edham Logistics - Auth API with Controllers
 * ============================================
 */

const express = require('express');
const router = express.Router();
const AuthController = require('../controllers/authController');
const { auth, authorize } = require('../middleware/auth');
const { validateUser, validateLogin } = require('../middleware/validation');
const { authLimiter } = require('../middleware/rateLimiter');
const { ROLES } = require('../config/constants');

// Public routes with rate limiting
router.post('/register', authLimiter, validateUser, AuthController.register);
router.post('/login', authLimiter, validateLogin, AuthController.login);

// Protected routes
router.get('/me', auth, AuthController.getCurrentUser);
router.put('/profile', auth, AuthController.updateProfile);
router.post('/change-password', auth, AuthController.changePassword);

// Admin routes
router.post('/create-user', auth, authorize(ROLES.ADMIN, ROLES.SUPERVISOR), validateUser, AuthController.register);

module.exports = router;
