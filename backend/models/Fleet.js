//
/**
 * ============================================
 * 🚛 Fleet Management - إدارة الأسطول المتقدم
 * ============================================
 */

const mongoose = require('mongoose');

// Vehicle Schema (Extended from Truck)
const VehicleSchema = new mongoose.Schema({
  vehicleNumber: {
    type: String,
    required: true,
    unique: true
  },
  type: {
    type: String,
    enum: ['truck', 'van', 'pickup', 'trailer', 'refrigerated', 'tanker', 'flatbed', 'container', 'specialized'],
    required: true
  },
  category: {
    type: String,
    enum: ['light', 'medium', 'heavy', 'extra_heavy'],
    required: true
  },
  make: {
    type: String,
    required: true
  },
  model: {
    type: String,
    required: true
  },
  year: {
    type: Number,
    required: true
  },
  vin: {
    type: String,
    required: true,
    unique: true
  },
  licensePlate: {
    number: { type: String, required: true },
    expiryDate: Date,
    issuingAuthority: String
  },
  specifications: {
    engine: {
      type: String,
      displacement: String,
      horsepower: Number,
      fuelType: {
        type: String,
        enum: ['diesel', 'gasoline', 'electric', 'hybrid', 'cng']
      }
    },
    transmission: {
      type: String,
      enum: ['manual', 'automatic', 'semi_automatic']
    },
    dimensions: {
      length: Number,
      width: Number,
      height: Number,
      unit: { type: String, default: 'm' }
    },
    capacity: {
      payload: { value: Number, unit: { type: String, default: 'kg' } },
      volume: { value: Number, unit: { type: String, default: 'cbm' } },
      fuelTank: { value: Number, unit: { type: String, default: 'liters' } }
    },
    axles: Number,
    wheels: Number
  },
  ownership: {
    type: {
      type: String,
      enum: ['owned', 'leased', 'rented', 'contracted'],
      default: 'owned'
    },
    acquisitionDate: Date,
    acquisitionCost: {
      amount: Number,
      currency: { type: String, default: 'SAR' }
    },
    vendor: {
      name: String,
      contact: String
    },
    warranty: {
      startDate: Date,
      endDate: Date,
      coverage: String
    },
    leaseDetails: {
      lessor: String,
      leaseStart: Date,
      leaseEnd: Date,
      monthlyPayment: Number,
      mileageLimit: Number
    }
  },
  status: {
    type: String,
    enum: ['active', 'maintenance', 'out_of_service', 'retired', 'sold', 'accident', 'inspection'],
    default: 'active'
  },
  assignment: {
    driver: { type: mongoose.Schema.Types.ObjectId, ref: 'Driver' },
    depot: { type: mongoose.Schema.Types.ObjectId, ref: 'Warehouse' },
    route: String,
    assignedSince: Date
  },
  odometer: {
    current: { type: Number, default: 0 },
    unit: { type: String, default: 'km' },
    lastUpdated: { type: Date, default: Date.now }
  },
  fuel: {
    currentLevel: { type: Number, default: 0 },
    tankCapacity: Number,
    fuelType: String,
    averageConsumption: Number, // per 100km
    lastRefuel: {
      date: Date,
      amount: Number,
      cost: Number,
      location: String,
      odometer: Number
    }
  },
  telematics: {
    deviceId: String,
    installedAt: Date,
    status: {
      type: String,
      enum: ['active', 'inactive', 'faulty'],
      default: 'active'
    },
    lastPing: Date,
    speed: Number,
    location: {
      lat: Number,
      lng: Number,
      address: String,
      updatedAt: Date
    },
    ignition: { type: Boolean, default: false },
    engineHours: Number,
    idleTime: Number,
    harshBraking: { type: Number, default: 0 },
    harshAcceleration: { type: Number, default: 0 },
    overspeeding: { type: Number, default: 0 }
  },
  maintenance: {
    lastService: {
      date: Date,
      type: String,
      odometer: Number,
      workShop: String,
      cost: Number
    },
    nextService: {
      date: Date,
      odometer: Number,
      type: String
    },
    serviceInterval: {
      distance: Number, // km
      time: Number // days
    }
  },
  inspections: {
    lastInspection: {
      date: Date,
      result: {
        type: String,
        enum: ['pass', 'fail', 'conditional']
      },
      inspector: String,
      nextDue: Date
    },
    registrationExpiry: Date,
    insuranceExpiry: Date,
    certifications: [{
      name: String,
      issuedBy: String,
      issuedDate: Date,
      expiryDate: Date,
      status: {
        type: String,
        enum: ['valid', 'expired', 'pending']
      }
    }]
  },
  documents: [{
    type: {
      type: String,
      enum: ['registration', 'insurance', 'inspection', 'warranty', 'manual', 'other']
    },
    name: String,
    fileUrl: String,
    issueDate: Date,
    expiryDate: Date,
    uploadedAt: { type: Date, default: Date.now }
  }],
  costs: {
    totalFuel: { type: Number, default: 0 },
    totalMaintenance: { type: Number, default: 0 },
    totalInsurance: { type: Number, default: 0 },
    totalTolls: { type: Number, default: 0 },
    totalFines: { type: Number, default: 0 },
    totalOther: { type: Number, default: 0 },
    currency: { type: String, default: 'SAR' }
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  createdAt: {
    type: Date,
    default: Date.now
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
});

VehicleSchema.index({ company: 1, status: 1 });
VehicleSchema.index({ vehicleNumber: 1 });
VehicleSchema.index({ vin: 1 });
VehicleSchema.index({ licensePlate: 1 });
VehicleSchema.index({ 'telematics.location': '2dsphere' });

// Trip Schema
const TripSchema = new mongoose.Schema({
  tripNumber: {
    type: String,
    required: true,
    unique: true
  },
  vehicle: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Vehicle',
    required: true
  },
  driver: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Driver',
    required: true
  },
  shipments: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Shipment'
  }],
  route: {
    origin: {
      address: String,
      coordinates: { lat: Number, lng: Number },
      timestamp: Date
    },
    destination: {
      address: String,
      coordinates: { lat: Number, lng: Number },
      timestamp: Date
    },
    waypoints: [{
      address: String,
      coordinates: { lat: Number, lng: Number },
      stopDuration: Number, // minutes
      arrivalTime: Date,
      departureTime: Date
    }],
    totalDistance: Number,
    estimatedDuration: Number,
    actualDuration: Number
  },
  schedule: {
    plannedStart: Date,
    plannedEnd: Date,
    actualStart: Date,
    actualEnd: Date
  },
  status: {
    type: String,
    enum: ['planned', 'in_progress', 'completed', 'cancelled', 'delayed'],
    default: 'planned'
  },
  metrics: {
    distance: {
      planned: Number,
      actual: Number
    },
    fuel: {
      estimated: Number,
      actual: Number,
      cost: Number
    },
    time: {
      driving: Number,
      idle: Number,
      stopped: Number,
      loading: Number,
      unloading: Number
    },
    efficiency: Number, // percentage
    co2Emissions: Number // kg
  },
  events: [{
    type: {
      type: String,
      enum: ['departure', 'arrival', 'pickup', 'delivery', 'rest_stop', 'refuel', 'breakdown', 'delay', 'incident']
    },
    timestamp: Date,
    location: {
      address: String,
      coordinates: { lat: Number, lng: Number }
    },
    description: String,
    odometer: Number
  }],
  issues: [{
    type: {
      type: String,
      enum: ['mechanical', 'traffic', 'weather', 'customer', 'documentation', 'other']
    },
    description: String,
    reportedAt: Date,
    resolvedAt: Date,
    impact: {
      delay: Number, // minutes
      cost: Number
    }
  }],
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  createdAt: {
    type: Date,
    default: Date.now
  }
});

TripSchema.index({ company: 1, status: 1 });
TripSchema.index({ vehicle: 1, 'schedule.plannedStart': -1 });

// Depot/Warehouse Schema (simplified for fleet)
const DepotSchema = new mongoose.Schema({
  name: String,
  code: String,
  type: {
    type: String,
    enum: ['main', 'regional', 'city', 'cross_dock']
  },
  location: {
    address: String,
    coordinates: { lat: Number, lng: Number }
  },
  capacity: {
    vehicles: Number,
    trailers: Number
  },
  facilities: [String],
  operatingHours: String,
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company'
  }
});

module.exports = {
  Vehicle: mongoose.model('Vehicle', VehicleSchema),
  Trip: mongoose.model('Trip', TripSchema),
  Depot: mongoose.model('Depot', DepotSchema)
};
