/**
 * ============================================
 * ⚙️ Spare Parts Page - نظام إدهام
 * Edham Logistics - Spare Parts Management
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Loader, AlertCircle, Wrench, Package, DollarSign, Plus, Filter } from 'lucide-react';

export default function SparePartsPage() {
  const [parts, setParts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filterStatus, setFilterStatus] = useState('all');

  useEffect(() => {
    fetchParts();
  }, [filterStatus]);

  const fetchParts = async () => {
    try {
      setLoading(true);
      const params = filterStatus !== 'all' ? { status: filterStatus } : {};
      const response = await api.get('/spare-parts', { params });
      setParts(response.data.data);
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
          <h1 className="text-3xl font-bold text-white">قطع الغيار</h1>
          <p className="text-gray-400 mt-1">إدارة مخزون قطع الغيار</p>
        </div>
        <button className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded flex items-center gap-2">
          <Plus className="w-4 h-4" />
          قطعة جديدة
        </button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <StatCard title="إجمالي القطع" value="156" color="blue" />
        <StatCard title="منخفض المخزون" value="12" color="yellow" />
        <StatCard title="نفذ المخزون" value="5" color="red" />
        <StatCard title="قيمة المخزون" value="45,000 ريال" color="gold" />
      </div>

      {/* Filter */}
      <div className="flex gap-4 mb-6">
        <select
          value={filterStatus}
          onChange={(e) => setFilterStatus(e.target.value)}
          className="bg-gray-800 text-white px-4 py-2 rounded border border-gray-700 focus:border-blue-500 outline-none"
        >
          <option value="all">جميع الحالات</option>
          <option value="in_stock">متوفر</option>
          <option value="low_stock">منخفض</option>
          <option value="out_of_stock">نفذ</option>
        </select>
      </div>

      {/* Parts List */}
      <div className="bg-gray-800 rounded-lg overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-white text-sm">
            <thead className="bg-gray-700">
              <tr>
                <th className="px-4 py-3 text-right">رقم القطعة</th>
                <th className="px-4 py-3 text-right">الاسم</th>
                <th className="px-4 py-3 text-right">الفئة</th>
                <th className="px-4 py-3 text-right">الكمية</th>
                <th className="px-4 py-3 text-right">الحد الأدنى</th>
                <th className="px-4 py-3 text-right">السعر</th>
                <th className="px-4 py-3 text-right">المورد</th>
                <th className="px-4 py-3 text-center">الحالة</th>
                <th className="px-4 py-3 text-center">الإجراء</th>
              </tr>
            </thead>
            <tbody>
              {parts.length === 0 ? (
                <tr>
                  <td colSpan="9" className="px-4 py-8 text-center text-gray-400">
                    لا توجد قطع غيار
                  </td>
                </tr>
              ) : (
                parts.map((part) => (
                  <tr key={part._id} className="border-t border-gray-700 hover:bg-gray-700">
                    <td className="px-4 py-3 font-semibold">{part.partNumber}</td>
                    <td className="px-4 py-3">{part.name}</td>
                    <td className="px-4 py-3">{part.category}</td>
                    <td className="px-4 py-3">{part.quantity}</td>
                    <td className="px-4 py-3">{part.minQuantity}</td>
                    <td className="px-4 py-3">{part.price} ريال</td>
                    <td className="px-4 py-3">{part.supplier || 'غير محدد'}</td>
                    <td className="px-4 py-3 text-center">
                      <StockStatusBadge 
                        status={part.quantity <= 0 ? 'out_of_stock' : part.quantity <= part.minQuantity ? 'low_stock' : 'in_stock'} 
                      />
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
    red: 'bg-red-600',
    gold: 'bg-yellow-500'
  };

  return (
    <div className={`${colors[color]} p-6 rounded-lg text-white`}>
      <h3 className="text-gray-100 mb-2">{title}</h3>
      <p className="text-2xl font-bold">{value}</p>
    </div>
  );
}

function StockStatusBadge({ status }) {
  const statusColors = {
    in_stock: 'bg-green-500',
    low_stock: 'bg-yellow-500',
    out_of_stock: 'bg-red-500'
  };

  const statusLabels = {
    in_stock: 'متوفر',
    low_stock: 'منخفض',
    out_of_stock: 'نفذ'
  };

  return (
    <span className={`${statusColors[status] || 'bg-gray-500'} px-3 py-1 rounded text-xs`}>
      {statusLabels[status] || status}
    </span>
  );
}
