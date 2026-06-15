import api from './api';

const tripService = {
  getAll: async (params = {}) => {
    const response = await api.get('/trips', { params });
    return response.data;
  },

  getById: async (id) => {
    const response = await api.get(`/trips/${id}`);
    return response.data;
  },

  getByDriver: async (driverId) => {
    const response = await api.get(`/trips/driver/${driverId}`);
    return response.data;
  },

  create: async (data) => {
    const response = await api.post('/trips', data);
    return response.data;
  },

  update: async (id, data) => {
    const response = await api.put(`/trips/${id}`, data);
    return response.data;
  },

  updateStatus: async (id, status) => {
    const response = await api.put(`/trips/${id}/status`, { status });
    return response.data;
  },

  updateLocation: async (id, location) => {
    const response = await api.post(`/trips/${id}/location`, location);
    return response.data;
  },

  complete: async (id, completionData) => {
    const response = await api.post(`/trips/${id}/complete`, completionData);
    return response.data;
  },

  delete: async (id) => {
    const response = await api.delete(`/trips/${id}`);
    return response.data;
  },

  getActiveTrips: async () => {
    const response = await api.get('/trips/active');
    return response.data;
  }
};

export default tripService;
