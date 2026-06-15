//
/**
 * ============================================
 * 🏥 Health Check Routes - مراقبة صحة النظام
 * ============================================
 */

const express = require('express');
const router = express.Router();
const mongoose = require('mongoose');
const os = require('os');
const logger = require('../utils/logger');

// @route   GET /api/v1/health
// @desc    Basic health check
// @access  Public
router.get('/', async (req, res) => {
  try {
    // Check database connection
    const dbState = mongoose.connection.readyState;
    const dbStatus = dbState === 1 ? 'connected' : 'disconnected';
    
    // Memory usage
    const memUsage = process.memoryUsage();
    const systemMem = {
      total: os.totalmem(),
      free: os.freemem(),
      used: os.totalmem() - os.freemem()
    };
    
    // Uptime
    const uptime = process.uptime();
    
    res.json({
      success: true,
      status: 'healthy',
      timestamp: new Date().toISOString(),
      uptime: {
        seconds: Math.floor(uptime),
        formatted: formatUptime(uptime)
      },
      database: {
        status: dbStatus,
        state: dbState
      },
      memory: {
        process: {
          heapUsed: formatBytes(memUsage.heapUsed),
          heapTotal: formatBytes(memUsage.heapTotal),
          rss: formatBytes(memUsage.rss)
        },
        system: {
          total: formatBytes(systemMem.total),
          free: formatBytes(systemMem.free),
          used: formatBytes(systemMem.used),
          percentage: Math.round((systemMem.used / systemMem.total) * 100)
        }
      },
      environment: process.env.NODE_ENV
    });
  } catch (error) {
    logger.error('Health check error:', error);
    res.status(500).json({
      success: false,
      status: 'unhealthy',
      error: error.message
    });
  }
});

// @route   GET /api/v1/health/detailed
// @desc    Detailed system health
// @access  Private (Admin)
router.get('/detailed', async (req, res) => {
  try {
    // Load average
    const loadAvg = os.loadavg();
    
    // CPU info
    const cpus = os.cpus();
    const cpuInfo = {
      model: cpus[0].model,
      cores: cpus.length,
      speed: cpus[0].speed
    };
    
    // Disk usage (mock for now - would need actual implementation)
    const diskUsage = {
      total: '100 GB',
      used: '45 GB',
      free: '55 GB',
      percentage: 45
    };
    
    // Model counts
    const User = require('../models/User');
    const Shipment = require('../models/Shipment');
    const Truck = require('../models/Truck');
    const Invoice = require('../models/Invoice');
    
    const counts = await Promise.all([
      User.countDocuments(),
      Shipment.countDocuments(),
      Truck.countDocuments(),
      Invoice.countDocuments()
    ]);
    
    res.json({
      success: true,
      status: 'healthy',
      timestamp: new Date().toISOString(),
      system: {
        platform: os.platform(),
        release: os.release(),
        arch: os.arch(),
        hostname: os.hostname(),
        loadAverage: {
          '1min': loadAvg[0].toFixed(2),
          '5min': loadAvg[1].toFixed(2),
          '15min': loadAvg[2].toFixed(2)
        },
        cpu: cpuInfo,
        disk: diskUsage
      },
      database: {
        status: mongoose.connection.readyState === 1 ? 'connected' : 'disconnected',
        collections: {
          users: counts[0],
          shipments: counts[1],
          trucks: counts[2],
          invoices: counts[3]
        }
      }
    });
  } catch (error) {
    logger.error('Detailed health check error:', error);
    res.status(500).json({
      success: false,
      status: 'unhealthy',
      error: error.message
    });
  }
});

// @route   GET /api/v1/health/metrics
// @desc    System metrics for monitoring
// @access  Private (Admin)
router.get('/metrics', async (req, res) => {
  try {
    // Gather metrics
    const memUsage = process.memoryUsage();
    
    res.json({
      success: true,
      timestamp: new Date().toISOString(),
      metrics: {
        memory_heap_used_bytes: memUsage.heapUsed,
        memory_heap_total_bytes: memUsage.heapTotal,
        memory_rss_bytes: memUsage.rss,
        uptime_seconds: process.uptime(),
        cpu_load_1min: os.loadavg()[0],
        cpu_load_5min: os.loadavg()[1],
        cpu_load_15min: os.loadavg()[2]
      }
    });
  } catch (error) {
    res.status(500).json({
      success: false,
      error: error.message
    });
  }
});

// Helper functions
function formatUptime(seconds) {
  const days = Math.floor(seconds / (24 * 60 * 60));
  const hours = Math.floor((seconds % (24 * 60 * 60)) / (60 * 60));
  const minutes = Math.floor((seconds % (60 * 60)) / 60);
  const secs = Math.floor(seconds % 60);
  
  if (days > 0) return `${days}d ${hours}h ${minutes}m`;
  if (hours > 0) return `${hours}h ${minutes}m ${secs}s`;
  if (minutes > 0) return `${minutes}m ${secs}s`;
  return `${secs}s`;
}

function formatBytes(bytes) {
  if (bytes === 0) return '0 B';
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
}

module.exports = router;
