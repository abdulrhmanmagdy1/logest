//
/**
 * ============================================
 * 🌍 Localization Service - خدمة تعدد اللغات
 * Multi-language Support System
 * ============================================
 */

const i18n = require('i18n');
const path = require('path');
const logger = require('../utils/logger');

class LocalizationService {
  constructor() {
    this.supportedLocales = ['ar', 'en', 'ur', 'bn', 'tl', 'fr'];
    this.defaultLocale = 'ar';
    
    this.initialize();
  }

  initialize() {
    i18n.configure({
      locales: this.supportedLocales,
      directory: path.join(__dirname, '../locales'),
      defaultLocale: this.defaultLocale,
      queryParameter: 'lang',
      cookie: 'locale',
      autoReload: true,
      updateFiles: false,
      syncFiles: false,
      objectNotation: true
    });
  }

  /**
   * Get supported languages
   */
  getSupportedLanguages() {
    return [
      { code: 'ar', name: 'العربية', nameEn: 'Arabic', direction: 'rtl' },
      { code: 'en', name: 'English', nameEn: 'English', direction: 'ltr' },
      { code: 'ur', name: 'اردو', nameEn: 'Urdu', direction: 'rtl' },
      { code: 'bn', name: 'বাংলা', nameEn: 'Bengali', direction: 'ltr' },
      { code: 'tl', name: 'Tagalog', nameEn: 'Tagalog', direction: 'ltr' },
      { code: 'fr', name: 'Français', nameEn: 'French', direction: 'ltr' }
    ];
  }

  /**
   * Translate key to specific locale
   */
  translate(key, locale = this.defaultLocale, interpolations = {}) {
    try {
      return i18n.__({ phrase: key, locale }, interpolations);
    } catch (error) {
      logger.error(`Translation error for key: ${key}`, error);
      return key;
    }
  }

  /**
   * Translate object keys recursively
   */
  translateObject(obj, locale, keysToTranslate = []) {
    if (!obj || typeof obj !== 'object') return obj;

    const translated = Array.isArray(obj) ? [...obj] : { ...obj };

    for (const key in translated) {
      if (typeof translated[key] === 'string' && 
          (keysToTranslate.length === 0 || keysToTranslate.includes(key))) {
        translated[key] = this.translate(translated[key], locale);
      } else if (typeof translated[key] === 'object') {
        translated[key] = this.translateObject(translated[key], locale, keysToTranslate);
      }
    }

    return translated;
  }

  /**
   * Get localized message for errors
   */
  getErrorMessage(errorCode, locale = this.defaultLocale) {
    return this.translate(`errors.${errorCode}`, locale);
  }

  /**
   * Format date in locale
   */
  formatDate(date, locale = this.defaultLocale, options = {}) {
    const defaultOptions = {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      ...options
    };

    return new Date(date).toLocaleDateString(
      this.getBCP47Locale(locale), 
      defaultOptions
    );
  }

  /**
   * Format currency in locale
   */
  formatCurrency(amount, currency = 'SAR', locale = this.defaultLocale) {
    return new Intl.NumberFormat(this.getBCP47Locale(locale), {
      style: 'currency',
      currency: currency
    }).format(amount);
  }

  /**
   * Format number in locale
   */
  formatNumber(number, locale = this.defaultLocale, options = {}) {
    return new Intl.NumberFormat(this.getBCP47Locale(locale), options).format(number);
  }

  /**
   * Get BCP 47 locale code
   */
  getBCP47Locale(locale) {
    const localeMap = {
      'ar': 'ar-SA',
      'en': 'en-US',
      'ur': 'ur-PK',
      'bn': 'bn-BD',
      'tl': 'tl-PH',
      'fr': 'fr-FR'
    };

    return localeMap[locale] || locale;
  }

  /**
   * Detect user's preferred language
   */
  detectLanguage(req) {
    // Check query param
    if (req.query.lang && this.supportedLocales.includes(req.query.lang)) {
      return req.query.lang;
    }

    // Check cookie
    if (req.cookies?.locale && this.supportedLocales.includes(req.cookies.locale)) {
      return req.cookies.locale;
    }

    // Check Accept-Language header
    const acceptLanguage = req.headers['accept-language'];
    if (acceptLanguage) {
      const preferred = acceptLanguage
        .split(',')[0]
        .split('-')[0]
        .toLowerCase();
      
      if (this.supportedLocales.includes(preferred)) {
        return preferred;
      }
    }

    // Default
    return this.defaultLocale;
  }

  /**
   * Apply localization middleware
   */
  middleware() {
    return (req, res, next) => {
      const locale = this.detectLanguage(req);
      
      req.locale = locale;
      res.locals.locale = locale;
      req.setLocale(locale);
      
      // Add translation function to response
      res.locals.__ = (key, ...args) => this.translate(key, locale, ...args);
      
      next();
    };
  }

  /**
   * Get translation file contents
   */
  getTranslations(locale) {
    try {
      const translations = require(`../locales/${locale}.json`);
      return translations;
    } catch (error) {
      logger.error(`Failed to load translations for ${locale}`, error);
      return {};
    }
  }

  /**
   * Validate locale
   */
  isValidLocale(locale) {
    return this.supportedLocales.includes(locale);
  }
}

module.exports = new LocalizationService();
