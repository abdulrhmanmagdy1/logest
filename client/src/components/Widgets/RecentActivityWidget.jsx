/**
 * ============================================
 * 📊 Recent Activity Widget - نظام إدهام
 * Edham Logistics - Recent Activity Widget
 * ============================================
 */

import React from 'react';
import { Activity, Clock, CheckCircle, XCircle, AlertCircle } from 'lucide-react';

export default function RecentActivityWidget() {
  const activities = [
    {
      id: 1,
      type: 'success',
      message: 'تم تسليم الشحنة #12345',
      time: 'منذ 5 دقائق',
      icon: <CheckCircle className="w-4 h-4" />
    },
    {
      id: 2,
      type: 'warning',
      message: 'الشاحنة TRK001 تحتاج صيانة',
      time: 'منذ 15 دقيقة',
      icon: <AlertCircle className="w-4 h-4" />
    },
    {
      id: 3,
      type: 'error',
      message: 'فشل محاولة تسجيل الدخول',
      time: 'منذ ساعة',
      icon: <XCircle className="w-4 h-4" />
    },
    {
      id: 4,
      type: 'success',
      message: 'تم إنشاء فاتورة جديدة #INV678',
      time: 'منذ ساعتين',
      icon: <CheckCircle className="w-4 h-4" />
    },
    {
      id: 5,
      type: 'success',
      message: 'أكمل خالد الرحلة #TRIP123',
      time: 'منذ 3 ساعات',
      icon: <CheckCircle className="w-4 h-4" />
    }
  ];

  const getTypeStyles = (type) => {
    const styles = {
      success: 'bg-green-500/20 text-green-500 border-green-500/30',
      warning: 'bg-yellow-500/20 text-yellow-500 border-yellow-500/30',
      error: 'bg-red-500/20 text-red-500 border-red-500/30',
      info: 'bg-blue-500/20 text-blue-500 border-blue-500/30'
    };
    return styles[type] || styles.info;
  };

  return (
    <div className="bg-gray-800 rounded-lg p-6">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-bold text-white flex items-center gap-2">
          <Activity className="w-5 h-5" />
          النشاط الأخير
        </h3>
        <button className="text-blue-500 hover:text-blue-400 text-sm">
          عرض الكل
        </button>
      </div>

      <div className="space-y-3">
        {activities.map((activity) => (
          <div
            key={activity.id}
            className={`flex items-start gap-3 p-3 rounded-lg border ${getTypeStyles(activity.type)}`}
          >
            <div className="mt-0.5">{activity.icon}</div>
            <div className="flex-1">
              <p className="text-white text-sm">{activity.message}</p>
              <p className="text-xs opacity-75 mt-1 flex items-center gap-1">
                <Clock className="w-3 h-3" />
                {activity.time}
              </p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
