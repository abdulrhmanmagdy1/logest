import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  Wrench, AlertTriangle, CheckCircle, Clock,
  Plus, Truck, LogOut, Package, Droplets,
} from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { useNotification } from "../context/NotificationContext";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

const MaintenanceDashboard = () => {
  const { user, logout } = useAuth();
  const { showToast } = useNotification();
  const navigate = useNavigate();

  const [records, setRecords] = useState([]);
  const [oilSchedules, setOilSchedules] = useState([]);
  const [spareParts, setSpareParts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState("maintenance");
  const [showNewMaintenance, setShowNewMaintenance] = useState(false);
  const [trucks, setTrucks] = useState([]);

  const [newRecord, setNewRecord] = useState({
    truck: "",
    type: "preventive",
    priority: "medium",
    description: "",
    scheduledDate: "",
  });

  useEffect(() => {
    fetchData();
  }, [activeTab]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [maintenanceRes, trucksRes] = await Promise.all([
        api.get("/maintenance?limit=20"),
        api.get("/trucks?status=active"),
      ]);
      setRecords(maintenanceRes.data.data.records);
      setTrucks(trucksRes.data.data.trucks);

      if (activeTab === "oil") {
        const oilRes = await api.get("/oil-schedule?limit=20");
        setOilSchedules(oilRes.data.data.schedules);
      }

      if (activeTab === "parts") {
        const partsRes = await api.get("/spare-parts?limit=30");
        setSpareParts(partsRes.data.data.parts);
      }
    } catch {
      showToast("خطأ", "فشل في جلب البيانات", "error");
    } finally {
      setLoading(false);
    }
  };

  const handleCreateMaintenance = async (e) => {
    e.preventDefault();
    try {
      await api.post("/maintenance", newRecord);
      showToast("✅ تم", "تم تسجيل طلب الصيانة", "success");
      setShowNewMaintenance(false);
      setNewRecord({ truck: "", type: "preventive", priority: "medium", description: "", scheduledDate: "" });
      fetchData();
    } catch (err) {
      showToast("خطأ", err.response?.data?.message || "فشل التسجيل", "error");
    }
  };

  const handleUpdateStatus = async (id, status) => {
    try {
      await api.patch(`/maintenance/${id}/status`, { status });
      showToast("✅ تم", "تم تحديث الحالة", "success");
      fetchData();
    } catch {
      showToast("خطأ", "فشل التحديث", "error");
    }
  };

  const priorityConfig = {
    low:      { label: "منخفضة",  class: "badge-gray" },
    medium:   { label: "متوسطة", class: "badge-info" },
    high:     { label: "عالية",  class: "badge-warning" },
    critical: { label: "حرجة",  class: "badge-danger" },
  };

  const statusConfig = {
    reported:    { label: "مُبلَّغ",    icon: AlertTriangle, color: "text-yellow-400" },
    scheduled:   { label: "مجدول",    icon: Clock,          color: "text-blue-400" },
    in_progress: { label: "جارٍ",     icon: Wrench,         color: "text-orange-400" },
    completed:   { label: "مكتمل",   icon: CheckCircle,    color: "text-green-400" },
    cancelled:   { label: "ملغي",    icon: AlertTriangle,  color: "text-gray-400" },
  };

  const tabs = [
    { id: "maintenance", label: "الصيانة",    icon: Wrench },
    { id: "oil",         label: "تغيير الزيت", icon: Droplets },
    { id: "parts",       label: "قطع الغيار", icon: Package },
  ];

  return (
    <div className="min-h-screen bg-gray-900">
      <header className="navbar">
        <div className="flex items-center gap-3">
          <div className="w-8 h-8 bg-red-600 rounded-lg flex items-center justify-center">
            <Wrench className="w-4 h-4 text-white" />
          </div>
          <div>
            <span className="font-black text-gradient text-sm">إدهام</span>
            <span className="text-gray-500 text-xs mr-2">| صيانة</span>
          </div>
        </div>
        <div className="flex items-center gap-4">
          <p className="text-sm text-white hidden sm:block">{user?.name}</p>
          <button
            onClick={() => { logout(); navigate("/login"); }}
            className="btn-icon text-gray-400 hover:text-red-400"
          >
            <LogOut className="w-5 h-5" />
          </button>
        </div>
      </header>

      <main className="pt-16 p-4 md:p-6 max-w-5xl mx-auto space-y-5">

        {/* Tabs */}
        <div className="flex gap-2 bg-gray-800 p-1 rounded-xl">
          {tabs.map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`flex-1 flex items-center justify-center gap-2 py-2.5 px-3
                          rounded-lg text-sm font-medium transition-all duration-200 ${
                activeTab === tab.id
                  ? "bg-red-600 text-white shadow"
                  : "text-gray-400 hover:text-white"
              }`}
            >
              <tab.icon className="w-4 h-4" />
              <span className="hidden sm:inline">{tab.label}</span>
            </button>
          ))}
        </div>

        {/* Maintenance Tab */}
        {activeTab === "maintenance" && (
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <h3 className="text-lg font-semibold text-white">سجلات الصيانة</h3>
              <button
                onClick={() => setShowNewMaintenance(true)}
                className="btn-primary btn-sm"
              >
                <Plus className="w-4 h-4" />
                طلب صيانة
              </button>
            </div>

            {loading ? (
              <div className="space-y-3">
                {[...Array(4)].map((_, i) => (
                  <div key={i} className="skeleton h-20 rounded-xl" />
                ))}
              </div>
            ) : records.length === 0 ? (
              <div className="empty-state card">
                <Wrench className="empty-state-icon" />
                <p className="empty-state-title">لا توجد سجلات صيانة</p>
              </div>
            ) : (
              <div className="space-y-3">
                {records.map((record) => {
                  const status = statusConfig[record.status];
                  const StatusIcon = status?.icon || Wrench;
                  return (
                    <motion.div
                      key={record._id}
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      className="card"
                    >
                      <div className="flex items-start justify-between gap-3">
                        <div className="flex items-start gap-3 flex-1 min-w-0">
                          <div className={`w-10 h-10 rounded-xl flex items-center
                                          justify-center flex-shrink-0 bg-gray-900
                                          ${status?.color}`}>
                            <StatusIcon className="w-5 h-5" />
                          </div>
                          <div className="flex-1 min-w-0">
                            <div className="flex items-center gap-2 flex-wrap mb-1">
                              <span className="text-white font-medium text-sm">
                                {record.truck?.plateNumber || "—"}
                              </span>
                              <span className={priorityConfig[record.priority]?.class || "badge-gray"}>
                                {priorityConfig[record.priority]?.label}
                              </span>
                            </div>
                            <p className="text-gray-400 text-xs truncate">{record.description}</p>
                            <p className="text-gray-600 text-xs mt-1">
                              {record.type} | {new Date(record.createdAt).toLocaleDateString("ar-EG")}
                            </p>
                          </div>
                        </div>

                        {/* أزرار التحديث */}
                        <div className="flex flex-col gap-1 flex-shrink-0">
                          {record.status === "reported" && (
                            <button
                              onClick={() => handleUpdateStatus(record._id, "in_progress")}
                              className="btn-primary btn-sm text-xs"
                            >
                              ابدأ
                            </button>
                          )}
                          {record.status === "in_progress" && (
                            <button
                              onClick={() => handleUpdateStatus(record._id, "completed")}
                              className="btn-success btn-sm text-xs"
                            >
                              أنهِ
                            </button>
                          )}
                        </div>
                      </div>
                    </motion.div>
                  );
                })}
              </div>
            )}
          </div>
        )}

        {/* Oil Schedule Tab */}
        {activeTab === "oil" && (
          <div className="space-y-4">
            <h3 className="text-lg font-semibold text-white">جدول تغيير الزيت</h3>
            {loading ? (
              <div className="space-y-3">
                {[...Array(4)].map((_, i) => (
                  <div key={i} className="skeleton h-16 rounded-xl" />
                ))}
              </div>
            ) : oilSchedules.length === 0 ? (
              <div className="empty-state card">
                <Droplets className="empty-state-icon" />
                <p className="empty-state-title">لا توجد جدولة</p>
              </div>
            ) : (
              <div className="space-y-3">
                {oilSchedules.map((schedule) => (
                  <div key={schedule._id} className="card">
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="text-white font-medium">
                          {schedule.truck?.plateNumber} - {schedule.truck?.brand}
                        </p>
                        <p className="text-gray-400 text-xs mt-1">
                          آخر تغيير: {new Date(schedule.lastChangeDate).toLocaleDateString("ar-EG")}
                          {" "}| الكم: {schedule.lastChangeKm?.toLocaleString()} كم
                        </p>
                        <p className="text-gray-400 text-xs">
                          التغيير القادم: {new Date(schedule.nextChangeDate).toLocaleDateString("ar-EG")}
                          {" "}| عند: {schedule.nextChangeKm?.toLocaleString()} كم
                        </p>
                      </div>
                      <span className={`badge ${
                        schedule.status === "overdue"  ? "badge-danger" :
                        schedule.status === "due_soon" ? "badge-warning" : "badge-success"
                      }`}>
                        {schedule.status === "overdue"  ? "متأخر!" :
                         schedule.status === "due_soon" ? "قريباً" : "منتظم"}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* Spare Parts Tab */}
        {activeTab === "parts" && (
          <div className="space-y-4">
            <h3 className="text-lg font-semibold text-white">قطع الغيار</h3>
            {loading ? (
              <div className="grid grid-cols-2 gap-3">
                {[...Array(6)].map((_, i) => (
                  <div key={i} className="skeleton h-24 rounded-xl" />
                ))}
              </div>
            ) : spareParts.length === 0 ? (
              <div className="empty-state card">
                <Package className="empty-state-icon" />
                <p className="empty-state-title">لا توجد قطع غيار</p>
              </div>
            ) : (
              <div className="grid grid-cols-2 md:grid-cols-3 gap-3">
                {spareParts.map((part) => (
                  <motion.div
                    key={part._id}
                    initial={{ opacity: 0, scale: 0.95 }}
                    animate={{ opacity: 1, scale: 1 }}
                    className={`card ${part.quantity <= part.minQuantity
                      ? "border-yellow-700/50"
                      : "border-gray-700"
                    }`}
                  >
                    <div className="flex items-start justify-between mb-2">
                      <span className="text-xs text-gray-500 bg-gray-900
                                       px-2 py-0.5 rounded-full">
                        {part.category}
                      </span>
                      {part.quantity <= part.minQuantity && (
                        <AlertTriangle className="w-4 h-4 text-yellow-400 flex-shrink-0" />
                      )}
                    </div>
                    <p className="text-white font-medium text-sm truncate">{part.name}</p>
                    {part.brand && (
                      <p className="text-gray-500 text-xs mt-0.5">{part.brand}</p>
                    )}
                    <div className="flex items-center justify-between mt-2">
                      <span className="text-gray-400 text-xs">
                        المتوفر: {part.quantity}
                      </span>
                      <span className="text-red-400 text-xs font-medium">
                        {part.price?.toLocaleString()} ج.م
                      </span>
                    </div>
                  </motion.div>
                ))}
              </div>
            )}
          </div>
        )}
      </main>

      {/* Modal: طلب صيانة جديد */}
      {showNewMaintenance && (
        <div className="modal-overlay">
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            className="modal-content w-full max-w-md"
          >
            <div className="modal-header">
              <div>
                <h3 className="text-lg font-bold text-white">طلب صيانة جديد</h3>
                <p className="text-xs text-gray-500 mt-0.5">تسجيل طلب صيانة للشاحنة</p>
              </div>
              <button
                onClick={() => setShowNewMaintenance(false)}
                className="btn-icon text-gray-400 hover:text-white"
              >
                ✕
              </button>
            </div>

            <form onSubmit={handleCreateMaintenance} className="modal-body space-y-4">
              <div className="input-group">
                <label className="input-label">الشاحنة</label>
                <select
                  value={newRecord.truck}
                  onChange={(e) => setNewRecord({ ...newRecord, truck: e.target.value })}
                  className="input-field"
                  required
                >
                  <option value="">اختر الشاحنة</option>
                  {trucks.map((truck) => (
                    <option key={truck._id} value={truck._id}>
                      {truck.plateNumber} - {truck.brand}
                    </option>
                  ))}
                </select>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div className="input-group">
                  <label className="input-label">النوع</label>
                  <select
                    value={newRecord.type}
                    onChange={(e) => setNewRecord({ ...newRecord, type: e.target.value })}
                    className="input-field"
                  >
                    <option value="preventive">وقائي</option>
                    <option value="corrective">تصحيحي</option>
                    <option value="emergency">طوارئ</option>
                  </select>
                </div>
                <div className="input-group">
                  <label className="input-label">الأولوية</label>
                  <select
                    value={newRecord.priority}
                    onChange={(e) => setNewRecord({ ...newRecord, priority: e.target.value })}
                    className="input-field"
                  >
                    <option value="low">منخفضة</option>
                    <option value="medium">متوسطة</option>
                    <option value="high">عالية</option>
                    <option value="critical">حرجة</option>
                  </select>
                </div>
              </div>

              <div className="input-group">
                <label className="input-label">الوصف</label>
                <textarea
                  value={newRecord.description}
                  onChange={(e) => setNewRecord({ ...newRecord, description: e.target.value })}
                  placeholder="وصف المشكلة أو الصيانة المطلوبة..."
                  className="input-field resize-none"
                  rows={3}
                  required
                />
              </div>

              <div className="input-group">
                <label className="input-label">التاريخ المجدول</label>
                <input
                  type="date"
                  value={newRecord.scheduledDate}
                  onChange={(e) => setNewRecord({ ...newRecord, scheduledDate: e.target.value })}
                  className="input-field"
                />
              </div>
            </form>

            <div className="modal-footer">
              <button
                onClick={() => setShowNewMaintenance(false)}
                className="btn-secondary"
              >
                إلغاء
              </button>
              <button onClick={handleCreateMaintenance} className="btn-primary">
                <Wrench className="w-4 h-4" />
                تسجيل الطلب
              </button>
            </div>
          </motion.div>
        </div>
      )}
    </div>
  );
};

export default MaintenanceDashboard;
