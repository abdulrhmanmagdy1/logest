/**
 * ============================================
 * 🔐 Auth Context - نظام إدهام
 * Edham Logistics - Authentication Context
 * ============================================
 */

import React, { createContext, useContext, useState, useEffect } from 'react';
import authService from '../services/authService';

// ── Auth Context ───────────────────────────
const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      fetchUser();
    } else {
      setLoading(false);
    }
  }, []);

  const fetchUser = async () => {
    try {
      const data = await authService.getMe();
      setUser(data);
    } catch (error) {
      localStorage.removeItem("token");
    } finally {
      setLoading(false);
    }
  };

  const login = async (email, password) => {
    setIsLoading(true);
    try {
      const { token, user: userData } = await authService.login(email, password);
      
      localStorage.setItem("token", token);
      setUser(userData);
      
      return { success: true, user: userData };
    } catch (error) {
      return {
        success: false,
        message: error.response?.data?.message || "فشل تسجيل الدخول",
      };
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem("token");
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        isLoading,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
