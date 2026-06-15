/**
 * ============================================
 * 👤 Client Dashboard - نظام إدهام
 * Edham Logistics - Client Dashboard
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';
import { Loader, AlertCircle, Plus, Package, FileText, MapPin } from 'lucide-react';

export default function ClientDashboard() {
  const [shipments, setShipments] = useState([]);
  const [invoices, setInvoices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchClientData();
  }, []);

  const fetchClientData = async () => {
    try {
      setLoading(true);
      const [shipmentsRes, invoicesRes] = await Promise.all([
        api.get('/shipments'),
        api.get('/invoices')
      ]);

      setShipments(shipmentsRes.data);
      setInvoices(invoicesRes.data);
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
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold text-white">لوحة العميل</h1>
        <button
          onClick={() => navigate('/shipments/new')}
          className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded flex items-center gap-2"
        >
          <Plus className="w-4 h-4" />
          شحنة جديدة
        </button>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <StatCard
          title="الشحنات النشطة"
          value={shipments.filter(s => s.status !== 'delivered').length}
          icon={<Package className="w-6 h-6" />}
          color="blue"
        />
        <StatCard
          title="الشحنات المكتملة"
          value={shipments.filter(s => s.status === 'delivered').length}
          icon={<Package className="w-6 h-6" />}
          color="green"
        />
        <StatCard
          title="الفواتير المستحقة"
          value={invoices.filter(i => i.status === 'unpaid').length}
          icon={<FileText className="w-6 h-6" />}
          color="yellow"
        />
        <StatCard
          title="إجمالي الفواتير"
          value={invoices.length}
          icon={<FileText className="w-6 h-6" />}
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
                <th className="px-4 py-2 text-right">من</th>
                <th className="px-4 py-2 text-right">إلى</th>
                <th className="px-4 py-2 text-center">الحالة</th>
                <th className="px-4 py-2 text-center">الإجراء</th>
              </tr>
            </thead>
            <tbody>
              {shipments.slice(0, 5).map((shipment) => (
                <tr key={shipment._id} className="border-t border-gray-700 hover:bg-gray-700">
                  <td className="px-4 py-3">{shipment.shipmentNumber}</td>
                  <td className="px-4 py-3">{shipment.description}</td>
                  <td className="px-4 py-3">{shipment.pickupLocation.city}</td>
                  <td className="px-4 py-3">{shipment.deliveryLocation.city}</td>
                  <td className="px-4 py-3 text-center">
                    <StatusBadge status={shipment.status} />
                  </td>
                  <td className="px-4 py-3 text-center">
                    <button
                      onClick={() => navigate(`/shipments/${shipment._id}`)}
                      className="text-blue-500 hover:underline"
                    >
                      عرض
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Recent Invoices */}
      <div className="bg-gray-800 p-6 rounded-lg">
        <h2 className="text-xl font-bold text-white mb-4">الفواتير الأخيرة</h2>
        <div className="overflow-x-auto">
          <table className="w-full text-white text-sm">
            <thead className="bg-gray-700">
              <tr>
                <th className="px-4 py-2 text-right">رقم الفاتورة</th>
                <th className="px-4 py-2 text-right">التاريخ</th>
                <th className="px-4 py-2 text-right">المبلغ</th>
                <th className="px-4 py-2 text-center">الحالة</th>
                <th className="px-4 py-2 text-center">الإجراء</th>
              </tr>
            </thead>
            <tbody>
              {invoices.slice(0, 5).map((invoice) => (
                <tr key={invoice._id} className="border-t border-gray-700 hover:bg-gray-700">
                  <td className="px-4 py-3">{invoice.invoiceNumber}</td>
                  <td className="px-4 py-3">{new Date(invoice.issueDate).toLocaleDateString('ar-SA')}</td>
                  <td className="px-4 py-3">{invoice.total} ريال</td>
                  <td className="px-4 py-3 text-center">
                    <InvoiceStatusBadge status={invoice.status} />
                  </td>
                  <td className="px-4 py-3 text-center">
                    <button
                      onClick={() => navigate(`/invoices/${invoice._id}`)}
                      className="text-blue-500 hover:underline"
                    >
                      عرض
                    </button>
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
