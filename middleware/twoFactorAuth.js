/**
 * ============================================
 * 🔐 Two-Factor Authentication - نظام إدهام الاحترافي
 * Edham Logistics - Advanced Security System
 * ============================================
 */

const speakeasy = require('speakeasy');
const qrcode = require('qrcode');
const crypto = require('crypto');
const logger = require('../utils/logger');
const User = require('../models/User');
const { MESSAGES } = require('../config/constants');

class TwoFactorAuth {
  constructor() {
    this.issuer = 'Edham Logistics';
    this.window = 2; // Time window for TOTP verification
    this.backupCodesCount = 10;
  }

  /**
   * Generate TOTP secret for user
   */
  async generateTOTPSecret(userId) {
    try {
      const user = await User.findById(userId);
      if (!user) {
        throw new Error('User not found');
      }

      // Generate secret
      const secret = speakeasy.generateSecret({
        name: user.email,
        issuer: this.issuer,
        length: 32
      });

      // Generate backup codes
      const backupCodes = this.generateBackupCodes();

      // Save to user
      user.twoFactorAuth = {
        enabled: false, // Not enabled until verified
        secret: secret.base32,
        backupCodes,
        method: 'totp',
        createdAt: new Date(),
        lastUsed: null
      };

      await user.save();

      // Generate QR code
      const qrCodeUrl = await this.generateQRCode(secret.otpauth_url);

      logger.info('TOTP secret generated', { userId, email: user.email });

      return {
        success: true,
        secret: secret.base32,
        qrCode: qrCodeUrl,
        backupCodes,
        manualEntryKey: secret.base32
      };

    } catch (error) {
      logger.error('Error generating TOTP secret:', error);
      return {
        success: false,
        error: 'Failed to generate 2FA secret'
      };
    }
  }

  /**
   * Verify TOTP token
   */
  async verifyTOTPToken(userId, token) {
    try {
      const user = await User.findById(userId);
      if (!user || !user.twoFactorAuth?.secret) {
        return {
          success: false,
          error: '2FA not configured for user'
        };
      }

      // Verify token
      const verified = speakeasy.totp.verify({
        secret: user.twoFactorAuth.secret,
        encoding: 'base32',
        token: token,
        window: this.window
      });

      if (verified) {
        // Update last used timestamp
        user.twoFactorAuth.lastUsed = new Date();
        await user.save();

        logger.info('TOTP verification successful', { userId });
      } else {
        logger.warn('TOTP verification failed', { userId, token });
      }

      return {
        success: verified,
        error: verified ? null : 'Invalid verification code'
      };

    } catch (error) {
      logger.error('Error verifying TOTP token:', error);
      return {
        success: false,
        error: 'Verification failed'
      };
    }
  }

  /**
   * Verify backup code
   */
  async verifyBackupCode(userId, code) {
    try {
      const user = await User.findById(userId);
      if (!user || !user.twoFactorAuth?.backupCodes) {
        return {
          success: false,
          error: 'No backup codes available'
        };
      }

      // Find matching backup code
      const backupCodeIndex = user.twoFactorAuth.backupCodes.findIndex(
        bc => bc.code === code && bc.used === false && !bc.expiresAt
      );

      if (backupCodeIndex === -1) {
        return {
          success: false,
          error: 'Invalid backup code'
        };
      }

      // Mark backup code as used
      user.twoFactorAuth.backupCodes[backupCodeIndex].used = true;
      user.twoFactorAuth.backupCodes[backupCodeIndex].usedAt = new Date();
      await user.save();

      logger.info('Backup code verification successful', { userId });

      return {
        success: true,
        remainingCodes: user.twoFactorAuth.backupCodes.filter(bc => !bc.used).length
      };

    } catch (error) {
      logger.error('Error verifying backup code:', error);
      return {
        success: false,
        error: 'Backup code verification failed'
      };
    }
  }

  /**
   * Enable 2FA for user
   */
  async enableTwoFactorAuth(userId, verificationToken) {
    try {
      // Verify the token first
      const verification = await this.verifyTOTPToken(userId, verificationToken);
      if (!verification.success) {
        return {
          success: false,
          error: 'Invalid verification token'
        };
      }

      const user = await User.findById(userId);
      if (!user) {
        return {
          success: false,
          error: 'User not found'
        };
      }

      // Enable 2FA
      user.twoFactorAuth.enabled = true;
      user.twoFactorAuth.enabledAt = new Date();
      await user.save();

      logger.success('2FA enabled', { userId });

      return {
        success: true,
        message: 'Two-factor authentication enabled successfully'
      };

    } catch (error) {
      logger.error('Error enabling 2FA:', error);
      return {
        success: false,
        error: 'Failed to enable 2FA'
      };
    }
  }

  /**
   * Disable 2FA for user
   */
  async disableTwoFactorAuth(userId, password, verificationToken) {
    try {
      const user = await User.findById(userId);
      if (!user) {
        return {
          success: false,
          error: 'User not found'
        };
      }

      // Verify password
      const bcrypt = require('bcryptjs');
      const passwordValid = await bcrypt.compare(password, user.password);
      if (!passwordValid) {
        return {
          success: false,
          error: 'Invalid password'
        };
      }

      // If 2FA is enabled, verify the token
      if (user.twoFactorAuth?.enabled) {
        const verification = await this.verifyTOTPToken(userId, verificationToken);
        if (!verification.success) {
          return {
            success: false,
            error: 'Invalid verification token'
          };
        }
      }

      // Disable 2FA
      user.twoFactorAuth = undefined;
      await user.save();

      logger.success('2FA disabled', { userId });

      return {
        success: true,
        message: 'Two-factor authentication disabled successfully'
      };

    } catch (error) {
      logger.error('Error disabling 2FA:', error);
      return {
        success: false,
        error: 'Failed to disable 2FA'
      };
    }
  }

  /**
   * Generate new backup codes
   */
  async regenerateBackupCodes(userId, verificationToken) {
    try {
      // Verify the token first
      const verification = await this.verifyTOTPToken(userId, verificationToken);
      if (!verification.success) {
        return {
          success: false,
          error: 'Invalid verification token'
        };
      }

      const user = await User.findById(userId);
      if (!user) {
        return {
          success: false,
          error: 'User not found'
        };
      }

      // Generate new backup codes
      const newBackupCodes = this.generateBackupCodes();
      
      // Update user with new codes
      user.twoFactorAuth.backupCodes = newBackupCodes;
      await user.save();

      logger.info('Backup codes regenerated', { userId });

      return {
        success: true,
        backupCodes: newBackupCodes,
        message: 'Backup codes regenerated successfully'
      };

    } catch (error) {
      logger.error('Error regenerating backup codes:', error);
      return {
        success: false,
        error: 'Failed to regenerate backup codes'
      };
    }
  }

  /**
   * Send SMS verification code
   */
  async sendSMSVerification(userId, phoneNumber) {
    try {
      const user = await User.findById(userId);
      if (!user) {
        return {
          success: false,
          error: 'User not found'
        };
      }

      // Generate 6-digit code
      const code = this.generateSMSCode();
      const expiresAt = new Date(Date.now() + 10 * 60 * 1000); // 10 minutes

      // Save SMS verification
      user.twoFactorAuth = {
        ...user.twoFactorAuth,
        smsVerification: {
          code,
          phoneNumber,
          expiresAt,
          attempts: 0,
          createdAt: new Date()
        }
      };

      await user.save();

      // Send SMS (integrate with SMS service)
      const smsSent = await this.sendSMS(phoneNumber, code);
      
      if (smsSent) {
        logger.info('SMS verification sent', { userId, phoneNumber });
        return {
          success: true,
          message: 'Verification code sent to your phone'
        };
      } else {
        return {
          success: false,
          error: 'Failed to send SMS verification'
        };
      }

    } catch (error) {
      logger.error('Error sending SMS verification:', error);
      return {
        success: false,
        error: 'Failed to send SMS verification'
      };
    }
  }

  /**
   * Verify SMS code
   */
  async verifySMSCode(userId, code) {
    try {
      const user = await User.findById(userId);
      if (!user || !user.twoFactorAuth?.smsVerification) {
        return {
          success: false,
          error: 'No SMS verification pending'
        };
      }

      const smsVerification = user.twoFactorAuth.smsVerification;

      // Check if expired
      if (new Date() > smsVerification.expiresAt) {
        return {
          success: false,
          error: 'Verification code expired'
        };
      }

      // Check attempts
      if (smsVerification.attempts >= 3) {
        return {
          success: false,
          error: 'Too many verification attempts'
        };
      }

      // Verify code
      const isValid = smsVerification.code === code;
      
      // Update attempts
      smsVerification.attempts++;
      await user.save();

      if (isValid) {
        // Clear SMS verification
        user.twoFactorAuth.smsVerification = undefined;
        await user.save();

        logger.info('SMS verification successful', { userId });
      }

      return {
        success: isValid,
        error: isValid ? null : 'Invalid verification code',
        remainingAttempts: 3 - smsVerification.attempts
      };

    } catch (error) {
      logger.error('Error verifying SMS code:', error);
      return {
        success: false,
        error: 'SMS verification failed'
      };
    }
  }

  /**
   * Generate QR code image
   */
  async generateQRCode(otpauthUrl) {
    try {
      return await qrcode.toDataURL(otpauthUrl, {
        errorCorrectionLevel: 'M',
        type: 'image/png',
        quality: 0.92,
        margin: 1,
        color: {
          dark: '#000000',
          light: '#FFFFFF'
        }
      });
    } catch (error) {
      logger.error('Error generating QR code:', error);
      throw error;
    }
  }

  /**
   * Generate backup codes
   */
  generateBackupCodes() {
    const codes = [];
    for (let i = 0; i < this.backupCodesCount; i++) {
      codes.push({
        code: crypto.randomBytes(4).toString('hex').toUpperCase(),
        used: false,
        createdAt: new Date(),
        expiresAt: new Date(Date.now() + 365 * 24 * 60 * 60 * 1000) // 1 year
      });
    }
    return codes;
  }

  /**
   * Generate SMS verification code
   */
  generateSMSCode() {
    return Math.floor(100000 + Math.random() * 900000).toString();
  }

  /**
   * Send SMS (integrate with SMS service)
   */
  async sendSMS(phoneNumber, code) {
    try {
      // This would integrate with an SMS service like Twilio, AWS SNS, etc.
      // For now, we'll simulate the SMS sending
      
      logger.info('SMS sent', { phoneNumber, code });
      
      // Example Twilio integration:
      /*
      const twilio = require('twilio')(process.env.TWILIO_ACCOUNT_SID, process.env.TWILIO_AUTH_TOKEN);
      
      await twilio.messages.create({
        body: `Your Edham Logistics verification code is: ${code}`,
        from: process.env.TWILIO_PHONE_NUMBER,
        to: phoneNumber
      });
      */

      return true;
    } catch (error) {
      logger.error('Error sending SMS:', error);
      return false;
    }
  }

  /**
   * Check if user has 2FA enabled
   */
  async isTwoFactorEnabled(userId) {
    try {
      const user = await User.findById(userId);
      return user?.twoFactorAuth?.enabled || false;
    } catch (error) {
      logger.error('Error checking 2FA status:', error);
      return false;
    }
  }

  /**
   * Get 2FA status for user
   */
  async getTwoFactorStatus(userId) {
    try {
      const user = await User.findById(userId);
      if (!user) {
        return {
          success: false,
          error: 'User not found'
        };
      }

      const twoFactorAuth = user.twoFactorAuth;
      
      return {
        success: true,
        enabled: twoFactorAuth?.enabled || false,
        method: twoFactorAuth?.method || null,
        createdAt: twoFactorAuth?.createdAt || null,
        lastUsed: twoFactorAuth?.lastUsed || null,
        hasBackupCodes: twoFactorAuth?.backupCodes?.length > 0 || false,
        unusedBackupCodes: twoFactorAuth?.backupCodes?.filter(bc => !bc.used).length || 0
      };

    } catch (error) {
      logger.error('Error getting 2FA status:', error);
      return {
        success: false,
        error: 'Failed to get 2FA status'
      };
    }
  }

  /**
   * Middleware for 2FA verification
   */
  static requireTwoFactor(req, res, next) {
    return async (req, res, next) => {
      try {
        const userId = req.user?.id;
        if (!userId) {
          return res.status(401).json({
            success: false,
            error: 'Authentication required'
          });
        }

        const twoFactorAuth = new TwoFactorAuth();
        const isEnabled = await twoFactorAuth.isTwoFactorEnabled(userId);

        if (!isEnabled) {
          return next(); // Skip 2FA if not enabled
        }

        // Check if 2FA is already verified in this session
        if (req.session?.twoFactorVerified) {
          return next();
        }

        // Check for 2FA token in request
        const twoFactorToken = req.headers['x-2fa-token'] || req.body.twoFactorToken;
        
        if (!twoFactorToken) {
          return res.status(403).json({
            success: false,
            error: 'Two-factor authentication required',
            code: 'TWO_FACTOR_REQUIRED'
          });
        }

        // Verify the token
        const verification = await twoFactorAuth.verifyTOTPToken(userId, twoFactorToken);
        
        if (!verification.success) {
          return res.status(403).json({
            success: false,
            error: 'Invalid two-factor authentication token',
            code: 'INVALID_2FA_TOKEN'
          });
        }

        // Mark 2FA as verified for this session
        req.session.twoFactorVerified = true;
        req.session.twoFactorVerifiedAt = new Date();

        next();

      } catch (error) {
        logger.error('2FA middleware error:', error);
        return res.status(500).json({
          success: false,
          error: 'Two-factor authentication check failed'
        });
      }
    };
  }

  /**
   * Middleware for optional 2FA
   */
  static optionalTwoFactor(req, res, next) {
    return async (req, res, next) => {
      try {
        const userId = req.user?.id;
        if (!userId) {
          return next();
        }

        const twoFactorAuth = new TwoFactorAuth();
        const isEnabled = await twoFactorAuth.isTwoFactorEnabled(userId);

        if (!isEnabled) {
          return next(); // Skip 2FA if not enabled
        }

        // Check if 2FA is already verified in this session
        if (req.session?.twoFactorVerified) {
          return next();
        }

        // Check for 2FA token in request
        const twoFactorToken = req.headers['x-2fa-token'] || req.body.twoFactorToken;
        
        if (twoFactorToken) {
          // Verify the token
          const verification = await twoFactorAuth.verifyTOTPToken(userId, twoFactorToken);
          
          if (verification.success) {
            // Mark 2FA as verified for this session
            req.session.twoFactorVerified = true;
            req.session.twoFactorVerifiedAt = new Date();
          }
        }

        next();

      } catch (error) {
        logger.error('Optional 2FA middleware error:', error);
        return next(); // Continue on error for optional middleware
      }
    };
  }
}

module.exports = TwoFactorAuth;
