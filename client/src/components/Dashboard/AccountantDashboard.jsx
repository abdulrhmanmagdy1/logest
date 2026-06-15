/**
 * ============================================
 * 💰 Accountant Dashboard - نظام إدهام
 * Edham Logistics - Accountant Dashboard
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import { Loader, AlertCircle, FileText, DollarSign, TrendingUp, AlertTriangle } from 'lucide-react';

export default function AccountantDashboard() {
  const [metrics, setMetrics] = useState(null);
  const [recentInvoices, setRecentInvoices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const [metricsRes, invoicesRes] = await Promise.all([
        api.get('/invoices/statistics'),
        api.get('/invoices?limit=5')
      ]);

      setMetrics(metricsRes.data);
      setRecentInvoices(invoicesRes.data);
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
        <h1 className="text-3xl font-bold text-white">لوحة المحاسب</h1>
        <p className="text-gray-400 mt-1">إدارة الفواتير والمدفوعات</p>
      </div>

      {/* Key Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <MetricCard
          title="إجمالي الفواتير"
          value={metrics?.totalInvoices || 0}
          subtitle="الفاتورة النشطة"
          icon={<FileText className="w-6 h-6" />}
          color="blue"
        />
        <MetricCard
          title="الإيرادات"
          value={`${metrics?.totalRevenue || 0} ريال`}
          subtitle={`مدفوع: ${metrics?.totalPaid || 0}`}
          icon={<DollarSign className="w-6 h-6" />}
          color="green"
        />
        <MetricCard
          title="الديون المستحقة"
          value={`${metrics?.totalPending || 0} ريال`}
          subtitle={`${metrics?.unpaidCount || 0} فاتورة`}
          icon={<AlertTriangle className="w-6 h-6" />}
          color="yellow"
        />
        <MetricCard
          title="معدل التحصيل"
          value={`${metrics?.collectionRate || 0}%`}
          subtitle="من إجمالي الفواتير"
          icon={<TrendingUp className="w-6 h-6" />}
          color="dark"
        />
      </div>

      {/* Recent Invoices */}
      <div className="bg-gray-800 p-6 rounded-lg">
        <h2 className="text-xl font-bold text-white mb-4">الفواتير الأخيرة</h2>
        <div className="overflow-x-auto">
          <table className="w-full text-white text-sm">
            <thead className="bg-gray-700">
              <tr>
                <th className="px-4 py-2 text-right">رقم الفاتورة</th>
                <th className="px-4 py-2 text-right">العميل</th>
                <th className="px-4 py-2 text-right">التاريخ</th>
                <th className="px-4 py-2 text-right">المبلغ</th>
                <th className="px-4 py-2 text-right">المدفوع</th>
                <th className="px-4 py-2 text-center">الحالة</th>
                <th className="px-4 py-2 text-center">الإجراء</th>
              </tr>
            </thead>
            <tbody>
              {recentInvoices.map((invoice) => (
                <tr key={invoice._id} className="border-t border-gray-700 hover:bg-gray-700">
                  <td className="px-4 py-3">{invoice.invoiceNumber}</td>
                  <td className="px-4 py-3">{invoice.client?.name || 'غير محدد'}</td>
                  <td className="px-4 py-3">{new Date(invoice.issueDate).toLocaleDateString('ar-SA')}</td>
                  <td className="px-4 py-3">{invoice.total} ريال</td>
                  <td className="px-4 py-3">{invoice.paidAmount} ريال</td>
                  <td className="px-4 py-3 text-center">
                    <InvoiceStatusBadge status={invoice.status} />
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
          title="فاتورة جديدة"
          description="إنشاء فاتورة جديدة"
          icon={<FileText className="w-8 h-8" />}
          color="blue"
        />
        <QuickActionCard
          title="تسجيل دفعة"
          description="تسجيل دفعة جديدة"
          icon={<DollarSign className="w-8 h-8" />}
          color="green"
        />
        <QuickActionCard
          title="التقارير المالية"
          description="عرض التقارير المالية"
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

function InvoiceStatusBadge({ status }) {
  const statusColors = {
    paid: 'bg-green-500',
    unpaid: 'bg-red-500',
    partial: 'bg-yellow-500',
    complete: 'bg-blue-500'
  };

  const statusLabels = {
    paid: 'مدفوعة',
    unpaid: 'غير مدفوعة',
    partial: 'جزئي',
    complete: 'مكتملة'
  };

  return (
    <span className={`${statusColors[status] || 'bg-gray-500'} px-3 py-1 rounded text-xs`}>
      {statusLabels[status] || status}
    </span>
  );
}
