/**
 * ============================================
 * 👨‍💼 Supervisor Dashboard - نظام إدهام
 * Edham Logistics - Supervisor Management
 * ============================================
 */

import React, { useState, useEffect } from "react";
import { Routes, Route, Link, useLocation, useNavigate } from "react-router-dom";
import { motion, AnimatePresence } from "framer-motion";
import {
  LayoutDashboard, Package, Truck, FileText, Users,
  MapPin, BarChart2, Settings, Bell, LogOut, Menu,
  X, ChevronDown, Search, TrendingUp, TrendingDown,
  AlertTriangle, CheckCircle, Clock, Activity,
} from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { useNotification } from "../context/NotificationContext";
import api from "../services/api";
import logger from "../utils/logger";
import ShipmentsPage from "./supervisor/ShipmentsPage";
import TrucksPage from "./supervisor/TrucksPage";
import UsersPage from "./supervisor/UsersPage";
import InvoicesPage from "./supervisor/InvoicesPage";
import TrackingPage from "./supervisor/TrackingPage";
import SettingsPage from "./supervisor/SettingsPage";

// ── Sidebar Items ─────────────────────────
const SIDEBAR_ITEMS = [
  { icon: LayoutDashboard, label: "الرئيسية",    path: "/supervisor" },
  { icon: Package,         label: "الشحنات",     path: "/supervisor/shipments" },
  { icon: Truck,           label: "الشاحنات",    path: "/supervisor/trucks" },
  { icon: Users,           label: "المستخدمون",  path: "/supervisor/users" },
  { icon: FileText,        label: "الفواتير",    path: "/supervisor/invoices" },
  { icon: MapPin,          label: "التتبع",      path: "/supervisor/tracking" },
  { icon: BarChart2,       label: "التحليلات",   path: "/analytics" },
  { icon: Settings,        label: "الإعدادات",   path: "/supervisor/settings" },
];

// ── Stat Card ─────────────────────────────
const StatCard = ({ title, value, icon: Icon, color, change, changeType }) => (
  <motion.div
    initial={{ opacity: 0, y: 20 }}
    animate={{ opacity: 1, y: 0 }}
    className="card hover-lift"
  >
    <div className="flex items-center justify-between mb-3">
      <div className={`stat-icon ${color}`}>
        <Icon className="w-5 h-5" />
      </div>
      {change !== undefined && (
        <div className={`stat-change ${changeType === "up" ? "positive" : "negative"}`}>
          {changeType === "up" ? (
            <TrendingUp className="w-3 h-3" />
          ) : (
            <TrendingDown className="w-3 h-3" />
          )}
          <span>{Math.abs(change)}%</span>
        </div>
      )}
    </div>
    <div className="stat-value">{value}</div>
    <div className="stat-label mt-1">{title}</div>
  </motion.div>
);

// ── Sidebar ───────────────────────────────
const Sidebar = ({ isOpen, onClose }) => {
  const location = useLocation();
  const { user, logout } = useAuth();
  const { unreadCount } = useNotification();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <>
      {/* Overlay موبايل */}
      <AnimatePresence>
        {isOpen && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="fixed inset-0 bg-black/60 z-30 lg:hidden"
            onClick={onClose}
          />
        )}
      </AnimatePresence>

      {/* Sidebar */}
      <motion.aside
        initial={false}
        animate={{ x: isOpen ? 0 : "100%" }}
        className="sidebar transition-transform duration-300 lg:translate-x-0"
      >
        {/* Logo */}
        <div className="flex items-center justify-between p-5 border-b border-edham-gray">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 bg-edham-primary rounded-xl flex items-center justify-center">
              <Truck className="w-5 h-5 text-white" />
            </div>
            <div>
              <span className="font-black text-lg text-gradient">إدهام</span>
              <p className="text-xs text-edham-text-muted">النقل المبرد</p>
            </div>
          </div>
          <button onClick={onClose} className="btn-icon text-edham-text-muted hover:text-edham-white lg:hidden">
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* User Info */}
        <div className="p-4 border-b border-edham-gray">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 bg-edham-primary/20 rounded-full flex items-center
                           justify-center border border-edham-primary/30">
              <span className="text-edham-primary font-bold text-sm">
                {user?.name?.charAt(0)}
              </span>
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-medium text-edham-white truncate">{user?.name}</p>
              <p className="text-xs text-edham-text-muted">مشرف</p>
            </div>
          </div>
        </div>

        {/* Nav Items */}
        <nav className="flex-1 overflow-y-auto py-3 no-scrollbar">
          {SIDEBAR_ITEMS.map((item) => {
            const isActive = location.pathname === item.path ||
              (item.path !== "/supervisor" && location.pathname.startsWith(item.path));
            return (
              <Link
                key={item.path}
                to={item.path}
                onClick={onClose}
                className={`sidebar-item ${isActive ? "active" : ""}`}
              >
                <item.icon className="w-4 h-4 flex-shrink-0" />
                <span>{item.label}</span>
                {item.label === "الإشعارات" && unreadCount > 0 && (
                  <span className="mr-auto bg-edham-primary text-white text-xs
                                   px-1.5 py-0.5 rounded-full min-w-[20px] text-center">
                    {unreadCount}
                  </span>
                )}
              </Link>
            );
          })}
        </nav>

        {/* Logout */}
        <div className="p-3 border-t border-edham-gray">
          <button
            onClick={handleLogout}
            className="sidebar-item w-full text-edham-primary hover:text-edham-primary-light hover:bg-edham-primary/10"
          >
            <LogOut className="w-4 h-4" />
            <span>تسجيل الخروج</span>
          </button>
        </div>
      </motion.aside>
    </>
  );
};

// ── Navbar ────────────────────────────────
const Navbar = ({ onMenuClick }) => {
  const { unreadCount, notifications, markAllAsRead } = useNotification();
  const [showNotif, setShowNotif] = useState(false);
  const { isConnected } = useNotification();

  return (
    <header className="navbar">
      <div className="flex items-center gap-4">
        <button
          onClick={onMenuClick}
          className="btn-icon text-edham-text-muted hover:text-edham-white lg:hidden"
        >
          <Menu className="w-5 h-5" />
        </button>

        {/* Search */}
        <div className="hidden md:flex items-center gap-2 bg-edham-gray border border-edham-gray
                        rounded-lg px-3 py-2 w-64">
          <Search className="w-4 h-4 text-edham-text-muted" />
          <input
            type="text"
            placeholder="بحث..."
            className="bg-transparent text-sm text-edham-white placeholder-edham-text-muted outline-none flex-1"
          />
        </div>
      </div>

      <div className="flex items-center gap-3">
        {/* مؤشر الاتصال */}
        <div className="flex items-center gap-1.5">
          <div className={`w-2 h-2 rounded-full ${
            isConnected ? "bg-green-400 animate-pulse" : "bg-red-400"
          }`} />
          <span className="text-xs text-edham-text-muted hidden sm:block">
            {isConnected ? "متصل" : "غير متصل"}
          </span>
        </div>

        {/* الإشعارات */}
        <div className="relative">
          <button
            onClick={() => setShowNotif(!showNotif)}
            className="btn-icon text-edham-text-muted hover:text-edham-white relative"
          >
            <Bell className="w-5 h-5" />
            {unreadCount > 0 && (
              <span className="absolute -top-1 -left-1 bg-edham-primary text-white
                               text-xs w-5 h-5 rounded-full flex items-center
                               justify-center font-bold">
                {unreadCount > 9 ? "9+" : unreadCount}
              </span>
            )}
          </button>

          <AnimatePresence>
            {showNotif && (
              <motion.div
                initial={{ opacity: 0, y: 10, scale: 0.95 }}
                animate={{ opacity: 1, y: 0, scale: 1 }}
                exit={{ opacity: 0, y: 10, scale: 0.95 }}
                className="absolute left-0 top-full mt-2 w-80 bg-edham-gray border
                           border-edham-gray rounded-xl shadow-2xl z-50 overflow-hidden"
              >
                <div className="flex items-center justify-between p-4 border-b border-edham-gray">
                  <span className="font-semibold text-edham-white">الإشعارات</span>
                  {unreadCount > 0 && (
                    <button
                      onClick={markAllAsRead}
                      className="text-xs text-edham-primary hover:text-edham-primary-light"
                    >
                      قراءة الكل
                    </button>
                  )}
                </div>
                <div className="max-h-72 overflow-y-auto">
                  {notifications.length === 0 ? (
                    <div className="p-6 text-center text-edham-text-muted text-sm">
                      لا توجد إشعارات
                    </div>
                  ) : (
                    notifications.slice(0, 10).map((notif) => (
                      <div
                        key={notif.id}
                        className={`p-4 border-b border-edham-gray hover:bg-edham-gray
                                   transition-colors ${!notif.isRead ? "bg-edham-gray" : ""}`}
                      >
                        <p className="text-sm text-edham-white font-medium">{notif.title}</p>
                        <p className="text-xs text-edham-text-muted mt-0.5">{notif.message}</p>
                        <p className="text-xs text-edham-text-muted mt-1">
                          {new Date(notif.receivedAt).toLocaleTimeString("ar-EG")}
                        </p>
                      </div>
                    ))
                  )}
                </div>
              </motion.div>
            )}
          </AnimatePresence>
        </div>
      </div>
    </header>
  );
};

// ── Overview Page ─────────────────────────
const OverviewPage = () => {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [recentShipments, setRecentShipments] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [statsRes, shipmentsRes] = await Promise.all([
          api.get("/analytics/dashboard"),
          api.get("/shipments?limit=5&sort=-createdAt"),
        ]);
        setStats(statsRes.data.data);
        setRecentShipments(shipmentsRes.data.data.shipments);
      } catch (err) {
        logger.error("خطأ في جلب البيانات:", err.message);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  if (loading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {[...Array(8)].map((_, i) => (
          <div key={i} className="skeleton h-28 rounded-xl" />
        ))}
      </div>
    );
  }

  const statusLabels = {
    pending:    { label: "معلقة",   class: "status-pending" },
    confirmed:  { label: "مؤكدة",  class: "status-confirmed" },
    assigned:   { label: "مسندة",  class: "status-assigned" },
    picked_up:  { label: "مستلمة", class: "status-picked_up" },
    in_transit: { label: "في الطريق", class: "status-in_transit" },
    delivered:  { label: "مسلمة",  class: "status-delivered" },
    failed:     { label: "فاشلة",  class: "status-failed" },
    cancelled:  { label: "ملغية",  class: "status-cancelled" },
  };

  return (
    <div className="space-y-6">
      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="إجمالي الشحنات"
          value={stats?.shipments?.total || 0}
          icon={Package}
          color="bg-blue-600/20 text-blue-400"
          change={stats?.shipments?.growth}
          changeType={stats?.shipments?.growth >= 0 ? "up" : "down"}
        />
        <StatCard
          title="شحنات نشطة"
          value={stats?.shipments?.active || 0}
          icon={Activity}
          color="bg-orange-600/20 text-orange-400"
        />
        <StatCard
          title="مكتملة"
          value={stats?.shipments?.completed || 0}
          icon={CheckCircle}
          color="bg-green-600/20 text-green-400"
        />
        <StatCard
          title="معلقة"
          value={stats?.shipments?.pending || 0}
          icon={Clock}
          color="bg-yellow-600/20 text-yellow-400"
        />
        <StatCard
          title="إجمالي الشاحنات"
          value={stats?.trucks?.total || 0}
          icon={Truck}
          color="bg-purple-600/20 text-purple-400"
        />
        <StatCard
          title="شاحنات نشطة"
          value={stats?.trucks?.active || 0}
          icon={Truck}
          color="bg-green-600/20 text-green-400"
        />
        <StatCard
          title="إيرادات الشهر"
          value={`${(stats?.finance?.monthRevenue || 0).toLocaleString()} ج.م`}
          icon={TrendingUp}
          color="bg-edham-primary/20 text-edham-primary"
          change={stats?.finance?.revenueGrowth}
          changeType={stats?.finance?.revenueGrowth >= 0 ? "up" : "down"}
        />
        <StatCard
          title="تنبيهات صيانة"
          value={stats?.maintenance?.pending || 0}
          icon={AlertTriangle}
          color="bg-yellow-600/20 text-yellow-400"
        />
      </div>

      {/* الشحنات الأخيرة */}
      <div className="card">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-lg font-semibold text-edham-white">آخر الشحنات</h3>
          <Link to="/supervisor/shipments" className="text-edham-primary hover:text-edham-primary-light text-sm">
            عرض الكل ←
          </Link>
        </div>

        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>رقم التتبع</th>
                <th>العميل</th>
                <th>من → إلى</th>
                <th>الحالة</th>
                <th>التاريخ</th>
              </tr>
            </thead>
            <tbody>
              {recentShipments.length === 0 ? (
                <tr>
                  <td colSpan={5} className="text-center text-edham-text-muted py-8">
                    لا توجد شحنات
                  </td>
                </tr>
              ) : (
                recentShipments.map((shipment) => (
                  <tr key={shipment._id}>
                    <td>
                      <span className="text-edham-primary font-mono text-xs font-medium">
                        {shipment.trackingNumber}
                      </span>
                    </td>
                    <td className="text-edham-white">
                      {shipment.client?.name || "—"}
                    </td>
                    <td className="text-edham-text-muted text-xs">
                      {shipment.pickup?.city || "—"} → {shipment.delivery?.city || "—"}
                    </td>
                    <td>
                      <span className={statusLabels[shipment.status]?.class || "badge-gray"}>
                        {statusLabels[shipment.status]?.label || shipment.status}
                      </span>
                    </td>
                    <td className="text-edham-text-muted text-xs">
                      {new Date(shipment.createdAt).toLocaleDateString("ar-EG")}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

// ── Main SupervisorDashboard ───────────────
const SupervisorDashboard = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="page-container">
      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />
      <Navbar onMenuClick={() => setSidebarOpen(true)} />

      <main className="page-content">
        <Routes>
          <Route index element={<OverviewPage />} />
          <Route path="shipments" element={<ShipmentsPage />} />
          <Route path="trucks"    element={<TrucksPage />} />
          <Route path="users"     element={<UsersPage />} />
          <Route path="invoices"  element={<InvoicesPage />} />
          <Route path="tracking"  element={<TrackingPage />} />
          <Route path="settings"  element={<SettingsPage />} />
        </Routes>
      </main>
    </div>
  );
};

export default SupervisorDashboard;
