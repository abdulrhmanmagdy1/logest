/**
 * ============================================
 * 📚 Swagger Configuration - نظام إدهام
 * API documentation setup
 * ============================================
 */

const swaggerJsdoc = require('swagger-jsdoc');
const swaggerUi = require('swagger-ui-express');

const options = {
  definition: {
    openapi: '3.0.0',
    info: {
      title: 'Edham Logistics API',
      description: `
        ## نظام إدهام للنقل المبرد 🚛
        
        API documentation for the Edham Logistics management system.
        
        ### Features:
        - 🔐 Authentication & Authorization
        - 📦 Shipment Management
        - 🚛 Fleet Management  
        - 💰 Invoicing & Payments
        - 📍 Real-time Tracking
        - 🔔 Notifications
        - 📊 Reports & Analytics
        
        ### Base URL: /api/v1
      `,
      version: '1.0.0',
      contact: {
        name: 'Edham Support',
        email: 'support@edham.com',
        url: 'https://edham.com'
      },
      license: {
        name: 'Proprietary',
        url: 'https://edham.com/license'
      }
    },
    servers: [
      {
        url: 'http://localhost:5000/api/v1',
        description: 'Development server'
      },
      {
        url: 'https://api.edham.com/api/v1',
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
            _id: { type: 'string', example: '507f1f77bcf86cd799439011' },
            firstName: { type: 'string', example: 'محمد' },
            lastName: { type: 'string', example: 'أحمد' },
            email: { type: 'string', example: 'mohammed@example.com' },
            phone: { type: 'string', example: '0501234567' },
            role: { type: 'string', enum: ['client', 'driver', 'employee', 'supervisor', 'admin', 'accountant'] },
            status: { type: 'string', enum: ['active', 'inactive', 'suspended', 'pending'] },
            companyName: { type: 'string' },
            createdAt: { type: 'string', format: 'date-time' }
          }
        },
        Shipment: {
          type: 'object',
          properties: {
            _id: { type: 'string' },
            trackingNumber: { type: 'string', example: 'EDH2401010001' },
            cargo: {
              type: 'object',
              properties: {
                type: { type: 'string' },
                description: { type: 'string' },
                weight: {
                  type: 'object',
                  properties: {
                    value: { type: 'number' },
                    unit: { type: 'string', enum: ['kg', 'ton'] }
                  }
                },
                temperature: {
                  type: 'object',
                  properties: {
                    min: { type: 'number' },
                    max: { type: 'number' }
                  }
                }
              }
            },
            pickup: {
              type: 'object',
              properties: {
                address: { type: 'object' },
                scheduledDate: { type: 'string', format: 'date-time' }
              }
            },
            delivery: {
              type: 'object',
              properties: {
                address: { type: 'object' },
                scheduledDate: { type: 'string', format: 'date-time' }
              }
            },
            status: {
              type: 'string',
              enum: ['pending', 'confirmed', 'assigned', 'in_transit', 'picked_up', 'on_the_way', 'delivered', 'completed', 'cancelled']
            },
            pricing: {
              type: 'object',
              properties: {
                total: { type: 'number' },
                currency: { type: 'string', default: 'SAR' }
              }
            }
          }
        },
        Invoice: {
          type: 'object',
          properties: {
            invoiceNumber: { type: 'string', example: 'INV-202401-0001' },
            client: { type: 'string', description: 'User ID' },
            shipments: { type: 'array', items: { type: 'string' } },
            total: { type: 'number' },
            status: { type: 'string', enum: ['draft', 'sent', 'paid', 'partial', 'overdue', 'cancelled'] },
            dueDate: { type: 'string', format: 'date-time' }
          }
        },
        Error: {
          type: 'object',
          properties: {
            success: { type: 'boolean', example: false },
            message: { type: 'string' },
            error: { type: 'string' },
            stack: { type: 'string' }
          }
        }
      }
    },
    security: [
      {
        bearerAuth: []
      }
    ],
    tags: [
      { name: 'Authentication', description: 'User authentication and authorization' },
      { name: 'Users', description: 'User management' },
      { name: 'Shipments', description: 'Shipment/Cargo management' },
      { name: 'Trucks', description: 'Fleet management' },
      { name: 'Drivers', description: 'Driver management' },
      { name: 'Invoices', description: 'Billing and invoicing' },
      { name: 'Tracking', description: 'Real-time tracking' },
      { name: 'Notifications', description: 'User notifications' },
      { name: 'Surveys', description: 'Feedback surveys' },
      { name: 'Reports', description: 'Analytics and reports' }
    ]
  },
  apis: ['./routes/*.js'] // Path to the API routes
};

const specs = swaggerJsdoc(options);

module.exports = { specs, swaggerUi };
