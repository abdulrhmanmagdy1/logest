import api from "./api";

const shipmentService = {
  // جلب كل الشحنات
  getAll: async (params = {}) => {
    const query = new URLSearchParams(params).toString();
    const res = await api.get(`/shipments?${query}`);
    return res.data.data;
  },

  // جلب شحنة محددة
  getById: async (id) => {
    const res = await api.get(`/shipments/${id}`);
    return res.data.data.shipment;
  },

  // تتبع برقم التتبع
  track: async (trackingNumber) => {
    const res = await api.get(`/shipments/track/${trackingNumber}`);
    return res.data.data.shipment;
  },

  // إنشاء شحنة
  create: async (data) => {
    const res = await api.post("/shipments", data);
    return res.data.data.shipment;
  },

  // تحديث شحنة
  update: async (id, data) => {
    const res = await api.put(`/shipments/${id}`, data);
    return res.data.data.shipment;
  },

  // تحديث الحالة
  updateStatus: async (id, status, note = "") => {
    const res = await api.patch(`/shipments/${id}/status`, { status, note });
    return res.data.data.shipment;
  },

  // إسناد سائق وشاحنة
  assign: async (id, driverId, truckId) => {
    const res = await api.put(`/shipments/${id}/assign`, {
      driverId,
      truckId,
    });
    return res.data.data.shipment;
  },

  // رفع مرفقات
  uploadAttachments: async (id, files, type, description = "") => {
    const formData = new FormData();
    files.forEach((file) => formData.append("files", file));
    formData.append("type", type);
    if (description) formData.append("description", description);

    const res = await api.post(`/shipments/${id}/attachments`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return res.data.data.attachments;
  },

  // حذف شحنة
  delete: async (id) => {
    const res = await api.delete(`/shipments/${id}`);
    return res.data;
  },

  // إحصائيات
  getStats: async () => {
    const res = await api.get("/analytics/dashboard");
    return res.data.data.shipments;
  },
};

export default shipmentService;
