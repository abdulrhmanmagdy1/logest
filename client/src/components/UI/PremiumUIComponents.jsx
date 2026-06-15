/**
 * ============================================
 * 🎨 Premium UI Components Library
 * Enhanced animations and micro-interactions
 * ============================================
 */

import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { ChevronDown, Check, AlertCircle, Info } from 'lucide-react';

// ========================
// PREMIUM BUTTON VARIANTS
// ========================

export const PremiumButton = ({
  children,
  variant = 'primary',
  size = 'md',
  loading = false,
  disabled = false,
  onClick,
  className = '',
  ...props
}) => {
  const variants = {
    primary: 'bg-gradient-to-r from-blue-500 to-blue-600 text-white hover:from-blue-600 hover:to-blue-700',
    secondary: 'bg-gray-200 text-gray-800 hover:bg-gray-300',
    danger: 'bg-gradient-to-r from-red-500 to-red-600 text-white hover:from-red-600 hover:to-red-700',
    success: 'bg-gradient-to-r from-green-500 to-green-600 text-white hover:from-green-600 hover:to-green-700'
  };

  const sizes = {
    sm: 'px-3 py-1 text-sm',
    md: 'px-4 py-2 text-base',
    lg: 'px-6 py-3 text-lg'
  };

  return (
    <motion.button
      whileHover={{ scale: disabled ? 1 : 1.02 }}
      whileTap={{ scale: disabled ? 1 : 0.98 }}
      onClick={onClick}
      disabled={disabled || loading}
      className={`
        rounded-lg font-semibold transition-all duration-200
        disabled:opacity-50 disabled:cursor-not-allowed
        flex items-center gap-2 justify-center
        ${variants[variant]} ${sizes[size]} ${className}
      `}
      {...props}
    >
      {loading && <div className="animate-spin h-4 w-4 border-2 border-white border-t-transparent rounded-full" />}
      {children}
    </motion.button>
  );
};

// ========================
// SKELETON LOADERS
// ========================

export const SkeletonLoader = ({ width = 'w-full', height = 'h-4', count = 3 }) => {
  return (
    <div className="space-y-2">
      {Array.from({ length: count }).map((_, i) => (
        <motion.div
          key={i}
          className={`${width} ${height} bg-gradient-to-r from-gray-200 via-gray-300 to-gray-200 rounded-lg`}
          animate={{
            backgroundPosition: ['0% 0%', '100% 0%', '0% 0%']
          }}
          transition={{
            duration: 2,
            repeat: Infinity
          }}
        />
      ))}
    </div>
  );
};

// ========================
// EMPTY STATE
// ========================

export const EmptyState = ({ icon: Icon, title, description, action }) => {
  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.9 }}
      animate={{ opacity: 1, scale: 1 }}
      className="flex flex-col items-center justify-center py-12 px-4"
    >
      <motion.div
        animate={{ y: [0, -10, 0] }}
        transition={{ duration: 3, repeat: Infinity }}
        className="mb-4"
      >
        <Icon className="w-16 h-16 text-gray-400" />
      </motion.div>
      <h3 className="text-lg font-semibold text-gray-700 mb-2">{title}</h3>
      <p className="text-gray-500 text-center mb-6 max-w-md">{description}</p>
      {action && (
        <PremiumButton onClick={action.onClick}>
          {action.label}
        </PremiumButton>
      )}
    </motion.div>
  );
};

// ========================
// ANIMATED CARD
// ========================

export const AnimatedCard = ({ children, hover = true, delay = 0 }) => {
  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay }}
      whileHover={hover ? { y: -4, boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1)' } : {}}
      className="bg-white rounded-lg shadow-md overflow-hidden"
    >
      {children}
    </motion.div>
  );
};

// ========================
// ALERT COMPONENTS
// ========================

export const Alert = ({ type = 'info', title, message, onClose, actions = [] }) => {
  const colors = {
    success: { bg: 'bg-green-50', border: 'border-green-200', icon: Check, text: 'text-green-800' },
    error: { bg: 'bg-red-50', border: 'border-red-200', icon: AlertCircle, text: 'text-red-800' },
    warning: { bg: 'bg-amber-50', border: 'border-amber-200', icon: AlertCircle, text: 'text-amber-800' },
    info: { bg: 'bg-blue-50', border: 'border-blue-200', icon: Info, text: 'text-blue-800' }
  };

  const config = colors[type];
  const Icon = config.icon;

  return (
    <motion.div
      initial={{ opacity: 0, x: -20 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: -20 }}
      className={`${config.bg} border ${config.border} rounded-lg p-4 flex items-start gap-4 ${config.text}`}
    >
      <Icon className="w-5 h-5 flex-shrink-0 mt-0.5" />
      <div className="flex-1">
        {title && <h3 className="font-semibold mb-1">{title}</h3>}
        <p className="text-sm">{message}</p>
        {actions.length > 0 && (
          <div className="flex gap-2 mt-3">
            {actions.map((action, idx) => (
              <button
                key={idx}
                onClick={action.onClick}
                className={`text-sm font-semibold underline hover:no-underline`}
              >
                {action.label}
              </button>
            ))}
          </div>
        )}
      </div>
      {onClose && (
        <button onClick={onClose} className="flex-shrink-0">
          ✕
        </button>
      )}
    </motion.div>
  );
};

// ========================
// DROPDOWN WITH ANIMATION
// ========================

export const AnimatedDropdown = ({ options, selected, onChange, placeholder = 'اختر خياراً' }) => {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <div className="relative">
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="w-full px-4 py-2 bg-white border border-gray-300 rounded-lg flex items-center justify-between hover:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-all"
      >
        <span>{selected?.label || placeholder}</span>
        <motion.div animate={{ rotate: isOpen ? 180 : 0 }}>
          <ChevronDown className="w-4 h-4" />
        </motion.div>
      </button>

      <AnimatePresence>
        {isOpen && (
          <motion.div
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            className="absolute top-full left-0 right-0 mt-2 bg-white border border-gray-300 rounded-lg shadow-lg z-10 overflow-hidden"
          >
            {options.map((option, idx) => (
              <motion.button
                key={idx}
                onClick={() => {
                  onChange(option);
                  setIsOpen(false);
                }}
                whileHover={{ backgroundColor: '#f3f4f6' }}
                className="w-full px-4 py-2 text-left hover:bg-gray-100 transition-colors"
              >
                {option.label}
              </motion.button>
            ))}
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

// ========================
// PROGRESS BAR
// ========================

export const ProgressBar = ({ value, max = 100, animated = true, color = 'blue' }) => {
  const percentage = (value / max) * 100;
  const colors = {
    blue: 'bg-blue-500',
    green: 'bg-green-500',
    red: 'bg-red-500',
    amber: 'bg-amber-500'
  };

  return (
    <div className="w-full bg-gray-200 rounded-full h-2 overflow-hidden">
      <motion.div
        className={`h-full ${colors[color]}`}
        initial={{ width: 0 }}
        animate={{ width: `${percentage}%` }}
        transition={{ duration: animated ? 0.5 : 0 }}
      />
    </div>
  );
};

// ========================
// STAT CARD WITH ANIMATION
// ========================

export const StatCard = ({ icon: Icon, label, value, trend, unit = '' }) => {
  const isTrendUp = trend >= 0;

  return (
    <AnimatedCard hover delay={0}>
      <div className="p-6">
        <div className="flex items-center justify-between mb-4">
          <div className="p-3 bg-blue-100 rounded-lg">
            <Icon className="w-6 h-6 text-blue-600" />
          </div>
          {trend !== undefined && (
            <motion.div
              initial={{ scale: 0 }}
              animate={{ scale: 1 }}
              className={`flex items-center gap-1 px-2 py-1 rounded-lg ${
                isTrendUp ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'
              }`}
            >
              <span className="text-sm font-semibold">
                {isTrendUp ? '+' : ''}{trend}%
              </span>
            </motion.div>
          )}
        </div>
        <p className="text-gray-600 text-sm mb-2">{label}</p>
        <motion.p
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="text-2xl font-bold text-gray-800"
        >
          {value}
          {unit && <span className="text-sm text-gray-500 ml-2">{unit}</span>}
        </motion.p>
      </div>
    </AnimatedCard>
  );
};

// ========================
// FADE TRANSITION WRAPPER
// ========================

export const FadeTransition = ({ children, isVisible = true }) => {
  return (
    <AnimatePresence>
      {isVisible && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          transition={{ duration: 0.3 }}
        >
          {children}
        </motion.div>
      )}
    </AnimatePresence>
  );
};

export default {
  PremiumButton,
  SkeletonLoader,
  EmptyState,
  AnimatedCard,
  Alert,
  AnimatedDropdown,
  ProgressBar,
  StatCard,
  FadeTransition
};
