/**
 * ============================================
 * 📊 Admin Dashboard - نظام إدهام
 * Edham Logistics - Professional Dashboard
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import { LineChart, Line, BarChart, Bar, PieChart, Pie, Cell, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import api from '../../services/api';
import { Loader, AlertCircle } from 'lucide-react';

export default function AdminDashboard() {
  const [metrics, setMetrics] = useState(null);
  const [monthlyData, setMonthlyData] = useState([]);
  const [driverPerformance, setDriverPerformance] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [timeRange, setTimeRange] = useState('month');

  useEffect(() => {
    fetchDashboardData();
  }, [timeRange]);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const [metricsRes, monthlyRes, driverRes] = await Promise.all([
        api.get('/analytics/dashboard', { params: { timeRange } }),
        api.get('/analytics/monthly-report'),
        api.get('/analytics/driver-performance')
      ]);

      setMetrics(metricsRes.data);
      setMonthlyData(monthlyRes.data);
      setDriverPerformance(driverRes.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Loader className="animate-spin w-8 h-8 text-red-600" />
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

  const COLORS = ['#0099D8', '#10B981', '#D4AF37', '#003D5C'];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold text-white">لوحة التحكم</h1>
        <select
          value={timeRange}
          onChange={(e) => setTimeRange(e.target.value)}
          className="bg-gray-700 text-white px-4 py-2 rounded border border-gray-600"
        >
          <option value="week">هذا الأسبوع</option>
          <option value="month">هذا الشهر</option>
          <option value="year">هذه السنة</option>
        </select>
      </div>

      {/* Key Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <MetricCard
          title="إجمالي الشحنات"
          value={metrics?.data?.shipments?.total || 0}
          subtitle={`${metrics?.data?.shipments?.completionRate || 0}% مكتملة`}
          color="blue"
        />
        <MetricCard
          title="الإيرادات"
          value={`${metrics?.data?.revenue?.totalRevenue || 0} ريال`}
          subtitle={`${metrics?.data?.revenue?.totalPaid || 0} ريال مدفوع`}
          color="gold"
        />
        <MetricCard
          title="الشاحنات النشطة"
          value={metrics?.data?.fleet?.active || 0}
          subtitle={`${metrics?.data?.fleet?.utilization || 0}% استخدام`}
          color="green"
        />
        <MetricCard
          title="معدل التسليم في الوقت"
          value={`${metrics?.data?.delivery?.onTimeRate || 0}%`}
          subtitle={`متوسط ${metrics?.data?.delivery?.avgTime || 0} ساعة`}
          color="dark"
        />
      </div>

      {/* Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Shipments Trend */}
        <div className="bg-gray-800 p-6 rounded-lg">
          <h2 className="text-xl font-bold text-white mb-4">اتجاه الشحنات</h2>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={monthlyData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#4B5563" />
              <XAxis dataKey="month" stroke="#9CA3AF" />
              <YAxis stroke="#9CA3AF" />
              <Tooltip
                contentStyle={{ backgroundColor: '#1F2937', border: 'none' }}
                labelStyle={{ color: '#FFF' }}
              />
              <Legend />
              <Line
                type="monotone"
                dataKey="total"
                stroke="#0099D8"
                name="إجمالي"
                strokeWidth={2}
              />
              <Line
                type="monotone"
                dataKey="completed"
                stroke="#10B981"
                name="مكتملة"
                strokeWidth={2}
              />
            </LineChart>
          </ResponsiveContainer>
        </div>

        {/* Revenue Chart */}
        <div className="bg-gray-800 p-6 rounded-lg">
          <h2 className="text-xl font-bold text-white mb-4">الإيرادات الشهرية</h2>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={monthlyData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#4B5563" />
              <XAxis dataKey="month" stroke="#9CA3AF" />
              <YAxis stroke="#9CA3AF" />
              <Tooltip
                contentStyle={{ backgroundColor: '#1F2937', border: 'none' }}
                labelStyle={{ color: '#FFF' }}
              />
              <Bar dataKey="revenue" fill="#0099D8" name="الإيرادات" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Driver Performance Table */}
      <div className="bg-gray-800 p-6 rounded-lg">
        <h2 className="text-xl font-bold text-white mb-4">أداء السائقين الأفضل</h2>
        <div className="overflow-x-auto">
          <table className="w-full text-white text-sm">
            <thead className="bg-gray-700">
              <tr>
                <th className="px-4 py-2 text-right">اسم السائق</th>
                <th className="px-4 py-2 text-center">عدد الرحلات</th>
                <th className="px-4 py-2 text-center">متوسط الوقت</th>
                <th className="px-4 py-2 text-center">معدل التسليم</th>
                <th className="px-4 py-2 text-center">التقييم</th>
              </tr>
            </thead>
            <tbody>
              {driverPerformance?.slice(0, 10).map((driver) => (
                <tr key={driver.driverId} className="border-t border-gray-700 hover:bg-gray-700">
                  <td className="px-4 py-3">{driver.driverName}</td>
                  <td className="px-4 py-3 text-center">{driver.totalTrips}</td>
                  <td className="px-4 py-3 text-center">{driver.avgDeliveryTime} ساعة</td>
                  <td className="px-4 py-3 text-center">{driver.onTimeRate}%</td>
                  <td className="px-4 py-3 text-center">
                    <span className="bg-green-600 px-3 py-1 rounded">
                      ⭐ {driver.rating.toFixed(1)}
                    </span>
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

function MetricCard({ title, value, subtitle, color }) {
  const colors = {
    blue: 'bg-blue-600',
    gold: 'bg-yellow-500',
    green: 'bg-green-600',
    dark: 'bg-gray-800'
  };

  return (
    <div className={`${colors[color]} p-6 rounded-lg text-white`}>
      <h3 className="text-gray-100 mb-2">{title}</h3>
      <p className="text-3xl font-bold mb-1">{value}</p>
      <p className="text-sm text-gray-100 opacity-75">{subtitle}</p>
    </div>
  );
}
