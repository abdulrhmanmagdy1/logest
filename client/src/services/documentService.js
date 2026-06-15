import api from './api';
import logger from '../utils/logger';

/**
 * Document Service
 * Handles all document-related API calls
 */
const documentService = {
  /**
   * Get user documents
   * @param {Object} params - Query parameters
   * @returns {Promise} - API response
   */
  async getDocuments(params = {}) {
    try {
      const response = await api.get('/documents', { params });
      return response.data;
    } catch (error) {
      logger.error('Error fetching documents:', error);
      throw error;
    }
  },

  /**
   * Upload document
   * @param {File} file - Document file
   * @param {Object} metadata - Document metadata
   * @param {Function} onProgress - Progress callback
   * @returns {Promise} - API response
   */
  async uploadDocument(file, metadata = {}, onProgress = null) {
    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('metadata', JSON.stringify(metadata));

      const config = {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      };

      if (onProgress) {
        config.onUploadProgress = (progressEvent) => {
          const percentCompleted = Math.round(
            (progressEvent.loaded * 100) / progressEvent.total
          );
          onProgress(percentCompleted);
        };
      }

      const response = await api.post('/documents', formData, config);
      return response.data;
    } catch (error) {
      logger.error('Error uploading document:', error);
      throw error;
    }
  },

  /**
   * Download document
   * @param {string} documentId - Document ID
   * @returns {Promise} - API response
   */
  async downloadDocument(documentId) {
    try {
      const response = await api.get(`/documents/${documentId}/download`, {
        responseType: 'blob',
      });
      return response.data;
    } catch (error) {
      logger.error('Error downloading document:', error);
      throw error;
    }
  },

  /**
   * Delete document
   * @param {string} documentId - Document ID
   * @returns {Promise} - API response
   */
  async deleteDocument(documentId) {
    try {
      const response = await api.delete(`/documents/${documentId}`);
      return response.data;
    } catch (error) {
      logger.error('Error deleting document:', error);
      throw error;
    }
  },

  /**
   * Get document details
   * @param {string} documentId - Document ID
   * @returns {Promise} - API response
   */
  async getDocumentDetails(documentId) {
    try {
      const response = await api.get(`/documents/${documentId}`);
      return response.data;
    } catch (error) {
      logger.error('Error fetching document details:', error);
      throw error;
    }
  },

  /**
   * Update document metadata
   * @param {string} documentId - Document ID
   * @param {Object} metadata - Updated metadata
   * @returns {Promise} - API response
   */
  async updateMetadata(documentId, metadata) {
    try {
      const response = await api.patch(`/documents/${documentId}`, metadata);
      return response.data;
    } catch (error) {
      logger.error('Error updating document metadata:', error);
      throw error;
    }
  },

  /**
   * Get document categories
   * @returns {Promise} - API response
   */
  async getCategories() {
    try {
      const response = await api.get('/documents/categories');
      return response.data;
    } catch (error) {
      logger.error('Error fetching document categories:', error);
      throw error;
    }
  },

  /**
   * Verify document
   * @param {string} documentId - Document ID
   * @returns {Promise} - API response
   */
  async verifyDocument(documentId) {
    try {
      const response = await api.post(`/documents/${documentId}/verify`);
      return response.data;
    } catch (error) {
      logger.error('Error verifying document:', error);
      throw error;
    }
  },

  /**
   * Get document statistics
   * @returns {Promise} - API response
   */
  async getDocumentStats() {
    try {
      const response = await api.get('/documents/stats');
      return response.data;
    } catch (error) {
      logger.error('Error fetching document stats:', error);
      throw error;
    }
  },
};

export default documentService;
