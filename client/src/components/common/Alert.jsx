/**
 * ============================================
 * ⚠️ Alert Component - نظام إدهام
 * Professional alert/notification component
 * ============================================
 */

import React from 'react';
import { AlertCircle, CheckCircle, Info, X, XCircle } from 'lucide-react';

const Alert = ({
  children,
  title,
  variant = 'info',
  isDismissible = false,
  onDismiss,
  className = '',
}) => {
  const variants = {
    info: {
      container: 'bg-blue-50 border-blue-200',
      icon: 'text-blue-400',
      title: 'text-blue-800',
      text: 'text-blue-700',
      Icon: Info,
    },
    success: {
      container: 'bg-green-50 border-green-200',
      icon: 'text-green-400',
      title: 'text-green-800',
      text: 'text-green-700',
      Icon: CheckCircle,
    },
    warning: {
      container: 'bg-yellow-50 border-yellow-200',
      icon: 'text-yellow-400',
      title: 'text-yellow-800',
      text: 'text-yellow-700',
      Icon: AlertCircle,
    },
    error: {
      container: 'bg-red-50 border-red-200',
      icon: 'text-red-400',
      title: 'text-red-800',
      text: 'text-red-700',
      Icon: XCircle,
    },
  };

  const { container, icon, title: titleColor, text, Icon } = variants[variant];

  const classes = [
    'border rounded-lg p-4',
    container,
    className,
  ].join(' ');

  return (
    <div className={classes} role="alert">
      <div className="flex">
        <div className="flex-shrink-0">
          <Icon className={`h-5 w-5 ${icon}`} aria-hidden="true" />
        </div>
        <div className="ml-3 flex-1">
          {title && (
            <h3 className={`text-sm font-medium ${titleColor}`}>{title}</h3>
          )}
          <div className={`text-sm ${text} ${title ? 'mt-2' : ''}`}>
            {children}
          </div>
        </div>
        {isDismissible && (
          <div className="ml-auto pl-3">
            <button
              onClick={onDismiss}
              className={`inline-flex rounded-md p-1.5 ${text} hover:bg-opacity-20 hover:bg-gray-500 focus:outline-none`}
            >
              <span className="sr-only">Dismiss</span>
              <X className="h-5 w-5" aria-hidden="true" />
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default Alert;
