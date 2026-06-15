/**
 * ============================================
 * 🟠 Orange Theme Components - LoadSphere Style
 * نظام إدهام - مكونات الواجهة البرتقالية
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Package, Truck, MapPin, Clock, User, Phone, 
  Plus, Minus, AlertCircle, Navigation, Bell,
  TrendingUp, DollarSign, Calendar, CheckCircle,
  ChevronRight, ArrowUp, ArrowDown, Menu, X
} from 'lucide-react';

// Color Palette - Orange/Dark Theme
const ORANGE_COLORS = {
  primary: '#f97316',
  primaryDark: '#ea580c',
  primaryLight: '#fb923c',
  secondary: '#1f2937',
  background: '#111827',
  surface: '#1f2937',
  card: '#374151',
  cardLight: '#4b5563',
  text: '#f9fafb',
  textSecondary: '#9ca3af',
  textMuted: '#6b7280',
  border: '#374151',
  success: '#22c55e',
  warning: '#f59e0b',
  error: '#ef4444',
  accent: '#fb923c'
};

// Animation Variants
const animations = {
  slideUp: {
    initial: { y: 30, opacity: 0 },
    animate: { y: 0, opacity: 1 },
    exit: { y: -30, opacity: 0 }
  },
  fadeIn: {
    initial: { opacity: 0 },
    animate: { opacity: 1 },
    exit: { opacity: 0 }
  },
  scaleIn: {
    initial: { scale: 0.9, opacity: 0 },
    animate: { scale: 1, opacity: 1 },
    exit: { scale: 0.9, opacity: 0 }
  },
  slideInRight: {
    initial: { x: 50, opacity: 0 },
    animate: { x: 0, opacity: 1 },
    exit: { x: 50, opacity: 0 }
  }
};

// ============================================
// 🟠 Orange Theme Button Component
// ============================================
export const OrangeButton = ({ 
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
    transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
    fontFamily: 'inherit',
    position: 'relative',
    overflow: 'hidden'
  };

  const variants = {
    primary: {
      backgroundColor: ORANGE_COLORS.primary,
      color: 'white',
      '&:hover': { backgroundColor: ORANGE_COLORS.primaryDark }
    },
    secondary: {
      backgroundColor: ORANGE_COLORS.surface,
      color: ORANGE_COLORS.text,
      border: `2px solid ${ORANGE_COLORS.border}`,
      '&:hover': { backgroundColor: ORANGE_COLORS.card }
    },
    dark: {
      backgroundColor: ORANGE_COLORS.background,
      color: ORANGE_COLORS.text,
      border: `1px solid ${ORANGE_COLORS.border}`,
      '&:hover': { backgroundColor: ORANGE_COLORS.surface }
    },
    ghost: {
      backgroundColor: 'transparent',
      color: ORANGE_COLORS.primary,
      '&:hover': { backgroundColor: `${ORANGE_COLORS.primary}20` }
    }
  };

  const sizes = {
    small: { padding: '8px 16px', fontSize: '14px' },
    medium: { padding: '12px 24px', fontSize: '16px' },
    large: { padding: '16px 32px', fontSize: '18px' },
    xlarge: { padding: '20px 40px', fontSize: '20px' }
  };

  return (
    <motion.button
      style={{
        ...baseStyles,
        ...variants[variant],
        ...sizes[size],
        width: fullWidth ? '100%' : 'auto',
        opacity: disabled ? 0.5 : 1
      }}
      whileHover={{ scale: disabled ? 1 : 1.02 }}
      whileTap={{ scale: disabled ? 1 : 0.98 }}
      onClick={onClick}
      disabled={disabled || loading}
      {...props}
    >
      {loading && (
        <motion.div
          style={{
            position: 'absolute',
            width: '20px',
            height: '20px',
            border: '2px solid rgba(255, 255, 255, 0.3)',
            borderTop: '2px solid white',
            borderRadius: '50%'
          }}
          animate={{ rotate: 360 }}
          transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
        />
      )}
      <span style={{ opacity: loading ? 0 : 1, display: 'flex', alignItems: 'center', gap: '8px' }}>
        {Icon && <Icon size={18} />}
        {children}
      </span>
    </motion.button>
  );
};

// ============================================
// 🟠 Orange Theme Card Component
// ============================================
export const OrangeCard = ({ 
  children, 
  padding = '24px', 
  shadow = true,
  hover = false,
  accent = false,
  ...props 
}) => {
  return (
    <motion.div
      style={{
        backgroundColor: accent ? ORANGE_COLORS.primary : ORANGE_COLORS.card,
        borderRadius: '16px',
        padding,
        boxShadow: shadow ? '0 10px 40px rgba(0, 0, 0, 0.4)' : 'none',
        border: accent ? 'none' : `1px solid ${ORANGE_COLORS.border}`,
        transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
        position: 'relative',
        overflow: 'hidden'
      }}
      whileHover={hover ? { 
        y: -4, 
        boxShadow: '0 20px 60px rgba(249, 115, 22, 0.3)',
        borderColor: ORANGE_COLORS.primary
      } : {}}
      {...props}
    >
      {accent && (
        <div style={{
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          height: '4px',
          background: `linear-gradient(90deg, ${ORANGE_COLORS.primary}, ${ORANGE_COLORS.primaryLight})`
        }} />
      )}
      {children}
    </motion.div>
  );
};

// ============================================
// 🟠 Customer Dashboard Component
// ============================================
export const CustomerDashboard = ({ user, onNewTracking, onOrderUs, notifications }) => {
  const [activeTracking, setActiveTracking] = useState({
    number: '#WE6K8J2M9P',
    status: 'In transit',
    progress: 65,
    estimatedTime: '25 mins',
    distance: '12 km'
  });

  const [recentShipments] = useState([
    {
      id: '1',
      number: '#WE6K8J2M9P',
      from: 'Riyadh, Saudi Arabia',
      to: 'Jeddah, Saudi Arabia',
      date: '2024-01-15',
      status: 'Delivered'
    },
    {
      id: '2',
      number: '#WE7L9N3K0Q',
      from: 'Dammam, Saudi Arabia',
      to: 'Riyadh, Saudi Arabia',
      date: '2024-01-14',
      status: 'In transit'
    },
    {
      id: '3',
      number: '#WE8M0P4L1R',
      from: 'Medina, Saudi Arabia',
      to: 'Mecca, Saudi Arabia',
      date: '2024-01-13',
      status: 'Delivered'
    }
  ]);

  return (
    <div style={{
      minHeight: '100vh',
      backgroundColor: ORANGE_COLORS.background,
      color: ORANGE_COLORS.text
    }}>
      {/* Header */}
      <motion.header
        style={{
          padding: '20px',
          backgroundColor: ORANGE_COLORS.surface,
          borderBottom: `1px solid ${ORANGE_COLORS.border}`
        }}
        initial={{ y: -50, opacity: 0 }}
        animate={{ y: 0, opacity: 1 }}
      >
        <div style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          maxWidth: '1200px',
          margin: '0 auto'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
            <div style={{
              width: '48px',
              height: '48px',
              borderRadius: '50%',
              backgroundColor: ORANGE_COLORS.primary,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: 'white',
              fontWeight: 'bold',
              fontSize: '18px'
            }}>
              {user?.name?.charAt(0) || 'U'}
            </div>
            <div>
              <h1 style={{ margin: 0, fontSize: '18px', fontWeight: '600' }}>
                Hi, {user?.name || 'Steven'}!
              </h1>
              <p style={{ 
                margin: 0, 
                fontSize: '14px', 
                color: ORANGE_COLORS.textSecondary 
              }}>
                Welcome back to your dashboard
              </p>
            </div>
          </div>
          
          <div style={{ position: 'relative' }}>
            <motion.button
              style={{
                width: '48px',
                height: '48px',
                borderRadius: '50%',
                backgroundColor: ORANGE_COLORS.card,
                border: `1px solid ${ORANGE_COLORS.border}`,
                color: ORANGE_COLORS.text,
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}
              whileHover={{ scale: 1.1 }}
              whileTap={{ scale: 0.9 }}
            >
              <Bell size={20} />
            </motion.button>
            {notifications > 0 && (
              <motion.div
                style={{
                  position: 'absolute',
                  top: '-4px',
                  right: '-4px',
                  width: '20px',
                  height: '20px',
                  borderRadius: '50%',
                  backgroundColor: ORANGE_COLORS.error,
                  color: 'white',
                  fontSize: '12px',
                  fontWeight: 'bold',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center'
                }}
                initial={{ scale: 0 }}
                animate={{ scale: 1 }}
              >
                {notifications}
              </motion.div>
            )}
          </div>
        </div>
      </motion.header>

      {/* Main Content */}
      <main style={{ 
        padding: '24px', 
        maxWidth: '1200px', 
        margin: '0 auto' 
      }}>
        {/* Quick Actions */}
        <motion.div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
            gap: '16px',
            marginBottom: '32px'
          }}
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
        >
          <OrangeCard 
            hover 
            onClick={onNewTracking}
            style={{ cursor: 'pointer' }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
              <div style={{
                width: '56px',
                height: '56px',
                borderRadius: '12px',
                backgroundColor: `${ORANGE_COLORS.primary}20`,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}>
                <MapPin size={28} color={ORANGE_COLORS.primary} />
              </div>
              <div>
                <h3 style={{ margin: 0, fontSize: '16px', fontWeight: '600' }}>
                  New Tracking
                </h3>
                <p style={{ 
                  margin: 0, 
                  fontSize: '14px', 
                  color: ORANGE_COLORS.textSecondary 
                }}>
                  Track your shipment
                </p>
              </div>
            </div>
          </OrangeCard>

          <OrangeCard 
            hover 
            onClick={onOrderUs}
            style={{ cursor: 'pointer' }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
              <div style={{
                width: '56px',
                height: '56px',
                borderRadius: '12px',
                backgroundColor: `${ORANGE_COLORS.primary}20`,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}>
                <Truck size={28} color={ORANGE_COLORS.primary} />
              </div>
              <div>
                <h3 style={{ margin: 0, fontSize: '16px', fontWeight: '600' }}>
                  Order Us
                </h3>
                <p style={{ 
                  margin: 0, 
                  fontSize: '14px', 
                  color: ORANGE_COLORS.textSecondary 
                }}>
                  Book new shipment
                </p>
              </div>
            </div>
          </OrangeCard>
        </motion.div>

        {/* Active Tracking Card */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
        >
          <OrangeCard accent style={{ marginBottom: '32px' }}>
            <div style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'flex-start',
              marginBottom: '20px'
            }}>
              <div>
                <h2 style={{ margin: 0, fontSize: '24px', fontWeight: 'bold', marginBottom: '8px' }}>
                  Active Tracking
                </h2>
                <div style={{
                  fontSize: '32px',
                  fontWeight: 'bold',
                  color: ORANGE_COLORS.primary,
                  marginBottom: '8px'
                }}>
                  {activeTracking.number}
                </div>
                <div style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px'
                }}>
                  <div style={{
                    width: '12px',
                    height: '12px',
                    borderRadius: '50%',
                    backgroundColor: ORANGE_COLORS.success,
                    boxShadow: `0 0 8px ${ORANGE_COLORS.success}`
                  }} />
                  <span style={{ color: ORANGE_COLORS.success, fontWeight: '600' }}>
                    {activeTracking.status}
                  </span>
                </div>
              </div>
              
              {/* Mini Map */}
              <div style={{
                width: '120px',
                height: '80px',
                backgroundColor: ORANGE_COLORS.surface,
                borderRadius: '12px',
                border: `1px solid ${ORANGE_COLORS.border}`,
                position: 'relative',
                overflow: 'hidden'
              }}>
                <div style={{
                  position: 'absolute',
                  top: '50%',
                  left: '20%',
                  transform: 'translate(-50%, -50%)',
                  width: '8px',
                  height: '8px',
                  borderRadius: '50%',
                  backgroundColor: ORANGE_COLORS.primary
                }} />
                <svg width="120" height="80" style={{ position: 'absolute' }}>
                  <motion.path
                    d="M 24 40 Q 60 20 96 40"
                    stroke={ORANGE_COLORS.primary}
                    strokeWidth="3"
                    fill="none"
                    strokeDasharray="5,5"
                    initial={{ pathLength: 0 }}
                    animate={{ pathLength: 1 }}
                    transition={{ duration: 2, repeat: Infinity }}
                  />
                </svg>
                <div style={{
                  position: 'absolute',
                  top: '50%',
                  right: '20%',
                  transform: 'translate(50%, -50%)',
                  width: '8px',
                  height: '8px',
                  borderRadius: '50%',
                  backgroundColor: ORANGE_COLORS.success
                }} />
              </div>
            </div>

            {/* Progress Info */}
            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))',
              gap: '16px'
            }}>
              <div>
                <p style={{ 
                  margin: 0, 
                  fontSize: '12px', 
                  color: ORANGE_COLORS.textSecondary,
                  marginBottom: '4px'
                }}>
                  Distance Remaining
                </p>
                <p style={{ 
                  margin: 0, 
                  fontSize: '18px', 
                  fontWeight: 'bold',
                  color: ORANGE_COLORS.primary
                }}>
                  {activeTracking.distance}
                </p>
              </div>
              <div>
                <p style={{ 
                  margin: 0, 
                  fontSize: '12px', 
                  color: ORANGE_COLORS.textSecondary,
                  marginBottom: '4px'
                }}>
                  Estimated Time
                </p>
                <p style={{ 
                  margin: 0, 
                  fontSize: '18px', 
                  fontWeight: 'bold',
                  color: ORANGE_COLORS.primary
                }}>
                  {activeTracking.estimatedTime}
                </p>
              </div>
            </div>

            {/* Progress Bar */}
            <div style={{ marginTop: '20px' }}>
              <div style={{
                width: '100%',
                height: '8px',
                backgroundColor: ORANGE_COLORS.surface,
                borderRadius: '4px',
                overflow: 'hidden'
              }}>
                <motion.div
                  style={{
                    height: '100%',
                    backgroundColor: ORANGE_COLORS.primary,
                    borderRadius: '4px'
                  }}
                  initial={{ width: 0 }}
                  animate={{ width: `${activeTracking.progress}%` }}
                  transition={{ duration: 1, delay: 0.5 }}
                />
              </div>
            </div>
          </OrangeCard>
        </motion.div>

        {/* Recent Shipments */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
        >
          <h2 style={{ 
            margin: '0 0 20px 0', 
            fontSize: '20px', 
            fontWeight: 'bold' 
          }}>
            Recent Shipments
          </h2>
          
          <div style={{
            display: 'flex',
            flexDirection: 'column',
            gap: '12px'
          }}>
            {recentShipments.map((shipment, index) => (
              <motion.div
                key={shipment.id}
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: 0.4 + index * 0.1 }}
              >
                <OrangeCard hover style={{ cursor: 'pointer' }}>
                  <div style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center'
                  }}>
                    <div style={{ flex: 1 }}>
                      <div style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '12px',
                        marginBottom: '8px'
                      }}>
                        <span style={{
                          fontSize: '16px',
                          fontWeight: '600',
                          color: ORANGE_COLORS.primary
                        }}>
                          {shipment.number}
                        </span>
                        <span style={{
                          padding: '4px 8px',
                          backgroundColor: ORANGE_COLORS.success,
                          color: 'white',
                          fontSize: '12px',
                          fontWeight: '600',
                          borderRadius: '6px'
                        }}>
                          {shipment.status}
                        </span>
                      </div>
                      
                      <div style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '8px',
                        fontSize: '14px',
                        color: ORANGE_COLORS.textSecondary
                      }}>
                        <MapPin size={14} />
                        <span>{shipment.from}</span>
                        <span>→</span>
                        <span>{shipment.to}</span>
                      </div>
                    </div>
                    
                    <div style={{
                      textAlign: 'right',
                      color: ORANGE_COLORS.textMuted,
                      fontSize: '14px'
                    }}>
                      {shipment.date}
                    </div>
                  </div>
                </OrangeCard>
              </motion.div>
            ))}
          </div>
        </motion.div>
      </main>
    </div>
  );
};

// ============================================
// 🟠 Booking Flow Component
// ============================================
export const BookingFlow = ({ onContinue }) => {
  const [pickupLocation, setPickupLocation] = useState('');
  const [dropLocation, setDropLocation] = useState('');
  const [selectedDate, setSelectedDate] = useState('');
  const [selectedTime, setSelectedTime] = useState('');
  const [selectedTruck, setSelectedTruck] = useState(null);
  const [helpers, setHelpers] = useState(0);

  const trucks = [
    { id: 'small', name: 'Small Truck', capacity: '1 ton', price: 24, image: '/truck-small.png' },
    { id: 'medium', name: 'Medium Truck', capacity: '3 tons', price: 35, image: '/truck-medium.png' },
    { id: 'large', name: 'Large Truck', capacity: '5 tons', price: 45, image: '/truck-large.png' },
    { id: 'xlarge', name: 'Extra Large', capacity: '10 tons', price: 65, image: '/truck-xlarge.png' }
  ];

  return (
    <div style={{
      minHeight: '100vh',
      backgroundColor: ORANGE_COLORS.background,
      color: ORANGE_COLORS.text,
      padding: '20px'
    }}>
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
      >
        {/* Location Card */}
        <OrangeCard style={{ marginBottom: '24px' }}>
          <h2 style={{ margin: '0 0 20px 0', fontSize: '20px', fontWeight: 'bold' }}>
            Where to drop?
          </h2>
          
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div style={{ position: 'relative' }}>
              <MapPin size={20} color={ORANGE_COLORS.primary} style={{ 
                position: 'absolute', 
                top: '12px', 
                left: '12px',
                zIndex: 1
              }} />
              <input
                type="text"
                placeholder="Pickup Location"
                value={pickupLocation}
                onChange={(e) => setPickupLocation(e.target.value)}
                style={{
                  width: '100%',
                  padding: '12px 16px 12px 48px',
                  backgroundColor: ORANGE_COLORS.surface,
                  border: `2px solid ${ORANGE_COLORS.border}`,
                  borderRadius: '12px',
                  color: ORANGE_COLORS.text,
                  fontSize: '16px',
                  outline: 'none'
                }}
              />
            </div>
            
            <div style={{ 
              position: 'relative', 
              display: 'flex', 
              alignItems: 'center',
              justifyContent: 'center'
            }}>
              <div style={{
                width: '2px',
                height: '20px',
                backgroundColor: ORANGE_COLORS.border,
                borderStyle: 'dashed'
              }} />
            </div>
            
            <div style={{ position: 'relative' }}>
              <Navigation size={20} color={ORANGE_COLORS.primary} style={{ 
                position: 'absolute', 
                top: '12px', 
                left: '12px',
                zIndex: 1
              }} />
              <input
                type="text"
                placeholder="Drop Location"
                value={dropLocation}
                onChange={(e) => setDropLocation(e.target.value)}
                style={{
                  width: '100%',
                  padding: '12px 16px 12px 48px',
                  backgroundColor: ORANGE_COLORS.surface,
                  border: `2px solid ${ORANGE_COLORS.border}`,
                  borderRadius: '12px',
                  color: ORANGE_COLORS.text,
                  fontSize: '16px',
                  outline: 'none'
                }}
              />
            </div>
          </div>
        </OrangeCard>

        {/* Date & Time */}
        <div style={{ 
          display: 'grid', 
          gridTemplateColumns: '1fr 1fr', 
          gap: '16px',
          marginBottom: '24px'
        }}>
          <OrangeCard>
            <label style={{ 
              display: 'block', 
              marginBottom: '8px', 
              fontSize: '14px',
              color: ORANGE_COLORS.textSecondary
            }}>
              Select Date
            </label>
            <input
              type="date"
              value={selectedDate}
              onChange={(e) => setSelectedDate(e.target.value)}
              style={{
                width: '100%',
                padding: '12px',
                backgroundColor: ORANGE_COLORS.surface,
                border: `1px solid ${ORANGE_COLORS.border}`,
                borderRadius: '8px',
                color: ORANGE_COLORS.text,
                fontSize: '16px'
              }}
            />
          </OrangeCard>
          
          <OrangeCard>
            <label style={{ 
              display: 'block', 
              marginBottom: '8px', 
              fontSize: '14px',
              color: ORANGE_COLORS.textSecondary
            }}>
              Select Time
            </label>
            <input
              type="time"
              value={selectedTime}
              onChange={(e) => setSelectedTime(e.target.value)}
              style={{
                width: '100%',
                padding: '12px',
                backgroundColor: ORANGE_COLORS.surface,
                border: `1px solid ${ORANGE_COLORS.border}`,
                borderRadius: '8px',
                color: ORANGE_COLORS.text,
                fontSize: '16px'
              }}
            />
          </OrangeCard>
        </div>

        {/* Truck Selection */}
        <OrangeCard style={{ marginBottom: '24px' }}>
          <h3 style={{ margin: '0 0 16px 0', fontSize: '18px', fontWeight: 'bold' }}>
            Select Truck
          </h3>
          
          <div style={{
            display: 'flex',
            gap: '12px',
            overflowX: 'auto',
            paddingBottom: '8px'
          }}>
            {trucks.map((truck) => (
              <motion.div
                key={truck.id}
                onClick={() => setSelectedTruck(truck)}
                style={{
                  minWidth: '140px',
                  padding: '16px',
                  backgroundColor: selectedTruck?.id === truck.id ? ORANGE_COLORS.primary : ORANGE_COLORS.surface,
                  border: `2px solid ${selectedTruck?.id === truck.id ? ORANGE_COLORS.primary : ORANGE_COLORS.border}`,
                  borderRadius: '12px',
                  cursor: 'pointer',
                  textAlign: 'center'
                }}
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
              >
                <div style={{
                  width: '60px',
                  height: '60px',
                  backgroundColor: ORANGE_COLORS.background,
                  borderRadius: '8px',
                  margin: '0 auto 12px',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  fontSize: '24px'
                }}>
                  🚛
                </div>
                <h4 style={{ 
                  margin: '0 0 4px 0', 
                  fontSize: '14px',
                  fontWeight: '600',
                  color: selectedTruck?.id === truck.id ? 'white' : ORANGE_COLORS.text
                }}>
                  {truck.name}
                </h4>
                <p style={{ 
                  margin: 0, 
                  fontSize: '12px',
                  color: selectedTruck?.id === truck.id ? 'rgba(255,255,255,0.8)' : ORANGE_COLORS.textSecondary
                }}>
                  {truck.capacity}
                </p>
                <p style={{ 
                  margin: '8px 0 0 0', 
                  fontSize: '16px',
                  fontWeight: 'bold',
                  color: ORANGE_COLORS.primary
                }}>
                  ${truck.price}/km
                </p>
              </motion.div>
            ))}
          </div>
        </OrangeCard>

        {/* Continue Button */}
        <OrangeButton
          onClick={onContinue}
          fullWidth
          size="xlarge"
          disabled={!pickupLocation || !dropLocation || !selectedTruck}
        >
          Continue
        </OrangeButton>
      </motion.div>
    </div>
  );
};

export default ORANGE_COLORS;
