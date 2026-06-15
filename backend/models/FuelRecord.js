//
/**
 * ============================================
 * ⛽ Fuel Management Model - نظام إدارة الوقود
 * ============================================
 */

const mongoose = require('mongoose');

const FuelRecordSchema = new mongoose.Schema({
  // Vehicle Info
  truck: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Truck',
    required: true
  },
  
  // Fueling Info
  fuelingDate: {
    type: Date,
    required: true
  },
  
  station: {
    name: String,
    location: {
      lat: Number,
      lng: Number,
      address: String
    },
    isPartner: {
      type: Boolean,
      default: false
    }
  },
  
  // Fuel Details
  fuelType: {
    type: String,
    enum: ['diesel', 'gasoline', 'electric', 'hybrid'],
    default: 'diesel'
  },
  
  quantity: {
    value: {
      type: Number,
      required: true
    },
    unit: {
      type: String,
      default: 'liters'
    }
  },
  
  pricePerUnit: {
    type: Number,
    required: true
  },
  
  totalCost: {
    type: Number,
    required: true
  },
  
  // Odometer Readings
  odometerBefore: {
    type: Number,
    required: true
  },
  
  odometerAfter: {
    type: Number,
    required: true
  },
  
  distanceSinceLast: {
    type: Number
  },
  
  // Efficiency Calculation
  efficiency: {
    kmPerLiter: Number,
    litersPer100km: Number,
    costPerKm: Number
  },
  
  // Driver & Trip
  driver: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  
  relatedTrip: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Shipment'
  },
  
  // Payment
  payment: {
    method: {
      type: String,
      enum: ['cash', 'card', 'fleet_card', 'company_account'],
      default: 'fleet_card'
    },
    receiptNumber: String,
    receiptImage: String,
    status: {
      type: String,
      enum: ['pending', 'approved', 'rejected'],
      default: 'approved'
    }
  },
  
  // Expense Category
  expenseCategory: {
    type: String,
    enum: ['regular', 'maintenance', 'emergency', 'tank_full'],
    default: 'regular'
  },
  
  // Notes
  notes: String,
  
  // Metadata
  recordedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
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

// Pre-save: Calculate derived fields
FuelRecordSchema.pre('save', function(next) {
  // Calculate total cost
  this.totalCost = this.quantity.value * this.pricePerUnit;
  
  // Calculate distance
  if (this.odometerBefore && this.odometerAfter) {
    this.distanceSinceLast = this.odometerAfter - this.odometerBefore;
  }
  
  // Calculate efficiency
  if (this.distanceSinceLast && this.quantity.value > 0) {
    this.efficiency = {
      kmPerLiter: Math.round((this.distanceSinceLast / this.quantity.value) * 100) / 100,
      litersPer100km: Math.round((this.quantity.value / this.distanceSinceLast) * 100 * 100) / 100,
      costPerKm: Math.round((this.totalCost / this.distanceSinceLast) * 100) / 100
    };
  }
  
  this.updatedAt = Date.now();
  next();
});

// Static: Get fuel statistics for a truck
FuelRecordSchema.statics.getTruckFuelStats = async function(truckId, startDate, endDate) {
  return await this.aggregate([
    {
      $match: {
        truck: new mongoose.Types.ObjectId(truckId),
        fuelingDate: { $gte: startDate, $lte: endDate }
      }
    },
    {
      $group: {
        _id: null,
        totalFuel: { $sum: '$quantity.value' },
        totalCost: { $sum: '$totalCost' },
        totalDistance: { $sum: '$distanceSinceLast' },
        fuelingCount: { $sum: 1 },
        avgPricePerUnit: { $avg: '$pricePerUnit' },
        avgEfficiency: { $avg: '$efficiency.kmPerLiter' },
        minEfficiency: { $min: '$efficiency.kmPerLiter' },
        maxEfficiency: { $max: '$efficiency.kmPerLiter' }
      }
    }
  ]);
};

// Static: Get fleet fuel summary
FuelRecordSchema.statics.getFleetFuelSummary = async function(startDate, endDate) {
  return await this.aggregate([
    {
      $match: {
        fuelingDate: { $gte: startDate, $lte: endDate }
      }
    },
    {
      $group: {
        _id: '$fuelType',
        totalFuel: { $sum: '$quantity.value' },
        totalCost: { $sum: '$totalCost' },
        avgPricePerUnit: { $avg: '$pricePerUnit' }
      }
    }
  ]);
};

module.exports = mongoose.model('FuelRecord', FuelRecordSchema);
