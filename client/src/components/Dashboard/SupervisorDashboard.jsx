/**
 * ============================================
 * 👨‍💼 Supervisor Dashboard - نظام إدهام
 * Edham Logistics - Supervisor Dashboard
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import { Loader, AlertCircle, Users, Truck, Package, TrendingUp } from 'lucide-react';

export default function SupervisorDashboard() {
  const [metrics, setMetrics] = useState(null);
  const [recentShipments, setRecentShipments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const [metricsRes, shipmentsRes] = await Promise.all([
        api.get('/analytics/dashboard'),
        api.get('/shipments?limit=5')
      ]);

      setMetrics(metricsRes.data);
      setRecentShipments(shipmentsRes.data);
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
        <h1 className="text-3xl font-bold text-white">لوحة المشرف</h1>
        <p className="text-gray-400 mt-1">نظرة عامة على العمليات</p>
      </div>

      {/* Key Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <MetricCard
          title="إجمالي الشحنات"
          value={metrics?.data?.shipments?.total || 0}
          subtitle={`${metrics?.data?.shipments?.completionRate || 0}% مكتملة`}
          icon={<Package className="w-6 h-6" />}
          color="blue"
        />
        <MetricCard
          title="الشاحنات النشطة"
          value={metrics?.data?.fleet?.active || 0}
          subtitle={`${metrics?.data?.fleet?.utilization || 0}% استخدام`}
          icon={<Truck className="w-6 h-6" />}
          color="green"
        />
        <MetricCard
          title="المستخدمين"
          value={metrics?.data?.users?.total || 0}
          subtitle={`${metrics?.data?.users?.active || 0} نشط`}
          icon={<Users className="w-6 h-6" />}
          color="gold"
        />
        <MetricCard
          title="الإيرادات"
          value={`${metrics?.data?.revenue?.totalRevenue || 0} ريال`}
          subtitle={`مدفوع: ${metrics?.data?.revenue?.totalPaid || 0}`}
          icon={<TrendingUp className="w-6 h-6" />}
          color="dark"
        />
      </div>

      {/* Recent Shipments */}
      <div className="bg-gray-800 p-6 rounded-lg">
        <h2 className="text-xl font-bold text-white mb-4">الشحنات الأخيرة</h2>
        <div className="overflow-x-auto">
          <table className="w-full text-white text-sm">
            <thead className="bg-gray-700">
              <tr>
                <th className="px-4 py-2 text-right">رقم الشحنة</th>
                <th className="px-4 py-2 text-right">الوصف</th>
                <th className="px-4 py-2 text-right">السائق</th>
                <th className="px-4 py-2 text-right">الشاحنة</th>
                <th className="px-4 py-2 text-center">الحالة</th>
                <th className="px-4 py-2 text-center">الإجراء</th>
              </tr>
            </thead>
            <tbody>
              {recentShipments.map((shipment) => (
                <tr key={shipment._id} className="border-t border-gray-700 hover:bg-gray-700">
                  <td className="px-4 py-3">{shipment.shipmentNumber}</td>
                  <td className="px-4 py-3">{shipment.description}</td>
                  <td className="px-4 py-3">{shipment.driver?.name || 'غير محدد'}</td>
                  <td className="px-4 py-3">{shipment.truck?.truckNumber || 'غير محدد'}</td>
                  <td className="px-4 py-3 text-center">
                    <StatusBadge status={shipment.status} />
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
          title="شحنة جديدة"
          description="إنشاء شحنة جديدة"
          icon={<Package className="w-8 h-8" />}
          color="blue"
        />
        <QuickActionCard
          title="إدارة الشاحنات"
          description="عرض وإدارة الأسطول"
          icon={<Truck className="w-8 h-8" />}
          color="green"
        />
        <QuickActionCard
          title="التقارير"
          description="عرض التقارير والتحليلات"
          icon={<TrendingUp className="w-8 h-8" />}
          color="gold"
        />
      </div>
    </div>
  );
}

function MetricCard({ title, value, subtitle, icon, color }) {
  const colors = {
    blue: 'bg-blue-600',
    green: 'bg-green-600',
    gold: 'bg-yellow-600',
    dark: 'bg-gray-800'
  };

  return (
    <div className={`${colors[color]} p-6 rounded-lg text-white`}>
      <div className="flex items-center justify-between mb-2">
        <h3 className="text-gray-100">{title}</h3>
        {icon}
      </div>
      <p className="text-3xl font-bold mb-1">{value}</p>
      <p className="text-sm text-gray-100 opacity-75">{subtitle}</p>
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

function StatusBadge({ status }) {
  const statusColors = {
    pending: 'bg-yellow-500',
    processing: 'bg-blue-500',
    in_transit: 'bg-purple-500',
    delivered: 'bg-green-500',
    cancelled: 'bg-red-500',
    delayed: 'bg-gray-500'
  };

  const statusLabels = {
    pending: 'قيد الانتظار',
    processing: 'قيد التجهيز',
    in_transit: 'في الطريق',
    delivered: 'تم التسليم',
    cancelled: 'ملغي',
    delayed: 'مؤجل'
  };

  return (
    <span className={`${statusColors[status] || 'bg-gray-500'} px-3 py-1 rounded text-xs`}>
      {statusLabels[status] || status}
    </span>
  );
}
