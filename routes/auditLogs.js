/**
 * ============================================
 * 📋 Audit Logs Routes - نظام إدهام
 * Edham Logistics - Audit Trail API
 * ============================================
 */

const express = require('express');
const router = express.Router();
const AuditLogController = require('../controllers/auditLogController');
const { auth, authorize } = require('../middleware/auth');
const { ROLES } = require('../config/constants');

// Get all audit logs with filters
router.get('/', auth, authorize(ROLES.ADMIN, ROLES.SUPERVISOR), AuditLogController.getAll);

// Get audit logs statistics
router.get('/stats', auth, authorize(ROLES.ADMIN, ROLES.SUPERVISOR), AuditLogController.getStats);

// Export audit logs
router.get('/export', auth, authorize(ROLES.ADMIN), AuditLogController.export);

// Get entity history
router.get('/entity/:entity/:entityId', auth, authorize(ROLES.ADMIN, ROLES.SUPERVISOR), AuditLogController.getEntityHistory);

// Create audit log
router.post('/', auth, AuditLogController.create);

module.exports = router;
