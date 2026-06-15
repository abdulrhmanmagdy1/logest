/**
 * ============================================
 * 🔔 Toast Component - نظام إدهام
 * Edham Logistics - Toast Notification
 * ============================================
 */

import React, { useEffect, useState } from 'react';
import { CheckCircle, XCircle, AlertCircle, Info, X } from 'lucide-react';

export default function Toast({ message, type = 'info', duration = 3000, onClose }) {
  const [visible, setVisible] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => {
      setVisible(false);
      setTimeout(onClose, 300);
    }, duration);

    return () => clearTimeout(timer);
  }, [duration, onClose]);

  const typeConfig = {
    success: {
      icon: <CheckCircle className="w-5 h-5" />,
      bgColor: 'bg-green-600',
      borderColor: 'border-green-500'
    },
    error: {
      icon: <XCircle className="w-5 h-5" />,
      bgColor: 'bg-red-600',
      borderColor: 'border-red-500'
    },
    warning: {
      icon: <AlertCircle className="w-5 h-5" />,
      bgColor: 'bg-yellow-600',
      borderColor: 'border-yellow-500'
    },
    info: {
      icon: <Info className="w-5 h-5" />,
      bgColor: 'bg-blue-600',
      borderColor: 'border-blue-500'
    }
  };

  const config = typeConfig[type] || typeConfig.info;

  return (
    <div
      className={`
        fixed top-4 right-4 z-50
        ${config.bgColor} ${config.borderColor} border
        text-white px-4 py-3 rounded-lg shadow-lg
        flex items-center gap-3
        transition-all duration-300
        ${visible ? 'opacity-100 translate-x-0' : 'opacity-0 translate-x-full'}
      `}
    >
      {config.icon}
      <span className="flex-1">{message}</span>
      <button
        onClick={() => {
          setVisible(false);
          setTimeout(onClose, 300);
        }}
        className="text-white hover:text-gray-200"
      >
        <X className="w-4 h-4" />
      </button>
    </div>
  );
}
