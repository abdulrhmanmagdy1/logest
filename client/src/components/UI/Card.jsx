/**
 * ============================================
 * 🃏 Card Component - نظام إدهام
 * Edham Logistics - Card Component
 * ============================================
 */

import React from 'react';

export default function Card({ children, className = '', onClick }) {
  const baseClasses = 'bg-gray-800 rounded-lg shadow-lg';
  const clickableClasses = onClick ? 'cursor-pointer hover:bg-gray-700 transition' : '';
  
  return (
    <div
      className={`${baseClasses} ${clickableClasses} ${className}`}
      onClick={onClick}
    >
      {children}
    </div>
  );
}

export function CardHeader({ children, className = '' }) {
  return (
    <div className={`p-4 border-b border-gray-700 ${className}`}>
      {children}
    </div>
  );
}

export function CardBody({ children, className = '' }) {
  return (
    <div className={`p-4 ${className}`}>
      {children}
    </div>
  );
}

export function CardFooter({ children, className = '' }) {
  return (
    <div className={`p-4 border-t border-gray-700 ${className}`}>
      {children}
    </div>
  );
}
