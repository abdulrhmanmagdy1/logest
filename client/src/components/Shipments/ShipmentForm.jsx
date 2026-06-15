/**
 * ============================================
 * 📦 Shipment Form - نظام إدهام
 * Edham Logistics - Professional Shipment Form
 * ============================================
 */

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../../services/api';
import { Loader, AlertCircle } from 'lucide-react';
import { SAUDI_CITIES } from '../../config/constants';

export default function ShipmentForm({ onSuccess }) {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [formData, setFormData] = useState({
    description: '',
    weight: '',
    quantity: 1,
    pickupLocation: {
      address: '',
      city: '',
      contactName: '',
      contactPhone: ''
    },
    deliveryLocation: {
      address: '',
      city: '',
      contactName: '',
      contactPhone: ''
    },
    estimatedCost: '',
    specialInstructions: ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleLocationChange = (locationType, field, value) => {
    setFormData(prev => ({
      ...prev,
      [locationType]: {
        ...prev[locationType],
        [field]: value
      }
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      // Validation
      if (!formData.description || !formData.weight) {
        setError('البيانات الأساسية مطلوبة');
        setLoading(false);
        return;
      }

      const response = await api.post('/shipments', formData);

      if (onSuccess) {
        onSuccess(response.data);
      } else {
        navigate('/shipments');
      }
    } catch (err) {
      setError(err.message || 'خطأ في إنشاء الشحنة');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="bg-gray-800 p-6 rounded-lg space-y-6">
      {error && (
        <div className="bg-red-500 text-white p-3 rounded flex items-center">
          <AlertCircle className="mr-2" />
          {error}
        </div>
      )}

      {/* Basic Information */}
      <div>
        <h2 className="text-xl font-bold text-white mb-4">المعلومات الأساسية</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <input
            type="text"
            name="description"
            placeholder="وصف الشحنة"
            value={formData.description}
            onChange={handleChange}
            className="bg-gray-700 text-white px-4 py-2 rounded border border-gray-600 focus:border-blue-500 outline-none"
            required
          />
          <input
            type="number"
            name="weight"
            placeholder="الوزن (كجم)"
            value={formData.weight}
            onChange={handleChange}
            className="bg-gray-700 text-white px-4 py-2 rounded border border-gray-600 focus:border-blue-500 outline-none"
            required
            min="0"
          />
          <input
            type="number"
            name="quantity"
            placeholder="الكمية"
            value={formData.quantity}
            onChange={handleChange}
            className="bg-gray-700 text-white px-4 py-2 rounded border border-gray-600 focus:border-blue-500 outline-none"
            min="1"
          />
          <input
            type="number"
            name="estimatedCost"
            placeholder="التكلفة المتوقعة"
            value={formData.estimatedCost}
            onChange={handleChange}
            className="bg-gray-700 text-white px-4 py-2 rounded border border-gray-600 focus:border-blue-500 outline-none"
            min="0"
          />
        </div>
      </div>

      {/* Pickup Location */}
      <LocationSection
        title="موقع الاستلام"
        location={formData.pickupLocation}
        onChange={(field, value) => handleLocationChange('pickupLocation', field, value)}
      />

      {/* Delivery Location */}
      <LocationSection
        title="موقع التسليم"
        location={formData.deliveryLocation}
        onChange={(field, value) => handleLocationChange('deliveryLocation', field, value)}
      />

      {/* Special Instructions */}
      <div>
        <label className="block text-white mb-2">تعليمات خاصة</label>
        <textarea
          name="specialInstructions"
          placeholder="أضف أي تعليمات خاصة للشحنة"
          value={formData.specialInstructions}
          onChange={handleChange}
          className="w-full bg-gray-700 text-white px-4 py-2 rounded border border-gray-600 focus:border-blue-500 outline-none h-24"
        />
      </div>

      {/* Submit Button */}
      <div className="flex gap-4">
        <button
          type="submit"
          disabled={loading}
          className="flex-1 bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded transition disabled:opacity-50 flex items-center justify-center"
        >
          {loading ? (
            <>
              <Loader className="animate-spin mr-2 w-4 h-4" />
              جاري الإرسال...
            </>
          ) : (
            'إنشاء شحنة'
          )}
        </button>
        <button
          type="button"
          onClick={() => navigate('/shipments')}
          className="flex-1 bg-gray-700 hover:bg-gray-600 text-white font-bold py-2 px-4 rounded transition"
        >
          إلغاء
        </button>
      </div>
    </form>
  );
}

function LocationSection({ title, location, onChange }) {
  return (
    <div>
      <h3 className="text-lg font-bold text-white mb-3">{title}</h3>
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <select
          value={location.city}
          onChange={(e) => onChange('city', e.target.value)}
          className="bg-gray-700 text-white px-4 py-2 rounded border border-gray-600 focus:border-blue-500 outline-none"
          required
        >
          <option value="">اختر المدينة</option>
          {SAUDI_CITIES.map(city => (
            <option key={city} value={city}>{city}</option>
          ))}
        </select>
        <input
          type="text"
          placeholder="العنوان التفصيلي"
          value={location.address}
          onChange={(e) => onChange('address', e.target.value)}
          className="bg-gray-700 text-white px-4 py-2 rounded border border-gray-600 focus:border-blue-500 outline-none"
          required
        />
        <input
          type="text"
          placeholder="اسم جهة الاتصال"
          value={location.contactName}
          onChange={(e) => onChange('contactName', e.target.value)}
          className="bg-gray-700 text-white px-4 py-2 rounded border border-gray-600 focus:border-blue-500 outline-none"
          required
        />
        <input
          type="tel"
          placeholder="رقم الهاتف"
          value={location.contactPhone}
          onChange={(e) => onChange('contactPhone', e.target.value)}
          className="bg-gray-700 text-white px-4 py-2 rounded border border-gray-600 focus:border-blue-500 outline-none"
          required
        />
      </div>
    </div>
  );
}
