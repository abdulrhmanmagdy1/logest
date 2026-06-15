//
/**
 * ============================================
 * ⭐ Review & Rating Model - نظام التقييمات
 * ============================================
 */

const mongoose = require('mongoose');

const ReviewSchema = new mongoose.Schema({
  // Review Type
  type: {
    type: String,
    enum: ['client_to_driver', 'driver_to_client', 'client_to_shipment', 'internal'],
    required: true
  },
  
  // Who is being reviewed
  subject: {
    model: {
      type: String,
      enum: ['User', 'Shipment', 'Truck'],
      required: true
    },
    id: {
      type: mongoose.Schema.Types.ObjectId,
      required: true
    },
    name: String // Denormalized for quick access
  },
  
  // Who wrote the review
  reviewer: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  
  // Related Shipment (optional)
  shipment: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Shipment'
  },
  
  // Ratings
  ratings: {
    overall: {
      type: Number,
      min: 1,
      max: 5,
      required: true
    },
    // For Driver Reviews
    punctuality: {
      type: Number,
      min: 1,
      max: 5
    },
    professionalism: {
      type: Number,
      min: 1,
      max: 5
    },
    cargoHandling: {
      type: Number,
      min: 1,
      max: 5
    },
    communication: {
      type: Number,
      min: 1,
      max: 5
    },
    vehicleCondition: {
      type: Number,
      min: 1,
      max: 5
    },
    temperatureMaintenance: {
      type: Number,
      min: 1,
      max: 5
    },
    // For Client Reviews
    easeOfBooking: {
      type: Number,
      min: 1,
      max: 5
    },
    communication: {
      type: Number,
      min: 1,
      max: 5
    },
    paymentPromptness: {
      type: Number,
      min: 1,
      max: 5
    }
  },
  
  // Written Review
  review: {
    title: String,
    content: {
      type: String,
      maxlength: 2000
    },
    pros: [String],
    cons: [String],
    language: {
      type: String,
      default: 'ar'
    }
  },
  
  // Verification
  isVerified: {
    type: Boolean,
    default: false
  },
  verificationMethod: {
    type: String,
    enum: ['completed_shipment', 'manual', 'none']
  },
  
  // Media Attachments
  attachments: [{
    type: {
      type: String,
      enum: ['image', 'video']
    },
    url: String,
    thumbnail: String,
    description: String
  }],
  
  // Helpful Count
  helpful: {
    count: {
      type: Number,
      default: 0
    },
    users: [{
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    }]
  },
  
  // Response from subject
  response: {
    content: String,
    respondedAt: Date,
    respondedBy: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    }
  },
  
  // Moderation
  moderation: {
    status: {
      type: String,
      enum: ['pending', 'approved', 'rejected', 'flagged'],
      default: 'pending'
    },
    moderatedBy: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    moderatedAt: Date,
    reason: String,
    flags: [{
      type: String,
      enum: ['inappropriate', 'fake', 'spam', 'harassment']
    }]
  },
  
  // Visibility
  isPublic: {
    type: Boolean,
    default: true
  },
  
  // Created At
  createdAt: {
    type: Date,
    default: Date.now
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
});

// Indexes
ReviewSchema.index({ 'subject.id': 1, type: 1 });
ReviewSchema.index({ reviewer: 1, createdAt: -1 });
ReviewSchema.index({ 'ratings.overall': -1 });
ReviewSchema.index({ 'moderation.status': 1 });
ReviewSchema.index({ createdAt: -1 });

// Pre-save
ReviewSchema.pre('save', function(next) {
  this.updatedAt = Date.now();
  next();
});

// Static: Calculate average rating for a subject
ReviewSchema.statics.calculateAverageRating = async function(subjectId, subjectModel) {
  const result = await this.aggregate([
    {
      $match: {
        'subject.id': new mongoose.Types.ObjectId(subjectId),
        'subject.model': subjectModel,
        'moderation.status': 'approved'
      }
    },
    {
      $group: {
        _id: null,
        avgOverall: { $avg: '$ratings.overall' },
        avgPunctuality: { $avg: '$ratings.punctuality' },
        avgProfessionalism: { $avg: '$ratings.professionalism' },
        avgCargoHandling: { $avg: '$ratings.cargoHandling' },
        avgCommunication: { $avg: '$ratings.communication' },
        avgVehicleCondition: { $avg: '$ratings.vehicleCondition' },
        avgTemperatureMaintenance: { $avg: '$ratings.temperatureMaintenance' },
        totalReviews: { $sum: 1 },
        fiveStar: { $sum: { $cond: [{ $eq: ['$ratings.overall', 5] }, 1, 0] } },
        fourStar: { $sum: { $cond: [{ $eq: ['$ratings.overall', 4] }, 1, 0] } },
        threeStar: { $sum: { $cond: [{ $eq: ['$ratings.overall', 3] }, 1, 0] } },
        twoStar: { $sum: { $cond: [{ $eq: ['$ratings.overall', 2] }, 1, 0] } },
        oneStar: { $sum: { $cond: [{ $eq: ['$ratings.overall', 1] }, 1, 0] } }
      }
    }
  ]);
  
  return result[0] || null;
};

// Instance: Mark as helpful
ReviewSchema.methods.markHelpful = async function(userId) {
  if (!this.helpful.users.includes(userId)) {
    this.helpful.users.push(userId);
    this.helpful.count += 1;
    await this.save();
  }
  return this;
};

// Instance: Add response
ReviewSchema.methods.addResponse = async function(content, userId) {
  this.response = {
    content,
    respondedAt: new Date(),
    respondedBy: userId
  };
  return await this.save();
};

module.exports = mongoose.model('Review', ReviewSchema);
