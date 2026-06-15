/**
 * ============================================
 * 🚗 Trip Page - نظام إدهام
 * Edham Logistics - Trip Page
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Loader, AlertCircle, Truck, MapPin, Clock, Plus, Filter } from 'lucide-react';

export default function TripPage() {
  const [trips, setTrips] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filterStatus, setFilterStatus] = useState('all');

  useEffect(() => {
    fetchTrips();
  }, [filterStatus]);

  const fetchTrips = async () => {
    try {
      setLoading(true);
      const params = filterStatus !== 'all' ? { status: filterStatus } : {};
      const response = await api.get('/trips', { params });
      setTrips(response.data.data);
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
          <h1 className="text-3xl font-bold text-white">الرحلات</h1>
          <p className="text-gray-400 mt-1">إدارة وتتبع الرحلات</p>
        </div>
        <button className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded flex items-center gap-2">
          <Plus className="w-4 h-4" />
          رحلة جديدة
        </button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <StatCard title="الرحلات النشطة" value="8" color="blue" />
        <StatCard title="مكتملة اليوم" value="12" color="green" />
        <StatCard title="الرحلات الملغاة" value="2" color="red" />
        <StatCard title="إجمالي الكيلومترات" value="5,420 كم" color="gold" />
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
          <option value="started">بدأت</option>
          <option value="in_progress">قيد التنفيذ</option>
          <option value="completed">مكتملة</option>
        </select>
      </div>

      {/* Trips List */}
      <div className="bg-gray-800 rounded-lg overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-white text-sm">
            <thead className="bg-gray-700">
              <tr>
                <th className="px-4 py-3 text-right">رقم الرحلة</th>
                <th className="px-4 py-3 text-right">السائق</th>
                <th className="px-4 py-3 text-right">الشاحنة</th>
                <th className="px-4 py-3 text-right">من</th>
                <th className="px-4 py-3 text-right">إلى</th>
                <th className="px-4 py-3 text-right">المسافة</th>
                <th className="px-4 py-3 text-center">الحالة</th>
                <th className="px-4 py-3 text-center">الإجراء</th>
              </tr>
            </thead>
            <tbody>
              {trips.length === 0 ? (
                <tr>
                  <td colSpan="8" className="px-4 py-8 text-center text-gray-400">
                    لا توجد رحلات
                  </td>
                </tr>
              ) : (
                trips.map((trip) => (
                  <tr key={trip._id} className="border-t border-gray-700 hover:bg-gray-700">
                    <td className="px-4 py-3 font-semibold">{trip.tripNumber}</td>
                    <td className="px-4 py-3">{trip.driver?.name || 'غير محدد'}</td>
                    <td className="px-4 py-3">{trip.truck?.truckNumber || 'غير محدد'}</td>
                    <td className="px-4 py-3">{trip.pickupLocation?.city}</td>
                    <td className="px-4 py-3">{trip.deliveryLocation?.city}</td>
                    <td className="px-4 py-3">{trip.distance} كم</td>
                    <td className="px-4 py-3 text-center">
                      <TripStatusBadge status={trip.status} />
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
    red: 'bg-red-600',
    gold: 'bg-yellow-500'
  };

  return (
    <div className={`${colors[color]} p-6 rounded-lg text-white`}>
      <h3 className="text-gray-100 mb-2">{title}</h3>
      <p className="text-2xl font-bold">{value}</p>
    </div>
  );
}

function TripStatusBadge({ status }) {
  const statusColors = {
    pending: 'bg-yellow-500',
    started: 'bg-blue-500',
    in_progress: 'bg-purple-500',
    completed: 'bg-green-500',
    cancelled: 'bg-red-500',
    paused: 'bg-gray-500'
  };

  const statusLabels = {
    pending: 'قيد الانتظار',
    started: 'بدأت',
    in_progress: 'قيد التنفيذ',
    completed: 'مكتملة',
    cancelled: 'ملغاة',
    paused: 'متوقفة'
  };

  return (
    <span className={`${statusColors[status] || 'bg-gray-500'} px-3 py-1 rounded text-xs`}>
      {statusLabels[status] || status}
    </span>
  );
}
