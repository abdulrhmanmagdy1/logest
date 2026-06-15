/**
 * ============================================
 * 📱 Side Screen Components - Advanced UI Elements
 * نظام إدهام - مكونات الشاشات الجانبية المتقدمة
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Lock, Mail, Smartphone, Check, AlertCircle, 
  Settings, ChevronRight, CreditCard, DollarSign,
  TrendingUp, TrendingDown, Wallet, Bell, Search,
  Package, Clock, User, Phone, MessageCircle,
  Home, Truck, Menu, X, Plus, Minus, Eye, EyeOff,
  Calendar, MapPin, Navigation, Star, Filter,
  Download, Printer, Edit, Camera, Shield
} from 'lucide-react';
import GREEN_COLORS from './GreenThemeComponents';
import ORANGE_COLORS from './OrangeThemeComponents';

// ============================================
// 🔐 Forgot Password Flow Components
// ============================================
export const ForgotPasswordScreen = ({ onBack, onContinue }) => {
  const [selectedMethod, setSelectedMethod] = useState('');
  const [phoneNumber, setPhoneNumber] = '';
  const [email, setEmail] = useState('');

  const recoveryMethods = [
    {
      id: 'sms',
      title: 'SMS',
      description: 'Send code to phone',
      icon: Smartphone,
      value: `+966 *** ${phoneNumber.slice(-4) || '1234'}`,
      color: GREEN_COLORS.primary
    },
    {
      id: 'email',
      title: 'Email',
      description: 'Send code to email',
      icon: Mail,
      value: `s***@gmail.com`,
      color: GREEN_COLORS.primary
    }
  ];

  return (
    <motion.div
      style={{
        minHeight: '100vh',
        backgroundColor: GREEN_COLORS.background,
        color: GREEN_COLORS.text,
        padding: '20px'
      }}
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
    >
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '32px' }}>
        <motion.button
          onClick={onBack}
          style={{
            width: '48px',
            height: '48px',
            borderRadius: '12px',
            backgroundColor: GREEN_COLORS.surface,
            border: `1px solid ${GREEN_COLORS.border}`,
            color: GREEN_COLORS.text,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: 'pointer',
            marginRight: '16px'
          }}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
        >
          ←
        </motion.button>
        
        <div>
          <h1 style={{ 
            margin: 0, 
            fontSize: '24px', 
            fontWeight: 'bold' 
          }}>
            Forgot Password
          </h1>
          <p style={{ 
            margin: '4px 0 0 0', 
            fontSize: '14px', 
            color: GREEN_COLORS.textSecondary 
          }}>
            Select recovery method
          </p>
        </div>
      </div>

      {/* Illustration */}
      <motion.div
        style={{
          width: '200px',
          height: '200px',
          margin: '0 auto 40px',
          backgroundColor: GREEN_COLORS.surface,
          borderRadius: '20px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center'
        }}
        initial={{ scale: 0.8, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        transition={{ delay: 0.2 }}
      >
        <Lock size={80} color={GREEN_COLORS.primary} />
      </motion.div>

      {/* Recovery Options */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
        {recoveryMethods.map((method, index) => (
          <motion.div
            key={method.id}
            onClick={() => setSelectedMethod(method.id)}
            style={{
              padding: '20px',
              backgroundColor: GREEN_COLORS.card,
              border: `2px solid ${selectedMethod === method.id ? method.color : GREEN_COLORS.border}`,
              borderRadius: '16px',
              cursor: 'pointer',
              transition: 'all 0.3s ease'
            }}
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ delay: 0.3 + index * 0.1 }}
          >
            <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
              <div style={{
                width: '56px',
                height: '56px',
                borderRadius: '12px',
                backgroundColor: selectedMethod === method.id ? method.color : GREEN_COLORS.surface,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}>
                <method.icon 
                  size={28} 
                  color={selectedMethod === method.id ? 'white' : GREEN_COLORS.text} 
                />
              </div>
              
              <div style={{ flex: 1 }}>
                <h3 style={{ 
                  margin: '0 0 4px 0', 
                  fontSize: '18px', 
                  fontWeight: '600',
                  color: selectedMethod === method.id ? method.color : GREEN_COLORS.text
                }}>
                  {method.title}
                </h3>
                <p style={{ 
                  margin: 0, 
                  fontSize: '14px', 
                  color: GREEN_COLORS.textSecondary 
                }}>
                  {method.value}
                </p>
              </div>
              
              {selectedMethod === method.id && (
                <motion.div
                  style={{
                    width: '24px',
                    height: '24px',
                    borderRadius: '50%',
                    backgroundColor: method.color,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                  }}
                  initial={{ scale: 0 }}
                  animate={{ scale: 1 }}
                >
                  <Check size={16} color="white" />
                </motion.div>
              )}
            </div>
          </motion.div>
        ))}
      </div>

      {/* Continue Button */}
      <motion.button
        onClick={() => onContinue(selectedMethod)}
        disabled={!selectedMethod}
        style={{
          width: '100%',
          padding: '16px',
          backgroundColor: selectedMethod ? GREEN_COLORS.primary : GREEN_COLORS.surface,
          color: selectedMethod ? 'white' : GREEN_COLORS.text,
          border: `1px solid ${selectedMethod ? GREEN_COLORS.primary : GREEN_COLORS.border}`,
          borderRadius: '12px',
          fontSize: '16px',
          fontWeight: '600',
          cursor: selectedMethod ? 'pointer' : 'not-allowed',
          marginTop: '32px',
          opacity: selectedMethod ? 1 : 0.5
        }}
        whileHover={selectedMethod ? { scale: 1.02 } : {}}
        whileTap={selectedMethod ? { scale: 0.98 } : {}}
      >
        Continue
      </motion.button>
    </motion.div>
  );
};

// ============================================
// 🔔 OTP Verification Component
// ============================================
export const OTPVerificationScreen = ({ onBack, onVerify, method = 'sms' }) => {
  const [otp, setOtp] = useState(['', '', '', '']);
  const [timeLeft, setTimeLeft] = useState(60);
  const [isVerifying, setIsVerifying] = useState(false);

  useEffect(() => {
    const timer = setInterval(() => {
      setTimeLeft((prev) => (prev > 0 ? prev - 1 : 0));
    }, 1000);

    return () => clearInterval(timer);
  }, []);

  const handleOtpChange = (index, value) => {
    if (value.length > 1) return;
    
    const newOtp = [...otp];
    newOtp[index] = value;
    setOtp(newOtp);

    // Auto-focus next input
    if (value && index < 3) {
      const nextInput = document.getElementById(`otp-${index + 1}`);
      if (nextInput) nextInput.focus();
    }
  };

  const handleVerify = () => {
    setIsVerifying(true);
    setTimeout(() => {
      onVerify(otp.join(''));
      setIsVerifying(false);
    }, 1000);
  };

  return (
    <motion.div
      style={{
        minHeight: '100vh',
        backgroundColor: GREEN_COLORS.background,
        color: GREEN_COLORS.text,
        padding: '20px'
      }}
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
    >
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '32px' }}>
        <motion.button
          onClick={onBack}
          style={{
            width: '48px',
            height: '48px',
            borderRadius: '12px',
            backgroundColor: GREEN_COLORS.surface,
            border: `1px solid ${GREEN_COLORS.border}`,
            color: GREEN_COLORS.text,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: 'pointer',
            marginRight: '16px'
          }}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
        >
          ←
        </motion.button>
        
        <div>
          <h1 style={{ 
            margin: 0, 
            fontSize: '24px', 
            fontWeight: 'bold' 
          }}>
            Verification
          </h1>
          <p style={{ 
            margin: '4px 0 0 0', 
            fontSize: '14px', 
            color: GREEN_COLORS.textSecondary 
          }}>
            Enter the code we sent you
          </p>
        </div>
      </div>

      {/* Info Message */}
      <motion.div
        style={{
          backgroundColor: `${GREEN_COLORS.primary}20`,
          border: `1px solid ${GREEN_COLORS.primary}`,
          borderRadius: '12px',
          padding: '16px',
          marginBottom: '32px',
          textAlign: 'center'
        }}
        initial={{ opacity: 0, y: -10 }}
        animate={{ opacity: 1, y: 0 }}
      >
        <p style={{ 
          margin: 0, 
          fontSize: '16px',
          color: GREEN_COLORS.primary
        }}>
          Code has been sent to {method === 'sms' ? '+966 *** 1234' : 's***@gmail.com'}
        </p>
      </motion.div>

      {/* OTP Inputs */}
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        gap: '16px',
        marginBottom: '32px'
      }}>
        {otp.map((digit, index) => (
          <motion.input
            key={index}
            id={`otp-${index}`}
            type="text"
            value={digit}
            onChange={(e) => handleOtpChange(index, e.target.value)}
            maxLength={1}
            style={{
              width: '60px',
              height: '60px',
              backgroundColor: GREEN_COLORS.surface,
              border: `2px solid ${digit ? GREEN_COLORS.primary : GREEN_COLORS.border}`,
              borderRadius: '12px',
              color: GREEN_COLORS.text,
              fontSize: '24px',
              fontWeight: 'bold',
              textAlign: 'center',
              outline: 'none',
              transition: 'all 0.3s ease'
            }}
            whileFocus={{ scale: 1.05 }}
          />
        ))}
      </div>

      {/* Timer */}
      <div style={{ textAlign: 'center', marginBottom: '32px' }}>
        <p style={{ 
          margin: 0, 
          fontSize: '14px', 
          color: GREEN_COLORS.textSecondary 
        }}>
          Resend code in {timeLeft}s
        </p>
        {timeLeft === 0 && (
          <motion.button
            style={{
              background: 'none',
              border: 'none',
              color: GREEN_COLORS.primary,
              fontSize: '14px',
              fontWeight: '600',
              cursor: 'pointer',
              marginTop: '8px'
            }}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
          >
            Resend Code
          </motion.button>
        )}
      </div>

      {/* Verify Button */}
      <motion.button
        onClick={handleVerify}
        disabled={otp.some(digit => !digit) || isVerifying}
        style={{
          width: '100%',
          padding: '16px',
          backgroundColor: otp.every(digit => digit) ? GREEN_COLORS.primary : GREEN_COLORS.surface,
          color: otp.every(digit => digit) ? 'white' : GREEN_COLORS.text,
          border: `1px solid ${otp.every(digit => digit) ? GREEN_COLORS.primary : GREEN_COLORS.border}`,
          borderRadius: '12px',
          fontSize: '16px',
          fontWeight: '600',
          cursor: otp.every(digit => digit) ? 'pointer' : 'not-allowed',
          opacity: otp.every(digit => digit) ? 1 : 0.5,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          gap: '12px'
        }}
        whileHover={otp.every(digit => digit) ? { scale: 1.02 } : {}}
        whileTap={otp.every(digit => digit) ? { scale: 0.98 } : {}}
      >
        {isVerifying ? (
          <>
            <motion.div
              style={{
                width: '20px',
                height: '20px',
                border: '2px solid rgba(255, 255, 255, 0.3)',
                borderTop: '2px solid white',
                borderRadius: '50%'
              }}
              animate={{ rotate: 360 }}
              transition={{ duration: 1, repeat: Infinity, ease: 'linear' }}
            />
            Verifying...
          </>
        ) : (
          'Verify Code'
        )}
      </motion.button>
    </motion.div>
  );
};

// ============================================
// 📱 Notifications Screen Component
// ============================================
export const NotificationsScreen = ({ onBack, onSettings }) => {
  const [notifications] = useState([
    {
      id: '1',
      type: 'payment',
      title: 'Payment Successful',
      description: 'Your payment of $120 has been processed',
      time: '2 hours ago',
      date: 'Today',
      icon: DollarSign,
      color: '#22c55e',
      read: false
    },
    {
      id: '2',
      type: 'shipping',
      title: 'Order Shipped',
      description: 'Your order #WE6K8J2M9P is on the way',
      time: '5 hours ago',
      date: 'Today',
      icon: Package,
      color: '#f97316',
      read: false
    },
    {
      id: '3',
      type: 'promotion',
      title: 'Special Offers',
      description: 'Get 20% off on your next shipment',
      time: 'Yesterday',
      date: 'Yesterday',
      icon: Star,
      color: '#f59e0b',
      read: true
    },
    {
      id: '4',
      type: 'system',
      title: 'System Update',
      description: 'New features available in the app',
      time: '2 days ago',
      date: 'December 20, 2024',
      icon: Settings,
      color: '#6b7280',
      read: true
    }
  ]);

  const groupedNotifications = notifications.reduce((acc, notification) => {
    if (!acc[notification.date]) {
      acc[notification.date] = [];
    }
    acc[notification.date].push(notification);
    return acc;
  }, {});

  return (
    <motion.div
      style={{
        minHeight: '100vh',
        backgroundColor: GREEN_COLORS.background,
        color: GREEN_COLORS.text,
        padding: '20px'
      }}
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
    >
      {/* Header */}
      <div style={{ 
        display: 'flex', 
        justifyContent: 'space-between', 
        alignItems: 'center',
        marginBottom: '24px'
      }}>
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <motion.button
            onClick={onBack}
            style={{
              width: '48px',
              height: '48px',
              borderRadius: '12px',
              backgroundColor: GREEN_COLORS.surface,
              border: `1px solid ${GREEN_COLORS.border}`,
              color: GREEN_COLORS.text,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              cursor: 'pointer',
              marginRight: '16px'
            }}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
          >
            ←
          </motion.button>
          
          <h1 style={{ 
            margin: 0, 
            fontSize: '24px', 
            fontWeight: 'bold' 
          }}>
            Notifications
          </h1>
        </div>
        
        <motion.button
          onClick={onSettings}
          style={{
            width: '48px',
            height: '48px',
            borderRadius: '12px',
            backgroundColor: GREEN_COLORS.surface,
            border: `1px solid ${GREEN_COLORS.border}`,
            color: GREEN_COLORS.text,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: 'pointer'
          }}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
        >
          <Settings size={20} />
        </motion.button>
      </div>

      {/* Notifications List */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
        {Object.entries(groupedNotifications).map(([date, dateNotifications]) => (
          <div key={date}>
            {/* Date Header */}
            <h3 style={{ 
              margin: '0 0 16px 0', 
              fontSize: '18px', 
              fontWeight: 'bold',
              color: GREEN_COLORS.textSecondary
            }}>
              {date}
            </h3>
            
            {/* Notification Items */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              {dateNotifications.map((notification, index) => (
                <motion.div
                  key={notification.id}
                  style={{
                    padding: '16px',
                    backgroundColor: notification.read ? GREEN_COLORS.surface : GREEN_COLORS.card,
                    border: `1px solid ${GREEN_COLORS.border}`,
                    borderRadius: '12px',
                    cursor: 'pointer',
                    transition: 'all 0.3s ease'
                  }}
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: index * 0.1 }}
                >
                  <div style={{ display: 'flex', alignItems: 'flex-start', gap: '16px' }}>
                    <div style={{
                      width: '48px',
                      height: '48px',
                      borderRadius: '12px',
                      backgroundColor: `${notification.color}20`,
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'center',
                      flexShrink: 0
                    }}>
                      <notification.icon 
                        size={24} 
                        color={notification.color} 
                      />
                    </div>
                    
                    <div style={{ flex: 1 }}>
                      <div style={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'flex-start',
                        marginBottom: '4px'
                      }}>
                        <h4 style={{ 
                          margin: 0, 
                          fontSize: '16px', 
                          fontWeight: '600',
                          color: GREEN_COLORS.text
                        }}>
                          {notification.title}
                        </h4>
                        {!notification.read && (
                          <div style={{
                            width: '8px',
                            height: '8px',
                            borderRadius: '50%',
                            backgroundColor: GREEN_COLORS.primary,
                            flexShrink: 0
                          }} />
                        )}
                      </div>
                      
                      <p style={{ 
                        margin: '0 0 8px 0', 
                        fontSize: '14px', 
                        color: GREEN_COLORS.textSecondary,
                        lineHeight: '1.4'
                      }}>
                        {notification.description}
                      </p>
                      
                      <p style={{ 
                        margin: 0, 
                        fontSize: '12px', 
                        color: GREEN_COLORS.textMuted 
                      }}>
                        {notification.time}
                      </p>
                    </div>
                  </div>
                </motion.div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </motion.div>
  );
};

// ============================================
// 💳 Wallet & Transactions Component
// ============================================
export const WalletScreen = ({ onBack, onTopUp, onMakeOrder }) => {
  const [balance] = useState(9729);
  const [transactions] = useState([
    {
      id: '1',
      type: 'payment',
      title: 'Order Payment',
      description: 'Shipment #WE6K8J2M9P',
      amount: -120,
      date: '2024-01-15',
      time: '2:30 PM'
    },
    {
      id: '2',
      type: 'refund',
      title: 'Refund',
      description: 'Cancelled order',
      amount: 50,
      date: '2024-01-14',
      time: '10:15 AM'
    },
    {
      id: '3',
      type: 'topup',
      title: 'Wallet Top Up',
      description: 'Added funds',
      amount: 500,
      date: '2024-01-13',
      time: '3:45 PM'
    },
    {
      id: '4',
      type: 'payment',
      title: 'Order Payment',
      description: 'Shipment #WE7L9N3K0Q',
      amount: -85,
      date: '2024-01-12',
      time: '11:20 AM'
    }
  ]);

  return (
    <motion.div
      style={{
        minHeight: '100vh',
        backgroundColor: GREEN_COLORS.background,
        color: GREEN_COLORS.text,
        padding: '20px'
      }}
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
    >
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '32px' }}>
        <motion.button
          onClick={onBack}
          style={{
            width: '48px',
            height: '48px',
            borderRadius: '12px',
            backgroundColor: GREEN_COLORS.surface,
            border: `1px solid ${GREEN_COLORS.border}`,
            color: GREEN_COLORS.text,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: 'pointer',
            marginRight: '16px'
          }}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
        >
          ←
        </motion.button>
        
        <h1 style={{ 
          margin: 0, 
          fontSize: '24px', 
          fontWeight: 'bold' 
        }}>
          Wallet
        </h1>
      </div>

      {/* User Card */}
      <motion.div
        style={{
          background: `linear-gradient(135deg, ${GREEN_COLORS.primary}, ${GREEN_COLORS.primaryDark})`,
          borderRadius: '20px',
          padding: '24px',
          marginBottom: '24px',
          position: 'relative',
          overflow: 'hidden'
        }}
        initial={{ scale: 0.9, opacity: 0 }}
        animate={{ scale: 1, opacity: 1 }}
        transition={{ delay: 0.2 }}
      >
        {/* Card Pattern */}
        <div style={{
          position: 'absolute',
          top: '-50%',
          right: '-50%',
          width: '200%',
          height: '200%',
          background: 'radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%)',
          transform: 'rotate(45deg)'
        }} />

        {/* Card Content */}
        <div style={{ position: 'relative', zIndex: 1 }}>
          <div style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'flex-start',
            marginBottom: '24px'
          }}>
            <div>
              <p style={{ 
                margin: '0 0 8px 0', 
                fontSize: '14px',
                color: 'rgba(255, 255, 255, 0.8)'
              }}>
                Available Balance
              </p>
              <h2 style={{ 
                margin: 0, 
                fontSize: '32px', 
                fontWeight: 'bold',
                color: 'white'
              }}>
                ${balance.toLocaleString()}
              </h2>
            </div>
            
            <div style={{
              width: '48px',
              height: '48px',
              borderRadius: '8px',
              backgroundColor: 'rgba(255, 255, 255, 0.2)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center'
            }}>
              <Wallet size={24} color="white" />
            </div>
          </div>
          
          {/* Card Number */}
          <div style={{
            display: 'flex',
            justifyContent: 'space-between',
            marginBottom: '16px'
          }}>
            <div style={{
              display: 'flex',
              gap: '8px'
            }}>
              {[1, 2, 3, 4].map((i) => (
                <div key={i} style={{
                  width: '40px',
                  height: '6px',
                  backgroundColor: 'rgba(255, 255, 255, 0.6)',
                  borderRadius: '2px'
                }} />
              ))}
            </div>
          </div>
          
          {/* Card Footer */}
          <div style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'flex-end'
          }}>
            <div>
              <p style={{ 
                margin: 0, 
                fontSize: '12px',
                color: 'rgba(255, 255, 255, 0.6)'
              }}>
                Card Holder
              </p>
              <p style={{ 
                margin: 0, 
                fontSize: '14px',
                color: 'white',
                fontWeight: '600'
              }}>
                STEVEN WILSON
              </p>
            </div>
            
            <div style={{
              width: '40px',
              height: '24px',
              backgroundColor: 'rgba(255, 255, 255, 0.2)',
              borderRadius: '4px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center'
            }}>
              <span style={{ 
                fontSize: '10px',
                color: 'white',
                fontWeight: 'bold'
              }}>
                VISA
              </span>
            </div>
          </div>
        </div>
      </motion.div>

      {/* Quick Actions */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(3, 1fr)',
        gap: '12px',
        marginBottom: '32px'
      }}>
        {[
          { icon: Package, label: 'Make Order', action: onMakeOrder },
          { icon: TrendingUp, label: 'Check Rates', action: () => {} },
          { icon: Plus, label: 'Top Up', action: onTopUp }
        ].map((action, index) => (
          <motion.button
            key={index}
            onClick={action.action}
            style={{
              padding: '16px',
              backgroundColor: GREEN_COLORS.card,
              border: `1px solid ${GREEN_COLORS.border}`,
              borderRadius: '12px',
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              gap: '8px',
              cursor: 'pointer'
            }}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.3 + index * 0.1 }}
          >
            <action.icon size={24} color={GREEN_COLORS.primary} />
            <span style={{ 
              fontSize: '12px',
              color: GREEN_COLORS.text,
              textAlign: 'center'
            }}>
              {action.label}
            </span>
          </motion.button>
        ))}
      </div>

      {/* Transaction History */}
      <div>
        <h3 style={{ 
          margin: '0 0 16px 0', 
          fontSize: '18px', 
          fontWeight: 'bold' 
        }}>
          Transaction History
        </h3>
        
        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
          {transactions.map((transaction, index) => (
            <motion.div
              key={transaction.id}
              style={{
                padding: '16px',
                backgroundColor: GREEN_COLORS.card,
                border: `1px solid ${GREEN_COLORS.border}`,
                borderRadius: '12px'
              }}
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.4 + index * 0.1 }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                  <div style={{
                    width: '40px',
                    height: '40px',
                    borderRadius: '10px',
                    backgroundColor: transaction.amount > 0 ? `${GREEN_COLORS.success}20` : `${GREEN_COLORS.error}20`,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                  }}>
                    {transaction.amount > 0 ? (
                      <TrendingUp size={20} color={GREEN_COLORS.success} />
                    ) : (
                      <TrendingDown size={20} color={GREEN_COLORS.error} />
                    )}
                  </div>
                  
                  <div>
                    <h4 style={{ 
                      margin: '0 0 4px 0', 
                      fontSize: '14px', 
                      fontWeight: '600',
                      color: GREEN_COLORS.text
                    }}>
                      {transaction.title}
                    </h4>
                    <p style={{ 
                      margin: 0, 
                      fontSize: '12px', 
                      color: GREEN_COLORS.textSecondary 
                    }}>
                      {transaction.description}
                    </p>
                  </div>
                </div>
                
                <div style={{ textAlign: 'right' }}>
                  <p style={{ 
                    margin: '0 0 4px 0', 
                    fontSize: '16px', 
                    fontWeight: 'bold',
                    color: transaction.amount > 0 ? GREEN_COLORS.success : GREEN_COLORS.error
                  }}>
                    {transaction.amount > 0 ? '+' : ''}${Math.abs(transaction.amount)}
                  </p>
                  <p style={{ 
                    margin: 0, 
                    fontSize: '10px', 
                    color: GREEN_COLORS.textMuted 
                  }}>
                    {transaction.date}
                  </p>
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      </div>
    </motion.div>
  );
};

// ============================================
// 📦 Bottom Navigation Component
// ============================================
export const BottomNavigation = ({ activeTab, onTabChange, onFabPress }) => {
  const tabs = [
    { id: 'home', icon: Home, label: 'Home' },
    { id: 'orders', icon: Truck, label: 'Orders' },
    { id: 'chats', icon: MessageCircle, label: 'Chats' },
    { id: 'settings', icon: Settings, label: 'Settings' }
  ];

  return (
    <motion.div
      style={{
        position: 'fixed',
        bottom: 0,
        left: 0,
        right: 0,
        backgroundColor: ORANGE_COLORS.background,
        borderTop: `1px solid ${ORANGE_COLORS.border}`,
        borderTopLeftRadius: '20px',
        borderTopRightRadius: '20px',
        padding: '12px 20px 20px',
        zIndex: 1000
      }}
      initial={{ y: 100 }}
      animate={{ y: 0 }}
      transition={{ type: 'spring', damping: 20 }}
    >
      <div style={{
        display: 'flex',
        justifyContent: 'space-around',
        alignItems: 'center',
        position: 'relative'
      }}>
        {tabs.map((tab) => (
          <motion.button
            key={tab.id}
            onClick={() => onTabChange(tab.id)}
            style={{
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              gap: '4px',
              padding: '8px 16px',
              backgroundColor: 'transparent',
              border: 'none',
              cursor: 'pointer',
              transition: 'all 0.3s ease'
            }}
            whileHover={{ scale: 1.1 }}
            whileTap={{ scale: 0.9 }}
          >
            <tab.icon 
              size={20} 
              color={activeTab === tab.id ? ORANGE_COLORS.primary : ORANGE_COLORS.textMuted} 
            />
            <span style={{ 
              fontSize: '10px',
              color: activeTab === tab.id ? ORANGE_COLORS.primary : ORANGE_COLORS.textMuted,
              fontWeight: activeTab === tab.id ? '600' : '400'
            }}>
              {tab.label}
            </span>
          </motion.button>
        ))}
        
        {/* Floating Action Button */}
        <motion.button
          onClick={onFabPress}
          style={{
            position: 'absolute',
            top: '-20px',
            left: '50%',
            transform: 'translateX(-50%)',
            width: '56px',
            height: '56px',
            borderRadius: '50%',
            backgroundColor: ORANGE_COLORS.primary,
            border: 'none',
            color: 'white',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: 'pointer',
            boxShadow: `0 4px 20px ${ORANGE_COLORS.primary}40`
          }}
          whileHover={{ scale: 1.1 }}
          whileTap={{ scale: 0.9 }}
        >
          <Plus size={24} />
        </motion.button>
      </div>
    </motion.div>
  );
};

// ============================================
// 📊 Order Details Component
// ============================================
export const OrderDetailsScreen = ({ onBack, orderData }) => {
  const [activeStep, setActiveStep] = useState(2); // In Transit

  const steps = [
    { id: 1, name: 'Packed', icon: Package, completed: true },
    { id: 2, name: 'Shipped', icon: Truck, completed: true },
    { id: 3, name: 'In Transit', icon: Navigation, completed: false, active: true },
    { id: 4, name: 'Delivered', icon: CheckCircle, completed: false }
  ];

  return (
    <motion.div
      style={{
        minHeight: '100vh',
        backgroundColor: ORANGE_COLORS.background,
        color: ORANGE_COLORS.text,
        padding: '20px'
      }}
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
    >
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '24px' }}>
        <motion.button
          onClick={onBack}
          style={{
            width: '48px',
            height: '48px',
            borderRadius: '12px',
            backgroundColor: ORANGE_COLORS.surface,
            border: `1px solid ${ORANGE_COLORS.border}`,
            color: ORANGE_COLORS.text,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            cursor: 'pointer',
            marginRight: '16px'
          }}
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
        >
          ←
        </motion.button>
        
        <h1 style={{ 
          margin: 0, 
          fontSize: '24px', 
          fontWeight: 'bold' 
        }}>
          Order Details
        </h1>
      </div>

      {/* Search Bar */}
      <div style={{
        position: 'relative',
        marginBottom: '24px'
      }}>
        <Search 
          size={20} 
          color={ORANGE_COLORS.textMuted} 
          style={{ 
            position: 'absolute', 
            left: '16px', 
            top: '50%', 
            transform: 'translateY(-50%)' 
          }} 
        />
        <input
          type="text"
          placeholder="Search Shipping"
          style={{
            width: '100%',
            padding: '12px 16px 12px 48px',
            backgroundColor: ORANGE_COLORS.surface,
            border: `1px solid ${ORANGE_COLORS.border}`,
            borderRadius: '20px',
            color: ORANGE_COLORS.text,
            fontSize: '16px',
            outline: 'none'
          }}
        />
      </div>

      {/* Order Card */}
      <motion.div
        style={{
          backgroundColor: ORANGE_COLORS.card,
          borderRadius: '16px',
          padding: '20px',
          marginBottom: '24px',
          border: `1px solid ${ORANGE_COLORS.border}`
        }}
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.2 }}
      >
        <div style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'flex-start',
          marginBottom: '16px'
        }}>
          <div>
            <h2 style={{ 
              margin: '0 0 8px 0', 
              fontSize: '20px', 
              fontWeight: 'bold',
              color: ORANGE_COLORS.text
            }}>
              ORD-123456789
            </h2>
            <div style={{
              display: 'inline-block',
              padding: '4px 12px',
              backgroundColor: `${ORANGE_COLORS.primary}20`,
              border: `1px solid ${ORANGE_COLORS.primary}`,
              borderRadius: '12px'
            }}>
              <span style={{
                fontSize: '12px',
                color: ORANGE_COLORS.primary,
                fontWeight: '600'
              }}>
                In Transit
              </span>
            </div>
          </div>
        </div>

        {/* Locations */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: '1fr 1fr',
          gap: '16px',
          marginBottom: '16px'
        }}>
          <div>
            <p style={{ 
              margin: '0 0 4px 0', 
              fontSize: '12px', 
              color: ORANGE_COLORS.textSecondary 
            }}>
              From
            </p>
            <p style={{ 
              margin: 0, 
              fontSize: '14px', 
              fontWeight: '600',
              color: ORANGE_COLORS.text
            }}>
              Los Angeles
            </p>
            <p style={{ 
              margin: '4px 0 0 0', 
              fontSize: '12px', 
              color: ORANGE_COLORS.textMuted 
            }}>
              Warehouse A, 123 Industrial Park
            </p>
          </div>
          
          <div>
            <p style={{ 
              margin: '0 0 4px 0', 
              fontSize: '12px', 
              color: ORANGE_COLORS.textSecondary 
            }}>
              To
            </p>
            <p style={{ 
              margin: 0, 
              fontSize: '14px', 
              fontWeight: '600',
              color: ORANGE_COLORS.text
            }}>
              New York
            </p>
            <p style={{ 
              margin: '4px 0 0 0', 
              fontSize: '12px', 
              color: ORANGE_COLORS.textMuted 
            }}>
              Central Logistics Hub
            </p>
          </div>
        </div>

        {/* Dates */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: '1fr 1fr',
          gap: '16px'
        }}>
          <div>
            <p style={{ 
              margin: '0 0 4px 0', 
              fontSize: '12px', 
              color: ORANGE_COLORS.textSecondary 
            }}>
              Placed by
            </p>
            <p style={{ 
              margin: 0, 
              fontSize: '14px', 
              fontWeight: '600',
              color: ORANGE_COLORS.text
            }}>
              12 Jan
            </p>
          </div>
          
          <div>
            <p style={{ 
              margin: '0 0 4px 0', 
              fontSize: '12px', 
              color: ORANGE_COLORS.textSecondary 
            }}>
              Estimated Date
            </p>
            <p style={{ 
              margin: 0, 
              fontSize: '14px', 
              fontWeight: '600',
              color: ORANGE_COLORS.text
            }}>
              26 Jan
            </p>
          </div>
        </div>
      </motion.div>

      {/* Progress Stepper */}
      <motion.div
        style={{
          backgroundColor: ORANGE_COLORS.card,
          borderRadius: '16px',
          padding: '20px',
          marginBottom: '24px',
          border: `1px solid ${ORANGE_COLORS.border}`
        }}
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.3 }}
      >
        <h3 style={{ 
          margin: '0 0 20px 0', 
          fontSize: '16px', 
          fontWeight: 'bold' 
        }}>
          Order Progress
        </h3>
        
        <div style={{ position: 'relative' }}>
          {/* Progress Line */}
          <div style={{
            position: 'absolute',
            top: '20px',
            left: '20px',
            right: '20px',
            height: '2px',
            backgroundColor: ORANGE_COLORS.border,
            zIndex: 1
          }}>
            <motion.div
              style={{
                height: '100%',
                backgroundColor: ORANGE_COLORS.primary,
                borderRadius: '1px'
              }}
              initial={{ width: '0%' }}
              animate={{ width: '50%' }}
              transition={{ duration: 1, delay: 0.5 }}
            />
          </div>
          
          {/* Steps */}
          <div style={{
            display: 'flex',
            justifyContent: 'space-between',
            position: 'relative',
            zIndex: 2
          }}>
            {steps.map((step, index) => (
              <div key={step.id} style={{ textAlign: 'center' }}>
                <motion.div
                  style={{
                    width: '40px',
                    height: '40px',
                    borderRadius: '50%',
                    backgroundColor: step.completed ? ORANGE_COLORS.primary : 
                                     step.active ? `${ORANGE_COLORS.primary}20` : ORANGE_COLORS.surface,
                    border: step.active ? `2px solid ${ORANGE_COLORS.primary}` : 
                           step.completed ? `2px solid ${ORANGE_COLORS.primary}` : 
                           `1px solid ${ORANGE_COLORS.border}`,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    margin: '0 auto 8px'
                  }}
                  whileHover={{ scale: 1.1 }}
                >
                  {step.active && (
                    <motion.div
                      style={{
                        width: '12px',
                        height: '12px',
                        borderRadius: '50%',
                        backgroundColor: ORANGE_COLORS.primary,
                        boxShadow: `0 0 10px ${ORANGE_COLORS.primary}`
                      }}
                      animate={{ scale: [1, 1.2, 1] }}
                      transition={{ duration: 2, repeat: Infinity }}
                    />
                  )}
                  {step.completed && !step.active && (
                    <Check size={20} color="white" />
                  )}
                  {!step.completed && !step.active && (
                    <step.icon size={16} color={ORANGE_COLORS.textMuted} />
                  )}
                </motion.div>
                
                <p style={{ 
                  margin: 0, 
                  fontSize: '10px', 
                  color: step.active ? ORANGE_COLORS.primary : 
                         step.completed ? ORANGE_COLORS.text : ORANGE_COLORS.textMuted,
                  fontWeight: step.active ? '600' : '400'
                }}>
                  {step.name}
                </p>
              </div>
            ))}
          </div>
        </div>
      </motion.div>

      {/* Delivery Partner */}
      <motion.div
        style={{
          backgroundColor: ORANGE_COLORS.card,
          borderRadius: '16px',
          padding: '20px',
          border: `1px solid ${ORANGE_COLORS.border}`
        }}
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.4 }}
      >
        <h3 style={{ 
          margin: '0 0 16px 0', 
          fontSize: '16px', 
          fontWeight: 'bold' 
        }}>
          Delivery Partner
        </h3>
        
        <div style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
            <div style={{
              width: '56px',
              height: '56px',
              borderRadius: '50%',
              backgroundColor: ORANGE_COLORS.surface,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontSize: '20px',
              fontWeight: 'bold',
              color: ORANGE_COLORS.primary
            }}>
              MJ
            </div>
            
            <div>
              <h4 style={{ 
                margin: '0 0 4px 0', 
                fontSize: '16px', 
                fontWeight: '600',
                color: ORANGE_COLORS.text
              }}>
                Michael Johnson
              </h4>
              <p style={{ 
                margin: 0, 
                fontSize: '14px', 
                color: ORANGE_COLORS.textSecondary 
              }}>
                Vehicle: A54 3571
              </p>
            </div>
          </div>
          
          <div style={{ display: 'flex', gap: '12px' }}>
            <motion.button
              style={{
                width: '48px',
                height: '48px',
                borderRadius: '50%',
                backgroundColor: ORANGE_COLORS.surface,
                border: `1px solid ${ORANGE_COLORS.border}`,
                color: ORANGE_COLORS.text,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                cursor: 'pointer'
              }}
              whileHover={{ scale: 1.1 }}
              whileTap={{ scale: 0.9 }}
            >
              <MessageCircle size={20} />
            </motion.button>
            
            <motion.button
              style={{
                width: '48px',
                height: '48px',
                borderRadius: '50%',
                backgroundColor: ORANGE_COLORS.primary,
                border: 'none',
                color: 'white',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                cursor: 'pointer'
              }}
              whileHover={{ scale: 1.1 }}
              whileTap={{ scale: 0.9 }}
            >
              <Phone size={20} />
            </motion.button>
          </div>
        </div>
      </motion.div>
    </motion.div>
  );
};

export default SideScreenComponents;
