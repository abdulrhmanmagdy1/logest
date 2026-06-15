/**
 * ============================================
 * 🔔 Notification Model - نظام إدهام
 * User notification schema
 * ============================================
 */

const mongoose = require('mongoose');

const NotificationSchema = new mongoose.Schema({
  // Recipient
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true,
    index: true
  },
  
  // Notification Type
  type: {
    type: String,
    enum: [
      'shipment_created',
      'shipment_assigned',
      'shipment_picked_up',
      'shipment_in_transit',
      'shipment_delivered',
      'shipment_completed',
      'shipment_cancelled',
      'shipment_delayed',
      'invoice_generated',
      'invoice_paid',
      'invoice_overdue',
      'payment_received',
      'driver_arriving',
      'system_alert',
      'maintenance_due',
      'survey_reminder',
      'promotion',
      'announcement'
    ],
    required: true
  },
  
  // Content
  title: {
    type: String,
    required: true
  },
  message: {
    type: String,
    required: true
  },
  
  // Related Data
  data: {
    shipmentId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Shipment'
    },
    invoiceId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Invoice'
    },
    receiptId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Receipt'
    },
    trackingNumber: String,
    url: String,
    actionText: String,
    actionUrl: String
  },
  
  // Priority
  priority: {
    type: String,
    enum: ['low', 'normal', 'high', 'urgent'],
    default: 'normal'
  },
  
  // Channels
  channels: {
    push: {
      sent: { type: Boolean, default: false },
      sentAt: Date,
      delivered: { type: Boolean, default: false },
      deliveredAt: Date,
      error: String
    },
    email: {
      sent: { type: Boolean, default: false },
      sentAt: Date,
      delivered: { type: Boolean, default: false },
      deliveredAt: Date,
      error: String
    },
    sms: {
      sent: { type: Boolean, default: false },
      sentAt: Date,
      delivered: { type: Boolean, default: false },
      deliveredAt: Date,
      error: String
    },
    inApp: {
      sent: { type: Boolean, default: true },
      sentAt: { type: Date, default: Date.now }
    }
  },
  
  // Status
  read: {
    type: Boolean,
    default: false
  },
  readAt: Date,
  
  // Actions
  actions: [{
    type: {
      type: String,
      enum: ['view', 'accept', 'reject', 'pay', 'rate', 'dismiss']
    },
    label: String,
    url: String,
    performed: { type: Boolean, default: false },
    performedAt: Date
  }],
  
  // Expiry
  expiresAt: Date,
  
  // Metadata
  createdBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  createdAt: {
    type: Date,
    default: Date.now,
    index: true
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
});

// Indexes
NotificationSchema.index({ user: 1, createdAt: -1 });
NotificationSchema.index({ user: 1, read: 1 });
NotificationSchema.index({ type: 1 });
NotificationSchema.index({ 'data.shipmentId': 1 });

// Update timestamp
NotificationSchema.pre('save', function(next) {
  this.updatedAt = Date.now();
  next();
});

// Mark as read
NotificationSchema.methods.markAsRead = function() {
  this.read = true;
  this.readAt = new Date();
  return this.save();
};

// Get unread count (static method)
NotificationSchema.statics.getUnreadCount = async function(userId) {
  return await this.countDocuments({ user: userId, read: false });
};

module.exports = mongoose.model('Notification', NotificationSchema);
