//
/**
 * ============================================
 * 🏆 Gamification & Rewards - نظام المكافآت والتحفيز
 * ============================================
 */

const mongoose = require('mongoose');

// Badge Schema
const BadgeSchema = new mongoose.Schema({
  badgeId: {
    type: String,
    required: true,
    unique: true
  },
  name: String,
  description: String,
  icon: String,
  color: String,
  category: {
    type: String,
    enum: ['performance', 'attendance', 'safety', 'customer_service', 'efficiency', 'milestone', 'special']
  },
  criteria: {
    type: {
      type: String,
      enum: ['shipments_completed', 'distance_driven', 'on_time_deliveries', 'customer_rating', 'safety_score', 'attendance_streak', 'years_of_service', 'custom']
    },
    threshold: Number,
    period: { type: String, enum: ['daily', 'weekly', 'monthly', 'quarterly', 'yearly', 'all_time'] }
  },
  points: { type: Number, default: 0 },
  rarity: {
    type: String,
    enum: ['common', 'uncommon', 'rare', 'epic', 'legendary'],
    default: 'common'
  },
  isActive: { type: Boolean, default: true },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

// User Badge Schema
const UserBadgeSchema = new mongoose.Schema({
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  badge: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Badge',
    required: true
  },
  earnedAt: {
    type: Date,
    default: Date.now
  },
  progress: {
    current: { type: Number, default: 0 },
    target: { type: Number, default: 100 }
  },
  displayed: { type: Boolean, default: true },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

// Points Transaction Schema
const PointsTransactionSchema = new mongoose.Schema({
  transactionId: {
    type: String,
    required: true,
    unique: true
  },
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  type: {
    type: String,
    enum: ['earned', 'spent', 'bonus', 'penalty', 'adjustment'],
    required: true
  },
  points: {
    type: Number,
    required: true
  },
  balance: Number, // Balance after transaction
  source: {
    type: { type: String, enum: ['badge', 'task', 'shipment', 'referral', 'purchase', 'reward', 'admin'] },
    id: mongoose.Schema.Types.ObjectId,
    description: String
  },
  metadata: mongoose.Schema.Types.Mixed,
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

// Leaderboard Entry Schema
const LeaderboardEntrySchema = new mongoose.Schema({
  period: {
    type: String,
    enum: ['daily', 'weekly', 'monthly', 'quarterly', 'yearly'],
    required: true
  },
  periodStart: Date,
  periodEnd: Date,
  category: {
    type: String,
    enum: ['overall', 'shipments', 'efficiency', 'safety', 'customer_satisfaction', 'points']
  },
  entries: [{
    user: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    score: Number,
    rank: Number,
    previousRank: Number,
    metrics: mongoose.Schema.Types.Mixed,
    badges: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Badge' }],
    trend: { type: String, enum: ['up', 'down', 'stable'] }
  }],
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
});

// Reward Schema
const RewardSchema = new mongoose.Schema({
  rewardId: {
    type: String,
    required: true,
    unique: true
  },
  name: String,
  description: String,
  type: {
    type: String,
    enum: ['physical', 'digital', 'experience', 'benefit', 'cash']
  },
  image: String,
  pointsCost: {
    type: Number,
    required: true
  },
  stock: {
    type: Number,
    default: -1 // -1 = unlimited
  },
  redeemed: { type: Number, default: 0 },
  availability: {
    startDate: Date,
    endDate: Date,
    userGroups: [String]
  },
  terms: String,
  isActive: { type: Boolean, default: true },
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  }
});

// Reward Redemption Schema
const RewardRedemptionSchema = new mongoose.Schema({
  redemptionId: {
    type: String,
    required: true,
    unique: true
  },
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true
  },
  reward: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Reward',
    required: true
  },
  pointsSpent: Number,
  status: {
    type: String,
    enum: ['pending', 'approved', 'processing', 'shipped', 'completed', 'cancelled', 'rejected'],
    default: 'pending'
  },
  delivery: {
    method: String,
    address: mongoose.Schema.Types.Mixed,
    tracking: String,
    estimatedDelivery: Date
  },
  notes: String,
  approvedBy: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User'
  },
  approvedAt: Date,
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

// Level/Progression Schema
const UserLevelSchema = new mongoose.Schema({
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: true,
    unique: true
  },
  currentLevel: {
    type: Number,
    default: 1
  },
  currentXP: {
    type: Number,
    default: 0
  },
  totalXP: {
    type: Number,
    default: 0
  },
  pointsBalance: {
    type: Number,
    default: 0
  },
  lifetimePoints: {
    type: Number,
    default: 0
  },
  stats: {
    badgesCount: { type: Number, default: 0 },
    rewardsRedeemed: { type: Number, default: 0 },
    currentStreak: { type: Number, default: 0 },
    longestStreak: { type: Number, default: 0 },
    lastActive: Date
  },
  nextLevelXP: Number,
  company: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Company',
    required: true
  },
  updatedAt: {
    type: Date,
    default: Date.now
  }
});

module.exports = {
  Badge: mongoose.model('Badge', BadgeSchema),
  UserBadge: mongoose.model('UserBadge', UserBadgeSchema),
  PointsTransaction: mongoose.model('PointsTransaction', PointsTransactionSchema),
  LeaderboardEntry: mongoose.model('LeaderboardEntry', LeaderboardEntrySchema),
  Reward: mongoose.model('Reward', RewardSchema),
  RewardRedemption: mongoose.model('RewardRedemption', RewardRedemptionSchema),
  UserLevel: mongoose.model('UserLevel', UserLevelSchema)
};
