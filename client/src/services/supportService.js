import api from './api';
import logger from '../utils/logger';

/**
 * Support Service
 * Handles all customer support-related API calls
 */
const supportService = {
  /**
   * Create support ticket
   * @param {Object} ticketData - Ticket data
   * @returns {Promise} - API response
   */
  async createTicket(ticketData) {
    try {
      const response = await api.post('/support/tickets', ticketData);
      return response.data;
    } catch (error) {
      logger.error('Error creating support ticket:', error);
      throw error;
    }
  },

  /**
   * Get user tickets
   * @param {Object} params - Query parameters
   * @returns {Promise} - API response
   */
  async getTickets(params = {}) {
    try {
      const response = await api.get('/support/tickets', { params });
      return response.data;
    } catch (error) {
      logger.error('Error fetching tickets:', error);
      throw error;
    }
  },

  /**
   * Get ticket details
   * @param {string} ticketId - Ticket ID
   * @returns {Promise} - API response
   */
  async getTicketDetails(ticketId) {
    try {
      const response = await api.get(`/support/tickets/${ticketId}`);
      return response.data;
    } catch (error) {
      logger.error('Error fetching ticket details:', error);
      throw error;
    }
  },

  /**
   * Add message to ticket
   * @param {string} ticketId - Ticket ID
   * @param {Object} messageData - Message data
   * @returns {Promise} - API response
   */
  async addMessage(ticketId, messageData) {
    try {
      const response = await api.post(`/support/tickets/${ticketId}/messages`, messageData);
      return response.data;
    } catch (error) {
      logger.error('Error adding message to ticket:', error);
      throw error;
    }
  },

  /**
   * Close ticket
   * @param {string} ticketId - Ticket ID
   * @returns {Promise} - API response
   */
  async closeTicket(ticketId) {
    try {
      const response = await api.patch(`/support/tickets/${ticketId}/close`);
      return response.data;
    } catch (error) {
      logger.error('Error closing ticket:', error);
      throw error;
    }
  },

  /**
   * Reopen ticket
   * @param {string} ticketId - Ticket ID
   * @returns {Promise} - API response
   */
  async reopenTicket(ticketId) {
    try {
      const response = await api.patch(`/support/tickets/${ticketId}/reopen`);
      return response.data;
    } catch (error) {
      logger.error('Error reopening ticket:', error);
      throw error;
    }
  },

  /**
   * Get FAQ categories
   * @returns {Promise} - API response
   */
  async getFaqCategories() {
    try {
      const response = await api.get('/support/faq/categories');
      return response.data;
    } catch (error) {
      logger.error('Error fetching FAQ categories:', error);
      throw error;
    }
  },

  /**
   * Get FAQs by category
   * @param {string} categoryId - Category ID
   * @returns {Promise} - API response
   */
  async getFaqsByCategory(categoryId) {
    try {
      const response = await api.get(`/support/faq/categories/${categoryId}`);
      return response.data;
    } catch (error) {
      logger.error('Error fetching FAQs:', error);
      throw error;
    }
  },

  /**
   * Search FAQs
   * @param {string} query - Search query
   * @returns {Promise} - API response
   */
  async searchFaqs(query) {
    try {
      const response = await api.get('/support/faq/search', { params: { q: query } });
      return response.data;
    } catch (error) {
      logger.error('Error searching FAQs:', error);
      throw error;
    }
  },

  /**
   * Submit feedback
   * @param {Object} feedbackData - Feedback data
   * @returns {Promise} - API response
   */
  async submitFeedback(feedbackData) {
    try {
      const response = await api.post('/support/feedback', feedbackData);
      return response.data;
    } catch (error) {
      logger.error('Error submitting feedback:', error);
      throw error;
    }
  },

  /**
   * Get support statistics
   * @returns {Promise} - API response
   */
  async getSupportStats() {
    try {
      const response = await api.get('/support/stats');
      return response.data;
    } catch (error) {
      logger.error('Error fetching support stats:', error);
      throw error;
    }
  },

  /**
   * Rate support agent
   * @param {string} ticketId - Ticket ID
   * @param {Object} ratingData - Rating data
   * @returns {Promise} - API response
   */
  async rateSupport(ticketId, ratingData) {
    try {
      const response = await api.post(`/support/tickets/${ticketId}/rate`, ratingData);
      return response.data;
    } catch (error) {
      logger.error('Error rating support:', error);
      throw error;
    }
  },
};

export default supportService;
