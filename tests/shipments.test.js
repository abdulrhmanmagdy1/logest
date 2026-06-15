const request = require('supertest');
const mongoose = require('mongoose');
const app = require('../server');
const Shipment = require('../models/Shipment');
const User = require('../models/User');

describe('Shipments API', () => {
  let authToken;
  let testUser;

  beforeAll(async () => {
    await mongoose.connect(process.env.MONGODB_URI_TEST || 'mongodb://localhost:27017/edham_test');
    
    // Create test user and get token
    testUser = await User.create({
      name: 'Test User',
      email: 'test@example.com',
      password: await require('bcryptjs').hash('password123', 10),
      role: 'supervisor',
      isActive: true
    });

    const loginRes = await request(app)
      .post('/api/auth/login')
      .send({ email: 'test@example.com', password: 'password123' });
    
    authToken = loginRes.body.token;
  });

  afterAll(async () => {
    await mongoose.connection.close();
  });

  beforeEach(async () => {
    await Shipment.deleteMany({});
  });

  describe('GET /api/shipments', () => {
    it('should get all shipments', async () => {
      // Create test shipment
      await Shipment.create({
        shipmentNumber: 'SHP001',
        customer: testUser._id,
        fromCity: 'الرياض',
        toCity: 'جدة',
        status: 'pending',
        weight: 1000
      });

      const res = await request(app)
        .get('/api/shipments')
        .set('Authorization', `Bearer ${authToken}`);

      expect(res.statusCode).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data).toHaveLength(1);
    });

    it('should filter shipments by status', async () => {
      await Shipment.create([
        { shipmentNumber: 'SHP001', customer: testUser._id, fromCity: 'الرياض', toCity: 'جدة', status: 'pending' },
        { shipmentNumber: 'SHP002', customer: testUser._id, fromCity: 'جدة', toCity: 'الرياض', status: 'delivered' }
      ]);

      const res = await request(app)
        .get('/api/shipments?status=pending')
        .set('Authorization', `Bearer ${authToken}`);

      expect(res.statusCode).toBe(200);
      expect(res.body.data).toHaveLength(1);
      expect(res.body.data[0].status).toBe('pending');
    });
  });

  describe('POST /api/shipments', () => {
    it('should create new shipment', async () => {
      const res = await request(app)
        .post('/api/shipments')
        .set('Authorization', `Bearer ${authToken}`)
        .send({
          shipmentNumber: 'SHP003',
          customer: testUser._id,
          fromCity: 'الدمام',
          toCity: 'الرياض',
          weight: 2000,
          description: 'Test shipment'
        });

      expect(res.statusCode).toBe(201);
      expect(res.body.success).toBe(true);
      expect(res.body.data.shipmentNumber).toBe('SHP003');
    });

    it('should not create shipment without required fields', async () => {
      const res = await request(app)
        .post('/api/shipments')
        .set('Authorization', `Bearer ${authToken}`)
        .send({
          fromCity: 'الرياض'
        });

      expect(res.statusCode).toBe(400);
    });
  });

  describe('PUT /api/shipments/:id', () => {
    it('should update shipment status', async () => {
      const shipment = await Shipment.create({
        shipmentNumber: 'SHP004',
        customer: testUser._id,
        fromCity: 'الرياض',
        toCity: 'جدة',
        status: 'pending'
      });

      const res = await request(app)
        .put(`/api/shipments/${shipment._id}/status`)
        .set('Authorization', `Bearer ${authToken}`)
        .send({ status: 'in_transit' });

      expect(res.statusCode).toBe(200);
      expect(res.body.data.status).toBe('in_transit');
    });
  });

  describe('DELETE /api/shipments/:id', () => {
    it('should delete shipment', async () => {
      const shipment = await Shipment.create({
        shipmentNumber: 'SHP005',
        customer: testUser._id,
        fromCity: 'الرياض',
        toCity: 'جدة'
      });

      const res = await request(app)
        .delete(`/api/shipments/${shipment._id}`)
        .set('Authorization', `Bearer ${authToken}`);

      expect(res.statusCode).toBe(200);
      expect(res.body.success).toBe(true);
    });
  });
});
