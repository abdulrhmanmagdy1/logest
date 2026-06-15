/**
 * ============================================
 * ⚡ Performance Middleware - نظام إدهام
 * Edham Logistics - Performance Monitoring
 * ============================================
 */

const { performance } = require('perf_hooks');
const logger = require('../utils/logger');
const { HTTP_STATUS, MESSAGES } = require('../config/constants');

// Performance tracking middleware
const performanceTracker = (req, res, next) => {
  const start = performance.now();
  
  const originalJson = res.json;
  
  res.json = function(data) {
    const duration = performance.now() - start;
    
    if (duration > 1000) {
      logger.warning('Slow Request', {
        method: req.method,
        path: req.path,
        duration: `${duration.toFixed(2)}ms`,
        userId: req.user?._id
      });
    }
    
    res.setHeader('X-Response-Time', `${duration.toFixed(2)}ms`);
    
    return originalJson.call(this, data);
  };
  
  next();
};

// Memory usage monitoring
const memoryMonitor = () => {
  const used = process.memoryUsage();
  const total = process.memoryUsage().heapTotal;
  const free = total - used;
  const usagePercent = (used / total) * 100;
  
  logger.info('Memory Usage', {
    used: `${Math.round(used / 1024 / 1024 * 100) / 100} MB`,
    free: `${Math.round(free / 1024 / 1024 * 100) / 100} MB`,
    total: `${Math.round(total / 1024 / 1024 * 100) / 100} MB`,
    usagePercent: `${usagePercent.toFixed(1)}%`
  });
  
  if (usagePercent > 80) {
    logger.warning('High memory usage detected!', { usagePercent });
  }
};

// Request size limiter
const requestSizeLimiter = (req, res, next) => {
  const maxSize = 10 * 1024 * 1024;
  const contentLength = req.get('content-length');
  
  if (contentLength && parseInt(contentLength) > maxSize) {
    logger.warning('Request too large', {
      path: req.path,
      size: contentLength,
      maxSize
    });
    return res.status(HTTP_STATUS.BAD_REQUEST).json({ 
      success: false,
      message: MESSAGES.REQUEST_TOO_LARGE
    });
  }
  
  next();
};

// Response size limiter
const responseSizeLimiter = (req, res, next) => {
  const originalSend = res.send;
  
  res.send = function(data) {
    const size = Buffer.byteLength(data);
    const maxSize = 10 * 1024 * 1024;
    
    if (size > maxSize) {
      logger.warning('Response too large', {
        path: req.path,
        size,
        maxSize
      });
      return res.status(HTTP_STATUS.INTERNAL_ERROR).json({ 
        success: false,
        message: MESSAGES.RESPONSE_TOO_LARGE
      });
    }
    
    res.setHeader('Content-Length', size);
    return originalSend.call(this, data);
  };
  
  next();
};

module.exports = {
  performanceTracker,
  memoryMonitor,
  requestSizeLimiter,
  responseSizeLimiter
};
