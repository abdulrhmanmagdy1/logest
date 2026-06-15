/**
 * ============================================
 * File Upload Routes — Azure Blob Storage
 * No local filesystem writes; memory → Azure.
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect } = require('../middleware/auth');
const { azureUpload } = require('../middleware/azureUpload');
const logger = require('../utils/logger');

// @route   POST /api/v1/uploads/image
// @desc    Upload a single image to Azure Blob Storage
// @access  Private
router.post('/image', protect, ...azureUpload.single('image'), async (req, res) => {
  try {
    if (!req.azureFile) {
      return res.status(400).json({ success: false, message: 'No file uploaded' });
    }

    logger.info(`Image uploaded to Azure Blob: ${req.azureFile.blobName} by user ${req.user.id}`);

    return res.json({
      success: true,
      message: 'Image uploaded successfully',
      data: {
        blobName: req.azureFile.blobName,
        url: req.azureFile.url,
        size: req.azureFile.size,
        mimetype: req.azureFile.mimetype,
      },
    });
  } catch (error) {
    logger.error('Upload image route error:', error.message);
    return res.status(500).json({ success: false, message: 'Upload failed' });
  }
});

// @route   POST /api/v1/uploads/document
// @desc    Upload a single document (PDF) to Azure Blob Storage
// @access  Private
router.post('/document', protect, ...azureUpload.single('document'), async (req, res) => {
  try {
    if (!req.azureFile) {
      return res.status(400).json({ success: false, message: 'No file uploaded' });
    }

    logger.info(`Document uploaded to Azure Blob: ${req.azureFile.blobName} by user ${req.user.id}`);

    return res.json({
      success: true,
      message: 'Document uploaded successfully',
      data: {
        blobName: req.azureFile.blobName,
        url: req.azureFile.url,
        size: req.azureFile.size,
        mimetype: req.azureFile.mimetype,
      },
    });
  } catch (error) {
    logger.error('Upload document route error:', error.message);
    return res.status(500).json({ success: false, message: 'Upload failed' });
  }
});

// @route   POST /api/v1/uploads/multiple
// @desc    Upload multiple files (max 10) to Azure Blob Storage
// @access  Private
router.post('/multiple', protect, ...azureUpload.array('files', 10), async (req, res) => {
  try {
    if (!req.azureFiles || req.azureFiles.length === 0) {
      return res.status(400).json({ success: false, message: 'No files uploaded' });
    }

    logger.info(`${req.azureFiles.length} file(s) uploaded to Azure Blob by user ${req.user.id}`);

    return res.json({
      success: true,
      message: `${req.azureFiles.length} file(s) uploaded successfully`,
      data: { files: req.azureFiles },
    });
  } catch (error) {
    logger.error('Upload multiple route error:', error.message);
    return res.status(500).json({ success: false, message: 'Upload failed' });
  }
});

module.exports = router;
