/**
 * ============================================
 * 🔐 AuthContext - نظام إدهام
 * Edham Logistics - Authentication Context
 * ============================================
 */

import React, { createContext, useContext, useState, useEffect } from 'react';
import api from '../services/api';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    checkAuth();
  }, []);

  const checkAuth = async () => {
    try {
      const token = localStorage.getItem('token');
      if (token) {
        api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        const response = await api.get('/auth/me');
        setUser(response.data.data);
      }
    } catch (err) {
      localStorage.removeItem('token');
      delete api.defaults.headers.common['Authorization'];
    } finally {
      setLoading(false);
    }
  };

  const login = async (email, password) => {
    try {
      setError(null);
      const response = await api.post('/auth/login', { email, password });
      const { token, user: userData } = response.data.data;
      
      localStorage.setItem('token', token);
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      setUser(userData);
      
      return userData;
    } catch (err) {
      setError(err.response?.data?.message || 'خطأ في تسجيل الدخول');
      throw err;
    }
  };

  const register = async (userData) => {
    try {
      setError(null);
      const response = await api.post('/auth/register', userData);
      const { token, user: newUser } = response.data.data;
      
      localStorage.setItem('token', token);
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      setUser(newUser);
      
      return newUser;
    } catch (err) {
      setError(err.response?.data?.message || 'خطأ في التسجيل');
      throw err;
    }
  };

  const logout = async () => {
    try {
      await api.post('/auth/logout');
    } catch (err) {
      console.error('Logout error:', err);
    } finally {
      localStorage.removeItem('token');
      delete api.defaults.headers.common['Authorization'];
      setUser(null);
    }
  };

  const updateProfile = async (profileData) => {
    try {
      setError(null);
      const response = await api.put('/auth/profile', profileData);
      setUser(response.data.data);
      return response.data.data;
    } catch (err) {
      setError(err.response?.data?.message || 'خطأ في تحديث الملف الشخصي');
      throw err;
    }
  };

  const value = {
    user,
    loading,
    error,
    login,
    register,
    logout,
    updateProfile,
    isAuthenticated: !!user
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};

export default AuthContext;
