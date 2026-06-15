/**
 * ============================================
 * 👨‍✈️ Driver Page - نظام إدهام
 * Edham Logistics - Driver Page
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Loader, AlertCircle, User, Truck, MapPin, Clock, Star, Plus, Filter } from 'lucide-react';

export default function DriverPage() {
  const [drivers, setDrivers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filterStatus, setFilterStatus] = useState('all');

  useEffect(() => {
    fetchDrivers();
  }, [filterStatus]);

  const fetchDrivers = async () => {
    try {
      setLoading(true);
      const params = filterStatus !== 'all' ? { status: filterStatus } : {};
      const response = await api.get('/users', { params: { ...params, role: 'driver' } });
      setDrivers(response.data.data);
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
          <h1 className="text-3xl font-bold text-white">السائقين</h1>
          <p className="text-gray-400 mt-1">إدارة السائقين وتتبع أدائهم</p>
        </div>
        <button className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded flex items-center gap-2">
          <Plus className="w-4 h-4" />
          سائق جديد
        </button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <StatCard title="إجمالي السائقين" value="25" color="blue" />
        <StatCard title="نشطون حالياً" value="18" color="green" />
        <StatCard title="في إجازة" value="4" color="yellow" />
        <StatCard title="متوسط التقييم" value="4.5" color="gold" />
      </div>

      {/* Filter */}
      <div className="flex gap-4 mb-6">
        <select
          value={filterStatus}
          onChange={(e) => setFilterStatus(e.target.value)}
          className="bg-gray-800 text-white px-4 py-2 rounded border border-gray-700 focus:border-blue-500 outline-none"
        >
          <option value="all">جميع الحالات</option>
          <option value="active">نشط</option>
          <option value="inactive">غير نشط</option>
          <option value="on_trip">في رحلة</option>
        </select>
      </div>

      {/* Drivers List */}
      <div className="bg-gray-800 rounded-lg overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-white text-sm">
            <thead className="bg-gray-700">
              <tr>
                <th className="px-4 py-3 text-right">الاسم</th>
                <th className="px-4 py-3 text-right">الهاتف</th>
                <th className="px-4 py-3 text-right">الشاحنة الحالية</th>
                <th className="px-4 py-3 text-right">الموقع</th>
                <th className="px-4 py-3 text-right">الرحلات المكتملة</th>
                <th className="px-4 py-3 text-center">التقييم</th>
                <th className="px-4 py-3 text-center">الحالة</th>
                <th className="px-4 py-3 text-center">الإجراء</th>
              </tr>
            </thead>
            <tbody>
              {drivers.length === 0 ? (
                <tr>
                  <td colSpan="8" className="px-4 py-8 text-center text-gray-400">
                    لا يوجد سائقين
                  </td>
                </tr>
              ) : (
                drivers.map((driver) => (
                  <tr key={driver._id} className="border-t border-gray-700 hover:bg-gray-700">
                    <td className="px-4 py-3 font-semibold">{driver.name}</td>
                    <td className="px-4 py-3">{driver.phone || 'غير محدد'}</td>
                    <td className="px-4 py-3">{driver.currentTruck?.truckNumber || 'غير محدد'}</td>
                    <td className="px-4 py-3">{driver.currentLocation?.city || 'غير محدد'}</td>
                    <td className="px-4 py-3">{driver.completedTrips || 0}</td>
                    <td className="px-4 py-3 text-center">
                      <div className="flex items-center gap-1 justify-center">
                        <Star className="w-4 h-4 text-yellow-500 fill-current" />
                        <span>{driver.rating || 0}</span>
                      </div>
                    </td>
                    <td className="px-4 py-3 text-center">
                      <DriverStatusBadge status={driver.status} />
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

function DriverStatusBadge({ status }) {
  const statusColors = {
    active: 'bg-green-500',
    inactive: 'bg-gray-500',
    on_trip: 'bg-blue-500',
    on_break: 'bg-yellow-500'
  };

  const statusLabels = {
    active: 'نشط',
    inactive: 'غير نشط',
    on_trip: 'في رحلة',
    on_break: 'في استراحة'
  };

  return (
    <span className={`${statusColors[status] || 'bg-gray-500'} px-3 py-1 rounded text-xs`}>
      {statusLabels[status] || status}
    </span>
  );
}
