/**
 * ============================================
 * ✅ Validation Middleware - نظام إدهام
 * Edham Logistics - Request Validation
 * ============================================
 */

const Joi = require('joi');
const logger = require('../utils/logger');
const { HTTP_STATUS, MESSAGES, ROLES } = require('../config/constants');

// Shipment validation schema
const shipmentSchema = Joi.object({
  shipmentNumber: Joi.string().required(),
  customerName: Joi.string().required().min(2).max(100),
  fromCity: Joi.string().required(),
  toCity: Joi.string().required(),
  status: Joi.string().valid('pending', 'in_transit', 'delivered', 'cancelled').default('pending'),
  weight: Joi.number().positive().optional(),
  temperature: Joi.number().optional()
});

// User validation schema
const userSchema = Joi.object({
  name: Joi.string().required().min(3).max(50),
  email: Joi.string().email().required(),
  password: Joi.string().min(6).required(),
  phone: Joi.string().optional(),
  role: Joi.string().valid(
    ROLES.ADMIN, 
    ROLES.SUPERVISOR, 
    ROLES.ACCOUNTANT, 
    ROLES.DRIVER, 
    ROLES.CLIENT, 
    ROLES.EMPLOYEE, 
    ROLES.MAINTENANCE
  ).default(ROLES.CLIENT)
});

// Truck validation schema
const truckSchema = Joi.object({
  plateNumber: Joi.string().required(),
  brand: Joi.string().required(),
  model: Joi.string().required(),
  year: Joi.number().integer().min(2000).max(new Date().getFullYear()),
  capacity: Joi.number().positive().required(),
  status: Joi.string().valid('active', 'inactive', 'maintenance').default('active')
});

// Validation middleware factory
const validate = (schema) => {
  return (req, res, next) => {
    const { error, value } = schema.validate(req.body, {
      abortEarly: false,
      stripUnknown: true
    });

    if (error) {
      const errors = error.details.map(detail => ({
        field: detail.path[0],
        message: detail.message
      }));
      
      logger.warning('Validation error', { errors, path: req.path });
      
      return res.status(HTTP_STATUS.BAD_REQUEST).json({
        success: false,
        message: MESSAGES.VALIDATION_ERROR,
        errors
      });
    }

    // Replace req.body with validated value
    req.body = value;
    next();
  };
};

// Export validators
module.exports = {
  validate,
  schemas: {
    shipment: shipmentSchema,
    user: userSchema,
    truck: truckSchema
  }
};
