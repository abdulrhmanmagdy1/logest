//
/**
 * ============================================
 * 📦 Warehouse Model - مخازن ومستودعات
 * ============================================
 */

const mongoose = require('mongoose');

const WarehouseSchema = new mongoose.Schema({
  name: {
    type: String,
    required: [true, 'Warehouse name is required'],
    trim: true
  },
  code: {
    type: String,
    required: true,
    unique: true,
    uppercase: true
  },
  type: {
    type: String,
    enum: ['main', 'distribution', 'cold_storage', 'bonded', 'virtual'],
    default: 'main'
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  location: {
    address: {
      street: String,
      city: String,
      region: String,
      country: { type: String, default: 'SA' },
      postalCode: String
    },
    coordinates: {
      lat: Number,
      lng: Number
    }
  },
  capacity: {
    totalArea: { value: Number, unit: { type: String, default: 'sqm' } },
    usableArea: { value: Number, unit: { type: String, default: 'sqm' } },
    maxPallets: Number,
    maxWeight: { value: Number, unit: { type: String, default: 'kg' } }
  },
  zones: [{
    name: String,
    code: String,
    type: {
      type: String,
      enum: ['receiving', 'storage', 'picking', 'shipping', 'cold', 'frozen', 'hazmat']
    },
    capacity: {
      maxPallets: Number,
      currentPallets: { type: Number, default: 0 }
    },
    temperature: {
      min: Number,
      max: Number,
      current: Number
    }
  }],
  operatingHours: {
    monday: { open: String, close: String, isOpen: Boolean },
    tuesday: { open: String, close: String, isOpen: Boolean },
    wednesday: { open: String, close: String, isOpen: Boolean },
    thursday: { open: String, close: String, isOpen: Boolean },
    friday: { open: String, close: String, isOpen: Boolean },
    saturday: { open: String, close: String, isOpen: Boolean },
    sunday: { open: String, close: String, isOpen: Boolean }
  },
  contacts: {
    manager: {
      name: String,
      phone: String,
      email: String
    },
    operations: {
      name: String,
      phone: String,
      email: String
    }
  },
  equipment: [{
    type: {
      type: String,
      enum: ['forklift', 'pallet_jack', 'conveyor', 'scanner', 'scale', 'wrap_machine', 'label_printer']
    },
    name: String,
    model: String,
    serialNumber: String,
    status: {
      type: String,
      enum: ['active', 'maintenance', 'retired'],
      default: 'active'
    },
    lastMaintenance: Date,
    nextMaintenance: Date
  }],
  status: {
    type: String,
    enum: ['active', 'inactive', 'under_maintenance', 'closed'],
    default: 'active'
  },
  settings: {
    allowCrossDocking: { type: Boolean, default: false },
    requireInspection: { type: Boolean, default: true },
    autoAllocate: { type: Boolean, default: true },
    fifoEnabled: { type: Boolean, default: true }
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

// Indexes
WarehouseSchema.index({ company: 1, status: 1 });
WarehouseSchema.index({ code: 1 });
WarehouseSchema.index({ 'location.coordinates': '2dsphere' });

// Methods
WarehouseSchema.methods.getUtilization = function() {
  const totalPallets = this.zones.reduce((sum, zone) => sum + (zone.capacity.maxPallets || 0), 0);
  const usedPallets = this.zones.reduce((sum, zone) => sum + (zone.capacity.currentPallets || 0), 0);
  return totalPallets > 0 ? (usedPallets / totalPallets) * 100 : 0;
};

WarehouseSchema.methods.isOpen = function() {
  const days = ['sunday', 'monday', 'tuesday', 'wednesday', 'thursday', 'friday', 'saturday'];
  const now = new Date();
  const dayName = days[now.getDay()];
  const daySchedule = this.operatingHours[dayName];
  
  if (!daySchedule || !daySchedule.isOpen) return false;
  
  const currentTime = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`;
  return currentTime >= daySchedule.open && currentTime <= daySchedule.close;
};

module.exports = mongoose.model('Warehouse', WarehouseSchema);

// Inventory Item Schema
const InventoryItemSchema = new mongoose.Schema({
  sku: {
    type: String,
    required: true,
    unique: true
  },
  name: {
    type: String,
    required: true
  },
  description: String,
  category: {
    type: String,
    enum: ['raw_material', 'finished_goods', 'packaging', 'spare_parts', 'consumables', 'other']
  },
  barcode: String,
  unitOfMeasure: {
    type: String,
    default: 'piece'
  },
  dimensions: {
    length: Number,
    width: Number,
    height: Number,
    weight: Number,
    unit: { type: String, default: 'cm' }
  },
  storageRequirements: {
    temperature: {
      min: Number,
      max: Number
    },
    humidity: {
      min: Number,
      max: Number
    },
    specialHandling: [String]
  },
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

module.exports.InventoryItem = mongoose.model('InventoryItem', InventoryItemSchema);

// Stock Level Schema
const StockLevelSchema = new mongoose.Schema({
  item: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'InventoryItem',
    required: true
  },
  warehouse: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Warehouse',
    required: true
  },
  zone: String,
  bin: String,
  quantity: {
    onHand: { type: Number, default: 0 },
    reserved: { type: Number, default: 0 },
    available: { type: Number, default: 0 },
    onOrder: { type: Number, default: 0 }
  },
  lotNumber: String,
  expiryDate: Date,
  receivedDate: Date,
  lastMovement: Date,
  status: {
    type: String,
    enum: ['good', 'damaged', 'expired', 'quarantine', 'hold'],
    default: 'good'
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
});

StockLevelSchema.index({ item: 1, warehouse: 1 });
StockLevelSchema.index({ warehouse: 1, zone: 1, bin: 1 });

module.exports.StockLevel = mongoose.model('StockLevel', StockLevelSchema);
