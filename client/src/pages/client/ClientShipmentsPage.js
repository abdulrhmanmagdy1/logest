/**
 * ============================================
 * 📦 Client Shipments Page - نظام إدهام
 * Edham Logistics - Client Shipment Management
 * ============================================
 */

import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  Package, Search, Filter, RefreshCw, Eye, MapPin, Calendar,
  DollarSign, Clock, ChevronDown, Truck, CheckCircle, X
} from "lucide-react";
import api from "../../services/api";
import logger from "../../utils/logger";

const STATUS_CONFIG = {
  pending:    { label: "معلقة",    color: "bg-yellow-500/20 text-yellow-400 border-yellow-500/30" },
  confirmed:  { label: "مؤكدة",    color: "bg-blue-500/20 text-blue-400 border-blue-500/30" },
  in_transit: { label: "في الطريق", color: "bg-cyan-500/20 text-cyan-400 border-cyan-500/30" },
  delivered:  { label: "تم التسليم", color: "bg-green-500/20 text-green-400 border-green-500/30" },
  cancelled:  { label: "ملغاة",    color: "bg-red-500/20 text-red-400 border-red-500/30" },
};

const ClientShipmentsPage = () => {
  const [shipments, setShipments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [statusFilter, setStatusFilter] = useState("all");
  const [selectedShipment, setSelectedShipment] = useState(null);

  const fetchShipments = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams({
        ...(statusFilter !== "all" && { status: statusFilter }),
        ...(searchQuery && { search: searchQuery }),
      });
      const res = await api.get(`/shipments/my-shipments?${params}`);
      setShipments(res.data.data?.shipments || []);
    } catch (err) {
      logger.error("خطأ في جلب الشحنات:", err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchShipments();
  }, [statusFilter, searchQuery]);

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-edham-white">شحناتي</h2>
          <p className="text-edham-text-muted text-sm mt-1">تتبع وإدارة شحناتك</p>
        </div>
        <button
          onClick={fetchShipments}
          className="btn-icon text-edham-text-muted hover:text-edham-white border border-edham-gray"
          disabled={loading}
        >
          <RefreshCw className={`w-4 h-4 ${loading ? "animate-spin" : ""}`} />
        </button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {Object.entries(STATUS_CONFIG).map(([key, config]) => {
          const count = shipments.filter((s) => s.status === key).length;
          return (
            <div
              key={key}
              onClick={() => setStatusFilter(key)}
              className={`card cursor-pointer transition-all hover:scale-[1.02] ${
                statusFilter === key ? "ring-2 ring-edham-primary" : ""
              }`}
            >
              <p className="text-2xl font-bold text-edham-white">{count}</p>
              <p className="text-xs text-edham-text-muted">{config.label}</p>
            </div>
          );
        })}
      </div>

      {/* Filters */}
      <div className="card">
        <div className="flex flex-col md:flex-row gap-4">
          <div className="relative flex-1">
            <Search className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-edham-text-muted" />
            <input
              type="text"
              placeholder="بحث برقم التتبع..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full bg-edham-black border border-edham-gray rounded-lg pr-10 pl-4 py-2.5 text-edham-white placeholder-edham-text-muted outline-none focus:border-edham-primary"
            />
          </div>
          <div className="relative">
            <Filter className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-edham-text-muted" />
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="appearance-none bg-edham-black border border-edham-gray rounded-lg pr-10 pl-8 py-2.5 text-edham-white outline-none focus:border-edham-primary cursor-pointer min-w-[140px]"
            >
              <option value="all">جميع الحالات</option>
              {Object.entries(STATUS_CONFIG).map(([key, config]) => (
                <option key={key} value={key}>{config.label}</option>
              ))}
            </select>
            <ChevronDown className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-edham-text-muted pointer-events-none" />
          </div>
        </div>
      </div>

      {/* Shipments List */}
      <div className="space-y-4">
        {loading ? (
          [...Array(4)].map((_, i) => (
            <div key={i} className="card"><div className="skeleton h-24 rounded-xl" /></div>
          ))
        ) : shipments.length === 0 ? (
          <div className="card py-16 text-center">
            <Package className="w-12 h-12 text-edham-text-muted mx-auto mb-3" />
            <p className="text-edham-text-muted">لا توجد شحنات</p>
          </div>
        ) : (
          shipments.map((shipment) => (
            <motion.div
              key={shipment._id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="card hover-lift"
            >
              <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div className="flex-1">
                  <div className="flex items-center gap-3 mb-3">
                    <div className={`w-12 h-12 rounded-xl flex items-center justify-center ${
                      STATUS_CONFIG[shipment.status]?.color.split(" ")[0] || "bg-gray-500/20"
                    }`}>
                      <Package className="w-6 h-6" />
                    </div>
                    <div>
                      <h4 className="text-lg font-bold text-edham-white">{shipment.trackingNumber}</h4>
                      <span className={`px-2 py-0.5 rounded text-xs font-medium border ${
                        STATUS_CONFIG[shipment.status]?.color || "bg-gray-500/20 text-gray-400 border-gray-500/30"
                      }`}>
                        {STATUS_CONFIG[shipment.status]?.label || shipment.status}
                      </span>
                    </div>
                  </div>
                  <div className="flex items-center gap-4 text-sm">
                    <span className="text-edham-text-muted">{shipment.pickup?.city} → {shipment.delivery?.city}</span>
                    <span className="text-edham-primary font-medium">{shipment.price?.toLocaleString()} ج.م</span>
                  </div>
                </div>
                <button
                  onClick={() => setSelectedShipment(shipment)}
                  className="btn-sm"
                >
                  <Eye className="w-4 h-4" />
                  <span>التفاصيل</span>
                </button>
              </div>
            </motion.div>
          ))
        )}
      </div>

      {/* Detail Modal */}
      {selectedShipment && (
        <div className="fixed inset-0 bg-black/70 backdrop-blur-sm z-50 flex items-center justify-center p-4"
             onClick={() => setSelectedShipment(null)}>
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="bg-edham-dark border border-edham-gray rounded-2xl shadow-2xl w-full max-w-lg"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="flex items-center justify-between p-5 border-b border-edham-gray">
              <h3 className="text-lg font-bold text-edham-white">شحنة #{selectedShipment.trackingNumber}</h3>
              <button onClick={() => setSelectedShipment(null)} className="btn-icon text-edham-text-muted hover:text-edham-white">
                <X className="w-5 h-5" />
              </button>
            </div>
            <div className="p-5 space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="bg-edham-black/50 rounded-xl p-3">
                  <p className="text-edham-text-muted text-xs mb-1">من</p>
                  <p className="text-edham-white">{selectedShipment.pickup?.city}</p>
                </div>
                <div className="bg-edham-black/50 rounded-xl p-3">
                  <p className="text-edham-text-muted text-xs mb-1">إلى</p>
                  <p className="text-edham-white">{selectedShipment.delivery?.city}</p>
                </div>
              </div>
              <div className="bg-edham-black/50 rounded-xl p-3">
                <p className="text-edham-text-muted text-xs mb-1">السعر</p>
                <p className="text-edham-primary font-bold text-xl">{selectedShipment.price?.toLocaleString()} ج.م</p>
              </div>
              {selectedShipment.assignedTruck && (
                <div className="bg-edham-primary/10 border border-edham-primary/30 rounded-xl p-3">
                  <p className="text-edham-primary text-xs mb-1">الشاحنة</p>
                  <p className="text-edham-white">{selectedShipment.assignedTruck.plateNumber}</p>
                </div>
              )}
            </div>
          </motion.div>
        </div>
      )}
    </div>
  );
};

export default ClientShipmentsPage;
