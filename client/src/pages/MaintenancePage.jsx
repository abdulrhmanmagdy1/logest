/**
 * ============================================
 * 🔧 Maintenance Page - نظام إدهام
 * Edham Logistics - Maintenance Page
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Loader, AlertCircle, Wrench, Plus, Calendar, Truck, Filter } from 'lucide-react';

export default function MaintenancePage() {
  const [maintenanceRecords, setMaintenanceRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filterStatus, setFilterStatus] = useState('all');

  useEffect(() => {
    fetchMaintenanceRecords();
  }, [filterStatus]);

  const fetchMaintenanceRecords = async () => {
    try {
      setLoading(true);
      const params = filterStatus !== 'all' ? { status: filterStatus } : {};
      const response = await api.get('/maintenance', { params });
      setMaintenanceRecords(response.data.data);
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
          <h1 className="text-3xl font-bold text-white">الصيانة</h1>
          <p className="text-gray-400 mt-1">إدارة سجلات وجداول الصيانة</p>
        </div>
        <button className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded flex items-center gap-2">
          <Plus className="w-4 h-4" />
          صيانة جديدة
        </button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <StatCard title="الصيانة المستحقة" value="5" color="yellow" />
        <StatCard title="قيد التنفيذ" value="3" color="blue" />
        <StatCard title="مكتملة هذا الشهر" value="12" color="green" />
        <StatCard title="إجمالي التكلفة" value="15,000 ريال" color="gold" />
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
          <option value="in_progress">قيد التنفيذ</option>
          <option value="completed">مكتملة</option>
        </select>
      </div>

      {/* Maintenance List */}
      <div className="bg-gray-800 rounded-lg overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-white text-sm">
            <thead className="bg-gray-700">
              <tr>
                <th className="px-4 py-3 text-right">رقم الصيانة</th>
                <th className="px-4 py-3 text-right">الشاحنة</th>
                <th className="px-4 py-3 text-right">النوع</th>
                <th className="px-4 py-3 text-right">التاريخ المجدول</th>
                <th className="px-4 py-3 text-right">التكلفة</th>
                <th className="px-4 py-3 text-center">الحالة</th>
                <th className="px-4 py-3 text-center">الإجراء</th>
              </tr>
            </thead>
            <tbody>
              {maintenanceRecords.length === 0 ? (
                <tr>
                  <td colSpan="7" className="px-4 py-8 text-center text-gray-400">
                    لا توجد سجلات صيانة
                  </td>
                </tr>
              ) : (
                maintenanceRecords.map((record) => (
                  <tr key={record._id} className="border-t border-gray-700 hover:bg-gray-700">
                    <td className="px-4 py-3 font-semibold">{record.maintenanceNumber}</td>
                    <td className="px-4 py-3">{record.truck?.truckNumber || 'غير محدد'}</td>
                    <td className="px-4 py-3">{record.type}</td>
                    <td className="px-4 py-3">
                      {new Date(record.scheduledDate).toLocaleDateString('ar-SA')}
                    </td>
                    <td className="px-4 py-3">{record.cost} ريال</td>
                    <td className="px-4 py-3 text-center">
                      <MaintenanceStatusBadge status={record.status} />
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
    gold: 'bg-yellow-500'
  };

  return (
    <div className={`${colors[color]} p-6 rounded-lg text-white`}>
      <h3 className="text-gray-100 mb-2">{title}</h3>
      <p className="text-2xl font-bold">{value}</p>
    </div>
  );
}

function MaintenanceStatusBadge({ status }) {
  const statusColors = {
    pending: 'bg-yellow-500',
    in_progress: 'bg-blue-500',
    completed: 'bg-green-500',
    cancelled: 'bg-red-500'
  };

  const statusLabels = {
    pending: 'قيد الانتظار',
    in_progress: 'قيد التنفيذ',
    completed: 'مكتملة',
    cancelled: 'ملغاة'
  };

  return (
    <span className={`${statusColors[status] || 'bg-gray-500'} px-3 py-1 rounded text-xs`}>
      {statusLabels[status] || status}
    </span>
  );
}
