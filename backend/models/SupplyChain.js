//
/**
 * ============================================
 * 🔗 Supply Chain Management - إدارة سلسلة التوريد
 * ============================================
 */

const mongoose = require('mongoose');

// Supplier Schema (for supply chain focus)
const SupplierSchema = new mongoose.Schema({
  supplierCode: {
    type: String,
    required: true,
    unique: true
  },
  name: {
    type: String,
    required: true
  },
  type: {
    type: String,
    enum: ['manufacturer', 'distributor', 'wholesaler', 'importer', 'local', 'international'],
    required: true
  },
  industry: String,
  region: {
    type: String,
    enum: ['local', 'regional', 'international'],
    required: true
  },
  country: { type: String, default: 'SA' },
  leadTime: {
    min: Number,
    max: Number,
    average: Number,
    unit: { type: String, default: 'days' }
  },
  capacity: {
    monthly: Number,
    utilized: Number,
    unit: String
  },
  minimumOrder: {
    quantity: Number,
    value: Number,
    currency: { type: String, default: 'SAR' }
  },
  paymentTerms: {
    days: { type: Number, default: 30 },
    method: String,
    creditLimit: Number
  },
  incoterms: [String],
  logistics: {
    canDeliver: { type: Boolean, default: false },
    shippingMethods: [String],
    preferredCarrier: String,
    consolidationPoint: String
  },
  performance: {
    onTimeDelivery: { type: Number, default: 0 },
    qualityRating: { type: Number, default: 0 },
    priceCompetitiveness: { type: Number, default: 0 },
    responsiveness: { type: Number, default: 0 },
    overallScore: { type: Number, default: 0 }
  },
  riskAssessment: {
    financialStability: { type: String, enum: ['low', 'medium', 'high'] },
    singleSource: { type: Boolean, default: false },
    geopoliticalRisk: { type: String, enum: ['low', 'medium', 'high'] },
    businessContinuity: { type: String, enum: ['low', 'medium', 'high'] }
  },
  alternatives: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Supplier'
  }],
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

// Demand Forecast Schema
const DemandForecastSchema = new mongoose.Schema({
  product: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'InventoryItem',
    required: true
  },
  forecastPeriod: {
    start: Date,
    end: Date
  },
  method: {
    type: String,
    enum: ['moving_average', 'exponential_smoothing', 'linear_regression', 'seasonal', 'machine_learning', 'manual'],
    default: 'moving_average'
  },
  monthlyForecasts: [{
    month: Number,
    year: Number,
    predictedDemand: Number,
    confidenceInterval: {
      lower: Number,
      upper: Number
    },
    actualDemand: Number,
    accuracy: Number
  }],
  totalForecast: Number,
  confidence: Number,
  factors: [{
    name: String,
    impact: Number, // positive or negative
    description: String
  }],
  createdBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
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

// Inventory Plan Schema
const InventoryPlanSchema = new mongoose.Schema({
  item: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'InventoryItem',
    required: true
  },
  warehouse: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Warehouse',
    required: true
  },
  policy: {
    method: {
      type: String,
      enum: ['reorder_point', 'periodic_review', 'min_max', 'abc_xyz', 'custom'],
      default: 'reorder_point'
    },
    reorderPoint: Number,
    reorderQuantity: Number,
    safetyStock: Number,
    maximumStock: Number,
    reviewPeriod: Number // days
  },
  costs: {
    holdingCost: Number,
    orderingCost: Number,
    shortageCost: Number,
    unitCost: Number
  },
  serviceLevel: {
    target: { type: Number, default: 95 },
    actual: Number
  },
  supplier: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Supplier'
  },
  leadTime: {
    average: Number,
    standardDeviation: Number
  },
  status: {
    type: String,
    enum: ['active', 'review', 'obsolete'],
    default: 'active'
  },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

// Supply Chain Event Schema
const SupplyChainEventSchema = new mongoose.Schema({
  eventId: {
    type: String,
    required: true,
    unique: true
  },
  type: {
    type: String,
    enum: ['disruption', 'delay', 'quality_issue', 'capacity_constraint', 'price_change', 'regulatory', 'natural_disaster', 'geopolitical'],
    required: true
  },
  severity: {
    type: String,
    enum: ['low', 'medium', 'high', 'critical'],
    required: true
  },
  description: String,
  affectedSuppliers: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Supplier'
  }],
  affectedProducts: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'InventoryItem'
  }],
  impact: {
    description: String,
    financial: Number,
    operational: String,
    timeline: String
  },
  mitigation: {
    actions: [String],
    responsible: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    status: {
      type: String,
      enum: ['identified', 'in_progress', 'resolved']
    }
  },
  startDate: Date,
  endDate: Date,
  status: {
    type: String,
    enum: ['active', 'monitoring', 'resolved'],
    default: 'active'
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

// Distribution Plan Schema
const DistributionPlanSchema = new mongoose.Schema({
  planNumber: {
    type: String,
    required: true,
    unique: true
  },
  shipment: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Shipment'
  },
  origin: {
    warehouse: { type: mongoose.Schema.Types.ObjectId, ref: 'Warehouse' },
    supplier: { type: mongoose.Schema.Types.ObjectId, ref: 'Supplier' }
  },
  destinations: [{
    warehouse: { type: mongoose.Schema.Types.ObjectId, ref: 'Warehouse' },
    customer: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    quantity: Number,
    requiredDate: Date,
    mode: {
      type: String,
      enum: ['truck', 'van', 'courier', 'air', 'sea']
    },
    priority: {
      type: String,
      enum: ['low', 'medium', 'high', 'urgent']
    }
  }],
  consolidation: {
    enabled: { type: Boolean, default: false },
    hub: { type: mongoose.Schema.Types.ObjectId, ref: 'Warehouse' },
    cutoffTime: Date
  },
  optimization: {
    objective: {
      type: String,
      enum: ['cost', 'time', 'emissions', 'balanced']
    },
    constraints: [String],
    solution: mongoose.Schema.Types.Mixed
  },
  status: {
    type: String,
    enum: ['planning', 'optimized', 'in_execution', 'completed'],
    default: 'planning'
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

module.exports = {
  Supplier: mongoose.model('Supplier', SupplierSchema),
  DemandForecast: mongoose.model('DemandForecast', DemandForecastSchema),
  InventoryPlan: mongoose.model('InventoryPlan', InventoryPlanSchema),
  SupplyChainEvent: mongoose.model('SupplyChainEvent', SupplyChainEventSchema),
  DistributionPlan: mongoose.model('DistributionPlan', DistributionPlanSchema)
};
