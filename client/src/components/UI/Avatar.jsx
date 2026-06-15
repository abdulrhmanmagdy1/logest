/**
 * ============================================
 * 👤 Avatar Component - نظام إدهام
 * Edham Logistics - Avatar
 * ============================================
 */

import React from 'react';
import { User } from 'lucide-react';

export default function Avatar({ src, alt = 'Avatar', size = 'md', className = '', onClick }) {
  const sizeClasses = {
    sm: 'w-8 h-8',
    md: 'w-10 h-10',
    lg: 'w-12 h-12',
    xl: 'w-16 h-16'
  };

  return (
    <div
      className={`${sizeClasses[size]} rounded-full overflow-hidden bg-blue-600 flex items-center justify-center ${onClick ? 'cursor-pointer' : ''} ${className}`}
      onClick={onClick}
    >
      {src ? (
        <img src={src} alt={alt} className="w-full h-full object-cover" />
      ) : (
        <User className="w-5 h-5 text-white" />
      )}
    </div>
  );
}

export function AvatarGroup({ children, max = 3, size = 'md' }) {
  const sizeClasses = {
    sm: 'w-8 h-8',
    md: 'w-10 h-10',
    lg: 'w-12 h-12',
    xl: 'w-16 h-16'
  };

  const childrenArray = React.Children.toArray(children);
  const visibleChildren = childrenArray.slice(0, max);
  const remainingCount = childrenArray.length - max;

  return (
    <div className="flex -space-x-2 rtl:space-x-reverse">
      {visibleChildren.map((child, index) => (
        <div
          key={index}
          className={`${sizeClasses[size]} rounded-full border-2 border-gray-800 overflow-hidden`}
          style={{ zIndex: max - index }}
        >
          {child}
        </div>
      ))}
      {remainingCount > 0 && (
        <div
          className={`${sizeClasses[size]} rounded-full border-2 border-gray-800 bg-gray-700 flex items-center justify-center text-white text-sm font-semibold`}
        >
          +{remainingCount}
        </div>
      )}
    </div>
  );
}
