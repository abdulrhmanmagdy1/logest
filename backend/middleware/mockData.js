/**
 * ============================================
 * 🧪 Mock Data Middleware - نظام إدهام
 * Returns mock data when MongoDB is not available
 * ============================================
 */

const logger = require('../utils/logger');

// Mock Users
const mockUsers = [
  {
    _id: 'user_001',
    email: 'admin@edham.com',
    fullName: 'Admin User',
    phone: '+966500000001',
    role: 'admin',
    createdAt: new Date()
  },
  {
    _id: 'user_002',
    email: 'supervisor@edham.com',
    fullName: 'Supervisor User',
    phone: '+966500000002',
    role: 'supervisor',
    createdAt: new Date()
  },
  {
    _id: 'user_003',
    email: 'driver@edham.com',
    fullName: 'Driver User',
    phone: '+966500000003',
    role: 'driver',
    createdAt: new Date()
  },
  {
    _id: 'user_004',
    email: 'customer@edham.com',
    fullName: 'Customer User',
    phone: '+966500000004',
    role: 'customer',
    createdAt: new Date()
  }
];

// Mock Shipments
const mockShipments = [
  {
    _id: 'ship_001',
    trackingNumber: 'EDH-001',
    status: 'in_transit',
    origin: { lat: 24.7136, lng: 46.6753, address: 'Riyadh, Saudi Arabia' },
    destination: { lat: 21.4225, lng: 39.8262, address: 'Jeddah, Saudi Arabia' },
    customer: 'user_004',
    driver: 'user_003',
    createdAt: new Date()
  },
  {
    _id: 'ship_002',
    trackingNumber: 'EDH-002',
    status: 'delivered',
    origin: { lat: 24.7136, lng: 46.6753, address: 'Riyadh, Saudi Arabia' },
    destination: { lat: 24.4672, lng: 39.6157, address: 'Medina, Saudi Arabia' },
    customer: 'user_004',
    driver: 'user_003',
    createdAt: new Date()
  }
];

// Mock Drivers
const mockDrivers = [
  {
    _id: 'driver_001',
    userId: 'user_003',
    licenseNumber: 'SA-123456',
    status: 'active',
    currentLocation: { lat: 24.7136, lng: 46.6753 },
    createdAt: new Date()
  }
];

// Mock Trucks
const mockTrucks = [
  {
    _id: 'truck_001',
    plateNumber: 'ABC-1234',
    model: 'Volvo FH16',
    status: 'active',
    capacity: 25000,
    createdAt: new Date()
  }
];

// Helper function to get mock data based on path
const getMockData = (path, method) => {
  // Auth endpoints
  if (path.includes('/auth/login') && method === 'POST') {
    return {
      success: true,
      data: {
        accessToken: 'mock_jwt_token_' + Date.now(),
        refreshToken: 'mock_refresh_token',
        user: mockUsers[0]
      }
    };
  }

  if (path.includes('/auth/register') && method === 'POST') {
    return {
      success: true,
      data: {
        accessToken: 'mock_jwt_token_' + Date.now(),
        refreshToken: 'mock_refresh_token',
        user: mockUsers[0]
      }
    };
  }

  // Users endpoints
  if (path.includes('/users') && method === 'GET') {
    return {
      success: true,
      data: mockUsers
    };
  }

  // Shipments endpoints
  if (path.includes('/shipments') && method === 'GET') {
    return {
      success: true,
      data: mockShipments
    };
  }

  if (path.match(/\/shipments\/\w+/) && method === 'GET') {
    return {
      success: true,
      data: mockShipments[0]
    };
  }

  // Drivers endpoints
  if (path.includes('/drivers') && method === 'GET') {
    return {
      success: true,
      data: mockDrivers
    };
  }

  // Trucks endpoints
  if (path.includes('/trucks') && method === 'GET') {
    return {
      success: true,
      data: mockTrucks
    };
  }

  // Health check
  if (path.includes('/health')) {
    return {
      status: 'success',
      message: 'Edham Logistics API is running in MOCK MODE',
      timestamp: new Date().toISOString(),
      version: '1.0.0',
      mode: 'mock'
    };
  }

  // Default response
  return {
    success: true,
    message: 'Mock response for ' + path,
    data: []
  };
};

// Mock middleware
const mockMiddleware = (req, res, next) => {
  const { USE_MOCK_MODE } = require('../config/database');
  
  if (!USE_MOCK_MODE) {
    return next();
  }

  logger.info(`🧪 Mock Mode: ${req.method} ${req.path}`);

  // Skip certain paths
  if (req.path.includes('/api-docs') || req.path.includes('/uploads')) {
    return next();
  }

  // Return mock data
  const mockResponse = getMockData(req.path, req.method);
  
  // Simulate network delay
  setTimeout(() => {
    res.json(mockResponse);
  }, 100);
};

module.exports = mockMiddleware;
