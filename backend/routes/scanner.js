//
/**
 * ============================================
 * 📱 Scanner Routes - نقاط نهاية الماسح الضوئي
 * Barcode & QR Code Management
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const QRCode = require('qrcode');
const Barcode = require('jsbarcode');
const { createCanvas } = require('canvas');
const logger = require('../utils/logger');

// @route   POST /api/v1/scanner/generate-qr
// @desc    Generate QR code for shipment
// @access  Private
router.post('/generate-qr', protect, async (req, res) => {
  try {
    const { shipmentId, type = 'shipment' } = req.body;

    let data;
    let label;

    switch (type) {
      case 'shipment':
        const Shipment = require('../models/Shipment');
        const shipment = await Shipment.findById(shipmentId);
        if (!shipment) {
          return res.status(404).json({
            success: false,
            message: 'Shipment not found'
          });
        }
        data = JSON.stringify({
          type: 'shipment',
          id: shipment._id,
          trackingNumber: shipment.trackingNumber
        });
        label = shipment.trackingNumber;
        break;

      case 'invoice':
        const Invoice = require('../models/Invoice');
        const invoice = await Invoice.findById(shipmentId);
        if (!invoice) {
          return res.status(404).json({
            success: false,
            message: 'Invoice not found'
          });
        }
        data = JSON.stringify({
          type: 'invoice',
          id: invoice._id,
          invoiceNumber: invoice.invoiceNumber
        });
        label = invoice.invoiceNumber;
        break;

      default:
        data = shipmentId;
        label = 'QR Code';
    }

    // Generate QR code
    const qrDataUrl = await QRCode.toDataURL(data, {
      width: 400,
      margin: 2,
      color: {
        dark: '#1a1a2e',
        light: '#ffffff'
      }
    });

    res.json({
      success: true,
      data: {
        qrCode: qrDataUrl,
        label,
        type,
        generatedAt: new Date()
      }
    });

  } catch (error) {
    logger.error('Generate QR error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/scanner/generate-barcode
// @desc    Generate barcode (Code 128)
// @access  Private (Driver, Supervisor)
router.post('/generate-barcode', protect, authorize(['driver', 'supervisor', 'admin']), async (req, res) => {
  try {
    const { trackingNumber } = req.body;

    // Create canvas
    const canvas = createCanvas(300, 100);
    
    // Generate barcode
    Barcode(canvas, trackingNumber, {
      format: 'CODE128',
      width: 2,
      height: 60,
      displayValue: true,
      fontSize: 16,
      textMargin: 10
    });

    const barcodeDataUrl = canvas.toDataURL('image/png');

    res.json({
      success: true,
      data: {
        barcode: barcodeDataUrl,
        trackingNumber,
        format: 'CODE128',
        generatedAt: new Date()
      }
    });

  } catch (error) {
    logger.error('Generate barcode error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/scanner/scan
// @desc    Process scanned code
// @access  Private (Driver, Supervisor)
router.post('/scan', protect, authorize(['driver', 'supervisor', 'admin']), async (req, res) => {
  try {
    const { code, type, location } = req.body;

    let result;
    let parsedData;

    try {
      parsedData = JSON.parse(code);
    } catch (e) {
      // Not JSON, treat as tracking number
      parsedData = { type: 'tracking', value: code };
    }

    switch (parsedData.type) {
      case 'shipment':
        result = await processShipmentScan(parsedData, req.user, location);
        break;

      case 'invoice':
        result = await processInvoiceScan(parsedData, req.user);
        break;

      case 'tracking':
        result = await processTrackingScan(parsedData.value, req.user, location);
        break;

      default:
        return res.status(400).json({
          success: false,
          message: 'Unknown code type'
        });
    }

    res.json({
      success: true,
      data: result
    });

  } catch (error) {
    logger.error('Scan processing error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/scanner/verify
// @desc    Verify scanned code without updating status
// @access  Private
router.post('/verify', protect, async (req, res) => {
  try {
    const { code } = req.body;

    let parsedData;
    try {
      parsedData = JSON.parse(code);
    } catch (e) {
      parsedData = { type: 'tracking', value: code };
    }

    let entity;

    if (parsedData.type === 'shipment') {
      const Shipment = require('../models/Shipment');
      entity = await Shipment.findById(parsedData.id)
        .select('trackingNumber status pickup delivery driver cargo');
    } else if (parsedData.type === 'tracking') {
      const Shipment = require('../models/Shipment');
      entity = await Shipment.findOne({ trackingNumber: parsedData.value })
        .select('trackingNumber status pickup delivery driver cargo');
    }

    if (!entity) {
      return res.status(404).json({
        success: false,
        message: 'Not found'
      });
    }

    res.json({
      success: true,
      data: {
        valid: true,
        entity,
        type: parsedData.type
      }
    });

  } catch (error) {
    logger.error('Verify code error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/scanner/bulk-scan
// @desc    Process multiple scans (for loading/unloading)
// @access  Private (Driver, Supervisor)
router.post('/bulk-scan', protect, authorize(['driver', 'supervisor', 'admin']), async (req, res) => {
  try {
    const { codes, operation, location } = req.body;

    const results = [];
    const errors = [];

    for (const code of codes) {
      try {
        const result = await processTrackingScan(code, req.user, location);
        results.push(result);
      } catch (error) {
        errors.push({ code, error: error.message });
      }
    }

    // Create bulk operation record
    const BulkScan = require('../models/BulkScan');
    await BulkScan.create({
      operation,
      driver: req.user.id,
      location,
      totalScanned: codes.length,
      successful: results.length,
      failed: errors.length,
      shipments: results.map(r => r.shipmentId),
      createdAt: new Date()
    });

    res.json({
      success: true,
      data: {
        total: codes.length,
        successful: results.length,
        failed: errors.length,
        results,
        errors
      }
    });

  } catch (error) {
    logger.error('Bulk scan error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/scanner/history
// @desc    Get scan history
// @access  Private
router.get('/history', protect, async (req, res) => {
  try {
    const { limit = 20, page = 1 } = req.query;

    const ScanLog = require('../models/ScanLog');
    const logs = await ScanLog.find({ scanner: req.user.id })
      .populate('shipment', 'trackingNumber status')
      .sort({ createdAt: -1 })
      .limit(limit * 1)
      .skip((page - 1) * limit);

    const total = await ScanLog.countDocuments({ scanner: req.user.id });

    res.json({
      success: true,
      count: logs.length,
      total,
      page: parseInt(page),
      pages: Math.ceil(total / limit),
      data: logs
    });

  } catch (error) {
    logger.error('Get scan history error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Helper functions
async function processShipmentScan(data, user, location) {
  const Shipment = require('../models/Shipment');
  const ScanLog = require('../models/ScanLog');

  const shipment = await Shipment.findById(data.id);

  if (!shipment) {
    throw new Error('Shipment not found');
  }

  // Log the scan
  await ScanLog.create({
    type: 'shipment',
    referenceId: shipment._id,
    scanner: user.id,
    location,
    status: shipment.status,
    createdAt: new Date()
  });

  return {
    type: 'shipment',
    shipmentId: shipment._id,
    trackingNumber: shipment.trackingNumber,
    status: shipment.status,
    action: 'verified'
  };
}

async function processInvoiceScan(data, user) {
  const Invoice = require('../models/Invoice');

  const invoice = await Invoice.findById(data.id);

  if (!invoice) {
    throw new Error('Invoice not found');
  }

  return {
    type: 'invoice',
    invoiceId: invoice._id,
    invoiceNumber: invoice.invoiceNumber,
    status: invoice.status,
    amount: invoice.total
  };
}

async function processTrackingScan(trackingNumber, user, location) {
  const Shipment = require('../models/Shipment');
  const ScanLog = require('../models/ScanLog');

  const shipment = await Shipment.findOne({ trackingNumber });

  if (!shipment) {
    throw new Error('Shipment not found');
  }

  // Determine action based on current status
  let action = 'verified';
  let newStatus = null;

  if (user.role === 'driver' && shipment.status === 'ready_for_pickup') {
    newStatus = 'picked_up';
    action = 'picked_up';
  } else if (user.role === 'driver' && shipment.status === 'in_transit') {
    newStatus = 'out_for_delivery';
    action = 'out_for_delivery';
  } else if (shipment.status === 'out_for_delivery') {
    newStatus = 'delivered';
    action = 'delivered';
  }

  // Update shipment if needed
  if (newStatus) {
    shipment.status = newStatus;
    shipment.statusHistory.push({
      status: newStatus,
      timestamp: new Date(),
      updatedBy: user.id,
      notes: `Updated via scan by ${user.firstName} ${user.lastName}`,
      location
    });
    await shipment.save();
  }

  // Log the scan
  await ScanLog.create({
    type: 'shipment',
    referenceId: shipment._id,
    scanner: user.id,
    location,
    status: shipment.status,
    action,
    createdAt: new Date()
  });

  return {
    type: 'shipment',
    shipmentId: shipment._id,
    trackingNumber: shipment.trackingNumber,
    previousStatus: shipment.statusHistory[shipment.statusHistory.length - 2]?.status || 'unknown',
    currentStatus: shipment.status,
    action
  };
}

module.exports = router;
