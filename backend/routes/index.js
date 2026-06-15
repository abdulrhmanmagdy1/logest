/**
 * ============================================
 * 🛣️ Routes Index - نظام إدهام
 * Centralized routes export
 * ============================================
 */

const authRoutes = require('./auth');
const userRoutes = require('./users');
const shipmentRoutes = require('./shipments');
const truckRoutes = require('./trucks');
const driverRoutes = require('./drivers');
const invoiceRoutes = require('./invoices');
const trackingRoutes = require('./tracking');
const notificationRoutes = require('./notifications');
const surveyRoutes = require('./surveys');
const reportRoutes = require('./reports');
const uploadRoutes = require('./uploads');

module.exports = {
  authRoutes,
  userRoutes,
  shipmentRoutes,
  truckRoutes,
  driverRoutes,
  invoiceRoutes,
  trackingRoutes,
  notificationRoutes,
  surveyRoutes,
  reportRoutes,
  uploadRoutes
};
