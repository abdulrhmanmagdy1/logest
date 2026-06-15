/**
 * ============================================
 * 👤 Profile Page - نظام إدهام
 * Edham Logistics - Profile Page
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { Loader, AlertCircle, User, Mail, Phone, MapPin, Save, Camera } from 'lucide-react';

export default function ProfilePage() {
  const { user, updateProfile } = useAuth();
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    address: '',
    city: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  useEffect(() => {
    if (user) {
      setFormData({
        name: user.name || '',
        email: user.email || '',
        phone: user.phone || '',
        address: user.address || '',
        city: user.city || ''
      });
    }
  }, [user]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);
    setLoading(true);

    try {
      await updateProfile(formData);
      setSuccess('تم تحديث الملف الشخصي بنجاح');
    } catch (err) {
      setError(err.message || 'خطأ في تحديث الملف الشخصي');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto p-6">
      <h1 className="text-3xl font-bold text-white mb-6">الملف الشخصي</h1>

      {error && (
        <div className="bg-red-500 text-white p-4 rounded mb-4 flex items-center gap-2">
          <AlertCircle className="w-5 h-5" />
          {error}
        </div>
      )}

      {success && (
        <div className="bg-green-500 text-white p-4 rounded mb-4">
          {success}
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Profile Picture */}
        <div className="bg-gray-800 p-6 rounded-lg">
          <div className="flex flex-col items-center">
            <div className="w-32 h-32 bg-blue-600 rounded-full flex items-center justify-center mb-4">
              <User className="w-16 h-16 text-white" />
            </div>
            <button className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded flex items-center gap-2">
              <Camera className="w-4 h-4" />
              تغيير الصورة
            </button>
          </div>
        </div>

        {/* Profile Form */}
        <div className="md:col-span-2 bg-gray-800 p-6 rounded-lg">
          <form onSubmit={handleSubmit}>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-gray-300 mb-2 flex items-center gap-2">
                  <User className="w-4 h-4" />
                  الاسم
                </label>
                <input
                  type="text"
                  name="name"
                  value={formData.name}
                  onChange={handleChange}
                  className="w-full bg-gray-700 text-white px-4 py-2 rounded border border-gray-600 focus:border-blue-500 outline-none"
                  required
                />
              </div>

              <div>
                <label className="block text-gray-300 mb-2 flex items-center gap-2">
                  <Mail className="w-4 h-4" />
                  البريد الإلكتروني
                </label>
                <input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  className="w-full bg-gray-700 text-white px-4 py-2 rounded border border-gray-600 focus:border-blue-500 outline-none"
                  disabled
                />
              </div>

              <div>
                <label className="block text-gray-300 mb-2 flex items-center gap-2">
                  <Phone className="w-4 h-4" />
                  رقم الهاتف
                </label>
                <input
                  type="tel"
                  name="phone"
                  value={formData.phone}
                  onChange={handleChange}
                  className="w-full bg-gray-700 text-white px-4 py-2 rounded border border-gray-600 focus:border-blue-500 outline-none"
                />
              </div>

              <div>
                <label className="block text-gray-300 mb-2 flex items-center gap-2">
                  <MapPin className="w-4 h-4" />
                  المدينة
                </label>
                <input
                  type="text"
                  name="city"
                  value={formData.city}
                  onChange={handleChange}
                  className="w-full bg-gray-700 text-white px-4 py-2 rounded border border-gray-600 focus:border-blue-500 outline-none"
                />
              </div>

              <div className="md:col-span-2">
                <label className="block text-gray-300 mb-2 flex items-center gap-2">
                  <MapPin className="w-4 h-4" />
                  العنوان
                </label>
                <textarea
                  name="address"
                  value={formData.address}
                  onChange={handleChange}
                  rows="3"
                  className="w-full bg-gray-700 text-white px-4 py-2 rounded border border-gray-600 focus:border-blue-500 outline-none"
                />
              </div>
            </div>

            <div className="mt-6 flex justify-end">
              <button
                type="submit"
                disabled={loading}
                className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-6 rounded flex items-center gap-2 disabled:opacity-50"
              >
                {loading ? (
                  <>
                    <Loader className="animate-spin w-4 h-4" />
                    جاري الحفظ...
                  </>
                ) : (
                  <>
                    <Save className="w-4 h-4" />
                    حفظ التغييرات
                  </>
                )}
              </button>
            </div>
          </form>
        </div>
      </div>

      {/* Account Info */}
      <div className="mt-6 bg-gray-800 p-6 rounded-lg">
        <h2 className="text-xl font-bold text-white mb-4">معلومات الحساب</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-gray-300">
          <div>
            <span className="text-gray-400">الدور:</span>
            <span className="mr-2">{user?.role || 'غير محدد'}</span>
          </div>
          <div>
            <span className="text-gray-400">تاريخ التسجيل:</span>
            <span className="mr-2">
              {user?.createdAt ? new Date(user.createdAt).toLocaleDateString('ar-SA') : 'غير محدد'}
            </span>
          </div>
          <div>
            <span className="text-gray-400">الحالة:</span>
            <span className="mr-2">{user?.status || 'نشط'}</span>
          </div>
        </div>
      </div>
    </div>
  );
}
