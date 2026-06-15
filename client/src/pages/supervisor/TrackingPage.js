/**
 * ============================================
 * 📍 Live Tracking Page - نظام إدهام
 * Edham Logistics - Real-time Fleet Tracking
 * ============================================
 */

import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  MapPin, Navigation, Truck, Package, Clock, Signal,
  Battery, Gauge, User, Phone, X, ChevronDown, RefreshCw,
  Maximize2, Layers, LocateFixed
} from "lucide-react";
import api from "../../services/api";
import logger from "../../utils/logger";

// ── Status Config ─────────────────────────
const STATUS_CONFIG = {
  available:   { label: "متاح",     color: "bg-green-500 text-white" },
  busy:        { label: "مشغول",    color: "bg-blue-500 text-white" },
  maintenance: { label: "صيانة",    color: "bg-yellow-500 text-edham-black" },
  offline:     { label: "غير متصل", color: "bg-gray-500 text-white" },
};

// ── Mock Map Component (Professional placeholder) ──
const MapView = ({ trucks, selectedTruck, onTruckSelect }) => {
  // In a real app, this would use Google Maps, Mapbox, or Leaflet
  return (
    <div className="relative w-full h-full bg-edham-black rounded-xl overflow-hidden">
      {/* Grid Pattern Background */}
      <div className="absolute inset-0 opacity-10"
           style={{
             backgroundImage: `
               linear-gradient(rgba(220, 38, 38, 0.3) 1px, transparent 1px),
               linear-gradient(90deg, rgba(220, 38, 38, 0.3) 1px, transparent 1px)
             `,
             backgroundSize: '50px 50px'
           }} />

      {/* Mock Map Elements */}
      <div className="absolute inset-0 flex items-center justify-center">
        <div className="text-center">
          <div className="w-24 h-24 bg-edham-primary/20 rounded-full flex items-center justify-center mx-auto mb-4">
            <MapPin className="w-12 h-12 text-edham-primary" />
          </div>
          <p className="text-edham-text-muted">نظام التتبع المباشر</p>
          <p className="text-edham-text-muted text-sm mt-1">يتم تحميل الخريطة...</p>
        </div>
      </div>

      {/* Truck Markers (Mock positions) */}
      {trucks.map((truck, index) => {
        const isSelected = selectedTruck?._id === truck._id;
        // Generate pseudo-random position based on truck ID
        const top = 20 + ((index * 15) % 60);
        const left = 10 + ((index * 25) % 80);

        return (
          <motion.button
            key={truck._id}
            initial={{ scale: 0 }}
            animate={{ scale: 1 }}
            className={`absolute transform -translate-x-1/2 -translate-y-1/2 transition-all ${
              isSelected ? "z-20" : "z-10"
            }`}
            style={{ top: `${top}%`, left: `${left}%` }}
            onClick={() => onTruckSelect(truck)}
          >
            <div className={`relative flex flex-col items-center ${isSelected ? "scale-125" : ""}`}>
              <div className={`w-10 h-10 rounded-xl flex items-center justify-center shadow-lg ${
                STATUS_CONFIG[truck.status]?.color || "bg-gray-500"
              } ${isSelected ? "ring-4 ring-edham-primary/50" : ""}`}>
                <Truck className="w-5 h-5" />
              </div>
              <div className="mt-1 px-2 py-0.5 bg-edham-dark border border-edham-gray rounded text-xs text-edham-white whitespace-nowrap shadow-lg">
                {truck.plateNumber}
              </div>
            </div>
          </motion.button>
        );
      })}

      {/* Map Controls */}
      <div className="absolute top-4 right-4 flex flex-col gap-2">
        <button className="w-10 h-10 bg-edham-dark border border-edham-gray rounded-lg flex items-center justify-center text-edham-text-muted hover:text-edham-white hover:border-edham-primary transition-colors shadow-lg">
          <Maximize2 className="w-5 h-5" />
        </button>
        <button className="w-10 h-10 bg-edham-dark border border-edham-gray rounded-lg flex items-center justify-center text-edham-text-muted hover:text-edham-white hover:border-edham-primary transition-colors shadow-lg">
          <Layers className="w-5 h-5" />
        </button>
        <button className="w-10 h-10 bg-edham-dark border border-edham-gray rounded-lg flex items-center justify-center text-edham-text-muted hover:text-edham-white hover:border-edham-primary transition-colors shadow-lg">
          <LocateFixed className="w-5 h-5" />
        </button>
      </div>

      {/* Legend */}
      <div className="absolute bottom-4 left-4 bg-edham-dark/90 backdrop-blur-sm border border-edham-gray rounded-xl p-3 shadow-lg">
        <div className="flex items-center gap-4 text-xs">
          {Object.entries(STATUS_CONFIG).map(([key, config]) => (
            <div key={key} className="flex items-center gap-1.5">
              <div className={`w-3 h-3 rounded ${config.color.split(" ")[0]}`} />
              <span className="text-edham-text-muted">{config.label}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

// ── Truck Detail Panel ────────────────────
const TruckDetailPanel = ({ truck, onClose }) => {
  if (!truck) return null;

  return (
    <motion.div
      initial={{ x: 300, opacity: 0 }}
      animate={{ x: 0, opacity: 1 }}
      exit={{ x: 300, opacity: 0 }}
      className="w-80 bg-edham-dark border-l border-edham-gray p-5 overflow-y-auto"
    >
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <h3 className="text-lg font-bold text-edham-white">تفاصيل الشاحنة</h3>
        <button onClick={onClose} className="btn-icon text-edham-text-muted hover:text-edham-white">
          <X className="w-5 h-5" />
        </button>
      </div>

      {/* Truck Info */}
      <div className="flex items-center gap-4 mb-6">
        <div className="w-16 h-16 bg-edham-primary/20 rounded-2xl flex items-center justify-center">
          <Truck className="w-8 h-8 text-edham-primary" />
        </div>
        <div>
          <h4 className="text-xl font-bold text-edham-white">{truck.plateNumber}</h4>
          <span className={`px-2 py-0.5 rounded text-xs font-medium ${
            STATUS_CONFIG[truck.status]?.color || "bg-gray-500 text-white"
          }`}>
            {STATUS_CONFIG[truck.status]?.label || truck.status}
          </span>
        </div>
      </div>

      {/* Stats */}
      <div className="space-y-4 mb-6">
        <div className="bg-edham-black/50 rounded-xl p-4">
          <div className="flex items-center gap-2 text-edham-text-muted text-xs mb-2">
            <Navigation className="w-4 h-4" />
            <span>الموقع الحالي</span>
          </div>
          <p className="text-edham-white text-sm">{truck.currentLocation?.address || "—"}</p>
          <p className="text-edham-text-muted text-xs mt-1">
            {truck.currentLocation?.lat?.toFixed(6)}, {truck.currentLocation?.lng?.toFixed(6)}
          </p>
        </div>

        <div className="bg-edham-black/50 rounded-xl p-4">
          <div className="flex items-center gap-2 text-edham-text-muted text-xs mb-2">
            <User className="w-4 h-4" />
            <span>السائق</span>
          </div>
          <p className="text-edham-white">{truck.driver?.name || "بدون سائق"}</p>
          <p className="text-edham-text-muted text-xs">{truck.driver?.phone || "—"}</p>
        </div>

        <div className="grid grid-cols-2 gap-3">
          <div className="bg-edham-black/50 rounded-xl p-3">
            <div className="flex items-center gap-1.5 text-edham-text-muted text-xs mb-1">
              <Gauge className="w-3.5 h-3.5" />
              <span>السرعة</span>
            </div>
            <p className="text-edham-white font-bold">{truck.speed || 0} كم/س</p>
          </div>
          <div className="bg-edham-black/50 rounded-xl p-3">
            <div className="flex items-center gap-1.5 text-edham-text-muted text-xs mb-1">
              <Battery className="w-3.5 h-3.5" />
              <span>البطارية</span>
            </div>
            <p className="text-edham-white font-bold">{truck.batteryLevel || 100}%</p>
          </div>
        </div>

        <div className="bg-edham-black/50 rounded-xl p-4">
          <div className="flex items-center gap-2 text-edham-text-muted text-xs mb-2">
            <Signal className="w-4 h-4" />
            <span>جودة الإشارة</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="flex-1 h-2 bg-edham-gray rounded-full overflow-hidden">
              <div
                className="h-full bg-green-500 rounded-full"
                style={{ width: `${truck.signalStrength || 85}%` }}
              />
            </div>
            <span className="text-edham-white text-sm">{truck.signalStrength || 85}%</span>
          </div>
        </div>

        <div className="bg-edham-black/50 rounded-xl p-4">
          <div className="flex items-center gap-2 text-edham-text-muted text-xs mb-2">
            <Clock className="w-4 h-4" />
            <span>آخر تحديث</span>
          </div>
          <p className="text-edham-white text-sm">
            {truck.locationUpdatedAt
              ? new Date(truck.locationUpdatedAt).toLocaleString("ar-EG")
              : "—"}
          </p>
        </div>
      </div>

      {/* Current Shipment */}
      {truck.currentShipment && (
        <div className="bg-edham-primary/10 border border-edham-primary/30 rounded-xl p-4">
          <div className="flex items-center gap-2 text-edham-primary mb-3">
            <Package className="w-5 h-5" />
            <span className="font-semibold">الشحنة الحالية</span>
          </div>
          <p className="text-edham-white font-medium mb-1">{truck.currentShipment.trackingNumber}</p>
          <p className="text-edham-text-muted text-sm">
            {truck.currentShipment.pickup?.city} → {truck.currentShipment.delivery?.city}
          </p>
        </div>
      )}
    </motion.div>
  );
};

// ── Main TrackingPage ─────────────────────
const TrackingPage = () => {
  const [trucks, setTrucks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedTruck, setSelectedTruck] = useState(null);
  const [statusFilter, setStatusFilter] = useState("all");
  const [searchQuery, setSearchQuery] = useState("");

  const fetchTrucks = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams({
        limit: "100",
        ...(statusFilter !== "all" && { status: statusFilter }),
      });

      const res = await api.get(`/trucks?${params}`);
      const trucksData = res.data.data.trucks.map(truck => ({
        ...truck,
        // Add mock tracking data for demonstration
        speed: Math.floor(Math.random() * 80),
        batteryLevel: Math.floor(Math.random() * 30) + 70,
        signalStrength: Math.floor(Math.random() * 20) + 80,
        currentLocation: truck.currentLocation || {
          lat: 30.0444 + (Math.random() - 0.5) * 0.1,
          lng: 31.2357 + (Math.random() - 0.5) * 0.1,
          address: "القاهرة، مصر"
        }
      }));
      setTrucks(trucksData);
    } catch (err) {
      logger.error("خطأ في جلب بيانات الشاحنات:", err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTrucks();
    // Auto-refresh every 30 seconds
    const interval = setInterval(fetchTrucks, 30000);
    return () => clearInterval(interval);
  }, [statusFilter]);

  const filteredTrucks = trucks.filter(truck =>
    searchQuery === "" ||
    truck.plateNumber?.toLowerCase().includes(searchQuery.toLowerCase()) ||
    truck.driver?.name?.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="h-full flex flex-col">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-4">
        <div>
          <h2 className="text-2xl font-bold text-edham-white">التتبع المباشر</h2>
          <p className="text-edham-text-muted text-sm mt-1">تتبع مواقع الشاحنات في الوقت الفعلي</p>
        </div>
        <div className="flex items-center gap-3">
          <button
            onClick={fetchTrucks}
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
              <option value="all">جميع الشاحنات</option>
              {Object.entries(STATUS_CONFIG).map(([key, config]) => (
                <option key={key} value={key}>{config.label}</option>
              ))}
            </select>
            <ChevronDown className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-edham-text-muted pointer-events-none" />
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="flex-1 flex gap-4 min-h-0">
        {/* Map */}
        <div className="flex-1 min-h-0">
          <MapView
            trucks={filteredTrucks}
            selectedTruck={selectedTruck}
            onTruckSelect={setSelectedTruck}
          />
        </div>

        {/* Sidebar */}
        <div className="w-80 flex flex-col gap-4">
          {/* Search */}
          <div className="card">
            <div className="relative">
              <MapPin className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-edham-text-muted" />
              <input
                type="text"
                placeholder="بحث بلوحة أو سائق..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="w-full bg-edham-black border border-edham-gray rounded-lg pr-10 pl-4 py-2.5 text-edham-white placeholder-edham-text-muted outline-none focus:border-edham-primary"
              />
            </div>
          </div>

          {/* Trucks List */}
          <div className="card flex-1 overflow-hidden flex flex-col">
            <h3 className="text-sm font-semibold text-edham-white mb-3">الشاحنات ({filteredTrucks.length})</h3>
            <div className="overflow-y-auto flex-1 -mx-3 px-3 space-y-2">
              {loading ? (
                [...Array(5)].map((_, i) => (
                  <div key={i} className="skeleton h-16 rounded-xl" />
                ))
              ) : filteredTrucks.length === 0 ? (
                <div className="text-center py-8">
                  <Truck className="w-8 h-8 text-edham-text-muted mx-auto mb-2" />
                  <p className="text-edham-text-muted text-sm">لا توجد شاحنات</p>
                </div>
              ) : (
                filteredTrucks.map((truck) => (
                  <button
                    key={truck._id}
                    onClick={() => setSelectedTruck(truck)}
                    className={`w-full text-right p-3 rounded-xl transition-colors ${
                      selectedTruck?._id === truck._id
                        ? "bg-edham-primary/20 border border-edham-primary/50"
                        : "bg-edham-black/50 hover:bg-edham-black"
                    }`}
                  >
                    <div className="flex items-center gap-3">
                      <div className={`w-10 h-10 rounded-lg flex items-center justify-center ${
                        STATUS_CONFIG[truck.status]?.color || "bg-gray-500"
                      }`}>
                        <Truck className="w-5 h-5" />
                      </div>
                      <div className="flex-1 min-w-0">
                        <p className="text-edham-white font-medium truncate">{truck.plateNumber}</p>
                        <p className="text-edham-text-muted text-xs truncate">
                          {truck.driver?.name || "بدون سائق"} • {truck.speed || 0} كم/س
                        </p>
                      </div>
                    </div>
                  </button>
                ))
              )}
            </div>
          </div>
        </div>

        {/* Detail Panel */}
        {selectedTruck && (
          <TruckDetailPanel
            truck={selectedTruck}
            onClose={() => setSelectedTruck(null)}
          />
        )}
      </div>
    </div>
  );
};

export default TrackingPage;
