/**
 * ============================================
 * 🗺️ Tracking Map Component - نظام إدهام
 * Edham Logistics - Tracking Map
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import { MapPin, Navigation, RefreshCw, Layers } from 'lucide-react';

export default function TrackingMap({ shipments = [], trucks = [], onMarkerClick }) {
  const [mapLoaded, setMapLoaded] = useState(false);
  const [showShipments, setShowShipments] = useState(true);
  const [showTrucks, setShowTrucks] = useState(true);

  useEffect(() => {
    // Simulate map loading
    const timer = setTimeout(() => setMapLoaded(true), 1000);
    return () => clearTimeout(timer);
  }, []);

  if (!mapLoaded) {
    return (
      <div className="bg-gray-800 rounded-lg h-96 flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="relative bg-gray-800 rounded-lg overflow-hidden">
      {/* Map Placeholder */}
      <div className="h-96 bg-gray-700 relative">
        <div className="absolute inset-0 flex items-center justify-center">
          <div className="text-center">
            <MapPin className="w-16 h-16 text-blue-500 mx-auto mb-4" />
            <p className="text-gray-400">خريطة التتبع</p>
            <p className="text-gray-500 text-sm">Google Maps API مطلوب</p>
          </div>
        </div>

        {/* Simulated Markers */}
        {showShipments && shipments.map((shipment) => (
          <div
            key={shipment._id}
            className="absolute cursor-pointer"
            style={{
              top: `${20 + Math.random() * 60}%`,
              left: `${20 + Math.random() * 60}%`
            }}
            onClick={() => onMarkerClick?.(shipment)}
          >
            <div className="bg-blue-600 w-8 h-8 rounded-full flex items-center justify-center shadow-lg">
              <MapPin className="w-4 h-4 text-white" />
            </div>
          </div>
        ))}

        {showTrucks && trucks.map((truck) => (
          <div
            key={truck._id}
            className="absolute cursor-pointer"
            style={{
              top: `${20 + Math.random() * 60}%`,
              left: `${20 + Math.random() * 60}%`
            }}
            onClick={() => onMarkerClick?.(truck)}
          >
            <div className="bg-green-600 w-8 h-8 rounded-full flex items-center justify-center shadow-lg">
              <Navigation className="w-4 h-4 text-white" />
            </div>
          </div>
        ))}
      </div>

      {/* Map Controls */}
      <div className="absolute top-4 right-4 flex flex-col gap-2">
        <button
          onClick={() => setShowShipments(!showShipments)}
          className={`p-2 rounded ${showShipments ? 'bg-blue-600' : 'bg-gray-700'} text-white`}
          title="إظهار الشحنات"
        >
          <MapPin className="w-4 h-4" />
        </button>
        <button
          onClick={() => setShowTrucks(!showTrucks)}
          className={`p-2 rounded ${showTrucks ? 'bg-green-600' : 'bg-gray-700'} text-white`}
          title="إظهار الشاحنات"
        >
          <Navigation className="w-4 h-4" />
        </button>
        <button
          className="p-2 bg-gray-700 rounded text-white"
          title="تحديث"
        >
          <RefreshCw className="w-4 h-4" />
        </button>
      </div>

      {/* Legend */}
      <div className="absolute bottom-4 left-4 bg-gray-900/90 p-3 rounded-lg">
        <div className="flex items-center gap-4 text-white text-sm">
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 bg-blue-600 rounded-full" />
            <span>شحنات</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-3 h-3 bg-green-600 rounded-full" />
            <span>شاحنات</span>
          </div>
        </div>
      </div>
    </div>
  );
}
