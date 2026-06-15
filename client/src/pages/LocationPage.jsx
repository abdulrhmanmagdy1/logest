/**
 * ============================================
 * 📍 Location Page - نظام إدهام
 * Edham Logistics - Location Tracking Page
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import api from '../services/api';
import TrackingMap from '../components/Map/TrackingMap';
import { Loader, AlertCircle, MapPin, Navigation, Clock, Filter } from 'lucide-react';

export default function LocationPage() {
  const [shipments, setShipments] = useState([]);
  const [trucks, setTrucks] = useState([]);
  const [selectedItem, setSelectedItem] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchLocationData();
  }, []);

  const fetchLocationData = async () => {
    try {
      setLoading(true);
      const [shipmentsRes, trucksRes] = await Promise.all([
        api.get('/shipments', { params: { status: 'in_transit' } }),
        api.get('/trucks', { params: { status: 'in_transit' } })
      ]);
      setShipments(shipmentsRes.data.data);
      setTrucks(trucksRes.data.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleMarkerClick = (item) => {
    setSelectedItem(item);
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-96">
        <Loader className="animate-spin w-8 h-8 text-blue-600" />
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-500 text-white p-4 rounded">
        <AlertCircle className="inline mr-2" />
        {error}
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto p-6">
      <div className="mb-6">
        <h1 className="text-3xl font-bold text-white">التتبع المباشر</h1>
        <p className="text-gray-400 mt-1">تتبع الشحنات والشاحنات في الوقت الفعلي</p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
        <StatCard title="الشحنات النشطة" value={shipments.length} color="blue" />
        <StatCard title="الشاحنات النشطة" value={trucks.length} color="green" />
        <StatCard title="آخر تحديث" value="منذ دقيقة" color="gold" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Map */}
        <div className="lg:col-span-2">
          <TrackingMap
            shipments={shipments}
            trucks={trucks}
            onMarkerClick={handleMarkerClick}
          />
        </div>

        {/* Details Panel */}
        <div className="bg-gray-800 rounded-lg p-6">
          <h2 className="text-xl font-bold text-white mb-4">تفاصيل الموقع</h2>
          
          {selectedItem ? (
            <div className="space-y-4">
              <div>
                <p className="text-gray-400 text-sm">النوع</p>
                <p className="text-white font-semibold">
                  {selectedItem.shipmentNumber ? 'شحنة' : 'شاحنة'}
                </p>
              </div>
              <div>
                <p className="text-gray-400 text-sm">الرقم</p>
                <p className="text-white font-semibold">
                  {selectedItem.shipmentNumber || selectedItem.truckNumber}
                </p>
              </div>
              {selectedItem.currentLocation && (
                <>
                  <div>
                    <p className="text-gray-400 text-sm">المدينة</p>
                    <p className="text-white">{selectedItem.currentLocation.city}</p>
                  </div>
                  <div>
                    <p className="text-gray-400 text-sm">العنوان</p>
                    <p className="text-white">{selectedItem.currentLocation.address}</p>
                  </div>
                  <div>
                    <p className="text-gray-400 text-sm">الإحداثيات</p>
                    <p className="text-white text-sm">
                      {selectedItem.currentLocation.coordinates?.lat}, {selectedItem.currentLocation.coordinates?.lng}
                    </p>
                  </div>
                </>
              )}
              {selectedItem.driver && (
                <div>
                  <p className="text-gray-400 text-sm">السائق</p>
                  <p className="text-white">{selectedItem.driver.name}</p>
                </div>
              )}
              <div>
                <p className="text-gray-400 text-sm">آخر تحديث</p>
                <p className="text-white">
                  {selectedItem.locationUpdatedAt ? new Date(selectedItem.locationUpdatedAt).toLocaleString('ar-SA') : 'غير محدد'}
                </p>
              </div>
            </div>
          ) : (
            <div className="text-center text-gray-400 py-8">
              <MapPin className="w-12 h-12 mx-auto mb-4" />
              <p>اختر عنصر من الخريطة لعرض التفاصيل</p>
            </div>
          )}
        </div>
      </div>

      {/* Active Shipments List */}
      <div className="mt-6 bg-gray-800 rounded-lg p-6">
        <h2 className="text-xl font-bold text-white mb-4 flex items-center gap-2">
          <MapPin className="w-5 h-5" />
          الشحنات النشطة
        </h2>
        <div className="space-y-3">
          {shipments.length === 0 ? (
            <p className="text-gray-400 text-center py-4">لا توجد شحنات نشطة</p>
          ) : (
            shipments.map((shipment) => (
              <div
                key={shipment._id}
                className="flex items-center justify-between p-3 bg-gray-700 rounded hover:bg-gray-600 cursor-pointer"
                onClick={() => setSelectedItem(shipment)}
              >
                <div>
                  <p className="text-white font-semibold">{shipment.shipmentNumber}</p>
                  <p className="text-gray-400 text-sm">
                    {shipment.pickupLocation?.city} → {shipment.deliveryLocation?.city}
                  </p>
                </div>
                <div className="text-left">
                  <p className="text-blue-500 text-sm">في الطريق</p>
                  <p className="text-gray-500 text-xs">
                    {shipment.driver?.name || 'غير محدد'}
                  </p>
                </div>
              </div>
            ))
          )}
        </div>
      </div>

      {/* Active Trucks List */}
      <div className="mt-6 bg-gray-800 rounded-lg p-6">
        <h2 className="text-xl font-bold text-white mb-4 flex items-center gap-2">
          <Navigation className="w-5 h-5" />
          الشاحنات النشطة
        </h2>
        <div className="space-y-3">
          {trucks.length === 0 ? (
            <p className="text-gray-400 text-center py-4">لا توجد شاحنات نشطة</p>
          ) : (
            trucks.map((truck) => (
              <div
                key={truck._id}
                className="flex items-center justify-between p-3 bg-gray-700 rounded hover:bg-gray-600 cursor-pointer"
                onClick={() => setSelectedItem(truck)}
              >
                <div>
                  <p className="text-white font-semibold">{truck.truckNumber}</p>
                  <p className="text-gray-400 text-sm">{truck.plateNumber}</p>
                </div>
                <div className="text-left">
                  <p className="text-green-500 text-sm">نشطة</p>
                  <p className="text-gray-500 text-xs">
                    {truck.currentDriver?.name || 'غير محدد'}
                  </p>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}

function StatCard({ title, value, color }) {
  const colors = {
    blue: 'bg-blue-600',
    green: 'bg-green-600',
    gold: 'bg-yellow-500'
  };

  return (
    <div className={`${colors[color]} p-6 rounded-lg text-white`}>
      <h3 className="text-gray-100 mb-2">{title}</h3>
      <p className="text-2xl font-bold">{value}</p>
    </div>
  );
}
