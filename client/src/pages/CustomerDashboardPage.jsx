/**
 * ============================================
 * 🎨 Edham Logistics - Customer Home Dashboard
 * نظام إدهام - الصفحة الرئيسية للعميل
 * Matching reference image design
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  MapPin, Plus, Search, ArrowRight, User,
  Truck, Package, Clock, CheckCircle
} from 'lucide-react';
import BottomNavigation from '../components/BottomNavigation';
import OrderCard from '../components/OrderCard';
import './CustomerDashboardPage.css';

const CustomerDashboardPage = () => {
  const [activeTab, setActiveTab] = useState('home');
  const [user, setUser] = useState({
    name: 'Steven',
    location: 'Forest Hills, NY',
    avatar: null
  });

  const [currentTracking, setCurrentTracking] = useState({
    id: '#WE6K-78RFE4',
    status: 'In transit',
    location: 'Hunters Point, NY',
    progress: 65,
    driver: {
      name: 'Cameron Williamson',
      plateNumber: 'AS4 3571',
      phone: '+1 234-567-8900',
      avatar: null
    }
  });

  const [recentShipments, setRecentShipments] = useState([
    {
      id: '#RW3E-74ESW4',
      status: 'Delivered',
      pickup: 'Morristown, NY',
      delivery: 'Ogdensburg, NY',
      placedDate: '2024-01-10',
      estimatedDate: '2024-01-12',
      progress: 100
    },
    {
      id: '#ED-2024-787',
      status: 'Delivered', 
      pickup: 'Brooklyn, NY',
      delivery: 'Manhattan, NY',
      placedDate: '2024-01-08',
      estimatedDate: '2024-01-09',
      progress: 100
    },
    {
      id: '#ED-2024-786',
      status: 'In transit',
      pickup: 'Queens, NY',
      delivery: 'Bronx, NY', 
      placedDate: '2024-01-11',
      estimatedDate: '2024-01-13',
      progress: 45
    }
  ]);

  const [recentOrders, setRecentOrders] = useState([
    {
      id: '#ED-2024-788',
      status: 'delivered',
      date: '2024-01-10',
      from: 'الرياض',
      to: 'الدمام',
      progress: 100,
      driver: 'عبدالله العتيبي',
      cost: 450
    },
    {
      id: '#ED-2024-787',
      status: 'in_transit',
      date: '2024-01-11',
      from: 'جدة',
      to: 'مكة',
      progress: 45,
      driver: 'خالد الأحمدي',
      cost: 280
    },
    {
      id: '#ED-2024-786',
      status: 'pending',
      date: '2024-01-12',
      from: 'المدينة',
      to: 'الرياض',
      progress: 10,
      driver: 'قيد التعيين',
      cost: 520
    }
  ]);

  const [searchQuery, setSearchQuery] = useState('');
  const [showNotifications, setShowNotifications] = useState(false);
  const [notifications, setNotifications] = useState([
    { id: 1, text: 'شحنتك في الطريق', time: 'منذ 5 دقائق', read: false },
    { id: 2, text: 'تم تأكيد طلبك', time: 'منذ ساعة', read: false },
    { id: 3, text: 'السائق وصل نقطة الاستلام', time: 'منذ ساعتين', read: true }
  ]);

  // Quick Actions
  const quickActions = [
    {
      id: 1,
      title: 'طلب حمولة جديدة',
      icon: <Plus className="w-6 h-6" />,
      color: 'primary',
      action: () => console.log('New shipment')
    },
    {
      id: 2,
      title: 'تتبع شحنة',
      icon: <Search className="w-6 h-6" />,
      color: 'secondary',
      action: () => console.log('Track shipment')
    },
    {
      id: 3,
      title: 'سجل الشحنات',
      icon: <Package className="w-6 h-6" />,
      color: 'accent',
      action: () => console.log('Shipment history')
    },
    {
      id: 4,
      title: 'الدعم الفني',
      icon: <MessageCircle className="w-6 h-6" />,
      color: 'info',
      action: () => console.log('Support')
    }
  ];

  // Status colors and icons
  const getStatusInfo = (status) => {
    const statusMap = {
      pending: { color: 'warning', icon: <Clock className="w-4 h-4" />, text: 'قيد الانتظار' },
      confirmed: { color: 'info', icon: <CheckCircle className="w-4 h-4" />, text: 'مؤكد' },
      in_transit: { color: 'primary', icon: <Truck className="w-4 h-4" />, text: 'في الطريق' },
      delivered: { color: 'success', icon: <CheckCircle className="w-4 h-4" />, text: 'تم التسليم' },
      cancelled: { color: 'error', icon: <AlertCircle className="w-4 h-4" />, text: 'ملغي' }
    };
    return statusMap[status] || statusMap.pending;
  };

  // Progress steps
  const progressSteps = [
    { id: 1, label: 'تم الاستلام', icon: <Package className="w-4 h-4" /> },
    { id: 2, label: 'قيد التحميل', icon: <Truck className="w-4 h-4" /> },
    { id: 3, label: 'في الطريق', icon: <Navigation className="w-4 h-4" /> },
    { id: 4, label: 'تم التسليم', icon: <CheckCircle className="w-4 h-4" /> }
  ];

  return (
    <div className="customer-dashboard">
      {/* Header */}
      <motion.header
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="dashboard-header"
      >
        <div className="header-content">
          <div className="user-section">
            <div className="user-avatar">
              {user.avatar ? (
                <img src={user.avatar} alt={user.name} />
              ) : (
                <div className="avatar-placeholder">
                  <User className="w-6 h-6" />
                </div>
              )}
            </div>
            <div className="user-greeting">
              <h1>Hi, {user.name}!</h1>
              <button className="change-profile-btn">Change Profile</button>
            </div>
          </div>
        </div>
      </motion.header>

      {/* Main Content */}
      <main className="dashboard-main">
        {/* Location & New Tracking Cards */}
        <motion.section
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="location-section"
        >
          <div className="location-cards">
            <motion.div
              whileHover={{ scale: 1.02 }}
              className="location-card"
            >
              <MapPin className="location-icon" />
              <div className="location-content">
                <h3>Your Location</h3>
                <p>{user.location}</p>
              </div>
            </motion.div>

            <motion.div
              whileHover={{ scale: 1.02 }}
              className="location-card"
            >
              <Search className="location-icon" />
              <div className="location-content">
                <h3>New Tracking</h3>
                <p>Tracking by ID</p>
              </div>
            </motion.div>
          </div>
        </motion.section>

        {/* Current Tracking */}
        <motion.section
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="current-tracking-section"
        >
          <div className="tracking-header">
            <h2>Current Tracking</h2>
            <span className="tracking-id">{currentTracking.id}</span>
          </div>
          
          <div className="tracking-card">
            <div className="tracking-status">
              <h3>{currentTracking.status}</h3>
              <p>{currentTracking.location}</p>
            </div>
            
            <div className="tracking-map">
              <img 
                src="/api/placeholder/300/150" 
                alt="Tracking Map"
                className="map-placeholder"
              />
            </div>
          </div>
        </motion.section>

        {/* Recent Shipping */}
        <motion.section
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="recent-shipping-section"
        >
          <div className="section-header">
            <h2>Recent Shipping</h2>
            <button className="see-all-btn">See All</button>
          </div>
          
          <div className="recent-orders">
            {recentShipments.map((order) => (
              <OrderCard
                key={order.id}
                order={order}
                variant="compact"
                onClick={() => console.log('Track order:', order.id)}
              />
            ))}
          </div>
        </motion.section>
      </main>

      {/* Bottom Navigation */}
      <BottomNavigation 
        activeTab={activeTab}
        onTabChange={setActiveTab}
      />
    </div>
  );
};

export default CustomerDashboardPage;
