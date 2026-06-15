/**
 * ============================================
 * 🗺️ Tracking Map Component - Orange/Dark Theme
 * نظام إدهام - مكون التتبع المباشر
 * ============================================
 */

import React, { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  MapPin, Navigation, Phone, User, Clock, 
  Truck, ArrowUp, ArrowDown, X, Minus, Plus
} from 'lucide-react';
import ORANGE_COLORS from './OrangeThemeComponents';

// ============================================
// 🗺️ Tracking Map Component
// ============================================
export const TrackingMap = ({ 
  shipmentData, 
  driverInfo,
  onCallDriver,
  onClose 
}) => {
  const [mapCenter, setMapCenter] = useState([24.7136, 46.6753]); // Riyadh coordinates
  const [zoom, setZoom] = useState(12);
  const [bottomSheetHeight, setBottomSheetHeight] = useState(40);
  const [isDragging, setIsDragging] = useState(false);
  const mapRef = useRef(null);
  const bottomSheetRef = useRef(null);

  // Mock route coordinates for demonstration
  const routeCoordinates = [
    [24.7136, 46.6753], // Start
    [24.7200, 46.6800],
    [24.7300, 46.6850],
    [24.7400, 46.6900],
    [24.7500, 46.6950], // Current position
    [24.7600, 46.7000],
    [24.7700, 46.7050],
    [24.7800, 46.7100]  // End
  ];

  useEffect(() => {
    // Simulate real-time location updates
    const interval = setInterval(() => {
      setMapCenter([
        24.7136 + Math.random() * 0.01,
        46.6753 + Math.random() * 0.01
      ]);
    }, 5000);

    return () => clearInterval(interval);
  }, []);

  const handleBottomSheetDrag = (e) => {
    if (!isDragging) return;
    
    const startY = e.clientY || e.touches[0].clientY;
    const startHeight = bottomSheetHeight;

    const handleMove = (moveEvent) => {
      const currentY = moveEvent.clientY || moveEvent.touches[0].clientY;
      const deltaY = startY - currentY;
      const newHeight = Math.max(40, Math.min(80, startHeight + (deltaY / window.innerHeight * 100)));
      setBottomSheetHeight(newHeight);
    };

    const handleEnd = () => {
      setIsDragging(false);
      document.removeEventListener('mousemove', handleMove);
      document.removeEventListener('mouseup', handleEnd);
      document.removeEventListener('touchmove', handleMove);
      document.removeEventListener('touchend', handleEnd);
    };

    document.addEventListener('mousemove', handleMove);
    document.addEventListener('mouseup', handleEnd);
    document.addEventListener('touchmove', handleMove);
    document.addEventListener('touchend', handleEnd);
  };

  const formatTime = (minutes) => {
    if (minutes < 60) return `${minutes} mins`;
    const hours = Math.floor(minutes / 60);
    const remainingMinutes = minutes % 60;
    return `${hours}h ${remainingMinutes}m`;
  };

  const getStatusColor = (status) => {
    switch (status.toLowerCase()) {
      case 'in transit': return ORANGE_COLORS.primary;
      case 'delivered': return ORANGE_COLORS.success;
      case 'pending': return ORANGE_COLORS.warning;
      default: return ORANGE_COLORS.textSecondary;
    }
  };

  return (
    <div style={{
      position: 'relative',
      width: '100vw',
      height: '100vh',
      backgroundColor: ORANGE_COLORS.background,
      overflow: 'hidden'
    }}>
      {/* Map Container */}
      <div 
        ref={mapRef}
        style={{
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background: `linear-gradient(135deg, ${ORANGE_COLORS.background} 0%, ${ORANGE_COLORS.surface} 100%)`
        }}
      >
        {/* Simulated Map */}
        <div style={{
          position: 'absolute',
          top: '50%',
          left: '50%',
          transform: 'translate(-50%, -50%)',
          width: '90%',
          height: '90%',
          backgroundColor: ORANGE_COLORS.surface,
          borderRadius: '20px',
          border: `2px solid ${ORANGE_COLORS.border}`,
          overflow: 'hidden'
        }}>
          {/* Route Line */}
          <svg
            width="100%"
            height="100%"
            style={{ position: 'absolute', top: 0, left: 0 }}
          >
            <motion.path
              d={`M ${routeCoordinates.map((coord, index) => {
                const x = (coord[1] - 46.6753) * 10000 + 200;
                const y = (coord[0] - 24.7136) * 10000 + 200;
                return `${index === 0 ? 'M' : 'L'} ${x} ${y}`;
              }).join(' ')}`}
              stroke={ORANGE_COLORS.primary}
              strokeWidth="4"
              fill="none"
              strokeDasharray="10,5"
              initial={{ pathLength: 0 }}
              animate={{ pathLength: 1 }}
              transition={{ duration: 2, ease: "easeInOut" }}
            />
          </svg>

          {/* Start Point */}
          <div style={{
            position: 'absolute',
            top: '200px',
            left: '200px',
            transform: 'translate(-50%, -50%)'
          }}>
            <motion.div
              style={{
                width: '20px',
                height: '20px',
                borderRadius: '50%',
                backgroundColor: ORANGE_COLORS.success,
                border: '3px solid white',
                boxShadow: `0 0 20px ${ORANGE_COLORS.success}`
              }}
              animate={{ scale: [1, 1.2, 1] }}
              transition={{ duration: 2, repeat: Infinity }}
            />
            <MapPin 
              size={16} 
              color="white" 
              style={{ 
                position: 'absolute', 
                top: '50%', 
                left: '50%', 
                transform: 'translate(-50%, -50%)' 
              }} 
            />
          </div>

          {/* Current Position (Truck) */}
          <motion.div
            style={{
              position: 'absolute',
              top: '300px',
              left: '400px',
              transform: 'translate(-50%, -50%)'
            }}
            animate={{
              top: `${300 + Math.sin(Date.now() / 1000) * 10}px`,
              left: `${400 + Math.cos(Date.now() / 1000) * 5}px`
            }}
            transition={{ duration: 2, repeat: Infinity, ease: "easeInOut" }}
          >
            <div style={{
              width: '40px',
              height: '40px',
              borderRadius: '50%',
              backgroundColor: ORANGE_COLORS.primary,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              boxShadow: `0 0 30px ${ORANGE_COLORS.primary}`,
              border: '3px solid white'
            }}>
              <Truck size={20} color="white" />
            </div>
          </motion.div>

          {/* End Point */}
          <div style={{
            position: 'absolute',
            bottom: '200px',
            right: '200px',
            transform: 'translate(50%, 50%)'
          }}>
            <div style={{
              width: '20px',
              height: '20px',
              borderRadius: '50%',
              backgroundColor: ORANGE_COLORS.textSecondary,
              border: '3px solid white',
              boxShadow: `0 0 20px ${ORANGE_COLORS.textSecondary}`
            }}>
              <Navigation 
                size={12} 
                color="white" 
                style={{ 
                  position: 'absolute', 
                  top: '50%', 
                  left: '50%', 
                  transform: 'translate(-50%, -50%)' 
                }} 
              />
            </div>
          </div>

          {/* Zoom Controls */}
          <div style={{
            position: 'absolute',
            top: '20px',
            right: '20px',
            display: 'flex',
            flexDirection: 'column',
            gap: '8px'
          }}>
            <motion.button
              style={{
                width: '48px',
                height: '48px',
                borderRadius: '12px',
                backgroundColor: ORANGE_COLORS.card,
                border: `1px solid ${ORANGE_COLORS.border}`,
                color: ORANGE_COLORS.text,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                cursor: 'pointer'
              }}
              whileHover={{ scale: 1.1, backgroundColor: ORANGE_COLORS.primary }}
              whileTap={{ scale: 0.9 }}
              onClick={() => setZoom(Math.min(zoom + 1, 20))}
            >
              <Plus size={20} />
            </motion.button>
            
            <motion.button
              style={{
                width: '48px',
                height: '48px',
                borderRadius: '12px',
                backgroundColor: ORANGE_COLORS.card,
                border: `1px solid ${ORANGE_COLORS.border}`,
                color: ORANGE_COLORS.text,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                cursor: 'pointer'
              }}
              whileHover={{ scale: 1.1, backgroundColor: ORANGE_COLORS.primary }}
              whileTap={{ scale: 0.9 }}
              onClick={() => setZoom(Math.max(zoom - 1, 5))}
            >
              <Minus size={20} />
            </motion.button>
          </div>

          {/* Close Button */}
          <motion.button
            style={{
              position: 'absolute',
              top: '20px',
              left: '20px',
              width: '48px',
              height: '48px',
              borderRadius: '50%',
              backgroundColor: ORANGE_COLORS.card,
              border: `1px solid ${ORANGE_COLORS.border}`,
              color: ORANGE_COLORS.text,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              cursor: 'pointer'
            }}
            whileHover={{ scale: 1.1, rotate: 90 }}
            whileTap={{ scale: 0.9 }}
            onClick={onClose}
          >
            <X size={20} />
          </motion.button>
        </div>
      </div>

      {/* Bottom Sheet */}
      <AnimatePresence>
        <motion.div
          ref={bottomSheetRef}
          style={{
            position: 'absolute',
            bottom: 0,
            left: 0,
            right: 0,
            height: `${bottomSheetHeight}%`,
            backgroundColor: ORANGE_COLORS.card,
            borderTop: `2px solid ${ORANGE_COLORS.border}`,
            borderTopLeftRadius: '20px',
            borderTopRightRadius: '20px',
            boxShadow: '0 -10px 40px rgba(0, 0, 0, 0.3)',
            cursor: isDragging ? 'grabbing' : 'grab'
          }}
          initial={{ y: '100%' }}
          animate={{ y: 0 }}
          exit={{ y: '100%' }}
          drag="y"
          dragConstraints={{ top: -window.innerHeight * 0.6, bottom: 0 }}
          onDragStart={() => setIsDragging(true)}
          onDragEnd={() => setIsDragging(false)}
        >
          {/* Drag Indicator */}
          <div style={{
            width: '40px',
            height: '4px',
            backgroundColor: ORANGE_COLORS.textMuted,
            borderRadius: '2px',
            margin: '12px auto',
            cursor: 'grab'
          }} />

          {/* Content */}
          <div style={{ padding: '20px', height: '100%', overflowY: 'auto' }}>
            {/* Shipment Info */}
            <div style={{ marginBottom: '24px' }}>
              <div style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                marginBottom: '16px'
              }}>
                <h2 style={{ 
                  margin: 0, 
                  fontSize: '24px', 
                  fontWeight: 'bold',
                  color: ORANGE_COLORS.text
                }}>
                  {shipmentData?.trackingNumber || '#WE6K8J2M9P'}
                </h2>
                <div style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: '8px',
                  padding: '8px 16px',
                  backgroundColor: `${getStatusColor(shipmentData?.status)}20`,
                  borderRadius: '20px',
                  border: `1px solid ${getStatusColor(shipmentData?.status)}`
                }}>
                  <div style={{
                    width: '8px',
                    height: '8px',
                    borderRadius: '50%',
                    backgroundColor: getStatusColor(shipmentData?.status),
                    boxShadow: `0 0 10px ${getStatusColor(shipmentData?.status)}`
                  }} />
                  <span style={{
                    color: getStatusColor(shipmentData?.status),
                    fontWeight: '600',
                    fontSize: '14px'
                  }}>
                    {shipmentData?.status || 'In Transit'}
                  </span>
                </div>
              </div>

              {/* Progress Info */}
              <div style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(2, 1fr)',
                gap: '16px',
                marginBottom: '20px'
              }}>
                <div>
                  <div style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '8px',
                    marginBottom: '4px'
                  }}>
                    <MapPin size={16} color={ORANGE_COLORS.textSecondary} />
                    <span style={{ 
                      fontSize: '12px', 
                      color: ORANGE_COLORS.textSecondary 
                    }}>
                      Distance Remaining
                    </span>
                  </div>
                  <p style={{ 
                    margin: 0, 
                    fontSize: '20px', 
                    fontWeight: 'bold',
                    color: ORANGE_COLORS.primary
                  }}>
                    {shipmentData?.distanceRemaining || '12 km'}
                  </p>
                </div>

                <div>
                  <div style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '8px',
                    marginBottom: '4px'
                  }}>
                    <Clock size={16} color={ORANGE_COLORS.textSecondary} />
                    <span style={{ 
                      fontSize: '12px', 
                      color: ORANGE_COLORS.textSecondary 
                    }}>
                      Estimated Time
                    </span>
                  </div>
                  <p style={{ 
                    margin: 0, 
                    fontSize: '20px', 
                    fontWeight: 'bold',
                    color: ORANGE_COLORS.primary
                  }}>
                    {formatTime(shipmentData?.estimatedTime || 25)}
                  </p>
                </div>
              </div>

              {/* Progress Bar */}
              <div style={{ marginBottom: '24px' }}>
                <div style={{
                  width: '100%',
                  height: '8px',
                  backgroundColor: ORANGE_COLORS.surface,
                  borderRadius: '4px',
                  overflow: 'hidden'
                }}>
                  <motion.div
                    style={{
                      height: '100%',
                      backgroundColor: ORANGE_COLORS.primary,
                      borderRadius: '4px'
                    }}
                    initial={{ width: 0 }}
                    animate={{ width: `${shipmentData?.progress || 65}%` }}
                    transition={{ duration: 1, delay: 0.5 }}
                  />
                </div>
                <div style={{
                  display: 'flex',
                  justifyContent: 'space-between',
                  marginTop: '8px'
                }}>
                  <span style={{ 
                    fontSize: '12px', 
                    color: ORANGE_COLORS.textSecondary 
                  }}>
                    Progress
                  </span>
                  <span style={{ 
                    fontSize: '12px', 
                    color: ORANGE_COLORS.textSecondary 
                  }}>
                    {shipmentData?.progress || 65}%
                  </span>
                </div>
              </div>
            </div>

            {/* Driver Info Card */}
            <div style={{
              backgroundColor: ORANGE_COLORS.surface,
              borderRadius: '16px',
              padding: '20px',
              border: `1px solid ${ORANGE_COLORS.border}`
            }}>
              <h3 style={{ 
                margin: '0 0 16px 0', 
                fontSize: '18px', 
                fontWeight: 'bold',
                color: ORANGE_COLORS.text
              }}>
                Driver Information
              </h3>

              <div style={{
                display: 'flex',
                alignItems: 'center',
                gap: '16px',
                marginBottom: '16px'
              }}>
                <div style={{
                  width: '60px',
                  height: '60px',
                  borderRadius: '50%',
                  backgroundColor: ORANGE_COLORS.primary,
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  color: 'white',
                  fontSize: '24px',
                  fontWeight: 'bold'
                }}>
                  {driverInfo?.name?.charAt(0) || 'D'}
                </div>
                
                <div style={{ flex: 1 }}>
                  <h4 style={{ 
                    margin: '0 0 4px 0', 
                    fontSize: '16px', 
                    fontWeight: '600',
                    color: ORANGE_COLORS.text
                  }}>
                    {driverInfo?.name || 'Ahmed Mohammed'}
                  </h4>
                  <p style={{ 
                    margin: 0, 
                    fontSize: '14px', 
                    color: ORANGE_COLORS.textSecondary 
                  }}>
                    🚛 {driverInfo?.vehicleNumber || 'ABC-1234'}
                  </p>
                  <div style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '8px',
                    marginTop: '8px'
                  }}>
                    <div style={{
                      width: '8px',
                      height: '8px',
                      borderRadius: '50%',
                      backgroundColor: ORANGE_COLORS.success,
                      boxShadow: `0 0 10px ${ORANGE_COLORS.success}`
                    }} />
                    <span style={{
                      fontSize: '12px',
                      color: ORANGE_COLORS.success,
                      fontWeight: '600'
                    }}>
                      Online
                    </span>
                  </div>
                </div>

                {/* Call Button */}
                <motion.button
                  onClick={onCallDriver}
                  style={{
                    width: '56px',
                    height: '56px',
                    borderRadius: '50%',
                    backgroundColor: ORANGE_COLORS.primary,
                    border: 'none',
                    color: 'white',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    cursor: 'pointer',
                    boxShadow: `0 4px 20px ${ORANGE_COLORS.primary}40`
                  }}
                  whileHover={{ scale: 1.1 }}
                  whileTap={{ scale: 0.9 }}
                >
                  <Phone size={24} />
                </motion.button>
              </div>

              {/* Driver Stats */}
              <div style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(2, 1fr)',
                gap: '12px'
              }}>
                <div style={{
                  backgroundColor: ORANGE_COLORS.background,
                  padding: '12px',
                  borderRadius: '8px'
                }}>
                  <p style={{ 
                    margin: '0 0 4px 0', 
                    fontSize: '12px', 
                    color: ORANGE_COLORS.textSecondary 
                  }}>
                    Rating
                  </p>
                  <p style={{ 
                    margin: 0, 
                    fontSize: '16px', 
                    fontWeight: 'bold',
                    color: ORANGE_COLORS.primary
                  }}>
                    ⭐ {driverInfo?.rating || '4.8'}
                  </p>
                </div>

                <div style={{
                  backgroundColor: ORANGE_COLORS.background,
                  padding: '12px',
                  borderRadius: '8px'
                }}>
                  <p style={{ 
                    margin: '0 0 4px 0', 
                    fontSize: '12px', 
                    color: ORANGE_COLORS.textSecondary 
                  }}>
                    Deliveries
                  </p>
                  <p style={{ 
                    margin: 0, 
                    fontSize: '16px', 
                    fontWeight: 'bold',
                    color: ORANGE_COLORS.primary
                  }}>
                    {driverInfo?.deliveries || '1,247'}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </motion.div>
      </AnimatePresence>
    </div>
  );
};

export default TrackingMap;
