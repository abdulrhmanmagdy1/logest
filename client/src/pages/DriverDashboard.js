import React, { useState, useEffect, useRef } from "react";
import { motion } from "framer-motion";
import {
  Truck, MapPin, Navigation, Thermometer, Clock,
  CheckCircle, AlertTriangle, Play, Square,
  Star, LogOut, Package, Phone, User,
} from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { useNotification } from "../context/NotificationContext";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import logger from "../utils/logger";

const DriverDashboard = () => {
  const { user, logout } = useAuth();
  const { socket, showToast } = useNotification();
  const navigate = useNavigate();

  const [activeTrip, setActiveTrip] = useState(null);
  const [trips, setTrips] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isTracking, setIsTracking] = useState(false);
  const [currentLocation, setCurrentLocation] = useState(null);
  const [showSurvey, setShowSurvey] = useState(false);
  const [completedTripId, setCompletedTripId] = useState(null);

  const watchIdRef = useRef(null);

  // ── Survey State ─────────────────────────
  const [survey, setSurvey] = useState({
    vehicleCondition: 5,
    routeEfficiency: 5,
    customerService: 5,
    timeManagement: 5,
    comments: "",
  });

  useEffect(() => {
    fetchTrips();
    return () => {
      if (watchIdRef.current) navigator.geolocation.clearWatch(watchIdRef.current);
    };
  }, []);

  const fetchTrips = async () => {
    try {
      const res = await api.get("/trips?limit=10");
      const allTrips = res.data.data.trips;
      setTrips(allTrips);

      const active = allTrips.find((t) =>
        ["started", "in_progress"].includes(t.status)
      );
      if (active) {
        setActiveTrip(active);
        setIsTracking(true);
        startGPSTracking(active);
      }
    } catch (err) {
      showToast("خطأ", "فشل في جلب الرحلات", "error");
    } finally {
      setLoading(false);
    }
  };

  // ── بدء تتبع GPS ──────────────────────────
  const startGPSTracking = (trip) => {
    if (!navigator.geolocation) {
      showToast("خطأ", "جهازك لا يدعم GPS", "error");
      return;
    }

    watchIdRef.current = navigator.geolocation.watchPosition(
      (position) => {
        const { latitude: lat, longitude: lng, speed } = position.coords;
        setCurrentLocation({ lat, lng });

        // إرسال عبر Socket.io
        if (socket?.connected) {
          socket.emit("driverLocation", {
            lat,
            lng,
            speed: speed ? speed * 3.6 : 0, // م/ث → كم/ساعة
            shipmentId: trip.shipment?._id || trip.shipment,
            tripId: trip._id,
            timestamp: new Date().toISOString(),
          });
        }
      },
      (err) => {
        logger.error("GPS Error:", err.message);
        showToast("تحذير", "فشل في الحصول على الموقع", "warning");
      },
      {
        enableHighAccuracy: true,
        maximumAge: 5000,
        timeout: 10000,
      }
    );
  };

  // ── بدء الرحلة ───────────────────────────
  const handleStartTrip = async (tripId) => {
    try {
      const startKm = prompt("أدخل قراءة العداد الحالية (كم):");
      if (!startKm) return;

      await api.patch(`/trips/${tripId}/start`, { startKm: parseInt(startKm) });

      const trip = trips.find((t) => t._id === tripId);
      setActiveTrip(trip);
      setIsTracking(true);
      startGPSTracking(trip);

      showToast("✅ بدأت الرحلة", `رحلة ${trip.tripNumber} جارية`, "success");
      fetchTrips();
    } catch (err) {
      showToast("خطأ", err.response?.data?.message || "فشل البدء", "error");
    }
  };

  // ── إنهاء الرحلة ─────────────────────────
  const handleCompleteTrip = async () => {
    if (!activeTrip) return;

    const endKm = prompt("أدخل قراءة العداد النهائية (كم):");
    if (!endKm) return;

    try {
      await api.patch(`/trips/${activeTrip._id}/complete`, {
        endKm: parseInt(endKm),
        driverNotes: "",
      });

      if (watchIdRef.current) {
        navigator.geolocation.clearWatch(watchIdRef.current);
        watchIdRef.current = null;
      }

      setCompletedTripId(activeTrip._id);
      setIsTracking(false);
      setActiveTrip(null);
      setShowSurvey(true);
      showToast("✅ اكتملت الرحلة", "أحسنت! يرجى تعبئة الاستبيان", "success");
      fetchTrips();
    } catch (err) {
      showToast("خطأ", err.response?.data?.message || "فشل الإنهاء", "error");
    }
  };

  // ── إرسال الاستبيان ───────────────────────
  const handleSubmitSurvey = async () => {
    try {
      await api.post("/surveys", {
        trip: completedTripId,
        ratings: {
          vehicleCondition: survey.vehicleCondition,
          routeEfficiency: survey.routeEfficiency,
          customerService: survey.customerService,
          timeManagement: survey.timeManagement,
        },
        comments: survey.comments,
      });
      showToast("شكراً!", "تم إرسال تقييمك بنجاح", "success");
      setShowSurvey(false);
      setSurvey({ vehicleCondition: 5, routeEfficiency: 5, customerService: 5, timeManagement: 5, comments: "" });
    } catch (err) {
      showToast("خطأ", "فشل إرسال الاستبيان", "error");
    }
  };

  // ── Star Rating Component ─────────────────
  const StarRating = ({ label, value, onChange }) => (
    <div className="flex items-center justify-between py-3 border-b border-gray-700 last:border-0">
      <span className="text-sm text-gray-300">{label}</span>
      <div className="flex gap-1">
        {[1, 2, 3, 4, 5].map((star) => (
          <button
            key={star}
            type="button"
            onClick={() => onChange(star)}
            className="transition-transform hover:scale-110"
          >
            <Star
              className={`w-6 h-6 transition-colors ${
                star <= value ? "text-yellow-400 fill-yellow-400" : "text-gray-600"
              }`}
            />
          </button>
        ))}
      </div>
    </div>
  );

  const tripStatusInfo = {
    scheduled:   { label: "مجدولة",    color: "text-blue-400",   bg: "bg-blue-900/30" },
    started:     { label: "جارية",     color: "text-green-400",  bg: "bg-green-900/30" },
    in_progress: { label: "قيد التنفيذ", color: "text-orange-400", bg: "bg-orange-900/30" },
    completed:   { label: "مكتملة",   color: "text-gray-400",   bg: "bg-gray-800" },
    cancelled:   { label: "ملغية",    color: "text-red-400",    bg: "bg-red-900/30" },
  };

  return (
    <div className="min-h-screen bg-gray-900">
      {/* Navbar */}
      <header className="navbar">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 bg-red-600 rounded-lg flex items-center justify-center">
            <Truck className="w-4 h-4 text-white" />
          </div>
          <div>
            <span className="font-black text-gradient text-sm">إدهام</span>
            <span className="text-gray-500 text-xs mr-2">| سائق</span>
          </div>
        </div>

        <div className="flex items-center gap-3">
          {isTracking && (
            <div className="flex items-center gap-2 bg-green-900/30 border border-green-700
                            px-3 py-1.5 rounded-full">
              <div className="w-2 h-2 bg-green-400 rounded-full animate-pulse" />
              <span className="text-green-400 text-xs font-medium">تتبع مباشر</span>
            </div>
          )}
          <div className="text-right hidden sm:block">
            <p className="text-sm font-medium text-white">{user?.name}</p>
          </div>
          <button
            onClick={() => { logout(); navigate("/login"); }}
            className="btn-icon text-gray-400 hover:text-red-400"
          >
            <LogOut className="w-5 h-5" />
          </button>
        </div>
      </header>

      <main className="pt-16 p-4 max-w-2xl mx-auto space-y-4">

        {/* الرحلة النشطة */}
        {activeTrip && (
          <motion.div
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            className="card border-2 border-green-600/50 glow-red"
          >
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center gap-2">
                <div className="w-3 h-3 bg-green-400 rounded-full animate-pulse" />
                <span className="text-green-400 font-semibold">رحلة نشطة</span>
              </div>
              <span className="font-mono text-xs text-gray-400">{activeTrip.tripNumber}</span>
            </div>

            {/* بيانات الرحلة */}
            <div className="grid grid-cols-2 gap-3 mb-4">
              <div className="p-3 bg-gray-900 rounded-xl">
                <p className="text-xs text-gray-500 mb-1">الشاحنة</p>
                <p className="text-white font-medium text-sm">
                  {activeTrip.truck?.plateNumber || "—"}
                </p>
              </div>
              <div className="p-3 bg-gray-900 rounded-xl">
                <p className="text-xs text-gray-500 mb-1">الشحنة</p>
                <p className="text-red-400 font-mono text-sm">
                  {activeTrip.shipment?.trackingNumber || "—"}
                </p>
              </div>
              <div className="p-3 bg-gray-900 rounded-xl">
                <p className="text-xs text-gray-500 mb-1">من</p>
                <p className="text-white text-sm truncate">
                  {activeTrip.shipment?.pickup?.city || activeTrip.shipment?.pickup?.address || "—"}
                </p>
              </div>
              <div className="p-3 bg-gray-900 rounded-xl">
                <p className="text-xs text-gray-500 mb-1">إلى</p>
                <p className="text-white text-sm truncate">
                  {activeTrip.shipment?.delivery?.city || activeTrip.shipment?.delivery?.address || "—"}
                </p>
              </div>
            </div>

            {/* الموقع الحالي */}
            {currentLocation && (
              <div className="flex items-center gap-2 p-3 bg-green-900/20 border
                              border-green-700/50 rounded-xl mb-4">
                <Navigation className="w-4 h-4 text-green-400 flex-shrink-0" />
                <span className="text-green-300 text-xs font-mono">
                  {currentLocation.lat.toFixed(6)}, {currentLocation.lng.toFixed(6)}
                </span>
                <span className="text-green-500 text-xs mr-auto">GPS نشط</span>
              </div>
            )}

            {/* إنهاء الرحلة */}
            <button
              onClick={handleCompleteTrip}
              className="w-full bg-green-700 hover:bg-green-600 text-white
                         font-semibold py-3 rounded-xl transition-colors flex
                         items-center justify-center gap-2"
            >
              <CheckCircle className="w-5 h-5" />
              إنهاء الرحلة
            </button>
          </motion.div>
        )}

        {/* رحلات مجدولة */}
        {!loading && trips.filter((t) => t.status === "scheduled").length > 0 && (
          <div className="card">
            <h3 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
              <Clock className="w-5 h-5 text-blue-400" />
              رحلات مجدولة
            </h3>
            <div className="space-y-3">
              {trips
                .filter((t) => t.status === "scheduled")
                .map((trip) => (
                  <div
                    key={trip._id}
                    className="p-4 bg-gray-900 rounded-xl border border-gray-800"
                  >
                    <div className="flex items-start justify-between gap-3">
                      <div className="flex-1 min-w-0">
                        <span className="font-mono text-xs text-gray-400">
                          {trip.tripNumber}
                        </span>
                        <p className="text-white text-sm font-medium mt-1">
                          {trip.shipment?.trackingNumber || "—"}
                        </p>
                        <p className="text-gray-500 text-xs mt-1 truncate">
                          {trip.truck?.plateNumber} | {trip.truck?.brand}
                        </p>
                        {trip.scheduledStart && (
                          <p className="text-blue-400 text-xs mt-1">
                            موعد البدء:{" "}
                            {new Date(trip.scheduledStart).toLocaleString("ar-EG")}
                          </p>
                        )}
                      </div>
                      <button
                        onClick={() => handleStartTrip(trip._id)}
                        className="btn-success btn-sm flex-shrink-0 flex items-center gap-1"
                      >
                        <Play className="w-4 h-4" />
                        ابدأ
                      </button>
                    </div>
                  </div>
                ))}
            </div>
          </div>
        )}

        {/* سجل الرحلات */}
        <div className="card">
          <h3 className="text-lg font-semibold text-white mb-4 flex items-center gap-2">
            <Package className="w-5 h-5 text-red-400" />
            سجل الرحلات
          </h3>

          {loading ? (
            <div className="space-y-2">
              {[...Array(3)].map((_, i) => (
                <div key={i} className="skeleton h-14 rounded-xl" />
              ))}
            </div>
          ) : trips.filter((t) => t.status === "completed").length === 0 ? (
            <div className="empty-state py-8">
              <Package className="w-10 h-10 text-gray-600 mb-2" />
              <p className="text-gray-500 text-sm">لا توجد رحلات مكتملة</p>
            </div>
          ) : (
            <div className="space-y-2">
              {trips
                .filter((t) => t.status === "completed")
                .slice(0, 5)
                .map((trip) => (
                  <div
                    key={trip._id}
                    className="flex items-center gap-3 p-3 bg-gray-900
                               rounded-xl border border-gray-800"
                  >
                    <CheckCircle className="w-5 h-5 text-green-400 flex-shrink-0" />
                    <div className="flex-1 min-w-0">
                      <p className="text-sm text-white font-medium">
                        {trip.tripNumber}
                      </p>
                      <p className="text-xs text-gray-500">
                        {trip.shipment?.trackingNumber || "—"}
                      </p>
                    </div>
                    <div className="text-left">
                      <p className="text-xs text-gray-500">
                        {trip.actualEnd
                          ? new Date(trip.actualEnd).toLocaleDateString("ar-EG")
                          : "—"}
                      </p>
                      {trip.isOnTime !== undefined && (
                        <span className={`text-xs ${trip.isOnTime ? "text-green-400" : "text-yellow-400"}`}>
                          {trip.isOnTime ? "في الوقت" : "متأخر"}
                        </span>
                      )}
                    </div>
                  </div>
                ))}
            </div>
          )}
        </div>
      </main>

      {/* Modal: الاستبيان */}
      {showSurvey && (
        <div className="modal-overlay">
          <motion.div
            initial={{ opacity: 0, scale: 0.9, y: 30 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            className="modal-content w-full max-w-md"
          >
            <div className="modal-header">
              <div>
                <h3 className="text-lg font-bold text-white">تقييم الرحلة</h3>
                <p className="text-xs text-gray-500 mt-0.5">رأيك يساعدنا على التحسين</p>
              </div>
              <Star className="w-6 h-6 text-yellow-400" />
            </div>

            <div className="modal-body">
              <StarRating
                label="حالة المركبة"
                value={survey.vehicleCondition}
                onChange={(v) => setSurvey({ ...survey, vehicleCondition: v })}
              />
              <StarRating
                label="كفاءة المسار"
                value={survey.routeEfficiency}
                onChange={(v) => setSurvey({ ...survey, routeEfficiency: v })}
              />
              <StarRating
                label="التعامل مع العميل"
                value={survey.customerService}
                onChange={(v) => setSurvey({ ...survey, customerService: v })}
              />
              <StarRating
                label="إدارة الوقت"
                value={survey.timeManagement}
                onChange={(v) => setSurvey({ ...survey, timeManagement: v })}
              />

              <div className="input-group mt-4">
                <label className="input-label">ملاحظات إضافية</label>
                <textarea
                  value={survey.comments}
                  onChange={(e) => setSurvey({ ...survey, comments: e.target.value })}
                  placeholder="أي ملاحظات أو مشاكل واجهتها..."
                  className="input-field resize-none"
                  rows={3}
                />
              </div>
            </div>

            <div className="modal-footer">
              <button
                onClick={() => setShowSurvey(false)}
                className="btn-ghost text-sm"
              >
                تخطي
              </button>
              <button onClick={handleSubmitSurvey} className="btn-primary">
                <Star className="w-4 h-4" />
                إرسال التقييم
              </button>
            </div>
          </motion.div>
        </div>
      )}
    </div>
  );
};

export default DriverDashboard;
