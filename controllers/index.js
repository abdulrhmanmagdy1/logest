/**
 * ============================================
 * 📦 Controllers Index - نظام إدهام
 * Edham Logistics - All Controllers Export
 * ============================================
 */

const BaseController = require('./baseController');
const AuthController = require('./authController');
const UserController = require('./userController');
const ShipmentController = require('./shipmentController');
const TruckController = require('./truckController');
const TripController = require('./tripController');
const InvoiceController = require('./invoiceController');
const PaymentController = require('./paymentController');
const MaintenanceController = require('./maintenanceController');
const OilScheduleController = require('./oilScheduleController');
const SparePartController = require('./sparePartController');
const LocationController = require('./locationController');
const AuditLogController = require('./auditLogController');
const AnalyticsController = require('./analyticsController');
const EmployeeVehicleController = require('./employeeVehicleController');
const SurveyController = require('./surveyController');

module.exports = {
  BaseController,
  AuthController,
  UserController,
  ShipmentController,
  TruckController,
  TripController,
  InvoiceController,
  PaymentController,
  MaintenanceController,
  OilScheduleController,
  SparePartController,
  LocationController,
  AuditLogController,
  AnalyticsController,
  EmployeeVehicleController,
  SurveyController
};
