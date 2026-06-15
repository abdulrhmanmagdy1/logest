//
/**
 * ============================================
 * 📦 Data Migration Routes - استيراد وتصدير البيانات
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { DataImport, DataExport, DataSync } = require('../models/DataMigration');
const logger = require('../utils/logger');

// Data Import
// @route   GET /api/v1/migration/imports
// @desc    Get import jobs
// @access  Private (Admin)
router.get('/imports', protect, authorize(['admin']), async (req, res) => {
  try {
    const { status, type } = req.query;

    let query = { company: req.user.company };
    if (status) query.status = status;
    if (type) query.type = type;

    const imports = await DataImport.find(query)
      .populate('initiatedBy', 'firstName lastName')
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: imports.length,
      data: imports
    });

  } catch (error) {
    logger.error('Get imports error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/migration/imports
// @desc    Create import job
// @access  Private (Admin)
router.post('/imports', protect, authorize(['admin']), async (req, res) => {
  try {
    const importId = `IMP-${Date.now()}`;

    const job = await DataImport.create({
      ...req.body,
      importId,
      initiatedBy: req.user.id,
      company: req.user.company,
      status: 'pending'
    });

    // TODO: Start import processing

    res.status(201).json({
      success: true,
      message: 'Import job created',
      data: job
    });

  } catch (error) {
    logger.error('Create import error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/migration/imports/:id
// @desc    Get import details
// @access  Private (Admin)
router.get('/imports/:id', protect, authorize(['admin']), async (req, res) => {
  try {
    const job = await DataImport.findOne({
      _id: req.params.id,
      company: req.user.company
    }).populate('initiatedBy', 'firstName lastName');

    if (!job) {
      return res.status(404).json({ success: false, message: 'Import not found' });
    }

    res.json({
      success: true,
      data: job
    });

  } catch (error) {
    logger.error('Get import error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/migration/imports/:id/validate
// @desc    Validate import file
// @access  Private (Admin)
router.post('/imports/:id/validate', protect, authorize(['admin']), async (req, res) => {
  try {
    const job = await DataImport.findOneAndUpdate(
      { _id: req.params.id, company: req.user.company },
      { status: 'validating' },
      { new: true }
    );

    if (!job) {
      return res.status(404).json({ success: false, message: 'Import not found' });
    }

    // TODO: Perform validation

    res.json({
      success: true,
      message: 'Validation started',
      data: job
    });

  } catch (error) {
    logger.error('Validate import error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Data Export
// @route   GET /api/v1/migration/exports
// @desc    Get export jobs
// @access  Private
router.get('/exports', protect, async (req, res) => {
  try {
    const { status, type } = req.query;

    let query = { company: req.user.company };
    if (status) query.status = status;
    if (type) query.type = type;

    const exports = await DataExport.find(query)
      .populate('initiatedBy', 'firstName lastName')
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: exports.length,
      data: exports
    });

  } catch (error) {
    logger.error('Get exports error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/migration/exports
// @desc    Create export job
// @access  Private
router.post('/exports', protect, async (req, res) => {
  try {
    const exportId = `EXP-${Date.now()}`;

    const job = await DataExport.create({
      ...req.body,
      exportId,
      initiatedBy: req.user.id,
      company: req.user.company,
      status: 'pending'
    });

    // TODO: Start export processing

    res.status(201).json({
      success: true,
      message: 'Export job created',
      data: job
    });

  } catch (error) {
    logger.error('Create export error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/migration/exports/:id/download
// @desc    Download export file
// @access  Private
router.get('/exports/:id/download', protect, async (req, res) => {
  try {
    const job = await DataExport.findOne({
      _id: req.params.id,
      company: req.user.company,
      status: 'completed'
    });

    if (!job) {
      return res.status(404).json({ success: false, message: 'Export not found or not completed' });
    }

    if (!job.result?.path) {
      return res.status(404).json({ success: false, message: 'Export file not found' });
    }

    // TODO: Stream file
    res.json({
      success: true,
      downloadUrl: job.result.url
    });

  } catch (error) {
    logger.error('Download export error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Data Sync
// @route   GET /api/v1/migration/syncs
// @desc    Get data sync configurations
// @access  Private (Admin)
router.get('/syncs', protect, authorize(['admin']), async (req, res) => {
  try {
    const syncs = await DataSync.find({ company: req.user.company })
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: syncs.length,
      data: syncs
    });

  } catch (error) {
    logger.error('Get syncs error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/migration/syncs
// @desc    Create data sync
// @access  Private (Admin)
router.post('/syncs', protect, authorize(['admin']), async (req, res) => {
  try {
    const syncId = `SYNC-${Date.now()}`;

    const sync = await DataSync.create({
      ...req.body,
      syncId,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: sync
    });

  } catch (error) {
    logger.error('Create sync error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/migration/syncs/:id/run
// @desc    Run data sync
// @access  Private (Admin)
router.post('/syncs/:id/run', protect, authorize(['admin']), async (req, res) => {
  try {
    const sync = await DataSync.findOneAndUpdate(
      { _id: req.params.id, company: req.user.company },
      {
        'lastRun.startedAt': new Date(),
        'lastRun.status': 'running'
      },
      { new: true }
    );

    if (!sync) {
      return res.status(404).json({ success: false, message: 'Sync not found' });
    }

    // TODO: Trigger sync job

    res.json({
      success: true,
      message: 'Sync started',
      data: sync
    });

  } catch (error) {
    logger.error('Run sync error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Templates
// @route   GET /api/v1/migration/templates
// @desc    Get import templates
// @access  Private
router.get('/templates', protect, async (req, res) => {
  try {
    const templates = [
      {
        type: 'shipments',
        name: 'Shipments Import',
        description: 'Import shipment data',
        requiredFields: ['origin', 'destination', 'weight'],
        optionalFields: ['dimensions', 'value', 'description'],
        sampleFile: '/templates/shipments_import.csv'
      },
      {
        type: 'customers',
        name: 'Customers Import',
        description: 'Import customer data',
        requiredFields: ['email', 'firstName', 'lastName'],
        optionalFields: ['phone', 'company', 'address'],
        sampleFile: '/templates/customers_import.csv'
      },
      {
        type: 'drivers',
        name: 'Drivers Import',
        description: 'Import driver data',
        requiredFields: ['email', 'firstName', 'lastName', 'phone'],
        optionalFields: ['licenseNumber', 'vehicleType', 'zone'],
        sampleFile: '/templates/drivers_import.csv'
      },
      {
        type: 'vehicles',
        name: 'Vehicles Import',
        description: 'Import vehicle data',
        requiredFields: ['licensePlate', 'type', 'capacity'],
        optionalFields: ['make', 'model', 'year', 'fuelType'],
        sampleFile: '/templates/vehicles_import.csv'
      }
    ];

    res.json({
      success: true,
      data: templates
    });

  } catch (error) {
    logger.error('Get templates error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
