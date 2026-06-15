//
/**
 * ============================================
 * 🌡️ Sensor Data Model - مراقبة IoT للنقل المبرد
 * ============================================
 */

const mongoose = require('mongoose');

const SensorReadingSchema = new mongoose.Schema({
  // Sensor Info
  sensorId: {
    type: String,
    required: true,
    index: true
  },
  sensorType: {
    type: String,
    enum: ['temperature', 'humidity', 'vibration', 'location', 'door', 'fuel', 'speed'],
    required: true
  },
  
  // Location (for mobile sensors)
  location: {
    lat: Number,
    lng: Number,
    accuracy: Number,
    altitude: Number,
    speed: Number,
    heading: Number
  },
  
  // Reading Value
  value: {
    raw: mongoose.Schema.Types.Mixed,
    formatted: mongoose.Schema.Types.Mixed,
    unit: String
  },
  
  // For Temperature Sensors
  temperature: {
    value: Number,
    unit: {
      type: String,
      default: 'celsius'
    },
    zone: String // e.g., 'front', 'back', 'freezer'
  },
  
  // For Door Sensors
  doorStatus: {
    isOpen: Boolean,
    duration: Number, // seconds
    openedAt: Date,
    closedAt: Date
  },
  
  // Quality & Validation
  quality: {
    type: String,
    enum: ['good', 'fair', 'poor', 'invalid'],
    default: 'good'
  },
  validation: {
    isValid: Boolean,
    errors: [String],
    warnings: [String]
  },
  
  // Battery & Connection
  deviceStatus: {
    battery: Number, // percentage
    signal: Number, // signal strength
    connected: Boolean
  },
  
  // Related To
  relatedTo: {
    truck: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Truck'
    },
    shipment: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Shipment'
    },
    driver: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    }
  },
  
  // Timestamp
  recordedAt: {
    type: Date,
    default: Date.now,
    index: true
  },
  receivedAt: {
    type: Date,
    default: Date.now
  }
});

// Indexes for time-series queries
SensorReadingSchema.index({ sensorId: 1, recordedAt: -1 });
SensorReadingSchema.index({ 'relatedTo.truck': 1, recordedAt: -1 });
SensorReadingSchema.index({ 'relatedTo.shipment': 1, recordedAt: -1 });
SensorReadingSchema.index({ sensorType: 1, recordedAt: -1 });

const SensorAlertSchema = new mongoose.Schema({
  alertType: {
    type: String,
    enum: [
      'temperature_high',
      'temperature_low',
      'temperature_critical',
      'door_opened',
      'door_left_open',
      'route_deviation',
      'speeding',
      'battery_low',
      'signal_lost',
      'sensor_offline',
      'delay_detected'
    ],
    required: true
  },
  
  severity: {
    type: String,
    enum: ['info', 'warning', 'critical', 'emergency'],
    required: true
  },
  
  title: String,
  description: String,
  
  // Thresholds
  threshold: {
    min: Number,
    max: Number,
    value: Number,
    unit: String
  },
  
  // Related Data
  sensorId: String,
  relatedTo: {
    truck: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Truck'
    },
    shipment: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Shipment'
    }
  },
  
  // Status
  status: {
    type: String,
    enum: ['active', 'acknowledged', 'resolved', 'ignored'],
    default: 'active'
  },
  
  // Resolution
  resolution: {
    resolvedBy: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    resolvedAt: Date,
    action: String,
    notes: String
  },
  
  // Notifications Sent
  notifications: [{
    channel: {
      type: String,
      enum: ['push', 'sms', 'email', 'dashboard']
    },
    sentAt: Date,
    status: String
  }],
  
  createdAt: {
    type: Date,
    default: Date.now
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
});

// Sensor Configuration Schema
const SensorConfigSchema = new mongoose.Schema({
  sensorId: {
    type: String,
    required: true,
    unique: true
  },
  
  name: String,
  description: String,
  
  type: {
    type: String,
    enum: ['temperature', 'humidity', 'vibration', 'gps', 'door', 'fuel', 'speed'],
    required: true
  },
  
  // Assigned To
  assignedTo: {
    truck: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Truck'
    },
    zone: String // e.g., 'cargo_area', 'engine', 'cabin'
  },
  
  // Thresholds & Alerts
  thresholds: {
    temperature: {
      min: { type: Number, default: -25 },
      max: { type: Number, default: 25 },
      criticalMin: { type: Number, default: -30 },
      criticalMax: { type: Number, default: 30 }
    },
    door: {
      maxOpenDuration: { type: Number, default: 300 } // 5 minutes
    },
    speed: {
      max: { type: Number, default: 120 }
    }
  },
  
  // Alert Settings
  alerts: {
    enabled: {
      type: Boolean,
      default: true
    },
    channels: [{
      type: String,
      enum: ['push', 'sms', 'email']
    }],
    notifyRoles: [{
      type: String,
      enum: ['admin', 'supervisor', 'driver', 'client']
    }]
  },
  
  // Reporting Interval
  reportingInterval: {
    type: Number,
    default: 60 // seconds
  },
  
  status: {
    type: String,
    enum: ['active', 'inactive', 'maintenance', 'offline'],
    default: 'active'
  },
  
  lastReading: Date,
  
  createdAt: {
    type: Date,
    default: Date.now
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
});

// Create models
const SensorReading = mongoose.model('SensorReading', SensorReadingSchema);
const SensorAlert = mongoose.model('SensorAlert', SensorAlertSchema);
const SensorConfig = mongoose.model('SensorConfig', SensorConfigSchema);

// Static methods for analytics
SensorReadingSchema.statics.getTemperatureStats = async function(truckId, startDate, endDate) {
  return await this.aggregate([
    {
      $match: {
        'relatedTo.truck': new mongoose.Types.ObjectId(truckId),
        sensorType: 'temperature',
        recordedAt: { $gte: startDate, $lte: endDate }
      }
    },
    {
      $group: {
        _id: '$temperature.zone',
        avgTemp: { $avg: '$temperature.value' },
        minTemp: { $min: '$temperature.value' },
        maxTemp: { $max: '$temperature.value' },
        readings: { $sum: 1 },
        violations: {
          $sum: {
            $cond: [
              { $or: [
                { $lt: ['$temperature.value', -25] },
                { $gt: ['$temperature.value', 25] }
              ]},
              1,
              0
            ]
          }
        }
      }
    }
  ]);
};

module.exports = { SensorReading, SensorAlert, SensorConfig };
