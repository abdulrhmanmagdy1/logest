/**
 * ============================================
 * 🚗 Employee Vehicles Page - نظام إدهام
 * Edham Logistics - Employee Vehicle Management
 * ============================================
 */

import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  Car, Search, Wrench, Calendar, Gauge, Fuel, AlertCircle,
  CheckCircle, Clock, MapPin, RefreshCw, ChevronDown, FileText,
  ArrowRight, Info
} from "lucide-react";
import api from "../../services/api";
import logger from "../../utils/logger";

// ── Status Config ─────────────────────────
const STATUS_CONFIG = {
  available:   { label: "متاحة",     color: "bg-green-500/20 text-green-400 border-green-500/30" },
  in_use:      { label: "قيد الاستخدام", color: "bg-blue-500/20 text-blue-400 border-blue-500/30" },
  maintenance: { label: "صيانة",     color: "bg-yellow-500/20 text-yellow-400 border-yellow-500/30" },
};

// ── Main VehiclesPage ─────────────────────
const VehiclesPage = () => {
  const [vehicles, setVehicles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedVehicle, setSelectedVehicle] = useState(null);

  const fetchVehicles = async () => {
    setLoading(true);
    try {
      const res = await api.get("/employee-vehicles/my-vehicles");
      setVehicles(res.data.data || []);
    } catch (err) {
      logger.error("خطأ في جلب المركبات:", err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchVehicles();
  }, []);

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-edham-white">مركباتي</h2>
          <p className="text-edham-text-muted text-sm mt-1">إدارة المركبات المسندة إليك</p>
        </div>
        <button
          onClick={fetchVehicles}
          className="btn-icon text-edham-text-muted hover:text-edham-white border border-edham-gray"
          disabled={loading}
        >
          <RefreshCw className={`w-4 h-4 ${loading ? "animate-spin" : ""}`} />
        </button>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-3 gap-4">
        <div className="card">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-blue-500/20 flex items-center justify-center text-blue-400">
              <Car className="w-5 h-5" />
            </div>
            <div>
              <p className="text-2xl font-bold text-edham-white">{vehicles.length}</p>
              <p className="text-xs text-edham-text-muted">إجمالي المركبات</p>
            </div>
          </div>
        </div>
        <div className="card">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-green-500/20 flex items-center justify-center text-green-400">
              <CheckCircle className="w-5 h-5" />
            </div>
            <div>
              <p className="text-2xl font-bold text-edham-white">
                {vehicles.filter(v => v.status === "available").length}
              </p>
              <p className="text-xs text-edham-text-muted">متاحة</p>
            </div>
          </div>
        </div>
        <div className="card">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-yellow-500/20 flex items-center justify-center text-yellow-400">
              <Wrench className="w-5 h-5" />
            </div>
            <div>
              <p className="text-2xl font-bold text-edham-white">
                {vehicles.filter(v => v.status === "maintenance").length}
              </p>
              <p className="text-xs text-edham-text-muted">في الصيانة</p>
            </div>
          </div>
        </div>
      </div>

      {/* Vehicles List */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {loading ? (
          [...Array(4)].map((_, i) => (
            <div key={i} className="card">
              <div className="skeleton h-48 rounded-xl" />
            </div>
          ))
        ) : vehicles.length === 0 ? (
          <div className="col-span-full card py-16">
            <div className="flex flex-col items-center gap-3">
              <div className="w-16 h-16 bg-edham-gray/30 rounded-full flex items-center justify-center">
                <Car className="w-8 h-8 text-edham-text-muted" />
              </div>
              <p className="text-edham-text-muted">لا توجد مركبات مسندة إليك</p>
            </div>
          </div>
        ) : (
          vehicles.map((vehicle) => (
            <motion.div
              key={vehicle._id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="card hover-lift"
            >
              <div className="flex items-start justify-between mb-4">
                <div className="flex items-center gap-3">
                  <div className="w-14 h-14 bg-edham-primary/20 rounded-xl flex items-center justify-center">
                    <Car className="w-7 h-7 text-edham-primary" />
                  </div>
                  <div>
                    <h4 className="text-lg font-bold text-edham-white">{vehicle.plateNumber}</h4>
                    <p className="text-edham-text-muted text-sm">{vehicle.model || "—"} {vehicle.year || ""}</p>
                  </div>
                </div>
                <span className={`px-2.5 py-1 rounded-lg text-xs font-medium border ${
                  STATUS_CONFIG[vehicle.status]?.color || STATUS_CONFIG.available.color
                }`}>
                  {STATUS_CONFIG[vehicle.status]?.label || "متاحة"}
                </span>
              </div>

              <div className="space-y-3 mb-4">
                <div className="flex items-center gap-2 text-edham-text-muted text-sm">
                  <Gauge className="w-4 h-4" />
                  <span>{vehicle.odometer?.toLocaleString() || 0} كم</span>
                </div>
                <div className="flex items-center gap-2 text-edham-text-muted text-sm">
                  <Fuel className="w-4 h-4" />
                  <span>مستوى الوقود: {vehicle.fuelLevel || 0}%</span>
                </div>
                <div className="flex items-center gap-2 text-edham-text-muted text-sm">
                  <Calendar className="w-4 h-4" />
                  <span>آخر صيانة: {vehicle.lastMaintenance
                    ? new Date(vehicle.lastMaintenance).toLocaleDateString("ar-EG")
                    : "—"}
                  </span>
                </div>
              </div>

              {/* Next Maintenance Alert */}
              {vehicle.nextMaintenance && new Date(vehicle.nextMaintenance) < new Date(Date.now() + 7 * 24 * 60 * 60 * 1000) && (
                <div className="bg-yellow-500/10 border border-yellow-500/30 rounded-lg p-3 mb-4">
                  <div className="flex items-center gap-2 text-yellow-400 text-sm">
                    <AlertCircle className="w-4 h-4" />
                    <span>صيانة قريبة: {new Date(vehicle.nextMaintenance).toLocaleDateString("ar-EG")}</span>
                  </div>
                </div>
              )}

              <div className="flex items-center gap-2 pt-4 border-t border-edham-gray">
                <button className="btn-sm flex-1">
                  <Info className="w-4 h-4" />
                  <span>التفاصيل</span>
                </button>
                <button className="btn-sm flex-1">
                  <Wrench className="w-4 h-4" />
                  <span>طلب صيانة</span>
                </button>
              </div>
            </motion.div>
          ))
        )}
      </div>
    </div>
  );
};

export default VehiclesPage;
