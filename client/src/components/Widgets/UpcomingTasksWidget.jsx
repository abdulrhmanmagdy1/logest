/**
 * ============================================
 * 📋 Upcoming Tasks Widget - نظام إدهام
 * Edham Logistics - Upcoming Tasks Widget
 * ============================================
 */

import React from 'react';
import { Calendar, Clock, MapPin, AlertTriangle } from 'lucide-react';

export default function UpcomingTasksWidget() {
  const tasks = [
    {
      id: 1,
      title: 'صيانة الشاحنة TRK001',
      date: 'اليوم',
      time: '09:00',
      location: 'مركز الصيانة',
      priority: 'high',
      status: 'pending'
    },
    {
      id: 2,
      title: 'تسليم الشحنة #12345',
      date: 'غداً',
      time: '14:00',
      location: 'الرياض',
      priority: 'medium',
      status: 'scheduled'
    },
    {
      id: 3,
      title: 'تغيير زيت الشاحنة TRK002',
      date: 'بعد 3 أيام',
      time: '08:00',
      location: 'مركز الصيانة',
      priority: 'low',
      status: 'scheduled'
    },
    {
      id: 4,
      title: 'فحص دوري الشاحنة TRK003',
      date: 'بعد أسبوع',
      time: '10:00',
      location: 'مركز الفحص',
      priority: 'medium',
      status: 'scheduled'
    }
  ];

  const getPriorityColor = (priority) => {
    const colors = {
      high: 'bg-red-500',
      medium: 'bg-yellow-500',
      low: 'bg-green-500'
    };
    return colors[priority] || colors.medium;
  };

  const getPriorityLabel = (priority) => {
    const labels = {
      high: 'عالي',
      medium: 'متوسط',
      low: 'منخفض'
    };
    return labels[priority] || priority;
  };

  return (
    <div className="bg-gray-800 rounded-lg p-6">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-bold text-white flex items-center gap-2">
          <Calendar className="w-5 h-5" />
          المهام القادمة
        </h3>
        <button className="text-blue-500 hover:text-blue-400 text-sm">
          عرض الكل
        </button>
      </div>

      <div className="space-y-3">
        {tasks.map((task) => (
          <div
            key={task.id}
            className="p-4 bg-gray-700 rounded-lg hover:bg-gray-600 transition"
          >
            <div className="flex items-start justify-between mb-2">
              <h4 className="text-white font-semibold">{task.title}</h4>
              <span className={`${getPriorityColor(task.priority)} px-2 py-1 rounded text-xs text-white`}>
                {getPriorityLabel(task.priority)}
              </span>
            </div>
            <div className="space-y-1 text-sm">
              <div className="flex items-center gap-2 text-gray-300">
                <Calendar className="w-4 h-4" />
                <span>{task.date}</span>
              </div>
              <div className="flex items-center gap-2 text-gray-300">
                <Clock className="w-4 h-4" />
                <span>{task.time}</span>
              </div>
              <div className="flex items-center gap-2 text-gray-300">
                <MapPin className="w-4 h-4" />
                <span>{task.location}</span>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
