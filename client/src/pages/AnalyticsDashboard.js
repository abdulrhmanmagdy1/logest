import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import logger from '../utils/logger';
import axios from 'axios';
import { 
  BarChart2, 
  TrendingUp, 
  Users, 
  Truck, 
  Package, 
  DollarSign, 
  LogOut, 
  Menu, 
  X,
  ArrowLeft,
  Clock,
  CheckCircle,
  AlertCircle,
  BarChart3,
  PieChart,
  Download
} from 'lucide-react';
import Logo from '../components/Logo';

const AnalyticsDashboard = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [stats, setStats] = useState({
    totalShipments: 0,
    activeShipments: 0,
    completedShipments: 0,
    totalTrucks: 0,
    totalDrivers: 0,
    totalRevenue: 0,
    averageDeliveryTime: 0,
    onTimeDeliveryRate: 0
  });
  const [loading, setLoading] = useState(true);
  const [timeRange, setTimeRange] = useState('month');

  useEffect(() => {
    fetchAnalytics();
  }, [timeRange]);

  const fetchAnalytics = async () => {
    try {
      const response = await axios.get(`http://192.168.1.12:5000/api/analytics?timeRange=${timeRange}`);
      setStats(response.data);
    } catch (error) {
      logger.error('Error fetching analytics:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleExportReport = async () => {
    try {
      const response = await axios.get(`http://192.168.1.12:5000/api/analytics/export?timeRange=${timeRange}`, {
        responseType: 'blob'
      });
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `analytics-report-${timeRange}.xlsx`);
      document.body.appendChild(link);
      link.click();
      link.remove();
    } catch (error) {
      logger.error('Error exporting report:', error);
      alert('حدث خطأ أثناء تصدير التقرير');
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-edham-black flex items-center justify-center">
        <div className="text-edham-white text-2xl">جاري التحميل...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-edham-black">
      {/* Header */}
      <div className="bg-edham-dark border-b border-edham-gray">
        <div className="container mx-auto px-4 py-4 flex justify-between items-center">
          <div className="flex items-center gap-4">
            <button onClick={() => navigate('/dashboard')} className="text-edham-white hover:text-edham-gold transition-colors">
              <ArrowLeft className="w-6 h-6" />
            </button>
            <div className="flex items-center gap-2">
              <div className="bg-edham-white p-1.5 rounded-full">
                <Logo size="sm" />
              </div>
              <h1 className="text-xl font-bold text-edham-white">لوحة التحليلات</h1>
            </div>
          </div>
          <div className="flex items-center gap-4">
            <select
              value={timeRange}
              onChange={(e) => setTimeRange(e.target.value)}
              className="bg-edham-black border border-edham-gray rounded-lg px-4 py-2 text-edham-white"
            >
              <option value="today">اليوم</option>
              <option value="week">الأسبوع</option>
              <option value="month">الشهر</option>
              <option value="year">السنة</option>
            </select>
            <button
              onClick={handleExportReport}
              className="bg-edham-primary text-white px-4 py-2 rounded-lg hover:bg-edham-primaryLight transition-all flex items-center gap-2"
            >
              <Download className="w-4 h-4" />
              تصدير التقرير
            </button>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          {/* Total Shipments */}
          <div className="bg-edham-white/10 backdrop-blur-lg rounded-2xl p-6 border border-edham-gray">
            <div className="flex items-center justify-between mb-4">
              <div className="bg-edham-blue/20 p-3 rounded-xl">
                <Package className="w-6 h-6 text-edham-blue" />
              </div>
              <span className="text-edham-green text-sm font-semibold flex items-center gap-1">
                <TrendingUp className="w-4 h-4" />
                +12%
              </span>
            </div>
            <h3 className="text-3xl font-bold text-edham-white mb-2">{stats.totalShipments}</h3>
            <p className="text-edham-white/70 text-sm">إجمالي الحمولات</p>
          </div>

          {/* Active Shipments */}
          <div className="bg-edham-white/10 backdrop-blur-lg rounded-2xl p-6 border border-edham-gray">
            <div className="flex items-center justify-between mb-4">
              <div className="bg-edham-yellow/20 p-3 rounded-xl">
                <Clock className="w-6 h-6 text-edham-yellow" />
              </div>
              <span className="text-edham-green text-sm font-semibold flex items-center gap-1">
                <TrendingUp className="w-4 h-4" />
                +5%
              </span>
            </div>
            <h3 className="text-3xl font-bold text-edham-white mb-2">{stats.activeShipments}</h3>
            <p className="text-edham-white/70 text-sm">الحمولات النشطة</p>
          </div>

          {/* Completed Shipments */}
          <div className="bg-edham-white/10 backdrop-blur-lg rounded-2xl p-6 border border-edham-gray">
            <div className="flex items-center justify-between mb-4">
              <div className="bg-edham-green/20 p-3 rounded-xl">
                <CheckCircle className="w-6 h-6 text-edham-green" />
              </div>
              <span className="text-edham-green text-sm font-semibold flex items-center gap-1">
                <TrendingUp className="w-4 h-4" />
                +8%
              </span>
            </div>
            <h3 className="text-3xl font-bold text-edham-white mb-2">{stats.completedShipments}</h3>
            <p className="text-edham-white/70 text-sm">الحمولات المكتملة</p>
          </div>

          {/* Total Revenue */}
          <div className="bg-edham-white/10 backdrop-blur-lg rounded-2xl p-6 border border-edham-gray">
            <div className="flex items-center justify-between mb-4">
              <div className="bg-edham-primary/20 p-3 rounded-xl">
                <DollarSign className="w-6 h-6 text-edham-primary" />
              </div>
              <span className="text-edham-green text-sm font-semibold flex items-center gap-1">
                <TrendingUp className="w-4 h-4" />
                +15%
              </span>
            </div>
            <h3 className="text-3xl font-bold text-edham-white mb-2">${stats.totalRevenue.toLocaleString()}</h3>
            <p className="text-edham-white/70 text-sm">إجمالي الإيرادات</p>
          </div>
        </div>

        {/* Secondary Stats */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          {/* Total Trucks */}
          <div className="bg-edham-white/10 backdrop-blur-lg rounded-2xl p-6 border border-edham-gray">
            <div className="flex items-center gap-3 mb-4">
              <div className="bg-edham-purple/20 p-3 rounded-xl">
                <Truck className="w-6 h-6 text-edham-purple" />
              </div>
              <h3 className="text-xl font-bold text-edham-white">{stats.totalTrucks}</h3>
            </div>
            <p className="text-edham-white/70 text-sm">إجمالي الشاحنات</p>
          </div>

          {/* Total Drivers */}
          <div className="bg-edham-white/10 backdrop-blur-lg rounded-2xl p-6 border border-edham-gray">
            <div className="flex items-center gap-3 mb-4">
              <div className="bg-edham-cyan/20 p-3 rounded-xl">
                <Users className="w-6 h-6 text-edham-cyan" />
              </div>
              <h3 className="text-xl font-bold text-edham-white">{stats.totalDrivers}</h3>
            </div>
            <p className="text-edham-white/70 text-sm">إجمالي السائقين</p>
          </div>

          {/* Average Delivery Time */}
          <div className="bg-edham-white/10 backdrop-blur-lg rounded-2xl p-6 border border-edham-gray">
            <div className="flex items-center gap-3 mb-4">
              <div className="bg-edham-orange/20 p-3 rounded-xl">
                <Clock className="w-6 h-6 text-edham-orange" />
              </div>
              <h3 className="text-xl font-bold text-edham-white">{stats.averageDeliveryTime}h</h3>
            </div>
            <p className="text-edham-white/70 text-sm">متوسط وقت التسليم</p>
          </div>

          {/* On-Time Delivery Rate */}
          <div className="bg-edham-white/10 backdrop-blur-lg rounded-2xl p-6 border border-edham-gray">
            <div className="flex items-center gap-3 mb-4">
              <div className="bg-edham-green/20 p-3 rounded-xl">
                <CheckCircle className="w-6 h-6 text-edham-green" />
              </div>
              <h3 className="text-xl font-bold text-edham-white">{stats.onTimeDeliveryRate}%</h3>
            </div>
            <p className="text-edham-white/70 text-sm">معدل التسليم في الوقت</p>
          </div>
        </div>

        {/* Charts Section */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Performance Chart */}
          <div className="bg-edham-white/10 backdrop-blur-lg rounded-2xl p-6 border border-edham-gray">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-xl font-bold text-edham-white flex items-center gap-2">
                <BarChart3 className="w-5 h-5" />
                أداء الحمولات
              </h2>
            </div>
            <div className="h-64 flex items-end justify-between gap-4">
              {[65, 80, 45, 90, 75, 85, 95].map((value, index) => (
                <div key={index} className="flex-1 flex flex-col items-center gap-2">
                  <div
                    className="w-full bg-gradient-to-t from-edham-primary to-edham-primaryLight rounded-t-lg transition-all hover:opacity-80"
                    style={{ height: `${value}%` }}
                  />
                  <span className="text-edham-white/50 text-xs">
                    {['الأحد', 'الاثنين', 'الثلاثاء', 'الأربعاء', 'الخميس', 'الجمعة', 'السبت'][index]}
                  </span>
                </div>
              ))}
            </div>
          </div>

          {/* Distribution Chart */}
          <div className="bg-edham-white/10 backdrop-blur-lg rounded-2xl p-6 border border-edham-gray">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-xl font-bold text-edham-white flex items-center gap-2">
                <PieChart className="w-5 h-5" />
                توزيع الحمولات
              </h2>
            </div>
            <div className="space-y-4">
              <div>
                <div className="flex justify-between mb-2">
                  <span className="text-edham-white">مكتملة</span>
                  <span className="text-edham-green">65%</span>
                </div>
                <div className="w-full bg-edham-black rounded-full h-2">
                  <div className="bg-edham-green h-2 rounded-full" style={{ width: '65%' }} />
                </div>
              </div>
              <div>
                <div className="flex justify-between mb-2">
                  <span className="text-edham-white">في الطريق</span>
                  <span className="text-edham-blue">20%</span>
                </div>
                <div className="w-full bg-edham-black rounded-full h-2">
                  <div className="bg-edham-blue h-2 rounded-full" style={{ width: '20%' }} />
                </div>
              </div>
              <div>
                <div className="flex justify-between mb-2">
                  <span className="text-edham-white">قيد الانتظار</span>
                  <span className="text-edham-yellow">10%</span>
                </div>
                <div className="w-full bg-edham-black rounded-full h-2">
                  <div className="bg-edham-yellow h-2 rounded-full" style={{ width: '10%' }} />
                </div>
              </div>
              <div>
                <div className="flex justify-between mb-2">
                  <span className="text-edham-white">ملغية</span>
                  <span className="text-edham-error">5%</span>
                </div>
                <div className="w-full bg-edham-black rounded-full h-2">
                  <div className="bg-edham-error h-2 rounded-full" style={{ width: '5%' }} />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AnalyticsDashboard;
