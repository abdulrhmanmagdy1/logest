const mongoose = require('mongoose');

const employeeVehicleSchema = new mongoose.Schema({
  vehicleNumber: {
    type: String,
    required: true,
    unique: true
  },
  plateNumber: {
    type: String,
    required: true,
    unique: true
  },
  employee: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  model: {
    type: String,
    required: true
  },
  year: {
    type: Number
  },
  color: {
    type: String
  },
  type: {
    type: String,
    enum: ['car', 'truck', 'van', 'motorcycle', 'other'],
    default: 'car'
  },
  capacity: {
    type: Number
  },
  fuelType: {
    type: String,
    enum: ['petrol', 'diesel', 'electric', 'hybrid'],
    default: 'petrol'
  },
  insuranceExpiry: {
    type: Date
  },
  registrationExpiry: {
    type: Date
  },
  status: {
    type: String,
    enum: ['active', 'inactive', 'maintenance'],
    default: 'active'
  },
  currentLocation: {
    latitude: Number,
    longitude: Number,
    address: String,
    updatedAt: Date
  },
  documents: [{
    type: String,
    url: String,
    expiryDate: Date
  }],
  createdAt: {
    type: Date,
    default: Date.now
  }
});

module.exports = mongoose.model('EmployeeVehicle', employeeVehicleSchema);
