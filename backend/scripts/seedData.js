/**
 * ============================================
 * 🌱 Seed Data Script - نظام إدهام
 * Populate database with demo data
 * ============================================
 */

const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
const User = require('../models/User');
const Shipment = require('../models/Shipment');
const Truck = require('../models/Truck');
const Invoice = require('../models/Invoice');
const logger = require('../utils/logger');

const seedData = async () => {
  try {
    // Connect to MongoDB
    await mongoose.connect(process.env.MONGODB_URI || 'mongodb://localhost:27017/logest');
    logger.info('Connected to MongoDB for seeding');

    // Clear existing data
    await User.deleteMany({});
    await Shipment.deleteMany({});
    await Truck.deleteMany({});
    await Invoice.deleteMany({});
    logger.info('Cleared existing data');

    // Create Admin
    const adminPassword = await bcrypt.hash('admin123', 10);
    const admin = await User.create({
      firstName: 'مدير',
      lastName: 'النظام',
      email: 'admin@edham.com',
      password: adminPassword,
      phone: '0500000000',
      role: 'admin',
      status: 'active',
      isEmailVerified: true,
      companyName: 'شركة إدهام'
    });
    logger.info(`Admin created: ${admin.email}`);

    // Create Supervisor
    const supervisorPassword = await bcrypt.hash('supervisor123', 10);
    const supervisor = await User.create({
      firstName: 'مشرف',
      lastName: 'العمليات',
      email: 'supervisor@edham.com',
      password: supervisorPassword,
      phone: '0501111111',
      role: 'supervisor',
      status: 'active',
      isEmailVerified: true
    });
    logger.info(`Supervisor created: ${supervisor.email}`);

    // Create Accountant
    const accountantPassword = await bcrypt.hash('accountant123', 10);
    const accountant = await User.create({
      firstName: 'محاسب',
      lastName: 'الشركة',
      email: 'accountant@edham.com',
      password: accountantPassword,
      phone: '0502222222',
      role: 'accountant',
      status: 'active',
      isEmailVerified: true
    });
    logger.info(`Accountant created: ${accountant.email}`);

    // Create Drivers
    const drivers = [];
    const driverPassword = await bcrypt.hash('driver123', 10);
    
    const driverData = [
      { firstName: 'أحمد', lastName: 'محمد', phone: '0503333333', licenseNumber: '123456789' },
      { firstName: 'خالد', lastName: 'عبدالله', phone: '0504444444', licenseNumber: '987654321' },
      { firstName: 'سعد', lastName: 'إبراهيم', phone: '0505555555', licenseNumber: '456789123' },
      { firstName: 'فهد', lastName: 'سلطان', phone: '0506666666', licenseNumber: '789123456' }
    ];

    for (const data of driverData) {
      const driver = await User.create({
        ...data,
        email: `${data.firstName.toLowerCase()}@edham.com`,
        password: driverPassword,
        role: 'driver',
        status: 'active',
        isEmailVerified: true,
        driverInfo: {
          licenseNumber: data.licenseNumber,
          licenseExpiry: new Date('2026-12-31'),
          isAvailable: true,
          currentLocation: {
            latitude: 24.7136,
            longitude: 46.6753
          },
          rating: 4.5 + Math.random() * 0.5,
          totalDeliveries: Math.floor(Math.random() * 500) + 100,
          joinedDate: new Date('2024-01-01')
        }
      });
      drivers.push(driver);
      logger.info(`Driver created: ${driver.email}`);
    }

    // Create Clients
    const clients = [];
    const clientPassword = await bcrypt.hash('client123', 10);
    
    const clientData = [
      { firstName: 'شركة', lastName: 'التمور الذهبية', companyName: 'التمور الذهبية', email: 'dates@example.com', phone: '0507777777' },
      { firstName: 'مؤسسة', lastName: 'الألبان الفاخرة', companyName: 'الألبان الفاخرة', email: 'dairy@example.com', phone: '0508888888' },
      { firstName: 'محمد', lastName: 'العمري', companyName: 'مؤسسة العمري', email: 'omari@example.com', phone: '0509999999' },
      { firstName: 'شركة', lastName: 'اللحوم الطازجة', companyName: 'اللحوم الطازجة', email: 'meat@example.com', phone: '0501010101' }
    ];

    for (const data of clientData) {
      const client = await User.create({
        ...data,
        password: clientPassword,
        role: 'client',
        status: 'active',
        isEmailVerified: true
      });
      clients.push(client);
      logger.info(`Client created: ${client.email}`);
    }

    // Create Trucks
    const trucks = [];
    const truckData = [
      { plateNumber: 'أ ب ت 1234', make: 'Mercedes', model: 'Actros', year: 2022, type: 'large', weight: 20 },
      { plateNumber: 'أ ب ت 5678', make: 'Volvo', model: 'FH', year: 2023, type: 'xl', weight: 25 },
      { plateNumber: 'أ ب ت 9012', make: 'MAN', model: 'TGX', year: 2021, type: 'large', weight: 18 },
      { plateNumber: 'أ ب ت 3456', make: 'Scania', model: 'R500', year: 2023, type: 'trailer', weight: 30 },
      { plateNumber: 'أ ب ت 7890', make: 'Mercedes', model: 'Arocs', year: 2022, type: 'large', weight: 22 }
    ];

    for (let i = 0; i < truckData.length; i++) {
      const data = truckData[i];
      const truck = await Truck.create({
        plateNumber: data.plateNumber,
        make: data.make,
        model: data.model,
        year: data.year,
        type: data.type,
        capacity: {
          weight: {
            value: data.weight,
            unit: 'ton'
          },
          volume: {
            value: 50 + i * 10,
            unit: 'm3'
          }
        },
        refrigeration: {
          hasRefrigeration: true,
          unitType: 'thermo-king',
          minTemp: i < 3 ? -5 : -25,
          maxTemp: i < 3 ? 10 : -18,
          currentTemp: i < 3 ? 4 : -20
        },
        status: i < 2 ? 'active' : 'inactive',
        documents: {
          registration: {
            number: `REG-${data.plateNumber.replace(/\s/g, '')}`,
            issueDate: new Date('2024-01-01'),
            expiryDate: new Date('2025-01-01'),
            documentUrl: '/docs/registration.pdf'
          },
          insurance: {
            provider: 'شركة التأمين الوطنية',
            policyNumber: `INS-${1000 + i}`,
            expiryDate: new Date('2025-06-01')
          }
        },
        maintenance: {
          lastMaintenance: new Date('2024-11-01'),
          nextMaintenance: new Date('2025-02-01'),
          maintenanceInterval: 15000
        },
        currentAssignment: i < 2 ? {
          driver: drivers[i]._id,
          shipment: null,
          assignedAt: new Date()
        } : null
      });
      trucks.push(truck);
      logger.info(`Truck created: ${truck.plateNumber}`);
    }

    // Create Shipments
    const shipmentStatuses = ['pending', 'confirmed', 'assigned', 'in_transit', 'picked_up', 'delivered', 'completed'];
    const cargoTypes = ['general', 'frozen', 'chilled', 'pharmaceutical', 'flowers', 'food'];
    const cities = [
      { name: 'الرياض', region: 'منطقة الرياض', lat: 24.7136, lng: 46.6753 },
      { name: 'جدة', region: 'منطقة مكة المكرمة', lat: 21.4858, lng: 39.1925 },
      { name: 'الدمام', region: 'المنطقة الشرقية', lat: 26.4207, lng: 50.0888 },
      { name: 'مكة', region: 'منطقة مكة المكرمة', lat: 21.3891, lng: 39.8579 },
      { name: 'المدينة المنورة', region: 'منطقة المدينة المنورة', lat: 24.5247, lng: 39.5692 }
    ];

    for (let i = 0; i < 15; i++) {
      const client = clients[i % clients.length];
      const driver = i < 8 ? drivers[i % drivers.length] : null;
      const status = shipmentStatuses[Math.min(i, shipmentStatuses.length - 1)];
      const fromCity = cities[Math.floor(Math.random() * cities.length)];
      let toCity = cities[Math.floor(Math.random() * cities.length)];
      while (toCity.name === fromCity.name) {
        toCity = cities[Math.floor(Math.random() * cities.length)];
      }

      const basePrice = 500 + Math.random() * 1500;
      const distance = 200 + Math.random() * 800;
      
      const shipment = await Shipment.create({
        trackingNumber: `EDH${String(2024).slice(2)}${String(i + 1).padStart(4, '0')}`,
        client: client._id,
        driver: driver?._id || null,
        cargo: {
          type: cargoTypes[Math.floor(Math.random() * cargoTypes.length)],
          description: `شحنة ${cargoTypes[Math.floor(Math.random() * cargoTypes.length)]} طازجة`,
          weight: {
            value: 100 + Math.random() * 900,
            unit: 'kg'
          },
          dimensions: {
            length: 120,
            width: 80,
            height: 100,
            unit: 'cm'
          },
          temperature: {
            min: -5,
            max: 10,
            critical: true
          },
          specialInstructions: 'Handle with care. Keep temperature controlled.'
        },
        pickup: {
          address: {
            street: 'شارع الملك فهد',
            city: fromCity.name,
            region: fromCity.region,
            country: 'المملكة العربية السعودية',
            zipCode: '12345',
            coordinates: {
              lat: fromCity.lat,
              lng: fromCity.lng
            }
          },
          contactPerson: {
            name: 'أحمد المستودع',
            phone: '0500000001',
            email: 'warehouse@example.com'
          },
          scheduledDate: new Date(Date.now() - i * 86400000),
          timeWindow: {
            start: '08:00',
            end: '12:00'
          },
          actualDate: i > 2 ? new Date(Date.now() - (i - 2) * 86400000) : null
        },
        delivery: {
          address: {
            street: 'شارع التحلية',
            city: toCity.name,
            region: toCity.region,
            country: 'المملكة العربية السعودية',
            zipCode: '54321',
            coordinates: {
              lat: toCity.lat,
              lng: toCity.lng
            }
          },
          contactPerson: {
            name: 'خالد المستلم',
            phone: '0500000002',
            email: 'receiver@example.com'
          },
          scheduledDate: new Date(Date.now() + (3 - i) * 86400000),
          timeWindow: {
            start: '14:00',
            end: '18:00'
          },
          actualDate: i > 5 ? new Date() : null
        },
        route: {
          estimatedDistance: distance,
          estimatedDuration: distance / 60,
          path: []
        },
        status: status,
        createdBy: client._id,
        statusHistory: [
          {
            status: 'pending',
            timestamp: new Date(Date.now() - i * 86400000),
            updatedBy: client._id,
            notes: 'تم إنشاء الطلب'
          }
        ],
        pricing: {
          basePrice: basePrice,
          distanceCharge: distance * 2,
          weightCharge: 0,
          specialHandlingFee: 100,
          fuelSurcharge: basePrice * 0.1,
          insurance: 50,
          vat: basePrice * 0.15,
          discount: 0,
          total: basePrice * 1.25 + distance * 2
        },
        payment: {
          status: i % 3 === 0 ? 'paid' : 'pending',
          method: i % 3 === 0 ? 'credit_card' : null,
          paidAt: i % 3 === 0 ? new Date() : null
        }
      });
      logger.info(`Shipment created: ${shipment.trackingNumber}`);
    }

    logger.info('✅ Seed data created successfully!');
    logger.info('');
    logger.info('🔑 Login Credentials:');
    logger.info('  Admin: admin@edham.com / admin123');
    logger.info('  Supervisor: supervisor@edham.com / supervisor123');
    logger.info('  Accountant: accountant@edham.com / accountant123');
    logger.info('  Drivers: [driver-name]@edham.com / driver123');
    logger.info('  Clients: [client-email] / client123');

    process.exit(0);
  } catch (error) {
    logger.error('Seed data error:', error);
    process.exit(1);
  }
};

seedData();
