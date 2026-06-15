/**
 * ============================================
 * 📍 Location Model - نظام إدهام
 * Edham Logistics - Driver Location Schema
 * ============================================
 */

const mongoose = require('mongoose');

const locationSchema = new mongoose.Schema({
  // Driver information (using driverId for consistency)
  driverId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true,
    index: true
  },
  
  // Keep backward compatibility
  driver: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  
  truck: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Truck'
  },
  
  shipment: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Shipment',
    index: true
  },
  
  // GPS coordinates
  latitude: {
    type: Number,
    required: true,
    min: -90,
    max: 90
  },
  
  longitude: {
    type: Number,
    required: true,
    min: -180,
    max: 180
  },
  
  // Address information
  address: {
    type: String,
    trim: true
  },
  
  // GPS accuracy and additional data
  accuracy: {
    type: Number,
    default: 0
  },
  
  altitude: {
    type: Number
  },
  
  speed: {
    type: Number,
    default: 0
  },
  
  heading: {
    type: Number,
    min: 0,
    max: 360
  },
  
  // Location source (GPS or manual)
  source: {
    type: String,
    enum: ['gps', 'manual'],
    default: 'gps'
  },
  
  // Timestamp
  timestamp: {
    type: Date,
    default: Date.now,
    index: true
  },
  
  // Active status (only one location per driver can be active)
  isActive: {
    type: Boolean,
    default: true,
    index: true
  },
  
  // Manual entry description
  description: {
    type: String,
    maxlength: 500
  },
  
  // Location metadata
  metadata: {
    type: mongoose.Schema.Types.Mixed,
    default: {}
  }
}, {
  timestamps: true,
  toJSON: { virtuals: true },
  toObject: { virtuals: true }
});

// Indexes for performance
locationSchema.index({ driverId: 1, timestamp: -1 });
locationSchema.index({ driverId: 1, isActive: 1 });
locationSchema.index({ shipmentId: 1, timestamp: -1 });
locationSchema.index({ source: 1, timestamp: -1 });

// Geospatial index for location-based queries
locationSchema.index({ latitude: 1, longitude: 1 });

// Virtual for formatted location
locationSchema.virtual('formattedLocation').get(function() {
  return `${this.latitude.toFixed(6)}, ${this.longitude.toFixed(6)}`;
});

// Virtual for time ago
locationSchema.virtual('timeAgo').get(function() {
  const now = new Date();
  const diff = now - this.timestamp;
  const minutes = Math.floor(diff / 60000);
  const hours = Math.floor(minutes / 60);
  const days = Math.floor(hours / 24);
  
  if (days > 0) return `منذ ${days} يوم`;
  if (hours > 0) return `منذ ${hours} ساعة`;
  if (minutes > 0) return `منذ ${minutes} دقيقة`;
  return 'الآن';
});

// Pre-save middleware
locationSchema.pre('save', async function(next) {
  // Ensure only one active location per driver
  if (this.isActive) {
    await this.constructor.updateMany(
      { driverId: this.driverId, isActive: true, _id: { $ne: this._id } },
      { isActive: false }
    );
  }
  next();
});

// Static methods
locationSchema.statics = {
  /**
   * Get driver's current active location
   */
  async getCurrentLocation(driverId) {
    return await this.findOne({ 
      driverId, 
      isActive: true 
    }).sort({ timestamp: -1 });
  },
  
  /**
   * Get location history for driver
   */
  async getDriverHistory(driverId, options = {}) {
    const {
      startDate,
      endDate,
      page = 1,
      limit = 50
    } = options;
    
    const query = { driverId };
    if (startDate || endDate) {
      query.timestamp = {};
      if (startDate) query.timestamp.$gte = new Date(startDate);
      if (endDate) query.timestamp.$lte = new Date(endDate);
    }
    
    const skip = (page - 1) * limit;
    
    const [locations, total] = await Promise.all([
      this.find(query)
        .sort({ timestamp: -1 })
        .skip(skip)
        .limit(limit),
      this.countDocuments(query)
    ]);
    
    return {
      locations,
      pagination: {
        page,
        limit,
        total,
        pages: Math.ceil(total / limit)
      }
    };
  }
};

module.exports = mongoose.model('Location', locationSchema);
