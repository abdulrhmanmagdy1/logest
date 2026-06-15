/**
 * ============================================
 * 📦 Main Entry Point - نظام إدهام
 * Edham Logistics - Application Entry Point
 * ============================================
 * 
 * This is the main entry point for the Edham Logistics backend.
 * It exports all major components for easy importing.
 * 
 * Usage:
 * const { controllers, models, middleware, utils } = require('./index');
 * const { ShipmentController } = require('./index').controllers;
 */

const server = require('./server');
const controllers = require('./controllers');
const models = require('./models');
const middleware = require('./middleware');
const utils = require('./utils');
const config = require('./config/constants');

module.exports = {
  // Main server
  server,
  
  // All controllers
  controllers,
  
  // All models
  models,
  
  // All middleware
  middleware,
  
  // All utilities
  utils,
  
  // Config/constants
  config,
  
  // Individual exports for convenience
  logger: utils.logger,
  ROLES: config.ROLES,
  HTTP_STATUS: config.HTTP_STATUS,
  MESSAGES: config.MESSAGES
};
