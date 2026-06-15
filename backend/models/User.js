/**
 * ============================================
 * 👤 User Model - نظام إدهام
 * User schema and authentication
 * ============================================
 */

const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');

const UserSchema = new mongoose.Schema({
  // Basic Info
  firstName: {
    type: String,
    required: [true, 'Please add a first name'],
    trim: true,
    maxlength: [50, 'First name cannot be more than 50 characters']
  },
  lastName: {
    type: String,
    required: [true, 'Please add a last name'],
    trim: true,
    maxlength: [50, 'Last name cannot be more than 50 characters']
  },
  email: {
    type: String,
    required: [true, 'Please add an email'],
    unique: true,
    lowercase: true,
    match: [
      /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
      'Please add a valid email'
    ]
  },
  phone: {
    type: String,
    required: [true, 'Please add a phone number'],
    match: [/^(05|5)\d{8}$/, 'Please add a valid Saudi phone number']
  },
  password: {
    type: String,
    required: [true, 'Please add a password'],
    minlength: [8, 'Password must be at least 8 characters'],
    select: false
  },
  
  // Role & Status
  role: {
    type: String,
    enum: ['client', 'driver', 'employee', 'supervisor', 'admin', 'accountant'],
    default: 'client'
  },
  status: {
    type: String,
    enum: ['active', 'inactive', 'suspended', 'pending'],
    default: 'pending'
  },
  
  // Profile
  avatar: {
    type: String,
    default: null
  },
  idNumber: {
    type: String,
    sparse: true,
    match: [/\d{10}/, 'Please add a valid ID number']
  },
  
  // Company Info (for clients)
  companyName: {
    type: String,
    trim: true
  },
  commercialRegistration: {
    type: String,
    sparse: true
  },
  vatNumber: {
    type: String,
    sparse: true
  },
  
  // Address
  address: {
    street: String,
    city: String,
    region: String,
    zipCode: String,
    coordinates: {
      lat: Number,
      lng: Number
    }
  },
  
  // Driver specific fields
  driverInfo: {
    licenseNumber: String,
    licenseExpiry: Date,
    truckAssigned: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'Truck'
    },
    currentLocation: {
      lat: Number,
      lng: Number,
      updatedAt: Date
    },
    rating: {
      type: Number,
      default: 0,
      min: 0,
      max: 5
    },
    totalTrips: {
      type: Number,
      default: 0
    },
    isAvailable: {
      type: Boolean,
      default: true
    }
  },
  
  // Notifications
  notificationSettings: {
    email: { type: Boolean, default: true },
    sms: { type: Boolean, default: true },
    push: { type: Boolean, default: true }
  },
  fcmToken: String,
  
  // Security
  loginAttempts: {
    type: Number,
    default: 0
  },
  lockUntil: Date,
  passwordChangedAt: Date,
  passwordResetToken: String,
  passwordResetExpire: Date,
  
  // Timestamps
  lastLogin: Date,
  createdAt: {
    type: Date,
    default: Date.now
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
});

// Virtual for full name
UserSchema.virtual('fullName').get(function() {
  return `${this.firstName} ${this.lastName}`;
});

// Encrypt password using bcrypt
UserSchema.pre('save', async function(next) {
  if (!this.isModified('password')) {
    next();
  }
  
  const salt = await bcrypt.genSalt(12);
  this.password = await bcrypt.hash(this.password, salt);
  
  // Update passwordChangedAt
  if (!this.isNew) {
    this.passwordChangedAt = Date.now() - 1000;
  }
  
  this.updatedAt = Date.now();
});

// Sign JWT and return
UserSchema.methods.getSignedJwtToken = function() {
  return jwt.sign(
    { id: this._id, role: this.role },
    process.env.JWT_SECRET || 'edham-secret-key',
    { expiresIn: process.env.JWT_EXPIRE || '30d' }
  );
};

// Match user entered password to hashed password in database
UserSchema.methods.matchPassword = async function(enteredPassword) {
  return await bcrypt.compare(enteredPassword, this.password);
};

// Check if password was changed after token was issued
UserSchema.methods.changedPasswordAfter = function(JWTTimestamp) {
  if (this.passwordChangedAt) {
    const changedTimestamp = parseInt(this.passwordChangedAt.getTime() / 1000, 10);
    return JWTTimestamp < changedTimestamp;
  }
  return false;
};

// Increment login attempts
UserSchema.methods.incrementLoginAttempts = function() {
  // If we have a previous lock that has expired, restart at 1
  if (this.lockUntil && this.lockUntil < Date.now()) {
    return this.updateOne({
      $set: { loginAttempts: 1 },
      $unset: { lockUntil: 1 }
    });
  }
  
  const updates = { $inc: { loginAttempts: 1 } };
  
  // Lock account after 5 failed attempts
  if (this.loginAttempts + 1 >= 5 && !this.isLocked) {
    updates.$set = { lockUntil: Date.now() + 2 * 60 * 60 * 1000 }; // 2 hours
  }
  
  return this.updateOne(updates);
};

// Check if account is locked
UserSchema.virtual('isLocked').get(function() {
  return !!(this.lockUntil && this.lockUntil > Date.now());
});

// Create password reset token
UserSchema.methods.createPasswordResetToken = function() {
  const resetToken = require('crypto').randomBytes(32).toString('hex');
  
  this.passwordResetToken = require('crypto')
    .createHash('sha256')
    .update(resetToken)
    .digest('hex');
  
  this.passwordResetExpire = Date.now() + 10 * 60 * 1000; // 10 minutes
  
  return resetToken;
};

module.exports = mongoose.model('User', UserSchema);
