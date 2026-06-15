/**
 * ============================================
 * 🔧 Utils Index - نظام إدهام
 * Edham Logistics - All Utilities Export
 * ============================================
 */

const logger = require('./logger');
const helpers = require('./helpers');
const { calculateDistance } = require('./distance');
const reports = require('./reports');

module.exports = {
  logger,
  helpers,
  calculateDistance,
  reports,
  
  // Export specific helper functions
  generateId: helpers.generateId,
  generateShipmentId: helpers.generateShipmentId,
  generateInvoiceId: helpers.generateInvoiceId,
  generateTripId: helpers.generateTripId,
  calculateHaversineDistance: helpers.calculateHaversineDistance,
  formatTime: helpers.formatTime,
  formatCurrency: helpers.formatCurrency,
  validateEmail: helpers.validateEmail,
  validatePhone: helpers.validatePhone,
  generateQRCode: helpers.generateQRCode,
  paginateResults: helpers.paginateResults
};
