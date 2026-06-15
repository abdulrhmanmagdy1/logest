const mongoose = require('mongoose');

const oilScheduleSchema = new mongoose.Schema({
  truck: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Truck',
    required: true
  },
  lastChangeDate: {
    type: Date,
    required: true
  },
  nextChangeDate: {
    type: Date,
    required: true
  },
  currentMileage: {
    type: Number,
    required: true
  },
  oilType: {
    type: String,
    required: true,
    enum: ['synthetic', 'conventional', 'blend', 'diesel', 'hydraulic']
  },
  oilBrand: {
    type: String
  },
  oilCapacity: {
    type: Number,
    required: true
  },
  filterChanged: {
    type: Boolean,
    default: true
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

oilScheduleSchema.pre('save', function(next) {
  this.updatedAt = Date.now();
  next();
});

module.exports = mongoose.model('OilSchedule', oilScheduleSchema);
