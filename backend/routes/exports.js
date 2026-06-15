//
/**
 * ============================================
 * 📄 Export Routes - نقاط نهاية التصدير
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const exportService = require('../services/exportService');
const logger = require('../utils/logger');

// @route   POST /api/v1/exports/shipment-pdf/:shipmentId
// @desc    Export shipment as PDF
// @access  Private
router.post('/shipment-pdf/:shipmentId', protect, async (req, res) => {
  try {
    const { language = 'ar' } = req.body;
    
    const result = await exportService.generateShipmentPDF(
      req.params.shipmentId,
      language
    );

    res.json({
      success: true,
      data: result
    });

  } catch (error) {
    logger.error('Export shipment PDF error:', error);
    res.status(500).json({ success: false, message: error.message });
  }
});

// @route   POST /api/v1/exports/invoice-pdf/:invoiceId
// @desc    Export invoice as PDF
// @access  Private
router.post('/invoice-pdf/:invoiceId', protect, async (req, res) => {
  try {
    const result = await exportService.generateInvoicePDF(req.params.invoiceId);

    res.json({
      success: true,
      data: result
    });

  } catch (error) {
    logger.error('Export invoice PDF error:', error);
    res.status(500).json({ success: false, message: error.message });
  }
});

// @route   POST /api/v1/exports/excel
// @desc    Export data as Excel
// @access  Private
router.post('/excel', protect, async (req, res) => {
  try {
    const { reportType, data, options = {} } = req.body;

    if (!reportType || !data) {
      return res.status(400).json({
        success: false,
        message: 'Report type and data are required'
      });
    }

    const result = await exportService.generateExcelReport(
      reportType,
      data,
      options
    );

    res.json({
      success: true,
      data: result
    });

  } catch (error) {
    logger.error('Export Excel error:', error);
    res.status(500).json({ success: false, message: error.message });
  }
});

// @route   POST /api/v1/exports/analytics
// @desc    Export analytics report
// @access  Private
router.post('/analytics', protect, authorize(['admin', 'accountant', 'supervisor']), async (req, res) => {
  try {
    const { startDate, endDate, options = {} } = req.body;

    const result = await exportService.generateAnalyticsReport(
      new Date(startDate),
      new Date(endDate),
      options
    );

    res.json({
      success: true,
      data: result
    });

  } catch (error) {
    logger.error('Export analytics error:', error);
    res.status(500).json({ success: false, message: error.message });
  }
});

// @route   GET /api/v1/exports/download/:fileName
// @desc    Download exported file
// @access  Private
router.get('/download/:fileName', protect, async (req, res) => {
  try {
    const path = require('path');
    const fs = require('fs');
    
    const filePath = path.join(__dirname, '../exports', req.params.fileName);
    
    // Security check
    if (!filePath.startsWith(path.join(__dirname, '../exports'))) {
      return res.status(403).json({
        success: false,
        message: 'Access denied'
      });
    }

    if (!fs.existsSync(filePath)) {
      return res.status(404).json({
        success: false,
        message: 'File not found'
      });
    }

    res.sendFile(filePath);

  } catch (error) {
    logger.error('Download error:', error);
    res.status(500).json({ success: false, message: 'Download failed' });
  }
});

// @route   GET /api/v1/exports/list
// @desc    List available exports
// @access  Private
router.get('/list', protect, async (req, res) => {
  try {
    const fs = require('fs').promises;
    const path = require('path');
    
    const exportsDir = path.join(__dirname, '../exports');
    
    try {
      await fs.access(exportsDir);
    } catch {
      return res.json({
        success: true,
        files: []
      });
    }

    const files = await fs.readdir(exportsDir);
    const fileDetails = [];

    for (const file of files) {
      const stat = await fs.stat(path.join(exportsDir, file));
      fileDetails.push({
        name: file,
        size: stat.size,
        createdAt: stat.birthtime,
        url: `/api/v1/exports/download/${file}`
      });
    }

    // Sort by date (newest first)
    fileDetails.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

    res.json({
      success: true,
      count: fileDetails.length,
      files: fileDetails
    });

  } catch (error) {
    logger.error('List exports error:', error);
    res.status(500).json({ success: false, message: 'Failed to list exports' });
  }
});

module.exports = router;
