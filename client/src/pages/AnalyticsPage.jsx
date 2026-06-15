/**
 * ============================================
 * 📊 Analytics Page - نظام إدهام
 * Edham Logistics - Analytics & Reports Page
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Loader, AlertCircle, TrendingUp, Package, Truck, DollarSign, Download, Calendar } from 'lucide-react';

export default function AnalyticsPage() {
  const [metrics, setMetrics] = useState(null);
  const [monthlyReport, setMonthlyReport] = useState(null);
  const [timeRange, setTimeRange] = useState('month');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchAnalyticsData();
  }, [timeRange]);

  const fetchAnalyticsData = async () => {
    try {
      setLoading(true);
      const [metricsRes, reportRes] = await Promise.all([
        api.get('/analytics/dashboard', { params: { timeRange } }),
        api.get('/analytics/monthly-report')
      ]);

      setMetrics(metricsRes.data);
      setMonthlyReport(reportRes.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const exportReport = async (format) => {
    try {
      const response = await api.get(`/analytics/export?format=${format}`, {
        responseType: 'blob'
      });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `report.${format}`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      console.error('Export error:', err);
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
          <h1 className="text-3xl font-bold text-white">التحليلات والتقارير</h1>
          <p className="text-gray-400 mt-1">نظرة شاملة على أداء النظام</p>
        </div>
        <div className="flex gap-2">
          <select
            value={timeRange}
            onChange={(e) => setTimeRange(e.target.value)}
            className="bg-gray-700 text-white px-4 py-2 rounded border border-gray-600 focus:border-blue-500 outline-none"
          >
            <option value="week">آخر أسبوع</option>
            <option value="month">آخر شهر</option>
            <option value="quarter">آخر ربع سنة</option>
            <option value="year">آخر سنة</option>
          </select>
          <button
            onClick={() => exportReport('csv')}
            className="bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-4 rounded flex items-center gap-2"
          >
            <Download className="w-4 h-4" />
            CSV
          </button>
          <button
            onClick={() => exportReport('pdf')}
            className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded flex items-center gap-2"
          >
            <Download className="w-4 h-4" />
            PDF
          </button>
        </div>
      </div>

      {/* Key Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <MetricCard
          title="إجمالي الشحنات"
          value={metrics?.data?.shipments?.total || 0}
          subtitle={`${metrics?.data?.shipments?.completionRate || 0}% مكتملة`}
          icon={<Package className="w-6 h-6" />}
          color="blue"
          trend="+12%"
        />
        <MetricCard
          title="الإيرادات"
          value={`${metrics?.data?.revenue?.totalRevenue || 0} ريال`}
          subtitle={`مدفوع: ${metrics?.data?.revenue?.totalPaid || 0}`}
          icon={<DollarSign className="w-6 h-6" />}
          color="green"
          trend="+8%"
        />
        <MetricCard
          title="الأسطول النشط"
          value={metrics?.data?.fleet?.active || 0}
          subtitle={`${metrics?.data?.fleet?.utilization || 0}% استخدام`}
          icon={<Truck className="w-6 h-6" />}
          color="gold"
          trend="+5%"
        />
        <MetricCard
          title="معدل التسليم في الوقت"
          value={`${metrics?.data?.delivery?.onTimeRate || 0}%`}
          subtitle="معدل الالتزام بالمواعيد"
          icon={<TrendingUp className="w-6 h-6" />}
          color="dark"
          trend="+3%"
        />
      </div>

      {/* Charts Section */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
        {/* Shipments by Status */}
        <div className="bg-gray-800 p-6 rounded-lg">
          <h2 className="text-xl font-bold text-white mb-4">الشحنات حسب الحالة</h2>
          <div className="space-y-3">
            <ProgressBar label="قيد الانتظار" value={30} color="yellow" />
            <ProgressBar label="قيد التجهيز" value={20} color="blue" />
            <ProgressBar label="في الطريق" value={25} color="purple" />
            <ProgressBar label="تم التسليم" value={20} color="green" />
            <ProgressBar label="ملغاة" value={5} color="red" />
          </div>
        </div>

        {/* Revenue by Month */}
        <div className="bg-gray-800 p-6 rounded-lg">
          <h2 className="text-xl font-bold text-white mb-4">الإيرادات الشهرية</h2>
          <div className="space-y-3">
            <MonthlyBar month="يناير" value={45000} />
            <MonthlyBar month="فبراير" value={52000} />
            <MonthlyBar month="مارس" value={48000} />
            <MonthlyBar month="أبريل" value={61000} />
            <MonthlyBar month="مايو" value={55000} />
            <MonthlyBar month="يونيو" value={67000} />
          </div>
        </div>
      </div>

      {/* Shipments by City */}
      <div className="bg-gray-800 p-6 rounded-lg mb-6">
        <h2 className="text-xl font-bold text-white mb-4">الشحنات حسب المدينة</h2>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <CityCard city="الرياض" count={150} />
          <CityCard city="جدة" count={120} />
          <CityCard city="مكة المكرمة" count={95} />
          <CityCard city="المدينة المنورة" count={80} />
          <CityCard city="الدمام" count={65} />
          <CityCard city="الخبر" count={55} />
          <CityCard city="تبوك" count={40} />
          <CityCard city="الطائف" count={35} />
        </div>
      </div>

      {/* Driver Performance */}
      <div className="bg-gray-800 p-6 rounded-lg mb-6">
        <h2 className="text-xl font-bold text-white mb-4">أداء السائقين</h2>
        <div className="overflow-x-auto">
          <table className="w-full text-white text-sm">
            <thead className="bg-gray-700">
              <tr>
                <th className="px-4 py-2 text-right">السائق</th>
                <th className="px-4 py-2 text-center">الرحلات</th>
                <th className="px-4 py-2 text-center">المكتملة</th>
                <th className="px-4 py-2 text-center">التقييم</th>
                <th className="px-4 py-2 text-center">الوقت المتوسط</th>
              </tr>
            </thead>
            <tbody>
              <tr className="border-t border-gray-700">
                <td className="px-4 py-3">أحمد محمد</td>
                <td className="px-4 py-3 text-center">45</td>
                <td className="px-4 py-3 text-center">42</td>
                <td className="px-4 py-3 text-center">4.8</td>
                <td className="px-4 py-3 text-center">3.5 ساعة</td>
              </tr>
              <tr className="border-t border-gray-700">
                <td className="px-4 py-3">خالد عبدالله</td>
                <td className="px-4 py-3 text-center">38</td>
                <td className="px-4 py-3 text-center">36</td>
                <td className="px-4 py-3 text-center">4.7</td>
                <td className="px-4 py-3 text-center">4.0 ساعة</td>
              </tr>
              <tr className="border-t border-gray-700">
                <td className="px-4 py-3">سعود علي</td>
                <td className="px-4 py-3 text-center">52</td>
                <td className="px-4 py-3 text-center">48</td>
                <td className="px-4 py-3 text-center">4.9</td>
                <td className="px-4 py-3 text-center">3.2 ساعة</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      {/* Recent Activity */}
      <div className="bg-gray-800 p-6 rounded-lg">
        <h2 className="text-xl font-bold text-white mb-4 flex items-center gap-2">
          <Calendar className="w-6 h-6" />
          النشاط الأخير
        </h2>
        <div className="space-y-3">
          <ActivityItem
            action="شحنة جديدة"
            details="تم إنشاء الشحنة #12345"
            time="منذ 5 دقائق"
            type="shipment"
          />
          <ActivityItem
            action="رحلة مكتملة"
            details="تم إكمال الرحلة #TRIP789"
            time="منذ 15 دقيقة"
            type="trip"
          />
          <ActivityItem
            action="فاتورة مدفوعة"
            details="تم دفع الفاتورة #INV456"
            time="منذ 30 دقيقة"
            type="invoice"
          />
          <ActivityItem
            action="صيانة مجدولة"
            details="تم جدولة صيانة للشاحنة #TRK001"
            time="منذ ساعة"
            type="maintenance"
          />
        </div>
      </div>
    </div>
  );
}

function MetricCard({ title, value, subtitle, icon, color, trend }) {
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
      <p className="text-sm text-gray-100 opacity-75 mb-2">{subtitle}</p>
      <span className="text-xs bg-white/20 px-2 py-1 rounded">{trend}</span>
    </div>
  );
}

function ProgressBar({ label, value, color }) {
  const colors = {
    yellow: 'bg-yellow-500',
    blue: 'bg-blue-500',
    purple: 'bg-purple-500',
    green: 'bg-green-500',
    red: 'bg-red-500'
  };

  return (
    <div>
      <div className="flex justify-between text-white mb-1">
        <span>{label}</span>
        <span>{value}%</span>
      </div>
      <div className="w-full bg-gray-700 rounded-full h-2">
        <div
          className={`${colors[color]} h-2 rounded-full transition-all`}
          style={{ width: `${value}%` }}
        />
      </div>
    </div>
  );
}

function MonthlyBar({ month, value }) {
  const maxValue = 70000;
  const percentage = (value / maxValue) * 100;

  return (
    <div>
      <div className="flex justify-between text-white mb-1">
        <span>{month}</span>
        <span>{value.toLocaleString()} ريال</span>
      </div>
      <div className="w-full bg-gray-700 rounded-full h-2">
        <div
          className="bg-blue-500 h-2 rounded-full transition-all"
          style={{ width: `${percentage}%` }}
        />
      </div>
    </div>
  );
}

function CityCard({ city, count }) {
  return (
    <div className="bg-gray-700 p-4 rounded-lg text-center">
      <p className="text-white font-semibold mb-1">{city}</p>
      <p className="text-2xl font-bold text-blue-500">{count}</p>
      <p className="text-gray-400 text-sm">شحنة</p>
    </div>
  );
}

function ActivityItem({ action, details, time, type }) {
  const typeColors = {
    shipment: 'bg-blue-500',
    trip: 'bg-green-500',
    invoice: 'bg-yellow-500',
    maintenance: 'bg-purple-500'
  };

  return (
    <div className="flex items-center gap-4 p-3 bg-gray-700 rounded">
      <div className={`w-2 h-2 rounded-full ${typeColors[type] || 'bg-gray-500'}`} />
      <div className="flex-1">
        <p className="text-white font-semibold">{action}</p>
        <p className="text-gray-400 text-sm">{details}</p>
      </div>
      <p className="text-gray-500 text-sm">{time}</p>
    </div>
  );
}
