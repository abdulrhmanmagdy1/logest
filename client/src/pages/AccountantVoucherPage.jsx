/**
 * ============================================
 * 💰 Accountant Voucher Page - نظام إدهام
 * Edham Logistics - Voucher Management Interface
 * ============================================
 */

import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  FileText, DollarSign, TrendingUp, TrendingDown, Users,
  Plus, Search, Filter, Download, Eye, Edit, Trash2,
  CheckCircle, XCircle, Clock, AlertCircle, Receipt,
  ArrowRightLeft, BarChart3, Calendar, Printer
} from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { useNotification } from "../context/NotificationContext";
import api from "../services/api";

const AccountantVoucherPage = () => {
  const { user } = useAuth();
  const { showToast } = useNotification();

  const [vouchers, setVouchers] = useState([]);
  const [clients, setClients] = useState([]);
  const [statistics, setStatistics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [modalType, setModalType] = useState('receipt');
  const [selectedVoucher, setSelectedVoucher] = useState(null);
  const [filters, setFilters] = useState({
    type: '',
    status: '',
    startDate: '',
    endDate: '',
    clientId: ''
  });
  const [searchTerm, setSearchTerm] = useState('');

  // Form data
  const [formData, setFormData] = useState({
    clientId: '',
    amount: '',
    description: '',
    paymentMethod: 'cash',
    referenceNumber: '',
    notes: '',
    fromClientId: '',
    toClientId: ''
  });

  useEffect(() => {
    fetchData();
  }, [filters, searchTerm]);

  const fetchData = async () => {
    try {
      setLoading(true);
      const params = new URLSearchParams();
      
      Object.entries(filters).forEach(([key, value]) => {
        if (value) params.append(key, value);
      });
      if (searchTerm) params.append('searchTerm', searchTerm);

      const [vouchersRes, clientsRes, statsRes] = await Promise.all([
        api.get(`/vouchers?${params}`),
        api.get('/users?role=client'),
        api.get('/vouchers/statistics/summary')
      ]);

      setVouchers(vouchersRes.data.vouchers || []);
      setClients(clientsRes.data.users || []);
      setStatistics(statsRes.data.statistics);
    } catch (error) {
      console.error('Error fetching accountant data:', error);
      showToast('فشل تحميل البيانات', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateVoucher = async () => {
    try {
      let endpoint = '';
      let data = { ...formData };

      switch (modalType) {
        case 'receipt':
          endpoint = '/vouchers/receipt';
          break;
        case 'payment':
          endpoint = '/vouchers/payment';
          break;
        case 'transfer':
          endpoint = '/vouchers/transfer';
          break;
        default:
          return;
      }

      const response = await api.post(endpoint, data);
      
      showToast('تم إنشاء السند بنجاح', 'success');
      setShowModal(false);
      resetForm();
      fetchData();
    } catch (error) {
      console.error('Error creating voucher:', error);
      showToast(error.response?.data?.message || 'فشل إنشاء السند', 'error');
    }
  };

  const handleUpdateStatus = async (voucherId, status) => {
    try {
      await api.patch(`/vouchers/${voucherId}/status`, { status });
      showToast('تم تحديث الحالة بنجاح', 'success');
      fetchData();
    } catch (error) {
      console.error('Error updating voucher status:', error);
      showToast('فشل تحديث الحالة', 'error');
    }
  };

  const handleCancelVoucher = async (voucherId) => {
    const reason = prompt('يرجى إدخال سبب الإلغاء:');
    if (!reason) return;

    try {
      await api.patch(`/vouchers/${voucherId}/cancel`, { reason });
      showToast('تم إلغاء السند بنجاح', 'success');
      fetchData();
    } catch (error) {
      console.error('Error cancelling voucher:', error);
      showToast('فشل إلغاء السند', 'error');
    }
  };

  const handleExport = async (format) => {
    try {
      const params = new URLSearchParams();
      Object.entries(filters).forEach(([key, value]) => {
        if (value) params.append(key, value);
      });

      const response = await api.get(`/vouchers/export/${format}?${params}`, {
        responseType: 'blob'
      });

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `vouchers.${format}`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);

      showToast(`تم تصدير السندات بصيغة ${format.toUpperCase()}`, 'success');
    } catch (error) {
      console.error('Error exporting vouchers:', error);
      showToast('فشل التصدير', 'error');
    }
  };

  const resetForm = () => {
    setFormData({
      clientId: '',
      amount: '',
      description: '',
      paymentMethod: 'cash',
      referenceNumber: '',
      notes: '',
      fromClientId: '',
      toClientId: ''
    });
  };

  const getStatusColor = (status) => {
    const colors = {
      pending: 'text-yellow-600 bg-yellow-100',
      completed: 'text-green-600 bg-green-100',
      cancelled: 'text-red-600 bg-red-100',
      failed: 'text-red-600 bg-red-100'
    };
    return colors[status] || 'text-gray-600 bg-gray-100';
  };

  const getStatusText = (status) => {
    const texts = {
      pending: 'معلق',
      completed: 'مكتمل',
      cancelled: 'ملغي',
      failed: 'فشل'
    };
    return texts[status] || status;
  };

  const getTypeText = (type) => {
    const texts = {
      receipt: 'سند قبض',
      payment: 'سند صرف',
      transfer: 'حوالة'
    };
    return texts[type] || type;
  };

  const getTypeIcon = (type) => {
    const icons = {
      receipt: TrendingUp,
      payment: TrendingDown,
      transfer: ArrowRightLeft
    };
    return icons[type] || FileText;
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">جاري تحميل البيانات...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50" dir="rtl">
      {/* Header */}
      <div className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-4 space-x-reverse">
              <Receipt className="w-8 h-8 text-blue-600" />
              <div>
                <h1 className="text-2xl font-bold text-gray-900">إدارة السندات المحاسبية</h1>
                <p className="text-sm text-gray-600">سندات القبض والصرف والحوالات</p>
              </div>
            </div>

            <div className="flex items-center space-x-2 space-x-reverse">
              <button
                onClick={() => handleExport('pdf')}
                className="flex items-center space-x-2 space-x-reverse px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
              >
                <Download className="w-4 h-4" />
                <span>تصدير PDF</span>
              </button>
              <button
                onClick={() => handleExport('excel')}
                className="flex items-center space-x-2 space-x-reverse px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
              >
                <Download className="w-4 h-4" />
                <span>تصدير Excel</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Statistics Cards */}
      {statistics && (
        <div className="max-w-7xl mx-auto px-4 py-6">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="bg-white rounded-lg shadow-sm p-4 border"
            >
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600">إجمالي سندات القبض</p>
                  <p className="text-2xl font-bold text-green-600">
                    {statistics.receipts.total.toLocaleString()} ريال
                  </p>
                  <p className="text-xs text-gray-500">{statistics.receipts.count} سند</p>
                </div>
                <TrendingUp className="w-8 h-8 text-green-600" />
              </div>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.1 }}
              className="bg-white rounded-lg shadow-sm p-4 border"
            >
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600">إجمالي سندات الصرف</p>
                  <p className="text-2xl font-bold text-red-600">
                    {statistics.payments.total.toLocaleString()} ريال
                  </p>
                  <p className="text-xs text-gray-500">{statistics.payments.count} سند</p>
                </div>
                <TrendingDown className="w-8 h-8 text-red-600" />
              </div>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.2 }}
              className="bg-white rounded-lg shadow-sm p-4 border"
            >
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600">إجمالي الحوالات</p>
                  <p className="text-2xl font-bold text-blue-600">
                    {statistics.transfers.total.toLocaleString()} ريال
                  </p>
                  <p className="text-xs text-gray-500">{statistics.transfers.count} حوالة</p>
                </div>
                <ArrowRightLeft className="w-8 h-8 text-blue-600" />
              </div>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.3 }}
              className="bg-white rounded-lg shadow-sm p-4 border"
            >
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-600">الصافي</p>
                  <p className="text-2xl font-bold text-gray-900">
                    {(statistics.receipts.total - statistics.payments.total).toLocaleString()} ريال
                  </p>
                </div>
                <BarChart3 className="w-8 h-8 text-gray-600" />
              </div>
            </motion.div>
          </div>
        </div>
      )}

      {/* Actions and Filters */}
      <div className="max-w-7xl mx-auto px-4 py-6">
        <div className="bg-white rounded-lg shadow-sm p-4 mb-6 border">
          <div className="flex flex-col lg:flex-row gap-4">
            {/* Create Buttons */}
            <div className="flex items-center space-x-2 space-x-reverse">
              <button
                onClick={() => { setModalType('receipt'); setShowModal(true); }}
                className="flex items-center space-x-2 space-x-reverse px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors"
              >
                <Plus className="w-4 h-4" />
                <span>سند قبض</span>
              </button>
              <button
                onClick={() => { setModalType('payment'); setShowModal(true); }}
                className="flex items-center space-x-2 space-x-reverse px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
              >
                <Plus className="w-4 h-4" />
                <span>سند صرف</span>
              </button>
              <button
                onClick={() => { setModalType('transfer'); setShowModal(true); }}
                className="flex items-center space-x-2 space-x-reverse px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                <ArrowRightLeft className="w-4 h-4" />
                <span>حوالة</span>
              </button>
            </div>

            {/* Search */}
            <div className="flex-1 relative">
              <Search className="absolute right-3 top-3 w-5 h-5 text-gray-400" />
              <input
                type="text"
                placeholder="ابحث بالرقم التسلسلي..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pr-10 pl-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            {/* Filters */}
            <div className="flex items-center space-x-2 space-x-reverse">
              <select
                value={filters.type}
                onChange={(e) => setFilters({ ...filters, type: e.target.value })}
                className="px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
              >
                <option value="">جميع الأنواع</option>
                <option value="receipt">سند قبض</option>
                <option value="payment">سند صرف</option>
                <option value="transfer">حوالة</option>
              </select>

              <select
                value={filters.status}
                onChange={(e) => setFilters({ ...filters, status: e.target.value })}
                className="px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
              >
                <option value="">جميع الحالات</option>
                <option value="pending">معلق</option>
                <option value="completed">مكتمل</option>
                <option value="cancelled">ملغي</option>
              </select>

              <input
                type="date"
                value={filters.startDate}
                onChange={(e) => setFilters({ ...filters, startDate: e.target.value })}
                className="px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
              />

              <input
                type="date"
                value={filters.endDate}
                onChange={(e) => setFilters({ ...filters, endDate: e.target.value })}
                className="px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>
        </div>

        {/* Vouchers Table */}
        <div className="bg-white rounded-lg shadow-sm overflow-hidden border">
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                    الرقم التسلسلي
                  </th>
                  <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                    النوع
                  </th>
                  <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                    العميل
                  </th>
                  <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                    المبلغ
                  </th>
                  <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                    الحالة
                  </th>
                  <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                    التاريخ
                  </th>
                  <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                    الإجراءات
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {vouchers.map((voucher, index) => {
                  const Icon = getTypeIcon(voucher.type);
                  return (
                    <motion.tr
                      key={voucher._id}
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      transition={{ delay: index * 0.05 }}
                      className="hover:bg-gray-50"
                    >
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                        {voucher.serialNumber}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <div className="flex items-center space-x-2 space-x-reverse">
                          <Icon className="w-4 h-4 text-gray-600" />
                          <span className="text-sm text-gray-900">{getTypeText(voucher.type)}</span>
                        </div>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                        {voucher.clientId?.name || 'N/A'}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                        {voucher.amount.toLocaleString()} ريال
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(voucher.status)}`}>
                          {getStatusText(voucher.status)}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {new Date(voucher.createdAt).toLocaleDateString('ar-SA')}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-left text-sm font-medium">
                        <div className="flex items-center space-x-2 space-x-reverse">
                          <button
                            onClick={() => setSelectedVoucher(voucher)}
                            className="text-blue-600 hover:text-blue-900"
                          >
                            <Eye className="w-4 h-4" />
                          </button>
                          {voucher.status === 'pending' && (
                            <>
                              <button
                                onClick={() => handleUpdateStatus(voucher._id, 'completed')}
                                className="text-green-600 hover:text-green-900"
                              >
                                <CheckCircle className="w-4 h-4" />
                              </button>
                              <button
                                onClick={() => handleCancelVoucher(voucher._id)}
                                className="text-red-600 hover:text-red-900"
                              >
                                <XCircle className="w-4 h-4" />
                              </button>
                            </>
                          )}
                        </div>
                      </td>
                    </motion.tr>
                  );
                })}
              </tbody>
            </table>
          </div>

          {vouchers.length === 0 && (
            <div className="text-center py-12">
              <Receipt className="w-12 h-12 text-gray-300 mx-auto mb-4" />
              <p className="text-gray-500">لا توجد سندات مطابقة للبحث</p>
            </div>
          )}
        </div>
      </div>

      {/* Create/Edit Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto"
          >
            <div className="p-6 border-b">
              <div className="flex items-center justify-between">
                <h2 className="text-xl font-semibold">
                  {modalType === 'receipt' && 'إنشاء سند قبض'}
                  {modalType === 'payment' && 'إنشاء سند صرف'}
                  {modalType === 'transfer' && 'إنشاء حوالة'}
                </h2>
                <button
                  onClick={() => { setShowModal(false); resetForm(); }}
                  className="text-gray-400 hover:text-gray-600"
                >
                  ×
                </button>
              </div>
            </div>

            <div className="p-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {modalType === 'transfer' ? (
                  <>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        العميل المحول
                      </label>
                      <select
                        value={formData.fromClientId}
                        onChange={(e) => setFormData({ ...formData, fromClientId: e.target.value })}
                        className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                        required
                      >
                        <option value="">اختر العميل</option>
                        {clients.map((client) => (
                          <option key={client._id} value={client._id}>
                            {client.name}
                          </option>
                        ))}
                      </select>
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">
                        العميل المستلم
                      </label>
                      <select
                        value={formData.toClientId}
                        onChange={(e) => setFormData({ ...formData, toClientId: e.target.value })}
                        className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                        required
                      >
                        <option value="">اختر العميل</option>
                        {clients.map((client) => (
                          <option key={client._id} value={client._id}>
                            {client.name}
                          </option>
                        ))}
                      </select>
                    </div>
                  </>
                ) : (
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      العميل
                    </label>
                    <select
                      value={formData.clientId}
                      onChange={(e) => setFormData({ ...formData, clientId: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                    >
                      <option value="">اختر العميل</option>
                      {clients.map((client) => (
                        <option key={client._id} value={client._id}>
                          {client.name}
                        </option>
                      ))}
                    </select>
                  </div>
                )}

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    المبلغ (ريال)
                  </label>
                  <input
                    type="number"
                    value={formData.amount}
                    onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                    required
                    min="0"
                    step="0.01"
                  />
                </div>

                {modalType !== 'transfer' && (
                  <div>
                    <label className="block text-sm font-medium text-gray-700 mb-2">
                      طريقة الدفع
                    </label>
                    <select
                      value={formData.paymentMethod}
                      onChange={(e) => setFormData({ ...formData, paymentMethod: e.target.value })}
                      className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                      required
                    >
                      <option value="cash">نقداً</option>
                      <option value="bank_transfer">تحويل بنكي</option>
                      <option value="check">شيك</option>
                      <option value="balance">من الرصيد</option>
                      <option value="card">بطاقة ائتمانية</option>
                    </select>
                  </div>
                )}

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    رقم المرجع
                  </label>
                  <input
                    type="text"
                    value={formData.referenceNumber}
                    onChange={(e) => setFormData({ ...formData, referenceNumber: e.target.value })}
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                    placeholder="اختياري"
                  />
                </div>

                <div className="md:col-span-2">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    الوصف
                  </label>
                  <textarea
                    value={formData.description}
                    onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                    rows="3"
                    required
                  />
                </div>

                <div className="md:col-span-2">
                  <label className="block text-sm font-medium text-gray-700 mb-2">
                    ملاحظات
                  </label>
                  <textarea
                    value={formData.notes}
                    onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                    rows="2"
                    placeholder="اختياري"
                  />
                </div>
              </div>

              <div className="flex items-center justify-end space-x-4 space-x-reverse mt-6">
                <button
                  onClick={() => { setShowModal(false); resetForm(); }}
                  className="px-6 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  إلغاء
                </button>
                <button
                  onClick={handleCreateVoucher}
                  className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                >
                  إنشاء السند
                </button>
              </div>
            </div>
          </motion.div>
        </div>
      )}

      {/* Voucher Details Modal */}
      {selectedVoucher && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="bg-white rounded-lg max-w-4xl w-full max-h-[90vh] overflow-y-auto"
          >
            <div className="p-6 border-b">
              <div className="flex items-center justify-between">
                <h2 className="text-xl font-semibold">تفاصيل السند</h2>
                <button
                  onClick={() => setSelectedVoucher(null)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  ×
                </button>
              </div>
            </div>

            <div className="p-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <h3 className="text-lg font-semibold mb-4">معلومات السند</h3>
                  <div className="space-y-3">
                    <div className="flex justify-between">
                      <span className="text-gray-600">الرقم التسلسلي:</span>
                      <span className="font-medium">{selectedVoucher.serialNumber}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">النوع:</span>
                      <span className="font-medium">{getTypeText(selectedVoucher.type)}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">المبلغ:</span>
                      <span className="font-medium">{selectedVoucher.amount.toLocaleString()} ريال</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">الحالة:</span>
                      <span className={`px-2 py-1 rounded-full text-xs ${getStatusColor(selectedVoucher.status)}`}>
                        {getStatusText(selectedVoucher.status)}
                      </span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">طريقة الدفع:</span>
                      <span className="font-medium">{selectedVoucher.paymentMethod}</span>
                    </div>
                  </div>
                </div>

                <div>
                  <h3 className="text-lg font-semibold mb-4">معلومات العميل</h3>
                  <div className="space-y-3">
                    <div className="flex justify-between">
                      <span className="text-gray-600">الاسم:</span>
                      <span className="font-medium">{selectedVoucher.clientId?.name || 'N/A'}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">البريد الإلكتروني:</span>
                      <span className="font-medium">{selectedVoucher.clientId?.email || 'N/A'}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">رقم الهاتف:</span>
                      <span className="font-medium">{selectedVoucher.clientId?.phone || 'N/A'}</span>
                    </div>
                  </div>
                </div>
              </div>

              <div className="mt-6">
                <h3 className="text-lg font-semibold mb-4">الوصف والملاحظات</h3>
                <div className="bg-gray-50 p-4 rounded-lg">
                  <p className="text-gray-900 mb-2">{selectedVoucher.description}</p>
                  {selectedVoucher.notes && (
                    <p className="text-gray-600 text-sm">ملاحظات: {selectedVoucher.notes}</p>
                  )}
                </div>
              </div>

              <div className="mt-6 grid grid-cols-2 md:grid-cols-4 gap-4 text-sm text-gray-600">
                <div>
                  <span className="block">إنشاء:</span>
                  <span className="font-medium">{selectedVoucher.createdBy?.name || 'N/A'}</span>
                </div>
                <div>
                  <span className="block">تاريخ الإنشاء:</span>
                  <span className="font-medium">{new Date(selectedVoucher.createdAt).toLocaleDateString('ar-SA')}</span>
                </div>
                {selectedVoucher.approvedBy && (
                  <div>
                    <span className="block">اعتماد:</span>
                    <span className="font-medium">{selectedVoucher.approvedBy?.name}</span>
                  </div>
                )}
                {selectedVoucher.approvedAt && (
                  <div>
                    <span className="block">تاريخ الاعتماد:</span>
                    <span className="font-medium">{new Date(selectedVoucher.approvedAt).toLocaleDateString('ar-SA')}</span>
                  </div>
                )}
              </div>

              <div className="flex items-center justify-end space-x-4 space-x-reverse mt-6">
                <button
                  onClick={() => setSelectedVoucher(null)}
                  className="px-6 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  إغلاق
                </button>
                <button
                  onClick={() => window.print()}
                  className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center space-x-2 space-x-reverse"
                >
                  <Printer className="w-4 h-4" />
                  <span>طباعة</span>
                </button>
              </div>
            </div>
          </motion.div>
        </div>
      )}
    </div>
  );
};

export default AccountantVoucherPage;
