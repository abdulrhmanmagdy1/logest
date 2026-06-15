/**
 * ============================================
 * 👤 Client Dashboard - نظام إدهام
 * Edham Logistics - Client Portal
 * ============================================
 */

import React, { useState, useEffect } from "react";
import { Routes, Route, Link, useLocation, useNavigate } from "react-router-dom";
import { motion, AnimatePresence } from "framer-motion";
import {
  LayoutDashboard, Package, MapPin, FileText, Plus,
  Search, Clock, CheckCircle, Truck, Bell, LogOut,
  Menu, X, TrendingUp, AlertCircle,
} from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { useNotification } from "../context/NotificationContext";
import api from "../services/api";
import logger from "../utils/logger";
import ClientShipmentsPage from "./client/ClientShipmentsPage";
import ClientTrackingPage from "./client/ClientTrackingPage";
import ClientInvoicesPage from "./client/ClientInvoicesPage";

// ── Sidebar Items ─────────────────────────
const SIDEBAR_ITEMS = [
  { icon: LayoutDashboard, label: "الرئيسية",    path: "/client" },
  { icon: Package,         label: "شحناتي",     path: "/client/shipments" },
  { icon: MapPin,          label: "التتبع",      path: "/client/tracking" },
  { icon: FileText,        label: "الفواتير",    path: "/client/invoices" },
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
              <p className="text-xs text-edham-text-muted">عميل</p>
            </div>
          </div>
        </div>

        <nav className="flex-1 overflow-y-auto py-3 no-scrollbar">
          {SIDEBAR_ITEMS.map((item) => {
            const isActive = location.pathname === item.path ||
              (item.path !== "/client" && location.pathname.startsWith(item.path));
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
            placeholder="بحث في الشحنات..."
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
  const [recentShipments, setRecentShipments] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [statsRes, shipmentsRes] = await Promise.all([
          api.get("/analytics/client"),
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
        {[...Array(4)].map((_, i) => (
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
        />
        <StatCard
          title="قيد الانتظار"
          value={stats?.shipments?.pending || 0}
          icon={Clock}
          color="bg-yellow-600/20 text-yellow-400"
        />
        <StatCard
          title="في الطريق"
          value={stats?.shipments?.inTransit || 0}
          icon={MapPin}
          color="bg-purple-600/20 text-purple-400"
        />
        <StatCard
          title="مكتملة"
          value={stats?.shipments?.completed || 0}
          icon={CheckCircle}
          color="bg-green-600/20 text-green-400"
        />
      </div>

      {/* الشحنات الأخيرة */}
      <div className="card">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-lg font-semibold text-edham-white">آخر الشحنات</h3>
          <Link to="/client/shipments" className="text-edham-primary hover:text-edham-primary-light text-sm">
            عرض الكل ←
          </Link>
        </div>

        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>رقم التتبع</th>
                <th>من → إلى</th>
                <th>الحالة</th>
                <th>التاريخ</th>
              </tr>
            </thead>
            <tbody>
              {recentShipments.length === 0 ? (
                <tr>
                  <td colSpan={4} className="text-center text-edham-text-muted py-8">
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

// ── Main ClientDashboard ───────────────────
const ClientDashboard = () => {
  const [sidebarOpen, setSidebarOpen] = useState(false);

  return (
    <div className="page-container">
      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />
      <Navbar onMenuClick={() => setSidebarOpen(true)} />

      <main className="page-content">
        <Routes>
          <Route index element={<OverviewPage />} />
          <Route path="shipments" element={<ClientShipmentsPage />} />
          <Route path="tracking"  element={<ClientTrackingPage />} />
          <Route path="invoices"  element={<ClientInvoicesPage />} />
        </Routes>
      </main>
    </div>
  );
};

export default ClientDashboard;
