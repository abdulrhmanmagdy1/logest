/**
 * ============================================
 * 📦 Edham Logistics - Order Model Schema
 * نظام إدهام - نموذج الطلبات في MongoDB
 * ============================================
 */

const mongoose = require('mongoose');
const Schema = mongoose.Schema;

const orderSchema = new Schema({
  // Customer Information
  customer_id: {
    type: Schema.Types.ObjectId,
    ref: 'User',
    required: true,
    index: true
  },
  
  // Order Basic Information
  order_number: {
    type: String,
    required: true,
    unique: true,
    index: true
  },
  
  status: {
    type: String,
    enum: ['PENDING', 'CONFIRMED', 'SEARCHING_FOR_DRIVER', 'ASSIGNED', 'PICKUP_CONFIRMED', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED', 'REFUNDED'],
    default: 'PENDING',
    index: true
  },
  
  // Route Information with GeoJSON for spatial queries
  route: {
    pickup: {
      address: {
        type: String,
        required: true
      },
      location: {
        type: {
          type: String,
          enum: ['Point'],
          default: 'Point'
        },
        coordinates: {
          type: [Number], // [longitude, latitude]
          required: true,
          index: '2dsphere' // For geospatial queries
        }
      },
      contact_person: {
        name: String,
        phone: String,
        email: String
      },
      instructions: String
    },
    dropoff: {
      address: {
        type: String,
        required: true
      },
      location: {
        type: {
          type: String,
          enum: ['Point'],
          default: 'Point'
        },
        coordinates: {
          type: [Number], // [longitude, latitude]
          required: true,
          index: '2dsphere'
        }
      },
      contact_person: {
        name: String,
        phone: String,
        email: String
      },
      instructions: String
    },
    distance_km: {
      type: Number,
      required: true,
      min: 0
    },
    estimated_duration_minutes: {
      type: Number,
      required: true,
      min: 0
    }
  },
  
  // Scheduling Information
  scheduling: {
    pickup_date: {
      type: Date,
      required: true
    },
    pickup_time: {
      type: String,
      required: true
    },
    preferred_delivery_date: Date,
    time_window: {
      start: String,
      end: String
    },
    is_urgent: {
      type: Boolean,
      default: false
    }
  },
  
  // Vehicle Information
  vehicle: {
    type: {
      type: String,
      required: true,
      enum: ['CARGO_TRUCK', 'PICKUP_VAN', 'LIGHT_TRUCK', 'HEAVY_TRUCK', 'MOTORCYCLE']
    },
    capacity_kg: {
      type: Number,
      required: true,
      min: 0
    },
    dimensions: {
      length: Number, // meters
      width: Number,  // meters
      height: Number  // meters
    },
    features: [String], // ['refrigerated', 'covered', 'open_bed', 'lift_gate']
    pricing: {
      base_rate_per_km: {
        type: Number,
        required: true,
        min: 0
      },
      minimum_charge: {
        type: Number,
        required: true,
        min: 0
      }
    }
  },
  
  // Additional Services
  services: {
    helpers: {
      count: {
        type: Number,
        default: 0,
        min: 0,
        max: 10
      },
      rate_per_helper: {
        type: Number,
        required: true,
        min: 0
      }
    },
    insurance: {
      enabled: {
        type: Boolean,
        default: false
      },
      coverage_amount: Number,
      premium_rate: Number
    },
    special_handling: {
      fragile: {
        type: Boolean,
        default: false
      },
      hazardous: {
        type: Boolean,
        default: false
      },
      oversized: {
        type: Boolean,
        default: false
      },
      temperature_controlled: {
        type: Boolean,
        default: false
      }
    }
  },
  
  // Pricing and Invoice Information
  invoice: {
    currency: {
      type: String,
      default: 'SAR'
    },
    items: [{
      description: String,
      quantity: Number,
      unit_price: Number,
      total: Number
    }],
    subtotal: {
      type: Number,
      required: true,
      min: 0
    },
    discounts: [{
      code: String,
      amount: Number,
      percentage: Number,
      description: String
    }],
    tax_rate: {
      type: Number,
      default: 0.15 // 15% VAT in Saudi Arabia
    },
    tax_amount: {
      type: Number,
      required: true,
      min: 0
    },
    total_amount: {
      type: Number,
      required: true,
      min: 0
    },
    payment_status: {
      type: String,
      enum: ['PENDING', 'PAID', 'FAILED', 'REFUNDED', 'PARTIALLY_REFUNDED'],
      default: 'PENDING'
    },
    payment_method: {
      type: String,
      enum: ['CREDIT_CARD', 'BANK_TRANSFER', 'CASH', 'WALLET']
    },
    payment_transaction_id: String
  },
  
  // Driver Assignment
  driver: {
    driver_id: {
      type: Schema.Types.ObjectId,
      ref: 'Driver'
    },
    assigned_at: Date,
    truck_id: {
      type: Schema.Types.ObjectId,
      ref: 'Truck'
    },
    estimated_arrival_time: Date,
    actual_pickup_time: Date,
    actual_delivery_time: Date
  },
  
  // Tracking Information
  tracking: {
    tracking_number: {
      type: String,
      unique: true,
      sparse: true
    },
    current_location: {
      type: {
        type: String,
        enum: ['Point'],
        default: 'Point'
      },
      coordinates: [Number]
    },
    last_updated: {
      type: Date,
      default: Date.now
    },
    events: [{
      timestamp: {
        type: Date,
        default: Date.now
      },
      event_type: {
        type: String,
        enum: ['ORDER_CREATED', 'DRIVER_ASSIGNED', 'PICKUP_CONFIRMED', 'IN_TRANSIT', 'DELIVERED', 'CANCELLED']
      },
      location: {
        type: {
          type: String,
          enum: ['Point'],
          default: 'Point'
        },
        coordinates: [Number]
      },
      description: String,
      metadata: Schema.Types.Mixed
    }]
  },
  
  // Cargo Details
  cargo: {
    description: String,
    weight_kg: {
      type: Number,
      required: true,
      min: 0
    },
    volume_cubic_meters: {
      type: Number,
      min: 0
    },
    package_count: {
      type: Number,
      required: true,
      min: 1
    },
    package_type: {
      type: String,
      enum: ['BOX', 'PALLET', 'CRATE', 'LOOSE', 'CONTAINER']
    },
    photos: [String], // URLs of cargo photos
    special_requirements: String
  },
  
  // Communication and Notifications
  communication: {
    customer_notifications: [{
      type: {
        type: String,
        enum: ['SMS', 'EMAIL', 'PUSH', 'WHATSAPP']
      },
      sent_at: Date,
      message: String,
      status: {
        type: String,
        enum: ['SENT', 'DELIVERED', 'FAILED'],
        default: 'SENT'
      }
    }],
    customer_notes: String,
    internal_notes: String
  },
  
  // Rating and Feedback
  rating: {
    customer_rating: {
      score: {
        type: Number,
        min: 1,
        max: 5
      },
      comment: String,
      rated_at: Date
    },
    driver_rating: {
      score: {
        type: Number,
        min: 1,
        max: 5
      },
      comment: String,
      rated_at: Date
    }
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
  created_by: {
    type: Schema.Types.ObjectId,
    ref: 'User'
  },
  updated_by: {
    type: Schema.Types.ObjectId,
    ref: 'User'
  },
  version: {
    type: Number,
    default: 1
  },
  is_deleted: {
    type: Boolean,
    default: false,
    index: true
  }
}, {
  timestamps: true,
  toJSON: { virtuals: true },
  toObject: { virtuals: true }
});

// Indexes for performance
orderSchema.index({ customer_id: 1, created_at: -1 });
orderSchema.index({ status: 1, created_at: -1 });
orderSchema.index({ 'driver.driver_id': 1, status: 1 });
orderSchema.index({ 'route.pickup.location': '2dsphere' });
orderSchema.index({ 'route.dropoff.location': '2dsphere' });
orderSchema.index({ 'tracking.tracking_number': 1 });
orderSchema.index({ order_number: 1 });
orderSchema.index({ 'invoice.payment_status': 1 });

// Virtual fields
orderSchema.virtual('duration_hours').get(function() {
  return this.route.estimated_duration_minutes / 60;
});

orderSchema.virtual('is_active').get(function() {
  return ['PENDING', 'CONFIRMED', 'SEARCHING_FOR_DRIVER', 'ASSIGNED', 'PICKUP_CONFIRMED', 'IN_TRANSIT'].includes(this.status);
});

orderSchema.virtual('can_be_cancelled').get(function() {
  return ['PENDING', 'CONFIRMED', 'SEARCHING_FOR_DRIVER'].includes(this.status);
});

orderSchema.virtual('progress_percentage').get(function() {
  const statusProgress = {
    'PENDING': 0,
    'CONFIRMED': 10,
    'SEARCHING_FOR_DRIVER': 20,
    'ASSIGNED': 30,
    'PICKUP_CONFIRMED': 50,
    'IN_TRANSIT': 75,
    'DELIVERED': 100,
    'CANCELLED': 0,
    'REFUNDED': 0
  };
  return statusProgress[this.status] || 0;
});

// Pre-save middleware
orderSchema.pre('save', function(next) {
  this.updated_at = new Date();
  
  // Generate order number if not exists
  if (!this.order_number) {
    this.order_number = generateOrderNumber();
  }
  
  // Generate tracking number if status is CONFIRMED and no tracking number exists
  if (this.status === 'CONFIRMED' && !this.tracking.tracking_number) {
    this.tracking.tracking_number = generateTrackingNumber();
  }
  
  next();
});

// Static methods
orderSchema.statics.calculatePrice = async function(orderData) {
  const { route, vehicle, services, discounts = [] } = orderData;
  
  // Base calculation: distance * rate per km
  let baseCost = route.distance_km * vehicle.pricing.base_rate_per_km;
  
  // Apply minimum charge if applicable
  if (baseCost < vehicle.pricing.minimum_charge) {
    baseCost = vehicle.pricing.minimum_charge;
  }
  
  // Add helper costs
  const helperCost = services.helpers.count * services.helpers.rate_per_helper;
  
  // Add insurance if enabled
  let insuranceCost = 0;
  if (services.insurance.enabled) {
    insuranceCost = services.insurance.coverage_amount * services.insurance.premium_rate;
  }
  
  // Special handling fees
  let handlingFees = 0;
  if (services.special_handling.fragile) handlingFees += 50;
  if (services.special_handling.hazardous) handlingFees += 200;
  if (services.special_handling.oversized) handlingFees += 100;
  if (services.special_handling.temperature_controlled) handlingFees += 150;
  
  // Calculate subtotal
  let subtotal = baseCost + helperCost + insuranceCost + handlingFees;
  
  // Apply discounts
  let totalDiscount = 0;
  for (const discount of discounts) {
    if (discount.amount) {
      totalDiscount += discount.amount;
    } else if (discount.percentage) {
      totalDiscount += subtotal * (discount.percentage / 100);
    }
  }
  
  subtotal = Math.max(0, subtotal - totalDiscount);
  
  // Calculate tax (15% VAT for Saudi Arabia)
  const taxRate = 0.15;
  const taxAmount = subtotal * taxRate;
  
  // Final total
  const totalAmount = subtotal + taxAmount;
  
  return {
    base_cost: baseCost,
    helper_cost: helperCost,
    insurance_cost: insuranceCost,
    handling_fees: handlingFees,
    subtotal: subtotal,
    total_discount: totalDiscount,
    tax_amount: taxAmount,
    tax_rate: taxRate,
    total_amount: totalAmount
  };
};

orderSchema.statics.findNearbyDrivers = async function(pickupLocation, maxDistanceKm = 50) {
  return this.aggregate([
    {
      $geoNear: {
        near: {
          type: 'Point',
          coordinates: pickupLocation
        },
        distanceField: 'distance',
        maxDistance: maxDistanceKm * 1000, // Convert km to meters
        spherical: true,
        query: {
          status: 'SEARCHING_FOR_DRIVER'
        }
      }
    },
    {
      $lookup: {
        from: 'drivers',
        localField: 'driver.driver_id',
        foreignField: '_id',
        as: 'driver_info'
      }
    },
    {
      $unwind: '$driver_info'
    },
    {
      $match: {
        'driver_info.is_available': true,
        'driver_info.current_location': {
          $near: {
            $geometry: {
              type: 'Point',
              coordinates: pickupLocation
            },
            $maxDistance: maxDistanceKm * 1000
          }
        }
      }
    },
    {
      $sort: { distance: 1 }
    },
    {
      $limit: 10
    }
  ]);
};

// Instance methods
orderSchema.methods.addTrackingEvent = function(eventType, description, location, metadata = {}) {
  this.tracking.events.push({
    event_type: eventType,
    description: description,
    location: location || this.tracking.current_location,
    metadata: metadata,
    timestamp: new Date()
  });
  this.tracking.last_updated = new Date();
  return this.save();
};

orderSchema.methods.assignDriver = function(driverId, truckId) {
  this.driver.driver_id = driverId;
  this.driver.truck_id = truckId;
  this.driver.assigned_at = new Date();
  this.status = 'ASSIGNED';
  return this.addTrackingEvent('DRIVER_ASSIGNED', 'Driver assigned to order');
};

orderSchema.methods.confirmPickup = function() {
  this.driver.actual_pickup_time = new Date();
  this.status = 'IN_TRANSIT';
  return this.addTrackingEvent('PICKUP_CONFIRMED', 'Driver has picked up the cargo');
};

orderSchema.methods.confirmDelivery = function() {
  this.driver.actual_delivery_time = new Date();
  this.status = 'DELIVERED';
  return this.addTrackingEvent('DELIVERED', 'Order has been delivered successfully');
};

// Helper functions
function generateOrderNumber() {
  const timestamp = Date.now();
  const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0');
  return `ED-${timestamp}-${random}`;
}

function generateTrackingNumber() {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
  let result = '';
  for (let i = 0; i < 12; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
}

module.exports = mongoose.model('Order', orderSchema);
