import axios from 'axios';

// NEXT_PUBLIC_API_URL  — set this in Vercel project settings to your Render URL
//   e.g. https://edham-backend.onrender.com/api/v1
// Falls back to NEXT_PUBLIC_API_BASE_URL (legacy name), then localhost for dev.
const api = axios.create({
  baseURL:
    process.env.NEXT_PUBLIC_API_URL ??
    process.env.NEXT_PUBLIC_API_BASE_URL ??
    'http://localhost:5000/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

api.interceptors.request.use((config) => {
  if (typeof window !== 'undefined') {
    const token = localStorage.getItem('edham_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  }
  return config;
});

export const authApi = {
  login: (payload: { email: string; password: string }) => api.post('/auth/login', payload),
  register: (payload: {
    firstName: string;
    lastName: string;
    email: string;
    phone: string;
    password: string;
    companyName?: string;
  }) => api.post('/auth/register', payload),
  refresh: () => api.post('/auth/refresh'),
};

export const dashboardApi = {
  stats: () => api.get('/dashboard/stats'),
};

export const shipmentsApi = {
  list: (params?: Record<string, string>) => api.get('/shipments', { params }),
  create: (data: Record<string, unknown>) => api.post('/shipments', data),
};

export const trucksApi = {
  list: (params?: Record<string, string>) => api.get('/trucks', { params }),
  create: (data: Record<string, unknown>) => api.post('/trucks', data),
};

export const usersApi = {
  list:   (params?: Record<string, string>) => api.get('/users', { params }),
  create: (data: Record<string, unknown>) => api.post('/users', data),
  update: (id: string, data: Record<string, unknown>) => api.put(`/users/${id}`, data),
  me:     () => api.get('/auth/me'),
};

export default api;
