/**
 * ============================================
 * 💳 Payment Component - Orange/Dark Theme
 * نظام إدهام - مكون الدفع الإلكتروني
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Users, DollarSign, Tag, Check, AlertCircle, 
  CreditCard, Smartphone, Wallet, ChevronDown,
  Plus, Minus, Info, Shield, Clock
} from 'lucide-react';
import ORANGE_COLORS from './OrangeThemeComponents';

// ============================================
// 💳 Payment Component
// ============================================
export const PaymentComponent = ({ 
  orderData,
  onPaymentComplete,
  onBack 
}) => {
  const [helpers, setHelpers] = useState(0);
  const [discountCode, setDiscountCode] = useState('');
  const [discountApplied, setDiscountApplied] = useState(null);
  const [paymentMethod, setPaymentMethod] = useState('card');
  const [showPaymentDetails, setShowPaymentDetails] = useState(false);
  const [processing, setProcessing] = useState(false);

  // Pricing calculation
  const basePrice = orderData?.truckPrice || 120;
  const helpersPrice = helpers * 50; // $50 per helper
  const tax = (basePrice + helpersPrice) * 0.15; // 15% tax
  const discount = discountApplied?.amount || 0;
  const subtotal = basePrice + helpersPrice;
  const totalBeforeTax = subtotal - discount;
  const totalAmount = totalBeforeTax + tax;

  const paymentMethods = [
    {
      id: 'card',
      name: 'Credit Card',
      icon: CreditCard,
      description: 'Pay with Visa, Mastercard, or Amex',
      color: ORANGE_COLORS.primary
    },
    {
      id: 'wallet',
      name: 'Digital Wallet',
      icon: Wallet,
      description: 'Apple Pay, Google Pay, or Samsung Pay',
      color: ORANGE_COLORS.success
    },
    {
      id: 'bank',
      name: 'Bank Transfer',
      icon: Smartphone,
      description: 'Direct bank transfer',
      color: ORANGE_COLORS.textSecondary
    }
  ];

  const applyDiscount = () => {
    // Mock discount codes
    const discounts = {
      'SAVE10': { amount: 10, type: 'percentage', description: '10% off' },
      'SAVE20': { amount: 20, type: 'percentage', description: '20% off' },
      'FLAT50': { amount: 50, type: 'fixed', description: '$50 off' },
      'NEWUSER': { amount: 15, type: 'percentage', description: '15% off for new users' }
    };

    const discount = discounts[discountCode.toUpperCase()];
    if (discount) {
      let discountAmount = 0;
      if (discount.type === 'percentage') {
        discountAmount = subtotal * (discount.amount / 100);
      } else {
        discountAmount = Math.min(discount.amount, subtotal);
      }
      
      setDiscountApplied({
        ...discount,
        amount: discountAmount,
        code: discountCode.toUpperCase()
      });
    } else {
      // Show error for invalid code
      setDiscountApplied({ error: 'Invalid discount code' });
      setTimeout(() => setDiscountApplied(null), 3000);
    }
  };

  const handlePayment = async () => {
    setProcessing(true);
    
    // Simulate payment processing
    setTimeout(() => {
      setProcessing(false);
      if (onPaymentComplete) {
        onPaymentComplete({
          method: paymentMethod,
          amount: totalAmount,
          helpers,
          discount: discountApplied,
          orderDetails: orderData
        });
      }
    }, 2000);
  };

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
        style={{ maxWidth: '600px', margin: '0 auto' }}
      >
        {/* Header */}
        <div style={{
          display: 'flex',
          alignItems: 'center',
          marginBottom: '32px'
        }}>
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
          
          <div>
            <h1 style={{ 
              margin: 0, 
              fontSize: '24px', 
              fontWeight: 'bold' 
            }}>
              Select Helpers & Payment
            </h1>
            <p style={{ 
              margin: '4px 0 0 0', 
              fontSize: '14px', 
              color: ORANGE_COLORS.textSecondary 
            }}>
              Complete your booking details
            </p>
          </div>
        </div>

        {/* Helpers Selection */}
        <motion.div
          style={{
            backgroundColor: ORANGE_COLORS.card,
            borderRadius: '16px',
            padding: '24px',
            marginBottom: '20px',
            border: `1px solid ${ORANGE_COLORS.border}`
          }}
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ delay: 0.1 }}
        >
          <div style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            marginBottom: '16px'
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
              <Users size={24} color={ORANGE_COLORS.primary} />
              <div>
                <h3 style={{ margin: 0, fontSize: '18px', fontWeight: '600' }}>
                  Select Helpers
                </h3>
                <p style={{ 
                  margin: '2px 0 0 0', 
                  fontSize: '14px', 
                  color: ORANGE_COLORS.textSecondary 
                }}>
                  Additional loading/unloading assistance
                </p>
              </div>
            </div>
            
            <div style={{
              display: 'flex',
              alignItems: 'center',
              gap: '16px'
            }}>
              <motion.button
                onClick={() => setHelpers(Math.max(0, helpers - 1))}
                style={{
                  width: '40px',
                  height: '40px',
                  borderRadius: '50%',
                  backgroundColor: ORANGE_COLORS.surface,
                  border: `1px solid ${ORANGE_COLORS.border}`,
                  color: ORANGE_COLORS.text,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  cursor: 'pointer'
                }}
                whileHover={{ scale: 1.1, backgroundColor: ORANGE_COLORS.primary }}
                whileTap={{ scale: 0.9 }}
              >
                <Minus size={18} />
              </motion.button>
              
              <div style={{
                minWidth: '60px',
                textAlign: 'center',
                fontSize: '24px',
                fontWeight: 'bold',
                color: ORANGE_COLORS.primary
              }}>
                {helpers}
              </div>
              
              <motion.button
                onClick={() => setHelpers(helpers + 1)}
                style={{
                  width: '40px',
                  height: '40px',
                  borderRadius: '50%',
                  backgroundColor: ORANGE_COLORS.surface,
                  border: `1px solid ${ORANGE_COLORS.border}`,
                  color: ORANGE_COLORS.text,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  cursor: 'pointer'
                }}
                whileHover={{ scale: 1.1, backgroundColor: ORANGE_COLORS.primary }}
                whileTap={{ scale: 0.9 }}
              >
                <Plus size={18} />
              </motion.button>
            </div>
          </div>
          
          <div style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center'
          }}>
            <span style={{ fontSize: '16px', color: ORANGE_COLORS.text }}>
              Helper Cost
            </span>
            <span style={{
              fontSize: '18px',
              fontWeight: 'bold',
              color: ORANGE_COLORS.primary
            }}>
              ${helpersPrice}
            </span>
          </div>
        </motion.div>

        {/* Discount Code */}
        <motion.div
          style={{
            backgroundColor: ORANGE_COLORS.card,
            borderRadius: '16px',
            padding: '24px',
            marginBottom: '20px',
            border: `1px solid ${ORANGE_COLORS.border}`
          }}
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ delay: 0.2 }}
        >
          <div style={{
            display: 'flex',
            gap: '12px',
            alignItems: 'flex-end'
          }}>
            <div style={{ flex: 1 }}>
              <label style={{
                display: 'block',
                marginBottom: '8px',
                fontSize: '14px',
                color: ORANGE_COLORS.textSecondary
              }}>
                Discount Code
              </label>
              <input
                type="text"
                placeholder="Enter discount code"
                value={discountCode}
                onChange={(e) => setDiscountCode(e.target.value)}
                style={{
                  width: '100%',
                  padding: '12px 16px',
                  backgroundColor: ORANGE_COLORS.surface,
                  border: `2px solid ${discountApplied?.error ? ORANGE_COLORS.error : ORANGE_COLORS.border}`,
                  borderRadius: '12px',
                  color: ORANGE_COLORS.text,
                  fontSize: '16px',
                  outline: 'none'
                }}
              />
            </div>
            
            <motion.button
              onClick={applyDiscount}
              disabled={!discountCode}
              style={{
                padding: '12px 24px',
                backgroundColor: ORANGE_COLORS.background,
                color: ORANGE_COLORS.text,
                border: `2px solid ${ORANGE_COLORS.border}`,
                borderRadius: '12px',
                fontSize: '16px',
                fontWeight: '600',
                cursor: discountCode ? 'pointer' : 'not-allowed',
                opacity: discountCode ? 1 : 0.5
              }}
              whileHover={discountCode ? { scale: 1.02 } : {}}
              whileTap={discountCode ? { scale: 0.98 } : {}}
            >
              Apply
            </motion.button>
          </div>
          
          <AnimatePresence>
            {discountApplied && !discountApplied.error && (
              <motion.div
                initial={{ opacity: 0, y: -10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
                style={{
                  marginTop: '12px',
                  padding: '12px',
                  backgroundColor: `${ORANGE_COLORS.success}20`,
                  border: `1px solid ${ORANGE_COLORS.success}`,
                  borderRadius: '8px',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px'
                }}
              >
                <Check size={16} color={ORANGE_COLORS.success} />
                <span style={{ 
                  fontSize: '14px', 
                  color: ORANGE_COLORS.success,
                  fontWeight: '500'
                }}>
                  {discountApplied.description} applied! You saved ${discountApplied.amount.toFixed(2)}
                </span>
              </motion.div>
            )}
            
            {discountApplied?.error && (
              <motion.div
                initial={{ opacity: 0, y: -10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
                style={{
                  marginTop: '12px',
                  padding: '12px',
                  backgroundColor: `${ORANGE_COLORS.error}20`,
                  border: `1px solid ${ORANGE_COLORS.error}`,
                  borderRadius: '8px',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px'
                }}
              >
                <AlertCircle size={16} color={ORANGE_COLORS.error} />
                <span style={{ 
                  fontSize: '14px', 
                  color: ORANGE_COLORS.error,
                  fontWeight: '500'
                }}>
                  {discountApplied.error}
                </span>
              </motion.div>
            )}
          </AnimatePresence>
        </motion.div>

        {/* Price Breakup */}
        <motion.div
          style={{
            backgroundColor: ORANGE_COLORS.card,
            borderRadius: '16px',
            padding: '24px',
            marginBottom: '20px',
            border: `1px solid ${ORANGE_COLORS.border}`
          }}
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ delay: 0.3 }}
        >
          <h3 style={{ 
            margin: '0 0 20px 0', 
            fontSize: '18px', 
            fontWeight: 'bold' 
          }}>
            Price Breakup
          </h3>
          
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <div style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center'
            }}>
              <span style={{ fontSize: '16px', color: ORANGE_COLORS.text }}>
                Truck Rental
              </span>
              <span style={{ fontSize: '16px', color: ORANGE_COLORS.text }}>
                ${basePrice.toFixed(2)}
              </span>
            </div>
            
            {helpers > 0 && (
              <div style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center'
              }}>
                <span style={{ fontSize: '16px', color: ORANGE_COLORS.text }}>
                  {helpers} Helper{helpers > 1 ? 's' : ''}
                </span>
                <span style={{ fontSize: '16px', color: ORANGE_COLORS.text }}>
                  ${helpersPrice.toFixed(2)}
                </span>
              </div>
            )}
            
            <div style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center'
            }}>
              <span style={{ fontSize: '16px', color: ORANGE_COLORS.text }}>
                Subtotal
              </span>
              <span style={{ fontSize: '16px', color: ORANGE_COLORS.text }}>
                ${subtotal.toFixed(2)}
              </span>
            </div>
            
            {discount > 0 && (
              <div style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center'
              }}>
                <span style={{ fontSize: '16px', color: ORANGE_COLORS.success }}>
                  Discount ({discountApplied?.code})
                </span>
              <span style={{ fontSize: '16px', color: ORANGE_COLORS.success }}>
                -${discount.toFixed(2)}
              </span>
              </div>
            )}
            
            <div style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center'
            }}>
              <span style={{ fontSize: '16px', color: ORANGE_COLORS.text }}>
                Tax (15%)
              </span>
              <span style={{ fontSize: '16px', color: ORANGE_COLORS.text }}>
                ${tax.toFixed(2)}
              </span>
            </div>
            
            <div style={{
              height: '1px',
              backgroundColor: ORANGE_COLORS.border,
              margin: '16px 0'
            }} />
            
            <div style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center'
            }}>
              <span style={{ 
                fontSize: '20px', 
                fontWeight: 'bold',
                color: ORANGE_COLORS.text 
              }}>
                Amount Payable
              </span>
              <span style={{ 
                fontSize: '24px', 
                fontWeight: 'bold',
                color: ORANGE_COLORS.primary 
              }}>
                ${totalAmount.toFixed(2)}
              </span>
            </div>
          </div>
        </motion.div>

        {/* Alert Message */}
        <motion.div
          style={{
            backgroundColor: `${ORANGE_COLORS.warning}20`,
            border: `1px solid ${ORANGE_COLORS.warning}`,
            borderRadius: '12px',
            padding: '16px',
            marginBottom: '24px',
            display: 'flex',
            alignItems: 'flex-start',
            gap: '12px'
          }}
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
        >
          <Info size={20} color={ORANGE_COLORS.warning} style={{ flexShrink: 0 }} />
          <div>
            <p style={{ 
              margin: 0, 
              fontSize: '14px', 
              color: ORANGE_COLORS.warning,
              lineHeight: '1.5'
            }}>
              Payment will be processed securely. Your booking will be confirmed immediately after successful payment.
            </p>
          </div>
        </motion.div>

        {/* Payment Methods */}
        <motion.div
          style={{
            backgroundColor: ORANGE_COLORS.card,
            borderRadius: '16px',
            padding: '24px',
            marginBottom: '32px',
            border: `1px solid ${ORANGE_COLORS.border}`
          }}
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ delay: 0.5 }}
        >
          <h3 style={{ 
            margin: '0 0 20px 0', 
            fontSize: '18px', 
            fontWeight: 'bold' 
          }}>
            Select Payment Method
          </h3>
          
          <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            {paymentMethods.map((method) => (
              <motion.div
                key={method.id}
                onClick={() => setPaymentMethod(method.id)}
                style={{
                  padding: '16px',
                  borderRadius: '12px',
                  border: `2px solid ${paymentMethod === method.id ? method.color : ORANGE_COLORS.border}`,
                  backgroundColor: paymentMethod === method.id ? `${method.color}20` : ORANGE_COLORS.surface,
                  cursor: 'pointer',
                  transition: 'all 0.3s ease'
                }}
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                  <div style={{
                    width: '48px',
                    height: '48px',
                    borderRadius: '12px',
                    backgroundColor: paymentMethod === method.id ? method.color : ORANGE_COLORS.background,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                  }}>
                    <method.icon 
                      size={24} 
                      color={paymentMethod === method.id ? 'white' : ORANGE_COLORS.text} 
                    />
                  </div>
                  
                  <div style={{ flex: 1 }}>
                    <h4 style={{ 
                      margin: '0 0 4px 0', 
                      fontSize: '16px', 
                      fontWeight: '600',
                      color: ORANGE_COLORS.text
                    }}>
                      {method.name}
                    </h4>
                    <p style={{ 
                      margin: 0, 
                      fontSize: '14px', 
                      color: ORANGE_COLORS.textSecondary 
                    }}>
                      {method.description}
                    </p>
                  </div>
                  
                  {paymentMethod === method.id && (
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
        </motion.div>

        {/* Pay Button */}
        <motion.button
          onClick={handlePayment}
          disabled={processing}
          style={{
            width: '100%',
            padding: '20px',
            backgroundColor: ORANGE_COLORS.background,
            color: ORANGE_COLORS.text,
            border: `2px solid ${ORANGE_COLORS.text}`,
            borderRadius: '16px',
            fontSize: '18px',
            fontWeight: 'bold',
            cursor: processing ? 'not-allowed' : 'pointer',
            opacity: processing ? 0.7 : 1,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            gap: '12px'
          }}
          whileHover={processing ? {} : { scale: 1.02 }}
          whileTap={processing ? {} : { scale: 0.98 }}
        >
          {processing ? (
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
              Processing Payment...
            </>
          ) : (
            <>
              <DollarSign size={20} />
              Pay ${totalAmount.toFixed(2)}
            </>
          )}
        </motion.button>

        {/* Security Badge */}
        <div style={{
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          gap: '8px',
          marginTop: '20px',
          color: ORANGE_COLORS.textSecondary,
          fontSize: '14px'
        }}>
          <Shield size={16} />
          <span>Secured by 256-bit SSL encryption</span>
        </div>
      </motion.div>
    </div>
  );
};

export default PaymentComponent;
