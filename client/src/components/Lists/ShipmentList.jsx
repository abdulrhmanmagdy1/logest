/**
 * ============================================
 * 📦 Shipment List Component - نظام إدهام
 * Edham Logistics - Shipment List
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import { Loader, AlertCircle, Package, Search, Filter, Plus } from 'lucide-react';

export default function ShipmentList() {
  const [shipments, setShipments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterStatus, setFilterStatus] = useState('all');
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  useEffect(() => {
    fetchShipments();
  }, [page, filterStatus]);

  const fetchShipments = async () => {
    try {
      setLoading(true);
      const params = {
        page,
        limit: 20,
        ...(filterStatus !== 'all' && { status: filterStatus })
      };
      const response = await api.get('/shipments', { params });
      setShipments(response.data.data);
      setTotalPages(response.data.pages || 1);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const filteredShipments = shipments.filter(shipment =>
    shipment.shipmentNumber?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    shipment.description?.toLowerCase().includes(searchTerm.toLowerCase())
  );

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
        <h1 className="text-3xl font-bold text-white">الشحنات</h1>
        <button className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded flex items-center gap-2">
          <Plus className="w-4 h-4" />
          شحنة جديدة
        </button>
      </div>

      {/* Search and Filter */}
      <div className="flex gap-4">
        <div className="flex-1 relative">
          <Search className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <input
            type="text"
            placeholder="بحث عن شحنة..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full bg-gray-800 text-white pr-10 pl-4 py-2 rounded border border-gray-700 focus:border-blue-500 outline-none"
          />
        </div>
        <select
          value={filterStatus}
          onChange={(e) => setFilterStatus(e.target.value)}
          className="bg-gray-800 text-white px-4 py-2 rounded border border-gray-700 focus:border-blue-500 outline-none"
        >
          <option value="all">جميع الحالات</option>
          <option value="pending">قيد الانتظار</option>
          <option value="assigned">مُسندة</option>
          <option value="in_transit">في الطريق</option>
          <option value="delivered">تم التسليم</option>
          <option value="cancelled">ملغاة</option>
        </select>
      </div>

      {/* Shipments Table */}
      <div className="bg-gray-800 rounded-lg overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-white text-sm">
            <thead className="bg-gray-700">
              <tr>
                <th className="px-4 py-3 text-right">رقم الشحنة</th>
                <th className="px-4 py-3 text-right">الوصف</th>
                <th className="px-4 py-3 text-right">من</th>
                <th className="px-4 py-3 text-right">إلى</th>
                <th className="px-4 py-3 text-right">السائق</th>
                <th className="px-4 py-3 text-right">الشاحنة</th>
                <th className="px-4 py-3 text-center">الحالة</th>
                <th className="px-4 py-3 text-right">التاريخ</th>
                <th className="px-4 py-3 text-center">الإجراء</th>
              </tr>
            </thead>
            <tbody>
              {filteredShipments.length === 0 ? (
                <tr>
                  <td colSpan="9" className="px-4 py-8 text-center text-gray-400">
                    لا توجد شحنات
                  </td>
                </tr>
              ) : (
                filteredShipments.map((shipment) => (
                  <tr key={shipment._id} className="border-t border-gray-700 hover:bg-gray-700">
                    <td className="px-4 py-3 font-semibold">{shipment.shipmentNumber}</td>
                    <td className="px-4 py-3">{shipment.description}</td>
                    <td className="px-4 py-3">{shipment.pickupLocation?.city}</td>
                    <td className="px-4 py-3">{shipment.deliveryLocation?.city}</td>
                    <td className="px-4 py-3">{shipment.driver?.name || 'غير محدد'}</td>
                    <td className="px-4 py-3">{shipment.truck?.truckNumber || 'غير محدد'}</td>
                    <td className="px-4 py-3 text-center">
                      <StatusBadge status={shipment.status} />
                    </td>
                    <td className="px-4 py-3">
                      {new Date(shipment.createdAt).toLocaleDateString('ar-SA')}
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

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex justify-center gap-2">
          <button
            onClick={() => setPage(p => Math.max(1, p - 1))}
            disabled={page === 1}
            className="bg-gray-700 hover:bg-gray-600 text-white px-4 py-2 rounded disabled:opacity-50"
          >
            السابق
          </button>
          <span className="text-white py-2">
            صفحة {page} من {totalPages}
          </span>
          <button
            onClick={() => setPage(p => Math.min(totalPages, p + 1))}
            disabled={page === totalPages}
            className="bg-gray-700 hover:bg-gray-600 text-white px-4 py-2 rounded disabled:opacity-50"
          >
            التالي
          </button>
        </div>
      )}
    </div>
  );
}

function StatusBadge({ status }) {
  const statusColors = {
    pending: 'bg-yellow-500',
    assigned: 'bg-blue-500',
    in_transit: 'bg-purple-500',
    delivered: 'bg-green-500',
    cancelled: 'bg-red-500',
    failed: 'bg-gray-500'
  };

  const statusLabels = {
    pending: 'قيد الانتظار',
    assigned: 'مُسندة',
    in_transit: 'في الطريق',
    delivered: 'تم التسليم',
    cancelled: 'ملغاة',
    failed: 'فشلت'
  };

  return (
    <span className={`${statusColors[status] || 'bg-gray-500'} px-3 py-1 rounded text-xs`}>
      {statusLabels[status] || status}
    </span>
  );
}
