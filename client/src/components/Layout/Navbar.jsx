/**
 * ============================================
 * 🧭 Navbar Component - نظام إدهام
 * Edham Logistics - Navigation Bar
 * ============================================
 */

import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { Menu, X, Bell, User, LogOut, Settings } from 'lucide-react';

export default function Navbar({ toggleSidebar, sidebarOpen }) {
  const { user, logout } = useAuth();
  const [showNotifications, setShowNotifications] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);

  return (
    <nav className="bg-gray-800 border-b border-gray-700 px-4 py-3 flex items-center justify-between">
      {/* Left Side */}
      <div className="flex items-center gap-4">
        <button
          onClick={toggleSidebar}
          className="text-white hover:text-blue-500 transition"
        >
          {sidebarOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
        </button>
        <div className="flex items-center gap-2">
          <img src="/logo/logo-icon.svg" alt="Logo" className="w-8 h-8" />
          <span className="text-white font-bold text-xl">إدهام</span>
        </div>
      </div>

      {/* Right Side */}
      <div className="flex items-center gap-4">
        {/* Notifications */}
        <div className="relative">
          <button
            onClick={() => setShowNotifications(!showNotifications)}
            className="text-white hover:text-blue-500 transition relative"
          >
            <Bell className="w-6 h-6" />
            <span className="absolute -top-1 -right-1 w-4 h-4 bg-red-500 rounded-full text-xs flex items-center justify-center">
              3
            </span>
          </button>
          
          {showNotifications && (
            <div className="absolute right-0 mt-2 w-80 bg-gray-700 rounded-lg shadow-xl z-50">
              <div className="p-4 border-b border-gray-600">
                <h3 className="text-white font-bold">الإشعارات</h3>
              </div>
              <div className="p-2">
                <NotificationItem title="شحنة جديدة" message="تم إسناد الشحنة #12345" time="منذ 5 دقائق" />
                <NotificationItem title="تذكير صيانة" message="الشاحنة #TRK001 تحتاج صيانة" time="منذ ساعة" />
                <NotificationItem title="فاتورة جديدة" message="فاتورة #INV67890 جاهزة" time="منذ يومين" />
              </div>
              <div className="p-2 border-t border-gray-600">
                <button className="w-full text-blue-500 hover:text-blue-400 text-sm">
                  عرض الكل
                </button>
              </div>
            </div>
          )}
        </div>

        {/* User Menu */}
        <div className="relative">
          <button
            onClick={() => setShowUserMenu(!showUserMenu)}
            className="flex items-center gap-2 text-white hover:text-blue-500 transition"
          >
            <div className="w-8 h-8 bg-blue-600 rounded-full flex items-center justify-center">
              <User className="w-5 h-5" />
            </div>
            <span className="hidden md:block">{user?.name}</span>
          </button>
          
          {showUserMenu && (
            <div className="absolute right-0 mt-2 w-48 bg-gray-700 rounded-lg shadow-xl z-50">
              <div className="p-4 border-b border-gray-600">
                <p className="text-white font-semibold">{user?.name}</p>
                <p className="text-gray-400 text-sm">{user?.email}</p>
              </div>
              <div className="p-2">
                <button className="w-full text-white hover:bg-gray-600 flex items-center gap-2 px-3 py-2 rounded">
                  <User className="w-4 h-4" />
                  الملف الشخصي
                </button>
                <button className="w-full text-white hover:bg-gray-600 flex items-center gap-2 px-3 py-2 rounded">
                  <Settings className="w-4 h-4" />
                  الإعدادات
                </button>
              </div>
              <div className="p-2 border-t border-gray-600">
                <button
                  onClick={logout}
                  className="w-full text-red-500 hover:bg-gray-600 flex items-center gap-2 px-3 py-2 rounded"
                >
                  <LogOut className="w-4 h-4" />
                  تسجيل الخروج
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
}

function NotificationItem({ title, message, time }) {
  return (
    <div className="p-3 hover:bg-gray-600 rounded cursor-pointer">
      <p className="text-white font-semibold text-sm">{title}</p>
      <p className="text-gray-400 text-xs">{message}</p>
      <p className="text-gray-500 text-xs mt-1">{time}</p>
    </div>
  );
}
