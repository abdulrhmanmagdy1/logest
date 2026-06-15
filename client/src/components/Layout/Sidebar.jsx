/**
 * ============================================
 * 📋 Sidebar Component - نظام إدهام
 * Edham Logistics - Sidebar Navigation
 * ============================================
 */

import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import {
  LayoutDashboard,
  Package,
  Truck,
  FileText,
  Users,
  Settings,
  BarChart3,
  Wrench,
  LogOut,
  ChevronRight,
  MapPin,
  CreditCard,
  User,
  FileSpreadsheet,
  MessageSquare,
  Droplet,
  Activity,
  FolderOpen,
  MessageCircle,
  Calendar
} from 'lucide-react';

export default function Sidebar({ isOpen, onClose }) {
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useAuth();

  const menuItems = getMenuItems(user?.role);

  const handleNavigation = (path) => {
    navigate(path);
    onClose();
  };

  return (
    <>
      {/* Overlay */}
      {isOpen && (
        <div
          className="fixed inset-0 bg-black bg-opacity-50 z-40 md:hidden"
          onClick={onClose}
        />
      )}

      {/* Sidebar */}
      <aside
        className={`fixed top-0 right-0 h-full bg-gray-800 z-50 transition-transform duration-300 ${
          isOpen ? 'translate-x-0' : 'translate-x-full'
        } md:translate-x-0 md:static w-64 border-l border-gray-700`}
      >
        <div className="flex flex-col h-full">
          {/* Logo */}
          <div className="p-4 border-b border-gray-700">
            <div className="flex items-center gap-3">
              <img src="/logo/logo-icon.svg" alt="Logo" className="w-10 h-10" />
              <div>
                <h1 className="text-white font-bold text-xl">إدهام</h1>
                <p className="text-gray-400 text-xs">نظام إدارة اللوجستيات</p>
              </div>
            </div>
          </div>

          {/* Menu */}
          <nav className="flex-1 overflow-y-auto p-4">
            <ul className="space-y-2">
              {menuItems.map((item) => (
                <li key={item.path}>
                  <button
                    onClick={() => handleNavigation(item.path)}
                    className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg transition ${
                      location.pathname === item.path
                        ? 'bg-blue-600 text-white'
                        : 'text-gray-300 hover:bg-gray-700'
                    }`}
                  >
                    {item.icon}
                    <span>{item.label}</span>
                    <ChevronRight className="w-4 h-4 mr-auto opacity-0 group-hover:opacity-100" />
                  </button>
                </li>
              ))}
            </ul>
          </nav>

          {/* User Info */}
          <div className="p-4 border-t border-gray-700">
            <div className="flex items-center gap-3 mb-3">
              <div className="w-10 h-10 bg-blue-600 rounded-full flex items-center justify-center">
                <Users className="w-5 h-5 text-white" />
              </div>
              <div className="flex-1">
                <p className="text-white font-semibold text-sm">{user?.name}</p>
                <p className="text-gray-400 text-xs">{getRoleLabel(user?.role)}</p>
              </div>
            </div>
            <button
              onClick={logout}
              className="w-full flex items-center gap-2 px-4 py-2 text-red-500 hover:bg-gray-700 rounded-lg transition"
            >
              <LogOut className="w-4 h-4" />
              <span>تسجيل الخروج</span>
            </button>
          </div>
        </div>
      </aside>
    </>
  );
}

function getMenuItems(role) {
  const commonItems = [
    { path: '/dashboard', label: 'لوحة التحكم', icon: <LayoutDashboard className="w-5 h-5" /> },
    { path: '/profile', label: 'الملف الشخصي', icon: <Users className="w-5 h-5" /> },
    { path: '/settings', label: 'الإعدادات', icon: <Settings className="w-5 h-5" /> },
  ];

  const roleSpecificItems = {
    admin: [
      { path: '/shipments', label: 'الشحنات', icon: <Package className="w-5 h-5" /> },
      { path: '/trucks', label: 'الشاحنات', icon: <Truck className="w-5 h-5" /> },
      { path: '/invoices', label: 'الفواتير', icon: <FileText className="w-5 h-5" /> },
      { path: '/users', label: 'المستخدمين', icon: <Users className="w-5 h-5" /> },
      { path: '/drivers', label: 'السائقين', icon: <User className="w-5 h-5" /> },
      { path: '/trips', label: 'الرحلات', icon: <Truck className="w-5 h-5" /> },
      { path: '/payments', label: 'المدفوعات', icon: <CreditCard className="w-5 h-5" /> },
      { path: '/maintenance', label: 'الصيانة', icon: <Wrench className="w-5 h-5" /> },
      { path: '/oil-schedule', label: 'جدول الزيت', icon: <Droplet className="w-5 h-5" /> },
      { path: '/spare-parts', label: 'قطع الغيار', icon: <Wrench className="w-5 h-5" /> },
      { path: '/location', label: 'التتبع', icon: <MapPin className="w-5 h-5" /> },
      { path: '/reports', label: 'التقارير', icon: <FileSpreadsheet className="w-5 h-5" /> },
      { path: '/documents', label: 'المستندات', icon: <FolderOpen className="w-5 h-5" /> },
      { path: '/audit-logs', label: 'سجل التدقيق', icon: <Activity className="w-5 h-5" /> },
      { path: '/chat', label: 'المحادثات', icon: <MessageCircle className="w-5 h-5" /> },
      { path: '/calendar', label: 'التقويم', icon: <Calendar className="w-5 h-5" /> },
      { path: '/analytics', label: 'التحليلات', icon: <BarChart3 className="w-5 h-5" /> },
    ],
    supervisor: [
      { path: '/shipments', label: 'الشحنات', icon: <Package className="w-5 h-5" /> },
      { path: '/trucks', label: 'الشاحنات', icon: <Truck className="w-5 h-5" /> },
      { path: '/drivers', label: 'السائقين', icon: <User className="w-5 h-5" /> },
      { path: '/trips', label: 'الرحلات', icon: <Truck className="w-5 h-5" /> },
      { path: '/maintenance', label: 'الصيانة', icon: <Wrench className="w-5 h-5" /> },
      { path: '/oil-schedule', label: 'جدول الزيت', icon: <Droplet className="w-5 h-5" /> },
      { path: '/spare-parts', label: 'قطع الغيار', icon: <Wrench className="w-5 h-5" /> },
      { path: '/location', label: 'التتبع', icon: <MapPin className="w-5 h-5" /> },
      { path: '/chat', label: 'المحادثات', icon: <MessageCircle className="w-5 h-5" /> },
      { path: '/calendar', label: 'التقويم', icon: <Calendar className="w-5 h-5" /> },
      { path: '/analytics', label: 'التحليلات', icon: <BarChart3 className="w-5 h-5" /> },
    ],
    accountant: [
      { path: '/invoices', label: 'الفواتير', icon: <FileText className="w-5 h-5" /> },
      { path: '/payments', label: 'المدفوعات', icon: <CreditCard className="w-5 h-5" /> },
      { path: '/reports', label: 'التقارير', icon: <FileSpreadsheet className="w-5 h-5" /> },
      { path: '/documents', label: 'المستندات', icon: <FolderOpen className="w-5 h-5" /> },
      { path: '/analytics', label: 'التقارير المالية', icon: <BarChart3 className="w-5 h-5" /> },
    ],
    driver: [
      { path: '/trips', label: 'الرحلات', icon: <Truck className="w-5 h-5" /> },
      { path: '/shipments', label: 'الشحنات', icon: <Package className="w-5 h-5" /> },
      { path: '/location', label: 'التتبع', icon: <MapPin className="w-5 h-5" /> },
      { path: '/chat', label: 'المحادثات', icon: <MessageCircle className="w-5 h-5" /> },
      { path: '/survey', label: 'التقييم', icon: <MessageSquare className="w-5 h-5" /> },
    ],
    client: [
      { path: '/shipments', label: 'الشحنات', icon: <Package className="w-5 h-5" /> },
      { path: '/invoices', label: 'الفواتير', icon: <FileText className="w-5 h-5" /> },
      { path: '/payments', label: 'المدفوعات', icon: <CreditCard className="w-5 h-5" /> },
      { path: '/location', label: 'التتبع', icon: <MapPin className="w-5 h-5" /> },
      { path: '/survey', label: 'التقييم', icon: <MessageSquare className="w-5 h-5" /> },
    ],
    employee: [
      { path: '/trucks', label: 'الشاحنات', icon: <Truck className="w-5 h-5" /> },
      { path: '/trips', label: 'الرحلات', icon: <Truck className="w-5 h-5" /> },
      { path: '/maintenance', label: 'الصيانة', icon: <Wrench className="w-5 h-5" /> },
      { path: '/chat', label: 'المحادثات', icon: <MessageCircle className="w-5 h-5" /> },
    ],
    maintenance: [
      { path: '/maintenance', label: 'الصيانة', icon: <Wrench className="w-5 h-5" /> },
      { path: '/trucks', label: 'الشاحنات', icon: <Truck className="w-5 h-5" /> },
      { path: '/oil-schedule', label: 'جدول الزيت', icon: <Droplet className="w-5 h-5" /> },
      { path: '/spare-parts', label: 'قطع الغيار', icon: <Wrench className="w-5 h-5" /> },
      { path: '/calendar', label: 'التقويم', icon: <Calendar className="w-5 h-5" /> },
    ],
  };

  return [...commonItems, ...(roleSpecificItems[role] || [])];
}

function getRoleLabel(role) {
  const labels = {
    admin: 'مشرف',
    supervisor: 'مشرف ميداني',
    accountant: 'محاسب',
    driver: 'سائق',
    client: 'عميل',
    employee: 'موظف',
    maintenance: 'صيانة',
  };
  return labels[role] || role;
}
