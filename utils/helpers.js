/**
 * ============================================
 * 🛠️ Helpers Utility - نظام إدهام
 * Edham Logistics - Helper Functions
 * ============================================
 */

const crypto = require('crypto');

class Helpers {
  /**
   * Generate unique ID
   */
  static generateId(prefix = '') {
    const timestamp = Date.now().toString(36);
    const randomStr = crypto.randomBytes(8).toString('hex').substring(0, 8);
    return `${prefix}${timestamp}${randomStr}`.toUpperCase();
  }

  /**
   * Generate shipment number
   */
  static generateShipmentNumber() {
    return this.generateId('SHP-');
  }

  /**
   * Generate invoice number
   */
  static generateInvoiceNumber() {
    return this.generateId('INV-');
  }

  /**
   * Generate trip number
   */
  static generateTripNumber() {
    return this.generateId('TRP-');
  }

  /**
   * Generate maintenance number
   */
  static generateMaintenanceNumber() {
    return this.generateId('MNT-');
  }

  /**
   * Calculate distance between two points (Haversine formula)
   */
  static calculateDistance(lat1, lon1, lat2, lon2) {
    const R = 6371; // Radius of the Earth in km
    const dLat = (lat2 - lat1) * Math.PI / 180;
    const dLon = (lon2 - lon1) * Math.PI / 180;
    const a = 
      Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
      Math.sin(dLon / 2) * Math.sin(dLon / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return Math.round(R * c * 100) / 100; // Round to 2 decimal places
  }

  /**
   * Format time remaining in human readable format (Arabic)
   */
  static formatTimeRemaining(milliseconds) {
    const seconds = Math.floor(milliseconds / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (days > 0) return `${days} يوم و ${hours % 24} ساعة`;
    if (hours > 0) return `${hours} ساعة و ${minutes % 60} دقيقة`;
    if (minutes > 0) return `${minutes} دقيقة و ${seconds % 60} ثانية`;
    return `${seconds} ثانية`;
  }

  /**
   * Validate email format
   */
  static isValidEmail(email) {
    const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return regex.test(email);
  }

  /**
   * Validate phone number (Saudi Arabia)
   */
  static isValidPhone(phone) {
    const regex = /^(\+966|0)(5|9)[0-9]{8}$/;
    return regex.test(phone);
  }

  /**
   * Generate random number between min and max
   */
  static randomBetween(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
  }

  /**
   * Format date in Arabic
   */
  static formatDateArabic(date) {
    const options = {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      locale: 'ar-SA'
    };
    return new Date(date).toLocaleDateString('ar-SA', options);
  }

  /**
   * Calculate percentage
   */
  static calculatePercentage(value, total) {
    if (total === 0) return 0;
    return Math.round((value / total) * 100);
  }

  /**
   * Generate QR Code URL
   */
  static generateQRCodeURL(text, size = 200) {
    return `https://api.qrserver.com/v1/create-qr-code/?size=${size}x${size}&data=${encodeURIComponent(text)}`;
  }
}

module.exports = Helpers;
