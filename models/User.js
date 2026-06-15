/**
 * ============================================
 * 👤 User Model - نظام إدهام
 * Edham Logistics - User Schema
 * ============================================
 */

const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');

const userSchema = new mongoose.Schema({
  // Basic Information
  name: {
    type: String,
    required: [true, 'Name is required'],
    trim: true,
    maxlength: [100, 'Name cannot exceed 100 characters']
  },
  email: {
    type: String,
    required: [true, 'Email is required'],
    unique: true,
    lowercase: true,
    trim: true,
    match: [/^\S+@\S+\.\S+$/, 'Please provide a valid email']
  },
  password: {
    type: String,
    required: [true, 'Password is required'],
    minlength: [6, 'Password must be at least 6 characters'],
    select: false // Don't return password by default
  },
  phone: {
    type: String,
    trim: true,
    match: [/^[+]?[0-9]{10,15}$/, 'Please provide a valid phone number']
  },
  
  // Role & Permissions - Unified App Roles
  role: {
    type: String,
    enum: ['client', 'driver', 'employee', 'maintenance', 'accountant', 'supervisor', 'admin', 'super_admin', 'workshop'],
    default: 'client',
    required: true,
    index: true
  },

  // Role-specific data references
  customer_details: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Customer'
  },
  driver_details: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Driver'
  },
  company_details: {
    tax_number: String,
    commercial_registration: String,
    company_name: String
  },
  
  // Profile
  avatar: {
    type: String,
    default: null
  },
  nationality: {
    type: String,
    default: null
  },
  idNumber: {
    type: String,
    default: null // For Saudi ID verification
  },
  
  // Driver Specific Fields
  licenseNumber: {
    type: String,
    default: null
  },
  licenseExpiry: {
    type: Date,
    default: null
  },
  
  // Status
  isActive: {
    type: Boolean,
    default: true
  },
  isVerified: {
    type: Boolean,
    default: false
  },
  lastLogin: {
    type: Date,
    default: null
  },
  loginAttempts: {
    type: Number,
    default: 0
  },
  lockUntil: {
    type: Date,
    default: null
  },

  // Department
  department: {
    type: String,
    enum: ['operations', 'finance', 'maintenance', 'support', 'logistics'],
    default: 'logistics'
  },

  // Address
  address: {
    type: String,
    default: null
  },
  city: {
    type: String,
    default: null
  },

  // Soft Delete
  deletedAt: {
    type: Date,
    default: null
  }
}, {
  timestamps: true // Automatically adds createdAt and updatedAt
});

// Indexes for better query performance
userSchema.index({ email: 1 });
userSchema.index({ role: 1 });
userSchema.index({ isActive: 1, isDeleted: 1 });

// Hash password before saving
userSchema.pre('save', async function(next) {
  if (!this.isModified('password')) {
    return next();
  }

  // Support pre-hashed passwords (useful for migrations/tests/seeding).
  if (typeof this.password === 'string' && this.password.startsWith('$2')) {
    return next();
  }
  
  const salt = await bcrypt.genSalt(10);
  this.password = await bcrypt.hash(this.password, salt);
  next();
});

// Method to compare password
userSchema.methods.comparePassword = async function(candidatePassword) {
  return await bcrypt.compare(candidatePassword, this.password);
};

// Check if account is locked
userSchema.methods.isLocked = function() {
  return this.lockUntil && this.lockUntil > Date.now();
};

// Increment login attempts
userSchema.methods.incLoginAttempts = async function() {
  if (this.lockUntil && this.lockUntil < Date.now()) {
    return this.updateOne({
      $set: { loginAttempts: 1 },
      $unset: { lockUntil: 1 }
    });
  }

  const updates = { $inc: { loginAttempts: 1 } };
  const maxAttempts = 5;
  const lockTime = 2 * 60 * 60 * 1000; // 2 hours

  if (this.loginAttempts + 1 >= maxAttempts && !this.isLocked()) {
    updates.$set = { lockUntil: new Date(Date.now() + lockTime) };
  }

  return this.updateOne(updates);
};

// Reset login attempts
userSchema.methods.resetLoginAttempts = async function() {
  return this.updateOne({
    $set: { loginAttempts: 0 },
    $unset: { lockUntil: 1 }
  });
};

// Method to get public user data (without sensitive info)
userSchema.methods.toJSON = function() {
  const user = this.toObject();
  delete user.password;
  delete user.__v;
  return user;
};

module.exports = mongoose.model('User', userSchema);
