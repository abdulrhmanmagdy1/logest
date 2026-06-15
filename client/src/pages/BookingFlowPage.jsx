/**
 * ============================================
 * 📦 Edham Logistics - Booking Flow Page
 * نظام إدهام - مسار طلب الحمولة
 * Matching reference image design
 * ============================================
 */

import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { 
  ArrowLeft, MoreVertical, MapPin, Package, Truck, 
  DollarSign, Users, Shield
} from 'lucide-react';
import BottomNavigation from '../components/BottomNavigation';
import './BookingFlowPage.css';

const BookingFlowPage = () => {
  const [activeTab, setActiveTab] = useState('booking');
  
  const [bookingData, setBookingData] = useState({
    pickup: {
      address: '278 Ash Dr. San Jose, South Dakota 83475',
      location: null
    },
    dropoff: {
      address: '4140 Parker Rd. Allentown, New Mexico 3034',
      location: null
    },
    vehicle: null,
    cargo: {
      description: '',
      weight: '',
      dimensions: ''
    }
  });

  const [selectedVehicle, setSelectedVehicle] = useState(null);
  
  const vehicleOptions = [
    {
      id: 'motorcycle',
      name: 'Motorcycle',
      description: 'For small packages and documents',
      capacity: 'Up to 20kg',
      dimensions: '60x40x30 cm',
      price: 15,
      icon: '🏍️',
      image: '/api/placeholder/100/80'
    },
    {
      id: 'van',
      name: 'Van',
      description: 'For medium-sized deliveries',
      capacity: 'Up to 500kg',
      dimensions: '300x200x200 cm',
      price: 25,
      icon: '🚐',
      image: '/api/placeholder/120/80'
    },
    {
      id: 'truck',
      name: 'Truck',
      description: 'For large cargo and furniture',
      capacity: 'Up to 2000kg',
      dimensions: '600x250x300 cm',
      price: 45,
      icon: '🚚',
      image: '/api/placeholder/140/80'
    }
  ];

  const handleVehicleSelect = (vehicle) => {
    setSelectedVehicle(vehicle);
    setBookingData(prev => ({
      ...prev,
      vehicle: vehicle
    }));
  };

  return (
    <div className="booking-flow-page">
      {/* Header */}
      <motion.header
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="booking-header"
      >
        <div className="header-content">
          <button className="back-button">
            <ArrowLeft className="w-6 h-6" />
          </button>
          <h1>New Order</h1>
          <button className="menu-button">
            <MoreVertical className="w-6 h-6" />
          </button>
        </div>
      </motion.header>

      {/* Main Content */}
      <main className="booking-main">
        {/* Location Section */}
        <motion.section
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="location-section"
        >
          <div className="location-cards">
            <div className="location-card">
              <div className="location-header">
                <MapPin className="location-icon" />
                <h3>Pickup</h3>
              </div>
              <div className="location-content">
                <p>{bookingData.pickup.address}</p>
                <button className="change-location-btn">Change</button>
              </div>
            </div>

            <div className="location-card">
              <div className="location-header">
                <Truck className="location-icon" />
                <h3>Dropoff</h3>
              </div>
              <div className="location-content">
                <p>{bookingData.dropoff.address}</p>
                <button className="change-location-btn">Change</button>
              </div>
            </div>
          </div>
        </motion.section>

        {/* Vehicle Selection */}
        <motion.section
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="vehicle-section"
        >
          <h2 className="section-title">Select Vehicle</h2>
          <div className="vehicle-grid">
            {vehicleOptions.map((vehicle) => (
              <motion.div
                key={vehicle.id}
                whileHover={{ scale: 1.02 }}
                whileTap={{ scale: 0.98 }}
                onClick={() => handleVehicleSelect(vehicle)}
                className={`vehicle-card ${selectedVehicle?.id === vehicle.id ? 'selected' : ''}`}
              >
                <div className="vehicle-image">
                  <img 
                    src={vehicle.image} 
                    alt={vehicle.name}
                    className="vehicle-img"
                  />
                  <div className="vehicle-icon">{vehicle.icon}</div>
                </div>
                <div className="vehicle-info">
                  <h4>{vehicle.name}</h4>
                  <p className="vehicle-description">{vehicle.description}</p>
                  <div className="vehicle-specs">
                    <div className="spec">
                      <Package className="w-4 h-4" />
                      <span>{vehicle.capacity}</span>
                    </div>
                    <div className="spec">
                      <Users className="w-4 h-4" />
                      <span>{vehicle.dimensions}</span>
                    </div>
                  </div>
                  <div className="vehicle-price">
                    <DollarSign className="w-4 h-4" />
                    <span>${vehicle.price}/hr</span>
                  </div>
                </div>
                {selectedVehicle?.id === vehicle.id && (
                  <div className="selected-badge">
                    <Shield className="w-4 h-4" />
                    <span>Selected</span>
                  </div>
                )}
              </motion.div>
            ))}
          </div>
        </motion.section>

        {/* Cargo Details */}
        <motion.section
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="cargo-section"
        >
          <h2 className="section-title">Cargo Details</h2>
          <div className="cargo-form">
            <div className="form-group">
              <label>Description</label>
              <textarea
                placeholder="Describe your cargo..."
                value={bookingData.cargo.description}
                onChange={(e) => setBookingData(prev => ({
                  ...prev,
                  cargo: { ...prev.cargo, description: e.target.value }
                }))}
                className="form-textarea"
                rows={3}
              />
            </div>
            <div className="form-row">
              <div className="form-group">
                <label>Weight (kg)</label>
                <input
                  type="number"
                  placeholder="0"
                  value={bookingData.cargo.weight}
                  onChange={(e) => setBookingData(prev => ({
                    ...prev,
                    cargo: { ...prev.cargo, weight: e.target.value }
                  }))}
                  className="form-input"
                />
              </div>
              <div className="form-group">
                <label>Dimensions</label>
                <input
                  type="text"
                  placeholder="LxWxH"
                  value={bookingData.cargo.dimensions}
                  onChange={(e) => setBookingData(prev => ({
                    ...prev,
                    cargo: { ...prev.cargo, dimensions: e.target.value }
                  }))}
                  className="form-input"
                />
              </div>
            </div>
          </div>
        </motion.section>

        {/* Action Buttons */}
        <motion.section
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
          className="action-section"
        >
          <button 
            className="proceed-btn"
            disabled={!selectedVehicle}
          >
            Proceed to Payment
          </button>
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

export default BookingFlowPage;
