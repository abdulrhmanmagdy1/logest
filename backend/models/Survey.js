/**
 * ============================================
 * 📋 Survey Model - نظام إدهام
 * Driver feedback survey schema
 * ============================================
 */

const mongoose = require('mongoose');

const SurveySchema = new mongoose.Schema({
  // References
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
  client: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  
  // Survey Status
  status: {
    type: String,
    enum: ['pending', 'sent', 'completed', 'expired'],
    default: 'pending'
  },
  
  // Timestamps
  sentAt: Date,
  completedAt: Date,
  expiresAt: Date,
  
  // Ratings
  ratings: {
    overall: {
      type: Number,
      min: 1,
      max: 5
    },
    driver: {
      professionalism: {
        type: Number,
        min: 1,
        max: 5
      },
      punctuality: {
        type: Number,
        min: 1,
        max: 5
      },
      communication: {
        type: Number,
        min: 1,
        max: 5
      },
      driving: {
        type: Number,
        min: 1,
        max: 5
      }
    },
    cargo: {
      handling: {
        type: Number,
        min: 1,
        max: 5
      },
      condition: {
        type: Number,
        min: 1,
        max: 5
      },
      temperature: {
        type: Number,
        min: 1,
        max: 5
      }
    },
    service: {
      booking: {
        type: Number,
        min: 1,
        max: 5
      },
      tracking: {
        type: Number,
        min: 1,
        max: 5
      },
      pricing: {
        type: Number,
        min: 1,
        max: 5
      }
    }
  },
  
  // Questions & Answers
  responses: [{
    questionId: String,
    question: String,
    answer: mongoose.Schema.Types.Mixed, // Can be string, number, array, etc.
    type: {
      type: String,
      enum: ['rating', 'text', 'choice', 'multiple_choice', 'boolean']
    }
  }],
  
  // Feedback
  positiveFeedback: String,
  negativeFeedback: String,
  suggestions: String,
  
  // Would Recommend
  wouldRecommend: {
    type: Boolean,
    default: null
  },
  
  // NPS Score (Net Promoter Score)
  npsScore: {
    type: Number,
    min: 0,
    max: 10
  },
  
  // Tags (for categorization)
  tags: [String],
  
  // Follow-up
  followUp: {
    required: {
      type: Boolean,
      default: false
    },
    reason: String,
    actionTaken: String,
    completed: {
      type: Boolean,
      default: false
    },
    completedAt: Date,
    completedBy: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    }
  },
  
  // Sentiment Analysis (if automated)
  sentiment: {
    overall: {
      type: String,
      enum: ['positive', 'neutral', 'negative']
    },
    confidence: Number, // 0-1
    analyzedAt: Date
  },
  
  // Metadata
  ipAddress: String,
  userAgent: String,
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
SurveySchema.index({ shipment: 1 });
SurveySchema.index({ driver: 1 });
SurveySchema.index({ client: 1 });
SurveySchema.index({ status: 1 });
SurveySchema.index({ createdAt: -1 });

// Update timestamp
SurveySchema.pre('save', function(next) {
  this.updatedAt = Date.now();
  next();
});

// Calculate average rating (virtual)
SurveySchema.virtual('averageRating').get(function() {
  const ratings = [];
  
  if (this.ratings?.overall) ratings.push(this.ratings.overall);
  
  if (this.ratings?.driver) {
    Object.values(this.ratings.driver).forEach(r => {
      if (r) ratings.push(r);
    });
  }
  
  if (ratings.length === 0) return 0;
  
  return (ratings.reduce((a, b) => a + b, 0) / ratings.length).toFixed(1);
});

// Mark as completed
SurveySchema.methods.complete = function(responses) {
  this.status = 'completed';
  this.completedAt = new Date();
  this.responses = responses || this.responses;
  return this.save();
};

module.exports = mongoose.model('Survey', SurveySchema);
