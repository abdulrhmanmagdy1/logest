/**
 * ============================================
 * 📊 Stat Card Component - نظام إدهام
 * Edham Logistics - Stat Card
 * ============================================
 */

import React from 'react';
import { TrendingUp, TrendingDown, Minus } from 'lucide-react';

export default function StatCard({ title, value, subtitle, icon, color = 'blue', trend, trendValue }) {
  const colorClasses = {
    blue: 'bg-blue-600',
    green: 'bg-green-600',
    yellow: 'bg-yellow-600',
    red: 'bg-red-600',
    purple: 'bg-purple-600',
    gold: 'bg-yellow-500',
    dark: 'bg-gray-800'
  };

  return (
    <div className={`${colorClasses[color]} p-6 rounded-lg text-white`}>
      <div className="flex items-center justify-between mb-2">
        <h3 className="text-gray-100">{title}</h3>
        {icon}
      </div>
      <p className="text-3xl font-bold mb-1">{value}</p>
      <p className="text-sm text-gray-100 opacity-75 mb-2">{subtitle}</p>
      {trend && (
        <div className="flex items-center gap-1 text-xs">
          {trend === 'up' && <TrendingUp className="w-3 h-3" />}
          {trend === 'down' && <TrendingDown className="w-3 h-3" />}
          {trend === 'neutral' && <Minus className="w-3 h-3" />}
          <span>{trendValue}</span>
        </div>
      )}
    </div>
  );
}
