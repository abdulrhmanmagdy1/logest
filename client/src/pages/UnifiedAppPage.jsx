/**
 * ============================================
 * 🚛 Edham Logistics - Unified Super App
 * نظام إدهام - التطبيق الموحد
 * ============================================
 */

import React, { useState, useEffect, useContext } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Truck, Package, DollarSign, Wrench, Settings, LogOut,
  User, Bell, Search, Menu, X, Home, MapPin, Navigation,
  Calendar, FileText, BarChart3, Shield, Clock, CheckCircle,
  AlertCircle, TrendingUp, Users, Activity, CreditCard
} from 'lucide-react';
import './UnifiedAppPage.css';

// Import all role-specific pages
import OnboardingPage from './OnboardingPage';
import AuthenticationPage from './AuthenticationPage';
import CustomerDashboardPage from './CustomerDashboardPage';
import BookingFlowPage from './BookingFlowPage';
import LiveTrackingPage from './LiveTrackingPage';
import DriverAppPage from './DriverAppPage';
import AdminDashboardPage from './AdminDashboardPage';

// Role-based navigation configurations
const ROLE_CONFIGS = {
  CUSTOMER: {
    name: 'العميل',
    icon: Package,
    color: '#3b82f6',
    navigation: [
      { id: 'dashboard', label: 'الرئيسية', icon: Home, component: 'CustomerDashboardPage' },
      { id: 'booking', label: 'طلب حمولة', icon: Package, component: 'BookingFlowPage' },
      { id: 'tracking', label: 'التتبع', icon: MapPin, component: 'LiveTrackingPage' },
      { id: 'history', label: 'السجل', icon: Clock, component: 'CustomerDashboardPage' },
      { id: 'profile', label: 'الملف الشخصي', icon: User, component: 'CustomerDashboardPage' }
    ]
  },
  DRIVER: {
    name: 'السائق',
    icon: Truck,
    color: '#10b981',
    navigation: [
      { id: 'dashboard', label: 'لوحة التحكم', icon: Home, component: 'DriverAppPage' },
      { id: 'tasks', label: 'المهام', icon: Package, component: 'DriverAppPage' },
      { id: 'location', label: 'الموقع', icon: Navigation, component: 'DriverAppPage' },
      { id: 'earnings', label: 'الأرباح', icon: DollarSign, component: 'DriverAppPage' },
      { id: 'profile', label: 'الملف الشخصي', icon: User, component: 'DriverAppPage' }
    ]
  },
  SUPERVISOR: {
    name: 'المشرف',
    icon: Shield,
    color: '#8b5cf6',
    navigation: [
      { id: 'dashboard', label: 'غرفة العمليات', icon: Home, component: 'AdminDashboardPage' },
      { id: 'fleet', label: 'الأسطول', icon: Truck, component: 'AdminDashboardPage' },
      { id: 'dispatch', label: 'توزيع الطلبات', icon: Package, component: 'AdminDashboardPage' },
      { id: 'drivers', label: 'السائقين', icon: Users, component: 'AdminDashboardPage' },
      { id: 'analytics', label: 'التحليلات', icon: BarChart3, component: 'AdminDashboardPage' }
    ]
  },
  ACCOUNTANT: {
    name: 'المحاسب',
    icon: DollarSign,
    color: '#f59e0b',
    navigation: [
      { id: 'dashboard', label: 'لوحة المالية', icon: Home, component: 'AdminDashboardPage' },
      { id: 'invoices', label: 'الفواتير', icon: FileText, component: 'AdminDashboardPage' },
      { id: 'payments', label: 'المدفوعات', icon: CreditCard, component: 'AdminDashboardPage' },
      { id: 'reports', label: 'التقارير', icon: BarChart3, component: 'AdminDashboardPage' },
      { id: 'tax', label: 'الضرائب', icon: FileText, component: 'AdminDashboardPage' }
    ]
  },
  WORKSHOP: {
    name: 'الورشة',
    icon: Wrench,
    color: '#ef4444',
    navigation: [
      { id: 'dashboard', label: 'لوحة الصيانة', icon: Home, component: 'AdminDashboardPage' },
      { id: 'fleet', label: 'حالة الأسطول', icon: Truck, component: 'AdminDashboardPage' },
      { id: 'maintenance', label: 'الصيانة', icon: Wrench, component: 'AdminDashboardPage' },
      { id: 'parts', label: 'القطع', icon: Package, component: 'AdminDashboardPage' },
      { id: 'alerts', label: 'التنبيهات', icon: AlertCircle, component: 'AdminDashboardPage' }
    ]
  },
  ADMIN: {
    name: 'المدير',
    icon: Shield,
    color: '#dc2626',
    navigation: [
      { id: 'dashboard', label: 'لوحة التحكم', icon: Home, component: 'AdminDashboardPage' },
      { id: 'supervisor', label: 'المشرفين', icon: Shield, component: 'AdminDashboardPage' },
      { id: 'accountant', label: 'المحاسبة', icon: DollarSign, component: 'AdminDashboardPage' },
      { id: 'workshop', label: 'الورشة', icon: Wrench, component: 'AdminDashboardPage' },
      { id: 'settings', label: 'الإعدادات', icon: Settings, component: 'AdminDashboardPage' }
    ]
  }
};

const UnifiedAppPage = () => {
  const [currentView, setCurrentView] = useState('onboarding');
  const [userRole, setUserRole] = useState(null);
  const [user, setUser] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [activeTab, setActiveTab] = useState('dashboard');
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [loading, setLoading] = useState(false);

  // Check authentication status on mount
  useEffect(() => {
    const token = localStorage.getItem('token');
    const storedUser = localStorage.getItem('user');
    
    if (token && storedUser) {
      try {
        const userData = JSON.parse(storedUser);
        setUser(userData);
        setUserRole(userData.role);
        setIsAuthenticated(true);
        setCurrentView('main');
      } catch (error) {
        console.error('Error parsing user data:', error);
        logout();
      }
    }
  }, []);

  // Handle authentication
  const handleAuthentication = (userData, token) => {
    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(userData));
    
    setUser(userData);
    setUserRole(userData.role);
    setIsAuthenticated(true);
    setCurrentView('main');
    setActiveTab('dashboard');
  };

  // Handle logout
  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setUser(null);
    setUserRole(null);
    setIsAuthenticated(false);
    setCurrentView('onboarding');
    setActiveTab('dashboard');
    setNotifications([]);
  };

  // Get current role configuration
  const roleConfig = ROLE_CONFIGS[userRole] || ROLE_CONFIGS.CUSTOMER;
  const navigationItems = roleConfig.navigation;

  // Render component based on current view
  const renderCurrentView = () => {
    switch (currentView) {
      case 'onboarding':
        return <OnboardingPage onGetStarted={() => setCurrentView('authentication')} />;
      
      case 'authentication':
        return (
          <AuthenticationPage 
            onAuthentication={handleAuthentication}
            onBackToOnboarding={() => setCurrentView('onboarding')}
          />
        );
      
      case 'main':
        return renderMainView();
      
      default:
        return <OnboardingPage onGetStarted={() => setCurrentView('authentication')} />;
    }
  };

  // Render main application view
  const renderMainView = () => {
    const activeNavigationItem = navigationItems.find(item => item.id === activeTab);
    const ComponentName = activeNavigationItem?.component;

    // Map component names to actual components
    const componentMap = {
      'CustomerDashboardPage': CustomerDashboardPage,
      'BookingFlowPage': BookingFlowPage,
      'LiveTrackingPage': LiveTrackingPage,
      'DriverAppPage': DriverAppPage,
      'AdminDashboardPage': AdminDashboardPage
    };

    const ActiveComponent = componentMap[ComponentName];

    if (!ActiveComponent) {
      return (
        <div className="component-not-found">
          <AlertCircle className="w-16 h-16" />
          <h2>المكون غير متاح</h2>
          <p>المكون {ComponentName} غير متاح حالياً</p>
        </div>
      );
    }

    // Pass role-specific props to components
    const componentProps = {
      userRole,
      user,
      onNavigate: setActiveTab,
      notifications,
      setNotifications
    };

    return <ActiveComponent {...componentProps} />;
  };

  // Render header based on role
  const renderHeader = () => {
    if (currentView !== 'main') return null;

    return (
      <motion.header 
        className="app-header"
        initial={{ y: -100 }}
        animate={{ y: 0 }}
        transition={{ type: "spring", stiffness: 100 }}
      >
        <div className="header-content">
          <div className="header-left">
            <button 
              className="menu-toggle"
              onClick={() => setSidebarOpen(!sidebarOpen)}
            >
              {sidebarOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
            
            <div className="app-brand">
              <Truck className="w-8 h-8" style={{ color: roleConfig.color }} />
              <h1>إدهام</h1>
              <span className="role-badge" style={{ backgroundColor: roleConfig.color }}>
                {roleConfig.name}
              </span>
            </div>
          </div>

          <div className="header-center">
            <div className="search-bar">
              <Search className="w-5 h-5" />
              <input
                type="text"
                placeholder="البحث..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>
          </div>

          <div className="header-right">
            <div className="notification-bell">
              <Bell className="w-6 h-6" />
              {notifications.length > 0 && (
                <span className="notification-badge">{notifications.length}</span>
              )}
            </div>
            
            <div className="user-menu">
              <div className="user-avatar">
                {user?.avatar ? (
                  <img src={user.avatar} alt={user.name} />
                ) : (
                  <User className="w-6 h-6" />
                )}
              </div>
              <span className="user-name">{user?.name}</span>
            </div>

            <button className="logout-btn" onClick={logout}>
              <LogOut className="w-5 h-5" />
            </button>
          </div>
        </div>
      </motion.header>
    );
  };

  // Render sidebar navigation
  const renderSidebar = () => {
    if (currentView !== 'main') return null;

    return (
      <AnimatePresence>
        {sidebarOpen && (
          <motion.div
            className="sidebar-overlay"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={() => setSidebarOpen(false)}
          />
        )}
        
        <motion.aside
          className={`app-sidebar ${sidebarOpen ? 'open' : ''}`}
          initial={{ x: -300 }}
          animate={{ x: sidebarOpen ? 0 : -300 }}
          exit={{ x: -300 }}
          transition={{ type: "spring", stiffness: 100 }}
        >
          <div className="sidebar-header">
            <div className="sidebar-brand">
              <Truck className="w-8 h-8" style={{ color: roleConfig.color }} />
              <h2>إدهام</h2>
            </div>
            <button 
              className="sidebar-close"
              onClick={() => setSidebarOpen(false)}
            >
              <X className="w-6 h-6" />
            </button>
          </div>

          <nav className="sidebar-nav">
            {navigationItems.map((item) => {
              const Icon = item.icon;
              const isActive = activeTab === item.id;
              
              return (
                <button
                  key={item.id}
                  className={`nav-item ${isActive ? 'active' : ''}`}
                  onClick={() => {
                    setActiveTab(item.id);
                    setSidebarOpen(false);
                  }}
                  style={{
                    '--nav-color': roleConfig.color,
                    '--nav-active-bg': `${roleConfig.color}15`,
                    '--nav-active-border': roleConfig.color
                  }}
                >
                  <Icon className="w-5 h-5" />
                  <span>{item.label}</span>
                  {isActive && <div className="active-indicator" />}
                </button>
              );
            })}
          </nav>

          <div className="sidebar-footer">
            <div className="user-info">
              <div className="user-avatar">
                {user?.avatar ? (
                  <img src={user.avatar} alt={user.name} />
                ) : (
                  <User className="w-8 h-8" />
                )}
              </div>
              <div className="user-details">
                <span className="user-name">{user?.name}</span>
                <span className="user-role">{roleConfig.name}</span>
              </div>
            </div>
            
            <button className="logout-sidebar" onClick={logout}>
              <LogOut className="w-5 h-5" />
              <span>تسجيل الخروج</span>
            </button>
          </div>
        </motion.aside>
      </AnimatePresence>
    );
  };

  // Render mobile bottom navigation
  const renderBottomNav = () => {
    if (currentView !== 'main') return null;

    return (
      <nav className="bottom-nav">
        {navigationItems.slice(0, 5).map((item) => {
          const Icon = item.icon;
          const isActive = activeTab === item.id;
          
          return (
            <button
              key={item.id}
              className={`bottom-nav-item ${isActive ? 'active' : ''}`}
              onClick={() => setActiveTab(item.id)}
              style={{ '--nav-color': roleConfig.color }}
            >
              <Icon className="w-5 h-5" />
              <span>{item.label}</span>
            </button>
          );
        })}
      </nav>
    );
  };

  return (
    <div className="unified-app" data-role={userRole?.toLowerCase()}>
      {/* Header */}
      {renderHeader()}

      {/* Sidebar */}
      {renderSidebar()}

      {/* Main Content */}
      <main className={`app-main ${currentView === 'main' ? 'with-header' : ''}`}>
        <AnimatePresence mode="wait">
          <motion.div
            key={`${currentView}-${activeTab}`}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            transition={{ duration: 0.3 }}
            className="content-wrapper"
          >
            {renderCurrentView()}
          </motion.div>
        </AnimatePresence>
      </main>

      {/* Bottom Navigation (Mobile) */}
      {renderBottomNav()}

      {/* Loading Overlay */}
      {loading && (
        <div className="loading-overlay">
          <div className="loading-spinner"></div>
          <p>جاري التحميل...</p>
        </div>
      )}
    </div>
  );
};

export default UnifiedAppPage;
