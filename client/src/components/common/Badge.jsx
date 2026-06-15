/**
 * ============================================
 * 🏷️ Badge Component - نظام إدهام
 * Professional badge/status component
 * ============================================
 */

import React from 'react';

const Badge = ({
  children,
  variant = 'default',
  size = 'md',
  isPill = false,
  className = '',
  dot = false,
}) => {
  const variants = {
    default: 'bg-gray-100 text-gray-800',
    primary: 'bg-blue-100 text-blue-800',
    success: 'bg-green-100 text-green-800',
    warning: 'bg-yellow-100 text-yellow-800',
    danger: 'bg-red-100 text-red-800',
    info: 'bg-cyan-100 text-cyan-800',
    purple: 'bg-purple-100 text-purple-800',
  };

  const sizes = {
    sm: 'px-2 py-0.5 text-xs',
    md: 'px-2.5 py-0.5 text-sm',
    lg: 'px-3 py-1 text-base',
  };

  const classes = [
    'inline-flex items-center font-medium',
    variants[variant],
    sizes[size],
    isPill ? 'rounded-full' : 'rounded-md',
    className,
  ].join(' ');

  return (
    <span className={classes}>
      {dot && (
        <span className={`w-2 h-2 mr-1.5 rounded-full ${
          variant === 'default' ? 'bg-gray-400' :
          variant === 'primary' ? 'bg-blue-400' :
          variant === 'success' ? 'bg-green-400' :
          variant === 'warning' ? 'bg-yellow-400' :
          variant === 'danger' ? 'bg-red-400' :
          variant === 'info' ? 'bg-cyan-400' :
          'bg-purple-400'
        }`} />
      )}
      {children}
    </span>
  );
};

export default Badge;
