const request = require('supertest');
const app = require('../server');
const mongoose = require('mongoose');

describe('API Tests', () => {
  let authToken;

  beforeAll(async () => {
    // Connect to test database
    await mongoose.connect(process.env.MONGODB_URI_TEST || 'mongodb://localhost:27017/logest_test');
  });

  afterAll(async () => {
    // Close database connection
    await mongoose.connection.close();
  });

  describe('Authentication', () => {
    it('should register a new user', async () => {
      const res = await request(app)
        .post('/api/auth/register')
        .send({
          name: 'Test User',
          email: 'test@example.com',
          password: 'password123',
          role: 'employee'
        });
      expect(res.statusCode).toEqual(201);
      expect(res.body).toHaveProperty('token');
    });

    it('should login with valid credentials', async () => {
      const res = await request(app)
        .post('/api/auth/login')
        .send({
          email: 'test@example.com',
          password: 'password123'
        });
      expect(res.statusCode).toEqual(200);
      expect(res.body).toHaveProperty('token');
      authToken = res.body.token;
    });

    it('should not login with invalid credentials', async () => {
      const res = await request(app)
        .post('/api/auth/login')
        .send({
          email: 'test@example.com',
          password: 'wrongpassword'
        });
      expect(res.statusCode).toEqual(401);
    });
  });

  describe('Shipments', () => {
    it('should get all shipments', async () => {
      const res = await request(app)
        .get('/api/shipments')
        .set('Authorization', `Bearer ${authToken}`);
      expect(res.statusCode).toEqual(200);
      expect(Array.isArray(res.body)).toBe(true);
    });

    it('should create a new shipment', async () => {
      const res = await request(app)
        .post('/api/shipments')
        .set('Authorization', `Bearer ${authToken}`)
        .send({
          pickupLocation: { address: 'Test Address' },
          deliveryLocation: { address: 'Test Delivery' },
          cargoDetails: 'Test Cargo',
          weight: 1000
        });
      expect(res.statusCode).toEqual(201);
      expect(res.body).toHaveProperty('_id');
    });
  });

  describe('Maintenance', () => {
    it('should get all maintenance records', async () => {
      const res = await request(app)
        .get('/api/maintenance')
        .set('Authorization', `Bearer ${authToken}`);
      expect(res.statusCode).toEqual(200);
      expect(Array.isArray(res.body)).toBe(true);
    });

    it('should create a maintenance record', async () => {
      const res = await request(app)
        .post('/api/maintenance')
        .set('Authorization', `Bearer ${authToken}`)
        .send({
          type: 'oil_change',
          description: 'Test maintenance',
          cost: 500
        });
      expect(res.statusCode).toEqual(201);
    });
  });

  describe('Analytics', () => {
    it('should get analytics data', async () => {
      const res = await request(app)
        .get('/api/analytics')
        .set('Authorization', `Bearer ${authToken}`);
      expect(res.statusCode).toEqual(200);
      expect(res.body).toHaveProperty('totalShipments');
    });
  });

  describe('Security', () => {
    it('should reject requests without authentication', async () => {
      const res = await request(app)
        .get('/api/shipments');
      expect(res.statusCode).toEqual(401);
    });

    it('should reject requests with invalid token', async () => {
      const res = await request(app)
        .get('/api/shipments')
        .set('Authorization', 'Bearer invalid-token');
      expect(res.statusCode).toEqual(401);
    });
  });
});
