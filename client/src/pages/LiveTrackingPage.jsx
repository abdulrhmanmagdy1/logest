/**
 * ============================================
 * 🗺️ Edham Logistics - Live Tracking Page
 * نظام إدهام - شاشة التتبع اللحظي
 * Matching reference image design
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { 
  ArrowLeft, MoreVertical, MapPin, Phone, MessageCircle,
  Truck, User
} from 'lucide-react';
import BottomNavigation from '../components/BottomNavigation';
import './LiveTrackingPage.css';

const LiveTrackingPage = () => {
  const [activeTab, setActiveTab] = useState('tracking');
  
  const [trackingData, setTrackingData] = useState({
    id: '#568999856921',
    status: 'Transit',
    kmsRemaining: 12,
    estimatedTime: 25,
    driver: {
      name: 'Cameron Williamson',
      plateNumber: 'AS4 3571',
      phone: '+1 234-567-8900',
      avatar: null
    },
    route: {
      from: '278 Ash Dr. San Jose, South Dakota 83475',
      to: '4140 Parker Rd. Allentown, New Mexico 3034',
      progress: 65
    }
  });

  return (
    <div className="live-tracking-page">
      {/* Header */}
      <motion.header
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="tracking-header"
      >
        <div className="header-content">
          <button className="back-button">
            <ArrowLeft className="w-6 h-6" />
          </button>
          <h1>Tracking Shipment</h1>
          <button className="menu-button">
            <MoreVertical className="w-6 h-6" />
          </button>
        </div>
      </motion.header>

      {/* Map Section */}
      <motion.section
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ delay: 0.2 }}
        className="map-section"
      >
        <div className="map-container">
          <img 
            src="/api/placeholder/400/300" 
            alt="Tracking Map"
            className="map-image"
          />
          <div className="route-overlay">
            <div className="route-line"></div>
            <div className="truck-marker">
              <Truck className="w-8 h-8" />
            </div>
          </div>
        </div>
      </motion.section>

      {/* Tracking Details Card */}
      <motion.section
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.4 }}
        className="tracking-details-section"
      >
        <div className="tracking-card">
          <div className="tracking-header">
            <h2>Tracking ID</h2>
            <span className="tracking-id">{trackingData.id}</span>
          </div>

          <div className="tracking-info">
            <div className="info-row">
              <span className="label">Status</span>
              <span className="value status">{trackingData.status}</span>
            </div>
            <div className="info-row">
              <span className="label">Kms Remaining</span>
              <span className="value">{trackingData.kmsRemaining} km</span>
            </div>
            <div className="info-row">
              <span className="label">Estimated Time</span>
              <span className="value">{trackingData.estimatedTime} mins</span>
            </div>
          </div>

          <div className="route-info">
            <div className="route-point">
              <MapPin className="w-5 h-5" />
              <div className="route-text">
                <span className="label">From</span>
                <span className="address">{trackingData.route.from}</span>
              </div>
            </div>
            <div className="route-point">
              <MapPin className="w-5 h-5" />
              <div className="route-text">
                <span className="label">To</span>
                <span className="address">{trackingData.route.to}</span>
              </div>
            </div>
          </div>

          <div className="driver-info">
            <div className="driver-header">
              <h3>Delivery Partner</h3>
              <div className="driver-avatar">
                {trackingData.driver.avatar ? (
                  <img src={trackingData.driver.avatar} alt={trackingData.driver.name} />
                ) : (
                  <div className="avatar-placeholder">
                    <User className="w-6 h-6" />
                  </div>
                )}
              </div>
            </div>
            <div className="driver-details">
              <h4>{trackingData.driver.name}</h4>
              <p className="plate-number">{trackingData.driver.plateNumber}</p>
            </div>
            <div className="driver-actions">
              <button className="action-button phone">
                <Phone className="w-5 h-5" />
              </button>
              <button className="action-button message">
                <MessageCircle className="w-5 h-5" />
              </button>
            </div>
          </div>
        </div>
      </motion.section>

      {/* Bottom Navigation */}
      <BottomNavigation 
        activeTab={activeTab}
        onTabChange={setActiveTab}
      />
    </div>
  );
};

export default LiveTrackingPage;
