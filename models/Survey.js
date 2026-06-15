const mongoose = require('mongoose');

const surveySchema = new mongoose.Schema({
  surveyNumber: {
    type: String,
    required: true,
    unique: true
  },
  shipment: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Shipment',
    required: true
  },
  driver: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  ratings: {
    routeQuality: {
      type: Number,
      min: 1,
      max: 5
    },
    truckCondition: {
      type: Number,
      min: 1,
      max: 5
    },
    communication: {
      type: Number,
      min: 1,
      max: 5
    },
    overallSatisfaction: {
      type: Number,
      min: 1,
      max: 5
    }
  },
  feedback: {
    type: String
  },
  issues: {
    type: String
  },
  suggestions: {
    type: String
  },
  submittedAt: {
    type: Date,
    default: Date.now
  }
});

module.exports = mongoose.model('Survey', surveySchema);
