/**
 * ============================================
 * 🎨 Edham Logistics - Order Card Component
 * نظام إدهام - كرت الطلب
 * ============================================
 */

import React from 'react';
import { motion } from 'framer-motion';
import { MapPin, Calendar, Package, Truck } from 'lucide-react';

const OrderCard = ({ 
  order, 
  onClick, 
  variant = 'default' // 'default', 'compact', 'detailed'
}) => {
  const getStatusColor = (status) => {
    switch (status?.toLowerCase()) {
      case 'delivered':
        return { bg: 'rgba(34, 197, 94, 0.2)', color: '#22C55E' };
      case 'in_transit':
      case 'transit':
        return { bg: 'rgba(249, 115, 22, 0.2)', color: '#F97316' };
      case 'pending':
        return { bg: 'rgba(245, 158, 11, 0.2)', color: '#F59E0B' };
      case 'cancelled':
        return { bg: 'rgba(239, 68, 68, 0.2)', color: '#EF4444' };
      default:
        return { bg: 'rgba(163, 163, 163, 0.2)', color: '#A3A3A3' };
    }
  };

  const statusStyle = getStatusColor(order.status);

  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('ar-EG', {
      day: 'numeric',
      month: 'short',
      year: 'numeric'
    });
  };

  if (variant === 'compact') {
    return (
      <motion.div
        onClick={() => onClick?.(order)}
        className="order-card compact"
        whileHover={{ scale: 1.02, x: 5 }}
        whileTap={{ scale: 0.98 }}
      >
        <div className="order-header-compact">
          <div className="order-id">#{order.id}</div>
          <div 
            className="status-badge compact"
            style={{
              backgroundColor: statusStyle.bg,
              color: statusStyle.color
            }}
          >
            {order.status}
          </div>
        </div>
        <div className="route-compact">
          <div className="location">
            <MapPin className="w-3 h-3" />
            <span>{order.pickup}</span>
          </div>
          <div className="location">
            <Truck className="w-3 h-3" />
            <span>{order.delivery}</span>
          </div>
        </div>
      </motion.div>
    );
  }

  return (
    <motion.div
      onClick={() => onClick?.(order)}
      className="order-card"
      whileHover={{ scale: 1.02, y: -2 }}
      whileTap={{ scale: 0.98 }}
      layout
    >
      <div className="order-header">
        <div className="order-info">
          <h3 className="order-id">#{order.id}</h3>
          <div className="order-date">
            <Calendar className="w-4 h-4" />
            <span>تم الطلب: {formatDate(order.placedDate)}</span>
          </div>
        </div>
        <div 
          className="status-badge"
          style={{
            backgroundColor: statusStyle.bg,
            color: statusStyle.color
          }}
        >
          {order.status}
        </div>
      </div>

      <div className="order-route">
        <div className="route-point pickup">
          <div className="route-icon">
            <MapPin className="w-5 h-5" />
          </div>
          <div className="route-info">
            <div className="route-label">من</div>
            <div className="route-address">{order.pickup}</div>
          </div>
        </div>

        <div className="route-line">
          <div className="route-dots">
            <div className="dot"></div>
            <div className="dot"></div>
            <div className="dot"></div>
          </div>
        </div>

        <div className="route-point delivery">
          <div className="route-icon">
            <Truck className="w-5 h-5" />
          </div>
          <div className="route-info">
            <div className="route-label">إلى</div>
            <div className="route-address">{order.delivery}</div>
          </div>
        </div>
      </div>

      {variant === 'detailed' && (
        <div className="order-details">
          <div className="detail-item">
            <Package className="w-4 h-4" />
            <span>{order.packageType || 'طرد عادي'}</span>
          </div>
          <div className="detail-item">
            <Calendar className="w-4 h-4" />
            <span>تقدير التوصيل: {formatDate(order.estimatedDate)}</span>
          </div>
        </div>
      )}

      {order.progress && (
        <div className="order-progress">
          <div className="progress-bar">
            <div 
              className="progress-fill"
              style={{ width: `${order.progress}%` }}
            ></div>
          </div>
          <span className="progress-text">{order.progress}% مكتمل</span>
        </div>
      )}
    </motion.div>
  );
};

export default OrderCard;
