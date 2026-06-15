/**
 * ============================================
 * 🛣️ Employee Trips Page - نظام إدهام
 * Edham Logistics - Trip Management
 * ============================================
 */

import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  Navigation, MapPin, Calendar, Clock, Truck, CheckCircle,
  AlertCircle, ArrowRight, RefreshCw, Filter, ChevronDown,
  Play, Pause, Flag, Package
} from "lucide-react";
import api from "../../services/api";
import logger from "../../utils/logger";

// ── Status Config ─────────────────────────
const STATUS_CONFIG = {
  scheduled:   { label: "مجدولة",    color: "bg-blue-500/20 text-blue-400 border-blue-500/30" },
  in_progress: { label: "قيد التنفيذ", color: "bg-yellow-500/20 text-yellow-400 border-yellow-500/30" },
  completed:   { label: "مكتملة",    color: "bg-green-500/20 text-green-400 border-green-500/30" },
  cancelled:   { label: "ملغاة",     color: "bg-red-500/20 text-red-400 border-red-500/30" },
};

// ── Main TripsPage ────────────────────────
const TripsPage = () => {
  const [trips, setTrips] = useState([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState("all");
  const [activeTrip, setActiveTrip] = useState(null);

  const fetchTrips = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams({
        ...(statusFilter !== "all" && { status: statusFilter }),
      });
      const res = await api.get(`/trips?${params}`);
      setTrips(res.data.data?.trips || []);
      // Find active trip
      const active = res.data.data?.trips?.find(t => t.status === "in_progress");
      setActiveTrip(active);
    } catch (err) {
      logger.error("خطأ في جلب الرحلات:", err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTrips();
  }, [statusFilter]);

  const handleStartTrip = async (tripId) => {
    try {
      await api.patch(`/trips/${tripId}/start`);
      fetchTrips();
    } catch (err) {
      logger.error("خطأ في بدء الرحلة:", err.message);
    }
  };

  const handleCompleteTrip = async (tripId) => {
    try {
      await api.patch(`/trips/${tripId}/complete`);
      fetchTrips();
    } catch (err) {
      logger.error("خطأ في إكمال الرحلة:", err.message);
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-edham-white">رحلاتي</h2>
          <p className="text-edham-text-muted text-sm mt-1">إدارة وتتبع الرحلات المسندة إليك</p>
        </div>
        <div className="flex items-center gap-3">
          <button
            onClick={fetchTrips}
            className="btn-icon text-edham-text-muted hover:text-edham-white border border-edham-gray"
            disabled={loading}
          >
            <RefreshCw className={`w-4 h-4 ${loading ? "animate-spin" : ""}`} />
          </button>
          <div className="relative">
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="appearance-none bg-edham-black border border-edham-gray rounded-lg pr-4 pl-8 py-2 text-edham-white outline-none focus:border-edham-primary cursor-pointer"
            >
              <option value="all">جميع الرحلات</option>
              {Object.entries(STATUS_CONFIG).map(([key, config]) => (
                <option key={key} value={key}>{config.label}</option>
              ))}
            </select>
            <ChevronDown className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-edham-text-muted pointer-events-none" />
          </div>
        </div>
      </div>

      {/* Active Trip Card */}
      {activeTrip && (
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          className="card border-2 border-edham-primary/50 bg-edham-primary/5"
        >
          <div className="flex items-center gap-2 text-edham-primary mb-4">
            <Play className="w-5 h-5" />
            <span className="font-semibold">الرحلة النشطة</span>
          </div>
          <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div className="flex-1">
              <div className="flex items-center gap-3 mb-3">
                <div className="w-12 h-12 bg-edham-primary/20 rounded-xl flex items-center justify-center">
                  <Navigation className="w-6 h-6 text-edham-primary" />
                </div>
                <div>
                  <h4 className="text-lg font-bold text-edham-white">رحلة #{activeTrip.tripNumber}</h4>
                  <p className="text-edham-text-muted text-sm">{activeTrip.vehicle?.plateNumber}</p>
                </div>
              </div>
              <div className="flex items-center gap-4 text-sm">
                <div className="flex items-center gap-2 text-edham-text-muted">
                  <MapPin className="w-4 h-4" />
                  <span>{activeTrip.origin?.city}</span>
                </div>
                <ArrowRight className="w-4 h-4 text-edham-primary" />
                <div className="flex items-center gap-2 text-edham-text-muted">
                  <Flag className="w-4 h-4" />
                  <span>{activeTrip.destination?.city}</span>
                </div>
              </div>
            </div>
            <button
              onClick={() => handleCompleteTrip(activeTrip._id)}
              className="btn-primary"
            >
              <CheckCircle className="w-4 h-4" />
              <span>إنهاء الرحلة</span>
            </button>
          </div>
        </motion.div>
      )}

      {/* Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <div className="card">
          <p className="text-2xl font-bold text-edham-white">{trips.length}</p>
          <p className="text-xs text-edham-text-muted">إجمالي الرحلات</p>
        </div>
        <div className="card">
          <p className="text-2xl font-bold text-edham-white">
            {trips.filter(t => t.status === "completed").length}
          </p>
          <p className="text-xs text-edham-text-muted">مكتملة</p>
        </div>
        <div className="card">
          <p className="text-2xl font-bold text-edham-white">
            {trips.filter(t => t.status === "in_progress").length}
          </p>
          <p className="text-xs text-edham-text-muted">قيد التنفيذ</p>
        </div>
        <div className="card">
          <p className="text-2xl font-bold text-edham-white">
            {Math.round(trips.filter(t => t.status === "completed").reduce((sum, t) => sum + (t.distance || 0), 0))} كم
          </p>
          <p className="text-xs text-edham-text-muted">إجمالي المسافة</p>
        </div>
      </div>

      {/* Trips List */}
      <div className="space-y-4">
        {loading ? (
          [...Array(4)].map((_, i) => (
            <div key={i} className="card">
              <div className="skeleton h-24 rounded-xl" />
            </div>
          ))
        ) : trips.length === 0 ? (
          <div className="card py-16">
            <div className="flex flex-col items-center gap-3">
              <div className="w-16 h-16 bg-edham-gray/30 rounded-full flex items-center justify-center">
                <Navigation className="w-8 h-8 text-edham-text-muted" />
              </div>
              <p className="text-edham-text-muted">لا توجد رحلات</p>
            </div>
          </div>
        ) : (
          trips.map((trip) => (
            <motion.div
              key={trip._id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="card hover-lift"
            >
              <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div className="flex-1">
                  <div className="flex items-center gap-3 mb-3">
                    <div className={`w-12 h-12 rounded-xl flex items-center justify-center ${
                      trip.status === "in_progress"
                        ? "bg-yellow-500/20 text-yellow-400"
                        : trip.status === "completed"
                        ? "bg-green-500/20 text-green-400"
                        : "bg-blue-500/20 text-blue-400"
                    }`}>
                      <Truck className="w-6 h-6" />
                    </div>
                    <div>
                      <h4 className="text-lg font-bold text-edham-white">رحلة #{trip.tripNumber}</h4>
                      <div className="flex items-center gap-2">
                        <span className={`px-2 py-0.5 rounded text-xs font-medium border ${
                          STATUS_CONFIG[trip.status]?.color || STATUS_CONFIG.scheduled.color
                        }`}>
                          {STATUS_CONFIG[trip.status]?.label || "مجدولة"}
                        </span>
                        <span className="text-edham-text-muted text-sm">{trip.vehicle?.plateNumber}</span>
                      </div>
                    </div>
                  </div>

                  <div className="flex items-center gap-6 text-sm">
                    <div className="flex items-center gap-2">
                      <MapPin className="w-4 h-4 text-edham-text-muted" />
                      <span className="text-edham-white">{trip.origin?.city || "—"}</span>
                    </div>
                    <ArrowRight className="w-4 h-4 text-edham-primary" />
                    <div className="flex items-center gap-2">
                      <Flag className="w-4 h-4 text-edham-text-muted" />
                      <span className="text-edham-white">{trip.destination?.city || "—"}</span>
                    </div>
                  </div>

                  <div className="flex items-center gap-6 mt-3 text-sm text-edham-text-muted">
                    <div className="flex items-center gap-1.5">
                      <Calendar className="w-4 h-4" />
                      <span>{new Date(trip.scheduledDate).toLocaleDateString("ar-EG")}</span>
                    </div>
                    {trip.distance && (
                      <div className="flex items-center gap-1.5">
                        <Navigation className="w-4 h-4" />
                        <span>{trip.distance} كم</span>
                      </div>
                    )}
                    {trip.shipments?.length > 0 && (
                      <div className="flex items-center gap-1.5">
                        <Package className="w-4 h-4" />
                        <span>{trip.shipments.length} شحنة</span>
                      </div>
                    )}
                  </div>
                </div>

                <div className="flex items-center gap-2">
                  {trip.status === "scheduled" && (
                    <button
                      onClick={() => handleStartTrip(trip._id)}
                      className="btn-primary"
                    >
                      <Play className="w-4 h-4" />
                      <span>بدء</span>
                    </button>
                  )}
                  {trip.status === "in_progress" && (
                    <button
                      onClick={() => handleCompleteTrip(trip._id)}
                      className="btn-primary"
                    >
                      <CheckCircle className="w-4 h-4" />
                      <span>إنهاء</span>
                    </button>
                  )}
                  <button className="btn-sm">
                    التفاصيل
                  </button>
                </div>
              </div>
            </motion.div>
          ))
        )}
      </div>
    </div>
  );
};

export default TripsPage;
