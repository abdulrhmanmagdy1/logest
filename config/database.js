/**
 * ============================================
 * 🗄️ Database Configuration - نظام إدهام
 * Edham Logistics - MongoDB Connection
 * ============================================
 */

const mongoose = require('mongoose');
const config = require('./environment');
const logger = require('../utils/logger');

const connectDB = async () => {
  try {
    const conn = await mongoose.connect(config.DB_URI);

    logger.success('MongoDB Connected', {
      host: conn.connection.host,
      database: conn.connection.name
    });

    // Handle connection events
    mongoose.connection.on('error', (err) => {
      logger.error('MongoDB connection error', err);
    });

    mongoose.connection.on('disconnected', () => {
      logger.warn('MongoDB disconnected');
    });

    mongoose.connection.on('reconnected', () => {
      logger.success('MongoDB reconnected');
    });

    // Graceful shutdown
    process.on('SIGINT', async () => {
      await mongoose.connection.close();
      logger.info('MongoDB connection closed through app termination');
      process.exit(0);
    });

  } catch (error) {
    logger.error('MongoDB connection failed', error);
    if (process.env.NODE_ENV === 'test') {
      throw error;
    }
    process.exit(1);
  }
};

module.exports = connectDB;
