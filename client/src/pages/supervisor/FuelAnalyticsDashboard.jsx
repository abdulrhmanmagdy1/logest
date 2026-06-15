/**
 * ============================================
 * ⛽ Fuel Analytics Dashboard - React Component
 * Advanced fuel analytics and visualization
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import {
  LineChart, Line, BarChart, Bar, PieChart, Pie, Cell,
  XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer
} from 'recharts';
import { motion } from 'framer-motion';
import axios from 'axios';
import {
  Droplet, TrendingDown, TrendingUp, AlertTriangle,
  Calendar, Filter, Download
} from 'lucide-react';

const FuelAnalyticsDashboard = () => {
  const [fuelStats, setFuelStats] = useState(null);
  const [fleetSummary, setFleetSummary] = useState(null);
  const [trends, setTrends] = useState([]);
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({
    startDate: new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
    endDate: new Date().toISOString().split('T')[0],
    truck: ''
  });

  const API_BASE = process.env.REACT_APP_API_URL || 'http://localhost:5000/api/v1';

  // Fetch data
  useEffect(() => {
    fetchAnalyticsData();
  }, [filters]);

  const fetchAnalyticsData = async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem('token');
      const headers = { Authorization: `Bearer ${token}` };

      // Fetch fleet summary
      const fleetRes = await axios.get(
        `${API_BASE}/fuel/analytics/fleet`,
        { headers, params: filters }
      );
      setFleetSummary(fleetRes.data.data);

      // Fetch trends
      const trendsRes = await axios.get(
        `${API_BASE}/fuel/analytics/trends`,
        { headers, params: { ...filters, days: 30 } }
      );
      setTrends(trendsRes.data.data);

      // Fetch recommendations
      const recRes = await axios.get(
        `${API_BASE}/fuel/analytics/recommendations`,
        { headers, params: filters }
      );
      setRecommendations(recRes.data.data);

      setLoading(false);
    } catch (error) {
      console.error('Error fetching analytics data:', error);
      setLoading(false);
    }
  };

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleDownloadReport = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await axios.get(
        `${API_BASE}/fuel/analytics/expense-report`,
        {
          headers: { Authorization: `Bearer ${token}` },
          params: filters,
          responseType: 'blob'
        }
      );
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `fuel-report-${new Date().toISOString().split('T')[0]}.pdf`);
      document.body.appendChild(link);
      link.click();
      link.parentElement.removeChild(link);
    } catch (error) {
      console.error('Error downloading report:', error);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <div className="animate-spin">
          <Droplet className="w-12 h-12 text-blue-500" />
        </div>
      </div>
    );
  }

  const COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444'];

  return (
    <div className="space-y-6 p-6 bg-gradient-to-br from-gray-50 to-gray-100 min-h-screen">
      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center justify-between"
      >
        <div className="flex items-center gap-3">
          <Droplet className="w-8 h-8 text-blue-500" />
          <h1 className="text-3xl font-bold text-gray-800">لوحة تحليلات الوقود</h1>
        </div>
        <button
          onClick={handleDownloadReport}
          className="flex items-center gap-2 px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition-colors"
        >
          <Download className="w-4 h-4" />
          تحميل التقرير
        </button>
      </motion.div>

      {/* Filters */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        className="bg-white rounded-lg shadow-md p-6"
      >
        <div className="flex items-center gap-2 mb-4">
          <Filter className="w-5 h-5 text-gray-600" />
          <h2 className="text-lg font-semibold text-gray-800">التصفية</h2>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <input
            type="date"
            name="startDate"
            value={filters.startDate}
            onChange={handleFilterChange}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <input
            type="date"
            name="endDate"
            value={filters.endDate}
            onChange={handleFilterChange}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <input
            type="text"
            name="truck"
            placeholder="معرّف المركبة"
            value={filters.truck}
            onChange={handleFilterChange}
            className="px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
      </motion.div>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        {fleetSummary && [
          {
            icon: Droplet,
            label: 'إجمالي الوقود',
            value: `${fleetSummary.summary.totalFuel.toFixed(0)} لتر`,
            color: 'blue'
          },
          {
            icon: TrendingDown,
            label: 'إجمالي التكاليف',
            value: `${fleetSummary.summary.totalCost.toFixed(2)} ريال`,
            color: 'red'
          },
          {
            icon: TrendingUp,
            label: 'متوسط الكفاءة',
            value: `${fleetSummary.summary.avgEfficiency || 0} كم/لتر`,
            color: 'green'
          },
          {
            icon: Calendar,
            label: 'عدد السجلات',
            value: fleetSummary.summary.recordCount || 0,
            color: 'amber'
          }
        ].map((item, idx) => (
          <motion.div
            key={idx}
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: idx * 0.1 }}
            className={`bg-gradient-to-br from-${item.color}-50 to-${item.color}-100 rounded-lg shadow-md p-6 border-l-4 border-${item.color}-500`}
          >
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-600 text-sm">{item.label}</p>
                <p className="text-2xl font-bold text-gray-800 mt-2">{item.value}</p>
              </div>
              <item.icon className={`w-12 h-12 text-${item.color}-500 opacity-20`} />
            </div>
          </motion.div>
        ))}
      </div>

      {/* Charts Row 1 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Consumption Trend */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="bg-white rounded-lg shadow-md p-6"
        >
          <h2 className="text-lg font-semibold text-gray-800 mb-4">اتجاه الاستهلاك</h2>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={trends}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="_id" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Line
                type="monotone"
                dataKey="totalFuel"
                stroke="#3b82f6"
                name="الوقود (لتر)"
                dot={{ fill: '#3b82f6' }}
              />
              <Line
                type="monotone"
                dataKey="totalCost"
                stroke="#ef4444"
                name="التكلفة (ريال)"
                dot={{ fill: '#ef4444' }}
              />
            </LineChart>
          </ResponsiveContainer>
        </motion.div>

        {/* Efficiency Trend */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="bg-white rounded-lg shadow-md p-6"
        >
          <h2 className="text-lg font-semibold text-gray-800 mb-4">كفاءة الاستهلاك</h2>
          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={trends}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="_id" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Line
                type="monotone"
                dataKey="avgEfficiency"
                stroke="#10b981"
                name="كم/لتر"
                dot={{ fill: '#10b981' }}
              />
            </LineChart>
          </ResponsiveContainer>
        </motion.div>
      </div>

      {/* Top Consumers */}
      {fleetSummary?.topTrucks && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="bg-white rounded-lg shadow-md p-6"
        >
          <h2 className="text-lg font-semibold text-gray-800 mb-4">أكثر المركبات استهلاكاً</h2>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={fleetSummary.topTrucks.slice(0, 10)}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="_id" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="totalCost" fill="#ef4444" name="التكلفة (ريال)" />
              <Bar dataKey="totalFuel" fill="#3b82f6" name="الوقود (لتر)" />
            </BarChart>
          </ResponsiveContainer>
        </motion.div>
      )}

      {/* Recommendations */}
      {recommendations.length > 0 && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          className="bg-white rounded-lg shadow-md p-6"
        >
          <div className="flex items-center gap-2 mb-4">
            <AlertTriangle className="w-5 h-5 text-amber-500" />
            <h2 className="text-lg font-semibold text-gray-800">التوصيات</h2>
          </div>
          <div className="space-y-3">
            {recommendations.map((rec, idx) => (
              <motion.div
                key={idx}
                initial={{ x: -20, opacity: 0 }}
                animate={{ x: 0, opacity: 1 }}
                transition={{ delay: idx * 0.1 }}
                className={`p-4 rounded-lg border-l-4 ${
                  rec.severity === 'high'
                    ? 'bg-red-50 border-red-500'
                    : rec.severity === 'warning'
                    ? 'bg-amber-50 border-amber-500'
                    : 'bg-blue-50 border-blue-500'
                }`}
              >
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <p className="font-semibold text-gray-800">{rec.message}</p>
                    <p className="text-gray-600 text-sm mt-1">{rec.detail}</p>
                  </div>
                  <span className={`px-3 py-1 rounded-full text-xs font-semibold whitespace-nowrap ${
                    rec.severity === 'high'
                      ? 'bg-red-200 text-red-700'
                      : rec.severity === 'warning'
                      ? 'bg-amber-200 text-amber-700'
                      : 'bg-blue-200 text-blue-700'
                  }`}>
                    {rec.severity === 'high' ? 'عالي' : rec.severity === 'warning' ? 'تحذير' : 'معلومة'}
                  </span>
                </div>
              </motion.div>
            ))}
          </div>
        </motion.div>
      )}
    </div>
  );
};

export default FuelAnalyticsDashboard;
