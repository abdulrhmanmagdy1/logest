/**
 * ============================================
 * 📅 Timeline Component - نظام إدهام
 * Edham Logistics - Timeline
 * ============================================
 */

import React from 'react';
import { CheckCircle, Clock, XCircle, AlertCircle } from 'lucide-react';

export default function Timeline({ items }) {
  return (
    <div className="space-y-4">
      {items.map((item, index) => (
        <TimelineItem
          key={index}
          title={item.title}
          date={item.date}
          status={item.status}
          description={item.description}
          isLast={index === items.length - 1}
        />
      ))}
    </div>
  );
}

function TimelineItem({ title, date, status = 'pending', description, isLast = false }) {
  const statusConfig = {
    completed: {
      icon: <CheckCircle className="w-4 h-4" />,
      color: 'bg-green-500',
      lineColor: 'bg-green-500'
    },
    pending: {
      icon: <Clock className="w-4 h-4" />,
      color: 'bg-gray-500',
      lineColor: 'bg-gray-500'
    },
    failed: {
      icon: <XCircle className="w-4 h-4" />,
      color: 'bg-red-500',
      lineColor: 'bg-red-500'
    },
    in_progress: {
      icon: <AlertCircle className="w-4 h-4" />,
      color: 'bg-blue-500',
      lineColor: 'bg-blue-500'
    }
  };

  const config = statusConfig[status] || statusConfig.pending;

  return (
    <div className="flex items-start gap-4">
      <div className="flex flex-col items-center">
        <div className={`w-8 h-8 rounded-full ${config.color} flex items-center justify-center text-white`}>
          {config.icon}
        </div>
        {!isLast && (
          <div className={`w-0.5 h-12 ${config.lineColor} mt-2`} />
        )}
      </div>
      <div className="flex-1 pb-4">
        <p className="text-white font-semibold">{title}</p>
        {description && <p className="text-gray-400 text-sm mt-1">{description}</p>}
        <p className="text-gray-500 text-sm mt-1">
          {date ? new Date(date).toLocaleString('ar-SA') : 'غير محدد'}
        </p>
      </div>
    </div>
  );
}
