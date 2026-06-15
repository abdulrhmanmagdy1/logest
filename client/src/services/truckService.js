import api from './api';

const truckService = {
  getAll: async (params = {}) => {
    const query = new URLSearchParams(params).toString();
    const res = await api.get(`/trucks?${query}`);
    return res.data.data;
  },

  getById: async (id) => {
    const res = await api.get(`/trucks/${id}`);
    return res.data.data.truck;
  },

  create: async (data) => {
    const res = await api.post("/trucks", data);
    return res.data.data.truck;
  },

  update: async (id, data) => {
    const res = await api.put(`/trucks/${id}`, data);
    return res.data.data.truck;
  },

  updateStatus: async (id, status) => {
    const res = await api.patch(`/trucks/${id}/status`, { status });
    return res.data.data.truck;
  },

  updateKm: async (id, currentKm) => {
    const res = await api.patch(`/trucks/${id}/km`, { currentKm });
    return res.data.data;
  },

  uploadImages: async (id, files, caption = "") => {
    const formData = new FormData();
    files.forEach((file) => formData.append("images", file));
    if (caption) formData.append("caption", caption);

    const res = await api.post(`/trucks/${id}/images`, formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });
    return res.data.data.images;
  },

  delete: async (id) => {
    const res = await api.delete(`/trucks/${id}`);
    return res.data;
  },

  getActiveDrivers: async () => {
    const res = await api.get("/locations/active-drivers");
    return res.data.data.activeDrivers;
  },
};

export default truckService;
