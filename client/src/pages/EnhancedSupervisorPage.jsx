/**
 * ============================================
 * 👨‍💼 Enhanced Supervisor Page - نظام إدهام
 * Edham Logistics - Advanced Fleet Management
 * ============================================
 */

import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  Truck, MapPin, Users, Package, BarChart2, Settings,
  Plus, Search, Filter, Bell, AlertTriangle, CheckCircle,
  Clock, TrendingUp, TrendingDown, Navigation, Phone,
  MessageSquare, Eye, Edit, Trash2, Calendar, Activity,
  Fuel, Wrench, FileText, Download, RefreshCw
} from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { useNotification } from "../context/NotificationContext";
import api from "../services/api";

const EnhancedSupervisorPage = () => {
  const { user } = useAuth();
  const { showToast } = useNotification();

  const [activeTab, setActiveTab] = useState('dashboard');
  const [shipments, setShipments] = useState([]);
  const [drivers, setDrivers] = useState([]);
  const [trucks, setTrucks] = useState([]);
  const [activeDrivers, setActiveDrivers] = useState([]);
  const [statistics, setStatistics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showDispatchModal, setShowDispatchModal] = useState(false);
  const [selectedShipment, setSelectedShipment] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [filters, setFilters] = useState({
    status: '',
    priority: '',
    driverId: '',
    truckId: ''
  });

  // Dispatch form state
  const [dispatchForm, setDispatchForm] = useState({
    shipmentId: '',
    driverId: '',
    truckId: '',
    estimatedDeparture: '',
    estimatedArrival: '',
    notes: '',
    priority: 'normal'
  });

  useEffect(() => {
    fetchData();
  }, [activeTab, filters, searchTerm]);

  const fetchData = async () => {
    try {
      setLoading(true);
      
      const [shipmentsRes, driversRes, trucksRes, statsRes] = await Promise.all([
        api.get('/shipments'),
        api.get('/drivers'),
        api.get('/trucks'),
        api.get('/supervisor/statistics')
      ]);

      setShipments(shipmentsRes.data.shipments || []);
      setDrivers(driversRes.data.drivers || []);
      setTrucks(trucksRes.data.trucks || []);
      setStatistics(statsRes.data.statistics);

      // Get active drivers with locations
      if (activeTab === 'tracking') {
        const activeRes = await api.get('/tracking/drivers/active');
        setActiveDrivers(activeRes.data.drivers || []);
      }
    } catch (error) {
      console.error('Error fetching supervisor data:', error);
      showToast('فشل تحميل البيانات', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleDispatch = async () => {
    try {
      await api.post('/supervisor/dispatch', dispatchForm);
      showToast('تم إرسال الشاحنة بنجاح', 'success');
      setShowDispatchModal(false);
      resetDispatchForm();
      fetchData();
    } catch (error) {
      console.error('Error dispatching truck:', error);
      showToast('فشل إرسال الشاحنة', 'error');
    }
  };

  const handleUpdateShipmentStatus = async (shipmentId, status) => {
    try {
      await api.patch(`/shipments/${shipmentId}/status`, { status });
      showToast('تم تحديث حالة الشحنة بنجاح', 'success');
      fetchData();
    } catch (error) {
      console.error('Error updating shipment status:', error);
      showToast('فشل تحديث الحالة', 'error');
    }
  };

  const resetDispatchForm = () => {
    setDispatchForm({
      shipmentId: '',
      driverId: '',
      truckId: '',
      estimatedDeparture: '',
      estimatedArrival: '',
      notes: '',
      priority: 'normal'
    });
  };

  const getPriorityColor = (priority) => {
    const colors = {
      urgent: 'text-red-600 bg-red-100',
      high: 'text-orange-600 bg-orange-100',
      normal: 'text-blue-600 bg-blue-100',
      low: 'text-gray-600 bg-gray-100'
    };
    return colors[priority] || colors.normal;
  };

  const getStatusColor = (status) => {
    const colors = {
      pending: 'text-yellow-600 bg-yellow-100',
      assigned: 'text-blue-600 bg-blue-100',
      in_transit: 'text-purple-600 bg-purple-100',
      delivered: 'text-green-600 bg-green-100',
      cancelled: 'text-red-600 bg-red-100'
    };
    return colors[status] || colors.pending;
  };

  const getDriverStatusColor = (status) => {
    const colors = {
      available: 'text-green-600 bg-green-100',
      busy: 'text-red-600 bg-red-100',
      offline: 'text-gray-600 bg-gray-100',
      on_break: 'text-yellow-600 bg-yellow-100'
    };
    return colors[status] || colors.offline;
  };

  const renderDashboard = () => (
    <div className="space-y-6">
      {/* Statistics Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-lg shadow-sm p-4 border"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">الشحنات النشطة</p>
              <p className="text-2xl font-bold text-blue-600">
                {statistics?.activeShipments || 0}
              </p>
              <p className="text-xs text-gray-500">
                {statistics?.todayShipments || 0} اليوم
              </p>
            </div>
            <Package className="w-8 h-8 text-blue-600" />
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
              <p className="text-sm text-gray-600">السائقون النشطون</p>
              <p className="text-2xl font-bold text-green-600">
                {statistics?.activeDrivers || 0}
              </p>
              <p className="text-xs text-gray-500">
                {statistics?.totalDrivers || 0} إجمالي
              </p>
            </div>
            <Users className="w-8 h-8 text-green-600" />
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
              <p className="text-sm text-gray-600">الشاحنات المتاحة</p>
              <p className="text-2xl font-bold text-purple-600">
                {statistics?.availableTrucks || 0}
              </p>
              <p className="text-xs text-gray-500">
                {statistics?.totalTrucks || 0} إجمالي
              </p>
            </div>
            <Truck className="w-8 h-8 text-purple-600" />
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
              <p className="text-sm text-gray-600">معدل التسليم</p>
              <p className="text-2xl font-bold text-orange-600">
                {statistics?.deliveryRate || 0}%
              </p>
              <p className="text-xs text-gray-500">
                {statistics?.onTimeDeliveries || 0} في الوقت
              </p>
            </div>
            <TrendingUp className="w-8 h-8 text-orange-600" />
          </div>
        </motion.div>
      </div>

      {/* Recent Shipments */}
      <div className="bg-white rounded-lg shadow-sm overflow-hidden border">
        <div className="p-4 border-b">
          <div className="flex items-center justify-between">
            <h3 className="text-lg font-semibold">الشحنات الأخيرة</h3>
            <button
              onClick={() => setActiveTab('shipments')}
              className="text-blue-600 hover:text-blue-800 text-sm"
            >
              عرض الكل
            </button>
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  رقم التتبع
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  العميل
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  من/إلى
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  السائق
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الحالة
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الأولوية
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {shipments.slice(0, 5).map((shipment, index) => (
                <motion.tr
                  key={shipment._id}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ delay: index * 0.05 }}
                  className="hover:bg-gray-50"
                >
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {shipment.trackingNumber}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {shipment.clientId?.name || 'N/A'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    <div className="text-xs">
                      <div>من: {shipment.origin?.city}</div>
                      <div>إلى: {shipment.destination?.city}</div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {shipment.assignedDriverId?.name || 'غير محدد'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(shipment.status)}`}>
                      {shipment.status === 'pending' ? 'معلق' :
                       shipment.status === 'assigned' ? 'معين' :
                       shipment.status === 'in_transit' ? 'قيد التوصيل' :
                       shipment.status === 'delivered' ? 'تم التسليم' : shipment.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getPriorityColor(shipment.priority)}`}>
                      {shipment.priority === 'urgent' ? 'عاجل' :
                       shipment.priority === 'high' ? 'مرتفع' :
                       shipment.priority === 'normal' ? 'عادي' : 'منخفض'}
                    </span>
                  </td>
                </motion.tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );

  const renderShipments = () => (
    <div className="space-y-6">
      {/* Filters and Actions */}
      <div className="bg-white rounded-lg shadow-sm p-4 border">
        <div className="flex flex-col lg:flex-row gap-4">
          <div className="flex-1 relative">
            <Search className="absolute right-3 top-3 w-5 h-5 text-gray-400" />
            <input
              type="text"
              placeholder="ابحث بالرقم التتبع أو العميل..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pr-10 pl-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>

          <div className="flex items-center space-x-2 space-x-reverse">
            <select
              value={filters.status}
              onChange={(e) => setFilters({ ...filters, status: e.target.value })}
              className="px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
            >
              <option value="">جميع الحالات</option>
              <option value="pending">معلق</option>
              <option value="assigned">معين</option>
              <option value="in_transit">قيد التوصيل</option>
              <option value="delivered">تم التسليم</option>
            </select>

            <select
              value={filters.priority}
              onChange={(e) => setFilters({ ...filters, priority: e.target.value })}
              className="px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
            >
              <option value="">جميع الأولويات</option>
              <option value="urgent">عاجل</option>
              <option value="high">مرتفع</option>
              <option value="normal">عادي</option>
              <option value="low">منخفض</option>
            </select>

            <button
              onClick={() => setShowDispatchModal(true)}
              className="flex items-center space-x-2 space-x-reverse px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              <Plus className="w-4 h-4" />
              <span>إرسال شاحنة</span>
            </button>
          </div>
        </div>
      </div>

      {/* Shipments Table */}
      <div className="bg-white rounded-lg shadow-sm overflow-hidden border">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  رقم التتبع
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  العميل
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  المسار
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  السائق
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الشاحنة
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الحالة
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الأولوية
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase">
                  الإجراءات
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {shipments.map((shipment, index) => (
                <motion.tr
                  key={shipment._id}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ delay: index * 0.05 }}
                  className="hover:bg-gray-50"
                >
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {shipment.trackingNumber}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {shipment.clientId?.name || 'N/A'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    <div className="text-xs">
                      <div>من: {shipment.origin?.city}</div>
                      <div>إلى: {shipment.destination?.city}</div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {shipment.assignedDriverId?.name || 'غير محدد'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {shipment.assignedTruckId?.plateNumber || 'غير محدد'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(shipment.status)}`}>
                      {shipment.status === 'pending' ? 'معلق' :
                       shipment.status === 'assigned' ? 'معين' :
                       shipment.status === 'in_transit' ? 'قيد التوصيل' :
                       shipment.status === 'delivered' ? 'تم التسليم' : shipment.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getPriorityColor(shipment.priority)}`}>
                      {shipment.priority === 'urgent' ? 'عاجل' :
                       shipment.priority === 'high' ? 'مرتفع' :
                       shipment.priority === 'normal' ? 'عادي' : 'منخفض'}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-left text-sm font-medium">
                    <div className="flex items-center space-x-2 space-x-reverse">
                      <button
                        onClick={() => setSelectedShipment(shipment)}
                        className="text-blue-600 hover:text-blue-900"
                      >
                        <Eye className="w-4 h-4" />
                      </button>
                      {shipment.status === 'pending' && (
                        <button
                          onClick={() => {
                            setDispatchForm({
                              ...dispatchForm,
                              shipmentId: shipment._id
                            });
                            setShowDispatchModal(true);
                          }}
                          className="text-green-600 hover:text-green-900"
                        >
                          <Truck className="w-4 h-4" />
                        </button>
                      )}
                      <button
                        onClick={() => {/* Track shipment */}}
                        className="text-purple-600 hover:text-purple-900"
                      >
                        <Navigation className="w-4 h-4" />
                      </button>
                    </div>
                  </td>
                </motion.tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );

  const renderTracking = () => (
    <div className="space-y-6">
      {/* Active Drivers Map */}
      <div className="bg-white rounded-lg shadow-sm overflow-hidden border">
        <div className="p-4 border-b">
          <h3 className="text-lg font-semibold">تتبع السائقين المباشر</h3>
        </div>
        
        <div className="h-96 bg-gray-100 flex items-center justify-center">
          <div className="text-center">
            <MapPin className="w-16 h-16 text-gray-400 mx-auto mb-4" />
            <p className="text-gray-600">الخريطة التفاعلية</p>
            <p className="text-sm text-gray-500">عرض مواقع السائقين في الوقت الفعلي</p>
          </div>
        </div>
      </div>

      {/* Active Drivers List */}
      <div className="bg-white rounded-lg shadow-sm overflow-hidden border">
        <div className="p-4 border-b">
          <div className="flex items-center justify-between">
            <h3 className="text-lg font-semibold">السائقون النشطون</h3>
            <button
              onClick={() => fetchData()}
              className="flex items-center space-x-2 space-x-reverse text-blue-600 hover:text-blue-800"
            >
              <RefreshCw className="w-4 h-4" />
              <span>تحديث</span>
            </button>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 p-4">
          {activeDrivers.map((driver, index) => (
            <motion.div
              key={driver.driverId}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.1 }}
              className="border rounded-lg p-4 hover:shadow-md transition-shadow"
            >
              <div className="flex items-start justify-between mb-3">
                <div>
                  <h4 className="font-semibold text-gray-900">{driver.driverName}</h4>
                  <p className="text-sm text-gray-600">{driver.driverPhone}</p>
                </div>
                <span className={`inline-flex items-center px-2 py-1 rounded-full text-xs font-medium ${getDriverStatusColor(driver.status)}`}>
                  {driver.status === 'available' ? 'متاح' :
                   driver.status === 'busy' ? 'مشغول' :
                   driver.status === 'offline' ? 'غير متصل' : 'في استراحة'}
                </span>
              </div>

              <div className="space-y-2 text-sm">
                <div className="flex items-center space-x-2 space-x-reverse">
                  <Truck className="w-4 h-4 text-gray-400" />
                  <span>{driver.vehicleInfo?.make} {driver.vehicleInfo?.model}</span>
                </div>
                
                <div className="flex items-center space-x-2 space-x-reverse">
                  <MapPin className="w-4 h-4 text-gray-400" />
                  <span className="text-gray-600">{driver.address || 'غير محدد'}</span>
                </div>

                {driver.speed > 0 && (
                  <div className="flex items-center space-x-2 space-x-reverse">
                    <Activity className="w-4 h-4 text-gray-400" />
                    <span className="text-gray-600">{driver.speed} كم/ساعة</span>
                  </div>
                )}

                {driver.currentShipment && (
                  <div className="bg-blue-50 p-2 rounded">
                    <div className="flex items-center space-x-2 space-x-reverse">
                      <Package className="w-4 h-4 text-blue-600" />
                      <span className="text-sm font-medium text-blue-900">
                        {driver.currentShipment.trackingNumber}
                      </span>
                    </div>
                    <div className="text-xs text-blue-700">
                      {driver.currentShipment.origin?.city} → {driver.currentShipment.destination?.city}
                    </div>
                  </div>
                )}
              </div>

              <div className="flex items-center justify-between mt-4 pt-3 border-t">
                <div className="text-xs text-gray-500">
                  آخر تحديث: {new Date(driver.timestamp).toLocaleTimeString('ar-SA')}
                </div>
                <div className="flex items-center space-x-2 space-x-reverse">
                  <button
                    onClick={() => {/* Call driver */}}
                    className="text-green-600 hover:text-green-900"
                  >
                    <Phone className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => {/* Message driver */}}
                    className="text-blue-600 hover:text-blue-900"
                  >
                    <MessageSquare className="w-4 h-4" />
                  </button>
                  <button
                    onClick={() => {/* Track driver */}}
                    className="text-purple-600 hover:text-purple-900"
                  >
                    <Navigation className="w-4 h-4" />
                  </button>
                </div>
              </div>
            </motion.div>
          ))}
        </div>

        {activeDrivers.length === 0 && (
          <div className="text-center py-12">
            <Users className="w-16 h-16 text-gray-300 mx-auto mb-4" />
            <p className="text-gray-500">لا يوجد سائقون نشطون حالياً</p>
          </div>
        )}
      </div>
    </div>
  );

  const renderDispatchModal = () => {
    if (!showDispatchModal) return null;

    return (
      <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
        <motion.div
          initial={{ opacity: 0, scale: 0.95 }}
          animate={{ opacity: 1, scale: 1 }}
          className="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto"
        >
          <div className="p-6 border-b">
            <div className="flex items-center justify-between">
              <h2 className="text-xl font-semibold">إرسال شاحنة</h2>
              <button
                onClick={() => setShowDispatchModal(false)}
                className="text-gray-400 hover:text-gray-600"
              >
                ×
              </button>
            </div>
          </div>

          <div className="p-6">
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  الشحنة
                </label>
                <select
                  value={dispatchForm.shipmentId}
                  onChange={(e) => setDispatchForm({ ...dispatchForm, shipmentId: e.target.value })}
                  className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                  required
                >
                  <option value="">اختر الشحنة</option>
                  {shipments
                    .filter(s => s.status === 'pending')
                    .map((shipment) => (
                      <option key={shipment._id} value={shipment._id}>
                        {shipment.trackingNumber} - {shipment.origin?.city} → {shipment.destination?.city}
                      </option>
                    ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  السائق
                </label>
                <select
                  value={dispatchForm.driverId}
                  onChange={(e) => setDispatchForm({ ...dispatchForm, driverId: e.target.value })}
                  className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                  required
                >
                  <option value="">اختر السائق</option>
                  {drivers
                    .filter(d => d.status === 'available')
                    .map((driver) => (
                      <option key={driver._id} value={driver._id}>
                        {driver.name} - {driver.phone}
                      </option>
                    ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  الشاحنة
                </label>
                <select
                  value={dispatchForm.truckId}
                  onChange={(e) => setDispatchForm({ ...dispatchForm, truckId: e.target.value })}
                  className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                  required
                >
                  <option value="">اختر الشاحنة</option>
                  {trucks
                    .filter(t => t.status === 'available')
                    .map((truck) => (
                      <option key={truck._id} value={truck._id}>
                        {truck.plateNumber} - {truck.make} {truck.model}
                      </option>
                    ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  الأولوية
                </label>
                <select
                  value={dispatchForm.priority}
                  onChange={(e) => setDispatchForm({ ...dispatchForm, priority: e.target.value })}
                  className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                >
                  <option value="low">منخفضة</option>
                  <option value="normal">عادية</option>
                  <option value="high">مرتفعة</option>
                  <option value="urgent">عاجلة</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  وقت المغادرة المتوقع
                </label>
                <input
                  type="datetime-local"
                  value={dispatchForm.estimatedDeparture}
                  onChange={(e) => setDispatchForm({ ...dispatchForm, estimatedDeparture: e.target.value })}
                  className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  وقت الوصول المتوقع
                </label>
                <input
                  type="datetime-local"
                  value={dispatchForm.estimatedArrival}
                  onChange={(e) => setDispatchForm({ ...dispatchForm, estimatedArrival: e.target.value })}
                  className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>

              <div className="md:col-span-2">
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  ملاحظات
                </label>
                <textarea
                  value={dispatchForm.notes}
                  onChange={(e) => setDispatchForm({ ...dispatchForm, notes: e.target.value })}
                  className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
                  rows="3"
                  placeholder="ملاحظات إضافية..."
                />
              </div>
            </div>

            <div className="flex items-center justify-end space-x-4 space-x-reverse mt-6">
              <button
                onClick={() => setShowDispatchModal(false)}
                className="px-6 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
              >
                إلغاء
              </button>
              <button
                onClick={handleDispatch}
                className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                إرسال الشاحنة
              </button>
            </div>
          </div>
        </motion.div>
      </div>
    );
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">جاري تحميل لوحة المشرف...</p>
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
              <BarChart2 className="w-8 h-8 text-blue-600" />
              <div>
                <h1 className="text-2xl font-bold text-gray-900">لوحة المشرف</h1>
                <p className="text-sm text-gray-600">إدارة الأسطول وتتبع الشحنات</p>
              </div>
            </div>

            <div className="flex items-center space-x-4 space-x-reverse">
              <button className="relative p-2 text-gray-600 hover:text-gray-900">
                <Bell className="w-6 h-6" />
                <span className="absolute top-0 right-0 w-2 h-2 bg-red-500 rounded-full"></span>
              </button>
              <div className="text-left">
                <p className="text-sm font-medium text-gray-900">{user.name}</p>
                <p className="text-xs text-gray-500">مشرف</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="bg-white border-b">
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex space-x-8 space-x-reverse">
            {[
              { id: 'dashboard', label: 'الرئيسية', icon: BarChart2 },
              { id: 'shipments', label: 'الشحنات', icon: Package },
              { id: 'tracking', label: 'التتبع', icon: Navigation },
              { id: 'drivers', label: 'السائقون', icon: Users },
              { id: 'trucks', label: 'الشاحنات', icon: Truck }
            ].map((tab) => {
              const Icon = tab.icon;
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`flex items-center space-x-2 space-x-reverse py-4 border-b-2 transition-colors ${
                    activeTab === tab.id
                      ? 'border-blue-600 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700'
                  }`}
                >
                  <Icon className="w-5 h-5" />
                  <span className="font-medium">{tab.label}</span>
                </button>
              );
            })}
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-7xl mx-auto px-4 py-6">
        {activeTab === 'dashboard' && renderDashboard()}
        {activeTab === 'shipments' && renderShipments()}
        {activeTab === 'tracking' && renderTracking()}
      </div>

      {/* Dispatch Modal */}
      {renderDispatchModal()}

      {/* Shipment Details Modal */}
      {selectedShipment && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="bg-white rounded-lg max-w-4xl w-full max-h-[90vh] overflow-y-auto"
          >
            <div className="p-6 border-b">
              <div className="flex items-center justify-between">
                <h2 className="text-xl font-semibold">تفاصيل الشحنة</h2>
                <button
                  onClick={() => setSelectedShipment(null)}
                  className="text-gray-400 hover:text-gray-600"
                >
                  ×
                </button>
              </div>
            </div>

            <div className="p-6">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <h3 className="text-lg font-semibold mb-4">معلومات الشحنة</h3>
                  <div className="space-y-3">
                    <div className="flex justify-between">
                      <span className="text-gray-600">رقم التتبع:</span>
                      <span className="font-medium">{selectedShipment.trackingNumber}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">العميل:</span>
                      <span className="font-medium">{selectedShipment.clientId?.name}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">الحالة:</span>
                      <span className={`px-2 py-1 rounded-full text-xs ${getStatusColor(selectedShipment.status)}`}>
                        {selectedShipment.status}
                      </span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-gray-600">الأولوية:</span>
                      <span className={`px-2 py-1 rounded-full text-xs ${getPriorityColor(selectedShipment.priority)}`}>
                        {selectedShipment.priority}
                      </span>
                    </div>
                  </div>
                </div>

                <div>
                  <h3 className="text-lg font-semibold mb-4">مسار الشحنة</h3>
                  <div className="space-y-3">
                    <div>
                      <span className="text-gray-600">من:</span>
                      <div className="font-medium">
                        {selectedShipment.origin?.address}, {selectedShipment.origin?.city}
                      </div>
                    </div>
                    <div>
                      <span className="text-gray-600">إلى:</span>
                      <div className="font-medium">
                        {selectedShipment.destination?.address}, {selectedShipment.destination?.city}
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div className="flex items-center justify-end space-x-4 space-x-reverse mt-6">
                <button
                  onClick={() => setSelectedShipment(null)}
                  className="px-6 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
                >
                  إغلاق
                </button>
                <button
                  onClick={() => {/* Track shipment */}}
                  className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors flex items-center space-x-2 space-x-reverse"
                >
                  <Navigation className="w-4 h-4" />
                  <span>تتبع</span>
                </button>
              </div>
            </div>
          </motion.div>
        </div>
      )}
    </div>
  );
};

export default EnhancedSupervisorPage;
