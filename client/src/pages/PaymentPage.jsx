/**
 * ============================================
 * 💳 Payment Page - نظام إدهام
 * Edham Logistics - Payment Page
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Loader, AlertCircle, CreditCard, DollarSign, Calendar, CheckCircle, Plus, Filter } from 'lucide-react';

export default function PaymentPage() {
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filterStatus, setFilterStatus] = useState('all');

  useEffect(() => {
    fetchPayments();
  }, [filterStatus]);

  const fetchPayments = async () => {
    try {
      setLoading(true);
      const params = filterStatus !== 'all' ? { status: filterStatus } : {};
      const response = await api.get('/payments', { params });
      setPayments(response.data.data);
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
          <h1 className="text-3xl font-bold text-white">المدفوعات</h1>
          <p className="text-gray-400 mt-1">إدارة المدفوعات والفواتير</p>
        </div>
        <button className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded flex items-center gap-2">
          <Plus className="w-4 h-4" />
          دفعة جديدة
        </button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <StatCard title="إجمالي المدفوعات" value="45,000 ريال" color="green" />
        <StatCard title="المعلقة" value="8,500 ريال" color="yellow" />
        <StatCard title="المدفوعة اليوم" value="12,000 ريال" color="blue" />
        <StatCard title="المتأخرة" value="3,200 ريال" color="red" />
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
          <option value="completed">مكتملة</option>
          <option value="failed">فشلت</option>
          <option value="refunded">مستردة</option>
        </select>
      </div>

      {/* Payments List */}
      <div className="bg-gray-800 rounded-lg overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-white text-sm">
            <thead className="bg-gray-700">
              <tr>
                <th className="px-4 py-3 text-right">رقم الدفعة</th>
                <th className="px-4 py-3 text-right">الفاتورة</th>
                <th className="px-4 py-3 text-right">العميل</th>
                <th className="px-4 py-3 text-right">المبلغ</th>
                <th className="px-4 py-3 text-right">طريقة الدفع</th>
                <th className="px-4 py-3 text-right">التاريخ</th>
                <th className="px-4 py-3 text-center">الحالة</th>
                <th className="px-4 py-3 text-center">الإجراء</th>
              </tr>
            </thead>
            <tbody>
              {payments.length === 0 ? (
                <tr>
                  <td colSpan="8" className="px-4 py-8 text-center text-gray-400">
                    لا توجد مدفوعات
                  </td>
                </tr>
              ) : (
                payments.map((payment) => (
                  <tr key={payment._id} className="border-t border-gray-700 hover:bg-gray-700">
                    <td className="px-4 py-3 font-semibold">{payment.paymentNumber}</td>
                    <td className="px-4 py-3">{payment.invoice?.invoiceNumber || 'غير محدد'}</td>
                    <td className="px-4 py-3">{payment.client?.name || 'غير محدد'}</td>
                    <td className="px-4 py-3">{payment.amount} ريال</td>
                    <td className="px-4 py-3">
                      <PaymentMethodBadge method={payment.method} />
                    </td>
                    <td className="px-4 py-3">
                      {new Date(payment.createdAt).toLocaleDateString('ar-SA')}
                    </td>
                    <td className="px-4 py-3 text-center">
                      <PaymentStatusBadge status={payment.status} />
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
    red: 'bg-red-600'
  };

  return (
    <div className={`${colors[color]} p-6 rounded-lg text-white`}>
      <h3 className="text-gray-100 mb-2">{title}</h3>
      <p className="text-2xl font-bold">{value}</p>
    </div>
  );
}

function PaymentMethodBadge({ method }) {
  const methodLabels = {
    cash: 'نقداً',
    card: 'بطاقة',
    bank_transfer: 'تحويل بنكي',
    stripe: 'Stripe',
    check: 'شيك'
  };

  return (
    <span className="bg-gray-600 px-3 py-1 rounded text-xs">
      {methodLabels[method] || method}
    </span>
  );
}

function PaymentStatusBadge({ status }) {
  const statusColors = {
    pending: 'bg-yellow-500',
    completed: 'bg-green-500',
    failed: 'bg-red-500',
    refunded: 'bg-purple-500',
    processing: 'bg-blue-500'
  };

  const statusLabels = {
    pending: 'قيد الانتظار',
    completed: 'مكتملة',
    failed: 'فشلت',
    refunded: 'مستردة',
    processing: 'قيد المعالجة'
  };

  return (
    <span className={`${statusColors[status] || 'bg-gray-500'} px-3 py-1 rounded text-xs`}>
      {statusLabels[status] || status}
    </span>
  );
}
