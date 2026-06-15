/**
 * ============================================
 * ⚙️ Settings Page - نظام إدهام
 * Edham Logistics - Settings Page
 * ============================================
 */

import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { Bell, Lock, Globe, Moon, Sun, Save, AlertCircle } from 'lucide-react';

export default function SettingsPage() {
  const { user } = useAuth();
  const [darkMode, setDarkMode] = useState(true);
  const [notifications, setNotifications] = useState({
    email: true,
    sms: false,
    push: true
  });
  const [language, setLanguage] = useState('ar');
  const [success, setSuccess] = useState(null);
  const [error, setError] = useState(null);

  const handleNotificationChange = (type) => {
    setNotifications(prev => ({
      ...prev,
      [type]: !prev[type]
    }));
  };

  const handleSave = () => {
    setSuccess('تم حفظ الإعدادات بنجاح');
    setTimeout(() => setSuccess(null), 3000);
  };

  return (
    <div className="max-w-4xl mx-auto p-6">
      <h1 className="text-3xl font-bold text-white mb-6">الإعدادات</h1>

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

      <div className="space-y-6">
        {/* Appearance */}
        <div className="bg-gray-800 p-6 rounded-lg">
          <h2 className="text-xl font-bold text-white mb-4 flex items-center gap-2">
            {darkMode ? <Moon className="w-6 h-6" /> : <Sun className="w-6 h-6" />}
            المظهر
          </h2>
          
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-white font-semibold">الوضع الداكن</p>
                <p className="text-gray-400 text-sm">تفعيل الوضع الداكن للتطبيق</p>
              </div>
              <button
                onClick={() => setDarkMode(!darkMode)}
                className={`w-14 h-8 rounded-full p-1 transition-colors ${
                  darkMode ? 'bg-blue-600' : 'bg-gray-600'
                }`}
              >
                <div
                  className={`w-6 h-6 bg-white rounded-full transition-transform ${
                    darkMode ? 'translate-x-6' : 'translate-x-0'
                  }`}
                />
              </button>
            </div>

            <div className="flex items-center justify-between">
              <div>
                <p className="text-white font-semibold">اللغة</p>
                <p className="text-gray-400 text-sm">اختر لغة التطبيق</p>
              </div>
              <select
                value={language}
                onChange={(e) => setLanguage(e.target.value)}
                className="bg-gray-700 text-white px-4 py-2 rounded border border-gray-600 focus:border-blue-500 outline-none"
              >
                <option value="ar">العربية</option>
                <option value="en">English</option>
              </select>
            </div>
          </div>
        </div>

        {/* Notifications */}
        <div className="bg-gray-800 p-6 rounded-lg">
          <h2 className="text-xl font-bold text-white mb-4 flex items-center gap-2">
            <Bell className="w-6 h-6" />
            الإشعارات
          </h2>
          
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-white font-semibold">إشعارات البريد الإلكتروني</p>
                <p className="text-gray-400 text-sm">استلام إشعارات عبر البريد الإلكتروني</p>
              </div>
              <button
                onClick={() => handleNotificationChange('email')}
                className={`w-14 h-8 rounded-full p-1 transition-colors ${
                  notifications.email ? 'bg-blue-600' : 'bg-gray-600'
                }`}
              >
                <div
                  className={`w-6 h-6 bg-white rounded-full transition-transform ${
                    notifications.email ? 'translate-x-6' : 'translate-x-0'
                  }`}
                />
              </button>
            </div>

            <div className="flex items-center justify-between">
              <div>
                <p className="text-white font-semibold">إشعارات SMS</p>
                <p className="text-gray-400 text-sm">استلام إشعارات عبر الرسائل النصية</p>
              </div>
              <button
                onClick={() => handleNotificationChange('sms')}
                className={`w-14 h-8 rounded-full p-1 transition-colors ${
                  notifications.sms ? 'bg-blue-600' : 'bg-gray-600'
                }`}
              >
                <div
                  className={`w-6 h-6 bg-white rounded-full transition-transform ${
                    notifications.sms ? 'translate-x-6' : 'translate-x-0'
                  }`}
                />
              </button>
            </div>

            <div className="flex items-center justify-between">
              <div>
                <p className="text-white font-semibold">إشعارات فورية</p>
                <p className="text-gray-400 text-sm">استلام إشعارات فورية في التطبيق</p>
              </div>
              <button
                onClick={() => handleNotificationChange('push')}
                className={`w-14 h-8 rounded-full p-1 transition-colors ${
                  notifications.push ? 'bg-blue-600' : 'bg-gray-600'
                }`}
              >
                <div
                  className={`w-6 h-6 bg-white rounded-full transition-transform ${
                    notifications.push ? 'translate-x-6' : 'translate-x-0'
                  }`}
                />
              </button>
            </div>
          </div>
        </div>

        {/* Security */}
        <div className="bg-gray-800 p-6 rounded-lg">
          <h2 className="text-xl font-bold text-white mb-4 flex items-center gap-2">
            <Lock className="w-6 h-6" />
            الأمان
          </h2>
          
          <div className="space-y-4">
            <button className="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded text-left">
              تغيير كلمة المرور
            </button>
            <button className="w-full bg-gray-700 hover:bg-gray-600 text-white font-bold py-2 px-4 rounded text-left">
              تفعيل المصادقة الثنائية
            </button>
            <button className="w-full bg-gray-700 hover:bg-gray-600 text-white font-bold py-2 px-4 rounded text-left">
              عرض سجل الجلسات
            </button>
          </div>
        </div>

        {/* Language & Region */}
        <div className="bg-gray-800 p-6 rounded-lg">
          <h2 className="text-xl font-bold text-white mb-4 flex items-center gap-2">
            <Globe className="w-6 h-6" />
            اللغة والمنطقة
          </h2>
          
          <div className="space-y-4">
            <div>
              <label className="block text-gray-300 mb-2">المنطقة الزمنية</label>
              <select className="w-full bg-gray-700 text-white px-4 py-2 rounded border border-gray-600 focus:border-blue-500 outline-none">
                <option value="Asia/Riyadh">الرياض (GMT+3)</option>
                <option value="Asia/Jeddah">جدة (GMT+3)</option>
                <option value="Asia/Dubai">دبي (GMT+4)</option>
                <option value="Asia/Cairo">القاهرة (GMT+2)</option>
              </select>
            </div>
            <div>
              <label className="block text-gray-300 mb-2">تنسيق التاريخ</label>
              <select className="w-full bg-gray-700 text-white px-4 py-2 rounded border border-gray-600 focus:border-blue-500 outline-none">
                <option value="dd/mm/yyyy">يوم/شهر/سنة</option>
                <option value="mm/dd/yyyy">شهر/يوم/سنة</option>
                <option value="yyyy-mm-dd">سنة-شهر-يوم</option>
              </select>
            </div>
          </div>
        </div>

        {/* Save Button */}
        <div className="flex justify-end">
          <button
            onClick={handleSave}
            className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-6 rounded flex items-center gap-2"
          >
            <Save className="w-4 h-4" />
            حفظ الإعدادات
          </button>
        </div>
      </div>
    </div>
  );
}
