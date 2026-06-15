/**
 * ============================================
 * 🛣️ Tracking Session Model - نظام إدهام
 * Edham Logistics - Driver Tracking Session Schema
 * ============================================
 */

const mongoose = require('mongoose');

const trackingSessionSchema = new mongoose.Schema({
  // Driver information
  driverId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true,
    index: true
  },
  
  // Related shipment (if applicable)
  shipmentId: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Shipment',
    index: true
  },
  
  // Session status
  status: {
    type: String,
    enum: ['active', 'completed', 'paused', 'cancelled'],
    default: 'active',
    index: true
  },
  
  // Session timing
  startTime: {
    type: Date,
    required: true,
    default: Date.now
  },
  
  endTime: {
    type: Date
  },
  
  pausedDuration: {
    type: Number,
    default: 0 // in minutes
  },
  
  // Route information
  startLocation: {
    latitude: Number,
    longitude: Number,
    address: String,
    timestamp: Date
  },
  
  endLocation: {
    latitude: Number,
    longitude: Number,
    address: String,
    timestamp: Date
  },
  
  // Distance and duration tracking
  totalDistance: {
    type: Number,
    default: 0 // in kilometers
  },
  
  totalDuration: {
    type: Number,
    default: 0 // in minutes
  },
  
  averageSpeed: {
    type: Number,
    default: 0 // in km/h
  },
  
  maxSpeed: {
    type: Number,
    default: 0 // in km/h
  },
  
  // Location points for this session
  locations: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Location'
  }],
  
  // Session events
  events: [{
    type: {
      type: String,
      enum: ['start', 'pause', 'resume', 'stop', 'speed_limit', 'geofence_enter', 'geofence_exit'],
      required: true
    },
    timestamp: {
      type: Date,
      default: Date.now
    },
    location: {
      latitude: Number,
      longitude: Number,
      address: String
    },
    data: {
      type: mongoose.Schema.Types.Mixed,
      default: {}
    }
  }],
  
  // Session metadata
  purpose: {
    type: String,
    enum: ['delivery', 'pickup', 'maintenance', 'personal', 'other'],
    default: 'delivery'
  },
  
  notes: {
    type: String,
    maxlength: 1000
  },
  
  // Vehicle information at session start
  vehicleInfo: {
    odometerStart: Number,
    odometerEnd: Number,
    fuelStart: Number,
    fuelEnd: Number
  },
  
  // Quality metrics
  routeEfficiency: {
    type: Number,
    min: 0,
    max: 100
  },
  
  driverRating: {
    type: Number,
    min: 1,
    max: 5
  },
  
  // Created by
  createdBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  
  // Last location update timestamp
  lastLocationUpdate: {
    type: Date,
    default: Date.now
  },
  
  // Estimated arrival time
  estimatedArrival: {
    type: Date
  },
  
  // Session metadata
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
trackingSessionSchema.index({ driverId: 1, status: 1 });
trackingSessionSchema.index({ shipmentId: 1, status: 1 });
trackingSessionSchema.index({ startTime: -1 });
trackingSessionSchema.index({ driverId: 1, startTime: -1 });

// Virtual for session duration
trackingSessionSchema.virtual('duration').get(function() {
  if (this.endTime) {
    return Math.floor((this.endTime - this.startTime) / (1000 * 60)); // minutes
  }
  return Math.floor((new Date() - this.startTime) / (1000 * 60)); // minutes
});

// Virtual for formatted duration
trackingSessionSchema.virtual('formattedDuration').get(function() {
  const duration = this.duration;
  const hours = Math.floor(duration / 60);
  const minutes = duration % 60;
  
  if (hours > 0) {
    return `${hours} ساعة ${minutes} دقيقة`;
  }
  return `${minutes} دقيقة`;
});

// Virtual for formatted distance
trackingSessionSchema.virtual('formattedDistance').get(function() {
  return `${this.totalDistance.toFixed(2)} كم`;
});

// Virtual for status in Arabic
trackingSessionSchema.virtual('statusAr').get(function() {
  const statusMap = {
    active: 'نشط',
    completed: 'مكتمل',
    paused: 'متوقف',
    cancelled: 'ملغي'
  };
  return statusMap[this.status] || this.status;
});

// Pre-save middleware
trackingSessionSchema.pre('save', async function(next) {
  // Calculate average speed if distance and duration are available
  if (this.totalDistance > 0 && this.totalDuration > 0) {
    this.averageSpeed = (this.totalDistance / (this.totalDuration / 60)).toFixed(2);
  }
  
  // Update end time if status is completed and endTime is not set
  if (this.isModified('status') && this.status === 'completed' && !this.endTime) {
    this.endTime = new Date();
    this.totalDuration = this.duration;
  }
  
  next();
});

// Static methods
trackingSessionSchema.statics = {
  /**
   * Get active session for driver
   */
  async getActiveSession(driverId) {
    return await this.findOne({ 
      driverId, 
      status: 'active' 
    }).populate('locations');
  },
  
  /**
   * Get session history for driver
   */
  async getDriverSessions(driverId, options = {}) {
    const {
      page = 1,
      limit = 20,
      status,
      startDate,
      endDate
    } = options;
    
    const query = { driverId };
    if (status) query.status = status;
    if (startDate || endDate) {
      query.startTime = {};
      if (startDate) query.startTime.$gte = new Date(startDate);
      if (endDate) query.startTime.$lte = new Date(endDate);
    }
    
    const skip = (page - 1) * limit;
    
    const [sessions, total] = await Promise.all([
      this.find(query)
        .populate('shipmentId', 'trackingNumber origin destination')
        .sort({ startTime: -1 })
        .skip(skip)
        .limit(limit),
      this.countDocuments(query)
    ]);
    
    return {
      sessions,
      pagination: {
        page,
        limit,
        total,
        pages: Math.ceil(total / limit)
      }
    };
  },
  
  /**
   * Get session statistics
   */
  async getStatistics(filters = {}) {
    const matchQuery = { ...filters };
    
    const [
      totalSessions,
      activeSessions,
      totalDistance,
      totalDuration,
      averageSessionDuration,
      statusStats,
      purposeStats
    ] = await Promise.all([
      this.countDocuments(matchQuery),
      this.countDocuments({ ...matchQuery, status: 'active' }),
      this.aggregate([
        { $match: { ...matchQuery, status: 'completed' } },
        { $group: { _id: null, total: { $sum: '$totalDistance' } } }
      ]),
      this.aggregate([
        { $match: { ...matchQuery, status: 'completed' } },
        { $group: { _id: null, total: { $sum: '$totalDuration' } } }
      ]),
      this.aggregate([
        { $match: { ...matchQuery, status: 'completed' } },
        { $group: { _id: null, avg: { $avg: '$totalDuration' } } }
      ]),
      this.aggregate([
        { $match: matchQuery },
        { $group: { _id: '$status', count: { $sum: 1 } } }
      ]),
      this.aggregate([
        { $match: matchQuery },
        { $group: { _id: '$purpose', count: { $sum: 1 } } }
      ])
    ]);
    
    return {
      totalSessions,
      activeSessions,
      totalDistance: totalDistance[0]?.total || 0,
      totalDuration: totalDuration[0]?.total || 0,
      averageSessionDuration: averageSessionDuration[0]?.avg || 0,
      byStatus: statusStats,
      byPurpose: purposeStats
    };
  }
};

// Instance methods
trackingSessionSchema.methods = {
  /**
   * Add event to session
   */
  async addEvent(type, location, data = {}) {
    this.events.push({
      type,
      location,
      data,
      timestamp: new Date()
    });
    await this.save();
    return this.events[this.events.length - 1];
  },
  
  /**
   * Add location to session
   */
  async addLocation(locationId) {
    this.locations.push(locationId);
    this.lastLocationUpdate = new Date();
    await this.save();
  },
  
  /**
   * Complete session
   */
  async complete(endLocation) {
    this.status = 'completed';
    this.endTime = new Date();
    this.totalDuration = this.duration;
    if (endLocation) {
      this.endLocation = endLocation;
    }
    await this.save();
    
    // Add completion event
    await this.addEvent('stop', endLocation);
    
    return this;
  },
  
  /**
   * Pause session
   */
  async pause() {
    if (this.status !== 'active') {
      throw new Error('لا يمكن إيقاف جلسة غير نشطة');
    }
    
    this.status = 'paused';
    await this.addEvent('pause');
    await this.save();
    
    return this;
  },
  
  /**
   * Resume session
   */
  async resume() {
    if (this.status !== 'paused') {
      throw new Error('لا يمكن استئناف جلسة غير متوقفة');
    }
    
    this.status = 'active';
    await this.addEvent('resume');
    await this.save();
    
    return this;
  },
  
  /**
   * Cancel session
   */
  async cancel(reason) {
    this.status = 'cancelled';
    this.endTime = new Date();
    this.totalDuration = this.duration;
    this.metadata.cancellationReason = reason;
    
    await this.addEvent('stop', null, { reason });
    await this.save();
    
    return this;
  },
  
  /**
   * Update distance
   */
  async updateDistance(newDistance) {
    this.totalDistance = newDistance;
    await this.save();
    return this;
  },
  
  /**
   * Get session summary
   */
  getSummary() {
    return {
      id: this._id,
      driverId: this.driverId,
      shipmentId: this.shipmentId,
      status: this.statusAr,
      startTime: this.startTime,
      endTime: this.endTime,
      duration: this.formattedDuration,
      distance: this.formattedDistance,
      averageSpeed: this.averageSpeed,
      maxSpeed: this.maxSpeed,
      purpose: this.purpose,
      events: this.events.length,
      locations: this.locations.length
    };
  }
};

module.exports = mongoose.model('TrackingSession', trackingSessionSchema);
