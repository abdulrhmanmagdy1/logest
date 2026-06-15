/**
 * ============================================
 * 📄 Invoice Detail Component - نظام إدهام
 * Edham Logistics - Invoice Detail
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import api from '../../services/api';
import { Loader, AlertCircle, FileText, DollarSign, Calendar, User, Download, Edit, Trash2, CheckCircle, Clock } from 'lucide-react';

export default function InvoiceDetail() {
  const { id } = useParams();
  const [invoice, setInvoice] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchInvoice();
  }, [id]);

  const fetchInvoice = async () => {
    try {
      setLoading(true);
      const response = await api.get(`/invoices/${id}`);
      setInvoice(response.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const downloadPDF = async () => {
    try {
      const response = await api.get(`/invoices/${id}/pdf`, {
        responseType: 'blob'
      });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `invoice-${invoice.invoiceNumber}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (err) {
      console.error('Download error:', err);
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

  if (!invoice) {
    return (
      <div className="bg-gray-800 p-8 rounded-lg text-center">
        <FileText className="w-16 h-16 text-gray-600 mx-auto mb-4" />
        <p className="text-gray-400">الفاتورة غير موجودة</p>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto p-6">
      {/* Header */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-white">تفاصيل الفاتورة</h1>
          <p className="text-gray-400 mt-1">{invoice.invoiceNumber}</p>
        </div>
        <div className="flex gap-2">
          <button
            onClick={downloadPDF}
            className="bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-4 rounded flex items-center gap-2"
          >
            <Download className="w-4 h-4" />
            PDF
          </button>
          <button className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded flex items-center gap-2">
            <Edit className="w-4 h-4" />
            تعديل
          </button>
          <button className="bg-red-600 hover:bg-red-700 text-white font-bold py-2 px-4 rounded flex items-center gap-2">
            <Trash2 className="w-4 h-4" />
            حذف
          </button>
        </div>
      </div>

      {/* Status Badge */}
      <div className="bg-gray-800 p-6 rounded-lg mb-6">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-xl font-bold text-white mb-2">الحالة</h2>
            <InvoiceStatusBadge status={invoice.status} />
          </div>
          <div className="text-right">
            <p className="text-gray-400 text-sm">تاريخ الإصدار</p>
            <p className="text-white font-semibold">
              {new Date(invoice.issueDate).toLocaleDateString('ar-SA')}
            </p>
          </div>
        </div>
      </div>

      {/* Invoice Summary */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
        <SummaryCard
          title="إجمالي الفاتورة"
          value={`${invoice.total} ريال`}
          icon={<DollarSign className="w-6 h-6" />}
          color="blue"
        />
        <SummaryCard
          title="المبلغ المدفوع"
          value={`${invoice.paidAmount} ريال`}
          icon={<CheckCircle className="w-6 h-6" />}
          color="green"
        />
        <SummaryCard
          title="المبلغ المتبقي"
          value={`${invoice.balanceDue} ريال`}
          icon={<Clock className="w-6 h-6" />}
          color="yellow"
        />
      </div>

      {/* Client Info */}
      <div className="bg-gray-800 p-6 rounded-lg mb-6">
        <h3 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
          <User className="w-5 h-5" />
          معلومات العميل
        </h3>
        {invoice.client ? (
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-gray-300">
            <div>
              <p className="text-gray-400 text-sm">الاسم</p>
              <p className="text-white font-semibold">{invoice.client.name}</p>
            </div>
            <div>
              <p className="text-gray-400 text-sm">البريد</p>
              <p className="text-white">{invoice.client.email}</p>
            </div>
            <div>
              <p className="text-gray-400 text-sm">الهاتف</p>
              <p className="text-white">{invoice.client.phone}</p>
            </div>
            <div>
              <p className="text-gray-400 text-sm">المدينة</p>
              <p className="text-white">{invoice.client.city}</p>
            </div>
          </div>
        ) : (
          <p className="text-gray-400">معلومات العميل غير متوفرة</p>
        )}
      </div>

      {/* Invoice Items */}
      <div className="bg-gray-800 p-6 rounded-lg mb-6">
        <h3 className="text-lg font-bold text-white mb-4">بنود الفاتورة</h3>
        <div className="overflow-x-auto">
          <table className="w-full text-white text-sm">
            <thead className="bg-gray-700">
              <tr>
                <th className="px-4 py-3 text-right">الوصف</th>
                <th className="px-4 py-3 text-center">الكمية</th>
                <th className="px-4 py-3 text-right">السعر</th>
                <th className="px-4 py-3 text-right">الإجمالي</th>
              </tr>
            </thead>
            <tbody>
              {invoice.items?.map((item, index) => (
                <tr key={index} className="border-t border-gray-700">
                  <td className="px-4 py-3">{item.description}</td>
                  <td className="px-4 py-3 text-center">{item.quantity}</td>
                  <td className="px-4 py-3">{item.price} ريال</td>
                  <td className="px-4 py-3">{item.total} ريال</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Payment History */}
      <div className="bg-gray-800 p-6 rounded-lg mb-6">
        <h3 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
          <Calendar className="w-5 h-5" />
          سجل الدفعات
        </h3>
        {invoice.payments?.length > 0 ? (
          <div className="space-y-3">
            {invoice.payments.map((payment, index) => (
              <div key={index} className="flex items-center justify-between p-3 bg-gray-700 rounded">
                <div>
                  <p className="text-white font-semibold">{payment.amount} ريال</p>
                  <p className="text-gray-400 text-sm">{payment.method}</p>
                </div>
                <p className="text-gray-400 text-sm">
                  {new Date(payment.date).toLocaleDateString('ar-SA')}
                </p>
              </div>
            ))}
          </div>
        ) : (
          <p className="text-gray-400">لا توجد دفعات مسجلة</p>
        )}
      </div>

      {/* Due Date */}
      <div className="bg-gray-800 p-6 rounded-lg">
        <h3 className="text-lg font-bold text-white mb-4">معلومات الاستحقاق</h3>
        <div className="grid grid-cols-2 gap-4 text-gray-300">
          <div>
            <p className="text-gray-400 text-sm">تاريخ الاستحقاق</p>
            <p className="text-white font-semibold">
              {invoice.dueDate ? new Date(invoice.dueDate).toLocaleDateString('ar-SA') : 'غير محدد'}
            </p>
          </div>
          <div>
            <p className="text-gray-400 text-sm">طريقة الدفع</p>
            <p className="text-white font-semibold">{invoice.paymentMethod || 'غير محدد'}</p>
          </div>
        </div>
      </div>
    </div>
  );
}

function InvoiceStatusBadge({ status }) {
  const statusColors = {
    pending: 'bg-yellow-500',
    sent: 'bg-blue-500',
    paid: 'bg-green-500',
    partial: 'bg-purple-500',
    overdue: 'bg-red-500',
    cancelled: 'bg-gray-500'
  };

  const statusLabels = {
    pending: 'قيد الانتظار',
    sent: 'مرسلة',
    paid: 'مدفوعة',
    partial: 'جزئي',
    overdue: 'متأخرة',
    cancelled: 'ملغاة'
  };

  return (
    <span className={`${statusColors[status] || 'bg-gray-500'} px-4 py-2 rounded text-lg font-semibold`}>
      {statusLabels[status] || status}
    </span>
  );
}

function SummaryCard({ title, value, icon, color }) {
  const colors = {
    blue: 'bg-blue-600',
    green: 'bg-green-600',
    yellow: 'bg-yellow-600'
  };

  return (
    <div className={`${colors[color]} p-6 rounded-lg text-white`}>
      <div className="flex items-center gap-2 mb-2">
        {icon}
        <h3 className="text-gray-100">{title}</h3>
      </div>
      <p className="text-2xl font-bold">{value}</p>
    </div>
  );
}
