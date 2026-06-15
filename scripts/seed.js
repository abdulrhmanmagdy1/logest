/**
 * ============================================
 * 🌱 Database Seed Script - نظام إدهام
 * Edham Logistics - Database Seeding
 * ============================================
 */

require('dotenv').config();
const mongoose = require('mongoose');
const User = require('../models/User');
const Truck = require('../models/Truck');
const Shipment = require('../models/Shipment');
const OilSchedule = require('../models/OilSchedule');
const logger = require('../utils/logger');

const seedDatabase = async () => {
  try {
    await mongoose.connect(process.env.MONGODB_URI);
    logger.success('Connected to MongoDB');

    // Clear existing data
    await User.deleteMany({});
    await Truck.deleteMany({});
    await Shipment.deleteMany({});
    await OilSchedule.deleteMany({});

    logger.info('Cleared existing data');

    // Create admin user
    const admin = new User({
      name: 'مسؤول النظام',
      email: 'admin@edham.com',
      password: 'admin123456',
      phone: '0501234567',
      role: 'admin',
      department: 'operations',
      isVerified: true
    });
    await admin.save();
    logger.success('Admin user created');

    // Create sample users
    const supervisor = new User({
      name: 'مشرف العمليات',
      email: 'supervisor@edham.com',
      password: 'supervisor123',
      phone: '0502345678',
      role: 'supervisor',
      department: 'operations',
      isVerified: true
    });
    await supervisor.save();

    const accountant = new User({
      name: 'محاسب النظام',
      email: 'accountant@edham.com',
      password: 'accountant123',
      phone: '0503456789',
      role: 'accountant',
      department: 'finance',
      isVerified: true
    });
    await accountant.save();

    const client = new User({
      name: 'عميل تجريبي',
      email: 'client@edham.com',
      password: 'client123',
      phone: '0504567890',
      role: 'client',
      department: 'logistics',
      isVerified: true
    });
    await client.save();

    logger.success('Sample users created');

    // Create sample trucks
    const trucks = [];
    for (let i = 1; i <= 5; i++) {
      const truck = new Truck({
        truckNumber: `TRK-${i.toString().padStart(3, '0')}`,
        plateNumber: `ABZ-${i}${i}${i}`,
        model: 'Hino 500',
        brand: 'Hino',
        year: 2023,
        capacity: 10000,
        status: 'active',
        maintenanceStatus: 'good',
        lastMaintenanceDate: new Date()
      });
      trucks.push(await truck.save());

      // Create oil schedule for each truck
      const oilSchedule = new OilSchedule({
        truck: truck._id,
        lastChangeDate: new Date(),
        nextChangeDate: new Date(Date.now() + 90 * 24 * 60 * 60 * 1000),
        lastChangeKilometers: 0,
        nextChangeKilometers: 5000
      });
      await oilSchedule.save();
    }

    logger.success('Sample trucks created');

    // Create sample drivers
    const drivers = [];
    for (let i = 1; i <= 5; i++) {
      const driver = new User({
        name: `سائق ${i}`,
        email: `driver${i}@edham.com`,
        password: 'driver123456',
        phone: `050${i}234567`,
        role: 'driver',
        department: 'logistics',
        isVerified: true
      });
      drivers.push(await driver.save());
    }

    logger.success('Sample drivers created');

    // Assign drivers to trucks
    for (let i = 0; i < 5; i++) {
      await Truck.findByIdAndUpdate(trucks[i]._id, {
        driver: drivers[i]._id
      });
    }

    logger.success('Drivers assigned to trucks');

    // Create sample shipments
    const cities = ['الرياض', 'جدة', 'مكة', 'المدينة', 'الدمام'];
    for (let i = 0; i < 10; i++) {
      const pickupCity = cities[Math.floor(Math.random() * cities.length)];
      let deliveryCity = cities[Math.floor(Math.random() * cities.length)];
      while (deliveryCity === pickupCity) {
        deliveryCity = cities[Math.floor(Math.random() * cities.length)];
      }

      const shipment = new Shipment({
        shipmentNumber: `SHP-${Date.now()}-${i}`,
        client: client._id,
        description: 'شحنة نموذجية',
        weight: Math.floor(Math.random() * 5000) + 1000,
        quantity: Math.floor(Math.random() * 20) + 1,
        pickupLocation: {
          address: `شارع الملك فهد، ${pickupCity}`,
          city: pickupCity,
          latitude: 24.7136 + (Math.random() - 0.5) * 0.1,
          longitude: 46.6753 + (Math.random() - 0.5) * 0.1
        },
        deliveryLocation: {
          address: `حي النرجس، ${deliveryCity}`,
          city: deliveryCity,
          latitude: 21.5433 + (Math.random() - 0.5) * 0.1,
          longitude: 39.1727 + (Math.random() - 0.5) * 0.1
        },
        estimatedCost: Math.floor(Math.random() * 1000) + 200,
        status: ['pending', 'assigned', 'in_transit', 'delivered'][Math.floor(Math.random() * 4)]
      });

      if (shipment.status !== 'pending') {
        shipment.driver = drivers[i % 5]._id;
        shipment.truck = trucks[i % 5]._id;
      }

      if (shipment.status === 'in_transit') {
        shipment.actualPickupDate = new Date(Date.now() - Math.random() * 24 * 60 * 60 * 1000);
      }

      if (shipment.status === 'delivered') {
        shipment.actualPickupDate = new Date(Date.now() - Math.random() * 48 * 60 * 60 * 1000);
        shipment.actualDeliveryDate = new Date(Date.now() - Math.random() * 24 * 60 * 60 * 1000);
      }

      await shipment.save();
    }

    logger.success('Sample shipments created');

    logger.success('Database seeded successfully');
    console.log('\n========================================');
    console.log('📋 Login Credentials:');
    console.log('========================================');
    console.log('Admin: admin@edham.com / admin123456');
    console.log('Supervisor: supervisor@edham.com / supervisor123');
    console.log('Accountant: accountant@edham.com / accountant123');
    console.log('Client: client@edham.com / client123');
    console.log('Drivers: driver1@edham.com / driver123456');
    console.log('========================================\n');

    await mongoose.connection.close();
    process.exit(0);
  } catch (error) {
    logger.error('Seeding error', error);
    process.exit(1);
  }
};

seedDatabase();
