/**
 * ============================================
 * 🟢 Green Theme Components - Saska Style
 * نظام إدهام - مكونات الواجهة الخضراء
 * ============================================
 */

import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Mail, Lock, Eye, EyeOff, User, Calendar, 
  MapPin, Phone, Check, ChevronRight, 
  Share2, Globe, Apple, Shield, Fingerprint
} from 'lucide-react';

// Color Palette - Green Theme
const GREEN_COLORS = {
  primary: '#10b981',
  primaryDark: '#059669',
  primaryLight: '#34d399',
  secondary: '#065f46',
  background: '#064e3b',
  surface: '#0f766e',
  card: '#14532d',
  text: '#ecfdf5',
  textSecondary: '#a7f3d0',
  border: '#047857',
  success: '#22c55e',
  warning: '#f59e0b',
  error: '#ef4444'
};

// Animation Variants
const animations = {
  fadeIn: {
    initial: { opacity: 0, y: 20 },
    animate: { opacity: 1, y: 0 },
    exit: { opacity: 0, y: -20 }
  },
  slideUp: {
    initial: { y: 100, opacity: 0 },
    animate: { y: 0, opacity: 1 },
    exit: { y: 100, opacity: 0 }
  },
  scale: {
    initial: { scale: 0.9, opacity: 0 },
    animate: { scale: 1, opacity: 1 },
    exit: { scale: 0.9, opacity: 0 }
  }
};

// ============================================
// 🟢 Green Theme Button Component
// ============================================
export const GreenButton = ({ 
  children, 
  variant = 'primary', 
  size = 'medium',
  fullWidth = false,
  icon: Icon,
  onClick,
  disabled = false,
  loading = false,
  ...props 
}) => {
  const baseStyles = {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '8px',
    border: 'none',
    borderRadius: '12px',
    fontWeight: '600',
    cursor: disabled ? 'not-allowed' : 'pointer',
    transition: 'all 0.3s ease',
    fontFamily: 'inherit'
  };

  const variants = {
    primary: {
      backgroundColor: GREEN_COLORS.primary,
      color: 'white',
      '&:hover': { backgroundColor: GREEN_COLORS.primaryDark }
    },
    secondary: {
      backgroundColor: 'transparent',
      color: GREEN_COLORS.primary,
      border: `2px solid ${GREEN_COLORS.primary}`,
      '&:hover': { backgroundColor: GREEN_COLORS.primary, color: 'white' }
    },
    outline: {
      backgroundColor: 'transparent',
      color: GREEN_COLORS.text,
      border: `1px solid ${GREEN_COLORS.border}`,
      '&:hover': { backgroundColor: GREEN_COLORS.surface }
    }
  };

  const sizes = {
    small: { padding: '8px 16px', fontSize: '14px' },
    medium: { padding: '12px 24px', fontSize: '16px' },
    large: { padding: '16px 32px', fontSize: '18px' }
  };

  return (
    <motion.button
      style={{
        ...baseStyles,
        ...variants[variant],
        ...sizes[size],
        width: fullWidth ? '100%' : 'auto',
        opacity: disabled ? 0.6 : 1
      }}
      whileHover={{ scale: disabled ? 1 : 1.02 }}
      whileTap={{ scale: disabled ? 1 : 0.98 }}
      onClick={onClick}
      disabled={disabled || loading}
      {...props}
    >
      {loading ? (
        <div className="animate-spin rounded-full h-5 w-5 border-2 border-white border-t-transparent" />
      ) : (
        <>
          {Icon && <Icon size={18} />}
          {children}
        </>
      )}
    </motion.button>
  );
};

// ============================================
// 🟢 Green Theme Input Component
// ============================================
export const GreenInput = ({ 
  label, 
  type = 'text', 
  placeholder, 
  value, 
  onChange, 
  icon: Icon,
  error = '',
  required = false,
  ...props 
}) => {
  const [showPassword, setShowPassword] = useState(false);
  const [isFocused, setIsFocused] = useState(false);

  const inputStyles = {
    width: '100%',
    padding: '12px 16px',
    paddingLeft: Icon ? '48px' : '16px',
    paddingRight: type === 'password' ? '48px' : '16px',
    backgroundColor: GREEN_COLORS.surface,
    border: `2px solid ${error ? GREEN_COLORS.error : isFocused ? GREEN_COLORS.primary : GREEN_COLORS.border}`,
    borderRadius: '12px',
    color: GREEN_COLORS.text,
    fontSize: '16px',
    outline: 'none',
    transition: 'all 0.3s ease'
  };

  return (
    <motion.div 
      style={{ marginBottom: '20px' }}
      initial={{ opacity: 0, x: -20 }}
      animate={{ opacity: 1, x: 0 }}
    >
      {label && (
        <label style={{ 
          display: 'block', 
          marginBottom: '8px', 
          color: GREEN_COLORS.text,
          fontWeight: '500',
          fontSize: '14px'
        }}>
          {label} {required && <span style={{ color: GREEN_COLORS.error }}>*</span>}
        </label>
      )}
      
      <div style={{ position: 'relative' }}>
        {Icon && (
          <div style={{
            position: 'absolute',
            left: '16px',
            top: '50%',
            transform: 'translateY(-50%)',
            color: GREEN_COLORS.textSecondary,
            zIndex: 1
          }}>
            <Icon size={20} />
          </div>
        )}
        
        <input
          type={type === 'password' ? (showPassword ? 'text' : 'password') : type}
          style={inputStyles}
          placeholder={placeholder}
          value={value}
          onChange={onChange}
          onFocus={() => setIsFocused(true)}
          onBlur={() => setIsFocused(false)}
          {...props}
        />
        
        {type === 'password' && (
          <button
            type="button"
            onClick={() => setShowPassword(!showPassword)}
            style={{
              position: 'absolute',
              right: '16px',
              top: '50%',
              transform: 'translateY(-50%)',
              background: 'none',
              border: 'none',
              color: GREEN_COLORS.textSecondary,
              cursor: 'pointer',
              padding: '4px'
            }}
          >
            {showPassword ? <EyeOff size={20} /> : <Eye size={20} />}
          </button>
        )}
      </div>
      
      {error && (
        <motion.p 
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          style={{ 
            color: GREEN_COLORS.error, 
            fontSize: '12px', 
            marginTop: '4px',
            display: 'flex',
            alignItems: 'center',
            gap: '4px'
          }}
        >
          {error}
        </motion.p>
      )}
    </motion.div>
  );
};

// ============================================
// 🟢 Green Theme Card Component
// ============================================
export const GreenCard = ({ 
  children, 
  padding = '24px', 
  shadow = true,
  hover = false,
  ...props 
}) => {
  return (
    <motion.div
      style={{
        backgroundColor: GREEN_COLORS.card,
        borderRadius: '16px',
        padding,
        boxShadow: shadow ? '0 8px 32px rgba(0, 0, 0, 0.3)' : 'none',
        border: `1px solid ${GREEN_COLORS.border}`,
        transition: 'all 0.3s ease'
      }}
      whileHover={hover ? { y: -4, boxShadow: '0 12px 40px rgba(0, 0, 0, 0.4)' } : {}}
      {...props}
    >
      {children}
    </motion.div>
  );
};

// ============================================
// 🟢 Social Login Buttons
// ============================================
export const SocialLoginButtons = ({ onSocialLogin }) => {
  const socialButtons = [
    {
      name: 'Google',
      icon: Globe,
      color: '#4285f4',
      hoverColor: '#357ae8'
    },
    {
      name: 'Facebook',
      icon: Share2,
      color: '#1877f2',
      hoverColor: '#166fe5'
    },
    {
      name: 'Apple',
      icon: Apple,
      color: '#000000',
      hoverColor: '#333333'
    }
  ];

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
      {socialButtons.map((social) => (
        <motion.button
          key={social.name}
          onClick={() => onSocialLogin(social.name.toLowerCase())}
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: '12px',
            padding: '12px 20px',
            backgroundColor: social.color,
            color: 'white',
            border: 'none',
            borderRadius: '12px',
            fontSize: '16px',
            fontWeight: '500',
            cursor: 'pointer',
            transition: 'all 0.3s ease'
          }}
          whileHover={{ scale: 1.02, backgroundColor: social.hoverColor }}
          whileTap={{ scale: 0.98 }}
        >
          <social.icon size={20} />
          Sign in with {social.name}
        </motion.button>
      ))}
    </div>
  );
};

// ============================================
// 🟢 Onboarding Screen Component
// ============================================
export const OnboardingScreen = ({ 
  image, 
  title, 
  description, 
  onNext, 
  isLast = false 
}) => {
  return (
    <motion.div
      style={{
        minHeight: '100vh',
        backgroundColor: GREEN_COLORS.background,
        display: 'flex',
        flexDirection: 'column',
        padding: '20px'
      }}
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
    >
      {/* Image Section */}
      <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <motion.img
          src={image}
          alt={title}
          style={{ maxWidth: '100%', maxHeight: '300px', borderRadius: '16px' }}
          initial={{ scale: 0.8, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          transition={{ delay: 0.2 }}
        />
      </div>

      {/* Content Section */}
      <GreenCard>
        <motion.h2
          style={{
            color: GREEN_COLORS.text,
            fontSize: '28px',
            fontWeight: 'bold',
            marginBottom: '16px',
            textAlign: 'center'
          }}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
        >
          {title}
        </motion.h2>
        
        <motion.p
          style={{
            color: GREEN_COLORS.textSecondary,
            fontSize: '16px',
            lineHeight: '1.6',
            textAlign: 'center',
            marginBottom: '32px'
          }}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
        >
          {description}
        </motion.p>

        <GreenButton
          onClick={onNext}
          fullWidth
          size="large"
          icon={ChevronRight}
        >
          {isLast ? 'Get Started' : 'Next'}
        </GreenButton>
      </GreenCard>
    </motion.div>
  );
};

// ============================================
// 🟢 PIN Input Component
// ============================================
export const PinInput = ({ length = 4, onComplete, error = '' }) => {
  const [pin, setPin] = useState('');
  const [showError, setShowError] = useState(false);

  const handleKeyPress = (key) => {
    if (key === 'clear') {
      setPin('');
      setShowError(false);
      return;
    }

    if (pin.length < length) {
      const newPin = pin + key;
      setPin(newPin);
      
      if (newPin.length === length) {
        setTimeout(() => {
          if (onComplete) {
            const isValid = onComplete(newPin);
            if (!isValid) {
              setShowError(true);
              setTimeout(() => {
                setPin('');
                setShowError(false);
              }, 1000);
            }
          }
        }, 200);
      }
    }
  };

  const pinNumbers = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '', '0', 'clear'];

  return (
    <motion.div
      style={{
        backgroundColor: GREEN_COLORS.background,
        minHeight: '100vh',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '20px'
      }}
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
    >
      {/* PIN Display */}
      <div style={{ marginBottom: '40px' }}>
        <div style={{ 
          display: 'flex', 
          gap: '16px', 
          marginBottom: '20px' 
        }}>
          {Array.from({ length }).map((_, index) => (
            <motion.div
              key={index}
              style={{
                width: '60px',
                height: '60px',
                borderRadius: '12px',
                border: `2px solid ${showError ? GREEN_COLORS.error : GREEN_COLORS.border}`,
                backgroundColor: GREEN_COLORS.surface,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}
              animate={{
                scale: pin[index] ? [1, 1.1, 1] : 1,
                borderColor: pin[index] ? GREEN_COLORS.primary : (showError ? GREEN_COLORS.error : GREEN_COLORS.border)
              }}
              transition={{ duration: 0.2 }}
            >
              {pin[index] && (
                <motion.div
                  style={{
                    width: '20px',
                    height: '20px',
                    borderRadius: '50%',
                    backgroundColor: GREEN_COLORS.primary
                  }}
                  initial={{ scale: 0 }}
                  animate={{ scale: 1 }}
                />
              )}
            </motion.div>
          ))}
        </div>
        
        {showError && (
          <motion.p
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            style={{
              color: GREEN_COLORS.error,
              textAlign: 'center',
              fontSize: '14px'
            }}
          >
            {error || 'Invalid PIN. Please try again.'}
          </motion.p>
        )}
      </div>

      {/* PIN Keypad */}
      <div style={{ 
        display: 'grid', 
        gridTemplateColumns: 'repeat(3, 1fr)', 
        gap: '16px',
        maxWidth: '300px'
      }}>
        {pinNumbers.map((key, index) => (
          <motion.button
            key={index}
            onClick={() => handleKeyPress(key)}
            disabled={!key}
            style={{
              width: '80px',
              height: '80px',
              borderRadius: '16px',
              backgroundColor: key ? GREEN_COLORS.surface : 'transparent',
              border: key ? `2px solid ${GREEN_COLORS.border}` : 'none',
              color: GREEN_COLORS.text,
              fontSize: '24px',
              fontWeight: '600',
              cursor: key ? 'pointer' : 'default',
              transition: 'all 0.3s ease'
            }}
            whileHover={key ? { scale: 1.05, backgroundColor: GREEN_COLORS.primary } : {}}
            whileTap={key ? { scale: 0.95 } : {}}
          >
            {key === 'clear' ? (
              <motion.div
                initial={{ rotate: 0 }}
                whileTap={{ rotate: 180 }}
              >
                ✕
              </motion.div>
            ) : (
              key
            )}
          </motion.button>
        ))}
      </div>
    </motion.div>
  );
};

// ============================================
// 🟢 Fingerprint Component
// ============================================
export const FingerprintAuth = ({ onSuccess, error = '' }) => {
  const [scanning, setScanning] = useState(false);
  const [showError, setShowError] = useState(false);

  const handleFingerprint = () => {
    setScanning(true);
    setShowError(false);
    
    // Simulate fingerprint scan
    setTimeout(() => {
      setScanning(false);
      const success = Math.random() > 0.3; // 70% success rate for demo
      
      if (success && onSuccess) {
        onSuccess();
      } else {
        setShowError(true);
        setTimeout(() => setShowError(false), 2000);
      }
    }, 2000);
  };

  return (
    <motion.div
      style={{
        backgroundColor: GREEN_COLORS.background,
        minHeight: '100vh',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '20px'
      }}
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
    >
      <motion.div
        style={{
          width: '120px',
          height: '120px',
          borderRadius: '50%',
          backgroundColor: GREEN_COLORS.surface,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          border: `3px solid ${showError ? GREEN_COLORS.error : GREEN_COLORS.primary}`,
          cursor: 'pointer',
          position: 'relative'
        }}
        onClick={handleFingerprint}
        whileHover={{ scale: 1.05 }}
        whileTap={{ scale: 0.95 }}
      >
        <Fingerprint 
          size={60} 
          color={scanning ? GREEN_COLORS.primary : GREEN_COLORS.text}
        />
        
        {scanning && (
          <motion.div
            style={{
              position: 'absolute',
              width: '140px',
              height: '140px',
              borderRadius: '50%',
              border: `3px solid ${GREEN_COLORS.primary}`,
              borderTopColor: 'transparent'
            }}
            animate={{ rotate: 360 }}
            transition={{ duration: 1.5, repeat: Infinity, ease: 'linear' }}
          />
        )}
      </motion.div>

      <motion.h2
        style={{
          color: GREEN_COLORS.text,
          fontSize: '24px',
          fontWeight: 'bold',
          marginTop: '32px',
          marginBottom: '16px',
          textAlign: 'center'
        }}
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.2 }}
      >
        {scanning ? 'Scanning...' : 'Place your finger'}
      </motion.h2>

      <motion.p
        style={{
          color: GREEN_COLORS.textSecondary,
          fontSize: '16px',
          textAlign: 'center',
          marginBottom: '32px'
        }}
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.3 }}
      >
        Touch the fingerprint sensor to authenticate
      </motion.p>

      {showError && (
        <motion.div
          initial={{ opacity: 0, scale: 0.8 }}
          animate={{ opacity: 1, scale: 1 }}
          style={{
            backgroundColor: GREEN_COLORS.error,
            color: 'white',
            padding: '12px 24px',
            borderRadius: '8px',
            fontSize: '14px',
            textAlign: 'center'
          }}
        >
          {error || 'Authentication failed. Please try again.'}
        </motion.div>
      )}
    </motion.div>
  );
};

// ============================================
// 🟢 Success Popup Component
// ============================================
export const SuccessPopup = ({ show, onClose, title = 'Congratulations!', message = '' }) => {
  return (
    <AnimatePresence>
      {show && (
        <motion.div
          style={{
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.8)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 1000
          }}
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          onClick={onClose}
        >
          <motion.div
            style={{
              backgroundColor: GREEN_COLORS.card,
              borderRadius: '20px',
              padding: '40px',
              textAlign: 'center',
              maxWidth: '400px',
              width: '90%'
            }}
            initial={{ scale: 0.8, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            exit={{ scale: 0.8, opacity: 0 }}
            onClick={(e) => e.stopPropagation()}
          >
            <motion.div
              style={{
                width: '80px',
                height: '80px',
                borderRadius: '50%',
                backgroundColor: GREEN_COLORS.success,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                margin: '0 auto 24px'
              }}
              initial={{ scale: 0 }}
              animate={{ scale: 1 }}
              transition={{ delay: 0.2, type: 'spring' }}
            >
              <Check size={40} color="white" />
            </motion.div>
            
            <motion.h2
              style={{
                color: GREEN_COLORS.text,
                fontSize: '24px',
                fontWeight: 'bold',
                marginBottom: '12px'
              }}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.3 }}
            >
              {title}
            </motion.h2>
            
            {message && (
              <motion.p
                style={{
                  color: GREEN_COLORS.textSecondary,
                  fontSize: '16px',
                  marginBottom: '24px'
                }}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: 0.4 }}
              >
                {message}
              </motion.p>
            )}
            
            <GreenButton onClick={onClose} fullWidth>
              Continue
            </GreenButton>
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );
};

export default GREEN_COLORS;
