/**
 * ============================================
 * 🎨 Edham Logistics - Authentication Page
 * نظام إدهام - صفحة المصادقة
 * Modern dark theme with orange accents
 * ============================================
 */

import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { 
  Mail, Lock, Eye, EyeOff, ArrowRight, User, Shield
} from 'lucide-react';
import BottomNavigation from '../components/BottomNavigation';
import './AuthenticationPage.css';

const AuthenticationPage = () => {
  const [activeTab, setActiveTab] = useState('profile');
  const [showPassword, setShowPassword] = useState(false);
  const [rememberMe, setRememberMe] = useState(false);
  
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });

  const [errors, setErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);

  const validateForm = () => {
    const newErrors = {};
    
    if (!formData.email) newErrors.email = 'Email is required';
    if (!formData.password) newErrors.password = 'Password is required';
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;
    
    setIsLoading(true);
    // Simulate API call
    setTimeout(() => {
      setIsLoading(false);
      // Navigate to dashboard on success
      window.location.href = '/dashboard';
    }, 2000);
  };

  const handleSocialLogin = (provider) => {
    setIsLoading(true);
    // Simulate social login
    setTimeout(() => {
      setIsLoading(false);
      window.location.href = '/dashboard';
    }, 1500);
  };

  // Login Screen
  const LoginScreen = () => (
    <motion.div
      initial={{ opacity: 0, x: -300 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: 300 }}
      className="auth-screen"
    >
      <div className="auth-container">
        <motion.div
          initial={{ scale: 0.8, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ delay: 0.2 }}
          className="auth-header"
        >
          <div className="auth-logo">
            <span className="logo-icon">🚛️</span>
          </div>
          <h1 className="auth-title">مرحباً بعودتك</h1>
          <p className="auth-subtitle">سجل دخولك لحسابك في نظام إدهام اللوجستي</p>
        </motion.div>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <div className="input-wrapper">
              <Mail className="input-icon" />
              <input
                type="email"
                placeholder="البريد الإلكتروني"
                value={formData.email}
                onChange={(e) => setFormData({...formData, email: e.target.value})}
                className={`auth-input ${errors.email ? 'error' : ''}`}
              />
            </div>
            {errors.email && <span className="error-message">{errors.email}</span>}
          </div>

          <div className="form-group">
            <div className="input-wrapper">
              <Lock className="input-icon" />
              <input
                type={showPassword ? 'text' : 'password'}
                placeholder="كلمة المرور"
                value={formData.password}
                onChange={(e) => setFormData({...formData, password: e.target.value})}
                className={`auth-input ${errors.password ? 'error' : ''}`}
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="password-toggle"
              >
                {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
              </button>
            </div>
            {errors.password && <span className="error-message">{errors.password}</span>}
          </div>

          <div className="form-options">
            <label className="checkbox-label">
              <input type="checkbox" />
              <span>تذكرني</span>
            </label>
            <a href="#" className="forgot-password">نسيت كلمة المرور؟</a>
          </div>

          <motion.button
            type="submit"
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            disabled={isLoading}
            className="auth-button primary"
          >
            {isLoading ? (
              <div className="loading-spinner"></div>
            ) : (
              'تسجيل الدخول'
            )}
          </motion.button>
        </form>

        <div className="social-auth">
          <div className="divider">
            <span>أو سجل الدخول باستخدام</span>
          </div>
          <div className="social-buttons">
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => handleSocialLogin('google')}
              className="social-button google"
            >
              <Globe className="w-5 h-5" />
              <span>Google</span>
            </motion.button>
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => handleSocialLogin('apple')}
              className="social-button apple"
            >
              <Apple className="w-5 h-5" />
              <span>Apple</span>
            </motion.button>
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => handleSocialLogin('facebook')}
              className="social-button facebook"
            >
              <Share2 className="w-5 h-5" />
              <span>Facebook</span>
            </motion.button>
          </div>
        </div>

        <div className="auth-footer">
          <p>ليس لديك حساب؟</p>
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={() => setCurrentView('register')}
            className="link-button"
          >
            إنشاء حساب جديد
          </motion.button>
        </div>
      </div>
    </motion.div>
  );

  // Register Screen
  const RegisterScreen = () => (
    <motion.div
      initial={{ opacity: 0, x: -300 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: 300 }}
      className="auth-screen"
    >
      <div className="auth-container">
        <motion.div
          initial={{ scale: 0.8, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ delay: 0.2 }}
          className="auth-header"
        >
          <div className="auth-logo">
            <span className="logo-icon">🚛️</span>
          </div>
          <h1 className="auth-title">إنشاء حساب جديد</h1>
          <p className="auth-subtitle">انضم إلى نظام إدهام اللوجستي</p>
        </motion.div>

        <form onSubmit={handleSubmit} className="auth-form">
          {/* Profile Image */}
          <div className="profile-image-section">
            <div className="image-upload-container">
              <div className="image-preview">
                {profileImage ? (
                  <img src={profileImage} alt="Profile" />
                ) : (
                  <User className="w-16 h-16 text-gray-400" />
                )}
              </div>
              <label className="image-upload-button">
                <Camera className="w-5 h-5" />
                <input
                  type="file"
                  accept="image/*"
                  onChange={handleImageUpload}
                  className="hidden"
                />
              </label>
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <div className="input-wrapper">
                <User className="input-icon" />
                <input
                  type="text"
                  placeholder="الاسم الكامل"
                  value={formData.fullName}
                  onChange={(e) => setFormData({...formData, fullName: e.target.value})}
                  className={`auth-input ${errors.fullName ? 'error' : ''}`}
                />
              </div>
              {errors.fullName && <span className="error-message">{errors.fullName}</span>}
            </div>
            <div className="form-group">
              <div className="input-wrapper">
                <User className="input-icon" />
                <input
                  type="text"
                  placeholder="الاسم المستعار"
                  value={formData.nickname}
                  onChange={(e) => setFormData({...formData, nickname: e.target.value})}
                  className="auth-input"
                />
              </div>
            </div>
          </div>

          <div className="form-group">
            <div className="input-wrapper">
              <Mail className="input-icon" />
              <input
                type="email"
                placeholder="البريد الإلكتروني"
                value={formData.email}
                onChange={(e) => setFormData({...formData, email: e.target.value})}
                className={`auth-input ${errors.email ? 'error' : ''}`}
              />
            </div>
            {errors.email && <span className="error-message">{errors.email}</span>}
          </div>

          <div className="form-group">
            <div className="input-wrapper">
              <Phone className="input-icon" />
              <input
                type="tel"
                placeholder="رقم الهاتف"
                value={formData.phone}
                onChange={(e) => setFormData({...formData, phone: e.target.value})}
                className="auth-input"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-group">
              <div className="input-wrapper">
                <Calendar className="input-icon" />
                <input
                  type="date"
                  value={formData.birthDate}
                  onChange={(e) => setFormData({...formData, birthDate: e.target.value})}
                  className="auth-input"
                />
              </div>
            </div>
            <div className="form-group">
              <div className="input-wrapper">
                <User className="input-icon" />
                <select
                  value={formData.gender}
                  onChange={(e) => setFormData({...formData, gender: e.target.value})}
                  className="auth-input"
                >
                  <option value="">النوع</option>
                  <option value="male">ذكر</option>
                  <option value="female">أنثى</option>
                </select>
              </div>
            </div>
          </div>

          <div className="form-group">
            <div className="input-wrapper">
              <Lock className="input-icon" />
              <input
                type={showPassword ? 'text' : 'password'}
                placeholder="كلمة المرور"
                value={formData.password}
                onChange={(e) => setFormData({...formData, password: e.target.value})}
                className={`auth-input ${errors.password ? 'error' : ''}`}
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="password-toggle"
              >
                {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
              </button>
            </div>
            {errors.password && <span className="error-message">{errors.password}</span>}
          </div>

          <div className="form-group">
            <div className="input-wrapper">
              <Lock className="input-icon" />
              <input
                type={showConfirmPassword ? 'text' : 'password'}
                placeholder="تأكيد كلمة المرور"
                value={formData.confirmPassword}
                onChange={(e) => setFormData({...formData, confirmPassword: e.target.value})}
                className={`auth-input ${errors.confirmPassword ? 'error' : ''}`}
              />
              <button
                type="button"
                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                className="password-toggle"
              >
                {showConfirmPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
              </button>
            </div>
            {errors.confirmPassword && <span className="error-message">{errors.confirmPassword}</span>}
          </div>

          <motion.button
            type="submit"
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            disabled={isLoading}
            className="auth-button primary"
          >
            {isLoading ? (
              <div className="loading-spinner"></div>
            ) : (
              'إنشاء الحساب'
            )}
          </motion.button>
        </form>

        <div className="auth-footer">
          <p>لديك حساب بالفعل؟</p>
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={() => setCurrentView('login')}
            className="link-button"
          >
            تسجيل الدخول
          </motion.button>
        </div>
      </div>
    </motion.div>
  );

  // Security Setup Screen
  const SecurityScreen = () => (
    <motion.div
      initial={{ opacity: 0, x: -300 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: 300 }}
      className="auth-screen security-screen"
    >
      <div className="auth-container">
        <motion.div
          initial={{ scale: 0.8, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ delay: 0.2 }}
          className="auth-header"
        >
          <Shield className="auth-icon" />
          <h1 className="auth-title">إعدادات الأمان</h1>
          <p className="auth-subtitle">حماية حسابك بميزات الأمان المتقدمة</p>
        </motion.div>

        {/* Location Selection */}
        <div className="security-section">
          <h3 className="section-title">
            <MapPin className="w-5 h-5" />
            تحديد الموقع الافتراضي
          </h3>
          <div className="map-container">
            <div className="map-placeholder">
              <MapPin className="w-12 h-12 text-gray-400" />
              <p>اختر موقعك على الخريطة</p>
            </div>
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => setLocationSelected(!locationSelected)}
              className={`location-button ${locationSelected ? 'selected' : ''}`}
            >
              {locationSelected ? (
                <>
                  <Check className="w-5 h-5" />
                  <span>تم تحديد الموقع</span>
                </>
              ) : (
                <>
                  <MapPin className="w-5 h-5" />
                  <span>اختيار الموقع</span>
                </>
              )}
            </motion.button>
          </div>
        </div>

        {/* PIN Code Setup */}
        <div className="security-section">
          <h3 className="section-title">
            <Lock className="w-5 h-5" />
            إنشاء رمز PIN
          </h3>
          <div className="pin-container">
            <p className="pin-instruction">أدخل 4 أرقام للرمز السري</p>
            <div className="pin-inputs">
              {[0, 1, 2, 3].map((index) => (
                <input
                  key={index}
                  id={`pin-${index}`}
                  type="password"
                  maxLength={1}
                  value={pinCode[index]}
                  onChange={(e) => handlePinChange(index, e.target.value)}
                  className="pin-input"
                />
              ))}
            </div>
          </div>
        </div>

        {/* Fingerprint Setup */}
        <div className="security-section">
          <h3 className="section-title">
            <Fingerprint className="w-5 h-5" />
            المصادقة بالبصمة
          </h3>
          <div className="fingerprint-container">
            <motion.div
              animate={{ scale: fingerprintEnabled ? [1, 1.1, 1] : 1 }}
              transition={{ duration: 0.3 }}
              className="fingerprint-scanner"
            >
              <Fingerprint className="w-16 h-16" />
            </motion.div>
            <p className="fingerprint-text">
              {fingerprintEnabled ? 'تم تفعيل المصادقة بالبصمة' : 'ضع إصبعك على المستشعر'}
            </p>
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => setFingerprintEnabled(!fingerprintEnabled)}
              className={`fingerprint-button ${fingerprintEnabled ? 'enabled' : ''}`}
            >
              {fingerprintEnabled ? (
                <>
                  <Check className="w-5 h-5" />
                  <span>مفعل</span>
                </>
              ) : (
                <>
                  <Fingerprint className="w-5 h-5" />
                  <span>تفعيل البصمة</span>
                </>
              )}
            </motion.button>
          </div>
        </div>

        <div className="security-actions">
          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            onClick={() => setCurrentView('dashboard')}
            className="auth-button primary"
          >
            إكمال الإعداد
            <ChevronRight className="w-5 h-5" />
          </motion.button>
        </div>
      </div>
    </motion.div>
  );

  return (
    <div className="authentication-page">
      {/* Header */}
      <motion.header
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="auth-header"
      >
        <div className="header-content">
          <div className="logo">
            <Shield className="w-8 h-8" />
            <span>Edham Logistics</span>
          </div>
        </div>
      </motion.header>

      {/* Main Content */}
      <main className="auth-main">
        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ delay: 0.2 }}
          className="auth-container"
        >
          {/* Login Form */}
          <div className="auth-form-container">
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.3 }}
              className="auth-header-content"
            >
              <h1>Welcome Back</h1>
              <p>Sign in to your Edham Logistics account</p>
            </motion.div>

            <form onSubmit={handleSubmit} className="auth-form">
              <div className="form-group">
                <div className="input-wrapper">
                  <Mail className="input-icon" />
                  <input
                    type="email"
                    placeholder="Email address"
                    value={formData.email}
                    onChange={(e) => setFormData({...formData, email: e.target.value})}
                    className={`auth-input ${errors.email ? 'error' : ''}`}
                  />
                </div>
                {errors.email && <span className="error-message">{errors.email}</span>}
              </div>

              <div className="form-group">
                <div className="input-wrapper">
                  <Lock className="input-icon" />
                  <input
                    type={showPassword ? 'text' : 'password'}
                    placeholder="Password"
                    value={formData.password}
                    onChange={(e) => setFormData({...formData, password: e.target.value})}
                    className={`auth-input ${errors.password ? 'error' : ''}`}
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="password-toggle"
                  >
                    {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                  </button>
                </div>
                {errors.password && <span className="error-message">{errors.password}</span>}
              </div>

              <div className="form-options">
                <label className="checkbox-label">
                  <input 
                    type="checkbox" 
                    checked={rememberMe}
                    onChange={(e) => setRememberMe(e.target.checked)}
                  />
                  <span>Remember me</span>
                </label>
                <a href="#" className="forgot-password">Forgot password?</a>
              </div>

              <motion.button
                type="submit"
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
                disabled={isLoading}
                className="auth-submit-btn"
              >
                {isLoading ? (
                  <div className="loading-spinner"></div>
                ) : (
                  <>
                    Sign In
                    <ArrowRight className="w-5 h-5" />
                  </>
                )}
              </motion.button>
            </form>

            <div className="divider">
              <span>Or continue with</span>
            </div>

            <div className="social-login">
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => handleSocialLogin('google')}
                className="social-btn google"
              >
                <div className="social-icon">G</div>
                <span>Google</span>
              </motion.button>

              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => handleSocialLogin('apple')}
                className="social-btn apple"
              >
                <div className="social-icon">🍎</div>
                <span>Apple</span>
              </motion.button>
            </div>

            <div className="auth-footer">
              <p>Don't have an account?</p>
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => setCurrentView('register')}
                className="link-btn"
              >
                Sign up
              </motion.button>
            </div>
          </div>

          {/* Side Illustration */}
          <motion.div
            initial={{ opacity: 0, x: 50 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.4 }}
            className="auth-illustration"
          >
            <div className="illustration-content">
              <div className="truck-icon">🚛️</div>
              <h2>Smart Shipping Made Simple</h2>
              <p>Track your shipments in real-time, manage logistics efficiently, and deliver with confidence.</p>
              <div className="features">
                <div className="feature">
                  <Shield className="w-5 h-5" />
                  <span>Secure & Reliable</span>
                </div>
                <div className="feature">
                  <User className="w-5 h-5" />
                  <span>24/7 Support</span>
                </div>
                <div className="feature">
                  <ArrowRight className="w-5 h-5" />
                  <span>Fast Delivery</span>
                </div>
              </div>
            </div>
          </motion.div>
        </motion.div>
      </main>

      {/* Bottom Navigation */}
      <BottomNavigation 
        activeTab={activeTab}
        onTabChange={setActiveTab}
      />
    </div>
  );
};

export default AuthenticationPage;
