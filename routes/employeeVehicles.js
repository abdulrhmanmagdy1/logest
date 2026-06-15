/**
 * ============================================
 * 🚗 Employee Vehicles Routes - نظام إدهام
 * Edham Logistics - Employee Vehicle Management API
 * ============================================
 */

const express = require('express');
const router = express.Router();
const EmployeeVehicleController = require('../controllers/employeeVehicleController');
const { auth, authorize } = require('../middleware/auth');
const { ROLES } = require('../config/constants');

// Get all vehicles
router.get('/', auth, EmployeeVehicleController.getAll);

// Get vehicles by employee
router.get('/employee/:employeeId', auth, EmployeeVehicleController.getByEmployee);

// Create new vehicle (Admin/Supervisor)
router.post('/', auth, authorize(ROLES.ADMIN, ROLES.SUPERVISOR), EmployeeVehicleController.create);

// Employee registers their own vehicle
router.post('/register', auth, EmployeeVehicleController.register);

// Update vehicle
router.put('/:id', auth, EmployeeVehicleController.update);

// Update vehicle location
router.patch('/:id/location', auth, EmployeeVehicleController.updateLocation);

// Update vehicle status
router.patch('/:id/status', auth, authorize(ROLES.ADMIN, ROLES.SUPERVISOR), EmployeeVehicleController.updateStatus);

// Get single vehicle
router.get('/:id', auth, EmployeeVehicleController.getById);

// Delete vehicle
router.delete('/:id', auth, authorize(ROLES.ADMIN), EmployeeVehicleController.delete);

module.exports = router;
