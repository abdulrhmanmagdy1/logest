/**
 * ============================================
 * 👷 Employee Dashboard - نظام إدهام
 * Edham Logistics - Employee Dashboard
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import { Loader, AlertCircle, Truck, Calendar, MapPin, Clock } from 'lucide-react';

export default function EmployeeDashboard() {
  const [trucks, setTrucks] = useState([]);
  const [trips, setTrips] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchEmployeeData();
  }, []);

  const fetchEmployeeData = async () => {
    try {
      setLoading(true);
      const [trucksRes, tripsRes] = await Promise.all([
        api.get('/trucks'),
        api.get('/trips?limit=5')
      ]);

      setTrucks(trucksRes.data);
      setTrips(tripsRes.data);
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
        <h1 className="text-3xl font-bold text-white">لوحة الموظف</h1>
        <p className="text-gray-400 mt-1">إدارة المركبات والرحلات</p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <StatCard
          title="إجمالي الشاحنات"
          value={trucks.length}
          icon={<Truck className="w-6 h-6" />}
          color="blue"
        />
        <StatCard
          title="شاحنات نشطة"
          value={trucks.filter(t => t.status === 'active').length}
          icon={<Truck className="w-6 h-6" />}
          color="green"
        />
        <StatCard
          title="تحتاج صيانة"
          value={trucks.filter(t => t.status === 'maintenance').length}
          icon={<Calendar className="w-6 h-6" />}
          color="yellow"
        />
        <StatCard
          title="الرحلات اليوم"
          value={trips.filter(t => 
            new Date(t.date).toDateString() === new Date().toDateString()
          ).length}
          icon={<Clock className="w-6 h-6" />}
          color="dark"
        />
      </div>

      {/* Trucks List */}
      <div className="bg-gray-800 p-6 rounded-lg">
        <h2 className="text-xl font-bold text-white mb-4 flex items-center gap-2">
          <Truck className="w-6 h-6" />
          الشاحنات
        </h2>
        <div className="overflow-x-auto">
          <table className="w-full text-white text-sm">
            <thead className="bg-gray-700">
              <tr>
                <th className="px-4 py-2 text-right">رقم الشاحنة</th>
                <th className="px-4 py-2 text-right">اللوحة</th>
                <th className="px-4 py-2 text-right">السعة</th>
                <th className="px-4 py-2 text-right">النوع</th>
                <th className="px-4 py-2 text-center">الحالة</th>
                <th className="px-4 py-2 text-center">الإجراء</th>
              </tr>
            </thead>
            <tbody>
              {trucks.slice(0, 5).map((truck) => (
                <tr key={truck._id} className="border-t border-gray-700 hover:bg-gray-700">
                  <td className="px-4 py-3">{truck.truckNumber}</td>
                  <td className="px-4 py-3">{truck.plateNumber}</td>
                  <td className="px-4 py-3">{truck.capacity} طن</td>
                  <td className="px-4 py-3">{truck.type === 'refrigerated' ? 'مبردة' : 'عادية'}</td>
                  <td className="px-4 py-3 text-center">
                    <TruckStatusBadge status={truck.status} />
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

      {/* Trips List */}
      <div className="bg-gray-800 p-6 rounded-lg">
        <h2 className="text-xl font-bold text-white mb-4 flex items-center gap-2">
          <Calendar className="w-6 h-6" />
          الرحلات
        </h2>
        <div className="overflow-x-auto">
          <table className="w-full text-white text-sm">
            <thead className="bg-gray-700">
              <tr>
                <th className="px-4 py-2 text-right">رقم الرحلة</th>
                <th className="px-4 py-2 text-right">الشاحنة</th>
                <th className="px-4 py-2 text-right">السائق</th>
                <th className="px-4 py-2 text-right">من</th>
                <th className="px-4 py-2 text-right">إلى</th>
                <th className="px-4 py-2 text-center">الحالة</th>
              </tr>
            </thead>
            <tbody>
              {trips.map((trip) => (
                <tr key={trip._id} className="border-t border-gray-700 hover:bg-gray-700">
                  <td className="px-4 py-3">{trip.tripNumber}</td>
                  <td className="px-4 py-3">{trip.truck?.truckNumber || 'غير محدد'}</td>
                  <td className="px-4 py-3">{trip.driver?.name || 'غير محدد'}</td>
                  <td className="px-4 py-3">{trip.pickupLocation}</td>
                  <td className="px-4 py-3">{trip.deliveryLocation}</td>
                  <td className="px-4 py-3 text-center">
                    <TripStatusBadge status={trip.status} />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

function StatCard({ title, value, icon, color }) {
  const colors = {
    blue: 'bg-blue-600',
    green: 'bg-green-600',
    yellow: 'bg-yellow-600',
    dark: 'bg-gray-800'
  };

  return (
    <div className={`${colors[color]} p-6 rounded-lg text-white flex items-center gap-4`}>
      <div className="p-3 bg-white/20 rounded-lg">{icon}</div>
      <div>
        <h3 className="text-gray-100 mb-1">{title}</h3>
        <p className="text-3xl font-bold">{value}</p>
      </div>
    </div>
  );
}

function TruckStatusBadge({ status }) {
  const statusColors = {
    active: 'bg-green-500',
    maintenance: 'bg-yellow-500',
    inactive: 'bg-gray-500'
  };

  const statusLabels = {
    active: 'نشطة',
    maintenance: 'صيانة',
    inactive: 'غير نشطة'
  };

  return (
    <span className={`${statusColors[status] || 'bg-gray-500'} px-3 py-1 rounded text-xs`}>
      {statusLabels[status] || status}
    </span>
  );
}

function TripStatusBadge({ status }) {
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
