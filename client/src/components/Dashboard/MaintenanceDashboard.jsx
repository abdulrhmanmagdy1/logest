/**
 * ============================================
 * 🔧 Maintenance Dashboard - نظام إدهام
 * Edham Logistics - Maintenance Dashboard
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import { Loader, AlertCircle, Wrench, Calendar, AlertTriangle, CheckCircle } from 'lucide-react';

export default function MaintenanceDashboard() {
  const [maintenanceRecords, setMaintenanceRecords] = useState([]);
  const [alerts, setAlerts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchMaintenanceData();
  }, []);

  const fetchMaintenanceData = async () => {
    try {
      setLoading(true);
      const [recordsRes, alertsRes] = await Promise.all([
        api.get('/maintenance'),
        api.get('/maintenance/alerts')
      ]);

      setMaintenanceRecords(recordsRes.data);
      setAlerts(alertsRes.data);
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
        <h1 className="text-3xl font-bold text-white">لوحة الصيانة</h1>
        <p className="text-gray-400 mt-1">إدارة الصيانة وجدولة الزيوت</p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <StatCard
          title="إجمالي الصيانة"
          value={maintenanceRecords.length}
          icon={<Wrench className="w-6 h-6" />}
          color="blue"
        />
        <StatCard
          title="مجدولة"
          value={maintenanceRecords.filter(r => r.status === 'scheduled').length}
          icon={<Calendar className="w-6 h-6" />}
          color="yellow"
        />
        <StatCard
          title="قيد التنفيذ"
          value={maintenanceRecords.filter(r => r.status === 'in_progress').length}
          icon={<Wrench className="w-6 h-6" />}
          color="blue"
        />
        <StatCard
          title="مكتملة"
          value={maintenanceRecords.filter(r => r.status === 'completed').length}
          icon={<CheckCircle className="w-6 h-6" />}
          color="green"
        />
      </div>

      {/* Alerts */}
      {alerts.length > 0 && (
        <div className="bg-gray-800 p-6 rounded-lg border-l-4 border-red-500">
          <h2 className="text-xl font-bold text-white mb-4 flex items-center gap-2">
            <AlertTriangle className="w-6 h-6" />
            تنبيهات الصيانة
          </h2>
          <div className="space-y-3">
            {alerts.map((alert) => (
              <div key={alert._id} className="bg-gray-700 p-4 rounded flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <AlertTriangle className="w-5 h-5 text-yellow-500" />
                  <div>
                    <p className="text-white font-semibold">{alert.message}</p>
                    <p className="text-gray-400 text-sm">{alert.truck?.truckNumber}</p>
                  </div>
                </div>
                <button className="text-blue-500 hover:underline text-sm">عرض التفاصيل</button>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Maintenance Records */}
      <div className="bg-gray-800 p-6 rounded-lg">
        <h2 className="text-xl font-bold text-white mb-4 flex items-center gap-2">
          <Wrench className="w-6 h-6" />
          سجلات الصيانة
        </h2>
        <div className="overflow-x-auto">
          <table className="w-full text-white text-sm">
            <thead className="bg-gray-700">
              <tr>
                <th className="px-4 py-2 text-right">رقم السجل</th>
                <th className="px-4 py-2 text-right">الشاحنة</th>
                <th className="px-4 py-2 text-right">النوع</th>
                <th className="px-4 py-2 text-right">التاريخ المجدول</th>
                <th className="px-4 py-2 text-right">التكلفة المتوقعة</th>
                <th className="px-4 py-2 text-center">الحالة</th>
                <th className="px-4 py-2 text-center">الإجراء</th>
              </tr>
            </thead>
            <tbody>
              {maintenanceRecords.map((record) => (
                <tr key={record._id} className="border-t border-gray-700 hover:bg-gray-700">
                  <td className="px-4 py-3">{record.recordNumber}</td>
                  <td className="px-4 py-3">{record.truck?.truckNumber || 'غير محدد'}</td>
                  <td className="px-4 py-3">
                    {record.type === 'routine' ? 'روتينية' : 
                     record.type === 'repair' ? 'إصلاح' : 'تغيير زيت'}
                  </td>
                  <td className="px-4 py-3">
                    {new Date(record.scheduledDate).toLocaleDateString('ar-SA')}
                  </td>
                  <td className="px-4 py-3">{record.estimatedCost} ريال</td>
                  <td className="px-4 py-3 text-center">
                    <MaintenanceStatusBadge status={record.status} />
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

      {/* Quick Actions */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <QuickActionCard
          title="صيانة جديدة"
          description="جدولة صيانة جديدة"
          icon={<Wrench className="w-8 h-8" />}
          color="blue"
        />
        <QuickActionCard
          title="جدول الزيوت"
          description="عرض وإدارة جدول الزيوت"
          icon={<Calendar className="w-8 h-8" />}
          color="green"
        />
        <QuickActionCard
          title="قطع الغيار"
          description="إدارة مخزون قطع الغيار"
          icon={<Wrench className="w-8 h-8" />}
          color="gold"
        />
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

function QuickActionCard({ title, description, icon, color }) {
  const colors = {
    blue: 'bg-blue-600 hover:bg-blue-700',
    green: 'bg-green-600 hover:bg-green-700',
    gold: 'bg-yellow-600 hover:bg-yellow-700'
  };

  return (
    <button className={`${colors[color]} p-6 rounded-lg text-white text-left transition`}>
      <div className="mb-3">{icon}</div>
      <h3 className="text-lg font-bold mb-1">{title}</h3>
      <p className="text-sm text-gray-100 opacity-75">{description}</p>
    </button>
  );
}

function MaintenanceStatusBadge({ status }) {
  const statusColors = {
    scheduled: 'bg-blue-500',
    in_progress: 'bg-yellow-500',
    completed: 'bg-green-500',
    overdue: 'bg-red-500'
  };

  const statusLabels = {
    scheduled: 'مجدولة',
    in_progress: 'قيد التنفيذ',
    completed: 'مكتملة',
    overdue: 'متأخرة'
  };

  return (
    <span className={`${statusColors[status] || 'bg-gray-500'} px-3 py-1 rounded text-xs`}>
      {statusLabels[status] || status}
    </span>
  );
}
