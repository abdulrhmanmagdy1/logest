/**
 * ============================================
 * 📍 Client Tracking Page - نظام إدهام
 * Edham Logistics - Shipment Tracking
 * ============================================
 */

import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  MapPin, Navigation, Package, Truck, Clock, Search,
  ChevronDown, RefreshCw, ArrowRight, CheckCircle, X
} from "lucide-react";
import api from "../../services/api";
import logger from "../../utils/logger";

const STATUS_STEPS = [
  { key: "pending", label: "تم الاستلام", icon: Package },
  { key: "confirmed", label: "تم التأكيد", icon: CheckCircle },
  { key: "assigned", label: "تم التسنيد", icon: Truck },
  { key: "picked_up", label: "تم الشحن", icon: Truck },
  { key: "in_transit", label: "في الطريق", icon: Navigation },
  { key: "delivered", label: "تم التسليم", icon: CheckCircle },
];

const ClientTrackingPage = () => {
  const [shipments, setShipments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState("");
  const [selectedShipment, setSelectedShipment] = useState(null);

  const fetchShipments = async () => {
    setLoading(true);
    try {
      const res = await api.get("/shipments/my-shipments?status=in_transit,assigned,picked_up");
      setShipments(res.data.data?.shipments || []);
    } catch (err) {
      logger.error("خطأ في جلب الشحنات:", err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchShipments();
    const interval = setInterval(fetchShipments, 30000);
    return () => clearInterval(interval);
  }, []);

  const filteredShipments = shipments.filter(s =>
    searchQuery === "" ||
    s.trackingNumber?.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const getStatusIndex = (status) => STATUS_STEPS.findIndex(s => s.key === status);

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-edham-white">تتبع الشحنات</h2>
          <p className="text-edham-text-muted text-sm mt-1">تتبع موقع شحناتك في الوقت الفعلي</p>
        </div>
        <button
          onClick={fetchShipments}
          className="btn-icon text-edham-text-muted hover:text-edham-white border border-edham-gray"
          disabled={loading}
        >
          <RefreshCw className={`w-4 h-4 ${loading ? "animate-spin" : ""}`} />
        </button>
      </div>

      {/* Search */}
      <div className="card">
        <div className="relative">
          <Search className="absolute right-3 top-1/2 -translate-y-1/2 w-4 h-4 text-edham-text-muted" />
          <input
            type="text"
            placeholder="ابحث برقم التتبع..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full bg-edham-black border border-edham-gray rounded-lg pr-10 pl-4 py-2.5 text-edham-white placeholder-edham-text-muted outline-none focus:border-edham-primary"
          />
        </div>
      </div>

      {/* Shipments */}
      <div className="space-y-4">
        {loading ? (
          [...Array(3)].map((_, i) => (
            <div key={i} className="card"><div className="skeleton h-40 rounded-xl" /></div>
          ))
        ) : filteredShipments.length === 0 ? (
          <div className="card py-16 text-center">
            <Navigation className="w-12 h-12 text-edham-text-muted mx-auto mb-3" />
            <p className="text-edham-text-muted">لا توجد شحنات قيد التنفيذ</p>
          </div>
        ) : (
          filteredShipments.map((shipment) => {
            const currentStep = getStatusIndex(shipment.status);
            return (
              <motion.div
                key={shipment._id}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                className="card"
              >
                <div className="flex items-center justify-between mb-4">
                  <div className="flex items-center gap-3">
                    <div className="w-12 h-12 bg-edham-primary/20 rounded-xl flex items-center justify-center">
                      <Package className="w-6 h-6 text-edham-primary" />
                    </div>
                    <div>
                      <h4 className="text-lg font-bold text-edham-white">{shipment.trackingNumber}</h4>
                      <p className="text-edham-text-muted text-sm">
                        {shipment.pickup?.city} <ArrowRight className="w-3 h-3 inline mx-1" /> {shipment.delivery?.city}
                      </p>
                    </div>
                  </div>
                  <button
                    onClick={() => setSelectedShipment(shipment)}
                    className="btn-sm"
                  >
                    عرض التفاصيل
                  </button>
                </div>

                {/* Progress Steps */}
                <div className="relative mt-6">
                  <div className="flex items-center justify-between">
                    {STATUS_STEPS.map((step, index) => {
                      const StepIcon = step.icon;
                      const isActive = index <= currentStep;
                      const isCurrent = index === currentStep;
                      return (
                        <div key={step.key} className="flex flex-col items-center relative">
                          <div className={`w-10 h-10 rounded-full flex items-center justify-center transition-colors ${
                            isActive
                              ? isCurrent
                                ? "bg-edham-primary text-white ring-4 ring-edham-primary/30"
                                : "bg-green-500/20 text-green-400"
                              : "bg-edham-gray/30 text-edham-text-muted"
                          }`}>
                            <StepIcon className="w-5 h-5" />
                          </div>
                          <span className={`text-xs mt-2 ${isActive ? "text-edham-white" : "text-edham-text-muted"}`}>
                            {step.label}
                          </span>
                          {index < STATUS_STEPS.length - 1 && (
                            <div className={`absolute top-5 left-full w-full h-0.5 -translate-y-1/2 ${
                              index < currentStep ? "bg-green-500" : "bg-edham-gray"
                            }`} style={{ width: "calc(100% - 2.5rem)" }} />
                          )}
                        </div>
                      );
                    })}
                  </div>
                </div>

                {/* Current Location */}
                {shipment.currentLocation && (
                  <div className="mt-4 p-3 bg-edham-black/50 rounded-xl">
                    <div className="flex items-center gap-2 text-edham-text-muted text-sm">
                      <MapPin className="w-4 h-4" />
                      <span>الموقع الحالي: {shipment.currentLocation.address || "—"}</span>
                    </div>
                    <p className="text-edham-text-muted text-xs mt-1">
                      آخر تحديث: {shipment.locationUpdatedAt
                        ? new Date(shipment.locationUpdatedAt).toLocaleString("ar-EG")
                        : "—"}
                    </p>
                  </div>
                )}
              </motion.div>
            );
          })
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
              <h3 className="text-lg font-bold text-edham-white">تفاصيل الشحنة</h3>
              <button onClick={() => setSelectedShipment(null)} className="btn-icon text-edham-text-muted hover:text-edham-white">
                <X className="w-5 h-5" />
              </button>
            </div>
            <div className="p-5 space-y-4">
              <p className="text-edham-text-muted text-sm">رقم التتبع</p>
              <p className="text-edham-white font-bold text-xl">{selectedShipment.trackingNumber}</p>
              {selectedShipment.assignedTruck && (
                <div className="bg-edham-primary/10 border border-edham-primary/30 rounded-xl p-3">
                  <p className="text-edham-primary text-xs mb-1">الشاحنة المسندة</p>
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

export default ClientTrackingPage;
