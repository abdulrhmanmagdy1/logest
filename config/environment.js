/**
 * ============================================
 * ⚙️ Environment Configuration - نظام إدهام
 * Edham Logistics - Centralized Config
 * ============================================
 */

require('dotenv').config();

const config = {
  // Server
  NODE_ENV: process.env.NODE_ENV || 'development',
  PORT: process.env.PORT || 5000,
  
  // Database
  DB_URI: process.env.MONGODB_URI || 'mongodb://localhost:27017/edham',
  
  // JWT
  JWT_SECRET: process.env.JWT_SECRET || 'your-super-secret-jwt-key-change-this',
  JWT_EXPIRE: process.env.JWT_EXPIRE || '7d',
  
  // CORS
  CORS_ORIGIN: process.env.CORS_ORIGIN || 'http://localhost:3000',
  
  // Stripe
  STRIPE_SECRET_KEY: process.env.STRIPE_SECRET_KEY,
  STRIPE_WEBHOOK_SECRET: process.env.STRIPE_WEBHOOK_SECRET,
  
  // Email
  EMAIL_HOST: process.env.EMAIL_HOST || 'smtp.gmail.com',
  EMAIL_PORT: process.env.EMAIL_PORT || 587,
  EMAIL_USER: process.env.EMAIL_USER,
  EMAIL_PASS: process.env.EMAIL_PASS,
  
  // API
  API_KEY_HEADER: 'x-api-key',
  
  // Pagination
  DEFAULT_PAGE_SIZE: 20,
  MAX_PAGE_SIZE: 100,
  
  // Rate Limiting
  RATE_LIMIT_MAX: 100,
  RATE_LIMIT_WINDOW: 15 * 60 * 1000, // 15 minutes
  
  // Body Parser
  MAX_BODY_SIZE: '10kb'
};

// Validation for required environment variables
const requiredEnvs = ['JWT_SECRET', 'MONGODB_URI'];
const missingEnvs = requiredEnvs.filter(env => !process.env[env]);

if (missingEnvs.length > 0) {
  console.warn('⚠️  Missing environment variables:', missingEnvs.join(', '));
  console.warn('Using default values - NOT recommended for production');
}

if (process.env.NODE_ENV === 'production' && missingEnvs.length > 0) {
  throw new Error(`Missing required environment variables in production: ${missingEnvs.join(', ')}`);
}

module.exports = config;
