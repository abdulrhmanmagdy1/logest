/**
 * ============================================
 * 👥 Users Management Page - نظام إدهام
 * Edham Logistics - User Management
 * ============================================
 */

import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  Users, Search, Plus, Filter, RefreshCw, Eye, Edit2, Trash2,
  User, Shield, Truck, Package, DollarSign, CheckCircle, X,
  Mail, Phone, Calendar, ChevronDown
} from "lucide-react";
import api from "../../services/api";
import logger from "../../utils/logger";

// ── Role Config ───────────────────────────
const ROLE_CONFIG = {
  admin:      { label: "مدير",       color: "bg-purple-500/20 text-purple-400 border-purple-500/30", icon: Shield },
  supervisor: { label: "مشرف",       color: "bg-blue-500/20 text-blue-400 border-blue-500/30", icon: Shield },
  accountant: { label: "محاسب",      color: "bg-green-500/20 text-green-400 border-green-500/30", icon: DollarSign },
  driver:     { label: "سائق",       color: "bg-orange-500/20 text-orange-400 border-orange-500/30", icon: Truck },
  client:     { label: "عميل",       color: "bg-cyan-500/20 text-cyan-400 border-cyan-500/30", icon: Package },
  employee:   { label: "موظف",      color: "bg-pink-500/20 text-pink-400 border-pink-500/30", icon: User },
};

// ── Status Config ─────────────────────────
const STATUS_CONFIG = {
  active:   { label: "نشط",    color: "bg-green-500/20 text-green-400 border-green-500/30" },
  inactive: { label: "غير نشط", color: "bg-gray-500/20 text-gray-400 border-gray-500/30" },
  suspended:{ label: "موقوف",  color: "bg-red-500/20 text-red-400 border-red-500/30" },
};

// ── Modal Component ───────────────────────
const Modal = ({ isOpen, onClose, title, children }) => {
  if (!isOpen) return null;
  return (
    <div className="fixed inset-0 bg-black/70 backdrop-blur-sm z-50 flex items-center justify-center p-4"
         onClick={onClose}>
      <motion.div
        initial={{ opacity: 0, scale: 0.95, y: 20 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        className="bg-edham-dark border border-edham-gray rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-hidden"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex items-center justify-between p-5 border-b border-edham-gray">
          <h3 className="text-lg font-bold text-edham-white">{title}</h3>
          <button onClick={onClose} className="btn-icon text-edham-text-muted hover:text-edham-white">
            <X className="w-5 h-5" />
          </button>
        </div>
        <div className="p-5 overflow-y-auto max-h-[70vh]">
          {children}
        </div>
      </motion.div>
    </div>
  );
};

// ── User Detail Modal ─────────────────────
const UserDetailModal = ({ user, isOpen, onClose, onToggleStatus }) => {
  if (!user) return null;
  const RoleIcon = ROLE_CONFIG[user.role]?.icon || User;

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={`مستخدم: ${user.name}`}>
      <div className="space-y-6">
        {/* Profile Header */}
        <div className="flex items-center gap-4 bg-edham-black/50 rounded-xl p-4">
          <div className="w-20 h-20 bg-edham-primary/20 rounded-2xl flex items-center justify-center">
            <RoleIcon className="w-10 h-10 text-edham-primary" />
          </div>
          <div className="flex-1">
            <h4 className="text-xl font-bold text-edham-white">{user.name}</h4>
            <p className="text-edham-text-muted text-sm">{user.email}</p>
            <div className="flex items-center gap-2 mt-2">
              <span className={`px-2.5 py-1 rounded-lg text-xs font-medium border ${
                ROLE_CONFIG[user.role]?.color || "bg-gray-500/20 text-gray-400 border-gray-500/30"
              }`}>
                {ROLE_CONFIG[user.role]?.label || user.role}
              </span>
              <span className={`px-2.5 py-1 rounded-lg text-xs font-medium border ${
                STATUS_CONFIG[user.status]?.color || STATUS_CONFIG.inactive.color
              }`}>
                {STATUS_CONFIG[user.status]?.label || user.status}
              </span>
            </div>
          </div>
        </div>

        {/* Details */}
        <div className="grid grid-cols-2 gap-4">
          <div className="bg-edham-black/50 rounded-xl p-4">
            <div className="flex items-center gap-2 text-edham-text-muted mb-2">
              <Phone className="w-4 h-4" />
              <span className="text-xs">رقم الهاتف</span>
            </div>
            <p className="text-edham-white font-medium">{user.phone || "—"}</p>
          </div>
          <div className="bg-edham-black/50 rounded-xl p-4">
            <div className="flex items-center gap-2 text-edham-text-muted mb-2">
              <Mail className="w-4 h-4" />
              <span className="text-xs">البريد الإلكتروني</span>
            </div>
            <p className="text-edham-white font-medium text-sm">{user.email}</p>
          </div>
          <div className="bg-edham-black/50 rounded-xl p-4">
            <div className="flex items-center gap-2 text-edham-text-muted mb-2">
              <Calendar className="w-4 h-4" />
              <span className="text-xs">تاريخ الإنشاء</span>
            </div>
            <p className="text-edham-white font-medium">
              {new Date(user.createdAt).toLocaleDateString("ar-EG")}
            </p>
          </div>
          <div className="bg-edham-black/50 rounded-xl p-4">
            <div className="flex items-center gap-2 text-edham-text-muted mb-2">
              <CheckCircle className="w-4 h-4" />
              <span className="text-xs">آخر تسجيل دخول</span>
            </div>
            <p className="text-edham-white font-medium">
              {user.lastLogin ? new Date(user.lastLogin).toLocaleDateString("ar-EG") : "—"}
            </p>
          </div>
        </div>

        {/* Stats based on role */}
        {user.stats && (
          <div className="bg-edham-black/50 rounded-xl p-4">
            <h5 className="text-sm font-semibold text-edham-white mb-3">الإحصائيات</h5>
            <div className="grid grid-cols-3 gap-4">
              {Object.entries(user.stats).map(([key, value]) => (
                <div key={key} className="text-center">
                  <p className="text-2xl font-bold text-edham-primary">{value}</p>
                  <p className="text-edham-text-muted text-xs">{key}</p>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Actions */}
        <div className="flex gap-3 pt-4 border-t border-edham-gray">
          <button
            onClick={() => onToggleStatus(user._id, user.status === "active" ? "inactive" : "active")}
            className={`flex-1 py-2.5 px-4 rounded-xl font-medium transition-colors flex items-center justify-center gap-2 ${
              user.status === "active"
                ? "bg-red-500/20 text-red-400 hover:bg-red-500/30"
                : "bg-green-500/20 text-green-400 hover:bg-green-500/30"
            }`}
          >
            {user.status === "active" ? "تعطيل الحساب" : "تفعيل الحساب"}
          </button>
          <button onClick={onClose} className="btn-secondary flex-1">
            إغلاق
          </button>
        </div>
      </div>
    </Modal>
  );
};

// ── Add User Modal ──────────────────────────
const AddUserModal = ({ isOpen, onClose, onAdd }) => {
  const [formData, setFormData] = useState({
    name: "",
    email: "",
    phone: "",
    role: "client",
    password: "",
  });
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await onAdd(formData);
      onClose();
    } catch (err) {
      logger.error("خطأ في إضافة المستخدم:", err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="إضافة مستخدم جديد">
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm text-edham-text-muted mb-1.5">الاسم الكامل *</label>
          <input
            type="text"
            required
            value={formData.name}
            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
            className="w-full bg-edham-black border border-edham-gray rounded-lg px-3 py-2.5 text-edham-white outline-none focus:border-edham-primary"
            placeholder="اسم المستخدم"
          />
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm text-edham-text-muted mb-1.5">البريد الإلكتروني *</label>
            <input
              type="email"
              required
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              className="w-full bg-edham-black border border-edham-gray rounded-lg px-3 py-2.5 text-edham-white outline-none focus:border-edham-primary"
              placeholder="email@example.com"
            />
          </div>
          <div>
            <label className="block text-sm text-edham-text-muted mb-1.5">رقم الهاتف</label>
            <input
              type="tel"
              value={formData.phone}
              onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
              className="w-full bg-edham-black border border-edham-gray rounded-lg px-3 py-2.5 text-edham-white outline-none focus:border-edham-primary"
              placeholder="01xxxxxxxxx"
            />
          </div>
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm text-edham-text-muted mb-1.5">الدور *</label>
            <select
              value={formData.role}
              onChange={(e) => setFormData({ ...formData, role: e.target.value })}
              className="w-full bg-edham-black border border-edham-gray rounded-lg px-3 py-2.5 text-edham-white outline-none focus:border-edham-primary"
            >
              {Object.entries(ROLE_CONFIG).map(([key, config]) => (
                <option key={key} value={key}>{config.label}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm text-edham-text-muted mb-1.5">كلمة المرور *</label>
            <input
              type="password"
              required
              value={formData.password}
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              className="w-full bg-edham-black border border-edham-gray rounded-lg px-3 py-2.5 text-edham-white outline-none focus:border-edham-primary"
              placeholder="********"
            />
          </div>
        </div>
        <div className="flex gap-3 pt-4">
          <button type="submit" disabled={loading} className="btn-primary flex-1 disabled:opacity-50">
            {loading ? "جاري الإضافة..." : "إضافة المستخدم"}
          </button>
          <button type="button" onClick={onClose} className="btn-secondary flex-1">
            إلغاء
          </button>
        </div>
      </form>
    </Modal>
  );
};

// ── Main UsersPage ────────────────────────
const UsersPage = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [roleFilter, setRoleFilter] = useState("all");
  const [statusFilter, setStatusFilter] = useState("all");
  const [selectedUser, setSelectedUser] = useState(null);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [isAddOpen, setIsAddOpen] = useState(false);
  const [pagination, setPagination] = useState({ page: 1, total: 0, pages: 1 });

  const fetchUsers = async (page = 1) => {
    setLoading(true);
    try {
      const params = new URLSearchParams({
        page: page.toString(),
        limit: "10",
        ...(roleFilter !== "all" && { role: roleFilter }),
        ...(statusFilter !== "all" && { status: statusFilter }),
        ...(searchQuery && { search: searchQuery }),
      });

      const res = await api.get(`/users?${params}`);
      setUsers(res.data.data.users);
      setPagination(res.data.data.pagination);
    } catch (err) {
      logger.error("خطأ في جلب المستخدمين:", err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers(1);
  }, [roleFilter, statusFilter, searchQuery]);

  const handleAddUser = async (data) => {
    try {
      await api.post("/auth/register", data);
      fetchUsers(pagination.page);
    } catch (err) {
      logger.error("خطأ في إضافة المستخدم:", err.message);
      throw err;
    }
  };

  const handleToggleStatus = async (id, newStatus) => {
    try {
      await api.patch(`/users/${id}/status`, { status: newStatus });
      fetchUsers(pagination.page);
      setIsDetailOpen(false);
    } catch (err) {
      logger.error("خطأ في تحديث الحالة:", err.message);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("هل أنت متأكد من حذف هذا المستخدم؟")) return;
    try {
      await api.delete(`/users/${id}`);
      fetchUsers(pagination.page);
    } catch (err) {
      logger.error("خطأ في حذف المستخدم:", err.message);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-edham-white">إدارة المستخدمين</h2>
          <p className="text-edham-text-muted text-sm mt-1">إدارة حسابات المستخدمين والصلاحيات</p>
        </div>
        <button onClick={() => setIsAddOpen(true)} className="btn-primary">
          <Plus className="w-4 h-4" />
          <span>مستخدم جديد</span>
        </button>
      </div>

      {/* Filters */}
      <div className="card">
        <div className="flex flex-col md:flex-row gap-4">
          <div className="relative flex-1">
            <Search className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-edham-text-muted" />
            <input
              type="text"
              placeholder="بحث بالاسم، البريد، أو الهاتف..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full bg-edham-black border border-edham-gray rounded-lg pr-10 pl-4 py-2.5 text-edham-white placeholder-edham-text-muted outline-none focus:border-edham-primary"
            />
          </div>
          <div className="relative">
            <Filter className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-edham-text-muted" />
            <select
              value={roleFilter}
              onChange={(e) => setRoleFilter(e.target.value)}
              className="appearance-none bg-edham-black border border-edham-gray rounded-lg pr-10 pl-8 py-2.5 text-edham-white outline-none focus:border-edham-primary cursor-pointer min-w-[140px]"
            >
              <option value="all">جميع الأدوار</option>
              {Object.entries(ROLE_CONFIG).map(([key, config]) => (
                <option key={key} value={key}>{config.label}</option>
              ))}
            </select>
            <ChevronDown className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-edham-text-muted pointer-events-none" />
          </div>
          <button
            onClick={() => fetchUsers(pagination.page)}
            className="btn-icon text-edham-text-muted hover:text-edham-white border border-edham-gray"
            disabled={loading}
          >
            <RefreshCw className={`w-4 h-4 ${loading ? "animate-spin" : ""}`} />
          </button>
        </div>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {Object.entries(ROLE_CONFIG).slice(0, 4).map(([key, config]) => {
          const count = users.filter((u) => u.role === key).length;
          const Icon = config.icon;
          return (
            <div
              key={key}
              onClick={() => setRoleFilter(key)}
              className={`card cursor-pointer transition-all hover:scale-[1.02] ${
                roleFilter === key ? "ring-2 ring-edham-primary" : ""
              }`}
            >
              <div className="flex items-center gap-3">
                <div className={`w-10 h-10 rounded-xl flex items-center justify-center ${config.color.split(" ")[0]}`}>
                  <Icon className="w-5 h-5" />
                </div>
                <div>
                  <p className="text-2xl font-bold text-edham-white">{count}</p>
                  <p className="text-xs text-edham-text-muted">{config.label}</p>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Table */}
      <div className="card overflow-hidden">
        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>المستخدم</th>
                <th>الدور</th>
                <th>الحالة</th>
                <th>تاريخ الإنشاء</th>
                <th>الإجراءات</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                [...Array(5)].map((_, i) => (
                  <tr key={i}><td colSpan={5}><div className="skeleton h-12 rounded-lg" /></td></tr>
                ))
              ) : users.length === 0 ? (
                <tr>
                  <td colSpan={5} className="text-center py-12">
                    <div className="flex flex-col items-center gap-3">
                      <div className="w-16 h-16 bg-edham-gray/30 rounded-full flex items-center justify-center">
                        <Users className="w-8 h-8 text-edham-text-muted" />
                      </div>
                      <p className="text-edham-text-muted">لا يوجد مستخدمين</p>
                    </div>
                  </td>
                </tr>
              ) : (
                users.map((user) => {
                  const RoleIcon = ROLE_CONFIG[user.role]?.icon || User;
                  return (
                    <tr key={user._id} className="hover:bg-edham-black/30 transition-colors">
                      <td>
                        <div className="flex items-center gap-3">
                          <div className={`w-10 h-10 rounded-full flex items-center justify-center ${
                            ROLE_CONFIG[user.role]?.color.split(" ")[0] || "bg-gray-500/20"
                          }`}>
                            <RoleIcon className="w-5 h-5" />
                          </div>
                          <div>
                            <p className="text-edham-white font-medium">{user.name}</p>
                            <p className="text-edham-text-muted text-xs">{user.email}</p>
                          </div>
                        </div>
                      </td>
                      <td>
                        <span className={`px-2.5 py-1 rounded-lg text-xs font-medium border ${
                          ROLE_CONFIG[user.role]?.color || "bg-gray-500/20 text-gray-400 border-gray-500/30"
                        }`}>
                          {ROLE_CONFIG[user.role]?.label || user.role}
                        </span>
                      </td>
                      <td>
                        <span className={`px-2.5 py-1 rounded-lg text-xs font-medium border ${
                          STATUS_CONFIG[user.status]?.color || STATUS_CONFIG.inactive.color
                        }`}>
                          {STATUS_CONFIG[user.status]?.label || user.status}
                        </span>
                      </td>
                      <td className="text-edham-text-muted text-sm">
                        {new Date(user.createdAt).toLocaleDateString("ar-EG")}
                      </td>
                      <td>
                        <div className="flex items-center gap-1">
                          <button
                            onClick={() => {
                              setSelectedUser(user);
                              setIsDetailOpen(true);
                            }}
                            className="btn-icon text-edham-text-muted hover:text-edham-primary"
                            title="عرض"
                          >
                            <Eye className="w-4 h-4" />
                          </button>
                          <button className="btn-icon text-edham-text-muted hover:text-blue-400" title="تعديل">
                            <Edit2 className="w-4 h-4" />
                          </button>
                          <button
                            onClick={() => handleDelete(user._id)}
                            className="btn-icon text-edham-text-muted hover:text-red-400"
                            title="حذف"
                          >
                            <Trash2 className="w-4 h-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>

        {pagination.pages > 1 && (
          <div className="flex items-center justify-between p-4 border-t border-edham-gray">
            <p className="text-sm text-edham-text-muted">صفحة {pagination.page} من {pagination.pages}</p>
            <div className="flex items-center gap-2">
              <button
                onClick={() => fetchUsers(pagination.page - 1)}
                disabled={pagination.page === 1}
                className="btn-sm disabled:opacity-50"
              >
                السابق
              </button>
              <button
                onClick={() => fetchUsers(pagination.page + 1)}
                disabled={pagination.page === pagination.pages}
                className="btn-sm disabled:opacity-50"
              >
                التالي
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Modals */}
      <UserDetailModal
        user={selectedUser}
        isOpen={isDetailOpen}
        onClose={() => {
          setIsDetailOpen(false);
          setSelectedUser(null);
        }}
        onToggleStatus={handleToggleStatus}
      />
      <AddUserModal
        isOpen={isAddOpen}
        onClose={() => setIsAddOpen(false)}
        onAdd={handleAddUser}
      />
    </div>
  );
};

export default UsersPage;
