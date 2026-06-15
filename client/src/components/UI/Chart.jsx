/**
 * ============================================
 * 📈 Chart Component - نظام إدهام
 * Edham Logistics - Chart Component
 * ============================================
 */

import React from 'react';

export default function Chart({ type = 'bar', data, labels, height = 300 }) {
  const maxValue = Math.max(...data);

  return (
    <div className="bg-gray-800 p-6 rounded-lg">
      <div style={{ height: `${height}px` }} className="flex items-end justify-between gap-2">
        {data.map((value, index) => (
          <div key={index} className="flex-1 flex flex-col items-center">
            <div
              className="w-full bg-blue-500 rounded-t transition-all duration-300 hover:bg-blue-600"
              style={{ height: `${(value / maxValue) * 100}%` }}
            />
            <span className="text-gray-400 text-xs mt-2 text-center">
              {labels[index]}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
}

export function LineChart({ data, labels, height = 300 }) {
  const maxValue = Math.max(...data);
  const points = data.map((value, index) => {
    const x = (index / (data.length - 1)) * 100;
    const y = 100 - (value / maxValue) * 100;
    return `${x},${y}`;
  }).join(' ');

  return (
    <div className="bg-gray-800 p-6 rounded-lg">
      <svg width="100%" height={height} viewBox="0 0 100 100" preserveAspectRatio="none">
        <polyline
          fill="none"
          stroke="#3B82F6"
          strokeWidth="2"
          points={points}
        />
        {data.map((value, index) => {
          const x = (index / (data.length - 1)) * 100;
          const y = 100 - (value / maxValue) * 100;
          return (
            <circle
              key={index}
              cx={x}
              cy={y}
              r="2"
              fill="#3B82F6"
            />
          );
        })}
      </svg>
      <div className="flex justify-between mt-2">
        {labels.map((label, index) => (
          <span key={index} className="text-gray-400 text-xs">
            {label}
          </span>
        ))}
      </div>
    </div>
  );
}

export function PieChart({ data, labels, size = 200 }) {
  const total = data.reduce((sum, value) => sum + value, 0);
  let currentAngle = 0;

  const colors = ['#3B82F6', '#22C55E', '#EAB308', '#EF4444', '#A855F7', '#EC4899'];

  const slices = data.map((value, index) => {
    const percentage = (value / total) * 100;
    const angle = (percentage / 100) * 360;
    const startAngle = currentAngle;
    const endAngle = currentAngle + angle;
    currentAngle += angle;

    const x1 = 50 + 50 * Math.cos((startAngle - 90) * Math.PI / 180);
    const y1 = 50 + 50 * Math.sin((startAngle - 90) * Math.PI / 180);
    const x2 = 50 + 50 * Math.cos((endAngle - 90) * Math.PI / 180);
    const y2 = 50 + 50 * Math.sin((endAngle - 90) * Math.PI / 180);

    const largeArcFlag = angle > 180 ? 1 : 0;

    return {
      path: `M 50 50 L ${x1} ${y1} A 50 50 0 ${largeArcFlag} 1 ${x2} ${y2} Z`,
      color: colors[index % colors.length],
      label: labels[index],
      value,
      percentage
    };
  });

  return (
    <div className="bg-gray-800 p-6 rounded-lg">
      <div className="flex items-center gap-8">
        <svg width={size} height={size} viewBox="0 0 100 100">
          {slices.map((slice, index) => (
            <path
              key={index}
              d={slice.path}
              fill={slice.color}
              className="hover:opacity-80 transition cursor-pointer"
            />
          ))}
        </svg>
        <div className="space-y-2">
          {slices.map((slice, index) => (
            <div key={index} className="flex items-center gap-2">
              <div
                className="w-3 h-3 rounded"
                style={{ backgroundColor: slice.color }}
              />
              <span className="text-white text-sm">
                {slice.label}: {Math.round(slice.percentage)}%
              </span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
