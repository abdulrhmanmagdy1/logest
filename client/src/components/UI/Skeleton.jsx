/**
 * ============================================
 * 💀 Skeleton Component - نظام إدهام
 * Edham Logistics - Skeleton Loading
 * ============================================
 */

import React from 'react';

export default function Skeleton({ className = '', width, height, variant = 'rect' }) {
  const variantClasses = {
    rect: 'rounded',
    circular: 'rounded-full',
    text: 'rounded h-4'
  };

  return (
    <div
      className={`animate-pulse bg-gray-700 ${variantClasses[variant]} ${className}`}
      style={{ width, height }}
    />
  );
}

export function SkeletonCard() {
  return (
    <div className="bg-gray-800 p-6 rounded-lg">
      <div className="flex items-center gap-4 mb-4">
        <Skeleton variant="circular" width={48} height={48} />
        <div className="flex-1 space-y-2">
          <Skeleton width="60%" height={20} />
          <Skeleton width="40%" height={16} />
        </div>
      </div>
      <div className="space-y-2">
        <Skeleton width="100%" height={16} />
        <Skeleton width="80%" height={16} />
        <Skeleton width="60%" height={16} />
      </div>
    </div>
  );
}

export function SkeletonTable({ rows = 5, columns = 4 }) {
  return (
    <div className="bg-gray-800 rounded-lg overflow-hidden">
      <div className="bg-gray-700 p-4">
        <div className="flex gap-4">
          {Array.from({ length: columns }).map((_, index) => (
            <Skeleton key={index} width="20%" height={20} />
          ))}
        </div>
      </div>
      <div className="p-4 space-y-3">
        {Array.from({ length: rows }).map((_, rowIndex) => (
          <div key={rowIndex} className="flex gap-4">
            {Array.from({ length: columns }).map((_, colIndex) => (
              <Skeleton key={colIndex} width="20%" height={16} />
            ))}
          </div>
        ))}
      </div>
    </div>
  );
}

export function SkeletonList({ count = 5 }) {
  return (
    <div className="space-y-3">
      {Array.from({ length: count }).map((_, index) => (
        <div key={index} className="flex items-center gap-4 p-4 bg-gray-800 rounded-lg">
          <Skeleton variant="circular" width={40} height={40} />
          <div className="flex-1 space-y-2">
            <Skeleton width="50%" height={16} />
            <Skeleton width="30%" height={14} />
          </div>
        </div>
      ))}
    </div>
  );
}
