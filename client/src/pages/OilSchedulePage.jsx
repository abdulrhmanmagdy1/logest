/**
 * ============================================
 * 🛢️ Oil Schedule Page - نظام إدهام
 * Edham Logistics - Oil Schedule Management
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Loader, AlertCircle, Droplet, Calendar, Truck, Plus, Filter } from 'lucide-react';

export default function OilSchedulePage() {
  const [schedules, setSchedules] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filterStatus, setFilterStatus] = useState('all');

  useEffect(() => {
    fetchSchedules();
  }, [filterStatus]);

  const fetchSchedules = async () => {
    try {
      setLoading(true);
      const params = filterStatus !== 'all' ? { status: filterStatus } : {};
      const response = await api.get('/oil-schedule', { params });
      setSchedules(response.data.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Loader className="animate-spin w-8 h-8 text-blue-600" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-500 text-white p-4 rounded">
        <AlertCircle className="inline mr-2" />
        {error}
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto p-6">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-white">جدولة تغيير الزيت</h1>
          <p className="text-gray-400 mt-1">إدارة وجدولة تغيير زيت الشاحنات</p>
        </div>
        <button className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded flex items-center gap-2">
          <Plus className="w-4 h-4" />
          جدولة جديدة
        </button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <StatCard title="مستحقة هذا الأسبوع" value="5" color="yellow" />
        <StatCard title="مكتملة هذا الشهر" value="12" color="green" />
        <StatCard title="متأخرة" value="2" color="red" />
        <StatCard title="إجمالي الجدول" value="45" color="blue" />
      </div>

      {/* Filter */}
      <div className="flex gap-4 mb-6">
        <select
          value={filterStatus}
          onChange={(e) => setFilterStatus(e.target.value)}
          className="bg-gray-800 text-white px-4 py-2 rounded border border-gray-700 focus:border-blue-500 outline-none"
        >
          <option value="all">جميع الحالات</option>
          <option value="pending">قيد الانتظار</option>
          <option value="completed">مكتملة</option>
          <option value="overdue">متأخرة</option>
        </select>
      </div>

      {/* Schedules List */}
      <div className="bg-gray-800 rounded-lg overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-white text-sm">
            <thead className="bg-gray-700">
              <tr>
                <th className="px-4 py-3 text-right">رقم الجدولة</th>
                <th className="px-4 py-3 text-right">الشاحنة</th>
                <th className="px-4 py-3 text-right">القراءة الحالية</th>
                <th className="px-4 py-3 text-right">القراءة المستهدفة</th>
                <th className="px-4 py-3 text-right">التاريخ المجدول</th>
                <th className="px-4 py-3 text-right">نوع الزيت</th>
                <th className="px-4 py-3 text-center">الحالة</th>
                <th className="px-4 py-3 text-center">الإجراء</th>
              </tr>
            </thead>
            <tbody>
              {schedules.length === 0 ? (
                <tr>
                  <td colSpan="8" className="px-4 py-8 text-center text-gray-400">
                    لا توجد جداول تغيير زيت
                  </td>
                </tr>
              ) : (
                schedules.map((schedule) => (
                  <tr key={schedule._id} className="border-t border-gray-700 hover:bg-gray-700">
                    <td className="px-4 py-3 font-semibold">{schedule.scheduleNumber}</td>
                    <td className="px-4 py-3">{schedule.truck?.truckNumber || 'غير محدد'}</td>
                    <td className="px-4 py-3">{schedule.currentReading} كم</td>
                    <td className="px-4 py-3">{schedule.targetReading} كم</td>
                    <td className="px-4 py-3">
                      {new Date(schedule.scheduledDate).toLocaleDateString('ar-SA')}
                    </td>
                    <td className="px-4 py-3">{schedule.oilType}</td>
                    <td className="px-4 py-3 text-center">
                      <OilStatusBadge status={schedule.status} />
                    </td>
                    <td className="px-4 py-3 text-center">
                      <button className="text-blue-500 hover:text-blue-400">عرض</button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

function StatCard({ title, value, color }) {
  const colors = {
    blue: 'bg-blue-600',
    green: 'bg-green-600',
    yellow: 'bg-yellow-600',
    red: 'bg-red-600'
  };

  return (
    <div className={`${colors[color]} p-6 rounded-lg text-white`}>
      <h3 className="text-gray-100 mb-2">{title}</h3>
      <p className="text-2xl font-bold">{value}</p>
    </div>
  );
}

function OilStatusBadge({ status }) {
  const statusColors = {
    pending: 'bg-yellow-500',
    completed: 'bg-green-500',
    overdue: 'bg-red-500',
    cancelled: 'bg-gray-500'
  };

  const statusLabels = {
    pending: 'قيد الانتظار',
    completed: 'مكتملة',
    overdue: 'متأخرة',
    cancelled: 'ملغاة'
  };

  return (
    <span className={`${statusColors[status] || 'bg-gray-500'} px-3 py-1 rounded text-xs`}>
      {statusLabels[status] || status}
    </span>
  );
}
