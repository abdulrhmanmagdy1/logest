/**
 * ============================================
 * 🚛 Edham Logistics - Driver Model Schema
 * نظام إدهام - نموذج السائقين في MongoDB
 * ============================================
 */

const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const driverSchema = new Schema({
  // Personal Information
  user_id: {
    type: Schema.Types.ObjectId,
    ref: 'User',
    required: true,
    unique: true,
    index: true
  },
  
  // Driver Status and Availability
  status: {
    type: String,
    enum: ['ACTIVE', 'INACTIVE', 'SUSPENDED', 'ON_DUTY', 'OFF_DUTY', 'BREAK'],
    default: 'INACTIVE',
    index: true
  },
  
  is_available: {
    type: Boolean,
    default: false,
    index: true
  },
  
  current_location: {
    type: {
      type: String,
      enum: ['Point'],
      default: 'Point'
    },
    coordinates: {
      type: [Number], // [longitude, latitude]
      default: [0, 0],
      index: '2dsphere'
    },
    last_updated: {
      type: Date,
      default: Date.now
    }
  },
  
  // Professional Information
  license_info: {
    license_number: {
      type: String,
      required: true,
      unique: true
    },
    license_type: {
      type: String,
      enum: ['HEAVY', 'MEDIUM', 'LIGHT', 'MOTORCYCLE'],
      required: true
    },
    issue_date: {
      type: Date,
      required: true
    },
    expiry_date: {
      type: Date,
      required: true
    },
    issuing_authority: String
  },
  
  // Vehicle Assignment
  assigned_vehicle: {
    truck_id: {
      type: Schema.Types.ObjectId,
      ref: 'Truck'
    },
    plate_number: String,
    assigned_at: Date,
    returned_at: Date
  },
  
  // Performance Metrics
  performance: {
    total_orders: {
      type: Number,
      default: 0,
      min: 0
    },
    completed_orders: {
      type: Number,
      default: 0,
      min: 0
    },
    cancelled_orders: {
      type: Number,
      default: 0,
      min: 0
    },
    total_distance_km: {
      type: Number,
      default: 0,
      min: 0
    },
    total_earnings: {
      type: Number,
      default: 0,
      min: 0
    },
    average_rating: {
      type: Number,
      default: 0,
      min: 0,
      max: 5
    },
    total_ratings: {
      type: Number,
      default: 0,
      min: 0
    },
    on_time_delivery_rate: {
      type: Number,
      default: 0,
      min: 0,
      max: 100
    }
  },
  
  // Current Task State Machine
  current_task: {
    order_id: {
      type: Schema.Types.ObjectId,
      ref: 'Order',
      sparse: true
    },
    task_state: {
      type: String,
      enum: [
        'IDLE',                    // No active task
        'TASK_ASSIGNED',          // New order assigned
        'TASK_ACCEPTED',           // Driver accepted the task
        'HEADING_TO_PICKUP',       // On the way to pickup
        'ARRIVED_AT_PICKUP',       // Arrived at pickup location
        'PICKUP_CONFIRMED',        // Pickup confirmed, loading started
        'LOADING_COMPLETE',        // Loading complete
        'HEADING_TO_DROPOFF',      // On the way to dropoff
        'ARRIVED_AT_DROPOFF',      // Arrived at dropoff location
        'UNLOADING_STARTED',       // Started unloading
        'DELIVERY_CONFIRMED',      // Delivery confirmed
        'TASK_COMPLETED'           // Task fully completed
      ],
      default: 'IDLE'
    },
    task_started_at: Date,
    last_state_change: {
      type: Date,
      default: Date.now
    },
    estimated_completion: Date,
    notes: String
  },
  
  // Location History for Performance Tracking
  location_history: [{
    timestamp: {
      type: Date,
      default: Date.now
    },
    location: {
      type: {
        type: String,
        enum: ['Point'],
        default: 'Point'
      },
      coordinates: [Number]
    },
    speed_kmh: Number,
    heading: Number, // Direction in degrees
    accuracy: Number, // GPS accuracy in meters
    source: {
      type: String,
      enum: ['GPS', 'NETWORK', 'PASSIVE'],
      default: 'GPS'
    }
  }],
  
  // Work Schedule and Preferences
  work_schedule: {
    preferred_shifts: [{
      day: {
        type: String,
        enum: ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY']
      },
      start_time: String, // HH:MM format
      end_time: String,   // HH:MM format
      is_available: Boolean
    }],
    max_orders_per_shift: {
      type: Number,
      default: 10,
      min: 1,
      max: 20
    },
    preferred_vehicle_types: [{
      type: String,
      enum: ['CARGO_TRUCK', 'PICKUP_VAN', 'LIGHT_TRUCK', 'HEAVY_TRUCK', 'MOTORCYCLE']
    }],
    service_areas: [{
      city: String,
      coordinates: {
        type: {
          type: String,
          enum: ['Polygon'],
          default: 'Polygon'
        },
        coordinates: [[[Number]]] // GeoJSON Polygon
      }
    }]
  },
  
  // Emergency and Safety Information
  emergency_contact: {
    name: String,
    phone: String,
    relationship: String
  },
  
  safety_certifications: [{
    certification_type: {
      type: String,
      enum: ['FIRST_AID', 'FIRE_SAFETY', 'DEFENSIVE_DRIVING', 'HAZARDOUS_MATERIALS']
    },
    certificate_number: String,
    issue_date: Date,
    expiry_date: Date,
    issuing_organization: String
  }],
  
  // Device and App Information
  device_info: {
    device_id: String,
    platform: {
      type: String,
      enum: ['ANDROID', 'IOS']
    },
    app_version: String,
    last_login: Date,
    push_notification_token: String
  },
  
  // Offline Sync Data
  offline_data: {
    pending_location_updates: [{
      timestamp: Date,
      location: {
        type: {
          type: String,
          enum: ['Point'],
          default: 'Point'
        },
        coordinates: [Number]
      },
      synced: {
        type: Boolean,
        default: false
      }
    }],
    pending_status_updates: [{
      order_id: Schema.Types.ObjectId,
      new_status: String,
      timestamp: Date,
      synced: {
        type: Boolean,
        default: false
      }
    }]
  },
  
  // Audit and System Fields
  created_at: {
    type: Date,
    default: Date.now,
    index: true
  },
  updated_at: {
    type: Date,
    default: Date.now
  },
  last_location_update: {
    type: Date,
    default: Date.now
  },
  is_deleted: {
    type: Boolean,
    default: false,
    index: true
  },
  version: {
    type: Number,
    default: 1
  }
}, {
  timestamps: true,
  toJSON: { virtuals: true },
  toObject: { virtuals: true }
});

// Indexes for performance
driverSchema.index({ user_id: 1 });
driverSchema.index({ status: 1, is_available: 1 });
driverSchema.index({ 'current_location.coordinates': '2dsphere' });
driverSchema.index({ 'current_task.order_id': 1 });
driverSchema.index({ 'current_task.task_state': 1 });
driverSchema.index({ last_location_update: 1 });
driverSchema.index({ 'performance.total_orders': -1 });
driverSchema.index({ 'performance.average_rating': -1 });

// Virtual fields
driverSchema.virtual('is_on_duty').get(function() {
  return this.status === 'ON_DUTY' || this.status === 'ACTIVE';
});

driverSchema.virtual('has_active_task').get(function() {
  return this.current_task.task_state !== 'IDLE';
});

driverSchema.virtual('task_completion_rate').get(function() {
  if (this.performance.total_orders === 0) return 0;
  return (this.performance.completed_orders / this.performance.total_orders) * 100;
});

driverSchema.virtual('location_update_frequency').get(function() {
  // Calculate based on recent location history
  const recentLocations = this.location_history.slice(-10); // Last 10 updates
  if (recentLocations.length < 2) return 0;
  
  const timeSpan = recentLocations[recentLocations.length - 1].timestamp - recentLocations[0].timestamp;
  const timeSpanMinutes = timeSpan / (1000 * 60);
  
  return timeSpanMinutes > 0 ? (recentLocations.length / timeSpanMinutes) : 0;
});

// Pre-save middleware
driverSchema.pre('save', function(next) {
  this.updated_at = new Date();
  
  // Update last_location_update when location changes
  if (this.isModified('current_location.coordinates')) {
    this.last_location_update = new Date();
  }
  
  // Add location to history if coordinates changed
  if (this.isModified('current_location.coordinates') && this.current_location.coordinates) {
    this.location_history.push({
      location: {
        type: 'Point',
        coordinates: this.current_location.coordinates
      },
      timestamp: new Date()
    });
    
    // Keep only last 1000 location points to prevent document bloat
    if (this.location_history.length > 1000) {
      this.location_history = this.location_history.slice(-1000);
    }
  }
  
  next();
});

// Static methods for state machine transitions
driverSchema.statics.canTransitionTo = function(currentState, targetState) {
  const validTransitions = {
    'IDLE': ['TASK_ASSIGNED'],
    'TASK_ASSIGNED': ['TASK_ACCEPTED', 'IDLE'], // Can reject task
    'TASK_ACCEPTED': ['HEADING_TO_PICKUP', 'IDLE'],
    'HEADING_TO_PICKUP': ['ARRIVED_AT_PICKUP'],
    'ARRIVED_AT_PICKUP': ['PICKUP_CONFIRMED'],
    'PICKUP_CONFIRMED': ['LOADING_COMPLETE'],
    'LOADING_COMPLETE': ['HEADING_TO_DROPOFF'],
    'HEADING_TO_DROPOFF': ['ARRIVED_AT_DROPOFF'],
    'ARRIVED_AT_DROPOFF': ['UNLOADING_STARTED'],
    'UNLOADING_STARTED': ['DELIVERY_CONFIRMED'],
    'DELIVERY_CONFIRMED': ['TASK_COMPLETED'],
    'TASK_COMPLETED': ['IDLE']
  };
  
  return validTransitions[currentState]?.includes(targetState) || false;
};

driverSchema.statics.findAvailableDrivers = function(pickupLocation, vehicleType, maxDistanceKm = 50) {
  return this.aggregate([
    {
      $match: {
        status: 'ON_DUTY',
        is_available: true,
        'current_task.task_state': 'IDLE'
      }
    },
    {
      $geoNear: {
        near: {
          type: 'Point',
          coordinates: pickupLocation
        },
        distanceField: 'distance',
        maxDistance: maxDistanceKm * 1000,
        spherical: true,
        query: {
          'work_schedule.service_areas': {
            $geoIntersects: {
              $geometry: {
                type: 'Point',
                coordinates: pickupLocation
              }
            }
          }
        }
      }
    },
    {
      $lookup: {
        from: 'trucks',
        localField: 'assigned_vehicle.truck_id',
        foreignField: '_id',
        as: 'vehicle'
      }
    },
    {
      $unwind: '$vehicle'
    },
    {
      $match: {
        'vehicle.type': vehicleType,
        'vehicle.status': 'AVAILABLE'
      }
    },
    {
      $sort: {
        distance: 1,
        'performance.average_rating': -1,
        'performance.on_time_delivery_rate': -1
      }
    },
    {
      $limit: 10
    }
  ]);
};

// Instance methods for task management
driverSchema.methods.assignTask = function(orderId, estimatedCompletion) {
  if (!this.constructor.canTransitionTo(this.current_task.task_state, 'TASK_ASSIGNED')) {
    throw new Error(`Cannot transition from ${this.current_task.task_state} to TASK_ASSIGNED`);
  }
  
  this.current_task = {
    order_id: orderId,
    task_state: 'TASK_ASSIGNED',
    task_started_at: new Date(),
    last_state_change: new Date(),
    estimated_completion: estimatedCompletion || new Date(Date.now() + 2 * 60 * 60 * 1000) // 2 hours default
  };
  
  return this.save();
};

driverSchema.methods.acceptTask = function() {
  if (!this.constructor.canTransitionTo(this.current_task.task_state, 'TASK_ACCEPTED')) {
    throw new Error(`Cannot transition from ${this.current_task.task_state} to TASK_ACCEPTED`);
  }
  
  this.current_task.task_state = 'TASK_ACCEPTED';
  this.current_task.last_state_change = new Date();
  
  return this.save();
};

driverSchema.methods.rejectTask = function(reason) {
  if (!this.constructor.canTransitionTo(this.current_task.task_state, 'IDLE')) {
    throw new Error(`Cannot transition from ${this.current_task.task_state} to IDLE`);
  }
  
  this.current_task = {
    task_state: 'IDLE',
    last_state_change: new Date(),
    notes: reason || 'Task rejected by driver'
  };
  
  return this.save();
};

driverSchema.methods.updateTaskState = function(newState, notes) {
  if (!this.constructor.canTransitionTo(this.current_task.task_state, newState)) {
    throw new Error(`Cannot transition from ${this.current_task.task_state} to ${newState}`);
  }
  
  this.current_task.task_state = newState;
  this.current_task.last_state_change = new Date();
  if (notes) {
    this.current_task.notes = notes;
  }
  
  // Update performance metrics
  if (newState === 'TASK_COMPLETED') {
    this.performance.completed_orders += 1;
  }
  
  return this.save();
};

driverSchema.methods.updateLocation = function(coordinates, speed, heading, accuracy, source) {
  this.current_location = {
    coordinates: coordinates,
    last_updated: new Date()
  };
  
  // Add to location history
  this.location_history.push({
    location: {
      type: 'Point',
      coordinates: coordinates
    },
    timestamp: new Date(),
    speed_kmh: speed,
    heading: heading,
    accuracy: accuracy,
    source: source || 'GPS'
  });
  
  // Keep location history manageable
  if (this.location_history.length > 1000) {
    this.location_history = this.location_history.slice(-1000);
  }
  
  return this.save();
};

driverSchema.methods.setAvailability = function(isAvailable, status = null) {
  this.is_available = isAvailable;
  if (status) {
    this.status = status;
  } else if (isAvailable) {
    this.status = 'ON_DUTY';
  } else {
    this.status = 'OFF_DUTY';
  }
  
  return this.save();
};

driverSchema.methods.addRating = function(rating, comment, orderId) {
  // Update average rating
  const totalRatings = this.performance.total_ratings + 1;
  const currentTotal = this.performance.average_rating * this.performance.total_ratings;
  this.performance.average_rating = (currentTotal + rating) / totalRatings;
  this.performance.total_ratings = totalRatings;
  
  // Store rating in separate collection for detailed analysis
  // This would be saved to a Ratings collection
  
  return this.save();
};

driverSchema.methods.calculatePerformanceMetrics = function() {
  // Calculate on-time delivery rate
  if (this.performance.total_orders > 0) {
    // This would be calculated based on actual delivery times vs estimates
    // For now, using a placeholder calculation
    this.performance.on_time_delivery_rate = Math.min(95, (this.performance.completed_orders / this.performance.total_orders) * 100);
  }
  
  return this.save();
};

// Helper function to generate driver ID
function generateDriverId() {
  const timestamp = Date.now().toString(36);
  const random = Math.random().toString(36).substr(2, 5);
  return `DRV-${timestamp}-${random}`.toUpperCase();
}

module.exports = mongoose.model('Driver', driverSchema);
