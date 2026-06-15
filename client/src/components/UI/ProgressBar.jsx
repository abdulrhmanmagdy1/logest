/**
 * ============================================
 * 📊 Progress Bar Component - نظام إدهام
 * Edham Logistics - Progress Bar
 * ============================================
 */

import React from 'react';

export default function ProgressBar({ value, max = 100, color = 'blue', showLabel = true, label }) {
  const percentage = Math.min(100, Math.max(0, (value / max) * 100));

  const colorClasses = {
    blue: 'bg-blue-500',
    green: 'bg-green-500',
    yellow: 'bg-yellow-500',
    red: 'bg-red-500',
    purple: 'bg-purple-500',
    gold: 'bg-yellow-500'
  };

  return (
    <div>
      {showLabel && (
        <div className="flex justify-between text-white mb-1">
          <span>{label || ''}</span>
          <span>{percentage}%</span>
        </div>
      )}
      <div className="w-full bg-gray-700 rounded-full h-2">
        <div
          className={`${colorClasses[color]} h-2 rounded-full transition-all duration-300`}
          style={{ width: `${percentage}%` }}
        />
      </div>
    </div>
  );
}

export function CircularProgress({ value, max = 100, size = 60, strokeWidth = 4, color = 'blue' }) {
  const percentage = Math.min(100, Math.max(0, (value / max) * 100));
  const radius = (size - strokeWidth) / 2;
  const circumference = radius * 2 * Math.PI;
  const offset = circumference - (percentage / 100) * circumference;

  const colorClasses = {
    blue: '#3B82F6',
    green: '#22C55E',
    yellow: '#EAB308',
    red: '#EF4444',
    purple: '#A855F7',
    gold: '#EAB308'
  };

  return (
    <div className="relative inline-flex items-center justify-center">
      <svg width={size} height={size} className="transform -rotate-90">
        <circle
          stroke="#374151"
          strokeWidth={strokeWidth}
          fill="transparent"
          r={radius}
          cx={size / 2}
          cy={size / 2}
        />
        <circle
          stroke={colorClasses[color]}
          strokeWidth={strokeWidth}
          fill="transparent"
          r={radius}
          cx={size / 2}
          cy={size / 2}
          strokeDasharray={circumference}
          strokeDashoffset={offset}
          strokeLinecap="round"
          className="transition-all duration-300"
        />
      </svg>
      <span className="absolute text-white font-semibold text-sm">
        {Math.round(percentage)}%
      </span>
    </div>
  );
}
