/**
 * ============================================
 * 📚 Swagger Documentation - نظام إدهام
 * Edham Logistics - API Documentation Config
 * ============================================
 */

const swaggerJsdoc = require('swagger-jsdoc');
const { ROLES, SHIPMENT_STATUS, TRUCK_STATUS } = require('./config/constants');

const options = {
  definition: {
    openapi: '3.0.0',
    info: {
      title: 'Edham Logistics API - نظام إدهام',
      version: '1.0.0',
      description: 'API documentation for Edham Logistics & Refrigerated Transportation System\nنظام إدهام للنقل المبرد واللوجستيات',
      contact: {
        name: 'Edham Support',
        email: 'info@edham.com'
      },
      license: {
        name: 'ISC',
        url: 'https://opensource.org/licenses/ISC'
      }
    },
    servers: [
      {
        url: 'http://localhost:5000/api',
        description: 'Development server'
      },
      {
        url: 'https://api.edham.com/api',
        description: 'Production server'
      }
    ],
    components: {
      securitySchemes: {
        bearerAuth: {
          type: 'http',
          scheme: 'bearer',
          bearerFormat: 'JWT',
          description: 'Enter your JWT token'
        }
      },
      schemas: {
        User: {
          type: 'object',
          properties: {
            id: { type: 'string' },
            name: { type: 'string' },
            email: { type: 'string' },
            role: { type: 'string', enum: Object.values(ROLES) },
            phone: { type: 'string' },
            isActive: { type: 'boolean' },
            createdAt: { type: 'string', format: 'date-time' }
          }
        },
        Shipment: {
          type: 'object',
          properties: {
            id: { type: 'string' },
            shipmentNumber: { type: 'string' },
            customerName: { type: 'string' },
            fromCity: { type: 'string' },
            toCity: { type: 'string' },
            status: { type: 'string', enum: Object.values(SHIPMENT_STATUS) },
            weight: { type: 'number' },
            temperature: { type: 'number' },
            createdAt: { type: 'string', format: 'date-time' }
          }
        },
        Truck: {
          type: 'object',
          properties: {
            id: { type: 'string' },
            plateNumber: { type: 'string' },
            brand: { type: 'string' },
            model: { type: 'string' },
            year: { type: 'number' },
            capacity: { type: 'number' },
            status: { type: 'string', enum: Object.values(TRUCK_STATUS) }
          }
        },
        Error: {
          type: 'object',
          properties: {
            success: { type: 'boolean', default: false },
            message: { type: 'string' },
            code: { type: 'string' },
            errors: { type: 'array', items: { type: 'object' } }
          }
        },
        Pagination: {
          type: 'object',
          properties: {
            total: { type: 'integer' },
            page: { type: 'integer' },
            limit: { type: 'integer' },
            pages: { type: 'integer' }
          }
        }
      }
    },
    security: [
      {
        bearerAuth: []
      }
    ]
  },
  apis: ['./routes/*.js', './models/*.js']
};

const specs = swaggerJsdoc(options);

module.exports = specs;
