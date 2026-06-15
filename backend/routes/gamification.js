//
/**
 * ============================================
 * 🏆 Gamification Routes - نظام المكافآت
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { protect, authorize } = require('../middleware/auth');
const { Badge, UserBadge, PointsTransaction, LeaderboardEntry, Reward, RewardRedemption, UserLevel } = require('../models/Gamification');
const logger = require('../utils/logger');

// Badges
// @route   GET /api/v1/gamification/badges
// @desc    Get all badges
// @access  Private
router.get('/badges', protect, async (req, res) => {
  try {
    const { category } = req.query;

    let query = { company: req.user.company, isActive: true };
    if (category) query.category = category;

    const badges = await Badge.find(query).sort({ points: -1 });

    // Get user's earned badges
    const userBadges = await UserBadge.find({
      user: req.user.id,
      company: req.user.company
    }).populate('badge');

    res.json({
      success: true,
      data: {
        allBadges: badges,
        earnedBadges: userBadges
      }
    });

  } catch (error) {
    logger.error('Get badges error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/gamification/badges
// @desc    Create badge (Admin only)
// @access  Private (Admin)
router.post('/badges', protect, authorize(['admin']), async (req, res) => {
  try {
    const badgeId = `BADGE-${Date.now()}`;

    const badge = await Badge.create({
      ...req.body,
      badgeId,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: badge
    });

  } catch (error) {
    logger.error('Create badge error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// User Level & Points
// @route   GET /api/v1/gamification/my-profile
// @desc    Get user's gamification profile
// @access  Private
router.get('/my-profile', protect, async (req, res) => {
  try {
    let profile = await UserLevel.findOne({
      user: req.user.id,
      company: req.user.company
    });

    if (!profile) {
      profile = await UserLevel.create({
        user: req.user.id,
        company: req.user.company,
        nextLevelXP: 1000
      });
    }

    const recentTransactions = await PointsTransaction.find({
      user: req.user.id,
      company: req.user.company
    })
      .sort({ createdAt: -1 })
      .limit(10);

    const badges = await UserBadge.find({
      user: req.user.id,
      company: req.user.company
    }).populate('badge', 'name icon color category');

    res.json({
      success: true,
      data: {
        profile,
        recentTransactions,
        badges
      }
    });

  } catch (error) {
    logger.error('Get gamification profile error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Leaderboard
// @route   GET /api/v1/gamification/leaderboard
// @desc    Get leaderboard
// @access  Private
router.get('/leaderboard', protect, async (req, res) => {
  try {
    const { period = 'monthly', category = 'overall' } = req.query;

    const now = new Date();
    let periodStart;

    switch (period) {
      case 'daily':
        periodStart = new Date(now.setHours(0, 0, 0, 0));
        break;
      case 'weekly':
        periodStart = new Date(now - 7 * 24 * 60 * 60 * 1000);
        break;
      case 'monthly':
        periodStart = new Date(now.getFullYear(), now.getMonth(), 1);
        break;
      case 'quarterly':
        const quarter = Math.floor(now.getMonth() / 3);
        periodStart = new Date(now.getFullYear(), quarter * 3, 1);
        break;
      case 'yearly':
        periodStart = new Date(now.getFullYear(), 0, 1);
        break;
      default:
        periodStart = new Date(now.getFullYear(), now.getMonth(), 1);
    }

    const leaderboard = await LeaderboardEntry.findOne({
      company: req.user.company,
      period,
      category,
      periodStart: { $lte: periodStart },
      periodEnd: { $gte: new Date() }
    }).populate('entries.user', 'firstName lastName avatar department');

    // If no leaderboard exists, generate one
    if (!leaderboard) {
      // Get users by points earned in period
      const topUsers = await PointsTransaction.aggregate([
        {
          $match: {
            company: req.user.company._id,
            type: 'earned',
            createdAt: { $gte: periodStart }
          }
        },
        {
          $group: {
            _id: '$user',
            totalPoints: { $sum: '$points' }
          }
        },
        { $sort: { totalPoints: -1 } },
        { $limit: 50 }
      ]);

      res.json({
        success: true,
        data: {
          period,
          category,
          entries: topUsers.map((u, index) => ({
            rank: index + 1,
            user: u._id,
            score: u.totalPoints
          }))
        }
      });
    } else {
      res.json({
        success: true,
        data: leaderboard
      });
    }

  } catch (error) {
    logger.error('Get leaderboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Rewards
// @route   GET /api/v1/gamification/rewards
// @desc    Get available rewards
// @access  Private
router.get('/rewards', protect, async (req, res) => {
  try {
    const userLevel = await UserLevel.findOne({
      user: req.user.id,
      company: req.user.company
    });

    const rewards = await Reward.find({
      company: req.user.company,
      isActive: true,
      $or: [
        { stock: -1 },
        { stock: { $gt: 0 } }
      ]
    }).sort({ pointsCost: 1 });

    res.json({
      success: true,
      pointsBalance: userLevel?.pointsBalance || 0,
      count: rewards.length,
      data: rewards
    });

  } catch (error) {
    logger.error('Get rewards error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/gamification/rewards
// @desc    Create reward (Admin only)
// @access  Private (Admin)
router.post('/rewards', protect, authorize(['admin']), async (req, res) => {
  try {
    const rewardId = `REWARD-${Date.now()}`;

    const reward = await Reward.create({
      ...req.body,
      rewardId,
      company: req.user.company
    });

    res.status(201).json({
      success: true,
      data: reward
    });

  } catch (error) {
    logger.error('Create reward error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/gamification/rewards/:id/redeem
// @desc    Redeem a reward
// @access  Private
router.post('/rewards/:id/redeem', protect, async (req, res) => {
  try {
    const reward = await Reward.findOne({
      _id: req.params.id,
      company: req.user.company,
      isActive: true
    });

    if (!reward) {
      return res.status(404).json({ success: false, message: 'Reward not found' });
    }

    const userLevel = await UserLevel.findOne({
      user: req.user.id,
      company: req.user.company
    });

    if (!userLevel || userLevel.pointsBalance < reward.pointsCost) {
      return res.status(400).json({
        success: false,
        message: 'Insufficient points'
      });
    }

    if (reward.stock !== -1 && reward.stock <= 0) {
      return res.status(400).json({
        success: false,
        message: 'Reward out of stock'
      });
    }

    // Create redemption
    const redemptionId = `RED-${Date.now()}`;
    const redemption = await RewardRedemption.create({
      redemptionId,
      user: req.user.id,
      reward: reward._id,
      pointsSpent: reward.pointsCost,
      company: req.user.company
    });

    // Deduct points
    userLevel.pointsBalance -= reward.pointsCost;
    userLevel.stats.rewardsRedeemed += 1;
    await userLevel.save();

    // Create transaction
    await PointsTransaction.create({
      transactionId: `TXN-${Date.now()}`,
      user: req.user.id,
      type: 'spent',
      points: -reward.pointsCost,
      balance: userLevel.pointsBalance,
      source: {
        type: 'reward',
        id: reward._id,
        description: `Redeemed: ${reward.name}`
      },
      company: req.user.company
    });

    // Update reward stock
    if (reward.stock !== -1) {
      reward.stock -= 1;
      reward.redeemed += 1;
      await reward.save();
    }

    res.json({
      success: true,
      message: 'Reward redeemed successfully',
      data: redemption
    });

  } catch (error) {
    logger.error('Redeem reward error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   GET /api/v1/gamification/my-redemptions
// @desc    Get my reward redemptions
// @access  Private
router.get('/my-redemptions', protect, async (req, res) => {
  try {
    const redemptions = await RewardRedemption.find({
      user: req.user.id,
      company: req.user.company
    })
      .populate('reward', 'name type image')
      .sort({ createdAt: -1 });

    res.json({
      success: true,
      count: redemptions.length,
      data: redemptions
    });

  } catch (error) {
    logger.error('Get my redemptions error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Points History
// @route   GET /api/v1/gamification/points-history
// @desc    Get points transaction history
// @access  Private
router.get('/points-history', protect, async (req, res) => {
  try {
    const { type, from, to, page = 1, limit = 20 } = req.query;

    let query = { user: req.user.id, company: req.user.company };
    if (type) query.type = type;
    if (from && to) {
      query.createdAt = {
        $gte: new Date(from),
        $lte: new Date(to)
      };
    }

    const transactions = await PointsTransaction.find(query)
      .sort({ createdAt: -1 })
      .skip((page - 1) * limit)
      .limit(limit * 1);

    const total = await PointsTransaction.countDocuments(query);

    res.json({
      success: true,
      count: transactions.length,
      pagination: {
        page: parseInt(page),
        pages: Math.ceil(total / limit),
        total
      },
      data: transactions
    });

  } catch (error) {
    logger.error('Get points history error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// Dashboard
// @route   GET /api/v1/gamification/dashboard
// @desc    Gamification dashboard
// @access  Private (Admin)
router.get('/dashboard', protect, authorize(['admin']), async (req, res) => {
  try {
    const stats = await Promise.all([
      // Total badges
      Badge.countDocuments({ company: req.user.company }),

      // Total rewards
      Reward.countDocuments({ company: req.user.company, isActive: true }),

      // Total points distributed
      PointsTransaction.aggregate([
        {
          $match: {
            company: req.user.company._id,
            type: 'earned'
          }
        },
        { $group: { _id: null, total: { $sum: '$points' } } }
      ]),

      // Top point earners
      UserLevel.find({ company: req.user.company })
        .populate('user', 'firstName lastName avatar')
        .sort({ totalXP: -1 })
        .limit(10)
        .select('user totalXP pointsBalance currentLevel'),

      // Recent redemptions
      RewardRedemption.find({ company: req.user.company })
        .populate('user', 'firstName lastName')
        .populate('reward', 'name')
        .sort({ createdAt: -1 })
        .limit(10)
    ]);

    res.json({
      success: true,
      data: {
        badges: stats[0],
        rewards: stats[1],
        totalPointsDistributed: stats[2][0]?.total || 0,
        topUsers: stats[3],
        recentRedemptions: stats[4]
      }
    });

  } catch (error) {
    logger.error('Gamification dashboard error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
