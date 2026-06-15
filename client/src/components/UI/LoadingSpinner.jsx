/**
 * ============================================
 * ⏳ Loading Spinner Component - نظام إدهام
 * Edham Logistics - Loading Spinner
 * ============================================
 */

import React from 'react';

export default function LoadingSpinner({ size = 'md', text = '' }) {
  const sizeClasses = {
    sm: 'w-4 h-4',
    md: 'w-8 h-8',
    lg: 'w-12 h-12',
    xl: 'w-16 h-16'
  };

  return (
    <div className="flex flex-col items-center justify-center">
      <div className={`animate-spin rounded-full border-t-2 border-b-2 border-blue-600 ${sizeClasses[size]}`}></div>
      {text && <p className="text-gray-400 mt-2">{text}</p>}
    </div>
  );
}
