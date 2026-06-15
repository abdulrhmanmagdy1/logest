import React, { useEffect, useState } from 'react';
import { io } from 'socket.io-client';
import { MapPin, Truck, Clock, Navigation } from 'lucide-react';
import { motion } from 'framer-motion';

const socket = io(process.env.REACT_APP_API_URL || 'http://localhost:5000');

const LiveTracking = () => {
  const [selectedTrip, setSelectedTrip] = useState(null);
  const [trips, setTrips] = useState([]);
  const [location, setLocation] = useState(null);
  const [logs, setLogs] = useState([]);

  useEffect(() => {
    // Mock trips data
    setTrips([
      { id: 'trip-1', driver: 'أحمد محمد', from: 'الرياض', to: 'جدة', status: 'active' },
      { id: 'trip-2', driver: 'خالد عبدالله', from: 'الدمام', to: 'الرياض', status: 'active' },
      { id: 'trip-3', driver: 'سعد إبراهيم', from: 'جدة', to: 'مكة', status: 'active' }
    ]);

    socket.on('locationUpdated', (data) => {
      setLocation({ lat: data.lat, lng: data.lng });
      setLogs(prev => [
        {
          time: new Date().toLocaleTimeString('ar-SA'),
          lat: data.lat,
          lng: data.lng,
          speed: data.speed || 0
        },
        ...prev.slice(0, 19)
      ]);
    });

    return () => {
      socket.off('locationUpdated');
    };
  }, []);

  const joinTrip = (tripId) => {
    setSelectedTrip(tripId);
    socket.emit('joinTripRoom', tripId);
  };

  return (
    <div className="p-6 bg-edham-black min-h-screen">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="max-w-6xl mx-auto"
      >
        <h1 className="text-3xl font-bold text-edham-white mb-8 flex items-center gap-3">
          <Navigation className="w-8 h-8 text-edham-primary" />
          التتبع المباشر
        </h1>

        <div className="grid lg:grid-cols-3 gap-6">
          {/* Trips List */}
          <div className="lg:col-span-1 space-y-4">
            <h2 className="text-xl font-semibold text-edham-white mb-4">الرحلات النشطة</h2>
            {trips.map((trip) => (
              <motion.button
                key={trip.id}
                whileHover={{ scale: 1.02 }}
                onClick={() => joinTrip(trip.id)}
                className={`w-full card text-right transition-colors ${
                  selectedTrip === trip.id ? 'border-edham-primary' : ''
                }`}
              >
                <div className="flex items-start gap-3">
                  <div className="w-10 h-10 bg-edham-primary/20 rounded-xl flex items-center justify-center flex-shrink-0">
                    <Truck className="w-5 h-5 text-edham-primary" />
                  </div>
                  <div className="flex-1">
                    <p className="text-edham-white font-medium">{trip.driver}</p>
                    <p className="text-edham-white/60 text-sm">
                      {trip.from} → {trip.to}
                    </p>
                    <div className="flex items-center gap-2 mt-2">
                      <span className="status-badge status-success">نشط</span>
                    </div>
                  </div>
                </div>
              </motion.button>
            ))}
          </div>

          {/* Map Placeholder */}
          <div className="lg:col-span-2 space-y-6">
            <div className="card h-96 flex items-center justify-center relative overflow-hidden">
              <div className="absolute inset-0 bg-gradient-to-br from-edham-dark to-edham-gray">
                {/* Grid pattern */}
                <div className="absolute inset-0 opacity-10" style={{
                  backgroundImage: 'linear-gradient(#DC2626 1px, transparent 1px), linear-gradient(90deg, #DC2626 1px, transparent 1px)',
                  backgroundSize: '40px 40px'
                }} />
              </div>
              
              {selectedTrip ? (
                <div className="relative z-10 text-center">
                  <div className="w-20 h-20 bg-edham-primary/30 rounded-full flex items-center justify-center mx-auto mb-4 animate-pulse">
                    <MapPin className="w-10 h-10 text-edham-primary" />
                  </div>
                  <p className="text-edham-white text-lg">جار التتبع...</p>
                  <p className="text-edham-white/60">الرحلة: {selectedTrip}</p>
                  {location && (
                    <p className="text-edham-primary mt-2">
                      Lat: {location.lat.toFixed(6)}, Lng: {location.lng.toFixed(6)}
                    </p>
                  )}
                </div>
              ) : (
                <div className="relative z-10 text-center">
                  <MapPin className="w-16 h-16 text-edham-white/20 mx-auto mb-4" />
                  <p className="text-edham-white/60 text-lg">اختر رحلة للتتبع</p>
                </div>
              )}
            </div>

            {/* Location Logs */}
            {selectedTrip && (
              <div className="card">
                <h3 className="text-lg font-semibold text-edham-white mb-4 flex items-center gap-2">
                  <Clock className="w-5 h-5 text-edham-primary" />
                  آخر التحديثات
                </h3>
                <div className="space-y-2 max-h-48 overflow-y-auto">
                  {logs.length > 0 ? (
                    logs.map((log, idx) => (
                      <div key={idx} className="flex items-center justify-between p-3 bg-edham-dark rounded-lg text-sm">
                        <span className="text-edham-white/60">{log.time}</span>
                        <span className="text-edham-white">
                          Lat: {log.lat.toFixed(4)}, Lng: {log.lng.toFixed(4)}
                        </span>
                        <span className="text-edham-primary">{log.speed} km/h</span>
                      </div>
                    ))
                  ) : (
                    <p className="text-edham-white/40 text-center py-4">لا توجد تحديثات حتى الآن</p>
                  )}
                </div>
              </div>
            )}
          </div>
        </div>
      </motion.div>
    </div>
  );
};

export default LiveTracking;
