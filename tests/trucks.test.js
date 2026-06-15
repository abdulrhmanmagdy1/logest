const request = require('supertest');
const mongoose = require('mongoose');
const app = require('../server');
const Truck = require('../models/Truck');
const User = require('../models/User');

describe('Trucks API', () => {
  let authToken;
  let adminUser;

  beforeAll(async () => {
    await mongoose.connect(process.env.MONGODB_URI_TEST || 'mongodb://localhost:27017/edham_test');
    
    // Create admin user
    adminUser = await User.create({
      name: 'Admin User',
      email: 'admin@example.com',
      password: await require('bcryptjs').hash('password123', 10),
      role: 'admin',
      isActive: true
    });

    const loginRes = await request(app)
      .post('/api/auth/login')
      .send({ email: 'admin@example.com', password: 'password123' });
    
    authToken = loginRes.body.token;
  });

  afterAll(async () => {
    await mongoose.connection.close();
  });

  beforeEach(async () => {
    await Truck.deleteMany({});
  });

  describe('GET /api/trucks', () => {
    it('should get all trucks', async () => {
      await Truck.create([
        { plateNumber: 'ABC-123', brand: 'Mercedes', model: 'Actros', year: 2020, capacity: 10000 },
        { plateNumber: 'XYZ-789', brand: 'Volvo', model: 'FH16', year: 2021, capacity: 12000 }
      ]);

      const res = await request(app)
        .get('/api/trucks')
        .set('Authorization', `Bearer ${authToken}`);

      expect(res.statusCode).toBe(200);
      expect(res.body.success).toBe(true);
      expect(res.body.data).toHaveLength(2);
    });

    it('should filter trucks by status', async () => {
      await Truck.create([
        { plateNumber: 'ABC-123', brand: 'Mercedes', model: 'Actros', status: 'active' },
        { plateNumber: 'XYZ-789', brand: 'Volvo', model: 'FH16', status: 'maintenance' }
      ]);

      const res = await request(app)
        .get('/api/trucks?status=active')
        .set('Authorization', `Bearer ${authToken}`);

      expect(res.statusCode).toBe(200);
      expect(res.body.data).toHaveLength(1);
      expect(res.body.data[0].status).toBe('active');
    });
  });

  describe('POST /api/trucks', () => {
    it('should create new truck', async () => {
      const res = await request(app)
        .post('/api/trucks')
        .set('Authorization', `Bearer ${authToken}`)
        .send({
          plateNumber: 'NEW-001',
          brand: 'Scania',
          model: 'R500',
          year: 2023,
          capacity: 15000,
          status: 'active'
        });

      expect(res.statusCode).toBe(201);
      expect(res.body.success).toBe(true);
      expect(res.body.data.plateNumber).toBe('NEW-001');
    });

    it('should not create truck with duplicate plate number', async () => {
      await Truck.create({
        plateNumber: 'DUP-001',
        brand: 'Mercedes',
        model: 'Actros'
      });

      const res = await request(app)
        .post('/api/trucks')
        .set('Authorization', `Bearer ${authToken}`)
        .send({
          plateNumber: 'DUP-001',
          brand: 'Volvo',
          model: 'FH16'
        });

      expect(res.statusCode).toBe(400);
    });
  });

  describe('PUT /api/trucks/:id/status', () => {
    it('should update truck status', async () => {
      const truck = await Truck.create({
        plateNumber: 'TST-001',
        brand: 'Mercedes',
        model: 'Actros',
        status: 'active'
      });

      const res = await request(app)
        .put(`/api/trucks/${truck._id}/status`)
        .set('Authorization', `Bearer ${authToken}`)
        .send({ status: 'maintenance' });

      expect(res.statusCode).toBe(200);
      expect(res.body.data.status).toBe('maintenance');
    });
  });

  describe('PUT /api/trucks/:id/mileage', () => {
    it('should update truck mileage', async () => {
      const truck = await Truck.create({
        plateNumber: 'MIL-001',
        brand: 'Mercedes',
        model: 'Actros',
        mileage: 50000
      });

      const res = await request(app)
        .put(`/api/trucks/${truck._id}/mileage`)
        .set('Authorization', `Bearer ${authToken}`)
        .send({ mileage: 55000 });

      expect(res.statusCode).toBe(200);
      expect(res.body.data.mileage).toBe(55000);
    });
  });

  describe('DELETE /api/trucks/:id', () => {
    it('should delete truck', async () => {
      const truck = await Truck.create({
        plateNumber: 'DEL-001',
        brand: 'Mercedes',
        model: 'Actros'
      });

      const res = await request(app)
        .delete(`/api/trucks/${truck._id}`)
        .set('Authorization', `Bearer ${authToken}`);

      expect(res.statusCode).toBe(200);
      expect(res.body.success).toBe(true);
    });
  });
});
