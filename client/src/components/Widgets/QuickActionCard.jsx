/**
 * ============================================
 * ⚡ Quick Action Card - نظام إدهام
 * Edham Logistics - Quick Action Widget
 * ============================================
 */

import React from 'react';
import { Plus, Truck, FileText, Package, User, ChevronRight } from 'lucide-react';

export default function QuickActionCard({ title, description, icon, onClick, color }) {
  const colors = {
    blue: 'bg-blue-600 hover:bg-blue-700',
    green: 'bg-green-600 hover:bg-green-700',
    yellow: 'bg-yellow-600 hover:bg-yellow-700',
    purple: 'bg-purple-600 hover:bg-purple-700',
    red: 'bg-red-600 hover:bg-red-700'
  };

  return (
    <button
      onClick={onClick}
      className={`${colors[color] || colors.blue} p-6 rounded-lg text-left transition-all hover:shadow-lg`}
    >
      <div className="flex items-start justify-between">
        <div className="flex-1">
          <div className="w-12 h-12 bg-white/20 rounded-lg flex items-center justify-center mb-4">
            {icon}
          </div>
          <h3 className="text-white font-bold text-lg mb-2">{title}</h3>
          <p className="text-white/80 text-sm">{description}</p>
        </div>
        <ChevronRight className="w-5 h-5 text-white/60" />
      </div>
    </button>
  );
}

export function QuickActionsGrid() {
  const actions = [
    {
      title: 'شحنة جديدة',
      description: 'إنشاء شحنة جديدة',
      icon: <Package className="w-6 h-6 text-white" />,
      color: 'blue',
      onClick: () => console.log('Create shipment')
    },
    {
      title: 'إضافة شاحنة',
      description: 'تسجيل شاحنة جديدة',
      icon: <Truck className="w-6 h-6 text-white" />,
      color: 'green',
      onClick: () => console.log('Add truck')
    },
    {
      title: 'إنشاء فاتورة',
      description: 'إصدار فاتورة جديدة',
      icon: <FileText className="w-6 h-6 text-white" />,
      color: 'yellow',
      onClick: () => console.log('Create invoice')
    },
    {
      title: 'إضافة مستخدم',
      description: 'إنشاء حساب جديد',
      icon: <User className="w-6 h-6 text-white" />,
      color: 'purple',
      onClick: () => console.log('Add user')
    }
  ];

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
      {actions.map((action, index) => (
        <QuickActionCard key={index} {...action} />
      ))}
    </div>
  );
}
