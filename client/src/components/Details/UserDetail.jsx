/**
 * ============================================
 * 👤 User Detail Component - نظام إدهام
 * Edham Logistics - User Detail
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import api from '../../services/api';
import { Loader, AlertCircle, User, Mail, Phone, MapPin, Calendar, Shield, Edit, Trash2, Clock } from 'lucide-react';

export default function UserDetail() {
  const { id } = useParams();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchUser();
  }, [id]);

  const fetchUser = async () => {
    try {
      setLoading(true);
      const response = await api.get(`/users/${id}`);
      setUser(response.data);
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

  if (!user) {
    return (
      <div className="bg-gray-800 p-8 rounded-lg text-center">
        <User className="w-16 h-16 text-gray-600 mx-auto mb-4" />
        <p className="text-gray-400">المستخدم غير موجود</p>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto p-6">
      {/* Header */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-white">تفاصيل المستخدم</h1>
          <p className="text-gray-400 mt-1">{user.name}</p>
        </div>
        <div className="flex gap-2">
          <button className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded flex items-center gap-2">
            <Edit className="w-4 h-4" />
            تعديل
          </button>
          <button className="bg-red-600 hover:bg-red-700 text-white font-bold py-2 px-4 rounded flex items-center gap-2">
            <Trash2 className="w-4 h-4" />
            حذف
          </button>
        </div>
      </div>

      {/* Status Badge */}
      <div className="bg-gray-800 p-6 rounded-lg mb-6">
        <div className="flex items-center justify-between">
          <div className="flex gap-4">
            <div>
              <h2 className="text-xl font-bold text-white mb-2">الحالة</h2>
              <UserStatusBadge status={user.status} />
            </div>
            <div>
              <h2 className="text-xl font-bold text-white mb-2">الدور</h2>
              <RoleBadge role={user.role} />
            </div>
          </div>
          <div className="text-right">
            <p className="text-gray-400 text-sm">تاريخ التسجيل</p>
            <p className="text-white font-semibold">
              {new Date(user.createdAt).toLocaleDateString('ar-SA')}
            </p>
          </div>
        </div>
      </div>

      {/* Personal Info */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
        {/* Basic Info */}
        <div className="bg-gray-800 p-6 rounded-lg">
          <h3 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
            <User className="w-5 h-5" />
            المعلومات الشخصية
          </h3>
          <div className="space-y-2 text-gray-300">
            <p><span className="text-gray-400">الاسم:</span> {user.name}</p>
            <p><span className="text-gray-400">البريد:</span> {user.email}</p>
            <p><span className="text-gray-400">الهاتف:</span> {user.phone || 'غير محدد'}</p>
            <p><span className="text-gray-400">الجنسية:</span> {user.nationality || 'غير محدد'}</p>
            <p><span className="text-gray-400">تاريخ الميلاد:</span> {user.dateOfBirth ? new Date(user.dateOfBirth).toLocaleDateString('ar-SA') : 'غير محدد'}</p>
          </div>
        </div>

        {/* Address */}
        <div className="bg-gray-800 p-6 rounded-lg">
          <h3 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
            <MapPin className="w-5 h-5" />
            العنوان
          </h3>
          <div className="space-y-2 text-gray-300">
            <p><span className="text-gray-400">المدينة:</span> {user.city || 'غير محدد'}</p>
            <p><span className="text-gray-400">العنوان:</span> {user.address || 'غير محدد'}</p>
            <p><span className="text-gray-400">الرمز البريدي:</span> {user.postalCode || 'غير محدد'}</p>
          </div>
        </div>
      </div>

      {/* Account Info */}
      <div className="bg-gray-800 p-6 rounded-lg mb-6">
        <h3 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
          <Shield className="w-5 h-5" />
          معلومات الحساب
        </h3>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-gray-300">
          <div>
            <p className="text-gray-400 text-sm">الدور</p>
            <p className="text-white font-semibold">{user.role}</p>
          </div>
          <div>
            <p className="text-gray-400 text-sm">الحالة</p>
            <p className="text-white font-semibold">{user.status}</p>
          </div>
          <div>
            <p className="text-gray-400 text-sm">آخر تسجيل دخول</p>
            <p className="text-white">
              {user.lastLogin ? new Date(user.lastLogin).toLocaleString('ar-SA') : 'غير محدد'}
            </p>
          </div>
          <div>
            <p className="text-gray-400 text-sm">تم التحقق</p>
            <p className="text-white font-semibold">{user.verified ? 'نعم' : 'لا'}</p>
          </div>
        </div>
      </div>

      {/* Company Info (if applicable) */}
      {user.company && (
        <div className="bg-gray-800 p-6 rounded-lg mb-6">
          <h3 className="text-lg font-bold text-white mb-4">معلومات الشركة</h3>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-gray-300">
            <div>
              <p className="text-gray-400 text-sm">اسم الشركة</p>
              <p className="text-white font-semibold">{user.company.name}</p>
            </div>
            <div>
              <p className="text-gray-400 text-sm">الرقم الضريبي</p>
              <p className="text-white">{user.company.taxNumber || 'غير محدد'}</p>
            </div>
            <div>
              <p className="text-gray-400 text-sm">السجل التجاري</p>
              <p className="text-white">{user.company.commercialRegistration || 'غير محدد'}</p>
            </div>
            <div>
              <p className="text-gray-400 text-sm">البريد</p>
              <p className="text-white">{user.company.email || 'غير محدد'}</p>
            </div>
          </div>
        </div>
      )}

      {/* Activity */}
      <div className="bg-gray-800 p-6 rounded-lg mb-6">
        <h3 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
          <Clock className="w-5 h-5" />
          النشاط الأخير
        </h3>
        <div className="space-y-3">
          <ActivityItem
            action="تسجيل الدخول"
            time={user.lastLogin ? new Date(user.lastLogin).toLocaleString('ar-SA') : 'غير محدد'}
          />
          <ActivityItem
            action="تحديث الملف"
            time={user.updatedAt ? new Date(user.updatedAt).toLocaleString('ar-SA') : 'غير محدد'}
          />
          <ActivityItem
            action="إنشاء الحساب"
            time={user.createdAt ? new Date(user.createdAt).toLocaleString('ar-SA') : 'غير محدد'}
          />
        </div>
      </div>

      {/* Permissions */}
      <div className="bg-gray-800 p-6 rounded-lg">
        <h3 className="text-lg font-bold text-white mb-4">الصلاحيات</h3>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <PermissionItem permission="عرض الشحنات" granted={true} />
          <PermissionItem permission="إنشاء شحنات" granted={user.role === 'admin' || user.role === 'supervisor' || user.role === 'client'} />
          <PermissionItem permission="إدارة المستخدمين" granted={user.role === 'admin'} />
          <PermissionItem permission="إدارة الفواتير" granted={user.role === 'admin' || user.role === 'accountant'} />
          <PermissionItem permission="إدارة الشاحنات" granted={user.role === 'admin' || user.role === 'supervisor'} />
          <PermissionItem permission="تتبع الموقع" granted={user.role === 'driver'} />
          <PermissionItem permission="إدارة الصيانة" granted={user.role === 'admin' || user.role === 'maintenance'} />
          <PermissionItem permission="عرض التقارير" granted={user.role === 'admin' || user.role === 'accountant'} />
        </div>
      </div>
    </div>
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
    <span className={`${statusColors[status] || 'bg-gray-500'} px-4 py-2 rounded text-lg font-semibold`}>
      {statusLabels[status] || status}
    </span>
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
    <span className={`${roleColors[role] || 'bg-gray-500'} px-4 py-2 rounded text-lg font-semibold flex items-center gap-2`}>
      <Shield className="w-4 h-4" />
      {roleLabels[role] || role}
    </span>
  );
}

function ActivityItem({ action, time }) {
  return (
    <div className="flex items-center justify-between p-3 bg-gray-700 rounded">
      <p className="text-white font-semibold">{action}</p>
      <p className="text-gray-400 text-sm">{time}</p>
    </div>
  );
}

function PermissionItem({ permission, granted }) {
  return (
    <div className={`p-3 rounded ${granted ? 'bg-green-900/30 border border-green-500' : 'bg-red-900/30 border border-red-500'}`}>
      <p className="text-white font-semibold mb-1">{permission}</p>
      <p className={`text-sm ${granted ? 'text-green-400' : 'text-red-400'}`}>
        {granted ? 'ممنوح' : 'غير ممنوح'}
      </p>
    </div>
  );
}
