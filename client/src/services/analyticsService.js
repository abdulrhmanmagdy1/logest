import api from './api';

const analyticsService = {
  getDashboard: async () => {
    const res = await api.get("/analytics/dashboard");
    return res.data.data;
  },

  getShipmentsChart: async (months = 6) => {
    const res = await api.get(`/analytics/shipments-chart?months=${months}`);
    return res.data.data.chart;
  },

  getRevenueChart: async (months = 6) => {
    const res = await api.get(`/analytics/revenue-chart?months=${months}`);
    return res.data.data.chart;
  },

  getDriverPerformance: async () => {
    const res = await api.get("/analytics/driver-performance");
    return res.data.data.drivers;
  },

  getFleetStatus: async () => {
    const res = await api.get("/analytics/fleet-status");
    return res.data.data;
  },
};

export default analyticsService;
