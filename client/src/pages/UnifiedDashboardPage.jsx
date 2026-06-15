/**
 * ============================================
 * 🎯 Unified Dashboard - نظام إدهام الاحترافي
 * Edham Logistics - Complete Professional Interface
 * ============================================
 */

import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  BarChart3, Users, Truck, Package, DollarSign, TrendingUp,
  Shield, Settings, Bell, Search, Filter, Download,
  MapPin, Clock, AlertTriangle, CheckCircle, Activity,
  FileText, Wrench, Battery, Phone, MessageSquare,
  Eye, Edit, Plus, ChevronDown, MoreVertical,
  Zap, Target, PieChart, Calendar, RefreshCw
} from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { useNotification } from "../context/NotificationContext";
import api from "../services/api";

const UnifiedDashboardPage = () => {
  const { user } = useAuth();
  const { showToast } = useNotification();

  const [activeView, setActiveView] = useState('overview');
  const [timeRange, setTimeRange] = useState('7d');
  const [searchTerm, setSearchTerm] = useState('');
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [notifications, setNotifications] = useState([]);

  const views = [
    { id: 'overview', label: 'نظرة عامة', icon: BarChart3 },
    { id: 'shipments', label: 'الشحنات', icon: Package },
    { id: 'customers', label: 'العملاء', icon: Users },
    { id: 'fleet', label: 'الأسطول', icon: Truck },
    { id: 'finance', label: 'المالية', icon: DollarSign },
    { id: 'analytics', label: 'التحليلات', icon: TrendingUp },
    { id: 'assets', label: 'الأصول', icon: Wrench },
    { id: 'reports', label: 'التقارير', icon: FileText },
    { id: 'settings', label: 'الإعدادات', icon: Settings }
  ];

  const timeRanges = [
    { id: '24h', label: '24 ساعة' },
    { id: '7d', label: '7 أيام' },
    { id: '30d', label: '30 يوم' },
    { id: '90d', label: '90 يوم' },
    { id: '1y', label: 'سنة' }
  ];

  useEffect(() => {
    fetchDashboardData();
    fetchNotifications();
  }, [activeView, timeRange]);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const response = await api.get('/unified/dashboard', {
        params: { view: activeView, timeRange, search: searchTerm }
      });
      setDashboardData(response.data);
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
      showToast('فشل تحليل البيانات', 'error');
    } finally {
      setLoading(false);
    }
  };

  const fetchNotifications = async () => {
    try {
      const response = await api.get('/notifications/unread');
      setNotifications(response.data.notifications || []);
    } catch (error) {
      console.error('Error fetching notifications:', error);
    }
  };

  const StatCard = ({ title, value, change, icon: Icon, color = 'blue', trend = 'up' }) => {
    const isPositive = change >= 0;
    
    return (
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-white rounded-xl shadow-lg p-6 border border-gray-100 hover:shadow-xl transition-all duration-300"
      >
        <div className="flex items-start justify-between mb-4">
          <div className={`p-3 rounded-lg bg-${color}-100`}>
            <Icon className={`w-6 h-6 text-${color}-600`} />
          </div>
          <button className="text-gray-400 hover:text-gray-600">
            <MoreVertical className="w-5 h-5" />
          </button>
        </div>

        <div className="space-y-2">
          <p className="text-sm text-gray-600 font-medium">{title}</p>
          <p className="text-3xl font-bold text-gray-900">
            {value?.toLocaleString() || '0'}
          </p>
          
          <div className="flex items-center space-x-2 space-x-reverse">
            <div className={`flex items-center space-x-1 space-x-reverse text-sm ${
              isPositive ? 'text-green-600' : 'text-red-600'
            }`}>
              {trend === 'up' ? (
                <TrendingUp className="w-4 h-4" />
              ) : (
                <TrendingUp className="w-4 h-4 rotate-180" />
              )}
              <span className="font-medium">
                {Math.abs(change)}%
              </span>
            </div>
            <span className="text-xs text-gray-500">
              عن الفترة الماضية
            </span>
          </div>
        </div>
      </motion.div>
    );
  };

  const QuickAction = ({ title, description, icon: Icon, color = 'blue', action, count }) => (
    <motion.div
      initial={{ opacity: 0, scale: 0.95 }}
      animate={{ opacity: 1, scale: 1 }}
      whileHover={{ scale: 1.02 }}
      onClick={action}
      className="bg-white rounded-xl shadow-lg p-6 border border-gray-100 hover:shadow-xl transition-all duration-300 cursor-pointer"
    >
      <div className="flex items-start justify-between mb-4">
        <div className={`p-3 rounded-lg bg-${color}-100`}>
          <Icon className={`w-6 h-6 text-${color}-600`} />
        </div>
        {count && (
          <span className="bg-red-500 text-white text-xs px-2 py-1 rounded-full">
            {count}
          </span>
        )}
      </div>

      <h3 className="text-lg font-semibold text-gray-900 mb-2">{title}</h3>
      <p className="text-sm text-gray-600">{description}</p>
    </motion.div>
  );

  const renderOverview = () => (
    <div className="space-y-6">
      {/* Key Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="إجمالي الإيرادات"
          value={dashboardData?.revenue?.total}
          change={dashboardData?.revenue?.growth}
          icon={DollarSign}
          color="green"
        />
        <StatCard
          title="الشحنات النشطة"
          value={dashboardData?.shipments?.active}
          change={dashboardData?.shipments?.growth}
          icon={Package}
          color="blue"
        />
        <StatCard
          title="العملاء النشطون"
          value={dashboardData?.customers?.active}
          change={dashboardData?.customers?.growth}
          icon={Users}
          color="purple"
        />
        <StatCard
          title="معدل التسليم في الوقت"
          value={`${dashboardData?.performance?.onTimeDelivery || 0}%`}
          change={dashboardData?.performance?.deliveryRateChange}
          icon={CheckCircle}
          color="orange"
        />
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <QuickAction
          title="إرسال شحنة جديدة"
          description="إنشاء وتعيين شحنة جديدة للسائق"
          icon={Plus}
          color="blue"
          action={() => {/* Navigate to new shipment */}}
          count={dashboardData?.pendingShipments}
        />
        <QuickAction
          title="تتبع الموقع المباشر"
          description="مراقبة مواقع السائقين في الوقت الفعلي"
          icon={MapPin}
          color="green"
          action={() => {/* Navigate to live tracking */}}
        />
        <QuickAction
          title="إنشاء تقرير"
          description="توليد تقارير مخصصة للإدارة"
          icon={FileText}
          color="purple"
          action={() => {/* Navigate to reports */}}
        />
        <QuickAction
          title="صيانة الأسطول"
          description="جدولة وإدارة صيانة المركبات"
          icon={Wrench}
          color="orange"
          action={() => {/* Navigate to maintenance */}}
          count={dashboardData?.maintenanceDue}
        />
        <QuickAction
          title="إدارة المخاطر"
          description="تقييم وإدارة مخاطر التشغيل"
          icon={Shield}
          color="red"
          action={() => {/* Navigate to risk management */}}
          count={dashboardData?.activeRisks}
        />
        <QuickAction
          title="رسائل للسائقين"
          description="إرسال إشعارات جماعية للسائقين"
          icon={MessageSquare}
          color="indigo"
          action={() => {/* Navigate to messages */}}
        />
      </div>

      {/* Recent Activity */}
      <div className="bg-white rounded-xl shadow-lg p-6 border border-gray-100">
        <div className="flex items-center justify-between mb-6">
          <h3 className="text-xl font-semibold text-gray-900">النشاط الأخير</h3>
          <button className="text-blue-600 hover:text-blue-800 text-sm">
            عرض الكل
          </button>
        </div>

        <div className="space-y-4">
          {dashboardData?.recentActivity?.slice(0, 5).map((activity, index) => (
            <motion.div
              key={activity.id}
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: index * 0.1 }}
              className="flex items-center space-x-4 space-x-reverse p-4 hover:bg-gray-50 rounded-lg"
            >
              <div className={`p-2 rounded-full ${
                activity.type === 'success' ? 'bg-green-100' :
                activity.type === 'warning' ? 'bg-yellow-100' :
                activity.type === 'error' ? 'bg-red-100' :
                'bg-blue-100'
              }`}>
                {activity.type === 'success' && <CheckCircle className="w-4 h-4 text-green-600" />}
                {activity.type === 'warning' && <AlertTriangle className="w-4 h-4 text-yellow-600" />}
                {activity.type === 'error' && <AlertTriangle className="w-4 h-4 text-red-600" />}
                {activity.type === 'info' && <Bell className="w-4 h-4 text-blue-600" />}
              </div>
              
              <div className="flex-1">
                <p className="font-medium text-gray-900">{activity.title}</p>
                <p className="text-sm text-gray-600">{activity.description}</p>
                <p className="text-xs text-gray-500 mt-1">
                  {new Date(activity.timestamp).toLocaleString('ar-SA')}
                </p>
              </div>
            </motion.div>
          ))}
        </div>
      </div>
    </div>
  );

  const renderShipments = () => (
    <div className="space-y-6">
      {/* Search and Filters */}
      <div className="bg-white rounded-xl shadow-lg p-6 border border-gray-100">
        <div className="flex flex-col lg:flex-row gap-4">
          <div className="flex-1 relative">
            <Search className="absolute right-3 top-3 w-5 h-5 text-gray-400" />
            <input
              type="text"
              placeholder="البحث عن الشحنات..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pr-10 pl-4 py-3 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
            />
          </div>
          
          <div className="flex items-center space-x-2 space-x-reverse">
            <select
              value={timeRange}
              onChange={(e) => setTimeRange(e.target.value)}
              className="px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500"
            >
              {timeRanges.map(range => (
                <option key={range.id} value={range.id}>
                  {range.label}
                </option>
              ))}
            </select>
            
            <button className="flex items-center space-x-2 space-x-reverse px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
              <Plus className="w-4 h-4" />
              <span>شحنة جديدة</span>
            </button>
          </div>
        </div>
      </div>

      {/* Shipments Table */}
      <div className="bg-white rounded-xl shadow-lg overflow-hidden border border-gray-100">
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
                  الإجراءات
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {dashboardData?.shipments?.list?.map((shipment, index) => (
                <motion.tr
                  key={shipment.id}
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ delay: index * 0.05 }}
                  className="hover:bg-gray-50"
                >
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                    {shipment.trackingNumber}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {shipment.clientName}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    <div className="text-xs">
                      <div>من: {shipment.origin}</div>
                      <div>إلى: {shipment.destination}</div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {shipment.driverName}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                      shipment.status === 'delivered' ? 'bg-green-100 text-green-800' :
                      shipment.status === 'in_transit' ? 'bg-blue-100 text-blue-800' :
                      shipment.status === 'pending' ? 'bg-yellow-100 text-yellow-800' :
                      'bg-gray-100 text-gray-800'
                    }`}>
                      {shipment.status === 'delivered' ? 'تم التسليم' :
                       shipment.status === 'in_transit' ? 'قيد التوصيل' :
                       shipment.status === 'pending' ? 'معلق' : shipment.status}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-left text-sm font-medium">
                    <div className="flex items-center space-x-2 space-x-reverse">
                      <button
                        onClick={() => {/* View details */}}
                        className="text-blue-600 hover:text-blue-900"
                      >
                        <Eye className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => {/* Track shipment */}}
                        className="text-green-600 hover:text-green-900"
                      >
                        <MapPin className="w-4 h-4" />
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

  const renderAnalytics = () => (
    <div className="space-y-6">
      {/* Analytics Charts */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-xl shadow-lg p-6 border border-gray-100"
        >
          <h3 className="text-lg font-semibold text-gray-900 mb-4">اتجاه الإيرادات</h3>
          <div className="h-64 bg-gradient-to-br from-green-50 to-emerald-100 rounded-lg flex items-center justify-center">
            <div className="text-center">
              <TrendingUp className="w-12 h-12 text-green-600 mx-auto mb-2" />
              <p className="text-gray-600">مخطط الإيرادات</p>
            </div>
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="bg-white rounded-xl shadow-lg p-6 border border-gray-100"
        >
          <h3 className="text-lg font-semibold text-gray-900 mb-4">أداء الشحنات</h3>
          <div className="h-64 bg-gradient-to-br from-blue-50 to-indigo-100 rounded-lg flex items-center justify-center">
            <div className="text-center">
              <Activity className="w-12 h-12 text-blue-600 mx-auto mb-2" />
              <p className="text-gray-600">مخطط الأداء</p>
            </div>
          </div>
        </motion.div>
      </div>

      {/* Performance Metrics */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <StatCard
          title="متوسط وقت التسليم"
          value={`${dashboardData?.analytics?.avgDeliveryTime || 0} ساعة`}
          change={dashboardData?.analytics?.deliveryTimeChange}
          icon={Clock}
          color="orange"
        />
        <StatCard
          title="معدل استخدام الأسطول"
          value={`${dashboardData?.analytics?.fleetUtilization || 0}%`}
          change={dashboardData?.analytics?.utilizationChange}
          icon={Truck}
          color="purple"
        />
        <StatCard
          title="رضا العملاء"
          value={`${dashboardData?.analytics?.customerSatisfaction || 0}/5`}
          change={dashboardData?.analytics?.satisfactionChange}
          icon={Users}
          color="green"
        />
      </div>
    </div>
  );

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-4 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">جاري تحميل لوحة التحكم الموحدة...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100" dir="rtl">
      {/* Header */}
      <div className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center space-x-4 space-x-reverse">
              <div className="p-2 bg-blue-600 rounded-lg">
                <BarChart3 className="w-6 h-6 text-white" />
              </div>
              <div>
                <h1 className="text-2xl font-bold text-gray-900">لوحة التحكم الموحدة</h1>
                <p className="text-sm text-gray-600">نظام إدهام اللوجستي المتكامل</p>
              </div>
            </div>

            <div className="flex items-center space-x-4 space-x-reverse">
              {/* Search */}
              <div className="relative">
                <Search className="absolute right-3 top-3 w-5 h-5 text-gray-400" />
                <input
                  type="text"
                  placeholder="بحث في النظام..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="w-64 pr-10 pl-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
              </div>

              {/* Notifications */}
              <div className="relative">
                <button className="relative p-2 text-gray-600 hover:text-gray-900">
                  <Bell className="w-6 h-6" />
                  {notifications.length > 0 && (
                    <span className="absolute -top-1 -right-1 w-3 h-3 bg-red-500 rounded-full"></span>
                  )}
                </button>
              </div>

              {/* User */}
              <div className="text-left">
                <p className="text-sm font-medium text-gray-900">{user.name}</p>
                <p className="text-xs text-gray-500">{user.role}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Navigation Tabs */}
      <div className="bg-white border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex space-x-8 space-x-reverse overflow-x-auto">
            {views.map((view) => {
              const Icon = view.icon;
              return (
                <button
                  key={view.id}
                  onClick={() => setActiveView(view.id)}
                  className={`flex items-center space-x-2 space-x-reverse py-4 border-b-2 transition-colors whitespace-nowrap ${
                    activeView === view.id
                      ? 'border-blue-600 text-blue-600'
                      : 'border-transparent text-gray-500 hover:text-gray-700'
                  }`}
                >
                  <Icon className="w-5 h-5" />
                  <span className="font-medium">{view.label}</span>
                </button>
              );
            })}
          </div>
        </div>
      </div>

      {/* Content */}
      <div className="max-w-7xl mx-auto px-4 py-6">
        {activeView === 'overview' && renderOverview()}
        {activeView === 'shipments' && renderShipments()}
        {activeView === 'analytics' && renderAnalytics()}
        
        {/* Other views would be rendered similarly */}
        {activeView === 'customers' && (
          <div className="text-center py-12">
            <Users className="w-16 h-16 text-gray-300 mx-auto mb-4" />
            <p className="text-gray-600">واجهة العملاء</p>
          </div>
        )}
        
        {activeView === 'fleet' && (
          <div className="text-center py-12">
            <Truck className="w-16 h-16 text-gray-300 mx-auto mb-4" />
            <p className="text-gray-600">واجهة الأسطول</p>
          </div>
        )}
        
        {activeView === 'finance' && (
          <div className="text-center py-12">
            <DollarSign className="w-16 h-16 text-gray-300 mx-auto mb-4" />
            <p className="text-gray-600">واجهة المالية</p>
          </div>
        )}
        
        {activeView === 'assets' && (
          <div className="text-center py-12">
            <Wrench className="w-16 h-16 text-gray-300 mx-auto mb-4" />
            <p className="text-gray-600">واجهة الأصول</p>
          </div>
        )}
        
        {activeView === 'reports' && (
          <div className="text-center py-12">
            <FileText className="w-16 h-16 text-gray-300 mx-auto mb-4" />
            <p className="text-gray-600">واجهة التقارير</p>
          </div>
        )}
        
        {activeView === 'settings' && (
          <div className="text-center py-12">
            <Settings className="w-16 h-16 text-gray-300 mx-auto mb-4" />
            <p className="text-gray-600">واجهة الإعدادات</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default UnifiedDashboardPage;
