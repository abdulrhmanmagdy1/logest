/**
 * ============================================
 * 🚛 Truck Detail Component - نظام إدهام
 * Edham Logistics - Truck Detail
 * ============================================
 */

import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import api from '../../services/api';
import { Loader, AlertCircle, Truck, Wrench, Calendar, MapPin, User, Gauge, Fuel, Edit, Trash2 } from 'lucide-react';

export default function TruckDetail() {
  const { id } = useParams();
  const [truck, setTruck] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchTruck();
  }, [id]);

  const fetchTruck = async () => {
    try {
      setLoading(true);
      const response = await api.get(`/trucks/${id}`);
      setTruck(response.data);
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

  if (!truck) {
    return (
      <div className="bg-gray-800 p-8 rounded-lg text-center">
        <Truck className="w-16 h-16 text-gray-600 mx-auto mb-4" />
        <p className="text-gray-400">الشاحنة غير موجودة</p>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto p-6">
      {/* Header */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h1 className="text-3xl font-bold text-white">تفاصيل الشاحنة</h1>
          <p className="text-gray-400 mt-1">{truck.truckNumber}</p>
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
            <TruckStatusBadge status={truck.status} />
          </div>
          <div className="text-right">
            <p className="text-gray-400 text-sm">تاريخ التسجيل</p>
            <p className="text-white font-semibold">
              {new Date(truck.createdAt).toLocaleDateString('ar-SA')}
            </p>
          </div>
        </div>
      </div>

      {/* Truck Info */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
        {/* Basic Info */}
        <div className="bg-gray-800 p-6 rounded-lg">
          <h3 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
            <Truck className="w-5 h-5" />
            المعلومات الأساسية
          </h3>
          <div className="space-y-2 text-gray-300">
            <p><span className="text-gray-400">رقم الشاحنة:</span> {truck.truckNumber}</p>
            <p><span className="text-gray-400">رقم اللوحة:</span> {truck.plateNumber}</p>
            <p><span className="text-gray-400">النوع:</span> {truck.type === 'refrigerated' ? 'مبردة' : 'عادية'}</p>
            <p><span className="text-gray-400">السعة:</span> {truck.capacity} طن</p>
            <p><span className="text-gray-400">الشركة المصنعة:</span> {truck.manufacturer || 'غير محدد'}</p>
            <p><span className="text-gray-400">سنة الصنع:</span> {truck.year || 'غير محدد'}</p>
          </div>
        </div>

        {/* Current Status */}
        <div className="bg-gray-800 p-6 rounded-lg">
          <h3 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
            <Gauge className="w-5 h-5" />
            الحالة الحالية
          </h3>
          <div className="space-y-2 text-gray-300">
            <p><span className="text-gray-400">الكيلومترات:</span> {truck.currentKilometers?.toLocaleString() || 0} كم</p>
            <p><span className="text-gray-400">مستوى الوقود:</span> {truck.fuelLevel || 0}%</p>
            <p><span className="text-gray-400">درجة الحرارة:</span> {truck.temperature || 0}°C</p>
            <p><span className="text-gray-400">آخر صيانة:</span> {truck.lastMaintenanceDate ? new Date(truck.lastMaintenanceDate).toLocaleDateString('ar-SA') : 'غير محدد'}</p>
            <p><span className="text-gray-400">حالة الصيانة:</span> {truck.maintenanceStatus || 'جيدة'}</p>
          </div>
        </div>
      </div>

      {/* Current Driver */}
      <div className="bg-gray-800 p-6 rounded-lg mb-6">
        <h3 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
          <User className="w-5 h-5" />
          السائق الحالي
        </h3>
        {truck.currentDriver ? (
          <div className="space-y-2 text-gray-300">
            <p><span className="text-gray-400">الاسم:</span> {truck.currentDriver.name}</p>
            <p><span className="text-gray-400">الهاتف:</span> {truck.currentDriver.phone}</p>
            <p><span className="text-gray-400">البريد:</span> {truck.currentDriver.email}</p>
          </div>
        ) : (
          <p className="text-gray-400">لا يوجد سائق حالي</p>
        )}
      </div>

      {/* Current Location */}
      <div className="bg-gray-800 p-6 rounded-lg mb-6">
        <h3 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
          <MapPin className="w-5 h-5" />
          الموقع الحالي
        </h3>
        {truck.currentLocation ? (
          <div className="space-y-2 text-gray-300">
            <p><span className="text-gray-400">المدينة:</span> {truck.currentLocation.city}</p>
            <p><span className="text-gray-400">العنوان:</span> {truck.currentLocation.address}</p>
            <p><span className="text-gray-400">الإحداثيات:</span> {truck.currentLocation.coordinates?.lat}, {truck.currentLocation.coordinates?.lng}</p>
            <p><span className="text-gray-400">آخر تحديث:</span> {new Date(truck.locationUpdatedAt).toLocaleString('ar-SA')}</p>
          </div>
        ) : (
          <p className="text-gray-400">الموقع غير متاح</p>
        )}
      </div>

      {/* Maintenance Schedule */}
      <div className="bg-gray-800 p-6 rounded-lg mb-6">
        <h3 className="text-lg font-bold text-white mb-4 flex items-center gap-2">
          <Wrench className="w-5 h-5" />
          جدول الصيانة
        </h3>
        <div className="space-y-2 text-gray-300">
          <p><span className="text-gray-400">تغيير الزيت:</span> {truck.oilChangeKm || 0} كم المتبقي</p>
          <p><span className="text-gray-400">فحص الإطارات:</span> {truck.tireCheckKm || 0} كم المتبقي</p>
          <p><span className="text-gray-400">فحص الفرامل:</span> {truck.brakeCheckKm || 0} كم المتبقي</p>
        </div>
      </div>

      {/* Documents */}
      <div className="bg-gray-800 p-6 rounded-lg">
        <h3 className="text-lg font-bold text-white mb-4">الوثائق</h3>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          <DocumentCard title="رخصة القيادة" status={truck.documents?.drivingLicense ? 'متوفر' : 'غير متوفر'} />
          <DocumentCard title="رخصة الشاحنة" status={truck.documents?.truckLicense ? 'متوفر' : 'غير متوفر'} />
          <DocumentCard title="التأمين" status={truck.documents?.insurance ? 'متوفر' : 'غير متوفر'} />
          <DocumentCard title="فحص الفني" status={truck.documents?.technicalInspection ? 'متوفر' : 'غير متوفر'} />
        </div>
      </div>
    </div>
  );
}

function TruckStatusBadge({ status }) {
  const statusColors = {
    active: 'bg-green-500',
    inactive: 'bg-gray-500',
    maintenance: 'bg-yellow-500',
    out_of_service: 'bg-red-500',
    in_transit: 'bg-blue-500'
  };

  const statusLabels = {
    active: 'نشطة',
    inactive: 'غير نشطة',
    maintenance: 'صيانة',
    out_of_service: 'خارج الخدمة',
    in_transit: 'في الطريق'
  };

  return (
    <span className={`${statusColors[status] || 'bg-gray-500'} px-4 py-2 rounded text-lg font-semibold`}>
      {statusLabels[status] || status}
    </span>
  );
}

function DocumentCard({ title, status }) {
  const isAvailable = status === 'متوفر';
  return (
    <div className={`p-4 rounded ${isAvailable ? 'bg-green-900/30 border border-green-500' : 'bg-red-900/30 border border-red-500'}`}>
      <p className="text-white font-semibold mb-1">{title}</p>
      <p className={`text-sm ${isAvailable ? 'text-green-400' : 'text-red-400'}`}>{status}</p>
    </div>
  );
}
