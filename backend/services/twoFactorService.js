//
/**
 * ============================================
 * 🔐 Two-Factor Authentication Service
 * خدمة التحقق الثنائي (2FA)
 * ============================================
 */

const speakeasy = require('speakeasy');
const QRCode = require('qrcode');
const logger = require('../utils/logger');

class TwoFactorService {
  /**
   * Generate 2FA secret for user
   */
  async generateSecret(userId, email) {
    try {
      const secret = speakeasy.generateSecret({
        name: `Edham Logistics (${email})`,
        issuer: 'Edham Logistics',
        length: 32
      });

      // Generate QR code
      const qrCodeUrl = await QRCode.toDataURL(secret.otpauth_url);

      return {
        success: true,
        secret: secret.base32,
        qrCode: qrCodeUrl,
        manualEntryKey: secret.base32
      };
    } catch (error) {
      logger.error('2FA generation error:', error);
      throw error;
    }
  }

  /**
   * Verify 2FA token
   */
  verifyToken(secret, token) {
    try {
      const verified = speakeasy.totp.verify({
        secret: secret,
        encoding: 'base32',
        token: token,
        window: 2 // Allow 2 time steps (60 seconds) for clock drift
      });

      return verified;
    } catch (error) {
      logger.error('2FA verification error:', error);
      return false;
    }
  }

  /**
   * Generate backup codes
   */
  generateBackupCodes() {
    const codes = [];
    for (let i = 0; i < 10; i++) {
      const code = Math.random().toString(36).substring(2, 10).toUpperCase();
      codes.push(code);
    }
    return codes;
  }

  /**
   * Verify backup code
   */
  verifyBackupCode(code, hashedCodes) {
    // In production, codes should be hashed
    return hashedCodes.includes(code);
  }

  /**
   * Generate TOTP for SMS/Email
   */
  generateTOTP(secret) {
    return speakeasy.totp({
      secret: secret,
      encoding: 'base32'
    });
  }

  /**
   * Generate QR code for authenticator apps
   */
  async generateQRCode(otpauthUrl) {
    return await QRCode.toDataURL(otpauthUrl);
  }
}

module.exports = new TwoFactorService();
