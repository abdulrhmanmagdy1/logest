//
/**
 * ============================================
 * 🤖 Advanced Analytics & ML Models - التحليلات المتقدمة والذكاء الاصطناعي
 * ============================================
 */

const mongoose = require('mongoose');

// ML Model Schema
const MLModelSchema = new mongoose.Schema({
  modelId: {
    type: String,
    required: true,
    unique: true
  },
  name: String,
  description: String,
  type: {
    type: String,
    enum: ['classification', 'regression', 'clustering', 'forecasting', 'anomaly_detection', 'recommendation', 'nlp', 'computer_vision'],
    required: true
  },
  purpose: {
    type: String,
    enum: ['demand_forecast', 'route_optimization', 'delivery_time_prediction', 'pricing_optimization', 'customer_churn', 'fraud_detection', 'maintenance_prediction', 'sentiment_analysis', 'document_classification', 'object_detection'],
    required: true
  },
  version: {
    type: String,
    default: '1.0.0'
  },
  status: {
    type: String,
    enum: ['training', 'testing', 'deployed', 'archived', 'failed'],
    default: 'training'
  },
  architecture: {
    algorithm: String,
    framework: {
      type: String,
      enum: ['tensorflow', 'pytorch', 'sklearn', 'xgboost', 'lightgbm', 'custom']
    },
    layers: [{
      type: String,
      units: Number,
      activation: String
    }],
    hyperparameters: mongoose.Schema.Types.Mixed
  },
  training: {
    dataset: {
      size: Number,
      features: [String],
      startDate: Date,
      endDate: Date
    },
    metrics: {
      accuracy: Number,
      precision: Number,
      recall: Number,
      f1Score: Number,
      mae: Number,
      rmse: Number,
      r2: Number
    },
    confusionMatrix: [[Number]],
    featureImportance: [{
      feature: String,
      importance: Number
    }],
    startedAt: Date,
    completedAt: Date,
    duration: Number // minutes
  },
  deployment: {
    endpoint: String,
    method: {
      type: String,
      enum: ['realtime', 'batch', 'streaming']
    },
    deployedAt: Date,
    lastPredictionAt: Date,
    totalPredictions: { type: Number, default: 0 },
    averageLatency: Number // ms
  },
  features: [{
    name: String,
    type: {
      type: String,
      enum: ['numeric', 'categorical', 'text', 'datetime', 'geospatial', 'image']
    },
    source: String,
    preprocessing: String,
    required: Boolean
  }],
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
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

// Prediction Schema
const PredictionSchema = new mongoose.Schema({
  predictionId: {
    type: String,
    required: true,
    unique: true
  },
  model: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'MLModel',
    required: true
  },
  input: mongoose.Schema.Types.Mixed,
  output: mongoose.Schema.Types.Mixed,
  confidence: Number,
  probabilities: mongoose.Schema.Types.Mixed,
  explanation: {
    type: String,
    enum: ['shap', 'lime', 'attention', 'feature_importance', 'none'],
    shapValues: mongoose.Schema.Types.Mixed,
    topFeatures: [{
      feature: String,
      value: Number,
      impact: String
    }]
  },
  actual: mongoose.Schema.Types.Mixed, // For evaluation
  accuracy: Number, // After actual is known
  latency: Number, // ms
  metadata: {
    user: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    entityType: String,
    entityId: mongoose.Schema.Types.ObjectId,
    timestamp: {
      type: Date,
      default: Date.now
    }
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

PredictionSchema.index({ model: 1, 'metadata.timestamp': -1 });

// Anomaly Detection Schema
const AnomalySchema = new mongoose.Schema({
  anomalyId: {
    type: String,
    required: true,
    unique: true
  },
  type: {
    type: String,
    enum: ['shipment_delay', 'route_deviation', 'fuel_consumption', 'driver_behavior', 'vehicle_maintenance', 'warehouse_capacity', 'cost_variance', 'demand_spike', 'document_fraud', 'payment_anomaly'],
    required: true
  },
  severity: {
    type: String,
    enum: ['low', 'medium', 'high', 'critical'],
    required: true
  },
  entityType: {
    type: String,
    enum: ['Shipment', 'Driver', 'Vehicle', 'Warehouse', 'Route', 'Invoice', 'Customer'],
    required: true
  },
  entityId: {
    type: mongoose.Schema.Types.ObjectId,
    required: true
  },
  detectedAt: {
    type: Date,
    default: Date.now
  },
  features: mongoose.Schema.Types.Mixed, // Values that triggered anomaly
  baseline: mongoose.Schema.Types.Mixed, // Normal values
  deviation: {
    metric: String,
    expected: Number,
    actual: Number,
    percentage: Number
  },
  model: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'MLModel'
  },
  score: Number, // Anomaly score
  status: {
    type: String,
    enum: ['open', 'investigating', 'resolved', 'false_positive'],
    default: 'open'
  },
  investigation: {
    assignedTo: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    notes: String,
    rootCause: String,
    resolution: String,
    resolvedAt: Date
  },
  alertSent: { type: Boolean, default: false },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

AnomalySchema.index({ company: 1, type: 1, detectedAt: -1 });

// Demand Forecast Schema
const DemandForecastSchema = new mongoose.Schema({
  forecastId: {
    type: String,
    required: true,
    unique: true
  },
  model: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'MLModel',
    required: true
  },
  period: {
    start: Date,
    end: Date,
    granularity: {
      type: String,
      enum: ['hourly', 'daily', 'weekly', 'monthly', 'quarterly']
    }
  },
  location: {
    type: String,
    enum: ['origin', 'destination', 'route', 'zone', 'global'],
    zoneId: mongoose.Schema.Types.ObjectId,
    coordinates: {
      lat: Number,
      lng: Number
    }
  },
  serviceType: String,
  predictions: [{
    timestamp: Date,
    value: Number,
    lowerBound: Number,
    upperBound: Number,
    confidence: Number
  }],
  historicalComparison: {
    samePeriodLastYear: Number,
    trend: {
      type: String,
      enum: ['increasing', 'decreasing', 'stable']
    },
    changePercentage: Number
  },
  factors: [{
    name: String,
    impact: Number,
    correlation: Number
  }],
  accuracy: {
    mape: Number, // Mean Absolute Percentage Error
    mae: Number,
    rmse: Number
  },
  status: {
    type: String,
    enum: ['generating', 'ready', 'expired'],
    default: 'generating'
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  createdAt: {
    type: Date,
    default: Date.now
  }
});

// Recommendation Schema
const RecommendationSchema = new mongoose.Schema({
  recommendationId: {
    type: String,
    required: true,
    unique: true
  },
  type: {
    type: String,
    enum: ['route_optimization', 'vehicle_assignment', 'driver_assignment', 'warehouse_placement', 'pricing_adjustment', 'capacity_planning', 'customer_retention', 'cost_reduction', 'service_improvement'],
    required: true
  },
  priority: {
    type: String,
    enum: ['low', 'medium', 'high', 'critical'],
    required: true
  },
  target: {
    entityType: String,
    entityId: mongoose.Schema.Types.ObjectId
  },
  current: mongoose.Schema.Types.Mixed,
  recommended: mongoose.Schema.Types.Mixed,
  impact: {
    costSavings: Number,
    timeSavings: Number,
    efficiency: Number,
    revenue: Number,
    customerSatisfaction: Number
  },
  confidence: Number,
  reasoning: String,
  factors: [String],
  model: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'MLModel'
  },
  status: {
    type: String,
    enum: ['pending', 'viewed', 'accepted', 'rejected', 'implemented'],
    default: 'pending'
  },
  feedback: {
    user: {
      type: mongoose.Schema.Types.ObjectId,
      ref: 'User'
    },
    action: String,
    reason: String,
    timestamp: Date
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  expiresAt: Date,
  createdAt: {
    type: Date,
    default: Date.now
  }
});

module.exports = {
  MLModel: mongoose.model('MLModel', MLModelSchema),
  Prediction: mongoose.model('Prediction', PredictionSchema),
  Anomaly: mongoose.model('Anomaly', AnomalySchema),
  DemandForecast: mongoose.model('DemandForecast', DemandForecastSchema),
  Recommendation: mongoose.model('Recommendation', RecommendationSchema)
};
