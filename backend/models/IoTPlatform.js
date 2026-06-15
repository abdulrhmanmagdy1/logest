//
/**
 * ============================================
 * 📡 IoT Platform - منصة إنترنت الأشياء
 * ============================================
 */

const mongoose = require('mongoose');

// IoT Device Schema
const IoTDeviceSchema = new mongoose.Schema({
  deviceId: {
    type: String,
    required: true,
    unique: true
  },
  name: String,
  type: {
    type: String,
    enum: ['gps_tracker', 'temperature_sensor', 'humidity_sensor', 'fuel_sensor', 'door_sensor', 'motion_sensor', 'vibration_sensor', 'pressure_sensor', 'camera', 'ecu', 'obd', 'rfid_reader', 'ble_beacon', 'smart_lock', 'weight_sensor'],
    required: true
  },
  category: {
    type: String,
    enum: ['vehicle', 'shipment', 'warehouse', 'container', 'asset'],
    required: true
  },
  manufacturer: {
    name: String,
    model: String,
    serialNumber: String,
    firmware: String,
    hardware: String
  },
  assignment: {
    entityType: {
      type: String,
      enum: ['Vehicle', 'Shipment', 'Warehouse', 'Container', 'Asset']
    },
    entityId: {
      type: mongoose.Schema.Types.ObjectId,
      refPath: 'assignment.entityType'
    },
    assignedAt: Date
  },
  connectivity: {
    protocol: {
      type: String,
      enum: ['cellular', 'wifi', 'bluetooth', 'ble', 'lora', 'nbiot', 'satellite', 'ethernet']
    },
    provider: String,
    simNumber: String,
    imei: String,
    iccid: String,
    network: {
      operator: String,
      signalStrength: Number,
      technology: String // 4G, 5G, etc.
    }
  },
  configuration: {
    reportingInterval: { type: Number, default: 300 }, // seconds
    sleepMode: { type: Boolean, default: false },
    sleepSchedule: {
      start: String,
      end: String
    },
    sensors: [{
      type: {
        type: String,
        enum: ['temperature', 'humidity', 'pressure', 'acceleration', 'gyroscope', 'magnetometer', 'light', 'gps', 'fuel', 'door', 'motion']
      },
      enabled: { type: Boolean, default: true },
      threshold: {
        min: Number,
        max: Number
      },
      calibration: {
        offset: Number,
        factor: Number
      }
    }],
    geofence: {
      enabled: { type: Boolean, default: false },
      fences: [{
        name: String,
        type: { type: String, enum: ['circle', 'polygon'] },
        coordinates: mongoose.Schema.Types.Mixed,
        radius: Number,
        action: { type: String, enum: ['enter', 'exit', 'both'] }
      }]
    },
    alerts: [{
      condition: String,
      threshold: Number,
      enabled: { type: Boolean, default: true }
    }]
  },
  status: {
    online: { type: Boolean, default: false },
    lastSeen: Date,
    battery: {
      level: Number,
      voltage: Number,
      charging: { type: Boolean, default: false }
    },
    signal: {
      strength: Number,
      quality: String
    },
    temperature: Number,
    firmwareStatus: {
      type: String,
      enum: ['up_to_date', 'update_available', 'updating']
    }
  },
  telemetry: {
    lastPayload: mongoose.Schema.Types.Mixed,
    lastPayloadAt: Date,
    totalMessages: { type: Number, default: 0 },
    totalBytes: { type: Number, default: 0 }
  },
  activation: {
    activated: { type: Boolean, default: false },
    activatedAt: Date,
    activatedBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User' }
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

IoTDeviceSchema.index({ company: 1, type: 1 });
IoTDeviceSchema.index({ deviceId: 1 });
IoTDeviceSchema.index({ 'assignment.entityId': 1 });

// Sensor Data Schema (Time Series)
const SensorDataSchema = new mongoose.Schema({
  device: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'IoTDevice',
    required: true
  },
  timestamp: {
    type: Date,
    required: true,
    default: Date.now
  },
  location: {
    lat: Number,
    lng: Number,
    accuracy: Number,
    altitude: Number,
    speed: Number,
    heading: Number,
    satellites: Number
  },
  sensors: {
    temperature: {
      value: Number,
      unit: { type: String, default: 'celsius' }
    },
    humidity: {
      value: Number,
      unit: { type: String, default: 'percent' }
    },
    pressure: {
      value: Number,
      unit: { type: String, default: 'hPa' }
    },
    fuel: {
      level: Number,
      unit: { type: String, default: 'percent' },
      volume: Number,
      consumption: Number
    },
    door: {
      status: { type: String, enum: ['open', 'closed', 'locked', 'unlocked'] }
    },
    motion: {
      detected: Boolean,
      intensity: Number
    },
    vibration: {
      level: Number,
      frequency: Number
    },
    light: {
      level: Number,
      unit: { type: String, default: 'lux' }
    },
    weight: {
      value: Number,
      unit: { type: String, default: 'kg' }
    }
  },
  vehicle: {
    ignition: Boolean,
    engineRpm: Number,
    speed: Number,
    odometer: Number,
    engineHours: Number,
    fuelLevel: Number,
    coolantTemp: Number,
    batteryVoltage: Number,
    dtc: [String], // Diagnostic Trouble Codes
    seatbelt: Boolean,
    handbrake: Boolean,
    doors: {
      frontLeft: { type: String, enum: ['open', 'closed'] },
      frontRight: { type: String, enum: ['open', 'closed'] },
      rearLeft: { type: String, enum: ['open', 'closed'] },
      rearRight: { type: String, enum: ['open', 'closed'] },
      trunk: { type: String, enum: ['open', 'closed'] }
    }
  },
  alerts: [{
    type: {
      type: String,
      enum: ['temperature_high', 'temperature_low', 'geofence_enter', 'geofence_exit', 'speeding', 'harsh_braking', 'harsh_acceleration', 'idle', 'fuel_low', 'door_open', 'motion_detected', 'tampering', 'low_battery', 'offline']
    },
    severity: {
      type: String,
      enum: ['info', 'warning', 'critical']
    },
    message: String,
    threshold: Number,
    value: Number
  }],
  metadata: {
    rssi: Number,
    snr: Number,
    battery: Number,
    firmware: String,
    sequence: Number
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

SensorDataSchema.index({ device: 1, timestamp: -1 });
SensorDataSchema.index({ company: 1, timestamp: -1 });
SensorDataSchema.index({ location: '2dsphere' });

// IoT Rule Schema
const IoTRuleSchema = new mongoose.Schema({
  name: String,
  description: String,
  enabled: { type: Boolean, default: true },
  trigger: {
    type: {
      type: String,
      enum: ['sensor_value', 'device_online', 'device_offline', 'geofence', 'schedule', 'anomaly']
    },
    condition: {
      sensor: String,
      operator: {
        type: String,
        enum: ['eq', 'ne', 'gt', 'lt', 'gte', 'lte', 'in', 'between']
      },
      value: mongoose.Schema.Types.Mixed,
      value2: mongoose.Schema.Types.Mixed, // for between
      duration: Number // seconds
    },
    schedule: {
      cron: String,
      timezone: String
    }
  },
  actions: [{
    type: {
      type: String,
      enum: ['notification', 'email', 'sms', 'webhook', 'command', 'alert', 'log']
    },
    config: mongoose.Schema.Types.Mixed
  }],
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

// Device Command Schema
const DeviceCommandSchema = new mongoose.Schema({
  device: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'IoTDevice',
    required: true
  },
  command: {
    type: {
      type: String,
      enum: ['reboot', 'update_firmware', 'change_interval', 'sleep', 'wake', 'lock', 'unlock', 'horn', 'light', 'request_location', 'request_status', 'custom']
    },
    parameters: mongoose.Schema.Types.Mixed
  },
  status: {
    type: String,
    enum: ['pending', 'sent', 'delivered', 'executed', 'failed', 'timeout'],
    default: 'pending'
  },
  priority: {
    type: String,
    enum: ['low', 'normal', 'high', 'critical'],
    default: 'normal'
  },
  sentAt: Date,
  deliveredAt: Date,
  executedAt: Date,
  result: mongoose.Schema.Types.Mixed,
  error: String,
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

module.exports = {
  IoTDevice: mongoose.model('IoTDevice', IoTDeviceSchema),
  SensorData: mongoose.model('SensorData', SensorDataSchema),
  IoTRule: mongoose.model('IoTRule', IoTRuleSchema),
  DeviceCommand: mongoose.model('DeviceCommand', DeviceCommandSchema)
};
