import api from './api';

const invoiceService = {
  getAll: async (params = {}) => {
    const query = new URLSearchParams(params).toString();
    const res = await api.get(`/invoices?${query}`);
    return res.data.data;
  },

  getById: async (id) => {
    const res = await api.get(`/invoices/${id}`);
    return res.data.data.invoice;
  },

  verify: async (invoiceNumber) => {
    const res = await api.get(`/invoices/verify/${invoiceNumber}`);
    return res.data.data.invoice;
  },

  create: async (data) => {
    const res = await api.post("/invoices", data);
    return res.data.data.invoice;
  },

  update: async (id, data) => {
    const res = await api.put(`/invoices/${id}`, data);
    return res.data.data.invoice;
  },

  addPayment: async (id, paymentData) => {
    const res = await api.post(`/invoices/${id}/payment`, paymentData);
    return res.data.data;
  },

  cancel: async (id) => {
    const res = await api.delete(`/invoices/${id}`);
    return res.data;
  },

  createStripeIntent: async (invoiceId) => {
    const res = await api.post("/payments/stripe/intent", { invoiceId });
    return res.data.data;
  },

  confirmStripePayment: async (paymentIntentId, invoiceId) => {
    const res = await api.post("/payments/stripe/confirm", {
      paymentIntentId,
      invoiceId,
    });
    return res.data.data;
  },
};

export default invoiceService;
