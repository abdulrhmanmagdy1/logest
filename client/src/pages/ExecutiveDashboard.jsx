/**
 * ============================================
 * 📊 Executive Dashboard - نظام إدهام الاحترافي
 * Edham Logistics - Advanced Analytics & Control Center
 * ============================================
 */

import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  TrendingUp, TrendingDown, BarChart3, PieChart, Activity,
  Users, Truck, Package, DollarSign, AlertTriangle,
  Calendar, Download, Filter, RefreshCw, Eye,
  Target, Zap, Shield, Clock, MapPin, ArrowUpRight,
  Settings, Bell, Search, ChevronDown, MoreVertical
} from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { useNotification } from "../context/NotificationContext";
import api from "../services/api";

const ExecutiveDashboard = () => {
  const { user } = useAuth();
  const { showToast } = useNotification();

  const [timeRange, setTimeRange] = useState('7d');
  const [selectedMetrics, setSelectedMetrics] = useState(['revenue', 'shipments', 'customers']);
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [autoRefresh, setAutoRefresh] = useState(true);
  const [alerts, setAlerts] = useState([]);

  const timeRanges = [
    { id: '24h', label: '24 ساعة', hours: 24 },
    { id: '7d', label: '7 أيام', hours: 168 },
    { id: '30d', label: '30 يوم', hours: 720 },
    { id: '90d', label: '90 يوم', hours: 2160 },
    { id: '1y', label: 'سنة', hours: 8760 }
  ];

  const availableMetrics = [
    { id: 'revenue', label: 'الإيرادات', icon: DollarSign, color: 'green' },
    { id: 'shipments', label: 'الشحنات', icon: Package, color: 'blue' },
    { id: 'customers', label: 'العملاء', icon: Users, color: 'purple' },
    { id: 'drivers', label: 'السائقون', icon: Truck, color: 'orange' },
    { id: 'efficiency', label: 'الكفاءة', icon: Zap, color: 'yellow' },
    { id: 'satisfaction', label: 'الرضا', icon: Target, color: 'pink' }
  ];

  useEffect(() => {
    fetchDashboardData();
    if (autoRefresh) {
      const interval = setInterval(fetchDashboardData, 30000); // Refresh every 30 seconds
      return () => clearInterval(interval);
    }
  }, [timeRange, selectedMetrics, autoRefresh]);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const response = await api.get('/executive/dashboard', {
        params: {
          timeRange,
          metrics: selectedMetrics.join(',')
        }
      });
      
      setDashboardData(response.data);
      
      // Check for critical alerts
      if (response.data.alerts) {
        setAlerts(response.data.alerts.filter(alert => alert.severity === 'critical'));
      }
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
      showToast('فشل تحميل بيانات لوحة التحكم', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleExportReport = async (format) => {
    try {
      const response = await api.get('/executive/export', {
        params: { format, timeRange, metrics: selectedMetrics.join(',') },
        responseType: 'blob'
      });

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.download = `executive-report-${Date.now()}.${format}`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);

      showToast(`تم تصدير التقرير بصيغة ${format.toUpperCase()}`, 'success');
    } catch (error) {
      console.error('Error exporting report:', error);
      showToast('فشل تصدير التقرير', 'error');
    }
  };

  const MetricCard = ({ metric, data }) => {
    const Icon = metric.icon;
    const change = data.change || 0;
    const isPositive = change >= 0;

    return (
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-white rounded-xl shadow-lg p-6 border border-gray-100 hover:shadow-xl transition-all duration-300"
      >
        <div className="flex items-start justify-between mb-4">
          <div className={`p-3 rounded-lg bg-${metric.color}-100`}>
            <Icon className={`w-6 h-6 text-${metric.color}-600`} />
          </div>
          <button className="text-gray-400 hover:text-gray-600">
            <MoreVertical className="w-5 h-5" />
          </button>
        </div>

        <div className="space-y-2">
          <p className="text-sm text-gray-600 font-medium">{metric.label}</p>
          <p className="text-3xl font-bold text-gray-900">
            {data.value?.toLocaleString() || '0'}
          </p>
          
          <div className="flex items-center space-x-2 space-x-reverse">
            <div className={`flex items-center space-x-1 space-x-reverse text-sm ${
              isPositive ? 'text-green-600' : 'text-red-600'
            }`}>
              {isPositive ? (
                <TrendingUp className="w-4 h-4" />
              ) : (
                <TrendingDown className="w-4 h-4" />
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

        {/* Mini Chart */}
        <div className="mt-4 h-16 bg-gradient-to-r from-gray-50 to-gray-100 rounded-lg">
          <div className="h-full flex items-end space-x-1 px-2">
            {(data.sparkline || []).map((value, index) => (
              <div
                key={index}
                className="flex-1 bg-blue-400 rounded-t"
                style={{ height: `${value}%` }}
              />
            ))}
          </div>
        </div>
      </motion.div>
    );
  };

  const AlertCard = ({ alert }) => {
    const getAlertColor = (severity) => {
      switch (severity) {
        case 'critical': return 'bg-red-50 border-red-200 text-red-800';
        case 'warning': return 'bg-yellow-50 border-yellow-200 text-yellow-800';
        case 'info': return 'bg-blue-50 border-blue-200 text-blue-800';
        default: return 'bg-gray-50 border-gray-200 text-gray-800';
      }
    };

    return (
      <motion.div
        initial={{ opacity: 0, x: -20 }}
        animate={{ opacity: 1, x: 0 }}
        className={`p-4 rounded-lg border ${getAlertColor(alert.severity)} mb-2`}
      >
        <div className="flex items-start space-x-3 space-x-reverse">
          <AlertTriangle className="w-5 h-5 mt-0.5 flex-shrink-0" />
          <div className="flex-1">
            <h4 className="font-semibold text-sm">{alert.title}</h4>
            <p className="text-xs mt-1">{alert.description}</p>
            <div className="flex items-center justify-between mt-2">
              <span className="text-xs">
                {new Date(alert.createdAt).toLocaleTimeString('ar-SA')}
              </span>
              <button className="text-xs underline hover:no-underline">
                عرض التفاصيل
              </button>
            </div>
          </div>
        </div>
      </motion.div>
    );
  };

  if (loading && !dashboardData) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-4 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600">جاري تحميل لوحة التحكم التنفيذية...</p>
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
                <h1 className="text-2xl font-bold text-gray-900">لوحة التحكم التنفيذية</h1>
                <p className="text-sm text-gray-600">نظرة شاملة على أداء النظام</p>
              </div>
            </div>

            <div className="flex items-center space-x-4 space-x-reverse">
              {/* Time Range Selector */}
              <div className="relative">
                <select
                  value={timeRange}
                  onChange={(e) => setTimeRange(e.target.value)}
                  className="appearance-none bg-white border border-gray-300 rounded-lg px-4 py-2 pr-10 text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  {timeRanges.map(range => (
                    <option key={range.id} value={range.id}>
                      {range.label}
                    </option>
                  ))}
                </select>
                <ChevronDown className="absolute left-3 top-3 w-4 h-4 text-gray-400 pointer-events-none" />
              </div>

              {/* Auto Refresh Toggle */}
              <button
                onClick={() => setAutoRefresh(!autoRefresh)}
                className={`p-2 rounded-lg border ${autoRefresh ? 'bg-blue-50 border-blue-300 text-blue-600' : 'bg-gray-50 border-gray-300 text-gray-600'}`}
              >
                <RefreshCw className={`w-4 h-4 ${autoRefresh ? 'animate-spin' : ''}`} />
              </button>

              {/* Export Button */}
              <button
                onClick={() => handleExportReport('pdf')}
                className="flex items-center space-x-2 space-x-reverse px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                <Download className="w-4 h-4" />
                <span>تصدير تقرير</span>
              </button>

              {/* Notifications */}
              <div className="relative">
                <button className="p-2 text-gray-600 hover:text-gray-900">
                  <Bell className="w-5 h-5" />
                  {alerts.length > 0 && (
                    <span className="absolute -top-1 -right-1 w-3 h-3 bg-red-500 rounded-full"></span>
                  )}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Critical Alerts */}
      {alerts.length > 0 && (
        <div className="max-w-7xl mx-auto px-4 py-4">
          <div className="bg-red-50 border border-red-200 rounded-lg p-4">
            <div className="flex items-center space-x-3 space-x-reverse">
              <AlertTriangle className="w-5 h-5 text-red-600" />
              <h3 className="font-semibold text-red-800">
                {alerts.length} تنبيهات حرجة تتطلب انتباهك
              </h3>
            </div>
            <div className="mt-3 space-y-2">
              {alerts.slice(0, 3).map((alert, index) => (
                <AlertCard key={index} alert={alert} />
              ))}
            </div>
          </div>
        </div>
      )}

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 py-6">
        {/* KPI Metrics */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
          {availableMetrics
            .filter(metric => selectedMetrics.includes(metric.id))
            .map((metric, index) => (
              <MetricCard
                key={metric.id}
                metric={metric}
                data={dashboardData?.metrics?.[metric.id] || {}}
              />
            ))}
        </div>

        {/* Charts Row */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          {/* Revenue Chart */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 }}
            className="bg-white rounded-xl shadow-lg p-6 border border-gray-100"
          >
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-lg font-semibold text-gray-900">اتجاه الإيرادات</h3>
              <button className="text-gray-400 hover:text-gray-600">
                <Eye className="w-5 h-5" />
              </button>
            </div>
            <div className="h-64 bg-gradient-to-br from-blue-50 to-indigo-100 rounded-lg flex items-center justify-center">
              <div className="text-center">
                <TrendingUp className="w-12 h-12 text-blue-600 mx-auto mb-2" />
                <p className="text-gray-600">مخطط الإيرادات</p>
              </div>
            </div>
          </motion.div>

          {/* Operations Chart */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.2 }}
            className="bg-white rounded-xl shadow-lg p-6 border border-gray-100"
          >
            <div className="flex items-center justify-between mb-6">
              <h3 className="text-lg font-semibold text-gray-900">كفاءة العمليات</h3>
              <button className="text-gray-400 hover:text-gray-600">
                <Settings className="w-5 h-5" />
              </button>
            </div>
            <div className="h-64 bg-gradient-to-br from-green-50 to-emerald-100 rounded-lg flex items-center justify-center">
              <div className="text-center">
                <Activity className="w-12 h-12 text-green-600 mx-auto mb-2" />
                <p className="text-gray-600">مخطط الكفاءة</p>
              </div>
            </div>
          </motion.div>
        </div>

        {/* Fleet Status */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="bg-white rounded-xl shadow-lg p-6 border border-gray-100 mb-8"
        >
          <div className="flex items-center justify-between mb-6">
            <h3 className="text-lg font-semibold text-gray-900">حالة الأسطول</h3>
            <div className="flex items-center space-x-2 space-x-reverse">
              <span className="text-sm text-gray-500">
                آخر تحديث: {dashboardData?.fleetStatus?.lastUpdate || 'الآن'}
              </span>
              <button
                onClick={fetchDashboardData}
                className="text-blue-600 hover:text-blue-800"
              >
                <RefreshCw className="w-4 h-4" />
              </button>
            </div>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {dashboardData?.fleetStatus?.status?.map((status, index) => (
              <div key={index} className="text-center p-4 bg-gray-50 rounded-lg">
                <div className={`w-16 h-16 mx-auto mb-2 rounded-full flex items-center justify-center ${
                  status.operational ? 'bg-green-100' : 'bg-red-100'
                }`}>
                  <Truck className={`w-8 h-8 ${status.operational ? 'text-green-600' : 'text-red-600'}`} />
                </div>
                <h4 className="font-semibold text-gray-900">{status.name}</h4>
                <p className="text-sm text-gray-600">{status.count} مركبة</p>
                <p className={`text-xs ${status.operational ? 'text-green-600' : 'text-red-600'}`}>
                  {status.percentage}% تشغيل
                </p>
              </div>
            ))}
          </div>
        </motion.div>

        {/* Quick Actions */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
          className="grid grid-cols-1 md:grid-cols-3 gap-6"
        >
          <div className="bg-gradient-to-r from-purple-600 to-indigo-600 rounded-xl p-6 text-white">
            <div className="flex items-center justify-between mb-4">
              <Users className="w-8 h-8" />
              <ArrowUpRight className="w-5 h-5" />
            </div>
            <h3 className="text-xl font-bold mb-2">إدارة العملاء</h3>
            <p className="text-sm opacity-90 mb-4">عرض وإدارة جميع العملاء والفرص</p>
            <button className="w-full bg-white bg-opacity-20 hover:bg-opacity-30 rounded-lg py-2 text-sm font-medium transition-colors">
              فتح CRM
            </button>
          </div>

          <div className="bg-gradient-to-r from-blue-600 to-cyan-600 rounded-xl p-6 text-white">
            <div className="flex items-center justify-between mb-4">
              <Truck className="w-8 h-8" />
              <ArrowUpRight className="w-5 h-5" />
            </div>
            <h3 className="text-xl font-bold mb-2">إدارة الأسطول</h3>
            <p className="text-sm opacity-90 mb-4">مراقبة وصيانة أسطول النقل</p>
            <button className="w-full bg-white bg-opacity-20 hover:bg-opacity-30 rounded-lg py-2 text-sm font-medium transition-colors">
              فتح الأسطول
            </button>
          </div>

          <div className="bg-gradient-to-r from-green-600 to-emerald-600 rounded-xl p-6 text-white">
            <div className="flex items-center justify-between mb-4">
              <BarChart3 className="w-8 h-8" />
              <ArrowUpRight className="w-5 h-5" />
            </div>
            <h3 className="text-xl font-bold mb-2">التقارير التحليلية</h3>
            <p className="text-sm opacity-90 mb-4">تقارير مفصلة وأداء متقدم</p>
            <button className="w-full bg-white bg-opacity-20 hover:bg-opacity-30 rounded-lg py-2 text-sm font-medium transition-colors">
              فتح التقارير
            </button>
          </div>
        </motion.div>
      </div>
    </div>
  );
};

export default ExecutiveDashboard;
