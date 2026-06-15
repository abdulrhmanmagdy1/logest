/**
 * ============================================
 * ⚠️ Alert Component - نظام إدهام
 * Edham Logistics - Alert Component
 * ============================================
 */

import React from 'react';
import { CheckCircle, XCircle, AlertCircle, Info, X } from 'lucide-react';

export default function Alert({ type = 'info', message, onClose, dismissible = false }) {
  const typeConfig = {
    success: {
      icon: <CheckCircle className="w-5 h-5" />,
      bgColor: 'bg-green-900/30',
      borderColor: 'border-green-500',
      textColor: 'text-green-400'
    },
    error: {
      icon: <XCircle className="w-5 h-5" />,
      bgColor: 'bg-red-900/30',
      borderColor: 'border-red-500',
      textColor: 'text-red-400'
    },
    warning: {
      icon: <AlertCircle className="w-5 h-5" />,
      bgColor: 'bg-yellow-900/30',
      borderColor: 'border-yellow-500',
      textColor: 'text-yellow-400'
    },
    info: {
      icon: <Info className="w-5 h-5" />,
      bgColor: 'bg-blue-900/30',
      borderColor: 'border-blue-500',
      textColor: 'text-blue-400'
    }
  };

  const config = typeConfig[type] || typeConfig.info;

  return (
    <div
      className={`
        ${config.bgColor} ${config.borderColor} border
        ${config.textColor} px-4 py-3 rounded-lg
        flex items-center gap-3
      `}
    >
      {config.icon}
      <span className="flex-1">{message}</span>
      {dismissible && (
        <button
          onClick={onClose}
          className={config.textColor}
        >
          <X className="w-4 h-4" />
        </button>
      )}
    </div>
  );
}
