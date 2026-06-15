//
/**
 * ============================================
 * 🔐 Two-Factor Authentication Routes
 * ============================================
 */

const express = require('express');
const router = express.Router();
const { body } = require('express-validator');
const { protect } = require('../middleware/auth');
const twoFactorService = require('../services/twoFactorService');
const logger = require('../utils/logger');

// @route   POST /api/v1/2fa/setup
// @desc    Setup 2FA for user
// @access  Private
router.post('/setup', protect, async (req, res) => {
  try {
    const { secret, qrCode, manualEntryKey } = await twoFactorService.generateSecret(
      req.user.id,
      req.user.email
    );

    // Temporarily store secret (will be confirmed after verification)
    req.user.twoFactorSecret = secret;
    req.user.twoFactorEnabled = false;
    await req.user.save({ validateBeforeSave: false });

    res.json({
      success: true,
      data: {
        qrCode,
        manualEntryKey,
        backupCodes: twoFactorService.generateBackupCodes()
      }
    });

  } catch (error) {
    logger.error('2FA setup error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/2fa/verify
// @desc    Verify and enable 2FA
// @access  Private
router.post('/verify', protect, [
  body('token').isLength({ min: 6, max: 6 }).isNumeric()
], async (req, res) => {
  try {
    const { token } = req.body;

    if (!req.user.twoFactorSecret) {
      return res.status(400).json({
        success: false,
        message: '2FA not set up'
      });
    }

    const verified = twoFactorService.verifyToken(req.user.twoFactorSecret, token);

    if (!verified) {
      return res.status(400).json({
        success: false,
        message: 'Invalid verification code'
      });
    }

    // Enable 2FA
    const backupCodes = twoFactorService.generateBackupCodes();
    
    req.user.twoFactorEnabled = true;
    req.user.twoFactorBackupCodes = backupCodes.map(code => 
      require('crypto').createHash('sha256').update(code).digest('hex')
    );
    await req.user.save({ validateBeforeSave: false });

    res.json({
      success: true,
      message: '2FA enabled successfully',
      backupCodes // Show once only
    });

  } catch (error) {
    logger.error('2FA verification error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/2fa/verify-login
// @desc    Verify 2FA during login
// @access  Public
router.post('/verify-login', async (req, res) => {
  try {
    const { userId, token } = req.body;

    const User = require('../models/User');
    const user = await User.findById(userId).select('+twoFactorSecret +twoFactorEnabled');

    if (!user || !user.twoFactorEnabled) {
      return res.status(400).json({
        success: false,
        message: '2FA not enabled'
      });
    }

    const verified = twoFactorService.verifyToken(user.twoFactorSecret, token);

    if (!verified) {
      return res.status(400).json({
        success: false,
        message: 'Invalid verification code'
      });
    }

    // Generate final auth token
    const authToken = user.getSignedJwtToken();

    res.json({
      success: true,
      token: authToken
    });

  } catch (error) {
    logger.error('2FA login verification error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/2fa/disable
// @desc    Disable 2FA
// @access  Private
router.post('/disable', protect, [
  body('token').isLength({ min: 6, max: 6 }).isNumeric()
], async (req, res) => {
  try {
    const { token } = req.body;

    if (!req.user.twoFactorEnabled) {
      return res.status(400).json({
        success: false,
        message: '2FA is not enabled'
      });
    }

    const verified = twoFactorService.verifyToken(req.user.twoFactorSecret, token);

    if (!verified) {
      return res.status(400).json({
        success: false,
        message: 'Invalid verification code'
      });
    }

    // Disable 2FA
    req.user.twoFactorEnabled = false;
    req.user.twoFactorSecret = undefined;
    req.user.twoFactorBackupCodes = undefined;
    await req.user.save({ validateBeforeSave: false });

    res.json({
      success: true,
      message: '2FA disabled successfully'
    });

  } catch (error) {
    logger.error('2FA disable error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// @route   POST /api/v1/2fa/recover
// @desc    Recover account using backup code
// @access  Public
router.post('/recover', async (req, res) => {
  try {
    const { email, backupCode } = req.body;

    const User = require('../models/User');
    const user = await User.findOne({ email }).select('+twoFactorBackupCodes');

    if (!user || !user.twoFactorEnabled) {
      return res.status(400).json({
        success: false,
        message: 'Invalid recovery attempt'
      });
    }

    // Hash provided code and compare
    const hashedCode = require('crypto').createHash('sha256').update(backupCode).digest('hex');
    const codeIndex = user.twoFactorBackupCodes.indexOf(hashedCode);

    if (codeIndex === -1) {
      return res.status(400).json({
        success: false,
        message: 'Invalid backup code'
      });
    }

    // Remove used backup code
    user.twoFactorBackupCodes.splice(codeIndex, 1);
    await user.save({ validateBeforeSave: false });

    // Generate auth token
    const token = user.getSignedJwtToken();

    res.json({
      success: true,
      message: 'Account recovered successfully',
      token,
      remainingBackupCodes: user.twoFactorBackupCodes.length
    });

  } catch (error) {
    logger.error('2FA recovery error:', error);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

module.exports = router;
