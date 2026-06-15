/**
 * ============================================
 * 📋 Audit Log Page - نظام إدهام
 * Edham Logistics - Audit Log Page
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import api from '../services/api';
import { Loader, AlertCircle, FileText, Filter, Search, User, Clock, Activity } from 'lucide-react';

export default function AuditLogPage() {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filterAction, setFilterAction] = useState('all');
  const [filterUser, setFilterUser] = useState('');
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    fetchLogs();
  }, [filterAction, filterUser]);

  const fetchLogs = async () => {
    try {
      setLoading(true);
      const params = {};
      if (filterAction !== 'all') params.action = filterAction;
      if (filterUser) params.user = filterUser;
      const response = await api.get('/audit-logs', { params });
      setLogs(response.data.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const filteredLogs = logs.filter(log => {
    if (searchTerm) {
      const searchLower = searchTerm.toLowerCase();
      return (
        log.action?.toLowerCase().includes(searchLower) ||
        log.entity?.toLowerCase().includes(searchLower) ||
        log.details?.toLowerCase().includes(searchLower)
      );
    }
    return true;
  });

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
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-white">سجل التدقيق</h1>
        <p className="text-gray-400 mt-1">تتبع جميع العمليات والأنشطة في النظام</p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <StatCard title="إجمالي العمليات" value={logs.length} color="blue" />
        <StatCard title="اليوم" value="24" color="green" />
        <StatCard title="هذا الأسبوع" value="156" color="yellow" />
        <StatCard title="هذا الشهر" value="642" color="gold" />
      </div>

      {/* Filters */}
      <div className="flex flex-wrap gap-4 mb-6">
        <select
          value={filterAction}
          onChange={(e) => setFilterAction(e.target.value)}
          className="bg-gray-800 text-white px-4 py-2 rounded border border-gray-700 focus:border-blue-500 outline-none"
        >
          <option value="all">جميع العمليات</option>
          <option value="create">إنشاء</option>
          <option value="update">تحديث</option>
          <option value="delete">حذف</option>
          <option value="login">تسجيل دخول</option>
          <option value="logout">تسجيل خروج</option>
        </select>

        <input
          type="text"
          value={filterUser}
          onChange={(e) => setFilterUser(e.target.value)}
          placeholder="تصفية بالمستخدم"
          className="bg-gray-800 text-white px-4 py-2 rounded border border-gray-700 focus:border-blue-500 outline-none"
        />

        <div className="flex-1 relative">
          <Search className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="بحث في السجل..."
            className="w-full bg-gray-800 text-white pr-10 pl-4 py-2 rounded border border-gray-700 focus:border-blue-500 outline-none"
          />
        </div>
      </div>

      {/* Logs Table */}
      <div className="bg-gray-800 rounded-lg overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-white text-sm">
            <thead className="bg-gray-700">
              <tr>
                <th className="px-4 py-3 text-right">التاريخ</th>
                <th className="px-4 py-3 text-right">المستخدم</th>
                <th className="px-4 py-3 text-right">العملية</th>
                <th className="px-4 py-3 text-right">الكيان</th>
                <th className="px-4 py-3 text-right">التفاصيل</th>
                <th className="px-4 py-3 text-right">IP</th>
              </tr>
            </thead>
            <tbody>
              {filteredLogs.length === 0 ? (
                <tr>
                  <td colSpan="6" className="px-4 py-8 text-center text-gray-400">
                    لا توجد سجلات
                  </td>
                </tr>
              ) : (
                filteredLogs.map((log) => (
                  <tr key={log._id} className="border-t border-gray-700 hover:bg-gray-700">
                    <td className="px-4 py-3">
                      {new Date(log.timestamp).toLocaleString('ar-SA')}
                    </td>
                    <td className="px-4 py-3">
                      <div className="flex items-center gap-2">
                        <User className="w-4 h-4 text-gray-400" />
                        <span>{log.user?.name || 'نظام'}</span>
                      </div>
                    </td>
                    <td className="px-4 py-3">
                      <ActionBadge action={log.action} />
                    </td>
                    <td className="px-4 py-3">{log.entity || '-'}</td>
                    <td className="px-4 py-3 text-gray-400 max-w-xs truncate">
                      {log.details || '-'}
                    </td>
                    <td className="px-4 py-3 text-gray-500 text-xs">
                      {log.ipAddress || '-'}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Activity Summary */}
      <div className="mt-6 bg-gray-800 p-6 rounded-lg">
        <h2 className="text-xl font-bold text-white mb-4 flex items-center gap-2">
          <Activity className="w-5 h-5" />
          ملخص النشاط
        </h2>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <ActivitySummary
            title="أكثر المستخدمين نشاطاً"
            items={[
              { name: 'أحمد محمد', count: 45 },
              { name: 'خالد عبدالله', count: 32 },
              { name: 'سعود علي', count: 28 }
            ]}
          />
          <ActivitySummary
            title="أكثر العمليات شيوعاً"
            items={[
              { name: 'تحديث الشحنات', count: 67 },
              { name: 'إنشاء فواتير', count: 45 },
              { name: 'تسجيل دخول', count: 89 }
            ]}
          />
          <ActivitySummary
            title="الكيانات الأكثر تعديلاً"
            items={[
              { name: 'الشحنات', count: 156 },
              { name: 'الشاحنات', count: 78 },
              { name: 'الفواتير', count: 45 }
            ]}
          />
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
    gold: 'bg-yellow-500'
  };

  return (
    <div className={`${colors[color]} p-6 rounded-lg text-white`}>
      <h3 className="text-gray-100 mb-2">{title}</h3>
      <p className="text-2xl font-bold">{value}</p>
    </div>
  );
}

function ActionBadge({ action }) {
  const actionColors = {
    create: 'bg-green-500',
    update: 'bg-blue-500',
    delete: 'bg-red-500',
    login: 'bg-purple-500',
    logout: 'bg-gray-500',
    view: 'bg-yellow-500'
  };

  const actionLabels = {
    create: 'إنشاء',
    update: 'تحديث',
    delete: 'حذف',
    login: 'تسجيل دخول',
    logout: 'تسجيل خروج',
    view: 'عرض'
  };

  return (
    <span className={`${actionColors[action] || 'bg-gray-500'} px-3 py-1 rounded text-xs`}>
      {actionLabels[action] || action}
    </span>
  );
}

function ActivitySummary({ title, items }) {
  return (
    <div>
      <h3 className="text-white font-semibold mb-3">{title}</h3>
      <div className="space-y-2">
        {items.map((item, index) => (
          <div key={index} className="flex justify-between text-gray-300">
            <span>{item.name}</span>
            <span className="text-white font-semibold">{item.count}</span>
          </div>
        ))}
      </div>
    </div>
  );
}
