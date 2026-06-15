//
/**
 * ============================================
 * 🧪 Test Setup - إعداد الاختبارات
 * ============================================
 */

const mongoose = require('mongoose');
const { MongoMemoryServer } = require('mongodb-memory-server');

let mongoServer;

// Setup test database
beforeAll(async () => {
  mongoServer = await MongoMemoryServer.create();
  const mongoUri = mongoServer.getUri();
  
  await mongoose.connect(mongoUri, {
    useNewUrlParser: true,
    useUnifiedTopology: true
  });
});

// Cleanup after tests
afterAll(async () => {
  await mongoose.disconnect();
  await mongoServer.stop();
});

// Clear collections before each test
beforeEach(async () => {
  const collections = mongoose.connection.collections;
  
  for (const key in collections) {
    await collections[key].deleteMany({});
  }
});

// Test utilities
const testUtils = {
  // Create a test user
  createTestUser: async (overrides = {}) => {
    const User = require('../models/User');
    
    const defaultUser = {
      email: 'test@example.com',
      password: 'TestPassword123',
      firstName: 'Test',
      lastName: 'User',
      phone: '+966500000000',
      role: 'client',
      status: 'active'
    };
    
    const user = new User({ ...defaultUser, ...overrides });
    await user.save();
    return user;
  },
  
  // Create a test shipment
  createTestShipment: async (overrides = {}) => {
    const Shipment = require('../models/Shipment');
    
    const defaultShipment = {
      trackingNumber: 'TEST-001',
      cargo: {
        type: 'frozen',
        description: 'Test Cargo',
        weight: { value: 100, unit: 'kg' },
        temperature: { min: -25, max: -15 }
      },
      pickup: {
        address: {
          city: 'Riyadh',
          region: 'Central',
          coordinates: { lat: 24.7136, lng: 46.6753 }
        }
      },
      delivery: {
        address: {
          city: 'Jeddah',
          region: 'Western',
          coordinates: { lat: 21.4858, lng: 39.1925 }
        }
      },
      status: 'pending'
    };
    
    const shipment = new Shipment({ ...defaultShipment, ...overrides });
    await shipment.save();
    return shipment;
  },
  
  // Generate auth token
  generateToken: (userId) => {
    const jwt = require('jsonwebtoken');
    return jwt.sign(
      { id: userId },
      process.env.JWT_SECRET || 'testsecret',
      { expiresIn: '1h' }
    );
  },
  
  // Mock request object
  mockRequest: (overrides = {}) => ({
    body: {},
    params: {},
    query: {},
    headers: {},
    user: null,
    ...overrides
  }),
  
  // Mock response object
  mockResponse: () => {
    const res = {};
    res.status = jest.fn().mockReturnValue(res);
    res.json = jest.fn().mockReturnValue(res);
    res.send = jest.fn().mockReturnValue(res);
    res.cookie = jest.fn().mockReturnValue(res);
    res.clearCookie = jest.fn().mockReturnValue(res);
    return res;
  }
};

module.exports = testUtils;
