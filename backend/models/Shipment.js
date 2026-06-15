/**
 * ============================================
 * 📦 Shipment Model - نظام إدهام
 * Shipment/Cargo schema for load management
 * ============================================
 */

const mongoose = require('mongoose');

const ShipmentSchema = new mongoose.Schema({
  // Tracking
  trackingNumber: {
    type: String,
    required: true,
    unique: true
  },
  
  // Client Info
  client: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  
  // Cargo Details
  cargo: {
    type: {
      type: String,
      enum: ['general', 'frozen', 'chilled', 'dry_ice', 'pharmaceutical', 'flowers', 'food'],
      required: true
    },
    description: {
      type: String,
      required: true
    },
    weight: {
      value: { type: Number, required: true },
      unit: { type: String, enum: ['kg', 'ton'], default: 'kg' }
    },
    dimensions: {
      length: Number,
      width: Number,
      height: Number,
      unit: { type: String, enum: ['cm', 'm'], default: 'cm' }
    },
    quantity: {
      type: Number,
      default: 1
    },
    temperature: {
      min: Number,
      max: Number,
      unit: { type: String, enum: ['celsius', 'fahrenheit'], default: 'celsius' }
    },
    specialRequirements: [String],
    hazardous: {
      type: Boolean,
      default: false
    },
    fragile: {
      type: Boolean,
      default: false
    }
  },
  
  // Locations
  pickup: {
    address: {
      street: { type: String, required: true },
      city: { type: String, required: true },
      region: { type: String, required: true },
      coordinates: {
        lat: Number,
        lng: Number
      }
    },
    contactName: String,
    contactPhone: String,
    scheduledDate: {
      type: Date,
      required: true
    },
    actualDate: Date,
    notes: String
  },
  
  delivery: {
    address: {
      street: { type: String, required: true },
      city: { type: String, required: true },
      region: { type: String, required: true },
      coordinates: {
        lat: Number,
        lng: Number
      }
    },
    contactName: String,
    contactPhone: String,
    scheduledDate: {
      type: Date,
      required: true
    },
    actualDate: Date,
    notes: String
  },
  
  // Route
  route: {
    distance: Number, // in km
    duration: Number, // in minutes
    polyline: String,
    checkpoints: [{
      location: {
        lat: Number,
        lng: Number
      },
      name: String,
      timestamp: Date
    }]
  },
  
  // Assignment
  driver: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    default: null
  },
  truck: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Truck',
    default: null
  },
  
  // Status
  status: {
    type: String,
    enum: [
      'pending',           // Request received
      'confirmed',         // Accepted by admin
      'assigned',          // Driver assigned
      'in_transit',        // Driver picked up cargo
      'at_pickup',         // At pickup location
      'picked_up',         // Cargo loaded
      'on_the_way',        // En route
      'at_delivery',       // At delivery location
      'delivered',         // Cargo delivered
      'completed',         // Job finished
      'cancelled',         // Cancelled
      'returned'           // Returned to origin
    ],
    default: 'pending'
  },
  
  // Status History
  statusHistory: [{
    status: String,
    timestamp: {
      type: Date,
      default: Date.now
    },
    location: {
      lat: Number,
      lng: Number
    },
    notes: String,
    updatedBy: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    }
  }],
  
  // Pricing
  pricing: {
    basePrice: Number,
    weightCharge: Number,
    distanceCharge: Number,
    temperatureCharge: Number,
    specialHandlingCharge: Number,
    tax: Number,
    discount: Number,
    total: Number,
    currency: {
      type: String,
      default: 'SAR'
    }
  },
  
  // Payment
  payment: {
    method: {
      type: String,
      enum: ['cash', 'credit_card', 'bank_transfer', 'invoice', 'wallet'],
      default: 'invoice'
    },
    status: {
      type: String,
      enum: ['pending', 'partial', 'paid', 'failed', 'refunded'],
      default: 'pending'
    },
    paidAmount: {
      type: Number,
      default: 0
    },
    paidAt: Date,
    transactionId: String
  },
  
  // Invoice
  invoice: {
    number: String,
    generatedAt: Date,
    dueDate: Date,
    sentAt: Date
  },
  
  // Receipt
  receipt: {
    number: String,
    signedBy: String,
    signatureImage: String,
    photos: [String],
    notes: String,
    deliveredAt: Date
  },
  
  // Documents
  documents: [{
    type: {
      type: String,
      enum: ['cargo_photo', 'invoice', 'receipt', 'waybill', 'customs', 'other']
    },
    url: String,
    uploadedAt: {
      type: Date,
      default: Date.now
    }
  }],
  
  // Survey
  survey: {
    completed: {
      type: Boolean,
      default: false
    },
    rating: {
      type: Number,
      min: 1,
      max: 5
    },
    feedback: String,
    completedAt: Date
  },
  
  // Tracking Updates
  trackingUpdates: [{
    timestamp: {
      type: Date,
      default: Date.now
    },
    location: {
      lat: Number,
      lng: Number
    },
    status: String,
    message: String
  }],
  
  // Priority
  priority: {
    type: String,
    enum: ['low', 'normal', 'high', 'urgent'],
    default: 'normal'
  },
  
  // Notes
  internalNotes: String,
  clientNotes: String,
  
  // Metadata
  createdBy: {
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

// Indexes for performance
ShipmentSchema.index({ trackingNumber: 1 });
ShipmentSchema.index({ client: 1, createdAt: -1 });
ShipmentSchema.index({ driver: 1, status: 1 });
ShipmentSchema.index({ status: 1, createdAt: -1 });
ShipmentSchema.index({ 'pickup.scheduledDate': 1 });
ShipmentSchema.index({ 'delivery.scheduledDate': 1 });

// Generate tracking number before saving
ShipmentSchema.pre('save', async function(next) {
  if (!this.trackingNumber) {
    const date = new Date();
    const year = date.getFullYear().toString().slice(-2);
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0');
    this.trackingNumber = `EDH${year}${month}${day}${random}`;
  }
  
  this.updatedAt = Date.now();
  next();
});

// Update status history on status change
ShipmentSchema.pre('save', function(next) {
  if (this.isModified('status')) {
    this.statusHistory.push({
      status: this.status,
      timestamp: new Date(),
      updatedBy: this.updatedBy
    });
  }
  next();
});

// Virtual for estimated delivery
ShipmentSchema.virtual('estimatedDelivery').get(function() {
  if (this.delivery.scheduledDate) {
    return this.delivery.scheduledDate;
  }
  
  // Calculate based on pickup date + route duration
  if (this.pickup.scheduledDate && this.route?.duration) {
    const pickup = new Date(this.pickup.scheduledDate);
    pickup.setMinutes(pickup.getMinutes() + this.route.duration);
    return pickup;
  }
  
  return null;
});

// Method to check if shipment is late
ShipmentSchema.methods.isLate = function() {
  if (this.status === 'delivered' || this.status === 'completed') {
    return false;
  }
  
  const now = new Date();
  const scheduled = this.delivery.scheduledDate;
  
  return scheduled && now > scheduled;
};

// Method to calculate progress percentage
ShipmentSchema.methods.getProgress = function() {
  const statusOrder = [
    'pending', 'confirmed', 'assigned', 'in_transit',
    'at_pickup', 'picked_up', 'on_the_way', 
    'at_delivery', 'delivered', 'completed'
  ];
  
  const currentIndex = statusOrder.indexOf(this.status);
  
  if (currentIndex === -1) return 0;
  if (this.status === 'cancelled') return 0;
  if (this.status === 'completed') return 100;
  
  return Math.round((currentIndex / (statusOrder.length - 1)) * 100);
};

module.exports = mongoose.model('Shipment', ShipmentSchema);
