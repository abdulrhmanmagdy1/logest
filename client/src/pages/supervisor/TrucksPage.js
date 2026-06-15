/**
 * ============================================
 * 🚛 Trucks Management Page - نظام إدهام
 * Edham Logistics - Fleet Management
 * ============================================
 */

import React, { useState, useEffect } from "react";
import { motion, AnimatePresence } from "framer-motion";
import {
  Truck, Search, Plus, Filter, RefreshCw, Eye, Edit2, Trash2,
  MapPin, User, Wrench, Fuel, Gauge, Calendar, CheckCircle, AlertCircle,
  X, ChevronDown, Navigation, Thermometer
} from "lucide-react";
import api from "../../services/api";
import logger from "../../utils/logger";

// ── Status Config ─────────────────────────
const STATUS_CONFIG = {
  available:   { label: "متاحة",     color: "bg-green-500/20 text-green-400 border-green-500/30" },
  busy:        { label: "مشغولة",    color: "bg-blue-500/20 text-blue-400 border-blue-500/30" },
  maintenance: { label: "صيانة",     color: "bg-yellow-500/20 text-yellow-400 border-yellow-500/30" },
  offline:     { label: "غير متصل",  color: "bg-gray-500/20 text-gray-400 border-gray-500/30" },
};

const TRUCK_TYPES = {
  refrigerated: { label: "مبردة", icon: Thermometer },
  dry:          { label: "جافة", icon: Truck },
  flatbed:      { label: "مسطحة", icon: Truck },
  container:    { label: "حاوية", icon: Truck },
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

// ── Truck Detail Modal ────────────────────
const TruckDetailModal = ({ truck, isOpen, onClose, onUpdateStatus }) => {
  if (!truck) return null;

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={`شاحنة ${truck.plateNumber}`}>
      <div className="space-y-6">
        {/* Header Info */}
        <div className="flex items-center gap-4 bg-edham-black/50 rounded-xl p-4">
          <div className="w-20 h-20 bg-edham-primary/20 rounded-2xl flex items-center justify-center">
            <Truck className="w-10 h-10 text-edham-primary" />
          </div>
          <div className="flex-1">
            <div className="flex items-center gap-3 mb-2">
              <h4 className="text-xl font-bold text-edham-white">{truck.plateNumber}</h4>
              <span className={`px-2.5 py-1 rounded-lg text-xs font-medium border ${
                STATUS_CONFIG[truck.status]?.color || STATUS_CONFIG.offline.color
              }`}>
                {STATUS_CONFIG[truck.status]?.label || "غير معروف"}
              </span>
            </div>
            <p className="text-edham-text-muted text-sm">
              {TRUCK_TYPES[truck.type]?.label || truck.type} • {truck.model || "—"} • {truck.year || "—"}
            </p>
          </div>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-2 gap-4">
          <div className="bg-edham-black/50 rounded-xl p-4">
            <div className="flex items-center gap-2 text-edham-text-muted mb-2">
              <User className="w-4 h-4" />
              <span className="text-xs">السائق</span>
            </div>
            <p className="text-edham-white font-medium">{truck.driver?.name || "بدون سائق"}</p>
            <p className="text-edham-text-muted text-xs">{truck.driver?.phone || "—"}</p>
          </div>
          <div className="bg-edham-black/50 rounded-xl p-4">
            <div className="flex items-center gap-2 text-edham-text-muted mb-2">
              <Gauge className="w-4 h-4" />
              <span className="text-xs">عداد المسافات</span>
            </div>
            <p className="text-edham-white font-bold text-lg">{truck.odometer?.toLocaleString() || 0} كم</p>
          </div>
          <div className="bg-edham-black/50 rounded-xl p-4">
            <div className="flex items-center gap-2 text-edham-text-muted mb-2">
              <Fuel className="w-4 h-4" />
              <span className="text-xs">مستوى الوقود</span>
            </div>
            <div className="flex items-center gap-2">
              <div className="flex-1 h-2 bg-edham-gray rounded-full overflow-hidden">
                <div
                  className="h-full bg-edham-primary rounded-full"
                  style={{ width: `${truck.fuelLevel || 0}%` }}
                />
              </div>
              <span className="text-edham-white text-sm">{truck.fuelLevel || 0}%</span>
            </div>
          </div>
          <div className="bg-edham-black/50 rounded-xl p-4">
            <div className="flex items-center gap-2 text-edham-text-muted mb-2">
              <Wrench className="w-4 h-4" />
              <span className="text-xs">آخر صيانة</span>
            </div>
            <p className="text-edham-white font-medium">
              {truck.lastMaintenance
                ? new Date(truck.lastMaintenance).toLocaleDateString("ar-EG")
                : "—"}
            </p>
          </div>
        </div>

        {/* Location */}
        {truck.currentLocation && (
          <div className="bg-edham-black/50 rounded-xl p-4">
            <div className="flex items-center gap-2 text-edham-text-muted mb-3">
              <Navigation className="w-4 h-4" />
              <span className="text-xs">الموقع الحالي</span>
            </div>
            <div className="flex items-start gap-3">
              <MapPin className="w-5 h-5 text-edham-primary flex-shrink-0 mt-0.5" />
              <div>
                <p className="text-edham-white">
                  {truck.currentLocation.address || "الموقع غير محدد"}
                </p>
                <p className="text-edham-text-muted text-xs mt-1">
                  {truck.currentLocation.lat?.toFixed(6)}, {truck.currentLocation.lng?.toFixed(6)}
                </p>
                <p className="text-edham-text-muted text-xs">
                  آخر تحديث: {truck.locationUpdatedAt
                    ? new Date(truck.locationUpdatedAt).toLocaleString("ar-EG")
                    : "—"}
                </p>
              </div>
            </div>
          </div>
        )}

        {/* Current Shipment */}
        {truck.currentShipment && (
          <div className="bg-edham-primary/10 border border-edham-primary/30 rounded-xl p-4">
            <div className="flex items-center gap-2 text-edham-primary mb-3">
              <CheckCircle className="w-4 h-4" />
              <span className="text-xs font-semibold">الشحنة الحالية</span>
            </div>
            <p className="text-edham-white font-medium">{truck.currentShipment.trackingNumber}</p>
            <p className="text-edham-text-muted text-sm">
              {truck.currentShipment.pickup?.city} → {truck.currentShipment.delivery?.city}
            </p>
          </div>
        )}

        {/* Actions */}
        <div className="flex gap-3 pt-4 border-t border-edham-gray">
          <button
            onClick={() => onUpdateStatus(truck._id, truck.status === "available" ? "maintenance" : "available")}
            className="btn-primary flex-1"
          >
            {truck.status === "available" ? (
              <><Wrench className="w-4 h-4" /><span>إرسال للصيانة</span></>
            ) : (
              <><CheckCircle className="w-4 h-4" /><span>تعيين متاحة</span></>
            )}
          </button>
          <button onClick={onClose} className="btn-secondary flex-1">
            إغلاق
          </button>
        </div>
      </div>
    </Modal>
  );
};

// ── Add Truck Modal ───────────────────────
const AddTruckModal = ({ isOpen, onClose, onAdd }) => {
  const [formData, setFormData] = useState({
    plateNumber: "",
    type: "refrigerated",
    model: "",
    year: new Date().getFullYear(),
    capacity: "",
    driverName: "",
    driverPhone: "",
  });
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await onAdd(formData);
      onClose();
    } catch (err) {
      logger.error("خطأ في إضافة الشاحنة:", err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="إضافة شاحنة جديدة">
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm text-edham-text-muted mb-1.5">رقم اللوحة *</label>
            <input
              type="text"
              required
              value={formData.plateNumber}
              onChange={(e) => setFormData({ ...formData, plateNumber: e.target.value })}
              className="w-full bg-edham-black border border-edham-gray rounded-lg px-3 py-2.5 text-edham-white outline-none focus:border-edham-primary"
              placeholder="مثال: 12345-أ ب ت"
            />
          </div>
          <div>
            <label className="block text-sm text-edham-text-muted mb-1.5">النوع *</label>
            <select
              value={formData.type}
              onChange={(e) => setFormData({ ...formData, type: e.target.value })}
              className="w-full bg-edham-black border border-edham-gray rounded-lg px-3 py-2.5 text-edham-white outline-none focus:border-edham-primary"
            >
              {Object.entries(TRUCK_TYPES).map(([key, config]) => (
                <option key={key} value={key}>{config.label}</option>
              ))}
            </select>
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm text-edham-text-muted mb-1.5">الموديل</label>
            <input
              type="text"
              value={formData.model}
              onChange={(e) => setFormData({ ...formData, model: e.target.value })}
              className="w-full bg-edham-black border border-edham-gray rounded-lg px-3 py-2.5 text-edham-white outline-none focus:border-edham-primary"
              placeholder="مثال: Mercedes Actros"
            />
          </div>
          <div>
            <label className="block text-sm text-edham-text-muted mb-1.5">السنة</label>
            <input
              type="number"
              value={formData.year}
              onChange={(e) => setFormData({ ...formData, year: parseInt(e.target.value) })}
              className="w-full bg-edham-black border border-edham-gray rounded-lg px-3 py-2.5 text-edham-white outline-none focus:border-edham-primary"
            />
          </div>
        </div>

        <div>
          <label className="block text-sm text-edham-text-muted mb-1.5">السعة (كجم)</label>
          <input
            type="number"
            value={formData.capacity}
            onChange={(e) => setFormData({ ...formData, capacity: e.target.value })}
            className="w-full bg-edham-black border border-edham-gray rounded-lg px-3 py-2.5 text-edham-white outline-none focus:border-edham-primary"
            placeholder="مثال: 10000"
          />
        </div>

        <div className="border-t border-edham-gray pt-4">
          <h4 className="text-sm font-semibold text-edham-white mb-3">معلومات السائق</h4>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm text-edham-text-muted mb-1.5">اسم السائق</label>
              <input
                type="text"
                value={formData.driverName}
                onChange={(e) => setFormData({ ...formData, driverName: e.target.value })}
                className="w-full bg-edham-black border border-edham-gray rounded-lg px-3 py-2.5 text-edham-white outline-none focus:border-edham-primary"
                placeholder="اسم السائق"
              />
            </div>
            <div>
              <label className="block text-sm text-edham-text-muted mb-1.5">رقم الهاتف</label>
              <input
                type="tel"
                value={formData.driverPhone}
                onChange={(e) => setFormData({ ...formData, driverPhone: e.target.value })}
                className="w-full bg-edham-black border border-edham-gray rounded-lg px-3 py-2.5 text-edham-white outline-none focus:border-edham-primary"
                placeholder="01xxxxxxxxx"
              />
            </div>
          </div>
        </div>

        <div className="flex gap-3 pt-4">
          <button
            type="submit"
            disabled={loading}
            className="btn-primary flex-1 disabled:opacity-50"
          >
            {loading ? "جاري الإضافة..." : "إضافة الشاحنة"}
          </button>
          <button type="button" onClick={onClose} className="btn-secondary flex-1">
            إلغاء
          </button>
        </div>
      </form>
    </Modal>
  );
};

// ── Main TrucksPage ───────────────────────
const TrucksPage = () => {
  const [trucks, setTrucks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [statusFilter, setStatusFilter] = useState("all");
  const [selectedTruck, setSelectedTruck] = useState(null);
  const [isDetailOpen, setIsDetailOpen] = useState(false);
  const [isAddOpen, setIsAddOpen] = useState(false);
  const [pagination, setPagination] = useState({ page: 1, total: 0, pages: 1 });

  const fetchTrucks = async (page = 1) => {
    setLoading(true);
    try {
      const params = new URLSearchParams({
        page: page.toString(),
        limit: "10",
        ...(statusFilter !== "all" && { status: statusFilter }),
        ...(searchQuery && { search: searchQuery }),
      });

      const res = await api.get(`/trucks?${params}`);
      setTrucks(res.data.data.trucks);
      setPagination(res.data.data.pagination);
    } catch (err) {
      logger.error("خطأ في جلب الشاحنات:", err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTrucks(1);
  }, [statusFilter, searchQuery]);

  const handleAddTruck = async (data) => {
    try {
      await api.post("/trucks", data);
      fetchTrucks(pagination.page);
    } catch (err) {
      logger.error("خطأ في إضافة الشاحنة:", err.message);
      throw err;
    }
  };

  const handleUpdateStatus = async (id, newStatus) => {
    try {
      await api.patch(`/trucks/${id}/status`, { status: newStatus });
      fetchTrucks(pagination.page);
      setIsDetailOpen(false);
    } catch (err) {
      logger.error("خطأ في تحديث الحالة:", err.message);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("هل أنت متأكد من حذف هذه الشاحنة؟")) return;
    try {
      await api.delete(`/trucks/${id}`);
      fetchTrucks(pagination.page);
    } catch (err) {
      logger.error("خطأ في حذف الشاحنة:", err.message);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-edham-white">إدارة الأسطول</h2>
          <p className="text-edham-text-muted text-sm mt-1">إدارة الشاحنات وتتبع مواقعها</p>
        </div>
        <button onClick={() => setIsAddOpen(true)} className="btn-primary">
          <Plus className="w-4 h-4" />
          <span>إضافة شاحنة</span>
        </button>
      </div>

      {/* Filters */}
      <div className="card">
        <div className="flex flex-col md:flex-row gap-4">
          <div className="relative flex-1">
            <Search className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-edham-text-muted" />
            <input
              type="text"
              placeholder="بحث برقم اللوحة، الموديل، أو السائق..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full bg-edham-black border border-edham-gray rounded-lg pr-10 pl-4 py-2.5
                       text-edham-white placeholder-edham-text-muted outline-none
                       focus:border-edham-primary transition-colors"
            />
          </div>
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
          <button
            onClick={() => fetchTrucks(pagination.page)}
            className="btn-icon text-edham-text-muted hover:text-edham-white border border-edham-gray"
            disabled={loading}
          >
            <RefreshCw className={`w-4 h-4 ${loading ? "animate-spin" : ""}`} />
          </button>
        </div>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {Object.entries(STATUS_CONFIG).map(([key, config]) => {
          const count = trucks.filter((t) => t.status === key).length;
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
                  <Truck className="w-5 h-5" />
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

      {/* Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {loading ? (
          [...Array(6)].map((_, i) => (
            <div key={i} className="card">
              <div className="skeleton h-32 rounded-xl" />
            </div>
          ))
        ) : trucks.length === 0 ? (
          <div className="col-span-full card py-16">
            <div className="flex flex-col items-center gap-3">
              <div className="w-16 h-16 bg-edham-gray/30 rounded-full flex items-center justify-center">
                <Truck className="w-8 h-8 text-edham-text-muted" />
              </div>
              <p className="text-edham-text-muted">لا توجد شاحنات</p>
            </div>
          </div>
        ) : (
          trucks.map((truck) => (
            <motion.div
              key={truck._id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="card hover-lift"
            >
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-center gap-3">
                  <div className="w-12 h-12 bg-edham-primary/20 rounded-xl flex items-center justify-center">
                    <Truck className="w-6 h-6 text-edham-primary" />
                  </div>
                  <div>
                    <h4 className="text-edham-white font-semibold">{truck.plateNumber}</h4>
                    <p className="text-edham-text-muted text-xs">
                      {TRUCK_TYPES[truck.type]?.label || truck.type}
                    </p>
                  </div>
                </div>
                <span className={`px-2 py-1 rounded-lg text-xs font-medium border ${
                  STATUS_CONFIG[truck.status]?.color || STATUS_CONFIG.offline.color
                }`}>
                  {STATUS_CONFIG[truck.status]?.label || "غير معروف"}
                </span>
              </div>

              <div className="space-y-2 mb-4">
                <div className="flex items-center gap-2 text-edham-text-muted text-sm">
                  <User className="w-4 h-4" />
                  <span>{truck.driver?.name || "بدون سائق"}</span>
                </div>
                <div className="flex items-center gap-2 text-edham-text-muted text-sm">
                  <MapPin className="w-4 h-4" />
                  <span className="truncate">{truck.currentLocation?.city || "الموقع غير معروف"}</span>
                </div>
                <div className="flex items-center gap-2 text-edham-text-muted text-sm">
                  <Gauge className="w-4 h-4" />
                  <span>{truck.odometer?.toLocaleString() || 0} كم</span>
                </div>
              </div>

              <div className="flex items-center gap-2 pt-4 border-t border-edham-gray">
                <button
                  onClick={() => {
                    setSelectedTruck(truck);
                    setIsDetailOpen(true);
                  }}
                  className="btn-sm flex-1"
                >
                  <Eye className="w-4 h-4" />
                  <span>عرض</span>
                </button>
                <button className="btn-icon text-edham-text-muted hover:text-blue-400">
                  <Edit2 className="w-4 h-4" />
                </button>
                <button
                  onClick={() => handleDelete(truck._id)}
                  className="btn-icon text-edham-text-muted hover:text-red-400"
                >
                  <Trash2 className="w-4 h-4" />
                </button>
              </div>
            </motion.div>
          ))
        )}
      </div>

      {/* Pagination */}
      {pagination.pages > 1 && (
        <div className="flex items-center justify-center gap-2">
          <button
            onClick={() => fetchTrucks(pagination.page - 1)}
            disabled={pagination.page === 1}
            className="btn-sm disabled:opacity-50"
          >
            السابق
          </button>
          <span className="text-edham-text-muted text-sm">
            صفحة {pagination.page} من {pagination.pages}
          </span>
          <button
            onClick={() => fetchTrucks(pagination.page + 1)}
            disabled={pagination.page === pagination.pages}
            className="btn-sm disabled:opacity-50"
          >
            التالي
          </button>
        </div>
      )}

      {/* Modals */}
      <TruckDetailModal
        truck={selectedTruck}
        isOpen={isDetailOpen}
        onClose={() => {
          setIsDetailOpen(false);
          setSelectedTruck(null);
        }}
        onUpdateStatus={handleUpdateStatus}
      />
      <AddTruckModal
        isOpen={isAddOpen}
        onClose={() => setIsAddOpen(false)}
        onAdd={handleAddTruck}
      />
    </div>
  );
};

export default TrucksPage;
