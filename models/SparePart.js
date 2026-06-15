const mongoose = require('mongoose');

const sparePartSchema = new mongoose.Schema({
  name: {
    type: String,
    required: true
  },
  partNumber: {
    type: String,
    required: true,
    unique: true
  },
  quantity: {
    type: Number,
    required: true,
    default: 0
  },
  minQuantity: {
    type: Number,
    required: true,
    default: 5
  },
  supplier: {
    type: String,
    required: true
  },
  unitPrice: {
    type: Number,
    required: true
  },
  truck: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Truck'
  },
  category: {
    type: String,
    enum: ['tire', 'oil', 'filter', 'brake', 'electrical', 'engine', 'other'],
    default: 'other'
  },
  location: {
    type: String,
    default: 'warehouse'
  },
  notes: {
    type: String
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

sparePartSchema.pre('save', function(next) {
  this.updatedAt = Date.now();
  next();
});

module.exports = mongoose.model('SparePart', sparePartSchema);
