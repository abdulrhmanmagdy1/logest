/**
 * ============================================
 * 🃏 Card Component - نظام إدهام
 * Professional card component
 * ============================================
 */

import React from 'react';

const Card = ({
  children,
  title,
  subtitle,
  headerAction,
  footer,
  isHoverable = false,
  isClickable = false,
  onClick,
  className = '',
  padding = 'md',
  shadow = 'md',
}) => {
  const paddings = {
    none: '',
    sm: 'p-4',
    md: 'p-6',
    lg: 'p-8',
  };

  const shadows = {
    none: '',
    sm: 'shadow-sm',
    md: 'shadow-md',
    lg: 'shadow-lg',
    xl: 'shadow-xl',
  };

  const classes = [
    'bg-white rounded-xl border border-gray-200 overflow-hidden',
    shadows[shadow],
    isHoverable ? 'hover:shadow-lg transition-shadow duration-200' : '',
    isClickable ? 'cursor-pointer' : '',
    className,
  ].join(' ');

  return (
    <div className={classes} onClick={onClick}>
      {(title || subtitle || headerAction) && (
        <div className="flex items-start justify-between px-6 py-4 border-b border-gray-100">
          <div>
            {title && (
              <h3 className="text-lg font-semibold text-gray-900">{title}</h3>
            )}
            {subtitle && (
              <p className="mt-1 text-sm text-gray-500">{subtitle}</p>
            )}
          </div>
          {headerAction && (
            <div className="flex-shrink-0">{headerAction}</div>
          )}
        </div>
      )}
      
      <div className={paddings[padding]}>{children}</div>
      
      {footer && (
        <div className="px-6 py-4 bg-gray-50 border-t border-gray-100">
          {footer}
        </div>
      )}
    </div>
  );
};

export default Card;
