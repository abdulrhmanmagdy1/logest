import api from './api';
import logger from '../utils/logger';

/**
 * Notification Service
 * Handles all notification-related API calls
 */
const notificationService = {
  /**
   * Get user notifications
   * @param {Object} params - Query parameters
   * @returns {Promise} - API response
   */
  async getNotifications(params = {}) {
    try {
      const response = await api.get('/notifications', { params });
      return response.data;
    } catch (error) {
      logger.error('Error fetching notifications:', error);
      throw error;
    }
  },

  /**
   * Mark notification as read
   * @param {string} notificationId - Notification ID
   * @returns {Promise} - API response
   */
  async markAsRead(notificationId) {
    try {
      const response = await api.patch(`/notifications/${notificationId}/read`);
      return response.data;
    } catch (error) {
      logger.error('Error marking notification as read:', error);
      throw error;
    }
  },

  /**
   * Mark all notifications as read
   * @returns {Promise} - API response
   */
  async markAllAsRead() {
    try {
      const response = await api.patch('/notifications/read-all');
      return response.data;
    } catch (error) {
      logger.error('Error marking all notifications as read:', error);
      throw error;
    }
  },

  /**
   * Delete notification
   * @param {string} notificationId - Notification ID
   * @returns {Promise} - API response
   */
  async deleteNotification(notificationId) {
    try {
      const response = await api.delete(`/notifications/${notificationId}`);
      return response.data;
    } catch (error) {
      logger.error('Error deleting notification:', error);
      throw error;
    }
  },

  /**
   * Get unread notification count
   * @returns {Promise} - API response
   */
  async getUnreadCount() {
    try {
      const response = await api.get('/notifications/unread-count');
      return response.data;
    } catch (error) {
      logger.error('Error fetching unread count:', error);
      throw error;
    }
  },

  /**
   * Update FCM token for push notifications
   * @param {string} token - FCM token
   * @returns {Promise} - API response
   */
  async updateFcmToken(token) {
    try {
      const response = await api.post('/notifications/fcm-token', { token });
      return response.data;
    } catch (error) {
      logger.error('Error updating FCM token:', error);
      throw error;
    }
  },

  /**
   * Subscribe to notification preferences
   * @param {Object} preferences - Notification preferences
   * @returns {Promise} - API response
   */
  async updatePreferences(preferences) {
    try {
      const response = await api.patch('/notifications/preferences', preferences);
      return response.data;
    } catch (error) {
      logger.error('Error updating notification preferences:', error);
      throw error;
    }
  },

  /**
   * Get notification preferences
   * @returns {Promise} - API response
   */
  async getPreferences() {
    try {
      const response = await api.get('/notifications/preferences');
      return response.data;
    } catch (error) {
      logger.error('Error fetching notification preferences:', error);
      throw error;
    }
  },
};

export default notificationService;
