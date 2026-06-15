//
/**
 * ============================================
 * 📚 Swagger/OpenAPI Documentation
 * ============================================
 */

const swaggerJsdoc = require('swagger-jsdoc');

const options = {
  definition: {
    openapi: '3.0.0',
    info: {
      title: 'Edham Logistics API',
      version: '1.0.0',
      description: `
        # نظام إدهام اللوجستي - API Documentation
        
        ## Overview
        Enterprise-grade logistics management system API for cold chain transportation.
        
        ## Features
        - 🔐 JWT Authentication & RBAC
        - 📦 Shipment Management
        - 🚛 Fleet Management
        - 💰 Financial Management
        - 🌡️ Temperature Monitoring
        - 📊 Analytics & Reporting
        - 💬 Real-time Chat
        - 🎫 Support Tickets
        
        ## Base URL
        - Production: 
          - https://api.edham-logistics.com/api/v1
        - Development:
          - http://localhost:5000/api/v1
        
        ## Authentication
        All endpoints (except login/register) require a valid JWT token in the header:
        
        \`\`\`
        Authorization: Bearer <your_jwt_token>
        \`\`\`
        
        ## Rate Limiting
        - Authentication: 5 requests per 15 minutes
        - API: 100 requests per minute
        - Strict operations: 10 requests per minute
        
        ## Response Format
        All responses follow this structure:
        \`\`\`json
        {
          "success": true,
          "message": "Operation completed successfully",
          "data": { ... },
          "meta": {
            "timestamp": "2024-01-15T10:30:00Z",
            "requestId": "req-uuid"
          }
        }
        \`\`\`
      `,
      contact: {
        name: 'Edham Logistics Support',
        email: 'support@edham-logistics.com',
        url: 'https://edham-logistics.com/support'
      },
      license: {
        name: 'Enterprise License',
        url: 'https://edham-logistics.com/license'
      }
    },
    servers: [
      {
        url: 'http://localhost:5000/api/v1',
        description: 'Development Server'
      },
      {
        url: 'https://api.edham-logistics.com/api/v1',
        description: 'Production Server'
      }
    ],
    components: {
      securitySchemes: {
        BearerAuth: {
          type: 'http',
          scheme: 'bearer',
          bearerFormat: 'JWT',
          description: 'JWT token obtained from /auth/login'
        }
      },
      schemas: {
        // User Schema
        User: {
          type: 'object',
          properties: {
            _id: { type: 'string', example: '60d5eca77e3f4a3c8c4b8c1d' },
            email: { type: 'string', example: 'user@example.com' },
            firstName: { type: 'string', example: 'أحمد' },
            lastName: { type: 'string', example: 'محمد' },
            phone: { type: 'string', example: '+966500000000' },
            role: { 
              type: 'string', 
              enum: ['admin', 'client', 'driver', 'accountant', 'supervisor', 'workshop'],
              example: 'client'
            },
            status: { type: 'string', enum: ['active', 'inactive', 'suspended'], example: 'active' },
            company: {
              type: 'object',
              properties: {
                name: { type: 'string', example: 'شركة التجارة' },
                registration: { type: 'string' }
              }
            },
            createdAt: { type: 'string', format: 'date-time' },
            updatedAt: { type: 'string', format: 'date-time' }
          },
          required: ['email', 'firstName', 'lastName', 'phone', 'role']
        },
        
        // Shipment Schema
        Shipment: {
          type: 'object',
          properties: {
            _id: { type: 'string' },
            trackingNumber: { type: 'string', example: 'EDH-2024-00156' },
            cargo: {
              type: 'object',
              properties: {
                type: { type: 'string', enum: ['general', 'frozen', 'chilled', 'pharmaceutical'] },
                description: { type: 'string' },
                weight: {
                  type: 'object',
                  properties: {
                    value: { type: 'number' },
                    unit: { type: 'string', default: 'kg' }
                  }
                },
                temperature: {
                  type: 'object',
                  properties: {
                    min: { type: 'number', example: -25 },
                    max: { type: 'number', example: -15 },
                    critical: { type: 'boolean' }
                  }
                }
              }
            },
            pickup: {
              type: 'object',
              properties: {
                address: { $ref: '#/components/schemas/Address' },
                scheduledDate: { type: 'string', format: 'date-time' },
                timeWindow: {
                  type: 'object',
                  properties: {
                    start: { type: 'string', example: '09:00' },
                    end: { type: 'string', example: '12:00' }
                  }
                }
              }
            },
            delivery: {
              type: 'object',
              properties: {
                address: { $ref: '#/components/schemas/Address' },
                scheduledDate: { type: 'string', format: 'date-time' },
                timeWindow: {
                  type: 'object',
                  properties: {
                    start: { type: 'string' },
                    end: { type: 'string' }
                  }
                }
              }
            },
            status: { 
              type: 'string', 
              enum: ['pending', 'confirmed', 'assigned', 'picked_up', 'in_transit', 'delivered', 'completed'],
              example: 'in_transit'
            },
            driver: { type: 'string', description: 'Driver ID' },
            truck: { type: 'string', description: 'Truck ID' },
            pricing: {
              type: 'object',
              properties: {
                base: { type: 'number' },
                distance: { type: 'number' },
                weight: { type: 'number' },
                temperature: { type: 'number' },
                total: { type: 'number', example: 450.00 }
              }
            },
            createdAt: { type: 'string', format: 'date-time' },
            updatedAt: { type: 'string', format: 'date-time' }
          }
        },
        
        // Address Schema
        Address: {
          type: 'object',
          properties: {
            street: { type: 'string' },
            city: { type: 'string', example: 'الرياض' },
            region: { type: 'string', example: 'الوسطى' },
            coordinates: {
              type: 'object',
              properties: {
                lat: { type: 'number', example: 24.7136 },
                lng: { type: 'number', example: 46.6753 }
              }
            }
          }
        },
        
        // Invoice Schema
        Invoice: {
          type: 'object',
          properties: {
            _id: { type: 'string' },
            invoiceNumber: { type: 'string', example: 'INV-2024-00156' },
            client: { type: 'string' },
            shipment: { type: 'string' },
            items: {
              type: 'array',
              items: {
                type: 'object',
                properties: {
                  description: { type: 'string' },
                  quantity: { type: 'number' },
                  unitPrice: { type: 'number' },
                  total: { type: 'number' }
                }
              }
            },
            subtotal: { type: 'number' },
            tax: { type: 'number' },
            total: { type: 'number' },
            status: { 
              type: 'string', 
              enum: ['pending', 'paid', 'partially_paid', 'overdue', 'cancelled'],
              example: 'pending'
            },
            dueDate: { type: 'string', format: 'date-time' },
            createdAt: { type: 'string', format: 'date-time' }
          }
        },
        
        // Error Schema
        Error: {
          type: 'object',
          properties: {
            success: { type: 'boolean', example: false },
            message: { type: 'string', example: 'Error description' },
            errorCode: { type: 'string', example: 'VALIDATION_ERROR' },
            errors: {
              type: 'array',
              items: { type: 'string' }
            }
          }
        },
        
        // Pagination Schema
        Pagination: {
          type: 'object',
          properties: {
            current: { type: 'integer', example: 1 },
            total: { type: 'integer', example: 10 },
            perPage: { type: 'integer', example: 20 },
            totalItems: { type: 'integer', example: 195 }
          }
        }
      },
      responses: {
        UnauthorizedError: {
          description: 'Access token is missing or invalid',
          content: {
            'application/json': {
              schema: { $ref: '#/components/schemas/Error' },
              example: {
                success: false,
                message: 'Not authorized to access this route',
                errorCode: 'UNAUTHORIZED'
              }
            }
          }
        },
        ValidationError: {
          description: 'Validation failed',
          content: {
            'application/json': {
              schema: { $ref: '#/components/schemas/Error' },
              example: {
                success: false,
                message: 'Validation failed',
                errorCode: 'VALIDATION_ERROR',
                errors: ['Email is required', 'Password must be at least 6 characters']
              }
            }
          }
        },
        NotFoundError: {
          description: 'Resource not found',
          content: {
            'application/json': {
              schema: { $ref: '#/components/schemas/Error' },
              example: {
                success: false,
                message: 'Resource not found',
                errorCode: 'NOT_FOUND'
              }
            }
          }
        }
      }
    },
    security: [
      {
        BearerAuth: []
      }
    ],
    tags: [
      { name: 'Authentication', description: 'User authentication and authorization' },
      { name: 'Users', description: 'User management' },
      { name: 'Shipments', description: 'Shipment management and tracking' },
      { name: 'Drivers', description: 'Driver management and assignments' },
      { name: 'Fleet', description: 'Vehicle and fleet management' },
      { name: 'Finance', description: 'Invoices, payments, and financial operations' },
      { name: 'Workshop', description: 'Maintenance and repairs' },
      { name: 'Analytics', description: 'Reports and analytics' },
      { name: 'Chat', description: 'Messaging and communication' },
      { name: 'Support', description: 'Support tickets and help desk' },
      { name: 'IoT', description: 'Temperature monitoring and sensors' },
      { name: 'Documents', description: 'Document management' }
    ]
  },
  apis: [
    './routes/*.js',
    './controllers/*.js'
  ]
};

const swaggerSpec = swaggerJsdoc(options);

module.exports = swaggerSpec;
