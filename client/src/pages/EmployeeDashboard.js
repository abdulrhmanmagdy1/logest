/**
 * ============================================
 * 👤 Employee Dashboard - نظام إدهام
 * Edham Logistics - Employee Portal
 * ============================================
 */

import React, { useState, useEffect } from "react";
import { Routes, Route, Link, useLocation, useNavigate } from "react-router-dom";
import { motion, AnimatePresence } from "framer-motion";
import {
  LayoutDashboard, Car, Navigation, Settings,
  Bell, LogOut, Menu, X, Search, CheckCircle,
} from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { useNotification } from "../context/NotificationContext";
import api from "../services/api";
import logger from "../utils/logger";
import VehiclesPage from "./employee/VehiclesPage";
import TripsPage from "./employee/TripsPage";
import SettingsPage from "./supervisor/SettingsPage";

// ── Sidebar Items ─────────────────────────
const SIDEBAR_ITEMS = [
  { icon: LayoutDashboard, label: "الرئيسية",    path: "/employee" },
  { icon: Car,             label: "المركبات",   path: "/employee/vehicles" },
  { icon: Navigation,      label: "الرحلات",     path: "/employee/trips" },
  { icon: Settings,        label: "الإعدادات",   path: "/employee/settings" },
];

// ── Stat Card ─────────────────────────────
const StatCard = ({ title, value, icon: Icon, color }) => (
  <motion.div
    initial={{ opacity: 0, y: 20 }}
    animate={{ opacity: 1, y: 0 }}
    className="card hover-lift"
  >
    <div className="flex items-center justify-between mb-3">
      <div className={`stat-icon ${color}`}>
        <Icon className="w-5 h-5" />
      </div>
    </div>
    <div className="stat-value">{value}</div>
    <div className="stat-label mt-1">{title}</div>
  </motion.div>
);

// ── Sidebar ───────────────────────────────
const Sidebar = ({ isOpen, onClose }) => {
  const location = useLocation();
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <>
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

      <motion.aside
        initial={false}
        animate={{ x: isOpen ? 0 : "100%" }}
        className="sidebar transition-transform duration-300 lg:translate-x-0"
      >
        <div className="flex items-center justify-between p-5 border-b border-edham-gray">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 bg-edham-primary rounded-xl flex items-center justify-center">
              <Car className="w-5 h-5 text-white" />
            </div>
            <div>
              <span className="font-black text-lg text-gradient">إدهام</span>
              <p className="text-xs text-edham-text-muted">الموظف</p>
            </div>
          </div>
          <button onClick={onClose} className="btn-icon text-edham-text-muted hover:text-edham-white lg:hidden">
            <X className="w-5 h-5" />
          </button>
        </div>

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
              <p className="text-xs text-edham-text-muted">موظف</p>
            </div>
          </div>
        </div>

        <nav className="flex-1 overflow-y-auto py-3 no-scrollbar">
          {SIDEBAR_ITEMS.map((item) => {
            const isActive = location.pathname === item.path ||
              (item.path !== "/employee" && location.pathname.startsWith(item.path));
            return (
              <Link
                key={item.path}
                to={item.path}
                onClick={onClose}
                className={`sidebar-item ${isActive ? "active" : ""}`}
              >
                <item.icon className="w-4 h-4 flex-shrink-0" />
                <span>{item.label}</span>
              </Link>
            );
          })}
        </nav>

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

  return (
    <header className="navbar">
      <div className="flex items-center gap-4">
        <button
          onClick={onMenuClick}
          className="btn-icon text-edham-text-muted hover:text-edham-white lg:hidden"
        >
          <Menu className="w-5 h-5" />
        </button>

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
  const [activeTrip, setActiveTrip] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [statsRes, tripsRes] = await Promise.all([
          api.get("/analytics/employee"),
          api.get("/trips?status=in_progress&limit=1"),
        ]);
        setStats(statsRes.data.data);
        setActiveTrip(tripsRes.data.data?.trips?.[0] || null);
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
        {[...Array(4)].map((_, i) => (
          <div key={i} className="skeleton h-28 rounded-xl" />
        ))}
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="المركبات"
          value={stats?.vehicles || 0}
          icon={Car}
          color="bg-blue-600/20 text-blue-400"
        />
        <StatCard
          title="الرحلات"
          value={stats?.trips?.total || 0}
          icon={Navigation}
          color="bg-green-600/20 text-green-400"
        />
        <StatCard
          title="مكتملة"
          value={stats?.trips?.completed || 0}
          icon={CheckCircle}
          color="bg-purple-600/20 text-purple-400"
        />
        <StatCard
          title="المسافة"
          value={`${(stats?.distance || 0).toFixed(0)} كم`}
          icon={Navigation}
          color="bg-yellow-600/20 text-yellow-400"
        />
      </div>

      {/* Active Trip */}
      {activeTrip ? (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="card border-2 border-edham-primary/50 bg-edham-primary/5"
        >
          <div className="flex items-center gap-2 text-edham-primary mb-3">
            <Navigation className="w-5 h-5" />
            <span className="font-semibold">الرحلة الحالية</span>
          </div>
          <div className="flex items-center justify-between">
            <div>
              <h4 className="text-lg font-bold text-edham-white">رحلة #{activeTrip.tripNumber}</h4>
              <p className="text-edham-text-muted text-sm">
                {activeTrip.origin?.city} → {activeTrip.destination?.city}
              </p>
            </div>
            <button className="btn-sm">
              عرض التفاصيل
            </button>
          </div>
        </motion.div>
      ) : (
        <div className="card">
          <h3 className="text-lg font-semibold text-edham-white mb-4">الرحلة الحالية</h3>
          <p className="text-edham-text-muted">لا توجد رحلات نشطة حالياً</p>
        </div>
      )}
    </div>
  );
};

// ── Main EmployeeDashboard ────────────────
const EmployeeDashboard = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="page-container">
      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />
      <Navbar onMenuClick={() => setSidebarOpen(true)} />

      <main className="page-content">
        <Routes>
          <Route index element={<OverviewPage />} />
          <Route path="vehicles" element={<VehiclesPage />} />
          <Route path="trips"    element={<TripsPage />} />
          <Route path="settings" element={<SettingsPage />} />
        </Routes>
      </main>
    </div>
  );
};

export default EmployeeDashboard;
