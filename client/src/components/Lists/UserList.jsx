/**
 * ============================================
 * 👥 User List Component - نظام إدهام
 * Edham Logistics - User List
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import api from '../../services/api';
import { Loader, AlertCircle, User, Search, Filter, Plus, Shield } from 'lucide-react';

export default function UserList() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterRole, setFilterRole] = useState('all');
  const [filterStatus, setFilterStatus] = useState('all');
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  useEffect(() => {
    fetchUsers();
  }, [page, filterRole, filterStatus]);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const params = {
        page,
        limit: 20,
        ...(filterRole !== 'all' && { role: filterRole }),
        ...(filterStatus !== 'all' && { status: filterStatus })
      };
      const response = await api.get('/users', { params });
      setUsers(response.data.data);
      setTotalPages(response.data.pages || 1);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const filteredUsers = users.filter(user =>
    user.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    user.email?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    user.phone?.toLowerCase().includes(searchTerm.toLowerCase())
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
        <h1 className="text-3xl font-bold text-white">المستخدمين</h1>
        <button className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded flex items-center gap-2">
          <Plus className="w-4 h-4" />
          مستخدم جديد
        </button>
      </div>

      {/* Search and Filter */}
      <div className="flex gap-4">
        <div className="flex-1 relative">
          <Search className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
          <input
            type="text"
            placeholder="بحث عن مستخدم..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full bg-gray-800 text-white pr-10 pl-4 py-2 rounded border border-gray-700 focus:border-blue-500 outline-none"
          />
        </div>
        <select
          value={filterRole}
          onChange={(e) => setFilterRole(e.target.value)}
          className="bg-gray-800 text-white px-4 py-2 rounded border border-gray-700 focus:border-blue-500 outline-none"
        >
          <option value="all">جميع الأدوار</option>
          <option value="admin">مشرف</option>
          <option value="supervisor">مشرف ميداني</option>
          <option value="accountant">محاسب</option>
          <option value="driver">سائق</option>
          <option value="client">عميل</option>
          <option value="employee">موظف</option>
          <option value="maintenance">صيانة</option>
        </select>
        <select
          value={filterStatus}
          onChange={(e) => setFilterStatus(e.target.value)}
          className="bg-gray-800 text-white px-4 py-2 rounded border border-gray-700 focus:border-blue-500 outline-none"
        >
          <option value="all">جميع الحالات</option>
          <option value="active">نشط</option>
          <option value="inactive">غير نشط</option>
          <option value="suspended">معلق</option>
        </select>
      </div>

      {/* Users Table */}
      <div className="bg-gray-800 rounded-lg overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-white text-sm">
            <thead className="bg-gray-700">
              <tr>
                <th className="px-4 py-3 text-right">الاسم</th>
                <th className="px-4 py-3 text-right">البريد الإلكتروني</th>
                <th className="px-4 py-3 text-right">الهاتف</th>
                <th className="px-4 py-3 text-right">الدور</th>
                <th className="px-4 py-3 text-right">المدينة</th>
                <th className="px-4 py-3 text-center">الحالة</th>
                <th className="px-4 py-3 text-right">تاريخ التسجيل</th>
                <th className="px-4 py-3 text-center">الإجراء</th>
              </tr>
            </thead>
            <tbody>
              {filteredUsers.length === 0 ? (
                <tr>
                  <td colSpan="8" className="px-4 py-8 text-center text-gray-400">
                    لا يوجد مستخدمين
                  </td>
                </tr>
              ) : (
                filteredUsers.map((user) => (
                  <tr key={user._id} className="border-t border-gray-700 hover:bg-gray-700">
                    <td className="px-4 py-3 font-semibold">{user.name}</td>
                    <td className="px-4 py-3">{user.email}</td>
                    <td className="px-4 py-3">{user.phone || 'غير محدد'}</td>
                    <td className="px-4 py-3">
                      <RoleBadge role={user.role} />
                    </td>
                    <td className="px-4 py-3">{user.city || 'غير محدد'}</td>
                    <td className="px-4 py-3 text-center">
                      <UserStatusBadge status={user.status} />
                    </td>
                    <td className="px-4 py-3">
                      {new Date(user.createdAt).toLocaleDateString('ar-SA')}
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

function RoleBadge({ role }) {
  const roleColors = {
    admin: 'bg-red-500',
    supervisor: 'bg-purple-500',
    accountant: 'bg-yellow-500',
    driver: 'bg-green-500',
    client: 'bg-blue-500',
    employee: 'bg-gray-500',
    maintenance: 'bg-orange-500'
  };

  const roleLabels = {
    admin: 'مشرف',
    supervisor: 'مشرف ميداني',
    accountant: 'محاسب',
    driver: 'سائق',
    client: 'عميل',
    employee: 'موظف',
    maintenance: 'صيانة'
  };

  return (
    <span className={`${roleColors[role] || 'bg-gray-500'} px-3 py-1 rounded text-xs flex items-center gap-1`}>
      <Shield className="w-3 h-3" />
      {roleLabels[role] || role}
    </span>
  );
}

function UserStatusBadge({ status }) {
  const statusColors = {
    active: 'bg-green-500',
    inactive: 'bg-gray-500',
    suspended: 'bg-red-500'
  };

  const statusLabels = {
    active: 'نشط',
    inactive: 'غير نشط',
    suspended: 'معلق'
  };

  return (
    <span className={`${statusColors[status] || 'bg-gray-500'} px-3 py-1 rounded text-xs`}>
      {statusLabels[status] || status}
    </span>
  );
}
