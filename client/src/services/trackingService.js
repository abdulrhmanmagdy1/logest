import api from './api';
import logger from '../utils/logger';

/**
 * Tracking Service
 * Handles all shipment tracking and location-related API calls
 */
const trackingService = {
  /**
   * Track shipment by tracking number
   * @param {string} trackingNumber - Shipment tracking number
   * @returns {Promise} - API response
   */
  async trackShipment(trackingNumber) {
    try {
      const response = await api.get(`/tracking/${trackingNumber}`);
      return response.data;
    } catch (error) {
      logger.error('Error tracking shipment:', error);
      throw error;
    }
  },

  /**
   * Get real-time shipment location
   * @param {string} shipmentId - Shipment ID
   * @returns {Promise} - API response
   */
  async getRealtimeLocation(shipmentId) {
    try {
      const response = await api.get(`/tracking/${shipmentId}/realtime`);
      return response.data;
    } catch (error) {
      logger.error('Error getting realtime location:', error);
      throw error;
    }
  },

  /**
   * Get shipment tracking history
   * @param {string} shipmentId - Shipment ID
   * @returns {Promise} - API response
   */
  async getTrackingHistory(shipmentId) {
    try {
      const response = await api.get(`/tracking/${shipmentId}/history`);
      return response.data;
    } catch (error) {
      logger.error('Error fetching tracking history:', error);
      throw error;
    }
  },

  /**
   * Get estimated delivery time
   * @param {string} shipmentId - Shipment ID
   * @returns {Promise} - API response
   */
  async getEstimatedDelivery(shipmentId) {
    try {
      const response = await api.get(`/tracking/${shipmentId}/eta`);
      return response.data;
    } catch (error) {
      logger.error('Error fetching estimated delivery:', error);
      throw error;
    }
  },

  /**
   * Get route details
   * @param {string} shipmentId - Shipment ID
   * @returns {Promise} - API response
   */
  async getRouteDetails(shipmentId) {
    try {
      const response = await api.get(`/tracking/${shipmentId}/route`);
      return response.data;
    } catch (error) {
      logger.error('Error fetching route details:', error);
      throw error;
    }
  },

  /**
   * Subscribe to shipment updates
   * @param {string} shipmentId - Shipment ID
   * @param {Function} callback - Callback function for updates
   * @returns {Function} - Unsubscribe function
   */
  subscribeToUpdates(shipmentId, callback) {
    // This would typically use WebSocket or Socket.io
    // For now, we'll use polling as a fallback
    const intervalId = setInterval(async () => {
      try {
        const data = await this.getRealtimeLocation(shipmentId);
        callback(data);
      } catch (error) {
        logger.error('Error in tracking subscription:', error);
      }
    }, 30000); // Poll every 30 seconds

    // Return unsubscribe function
    return () => clearInterval(intervalId);
  },

  /**
   * Share tracking link
   * @param {string} shipmentId - Shipment ID
   * @returns {Promise} - API response with shareable link
   */
  async generateShareableLink(shipmentId) {
    try {
      const response = await api.post(`/tracking/${shipmentId}/share`);
      return response.data;
    } catch (error) {
      logger.error('Error generating shareable link:', error);
      throw error;
    }
  },

  /**
   * Get tracking statistics
   * @param {Object} params - Query parameters
   * @returns {Promise} - API response
   */
  async getTrackingStats(params = {}) {
    try {
      const response = await api.get('/tracking/stats', { params });
      return response.data;
    } catch (error) {
      logger.error('Error fetching tracking stats:', error);
      throw error;
    }
  },
};

export default trackingService;
