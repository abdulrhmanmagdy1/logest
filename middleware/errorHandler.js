/**
 * ============================================
 * ⚠️ Error Handler Middleware - نظام إدهام
 * Edham Logistics - Centralized Error Handling
 * ============================================
 */

const { MESSAGES, HTTP_STATUS } = require('../config/constants');
const logger = require('../utils/logger');

class AppError extends Error {
  constructor(message, statusCode) {
    super(message);
    this.statusCode = statusCode;
    this.isOperational = true;
    Error.captureStackTrace(this, this.constructor);
  }
}

const errorHandler = (err, req, res, next) => {
  let error = { ...err };
  error.message = err.message;

  // Log error
  logger.error('Error occurred', {
    message: err.message,
    stack: err.stack,
    url: req.url,
    method: req.method
  });

  // Mongoose bad ObjectId
  if (err.name === 'CastError') {
    const message = 'المعرف غير صحيح';
    error = new AppError(message, HTTP_STATUS.BAD_REQUEST);
  }

  // Mongoose duplicate key
  if (err.code === 11000) {
    const message = 'البيانات موجودة بالفعل';
    error = new AppError(message, HTTP_STATUS.CONFLICT);
  }

  // Mongoose validation error
  if (err.name === 'ValidationError') {
    const message = Object.values(err.errors).map(val => val.message).join(', ');
    error = new AppError(message, HTTP_STATUS.BAD_REQUEST);
  }

  // JWT errors
  if (err.name === 'JsonWebTokenError') {
    const message = MESSAGES.INVALID_TOKEN;
    error = new AppError(message, HTTP_STATUS.UNAUTHORIZED);
  }

  if (err.name === 'TokenExpiredError') {
    const message = MESSAGES.TOKEN_EXPIRED;
    error = new AppError(message, HTTP_STATUS.UNAUTHORIZED);
  }

  res.status(error.statusCode || HTTP_STATUS.INTERNAL_ERROR).json({
    success: false,
    message: error.message || MESSAGES.ERROR,
    ...(process.env.NODE_ENV === 'development' && { stack: err.stack })
  });
};

const notFound = (req, res, next) => {
  const error = new AppError(`المسار ${req.originalUrl} غير موجود`, HTTP_STATUS.NOT_FOUND);
  next(error);
};

module.exports = { errorHandler, notFound, AppError };
