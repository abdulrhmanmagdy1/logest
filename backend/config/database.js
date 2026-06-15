/**
 * ============================================
 * 🗄️ Database Configuration - نظام إدهام
 * MongoDB connection setup with Mock Mode
 * ============================================
 */

const mongoose = require('mongoose');
const logger = require('../utils/logger');

// Accept MONGO_URI (Atlas standard) or MONGODB_URI (legacy var name)
const mongoUri = process.env.MONGODB_URI || process.env.MONGO_URI;

// Mock Mode: Run without MongoDB for testing
const USE_MOCK_MODE = process.env.USE_MOCK_MODE === 'true' || !mongoUri;

const connectDB = async () => {
  if (USE_MOCK_MODE) {
    logger.info('🧪 Running in MOCK MODE - No MongoDB required');
    logger.info('⚠️  Data will not persist after restart');
    return;
  }

  try {
    const conn = await mongoose.connect(mongoUri);

    logger.info(`✅ MongoDB Connected: ${conn.connection.host}`);
    
    // Handle connection events
    mongoose.connection.on('error', (err) => {
      logger.error(`MongoDB connection error: ${err}`);
    });

    mongoose.connection.on('disconnected', () => {
      logger.warn('⚠️ MongoDB disconnected. Attempting to reconnect...');
    });

    mongoose.connection.on('reconnected', () => {
      logger.info('✅ MongoDB reconnected');
    });

  } catch (error) {
    logger.error(`❌ MongoDB connection failed: ${error.message}`);
    logger.info('🧪 Falling back to MOCK MODE');
    // Don't exit, continue in mock mode
  }
};

module.exports = connectDB;
module.exports.USE_MOCK_MODE = USE_MOCK_MODE;
