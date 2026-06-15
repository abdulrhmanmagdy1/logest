/**
 * ============================================
 * 📊 Dashboard Redirect - نظام إدهام
 * Edham Logistics - Role-based Redirect
 * ============================================
 */

import React from "react";
import { useAuth } from "../context/AuthContext";
import { Navigate } from "react-router-dom";

// Redirect لوحة التحكم حسب الدور
const Dashboard = () => {
  const { user } = useAuth();

  const routes = {
    client:      "/client",
    supervisor:  "/supervisor",
    accountant:  "/accountant",
    driver:      "/driver",
    employee:    "/employee",
    maintenance: "/maintenance",
    admin:       "/supervisor",
  };

  return <Navigate to={routes[user?.role] || "/login"} replace />;
};

export default Dashboard;

