/**
 * ============================================
 * 📦 Shipments Management Page - نظام إدهام
 * Edham Logistics - Professional Shipment Management
 * ============================================
 */

import React, { useState, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";
import {
  Package, Search, Filter, RefreshCw, Plus, Eye, Edit2,
  Trash2, MapPin, User, Calendar, DollarSign, Truck,
  ChevronDown, X, CheckCircle, Clock, AlertCircle
} from "lucide-react";
import api from "../../services/api";
import logger from "../../utils/logger";

// ── Status Config ─────────────────────────
const STATUS_CONFIG = {
  pending:    { label: "معلقة",    color: "bg-yellow-500/20 text-yellow-400 border-yellow-500/30" },
  confirmed:  { label: "مؤكدة",    color: "bg-blue-500/20 text-blue-400 border-blue-500/30" },
  assigned:   { label: "مسندة",    color: "bg-purple-500/20 text-purple-400 border-purple-500/30" },
  picked_up:  { label: "مستلمة",   color: "bg-indigo-500/20 text-indigo-400 border-indigo-500/30" },
  in_transit: { label: "في الطريق", color: "bg-cyan-500/20 text-cyan-400 border-cyan-500/30" },
  delivered:  { label: "مسلمة",    color: "bg-green-500/20 text-green-400 border-green-500/30" },
  failed:     { label: "فاشلة",    color: "bg-red-500/20 text-red-400 border-red-500/30" },
  cancelled:  { label: "ملغية",    color: "bg-gray-500/20 text-gray-400 border-gray-500/30" },
};

// ── Modal Component ───────────────────────
const Modal = ({ isOpen, onClose, title, children }) => {
  if (!isOpen) return null;
  return (
    <AnimatePresence>
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        className="fixed inset-0 bg-black/70 backdrop-blur-sm z-50 flex items-center justify-center p-4"
        onClick={onClose}
      >
        <motion.div
          initial={{ opacity: 0, scale: 0.95, y: 20 }}
          animate={{ opacity: 1, scale: 1, y: 0 }}
          exit={{ opacity: 0, scale: 0.95, y: 20 }}
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
      </motion.div>
    </AnimatePresence>
  );
};

// ── Shipment Detail Modal ─────────────────
const ShipmentDetailModal = ({ shipment, isOpen, onClose, onUpdateStatus }) => {
  if (!shipment) return null;

  const statusFlow = ["pending", "confirmed", "assigned", "picked_up", "in_transit", "delivered"];
  const currentIndex = statusFlow.indexOf(shipment.status);

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={`شحنة #${shipment.trackingNumber}`}>
      <div className="space-y-6">
        {/* Status Timeline */}
        <div className="bg-edham-black/50 rounded-xl p-4">
          <h4 className="text-sm font-semibold text-edham-white mb-4">حالة الشحنة</h4>
          <div className="flex items-center gap-2 flex-wrap">
            {statusFlow.map((status, index) => {
              const isActive = index <= currentIndex;
              const isCurrent = index === currentIndex;
              return (
                <React.Fragment key={status}>
                  <div className={`flex items-center gap-2 px-3 py-2 rounded-lg border ${
                    isActive ? STATUS_CONFIG[status].color : "bg-edham-gray/30 text-edham-text-muted border-edham-gray/30"
                  } ${isCurrent ? "ring-2 ring-edham-primary ring-offset-2 ring-offset-edham-dark" : ""}`}>
                    <div className={`w-2 h-2 rounded-full ${isActive ? "bg-current" : "bg-edham-text-muted"}`} />
                    <span className="text-xs font-medium">{STATUS_CONFIG[status]?.label || status}</span>
                  </div>
                  {index < statusFlow.length - 1 && (
                    <div className={`w-8 h-0.5 ${index < currentIndex ? "bg-edham-primary" : "bg-edham-gray"}`} />
                  )}
                </React.Fragment>
              );
            })}
          </div>
        </div>

        {/* Details Grid */}
        <div className="grid grid-cols-2 gap-4">
          <div className="bg-edham-black/50 rounded-xl p-4">
            <div className="flex items-center gap-2 text-edham-text-muted mb-2">
              <User className="w-4 h-4" />
              <span className="text-xs">العميل</span>
            </div>
            <p className="text-edham-white font-medium">{shipment.client?.name || "—"}</p>
            <p className="text-edham-text-muted text-xs">{shipment.client?.phone || "—"}</p>
          </div>
          <div className="bg-edham-black/50 rounded-xl p-4">
            <div className="flex items-center gap-2 text-edham-text-muted mb-2">
              <DollarSign className="w-4 h-4" />
              <span className="text-xs">السعر</span>
            </div>
            <p className="text-edham-primary font-bold text-lg">{shipment.price?.toLocaleString() || 0} ج.م</p>
          </div>
          <div className="bg-edham-black/50 rounded-xl p-4">
            <div className="flex items-center gap-2 text-edham-text-muted mb-2">
              <MapPin className="w-4 h-4" />
              <span className="text-xs">نقطة الاستلام</span>
            </div>
            <p className="text-edham-white font-medium">{shipment.pickup?.address || "—"}</p>
            <p className="text-edham-text-muted text-xs">{shipment.pickup?.city || "—"}</p>
          </div>
          <div className="bg-edham-black/50 rounded-xl p-4">
            <div className="flex items-center gap-2 text-edham-text-muted mb-2">
              <MapPin className="w-4 h-4" />
              <span className="text-xs">نقطة التسليم</span>
            </div>
            <p className="text-edham-white font-medium">{shipment.delivery?.address || "—"}</p>
            <p className="text-edham-text-muted text-xs">{shipment.delivery?.city || "—"}</p>
          </div>
        </div>

        {/* Truck Assignment */}
        {shipment.assignedTruck && (
          <div className="bg-edham-black/50 rounded-xl p-4">
            <div className="flex items-center gap-2 text-edham-text-muted mb-3">
              <Truck className="w-4 h-4" />
              <span className="text-xs">الشاحنة المسندة</span>
            </div>
            <div className="flex items-center gap-3">
              <div className="w-12 h-12 bg-edham-primary/20 rounded-xl flex items-center justify-center">
                <Truck className="w-6 h-6 text-edham-primary" />
              </div>
              <div>
                <p className="text-edham-white font-medium">{shipment.assignedTruck.plateNumber}</p>
                <p className="text-edham-text-muted text-xs">{shipment.assignedTruck.driver?.name || "بدون سائق"}</p>
              </div>
            </div>
          </div>
        )}

        {/* Actions */}
        <div className="flex gap-3 pt-4 border-t border-edham-gray">
          {currentIndex < statusFlow.length - 1 && shipment.status !== "cancelled" && (
            <button
              onClick={() => onUpdateStatus(shipment._id, statusFlow[currentIndex + 1])}
              className="btn-primary flex-1"
            >
              <CheckCircle className="w-4 h-4" />
              <span>تحديث للحالة التالية</span>
            </button>
          )}
          <button onClick={onClose} className="btn-secondary flex-1">
            إغلاق
          </button>
        </div>
      </div>
    </Modal>
  );
};

// ── Main ShipmentsPage ────────────────────
const ShipmentsPage = () => {
  const [shipments, setShipments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [statusFilter, setStatusFilter] = useState("all");
  const [selectedShipment, setSelectedShipment] = useState(null);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [pagination, setPagination] = useState({ page: 1, total: 0, pages: 1 });

  const fetchShipments = async (page = 1) => {
    setLoading(true);
    try {
      const params = new URLSearchParams({
        page: page.toString(),
        limit: "10",
        sort: "-createdAt",
        ...(statusFilter !== "all" && { status: statusFilter }),
        ...(searchQuery && { search: searchQuery }),
      });

      const res = await api.get(`/shipments?${params}`);
      setShipments(res.data.data.shipments);
      setPagination(res.data.data.pagination);
    } catch (err) {
      logger.error("خطأ في جلب الشحنات:", err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchShipments(1);
  }, [statusFilter, searchQuery]);

  const handleUpdateStatus = async (id, newStatus) => {
    try {
      await api.patch(`/shipments/${id}/status`, { status: newStatus });
      fetchShipments(pagination.page);
      setIsDetailOpen(false);
    } catch (err) {
      logger.error("خطأ في تحديث الحالة:", err.message);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("هل أنت متأكد من حذف هذه الشحنة؟")) return;
    try {
      await api.delete(`/shipments/${id}`);
      fetchShipments(pagination.page);
    } catch (err) {
      logger.error("خطأ في حذف الشحنة:", err.message);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-edham-white">إدارة الشحنات</h2>
          <p className="text-edham-text-muted text-sm mt-1">إدارة وتتبع جميع الشحنات في النظام</p>
        </div>
        <button className="btn-primary">
          <Plus className="w-4 h-4" />
          <span>شحنة جديدة</span>
        </button>
      </div>

      {/* Filters */}
      <div className="card">
        <div className="flex flex-col md:flex-row gap-4">
          {/* Search */}
          <div className="relative flex-1">
            <Search className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-edham-text-muted" />
            <input
              type="text"
              placeholder="بحث برقم التتبع، العميل، أو الموقع..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full bg-edham-black border border-edham-gray rounded-lg pr-10 pl-4 py-2.5
                       text-edham-white placeholder-edham-text-muted outline-none
                       focus:border-edham-primary transition-colors"
            />
          </div>

          {/* Status Filter */}
          <div className="relative">
            <Filter className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-edham-text-muted" />
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="appearance-none bg-edham-black border border-edham-gray rounded-lg pr-10 pl-8 py-2.5
                       text-edham-white outline-none focus:border-edham-primary transition-colors cursor-pointer min-w-[140px]"
            >
              <option value="all">جميع الحالات</option>
              {Object.entries(STATUS_CONFIG).map(([key, config]) => (
                <option key={key} value={key}>{config.label}</option>
              ))}
            </select>
            <ChevronDown className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-edham-text-muted pointer-events-none" />
          </div>

          {/* Refresh */}
          <button
            onClick={() => fetchShipments(pagination.page)}
            className="btn-icon text-edham-text-muted hover:text-edham-white border border-edham-gray"
            disabled={loading}
          >
            <RefreshCw className={`w-4 h-4 ${loading ? "animate-spin" : ""}`} />
          </button>
        </div>
      </div>

      {/* Stats Summary */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {Object.entries(STATUS_CONFIG).slice(0, 4).map(([key, config]) => {
          const count = shipments.filter((s) => s.status === key).length;
          return (
            <div
              key={key}
              onClick={() => setStatusFilter(key)}
              className={`card cursor-pointer transition-all hover:scale-[1.02] ${
                statusFilter === key ? "ring-2 ring-edham-primary" : ""
              }`}
            >
              <div className="flex items-center gap-3">
                <div className={`w-10 h-10 rounded-xl flex items-center justify-center ${config.color.split(" ")[0]}`}>
                  <Package className="w-5 h-5" />
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
                <th>رقم التتبع</th>
                <th>العميل</th>
                <th>من → إلى</th>
                <th>الحالة</th>
                <th>السعر</th>
                <th>التاريخ</th>
                <th>الإجراءات</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                [...Array(5)].map((_, i) => (
                  <tr key={i}>
                    <td colSpan={7}><div className="skeleton h-12 rounded-lg" /></td>
                  </tr>
                ))
              ) : shipments.length === 0 ? (
                <tr>
                  <td colSpan={7} className="text-center py-12">
                    <div className="flex flex-col items-center gap-3">
                      <div className="w-16 h-16 bg-edham-gray/30 rounded-full flex items-center justify-center">
                        <Package className="w-8 h-8 text-edham-text-muted" />
                      </div>
                      <p className="text-edham-text-muted">لا توجد شحنات</p>
                    </div>
                  </td>
                </tr>
              ) : (
                shipments.map((shipment) => (
                  <tr key={shipment._id} className="hover:bg-edham-black/30 transition-colors">
                    <td>
                      <span className="text-edham-primary font-mono text-sm font-medium">
                        {shipment.trackingNumber}
                      </span>
                    </td>
                    <td>
                      <div className="flex items-center gap-2">
                        <div className="w-8 h-8 bg-edham-primary/20 rounded-full flex items-center justify-center">
                          <span className="text-xs font-bold text-edham-primary">
                            {shipment.client?.name?.charAt(0) || "?"}
                          </span>
                        </div>
                        <span className="text-edham-white">{shipment.client?.name || "—"}</span>
                      </div>
                    </td>
                    <td className="text-edham-text-muted text-sm">
                      <div className="flex items-center gap-1">
                        <span>{shipment.pickup?.city || "—"}</span>
                        <span className="text-edham-primary">→</span>
                        <span>{shipment.delivery?.city || "—"}</span>
                      </div>
                    </td>
                    <td>
                      <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg text-xs font-medium border ${
                        STATUS_CONFIG[shipment.status]?.color || "bg-gray-500/20 text-gray-400 border-gray-500/30"
                      }`}>
                        <span className={`w-1.5 h-1.5 rounded-full ${
                          shipment.status === "delivered" ? "bg-green-400" :
                          shipment.status === "cancelled" ? "bg-gray-400" :
                          "bg-current animate-pulse"
                        }`} />
                        {STATUS_CONFIG[shipment.status]?.label || shipment.status}
                      </span>
                    </td>
                    <td className="text-edham-white font-medium">
                      {shipment.price?.toLocaleString() || 0} ج.م
                    </td>
                    <td className="text-edham-text-muted text-sm">
                      {new Date(shipment.createdAt).toLocaleDateString("ar-EG")}
                    </td>
                    <td>
                      <div className="flex items-center gap-1">
                        <button
                          onClick={() => {
                            setSelectedShipment(shipment);
                            setIsDetailOpen(true);
                          }}
                          className="btn-icon text-edham-text-muted hover:text-edham-primary"
                          title="عرض التفاصيل"
                        >
                          <Eye className="w-4 h-4" />
                        </button>
                        <button className="btn-icon text-edham-text-muted hover:text-blue-400" title="تعديل">
                          <Edit2 className="w-4 h-4" />
                        </button>
                        <button
                          onClick={() => handleDelete(shipment._id)}
                          className="btn-icon text-edham-text-muted hover:text-red-400"
                          title="حذف"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        {pagination.pages > 1 && (
          <div className="flex items-center justify-between p-4 border-t border-edham-gray">
            <p className="text-sm text-edham-text-muted">
              صفحة {pagination.page} من {pagination.pages}
            </p>
            <div className="flex items-center gap-2">
              <button
                onClick={() => fetchShipments(pagination.page - 1)}
                disabled={pagination.page === 1}
                className="btn-sm disabled:opacity-50"
              >
                السابق
              </button>
              <button
                onClick={() => fetchShipments(pagination.page + 1)}
                disabled={pagination.page === pagination.pages}
                className="btn-sm disabled:opacity-50"
              >
                التالي
              </button>
            </div>
          </div>
        )}
      </div>

      {/* Detail Modal */}
      <ShipmentDetailModal
        shipment={selectedShipment}
        isOpen={isDetailOpen}
        onClose={() => {
          setIsDetailOpen(false);
          setSelectedShipment(null);
        }}
        onUpdateStatus={handleUpdateStatus}
      />
    </div>
  );
};

export default ShipmentsPage;
