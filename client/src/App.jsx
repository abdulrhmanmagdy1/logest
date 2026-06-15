/**
 * ============================================
 * 🚛 App Component - نظام إدهام
 * Edham Logistics - Main Application
 * ============================================
 */

import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Layout from './components/Layout/Layout';
import LoginPage from './pages/LoginPage';
import AboutPage from './pages/AboutPage';
import ContactPage from './pages/ContactPage';
import ProfilePage from './pages/ProfilePage';
import SettingsPage from './pages/SettingsPage';
import NotificationsPage from './pages/NotificationsPage';
import AnalyticsPage from './pages/AnalyticsPage';
import MaintenancePage from './pages/MaintenancePage';
import TripPage from './pages/TripPage';
import DriverPage from './pages/DriverPage';
import PaymentPage from './pages/PaymentPage';
import LocationPage from './pages/LocationPage';
import ReportsPage from './pages/ReportsPage';
import SurveyPage from './pages/SurveyPage';
import OilSchedulePage from './pages/OilSchedulePage';
import SparePartsPage from './pages/SparePartsPage';
import AuditLogPage from './pages/AuditLogPage';
import DocumentsPage from './pages/DocumentsPage';
import ChatPage from './pages/ChatPage';
import CalendarPage from './pages/CalendarPage';
import AdminDashboard from './components/Dashboard/AdminDashboard';
import ClientDashboard from './components/Dashboard/ClientDashboard';
import SupervisorDashboard from './components/Dashboard/SupervisorDashboard';
import AccountantDashboard from './components/Dashboard/AccountantDashboard';
import DriverDashboard from './components/Dashboard/DriverDashboard';
import EmployeeDashboard from './components/Dashboard/EmployeeDashboard';
import MaintenanceDashboard from './components/Dashboard/MaintenanceDashboard';
import ShipmentForm from './components/Shipments/ShipmentForm';
import TruckForm from './components/forms/TruckForm';
import InvoiceForm from './components/forms/InvoiceForm';
import UserForm from './components/forms/UserForm';
import ShipmentList from './components/Lists/ShipmentList';
import TruckList from './components/Lists/TruckList';
import InvoiceList from './components/Lists/InvoiceList';
import UserList from './components/Lists/UserList';
import ShipmentDetail from './components/Details/ShipmentDetail';
import TruckDetail from './components/Details/TruckDetail';
import InvoiceDetail from './components/Details/InvoiceDetail';
import UserDetail from './components/Details/UserDetail';

// Protected Route Component
const ProtectedRoute = ({ children, allowedRoles }) => {
  const { user, loading, isAuthenticated } = useAuth();

  if (loading) {
    return (
      <div className="flex items-center justify-center h-screen bg-gray-900">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(user?.role)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return children;
};

// Dashboard Router based on user role
const DashboardRouter = () => {
  const { user } = useAuth();

  const dashboardMap = {
    admin: <AdminDashboard />,
    supervisor: <SupervisorDashboard />,
    accountant: <AccountantDashboard />,
    driver: <DriverDashboard />,
    employee: <EmployeeDashboard />,
    client: <ClientDashboard />,
    maintenance: <MaintenanceDashboard />,
  };

  return dashboardMap[user?.role] || <Navigate to="/login" replace />;
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="min-h-screen bg-gray-900">
          <Routes>
            {/* Public Routes */}
            <Route path="/login" element={<LoginPage />} />
            <Route path="/about" element={<AboutPage />} />
            <Route path="/contact" element={<ContactPage />} />
            
            {/* Protected Routes with Layout */}
            <Route
              path="/"
              element={
                <ProtectedRoute>
                  <Layout />
                </ProtectedRoute>
              }
            >
              {/* Dashboard */}
              <Route index element={<DashboardRouter />} />
              <Route path="dashboard" element={<DashboardRouter />} />
              
              {/* Pages */}
              <Route path="profile" element={<ProfilePage />} />
              <Route path="settings" element={<SettingsPage />} />
              <Route path="notifications" element={<NotificationsPage />} />
              <Route path="analytics" element={<AnalyticsPage />} />
              <Route
                path="maintenance"
                element={
                  <ProtectedRoute allowedRoles={['admin', 'supervisor', 'maintenance']}>
                    <MaintenancePage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="trips"
                element={
                  <ProtectedRoute allowedRoles={['admin', 'supervisor', 'driver']}>
                    <TripPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="drivers"
                element={
                  <ProtectedRoute allowedRoles={['admin', 'supervisor']}>
                    <DriverPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="payments"
                element={
                  <ProtectedRoute allowedRoles={['admin', 'accountant']}>
                    <PaymentPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="location"
                element={
                  <ProtectedRoute allowedRoles={['admin', 'supervisor', 'driver']}>
                    <LocationPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="reports"
                element={
                  <ProtectedRoute allowedRoles={['admin', 'accountant']}>
                    <ReportsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="survey"
                element={
                  <ProtectedRoute allowedRoles={['admin', 'client', 'driver']}>
                    <SurveyPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="oil-schedule"
                element={
                  <ProtectedRoute allowedRoles={['admin', 'supervisor', 'maintenance']}>
                    <OilSchedulePage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="spare-parts"
                element={
                  <ProtectedRoute allowedRoles={['admin', 'supervisor', 'maintenance']}>
                    <SparePartsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="audit-logs"
                element={
                  <ProtectedRoute allowedRoles={['admin']}>
                    <AuditLogPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="documents"
                element={
                  <ProtectedRoute allowedRoles={['admin', 'supervisor', 'accountant']}>
                    <DocumentsPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="chat"
                element={
                  <ProtectedRoute allowedRoles={['admin', 'supervisor', 'driver', 'employee']}>
                    <ChatPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="calendar"
                element={
                  <ProtectedRoute allowedRoles={['admin', 'supervisor', 'maintenance']}>
                    <CalendarPage />
                  </ProtectedRoute>
                }
              />
              
              {/* Lists */}
              <Route
                path="shipments"
                element={
                  <ProtectedRoute allowedRoles={['admin', 'supervisor', 'client', 'driver']}>
                    <ShipmentList />
                  </ProtectedRoute>
                }
              />
              <Route
                path="trucks"
                element={
                  <ProtectedRoute allowedRoles={['admin', 'supervisor', 'employee', 'maintenance']}>
                    <TruckList />
                  </ProtectedRoute>
                }
              />
              <Route
                path="invoices"
                element={
                  <ProtectedRoute allowedRoles={['admin', 'accountant', 'client']}>
                    <InvoiceList />
                  </ProtectedRoute>
                }
              />
              <Route
                path="users"
                element={
                  <ProtectedRoute allowedRoles={['admin']}>
                    <UserList />
                  </ProtectedRoute>
                }
              />
              
              {/* Details */}
              <Route path="shipments/:id" element={<ShipmentDetail />} />
              <Route path="trucks/:id" element={<TruckDetail />} />
              <Route path="invoices/:id" element={<InvoiceDetail />} />
              <Route path="users/:id" element={<UserDetail />} />
              
              {/* Forms */}
              <Route
                path="shipments/new"
                element={
                  <ProtectedRoute allowedRoles={['admin', 'supervisor', 'client']}>
                    <ShipmentForm />
                  </ProtectedRoute>
                }
              />
              <Route
                path="trucks/new"
                element={
                  <ProtectedRoute allowedRoles={['admin', 'supervisor']}>
                    <TruckForm />
                  </ProtectedRoute>
                }
              />
              <Route
                path="trucks/:id/edit"
                element={
                  <ProtectedRoute allowedRoles={['admin', 'supervisor']}>
                    <TruckForm />
                  </ProtectedRoute>
                }
              />
              <Route
                path="invoices/new"
                element={
                  <ProtectedRoute allowedRoles={['admin', 'accountant']}>
                    <InvoiceForm />
                  </ProtectedRoute>
                }
              />
              <Route
                path="invoices/:id/edit"
                element={
                  <ProtectedRoute allowedRoles={['admin', 'accountant']}>
                    <InvoiceForm />
                  </ProtectedRoute>
                }
              />
              <Route
                path="users/new"
                element={
                  <ProtectedRoute allowedRoles={['admin']}>
                    <UserForm />
                  </ProtectedRoute>
                }
              />
              <Route
                path="users/:id/edit"
                element={
                  <ProtectedRoute allowedRoles={['admin']}>
                    <UserForm />
                  </ProtectedRoute>
                }
              />
            </Route>
            
            {/* Default Route */}
            <Route path="/" element={<Navigate to="/login" replace />} />
            
            {/* Catch-all Route */}
            <Route path="*" element={<Navigate to="/login" replace />} />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
