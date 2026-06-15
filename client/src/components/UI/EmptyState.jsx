/**
 * ============================================
 * 📭 Empty State Component - نظام إدهام
 * Edham Logistics - Empty State
 * ============================================
 */

import React from 'react';
import { Package, Truck, FileText, Users, Inbox, AlertCircle } from 'lucide-react';

export default function EmptyState({ icon, title, description, action, actionLabel }) {
  const iconMap = {
    package: <Package className="w-16 h-16" />,
    truck: <Truck className="w-16 h-16" />,
    file: <FileText className="w-16 h-16" />,
    users: <Users className="w-16 h-16" />,
    inbox: <Inbox className="w-16 h-16" />,
    alert: <AlertCircle className="w-16 h-16" />
  };

  const IconComponent = iconMap[icon] || iconMap.inbox;

  return (
    <div className="flex flex-col items-center justify-center p-8 text-center">
      <div className="text-gray-600 mb-4">
        {IconComponent}
      </div>
      <h3 className="text-xl font-bold text-white mb-2">{title}</h3>
      <p className="text-gray-400 mb-6 max-w-md">{description}</p>
      {action && (
        <button
          onClick={action}
          className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-6 rounded transition"
        >
          {actionLabel}
        </button>
      )}
    </div>
  );
}
