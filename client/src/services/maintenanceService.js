import api from './api';

const maintenanceService = {
  getAll: async (params = {}) => {
    const response = await api.get('/maintenance', { params });
    return response.data;
  },

  getById: async (id) => {
    const response = await api.get(`/maintenance/${id}`);
    return response.data;
  },

  create: async (data) => {
    const response = await api.post('/maintenance', data);
    return response.data;
  },

  update: async (id, data) => {
    const response = await api.put(`/maintenance/${id}`, data);
    return response.data;
  },

  delete: async (id) => {
    const response = await api.delete(`/maintenance/${id}`);
    return response.data;
  },

  getOilSchedule: async (truckId) => {
    const response = await api.get(`/maintenance/oil-schedule/${truckId}`);
    return response.data;
  },

  updateOilSchedule: async (truckId, data) => {
    const response = await api.put(`/maintenance/oil-schedule/${truckId}`, data);
    return response.data;
  },

  getSpareParts: async () => {
    const response = await api.get('/maintenance/spare-parts');
    return response.data;
  },

  addSparePart: async (data) => {
    const response = await api.post('/maintenance/spare-parts', data);
    return response.data;
  }
};

export default maintenanceService;
