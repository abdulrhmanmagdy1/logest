/**
 * ============================================
 * 🏷️ Badge Component - نظام إدهام
 * Edham Logistics - Badge Component
 * ============================================
 */

import React from 'react';

export default function Badge({ children, variant = 'default', size = 'md', className = '' }) {
  const variantClasses = {
    default: 'bg-gray-600',
    primary: 'bg-blue-600',
    success: 'bg-green-600',
    warning: 'bg-yellow-600',
    danger: 'bg-red-600',
    info: 'bg-cyan-600',
    purple: 'bg-purple-600',
    gold: 'bg-yellow-500'
  };

  const sizeClasses = {
    sm: 'px-2 py-0.5 text-xs',
    md: 'px-3 py-1 text-sm',
    lg: 'px-4 py-2 text-base'
  };

  return (
    <span
      className={`${variantClasses[variant]} ${sizeClasses[size]} text-white rounded-full inline-flex items-center justify-center ${className}`}
    >
      {children}
    </span>
  );
}

export function StatusBadge({ status, type = 'shipment' }) {
  const statusConfig = {
    shipment: {
      pending: { color: 'warning', label: 'قيد الانتظار' },
      assigned: { color: 'primary', label: 'مُسندة' },
      in_transit: { color: 'purple', label: 'في الطريق' },
      delivered: { color: 'success', label: 'تم التسليم' },
      cancelled: { color: 'danger', label: 'ملغاة' },
      failed: { color: 'default', label: 'فشلت' }
    },
    invoice: {
      pending: { color: 'warning', label: 'قيد الانتظار' },
      sent: { color: 'primary', label: 'مرسلة' },
      paid: { color: 'success', label: 'مدفوعة' },
      partial: { color: 'purple', label: 'جزئي' },
      overdue: { color: 'danger', label: 'متأخرة' },
      cancelled: { color: 'default', label: 'ملغاة' }
    },
    truck: {
      active: { color: 'success', label: 'نشطة' },
      inactive: { color: 'default', label: 'غير نشطة' },
      maintenance: { color: 'warning', label: 'صيانة' },
      out_of_service: { color: 'danger', label: 'خارج الخدمة' },
      in_transit: { color: 'primary', label: 'في الطريق' }
    },
    user: {
      active: { color: 'success', label: 'نشط' },
      inactive: { color: 'default', label: 'غير نشط' },
      suspended: { color: 'danger', label: 'معلق' }
    }
  };

  const config = statusConfig[type]?.[status] || { color: 'default', label: status };

  return <Badge variant={config.color}>{config.label}</Badge>;
}
