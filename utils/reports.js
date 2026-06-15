/**
 * ============================================
 * 📈 Reports Utility - نظام إدهام
 * Edham Logistics - Report Generation Utilities
 * ============================================
 * Helper functions for generating CSV, PDF, and Excel reports
 */

const logger = require('./logger');

/**
 * Convert data to CSV format
 * @param {Array} data - Array of objects
 * @param {Array} headers - Array of {key, label} objects
 * @returns {String} CSV string
 */
const toCSV = (data, headers) => {
  // Header row
  const headerRow = headers.map(h => h.label).join(',');
  
  // Data rows
  const rows = data.map(row => {
    return headers.map(h => {
      const value = row[h.key];
      // Escape values with commas or quotes
      if (typeof value === 'string' && (value.includes(',') || value.includes('"'))) {
        return `"${value.replace(/"/g, '""')}"`;
      }
      return value;
    }).join(',');
  });
  
  return [headerRow, ...rows].join('\n');
};

/**
 * Generate financial report data
 * @param {Array} invoices - Array of invoice objects
 * @param {String} period - Report period
 * @returns {Object} Formatted report data
 */
const generateFinancialReport = (invoices, period) => {
  const totalRevenue = invoices.reduce((sum, inv) => sum + (inv.amount || 0), 0);
  const totalPaid = invoices.reduce((sum, inv) => sum + (inv.paid || 0), 0);
  const totalBalance = totalRevenue - totalPaid;
  
  const byStatus = {
    paid: invoices.filter(inv => inv.status === 'paid').length,
    pending: invoices.filter(inv => inv.status === 'pending').length,
    overdue: invoices.filter(inv => inv.status === 'overdue').length
  };
  
  return {
    period,
    summary: {
      totalRevenue,
      totalPaid,
      totalBalance,
      invoiceCount: invoices.length
    },
    byStatus,
    details: invoices.map(inv => ({
      id: inv._id,
      invoiceNumber: inv.invoiceNumber,
      customer: inv.customer?.name || 'Unknown',
      amount: inv.amount,
      paid: inv.paid,
      balance: inv.amount - inv.paid,
      status: inv.status,
      date: inv.createdAt
    }))
  };
};

/**
 * Generate shipment report data
 * @param {Array} shipments - Array of shipment objects
 * @param {String} period - Report period
 * @returns {Object} Formatted report data
 */
const generateShipmentReport = (shipments, period) => {
  const byStatus = {
    pending: shipments.filter(s => s.status === 'pending').length,
    in_transit: shipments.filter(s => s.status === 'in_transit').length,
    delivered: shipments.filter(s => s.status === 'delivered').length,
    cancelled: shipments.filter(s => s.status === 'cancelled').length
  };
  
  const byCity = shipments.reduce((acc, s) => {
    const city = s.toCity || 'Unknown';
    acc[city] = (acc[city] || 0) + 1;
    return acc;
  }, {});
  
  return {
    period,
    summary: {
      total: shipments.length,
      byStatus,
      byCity
    },
    details: shipments.map(s => ({
      id: s._id,
      shipmentNumber: s.shipmentNumber,
      customer: s.customer?.name || 'Unknown',
      fromCity: s.fromCity,
      toCity: s.toCity,
      status: s.status,
      weight: s.weight,
      driver: s.driver?.name || 'Unassigned',
      createdAt: s.createdAt,
      deliveredAt: s.deliveredDate
    }))
  };
};

module.exports = {
  toCSV,
  generateFinancialReport,
  generateShipmentReport
};
