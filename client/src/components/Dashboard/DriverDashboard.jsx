/**
 * ============================================
 * 🚛 Driver Dashboard - نظام إدهام
 * Edham Logistics - Driver Dashboard
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import { Loader, AlertCircle, MapPin, Clock, CheckCircle, Truck } from 'lucide-react';

export default function DriverDashboard() {
  const [activeTrip, setActiveTrip] = useState(null);
  const [completedTrips, setCompletedTrips] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchDriverData();
  }, []);

  const fetchDriverData = async () => {
    try {
      setLoading(true);
      const [tripsRes, completedRes] = await Promise.all([
        api.get('/trips/active'),
        api.get('/trips/completed?limit=5')
      ]);

      setActiveTrip(tripsRes.data);
      setCompletedTrips(completedRes.data);
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
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-bold text-white">لوحة السائق</h1>
        <p className="text-gray-400 mt-1">إدارة الرحلات والموقع</p>
      </div>

      {/* Active Trip */}
      {activeTrip ? (
        <div className="bg-gray-800 p-6 rounded-lg border-l-4 border-blue-500">
          <h2 className="text-xl font-bold text-white mb-4 flex items-center gap-2">
            <Truck className="w-6 h-6" />
            الرحلة النشطة
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="bg-gray-700 p-4 rounded">
              <h3 className="text-gray-400 text-sm mb-1 flex items-center gap-2">
                <MapPin className="w-4 h-4" />
                موقع الاستلام
              </h3>
              <p className="text-white font-semibold">{activeTrip.pickupLocation}</p>
            </div>
            <div className="bg-gray-700 p-4 rounded">
              <h3 className="text-gray-400 text-sm mb-1 flex items-center gap-2">
                <MapPin className="w-4 h-4" />
                موقع التسليم
              </h3>
              <p className="text-white font-semibold">{activeTrip.deliveryLocation}</p>
            </div>
            <div className="bg-gray-700 p-4 rounded">
              <h3 className="text-gray-400 text-sm mb-1 flex items-center gap-2">
                <Clock className="w-4 h-4" />
                وقت البدء
              </h3>
              <p className="text-white font-semibold">
                {new Date(activeTrip.startTime).toLocaleString('ar-SA')}
              </p>
            </div>
            <div className="bg-gray-700 p-4 rounded">
              <h3 className="text-gray-400 text-sm mb-1">الحالة</h3>
              <p className="text-white font-semibold">
                <StatusBadge status={activeTrip.status} />
              </p>
            </div>
          </div>
          <div className="mt-4 flex gap-4">
            <button className="flex-1 bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
              تحديث الموقع
            </button>
            <button className="flex-1 bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-4 rounded">
              إكمال الرحلة
            </button>
          </div>
        </div>
      ) : (
        <div className="bg-gray-800 p-6 rounded-lg text-center">
          <Truck className="w-12 h-12 text-gray-600 mx-auto mb-4" />
          <p className="text-gray-400">لا توجد رحلات نشطة حالياً</p>
        </div>
      )}

      {/* Completed Trips */}
      <div className="bg-gray-800 p-6 rounded-lg">
        <h2 className="text-xl font-bold text-white mb-4 flex items-center gap-2">
          <CheckCircle className="w-6 h-6" />
          الرحلات المكتملة
        </h2>
        <div className="overflow-x-auto">
          <table className="w-full text-white text-sm">
            <thead className="bg-gray-700">
              <tr>
                <th className="px-4 py-2 text-right">رقم الرحلة</th>
                <th className="px-4 py-2 text-right">من</th>
                <th className="px-4 py-2 text-right">إلى</th>
                <th className="px-4 py-2 text-right">التاريخ</th>
                <th className="px-4 py-2 text-center">الحالة</th>
                <th className="px-4 py-2 text-center">الإجراء</th>
              </tr>
            </thead>
            <tbody>
              {completedTrips.map((trip) => (
                <tr key={trip._id} className="border-t border-gray-700 hover:bg-gray-700">
                  <td className="px-4 py-3">{trip.tripNumber}</td>
                  <td className="px-4 py-3">{trip.pickupLocation}</td>
                  <td className="px-4 py-3">{trip.deliveryLocation}</td>
                  <td className="px-4 py-3">{new Date(trip.completedAt).toLocaleDateString('ar-SA')}</td>
                  <td className="px-4 py-3 text-center">
                    <StatusBadge status={trip.status} />
                  </td>
                  <td className="px-4 py-3 text-center">
                    <button className="text-blue-500 hover:underline">عرض</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <StatCard
          title="الرحلات المكتملة اليوم"
          value={completedTrips.filter(t => 
            new Date(t.completedAt).toDateString() === new Date().toDateString()
          ).length}
          icon={<CheckCircle className="w-6 h-6" />}
          color="green"
        />
        <StatCard
          title="إجمالي الرحلات"
          value={completedTrips.length}
          icon={<Truck className="w-6 h-6" />}
          color="blue"
        />
        <StatCard
          title="التقييم"
          value="4.8"
          subtitle="من 5"
          icon={<CheckCircle className="w-6 h-6" />}
          color="gold"
        />
      </div>
    </div>
  );
}

function StatCard({ title, value, subtitle, icon, color }) {
  const colors = {
    blue: 'bg-blue-600',
    green: 'bg-green-600',
    gold: 'bg-yellow-600'
  };

  return (
    <div className={`${colors[color]} p-6 rounded-lg text-white flex items-center gap-4`}>
      <div className="p-3 bg-white/20 rounded-lg">{icon}</div>
      <div>
        <h3 className="text-gray-100 mb-1">{title}</h3>
        <p className="text-3xl font-bold">{value}</p>
        {subtitle && <p className="text-sm text-gray-100 opacity-75">{subtitle}</p>}
      </div>
    </div>
  );
}

function StatusBadge({ status }) {
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
