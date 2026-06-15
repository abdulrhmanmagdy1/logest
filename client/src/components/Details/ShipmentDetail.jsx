/**
 * ============================================
 * 📦 Shipment Detail Component - نظام إدهام
 * Edham Logistics - Shipment Detail
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../../services/api';
import { Loader, AlertCircle, Package, MapPin, Truck, User, Calendar, Clock, ArrowRight, Edit, Trash2 } from 'lucide-react';

export default function ShipmentDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [shipment, setShipment] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchShipment();
  }, [id]);

  const fetchShipment = async () => {
    try {
      setLoading(true);
      const response = await api.get(`/shipments/${id}`);
      setShipment(response.data);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
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

  if (!shipment) {
    return (
      <div className="bg-gray-800 p-8 rounded-lg text-center">
        <Package className="w-16 h-16 text-gray-600 mx-auto mb-4" />
        <p className="text-gray-400">الشحنة غير موجودة</p>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto p-6">
      {/* Header */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-white">تفاصيل الشحنة</h1>
          <p className="text-gray-400 mt-1">{shipment.shipmentNumber}</p>
        </div>
        <div className="flex gap-2">
          <button className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded flex items-center gap-2">
            <Edit className="w-4 h-4" />
            تعديل
          </button>
          <button className="bg-red-600 hover:bg-red-700 text-white font-bold py-2 px-4 rounded flex items-center gap-2">
            <Trash2 className="w-4 h-4" />
            حذف
          </button>
        </div>
      </div>

      {/* Status Badge */}
      <div className="bg-gray-800 p-6 rounded-lg mb-6">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-xl font-bold text-white mb-2">الحالة</h2>
            <StatusBadge status={shipment.status} />
          </div>
          <div className="text-right">
            <p className="text-gray-400 text-sm">تاريخ الإنشاء</p>
            <p className="text-white font-semibold">
              {new Date(shipment.createdAt).toLocaleDateString('ar-SA')}
            </p>
          </div>
        </div>
      </div>

      {/* Shipment Info */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
        {/* Pickup Location */}
        <div className="bg-gray-800 p-6 rounded-lg">
          <h3 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
            <MapPin className="w-5 h-5 text-green-500" />
            موقع الاستلام
          </h3>
          <div className="space-y-2 text-gray-300">
            <p><span className="text-gray-400">المدينة:</span> {shipment.pickupLocation?.city}</p>
            <p><span className="text-gray-400">العنوان:</span> {shipment.pickupLocation?.address}</p>
            <p><span className="text-gray-400">الإحداثيات:</span> {shipment.pickupLocation?.coordinates?.lat}, {shipment.pickupLocation?.coordinates?.lng}</p>
            <p><span className="text-gray-400">التاريخ المجدول:</span> {new Date(shipment.scheduledPickupDate).toLocaleString('ar-SA')}</p>
          </div>
        </div>

        {/* Delivery Location */}
        <div className="bg-gray-800 p-6 rounded-lg">
          <h3 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
            <MapPin className="w-5 h-5 text-red-500" />
            موقع التسليم
          </h3>
          <div className="space-y-2 text-gray-300">
            <p><span className="text-gray-400">المدينة:</span> {shipment.deliveryLocation?.city}</p>
            <p><span className="text-gray-400">العنوان:</span> {shipment.deliveryLocation?.address}</p>
            <p><span className="text-gray-400">الإحداثيات:</span> {shipment.deliveryLocation?.coordinates?.lat}, {shipment.deliveryLocation?.coordinates?.lng}</p>
            <p><span className="text-gray-400">التاريخ المجدول:</span> {new Date(shipment.scheduledDeliveryDate).toLocaleString('ar-SA')}</p>
          </div>
        </div>
      </div>

      {/* Route Visualization */}
      <div className="bg-gray-800 p-6 rounded-lg mb-6">
        <h3 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
          <ArrowRight className="w-5 h-5" />
          المسار
        </h3>
        <div className="flex items-center justify-between">
          <div className="text-center">
            <p className="text-white font-semibold">{shipment.pickupLocation?.city}</p>
            <p className="text-gray-400 text-sm">الاستلام</p>
          </div>
          <div className="flex-1 mx-4 h-1 bg-gray-700 rounded relative">
            <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 bg-blue-500 rounded-full w-4 h-4" />
          </div>
          <div className="text-center">
            <p className="text-white font-semibold">{shipment.deliveryLocation?.city}</p>
            <p className="text-gray-400 text-sm">التسليم</p>
          </div>
        </div>
      </div>

      {/* Shipment Details */}
      <div className="bg-gray-800 p-6 rounded-lg mb-6">
        <h3 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
          <Package className="w-5 h-5" />
          تفاصيل الشحنة
        </h3>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-gray-300">
          <div>
            <p className="text-gray-400 text-sm">الوصف</p>
            <p className="text-white font-semibold">{shipment.description}</p>
          </div>
          <div>
            <p className="text-gray-400 text-sm">الوزن</p>
            <p className="text-white font-semibold">{shipment.weight} كجم</p>
          </div>
          <div>
            <p className="text-gray-400 text-sm">الحجم</p>
            <p className="text-white font-semibold">{shipment.volume} م³</p>
          </div>
          <div>
            <p className="text-gray-400 text-sm">النوع</p>
            <p className="text-white font-semibold">{shipment.type}</p>
          </div>
        </div>
      </div>

      {/* Assigned Resources */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
        {/* Driver */}
        <div className="bg-gray-800 p-6 rounded-lg">
          <h3 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
            <User className="w-5 h-5" />
            السائق
          </h3>
          {shipment.driver ? (
            <div className="space-y-2 text-gray-300">
              <p><span className="text-gray-400">الاسم:</span> {shipment.driver.name}</p>
              <p><span className="text-gray-400">الهاتف:</span> {shipment.driver.phone}</p>
              <p><span className="text-gray-400">البريد:</span> {shipment.driver.email}</p>
            </div>
          ) : (
            <p className="text-gray-400">لم يتم إسناد سائق</p>
          )}
        </div>

        {/* Truck */}
        <div className="bg-gray-800 p-6 rounded-lg">
          <h3 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
            <Truck className="w-5 h-5" />
            الشاحنة
          </h3>
          {shipment.truck ? (
            <div className="space-y-2 text-gray-300">
              <p><span className="text-gray-400">الرقم:</span> {shipment.truck.truckNumber}</p>
              <p><span className="text-gray-400">اللوحة:</span> {shipment.truck.plateNumber}</p>
              <p><span className="text-gray-400">السعة:</span> {shipment.truck.capacity} طن</p>
            </div>
          ) : (
            <p className="text-gray-400">لم يتم إسناد شاحنة</p>
          )}
        </div>
      </div>

      {/* Timeline */}
      <div className="bg-gray-800 p-6 rounded-lg">
        <h3 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
          <Clock className="w-5 h-5" />
          الجدول الزمني
        </h3>
        <div className="space-y-4">
          <TimelineItem
            title="تم إنشاء الشحنة"
            date={shipment.createdAt}
            completed={true}
          />
          {shipment.actualPickupDate && (
            <TimelineItem
              title="تم الاستلام"
              date={shipment.actualPickupDate}
              completed={true}
            />
          )}
          {shipment.actualDeliveryDate && (
            <TimelineItem
              title="تم التسليم"
              date={shipment.actualDeliveryDate}
              completed={true}
            />
          )}
          {!shipment.actualDeliveryDate && (
            <TimelineItem
              title="التسليم المجدول"
              date={shipment.scheduledDeliveryDate}
              completed={false}
            />
          )}
        </div>
      </div>
    </div>
  );
}

function StatusBadge({ status }) {
  const statusColors = {
    pending: 'bg-yellow-500',
    assigned: 'bg-blue-500',
    in_transit: 'bg-purple-500',
    delivered: 'bg-green-500',
    cancelled: 'bg-red-500',
    failed: 'bg-gray-500'
  };

  const statusLabels = {
    pending: 'قيد الانتظار',
    assigned: 'مُسندة',
    in_transit: 'في الطريق',
    delivered: 'تم التسليم',
    cancelled: 'ملغاة',
    failed: 'فشلت'
  };

  return (
    <span className={`${statusColors[status] || 'bg-gray-500'} px-4 py-2 rounded text-lg font-semibold`}>
      {statusLabels[status] || status}
    </span>
  );
}

function TimelineItem({ title, date, completed }) {
  return (
    <div className="flex items-center gap-4">
      <div className={`w-3 h-3 rounded-full ${completed ? 'bg-green-500' : 'bg-gray-500'}`} />
      <div className="flex-1">
        <p className="text-white font-semibold">{title}</p>
        <p className="text-gray-400 text-sm">
          {date ? new Date(date).toLocaleString('ar-SA') : 'غير محدد'}
        </p>
      </div>
    </div>
  );
}
