/**
 * ============================================
 * 📦 Models Index - نظام إدهام
 * Centralized models export
 * ============================================
 */

const User = require('./User');
const Shipment = require('./Shipment');
const Truck = require('./Truck');
const Invoice = require('./Invoice');
const Receipt = require('./Receipt');
const Notification = require('./Notification');
const Survey = require('./Survey');

module.exports = {
  User,
  Shipment,
  Truck,
  Invoice,
  Receipt,
  Notification,
  Survey
};
